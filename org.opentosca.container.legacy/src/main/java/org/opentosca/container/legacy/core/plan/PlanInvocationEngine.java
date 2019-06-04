package org.opentosca.container.legacy.core.plan;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.client.Client;
import javax.xml.namespace.QName;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.winery.model.tosca.*;
import org.opentosca.container.core.common.NotFoundException;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.engine.ToscaEngine;
import org.opentosca.container.core.engine.management.IManagementBus;
import org.opentosca.container.core.engine.xml.IXMLSerializerService;
import org.opentosca.container.core.impl.plan.PlanLogHandler;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.model.instance.ServiceTemplateInstanceID;
import org.opentosca.container.core.next.model.PlanInstance;
import org.opentosca.container.core.next.model.PlanInstanceInput;
import org.opentosca.container.core.next.model.PlanInstanceOutput;
import org.opentosca.container.core.next.model.PlanInstanceState;
import org.opentosca.container.core.next.model.PlanLanguage;
import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.container.core.next.repository.PlanInstanceRepository;
import org.opentosca.container.core.next.repository.ServiceTemplateInstanceRepository;
import org.opentosca.container.core.service.CsarStorageService;
import org.opentosca.container.core.service.ICSARInstanceManagementService;
import org.opentosca.container.core.service.IPlanInvocationEngine;
import org.opentosca.container.core.service.IPlanLogHandler;
import org.opentosca.container.core.tosca.extension.PlanInvocationEvent;
import org.opentosca.container.core.tosca.extension.PlanTypes;
import org.opentosca.container.core.tosca.extension.TParameterDTO;
import org.opentosca.container.core.tosca.extension.TPlanDTO;
import org.opentosca.container.legacy.core.engine.IToscaReferenceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

/**
 * The Implementation of the Engine. Also deals with OSGI events for communication with the mock-up
 * Servicebus.
 * <p>
 * Copyright 2013 Christian Endres
 *
 * @author endrescn@fachschaft.informatik.uni-stuttgart.de
 */
@Service
@NonNullByDefault
public class PlanInvocationEngine implements IPlanInvocationEngine {

  private static final ServiceTemplateInstanceRepository stiRepo = new ServiceTemplateInstanceRepository();
  private static final Logger LOG = LoggerFactory.getLogger(PlanInvocationEngine.class);

  private static final String nsBPEL = "http://docs.oasis-open.org/wsbpel/2.0/process/executable";
  private static final String nsBPMN = "http://www.omg.org/spec/BPMN";

  // this is one of the dependecies keeping PlanInvocationEngine in legacy.
  @Deprecated
  private final IToscaReferenceMapper toscaReferenceMapper;
  private final CorrelationHandler correlationHandler;
  private final ICSARInstanceManagementService csarInstanceManagement;
  private final IManagementBus managementBus;
  private final IXMLSerializerService xmlSerializerService;
  private final CsarStorageService csarStorage;
  private final RulesChecker rulesChecker;

  @Inject
  public PlanInvocationEngine(IToscaReferenceMapper toscaReferenceMapper,
                              CorrelationHandler correlationHandler,
                              ICSARInstanceManagementService csarInstanceManagement,
                              IManagementBus managementBus,
                              IXMLSerializerService xmlSerializerService,
                              CsarStorageService csarStorage,
                              RulesChecker rulesChecker) {
    this.toscaReferenceMapper = toscaReferenceMapper;
    this.correlationHandler = correlationHandler;
    this.csarInstanceManagement = csarInstanceManagement;
    this.managementBus = managementBus;
    this.xmlSerializerService = xmlSerializerService;
    this.csarStorage = csarStorage;
    this.rulesChecker = rulesChecker;
  }

