package org.opentosca.planengine.plugin.camunda.service.impl;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.opentosca.core.endpoint.service.ICoreEndpointService;
import org.opentosca.core.file.service.ICoreFileService;
import org.opentosca.core.model.artifact.AbstractArtifact;
import org.opentosca.core.model.artifact.file.AbstractFile;
import org.opentosca.core.model.csar.CSARContent;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.core.model.endpoint.wsdl.WSDLEndpoint;
import org.opentosca.exceptions.SystemException;
import org.opentosca.exceptions.UserException;
import org.opentosca.model.tosca.TPlan.PlanModelReference;
import org.opentosca.planengine.plugin.camunda.service.impl.iaenginecopies.CopyOfIAEnginePluginWarTomcatServiceImpl;
import org.opentosca.planengine.plugin.camunda.service.impl.util.Messages;
import org.opentosca.planengine.plugin.service.IPlanEnginePlanRefPluginService;
import org.opentosca.toscaengine.service.IToscaEngineService;
import org.opentosca.util.fileaccess.service.IFileAccessService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CamundaPlanEnginePlugin implements IPlanEnginePlanRefPluginService {

	final private static Logger LOG = LoggerFactory.getLogger(CamundaPlanEnginePlugin.class);

	private ICoreFileService fileService = null;
	private IFileAccessService fileAccessService = null;
	private IToscaEngineService toscaEngineService;
	private ICoreEndpointService endpointService;

	@Override
	public String getLanguageUsed() {
		return Messages.CamundaPlanEnginePlugin_language;
	}

	@Override
	public List<String> getCapabilties() {
		List<String> capabilities = new ArrayList<String>();

		for (String capability : Messages.CamundaPlanEnginePlugin_capabilities.split("[,;]")) {
			capabilities.add(capability.trim());
		}
		return capabilities;
	}

	@Override
	public boolean deployPlanReference(QName planId, PlanModelReference planRef, CSARID csarId) {

		bindServices();

		Path fetchedPlan;
		
		String planName = "";

//		File tempDir;
//		File tempPlan;

		// variable for the (inbound) portType of the process, if this is null
		// till end the process can't be instantiated by the container
		// !!! in this copy, no wsdl is available
		// QName portType = null;

		// retrieve process
		if (this.fileService != null) {

			CSARContent csar = null;

			try {
				csar = this.fileService.getCSAR(csarId);
			} catch (UserException exc) {
				CamundaPlanEnginePlugin.LOG
						.error("Could not get the CSAR from file service. An User Exception occured.", exc);
				return false;
			}

			AbstractArtifact planReference = null;

			// try {
			// TODO
			// planReference =
			// csar.resolveArtifactReference(planRef.getReference());
			planReference = this.toscaEngineService.getPlanModelReferenceAbstractArtifact(csar, planId);
			// } catch (UserException exc) {
			// CamundaPlanEnginePlugin.LOG.error("An User Exception occured.",
			// exc);
			// } catch (SystemException exc) {
			// CamundaPlanEnginePlugin.LOG.error("A System Exception occured.",
			// exc);
			// }

			if (planReference == null) {
				CamundaPlanEnginePlugin.LOG.error("Plan reference '{}' resulted in a null ArtifactReference.",
						planRef.getReference());
				return false;
			}

			if (!planReference.isFileArtifact()) {
				CamundaPlanEnginePlugin.LOG.warn("Only plan references pointing to a file are supported!");
				return false;
			}

			AbstractFile plan = planReference.getFile("");

			if (plan == null) {
				CamundaPlanEnginePlugin.LOG.error("ArtifactReference resulted in null AbstractFile.");
				return false;
			}

			if (!plan.getName().substring(plan.getName().lastIndexOf('.') + 1).equals("war")) {
				CamundaPlanEnginePlugin.LOG.debug("Plan reference is not a WAR file. It was '{}'.", plan.getName());
				return false;
			}

			try {
				fetchedPlan = plan.getFile();

				if (null == fetchedPlan || fetchedPlan.equals("")) {
					CamundaPlanEnginePlugin.LOG.error("No path for plan.");
					return false;
				} else {
					LOG.debug("Plan should be located at {}", fetchedPlan.toString());
				}
				
				if (fetchedPlan.toFile().exists()){
					LOG.debug("Plan file exists at {}", fetchedPlan.toString());
				}
			} catch (SystemException exc) {
				CamundaPlanEnginePlugin.LOG.error("An System Exception occured. File could not be fetched.", exc);
				return false;
			}

			if (this.fileAccessService != null) {
				// creating temporary dir for update
//				tempDir = this.fileAccessService.getTemp();
//				tempPlan = fetchedPlan.toFile();
//				
//
//				if (null == tempPlan || !tempPlan.exists()) {
//					CamundaPlanEnginePlugin.LOG.error("Temporary copy of plan is missing.");
//					return false;
//				}

			} else {
				CamundaPlanEnginePlugin.LOG
						.error("FileAccessService is not available, can't create needed temporary space on disk");
				return false;
			}

		} else {
			CamundaPlanEnginePlugin.LOG.error("Can't fetch relevant files from FileService: FileService not available");
			return false;
		}

		// changing endpoints in WSDLs
		// ODEEndpointUpdater odeUpdater;
		// try {
		// odeUpdater = new ODEEndpointUpdater();
		// portType = odeUpdater.getPortType(planContents);
		// if (!odeUpdater.changeEndpoints(planContents, csarId)) {
		// CamundaPlanEnginePlugin.LOG.error("Not all endpoints used by the plan
		// {} have been changed", planRef.getReference());
		// }
		// } catch (WSDLException e) {
		// CamundaPlanEnginePlugin.LOG.error("Couldn't load ODEEndpointUpdater",
		// e);
		// }

		// update the bpel and bpel4restlight elements (ex.: GET, PUT,..)
		// BPELRESTLightUpdater bpelRestUpdater;
		// try {
		// bpelRestUpdater = new BPELRESTLightUpdater();
		// if (!bpelRestUpdater.changeEndpoints(planContents, csarId)) {
		// // we don't abort deployment here
		// CamundaPlanEnginePlugin.LOG.warn(
		// "Could'nt change all endpoints inside BPEL4RESTLight Elements in the
		// given process {}",
		// planRef.getReference());
		// }
		// } catch (TransformerConfigurationException e) {
		// CamundaPlanEnginePlugin.LOG.error("Couldn't load
		// BPELRESTLightUpdater", e);
		// } catch (ParserConfigurationException e) {
		// CamundaPlanEnginePlugin.LOG.error("Couldn't load
		// BPELRESTLightUpdater", e);
		// } catch (SAXException e) {
		// CamundaPlanEnginePlugin.LOG.error("ParseError: Couldn't parse .bpel
		// file", e);
		// } catch (IOException e) {
		// CamundaPlanEnginePlugin.LOG.error("IOError: Couldn't access .bpel
		// file", e);
		// }

		// package process
		// CamundaPlanEnginePlugin.LOG.info("Prepare deployment of
		// PlanModelReference");
		// BpsConnector connector = new BpsConnector();
		//
		// if (this.fileAccessService != null) {
		// try {
		// if (tempPlan.createNewFile()) {
		// // package the updated files
		// CamundaPlanEnginePlugin.LOG.debug("Packaging plan to {} ",
		// tempPlan.getAbsolutePath());
		// tempPlan = this.fileAccessService.zip(tempDir, tempPlan);
		// } else {
		// CamundaPlanEnginePlugin.LOG.error("Can't package temporary plan for
		// deployment");
		// return false;
		// }
		// } catch (IOException e) {
		// CamundaPlanEnginePlugin.LOG.error("Can't package temporary plan for
		// deployment", e);
		// return false;
		// }
		// }

		// ##################################################################################################################################################
		// ### dirty copy of IAEngine War Tomcat Plugin
		// ### TODO make this pretty
		// ##################################################################################################################################################

		CopyOfIAEnginePluginWarTomcatServiceImpl deployer = new CopyOfIAEnginePluginWarTomcatServiceImpl();
		deployer.deployImplementationArtifact(csarId, new QName("http://www.example.com/ToscaTypes", "WAR"), null, null,
				null, fetchedPlan.toFile(), null);
		// POST http://localhost:8080/engine-rest/process-definition/{id}/start
		URI endpointURI = null;
		try {
			planName = toscaEngineService.getPlanName(csarId, planId);
			endpointURI = new URI("http://localhost:8080/engine-rest/process-definition/"
					+ planName + "/start");
			LOG.debug("Endpoint URI is {}", endpointURI.getPath());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// ##################################################################################################################################################
		// ##################################################################################################################################################

		// deploy process
		// CamundaPlanEnginePlugin.LOG.info("Deploying Plan: {}",
		// tempPlan.getName());
		// String processId = connector.deploy(tempPlan,
		// Messages.CamundaPlanEnginePlugin_bpsAddress,
		// Messages.CamundaPlanEnginePlugin_bpsLoginName,
		// Messages.CamundaPlanEnginePlugin_bpsLoginPw);
		// Map<String, URI> endpoints = connector.getEndpointsForPID(processId,
		// Messages.CamundaPlanEnginePlugin_bpsAddress,
		// Messages.CamundaPlanEnginePlugin_bpsLoginName,
		// Messages.CamundaPlanEnginePlugin_bpsLoginPw);

		// this will be the endpoint the container can use to instantiate the
		// BPEL Process
		// URI endpoint = null;
		// if (endpoints.keySet().size() == 1) {
		// endpoint = (URI) endpoints.values().toArray()[0];
		// } else {
		// for (String partnerLink : endpoints.keySet()) {
		// if (partnerLink.equals("client")) {
		// endpoint = endpoints.get(partnerLink);
		// }
		// }
		// }

		if (endpointURI == null) {
			CamundaPlanEnginePlugin.LOG.warn(
					"No endpoint for Plan {} could be determined, container won't be able to instantiate it",
					planRef.getReference());
			return false;
		}
		//
		// if ((endpoint != null) && (portType != null)) {
		// CamundaPlanEnginePlugin.LOG.debug("Endpoint for ProcessID \"" +
		// processId + "\" is \"" + endpoints + "\".");
		// CamundaPlanEnginePlugin.LOG.info("Deployment of Plan was successfull:
		// {}", tempPlan.getName());
		//
		// // save endpoint
		// WSDLEndpoint wsdlEndpoint = new WSDLEndpoint(endpoint, portType,
		// csarId, planId, null, null);
		//
		// if (this.endpointService != null) {
		// CamundaPlanEnginePlugin.LOG.debug("Store new endpoint!");
		// this.endpointService.storeWSDLEndpoint(wsdlEndpoint);
		
		if(null == endpointService){
			LOG.error("Endpoint serivce is offline.");
		}
		
		WSDLEndpoint point = new WSDLEndpoint();
		point.setCSARId(csarId);
		point.setPlanId(planId);
		point.setIaName(planName);
		point.setURI(endpointURI);
		
		endpointService.storeWSDLEndpoint(point);
		
		
		// } else {
		// CamundaPlanEnginePlugin.LOG.warn(
		// "Couldn't store endpoint {} for plan {}, cause endpoint service is
		// not available",
		// endpoint.toString(), planRef.getReference());
		// return false;
		// }
		// } else {
		// CamundaPlanEnginePlugin.LOG.error("Error while processing plan");
		// if (processId == null) {
		// CamundaPlanEnginePlugin.LOG.error("ProcessId is null");
		// }
		// if (endpoint == null) {
		// CamundaPlanEnginePlugin.LOG.error("Endpoint for process is null");
		// }
		// if (portType == null) {
		// CamundaPlanEnginePlugin.LOG.error("PortType of process is null");
		// }
		// return false;
		// }
		return true;
	}

	private void bindServices() {
		BundleContext context = Activator.getContext();

		ServiceReference<ICoreFileService> coreRef = context.getServiceReference(ICoreFileService.class);
		fileService = context.getService(coreRef);

		ServiceReference<IFileAccessService> fileAccess = context.getServiceReference(IFileAccessService.class);
		fileAccessService = context.getService(fileAccess);

		ServiceReference<IToscaEngineService> toscaEngine = context.getServiceReference(IToscaEngineService.class);
		toscaEngineService = context.getService(toscaEngine);
		
		ServiceReference<ICoreEndpointService> endpointService = context.getServiceReference(ICoreEndpointService.class);
		this.endpointService = context.getService(endpointService);
	}

	@Override
	public boolean undeployPlanReference(QName planId, PlanModelReference planRef, CSARID csarId) {
		LOG.warn("The undeploy method for the Camunda plan engine is not implemented yet.");
		return false;
	}

	@Override
	public String toString() {
		return Messages.CamundaPlanEnginePlugin_description;
	}
}
