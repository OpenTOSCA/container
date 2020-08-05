package org.opentosca.bus.management.api.soaphttp.processor;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.winery.model.tosca.TNodeTemplate;

import com.google.gson.Gson;

import ch.qos.logback.classic.pattern.Util;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.apache.commons.io.FilenameUtils;
import org.apache.cxf.binding.soap.SoapHeader;
import org.apache.cxf.headers.Header;
import org.opentosca.bus.management.api.soaphttp.model.Doc;
import org.opentosca.bus.management.api.soaphttp.model.InvokeOperationAsync;
import org.opentosca.bus.management.api.soaphttp.model.InvokeOperationSync;
import org.opentosca.bus.management.api.soaphttp.model.InvokePlan;
import org.opentosca.bus.management.api.soaphttp.model.NotifyPartner;
import org.opentosca.bus.management.api.soaphttp.model.NotifyPartners;
import org.opentosca.bus.management.api.soaphttp.model.ParamsMap;
import org.opentosca.bus.management.api.soaphttp.model.ParamsMapItemType;
import org.opentosca.bus.management.api.soaphttp.model.ReceiveNotifyPartner;
import org.opentosca.bus.management.api.soaphttp.model.ReceiveNotifyPartners;
import org.opentosca.bus.management.header.MBHeader;
import org.opentosca.bus.management.service.IManagementBusService;
import org.opentosca.bus.management.utils.MBUtils;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.engine.ResolvedArtifacts;
import org.opentosca.container.core.engine.ResolvedArtifacts.ResolvedDeploymentArtifact;
import org.opentosca.container.core.engine.ToscaEngine;
import org.opentosca.container.core.engine.next.ContainerEngine;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.service.CsarStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Request-Processor of the Management Bus-SOAP/HTTP-API.<br>
 * <br>
 * <p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * <p>
 * This processor processes the incoming requests of the Management
 * Bus-SOAP/HTTP-API. It transforms the incoming unmarshalled SOAP message into
 * a from the Management Bus understandable camel exchange message. The
 * MBHeader-Enum is used here to define the headers of the exchange message.
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 * @see MBHeader
 */
public class RequestProcessor implements Processor {
	public static final String MB_MANAGEMENT_SOAPHTTP_API_ID = "org.opentosca.bus.management.api.soaphttp";

	private static final Logger LOG = LoggerFactory.getLogger(RequestProcessor.class);
	private final CsarStorageService csarStorage;
	private final ContainerEngine containerEngine;
	private final IManagementBusService managementBusService;

	// manually instantiated from within the Route definition. Therefore no @Inject
	// annotation
	public RequestProcessor(CsarStorageService csarStorage, ContainerEngine containerEngine, IManagementBusService managementBusService) {
		this.csarStorage = csarStorage;
		this.containerEngine = containerEngine;	
		this.managementBusService = managementBusService;
	}

