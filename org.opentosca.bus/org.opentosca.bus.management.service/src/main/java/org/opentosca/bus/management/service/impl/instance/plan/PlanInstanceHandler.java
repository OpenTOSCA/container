package org.opentosca.bus.management.service.impl.instance.plan;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.persistence.NoResultException;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TParameter;
import org.eclipse.winery.model.tosca.TPlan;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.client.JerseyWebTarget;
import org.opentosca.bus.management.service.impl.Constants;
import org.opentosca.container.core.common.NotFoundException;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.engine.ToscaEngine;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.next.model.PlanInstance;
import org.opentosca.container.core.next.model.PlanInstanceInput;
import org.opentosca.container.core.next.model.PlanInstanceOutput;
import org.opentosca.container.core.next.model.PlanInstanceState;
import org.opentosca.container.core.next.model.PlanLanguage;
import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.container.core.next.repository.PlanInstanceRepository;
import org.opentosca.container.core.next.repository.ServiceTemplateInstanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class which handles the creation and updating of plan instance data.<br>
 * <br>
 * <p>
 * Copyright 2019 IAAS University of Stuttgart
 */
public class PlanInstanceHandler {

    private final static Logger LOG = LoggerFactory.getLogger(PlanInstanceHandler.class);

    private final static ServiceTemplateInstanceRepository stiRepo = new ServiceTemplateInstanceRepository();
    private final static PlanInstanceRepository planRepo = new PlanInstanceRepository();

    /**
     * Create a plan instance for the instance API and add the details about name, type, input parameters, etc.
     *
     * @param csar                      the CSAR the plan belongs to
     * @param serviceTemplateId         the Id of the ServiceTemplate the plan belongs to
     * @param serviceTemplateInstanceId the Id of the ServiceTemplate instance the plan belongs to
     * @param planId                    the ID of the plan
     * @param correlationId             the correlation Id that uniquely identifies the plan instance
     * @param input                     the input parameters of the plan instance
     * @return the created PlanInstance or <code>null</code> if the creation failed
     */
    public static PlanInstance createPlanInstance(final Csar csar, final QName serviceTemplateId,
                                                  final long serviceTemplateInstanceId, final QName planId,
                                                  final String operationName, final String correlationId,
                                                  final Object input) throws CorrelationIdAlreadySetException {

        if (Objects.isNull(planId)) {
            LOG.error("Plan ID is null! Unable to create PlanInstance!");
            return null;
        }

        final TPlan storedPlan;
        try {
            storedPlan = ToscaEngine.resolvePlanReference(csar, planId);
        } catch (NotFoundException e) {
            LOG.error("Plan with ID {} does not exist in CSAR {}!", planId, csar.id().csarName());
            return null;
        }

        // create a new plan instance
        final PlanInstance plan = new PlanInstance();
        plan.setCorrelationId(correlationId);
        plan.setLanguage(PlanLanguage.fromString(storedPlan.getPlanLanguage()));
        plan.setType(PlanType.fromString(storedPlan.getPlanType()));
        plan.setState(PlanInstanceState.RUNNING);
        plan.setTemplateId(planId);

        // check if plan instance with that correlation ID is already present
        final Optional<PlanInstance> planOptional =
            planRepo.findAll().stream().filter(p -> p.getCorrelationId().equals(correlationId)).findFirst();
        if (planOptional.isPresent()) {
            if (operationName.equals("receiveNotify")) {
                LOG.debug("Processing receiveNotify and plan instance already exists!");
                return planOptional.get();
            } else {
                throw new CorrelationIdAlreadySetException(
                    "Plan instance with correlation ID " + correlationId + " is already existing.");
            }
        }

        // cast input parameters for the plan invocation
        Map<String, String> inputMap = new HashMap<>();
        if (input instanceof HashMap) {
            inputMap = (HashMap<String, String>) input;
        }

        // add input parameters to the plan instance
        for (final TParameter param : storedPlan.getInputParameters().getInputParameter()) {
            new PlanInstanceInput(param.getName(), inputMap.getOrDefault(param.getName(), ""),
                param.getType()).setPlanInstance(plan);
        }

        // add connection to the service template and update the repository
        stiRepo.find(serviceTemplateInstanceId)
            .ifPresent(serviceTemplateInstance -> plan.setServiceTemplateInstance(serviceTemplateInstance));
        planRepo.add(plan);

        return plan;
    }

    /**
     * Create a unique correlation ID based on the current time.
     *
     * @return the unique correlation ID
     */
    public static String createCorrelationId() {
        // generate CorrelationId for the plan execution
        while (true) {
            final String correlationId = String.valueOf(System.currentTimeMillis());

            try {
                planRepo.findByCorrelationId(correlationId);
                LOG.debug("CorrelationId {} already in use.", correlationId);
            } catch (final NoResultException e) {
                return correlationId;
            }
        }
    }

