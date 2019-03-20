package org.opentosca.container.control.impl;

import static org.opentosca.container.core.model.deployment.process.DeploymentProcessState.*;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.xml.namespace.QName;

import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.model.tosca.TPlans;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.opentosca.container.control.OpenToscaControlService;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.engine.IToscaEngineService;
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
public class OpenToscaControlServiceImplReplacement implements OpenToscaControlService {

  private static final Logger LOGGER = LoggerFactory.getLogger(OpenToscaControlServiceImplReplacement.class);

  @Inject
  private IToscaEngineService toscaEngine;
  @Inject
  private DeploymentTracker deploymentTracker;
  @Inject
  private IPlanEngineService planEngine;
  @Inject
  private IPlanInvocationEngine planInvocationEngine;
  @Inject
  private CsarStorageService storage;
  // used only for instanceIdOfCorrelation
  @Inject
  private ICSARInstanceManagementService instanceManagement;

  @Override
  public boolean invokeToscaProcessing(CsarId csar) {
    LOGGER.debug("Start resolving ServiceTemplates of [{}]", csar.csarName());
    deploymentTracker.storeDeploymentState(csar, TOSCAPROCESSING_ACTIVE);
    // FIXME: We cannot resolve definitions based on the bridge, because the definitions resolution
    // accesses the ToscaMetaFile. That file is not in the imported CSAR representation.
    // Additionally the CsarImporter largely takes care of definitions resolution for us!
    // use new ToscaMetaFileReplacement(csar); to obtain a suitable toscametafile
    // FIXME: resolving definitions doesn't actually resolve the definitions. Well it does, but it stores them into
    // the ToscaReferenceMapper in addition to resolving them. As such this method should become unnecessar
    if (true || toscaEngine.resolveDefinitions(csar.toOldCsarId())) {
      LOGGER.info("Processing of Definitions completed successfully for [{}]", csar.csarName());
      deploymentTracker.storeDeploymentState(csar, TOSCA_PROCESSED);
      return true;
    } else {
      LOGGER.info("Processing of Definitions failed for [{}]", csar.csarName());
      deploymentTracker.storeDeploymentState(csar, STORED);
      return false;
    }
  }

  @Override
  public boolean generatePlans(CsarId csarId, ServiceTemplateId serviceTemplate) {
    // assumption: current deployment state is IAS_DEPLOYED
    if (planEngine == null) {
      LOGGER.error("PlanEngine is not alive!");
      return false;
    }

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
  public String invokePlanInvocation(CsarId csar, TServiceTemplate serviceTemplate, long instanceId,
                                     TPlanDTO plan) throws UnsupportedEncodingException {
    if (planInvocationEngine == null) {
      LOGGER.error("PlanInvocationEngine is not available!");
      return null;
    }
    LOGGER.info("Invoking Plan [{}]", plan.getName());
    final String correlationId = planInvocationEngine.invokePlan(csar.toOldCsarId(), new QName(serviceTemplate.getId()), instanceId, plan);
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
    return planInvocationEngine.getActiveCorrelationsOfInstance(new ServiceTemplateInstanceID(csar.toOldCsarId(), new QName(serviceTemplate.getId()), (int) instanceId));
  }

  @Override
  public TPlanDTO getActivePlanOfInstance(CsarId csar, ServiceTemplateId serviceTemplate, long instanceId,
                                          String correlationId) {
    final ServiceTemplateInstanceID stInstanceId = new ServiceTemplateInstanceID(csar.toOldCsarId(), serviceTemplate.getQName(), (int) instanceId);
    return planInvocationEngine.getActivePublicPlanOfInstance(stInstanceId, correlationId);
  }

  @Override
  public long instanceIdOfCorrelation(String correlationId) {
    return instanceManagement.getInstanceForCorrelation(correlationId).getInstanceID();
  }

  @Override
  public List<String> deleteCsar(CsarId csarId) {
    List<String> errors = new ArrayList<>();
    // FIXME: undeployPlans
    final Csar csar = storage.findById(csarId);

    if (!toscaEngine.clearCSARContent(csarId.toOldCsarId())) {
      LOGGER.warn("Could not clear CSAR information about {} from ToscaEngine!", csarId.csarName());
      errors.add("Could not delete TOSCA data.");
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

