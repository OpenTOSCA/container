package org.opentosca.planinvocationengine.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.csarinstancemanagement.service.ICSARInstanceManagementService;
import org.opentosca.model.csarinstancemanagement.CSARInstanceID;
import org.opentosca.model.tosca.TParameter;
import org.opentosca.model.tosca.TPlan;
import org.opentosca.model.tosca.extension.helpers.PlanTypes;
import org.opentosca.model.tosca.extension.planinvocationevent.PlanInvocationEvent;
import org.opentosca.model.tosca.extension.transportextension.TParameterDTO;
import org.opentosca.model.tosca.extension.transportextension.TPlanDTO;
import org.opentosca.planinvocationengine.service.IPlanInvocationEngine;
import org.opentosca.planinvocationengine.service.impl.messages.generation.RESTMessageGenerator;
import org.opentosca.planinvocationengine.service.impl.messages.generation.SOAPMessageGenerator;
import org.opentosca.planinvocationengine.service.impl.messages.parsing.ResponseParser;
import org.opentosca.settings.Settings;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

/**
 * The Implementation of the Engine. Also deals with OSGI events for
 * communication with the mock-up Servicebus.
 * 
 * Copyright 2013 Christian Endres
 * 
 * @author endrescn@fachschaft.informatik.uni-stuttgart.de
 * 
 */
public class PlanInvocationEngine implements IPlanInvocationEngine, EventHandler {
	
	
	private final SOAPMessageGenerator soapInvokeMessageGenerator = new SOAPMessageGenerator();
	private final ResponseParser responseParser = new ResponseParser();
	private final RESTMessageGenerator restInvokeMessageGenerator = new RESTMessageGenerator();
	
	private final Logger LOG = LoggerFactory.getLogger(PlanInvocationEngine.class);
	
