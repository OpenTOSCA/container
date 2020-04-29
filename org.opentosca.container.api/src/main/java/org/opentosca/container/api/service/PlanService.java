package org.opentosca.container.api.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.model.tosca.TServiceTemplate;

import org.opentosca.container.api.dto.plan.PlanDTO;
import org.opentosca.container.control.OpenToscaControlService;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.next.model.PlanInstance;
import org.opentosca.container.core.next.model.PlanInstanceEvent;
import org.opentosca.container.core.next.model.PlanInstanceState;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.repository.PlanInstanceRepository;
import org.opentosca.container.core.next.repository.ServiceTemplateInstanceRepository;
import org.opentosca.container.core.tosca.convention.Interfaces;
import org.opentosca.container.core.tosca.extension.PlanTypes;
import org.opentosca.container.core.tosca.extension.TParameter;
import org.opentosca.deployment.checks.DeploymentTestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PlanService {

  private static Logger logger = LoggerFactory.getLogger(PlanService.class);

  private static final PlanTypes[] ALL_PLAN_TYPES = PlanTypes.values();

  private final OpenToscaControlService controlService;
  private final DeploymentTestService deploymentTestService;
  private final PlanInstanceRepository planInstanceRepository = new PlanInstanceRepository();

  @Inject
  public PlanService(OpenToscaControlService controlService, DeploymentTestService deploymentTestService) {
    this.controlService = controlService;
    this.deploymentTestService = deploymentTestService;
  }

  public List<PlanInstance> getPlanInstances(final Csar csar, final TServiceTemplate serviceTemplate, String planName, final PlanTypes... planTypes) {
    TPlan plan = csar.plans().stream()
      .filter(tplan -> (planName.equals(tplan.getName()) || planName.equals(tplan.getId()))
          && Arrays.stream(planTypes).anyMatch(pt -> tplan.getPlanType().equals(pt.toString())))
      .findFirst()
      .orElseThrow(NotFoundException::new);

    final ServiceTemplateInstanceRepository repo = new ServiceTemplateInstanceRepository();
    final Collection<ServiceTemplateInstance> serviceInstances = repo.findByCsarId(csar.id());
    return serviceInstances.stream()
      .flatMap(sti -> sti.getPlanInstances().stream())
      .filter(p -> {
        final PlanTypes currentType = PlanTypes.isPlanTypeURI(p.getType().toString());
        return Arrays.stream(planTypes).anyMatch(pt -> pt.equals(currentType));
      })
      .collect(Collectors.toList());
  }

  public PlanInstance getPlanInstanceByCorrelationId(final String correlationId) {
    return planInstanceRepository.findByCorrelationId(correlationId);
  }

  public PlanInstance resolvePlanInstance(Csar csar, TServiceTemplate serviceTemplate, Long serviceTemplateInstanceId, String planId, String planInstanceId, PlanTypes... planTypes) {
    TPlan plan = csar.plans().stream()
      .filter(tplan -> tplan.getId().equals(planId) && Arrays.stream(planTypes).anyMatch(pt -> tplan.getPlanType().equals(pt.toString())))
      .findFirst()
      .orElseThrow(() -> new NotFoundException("Plan \"" + planId + "\" could not be found"));

    final PlanInstanceRepository repository = new PlanInstanceRepository();
    final PlanInstance pi = repository.findByCorrelationId(planInstanceId);

    if (pi == null) {
      final String msg = "Plan instance '" + planInstanceId + "' not found";
      logger.info(msg);
      throw new NotFoundException(msg);
    }
    if (!pi.getTemplateId().getLocalPart().equals(plan.getId())) {
      throw new NotFoundException(String.format("The passed plan instance <%s> does not belong to the passed plan template: %s", planInstanceId, plan));
    }

    final Long id = pi.getServiceTemplateInstance().getId();
    if (serviceTemplateInstanceId != null && serviceTemplateInstanceId != id) {
      throw new NotFoundException(String.format("The passed service template instance id <%s> does not match the service template instance id that is associated with the plan instance <%s> ",
        serviceTemplateInstanceId, id, planInstanceId));
    }
    return pi;
  }

  public boolean updatePlanInstanceState(PlanInstance instance, PlanInstanceState newState) {
    try {
      instance.setState(newState);
      this.planInstanceRepository.update(instance);
      return true;
    } catch (final IllegalArgumentException e) {
      logger.info("The given state {} is an illegal plan instance state.", newState);
      return false;
    }
  }

  public void addLogToPlanInstance(PlanInstance instance, PlanInstanceEvent event) {
    instance.addEvent(event);
    planInstanceRepository.update(instance);
  }

  public String invokePlan(Csar csar, TServiceTemplate serviceTemplate, Long serviceTemplateInstanceId, String planId, List<TParameter> parameters, PlanTypes... planTypes) {
    TPlan plan = csar.plans().stream()
      .filter(tplan -> tplan.getId().equals(planId)
        && Arrays.stream(planTypes).anyMatch(pt -> tplan.getPlanType().equals(pt.toString())))
      .findFirst()
      .orElseThrow(() -> new NotFoundException("Plan \"" + planId + "\" could not be found"));

    final String namespace = serviceTemplate.getTargetNamespace();
    final PlanDTO dto = new PlanDTO(plan);

    dto.setId(new QName(namespace, plan.getId()).toString());
    enhanceInputParameters(csar, serviceTemplate, serviceTemplateInstanceId, parameters);
    dto.setInputParameters(parameters);

    final String correlationId = controlService.invokePlanInvocation(csar.id(), serviceTemplate,
      serviceTemplateInstanceId,
      PlanDTO.Converter.convert(dto));
    if (PlanTypes.isPlanTypeURI(plan.getPlanType()).equals(PlanTypes.BUILD)
      && Boolean.parseBoolean(Settings.OPENTOSCA_DEPLOYMENT_TESTS)) {
      logger.debug("Plan \"{}\" is a build plan, so we schedule deployment tests...", plan.getName());
      this.deploymentTestService.runAfterPlan(csar.id(), correlationId);
    }
    return correlationId;
  }

  private void enhanceInputParameters(Csar csar, TServiceTemplate serviceTemplate, Long serviceTemplateInstanceId, List<TParameter> parameters) {
    // set "meta" params
    for (final TParameter param : parameters) {
      if (param.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE_FREEZE_MANDATORY_PARAM_ENDPOINT)
        && param.getValue() != null && param.getValue().isEmpty()) {
        final String containerRepoUrl = Settings.getSetting("org.opentosca.container.connector.winery.url");
        param.setValue(containerRepoUrl);
      }
    }
  }
}
