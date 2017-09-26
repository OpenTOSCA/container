package org.opentosca.container.engine.plan.plugin.bpel;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;

import org.opentosca.container.connector.bps.BpsConnector;
import org.opentosca.container.connector.ode.OdeConnector;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.engine.IToscaEngineService;
import org.opentosca.container.core.model.AbstractArtifact;
import org.opentosca.container.core.model.AbstractFile;
import org.opentosca.container.core.model.csar.CSARContent;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.model.endpoint.wsdl.WSDLEndpoint;
import org.opentosca.container.core.service.ICoreEndpointService;
import org.opentosca.container.core.service.ICoreFileService;
import org.opentosca.container.core.service.IFileAccessService;
import org.opentosca.container.core.tosca.model.TPlan.PlanModelReference;
import org.opentosca.container.engine.plan.plugin.IPlanEnginePlanRefPluginService;
import org.opentosca.container.engine.plan.plugin.bpel.util.BPELRESTLightUpdater;
import org.opentosca.container.engine.plan.plugin.bpel.util.Messages;
import org.opentosca.container.engine.plan.plugin.bpel.util.ODEEndpointUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * This class implements functionality for deployment of WS-BPEL 2.0 Processes
 * through the
 * {@link org.opentosca.planengine.plugin.service.IPlanEnginePlanRefPluginService}
 * unto a WSO2 Business Process Server or Apache Orchestration Director Engine
 * (ODE).
 *
 * The class is the highlevel control of the plugin. It uses the classes
 * {@link org.opentosca.container.engine.plan.plugin.bpel.util.BPELRESTLightUpdater}
 * to update BPEL4RESTLight (see:
 * OpenTOSCA/trunk/examples/org.opentosca.bpel4restlight.bpelextension)
 * extension activities with up-to-date endpoints. The plugin also uses
 * {@link org.opentosca.container.engine.plan.plugin.bpel.util.ODEEndpointUpdater}
 * to update the bindings inside the used WSDL Descriptions referenced in the
 * BPEL process. The endpoints for the update are retrieved through a service
 * that implements the
 * {@link org.opentosca.core.endpoint.service.ICoreEndpointService} interface.
 *
 * The actual deployment is done on the endpoint which is declared in the
 * {@link org.opentosca.container.engine.plan.plugin.bpel.util.Messages} class.
 * The plugin uses {@link org.opentosca.container.connector.bps.BpsConnector} or
 * {@link org.opentosca.container.connector.ode.OdeConnector} class to deploy
 * the updated plan unto the WSO2 BPS or Apache ODE behind the endpoint,
 * respectively.
 *
 * @see org.opentosca.planengine.plugin.bpelwso2.util.BPELRESTLightUpdates
 * @see org.opentosca.container.engine.plan.plugin.bpel.util.ODEEndpointUpdater
 * @see org.opentosca.container.connector.bps.BpsConnector
 * @see org.opentosca.container.connector.ode.OdeConnector
 * @see org.opentosca.container.engine.plan.plugin.bpel.util.Messages
 * @see org.opentosca.core.endpoint.service.ICoreEndpointService
 */
public class BpelPlanEnginePlugin implements IPlanEnginePlanRefPluginService {

	final private static Logger LOG = LoggerFactory.getLogger(BpelPlanEnginePlugin.class);

	private ICoreFileService fileService = null;
	private ICoreFileService oldFileService = null;

	private IFileAccessService fileAccessService = null;
	private IFileAccessService oldFileAccessService = null;

	private ICoreEndpointService endpointService;
	private ICoreEndpointService oldEndpointService;

	private IToscaEngineService toscaEngine;
	
	public static final String BPS_ENGINE = "BPS";

	static private String ENGINE = Messages.BpelPlanEnginePlugin_engine;
	static private String USERNAME = Messages.BpelPlanEnginePlugin_engineLoginName;
	static private String PASSWORD = Messages.BpelPlanEnginePlugin_engineLoginPw;
	static private String URL = Messages.BpelPlanEnginePlugin_engineAddress;
	static private String SERVICESURL = Messages.BpelPlanEnginPlugin_engineServiceRootAddress;
	
