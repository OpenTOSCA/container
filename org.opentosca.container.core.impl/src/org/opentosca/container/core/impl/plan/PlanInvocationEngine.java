package org.opentosca.container.core.impl.plan;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.model.instance.ServiceTemplateInstanceID;
import org.opentosca.container.core.next.model.PlanInstance;
import org.opentosca.container.core.next.model.PlanInstanceInput;
import org.opentosca.container.core.next.model.PlanInstanceOutput;
import org.opentosca.container.core.next.model.PlanInstanceState;
import org.opentosca.container.core.next.model.PlanLanguage;
import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.container.core.next.repository.PlanInstanceRepository;
import org.opentosca.container.core.next.repository.ServiceTemplateInstanceRepository;
import org.opentosca.container.core.service.IPlanInvocationEngine;
import org.opentosca.container.core.tosca.extension.PlanInvocationEvent;
import org.opentosca.container.core.tosca.extension.PlanTypes;
import org.opentosca.container.core.tosca.extension.TParameterDTO;
import org.opentosca.container.core.tosca.extension.TPlanDTO;
import org.opentosca.container.core.tosca.model.TParameter;
import org.opentosca.container.core.tosca.model.TPlan;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * The Implementation of the Engine. Also deals with OSGI events for communication with the mock-up
 * Servicebus.
 *
 * Copyright 2013 Christian Endres
 *
 * @author endrescn@fachschaft.informatik.uni-stuttgart.de
 *
 */
public class PlanInvocationEngine implements IPlanInvocationEngine, EventHandler {

    private final Logger LOG = LoggerFactory.getLogger(PlanInvocationEngine.class);

    private static String nsBPEL = "http://docs.oasis-open.org/wsbpel/2.0/process/executable";
    private static String nsBPMN = "http://www.omg.org/spec/BPMN";

    private final String PROCESS_INSTANCE_PATH = "/process-instance?processInstanceIds=";
    private final String HISTORY_PATH = "/history/variable-instance";
    private final String EMPTY_JSON = "[]";

    private final static ServiceTemplateInstanceRepository stiRepo = new ServiceTemplateInstanceRepository();

