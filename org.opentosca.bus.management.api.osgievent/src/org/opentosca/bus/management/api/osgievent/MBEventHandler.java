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
import org.opentosca.container.core.next.model.PlanInstance;
import org.opentosca.container.core.next.model.PlanInstanceInput;
import org.opentosca.container.core.next.model.PlanInstanceState;
import org.opentosca.container.core.next.model.PlanLanguage;
import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.container.core.next.model.RelationshipTemplateInstance;
import org.opentosca.container.core.next.model.RelationshipTemplateInstanceState;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.repository.PlanInstanceRepository;
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

    private ConsumerTemplate invokePlan(String operationName, String messageID, boolean async, String serviceInstanceID,
                                        Object message, CSARID csarID, QName planID, String planLanguage) {
        MBEventHandler.LOG.debug("Plan invocation is asynchronous: {}", async);

        // create the headers for the Exchange which is send to the Management Bus
        final Map<String, Object> headers = new HashMap<>();
        headers.put(MBHeader.CSARID.toString(), csarID);
        headers.put(MBHeader.PLANID_QNAME.toString(), planID);
        headers.put(MBHeader.OPERATIONNAME_STRING.toString(), operationName);
        headers.put(MBHeader.PLANCORRELATIONID_STRING.toString(), messageID);
        headers.put("OPERATION", OsgiEventOperations.INVOKE_PLAN.getHeaderValue());
        headers.put("PlanLanguage", planLanguage);


        if (message instanceof HashMap) {
            MBEventHandler.LOG.debug("Invocation body is of type HashMap.");

            if (serviceInstanceID != null) {
                URI serviceInstanceURI;
                try {
                    serviceInstanceURI = new URI(serviceInstanceID);
                    headers.put(MBHeader.SERVICEINSTANCEID_URI.toString(), serviceInstanceURI);
                }
                catch (final URISyntaxException e) {
                    MBEventHandler.LOG.warn("Could not generate service instance URL: {}", e.getMessage(), e);
                }
            } else {
                MBEventHandler.LOG.warn("Service instance ID is null.");
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
                final Map<String, String> inputParameter = (Map<String, String>) event.getProperty("INPUTS");

                Map<String, String> message = new HashMap<String, String>();

                

                try {
                    message = this.createRequestBody(csarID, serviceTemplateID, serviceInstanceID, inputParameter, messageID);
                }
                catch (UnsupportedEncodingException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }



                ConsumerTemplate consumer = this.invokePlan(operationName, messageID, async, serviceInstanceID.toString(), message,
                                                            csarID, planID, planLanguage);

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
            ServiceTemplateInstance instance = (ServiceTemplateInstance) event.getProperty("SERVICEINSTANCE");

            Map<String, Collection<Long>> nodeIds2situationIds =
                (Map<String, Collection<Long>>) event.getProperty("NODE2SITUATIONS");

            Importer importer = new Importer();
            Exporter exporter = new Exporter();

            AbstractTopologyTemplate topology =
                importer.getMainDefinitions(instance.getCsarId()).getServiceTemplates().get(0).getTopologyTemplate();

            ServiceTemplateInstanceConfiguration currentConfig =
                this.getCurrentServiceTemplateInstanceConfiguration(topology, instance);
            ServiceTemplateInstanceConfiguration targetConfig =
                this.getValidServiceTemplateInstanceConfiguration(topology, nodeIds2situationIds);



            Collection<String> currentConfigNodeIds =
                currentConfig.nodeTemplates.stream().map(x -> x.getId()).collect(Collectors.toList());
            Collection<String> currentConfigRelationIds =
                currentConfig.relationshipTemplates.stream().map(x -> x.getId()).collect(Collectors.toList());

            Collection<String> targetConfigNodeIds =
                targetConfig.nodeTemplates.stream().map(x -> x.getId()).collect(Collectors.toList());
            Collection<String> targetConfigRelationIds =
                targetConfig.relationshipTemplates.stream().map(x -> x.getId()).collect(Collectors.toList());

            if (currentConfigNodeIds.equals(targetConfigNodeIds)
                & currentConfigRelationIds.equals(targetConfigRelationIds)) {
                MBEventHandler.LOG.debug("Current configuration is equal to target configuration, no adaptation is needed");
                return;
            }



            WSDLEndpoint endpoint = this.getAdaptationPlanEndpoint(currentConfigNodeIds, currentConfigRelationIds,
                                                                   targetConfigNodeIds, targetConfigRelationIds);



            String correlationID = String.valueOf(System.currentTimeMillis());
            QName planId = null;
            PlanType planType = null;
            Map<String, String> inputs = null;

            if (endpoint != null) {
                planId = endpoint.getPlanId();
                planType = PlanType.fromString(endpoint.getMetadata().get("PLANTYPE"));

                inputs = new HashMap<String, String>();

                for (String input : this.toStringCollection(endpoint.getMetadata().get("INPUTS"), ",")) {
                    inputs.put(input, null);
                }

            } else {
                try {
                    BPELPlan adaptationPlan =
                        (BPELPlan) importer.generateAdaptationPlan(instance.getCsarId(), instance.getTemplateId(),
                                                                   currentConfigNodeIds, currentConfigRelationIds,
                                                                   targetConfigNodeIds, targetConfigRelationIds);

                    planType = PlanType.fromString(adaptationPlan.getType().getString());
                    inputs = this.createInput(adaptationPlan);
                    Path tempFile = Files.createTempFile(adaptationPlan.getId(), ".zip");
                    exporter.exportToPlanFile(tempFile.toUri(), adaptationPlan);
                    BpelPlanEnginePlugin deployPlugin = this.getBpelDeployPlugin();

                    Map<String, String> endpointMetadata =
                        this.toEndpointMetadata(currentConfigNodeIds, currentConfigRelationIds, targetConfigNodeIds,
                                                targetConfigRelationIds);

                    endpointMetadata.put("PLANTYPE", planType.toString());
                    endpointMetadata.put("INPUTS", this.toCSV(inputs.keySet()));

                    deployPlugin.deployPlanFile(tempFile, instance.getCsarId(), endpointMetadata);

                    planId = new QName(tempFile.getFileName().toString());


                }
                catch (SystemException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                catch (JAXBException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            Map<String, String> requestBody = null;
            try {
                requestBody = this.createRequestBody(instance.getCsarId(), instance.getTemplateId(), instance.getId(),
                                                     inputs, correlationID);
            }
            catch (UnsupportedEncodingException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }


            // Create a new instance
            final PlanInstanceRepository repository = new PlanInstanceRepository();
            final PlanInstance pi = new PlanInstance();
            pi.setCorrelationId(correlationID);

            pi.setLanguage(PlanLanguage.fromString(BPELNS));

            pi.setType(planType);
            pi.setState(PlanInstanceState.RUNNING);
            pi.setTemplateId(planId);
            pi.setServiceTemplateInstance(instance);
            pi.setInputs(this.toPlanInstanceInputs(inputs));

            repository.add(pi);


            ConsumerTemplate consumer = this.invokePlan("adapt", correlationID, true, String.valueOf(instance.getId()),
                                                        requestBody, instance.getCsarId(), planId, BPELNS);

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


            // importer.generateAdaptationPlan(instance.getCsarId().getFileName(), instance.getTemplateId(),
            // sourceNodeTemplateIds, sourceRelationshipTemplateIds, targetNodeTemplateId,
            // targetRelationshipTemplateId)
        }
    }

    private WSDLEndpoint getAdaptationPlanEndpoint(Collection<String> sourceNodeIDs,
                                                   Collection<String> sourceRelationIDs,
                                                   Collection<String> targetNodeIDs,
                                                   Collection<String> targetRelationIDs) {
        ICoreEndpointService endpointService = this.getEndpointService();
        for (WSDLEndpoint endpoint : endpointService.getWSDLEndpoints()) {
            Collection<String> sourceNodesMetadata = toStringCollection(endpoint.getMetadata().get("SOURCENODES"), ",");
            Collection<String> sourceRelationsMetadata =
                toStringCollection(endpoint.getMetadata().get("SOURCERELATIONS"), ",");
            Collection<String> targetNodesMetadata = toStringCollection(endpoint.getMetadata().get("TARGETNODES"), ",");
            Collection<String> targetRelationsMetadata =
                toStringCollection(endpoint.getMetadata().get("TARGETRELATIONS"), ",");

            if (sourceNodeIDs.equals(sourceNodesMetadata) && sourceRelationIDs.equals(sourceRelationsMetadata)
                && targetNodeIDs.equals(targetNodesMetadata) && targetRelationIDs.equals(targetRelationsMetadata)) {
                return endpoint;
            }
        }

        return null;
    }

    private Map<String, String> toEndpointMetadata(Collection<String> sourceNodeIDs,
                                                   Collection<String> sourceRelationIDs,
                                                   Collection<String> targetNodeIDs,
                                                   Collection<String> targetRelationIDs) {
        Map<String, String> result = new HashMap<String, String>();

        result.put("SOURCENODES", this.toCSV(sourceNodeIDs));
        result.put("SOURCERELATIONS", this.toCSV(sourceRelationIDs));
        result.put("TARGETNODES", this.toCSV(targetNodeIDs));
        result.put("TARGETRELATIONS", this.toCSV(targetRelationIDs));

        return result;
    }

    private Collection<String> toStringCollection(String data, String separator) {
        Collection<String> result = new ArrayList<String>();

        if(data == null || data.isEmpty()) {
            return result;
        }
        
        String[] split = data.split(separator);

        for (String part : split) {
            if (part != null && !part.equals("") && !part.isEmpty()) {
                result.add(part);
            }
        }

        return result;
    }

    private String toCSV(Collection<String> strings) {
        StringBuilder strB = new StringBuilder();

        strings.forEach(s -> strB.append(s).append(","));

        return strB.toString();
    }

    private Set<PlanInstanceInput> toPlanInstanceInputs(Map<String, String> inputs) {
        Set<PlanInstanceInput> result = new HashSet<PlanInstanceInput>();

        for (String key : inputs.keySet()) {
            String val = inputs.get(key);
            result.add(new PlanInstanceInput(key, val, "string"));
        }

        return result;
    }

    private Map<String, String> createInput(BPELPlan plan) {
        Collection<String> inputs = plan.getWsdl().getInputMessageLocalNames();

        Map<String, String> result = new HashMap<String, String>();

        for (String input : inputs) {
            result.put(input, null);
        }

        return result;
    }

    private BpelPlanEnginePlugin getBpelDeployPlugin() {
        BundleContext ctx = Activator.ctx;
        try {
            ServiceReference<?>[] refs =
                ctx.getAllServiceReferences(IPlanEnginePlanRefPluginService.class.getName(), null);
            if (refs != null) {
                for (ServiceReference<?> ref : refs) {
                    IPlanEnginePlanRefPluginService plugin = (IPlanEnginePlanRefPluginService) ctx.getService(ref);
                    if (plugin instanceof BpelPlanEnginePlugin) {
                        return (BpelPlanEnginePlugin) plugin;
                    }
                }
            }
        }
        catch (InvalidSyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        return null;
    }

    private ICoreEndpointService getEndpointService() {
        BundleContext ctx = Activator.ctx;
        try {
            ServiceReference<?>[] refs = ctx.getAllServiceReferences(ICoreEndpointService.class.getName(), null);
            if (refs != null) {
                for (ServiceReference<?> ref : refs) {
                    ICoreEndpointService plugin = (ICoreEndpointService) ctx.getService(ref);
                    return plugin;
                }
            }
        }
        catch (InvalidSyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        return null;
    }

    private ServiceTemplateInstanceConfiguration getCurrentServiceTemplateInstanceConfiguration(AbstractTopologyTemplate topology,
                                                                                                ServiceTemplateInstance instance) {

        Collection<AbstractNodeTemplate> currentlyRunningNodes = new HashSet<AbstractNodeTemplate>();
        Collection<AbstractRelationshipTemplate> currentlyRunningRelations =
            new HashSet<AbstractRelationshipTemplate>();

        Collection<NodeTemplateInstanceState> validNodeState = new HashSet<NodeTemplateInstanceState>();
        validNodeState.add(NodeTemplateInstanceState.STARTED);
        validNodeState.add(NodeTemplateInstanceState.CREATED);
        validNodeState.add(NodeTemplateInstanceState.CONFIGURED);

        Collection<RelationshipTemplateInstanceState> validRelationState =
            new HashSet<RelationshipTemplateInstanceState>();
        validRelationState.add(RelationshipTemplateInstanceState.CREATED);

        for (AbstractNodeTemplate node : topology.getNodeTemplates()) {
            for (NodeTemplateInstance inst : instance.getNodeTemplateInstances()) {
                if (inst.getTemplateId().getLocalPart().equals(node.getId())
                    && validNodeState.contains(inst.getState())) {
                    currentlyRunningNodes.add(node);
                }
            }
        }

        for (AbstractRelationshipTemplate relation : topology.getRelationshipTemplates()) {
            for (RelationshipTemplateInstance inst : instance.getRelationshipTemplateInstances()) {
                if (inst.getTemplateId().getLocalPart().equals(relation.getId())
                    && validRelationState.contains(inst.getState())) {
                    currentlyRunningRelations.add(relation);
                }
            }
        }

        return new ServiceTemplateInstanceConfiguration(currentlyRunningNodes, currentlyRunningRelations);
    }

    private ServiceTemplateInstanceConfiguration getValidServiceTemplateInstanceConfiguration(AbstractTopologyTemplate topology,
                                                                                              Map<String, Collection<Long>> nodeIds2situationIds) {
        QName situationPolicyType =
            new QName("http://opentosca.org/servicetemplates/policytypes", "SituationPolicy_w1-wip1");

        Collection<AbstractNodeTemplate> validNodes = new ArrayList<AbstractNodeTemplate>();
        Collection<AbstractRelationshipTemplate> validRelations = new ArrayList<AbstractRelationshipTemplate>();

        for (AbstractNodeTemplate nodeTemplate : topology.getNodeTemplates()) {
            Collection<AbstractPolicy> policies = this.getPolicies(situationPolicyType, nodeTemplate);
            if (policies.isEmpty()) {
                validNodes.add(nodeTemplate);
            } else {
                if (this.isValidUnderSituations(nodeTemplate, nodeIds2situationIds)) {
                    validNodes.add(nodeTemplate);
                }
            }
        }

        // check if node set is deployable
        Collection<AbstractNodeTemplate> deployableAndValidNodeSet =
            this.getDeployableSubgraph(validNodes, nodeIds2situationIds);
        for (AbstractRelationshipTemplate relations : topology.getRelationshipTemplates()) {
            if (deployableAndValidNodeSet.contains(relations.getSource())
                & deployableAndValidNodeSet.contains(relations.getTarget())) {
                validRelations.add(relations);
            }
        }

        return new ServiceTemplateInstanceConfiguration(deployableAndValidNodeSet, validRelations);
    }

    private Collection<AbstractNodeTemplate> getDeployableSubgraph(Collection<AbstractNodeTemplate> nodeTemplates,
                                                                   Map<String, Collection<Long>> nodeIds2situationIds) {
        Set<AbstractNodeTemplate> validDeploymentSubgraph = new HashSet<AbstractNodeTemplate>(nodeTemplates);
        Collection<AbstractNodeTemplate> toRemove = new HashSet<AbstractNodeTemplate>();

        for (AbstractNodeTemplate nodeTemplate : nodeTemplates) {
            Collection<AbstractRelationshipTemplate> hostingRelations = this.getOutgoingHostedOnRelations(nodeTemplate);
            if (!hostingRelations.isEmpty()) {
                // if we have hostedOn relations check if it is valid under the situation and is in the set
                boolean foundValidHost = false;
                for (AbstractRelationshipTemplate relationshipTemplate : hostingRelations) {
                    AbstractNodeTemplate hostingNode = relationshipTemplate.getTarget();
                    if (this.isValidUnderSituations(hostingNode, nodeIds2situationIds)) {
                        if (nodeTemplates.contains(hostingNode)) {
                            foundValidHost = true;
                        }
                    }
                    if (foundValidHost) {
                        // if atleast one hosting relationship is fulfilled we are happy
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

    private boolean isValidUnderSituations(AbstractNodeTemplate nodeTemplate,
                                           Map<String, Collection<Long>> nodeIds2situationIds) {



        // check if the situation of the policy is active
        Collection<Long> situationIds = null;

        if ((situationIds = nodeIds2situationIds.get(nodeTemplate.getId())) == null) {
            return true;
        }
        boolean isValid = true;

        for (Long sitId : situationIds) {
            isValid &= this.isSituationActive(sitId);
        }

        return isValid;
    }

    private Collection<AbstractRelationshipTemplate> getOutgoingHostedOnRelations(AbstractNodeTemplate nodeTemplate) {
        return nodeTemplate.getOutgoingRelations().stream().filter(x -> x.getType().equals(Types.hostedOnRelationType))
                           .collect(Collectors.toList());
    }



    private Collection<AbstractPolicy> getPolicies(QName policyType, AbstractNodeTemplate nodeTemplate) {
        Collection<AbstractPolicy> policies = new ArrayList<AbstractPolicy>();
        for (AbstractPolicy policy : nodeTemplate.getPolicies()) {
            if (policy.getType().getId().equals(policyType)) {
                policies.add(policy);
            }
        }
        return policies;
    }

    private class ServiceTemplateInstanceConfiguration {
        Collection<AbstractNodeTemplate> nodeTemplates;
        Collection<AbstractRelationshipTemplate> relationshipTemplates;

        public ServiceTemplateInstanceConfiguration(Collection<AbstractNodeTemplate> nodes,
                                                    Collection<AbstractRelationshipTemplate> relations) {
            this.nodeTemplates = nodes;
            this.relationshipTemplates = relations;
        }
    }

    private boolean isSituationActive(Long situationId) {
        return this.getSituationRepository().find(situationId).get().isActive();
    }

    private SituationRepository getSituationRepository() {
        return new SituationRepository();
    }

    public Map<String, String> createRequestBody(final CSARID csarID, final QName serviceTemplateID,
                                                 Long serviceTemplateInstanceId,
                                                 final Map<String, String> inputParameter,
                                                 final String correlationID) throws UnsupportedEncodingException {

        final Map<String, String> map = new HashMap<>();


        LOG.trace("Processing a list of {} parameters", inputParameter.size());
        for (final String para : inputParameter.keySet()) {
            String value = inputParameter.get(para);
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
                String serviceTemplateInstanceUrl =
                    this.createServiceInstanceURI(csarID, serviceTemplateID, serviceTemplateInstanceId);
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
                str = str.replace("{servicetemplateid}",
                                  URLEncoder.encode(URLEncoder.encode(serviceTemplateID.toString(), "UTF-8"), "UTF-8"));
                LOG.debug("instance api: {}", str);
                map.put(para, str);
            } else if (para.equalsIgnoreCase("csarEntrypoint")) {
                LOG.debug("Found csarEntrypoint Element! Put in instanceDataAPIUrl \"" + Settings.CONTAINER_API_LEGACY
                    + "/" + csarID + "\".");
                map.put(para, Settings.CONTAINER_API_LEGACY + "/CSARs/" + csarID);
            } else {
                map.put(para, value);
            }
        }

        return map;
    }

    private String createServiceInstanceURI(CSARID csarId, QName serviceTemplate, Long serviceTemplateInstanceId) {
        String url = Settings.CONTAINER_INSTANCEDATA_API + "/" + serviceTemplateInstanceId;
        url = url.replace("{csarid}", csarId.getFileName());
        url = url.replace("{servicetemplateid}",
                          UriComponent.encode(UriComponent.encode(serviceTemplate.toString(), UriComponent.Type.PATH_SEGMENT), UriComponent.Type.PATH_SEGMENT));

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
