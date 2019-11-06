package org.opentosca.bus.management.api.osgievent;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultExchange;
import org.glassfish.jersey.uri.UriComponent;
import org.opentosca.bus.management.header.MBHeader;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.model.endpoint.wsdl.WSDLEndpoint;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.NodeTemplateInstanceState;
import org.opentosca.container.core.next.model.PlanInstanceInput;
import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.container.core.next.model.RelationshipTemplateInstance;
import org.opentosca.container.core.next.model.RelationshipTemplateInstanceState;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.repository.SituationRepository;
import org.opentosca.container.core.service.ICoreEndpointService;
import org.opentosca.container.core.tosca.convention.Types;
import org.opentosca.container.engine.plan.plugin.IPlanEnginePlanRefPluginService;
import org.opentosca.container.engine.plan.plugin.bpel.BpelPlanEnginePlugin;
import org.opentosca.planbuilder.export.Exporter;
import org.opentosca.planbuilder.importer.Importer;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractPolicy;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractTopologyTemplate;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * EventHandler of the Management Bus-OSGi-Event-API.<br>
 * <br>
 *
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * Handles the events (receive and sent) of the Management Bus-OSGi-Event-API.
 *
 *
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 * @author Benjamin Weder - st100495@stud.uni-stuttgart.de
 * @author Kálmán Képes - kepes@iaas.uni-stuttgart.de
 */
public class MBEventHandler implements EventHandler {

    private static final String BPMNNS = "http://www.omg.org/spec/BPMN/20100524/MODEL";
    private static final String BPELNS = "http://docs.oasis-open.org/wsbpel/2.0/process/executable";

    private static Logger LOG = LoggerFactory.getLogger(MBEventHandler.class);

    private final ExecutorService executor = Executors.newFixedThreadPool(5);

    private EventAdmin eventAdmin;

    private ConsumerTemplate invokePlan(final String operationName, final String messageID, final boolean async,
                                        final Long serviceInstanceID, final QName serviceTemplateID,
                                        final Object message, final CSARID csarID, final QName planID,
                                        final String planLanguage) {
        MBEventHandler.LOG.debug("Plan invocation is asynchronous: {}", async);

        // create the headers for the Exchange which is send to the Management Bus
        final Map<String, Object> headers = new HashMap<>();
        headers.put(MBHeader.CSARID.toString(), csarID);
        headers.put(MBHeader.PLANID_QNAME.toString(), planID);
        headers.put(MBHeader.OPERATIONNAME_STRING.toString(), operationName);
        headers.put(MBHeader.PLANCORRELATIONID_STRING.toString(), messageID);
        headers.put(MBHeader.SERVICETEMPLATEID_QNAME.toString(), serviceTemplateID);
        headers.put("OPERATION", OsgiEventOperations.INVOKE_PLAN.getHeaderValue());
        headers.put("PlanLanguage", planLanguage);


        if (message instanceof HashMap) {
            MBEventHandler.LOG.debug("Invocation body is of type HashMap.");

            URI serviceInstanceURI;
            try {
                serviceInstanceURI = new URI(serviceInstanceID.toString());
                headers.put(MBHeader.SERVICEINSTANCEID_URI.toString(), serviceInstanceURI);
            }
            catch (final URISyntaxException e) {
                MBEventHandler.LOG.warn("Could not generate service instance URL: {}", e.getMessage(), e);
            }
        } else {
            MBEventHandler.LOG.warn("Invocation body is of type: {}", message.getClass());
        }

        // templates to communicate with the Management Bus
        final ProducerTemplate template = Activator.camelContext.createProducerTemplate();
        final ConsumerTemplate consumer = Activator.camelContext.createConsumerTemplate();

        MBEventHandler.LOG.debug("Correlation id: {}", messageID);
        MBEventHandler.LOG.debug("Sending message {}", message);

        // forward request to the Management Bus
        final Exchange requestExchange = new DefaultExchange(Activator.camelContext);
        requestExchange.getIn().setBody(message);
        requestExchange.getIn().setHeaders(headers);
        template.asyncSend("direct:invoke", requestExchange);
        return consumer;
    }

