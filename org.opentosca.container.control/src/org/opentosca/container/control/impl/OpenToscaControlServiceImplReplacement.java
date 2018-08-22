package org.opentosca.container.control.impl;

import static org.opentosca.container.core.model.deployment.process.DeploymentProcessState.*;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.model.deployment.process.DeploymentProcessOperation;
import org.opentosca.container.core.model.deployment.process.DeploymentProcessState;
import org.opentosca.container.core.model.instance.ServiceTemplateInstanceID;
import org.opentosca.container.core.service.CsarStorageService;
import org.opentosca.container.core.service.ICSARInstanceManagementService;
import org.opentosca.container.core.service.ICoreDeploymentTrackerService;
import org.opentosca.container.core.service.IPlanInvocationEngine;
import org.opentosca.container.core.tosca.extension.TPlanDTO;
import org.opentosca.container.engine.ia.IIAEngineService;
import org.opentosca.container.engine.plan.IPlanEngineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenToscaControlServiceImplReplacement implements OpenToscaControlService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenToscaControlServiceImplReplacement.class);
    
    private static IToscaEngineService toscaEngine;
    private static IIAEngineService iAEngine;
    private static ICoreDeploymentTrackerService coreDeploymentTracker;
    private static IPlanEngineService planEngine;
    private static IPlanInvocationEngine planInvocationEngine;
    private static CsarStorageService storage;
    // used only for instanceIdOfCorrelation
    private static ICSARInstanceManagementService instanceManagement;
    
    @Override
    public boolean invokeToscaProcessing(CsarId csar) {
        LOGGER.debug("Start resolving ServiceTemplates of [{}]", csar.csarName());
        CSARID bridge = csar.toOldCsarId(); 
        coreDeploymentTracker.storeDeploymentState(bridge, TOSCAPROCESSING_ACTIVE);
        // FIXME: We cannot resolve definitions based on the bridge, because the definitions resolution
        // accesses the ToscaMetaFile. That file is not in the imported CSAR representation.
        // Additionally the CsarImporter largely takes care of definitions resolution for us!
        if (/*toscaEngine.resolveDefinitions(bridge)*/ true) {
            LOGGER.info("Processing of Definitions completed successfully for [{}]", csar.csarName());
            coreDeploymentTracker.storeDeploymentState(bridge, TOSCA_PROCESSED);
            return true;
        } else {
            LOGGER.info("Processing of Definitions failed for [{}]", csar.csarName());
            coreDeploymentTracker.storeDeploymentState(bridge, STORED);
            return false;
        }
    }

    @Override
    public boolean registerImplementationArtifacts(CsarId csar, ServiceTemplateId serviceTemplate) {
        CSARID bridge = csar.toOldCsarId();
        coreDeploymentTracker.storeDeploymentState(bridge, IA_DEPLOYMENT_ACTIVE);
        if (iAEngine == null) {
            LOGGER.error("IA Engine is not alive!");
            coreDeploymentTracker.storeDeploymentState(bridge, TOSCA_PROCESSED);
            return false;
        }
        
        LOGGER.info("Invoking IAEngine to process [{}] in CSAR [{}]", serviceTemplate, csar);
        final List<String> undeployedIAs = iAEngine.deployImplementationArtifacts(bridge, serviceTemplate.getQName());
        if (undeployedIAs == null) {
            LOGGER.error("Deployment of [{}] for CSAR [{}] failed", serviceTemplate, csar);
            coreDeploymentTracker.storeDeploymentState(bridge, TOSCA_PROCESSED);
            return false;
        } 
        coreDeploymentTracker.storeDeploymentState(bridge, IAS_DEPLOYED);
        if (!undeployedIAs.isEmpty()) {
            for (final String failedIA : undeployedIAs) {
                LOGGER.error("Could not deploy ImplementationArtifact {}", failedIA);
            }
            return true;
        }
        LOGGER.info("Successfully deployed ImplementationArtifacts");
        return true;
    }

    @Override
    public boolean generatePlans(CsarId csarId, ServiceTemplateId serviceTemplate) {
        CSARID bridge = csarId.toOldCsarId();
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
        coreDeploymentTracker.storeDeploymentState(bridge, PLAN_DEPLOYMENT_ACTIVE);
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
            if (!planEngine.deployPlan(plan, namespace, bridge)) {
                undeployed.add(plan);
            }
        }
        
        if (!undeployed.isEmpty()) {
            LOGGER.error("Plan deployment failed");
            coreDeploymentTracker.storeDeploymentState(bridge, IAS_DEPLOYED);
            return false;
        }
        LOGGER.info("Successfully deployeed management plans of [{}] in CSAR [{}]", serviceTemplate, csarId);
        coreDeploymentTracker.storeDeploymentState(bridge, PLANS_DEPLOYED);
        // endpointService.printPlanEndpoints();
        return true;
    }

    @Override
    public String invokePlanInvocation(CsarId csar, ServiceTemplateId serviceTemplate, long instanceId,
                                       TPlanDTO plan) throws UnsupportedEncodingException {
        if (planInvocationEngine == null) {
            LOGGER.error("PlanInvocationEngine is not available!");
            return null;
        }
        LOGGER.info("Invoking Plan [{}]", plan.getName());
        final String correlationId = planInvocationEngine.invokePlan(csar.toOldCsarId(), serviceTemplate.getQName(), instanceId, plan);
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
        
        switch (coreDeploymentTracker.getDeploymentState(csar.toOldCsarId())) {
            case IAS_DEPLOYED:
            case PLANS_DEPLOYED:
                operations.add(DeploymentProcessOperation.INVOKE_PLAN_DEPL);
                // intentional fallthrough
            case TOSCA_PROCESSED:
                operations.add(DeploymentProcessOperation.INVOKE_IA_DEPL);
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
        return coreDeploymentTracker.storeDeploymentState(csar.toOldCsarId(), STORED);
    }

    @Override
    public DeploymentProcessState currentDeploymentProcessState(CsarId csar) {
        return coreDeploymentTracker.getDeploymentState(csar.toOldCsarId());
    }

    @Override
    // FIXME investigate why old ControlService sometimes took long instanceIds
    public List<String> correlationsForServiceTemplateInstance(CsarId csar, ServiceTemplateId serviceTemplate,
                                                               long instanceId) {
        return planInvocationEngine.getActiveCorrelationsOfInstance(new ServiceTemplateInstanceID(csar.toOldCsarId(), serviceTemplate.getQName(), (int)instanceId));
    }

    @Override
    public TPlanDTO getActivePlanOfInstance(CsarId csar, ServiceTemplateId serviceTemplate, long instanceId,
                                            String correlationId) {
        final ServiceTemplateInstanceID stInstanceId = new ServiceTemplateInstanceID(csar.toOldCsarId(), serviceTemplate.getQName(), (int)instanceId);
        return planInvocationEngine.getActivePublicPlanOfInstance(stInstanceId, correlationId);
    }

    @Override
    public long instanceIdOfCorrelation(String correlationId) {
        return instanceManagement.getInstanceForCorrelation(correlationId).getInstanceID();
    }
    
    @Override
    public List<String> deleteCsar(CsarId csar) {
        List<String> errors = new ArrayList<>();
        // FIXME: undeployPlans
        if (!iAEngine.undeployImplementationArtifacts(csar.toOldCsarId())) {
            LOGGER.warn("Could not delete all ImplementationArtifacts of CSAR {}", csar.csarName());
            errors.add("Could not undeploy all ImplementationArtifacts.");
        }
        if (!toscaEngine.clearCSARContent(csar.toOldCsarId())) {
            LOGGER.warn("Could not clear CSAR information about {} from ToscaEngine!", csar.csarName());
            errors.add("Could not delete TOSCA data.");
        }
         coreDeploymentTracker.deleteDeploymentState(csar.toOldCsarId());
        // FIXME removeEndpoints
        try {
            storage.deleteCSAR(csar);
        } catch (UserException | SystemException e) {
            errors.add(e.getMessage());
        }
        return errors;
    }

    public void bindIAEngineService(IIAEngineService service) {
        LOGGER.debug("Binding IAEngineService");
        iAEngine = service;
    }
    
    public void unbindIAEngineService(IIAEngineService service) {
        LOGGER.debug("Unbinding IAEngineService");
        iAEngine = null;
    }
    
    public void bindToscaEngineService(IToscaEngineService service) {
        LOGGER.debug("Binding ToscaEngineService");
        toscaEngine = service;
    }
    
    public void unbindToscaEngineService(IToscaEngineService service) {
        LOGGER.debug("Unbinding ToscaEngineService");
        toscaEngine = null;
    }
    
    public void bindCoreDeploymentTrackerService(ICoreDeploymentTrackerService service) {
        LOGGER.debug("Binding CoreDeploymentTrackerService");
        coreDeploymentTracker = service;
    }
    
    public void unbindCoreDeploymentTrackerService(ICoreDeploymentTrackerService service) {
        LOGGER.debug("Unbinding CoreDeploymentTrackerService");
        coreDeploymentTracker = null;
    }
    
    public void bindPlanEngineService(IPlanEngineService service) {
        LOGGER.debug("Binding PlanEngineService");
        planEngine = service;
    }
    
    public void unbindPlanEngineService(IPlanEngineService service) {
        LOGGER.debug("Unbinding PlanEngineService");
        planEngine = null;
    }
    
    public void bindPlanInvocationEngine(IPlanInvocationEngine service) {
        LOGGER.debug("Binding PlanInvocationEngine");
        planInvocationEngine = service;
    }
    
    public void unbindPlanInvocationEngine(IPlanInvocationEngine service) {
        LOGGER.debug("Unbinding PlanInvocationEngine");
        planInvocationEngine = null;
    }
    
    public void bindCsarStorageService(CsarStorageService service) {
        LOGGER.debug("Binding CsarStorageService");
        storage = service;
    }
    
    public void unbindCsarStorageService(CsarStorageService service) {
        LOGGER.debug("Unbinding CsarStorageService");
        storage = null;
    }
    
    public void bindInstanceManagementService(ICSARInstanceManagementService service) {
        LOGGER.debug("Binding InstanceManagementService");
        instanceManagement = service;
    }
    
    public void unbindInstanceManagementService(ICSARInstanceManagementService service) {
        LOGGER.debug("Unbinding InstanceManagementService");
        instanceManagement = null;
    }

    @Override
    public boolean invokeIADeployment(CsarId csarId, TServiceTemplate serviceTemplate) {
        CSARID bridge = csarId.toOldCsarId();
        coreDeploymentTracker.storeDeploymentState(bridge, DeploymentProcessState.IA_DEPLOYMENT_ACTIVE);
        if (iAEngine == null) {
            LOGGER.error("IAEngine is not alive!");
            coreDeploymentTracker.storeDeploymentState(bridge, TOSCA_PROCESSED);
            return false;
        }
        LOGGER.trace("Invoking IAEngine for processing ServiceTemplate [{}] for CSAR [{}]", serviceTemplate.getId(), csarId.csarName());
        final List<String> undeployedIAs = iAEngine.deployImplementationArtifacts(bridge, new QName(serviceTemplate.getId()));
        if (undeployedIAs == null) {
            LOGGER.info("Failed to deploy ServiceTemplate [{}] for CSAR [{}]", serviceTemplate.getId(), csarId.csarName());
            coreDeploymentTracker.storeDeploymentState(bridge, TOSCA_PROCESSED);
            return false;
        }
        if (!undeployedIAs.isEmpty()) {
            undeployedIAs.forEach(s -> LOGGER.info("ImplementationArtifact \"{}\" was not deployed", s));
        } else {
            LOGGER.trace("Deployment of ImplementationArtifacts was successful");
        }
        coreDeploymentTracker.storeDeploymentState(bridge, IAS_DEPLOYED);
        return true;
    }

    @Override
    public boolean invokePlanDeployment(CsarId csarId, TServiceTemplate serviceTemplate) {
        CSARID bridge = csarId.toOldCsarId();
        coreDeploymentTracker.storeDeploymentState(bridge, PLAN_DEPLOYMENT_ACTIVE);
        final List<TPlan> undeployedPlans = new ArrayList<>();
        LOGGER.trace("Invoking PlanEngine to process Plans");
        if (planEngine == null) {
            LOGGER.error("PlanEngine is not alive!");
            coreDeploymentTracker.storeDeploymentState(bridge, IAS_DEPLOYED);
            return false;
        }
        
        if (serviceTemplate == null) {
            LOGGER.warn("Could not find the main ServiceTemplate");
            coreDeploymentTracker.storeDeploymentState(bridge, IAS_DEPLOYED);
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
            if (!planEngine.deployPlan(plan, namespace, bridge)) {
                undeployedPlans.add(plan);
            }
        }
        
        if (!undeployedPlans.isEmpty()) {
            LOGGER.warn("Plan deployment failed!");
            coreDeploymentTracker.storeDeploymentState(bridge, IAS_DEPLOYED);
            return false;
        }
        
        LOGGER.trace("The deployment of management plans for ServiceTemplate \"{}\" inside CSAR [{}] was successful", serviceTemplate.getId(), csarId.csarName());
        coreDeploymentTracker.storeDeploymentState(bridge, PLANS_DEPLOYED);
        
        // there used to be a debug print for all endpoints here. We just don't because that's another dependency
        return true;
    }
}

