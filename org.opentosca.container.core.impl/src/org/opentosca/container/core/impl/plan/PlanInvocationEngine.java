package org.opentosca.container.core.impl.plan;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;

import javax.persistence.NoResultException;
import javax.xml.namespace.QName;

import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.next.model.PlanInstance;
import org.opentosca.container.core.next.repository.PlanInstanceRepository;
import org.opentosca.container.core.service.IPlanInvocationEngine;
import org.opentosca.container.core.tosca.extension.TParameterDTO;
import org.opentosca.container.core.tosca.extension.TPlanDTO;
import org.osgi.service.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Implementation of the Plan Invocation Engine. Also deals with OSGI events for communication
 * with the Management Bus.
 */
public class PlanInvocationEngine implements IPlanInvocationEngine {

    private final Logger LOG = LoggerFactory.getLogger(PlanInvocationEngine.class);

    private final static PlanInstanceRepository planRepo = new PlanInstanceRepository();

    @Override
    public String createCorrelationId() {
        // generate CorrelationId for the plan execution
        while (true) {
            final String correlationId = String.valueOf(System.currentTimeMillis());

            try {
                PlanInstance instance = planRepo.findByCorrelationId(correlationId);
                if(instance == null) {
                    return correlationId;
                }
                this.LOG.debug("CorrelationId {} already in use.", correlationId);
            }
            catch (final NoResultException e) {
                return correlationId;
            }
        }
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
                this.LOG.debug("Deployment Rules are not fulfilled. Aborting the provisioning.");
                return;
            }
        }

        this.LOG.info("Invoke the Plan {} of type {} of CSAR {}", givenPlan.getId(), givenPlan.getPlanType(), csarID);

        final HashMap<String, String> input = new HashMap<>();
        for (final TParameterDTO param : givenPlan.getInputParameters().getInputParameter()) {
            if (Objects.isNull(param.getValue())) {
                input.put(param.getName(), "");
            } else {
                input.put(param.getName(), param.getValue());
            }
        }

        // prepare the message for the bus
        final Map<String, Object> eventValues = new Hashtable<>();
        eventValues.put("CSARID", csarID);
        eventValues.put("SERVICETEMPLATEID", serviceTemplateId);
        eventValues.put("PLANID", givenPlan.getId());
        eventValues.put("PLANLANGUAGE", givenPlan.getPlanLanguage());
        eventValues.put("SERVICEINSTANCEID", serviceTemplateInstanceID);
        eventValues.put("MESSAGEID", correlationID);
        eventValues.put("OPERATIONNAME",
                        ServiceProxy.toscaReferenceMapper.getOperationNameOfPlan(csarID, givenPlan.getId()));
        eventValues.put("INPUTS", input);

        // determine execution style (sync/async)
        if (Objects.isNull(ServiceProxy.toscaReferenceMapper.isPlanAsynchronous(csarID, givenPlan.getId()))
            || ServiceProxy.toscaReferenceMapper.isPlanAsynchronous(csarID, givenPlan.getId())) {
            eventValues.put("ASYNC", true);
        } else {
            eventValues.put("ASYNC", false);
        }

        // send the message to the service bus
        final Event event = new Event("org_opentosca_plans/requests", eventValues);
        this.LOG.debug("Send event with parameters for invocation with the CorrelationID \"{}\".", correlationID);
        ServiceProxy.eventAdmin.sendEvent(event);
    }
}