    @Override
    public void handleEvent(final Event event) {

        // Handle plan invoke requests
        if ("org_opentosca_plans/requests".equals(event.getTopic())) {
            MBEventHandler.LOG.debug("Process event of topic \"org_opentosca_plans/requests\".");

            final CSARID csarID = (CSARID) event.getProperty("CSARID");
            final QName planID = (QName) event.getProperty("PLANID");
            final String planLanguage = (String) event.getProperty("PLANLANGUAGE");

            if (planLanguage.startsWith(BPMNNS) || planLanguage.startsWith(BPELNS)) {
                MBEventHandler.LOG.debug("Plan invocation with plan language: {}", planLanguage);

                final String operationName = (String) event.getProperty("OPERATIONNAME");
                final String messageID = (String) event.getProperty("MESSAGEID");
                final boolean async = (boolean) event.getProperty("ASYNC");

                // Optional parameter if message is of type HashMap. Not needed for Document.
                final Long serviceInstanceID = (Long) event.getProperty("SERVICEINSTANCEID");
                final QName serviceTemplateID = (QName) event.getProperty("SERVICETEMPLATEID");
                // Should be of type Document or HashMap<String, String>. Maybe better handle them
                // with different topics.
                // final Object message = event.getProperty("BODY");
                Map<String, String> inputParameter = (Map<String, String>) event.getProperty("INPUTS");

                if (inputParameter == null) {
                    inputParameter = new HashMap<>();
                }

                final Map<String, String> message =
                    createRequestBody(csarID, serviceTemplateID, serviceInstanceID, inputParameter, messageID);

                final ConsumerTemplate consumer = invokePlan(operationName, messageID, async, serviceInstanceID,
                                                             serviceTemplateID, message, csarID, planID, planLanguage);

                // Threaded reception of response
                this.executor.submit(() -> {

                    Object response = null;

                    try {
                        consumer.start();
                        final Exchange exchange = consumer.receive("direct:response" + messageID);
                        response = exchange.getIn().getBody();
                        consumer.stop();
                    }
                    catch (final Exception e) {
                        MBEventHandler.LOG.error("Error occured: {}", e.getMessage(), e);
                        return;
                    }

                    MBEventHandler.LOG.debug("Received response for request with id {}.", messageID);

                    final Map<String, Object> responseMap = new HashMap<>();
                    responseMap.put("RESPONSE", response);
                    responseMap.put("MESSAGEID", messageID);
                    responseMap.put("PLANLANGUAGE", planLanguage);
                    final Event responseEvent = new Event("org_opentosca_plans/responses", responseMap);

                    MBEventHandler.LOG.debug("Posting response as OSGi event.");
                    this.eventAdmin.postEvent(responseEvent);
                });

            } else {
                MBEventHandler.LOG.warn("Unsupported plan language: {}", planLanguage);
            }
        }

        // Handle IA invoke requests
        if ("org_opentosca_ia/requests".equals(event.getTopic())) {
            MBEventHandler.LOG.debug("Process event of topic \"org_opentosca_ia/requests\".");

            // TODO when needed.
            // Adapt 'MBEventHandler - component.xml' to receive messages from this topic too...

        }

        if ("org_opentosca_situationadaptation/requests".equals(event.getTopic())) {
            MBEventHandler.LOG.debug("Received SituationAware Adapatioan Event");
            final ServiceTemplateInstance instance = (ServiceTemplateInstance) event.getProperty("SERVICEINSTANCE");

            final Map<String, Collection<Long>> nodeIds2situationIds =
                (Map<String, Collection<Long>>) event.getProperty("NODE2SITUATIONS");

            final Importer importer = new Importer();
            final Exporter exporter = new Exporter();

            final AbstractTopologyTemplate topology =
                importer.getMainDefinitions(instance.getCsarId()).getServiceTemplates().get(0).getTopologyTemplate();

            final ServiceTemplateInstanceConfiguration currentConfig =
                getCurrentServiceTemplateInstanceConfiguration(topology, instance);
            final ServiceTemplateInstanceConfiguration targetConfig =
                getValidServiceTemplateInstanceConfiguration(topology, nodeIds2situationIds);



            final Collection<String> currentConfigNodeIds =
                currentConfig.nodeTemplates.stream().map(x -> x.getId()).collect(Collectors.toList());
            final Collection<String> currentConfigRelationIds =
                currentConfig.relationshipTemplates.stream().map(x -> x.getId()).collect(Collectors.toList());

            final Collection<String> targetConfigNodeIds =
                targetConfig.nodeTemplates.stream().map(x -> x.getId()).collect(Collectors.toList());
            final Collection<String> targetConfigRelationIds =
                targetConfig.relationshipTemplates.stream().map(x -> x.getId()).collect(Collectors.toList());

            if (currentConfigNodeIds.equals(targetConfigNodeIds)
                & currentConfigRelationIds.equals(targetConfigRelationIds)) {
                MBEventHandler.LOG.debug("Current configuration is equal to target configuration, no adaptation is needed");
                return;
            }



            final WSDLEndpoint endpoint = getAdaptationPlanEndpoint(currentConfigNodeIds, currentConfigRelationIds,
                                                                    targetConfigNodeIds, targetConfigRelationIds);
            final String correlationID = String.valueOf(System.currentTimeMillis());
            QName planId = null;
            PlanType planType = null;
            Map<String, String> inputs = null;

            if (endpoint != null) {
                planId = endpoint.getPlanId();
                planType = PlanType.fromString(endpoint.getMetadata().get("PLANTYPE"));

                inputs = new HashMap<>();

                for (final String input : toStringCollection(endpoint.getMetadata().get("INPUTS"), ",")) {
                    inputs.put(input, null);
                }

            } else {
                try {
                    final BPELPlan adaptationPlan =
                        (BPELPlan) importer.generateAdaptationPlan(instance.getCsarId(), instance.getTemplateId(),
                                                                   currentConfigNodeIds, currentConfigRelationIds,
                                                                   targetConfigNodeIds, targetConfigRelationIds);

                    planType = PlanType.fromString(adaptationPlan.getType().getString());
                    inputs = createInput(adaptationPlan);
                    final Path tempFile = Files.createTempFile(adaptationPlan.getId(), ".zip");
                    exporter.exportToPlanFile(tempFile.toUri(), adaptationPlan);
                    final BpelPlanEnginePlugin deployPlugin = getBpelDeployPlugin();

                    final Map<String, String> endpointMetadata =
                        toEndpointMetadata(currentConfigNodeIds, currentConfigRelationIds, targetConfigNodeIds,
                                           targetConfigRelationIds);

                    endpointMetadata.put("PLANTYPE", planType.toString());
                    endpointMetadata.put("INPUTS", toCSV(inputs.keySet()));

                    planId = new QName(tempFile.getFileName().toString());
                    deployPlugin.deployPlanFile(tempFile, instance.getCsarId(), planId, endpointMetadata);
                }
                catch (final SystemException e) {
                    LOG.error("Internal error", e);
                    return;
                }
                catch (final IOException e) {
                    LOG.error("Couldn't read files", e);
                    return;
                }
                catch (final JAXBException e) {
                    LOG.error("Couldn't parse files", e);
                    return;
                }
            }

            final Map<String, String> requestBody = createRequestBody(instance.getCsarId(), instance.getTemplateId(),
                                                                      instance.getId(), inputs, correlationID);

            final ConsumerTemplate consumer =
                invokePlan("adapt", correlationID, true, instance.getId(), instance.getTemplateId(), requestBody,
                           instance.getCsarId(), planId, BPELNS);

            // Threaded reception of response
            this.executor.submit(() -> {

                Object response = null;

                try {
                    consumer.start();
                    final Exchange exchange = consumer.receive("direct:response" + correlationID);
                    response = exchange.getIn().getBody();
                    consumer.stop();
                }
                catch (final Exception e) {
                    MBEventHandler.LOG.error("Error occured: {}", e.getMessage(), e);
                    return;
                }

                MBEventHandler.LOG.debug("Received response for request with id {}.", correlationID);

                final Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("RESPONSE", response);
                responseMap.put("MESSAGEID", correlationID);
                responseMap.put("PLANLANGUAGE", BPELNS);
                final Event responseEvent = new Event("org_opentosca_situationadaptation/responses", responseMap);

                MBEventHandler.LOG.debug("Posting response as OSGi event.");
                this.eventAdmin.postEvent(responseEvent);
            });

        }
    }