	private static String nsBPEL = "http://docs.oasis-open.org/wsbpel/2.0/process/executable";
	private static String nsBPMN = "http://www.omg.org/spec/BPMN/2.0";
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String invokePlan(CSARID csarID, int csarInstanceID, TPlanDTO givenPlan) {
		
		// refill information that might not be sent
		TPlan storedPlan = ServiceHandler.toscaReferenceMapper.getPlanForCSARIDAndPlanID(csarID, givenPlan.getId());
		
		if ((null == storedPlan)) {
			LOG.error("Plan " + givenPlan.getId() + " with name " + givenPlan.getName() + " is null!");
			return null;
		}
		if (!storedPlan.getId().equals(givenPlan.getId().getLocalPart())) {
			LOG.error("Plan " + givenPlan.getId() + " with internal ID " + givenPlan.getName() + " should copy of PublicPlan " + storedPlan.getId() + "!");
			return null;
		}
		
		givenPlan.setName(storedPlan.getName());
		givenPlan.setPlanLanguage(storedPlan.getPlanLanguage());
		givenPlan.setPlanType(storedPlan.getPlanType());
		givenPlan.setOutputParameters(storedPlan.getOutputParameters());
		
		PlanInvocationEvent planEvent = new PlanInvocationEvent();
		
		LOG.info("Invoke the Plan \"" + givenPlan.getId() + "\" of type \"" + givenPlan.getPlanType() + " of CSAR \"" + csarID + "\".");
		
		// fill in the informations about this PublicPlan which is not provided
		// by the PublicPlan received by the REST API
		Map<QName, TPlan> publicPlanMap = ServiceHandler.toscaReferenceMapper.getCSARIDToPlans(csarID).get(PlanTypes.isPlanTypeURI(givenPlan.getPlanType()));
		
		if (null == publicPlanMap) {
			LOG.error("Wrong type! \"" + givenPlan.getPlanType() + "\"");
			return null;
		}
		
		planEvent.setCSARID(csarID.toString());
		planEvent.setInputMessageID(ServiceHandler.toscaReferenceMapper.getPlanInputMessageID(csarID, givenPlan.getId()));
		planEvent.setInterfaceName(ServiceHandler.toscaReferenceMapper.getIntferaceNameOfPlan(csarID, givenPlan.getId()));
		planEvent.setOperationName(ServiceHandler.toscaReferenceMapper.getOperationNameOfPlan(csarID, givenPlan.getId()));
		// planEvent.setOutputMessageID(storedPlan.getOutputMessageID());
		planEvent.setPlanLanguage(storedPlan.getPlanLanguage());
		planEvent.setPlanType(storedPlan.getPlanType());
		planEvent.setPlanID(givenPlan.getId());
		planEvent.setIsActive(true);
		planEvent.setHasFailed(false);
		for (TParameter temp : storedPlan.getInputParameters().getInputParameter()) {
			boolean found = false;
			
			LOG.trace("Processing input parameter {}", temp.getName());
			
			List<TParameterDTO> params = givenPlan.getInputParameters().getInputParameter();
			for (TParameterDTO param : params) {
				
				if (param.getName().equals(temp.getName())) {
					TParameterDTO dto = param;
					// param.setRequired(temp.getRequired());
					// param.setType(temp.getType());
					found = true;
					planEvent.getInputParameter().add(dto);
					LOG.trace("Found input param {} with value {}", param.getName(), param.getValue());
				}
			}
			if (!found) {
				LOG.trace("Did not found input param {}, thus, insert empty one.", temp.getName());
				TParameterDTO newParam = new TParameterDTO();
				newParam.setName(temp.getName());
				newParam.setType(temp.getType());
				newParam.setRequired(temp.getRequired());
				planEvent.getInputParameter().add(newParam);
			}
		}
		for (TParameter temp : storedPlan.getOutputParameters().getOutputParameter()) {
			TParameterDTO param = new TParameterDTO();
			
			param.setName(temp.getName());
			param.setRequired(temp.getRequired());
			param.setType(temp.getType());
			
			planEvent.getOutputParameter().add(param);
		}
		
		// get new correlationID
		String correlationID = ServiceHandler.correlationHandler.getNewCorrelationID(csarID, planEvent);
		
		// plan is of type build, thus create an instance and put the
		// CSARInstanceID into the plan
		CSARInstanceID instanceID;
		if (PlanTypes.isPlanTypeURI(planEvent.getPlanType()).equals(PlanTypes.BUILD)) {
			instanceID = ServiceHandler.csarInstanceManagement.createNewInstance(csarID);
			planEvent.setCSARInstanceID(instanceID.getInstanceID());
		} else {
			instanceID = new CSARInstanceID(csarID, csarInstanceID);
		}
		ServiceHandler.csarInstanceManagement.correlateCSARInstanceWithPlanInstance(instanceID, correlationID);
		ServiceHandler.csarInstanceManagement.setCorrelationAsActive(csarID, correlationID);
		
		Map<String, Object> eventValues = new Hashtable<String, Object>();
		eventValues.put("CSARID", csarID);
		eventValues.put("PLANID", planEvent.getPlanID());
		eventValues.put("PLANLANGUAGE", planEvent.getPlanLanguage());
		eventValues.put("OPERATIONNAME", planEvent.getOperationName());
		
		LOG.debug("complete the list of parameters {}", givenPlan.getId());
		
		Map<String, String> message = createRequest(csarID, ServiceHandler.toscaReferenceMapper.getPlanInputMessageID(csarID, givenPlan.getId()), planEvent.getInputParameter(), correlationID);
		
		if (null == message) {
			LOG.error("Failed to construct parameter list for plan {} of type {}", givenPlan.getId(), givenPlan.getPlanLanguage());
			return null;
		}
		
		StringBuilder builder = new StringBuilder("Invoking the plan with the following parameters:\n");
		for (String key : message.keySet()) {
			builder.append("     " + key + " : " + message.get(key) + "\n");
		}
		LOG.trace(builder.toString());
		
		eventValues.put("BODY", message);
		
		if (null == ServiceHandler.toscaReferenceMapper.isPlanAsynchronous(csarID, givenPlan.getId())) {
			LOG.error(" There are no informations stored about whether the plan is synchronous or asynchronous. Thus, we believe it is asynchronous.");
			eventValues.put("ASYNC", true);
		} else if (ServiceHandler.toscaReferenceMapper.isPlanAsynchronous(csarID, givenPlan.getId())) {
			eventValues.put("ASYNC", true);
		} else {
			eventValues.put("ASYNC", false);
		}
		eventValues.put("MESSAGEID", correlationID);
		
		ServiceHandler.csarInstanceManagement.storePublicPlanToHistory(correlationID, planEvent);
		
		// send the message to the service bus
		Event event = new Event("org_opentosca_plans/requests", eventValues);
		LOG.debug("Send event with parameters for invocation with the CorrelationID \"{}\".", correlationID);
		ServiceHandler.eventAdmin.postEvent(event);
		
		return correlationID;
	}
	