    @Override
    public String invokePlan(final CSARID csarID, final QName serviceTemplateId, long serviceTemplateInstanceID,
                             final TPlanDTO givenPlan) throws UnsupportedEncodingException {

        // refill information that might not be sent
        final TPlan storedPlan = ServiceProxy.toscaReferenceMapper.getPlanForCSARIDAndPlanID(csarID, givenPlan.getId());

        if (null == storedPlan) {
            this.LOG.error("Plan " + givenPlan.getId() + " with name " + givenPlan.getName() + " is null!");
            return null;
        }
        if (!storedPlan.getId().equals(givenPlan.getId().getLocalPart())) {
            this.LOG.error("Plan " + givenPlan.getId() + " with internal ID " + givenPlan.getName()
                + " should copy of PublicPlan " + storedPlan.getId() + "!");
            return null;
        }

        givenPlan.setName(storedPlan.getName());
        givenPlan.setPlanLanguage(storedPlan.getPlanLanguage());
        givenPlan.setPlanType(storedPlan.getPlanType());
        givenPlan.setOutputParameters(storedPlan.getOutputParameters());

        final PlanInvocationEvent planEvent = new PlanInvocationEvent();

        this.LOG.info("Invoke the Plan \"" + givenPlan.getId() + "\" of type \"" + givenPlan.getPlanType()
            + "\" of CSAR \"" + csarID + "\".");

        // fill in the informations about this PublicPlan which is not provided
        // by the PublicPlan received by the REST API
        final Map<QName, TPlan> publicPlanMap =
            ServiceProxy.toscaReferenceMapper.getCSARIDToPlans(csarID)
                                             .get(PlanTypes.isPlanTypeURI(givenPlan.getPlanType()));

        if (null == publicPlanMap) {
            this.LOG.error("Wrong type! \"" + givenPlan.getPlanType() + "\"");
            return null;
        }

        planEvent.setCSARID(csarID.toString());
        // planEvent.setInputMessageID(ServiceProxy.toscaReferenceMapper.getPlanInputMessageID(csarID,
        // givenPlan.getId()));
        planEvent.setInterfaceName(ServiceProxy.toscaReferenceMapper.getIntferaceNameOfPlan(csarID, givenPlan.getId()));
        planEvent.setOperationName(ServiceProxy.toscaReferenceMapper.getOperationNameOfPlan(csarID, givenPlan.getId()));
        // planEvent.setOutputMessageID(storedPlan.getOutputMessageID());
        planEvent.setPlanLanguage(storedPlan.getPlanLanguage());
        planEvent.setPlanType(storedPlan.getPlanType());
        planEvent.setPlanID(givenPlan.getId());
        planEvent.setIsActive(true);
        planEvent.setHasFailed(false);
        for (final TParameter temp : storedPlan.getInputParameters().getInputParameter()) {
            boolean found = false;

            this.LOG.trace("Processing input parameter {}", temp.getName());

            final List<TParameterDTO> params = givenPlan.getInputParameters().getInputParameter();
            for (final TParameterDTO param : params) {

                if (param.getName().equals(temp.getName())) {
                    final TParameterDTO dto = param;
                    // param.setRequired(temp.getRequired());
                    // param.setType(temp.getType());
                    found = true;
                    planEvent.getInputParameter().add(dto);
                    String value = dto.getValue();
                    if (value == null) {
                        value = "";
                    }
                    // Probably copied from:
                    // https://stackoverflow.com/a/3777853/7065173
                    // TODO: Check if can use Apache Common's normalize method
                    // to implement this platfrom independently
                    value = value.replace("\\r", "\r");
                    value = value.replace("\r", "");
                    value = value.replace("\\n", "\n");
                    dto.setValue(value);
                    this.LOG.trace("Found input param {} with value {}", param.getName(), param.getValue());
                }
            }
            if (!found) {
                this.LOG.trace("Did not found input param {}, thus, insert empty one.", temp.getName());
                final TParameterDTO newParam = new TParameterDTO();
                newParam.setName(temp.getName());
                newParam.setType(temp.getType());
                newParam.setRequired(temp.getRequired());
                planEvent.getInputParameter().add(newParam);
            }
        }
        for (final TParameter temp : storedPlan.getOutputParameters().getOutputParameter()) {
            final TParameterDTO param = new TParameterDTO();

            param.setName(temp.getName());
            param.setRequired(temp.getRequired());
            param.setType(temp.getType());

            planEvent.getOutputParameter().add(param);
        }

        String correlationID;
        // build plan, thus, faked instance id that has to be replaced later
        /**
         * TODO this is a hack! problem is, that the instance id of a service template is created by @see
         * {@link org.opentosca.containerapi.resources.csar.servicetemplate.instances.ServiceTemplateInstancesResource#createServiceInstance()}
         * , thus, we do not know it yet and have to correct it later with
         *
         * @see {@link org.opentosca.planinvocationengine.service.impl.correlation.CorrelationHandler#correlateBuildPlanCorrToServiceTemplateInstanceId()}
         */
        if (serviceTemplateInstanceID == -1) {
            serviceTemplateInstanceID = 1000 + (int) (Math.random() * (Integer.MAX_VALUE - 1000));
            // get new correlationID
            correlationID =
                ServiceProxy.correlationHandler.getNewCorrelationID(csarID, serviceTemplateId,
                                                                    (int) serviceTemplateInstanceID, planEvent, true);
        } else {
            // get new correlationID

            correlationID =
                ServiceProxy.correlationHandler.getNewCorrelationID(csarID, serviceTemplateId,
                                                                    (int) serviceTemplateInstanceID, planEvent, false);
        }

        // plan is of type build, thus create an instance and put the
        // CSARInstanceID into the plan
        ServiceTemplateInstanceID instanceID;
        if (PlanTypes.isPlanTypeURI(planEvent.getPlanType()).equals(PlanTypes.BUILD)) {
            instanceID = ServiceProxy.csarInstanceManagement.createNewInstance(csarID, serviceTemplateId);
            planEvent.setCSARInstanceID(instanceID.getInstanceID());
        } else {
            instanceID = new ServiceTemplateInstanceID(csarID, serviceTemplateId, (int) serviceTemplateInstanceID);
        }
        ServiceProxy.csarInstanceManagement.correlateCSARInstanceWithPlanInstance(instanceID, correlationID);
        ServiceProxy.csarInstanceManagement.setCorrelationAsActive(csarID, correlationID);
        ServiceProxy.csarInstanceManagement.correlateCorrelationIdToPlan(correlationID, planEvent);

        final Map<String, Object> eventValues = new Hashtable<>();
        eventValues.put("CSARID", csarID);
        eventValues.put("PLANID", planEvent.getPlanID());
        eventValues.put("PLANLANGUAGE", planEvent.getPlanLanguage());
        eventValues.put("OPERATIONNAME", planEvent.getOperationName());

        this.LOG.debug("complete the list of parameters {}", givenPlan.getId());

        final Map<String, String> message = createRequest(csarID, serviceTemplateId, serviceTemplateInstanceID, null,
                                                          // ServiceProxy.toscaReferenceMapper.getPlanInputMessageID(csarID,
                                                          // givenPlan.getId()),
                                                          planEvent.getInputParameter(), correlationID);

        if (null == message) {
            this.LOG.error("Failed to construct parameter list for plan {} of type {}", givenPlan.getId(),
                           givenPlan.getPlanLanguage());
            return null;
        }

        final StringBuilder builder = new StringBuilder("Invoking the plan with the following parameters:\n");
        for (final String key : message.keySet()) {
            builder.append("     " + key + " : " + message.get(key) + "\n");
        }
        this.LOG.trace(builder.toString());

        eventValues.put("BODY", message);
        eventValues.put("INPUTS", message);

        if (null == ServiceProxy.toscaReferenceMapper.isPlanAsynchronous(csarID, givenPlan.getId())) {
            this.LOG.warn(" There are no informations stored about whether the plan is synchronous or asynchronous. Thus, we believe it is asynchronous.");
            eventValues.put("ASYNC", true);
        } else if (ServiceProxy.toscaReferenceMapper.isPlanAsynchronous(csarID, givenPlan.getId())) {
            eventValues.put("ASYNC", true);
        } else {
            eventValues.put("ASYNC", false);
        }

        eventValues.put("MESSAGEID", correlationID);
        eventValues.put("SERVICEINSTANCEID", serviceTemplateInstanceID);
        eventValues.put("SERVICETEMPLATEID", serviceTemplateId);


        ServiceProxy.csarInstanceManagement.storePublicPlanToHistory(correlationID, planEvent);

        // Create a new instance
        final PlanInstanceRepository repository = new PlanInstanceRepository();
        final PlanInstance pi = new PlanInstance();
        pi.setCorrelationId(correlationID);
        this.LOG.debug("Plan Language: {}", storedPlan.getPlanLanguage());
        pi.setLanguage(PlanLanguage.fromString(storedPlan.getPlanLanguage()));
        this.LOG.debug("Plan Type: {}", storedPlan.getPlanType());
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
        final Event event = new Event("org_opentosca_plans/requests", eventValues);
        this.LOG.debug("Send event with parameters for invocation with the CorrelationID \"{}\".", correlationID);
        ServiceProxy.eventAdmin.sendEvent(event);

        return correlationID;
    }

