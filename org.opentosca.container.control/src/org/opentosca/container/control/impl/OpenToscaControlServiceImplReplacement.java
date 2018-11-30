package org.opentosca.container.control.impl;

import static org.opentosca.container.core.model.deployment.process.DeploymentProcessState.*;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.model.tosca.TPlans;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.opentosca.container.control.OpenToscaControlService;
import org.opentosca.container.core.common.NotFoundException;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.engine.IToscaEngineService;
import org.opentosca.container.core.engine.ToscaEngine;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.model.deployment.process.DeploymentProcessOperation;
import org.opentosca.container.core.model.deployment.process.DeploymentProcessState;
import org.opentosca.container.core.model.instance.ServiceTemplateInstanceID;
import org.opentosca.container.core.service.CsarStorageService;
import org.opentosca.container.core.service.DeploymentTracker;
import org.opentosca.container.core.service.ICSARInstanceManagementService;
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
    private static DeploymentTracker deploymentTracker;
    private static IPlanEngineService planEngine;
    private static IPlanInvocationEngine planInvocationEngine;
    private static CsarStorageService storage;
    // used only for instanceIdOfCorrelation
    private static ICSARInstanceManagementService instanceManagement;
    
    @Override
    public boolean invokeToscaProcessing(CsarId csar) {
        LOGGER.debug("Start resolving ServiceTemplates of [{}]", csar.csarName());
        deploymentTracker.storeDeploymentState(csar, TOSCAPROCESSING_ACTIVE);
        // FIXME: We cannot resolve definitions based on the bridge, because the definitions resolution
        // accesses the ToscaMetaFile. That file is not in the imported CSAR representation.
        // Additionally the CsarImporter largely takes care of definitions resolution for us!
        // use new ToscaMetaFileReplacement(csar); to obtain a suitable toscametafile
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
    public boolean registerImplementationArtifacts(CsarId csarId, ServiceTemplateId serviceTemplateId) {
        deploymentTracker.storeDeploymentState(csarId, IA_DEPLOYMENT_ACTIVE);
        if (iAEngine == null) {
            LOGGER.error("IA Engine is not alive!");
            deploymentTracker.storeDeploymentState(csarId, TOSCA_PROCESSED);
            return false;
        }
        
        Csar csar = storage.findById(csarId);
        TServiceTemplate serviceTemplate;
        try {
            serviceTemplate = ToscaEngine.findServiceTemplate(csar, serviceTemplateId.getQName());
        }
        catch (NotFoundException e) {
            LOGGER.warn("Could not find service template [{}] to register IAs in Csar {}", serviceTemplateId, csarId);
            return false;
        }
        LOGGER.info("Invoking IAEngine to process [{}] in CSAR [{}]", serviceTemplate, csarId);
        final List<String> undeployedIAs = iAEngine.deployImplementationArtifacts(csar, serviceTemplate);
        deploymentTracker.storeDeploymentState(csarId, IAS_DEPLOYED);
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
            deploymentTracker.storeDeploymentState(csarId, IAS_DEPLOYED);
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
        return planInvocationEngine.getActiveCorrelationsOfInstance(new ServiceTemplateInstanceID(csar.toOldCsarId(), new QName(serviceTemplate.getId()), (int)instanceId));
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
    public List<String> deleteCsar(CsarId csarId) {
        List<String> errors = new ArrayList<>();
        // FIXME: undeployPlans
        final Csar csar = storage.findById(csarId);
        
        if (!iAEngine.undeployImplementationArtifacts(csar)) {
            LOGGER.warn("Could not delete all ImplementationArtifacts of CSAR {}", csarId.csarName());
            errors.add("Could not undeploy all ImplementationArtifacts.");
        }
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
    
    public void bindCoreDeploymentTrackerService(DeploymentTracker service) {
        LOGGER.debug("Binding CoreDeploymentTrackerService");
        deploymentTracker = service;
    }
    
    public void unbindCoreDeploymentTrackerService(DeploymentTracker service) {
        LOGGER.debug("Unbinding CoreDeploymentTrackerService");
        deploymentTracker = null;
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
        deploymentTracker.storeDeploymentState(csarId, DeploymentProcessState.IA_DEPLOYMENT_ACTIVE);
        if (iAEngine == null) {
            LOGGER.error("IAEngine is not alive!");
            deploymentTracker.storeDeploymentState(csarId, TOSCA_PROCESSED);
            return false;
        } 
        
        Csar csar= storage.findById(csarId);
        LOGGER.trace("Invoking IAEngine for processing ServiceTemplate [{}] for CSAR [{}]", serviceTemplate.getId(), csarId.csarName());
        final List<String> undeployedIAs = iAEngine.deployImplementationArtifacts(csar, serviceTemplate);
        if (!undeployedIAs.isEmpty()) {
            undeployedIAs.forEach(s -> LOGGER.info("ImplementationArtifact \"{}\" was not deployed", s));
        } else {
            LOGGER.trace("Deployment of ImplementationArtifacts was successful");
        }
        deploymentTracker.storeDeploymentState(csarId, IAS_DEPLOYED);
        return true;
    }

    @Override
    public boolean invokePlanDeployment(CsarId csar, TServiceTemplate serviceTemplate) {
        deploymentTracker.storeDeploymentState(csar, PLAN_DEPLOYMENT_ACTIVE);
        final List<TPlan> undeployedPlans = new ArrayList<>();
        LOGGER.trace("Invoking PlanEngine to process Plans");
        if (planEngine == null) {
            LOGGER.error("PlanEngine is not alive!");
            deploymentTracker.storeDeploymentState(csar, IAS_DEPLOYED);
            return false;
        }
        
        if (serviceTemplate == null) {
            LOGGER.warn("Could not find the main ServiceTemplate");
            deploymentTracker.storeDeploymentState(csar, IAS_DEPLOYED);
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
            deploymentTracker.storeDeploymentState(csar, IAS_DEPLOYED);
            return false;
        }
        
        LOGGER.trace("The deployment of management plans for ServiceTemplate \"{}\" inside CSAR [{}] was successful", serviceTemplate.getId(), csar.csarName());
        deploymentTracker.storeDeploymentState(csar, PLANS_DEPLOYED);
        return true;
    }

    @Deprecated
    @Override
    public boolean invokeIADeployment(CsarId csarId, QName qname) {
        Csar csar = storage.findById(csarId);
        final Optional<TServiceTemplate> serviceTemplate = csar.serviceTemplates().stream()
            .filter(st -> st.getId().equals(qname.toString()))
            .findFirst();
        return serviceTemplate.isPresent() ? invokeIADeployment(csarId, serviceTemplate.get()) : false;
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
        return serviceTemplate.isPresent() ? invokeIADeployment(csarId, serviceTemplate.get()) : false;
    }

    @Deprecated
    @Override
    public List<QName> getAllContainedServiceTemplates(CsarId csarid) {
        return storage.findById(csarid).serviceTemplates().stream()
            .map(TServiceTemplate::getId)
            .map(QName::new).collect(Collectors.toList());
    }
}

