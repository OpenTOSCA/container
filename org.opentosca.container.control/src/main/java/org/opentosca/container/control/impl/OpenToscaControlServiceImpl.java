package org.opentosca.container.control.impl;

import static org.opentosca.container.core.model.deployment.process.DeploymentProcessState.*;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.xml.namespace.QName;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.model.tosca.TPlans;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.opentosca.container.control.OpenToscaControlService;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.model.deployment.process.DeploymentProcessOperation;
import org.opentosca.container.core.model.deployment.process.DeploymentProcessState;
import org.opentosca.container.core.model.instance.ServiceTemplateInstanceID;
import org.opentosca.container.core.service.CsarStorageService;
import org.opentosca.container.core.service.DeploymentTracker;
import org.opentosca.container.core.service.ICSARInstanceManagementService;
import org.opentosca.container.core.service.IPlanInvocationEngine;
import org.opentosca.container.core.tosca.extension.TPlanDTO;
import org.opentosca.container.engine.plan.IPlanEngineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@NonNullByDefault
public class OpenToscaControlServiceImpl implements OpenToscaControlService {

  private static final Logger LOGGER = LoggerFactory.getLogger(OpenToscaControlServiceImpl.class);

  private final DeploymentTracker deploymentTracker;
  private final IPlanEngineService planEngine;
  private final IPlanInvocationEngine planInvocationEngine;
  private final CsarStorageService storage;
  // used only for instanceIdOfCorrelation
  private final ICSARInstanceManagementService instanceManagement;

  @Inject
  public OpenToscaControlServiceImpl(DeploymentTracker deploymentTracker,
                                     IPlanEngineService planEngine,
                                     IPlanInvocationEngine planInvocationEngine,
                                     CsarStorageService storage,
                                     ICSARInstanceManagementService instanceManagement) {
    this.deploymentTracker = deploymentTracker;
    this.planEngine = planEngine;
    this.planInvocationEngine = planInvocationEngine;
    this.storage = storage;

    this.instanceManagement = instanceManagement;
  }

  @Override
  public boolean invokeToscaProcessing(CsarId csar) {
    LOGGER.debug("Start resolving ServiceTemplates of [{}]", csar.csarName());
    deploymentTracker.storeDeploymentState(csar, TOSCAPROCESSING_ACTIVE);
    LOGGER.info("Processing of Definitions completed successfully for [{}]", csar.csarName());
    deploymentTracker.storeDeploymentState(csar, TOSCA_PROCESSED);
    return true;
  }

  @Override
  public boolean generatePlans(CsarId csarId, ServiceTemplateId serviceTemplate) {
    Csar csar = storage.findById(csarId);
    final TServiceTemplate entryServiceTemplate = csar.entryServiceTemplate();
    if (entryServiceTemplate == null) {
      LOGGER.error("No EntryServiceTemplate defined for CSAR [{}]. Aborting plan generation", csarId);
      return false;
    }
    deploymentTracker.storeDeploymentState(csarId, PLAN_DEPLOYMENT_ACTIVE);
    TPlans plans = entryServiceTemplate.getPlans();
    if (plans == null) {
      LOGGER.info("No Plans to process");
      return true;
    }
    String namespace = plans.getTargetNamespace();
    if (namespace == null) {
      // Plans has no targetNamespace, fallback to ServiceTemplate namespace
      namespace = serviceTemplate.getQName().getNamespaceURI();
    }
    List<TPlan> undeployed = new ArrayList<>();
    for (final TPlan plan : plans.getPlan()) {
      if (!planEngine.deployPlan(plan, namespace, csarId)) {
        undeployed.add(plan);
      }
    }

    if (!undeployed.isEmpty()) {
      LOGGER.error("Plan deployment failed");
      deploymentTracker.storeDeploymentState(csarId, TOSCA_PROCESSED);
      return false;
    }
    LOGGER.info("Successfully deployeed management plans of [{}] in CSAR [{}]", serviceTemplate, csarId);
    deploymentTracker.storeDeploymentState(csarId, PLANS_DEPLOYED);
    // endpointService.printPlanEndpoints();
    return true;
  }

  @Override
  public String invokePlanInvocation(CsarId csarId, TServiceTemplate serviceTemplate, long instanceId,
                                     TPlanDTO plan) throws UnsupportedEncodingException {
    LOGGER.info("Invoking Plan [{}]", plan.getName());
    final String correlationId = planInvocationEngine.invokePlan(csarId, new QName(serviceTemplate.getId()), instanceId, plan);
    if (correlationId != null) {
      LOGGER.info("Plan Invocation was sucessful.");
    } else {
      LOGGER.info("Plan Invocation failed.");
    }
    return correlationId;
  }

  @Override
  public Set<DeploymentProcessOperation> executableDeploymentProcessOperations(CsarId csar) {
    final Set<DeploymentProcessOperation> operations = new HashSet<>();

    switch (deploymentTracker.getDeploymentState(csar)) {
      case PLANS_DEPLOYED:
      case TOSCA_PROCESSED:
        operations.add(DeploymentProcessOperation.INVOKE_PLAN_DEPL);
        // intentional fallthrough
      case STORED:
        operations.add(DeploymentProcessOperation.PROCESS_TOSCA);
        break;
      default:
        // during active processing no operations are allowed for the csar
        break;
    }
    return operations;
  }

  @Override
  public boolean declareStored(CsarId csar) {
    LOGGER.trace("Forcibly marking csar {} as STORED", csar.csarName());
    deploymentTracker.storeDeploymentState(csar, STORED);
    return true;
  }

  @Override
  public DeploymentProcessState currentDeploymentProcessState(CsarId csar) {
    return deploymentTracker.getDeploymentState(csar);
  }

