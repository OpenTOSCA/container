package org.opentosca.container.core.impl.plan.messages;

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

import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.tosca.extension.TParameterDTO;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The SOAPMessageGenerator generates request messages for PublicPlans. Also it receives the
 * CallbackAddress due OSGI events by the mock-up Servicebus.
 */
public class SOAPMessageGenerator implements EventHandler {

  private static final Logger LOG = LoggerFactory.getLogger(SOAPMessageGenerator.class);

  private static String callbackAddress = "http://" + Settings.OPENTOSCA_CONTAINER_HOSTNAME + ":8087/callback";


  /**
   * Creates a SOAP request message.
   *
   * @param plan
   * @param correlationID
   * @return SOAPMessage
   */
  public SOAPMessage createRequest(final CSARID csarID, final QName messageID, final List<TParameterDTO> params,
                                   final String correlationID) {

    LOG.debug("Create new BPEL/SOAP message with correlation \"" + correlationID + "\".");
    try {
      final MessageFactory messageFactory = MessageFactory.newInstance();
      final SOAPMessage soapMessage = messageFactory.createMessage();
      soapMessage.setProperty(SOAPMessage.CHARACTER_SET_ENCODING, "UTF-8");

      final SOAPPart soapPart = soapMessage.getSOAPPart();
      final SOAPEnvelope envelope = soapPart.getEnvelope();

      envelope.addNamespaceDeclaration("env", "http://schemas.xmlsoap.org/soap/envelop/");
      envelope.setEncodingStyle("http://schemas.xmlsoap.org/soap/encoding/");

      final SOAPBody soapBody = soapMessage.getSOAPBody();

      final String messageName = messageID.getLocalPart();
      final String messageNS = messageID.getNamespaceURI();
      LOG.trace("Message has name {} and namespace {}", messageName, messageNS);

      final Name bodyName = envelope.createName(messageName, "custom", messageNS);
      final SOAPBodyElement payload = soapBody.addBodyElement(bodyName);

      // put in the InputParameter details.
      for (final TParameterDTO para : params) {

        if (para.getType().equalsIgnoreCase("correlation") || para.getName().equalsIgnoreCase("CorrelationID")) {
          LOG.debug("Found Correlation Element! Put in CorrelationID \"" + correlationID + "\".");
          final Name elementName = envelope.createName(para.getName(), "tosca", messageNS);
          payload.addChildElement(elementName).addTextNode(correlationID);
          // para.setValue(correlationID);
        } else if (para.getType().equalsIgnoreCase("callbackaddress")) {
          LOG.debug("Found CallbackAddress Element! Put in CallbackAddress \"" + SOAPMessageGenerator.callbackAddress + "\".");
          final Name elementName = envelope.createName(para.getName(), "tosca", messageNS);
          payload.addChildElement(elementName).addTextNode(SOAPMessageGenerator.callbackAddress);
          // para.setValue(SOAPMessageGenerator.callbackAddress);
        } else if (para.getName().equalsIgnoreCase("csarName")) {
          LOG.debug("Found csarName Element! Put in csarName \"" + csarID + "\".");
          final Name elementName = envelope.createName(para.getName(), "tosca", messageNS);
          payload.addChildElement(elementName).addTextNode(csarID.toString());
          // para.setValue(csarID);
        } else if (para.getName().equalsIgnoreCase("containerApiAddress")) {
          LOG.debug("Found containerApiAddress Element! Put in containerApiAddress \"" + Settings.CONTAINER_API_LEGACY + "\".");
          final Name elementName = envelope.createName(para.getName(), "tosca", messageNS);
          payload.addChildElement(elementName).addTextNode(Settings.CONTAINER_API_LEGACY);
          // para.setValue(Settings.CONTAINER_API);
        } else if (para.getName().equalsIgnoreCase("instanceDataAPIUrl")) {
          LOG.debug("Found instanceDataAPIUrl Element! Put in instanceDataAPIUrl \"" + Settings.CONTAINER_INSTANCEDATA_LEGACY_API + "\".");
          final Name elementName = envelope.createName(para.getName(), "tosca", messageNS);
          payload.addChildElement(elementName).addTextNode(Settings.CONTAINER_INSTANCEDATA_LEGACY_API);
        } else if (para.getName().equalsIgnoreCase("csarEntrypoint")) {
          LOG.debug("Found csarEntrypoint Element! Put in instanceDataAPIUrl \"" + Settings.CONTAINER_API_LEGACY + "/" + csarID + "\".");
          final Name elementName = envelope.createName(para.getName(), "tosca", messageNS);
          payload.addChildElement(elementName).addTextNode(Settings.CONTAINER_API_LEGACY + "/" + csarID);
        } else {
          LOG.debug("Found element \"" + para.getName() + "\"! Put in \"" + para.getValue() + "\".");
          final Name elementName = envelope.createName(para.getName(), "tns", messageNS);
          payload.addChildElement(elementName).addTextNode(para.getValue());
        }
      }

      soapMessage.saveChanges();
      return soapMessage;
    } catch (final SOAPException e) {
      LOG.error(e.getLocalizedMessage());
    }
    return null;
  }

  /**
   * Receives the CallbackAddress.
   */
  @Override
  public void handleEvent(final Event event) {
    SOAPMessageGenerator.callbackAddress = (String) event.getProperty("callbackAddress");
    LOG.debug("Recieved the current callback address: \"" + SOAPMessageGenerator.callbackAddress + "\".");
  }
}