  @Override
  public String invokePlan(final CsarId csarID, final QName serviceTemplateId, long serviceTemplateInstanceID,
                           final TPlanDTO givenPlan) throws UnsupportedEncodingException {

    final Csar csar = csarStorage.findById(csarID);
    // refill information that might not be sent
    LOG.info("Invoke the Plan \"" + givenPlan.getId() + "\" of type \"" + givenPlan.getPlanType() + "\" of CSAR \"" + csarID + "\".");
    final TPlan storedPlan;
    final TServiceTemplate serviceTemplate;
    try {
      storedPlan = retrieveStoredPlan(csar, givenPlan);
      serviceTemplate = ToscaEngine.resolveServiceTemplate(csar, serviceTemplateId);
    } catch (NotFoundException e) {
      // WTF, this warrants a 500: ServiceTemplate was deleted between invocation and reaching this point?!
      // FIXME it's easier to pass the TServiceTemplate in the first place
      return null;
    }
    final PlanInvocationEvent planEvent = buildPlanInvocationEvent(csarID, serviceTemplate, csar, storedPlan);

    processInputParameters(givenPlan, storedPlan, planEvent);
    processOutputParameters(storedPlan, planEvent);

    String correlationID;
    // build plan, thus, faked instance id that has to be replaced later
    /**
     * TODO this is a hack! problem is, that the instance id of a service template is created
     * by @see
     * {@link org.opentosca.containerapi.resources.csar.servicetemplate.instances.ServiceTemplateInstancesResource#createServiceInstance()}
     * , thus, we do not know it yet and have to correct it later with
     *
     * @see {@link org.opentosca.planinvocationengine.service.impl.correlation.CorrelationHandler#correlateBuildPlanCorrToServiceTemplateInstanceId()}
     */
    if (serviceTemplateInstanceID == -1) {
      serviceTemplateInstanceID = 1000 + (int) (Math.random() * (Integer.MAX_VALUE - 1000));
      // get new correlationID
      correlationID = correlationHandler.getNewCorrelationID(csarID, serviceTemplateId, (int) serviceTemplateInstanceID, planEvent, true);
    } else {
      // get new correlationID
      correlationID = correlationHandler.getNewCorrelationID(csarID, serviceTemplateId, (int) serviceTemplateInstanceID, planEvent, false);
    }

    // plan is of type build, thus create an instance and put the
    // CSARInstanceID into the plan
    ServiceTemplateInstanceID instanceID;
    if (PlanTypes.isPlanTypeURI(planEvent.getPlanType()).equals(PlanTypes.BUILD)) {
      instanceID = csarInstanceManagement.createNewInstance(csarID, serviceTemplateId);
      planEvent.setCSARInstanceID(instanceID.getInstanceID());
    } else {
      instanceID = new ServiceTemplateInstanceID(csarID, serviceTemplateId, (int) serviceTemplateInstanceID);
    }
    csarInstanceManagement.correlateCSARInstanceWithPlanInstance(instanceID, correlationID);
    csarInstanceManagement.setCorrelationAsActive(csarID, correlationID);
    csarInstanceManagement.correlateCorrelationIdToPlan(correlationID, planEvent);


    LOG.debug("complete the list of parameters {}", givenPlan.getId());

    final Map<String, String> message = createRequest(csar, serviceTemplateId, planEvent.getInputParameter(), correlationID);

    if (null == message) {
      LOG.error("Failed to construct parameter list for plan {} of type {}", givenPlan.getId(),
        givenPlan.getPlanLanguage());
      return null;
    }

    final StringBuilder builder = new StringBuilder("Invoking the plan with the following parameters:\n");
    for (final String key : message.keySet()) {
      builder.append("     " + key + " : " + message.get(key) + "\n");
    }
    LOG.trace(builder.toString());
    csarInstanceManagement.storePublicPlanToHistory(correlationID, planEvent);

    // Create a new instance
    final PlanInstanceRepository repository = new PlanInstanceRepository();
    final PlanInstance pi = new PlanInstance();
    pi.setCorrelationId(correlationID);
    LOG.debug("Plan Language: {}", storedPlan.getPlanLanguage());
    pi.setLanguage(PlanLanguage.fromString(storedPlan.getPlanLanguage()));
    LOG.debug("Plan Type: {}", storedPlan.getPlanType());
    pi.setType(PlanType.fromString(storedPlan.getPlanType()));
    pi.setState(PlanInstanceState.RUNNING);
    pi.setTemplateId(givenPlan.getId());

    stiRepo.find(serviceTemplateInstanceID)
      .ifPresent(serviceTemplateInstance -> pi.setServiceTemplateInstance(serviceTemplateInstance));

    planEvent.getInputParameter().stream().forEach(p -> {
      new PlanInstanceInput(p.getName(), p.getValue(), p.getType()).setPlanInstance(pi);
    });
    repository.add(pi);

    // send the message to the service bus
    final Map<String, Object> eventValues = new Hashtable<>();
    eventValues.put("CSARID", csarID);
    eventValues.put("PLANID", planEvent.getPlanID());
    eventValues.put("PLANLANGUAGE", planEvent.getPlanLanguage());
    eventValues.put("OPERATIONNAME", planEvent.getOperationName());
    eventValues.put("BODY", message);
    eventValues.put("MESSAGEID", correlationID);
    if (null == toscaReferenceMapper.isPlanAsynchronous(csarID.toOldCsarId(), givenPlan.getId())) {
      LOG.warn(" There are no informations stored about whether the plan is synchronous or asynchronous. Thus, we believe it is asynchronous.");
      eventValues.put("ASYNC", true);
    } else if (toscaReferenceMapper.isPlanAsynchronous(csarID.toOldCsarId(), givenPlan.getId())) {
      eventValues.put("ASYNC", true);
    } else {
      eventValues.put("ASYNC", false);
    }

    LOG.debug("Send event with parameters for invocation with the CorrelationID \"{}\".", correlationID);
    managementBus.invokePlan(eventValues, this::handleResponse);

    return correlationID;
  }