  @Override
  // FIXME investigate why old ControlService sometimes took long instanceIds
  public List<String> correlationsForServiceTemplateInstance(CsarId csar, TServiceTemplate serviceTemplate,
                                                             long instanceId) {
    return planInvocationEngine.getActiveCorrelationsOfInstance(new ServiceTemplateInstanceID(csar, new QName(serviceTemplate.getId()), (int) instanceId));
  }

  @Override
  public TPlanDTO getActivePlanOfInstance(CsarId csar, ServiceTemplateId serviceTemplate, long instanceId,
                                          String correlationId) {
    final ServiceTemplateInstanceID stInstanceId = new ServiceTemplateInstanceID(csar, serviceTemplate.getQName(), (int) instanceId);
    return planInvocationEngine.getActivePublicPlanOfInstance(stInstanceId, correlationId);
  }

  @Override
  public long instanceIdOfCorrelation(String correlationId) {
    return instanceManagement.getInstanceForCorrelation(correlationId).getInstanceID();
  }

  @Override
  public List<String> deleteCsar(CsarId csarId) {
    List<String> errors = new ArrayList<>();
    final Csar csar = storage.findById(csarId);

    if (!undeployAllPlans(csar)) {
      errors.add("Failed to undeploy all plans for csar " + csarId);
    }
    deploymentTracker.deleteDeploymentState(csarId);
    // FIXME removeEndpoints
    try {
      storage.deleteCSAR(csarId);
    } catch (UserException | SystemException e) {
      errors.add(e.getMessage());
    }
    return errors;
  }

  /**
   * Undeploys all plans associated with the given csar
   *
   * @return true, if undeploying all plans was successful, false otherwise
   */
  private boolean undeployAllPlans(Csar csar) {
    return csar.serviceTemplates().stream().allMatch(st -> undeployAllPlans(csar.id(), st));
  }

  /**
   * Undeploys all plans associated to the given serviceTemplate
   *
   * @return true, if undeploying all plans was successful, false otherwise
   */
  private boolean undeployAllPlans(CsarId csarId,TServiceTemplate serviceTemplate) {
    TPlans plans = serviceTemplate.getPlans();
    if (plans == null) {
      LOGGER.info("No Plans to undeploy");
      return true;
    }
    String namespace = plans.getTargetNamespace();
    if (namespace == null) {
      // Plans has no targetNamespace, fallback to ServiceTemplate namespace
      namespace = serviceTemplate.getTargetNamespace();
    }
    List<TPlan> undeployed = new ArrayList<>();
    for (final TPlan plan : plans.getPlan()) {
      if (!planEngine.undeployPlan(plan, namespace, csarId)) {
        undeployed.add(plan);
      }
    }
    return undeployed.isEmpty();
  }

  @Override
  public boolean invokePlanDeployment(CsarId csar, TServiceTemplate serviceTemplate) {
    deploymentTracker.storeDeploymentState(csar, PLAN_DEPLOYMENT_ACTIVE);
    final List<TPlan> undeployedPlans = new ArrayList<>();
    LOGGER.trace("Invoking PlanEngine to process Plans");
    if (planEngine == null) {
      LOGGER.error("PlanEngine is not alive!");
      deploymentTracker.storeDeploymentState(csar, TOSCA_PROCESSED);
      return false;
    }

    if (serviceTemplate == null) {
      LOGGER.warn("Could not find the main ServiceTemplate");
      deploymentTracker.storeDeploymentState(csar, TOSCA_PROCESSED);
      return false;
    }

    final TPlans plans = serviceTemplate.getPlans();
    if (plans == null || plans.getPlan() == null || plans.getPlan().isEmpty()) {
      LOGGER.info("No plans to process");
      return true;
    }

    // fallback to serviceTemplate NamespaceURI
    final String namespace = plans.getTargetNamespace() == null
      ? serviceTemplate.getTargetNamespace()
      : plans.getTargetNamespace();

    for (final TPlan plan : plans.getPlan()) {
      if (!planEngine.deployPlan(plan, namespace, csar)) {
        undeployedPlans.add(plan);
      }
    }

    if (!undeployedPlans.isEmpty()) {
      LOGGER.warn("Plan deployment failed!");
      deploymentTracker.storeDeploymentState(csar, TOSCA_PROCESSED);
      return false;
    }

    LOGGER.trace("The deployment of management plans for ServiceTemplate \"{}\" inside CSAR [{}] was successful", serviceTemplate.getId(), csar.csarName());
    deploymentTracker.storeDeploymentState(csar, PLANS_DEPLOYED);
    return true;
  }

  @Deprecated
  @Override
  public String invokePlanInvocation(CsarId csarId, QName qname, int instanceId, TPlanDTO plan) throws UnsupportedEncodingException {
    Csar csar = storage.findById(csarId);
    final Optional<TServiceTemplate> serviceTemplate = csar.serviceTemplates().stream()
      .filter(st -> st.getId().equals(qname.toString()))
      .findFirst();
    return serviceTemplate.isPresent() ? invokePlanInvocation(csarId, serviceTemplate.get(), instanceId, plan) : "";
  }

  @Deprecated
  @Override
  public boolean invokePlanDeployment(CsarId csarId, QName qname) {
    Csar csar = storage.findById(csarId);
    final Optional<TServiceTemplate> serviceTemplate = csar.serviceTemplates().stream()
      .filter(st -> st.getId().equals(qname.toString()))
      .findFirst();
    return serviceTemplate.isPresent() ? invokePlanDeployment(csarId, serviceTemplate.get()) : false;
  }

  @Deprecated
  @Override
  public List<QName> getAllContainedServiceTemplates(CsarId csarid) {
    return storage.findById(csarid).serviceTemplates().stream()
      .map(TServiceTemplate::getId)
      .map(QName::new).collect(Collectors.toList());
  }
}