    @Override
    public String createCorrelationId(final CSARID csarID, final QName serviceTemplateId,
                                      long serviceTemplateInstanceID, final TPlanDTO givenPlan) {

        // refill information that might not be sent
        final TPlan storedPlan = ServiceProxy.toscaReferenceMapper.getPlanForCSARIDAndPlanID(csarID, givenPlan.getId());
        final PlanInvocationEvent planEvent = new PlanInvocationEvent();
        String correlationID;

        planEvent.setCSARID(csarID.toString());
        // planEvent.setInputMessageID(ServiceProxy.toscaReferenceMapper.getPlanInputMessageID(csarID,
        // givenPlan.getId()));
        planEvent.setInterfaceName(ServiceProxy.toscaReferenceMapper.getIntferaceNameOfPlan(csarID, givenPlan.getId()));
        planEvent.setOperationName(ServiceProxy.toscaReferenceMapper.getOperationNameOfPlan(csarID, givenPlan.getId()));
        // planEvent.setOutputMessageID(storedPlan.getOutputMessageID());
        planEvent.setPlanLanguage(storedPlan.getPlanLanguage());
        planEvent.setPlanType(storedPlan.getPlanType());
        planEvent.setPlanID(givenPlan.getId());
        planEvent.setIsActive(true);
        planEvent.setHasFailed(false);

        if (serviceTemplateInstanceID == -1) {
            serviceTemplateInstanceID = 1000 + (int) (Math.random() * (Integer.MAX_VALUE - 1000));
            // get new correlationID
            correlationID =
                ServiceProxy.correlationHandler.getNewCorrelationID(csarID, serviceTemplateId,
                                                                    (int) serviceTemplateInstanceID, planEvent, true);
        } else {
            // get new correlationID

            correlationID =
                ServiceProxy.correlationHandler.getNewCorrelationID(csarID, serviceTemplateId,
                                                                    (int) serviceTemplateInstanceID, planEvent, false);
        }

        // plan is of type build, thus create an instance and put the
        // CSARInstanceID into the plan
        ServiceTemplateInstanceID instanceID;
        if (PlanTypes.isPlanTypeURI(planEvent.getPlanType()).equals(PlanTypes.BUILD)) {
            instanceID = ServiceProxy.csarInstanceManagement.createNewInstance(csarID, serviceTemplateId);
            planEvent.setCSARInstanceID(instanceID.getInstanceID());
        } else {
            instanceID = new ServiceTemplateInstanceID(csarID, serviceTemplateId, (int) serviceTemplateInstanceID);
        }
        ServiceProxy.csarInstanceManagement.correlateCSARInstanceWithPlanInstance(instanceID, correlationID);
        ServiceProxy.csarInstanceManagement.setCorrelationAsActive(csarID, correlationID);
        ServiceProxy.csarInstanceManagement.correlateCorrelationIdToPlan(correlationID, planEvent);

        return correlationID;
    }


    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedEncodingException
     */
    @Override
    public void invokePlan(final CSARID csarID, final QName serviceTemplateId, final long serviceTemplateInstanceID,
                           final TPlanDTO givenPlan, final String correlationID) throws UnsupportedEncodingException {

        boolean rulesFulfilled = true;
        if (RulesChecker.areRulesContained(csarID)) {
            rulesFulfilled = RulesChecker.check(csarID, serviceTemplateId, givenPlan.getInputParameters());
            if (rulesFulfilled) {
                this.LOG.debug("Deployment Rules are fulfilled. Continuing the provisioning.");
            } else {
                this.LOG.debug("Deployment Rules are not fulfilled. Arborting the provisioning.");
                return;
            }
        }
        if (rulesFulfilled) {

            // refill information that might not be sent
            final TPlan storedPlan =
                ServiceProxy.toscaReferenceMapper.getPlanForCSARIDAndPlanID(csarID, givenPlan.getId());

            if (null == storedPlan) {
                this.LOG.error("Plan " + givenPlan.getId() + " with name " + givenPlan.getName() + " is null!");
                return;
            }
            if (!storedPlan.getId().equals(givenPlan.getId().getLocalPart())) {
                this.LOG.error("Plan " + givenPlan.getId() + " with internal ID " + givenPlan.getName()
                    + " should copy of PublicPlan " + storedPlan.getId() + "!");
                return;
            }

            givenPlan.setName(storedPlan.getName());
            givenPlan.setPlanLanguage(storedPlan.getPlanLanguage());
            givenPlan.setPlanType(storedPlan.getPlanType());
            givenPlan.setOutputParameters(storedPlan.getOutputParameters());

            final PlanInvocationEvent planEvent = new PlanInvocationEvent();

            this.LOG.info("Invoke the Plan \"" + givenPlan.getId() + "\" of type \"" + givenPlan.getPlanType()
                + "\" of CSAR \"" + csarID + "\".");

            // fill in the informations about this PublicPlan which is not provided
            // by the PublicPlan received by the REST API
            final Map<QName, TPlan> publicPlanMap =
                ServiceProxy.toscaReferenceMapper.getCSARIDToPlans(csarID)
                                                 .get(PlanTypes.isPlanTypeURI(givenPlan.getPlanType()));

            if (null == publicPlanMap) {
                this.LOG.error("Wrong type! \"" + givenPlan.getPlanType() + "\"");
                return;

            }

            planEvent.setCSARID(csarID.toString());

            planEvent.setInterfaceName(ServiceProxy.toscaReferenceMapper.getIntferaceNameOfPlan(csarID,
                                                                                                givenPlan.getId()));
            planEvent.setOperationName(ServiceProxy.toscaReferenceMapper.getOperationNameOfPlan(csarID,
                                                                                                givenPlan.getId()));
            // planEvent.setOutputMessageID(storedPlan.getOutputMessageID());
            planEvent.setPlanLanguage(storedPlan.getPlanLanguage());
            planEvent.setPlanType(storedPlan.getPlanType());
            planEvent.setPlanID(givenPlan.getId());
            planEvent.setIsActive(true);
            planEvent.setHasFailed(false);
            for (final TParameter temp : storedPlan.getInputParameters().getInputParameter()) {
                boolean found = false;

                this.LOG.trace("Processing input parameter {}", temp.getName());

                final List<TParameterDTO> params = givenPlan.getInputParameters().getInputParameter();
                for (final TParameterDTO param : params) {

                    if (param.getName().equals(temp.getName())) {
                        final TParameterDTO dto = param;
                        // param.setRequired(temp.getRequired());
                        // param.setType(temp.getType());
                        found = true;
                        planEvent.getInputParameter().add(dto);
                        String value = dto.getValue();
                        if (value == null) {
                            value = "";
                        }
                        // Probably copied from:
                        // https://stackoverflow.com/a/3777853/7065173
                        // TODO: Check if can use Apache Common's normalize method
                        // to implement this platfrom independently
                        value = value.replace("\\r", "\r");
                        value = value.replace("\r", "");
                        value = value.replace("\\n", "\n");
                        dto.setValue(value);
                        this.LOG.trace("Found input param {} with value {}", param.getName(), param.getValue());
                    }
                }
                if (!found) {
                    this.LOG.trace("Did not found input param {}, thus, insert empty one.", temp.getName());
                    final TParameterDTO newParam = new TParameterDTO();
                    newParam.setName(temp.getName());
                    newParam.setType(temp.getType());
                    newParam.setRequired(temp.getRequired());
                    newParam.setValue("");
                    planEvent.getInputParameter().add(newParam);
                }
            }
            for (final TParameter temp : storedPlan.getOutputParameters().getOutputParameter()) {
                final TParameterDTO param = new TParameterDTO();

                param.setName(temp.getName());
                param.setRequired(temp.getRequired());
                param.setType(temp.getType());

                planEvent.getOutputParameter().add(param);
            }

            final Map<String, Object> eventValues = new Hashtable<>();
            eventValues.put("CSARID", csarID);
            eventValues.put("SERVICETEMPLATEID", serviceTemplateId);
            eventValues.put("PLANID", planEvent.getPlanID());
            eventValues.put("PLANLANGUAGE", planEvent.getPlanLanguage());
            eventValues.put("OPERATIONNAME", planEvent.getOperationName());
            eventValues.put("INPUTS", transform(planEvent.getInputParameter()));
            eventValues.put("SERVICEINSTANCEID", serviceTemplateInstanceID);

            this.LOG.debug("complete the list of parameters {}", givenPlan.getId());

            if (null == ServiceProxy.toscaReferenceMapper.isPlanAsynchronous(csarID, givenPlan.getId())) {
                this.LOG.warn(" There are no informations stored about whether the plan is synchronous or asynchronous. Thus, we believe it is asynchronous.");
                eventValues.put("ASYNC", true);
            } else if (ServiceProxy.toscaReferenceMapper.isPlanAsynchronous(csarID, givenPlan.getId())) {
                eventValues.put("ASYNC", true);
            } else {
                eventValues.put("ASYNC", false);
            }
            eventValues.put("MESSAGEID", correlationID);

            ServiceProxy.csarInstanceManagement.storePublicPlanToHistory(correlationID, planEvent);

            // Create a new instance
            final PlanInstanceRepository repository = new PlanInstanceRepository();
            final PlanInstance pi = new PlanInstance();
            pi.setCorrelationId(correlationID);
            this.LOG.debug("Plan Language: {}", storedPlan.getPlanLanguage());
            pi.setLanguage(PlanLanguage.fromString(storedPlan.getPlanLanguage()));
            this.LOG.debug("Plan Type: {}", storedPlan.getPlanType());
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
            final Event event = new Event("org_opentosca_plans/requests", eventValues);
            this.LOG.debug("Send event with parameters for invocation with the CorrelationID \"{}\".", correlationID);
            ServiceProxy.eventAdmin.sendEvent(event);
        }
    }