  private void processInputParameters(TPlanDTO givenPlan, TPlan storedPlan, PlanInvocationEvent planEvent) {
    for (final TParameter storedParam : storedPlan.getInputParameters().getInputParameter()) {
      LOG.trace("Processing input parameter {}", storedParam.getName());

      boolean found = false;
      for (final TParameterDTO assigned : givenPlan.getInputParameters().getInputParameter()) {
        if (!assigned.getName().equals(storedParam.getName())) {
          continue;
        }
        // we trust the server, the client could've passed any old nonsense
        assigned.setRequired(storedParam.getRequired());
        assigned.setType(storedParam.getType());
        found = true;
        planEvent.getInputParameter().add(assigned);
        normalizeValue(assigned);
        LOG.trace("Found input param {} with value {}", assigned.getName(), assigned.getValue());
      }
      if (!found) {
        LOG.trace("Did not find input param {} in assignmments, inserting empty stub.", storedParam.getName());
        final TParameterDTO newParam = new TParameterDTO();
        newParam.setName(storedParam.getName());
        newParam.setType(storedParam.getType());
        newParam.setRequired(storedParam.getRequired());
        planEvent.getInputParameter().add(newParam);
      }
    }
  }

  private void normalizeValue(TParameterDTO assigned) {
    String value = assigned.getValue();
    if (value == null) {
      assigned.setValue("");
      return;
    }
    // Probably copied from: https://stackoverflow.com/a/3777853/7065173
    // TODO: Check if can use Apache Common's normalize method to implement this platfrom independently
    value = value.replace("\\r", "\r")
      .replace("\r", "")
      .replace("\\n", "\n");
    assigned.setValue(value);
  }

  private PlanInvocationEvent buildPlanInvocationEvent(CsarId csarID, TServiceTemplate serviceTemplate, Csar csar, TPlan storedPlan) {
    final PlanInvocationEvent planEvent = new PlanInvocationEvent();

    TExportedOperation operation = ToscaEngine.findReferencingOperationWithin(serviceTemplate, storedPlan);
    TExportedInterface exportedInterface = ToscaEngine.findReferencingInterfaceWithin(serviceTemplate, operation);

    planEvent.setCSARID(csarID.csarName());
    planEvent.setInterfaceName(exportedInterface.getName());
    planEvent.setOperationName(operation.getName());
    planEvent.setPlanLanguage(storedPlan.getPlanLanguage());
    planEvent.setPlanType(storedPlan.getPlanType());
    // TODO consider move from QName to String?
    planEvent.setPlanID(new QName(storedPlan.getId()));
    planEvent.setIsActive(true);
    planEvent.setHasFailed(false);
    return planEvent;
  }