	public Map<String, String> createRequest(CSARID csarID, QName planInputMessageID, List<TParameterDTO> inputParameter, String correlationID) {
		
		Map<String, String> map = new HashMap<String, String>();
		List<Document> docs = new ArrayList<Document>();
		
		List<QName> serviceTemplates = ServiceHandler.toscaEngineService.getServiceTemplatesInCSAR(csarID);
		for (QName serviceTemplate : serviceTemplates) {
			List<String> nodeTemplates = ServiceHandler.toscaEngineService.getNodeTemplatesOfServiceTemplate(csarID, serviceTemplate);
			
			for (String nodeTemplate : nodeTemplates) {
				Document doc = ServiceHandler.toscaEngineService.getPropertiesOfNodeTemplate(csarID, serviceTemplate, nodeTemplate);
				if (null != doc) {
					docs.add(doc);
					LOG.trace("Found property document: {}", ServiceHandler.xmlSerializerService.getXmlSerializer().docToString(doc, false));
				}
			}
		}
		
		LOG.trace("Processing a list of {} parameters", inputParameter.size());
		for (TParameterDTO para : inputParameter) {
			LOG.trace("Put in the parameter {} with value \"{}\".", para.getName(), para.getValue());
			
			if (para.getName().equalsIgnoreCase("CorrelationID")) {
				LOG.debug("Found Correlation Element! Put in CorrelationID \"" + correlationID + "\".");
				map.put(para.getName(), correlationID);
			} else if (para.getName().equalsIgnoreCase("csarName")) {
				LOG.debug("Found csarName Element! Put in csarName \"" + csarID + "\".");
				map.put(para.getName(), csarID.toString());
			} else if (para.getName().equalsIgnoreCase("containerApiAddress")) {
				LOG.debug("Found containerApiAddress Element! Put in containerApiAddress \"" + Settings.CONTAINER_API + "\".");
				map.put(para.getName(), Settings.CONTAINER_API);
			} else if (para.getName().equalsIgnoreCase("instanceDataAPIUrl")) {
				LOG.debug("Found instanceDataAPIUrl Element! Put in instanceDataAPIUrl \"" + Settings.CONTAINER_INSTANCEDATA_API + "\".");
				map.put(para.getName(), Settings.CONTAINER_INSTANCEDATA_API);
			} else if (para.getName().equalsIgnoreCase("csarEntrypoint")){
				LOG.debug("Found csarEntrypoint Element! Put in instanceDataAPIUrl \"" + Settings.CONTAINER_API + "/" + csarID + "\".");
				map.put(para.getName(), Settings.CONTAINER_API + "/CSARs/" + csarID);
			} else {
				if (para.getName() == null || null == para.getValue() || para.getValue().equals("")) {
					LOG.debug("The parameter \"" + para.getName() + "\" has an empty value, thus search in the properties.");
					String value = "";
					for (Document doc : docs) {
						NodeList nodes = doc.getElementsByTagNameNS("*", para.getName());
						LOG.trace("Found {} nodes.", nodes.getLength());
						if (nodes.getLength() > 0) {
							value = nodes.item(0).getTextContent();
							LOG.debug("Found value {}", value);
							break;
						}
					}
					if (value.equals("")) {
						LOG.debug("No value found.");
					}
					map.put(para.getName(), value);
				} else {
					LOG.debug("Found element \"" + para.getName() + "\"! Put in \"" + para.getValue() + "\".");
					map.put(para.getName(), para.getValue());
				}
			}
		}
		
		return map;
	}
	
