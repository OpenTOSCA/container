package org.opentosca.container.control.impl;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.opentosca.container.control.IOpenToscaControlService;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.engine.IToscaEngineService;
import org.opentosca.container.core.engine.xml.IXMLSerializerService;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.model.deployment.process.DeploymentProcessOperation;
import org.opentosca.container.core.model.deployment.process.DeploymentProcessState;
import org.opentosca.container.core.service.ICoreDeploymentTrackerService;
import org.opentosca.container.core.service.ICoreEndpointService;
import org.opentosca.container.core.service.ICoreFileService;
import org.opentosca.container.core.service.ICoreModelRepositoryService;
import org.opentosca.container.core.service.IPlanInvocationEngine;
import org.opentosca.container.core.tosca.extension.TPlanDTO;
import org.opentosca.container.core.tosca.model.TPlan;
import org.opentosca.container.core.tosca.model.TPlans;
import org.opentosca.container.core.tosca.model.TServiceTemplate;
import org.opentosca.container.engine.plan.IPlanEngineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The instance of this interface is used by org.opentosca.container.api which invokes each step in
 * the deployment process. For handling the states of processing of each CSAR, this component uses
 * the org.opentosca.core.deployment.tracker.service.ICoreDeploymentTrackerService to read and set
 * the current state of a certain CSAR and provides a HashSet with the possible process invocations
 * for a certain CSAR.
 */
public class OpenToscaControlServiceImpl implements IOpenToscaControlService {

    protected static IPlanEngineService planEngine = null;
    protected static ICoreFileService fileService = null;
    protected static IToscaEngineService toscaEngine = null;
    protected static IXMLSerializerService xmlSerializerService = null;
    protected static ICoreDeploymentTrackerService coreDeploymentTracker = null;
    protected static ICoreModelRepositoryService modelRepositoryService = null;
    protected static ICoreFileService coreFileService = null;
    protected static ICoreEndpointService endpointService = null;
    protected static IPlanInvocationEngine planInvocationEngine = null;

