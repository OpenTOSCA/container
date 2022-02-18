package org.opentosca.container.api.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.model.tosca.TServiceTemplate;

import org.opentosca.container.api.dto.plan.PlanDTO;
import org.opentosca.container.control.OpenToscaControlService;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.convention.Interfaces;
import org.opentosca.container.core.extension.TParameter;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.next.model.PlanInstance;
import org.opentosca.container.core.next.model.PlanInstanceEvent;
import org.opentosca.container.core.next.model.PlanInstanceState;
import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.repository.PlanInstanceRepository;
import org.opentosca.container.core.next.repository.ServiceTemplateInstanceRepository;
import org.opentosca.deployment.checks.DeploymentTestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PlanService {

    private static final Logger logger = LoggerFactory.getLogger(PlanService.class);
    private final OpenToscaControlService controlService;
    private final DeploymentTestService deploymentTestService;
    private final ServiceTemplateInstanceRepository serviceTemplateInstanceRepository;
    private final PlanInstanceRepository planInstanceRepository;

    @Inject
    public PlanService(OpenToscaControlService controlService, DeploymentTestService deploymentTestService,
                       ServiceTemplateInstanceRepository serviceTemplateInstanceRepository, PlanInstanceRepository planInstanceRepository) {
        this.controlService = controlService;
        this.deploymentTestService = deploymentTestService;
        this.serviceTemplateInstanceRepository = serviceTemplateInstanceRepository;
        this.planInstanceRepository = planInstanceRepository;
    }

    public PlanInstance getPlanInstance(Long id) {
        return this.planInstanceRepository.findById(id).orElse(null);
    }

    public List<PlanInstance> getPlanInstances(final Csar csar, final PlanType... planTypes) {
        final Collection<ServiceTemplateInstance> serviceInstances = serviceTemplateInstanceRepository.findByCsarId(csar.id());
        return serviceInstances.stream()
            .flatMap(sti -> sti.getPlanInstances().stream())
            .filter(p -> {
                final PlanType currentType = PlanType.fromString(p.getType().toString());
                return Arrays.stream(planTypes).anyMatch(pt -> pt.equals(currentType));
            })
            .collect(Collectors.toList());
    }

    /**
     * Get DTO for the plan with the given Id in the given Csar
     *
     * @param csar      the Csar containing the plan
     * @param planTypes an array with possible types of the plan
     * @param planId    the Id of the plan
     * @return the PlanDto if found or
     * @throws NotFoundException is thrown if the plan can not be found
     */
    public PlanDTO getPlanDto(Csar csar, PlanType[] planTypes, String planId) throws NotFoundException {
        return csar.plans().stream()
            .filter(tplan -> Arrays.stream(planTypes).anyMatch(pt -> tplan.getPlanType().equals(pt.toString())))
            .filter(tplan -> tplan.getId() != null && tplan.getId().equals(planId))
            .findFirst()
            .map(PlanDTO::new)
            .orElseThrow(NotFoundException::new);
    }

    public PlanInstance getPlanInstanceByCorrelationId(final String correlationId) {
        return planInstanceRepository.findByCorrelationId(correlationId);
    }

    public PlanInstance resolvePlanInstance(Long serviceTemplateInstanceId, String correlationId) {
        final PlanInstance pi = planInstanceRepository.findByCorrelationId(correlationId);

        if (pi == null) {
            final String msg = "Plan instance with correlationId '" + correlationId + "' not found";
            logger.error(msg);
            throw new NotFoundException(msg);
        }

        if (pi.getServiceTemplateInstance() != null && serviceTemplateInstanceId != null && serviceTemplateInstanceId != pi.getServiceTemplateInstance().getId()) {
            throw new NotFoundException(String.format("The passed service template instance id <%s> does not match the service template instance id that is associated with the plan instance <%s> ",
                serviceTemplateInstanceId, correlationId));
        }
        return pi;
    }

    public boolean updatePlanInstanceState(PlanInstance instance, PlanInstanceState newState) {
        try {
            instance.setState(newState);
            this.planInstanceRepository.save(instance);
            return true;
        } catch (final IllegalArgumentException e) {
            logger.info("The given state {} is an illegal plan instance state.", newState);
            return false;
        }
    }

    public void addLogToPlanInstance(PlanInstance instance, PlanInstanceEvent event) {
        instance.addEvent(event);
        planInstanceRepository.save(instance);
    }

    public String invokePlan(Csar csar, TServiceTemplate serviceTemplate, Long serviceTemplateInstanceId, String planId, List<TParameter> parameters, PlanType... planTypes) {
        TPlan plan = csar.plans().stream()
            .filter(tplan -> tplan.getId().equals(planId)
                && Arrays.stream(planTypes).anyMatch(pt -> tplan.getPlanType().equals(pt.toString())))
            .findFirst()
            .orElseThrow(() -> new NotFoundException("Plan \"" + planId + "\" could not be found"));

        final PlanDTO dto = new PlanDTO(plan);

        dto.setId(plan.getId());
        enhanceInputParameters(parameters);
        dto.setInputParameters(parameters);

        final String correlationId = controlService.invokePlanInvocation(csar.id(), serviceTemplate,
            serviceTemplateInstanceId,
            PlanDTO.Converter.convert(dto));
        if (PlanType.fromString(plan.getPlanType()).equals(PlanType.BUILD)
            && Boolean.parseBoolean(Settings.OPENTOSCA_DEPLOYMENT_TESTS)) {
            logger.debug("Plan \"{}\" is a build plan, so we schedule deployment tests...", plan.getName());
            this.deploymentTestService.runAfterPlan(csar.id(), correlationId);
        }
        return correlationId;
    }

    // @TODO move or merge this with MBJavaAPI#createRequestBody
    private void enhanceInputParameters(List<TParameter> parameters) {
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