    private WSDLEndpoint getAdaptationPlanEndpoint(final Collection<String> sourceNodeIDs,
                                                   final Collection<String> sourceRelationIDs,
                                                   final Collection<String> targetNodeIDs,
                                                   final Collection<String> targetRelationIDs) {
        final ICoreEndpointService endpointService = getEndpointService();
        for (final WSDLEndpoint endpoint : endpointService.getWSDLEndpoints()) {
            final Collection<String> sourceNodesMetadata =
                toStringCollection(endpoint.getMetadata().get("SOURCENODES"), ",");
            final Collection<String> sourceRelationsMetadata =
                toStringCollection(endpoint.getMetadata().get("SOURCERELATIONS"), ",");
            final Collection<String> targetNodesMetadata =
                toStringCollection(endpoint.getMetadata().get("TARGETNODES"), ",");
            final Collection<String> targetRelationsMetadata =
                toStringCollection(endpoint.getMetadata().get("TARGETRELATIONS"), ",");

            if (sourceNodeIDs.equals(sourceNodesMetadata) && sourceRelationIDs.equals(sourceRelationsMetadata)
                && targetNodeIDs.equals(targetNodesMetadata) && targetRelationIDs.equals(targetRelationsMetadata)) {
                return endpoint;
            }
        }

        return null;
    }