    /**
     * Update the plan instance information with the output parameters from the plan invocation.
     *
     * @param plan the plan instance object to update
     * @param csar the Id of the CSAR the plan belongs to
     * @param body the body of the camel envelope resulting from the invocation and containing the output parameters
     */
    public static void updatePlanInstanceOutput(final PlanInstance plan, final Csar csar, final Object body) {

        final TPlan planModel;
        try {
            planModel = ToscaEngine.resolvePlanReference(csar, plan.getTemplateId());
        } catch (NotFoundException e) {
            LOG.warn("Could not find plan with id {} corresponding to instance {} in csar {}! Aborting output update.",
                plan.getTemplateId(), plan.getCorrelationId(), csar.id().csarName());
            return;
        }

        if (plan.getLanguage().equals(PlanLanguage.BPEL)) {
            LOG.debug("Received response from BPEL plan");
            if (body instanceof Map) {
                @SuppressWarnings("unchecked") final Map<String, String> map = (Map<String, String>) body;
                // add output parameters to the PlanInstance object and update repository
                for (final TParameter param : planModel.getOutputParameters().getOutputParameter()) {
                    new PlanInstanceOutput(param.getName(), map.get(param.getName()),
                        param.getType()).setPlanInstance(plan);
                }
            } else {
                LOG.error("Response from BPEL plan is not of type Map");
            }
        } else if (plan.getLanguage().equals(PlanLanguage.BPMN)) {
            LOG.debug("Received response from BPMN plan");
            // parse process instance ID out of REST response
            final String planInstanceID = parseRESTResponse(body);
            if (Objects.isNull(planInstanceID) || planInstanceID.equals("")) {
                LOG.error("The parsing of the response failed!");
                return;
            }
            LOG.debug("Instance ID of the plan in Camunda: {}", planInstanceID);

            // create web resource to retrieve the current state of the process instance
            final JerseyClient client = JerseyClientBuilder.createClient();
            JerseyWebTarget webResource =
                client.target(Settings.ENGINE_PLAN_BPMN_URL + Constants.PROCESS_INSTANCE_PATH + planInstanceID);

            // wait until the process instance terminates
            while (true) {
                final String resp = webResource.request().get().readEntity(String.class);
                LOG.debug("Active process instance response: " + resp);

                try {
                    Thread.sleep(10000);
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }

                // check if history contains process instance with this ID
                if (resp.equals("[]")) {
                    LOG.debug("The plan instance {} is not active any more.", planInstanceID);
                    break;
                }
            }

            // get output parameters of the plan from the process instance variables
            for (final TParameter param : planModel.getOutputParameters().getOutputParameter()) {
                final String path = Settings.ENGINE_PLAN_BPMN_URL + Constants.HISTORY_PATH;

                // get variable instances of the process instance with the param name
                webResource = client.target(path);
                webResource = webResource.queryParam("processInstanceId", planInstanceID);
                webResource = webResource.queryParam("activityInstanceIdIn", planInstanceID);
                webResource = webResource.queryParam("variableName", param.getName());
                final String responseStr = webResource.request().get().readEntity(String.class);

                if (responseStr.equals("[]")) {
                    LOG.warn("Unable to find variable instance for output parameter: {}", param.getName());
                    continue;
                }

                String value = null;
                try {
                    final JsonParser parser = new JsonParser();
                    final JsonObject json =
                        (JsonObject) parser.parse(responseStr.substring(1, responseStr.length() - 1));
                    value = json.get("value").getAsString();
                } catch (final ClassCastException e) {
                    LOG.trace("value is null");
                    value = "";
                }
                LOG.debug("For variable \"{}\" the output value is \"{}\"", param.getName(), value);
                new PlanInstanceOutput(param.getName(), value, param.getType()).setPlanInstance(plan);
            }
        } else {
            LOG.error("Unable to handle response for plan invocations with the plan language: {}", plan.getLanguage());
        }

        // update the repo with the changed plan instance
        planRepo.update(plan);
    }

    /**
     * Parse the REST response returned by Camunda BPMN
     *
     * @param responseBody the body of the response
     * @return the Camunda instance ID identifying the plan instance
     */
    private static String parseRESTResponse(final Object responseBody) {
        final String resp = (String) responseBody;
        final String instanceID = resp.substring(resp.indexOf("href\":\"") + 7, resp.length());
        return instanceID.substring(instanceID.lastIndexOf("/") + 1, instanceID.indexOf("\""));
    }
}
