package org.opentosca.opentoscacontrol.service.impl;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.opentosca.core.deployment.tracker.service.ICoreDeploymentTrackerService;
import org.opentosca.core.endpoint.service.ICoreEndpointService;
import org.opentosca.core.file.service.ICoreFileService;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.core.model.deployment.process.DeploymentProcessOperation;
import org.opentosca.core.model.deployment.process.DeploymentProcessState;
import org.opentosca.core.model.repository.service.ICoreModelRepositoryService;
import org.opentosca.csarinstancemanagement.service.ICSARInstanceManagementService;
import org.opentosca.exceptions.SystemException;
import org.opentosca.exceptions.UserException;
import org.opentosca.iaengine.service.IIAEngineService;
import org.opentosca.model.csarinstancemanagement.ServiceTemplateInstanceID;
import org.opentosca.model.tosca.TPlan;
import org.opentosca.model.tosca.TPlans;
import org.opentosca.model.tosca.TServiceTemplate;
import org.opentosca.model.tosca.extension.transportextension.TPlanDTO;
import org.opentosca.opentoscacontrol.service.IOpenToscaControlService;
import org.opentosca.planengine.service.IPlanEngineService;
import org.opentosca.planinvocationengine.service.IPlanInvocationEngine;
import org.opentosca.toscaengine.service.IToscaEngineService;
import org.opentosca.toscaengine.xmlserializer.service.IXMLSerializerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The instance of this interface is used by org.opentosca.containerapi which
 * invokes each step in the deployment process. For handling the states of
 * processing of each CSAR, this component uses the
 * org.opentosca.core.deployment.tracker.service.ICoreDeploymentTrackerService
 * to read and set the current state of a certain CSAR and provides a HashSet
 * with the possible process invocations for a certain CSAR.
 *
 * @author Christian Endres - endrescn@studi.informatik.uni-stuttgart.de
 *
 */
public class OpenToscaControlServiceImpl implements IOpenToscaControlService {
	
	
	protected static IIAEngineService iAEngine = null;
	protected static IPlanEngineService planEngine = null;
	protected static ICoreFileService fileService = null;
	protected static IToscaEngineService toscaEngine = null;
	protected static IXMLSerializerService xmlSerializerService = null;
	protected static ICoreDeploymentTrackerService coreDeploymentTracker = null;
	protected static ICoreModelRepositoryService modelRepositoryService = null;
	protected static ICoreFileService coreFileService = null;
	protected static ICoreEndpointService endpointService = null;
	protected static IPlanInvocationEngine planInvocationEngine = null;
	protected static ICSARInstanceManagementService instanceManagement = null;
	
