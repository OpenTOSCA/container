package org.opentosca.planinvocationengine.service.impl.messages.generation;

import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.model.tosca.extension.transportextension.TParameterDTO;
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
	
	private static String callbackAddress = "http://" + Settings.OPENTOSCA_CONTAINER_HOSTNAME + ":8090/callback";
	
	
	/**
	 * Creates a SOAP request message.
	 *
	 * @param plan
	 * @param correlationID
	 * @return SOAPMessage
	 */
	public SOAPMessage createRequest(CSARID csarID, QName messageID, List<TParameterDTO> params, String correlationID) {
		
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
			
			String messageName = messageID.getLocalPart();
			String messageNS = messageID.getNamespaceURI();
			this.LOG.trace("Message has name {} and namespace {}", messageName, messageNS);
			
			Name bodyName = envelope.createName(messageName, "custom", messageNS);
			SOAPBodyElement payload = soapBody.addBodyElement(bodyName);
			
			// put in the InputParameter details.
			for (TParameterDTO para : params) {
				
				if (para.getType().equalsIgnoreCase("correlation") || para.getName().equalsIgnoreCase("CorrelationID")) {
					this.LOG.debug("Found Correlation Element! Put in CorrelationID \"" + correlationID + "\".");
					Name elementName = envelope.createName(para.getName(), "tosca", messageNS);
					payload.addChildElement(elementName).addTextNode(correlationID);
					// para.setValue(correlationID);
				} else if (para.getType().equalsIgnoreCase("callbackaddress")) {
					this.LOG.debug("Found CallbackAddress Element! Put in CallbackAddress \"" + SOAPMessageGenerator.callbackAddress + "\".");
					Name elementName = envelope.createName(para.getName(), "tosca", messageNS);
					payload.addChildElement(elementName).addTextNode(SOAPMessageGenerator.callbackAddress);
					// para.setValue(SOAPMessageGenerator.callbackAddress);
				} else if (para.getName().equalsIgnoreCase("csarName")) {
					this.LOG.debug("Found csarName Element! Put in csarName \"" + csarID + "\".");
					Name elementName = envelope.createName(para.getName(), "tosca", messageNS);
					payload.addChildElement(elementName).addTextNode(csarID.toString());
					// para.setValue(csarID);
				} else if (para.getName().equalsIgnoreCase("containerApiAddress")) {
					this.LOG.debug("Found containerApiAddress Element! Put in containerApiAddress \"" + Settings.CONTAINER_API + "\".");
					Name elementName = envelope.createName(para.getName(), "tosca", messageNS);
					payload.addChildElement(elementName).addTextNode(Settings.CONTAINER_API);
					// para.setValue(Settings.CONTAINER_API);
				} else if (para.getName().equalsIgnoreCase("instanceDataAPIUrl")) {
					this.LOG.debug("Found instanceDataAPIUrl Element! Put in instanceDataAPIUrl \"" + Settings.CONTAINER_INSTANCEDATA_API + "\".");
					Name elementName = envelope.createName(para.getName(), "tosca", messageNS);
					payload.addChildElement(elementName).addTextNode(Settings.CONTAINER_INSTANCEDATA_API);
				} else if (para.getName().equalsIgnoreCase("csarEntrypoint")) {
					this.LOG.debug("Found csarEntrypoint Element! Put in instanceDataAPIUrl \"" + Settings.CONTAINER_API + "/" + csarID + "\".");
					Name elementName = envelope.createName(para.getName(), "tosca", messageNS);
					payload.addChildElement(elementName).addTextNode(Settings.CONTAINER_API + "/" + csarID);
				} else {
					this.LOG.debug("Found element \"" + para.getName() + "\"! Put in \"" + para.getValue() + "\".");
					Name elementName = envelope.createName(para.getName(), "tns", messageNS);
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
