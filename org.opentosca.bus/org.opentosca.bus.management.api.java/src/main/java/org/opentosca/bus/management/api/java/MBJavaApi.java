package org.opentosca.bus.management.api.java;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.apache.camel.*;
import org.apache.camel.impl.DefaultExchange;
import org.glassfish.jersey.uri.UriComponent;
import org.opentosca.bus.management.header.MBHeader;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.engine.management.IManagementBus;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.model.endpoint.wsdl.WSDLEndpoint;
import org.opentosca.container.core.next.model.*;
import org.opentosca.container.core.next.repository.PlanInstanceRepository;
import org.opentosca.container.core.next.repository.SituationRepository;
import org.opentosca.container.core.service.ICoreEndpointService;
import org.opentosca.container.core.tosca.convention.Types;
import org.opentosca.container.engine.plan.plugin.bpel.BpelPlanEnginePlugin;
import org.opentosca.planbuilder.export.Exporter;
import org.opentosca.planbuilder.importer.Importer;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractPolicy;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractTopologyTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * EventHandler of the Management Bus-OSGi-Event-API.<br>
 * <br>
 * <p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * <p>
 * Handles the events (receive and sent) of the Management Bus-OSGi-Event-API.
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 * @author Benjamin Weder - st100495@stud.uni-stuttgart.de
 * @author Kálmán Képes - kepes@iaas.uni-stuttgart.de
 */
@Component
@Singleton
public class MBJavaApi implements IManagementBus {

  public static final String PLAN_REQUEST_TOPIC = "org_opentosca_plans/requests";
  public static final String IA_INVOKE_TOPIC = "org_opentosca_ia/requests";

  private static final String BPMNNS = "http://www.omg.org/spec/BPMN/20100524/MODEL";
  private static final String BPELNS = "http://docs.oasis-open.org/wsbpel/2.0/process/executable";

  private static Logger LOG = LoggerFactory.getLogger(MBJavaApi.class);

  private final ExecutorService executor = Executors.newFixedThreadPool(5);

  private final CamelContext camelContext;
  private final Importer importer;
  private final Exporter exporter;
  private final ICoreEndpointService endpointService;
  private final BpelPlanEnginePlugin bpelDeployPlugin;

  @Inject
  public MBJavaApi(CamelContext camelContext, Importer importer, Exporter exporter,
                   ICoreEndpointService endpointService, BpelPlanEnginePlugin bpelPlanEnginePlugin) {
    this.camelContext = camelContext;
    this.importer = importer;
    this.exporter = exporter;
    this.endpointService = endpointService;
    this.bpelDeployPlugin = bpelPlanEnginePlugin;
    LOG.info("Starting direct Java invocation API for Management Bus");
  }

  private ConsumerTemplate invokePlan(String operationName, String messageID, boolean async, String serviceInstanceID,
                                      Object message, CsarId csarId, QName planID, String planLanguage) {
    LOG.debug("Plan invocation is asynchronous: {}", async);

    // create the headers for the Exchange which is send to the Management Bus
    final Map<String, Object> headers = new HashMap<>();
    headers.put(MBHeader.CSARID.toString(), csarId.csarName());
    headers.put(MBHeader.PLANID_QNAME.toString(), planID);
    headers.put(MBHeader.OPERATIONNAME_STRING.toString(), operationName);
    headers.put(MBHeader.PLANCORRELATIONID_STRING.toString(), messageID);
    // FIXME considering that this is constant, we bind to the bean directly.
    // Is this used downstream?
    headers.put("OPERATION", ExposedManagementBusOperations.INVOKE_PLAN.getHeaderValue());
    headers.put("PlanLanguage", planLanguage);

    if (message instanceof HashMap) {
      LOG.debug("Invocation body is of type HashMap.");

      if (serviceInstanceID != null) {
        URI serviceInstanceURI;
        try {
          serviceInstanceURI = new URI(serviceInstanceID);
          headers.put(MBHeader.SERVICEINSTANCEID_URI.toString(), serviceInstanceURI);
        } catch (final URISyntaxException e) {
          LOG.warn("Could not generate service instance URL: {}", e.getMessage(), e);
        }
      } else {
        LOG.warn("Service instance ID is null.");
      }
    } else {
      LOG.warn("Invocation body is of type: {}", message.getClass());
    }

    // templates to communicate with the Management Bus
    final ProducerTemplate template = camelContext.createProducerTemplate();
    final ConsumerTemplate consumer = camelContext.createConsumerTemplate();

    LOG.debug("Correlation id: {}", messageID);
    LOG.debug("Sending message {}", message);

    // forward request to the Management Bus
    final Exchange requestExchange = new DefaultExchange(camelContext);
    requestExchange.getIn().setBody(message);
    requestExchange.getIn().setHeaders(headers);
    template.asyncSend("direct:invoke", requestExchange);

    return consumer;
  }