    private Map<String, String> toEndpointMetadata(final Collection<String> sourceNodeIDs,
                                                   final Collection<String> sourceRelationIDs,
                                                   final Collection<String> targetNodeIDs,
                                                   final Collection<String> targetRelationIDs) {
        final Map<String, String> result = new HashMap<>();

        result.put("SOURCENODES", toCSV(sourceNodeIDs));
        result.put("SOURCERELATIONS", toCSV(sourceRelationIDs));
        result.put("TARGETNODES", toCSV(targetNodeIDs));
        result.put("TARGETRELATIONS", toCSV(targetRelationIDs));

        return result;
    }

    private Collection<String> toStringCollection(final String data, final String separator) {
        final Collection<String> result = new ArrayList<>();

        if (data == null || data.isEmpty()) {
            return result;
        }

        final String[] split = data.split(separator);

        for (final String part : split) {
            if (part != null && !part.equals("") && !part.isEmpty()) {
                result.add(part);
            }
        }

        return result;
    }

    private String toCSV(final Collection<String> strings) {
        return strings.stream().collect(Collectors.joining(","));
    }

    private Set<PlanInstanceInput> toPlanInstanceInputs(final Map<String, String> inputs) {
        final Set<PlanInstanceInput> result = new HashSet<>();
        inputs.forEach((key, value) -> result.add(new PlanInstanceInput(key, value, "string")));
        return result;
    }

    private Map<String, String> createInput(final BPELPlan plan) {
        final Collection<String> inputs = plan.getWsdl().getInputMessageLocalNames();

        final Map<String, String> result = new HashMap<>();

        for (final String input : inputs) {
            result.put(input, null);
        }

        return result;
    }

