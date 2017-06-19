package org.opentosca.bus.management.api.soaphttp.processor;

import java.net.URI;
import java.util.HashMap;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.apache.cxf.binding.soap.SoapHeader;
import org.apache.cxf.headers.Header;
import org.opentosca.bus.management.api.soaphttp.Activator;
import org.opentosca.bus.management.api.soaphttp.model.Doc;
import org.opentosca.bus.management.api.soaphttp.model.InvokeOperationAsync;
import org.opentosca.bus.management.api.soaphttp.model.InvokeOperationSync;
import org.opentosca.bus.management.api.soaphttp.model.InvokePlan;
import org.opentosca.bus.management.api.soaphttp.model.ParamsMap;
import org.opentosca.bus.management.api.soaphttp.model.ParamsMapItemType;
import org.opentosca.bus.management.header.MBHeader;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Request-Processor of the Management Bus-SOAP/HTTP-API.<br>
 * <br>
 *
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * This processor processes the incoming requests of the Management
 * Bus-SOAP/HTTP-API. It transforms the incoming unmarshalled SOAP message into
 * a from the Management Bus understandable camel exchange message. The
 * MBHeader-Enum is used here to define the headers of the exchange message.
 *
 * @see MBHeader
 *
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 *
 */
public class RequestProcessor implements Processor {

	final private static Logger LOG = LoggerFactory.getLogger(RequestProcessor.class);


