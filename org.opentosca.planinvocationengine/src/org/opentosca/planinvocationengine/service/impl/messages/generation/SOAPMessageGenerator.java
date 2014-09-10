package org.opentosca.planinvocationengine.service.impl.messages.generation;

import java.util.List;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.opentosca.model.consolidatedtosca.Parameter;
import org.opentosca.model.consolidatedtosca.PublicPlan;
import org.opentosca.settings.Settings;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The SOAPMessageGenerator generates request messages for PublicPlans. Also it
 * receives the CallbackAddress due OSGI events by the mock-up Servicebus.
 * 
 * Copyright 2013 Christian Endres
 * 
 * @author endrescn@fachschaft.informatik.uni-stuttgart.de
 * 
 */
public class SOAPMessageGenerator implements EventHandler {
	
	private Logger LOG = LoggerFactory.getLogger(SOAPMessageGenerator.class);
	
	// FIXME callback address is not shared between siengine and
	// planinvocationengine but has to
	private static String callbackAddress = "http://localhost:8090/callback";
	
	
	/**
	 * Creates a SOAP request message.
	 * 
	 * @param publicPlan
	 * @param correlationID
	 * @return SOAPMessage
	 */
	public SOAPMessage createRequest(PublicPlan publicPlan, String correlationID) {
		
		this.LOG.debug("Create new BPEL/SOAP message with correlation \"" + correlationID + "\".");
		
		try {
			
			MessageFactory messageFactory = MessageFactory.newInstance();
			SOAPMessage soapMessage = messageFactory.createMessage();
			soapMessage.setProperty(SOAPMessage.CHARACTER_SET_ENCODING, "UTF-8");
			
			SOAPPart soapPart = soapMessage.getSOAPPart();
			SOAPEnvelope envelope = soapPart.getEnvelope();
			
			envelope.addNamespaceDeclaration("env", "http://schemas.xmlsoap.org/soap/envelop/");
			envelope.setEncodingStyle("http://schemas.xmlsoap.org/soap/encoding/");
			
			SOAPBody soapBody = soapMessage.getSOAPBody();
			
			Name bodyName = envelope.createName(publicPlan.getInputMessageID().getLocalPart(), "custom", publicPlan.getInputMessageID().getNamespaceURI());
			SOAPBodyElement payload = soapBody.addBodyElement(bodyName);
			
			List<Parameter> inputParameters = publicPlan.getInputParameter();
			
			// put in the InputParameter details.
			for (Parameter para : inputParameters) {
				
				if (para.getType().equalsIgnoreCase("correlation")) {
					this.LOG.debug("Found Correlation Element! Put in CorrelationID \"" + correlationID + "\".");
					Name elementName = envelope.createName(para.getName(), "tosca", publicPlan.getInputMessageID().getNamespaceURI());
					payload.addChildElement(elementName).addTextNode(correlationID);
					para.setValue(correlationID);
				} else if (para.getType().equalsIgnoreCase("callbackaddress")) {
					this.LOG.debug("Found CallbackAddress Element! Put in CallbackAddress \"" + SOAPMessageGenerator.callbackAddress + "\".");
					Name elementName = envelope.createName(para.getName(), "tosca", publicPlan.getInputMessageID().getNamespaceURI());
					payload.addChildElement(elementName).addTextNode(SOAPMessageGenerator.callbackAddress);
					para.setValue(SOAPMessageGenerator.callbackAddress);
				} else if (para.getType().equalsIgnoreCase("csarName")) {
					this.LOG.debug("Found csarName Element! Put in csarName \"" + publicPlan.getCSARID() + "\".");
					Name elementName = envelope.createName(para.getName(), "tosca", publicPlan.getInputMessageID().getNamespaceURI());
					payload.addChildElement(elementName).addTextNode(publicPlan.getCSARID());
					para.setValue(publicPlan.getCSARID());
				} else if (para.getType().equalsIgnoreCase("containerApiAddress")) {
					this.LOG.debug("Found containerApiAddress Element! Put in containerApiAddress \"" + Settings.CONTAINER_API + "\".");
					Name elementName = envelope.createName(para.getName(), "tosca", publicPlan.getInputMessageID().getNamespaceURI());
					payload.addChildElement(elementName).addTextNode(Settings.CONTAINER_API);
					para.setValue(Settings.CONTAINER_API);
				} else {
					this.LOG.debug("Found element \"" + para.getName() + "\"! Put in \"" + para.getValue() + "\".");
					Name elementName = envelope.createName(para.getName(), "tns", publicPlan.getInputMessageID().getNamespaceURI());
					payload.addChildElement(elementName).addTextNode(para.getValue());
				}
			}
			
			soapMessage.saveChanges();
			
			return soapMessage;
			
		} catch (SOAPException e) {
			this.LOG.error(e.getLocalizedMessage());
		}
		
		return null;
	}
	
	/**
	 * Receives the CallbackAddress.
	 */
	@Override
	public void handleEvent(Event event) {
		SOAPMessageGenerator.callbackAddress = (String) event.getProperty("callbackAddress");
		this.LOG.debug("Recieved the current callback address: \"" + SOAPMessageGenerator.callbackAddress + "\".");
	}
}