  @Override
  public String createCorrelationId(final CsarId csarID, final QName serviceTemplateId,
                                    long serviceTemplateInstanceID, final TPlanDTO givenPlan) {

    final Csar csar = csarStorage.findById(csarID);
    final TPlan storedPlan;
    final TServiceTemplate serviceTemplate;
    try {
      serviceTemplate = ToscaEngine.resolveServiceTemplate(csar, serviceTemplateId);
      storedPlan = retrieveStoredPlan(csar, givenPlan);
    } catch (NotFoundException e) {
      e.printStackTrace();
      return null;
    }
    final PlanInvocationEvent planEvent = buildPlanInvocationEvent(csarID, serviceTemplate, csar, storedPlan);

    String correlationID;
    if (serviceTemplateInstanceID == -1) {
      serviceTemplateInstanceID = 1000 + (int) (Math.random() * (Integer.MAX_VALUE - 1000));
      // get new correlationID
      correlationID =
        correlationHandler.getNewCorrelationID(csarID, serviceTemplateId,
          (int) serviceTemplateInstanceID, planEvent, true);
    } else {
      // get new correlationID

      correlationID =
        correlationHandler.getNewCorrelationID(csarID, serviceTemplateId,
          (int) serviceTemplateInstanceID, planEvent, false);
    }

    // plan is of type build, thus create an instance and put the
    // CSARInstanceID into the plan
    ServiceTemplateInstanceID instanceID;
    if (PlanTypes.isPlanTypeURI(planEvent.getPlanType()).equals(PlanTypes.BUILD)) {
      instanceID = csarInstanceManagement.createNewInstance(csarID, serviceTemplateId);
      planEvent.setCSARInstanceID(instanceID.getInstanceID());
    } else {
      instanceID = new ServiceTemplateInstanceID(csarID, serviceTemplateId, (int) serviceTemplateInstanceID);
    }
    csarInstanceManagement.correlateCSARInstanceWithPlanInstance(instanceID, correlationID);
    csarInstanceManagement.setCorrelationAsActive(csarID, correlationID);
    csarInstanceManagement.correlateCorrelationIdToPlan(correlationID, planEvent);

    return correlationID;
  }