    private Map<String, String> transform(final List<TParameterDTO> params) {
        return params.stream().collect(Collectors.toMap(TParameterDTO::getName, TParameterDTO::getValue));
    }


    public Map<String, String> createRequest(final CSARID csarID, final QName serviceTemplateID,
                                             final Long serviceTemplateInstanceId, final QName planInputMessageID,
                                             final List<TParameterDTO> inputParameter,
                                             final String correlationID) throws UnsupportedEncodingException {

        final Map<String, String> map = new HashMap<>();
        final List<Document> docs = new ArrayList<>();

        final List<QName> serviceTemplates = ServiceProxy.toscaEngineService.getServiceTemplatesInCSAR(csarID);
        for (final QName serviceTemplate : serviceTemplates) {
            final List<String> nodeTemplates =
                ServiceProxy.toscaEngineService.getNodeTemplatesOfServiceTemplate(csarID, serviceTemplate);

            for (final String nodeTemplate : nodeTemplates) {
                final Document doc =
                    ServiceProxy.toscaEngineService.getPropertiesOfTemplate(csarID, serviceTemplate, nodeTemplate);
                if (null != doc) {
                    docs.add(doc);
                    this.LOG.trace("Found property document: {}",
                                   ServiceProxy.xmlSerializerService.getXmlSerializer().docToString(doc, false));
                }
            }
        }

        this.LOG.debug("Processing a list of {} parameters", inputParameter.size());
        for (final TParameterDTO para : inputParameter) {
            this.LOG.debug("Put in the parameter {} with value \"{}\".", para.getName(), para.getValue());

            if (para.getName().equalsIgnoreCase("CorrelationID")) {
                this.LOG.debug("Found Correlation Element! Put in CorrelationID \"" + correlationID + "\".");
                map.put(para.getName(), correlationID);
            } else if (para.getName().equalsIgnoreCase("csarID")) {
                this.LOG.debug("Found csarID Element! Put in csarID \"" + csarID + "\".");
                map.put(para.getName(), csarID.toString());
            } else if (para.getName().equalsIgnoreCase("serviceTemplateID")) {
                this.LOG.debug("Found serviceTemplateID Element! Put in serviceTemplateID \"" + serviceTemplateID
                    + "\".");
                map.put(para.getName(), serviceTemplateID.toString());
            } else if (para.getName().equalsIgnoreCase("containerApiAddress")) {
                this.LOG.debug("Found containerApiAddress Element! Put in containerApiAddress \""
                    + Settings.CONTAINER_API_LEGACY + "\".");
                map.put(para.getName(), Settings.CONTAINER_API_LEGACY);
            } else if (para.getName().equalsIgnoreCase("instanceDataAPIUrl")) {
                this.LOG.debug("Found instanceDataAPIUrl Element! Put in instanceDataAPIUrl \""
                    + Settings.CONTAINER_INSTANCEDATA_API + "\".");
                String str = Settings.CONTAINER_INSTANCEDATA_API;
                str = str.replace("{csarid}", csarID.getFileName());
                str = str.replace("{servicetemplateid}",
                                  URLEncoder.encode(URLEncoder.encode(serviceTemplateID.toString(), "UTF-8"), "UTF-8"));
                this.LOG.debug("instance api: {}", str);
                map.put(para.getName(), str);
            } else if (para.getName().equalsIgnoreCase("csarEntrypoint")) {
                this.LOG.debug("Found csarEntrypoint Element! Put in instanceDataAPIUrl \""
                    + Settings.CONTAINER_API_LEGACY + "/" + csarID + "\".");
                map.put(para.getName(), Settings.CONTAINER_API_LEGACY + "/CSARs/" + csarID);
            } else {
                if (para.getName() == null || null == para.getValue() || para.getValue().equals("")) {
                    this.LOG.debug("The parameter \"" + para.getName()
                        + "\" has an empty value, thus search in the properties.");
                    String value = "";
                    for (final Document doc : docs) {
                        final NodeList nodes = doc.getElementsByTagNameNS("*", para.getName());
                        this.LOG.trace("Found {} nodes.", nodes.getLength());
                        if (nodes.getLength() > 0) {
                            value = nodes.item(0).getTextContent();
                            this.LOG.debug("Found value {}", value);
                            break;
                        }
                    }
                    if (value.equals("")) {
                        this.LOG.debug("No value found.");
                    }
                    map.put(para.getName(), value);
                } else {
                    this.LOG.debug("Found element \"" + para.getName() + "\"! Put in \"" + para.getValue() + "\".");
                    map.put(para.getName(), para.getValue());
                }
            }
        }

        return map;
    }