	@Override
	public void process(final Exchange exchange) throws Exception {

		ParamsMap paramsMap = null;
		Doc doc = null;
		String csarIDString = null;
		String serviceInstanceID = null;
		String callbackAddress = null;
		String messageID = null;
		String interfaceName = null;
		String operationName = null;

		// copy SOAP headers in camel exchange object
		RequestProcessor.LOG.debug("copy SOAP headers in camel exchange object");
		@SuppressWarnings("unchecked")
		final List<SoapHeader> soapHeaders = (List<SoapHeader>) exchange.getIn().getHeader(Header.HEADER_LIST);
		Element elementx;
		if (soapHeaders != null) {
			for (final SoapHeader header : soapHeaders) {
				elementx = (Element) header.getObject();
				exchange.getIn().setHeader(elementx.getLocalName(), elementx.getTextContent());
			}
		}

		if (exchange.getIn().getBody() instanceof InvokeOperationAsync) {

			RequestProcessor.LOG.debug("Processing async operation invocation");

			final InvokeOperationAsync invokeIaRequest = (InvokeOperationAsync) exchange.getIn().getBody();

			csarIDString = invokeIaRequest.getCsarID();

			serviceInstanceID = invokeIaRequest.getServiceInstanceID();

			final String nodeInstanceID = invokeIaRequest.getNodeInstanceID();
			exchange.getIn().setHeader(MBHeader.NODEINSTANCEID_STRING.toString(), nodeInstanceID);

			final String serviceTemplateIDNamespaceURI = invokeIaRequest.getServiceTemplateIDNamespaceURI();
			final String serviceTemplateIDLocalPart = invokeIaRequest.getServiceTemplateIDLocalPart();

			final QName serviceTemplateID = new QName(serviceTemplateIDNamespaceURI, serviceTemplateIDLocalPart);

			exchange.getIn().setHeader(MBHeader.SERVICETEMPLATEID_QNAME.toString(), serviceTemplateID);

			final String nodeTemplateID = invokeIaRequest.getNodeTemplateID();
			exchange.getIn().setHeader(MBHeader.NODETEMPLATEID_STRING.toString(), nodeTemplateID);

			final String relationshipTemplateID = invokeIaRequest.getRelationshipTemplateID();
			exchange.getIn().setHeader(MBHeader.RELATIONSHIPTEMPLATEID_STRING.toString(), relationshipTemplateID);

			interfaceName = invokeIaRequest.getInterfaceName();

			if ((interfaceName != null) && !(interfaceName.equals("?") || interfaceName.isEmpty())) {
				exchange.getIn().setHeader(MBHeader.INTERFACENAME_STRING.toString(), interfaceName);
			}

			operationName = invokeIaRequest.getOperationName();

			callbackAddress = invokeIaRequest.getReplyTo();

			messageID = invokeIaRequest.getMessageID();

			paramsMap = invokeIaRequest.getParams();

			doc = invokeIaRequest.getDoc();

			if ((callbackAddress != null) && !(callbackAddress.isEmpty() || callbackAddress.equals("?"))) {
				exchange.getIn().setHeader("ReplyTo", callbackAddress);
			}

			if ((messageID != null) && !(messageID.isEmpty() || messageID.equals("?"))) {
				exchange.getIn().setHeader("MessageID", messageID);
			}

			exchange.getIn().setHeader(CxfConstants.OPERATION_NAME, "invokeIA");

		}

		if (exchange.getIn().getBody() instanceof InvokeOperationSync) {

			RequestProcessor.LOG.debug("Processing sync operation invocation");

			final InvokeOperationSync invokeIaRequest = (InvokeOperationSync) exchange.getIn().getBody();

			csarIDString = invokeIaRequest.getCsarID();

			serviceInstanceID = invokeIaRequest.getServiceInstanceID();

			final String nodeInstanceID = invokeIaRequest.getNodeInstanceID();
			exchange.getIn().setHeader(MBHeader.NODEINSTANCEID_STRING.toString(), nodeInstanceID);

			final String serviceTemplateIDNamespaceURI = invokeIaRequest.getServiceTemplateIDNamespaceURI();
			final String serviceTemplateIDLocalPart = invokeIaRequest.getServiceTemplateIDLocalPart();

			final QName serviceTemplateID = new QName(serviceTemplateIDNamespaceURI, serviceTemplateIDLocalPart);

			exchange.getIn().setHeader(MBHeader.SERVICETEMPLATEID_QNAME.toString(), serviceTemplateID);

			final String nodeTemplateID = invokeIaRequest.getNodeTemplateID();
			exchange.getIn().setHeader(MBHeader.NODETEMPLATEID_STRING.toString(), nodeTemplateID);

			final String relationshipTemplateID = invokeIaRequest.getRelationshipTemplateID();
			exchange.getIn().setHeader(MBHeader.RELATIONSHIPTEMPLATEID_STRING.toString(), relationshipTemplateID);

			interfaceName = invokeIaRequest.getInterfaceName();

			if ((interfaceName != null) && !(interfaceName.equals("?") || interfaceName.isEmpty())) {
				exchange.getIn().setHeader(MBHeader.INTERFACENAME_STRING.toString(), interfaceName);
			}

			operationName = invokeIaRequest.getOperationName();

			paramsMap = invokeIaRequest.getParams();

			doc = invokeIaRequest.getDoc();

			exchange.getIn().setHeader(CxfConstants.OPERATION_NAME, "invokeIA");

		}

		if (exchange.getIn().getBody() instanceof InvokePlan) {

			RequestProcessor.LOG.debug("Processing plan invocation");

			final InvokePlan invokePlanRequest = (InvokePlan) exchange.getIn().getBody();

			csarIDString = invokePlanRequest.getCsarID();

			serviceInstanceID = invokePlanRequest.getServiceInstanceID();

			final String planIDNamespaceURI = invokePlanRequest.getPlanIDNamespaceURI();
			final String planIDLocalPart = invokePlanRequest.getPlanIDLocalPart();

			final QName planID = new QName(planIDNamespaceURI, planIDLocalPart);
			exchange.getIn().setHeader(MBHeader.PLANID_QNAME.toString(), planID);

			operationName = invokePlanRequest.getOperationName();

			callbackAddress = invokePlanRequest.getReplyTo();

			messageID = invokePlanRequest.getMessageID();

			paramsMap = invokePlanRequest.getParams();

			doc = invokePlanRequest.getDoc();

			if ((callbackAddress != null) && !(callbackAddress.isEmpty() || callbackAddress.equals("?"))) {
				exchange.getIn().setHeader("ReplyTo", callbackAddress);
			}

			if ((messageID != null) && !(messageID.isEmpty() || messageID.equals("?"))) {
				exchange.getIn().setHeader("MessageID", messageID);
			}

			exchange.getIn().setHeader(CxfConstants.OPERATION_NAME, "invokePlan");
		}

		final CSARID csarID = new CSARID(csarIDString);

		if (serviceInstanceID != null) {
			final URI serviceInstanceURI = new URI(serviceInstanceID);
			exchange.getIn().setHeader(MBHeader.SERVICEINSTANCEID_URI.toString(), serviceInstanceURI);
		}

		exchange.getIn().setHeader(MBHeader.CSARID.toString(), csarID);
		exchange.getIn().setHeader(MBHeader.OPERATIONNAME_STRING.toString(), operationName);
		exchange.getIn().setHeader(MBHeader.APIID_STRING.toString(), Activator.apiID);

		if (paramsMap != null) {
			// put key-value params into camel exchange body as hashmap
			final HashMap<String, String> params = new HashMap<>();

			for (final ParamsMapItemType param : paramsMap.getParam()) {
				params.put(param.getKey(), param.getValue());
			}
			exchange.getIn().setBody(params);

		}

		else if ((doc != null) && (doc.getAny() != null)) {
			final DocumentBuilderFactory dFact = DocumentBuilderFactory.newInstance();
			final DocumentBuilder build = dFact.newDocumentBuilder();
			final Document document = build.newDocument();

			final Element element = doc.getAny();

			document.adoptNode(element);
			document.appendChild(element);

			exchange.getIn().setBody(document);

		} else {
			exchange.getIn().setBody(null);
		}

	}
}