  @Override
  public void invokePlan(Map<String, Object> eventValues, Consumer<Map<String, Object>> responseCallback) {
    final String planLanguage = (String) eventValues.get("PLANLANGUAGE");
    if (!planLanguage.startsWith(BPMNNS) && !planLanguage.startsWith(BPELNS)) {
      LOG.warn("Unsupported plan language: {}", planLanguage);
      return;
    }
    LOG.debug("Plan invocation with plan language: {}", planLanguage);

    final CsarId csarID = (CsarId) eventValues.get("CSARID");
    final QName planID = (QName) eventValues.get("PLANID");
    final String operationName = (String) eventValues.get("OPERATIONNAME");
    final String messageID = (String) eventValues.get("MESSAGEID");
    final boolean async = (boolean) eventValues.get("ASYNC");

    // Optional parameter if message is of type HashMap. Not needed for Document.
    final Long serviceInstanceID = (Long) eventValues.get("SERVICEINSTANCEID");
    final QName serviceTemplateID = (QName) eventValues.get("SERVICETEMPLATEID");

    // Should be of type Document or HashMap<String, String>. Maybe better handle them
    // with different topics.
    // final Object message = eventValues.get("BODY");
    final Map<String, String> inputParameter = (Map<String, String>) eventValues.get("INPUTS");

    Map<String, String> message = createRequestBody(csarID, serviceTemplateID, serviceInstanceID, inputParameter, messageID);

    ConsumerTemplate consumer = invokePlan(operationName, messageID, async, serviceInstanceID.toString(), message, csarID, planID, planLanguage);

    // set up response handling
    executor.submit(() -> {
      final Map<String, Object> responseMap = new HashMap<>();
      responseMap.put("MESSAGEID", messageID);
      responseMap.put("PLANLANGUAGE", planLanguage);
      final Object responseBody;
      try {
        consumer.start();
        responseBody = consumer.receive("direct:response" + messageID).getIn().getBody();
      } catch (Exception e) {
        LOG.warn("Receiving management bus internal plan invocation response failed with exception", e);
        responseMap.put("EXCEPTION", e);
        responseMap.put("RESPONSE", null);
        responseCallback.accept(responseMap);
        return;
      } finally {
        try {
          consumer.stop();
        } catch (Exception e) {
          // swallow
        }
      }
      LOG.debug("Passing direct response for request with id {} to callback.", messageID);
      responseMap.put("RESPONSE", responseBody);
      responseCallback.accept(responseMap);
    });
  }

  @Override
  public void invokeIA(Map<String, Object> eventValues, Consumer<Map<String, Object>> responseCallback) {
    // TODO when needed.
    // Adapt 'MBJavaApi - component.xml' to receive messages from this topic too...
  }

