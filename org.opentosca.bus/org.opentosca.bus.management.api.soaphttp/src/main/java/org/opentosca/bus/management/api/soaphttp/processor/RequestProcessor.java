package org.opentosca.bus.management.api.soaphttp.processor;

import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.model.tosca.TServiceTemplate;

import com.google.gson.Gson;
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
import org.opentosca.bus.management.service.impl.Constants;
import org.opentosca.bus.management.utils.MBUtils;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.convention.Types;
import org.opentosca.container.core.engine.ResolvedArtifacts;
import org.opentosca.container.core.engine.ResolvedArtifacts.ResolvedDeploymentArtifact;
import org.opentosca.container.core.engine.ToscaEngine;
import org.opentosca.container.core.engine.next.ContainerEngine;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.next.model.PlanInstance;
import org.opentosca.container.core.next.repository.PlanInstanceRepository;
import org.opentosca.container.core.plan.ChoreographyHandler;
import org.opentosca.container.core.service.CsarStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static org.opentosca.container.core.convention.PlanConstants.OpenTOSCA_BuildPlanOperation;
import static org.opentosca.container.core.convention.PlanConstants.OpenTOSCA_LifecycleInterface;

/**
 * Request-Processor of the Management Bus-SOAP/HTTP-API.<br>
 * <br>
 * <p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * <p>
 * This processor processes the incoming requests of the Management Bus-SOAP/HTTP-API. It transforms the incoming
 * unmarshalled SOAP message into a from the Management Bus understandable camel exchange message. The MBHeader-Enum is
 * used here to define the headers of the exchange message.
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
    private final ChoreographyHandler choreoHandler;

    // manually instantiated from within the Route definition. Therefore no @Inject
    // annotation
    public RequestProcessor(CsarStorageService csarStorage, ContainerEngine containerEngine,
                            IManagementBusService managementBusService, ChoreographyHandler choreoHandler) {
        this.csarStorage = csarStorage;
        this.containerEngine = containerEngine;
        this.managementBusService = managementBusService;
        this.choreoHandler = choreoHandler;
    }

    @Override
    public void process(final Exchange exchange) throws Exception {

        // copy SOAP headers in camel exchange object
        LOG.debug("copy SOAP headers in camel exchange object");
        @SuppressWarnings("unchecked") final List<SoapHeader> soapHeaders = (List<SoapHeader>) exchange.getIn().getHeader(Header.HEADER_LIST);
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
                final Csar csar = this.csarStorage.findById(new CsarId(csarIDString));
                final TServiceTemplate serviceTemplate = csar.entryServiceTemplate();

                final TNodeTemplate nodeTemplate = ToscaEngine.resolveNodeTemplate(csar, serviceTemplateID,
                    nodeTemplateID);

                if (Types.openStackTrainNodeType.getLocalPart().equals(nodeTemplate.getType().getLocalPart())
                    || Types.openStackTrainNodeType_legacy.getLocalPart()
                    .equals(nodeTemplate.getType().getLocalPart())) {
                    final List<TNodeTemplate> relatedSourceNodeTemplate = ToscaEngine.getRelatedSourceNodeTemplate(
                        serviceTemplate, nodeTemplate, Types.hostedOnRelationType, Types.deployedOnRelationType,
                        Types.dependsOnRelationType);
                    for (final TNodeTemplate nodeTemplate1 : relatedSourceNodeTemplate) {
                        // FIXME? TODO? Is the DA header only good for ubuntu DAs ? How about for future IAs this may come in handy right ?
                        if (nodeTemplate1.getName().startsWith("Ubuntu")) {
                            final TNodeType nodeType = ToscaEngine.resolveNodeTypeReference(csar,
                                nodeTemplate1.getType());

                            List<TNodeTypeImplementation> nodeTypeImpls = csar.nodeTypeImplementations();
                            for (TNodeTypeImplementation nodeTypeImpl : nodeTypeImpls) {
                                if (nodeTypeImpl.getNodeType().equals(nodeType.getQName())
                                    && nodeTypeImpl.getDeploymentArtifacts() != null) {
                                    final ResolvedArtifacts resolvedArtifacts = this.containerEngine
                                        .resolvedDeploymentArtifactsOfNodeTypeImpl(csar, nodeTypeImpl);
                                    resolvedDAs.addAll(resolvedArtifacts.getDeploymentArtifacts());
                                }
                            }
                        }
                    }
                }

                final ResolvedArtifacts resolvedArtifacts = this.containerEngine.resolvedDeploymentArtifacts(csar,
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

            planCorrelationID = notifyPartnersRequest.getPlanChorCorrelation();
            exchange.getIn().setHeader(MBHeader.PLANCHORCORRELATIONID_STRING.toString(), planCorrelationID);

            exchange.getIn().setHeader(MBHeader.SERVICETEMPLATEID_QNAME.toString(),
                notifyPartnersRequest.getServiceTemplateIDLocalPart());

            csarIDString = notifyPartnersRequest.getCsarID();
            paramsMap = notifyPartnersRequest.getParams();
            doc = notifyPartnersRequest.getDoc();

            exchange.getIn().setHeader(CxfConstants.OPERATION_NAME, "notifyPartners");
        } else if (exchange.getIn().getBody() instanceof NotifyPartner) {
            // retrieve information about NotifyPartner request and add to exchange headers

            LOG.debug("Processing NotifyPartner");

            final NotifyPartner notifyPartnerRequest = (NotifyPartner) exchange.getIn().getBody();

            // set choreography headers
            final PlanInstance planInstance = new PlanInstanceRepository()
                .findByCorrelationId(notifyPartnerRequest.getPlanCorrelationID());
            exchange.getIn().setHeader(MBHeader.CHOREOGRAPHY_PARTNERS.toString(),
                planInstance.getChoreographyPartners());
            exchange.getIn().setHeader(MBHeader.PLANCHORCORRELATIONID_STRING.toString(),
                planInstance.getChoreographyCorrelationId());
            exchange.getIn().setHeader(MBHeader.SERVICETEMPLATEID_QNAME.toString(),
                notifyPartnerRequest.getServiceTemplateIDLocalPart());

            csarIDString = notifyPartnerRequest.getCsarID();
            paramsMap = notifyPartnerRequest.getParams();
            doc = notifyPartnerRequest.getDoc();

            exchange.getIn().setHeader(CxfConstants.OPERATION_NAME, "notifyPartner");
        } else if (exchange.getIn().getBody() instanceof ReceiveNotifyPartner) {

            LOG.debug("Invoking plan after reception of ReceiveNotifyPartner");

            final ReceiveNotifyPartner receiveNotifyRequest = (ReceiveNotifyPartner) exchange.getIn().getBody();

            final String receivingPartner = getParamByName(receiveNotifyRequest.getParams(),
                Constants.RECEIVING_PARTNER_PARAM);
            final String appChoreoId = getAppChoreoId(receiveNotifyRequest.getParams());
            if (appChoreoId == null) {
                LOG.warn("Received NotifyPartners message but found no participating CSAR, message:  {}",
                    receiveNotifyRequest);
                return;
            }
            final Csar choreoCsar = this.choreoHandler.getChoreographyCsar(appChoreoId, this.csarStorage.findAll(),
                receivingPartner);
            if (choreoCsar == null) {
                LOG.warn("Received NotifyPartners message but found no participating CSAR, message:  {}",
                    receiveNotifyRequest);
                return;
            }
            final TServiceTemplate choreoServiceTemplate = choreoCsar.entryServiceTemplate();

            // get plan ID from the boundary definitions

            final QName planID = MBUtils.findPlanByOperation(choreoCsar, OpenTOSCA_LifecycleInterface, OpenTOSCA_BuildPlanOperation);

            final String planCorrelationId = new PlanInstanceRepository()
                .findByChoreographyCorrelationId(receiveNotifyRequest.getPlanChorCorrelation(), planID)
                .getCorrelationId();
            receiveNotifyRequest.setPlanCorrelationID(planCorrelationId);
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
            exchange.getIn().setHeader(MBHeader.PLANCHORCORRELATIONID_STRING.toString(),
                receiveNotifyRequest.getPlanChorCorrelation());

            exchange.getIn().setHeader(MBHeader.PLANCORRELATIONID_STRING.toString(), planCorrelationId);

            exchange.getIn().setHeader(MBHeader.CALLBACK_BOOLEAN.toString(), true);
            exchange.getIn().setHeader(MBHeader.SERVICETEMPLATEID_QNAME.toString(), choreoServiceTemplate.getId());
            exchange.getIn().setHeader(MBHeader.CSARID.toString(), choreoCsar.id().csarName());
            exchange.getIn().setHeader(MBHeader.PLANID_QNAME.toString(), planID);
            // exchange.getIn().setHeader(MBHeader.APIID_STRING.toString(),
            // Activator.apiID);
            exchange.getIn().setHeader(MBHeader.OPERATIONNAME_STRING.toString(), "receiveNotify");
            exchange.getIn().setHeader(CxfConstants.OPERATION_NAME, "invokePlan");
            return;
        } else if (exchange.getIn().getBody() instanceof ReceiveNotifyPartners) {

            LOG.debug("Invoking plan after reception of ReceiveNotifyPartners");

            final ReceiveNotifyPartners receiveNotifyRequest = (ReceiveNotifyPartners) exchange.getIn().getBody();

            final String receivingPartner = getParamByName(receiveNotifyRequest.getParams(),
                Constants.RECEIVING_PARTNER_PARAM);
            final String appChoreoId = getAppChoreoId(receiveNotifyRequest.getParams());
            if (appChoreoId == null) {
                LOG.warn("Received NotifyPartners message but found no participating CSAR, message:  {}",
                    receiveNotifyRequest);
                return;
            }
            final Csar choreoCsar = this.choreoHandler.getChoreographyCsar(appChoreoId, this.csarStorage.findAll(),
                receivingPartner);
            if (choreoCsar == null) {
                LOG.warn("Received NotifyPartners message but found no participating CSAR, message:  {}",
                    receiveNotifyRequest);
                return;
            }
            final TServiceTemplate choreoServiceTemplate = choreoCsar.entryServiceTemplate();

            final QName serviceTemplateID = new QName(choreoServiceTemplate.getTargetNamespace(),
                choreoServiceTemplate.getId());

            // get plan ID from the boundary definitions
            final QName planID = MBUtils.findPlanByOperation(choreoCsar, OpenTOSCA_LifecycleInterface, OpenTOSCA_BuildPlanOperation);

            // create plan invocation request from given parameters
            exchange.getIn().setBody(createRequestBody(choreoCsar.id().csarName(), serviceTemplateID,
                receiveNotifyRequest.getPlanCorrelationID()));

            // add required header fields for the bus
            exchange.getIn().setHeader(MBHeader.PLANCHORCORRELATIONID_STRING.toString(),
                receiveNotifyRequest.getPlanChorCorrelation());
            exchange.getIn().setHeader(MBHeader.SERVICETEMPLATEID_QNAME.toString(), choreoServiceTemplate.getId());
            exchange.getIn().setHeader(MBHeader.CSARID.toString(), choreoCsar.id().csarName());
            exchange.getIn().setHeader(MBHeader.PLANID_QNAME.toString(), planID);
            // exchange.getIn().setHeader(MBHeader.APIID_STRING.toString(),
            // Activator.apiID);
            exchange.getIn().setHeader(MBHeader.OPERATIONNAME_STRING.toString(), OpenTOSCA_BuildPlanOperation);
            exchange.getIn().setHeader(CxfConstants.OPERATION_NAME, "invokePlan");

            final String partner = receiveNotifyRequest.getParams().getParam().stream()
                .filter(param -> param.getKey().equals("SendingPartner"))
                .findFirst().map(ParamsMapItemType::getValue)
                .orElse(null);
            LOG.debug("Adding partner: {}", partner);
            this.managementBusService.addPartnerToReadyList(receiveNotifyRequest.getPlanChorCorrelation(), partner);
            // addPartnerToReadyList(receiveNotifyRequest.getPlanCorrelationID(), partner);
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

    public String getAppChoreoId(ParamsMap params) {
        return getParamByName(params, MBHeader.APP_CHOREO_ID.toString());
    }

    public String getParamByName(ParamsMap params, String name) {
        final Iterator<?> iter = params.getParam().iterator();
        String appChoreoId = null;
        while (iter.hasNext()) {
            final ParamsMapItemType item = (ParamsMapItemType) iter.next();
            if (item.getKey().equals(name)) {
                appChoreoId = item.getValue();
                break;
            }
        }

        return appChoreoId;
    }

    private Map<String, String> createRequestBody(final String csarID, final QName serviceTemplateID,
                                                  final String planCorrelationID) {

        String str = Settings.CONTAINER_INSTANCEDATA_API.replace("{csarid}", csarID);
        str = str.replace("{servicetemplateid}",
            URLEncoder.encode(URLEncoder.encode(serviceTemplateID.getLocalPart(), StandardCharsets.UTF_8), StandardCharsets.UTF_8));

        final HashMap<String, String> map = new HashMap<>();
        map.put("instanceDataAPIUrl", str);
        map.put("csarEntrypoint", Settings.OPENTOSCA_CONTAINER_CONTENT_API.replace("{csarid}", csarID));
        map.put("CorrelationID", planCorrelationID);
        map.put("planCallbackAddress_invoker", "");
        return map;
    }
}
