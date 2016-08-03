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
import org.opentosca.planinvocationengine.service.impl.messages.generation.SOAPMessageGenerator;
import org.opentosca.planinvocationengine.service.impl.messages.parsing.SOAPResponseParser;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private final SOAPMessageGenerator messageGenerator = new SOAPMessageGenerator();
    private final SOAPResponseParser responseParser = new SOAPResponseParser();

    private final Logger LOG = LoggerFactory.getLogger(PlanInvocationEngine.class);

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
	    for (TParameter param : plan.getInputParameters().getInputParameter()) {
		if (param.getName().equals(temp.getName())) {
		    param.setRequired(temp.getRequired());
		    param.setType(temp.getType());
		    found = true;
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

	// build message
	SOAPMessage message = messageGenerator.createRequest(csarID,
	    ServiceHandler.toscaReferenceMapper.getPlanInputMessageID(csarID, plan.getId()),
	    planEvent.getInputParameter(), correlationID);

	if (null == message) {
	    return false;
	}

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

	// send the message to the service bus
	Map<String, Object> eventValues = new Hashtable<String, Object>();
	eventValues.put("CSARID", csarID);
	eventValues.put("PLANID", planEvent.getPlanID());
	eventValues.put("PLANLANGUAGE", planEvent.getPlanLanguage());
	try {
	    eventValues.put("BODY", message.getSOAPBody().extractContentAsDocument());
	} catch (SOAPException e) {
	    LOG.error(e.getLocalizedMessage());
	    e.printStackTrace();
	    return false;
	}

	// FIXME implement!
	// if the plan is asynchronous: send the correlation id with the event
	// if the plan is synchronous: do not send the correlation id!!!
	if (null == ServiceHandler.toscaReferenceMapper.isPlanAsynchronous(csarID, plan.getId())) {
	    LOG.error(
		" There are no informations stored about whether the plan is synchronous or asynchronous. Thus abort.");
	    return false;
	} else if (ServiceHandler.toscaReferenceMapper.isPlanAsynchronous(csarID, plan.getId())) {
	    eventValues.put("ASYNC", true);
	} else {
	    eventValues.put("ASYNC", false);
	}
	eventValues.put("MESSAGEID", correlationID);

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

	org.w3c.dom.Document responseBody = (org.w3c.dom.Document) eve.getProperty("RESPONSE");
	String correlationID = (String) eve.getProperty("MESSAGEID");

	LOG.debug("Received an event with a SOAP response body " + responseBody.getChildNodes().item(0).getLocalName());

	PlanInvocationEvent event = ServiceHandler.csarInstanceManagement.getPlanFromHistory(correlationID);
	CSARID csarID = new CSARID(event.getCSARID());

	// parse the body
	correlationID = responseParser.parseSOAPBody(csarID, event.getPlanID(), correlationID, responseBody);

	// if plan is not null
	if (null == correlationID) {
	    LOG.error("The parsing of the response failed!");
	    return;
	}

	// save
	CSARInstanceID instanceID = new CSARInstanceID(csarID, event.getCSARInstanceID());
	LOG.debug("The instanceID is: " + instanceID);
	ServiceHandler.csarInstanceManagement.storeCorrelationForAnInstance(csarID, instanceID, correlationID);

	if (event.isHasFailed()) {
	    LOG.info("The process instance was not successful.");

	} else {
	    if (PlanTypes.isPlanTypeURI(event.getPlanType()).equals(PlanTypes.TERMINATION)) {
		boolean deletion = ServiceHandler.csarInstanceManagement.deleteInstance(csarID, instanceID);
		LOG.debug("Delete of instance returns: " + deletion);
	    }
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