  @Override
  public void situationAdaption(Map<String, Object> eventValues, Consumer<Map<String, Object>> responseCallback) {
    LOG.debug("Received SituationAware Adapatioan Event");
    ServiceTemplateInstance instance = (ServiceTemplateInstance) eventValues.get("SERVICEINSTANCE");

    Map<String, Collection<Long>> nodeIds2situationIds = (Map<String, Collection<Long>>) eventValues.get("NODE2SITUATIONS");

    AbstractTopologyTemplate topology =
      importer.getMainDefinitions(instance.getCsarId().toOldCsarId()).getServiceTemplates().get(0).getTopologyTemplate();

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
      LOG.debug("Current configuration is equal to target configuration, no adaptation is needed");
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
          (BPELPlan) importer.generateAdaptationPlan(instance.getCsarId().toOldCsarId(), instance.getTemplateId(),
            currentConfigNodeIds, currentConfigRelationIds,
            targetConfigNodeIds, targetConfigRelationIds);

        planType = PlanType.fromString(adaptationPlan.getType().getString());
        inputs = this.createInput(adaptationPlan);
        Path tempFile = Files.createTempFile(adaptationPlan.getId(), ".zip");
        exporter.exportToPlanFile(tempFile.toUri(), adaptationPlan);

        Map<String, String> endpointMetadata =
          this.toEndpointMetadata(currentConfigNodeIds, currentConfigRelationIds, targetConfigNodeIds,
            targetConfigRelationIds);

        endpointMetadata.put("PLANTYPE", planType.toString());
        endpointMetadata.put("INPUTS", this.toCSV(inputs.keySet()));

        planId = new QName(tempFile.getFileName().toString());
        bpelDeployPlugin.deployPlanFile(tempFile, instance.getCsarId(), planId, endpointMetadata);
      }
      catch (SystemException e) {
        LOG.error("Internal error", e);
        return;
      }
      catch (IOException e) {
        LOG.error("Couldn't read files", e);
        return;
      }
      catch (JAXBException e) {
        LOG.error("Couldn't parse files", e);
        return;
      }
    }

    Map<String, String> requestBody = this.createRequestBody(instance.getCsarId(), instance.getTemplateId(),
      instance.getId(), inputs, correlationID);


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
        LOG.error("Error occured: {}", e.getMessage(), e);
        return;
      }

      LOG.debug("Received response for request with id {}.", correlationID);

      final Map<String, Object> responseMap = new HashMap<>();
      responseMap.put("RESPONSE", response);
      responseMap.put("MESSAGEID", correlationID);
      responseMap.put("PLANLANGUAGE", BPELNS);

      LOG.debug("Passing management bus response to callback");
      responseCallback.accept(responseMap);
    });

    // importer.generateAdaptationPlan(instance.getCsarId().getFileName(), instance.getTemplateId(),
    // sourceNodeTemplateIds, sourceRelationshipTemplateIds, targetNodeTemplateId,
    // targetRelationshipTemplateId)
  }

  private WSDLEndpoint getAdaptationPlanEndpoint(Collection<String> sourceNodeIDs,
                                                 Collection<String> sourceRelationIDs,
                                                 Collection<String> targetNodeIDs,
                                                 Collection<String> targetRelationIDs) {
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

    if (data == null || data.isEmpty()) {
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
    return strings.stream().collect(Collectors.joining(","));
  }

  private Set<PlanInstanceInput> toPlanInstanceInputs(Map<String, String> inputs) {
    Set<PlanInstanceInput> result = new HashSet<PlanInstanceInput>();
    inputs.forEach((key, value) -> result.add(new PlanInstanceInput(key, value, "string")));
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


    Collection<AbstractNodeTemplate> validNodes = new ArrayList<AbstractNodeTemplate>();
    Collection<AbstractRelationshipTemplate> validRelations = new ArrayList<AbstractRelationshipTemplate>();

    for (AbstractNodeTemplate nodeTemplate : topology.getNodeTemplates()) {
      Collection<AbstractPolicy> policies = this.getPolicies(Types.situationPolicyType, nodeTemplate);
      if (policies.isEmpty()) {
        validNodes.add(nodeTemplate);
      } else if (this.isValidUnderSituations(nodeTemplate, nodeIds2situationIds)) {
        validNodes.add(nodeTemplate);
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
          if (this.isValidUnderSituations(hostingNode, nodeIds2situationIds)
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
    return nodeTemplate.getPolicies().stream().filter(x -> x.getType().getId().equals(policyType)).collect(Collectors.toList());
  }

  private static class ServiceTemplateInstanceConfiguration {
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

  public Map<String, String> createRequestBody(final CsarId csarID, final QName serviceTemplateID,
                                               Long serviceTemplateInstanceId,
                                               final Map<String, String> inputParameter, final String correlationID) {

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
        LOG.debug("Found OpenTOSCAContainerAPIServiceInstanceURL element! Put in STinstanceUrl \"{}\"", String.valueOf(serviceTemplateInstanceUrl));
      } else if (para.equalsIgnoreCase("containerApiAddress")) {
        LOG.debug("Found containerApiAddress Element! Put in containerApiAddress \""
          + Settings.CONTAINER_API_LEGACY + "\".");
        map.put(para, Settings.CONTAINER_API_LEGACY);
      } else if (para.equalsIgnoreCase("instanceDataAPIUrl")) {
        LOG.debug("Found instanceDataAPIUrl Element! Put in instanceDataAPIUrl \""
          + Settings.CONTAINER_INSTANCEDATA_API + "\".");
        String str = Settings.CONTAINER_INSTANCEDATA_API;
        str = str.replace("{csarid}", csarID.csarName());
        try {
          str = str.replace("{servicetemplateid}",
            URLEncoder.encode(URLEncoder.encode(serviceTemplateID.toString(), "UTF-8"),
              "UTF-8"));
        }
        catch (UnsupportedEncodingException e) {
          LOG.error("Couldn't encode Service Template URL", e);
        }
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

  private String createServiceInstanceURI(CsarId csarId, QName serviceTemplate, Long serviceTemplateInstanceId) {
    String url = Settings.CONTAINER_INSTANCEDATA_API + "/" + serviceTemplateInstanceId;
    url = url.replace("{csarid}", csarId.csarName());
    url = url.replace("{servicetemplateid}",
      UriComponent.encode(UriComponent.encode(serviceTemplate.toString(),
        UriComponent.Type.PATH_SEGMENT),
        UriComponent.Type.PATH_SEGMENT));

    return url;
  }

}
