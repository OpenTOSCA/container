package org.opentosca.container.core.next.services;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.next.model.PlanInstance;
import org.opentosca.container.core.next.model.PlanInstanceEvent;
import org.opentosca.container.core.next.model.PlanInstanceState;
import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.repository.PlanInstanceRepository;
import org.opentosca.container.core.next.repository.ServiceTemplateInstanceRepository;
import org.opentosca.container.core.next.trigger.PlanInstanceSubscriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PlanInstanceService {

    private static final Logger logger = LoggerFactory.getLogger(PlanInstanceService.class);
    private final ServiceTemplateInstanceRepository serviceTemplateInstanceRepository;
    private final PlanInstanceRepository planInstanceRepository;
    private final PlanInstanceSubscriptionService subscriptionService;

    @Inject
    public PlanInstanceService(ServiceTemplateInstanceRepository serviceTemplateInstanceRepository,
                               PlanInstanceRepository planInstanceRepository,
                               PlanInstanceSubscriptionService subscriptionService) {
        this.serviceTemplateInstanceRepository = serviceTemplateInstanceRepository;
        this.planInstanceRepository = planInstanceRepository;
        this.subscriptionService = subscriptionService;
    }

    public List<PlanInstance> getPlanInstance(final Long serviceTemplateInstanceId, final PlanType... planTypes) {
        return this.planInstanceRepository.findAll().stream()
            .filter(p -> {
                final PlanType currentType = PlanType.fromString(p.getType().toString());
                return Arrays.stream(planTypes).anyMatch(pt -> pt.equals(currentType)) && p.getServiceTemplateInstance().getId().equals(serviceTemplateInstanceId);
            })
            .collect(Collectors.toList());
    }

    public List<PlanInstance> getPlanInstances(final Csar csar, final PlanType... planTypes) {
        final Collection<ServiceTemplateInstance> serviceInstances = serviceTemplateInstanceRepository.findByCsarId(csar.id());
        return serviceInstances.stream()
            .flatMap(sti -> sti.getPlanInstances().stream())
            .filter(p -> {
                final PlanType currentType = PlanType.fromString(p.getType().toString());
                return Arrays.stream(planTypes).anyMatch(pt -> pt.equals(currentType));
            })
            .collect(Collectors.toList());
    }

    public PlanInstance getPlanInstanceByCorrelationId(final String correlationId) {
        return planInstanceRepository.findByCorrelationId(correlationId);
    }

    public PlanInstance getPlanInstanceWithLogsByCorrelationId(final String correlationId) {
        return planInstanceRepository.findWithLogsByCorrelationId(correlationId);
    }

    public PlanInstance resolvePlanInstance(Long serviceTemplateInstanceId, String correlationId) {
        PlanInstance pi = (PlanInstance) this.waitForInstanceAvailable(correlationId).joinAndGet(30000);

        if (pi == null) {
            final String msg = "Plan instance with correlationId '" + correlationId + "' not found";
            logger.error(msg);
            throw new NotFoundException(msg);
        }

        if (pi.getServiceTemplateInstance() != null && serviceTemplateInstanceId != null && serviceTemplateInstanceId != pi.getServiceTemplateInstance().getId()) {
            throw new NotFoundException(String.format("The passed service template instance id <%s> does not match the service template instance id that is associated with the plan instance <%s> ",
                serviceTemplateInstanceId, correlationId));
        }
        return pi;
    }

    public PlanInstance resolvePlanInstanceWithLogs(Long serviceTemplateInstanceId, String correlationId) {
        // FIXME this can be done better, im pretty sure about that, e.g., subscribing to a planinstance with logs?
        // right now we will have 2 "queries" at least
        PlanInstance pi = this.resolvePlanInstance(serviceTemplateInstanceId, correlationId);

        return this.planInstanceRepository.findWithLogsById(pi.getId());
    }

    public boolean updatePlanInstanceState(PlanInstance instance, PlanInstanceState newState) {
        try {
            instance.setState(newState);
            this.planInstanceRepository.save(instance);
            return true;
        } catch (final IllegalArgumentException e) {
            logger.info("The given state {} is an illegal plan instance state.", newState);
            return false;
        }
    }

    public void addLogToPlanInstance(PlanInstance instance, PlanInstanceEvent event) {
        instance.addEvent(event);
        planInstanceRepository.save(instance);
    }

    public PlanInstanceSubscriptionService.SubscriptionRunner waitForStateChange(PlanInstance instance, PlanInstanceState expectedState) {
        return this.subscriptionService.subscribeToStateChange(instance, expectedState);
    }

    public PlanInstance getPlanInstanceWithOutputs(Long id) {
        return this.planInstanceRepository.findWithOutputsById(id);
    }

    public PlanInstanceSubscriptionService.SubscriptionRunner waitForInstanceAvailable(String correlationId) {
        return this.subscriptionService.subscribeToInstanceAvailable(correlationId, this.planInstanceRepository);
    }
}