    private final Logger LOG = LoggerFactory.getLogger(OpenToscaControlServiceImpl.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean invokeTOSCAProcessing(final CSARID csarID) {

        this.LOG.debug("Start the resolving of the ServiceTemplates of the CSAR \"" + csarID + "\".");
        OpenToscaControlServiceImpl.coreDeploymentTracker.storeDeploymentState(csarID,
                                                                               DeploymentProcessState.TOSCAPROCESSING_ACTIVE);

        // start the resolving and store the state according to success
        if (OpenToscaControlServiceImpl.toscaEngine.resolveDefinitions(csarID)) {
            this.LOG.info("Processing of the Definitions completed.");
            OpenToscaControlServiceImpl.coreDeploymentTracker.storeDeploymentState(csarID,
                                                                                   DeploymentProcessState.TOSCA_PROCESSED);
        } else {
            this.LOG.error("Processing of the Definitions failed!");
            OpenToscaControlServiceImpl.coreDeploymentTracker.storeDeploymentState(csarID,
                                                                                   DeploymentProcessState.STORED);
            return false;
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean invokePlanDeployment(final CSARID csarID, final QName serviceTemplateID) {

        OpenToscaControlServiceImpl.coreDeploymentTracker.storeDeploymentState(csarID,
                                                                               DeploymentProcessState.PLAN_DEPLOYMENT_ACTIVE);

        // list of failure - not deployed artifacts
        final List<TPlan> listOfUndeployedPlans = new ArrayList<>();

        // invoke PlanEngine
        this.LOG.info("Invoke the PlanEngine for processing the Plans.");
        if (OpenToscaControlServiceImpl.planEngine != null) {

            final TServiceTemplate mainServiceTemplate =
                (TServiceTemplate) OpenToscaControlServiceImpl.toscaEngine.getToscaReferenceMapper()
                                                                          .getJAXBReference(csarID, serviceTemplateID);

            if (mainServiceTemplate == null) {
                this.LOG.error("Did not found the main ServiceTemplate \"" + serviceTemplateID + "\".");
                OpenToscaControlServiceImpl.coreDeploymentTracker.storeDeploymentState(csarID,
                                                                                       DeploymentProcessState.TOSCA_PROCESSED);
                return false;
            }

            if (mainServiceTemplate.getPlans() == null) {
                this.LOG.info("No plans to process ...");
                return true;
            }

            this.LOG.debug("PlanEngine is alive!");

            final TPlans plans = mainServiceTemplate.getPlans();

            String namespace = plans.getTargetNamespace();

            if (namespace == null) {
                // the Plans element has no targetNamespace defined fallback to
                // ServiceTemplate namespace
                namespace = serviceTemplateID.getNamespaceURI();
            }

            for (final TPlan plan : plans.getPlan()) {

                if (!OpenToscaControlServiceImpl.planEngine.deployPlan(plan, namespace, csarID)) {
                    listOfUndeployedPlans.add(plan);
                }
            }

            // check the success of the plan deployment
            if (listOfUndeployedPlans.size() != 0) {
                this.LOG.error("Plan deployment failed!");
                OpenToscaControlServiceImpl.coreDeploymentTracker.storeDeploymentState(csarID,
                                                                                       DeploymentProcessState.TOSCA_PROCESSED);
                return false;
            }

        } else {
            this.LOG.error("PlanEngine is not alive!");
            OpenToscaControlServiceImpl.coreDeploymentTracker.storeDeploymentState(csarID,
                                                                                   DeploymentProcessState.TOSCA_PROCESSED);
            return false;
        }

        this.LOG.info("The deployment of the management plans of the Service Template " + serviceTemplateID.toString()
            + "\" inside of the CSAR \"" + csarID + "\" was successfull.");
        OpenToscaControlServiceImpl.coreDeploymentTracker.storeDeploymentState(csarID,
                                                                               DeploymentProcessState.PLANS_DEPLOYED);

        OpenToscaControlServiceImpl.endpointService.printPlanEndpoints();

        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedEncodingException
     */
    @Override
    public String invokePlanInvocation(final CSARID csarID, final QName serviceTemplateId, final long csarInstanceID,
                                       final TPlanDTO plan) throws UnsupportedEncodingException {

        this.LOG.info("Invoke Plan Invocation!");

        final String correlationID = OpenToscaControlServiceImpl.planInvocationEngine.createCorrelationId();

        if (null != correlationID) {
            this.LOG.info("The Plan Invocation was successfull!!!");
            OpenToscaControlServiceImpl.planInvocationEngine.invokePlan(csarID, serviceTemplateId, csarInstanceID, plan,
                                                                        correlationID);
        } else {
            this.LOG.error("The Plan Invocation was not successfull!!!");
            return null;
        }

        return correlationID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<CSARID> getAllStoredCSARs() {

        return OpenToscaControlServiceImpl.fileService.getCSARIDs();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> deleteCSAR(final CSARID csarID) {

        final List<String> errors = new ArrayList<>();

        // TODO following code should be active again
        // if
        // (!OpenToscaControlServiceImpl.instanceManagement.getInstancesOfCSAR(csarID).isEmpty())
        // {
        // // There are instances, thus deletion is not legal.
        // LOG.error("CSAR \"{}\" has instances.", csarID);
        // errors.add("CSAR has instances.");
        // return errors;
        // }

        if (!undeployPlans(csarID)) {
            this.LOG.warn("It was not possible to undeploy all plans of the CSAR \"" + csarID + ".");
            errors.add("Could not undeploy all plans.");
        }

        // Delete operation is legal, thus continue.
        if (!OpenToscaControlServiceImpl.toscaEngine.clearCSARContent(csarID)) {
            this.LOG.error("It was not possible to delete all content of the CSAR \"" + csarID
                + "\" inside the ToscaEngine.");
            errors.add("Could not delete TOSCA data.");
        }

        OpenToscaControlServiceImpl.coreDeploymentTracker.deleteDeploymentState(csarID);

        // Delete all plan endpoints related to this CSAR. IA endpoints are undeployed and deleted
        // by the Management Bus.
        OpenToscaControlServiceImpl.endpointService.removePlanEndpoints(Settings.OPENTOSCA_CONTAINER_HOSTNAME, csarID);

        try {
            OpenToscaControlServiceImpl.fileService.deleteCSAR(csarID);
        }
        catch (SystemException | UserException e) {
            this.LOG.error("The file service could not delete all data of the CSAR \"{}\". ", csarID, e);
            errors.add("Could not delete CSAR files.");
        }

        if (errors.isEmpty()) {
            this.LOG.info("Contents of CSAR \"" + csarID + "\" deleted.");
        } else {
            String errorList = "";
            for (final String err : errors) {
                errorList = errorList + err + "\\n";
            }
            this.LOG.error("Errors while deleting: " + errorList);
        }

        return errors;
    }

    private boolean undeployPlans(final CSARID csarID) {
        final List<TPlan> listOfUndeployedPlans = new ArrayList<>();
        // invoke PlanEngine
        if (OpenToscaControlServiceImpl.toscaEngine.getServiceTemplatesInCSAR(csarID) == null) {
            // nothing to delete
            return true;
        }

        switch (getDeploymentProcessState(csarID)) {
            case STORED:
            case TOSCA_PROCESSED:
            case TOSCAPROCESSING_ACTIVE:
                return true;
            default:
                break;
        }

        for (final QName serviceTemplateID : OpenToscaControlServiceImpl.toscaEngine.getServiceTemplatesInCSAR(csarID)) {

            this.LOG.info("Invoke the PlanEngine for processing the Plans.");
            if (OpenToscaControlServiceImpl.planEngine != null) {

                final TServiceTemplate mainServiceTemplate =
                    (TServiceTemplate) OpenToscaControlServiceImpl.toscaEngine.getToscaReferenceMapper()
                                                                              .getJAXBReference(csarID,
                                                                                                serviceTemplateID);

                if (mainServiceTemplate == null) {
                    this.LOG.error("Did not found the main ServiceTemplate \"" + serviceTemplateID + "\".");
                    OpenToscaControlServiceImpl.coreDeploymentTracker.storeDeploymentState(csarID,
                                                                                           DeploymentProcessState.TOSCA_PROCESSED);
                    return false;
                }

                if (mainServiceTemplate.getPlans() == null) {
                    this.LOG.info("No plans to process ...");
                    return true;
                }

                this.LOG.debug("PlanEngine is alive!");

                final TPlans plans = mainServiceTemplate.getPlans();

                String namespace = plans.getTargetNamespace();

                if (namespace == null) {
                    // the Plans element has no targetNamespace defined fallback
                    // to ServiceTemplate namespace
                    namespace = serviceTemplateID.getNamespaceURI();
                }

                for (final TPlan plan : plans.getPlan()) {
                    if (!OpenToscaControlServiceImpl.planEngine.undeployPlan(plan, namespace, csarID)) {
                        listOfUndeployedPlans.add(plan);
                    }
                }

            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<QName> getAllContainedServiceTemplates(final CSARID csarID) {
        return OpenToscaControlServiceImpl.toscaEngine.getToscaReferenceMapper()
                                                      .getServiceTemplateIDsContainedInCSAR(csarID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<DeploymentProcessOperation> getExecutableDeploymentProcessOperations(final CSARID csarID) {

        final Set<DeploymentProcessOperation> operationList = new HashSet<>();

        // add all possible operations for a passed CSAR
        switch (OpenToscaControlServiceImpl.coreDeploymentTracker.getDeploymentState(csarID)) {
            case STORED:

                operationList.add(DeploymentProcessOperation.PROCESS_TOSCA);

                break;

            case TOSCA_PROCESSED:

                operationList.add(DeploymentProcessOperation.PROCESS_TOSCA);
                operationList.add(DeploymentProcessOperation.INVOKE_PLAN_DEPL);
                break;

            case PLANS_DEPLOYED:

                operationList.add(DeploymentProcessOperation.PROCESS_TOSCA);
                operationList.add(DeploymentProcessOperation.INVOKE_PLAN_DEPL);
                break;

            default:

                // during active processing (states ending with active) there are no
                // operations allowed for a certain CSAR
                break;
        }

        // return possible operations
        return operationList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean setDeploymentProcessStateStored(final CSARID csarID) {
        this.LOG.trace("Setting CSAR {} to state \"{}\"", csarID, DeploymentProcessState.STORED.name());
        return OpenToscaControlServiceImpl.coreDeploymentTracker.storeDeploymentState(csarID,
                                                                                      DeploymentProcessState.STORED);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DeploymentProcessState getDeploymentProcessState(final CSARID csarID) {
        return OpenToscaControlServiceImpl.coreDeploymentTracker.getDeploymentState(csarID);
    }

    protected void bindPlanEngine(final IPlanEngineService service) {
        if (service == null) {
            this.LOG.error("Service PlanEngine is null.");
        } else {
            this.LOG.debug("Bind of the PlanEngine.");
            OpenToscaControlServiceImpl.planEngine = service;
        }
    }

    protected void unbindPlanEngine(final IPlanEngineService service) {
        this.LOG.debug("Unbind of the PlanEngine.");
        OpenToscaControlServiceImpl.planEngine = null;
    }

    protected void bindFileService(final ICoreFileService service) {
        if (service == null) {
            this.LOG.error("Service FileService is null.");
        } else {
            this.LOG.debug("Bind of the FileService.");
            OpenToscaControlServiceImpl.fileService = service;
        }
    }

    protected void unbindFileService(final ICoreFileService service) {
        this.LOG.debug("Unbind of the FileService.");
        OpenToscaControlServiceImpl.fileService = null;
    }

    protected void bindToscaEngine(final IToscaEngineService service) {
        if (service == null) {
            this.LOG.error("Service ToscaEngine is null.");
        } else {
            this.LOG.debug("Bind of the ToscaEngine.");
            OpenToscaControlServiceImpl.toscaEngine = service;
        }
    }

    protected void unbindToscaEngine(final IToscaEngineService service) {
        this.LOG.debug("Unbind of the ToscaEngine.");
        OpenToscaControlServiceImpl.toscaEngine = null;
    }

    protected void bindDeploymentTrackerService(final ICoreDeploymentTrackerService service) {
        if (service == null) {
            this.LOG.error("Service CoreDeploymentTracker is null.");
        } else {
            this.LOG.debug("Bind of the Core Deployment Tracker.");
            OpenToscaControlServiceImpl.coreDeploymentTracker = service;
        }
    }

    protected void unbindDeploymentTrackerService(final ICoreDeploymentTrackerService service) {
        this.LOG.debug("Unbind of the Core Deployment Tracker.");
        OpenToscaControlServiceImpl.coreDeploymentTracker = null;
    }

    protected void bindModelRepo(final ICoreModelRepositoryService service) {
        if (service == null) {
            this.LOG.error("Service ModelRepository is null.");
        } else {
            this.LOG.debug("Bind of the ModelRepository.");
            OpenToscaControlServiceImpl.modelRepositoryService = service;
        }
    }

    protected void unbindModelRepo(final ICoreModelRepositoryService service) {
        this.LOG.debug("Unbind of the ModelRepository.");
        OpenToscaControlServiceImpl.modelRepositoryService = null;
    }

    protected void bindIXMLSerializerService(final IXMLSerializerService service) {
        if (service == null) {
            this.LOG.error("Service IXMLSerializerService is null.");
        } else {
            this.LOG.debug("Bind of the IXMLSerializerService.");
            OpenToscaControlServiceImpl.xmlSerializerService = service;
        }
    }

    protected void unbindIXMLSerializerService(final IXMLSerializerService service) {
        this.LOG.debug("Unbind of the IXMLSerializerService.");
        OpenToscaControlServiceImpl.xmlSerializerService = null;
    }

    protected void bindEndpointService(final ICoreEndpointService service) {
        if (service == null) {
            this.LOG.error("Service ICoreEndpointService is null.");
        } else {
            this.LOG.debug("Bind of the ICoreEndpointService.");
            OpenToscaControlServiceImpl.endpointService = service;
        }
    }

    protected void unbindEndpointService(final ICoreEndpointService service) {
        this.LOG.debug("Unbind of the ICoreEndpointService.");
        OpenToscaControlServiceImpl.endpointService = null;
    }

    protected void bindPlanInvocationEngine(final IPlanInvocationEngine service) {
        if (service == null) {
            this.LOG.error("Service planInvocationEngine is null.");
        } else {
            this.LOG.debug("Bind of the planInvocationEngine.");
            OpenToscaControlServiceImpl.planInvocationEngine = service;
        }
    }

    protected void unbindPlanInvocationEngine(final IPlanInvocationEngine service) {
        this.LOG.debug("Unbind of the planInvocationEngine.");
        OpenToscaControlServiceImpl.planInvocationEngine = null;
    }
}
