package org.opentosca.planinvocationengine.service.impl;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.opentosca.core.model.csar.id.CSARID;
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
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private final ResponseParser soapResponseParser = new ResponseParser();
    private final RESTMessageGenerator restInvokeMessageGenerator = new RESTMessageGenerator();

    private final Logger LOG = LoggerFactory.getLogger(PlanInvocationEngine.class);

    private static String nsBPEL = "http://docs.oasis-open.org/wsbpel/2.0/process/executable";
    private static String nsBPMN = "http://www.omg.org/spec/BPMN/2.0";

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean invokePlan(CSARID csarID, int csarInstanceID, TPlanDTO plan) {

	// refill information that might not be sent
	TPlan storedPlan = ServiceHandler.toscaReferenceMapper.getPlanForCSARIDAndPlanID(csarID, plan.getId());

	if ((null == storedPlan)) {
	    LOG.error("Plan " + plan.getId() + " with name " + plan.getName() + " is null!");
	    return false;
	}
	if (!storedPlan.getId().equals(plan.getId().getLocalPart())) {
	    LOG.error("Plan " + plan.getId() + " with internal ID " + plan.getName() + " should copy of PublicPlan "
		+ storedPlan.getId() + "!");
	    return false;
	}

	plan.setName(storedPlan.getName());
	plan.setPlanLanguage(storedPlan.getPlanLanguage());
	plan.setPlanType(storedPlan.getPlanType());
	plan.setOutputParameters(storedPlan.getOutputParameters());

	PlanInvocationEvent planEvent = new PlanInvocationEvent();

	LOG.info("Invoke the Plan \"" + plan.getId() + "\" of type \"" + plan.getPlanType() + " of CSAR \"" + csarID
	    + "\".");

	// fill in the informations about this PublicPlan which is not provided
	// by the PublicPlan received by the REST API
	Map<QName, TPlan> publicPlanMap = ServiceHandler.toscaReferenceMapper.getCSARIDToPlans(csarID)
	    .get(PlanTypes.isPlanTypeURI(plan.getPlanType()));

	if (null == publicPlanMap) {
	    LOG.error("Wrong type! \"" + plan.getPlanType() + "\"");
	    return false;
	}

	planEvent.setInputMessageID(ServiceHandler.toscaReferenceMapper.getPlanInputMessageID(csarID, plan.getId()));
	planEvent.setInterfaceName(ServiceHandler.toscaReferenceMapper.getIntferaceNameOfPlan(csarID, plan.getId()));
	planEvent.setOperationName(ServiceHandler.toscaReferenceMapper.getOperationNameOfPlan(csarID, plan.getId()));
	// planEvent.setOutputMessageID(storedPlan.getOutputMessageID());
	planEvent.setPlanLanguage(storedPlan.getPlanLanguage());
	planEvent.setPlanType(storedPlan.getPlanType());
	planEvent.setPlanID(plan.getId());
	planEvent.setIsActive(true);
	planEvent.setHasFailed(false);
	for (TParameter temp : storedPlan.getInputParameters().getInputParameter()) {
	    boolean found = false;

	    List<TParameterDTO> params = plan.getInputParameters().getInputParameter();
	    for (TParameterDTO param : params) {

		if (param.getName().equals(temp.getName())) {
		    TParameterDTO dto = param;
		    // param.setRequired(temp.getRequired());
		    // param.setType(temp.getType());
		    found = true;
		    planEvent.getInputParameter().add(dto);
		}
	    }
	    if (!found) {
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

	Map<String, Object> eventValues = new Hashtable<String, Object>();
	eventValues.put("CSARID", csarID);
	eventValues.put("PLANID", planEvent.getPlanID());
	eventValues.put("PLANLANGUAGE", planEvent.getPlanLanguage());

	// build message
	if (plan.getPlanLanguage().startsWith(nsBPEL)) {
	    LOG.debug("Start of BPEL message construction for plan {}", plan.getId());
	    SOAPMessage message = soapInvokeMessageGenerator.createRequest(csarID,
		ServiceHandler.toscaReferenceMapper.getPlanInputMessageID(csarID, plan.getId()),
		planEvent.getInputParameter(), correlationID);

	    if (null == message) {
		LOG.error("Failed to construct message for plan {} of type {}", plan.getId(), plan.getPlanLanguage());
		return false;
	    }
	    try {
		eventValues.put("BODY", message.getSOAPBody().extractContentAsDocument());
	    } catch (SOAPException e) {
		LOG.error(e.getLocalizedMessage());
		e.printStackTrace();
		LOG.error("Failed to construct message for plan {} of type {}", plan.getId(), plan.getPlanLanguage());
		return false;
	    }
	} else if (plan.getPlanLanguage().startsWith(nsBPMN)) {

	    LOG.debug("Start of BPMN message construction for plan {}", plan.getId());

	    Map<String, String> message = restInvokeMessageGenerator.createRequest(csarID,
		ServiceHandler.toscaReferenceMapper.getPlanInputMessageID(csarID, plan.getId()),
		planEvent.getInputParameter(), correlationID);

	    // TODO correlation id

	    if (null == message) {
		LOG.error("Failed to construct message for plan {} of type {}", plan.getId(), plan.getPlanLanguage());
		return false;
	    }

	    eventValues.put("BODY", message);
	} else {
	    LOG.error("No message construction found for plan {} of type {}", plan.getId(), plan.getPlanLanguage());
	    return false;
	}

	if (null == ServiceHandler.toscaReferenceMapper.isPlanAsynchronous(csarID, plan.getId())) {
	    LOG.error(
		" There are no informations stored about whether the plan is synchronous or asynchronous. Thus, we believe it is asynchronous.");
	    eventValues.put("ASYNC", true);
	} else if (ServiceHandler.toscaReferenceMapper.isPlanAsynchronous(csarID, plan.getId())) {
	    eventValues.put("ASYNC", true);
	} else {
	    eventValues.put("ASYNC", false);
	}
	eventValues.put("MESSAGEID", correlationID);

	ServiceHandler.csarInstanceManagement.storePublicPlanToHistory(correlationID, planEvent);

	// send the message to the service bus
	Event event = new Event("org_opentosca_plans/requests", eventValues);
	LOG.debug("Send event with SOAP message.");
	ServiceHandler.eventAdmin.postEvent(event);

	return true;
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

	PlanInvocationEvent event;

	if (planLanguage.startsWith(nsBPEL)) {

	    org.w3c.dom.Document responseBody = (org.w3c.dom.Document) eve.getProperty("RESPONSE");

	    LOG.debug(
		"Received an event with a SOAP response body " + responseBody.getChildNodes().item(0).getLocalName());

	    event = ServiceHandler.csarInstanceManagement.getPlanFromHistory(correlationID);
	    CSARID csarID = new CSARID(event.getCSARID());

	    // parse the body
	    correlationID = soapResponseParser.parseSOAPBody(csarID, event.getPlanID(), correlationID, responseBody);

	    // if plan is not null
	    if (null == correlationID) {
		LOG.error("The parsing of the response failed!");
		return;
	    }

	    // save
	    CSARInstanceID instanceID = ServiceHandler.csarInstanceManagement.getInstanceForCorrelation(correlationID);
	    LOG.debug("The instanceID is: " + instanceID);
	    ServiceHandler.csarInstanceManagement.storeCorrelationForAnInstance(instanceID.getOwner(), instanceID,
		correlationID);

	    if (event.isHasFailed()) {
		LOG.info("The process instance was not successful.");

	    } else {
		if (PlanTypes.isPlanTypeURI(event.getPlanType()).equals(PlanTypes.TERMINATION)) {
		    boolean deletion = ServiceHandler.csarInstanceManagement.deleteInstance(instanceID.getOwner(),
			instanceID);
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
	    String planInstanceID = soapResponseParser.parseRESTResponse(csarID, event.getPlanID(), correlationID,
		response);

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
	    String pathProcessInstance = "process-instance/";
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
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}

		if (resp.contains("Process instance with id " + planInstanceID + " does not exist")) {
		    ended = true;
		}
	    }

	    // History of process instance
	    path = pathBase + pathHistoryVariables + "?processInstanceId=" + planInstanceID;

	    webResource = client.resource(path);
	    camundaResponse = webResource.get(ClientResponse.class);
	    LOG.debug("History with output response: " + camundaResponse.getEntity(String.class));

	    // save
	    CSARInstanceID instanceID = ServiceHandler.csarInstanceManagement.getInstanceForCorrelation(correlationID);
	    LOG.debug("The instanceID is: " + instanceID);
	    ServiceHandler.csarInstanceManagement.storeCorrelationForAnInstance(instanceID.getOwner(), instanceID,
		correlationID);

	    if (event.isHasFailed()) {
		LOG.info("The process instance was not successful.");

	    } else {
		if (PlanTypes.isPlanTypeURI(event.getPlanType()).equals(PlanTypes.TERMINATION)) {
		    boolean deletion = ServiceHandler.csarInstanceManagement.deleteInstance(instanceID.getOwner(),
			instanceID);
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