	@Override
	public void process(final Exchange exchange) throws Exception {

		// copy SOAP headers in camel exchange object
		LOG.debug("copy SOAP headers in camel exchange object");
		@SuppressWarnings("unchecked")
		final List<SoapHeader> soapHeaders = (List<SoapHeader>) exchange.getIn().getHeader(Header.HEADER_LIST);
		Element elementx;
		if (soapHeaders != null) {
			for (final SoapHeader header : soapHeaders) {
				elementx = (Element) header.getObject();
				exchange.getIn().setHeader(elementx.getLocalName(), elementx.getTextContent());
			}
		}

		ParamsMap paramsMap = null;
		Doc doc = null;
		String planCorrelationID = null;
		String csarIDString = null;
		String serviceInstanceID = null;
		String callbackAddress = null;
		String messageID = null;
		String interfaceName = null;
		String operationName = null;
		if (exchange.getIn().getBody() instanceof InvokeOperationAsync) {

			LOG.debug("Processing async operation invocation");

			final InvokeOperationAsync invokeIaRequest = (InvokeOperationAsync) exchange.getIn().getBody();

			csarIDString = invokeIaRequest.getCsarID();

			serviceInstanceID = invokeIaRequest.getServiceInstanceID();
			exchange.getIn().setHeader(MBHeader.SERVICEINSTANCEID_URI.toString(), new URI(serviceInstanceID));

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

			// Support new Deployment Artifact Header
			final List<ResolvedDeploymentArtifact> resolvedDAs = new ArrayList<>();
			if (nodeTemplateID != null) {
				final Csar csar = csarStorage.findById(new CsarId(csarIDString));
				final TNodeTemplate nodeTemplate = ToscaEngine.resolveNodeTemplate(csar, serviceTemplateID,
						nodeTemplateID);

				final ResolvedArtifacts resolvedArtifacts = containerEngine.resolvedDeploymentArtifacts(csar,
						nodeTemplate);
				resolvedDAs.addAll(resolvedArtifacts.getDeploymentArtifacts());
			}

			final URL serviceInstanceIDUrl = new URL(serviceInstanceID);
			final HashMap<QName, HashMap<String, String>> DAs = new HashMap<>();
			for (final ResolvedDeploymentArtifact resolvedDeploymentArtifact : resolvedDAs) {
				LOG.info("DA name:" + resolvedDeploymentArtifact.getName());
				final QName DAname = resolvedDeploymentArtifact.getType();
				final HashMap<String, String> DAfiles = new HashMap<>();
				DAs.put(DAname, DAfiles);
				for (final String s : resolvedDeploymentArtifact.getReferences()) {
					LOG.info("DA getReferences:" + s);
					final String url = serviceInstanceIDUrl.getProtocol() + "://" + serviceInstanceIDUrl.getHost() + ":"
							+ serviceInstanceIDUrl.getPort() + "/csars/" + csarIDString + "/content/";
					final String urlWithDa = url + s;

					LOG.info(urlWithDa);
					DAfiles.put(FilenameUtils.getName(urlWithDa), urlWithDa);
				}
			}
			final Gson gson = new Gson();
			exchange.getIn().setHeader(MBHeader.DEPLOYMENT_ARTIFACTS_STRING.toString(), gson.toJson(DAs));
			LOG.info("serviceInstanceID:" + serviceInstanceID);
			LOG.info("OPENTOSCA_CONTAINER_HOSTNAME:" + Settings.OPENTOSCA_CONTAINER_HOSTNAME);
			LOG.info("OPENTOSCA_CONTAINER_PORT:" + Settings.OPENTOSCA_CONTAINER_PORT);
			LOG.info("serviceTemplateIDNamespaceURI:" + serviceTemplateIDNamespaceURI);

			interfaceName = invokeIaRequest.getInterfaceName();

			if (interfaceName != null && !(interfaceName.equals("?") || interfaceName.isEmpty())) {
				exchange.getIn().setHeader(MBHeader.INTERFACENAME_STRING.toString(), interfaceName);
			}

			operationName = invokeIaRequest.getOperationName();

			callbackAddress = invokeIaRequest.getReplyTo();

			messageID = invokeIaRequest.getMessageID();

			paramsMap = invokeIaRequest.getParams();

			doc = invokeIaRequest.getDoc();

			if (callbackAddress != null && !(callbackAddress.isEmpty() || callbackAddress.equals("?"))) {
				exchange.getIn().setHeader("ReplyTo", callbackAddress);
			}

			if (messageID != null && !(messageID.isEmpty() || messageID.equals("?"))) {
				exchange.getIn().setHeader("MessageID", messageID);
			}

			exchange.getIn().setHeader(CxfConstants.OPERATION_NAME, "invokeIA");
		} else if (exchange.getIn().getBody() instanceof InvokeOperationSync) {

			LOG.debug("Processing sync operation invocation");

			final InvokeOperationSync invokeIaRequest = (InvokeOperationSync) exchange.getIn().getBody();

			csarIDString = invokeIaRequest.getCsarID();

			serviceInstanceID = invokeIaRequest.getServiceInstanceID();
			exchange.getIn().setHeader(MBHeader.SERVICEINSTANCEID_URI.toString(), new URI(serviceInstanceID));

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

			if (interfaceName != null && !(interfaceName.equals("?") || interfaceName.isEmpty())) {
				exchange.getIn().setHeader(MBHeader.INTERFACENAME_STRING.toString(), interfaceName);
			}

			operationName = invokeIaRequest.getOperationName();

			paramsMap = invokeIaRequest.getParams();

			doc = invokeIaRequest.getDoc();

			exchange.getIn().setHeader(CxfConstants.OPERATION_NAME, "invokeIA");
		} else if (exchange.getIn().getBody() instanceof InvokePlan) {

			LOG.debug("Processing plan invocation");

			final InvokePlan invokePlanRequest = (InvokePlan) exchange.getIn().getBody();

			csarIDString = invokePlanRequest.getCsarID();

			serviceInstanceID = invokePlanRequest.getServiceInstanceID();
			if (serviceInstanceID != null) {
				exchange.getIn().setHeader(MBHeader.SERVICEINSTANCEID_URI.toString(), new URI(serviceInstanceID));
			}

			final String planIDNamespaceURI = invokePlanRequest.getPlanIDNamespaceURI();
			final String planIDLocalPart = invokePlanRequest.getPlanIDLocalPart();

			final QName planID = new QName(planIDNamespaceURI, planIDLocalPart);
			exchange.getIn().setHeader(MBHeader.PLANID_QNAME.toString(), planID);

			operationName = invokePlanRequest.getOperationName();

			callbackAddress = invokePlanRequest.getReplyTo();

			messageID = invokePlanRequest.getMessageID();

			paramsMap = invokePlanRequest.getParams();

			doc = invokePlanRequest.getDoc();

			if (callbackAddress != null && !(callbackAddress.isEmpty() || callbackAddress.equals("?"))) {
				exchange.getIn().setHeader("ReplyTo", callbackAddress);
			}

			if (messageID != null && !(messageID.isEmpty() || messageID.equals("?"))) {
				exchange.getIn().setHeader("MessageID", messageID);
			}

			exchange.getIn().setHeader(CxfConstants.OPERATION_NAME, "invokePlan");

		} else if (exchange.getIn().getBody() instanceof NotifyPartners) {
			// retrieve information about NotifyPartners request and add to exchange headers
			LOG.debug("Processing NotifyPartners");

			final NotifyPartners notifyPartnersRequest = (NotifyPartners) exchange.getIn().getBody();

			planCorrelationID = notifyPartnersRequest.getPlanCorrelationID();
			exchange.getIn().setHeader(MBHeader.PLANCORRELATIONID_STRING.toString(), planCorrelationID);

			final QName serviceTemplateID = new QName(notifyPartnersRequest.getServiceTemplateIDNamespaceURI(),
					notifyPartnersRequest.getServiceTemplateIDLocalPart());
			exchange.getIn().setHeader(MBHeader.SERVICETEMPLATEID_QNAME.toString(), serviceTemplateID);

			csarIDString = notifyPartnersRequest.getCsarID();
			paramsMap = notifyPartnersRequest.getParams();
			doc = notifyPartnersRequest.getDoc();

			exchange.getIn().setHeader(CxfConstants.OPERATION_NAME, "notifyPartners");
		} else if (exchange.getIn().getBody() instanceof NotifyPartner) {
			// retrieve information about NotifyPartner request and add to exchange headers

			LOG.debug("Processing NotifyPartner");

			final NotifyPartner notifyPartnerRequest = (NotifyPartner) exchange.getIn().getBody();

			planCorrelationID = notifyPartnerRequest.getPlanCorrelationID();
			exchange.getIn().setHeader(MBHeader.PLANCORRELATIONID_STRING.toString(), planCorrelationID);

			final QName serviceTemplateID = new QName(notifyPartnerRequest.getServiceTemplateIDNamespaceURI(),
					notifyPartnerRequest.getServiceTemplateIDLocalPart());
			exchange.getIn().setHeader(MBHeader.SERVICETEMPLATEID_QNAME.toString(), serviceTemplateID);

			csarIDString = notifyPartnerRequest.getCsarID();
			paramsMap = notifyPartnerRequest.getParams();
			doc = notifyPartnerRequest.getDoc();

			exchange.getIn().setHeader(CxfConstants.OPERATION_NAME, "notifyPartner");
		} else if (exchange.getIn().getBody() instanceof ReceiveNotifyPartner) {

			LOG.debug("Invoking plan after reception of ReceiveNotifyPartner");

			final ReceiveNotifyPartner receiveNotifyRequest = (ReceiveNotifyPartner) exchange.getIn().getBody();

			final QName serviceTemplateID = new QName(receiveNotifyRequest.getServiceTemplateIDNamespaceURI(),
					receiveNotifyRequest.getServiceTemplateIDLocalPart());

			// get plan ID from the boundary definitions

			final QName planID = MBUtils.findPlanByOperation(
					this.csarStorage.findById(new CsarId(receiveNotifyRequest.getCsarID())),
					"OpenTOSCA-Lifecycle-Interface", "initiate");
			// create the body for the receiveNotify request that must be send to the plan
			final JAXBContext jc = JAXBContext.newInstance(ReceiveNotifyPartner.class);
			final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			final DocumentBuilder db = dbf.newDocumentBuilder();
			final Document document = db.newDocument();
			final Marshaller marshaller = jc.createMarshaller();
			marshaller.marshal(receiveNotifyRequest, document);
			document.renameNode(document.getDocumentElement(), "http://siserver.org/schema", "receiveNotify");
			exchange.getIn().setBody(document);

			// add required header fields for the bus
			exchange.getIn().setHeader(MBHeader.PLANCORRELATIONID_STRING.toString(),
					receiveNotifyRequest.getPlanCorrelationID());
			exchange.getIn().setHeader(MBHeader.CALLBACK_BOOLEAN.toString(), true);
			exchange.getIn().setHeader(MBHeader.SERVICETEMPLATEID_QNAME.toString(), serviceTemplateID);
			exchange.getIn().setHeader(MBHeader.CSARID.toString(), receiveNotifyRequest.getCsarID());
			exchange.getIn().setHeader(MBHeader.PLANID_QNAME.toString(), planID);
			// exchange.getIn().setHeader(MBHeader.APIID_STRING.toString(),
			// Activator.apiID);
			exchange.getIn().setHeader(MBHeader.OPERATIONNAME_STRING.toString(), "receiveNotify");
			exchange.getIn().setHeader(CxfConstants.OPERATION_NAME, "invokePlan");
			return;
		} else if (exchange.getIn().getBody() instanceof ReceiveNotifyPartners) {

			LOG.debug("Invoking plan after reception of ReceiveNotifyPartners");

			final ReceiveNotifyPartners receiveNotifyRequest = (ReceiveNotifyPartners) exchange.getIn().getBody();

			final QName serviceTemplateID = new QName(receiveNotifyRequest.getServiceTemplateIDNamespaceURI(),
					receiveNotifyRequest.getServiceTemplateIDLocalPart());

			// get plan ID from the boundary definitions
			final QName planID = MBUtils.findPlanByOperation(
					this.csarStorage.findById(new CsarId(receiveNotifyRequest.getCsarID())),
					"OpenTOSCA-Lifecycle-Interface", "initiate");

			// create plan invocation request from given parameters
			exchange.getIn().setBody(createRequestBody(receiveNotifyRequest.getCsarID(), serviceTemplateID,
					receiveNotifyRequest.getPlanCorrelationID()));

			// add required header fields for the bus
			exchange.getIn().setHeader(MBHeader.PLANCORRELATIONID_STRING.toString(),
					receiveNotifyRequest.getPlanCorrelationID());
			exchange.getIn().setHeader(MBHeader.SERVICETEMPLATEID_QNAME.toString(), serviceTemplateID);
			exchange.getIn().setHeader(MBHeader.CSARID.toString(), receiveNotifyRequest.getCsarID());
			exchange.getIn().setHeader(MBHeader.PLANID_QNAME.toString(), planID);
			// exchange.getIn().setHeader(MBHeader.APIID_STRING.toString(),
			// Activator.apiID);
			exchange.getIn().setHeader(MBHeader.OPERATIONNAME_STRING.toString(), "initiate");
			exchange.getIn().setHeader(CxfConstants.OPERATION_NAME, "invokePlan");

			final String partner = receiveNotifyRequest.getParams().getParam().stream()
					.filter(param -> param.getKey().equals("SendingPartner")).findFirst().map(param -> param.getValue())
					.orElse(null);
			LOG.debug("Adding partner: {}", partner);
			this.managementBusService.addPartnerToReadyList(receiveNotifyRequest.getPlanCorrelationID(), partner);
			//addPartnerToReadyList(receiveNotifyRequest.getPlanCorrelationID(), partner);
			return;
		}

		final CsarId csarID = new CsarId(csarIDString);

		exchange.getIn().setHeader(MBHeader.CSARID.toString(), csarID);
		exchange.getIn().setHeader(MBHeader.OPERATIONNAME_STRING.toString(), operationName);
		exchange.getIn().setHeader(MBHeader.APIID_STRING.toString(), MB_MANAGEMENT_SOAPHTTP_API_ID);

		if (paramsMap != null) {
			// put key-value params into camel exchange body as hashmap
			final HashMap<String, String> params = new HashMap<>();

			for (final ParamsMapItemType param : paramsMap.getParam()) {
				params.put(param.getKey(), param.getValue());
			}
			exchange.getIn().setBody(params);
		} else if (doc != null && doc.getAny() != null) {
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

	private Map<String, String> createRequestBody(final String csarID, final QName serviceTemplateID,
			final String planCorrelationID) {

		String str = Settings.CONTAINER_INSTANCEDATA_API.replace("{csarid}", csarID);
		try {
			str = str.replace("{servicetemplateid}",
					URLEncoder.encode(URLEncoder.encode(serviceTemplateID.toString(), "UTF-8"), "UTF-8"));
		} catch (final UnsupportedEncodingException e) {
			LOG.error("Couldn't encode Service Template URL", e);
		}

		final HashMap<String, String> map = new HashMap<>();
		map.put("instanceDataAPIUrl", str);
		map.put("csarEntrypoint", Settings.CONTAINER_API_LEGACY + "/CSARs/" + csarID);
		map.put("CorrelationID", planCorrelationID);
		map.put("planCallbackAddress_invoker", "");
		return map;
	}

	
}
