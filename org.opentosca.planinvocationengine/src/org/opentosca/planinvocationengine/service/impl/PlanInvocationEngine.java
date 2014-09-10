package org.opentosca.planinvocationengine.service.impl;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.model.consolidatedtosca.Parameter;
import org.opentosca.model.consolidatedtosca.PublicPlan;
import org.opentosca.model.consolidatedtosca.PublicPlanTypes;
import org.opentosca.model.csarinstancemanagement.CSARInstanceID;
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
	public boolean invokePlan(CSARID csarID, PublicPlan publicPlan) {
		
		if (null == publicPlan) {
			this.LOG.error("No exported plan interface found.");
			return false;
		}
		
		this.LOG.info("Invoke the Plan \"" + publicPlan.getPlanID() + "\" of type \"" + publicPlan.getPlanType() + "\" with internal ID " + publicPlan.getInternalPlanID() + " of CSAR \"" + csarID + "\".");
		this.LOG.debug(publicPlan.getPlanType() + "= " + PublicPlanTypes.isPlanTypeURI(publicPlan.getPlanType()).name());
		
		// fill in the informations about this PublicPlan which is not provided
		// by the PublicPlan received by the REST API
		Map<Integer, PublicPlan> publicPlanMap = ServiceHandler.toscaReferenceMapper.getCSARIDToPublicPlans(csarID).get(PublicPlanTypes.isPlanTypeURI(publicPlan.getPlanType()));
		
		if (null == publicPlanMap) {
			this.LOG.error("Wrong type! \"" + publicPlan.getPlanType() + "\"");
			return false;
		}
		
		PublicPlan storedPublicPlan = publicPlanMap.get(publicPlan.getInternalPlanID());
		if ((null == storedPublicPlan)) {
			this.LOG.error("Plan " + publicPlan.getPlanID() + " with internal ID " + publicPlan.getInternalPlanID() + " is null!");
			return false;
		}
		if (!storedPublicPlan.getPlanID().equals(publicPlan.getPlanID())) {
			this.LOG.error("Plan " + publicPlan.getPlanID() + " with internal ID " + publicPlan.getInternalPlanID() + " should copy of PublicPlan " + storedPublicPlan.getPlanID() + "!");
			return false;
		}
		
		publicPlan.setInputMessageID(storedPublicPlan.getInputMessageID());
		publicPlan.setInterfaceName(storedPublicPlan.getInterfaceName());
		publicPlan.setOperationName(storedPublicPlan.getOperationName());
		publicPlan.setOutputMessageID(storedPublicPlan.getOutputMessageID());
		publicPlan.setPlanLanguage(storedPublicPlan.getPlanLanguage());
		publicPlan.setIsActive(true);
		publicPlan.setHasFailed(false);
		for (Parameter temp : storedPublicPlan.getInputParameter()) {
			boolean found = false;
			for (Parameter param : publicPlan.getInputParameter()) {
				if (param.getName().equals(temp.getName())) {
					param.setRequired(temp.isRequired());
					param.setType(temp.getType());
					found = true;
				}
			}
			if (!found) {
				publicPlan.getInputParameter().add(new Parameter(temp.getName(), temp.getType(), temp.isRequired()));
			}
		}
		for (Parameter temp : storedPublicPlan.getOutputParameter()) {
			Parameter param = new Parameter();
			
			param.setName(temp.getName());
			param.setRequired(temp.isRequired());
			param.setType(temp.getType());
			
			publicPlan.getOutputParameter().add(param);
		}
		
		// get new correlationID
		String correlationID = ServiceHandler.correlationHandler.getNewCorrelationID(publicPlan);
		
		// build message
		SOAPMessage message = this.messageGenerator.createRequest(publicPlan, correlationID);
		
		if (null == message) {
			return false;
		}
		
		// plan is of type build, thus create an instance and put the
		// CSARInstanceID into the plan
		if (PublicPlanTypes.isPlanTypeURI(publicPlan.getPlanType()).equals(PublicPlanTypes.BUILD)) {
			CSARInstanceID instanceID = ServiceHandler.csarInstanceManagement.createNewInstance(csarID);
			publicPlan.setInternalInstanceInternalID(instanceID.getInternalID());
		}
		// send the message to the service bus
		Map<String, Object> eventValues = new Hashtable<String, Object>();
		eventValues.put("CSARID", csarID);
		eventValues.put("PLANID", publicPlan.getPlanID());
		try {
			eventValues.put("BODY", message.getSOAPBody().extractContentAsDocument());
		} catch (SOAPException e) {
			this.LOG.error(e.getLocalizedMessage());
			e.printStackTrace();
			return false;
		}
		
		// FIXME implement!
		// if the plan is asynchronous: send the correlation id with the event
		// if the plan is synchronous: do not send the correlation id!!!
		if (null == ServiceHandler.toscaReferenceMapper.isPlanAsynchronous(csarID, publicPlan.getPlanID())) {
			this.LOG.error(" There are no informations stored about whether the plan is synchronous or asynchronous. Thus abort.");
			return false;
		} else if (ServiceHandler.toscaReferenceMapper.isPlanAsynchronous(csarID, publicPlan.getPlanID())) {
			eventValues.put("ASYNC", true);
		} else {
			eventValues.put("ASYNC", false);
		}
		eventValues.put("MESSAGEID", correlationID);
		
		Event event = new Event("org_opentosca_plans/requests", eventValues);
		
		this.LOG.debug("Send event with SOAP message.");
		ServiceHandler.eventAdmin.postEvent(event);
		
		return true;
	}
	
	/**
	 * Receives events of the topic list org_opentosca_plans/response. This
	 * method handles responses of BPEL-plans.
	 */
	@Override
	public void handleEvent(Event event) {
		
		org.w3c.dom.Document responseBody = (org.w3c.dom.Document) event.getProperty("RESPONSE");
		String correlationID = (String) event.getProperty("MESSAGEID");
		
		this.LOG.debug("Received an event with a SOAP response body " + responseBody.getChildNodes().item(0).getLocalName());
		
		// parse the body
		correlationID = this.responseParser.parseSOAPBody(responseBody, correlationID);
		
		PublicPlan publicPlan = ServiceHandler.csarInstanceManagement.getPublicPlanFromHistory(correlationID);
		
		// if plan is not null
		if (null == publicPlan) {
			this.LOG.error("The parsing of the response failed!");
			return;
		}
		
		// save
		CSARID csarID = new CSARID(publicPlan.getCSARID());
		CSARInstanceID instanceID = new CSARInstanceID(csarID, publicPlan.getInternalInstanceInternalID());
		this.LOG.debug("The instanceID is: " + instanceID);
		ServiceHandler.csarInstanceManagement.storeCorrelationForAnInstance(csarID, instanceID, correlationID);
		
		if (publicPlan.isHasFailed()) {
			this.LOG.info("The process instance was not successful.");
			
		} else {
			if (PublicPlanTypes.isPlanTypeURI(publicPlan.getPlanType()).equals(PublicPlanTypes.TERMINATION)) {
				boolean deletion = ServiceHandler.csarInstanceManagement.deleteInstance(csarID, instanceID);
				this.LOG.debug("Delete of instance returns: " + deletion);
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
	public PublicPlan getActivePublicPlanOfInstance(CSARInstanceID csarInstanceID, String correlationID) {
		return ServiceHandler.correlationHandler.getPublicPlanForCorrelation(csarInstanceID, correlationID);
	}
}