    /**
     * Receives events of the topic list org_opentosca_plans/response. This method handles responses of
     * BPEL-plans.
     */
    @Override
    public void handleEvent(final Event eve) {
        if (eve.getTopic().equals("org_opentosca_plans/responses")) {

            final String correlationID = (String) eve.getProperty("MESSAGEID");
            PlanInvocationEvent event = ServiceProxy.csarInstanceManagement.getPlanFromHistory(correlationID);
            final String planLanguage = event.getPlanLanguage();
            this.LOG.trace("The correlation ID is {} and plan language is {}", correlationID, planLanguage);

            // TODO the concrete handling and parsing shall be in the plugin?!
            if (planLanguage.startsWith(nsBPEL)) {

                @SuppressWarnings("unchecked")
                final Map<String, String> map = (Map<String, String>) eve.getProperty("RESPONSE");

                this.LOG.debug("Received an event with a SOAP response");

                final CSARID csarID = new CSARID(event.getCSARID());

                // parse the body
                // correlationID = responseParser.parseSOAPBody(csarID,
                // event.getPlanID(), correlationID, map);

                // if plan is not null
                if (null == correlationID) {
                    this.LOG.error("The parsing of the response failed!");
                    return;
                }

                this.LOG.trace("Print the plan output:");
                for (final String key : map.keySet()) {
                    this.LOG.trace("   " + key + ": " + map.get(key));
                }

                for (final TParameterDTO param : event.getOutputParameter()) {

                    this.LOG.debug("For variable \"{}\" the output value is \"{}\"", param.getName(),
                                   map.get(param.getName()));
                    param.setValue(map.get(param.getName()));
                    // map.put(param.getName(), value);
                }

                ServiceProxy.csarInstanceManagement.getOutputForCorrelation(correlationID).putAll(map);
                ServiceProxy.csarInstanceManagement.setCorrelationAsFinished(csarID, correlationID);

                // Update state
                final PlanInstanceRepository repository = new PlanInstanceRepository();
                final PlanInstance pi = repository.findByCorrelationId(correlationID);
                if (pi != null) {
                    event.getInputParameter().stream().forEach(p -> {
                        new PlanInstanceInput(p.getName(), p.getValue(), p.getType()).setPlanInstance(pi);
                    });
                    event.getOutputParameter().stream().forEach(p -> {
                        new PlanInstanceOutput(p.getName(), p.getValue(), p.getType()).setPlanInstance(pi);
                    });
                    pi.setState(PlanInstanceState.FINISHED);
                    repository.update(pi);
                } else {
                    this.LOG.error("Plan instance for correlation id '{}' not found", correlationID);
                }

                // save
                final ServiceTemplateInstanceID instanceID =
                    ServiceProxy.csarInstanceManagement.getInstanceForCorrelation(correlationID);
                this.LOG.debug("The instanceID is: " + instanceID);
                ServiceProxy.csarInstanceManagement.storeCorrelationForAnInstance(instanceID.getCsarId(), instanceID,
                                                                                  correlationID);

                if (event.isHasFailed()) {
                    this.LOG.info("The process instance was not successful.");

                } else {
                    if (PlanTypes.isPlanTypeURI(event.getPlanType()).equals(PlanTypes.TERMINATION)) {
                        final boolean deletion =
                            ServiceProxy.csarInstanceManagement.deleteInstance(instanceID.getCsarId(), instanceID);
                        this.LOG.debug("Delete of instance returns: " + deletion);
                    }
                }
            } else if (planLanguage.startsWith(nsBPMN)) {

                final Object response = eve.getProperty("RESPONSE");

                this.LOG.debug("Received an event with a REST response: {}", response);

                event = ServiceProxy.csarInstanceManagement.getPlanFromHistory(correlationID);
                this.LOG.trace("Found invocation in plan history for instance: {}", event.getCSARInstanceID());
                final CSARID csarID = new CSARID(event.getCSARID());

                // parse process instance ID out of REST response
                final String planInstanceID = parseRESTResponse(response);
                if (null == planInstanceID || planInstanceID.equals("")) {
                    this.LOG.error("The parsing of the response failed!");
                    return;
                }
                this.LOG.debug("Instance ID: " + planInstanceID);

                // create web resource to retrieve the current state of the process instance
                final Client client = Client.create();
                WebResource webResource =
                    Client.create()
                          .resource(Settings.ENGINE_PLAN_BPMN_URL + this.PROCESS_INSTANCE_PATH + planInstanceID);

                // wait until the process instance terminates
                while (true) {
                    final String resp = webResource.get(ClientResponse.class).getEntity(String.class);
                    this.LOG.debug("Active process instance response: " + resp);

                    try {
                        Thread.sleep(10000);
                    }
                    catch (final InterruptedException e) {
                        e.printStackTrace();
                    }

                    // check if history contains process instance with this ID
                    if (resp.equals(this.EMPTY_JSON)) {
                        this.LOG.debug("The plan instance {} is not active any more.", planInstanceID);
                        break;
                    }
                }

                final Map<String, String> map =
                    ServiceProxy.csarInstanceManagement.getOutputForCorrelation(correlationID);

                // get output parameters of the plan from the process instance variables
                for (final TParameterDTO param : event.getOutputParameter()) {
                    final String path = Settings.ENGINE_PLAN_BPMN_URL + this.HISTORY_PATH;

                    // get variable instances of the process instance with the param name
                    webResource = client.resource(path);
                    webResource = webResource.queryParam("processInstanceId", planInstanceID);
                    webResource = webResource.queryParam("activityInstanceIdIn", planInstanceID);
                    webResource = webResource.queryParam("variableName", param.getName());
                    final String responseStr = webResource.get(ClientResponse.class).getEntity(String.class);

                    if (responseStr.equals(this.EMPTY_JSON)) {
                        this.LOG.warn("Unable to find variable instance for output parameter: {}", param.getName());
                        continue;
                    }

                    String value = null;
                    try {
                        final JsonParser parser = new JsonParser();
                        final JsonObject json =
                            (JsonObject) parser.parse(responseStr.substring(1, responseStr.length() - 1));
                        value = json.get("value").getAsString();
                    }
                    catch (final ClassCastException e) {
                        this.LOG.trace("value is null");
                        value = "";
                    }
                    this.LOG.debug("For variable \"{}\" the output value is \"{}\"", param.getName(), value);
                    param.setValue(value);
                    map.put(param.getName(), value);
                }

                ServiceProxy.csarInstanceManagement.getOutputForCorrelation(correlationID).putAll(map);
                ServiceProxy.csarInstanceManagement.setCorrelationAsFinished(csarID, correlationID);

                // Update state
                final PlanInstanceRepository repository = new PlanInstanceRepository();
                final PlanInstance pi = repository.findByCorrelationId(correlationID);
                if (pi != null) {
                    pi.setState(PlanInstanceState.FINISHED);
                    repository.update(pi);
                } else {
                    this.LOG.error("Plan instance for correlation id '{}' not found", correlationID);
                }

                // save
                final ServiceTemplateInstanceID instanceID =
                    ServiceProxy.csarInstanceManagement.getInstanceForCorrelation(correlationID);
                this.LOG.debug("The instanceID is: " + instanceID);
                ServiceProxy.csarInstanceManagement.storeCorrelationForAnInstance(instanceID.getCsarId(), instanceID,
                                                                                  correlationID);

                if (event.isHasFailed()) {
                    this.LOG.info("The process instance was not successful.");

                } else {
                    if (PlanTypes.isPlanTypeURI(event.getPlanType()).equals(PlanTypes.TERMINATION)) {
                        final boolean deletion =
                            ServiceProxy.csarInstanceManagement.deleteInstance(instanceID.getCsarId(), instanceID);
                        this.LOG.debug("Delete of instance returns: " + deletion);
                    }
                }
            } else {
                this.LOG.error("The returned response cannot be matched to a supported plan language!");
                return;
            }

            ServiceProxy.correlationHandler.removeCorrelation(correlationID);
        }
    }

    private String parseRESTResponse(final Object responseBody) {
        final String resp = (String) responseBody;
        final String instanceID = resp.substring(resp.indexOf("href\":\"") + 7, resp.length());
        return instanceID.substring(instanceID.lastIndexOf("/") + 1, instanceID.indexOf("\""));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TPlanDTO getActivePublicPlanOfInstance(final ServiceTemplateInstanceID csarInstanceID,
                                                  final String correlationID) {
        return ServiceProxy.correlationHandler.getPlanDTOForCorrelation(csarInstanceID, correlationID);
    }
}
