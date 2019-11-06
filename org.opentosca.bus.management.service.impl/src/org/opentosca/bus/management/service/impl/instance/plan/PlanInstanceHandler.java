package org.opentosca.bus.management.service.impl.instance.plan;

import java.util.HashMap;
import java.util.Objects;

import javax.persistence.NoResultException;
import javax.xml.namespace.QName;

import org.opentosca.bus.management.service.impl.servicehandler.ServiceHandler;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.next.model.PlanInstance;
import org.opentosca.container.core.next.model.PlanInstanceInput;
import org.opentosca.container.core.next.model.PlanInstanceState;
import org.opentosca.container.core.next.model.PlanLanguage;
import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.container.core.next.repository.PlanInstanceRepository;
import org.opentosca.container.core.next.repository.ServiceTemplateInstanceRepository;
import org.opentosca.container.core.tosca.model.TParameter;
import org.opentosca.container.core.tosca.model.TPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class which handles the creation and updating of plan instance data.<br>
 * <br>
 *
 * Copyright 2019 IAAS University of Stuttgart
 */
public class PlanInstanceHandler {

    private final static Logger LOG = LoggerFactory.getLogger(PlanInstanceHandler.class);

    private final static ServiceTemplateInstanceRepository stiRepo = new ServiceTemplateInstanceRepository();
    private final static PlanInstanceRepository planRepo = new PlanInstanceRepository();

    /**
     * TODO
     *
     * @param csarId
     * @param serviceTemplateId
     * @param serviceTemplateInstanceID
     * @param planId
     * @param correlationId
     * @param input
     */
    public static void createPlanInstance(final CSARID csarId, final QName serviceTemplateId,
                                          final long serviceTemplateInstanceID, final QName planId,
                                          final String correlationId, final Object input) {

        final TPlan storedPlan = ServiceHandler.toscaReferenceMapper.getPlanForCSARIDAndPlanID(csarId, planId);
        if (Objects.isNull(storedPlan)) {
            LOG.error("Plan with ID {} in CSAR {} is null!", planId, csarId);
            return;
        }

        // create a new plan instance
        final PlanInstance plan = new PlanInstance();
        plan.setCorrelationId(correlationId);
        plan.setLanguage(PlanLanguage.fromString(storedPlan.getPlanLanguage()));
        plan.setType(PlanType.fromString(storedPlan.getPlanType()));
        plan.setState(PlanInstanceState.RUNNING);
        plan.setTemplateId(planId);

        // cast input parameters for the plan invocation
        HashMap<String, String> inputMap = new HashMap<>();
        if (input instanceof HashMap) {
            inputMap = (HashMap<String, String>) input;
        }

        // add input parameters to the plan instance
        for (final TParameter param : storedPlan.getInputParameters().getInputParameter()) {
            new PlanInstanceInput(param.getName(), inputMap.getOrDefault(param.getName(), ""),
                param.getType()).setPlanInstance(plan);
        }

        // add connection to the service template and update the repository
        stiRepo.find(serviceTemplateInstanceID)
               .ifPresent(serviceTemplateInstance -> plan.setServiceTemplateInstance(serviceTemplateInstance));
        planRepo.add(plan);
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
            }
            catch (final NoResultException e) {
                return correlationId;
            }
        }
    }
}