	private final Logger LOG = LoggerFactory.getLogger(OpenToscaControlServiceImpl.class);
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean invokeTOSCAProcessing(CSARID csarID) {
		
		LOG.debug("Start the resolving of the ServiceTemplates of the CSAR \"" + csarID + "\".");
		OpenToscaControlServiceImpl.coreDeploymentTracker.storeDeploymentState(csarID, DeploymentProcessState.TOSCAPROCESSING_ACTIVE);
		
		// start the resolving and store the state according to success
		if (OpenToscaControlServiceImpl.toscaEngine.resolveDefinitions(csarID)) {
			LOG.info("Processing of the Definitions completed.");
			OpenToscaControlServiceImpl.coreDeploymentTracker.storeDeploymentState(csarID, DeploymentProcessState.TOSCA_PROCESSED);
		} else {
			LOG.error("Processing of the Definitions failed!");
			OpenToscaControlServiceImpl.coreDeploymentTracker.storeDeploymentState(csarID, DeploymentProcessState.STORED);
			return false;
		}
		
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean invokeIADeployment(CSARID csarID, QName serviceTemplateID) {
		
		OpenToscaControlServiceImpl.coreDeploymentTracker.storeDeploymentState(csarID, DeploymentProcessState.IA_DEPLOYMENT_ACTIVE);
		
		if (OpenToscaControlServiceImpl.iAEngine != null) {
			
			// invoke IAEngine
			LOG.info("Invoke the IAEngine for processing the ServiceTemplate \"" + serviceTemplateID + "\" of the CSAR \"" + csarID + "\".");
			
			List<String> undeployedIAs = OpenToscaControlServiceImpl.iAEngine.deployImplementationArtifacts(csarID, serviceTemplateID);
			
			if (undeployedIAs == null) {
				LOG.error("It was not possible to deploy the ServiceTemplate \"" + serviceTemplateID + "\" of the CSAR \"" + csarID + "\".");
				OpenToscaControlServiceImpl.coreDeploymentTracker.storeDeploymentState(csarID, DeploymentProcessState.TOSCA_PROCESSED);
				return false;
			} else if (undeployedIAs.size() > 0) {
				OpenToscaControlServiceImpl.coreDeploymentTracker.storeDeploymentState(csarID, DeploymentProcessState.IAS_DEPLOYED);
				for (String undeployedIAName : undeployedIAs) {
					LOG.error("The ImplementationArtifact \"" + undeployedIAName + "\" was not deployed.");
				}
				return true;
			}
			
		} else {
			LOG.error("IAEngine is not alive!");
			OpenToscaControlServiceImpl.coreDeploymentTracker.storeDeploymentState(csarID, DeploymentProcessState.TOSCA_PROCESSED);
			return false;
		}
		
		OpenToscaControlServiceImpl.coreDeploymentTracker.storeDeploymentState(csarID, DeploymentProcessState.IAS_DEPLOYED);
		LOG.info("Deployment of the ImplementationArtifacts was successfull.");
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean invokePlanDeployment(CSARID csarID, QName serviceTemplateID) {
		
		OpenToscaControlServiceImpl.coreDeploymentTracker.storeDeploymentState(csarID, DeploymentProcessState.PLAN_DEPLOYMENT_ACTIVE);
		
		// list of failure - not deployed artifacts
		List<TPlan> listOfUndeployedPlans = new ArrayList<TPlan>();
		
		// invoke PlanEngine
		LOG.info("Invoke the PlanEngine for processing the Plans.");
		if (OpenToscaControlServiceImpl.planEngine != null) {
			
			TServiceTemplate mainServiceTemplate = (TServiceTemplate) OpenToscaControlServiceImpl.toscaEngine.getToscaReferenceMapper().getJAXBReference(csarID, serviceTemplateID);
			
			if (mainServiceTemplate == null) {
				LOG.error("Did not found the main ServiceTemplate \"" + serviceTemplateID + "\".");
				OpenToscaControlServiceImpl.coreDeploymentTracker.storeDeploymentState(csarID, DeploymentProcessState.IAS_DEPLOYED);
				return false;
			}
			
			if (mainServiceTemplate.getPlans() == null) {
				LOG.info("No plans to process ...");
				return true;
			}
			
			LOG.debug("PlanEngine is alive!");
			
			TPlans plans = mainServiceTemplate.getPlans();
			
			String namespace = plans.getTargetNamespace();
			
			if (namespace == null) {
				// the Plans element has no targetNamespace defined fallback to
				// ServiceTemplate namespace
				namespace = serviceTemplateID.getNamespaceURI();
			}
			
			for (TPlan plan : plans.getPlan()) {
				if (!OpenToscaControlServiceImpl.planEngine.deployPlan(plan, namespace, csarID)) {
					listOfUndeployedPlans.add(plan);
				}
			}
			
			// check the success of the plan deployment
			if (listOfUndeployedPlans.size() != 0) {
				LOG.error("Plan deployment failed!");
				OpenToscaControlServiceImpl.coreDeploymentTracker.storeDeploymentState(csarID, DeploymentProcessState.IAS_DEPLOYED);
				return false;
			}
			
		} else {
			LOG.error("PlanEngine is not alive!");
			OpenToscaControlServiceImpl.coreDeploymentTracker.storeDeploymentState(csarID, DeploymentProcessState.IAS_DEPLOYED);
			return false;
		}
		
		LOG.info("The deployment of the management plans of the Service Template " + serviceTemplateID.toString() + "\" inside of the CSAR \"" + csarID + "\" was successfull.");
		OpenToscaControlServiceImpl.coreDeploymentTracker.storeDeploymentState(csarID, DeploymentProcessState.PLANS_DEPLOYED);
		
		OpenToscaControlServiceImpl.endpointService.printPlanEndpoints();
		
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 * @throws UnsupportedEncodingException 
	 */
	@Override
	public String invokePlanInvocation(CSARID csarID, QName serviceTemplateId, int csarInstanceID, TPlanDTO plan) throws UnsupportedEncodingException {
		
		LOG.info("Invoke Plan Invocation!");
		
		String correlationID = OpenToscaControlServiceImpl.planInvocationEngine.invokePlan(csarID, serviceTemplateId, csarInstanceID, plan);
		if (null != correlationID) {
			LOG.info("The Plan Invocation was successfull!!!");
		} else {
			LOG.error("The Plan Invocation was not successfull!!!");
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
	public List<String> deleteCSAR(CSARID csarID) {
		
		List<String> errors = new ArrayList<String>();
		
		// TODO following code should be active again
		// if
		// (!OpenToscaControlServiceImpl.instanceManagement.getInstancesOfCSAR(csarID).isEmpty())
		// {
		// // There are instances, thus deletion is not legal.
		// LOG.error("CSAR \"{}\" has instances.", csarID);
		// errors.add("CSAR has instances.");
		// return errors;
		// }
		
		if (!OpenToscaControlServiceImpl.iAEngine.undeployImplementationArtifacts(csarID)) {
			LOG.warn("It was not possible to delete all ImplementationArtifacts of the CSAR \"" + csarID + ".");
			errors.add("Could not undeploy all ImplementationArtifacts.");
		}
		
		// Delete operation is legal, thus continue.
		if (!OpenToscaControlServiceImpl.toscaEngine.clearCSARContent(csarID)) {
			LOG.error("It was not possible to delete all content of the CSAR \"" + csarID + "\" inside the ToscaEngine.");
			errors.add("Could not delete TOSCA data.");
		}
		
		OpenToscaControlServiceImpl.coreDeploymentTracker.deleteDeploymentState(csarID);
		OpenToscaControlServiceImpl.endpointService.removeEndpoints(csarID);
		
		try {
			OpenToscaControlServiceImpl.fileService.deleteCSAR(csarID);
		} catch (SystemException | UserException e) {
			// e.printStackTrace();
			LOG.error("The file service could not delete all data of the CSAR \"{}\". ", csarID, e);
			errors.add("Could not delete CSAR files.");
		}
		
		if (errors.isEmpty()) {
			LOG.info("Contents of CSAR \"" + csarID + "\" deleted.");
		} else {
			String errorList = "";
			for (String err : errors) {
				errorList = errorList + err + "\\n";
			}
			LOG.error("Errors while deleting: " + errorList);
		}
		
		return errors;
		
	}
	
	@Override
	public int getCSARInstanceIDForCorrelationID(String correlationID) {
		return OpenToscaControlServiceImpl.instanceManagement.getInstanceForCorrelation(correlationID).getInstanceID();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getCorrelationsOfServiceTemplateInstance(ServiceTemplateInstanceID csarInstanceID) {
		return OpenToscaControlServiceImpl.planInvocationEngine.getActiveCorrelationsOfInstance(csarInstanceID);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public TPlanDTO getActivePlanOfInstance(ServiceTemplateInstanceID csarInstanceID, String correlationID) {
		return OpenToscaControlServiceImpl.planInvocationEngine.getActivePublicPlanOfInstance(csarInstanceID, correlationID);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<QName> getAllContainedServiceTemplates(CSARID csarID) {
		return OpenToscaControlServiceImpl.toscaEngine.getToscaReferenceMapper().getServiceTemplateIDsContainedInCSAR(csarID);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<DeploymentProcessOperation> getExecutableDeploymentProcessOperations(CSARID csarID) {
		
		Set<DeploymentProcessOperation> operationList = new HashSet<DeploymentProcessOperation>();
		
		// add all possible operations for a passed CSAR
		switch (OpenToscaControlServiceImpl.coreDeploymentTracker.getDeploymentState(csarID)) {
		case STORED:
			
			operationList.add(DeploymentProcessOperation.PROCESS_TOSCA);
			
			break;
			
		case TOSCA_PROCESSED:
			
			operationList.add(DeploymentProcessOperation.PROCESS_TOSCA);
			operationList.add(DeploymentProcessOperation.INVOKE_IA_DEPL);
			break;
			
		case IAS_DEPLOYED:
			
			operationList.add(DeploymentProcessOperation.PROCESS_TOSCA);
			operationList.add(DeploymentProcessOperation.INVOKE_IA_DEPL);
			operationList.add(DeploymentProcessOperation.INVOKE_PLAN_DEPL);
			break;
			
		case PLANS_DEPLOYED:
			
			operationList.add(DeploymentProcessOperation.PROCESS_TOSCA);
			operationList.add(DeploymentProcessOperation.INVOKE_IA_DEPL);
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
	public Boolean setDeploymentProcessStateStored(CSARID csarID) {
		LOG.trace("Setting CSAR {} to state \"{}\"", csarID, DeploymentProcessState.STORED.name());
		return OpenToscaControlServiceImpl.coreDeploymentTracker.storeDeploymentState(csarID, DeploymentProcessState.STORED);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public DeploymentProcessState getDeploymentProcessState(CSARID csarID) {
		return OpenToscaControlServiceImpl.coreDeploymentTracker.getDeploymentState(csarID);
	}
	
	protected void bindIAEngine(IIAEngineService service) {
		if (service == null) {
			LOG.error("Service IAEngine is null.");
		} else {
			LOG.debug("Bind of the IAEngine.");
			OpenToscaControlServiceImpl.iAEngine = service;
		}
	}
	
	protected void unbindIAEngine(IIAEngineService service) {
		LOG.debug("Unbind of the IAEngine.");
		OpenToscaControlServiceImpl.iAEngine = null;
	}
	
	protected void bindPlanEngine(IPlanEngineService service) {
		if (service == null) {
			LOG.error("Service PlanEngine is null.");
		} else {
			LOG.debug("Bind of the PlanEngine.");
			OpenToscaControlServiceImpl.planEngine = service;
		}
	}
	
	protected void unbindPlanEngine(IPlanEngineService service) {
		LOG.debug("Unbind of the PlanEngine.");
		OpenToscaControlServiceImpl.planEngine = null;
	}
	
	protected void bindFileService(ICoreFileService service) {
		if (service == null) {
			LOG.error("Service FileService is null.");
		} else {
			LOG.debug("Bind of the FileService.");
			OpenToscaControlServiceImpl.fileService = service;
		}
	}
	
	protected void unbindFileService(ICoreFileService service) {
		LOG.debug("Unbind of the FileService.");
		OpenToscaControlServiceImpl.fileService = null;
	}
	
	protected void bindToscaEngine(IToscaEngineService service) {
		if (service == null) {
			LOG.error("Service ToscaEngine is null.");
		} else {
			LOG.debug("Bind of the ToscaEngine.");
			OpenToscaControlServiceImpl.toscaEngine = service;
		}
	}
	
	protected void unbindToscaEngine(IToscaEngineService service) {
		LOG.debug("Unbind of the ToscaEngine.");
		OpenToscaControlServiceImpl.toscaEngine = null;
	}
	
	protected void bindDeploymentTrackerService(ICoreDeploymentTrackerService service) {
		if (service == null) {
			LOG.error("Service CoreDeploymentTracker is null.");
		} else {
			LOG.debug("Bind of the Core Deployment Tracker.");
			OpenToscaControlServiceImpl.coreDeploymentTracker = service;
		}
	}
	
	protected void unbindDeploymentTrackerService(ICoreDeploymentTrackerService service) {
		LOG.debug("Unbind of the Core Deployment Tracker.");
		OpenToscaControlServiceImpl.coreDeploymentTracker = null;
	}
	
	protected void bindModelRepo(ICoreModelRepositoryService service) {
		if (service == null) {
			LOG.error("Service ModelRepository is null.");
		} else {
			LOG.debug("Bind of the ModelRepository.");
			OpenToscaControlServiceImpl.modelRepositoryService = service;
		}
	}
	
	protected void unbindModelRepo(ICoreModelRepositoryService service) {
		LOG.debug("Unbind of the ModelRepository.");
		OpenToscaControlServiceImpl.modelRepositoryService = null;
	}
	
	protected void bindIXMLSerializerService(IXMLSerializerService service) {
		if (service == null) {
			LOG.error("Service IXMLSerializerService is null.");
		} else {
			LOG.debug("Bind of the IXMLSerializerService.");
			OpenToscaControlServiceImpl.xmlSerializerService = service;
		}
	}
	
	protected void unbindIXMLSerializerService(IXMLSerializerService service) {
		LOG.debug("Unbind of the IXMLSerializerService.");
		OpenToscaControlServiceImpl.xmlSerializerService = null;
	}
	
	protected void bindEndpointService(ICoreEndpointService service) {
		if (service == null) {
			LOG.error("Service ICoreEndpointService is null.");
		} else {
			LOG.debug("Bind of the ICoreEndpointService.");
			OpenToscaControlServiceImpl.endpointService = service;
		}
	}
	
	protected void unbindEndpointService(ICoreEndpointService service) {
		LOG.debug("Unbind of the ICoreEndpointService.");
		OpenToscaControlServiceImpl.endpointService = null;
	}
	
	protected void bindPlanInvocationEngine(IPlanInvocationEngine service) {
		if (service == null) {
			LOG.error("Service planInvocationEngine is null.");
		} else {
			LOG.debug("Bind of the planInvocationEngine.");
			OpenToscaControlServiceImpl.planInvocationEngine = service;
		}
	}
	
	protected void unbindPlanInvocationEngine(IPlanInvocationEngine service) {
		LOG.debug("Unbind of the planInvocationEngine.");
		OpenToscaControlServiceImpl.planInvocationEngine = null;
	}
	
	protected void bindICSARInstanceManagementService(ICSARInstanceManagementService service) {
		if (service == null) {
			LOG.error("Service ICSARInstanceManagementService is null.");
		} else {
			LOG.debug("Bind of the ICSARInstanceManagementService.");
			OpenToscaControlServiceImpl.instanceManagement = service;
		}
	}
	
	protected void unbindICSARInstanceManagementService(ICSARInstanceManagementService service) {
		LOG.debug("Unbind of the ICSARInstanceManagementService.");
		OpenToscaControlServiceImpl.instanceManagement = null;
	}
}
