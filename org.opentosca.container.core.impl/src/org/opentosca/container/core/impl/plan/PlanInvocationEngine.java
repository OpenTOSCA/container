package org.opentosca.container.core.impl.plan;

import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.NoResultException;
import javax.xml.namespace.QName;

import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.model.csar.id.CSARID;
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

    private final String PROCESS_INSTANCE_PATH = "/process-instance?processInstanceIds=";
    private final String HISTORY_PATH = "/history/variable-instance";
    private final String EMPTY_JSON = "[]";

    private final static ServiceTemplateInstanceRepository stiRepo = new ServiceTemplateInstanceRepository();

    private final static PlanInstanceRepository planRepo = new PlanInstanceRepository();

    @Override
    public String createCorrelationId(final CSARID csarID, final QName serviceTemplateId,
                                      long serviceTemplateInstanceID, final TPlanDTO givenPlan) {

        // refill information that might not be sent
        final TPlan storedPlan = ServiceProxy.toscaReferenceMapper.getPlanForCSARIDAndPlanID(csarID, givenPlan.getId());

        // TODO: delete after refactor
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

        // generate CorrelationId for the plan execution
        correlationID = generateUniqueCorrelationId();

        // update serviceTemplateInstanceID for build plas
        if (serviceTemplateInstanceID == -1) {
            serviceTemplateInstanceID = 1000 + (int) (Math.random() * (Integer.MAX_VALUE - 1000));
        }

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

        if (RulesChecker.areRulesContained(csarID)) {
            if (RulesChecker.check(csarID, serviceTemplateId, givenPlan.getInputParameters())) {
                this.LOG.debug("Deployment Rules are fulfilled. Continuing the provisioning.");
            } else {
                this.LOG.debug("Deployment Rules are not fulfilled. Arborting the provisioning.");
                return;
            }
        }

        // refill information that might not be sent
        final TPlan storedPlan = ServiceProxy.toscaReferenceMapper.getPlanForCSARIDAndPlanID(csarID, givenPlan.getId());

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

        // TODO: add the input parameters directly to pi
        planEvent.getInputParameter().stream().forEach(p -> {
            new PlanInstanceInput(p.getName(), p.getValue(), p.getType()).setPlanInstance(pi);
        });
        repository.add(pi);

        // send the message to the service bus
        final Event event = new Event("org_opentosca_plans/requests", eventValues);
        this.LOG.debug("Send event with parameters for invocation with the CorrelationID \"{}\".", correlationID);
        ServiceProxy.eventAdmin.sendEvent(event);
    }

    private Map<String, String> transform(final List<TParameterDTO> params) {
        return params.stream().collect(Collectors.toMap(TParameterDTO::getName, TParameterDTO::getValue));
    }

    /**
     * Receives events of the topic list org_opentosca_plans/response. This method handles responses of
     * BPEL-plans.
     */
    @Override
    public void handleEvent(final Event eve) {
        if (eve.getTopic().equals("org_opentosca_plans/responses")) {

            final String correlationID = (String) eve.getProperty("MESSAGEID");
            if (Objects.isNull(correlationID)) {
                this.LOG.error("The parsing of the response failed!");
                return;
            }

            final PlanInstance plan = planRepo.findByCorrelationId(correlationID);
            final PlanLanguage language = plan.getLanguage();
            this.LOG.debug("Received response that belongs to plan with correlation ID: {}, state: {}, language: {}",
                           correlationID, plan.getState().toString(), language.toString());

            final CSARID csarID = plan.getServiceTemplateInstance().getCsarId();
            final TPlan planModel =
                ServiceProxy.toscaReferenceMapper.getPlanForCSARIDAndPlanID(csarID, plan.getTemplateId());

            if (language.equals(PlanLanguage.BPEL)) {

                @SuppressWarnings("unchecked")
                final Map<String, String> map = (Map<String, String>) eve.getProperty("RESPONSE");

                this.LOG.trace("Print the plan output:");
                for (final String key : map.keySet()) {
                    this.LOG.trace("   " + key + ": " + map.get(key));
                }

                // add output parameters to the PLanInstance object and update repository
                for (final TParameter param : planModel.getOutputParameters().getOutputParameter()) {
                    new PlanInstanceOutput(param.getName(), map.get(param.getName()),
                        param.getType()).setPlanInstance(plan);
                }
                planRepo.update(plan);

            } else if (language.equals(PlanLanguage.BPMN)) {

                final Object response = eve.getProperty("RESPONSE");

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

                // get output parameters of the plan from the process instance variables
                for (final TParameter param : planModel.getOutputParameters().getOutputParameter()) {
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
                    new PlanInstanceOutput(param.getName(), value, param.getType()).setPlanInstance(plan);
                }
                planRepo.update(plan);

            } else {
                this.LOG.error("The returned response cannot be matched to a supported plan language!");
                return;
            }
        }
    }

    private String parseRESTResponse(final Object responseBody) {
        final String resp = (String) responseBody;
        final String instanceID = resp.substring(resp.indexOf("href\":\"") + 7, resp.length());
        return instanceID.substring(instanceID.lastIndexOf("/") + 1, instanceID.indexOf("\""));
    }

    private synchronized String generateUniqueCorrelationId() {
        while (true) {
            final String correlationId = String.valueOf(System.currentTimeMillis());

            try {
                planRepo.findByCorrelationId(correlationId);
                this.LOG.debug("CorrelationId {} already in use.", correlationId);
            }
            catch (final NoResultException e) {
                return correlationId;
            }
        }
    }
}