  /**
   * {@inheritDoc}
   *
   * @throws UnsupportedEncodingException
   */
  @Override
  public void invokePlan(final CsarId csarID, final QName serviceTemplateId, final long serviceTemplateInstanceID,
                         final TPlanDTO givenPlan, final String correlationID) throws UnsupportedEncodingException {

    final Csar csar = csarStorage.findById(csarID);
    final TPlan storedPlan;
    final TServiceTemplate serviceTemplate;
    try {
      storedPlan = retrieveStoredPlan(csar, givenPlan);
      serviceTemplate = ToscaEngine.resolveServiceTemplate(csar, serviceTemplateId);
    } catch (NotFoundException e) {
      LOG.error("Could not resolve Plan or ServiceTemplate for plan invocation", e);
      return;
    }
    if (rulesChecker.areRulesContained(csar)) {
      if (!rulesChecker.check(csar, serviceTemplate, givenPlan.getInputParameters())) {
        LOG.debug("Deployment Rules are not fulfilled. Aborting the provisioning.");
        return;
      }
      LOG.debug("Deployment Rules are fulfilled. Continuing the provisioning.");
    }

    LOG.info("Invoke the Plan \"" + givenPlan.getId() + "\" of type \"" + givenPlan.getPlanType() + "\" of CSAR \"" + csarID + "\".");
    final PlanInvocationEvent planEvent = buildPlanInvocationEvent(csarID, serviceTemplate, csar, storedPlan);

    processInputParameters(givenPlan, storedPlan, planEvent);
    processOutputParameters(storedPlan, planEvent);

    LOG.debug("complete the list of parameters {}", givenPlan.getId());

    final Map<String, String> message = createRequest(csar, serviceTemplateId, planEvent.getInputParameter(), correlationID);

    if (null == message) {
      LOG.error("Failed to construct parameter list for plan {} of type {}", givenPlan.getId(), givenPlan.getPlanLanguage());
      return;
    }

    final StringBuilder builder = new StringBuilder("Invoking the plan with the following parameters:\n");
    for (final String key : message.keySet()) {
      builder.append("     ").append(key).append(" : ").append(message.get(key)).append("\n");
    }
    LOG.trace(builder.toString());

    csarInstanceManagement.storePublicPlanToHistory(correlationID, planEvent);

    // Create a new instance
    final PlanInstanceRepository repository = new PlanInstanceRepository();
    final PlanInstance pi = new PlanInstance();
    pi.setCorrelationId(correlationID);
    LOG.debug("Plan Language: {}", storedPlan.getPlanLanguage());
    pi.setLanguage(PlanLanguage.fromString(storedPlan.getPlanLanguage()));
    LOG.debug("Plan Type: {}", storedPlan.getPlanType());
    pi.setType(PlanType.fromString(storedPlan.getPlanType()));
    pi.setState(PlanInstanceState.RUNNING);
    pi.setTemplateId(givenPlan.getId());

    stiRepo.find(serviceTemplateInstanceID)
      .ifPresent(pi::setServiceTemplateInstance);

    planEvent.getInputParameter().stream().forEach(p -> {
      new PlanInstanceInput(p.getName(), p.getValue(), p.getType()).setPlanInstance(pi);
    });
    repository.add(pi);

    // send the message to the service bus
    final Map<String, Object> eventValues = new Hashtable<>();
    eventValues.put("CSARID", csar.id());
    eventValues.put("PLANID", planEvent.getPlanID());
    eventValues.put("PLANLANGUAGE", planEvent.getPlanLanguage());
    eventValues.put("OPERATIONNAME", planEvent.getOperationName());
    eventValues.put("BODY", message);
    eventValues.put("MESSAGEID", correlationID);
    if (null == toscaReferenceMapper.isPlanAsynchronous(csarID.toOldCsarId(), givenPlan.getId())) {
      LOG.warn(" There are no informations stored about whether the plan is synchronous or asynchronous. Thus, we believe it is asynchronous.");
      eventValues.put("ASYNC", true);
    } else if (toscaReferenceMapper.isPlanAsynchronous(csarID.toOldCsarId(), givenPlan.getId())) {
      eventValues.put("ASYNC", true);
    } else {
      eventValues.put("ASYNC", false);
    }
    LOG.debug("Send event with parameters for invocation with the CorrelationID \"{}\".", correlationID);
    managementBus.invokePlan(eventValues, this::handleResponse);
  }

  private void processOutputParameters(TPlan storedPlan, PlanInvocationEvent planEvent) {
    for (final TParameter temp : storedPlan.getOutputParameters().getOutputParameter()) {
      final TParameterDTO param = new TParameterDTO();

      param.setName(temp.getName());
      param.setRequired(temp.getRequired());
      param.setType(temp.getType());

      planEvent.getOutputParameter().add(param);
    }
  }

  private TPlan retrieveStoredPlan(final Csar csar, final TPlanDTO givenPlan) throws NotFoundException {
    return csar.plans().stream()
      .filter(tPlan -> tPlan.getId().equals(givenPlan.getId().getLocalPart()))
      .findFirst()
      .orElseThrow(() -> new NotFoundException("Plan with id " + givenPlan.getId() + " was not found in Csar " + csar.id()));
  }