    private BpelPlanEnginePlugin getBpelDeployPlugin() {
        final BundleContext ctx = Activator.ctx;
        try {
            final ServiceReference<?>[] refs =
                ctx.getAllServiceReferences(IPlanEnginePlanRefPluginService.class.getName(), null);
            if (refs != null) {
                for (final ServiceReference<?> ref : refs) {
                    final IPlanEnginePlanRefPluginService plugin =
                        (IPlanEnginePlanRefPluginService) ctx.getService(ref);
                    if (plugin instanceof BpelPlanEnginePlugin) {
                        return (BpelPlanEnginePlugin) plugin;
                    }
                }
            }
        }
        catch (final InvalidSyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        return null;
    }

    private ICoreEndpointService getEndpointService() {
        final BundleContext ctx = Activator.ctx;
        try {
            final ServiceReference<?>[] refs = ctx.getAllServiceReferences(ICoreEndpointService.class.getName(), null);
            if (refs != null) {
                for (final ServiceReference<?> ref : refs) {
                    final ICoreEndpointService plugin = (ICoreEndpointService) ctx.getService(ref);
                    return plugin;
                }
            }
        }
        catch (final InvalidSyntaxException e) {
            LOG.error("Couldn't fetch ICoreEndpointService instance", e);
        }

        return null;
    }

    private ServiceTemplateInstanceConfiguration getCurrentServiceTemplateInstanceConfiguration(final AbstractTopologyTemplate topology,
                                                                                                final ServiceTemplateInstance instance) {

        final Collection<AbstractNodeTemplate> currentlyRunningNodes = new HashSet<>();
        final Collection<AbstractRelationshipTemplate> currentlyRunningRelations = new HashSet<>();

        final Collection<NodeTemplateInstanceState> validNodeState = new HashSet<>();
        validNodeState.add(NodeTemplateInstanceState.STARTED);
        validNodeState.add(NodeTemplateInstanceState.CREATED);
        validNodeState.add(NodeTemplateInstanceState.CONFIGURED);

        final Collection<RelationshipTemplateInstanceState> validRelationState = new HashSet<>();
        validRelationState.add(RelationshipTemplateInstanceState.CREATED);

        for (final AbstractNodeTemplate node : topology.getNodeTemplates()) {
            for (final NodeTemplateInstance inst : instance.getNodeTemplateInstances()) {
                if (inst.getTemplateId().getLocalPart().equals(node.getId())
                    && validNodeState.contains(inst.getState())) {
                    currentlyRunningNodes.add(node);
                }
            }
        }

        for (final AbstractRelationshipTemplate relation : topology.getRelationshipTemplates()) {
            for (final RelationshipTemplateInstance inst : instance.getRelationshipTemplateInstances()) {
                if (inst.getTemplateId().getLocalPart().equals(relation.getId())
                    && validRelationState.contains(inst.getState())) {
                    currentlyRunningRelations.add(relation);
                }
            }
        }

        return new ServiceTemplateInstanceConfiguration(currentlyRunningNodes, currentlyRunningRelations);
    }

    private ServiceTemplateInstanceConfiguration getValidServiceTemplateInstanceConfiguration(final AbstractTopologyTemplate topology,
                                                                                              final Map<String, Collection<Long>> nodeIds2situationIds) {


        final Collection<AbstractNodeTemplate> validNodes = new ArrayList<>();
        final Collection<AbstractRelationshipTemplate> validRelations = new ArrayList<>();

        for (final AbstractNodeTemplate nodeTemplate : topology.getNodeTemplates()) {
            final Collection<AbstractPolicy> policies = getPolicies(Types.situationPolicyType, nodeTemplate);
            if (policies.isEmpty()) {
                validNodes.add(nodeTemplate);
            } else if (isValidUnderSituations(nodeTemplate, nodeIds2situationIds)) {
                validNodes.add(nodeTemplate);
            }
        }

        // check if node set is deployable
        final Collection<AbstractNodeTemplate> deployableAndValidNodeSet =
            getDeployableSubgraph(validNodes, nodeIds2situationIds);
        for (final AbstractRelationshipTemplate relations : topology.getRelationshipTemplates()) {
            if (deployableAndValidNodeSet.contains(relations.getSource())
                & deployableAndValidNodeSet.contains(relations.getTarget())) {
                validRelations.add(relations);
            }
        }

        return new ServiceTemplateInstanceConfiguration(deployableAndValidNodeSet, validRelations);
    }

    private Collection<AbstractNodeTemplate> getDeployableSubgraph(final Collection<AbstractNodeTemplate> nodeTemplates,
                                                                   final Map<String, Collection<Long>> nodeIds2situationIds) {
        final Set<AbstractNodeTemplate> validDeploymentSubgraph = new HashSet<>(nodeTemplates);
        final Collection<AbstractNodeTemplate> toRemove = new HashSet<>();

        for (final AbstractNodeTemplate nodeTemplate : nodeTemplates) {
            final Collection<AbstractRelationshipTemplate> hostingRelations =
                getOutgoingHostedOnRelations(nodeTemplate);
            if (!hostingRelations.isEmpty()) {
                // if we have hostedOn relations check if it is valid under the situation and is in the set
                boolean foundValidHost = false;
                for (final AbstractRelationshipTemplate relationshipTemplate : hostingRelations) {
                    final AbstractNodeTemplate hostingNode = relationshipTemplate.getTarget();
                    if (isValidUnderSituations(hostingNode, nodeIds2situationIds)
                        && nodeTemplates.contains(hostingNode)) {
                        foundValidHost = true;
                        break;
                    }
                }
                if (!foundValidHost) {
                    toRemove.add(nodeTemplate);
                }
            }
        }

        if (toRemove.isEmpty()) {
            return validDeploymentSubgraph;
        } else {
            validDeploymentSubgraph.removeAll(toRemove);
            return getDeployableSubgraph(validDeploymentSubgraph, nodeIds2situationIds);
        }
    }

    private boolean isValidUnderSituations(final AbstractNodeTemplate nodeTemplate,
                                           final Map<String, Collection<Long>> nodeIds2situationIds) {
        // check if the situation of the policy is active
        Collection<Long> situationIds = null;

        if ((situationIds = nodeIds2situationIds.get(nodeTemplate.getId())) == null) {
            return true;
        }


        boolean isValid = true;
        for (final Long sitId : situationIds) {
            isValid &= isSituationActive(sitId);
        }
        return isValid;
    }

    private Collection<AbstractRelationshipTemplate> getOutgoingHostedOnRelations(final AbstractNodeTemplate nodeTemplate) {
        return nodeTemplate.getOutgoingRelations().stream().filter(x -> x.getType().equals(Types.hostedOnRelationType))
                           .collect(Collectors.toList());
    }



    private Collection<AbstractPolicy> getPolicies(final QName policyType, final AbstractNodeTemplate nodeTemplate) {
        return nodeTemplate.getPolicies().stream().filter(x -> x.getType().getId().equals(policyType))
                           .collect(Collectors.toList());
    }

    private static class ServiceTemplateInstanceConfiguration {
        Collection<AbstractNodeTemplate> nodeTemplates;
        Collection<AbstractRelationshipTemplate> relationshipTemplates;

        public ServiceTemplateInstanceConfiguration(final Collection<AbstractNodeTemplate> nodes,
                                                    final Collection<AbstractRelationshipTemplate> relations) {
            this.nodeTemplates = nodes;
            this.relationshipTemplates = relations;
        }
    }

    private boolean isSituationActive(final Long situationId) {
        return getSituationRepository().find(situationId).get().isActive();
    }

    private SituationRepository getSituationRepository() {
        return new SituationRepository();
    }

    public Map<String, String> createRequestBody(final CSARID csarID, final QName serviceTemplateID,
                                                 final Long serviceTemplateInstanceId,
                                                 final Map<String, String> inputParameter, final String correlationID) {

        final Map<String, String> map = new HashMap<>();


        LOG.trace("Processing a list of {} parameters", inputParameter.size());
        for (final String para : inputParameter.keySet()) {
            final String value = inputParameter.get(para);
            LOG.trace("Put in the parameter {} with value \"{}\".", para, value);
            if (para.equalsIgnoreCase("CorrelationID")) {
                LOG.debug("Found Correlation Element! Put in CorrelationID \"" + correlationID + "\".");
                map.put(para, correlationID);
            } else if (para.equalsIgnoreCase("csarID")) {
                LOG.debug("Found csarID Element! Put in csarID \"" + csarID + "\".");
                map.put(para, csarID.toString());
            } else if (para.equalsIgnoreCase("serviceTemplateID")) {
                LOG.debug("Found serviceTemplateID Element! Put in serviceTemplateID \"" + serviceTemplateID + "\".");
                map.put(para, serviceTemplateID.toString());
            } else if (para.equalsIgnoreCase("OpenTOSCAContainerAPIServiceInstanceURL")
                & serviceTemplateInstanceId != null) {
                    final String serviceTemplateInstanceUrl =
                        createServiceInstanceURI(csarID, serviceTemplateID, serviceTemplateInstanceId);
                    map.put(para, String.valueOf(serviceTemplateInstanceUrl));
                } else if (para.equalsIgnoreCase("containerApiAddress")) {
                    LOG.debug("Found containerApiAddress Element! Put in containerApiAddress \""
                        + Settings.CONTAINER_API_LEGACY + "\".");
                    map.put(para, Settings.CONTAINER_API_LEGACY);
                } else if (para.equalsIgnoreCase("instanceDataAPIUrl")) {
                    LOG.debug("Found instanceDataAPIUrl Element! Put in instanceDataAPIUrl \""
                        + Settings.CONTAINER_INSTANCEDATA_API + "\".");
                    String str = Settings.CONTAINER_INSTANCEDATA_API;
                    str = str.replace("{csarid}", csarID.getFileName());
                    try {
                        str = str.replace("{servicetemplateid}",
                                          URLEncoder.encode(URLEncoder.encode(serviceTemplateID.toString(), "UTF-8"),
                                                            "UTF-8"));
                    }
                    catch (final UnsupportedEncodingException e) {
                        LOG.error("Couldn't encode Service Template URL", e);
                    }
                    LOG.debug("instance api: {}", str);
                    map.put(para, str);
                } else if (para.equalsIgnoreCase("csarEntrypoint")) {
                    LOG.debug("Found csarEntrypoint Element! Put in instanceDataAPIUrl \""
                        + Settings.CONTAINER_API_LEGACY + "/" + csarID + "\".");
                    map.put(para, Settings.CONTAINER_API_LEGACY + "/CSARs/" + csarID);
                } else {
                    map.put(para, value);
                }
        }

        return map;
    }

    private String createServiceInstanceURI(final CSARID csarId, final QName serviceTemplate,
                                            final Long serviceTemplateInstanceId) {
        String url = Settings.CONTAINER_INSTANCEDATA_API + "/" + serviceTemplateInstanceId;
        url = url.replace("{csarid}", csarId.getFileName());
        url = url.replace("{servicetemplateid}",
                          UriComponent.encode(UriComponent.encode(serviceTemplate.toString(),
                                                                  UriComponent.Type.PATH_SEGMENT),
                                              UriComponent.Type.PATH_SEGMENT));

        return url;
    }


    public void bindEventAdmin(final EventAdmin eventAdmin) {
        this.eventAdmin = eventAdmin;
    }

    public void unbindEventAdmin(final EventAdmin eventAdmin) {
        try {
            this.executor.shutdown();
            this.executor.awaitTermination(5, TimeUnit.SECONDS);
        }
        catch (final InterruptedException e) {
            // Ignore
        }
        finally {
            this.executor.shutdownNow();
        }
        this.eventAdmin = null;
    }
}