	/**
	 * Receives events of the topic list org_opentosca_plans/response. This
	 * method handles responses of BPEL-plans.
	 */
	@Override
	public void handleEvent(Event eve) {
		
		String correlationID = (String) eve.getProperty("MESSAGEID");
		String planLanguage = (String) eve.getProperty("PLANLANGUAGE");
		LOG.trace("The correlation ID is {} and plan language is {}", correlationID, planLanguage);
		
		ServiceHandler.correlationHandler.removeCorrelation(correlationID);
		
		PlanInvocationEvent event;
		
		// TODO the concrete handling and parsing shall be in the plugin?!
		if (planLanguage.startsWith(nsBPEL)) {
			
			org.w3c.dom.Document responseBody = (org.w3c.dom.Document) eve.getProperty("RESPONSE");
			
			LOG.debug("Received an event with a SOAP response body " + responseBody.getChildNodes().item(0).getLocalName());
			
			event = ServiceHandler.csarInstanceManagement.getPlanFromHistory(correlationID);
			CSARID csarID = new CSARID(event.getCSARID());
			
			// parse the body
			correlationID = responseParser.parseSOAPBody(csarID, event.getPlanID(), correlationID, responseBody);
			
			// if plan is not null
			if (null == correlationID) {
				LOG.error("The parsing of the response failed!");
				return;
			}
			
			// save
			CSARInstanceID instanceID = ServiceHandler.csarInstanceManagement.getInstanceForCorrelation(correlationID);
			LOG.debug("The instanceID is: " + instanceID);
			ServiceHandler.csarInstanceManagement.storeCorrelationForAnInstance(instanceID.getOwner(), instanceID, correlationID);
			
			if (event.isHasFailed()) {
				LOG.info("The process instance was not successful.");
				
			} else {
				if (PlanTypes.isPlanTypeURI(event.getPlanType()).equals(PlanTypes.TERMINATION)) {
					boolean deletion = ServiceHandler.csarInstanceManagement.deleteInstance(instanceID.getOwner(), instanceID);
					LOG.debug("Delete of instance returns: " + deletion);
				}
			}
		} else if (planLanguage.startsWith(nsBPMN)) {
			
			Object response = eve.getProperty("RESPONSE");
			
			LOG.debug("Received an event with a REST response: {}", response);
			
			event = ServiceHandler.csarInstanceManagement.getPlanFromHistory(correlationID);
			LOG.trace("Found invocation in plan history for instance: {}", event.getCSARInstanceID());
			CSARID csarID = new CSARID(event.getCSARID());
			
			// parse the body
			String planInstanceID = responseParser.parseRESTResponse(csarID, event.getPlanID(), correlationID, response);
			
			// if plan is not null
			if (null == planInstanceID || planInstanceID.equals("")) {
				LOG.error("The parsing of the response failed!");
				return;
			}
			
			/**
			 * TODO remove jersey and search for the history with the bus(?)!!!
			 */
			
			// searching for history
			String pathBase = "http://localhost:8080/engine-rest/";
			String pathProcessInstance = "process-instance?processInstanceIds=";
			String pathHistoryVariables = "history/variable-instance";
			
			LOG.debug("Instance ID: " + planInstanceID);
			
			Client client = Client.create();
			client.addFilter(new HTTPBasicAuthFilter("demo", "demo"));
			
			boolean ended = false;
			String path = pathBase + pathProcessInstance + planInstanceID;
			WebResource webResource = client.resource(path);
			
			ClientResponse camundaResponse;
			while (!ended) {
				camundaResponse = webResource.get(ClientResponse.class);
				String resp = camundaResponse.getEntity(String.class);
				LOG.debug("Active process instance response: " + resp);
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				if (resp.equals("[]")) {
					LOG.debug("The plan instance {} is not active any more, thus, the output can be retrieved.", planInstanceID);
					ended = true;
				}
				
				if (resp.contains("Process instance with id " + planInstanceID + " does not exist")) {
					ended = true;
				}
				
			}
			
			ICSARInstanceManagementService instMngr = ServiceHandler.csarInstanceManagement;
			Map<String, String> map = instMngr.getOutputForCorrelation(correlationID);
			
			for (TParameterDTO param : event.getOutputParameter()) {
				// History of process instance TODO get here the output
				// parameters
				path = pathBase + pathHistoryVariables;
				// + "?processInstanceId=" + planInstanceID;
				
				webResource = client.resource(path);
				webResource = webResource.queryParam("processInstanceId", planInstanceID);
				webResource = webResource.queryParam("activityInstanceIdIn", planInstanceID);
				// webResource = webResource.queryParam("variableName",
				// "ApplicationURL");
				webResource = webResource.queryParam("variableName", param.getName());
				camundaResponse = webResource.get(ClientResponse.class);
				String responseStr = camundaResponse.getEntity(String.class);
				LOG.trace("Query:\n{}", webResource.getURI());
				LOG.trace("History has for variable \"{}\" the value \"{}\"", param.getName(), responseStr);
				
				JsonParser parser = new JsonParser();
				String value = null;
				try {
					JsonObject json = (JsonObject) parser.parse(responseStr.substring(1, responseStr.length() - 1));
					value = json.get("value").getAsString();
				} catch (ClassCastException e) {
					LOG.trace("value is null");
					value = "";
				}
				LOG.debug("For variable \"{}\" the output value is \"{}\"", param.getName(), value);
				param.setValue(value);
				map.put(param.getName(), value);
			}
			
			ServiceHandler.csarInstanceManagement.getOutputForCorrelation(correlationID).putAll(map);
			ServiceHandler.csarInstanceManagement.setCorrelationAsFinished(csarID, correlationID);
			
			// save
			CSARInstanceID instanceID = ServiceHandler.csarInstanceManagement.getInstanceForCorrelation(correlationID);
			LOG.debug("The instanceID is: " + instanceID);
			ServiceHandler.csarInstanceManagement.storeCorrelationForAnInstance(instanceID.getOwner(), instanceID, correlationID);
			
			if (event.isHasFailed()) {
				LOG.info("The process instance was not successful.");
				
			} else {
				if (PlanTypes.isPlanTypeURI(event.getPlanType()).equals(PlanTypes.TERMINATION)) {
					boolean deletion = ServiceHandler.csarInstanceManagement.deleteInstance(instanceID.getOwner(), instanceID);
					LOG.debug("Delete of instance returns: " + deletion);
				}
			}
		} else {
			LOG.error("The returned response cannot be matched to a supported plan language!");
			return;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getActiveCorrelationsOfInstance(CSARInstanceID csarInstanceID) {
		return ServiceHandler.correlationHandler.getActiveCorrelationsOfInstance(csarInstanceID);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public TPlanDTO getActivePublicPlanOfInstance(CSARInstanceID csarInstanceID, String correlationID) {
		return ServiceHandler.correlationHandler.getPlanDTOForCorrelation(csarInstanceID, correlationID);
	}
}