  @Override
  public void correctCorrelationToServiceTemplateInstanceIdMapping(final CsarId csarID, final QName serviceTemplateId,
                                                                   final String corrId,
                                                                   final int correctSTInstanceId) {
    correlationHandler.correlateBuildPlanCorrToServiceTemplateInstanceId(csarID, serviceTemplateId, corrId, correctSTInstanceId);
  }

  private Map<String, String> createRequest(final Csar csar, final QName serviceTemplateID,final List<TParameterDTO> inputParameters,
                                           final String correlationID) throws UnsupportedEncodingException {
    final Map<String, String> map = new HashMap<>();

    final List<Document> docs = csar.serviceTemplates().stream()
      .flatMap(st -> st.getTopologyTemplate().getNodeTemplates().stream())
      .map(ToscaEngine::getNodeTemplateProperties)
      .filter(Objects::nonNull)
      .collect(Collectors.toList());

    LOG.trace("Processing a list of {} parameters", inputParameters.size());
    for (final TParameterDTO param : inputParameters) {
      LOG.trace("Put in the parameter {} with value \"{}\".", param.getName(), param.getValue());
      if (param.getName().equalsIgnoreCase("CorrelationID")) {
        LOG.debug("Found Correlation Element! Put in CorrelationID \"" + correlationID + "\".");
        map.put(param.getName(), correlationID);
      } else if (param.getName().equalsIgnoreCase("csarID")) {
        LOG.debug("Found csarID Element! Put in csarID \"" + csar.id() + "\".");
        map.put(param.getName(), csar.id().csarName());
      } else if (param.getName().equalsIgnoreCase("serviceTemplateID")) {
        LOG.debug("Found serviceTemplateID Element! Put in serviceTemplateID \"" + serviceTemplateID + "\".");
        map.put(param.getName(), serviceTemplateID.toString());
      } else if (param.getName().equalsIgnoreCase("instanceDataAPIUrl")) {
        LOG.debug("Found instanceDataAPIUrl Element! Put in instanceDataAPIUrl \"" + Settings.CONTAINER_INSTANCEDATA_API + "\".");
        String str = Settings.CONTAINER_INSTANCEDATA_API;
        str = str.replace("{csarid}", csar.id().csarName());
        str = str.replace("{servicetemplateid}", URLEncoder.encode(URLEncoder.encode(serviceTemplateID.toString(), "UTF-8"), "UTF-8"));
        LOG.debug("instance api: {}", str);
        map.put(param.getName(), str);
      } else {
        if (param.getName() != null && null != param.getValue() && !param.getValue().equals("")) {
          LOG.trace("Found element [{}]! Set value to \"{}\".", param.getName(), param.getValue());
          map.put(param.getName(), param.getValue());
          continue;
        }
        LOG.debug("The parameter [{}] has an empty value, thus search in the properties.", param.getName());
        String value = "";
        for (final Document doc : docs) {
          final NodeList nodes = doc.getElementsByTagNameNS("*", param.getName());
          if (nodes.getLength() > 0) {
            value = nodes.item(0).getTextContent();
            LOG.trace("Found value {}", value);
            break;
          }
        }
        if (value.equals("")) {
          LOG.warn("No value found for parameter [{}]", param.getName());
        }
        map.put(param.getName(), value);
      }
    }

    return map;
  }