	public BpelPlanEnginePlugin() {
		String processEngine = Settings.getSetting("org.opentosca.container.engine.plan.plugin.bpel.engine");

		if (processEngine != null) {
			ENGINE = processEngine;
		}

		String url = Settings.getSetting("org.opentosca.container.engine.plan.plugin.bpel.url");

		if (url != null) {
			URL = url;
		}

		String servicesUrl = Settings.getSetting("org.opentosca.container.engine.plan.plugin.bpel.services.url");

		if (servicesUrl != null) {
			SERVICESURL = servicesUrl;
		}

		String userName = Settings.getSetting("org.opentosca.container.engine.plan.plugin.bpel.username");

		if (userName != null) {
			USERNAME = userName;
		}

		String password = Settings.getSetting("org.opentosca.container.engine.plan.plugin.bpel.password");

		if (password != null) {
			PASSWORD = password;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLanguageUsed() {
		return Messages.BpelPlanEnginePlugin_language;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getCapabilties() {
		final List<String> capabilities = new ArrayList<>();

		for (final String capability : Messages.BpelPlanEnginePlugin_capabilities.split("[,;]")) {
			capabilities.add(capability.trim());
		}
		return capabilities;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean deployPlanReference(final QName planId, final PlanModelReference planRef, final CSARID csarId) {
		List<File> planContents;
		File tempDir;
		File tempPlan;

		// variable for the (inbound) portType of the process, if this is null
		// till end the process can't be instantiated by the container
		QName portType = null;

		// retrieve process
		if (this.fileService != null) {

			CSARContent csar = null;

			try {
				csar = this.fileService.getCSAR(csarId);
			} catch (final UserException exc) {
				BpelPlanEnginePlugin.LOG.error("An User Exception occured.", exc);
				return false;
			}

			AbstractArtifact planReference = null;

			// try {
			// TODO
			// planReference =
			// csar.resolveArtifactReference(planRef.getReference());
			planReference = this.toscaEngine.getPlanModelReferenceAbstractArtifact(csar, planId);
			// } catch (UserException exc) {
			// BpsPlanEnginePlugin.LOG.error("An User Exception occured.", exc);
			// } catch (SystemException exc) {
			// BpsPlanEnginePlugin.LOG.error("A System Exception occured.",
			// exc);
			// }

			if (planReference == null) {
				BpelPlanEnginePlugin.LOG.error("Plan reference '{}' resulted in a null ArtifactReference.",
						planRef.getReference());
				return false;
			}

			if (!planReference.isFileArtifact()) {
				BpelPlanEnginePlugin.LOG.warn("Only plan references pointing to a file are supported!");
				return false;
			}

			final AbstractFile plan = planReference.getFile("");

			if (plan == null) {
				BpelPlanEnginePlugin.LOG.error("ArtifactReference resulted in null AbstractFile.");
				return false;
			}

			if (!plan.getName().substring(plan.getName().lastIndexOf('.') + 1).equals("zip")) {
				BpelPlanEnginePlugin.LOG.debug("Plan reference is not a ZIP file. It was '{}'.", plan.getName());
				return false;
			}

			Path fetchedPlan;

			try {
				fetchedPlan = plan.getFile();
			} catch (final SystemException exc) {
				BpelPlanEnginePlugin.LOG.error("An System Exception occured. File could not be fetched.", exc);
				return false;
			}

			if (this.fileAccessService != null) {
				// creating temporary dir for update
				tempDir = this.fileAccessService.getTemp();
				tempPlan = new File(tempDir, fetchedPlan.getFileName().toString());
				BpelPlanEnginePlugin.LOG.debug("Unzipping Plan '{}' to '{}'.", fetchedPlan.getFileName().toString(),
						tempDir.getAbsolutePath());
				planContents = this.fileAccessService.unzip(fetchedPlan.toFile(), tempDir);
			} else {
				BpelPlanEnginePlugin.LOG
						.error("FileAccessService is not available, can't create needed temporary space on disk");
				return false;
			}

		} else {
			BpelPlanEnginePlugin.LOG.error("Can't fetch relevant files from FileService: FileService not available");
			return false;
		}

		// changing endpoints in WSDLs
		ODEEndpointUpdater odeUpdater;
		try {
			odeUpdater = new ODEEndpointUpdater(SERVICESURL, ENGINE);
			portType = odeUpdater.getPortType(planContents);
			if (!odeUpdater.changeEndpoints(planContents, csarId)) {
				BpelPlanEnginePlugin.LOG.error("Not all endpoints used by the plan {} have been changed",
						planRef.getReference());
			}
		} catch (final WSDLException e) {
			BpelPlanEnginePlugin.LOG.error("Couldn't load ODEEndpointUpdater", e);
		}

		// update the bpel and bpel4restlight elements (ex.: GET, PUT,..)
		BPELRESTLightUpdater bpelRestUpdater;
		try {
			bpelRestUpdater = new BPELRESTLightUpdater();
			if (!bpelRestUpdater.changeEndpoints(planContents, csarId)) {
				// we don't abort deployment here
				BpelPlanEnginePlugin.LOG.warn(
						"Could'nt change all endpoints inside BPEL4RESTLight Elements in the given process {}",
						planRef.getReference());
			}
		} catch (final TransformerConfigurationException e) {
			BpelPlanEnginePlugin.LOG.error("Couldn't load BPELRESTLightUpdater", e);
		} catch (final ParserConfigurationException e) {
			BpelPlanEnginePlugin.LOG.error("Couldn't load BPELRESTLightUpdater", e);
		} catch (final SAXException e) {
			BpelPlanEnginePlugin.LOG.error("ParseError: Couldn't parse .bpel file", e);
		} catch (final IOException e) {
			BpelPlanEnginePlugin.LOG.error("IOError: Couldn't access .bpel file", e);
		}

		// package process
		BpelPlanEnginePlugin.LOG.info("Prepare deployment of PlanModelReference");

		if (this.fileAccessService != null) {
			try {
				if (tempPlan.createNewFile()) {
					// package the updated files
					BpelPlanEnginePlugin.LOG.debug("Packaging plan to {} ", tempPlan.getAbsolutePath());
					tempPlan = this.fileAccessService.zip(tempDir, tempPlan);
				} else {
					BpelPlanEnginePlugin.LOG.error("Can't package temporary plan for deployment");
					return false;
				}
			} catch (final IOException e) {
				BpelPlanEnginePlugin.LOG.error("Can't package temporary plan for deployment", e);
				return false;
			}
		}

		// deploy process
		BpelPlanEnginePlugin.LOG.info("Deploying Plan: {}", tempPlan.getName());
		String processId = "";
		Map<String, URI> endpoints = Collections.emptyMap();
		try {
			if (ENGINE.equalsIgnoreCase(BPS_ENGINE)) {
				BpsConnector connector = new BpsConnector();
				
				processId = connector.deploy(tempPlan, URL, USERNAME, PASSWORD);
				
				endpoints = connector.getEndpointsForPID(processId, URL, USERNAME, PASSWORD);
			} else {
				OdeConnector connector = new OdeConnector();
				
				processId = connector.deploy(tempPlan, URL);
				
				endpoints = connector.getEndpointsForPID(processId, URL);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// this will be the endpoint the container can use to instantiate the
		// BPEL Process
		URI endpoint = null;
		if (endpoints.keySet().size() == 1) {
			endpoint = (URI) endpoints.values().toArray()[0];
		} else {
			for (final String partnerLink : endpoints.keySet()) {
				if (partnerLink.equals("client")) {
					endpoint = endpoints.get(partnerLink);
				}
			}
		}

		if (endpoint == null) {
			BpelPlanEnginePlugin.LOG.warn(
					"No endpoint for Plan {} could be determined, container won't be able to instantiate it",
					planRef.getReference());
			return false;
		}

		if ((processId != null) && (endpoint != null) && (portType != null)) {
			BpelPlanEnginePlugin.LOG.debug("Endpoint for ProcessID \"" + processId + "\" is \"" + endpoints + "\".");
			BpelPlanEnginePlugin.LOG.info("Deployment of Plan was successfull: {}", tempPlan.getName());

			// save endpoint
			final WSDLEndpoint wsdlEndpoint = new WSDLEndpoint(endpoint, portType, csarId, planId, null, null);

			if (this.endpointService != null) {
				BpelPlanEnginePlugin.LOG.debug("Store new endpoint!");
				this.endpointService.storeWSDLEndpoint(wsdlEndpoint);
			} else {
				BpelPlanEnginePlugin.LOG.warn(
						"Couldn't store endpoint {} for plan {}, cause endpoint service is not available",
						endpoint.toString(), planRef.getReference());
				return false;
			}
		} else {
			BpelPlanEnginePlugin.LOG.error("Error while processing plan");
			if (processId == null) {
				BpelPlanEnginePlugin.LOG.error("ProcessId is null");
			}
			if (endpoint == null) {
				BpelPlanEnginePlugin.LOG.error("Endpoint for process is null");
			}
			if (portType == null) {
				BpelPlanEnginePlugin.LOG.error("PortType of process is null");
			}
			return false;
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean undeployPlanReference(final QName planId, final PlanModelReference planRef, final CSARID csarId) {
		// retrieve process
		if (this.fileService != null) {

			CSARContent csar = null;

			try {
				csar = this.fileService.getCSAR(csarId);
			} catch (final UserException exc) {
				BpelPlanEnginePlugin.LOG.error("An User Exception occured.", exc);
				return false;
			}

			AbstractArtifact planReference = null;

			planReference = this.toscaEngine.getPlanModelReferenceAbstractArtifact(csar, planId);

			if (planReference == null) {
				BpelPlanEnginePlugin.LOG.error("Plan reference '{}' resulted in a null ArtifactReference.",
						planRef.getReference());
				return false;
			}

			if (!planReference.isFileArtifact()) {
				BpelPlanEnginePlugin.LOG.warn("Only plan references pointing to a file are supported!");
				return false;
			}

			final AbstractFile plan = planReference.getFile("");

			if (plan == null) {
				BpelPlanEnginePlugin.LOG.error("ArtifactReference resulted in null AbstractFile.");
				return false;
			}

			if (!plan.getName().substring(plan.getName().lastIndexOf('.') + 1).equals("zip")) {
				BpelPlanEnginePlugin.LOG.debug("Plan reference is not a ZIP file. It was '{}'.", plan.getName());
				return false;
			}

			Path fetchedPlan;

			try {
				fetchedPlan = plan.getFile();
			} catch (final SystemException exc) {
				BpelPlanEnginePlugin.LOG.error("An System Exception occured. File could not be fetched.", exc);
				return false;
			}

			boolean wasUndeployed = false;
			if (ENGINE.equalsIgnoreCase(BPS_ENGINE)) {
				BpsConnector connector = new BpsConnector();
				
				wasUndeployed = connector.undeploy(fetchedPlan.toFile(), URL, USERNAME, PASSWORD);
			} else {
				OdeConnector connector = new OdeConnector();
				
				wasUndeployed = connector.undeploy(fetchedPlan.toFile(), URL);
			}
			
			// remove endpoint from core
			WSDLEndpoint endpoint = null;
			if (this.endpointService != null) {
				BpelPlanEnginePlugin.LOG.debug("Starting to remove endpoint!");
				// this.endpointService.storeWSDLEndpoint(wsdlEndpoint);
				endpoint = this.endpointService.getWSDLEndpointForPlanId(csarId, planId);

				if (endpoint == null) {
					BpelPlanEnginePlugin.LOG.warn(
							"Couldn't remove endpoint for plan {}, because endpoint service didn't find any endpoint associated with the plan to remove",
							planRef.getReference());
				}

				if (this.endpointService.removeWSDLEndpoint(csarId, endpoint)) {
					BpelPlanEnginePlugin.LOG.debug("Removed endpoint {} for plan {}", endpoint.toString(),
							planRef.getReference());
				}

			} else {
				BpelPlanEnginePlugin.LOG.warn(
						"Couldn't remove endpoint for plan {}, cause endpoint service is not available",
						planRef.getReference());
			}

			if (wasUndeployed) {
				BpelPlanEnginePlugin.LOG.info("Undeployment of Plan " + planRef.getReference() + " was successful");
			} else {
				BpelPlanEnginePlugin.LOG.warn("Undeployment of Plan " + planRef.getReference() + " was unsuccessful");
			}

			return wasUndeployed;
		} else {
			BpelPlanEnginePlugin.LOG.error("Can't fetch relevant files from FileService: FileService not available");
			return false;
		}
	}

	/**
	 * Bind method for IFileServices
	 *
	 * @param fileService
	 *            the file service to bind
	 */
	public void registerFileService(final ICoreFileService fileService) {
		if (fileService != null) {
			BpelPlanEnginePlugin.LOG.debug("Registering FileService {}", fileService.toString());
			if (this.fileService == null) {
				this.fileService = fileService;
			} else {
				this.oldFileService = fileService;
				this.fileService = fileService;
			}
			BpelPlanEnginePlugin.LOG.debug("Registered FileService {}", fileService.toString());
		}
	}

	/**
	 * Unbind method for IFileServices
	 *
	 * @param fileService
	 *            the file service to unbind
	 */
	protected void unregisterFileService(final ICoreFileService fileService) {
		BpelPlanEnginePlugin.LOG.debug("Unregistering FileService {}", fileService.toString());
		if (this.oldFileService == null) {
			this.fileService = null;
		} else {
			this.oldFileService = null;
		}
		BpelPlanEnginePlugin.LOG.debug("Unregistered FileService {}", fileService.toString());
	}

	/**
	 * Bind method for IFileAccessServices
	 *
	 * @param fileAccessService
	 *            the fileAccessService to bind
	 */
	public void registerFileAccessService(final IFileAccessService fileAccessService) {
		if (fileAccessService != null) {
			BpelPlanEnginePlugin.LOG.debug("Registering FileAccessService {}", fileAccessService.toString());
			if (this.fileAccessService == null) {
				this.fileAccessService = fileAccessService;
			} else {
				this.oldFileAccessService = fileAccessService;
				this.fileAccessService = fileAccessService;
			}
			BpelPlanEnginePlugin.LOG.debug("Registered FileAccessService {}", fileAccessService.toString());
		}
	}

	/**
	 * Unbind method for IFileAccessServices
	 *
	 * @param fileAccessService
	 *            the fileAccessService to unbind
	 */
	protected void unregisterFileAccessService(final IFileAccessService fileAccessService) {
		BpelPlanEnginePlugin.LOG.debug("Unregistering FileAccessService {}", fileAccessService.toString());
		if (this.oldFileAccessService == null) {
			this.fileAccessService = null;
		} else {
			this.oldFileAccessService = null;
		}
		BpelPlanEnginePlugin.LOG.debug("Unregistered FileAccessService {}", fileAccessService.toString());
	}

	/**
	 * Bind method for ICoreEndpointServices
	 *
	 * @param endpointService
	 *            the endpointService to bind
	 */
	public void registerEndpointService(final ICoreEndpointService endpointService) {
		if (endpointService != null) {
			BpelPlanEnginePlugin.LOG.debug("Registering EndpointService {}", endpointService.toString());
			if (this.endpointService == null) {
				this.endpointService = endpointService;
			} else {
				this.oldEndpointService = endpointService;
				this.endpointService = endpointService;
			}
			BpelPlanEnginePlugin.LOG.debug("Registered EndpointService {}", endpointService.toString());
		}
	}

	/**
	 * Unbind method for ICoreEndpointServices
	 *
	 * @param endpointService
	 *            the endpointService to unbind
	 */
	protected void unregisterEndpointService(final ICoreEndpointService endpointService) {
		BpelPlanEnginePlugin.LOG.debug("Unregistering EndpointService {}", endpointService.toString());
		if (this.oldEndpointService == null) {
			this.endpointService = null;
		} else {
			this.oldEndpointService = null;
		}
		BpelPlanEnginePlugin.LOG.debug("Unregistered EndpointService {}", endpointService.toString());
	}

	/**
	 * Bind method for IToscaEngineService
	 *
	 * @param service
	 *            the IToscaEngineService to bind
	 */
	public void registerToscaEngine(final IToscaEngineService service) {
		if (service != null) {
			this.toscaEngine = service;
			BpelPlanEnginePlugin.LOG.debug("Registered IToscaEngineService {}", service.toString());
		}
	}

	/**
	 * Unbind method for IToscaEngineService
	 *
	 * @param endpointService
	 *            the IToscaEngineService to unbind
	 */
	protected void unregisterToscaEngine(final IToscaEngineService endpointService) {
		this.toscaEngine = null;
		BpelPlanEnginePlugin.LOG.debug("Unregistered IToscaEngineService {}", endpointService.toString());
	}

	@Override
	public String toString() {
		return Messages.BpelPlanEnginePlugin_description;
	}
}
