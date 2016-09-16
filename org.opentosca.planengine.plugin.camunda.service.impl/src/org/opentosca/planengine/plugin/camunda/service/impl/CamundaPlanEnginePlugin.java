package org.opentosca.planengine.plugin.camunda.service.impl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
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
import org.opentosca.util.http.service.IHTTPService;
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
		
		// retrieve process
		if (fileService != null) {
			
			CSARContent csar = null;
			
			try {
				csar = fileService.getCSAR(csarId);
			} catch (UserException exc) {
				CamundaPlanEnginePlugin.LOG.error("Could not get the CSAR from file service. An User Exception occured.", exc);
				return false;
			}
			
			AbstractArtifact planReference = null;
			
			planReference = toscaEngineService.getPlanModelReferenceAbstractArtifact(csar, planId);
			
			if (planReference == null) {
				CamundaPlanEnginePlugin.LOG.error("Plan reference '{}' resulted in a null ArtifactReference.", planRef.getReference());
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
				
				if (fetchedPlan.toFile().exists()) {
					LOG.debug("Plan file exists at {}", fetchedPlan.toString());
				}
			} catch (SystemException exc) {
				CamundaPlanEnginePlugin.LOG.error("An System Exception occured. File could not be fetched.", exc);
				return false;
			}
			
		} else {
			CamundaPlanEnginePlugin.LOG.error("Can't fetch relevant files from FileService: FileService not available");
			return false;
		}
		
		// ##################################################################################################################################################
		// ### dirty copy of IAEngine War Tomcat Plugin
		// ### TODO make this pretty
		// ##################################################################################################################################################
		
		CopyOfIAEnginePluginWarTomcatServiceImpl deployer = new CopyOfIAEnginePluginWarTomcatServiceImpl();
		deployer.deployImplementationArtifact(csarId, fetchedPlan.toFile());
		// POST http://localhost:8080/engine-rest/process-definition/{id}/start
		URI endpointURI = null;
		try {
			planName = toscaEngineService.getPlanName(csarId, planId);
			endpointURI = searchForEndpoint(planName);
			LOG.debug("Endpoint URI is {}", endpointURI.getPath());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			
		}
		
		// ##################################################################################################################################################
		// ##################################################################################################################################################
		
		if (endpointURI == null) {
			CamundaPlanEnginePlugin.LOG.warn("No endpoint for Plan {} could be determined, container won't be able to instantiate it", planRef.getReference());
			return false;
		}
		
		if (null == endpointService) {
			LOG.error("Endpoint serivce is offline.");
		}
		
		WSDLEndpoint point = new WSDLEndpoint();
		point.setCSARId(csarId);
		point.setPlanId(planId);
		point.setIaName(planName);
		point.setURI(endpointURI);
		
		endpointService.storeWSDLEndpoint(point);
		
		return true;
	}
	
	private URI searchForEndpoint(String planName) throws URISyntaxException {
		URI endpointURI;
		LOG.debug("Search for Plan Endpoint");
		
		String processDefinitions = "http://localhost:8080/engine-rest/process-definition/";
		
		IHTTPService httpService;
		BundleContext context = Activator.getContext();
		ServiceReference<IHTTPService> tmpHttpService = context.getServiceReference(IHTTPService.class);
		httpService = context.getService(tmpHttpService);
		
		HttpResponse response;
		String output = null;
		
		LOG.debug("Retrieve list of deployed plans");
		try {
			response = httpService.Get(processDefinitions);
			output = EntityUtils.toString(response.getEntity(), "UTF-8");
			output = output.substring(1, output.length() - 1);
		} catch (IOException e) {
			LOG.error("An error occured while retrieving the deployed plan list from camunda: ", e.getLocalizedMessage());
			e.printStackTrace();
			return null;
		}
		String json = output;
		
		LOG.trace("Response json: {}", json);
		
		String[] list = json.split("\\{");
		
		HashMap<String, String> ids = new HashMap<String, String>();
		
		for (String entry : list) {
			if (null != entry && !entry.equals("")) {
				String[] fields = entry.split(",");
				
				String id = fields[0].substring(6, fields[0].length() - 1);
				String key = fields[1].substring(7, fields[1].length() - 1);
				
				ids.put(id, key);
				LOG.trace("ID {} KEY {}", id, key);
			}
		}
		
		String planID = "";
		
		if (ids.containsValue(planName)) {
			for (String id : ids.keySet()) {
				if (ids.get(id).equals(planName)) {
					planID = ids.get(id);
				}
			}
		}
		
		if (planID.equals("")) {
			LOG.error("No endpoint found for plan {}!", planName);
			return null;
		}
		
		endpointURI = new URI(processDefinitions + "key/" + planID + "/start");
		return endpointURI;
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