  private void handleResponse(Map<String, Object> eventValues) {
    final String correlationID = (String) eventValues.get("MESSAGEID");
    PlanInvocationEvent event = csarInstanceManagement.getPlanFromHistory(correlationID);
    final String planLanguage = event.getPlanLanguage();
    LOG.trace("The correlation ID is {} and plan language is {}", correlationID, planLanguage);

    // TODO the concrete handling and parsing shall be in the plugin?!
    if (planLanguage.startsWith(nsBPEL)) {

      @SuppressWarnings("unchecked") final Map<String, String> map = (Map<String, String>) eventValues.get("RESPONSE");
      LOG.debug("Received an event with a SOAP response");

      final CsarId csarID = new CsarId(event.getCSARID());
      // parse the body
      // correlationID = responseParser.parseSOAPBody(csarID,
      // event.getPlanID(), correlationID, map);

      // if plan is not null
      if (null == correlationID) {
        LOG.error("The parsing of the response failed!");
        return;
      }

      LOG.trace("Print the plan output:");
      for (final String key : map.keySet()) {
        LOG.trace("   " + key + ": " + map.get(key));
      }

      for (final TParameterDTO param : event.getOutputParameter()) {
        LOG.debug("For variable \"{}\" the output value is \"{}\"", param.getName(), map.get(param.getName()));
        param.setValue(map.get(param.getName()));
      }

      csarInstanceManagement.getOutputForCorrelation(correlationID).putAll(map);
      csarInstanceManagement.setCorrelationAsFinished(csarID, correlationID);

      // Update state
      final PlanInstanceRepository repository = new PlanInstanceRepository();
      final PlanInstance pi = repository.findByCorrelationId(correlationID);
      if (pi != null) {
        event.getInputParameter().stream().map(p -> new PlanInstanceInput(p.getName(), p.getValue(), p.getType()))
          .forEach(pii -> pii.setPlanInstance(pi));
        event.getOutputParameter().stream().map(p -> new PlanInstanceOutput(p.getName(), p.getValue(), p.getType()))
          .forEach(pii -> pii.setPlanInstance(pi));
        pi.setState(PlanInstanceState.FINISHED);
        repository.update(pi);
      } else {
        LOG.error("Plan instance for correlation id '{}' not found", correlationID);
      }

      // save
      final ServiceTemplateInstanceID instanceID = csarInstanceManagement.getInstanceForCorrelation(correlationID);
      LOG.debug("The instanceID is: " + instanceID);
      csarInstanceManagement.storeCorrelationForAnInstance(instanceID.getCsarId(), instanceID, correlationID);

      if (event.isHasFailed()) {
        LOG.info("The process instance was not successful.");
      } else {
        if (PlanTypes.isPlanTypeURI(event.getPlanType()).equals(PlanTypes.TERMINATION)) {
          final boolean deletion = csarInstanceManagement.deleteInstance(instanceID.getCsarId(), instanceID);
          LOG.debug("Delete of instance returns: " + deletion);
        }
      }
    } else if (planLanguage.startsWith(nsBPMN)) {
      final Object response = eventValues.get("RESPONSE");
      LOG.debug("Received an event with a REST response: {}", response);
      event = csarInstanceManagement.getPlanFromHistory(correlationID);
      LOG.trace("Found invocation in plan history for instance: {}", event.getCSARInstanceID());
      final CsarId csarID = new CsarId(event.getCSARID());

      // parse the body
      final String planInstanceID = this.parseRESTResponse(response);
      LOG.debug("Parsing REST response, found instance ID {} for Correlation {}", planInstanceID, correlationID);

      // if plan is not null
      if (null == planInstanceID || planInstanceID.equals("")) {
        LOG.error("The parsing of the response failed!");
        return;
      }

      /**
       * TODO remove jersey and search for the history with the bus(?)!!!
       */

      // searching for history
      final String pathBase = "http://localhost:8080/engine-rest/";
      final String pathProcessInstance = "process-instance?processInstanceIds=";
      final String pathHistoryVariables = "history/variable-instance";

      LOG.debug("Instance ID: " + planInstanceID);
      final Client client = ClientBuilder.newClient();
      client.register(HttpAuthenticationFeature.basic("demo", "demo"));

      boolean ended = false;
      String path = pathBase + pathProcessInstance + planInstanceID;

      WebTarget webResource = client.target(path);
      Response camundaResponse;
      while (!ended) {
        camundaResponse = webResource.request().get();
        final String resp = camundaResponse.readEntity(String.class);
        LOG.debug("Active process instance response: " + resp);

        try {
          Thread.sleep(1000);
        } catch (final InterruptedException e) {
          e.printStackTrace();
        }

        if (resp.equals("[]")) {
          LOG.debug("The plan instance {} is not active any more, thus, the output can be retrieved.",
            planInstanceID);
          ended = true;
        }

        if (resp.contains("Process instance with id " + planInstanceID + " does not exist")) {
          ended = true;
        }
      }

      final ICSARInstanceManagementService instMngr = csarInstanceManagement;
      final Map<String, String> map = instMngr.getOutputForCorrelation(correlationID);

      for (final TParameterDTO param : event.getOutputParameter()) {
        // History of process instance TODO get here the output
        // parameters
        path = pathBase + pathHistoryVariables;
        // + "?processInstanceId=" + planInstanceID;

        webResource = client.target(path);
        webResource = webResource.queryParam("processInstanceId", planInstanceID);
        webResource = webResource.queryParam("activityInstanceIdIn", planInstanceID);
        // webResource = webResource.queryParam("variableName",
        // "ApplicationURL");
        webResource = webResource.queryParam("variableName", param.getName());
        camundaResponse = webResource.request().get();
        final String responseStr = camundaResponse.readEntity(String.class);
        LOG.trace("Query:\n{}", webResource.getUri());
        LOG.trace("History has for variable \"{}\" the value \"{}\"", param.getName(), responseStr);

        final JsonParser parser = new JsonParser();
        String value = null;
        try {
          final JsonObject json =
            (JsonObject) parser.parse(responseStr.substring(1, responseStr.length() - 1));
          value = json.get("value").getAsString();
        } catch (final ClassCastException e) {
          LOG.trace("value is null");
          value = "";
        }
        LOG.debug("For variable \"{}\" the output value is \"{}\"", param.getName(), value);
        param.setValue(value);
        map.put(param.getName(), value);
      }

      csarInstanceManagement.getOutputForCorrelation(correlationID).putAll(map);
      csarInstanceManagement.setCorrelationAsFinished(csarID, correlationID);

      // Update state
      final PlanInstanceRepository repository = new PlanInstanceRepository();
      final PlanInstance pi = repository.findByCorrelationId(correlationID);
      if (pi != null) {
        pi.setState(PlanInstanceState.FINISHED);
        repository.update(pi);
      } else {
        LOG.error("Plan instance for correlation id '{}' not found", correlationID);
      }

      // save
      final ServiceTemplateInstanceID instanceID = csarInstanceManagement.getInstanceForCorrelation(correlationID);
      LOG.debug("The instanceID is: " + instanceID);
      csarInstanceManagement.storeCorrelationForAnInstance(instanceID.getCsarId(), instanceID, correlationID);

      if (event.isHasFailed()) {
        LOG.info("The process instance was not successful.");
      } else {
        if (PlanTypes.isPlanTypeURI(event.getPlanType()).equals(PlanTypes.TERMINATION)) {
          final boolean deletion = csarInstanceManagement.deleteInstance(instanceID.getCsarId(), instanceID);
          LOG.debug("Delete of instance returns: " + deletion);
        }
      }
    } else {
      LOG.error("The returned response cannot be matched to a supported plan language!");
      return;
    }

    correlationHandler.removeCorrelation(correlationID);
  }

  private String parseRESTResponse(final Object responseBody) {
    final String resp = (String) responseBody;
    String instanceID = resp.substring(resp.indexOf("href\":\"") + 7, resp.length());
    instanceID = instanceID.substring(instanceID.lastIndexOf("/") + 1, instanceID.indexOf("\""));
    return instanceID;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<String> getActiveCorrelationsOfInstance(final ServiceTemplateInstanceID csarInstanceID) {
    return correlationHandler.getActiveCorrelationsOfInstance(csarInstanceID);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TPlanDTO getActivePublicPlanOfInstance(final ServiceTemplateInstanceID csarInstanceID,
                                                final String correlationID) {
    return correlationHandler.getPlanDTOForCorrelation(csarInstanceID, correlationID);
  }

  @Override
  public IPlanLogHandler getPlanLogHandler() {
    return PlanLogHandler.instance;
  }
}
