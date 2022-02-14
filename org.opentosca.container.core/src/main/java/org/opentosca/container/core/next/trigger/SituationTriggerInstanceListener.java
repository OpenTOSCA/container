package org.opentosca.container.core.next.trigger;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.persistence.PostPersist;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TExportedOperation;
import org.eclipse.winery.model.tosca.TPlan;

import com.google.common.collect.Lists;
import org.glassfish.jersey.uri.UriComponent;
import org.opentosca.container.core.common.NotFoundException;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.engine.ToscaEngine;
import org.opentosca.container.core.extension.TParameterDTO;
import org.opentosca.container.core.extension.TPlanDTO;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.PlanInstance;
import org.opentosca.container.core.next.model.PlanInstanceEvent;
import org.opentosca.container.core.next.model.PlanInstanceState;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.model.Situation;
import org.opentosca.container.core.next.model.SituationTriggerInstance;
import org.opentosca.container.core.next.model.SituationTriggerInstanceProperty;
import org.opentosca.container.core.next.model.SituationTriggerProperty;
import org.opentosca.container.core.next.repository.PlanInstanceRepository;
import org.opentosca.container.core.next.repository.SituationTriggerInstanceRepository;
import org.opentosca.container.core.service.CsarStorageService;
import org.opentosca.container.core.service.IPlanInvocationEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

public class SituationTriggerInstanceListener {

    private static final List<SituationTriggerInstanceObserver> obs = Lists.newArrayList();

    private static Map<String, List<String>> planToOperationMap = new HashMap<>();

    @PostPersist
    public void startSituationTriggerInstanceObserver(final SituationTriggerInstance instance) {
        final SituationTriggerInstanceObserver obs = new SituationTriggerInstanceObserver(instance);
        // this performs service injection for us
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(obs);

        SituationTriggerInstanceListener.obs.add(obs);
        new Thread(obs).start();
    }

    private boolean isPlanExecutionFinished(final TPlanDTO plan, final String correlationId) {

        for (final TParameterDTO param : plan.getOutputParameters()) {
            if (param.getName().equalsIgnoreCase("correlationid") && param.getValue() != null
                && param.getValue().equals(correlationId)) {
                return true;
            }
        }

        return false;
    }

    public Map<String, List<String>> getPlanToOperationMap() {
        return SituationTriggerInstanceListener.planToOperationMap;
    }

    public void setPlanToOperationMap(final Map<String, List<String>> planToOperationMap) {
        SituationTriggerInstanceListener.planToOperationMap = planToOperationMap;
    }

    /**
     * calculate the WCET for the given Plan by summing up operation times in plan. Does not regard parallel
     * executions.
     */
    public long calculateWCETForPlan(final TPlan plan) {
        long calculatedTimeFromPreviousExecutions = 0;

        // contains mapping of PlanName to its contained operations
        final Map<String, List<String>> planNameToOperationsMap = getPlanToOperationMap();
        // map of longest execution times for each operation
        final Map<String, Long> longestDurationMap = new HashMap<>();
        // find all operations contained in current plan
        final List<String> allOperationsInPlan = planNameToOperationsMap.get(plan.getId());

        // get all previously completed PlanInstances from DB
        final PlanInstanceRepository planRepo = new PlanInstanceRepository();
        final Collection<PlanInstance> allOccurences = planRepo.findAll();

        // iterate all instances until match is found
        if (allOperationsInPlan != null) {
            for (final PlanInstance currInstance : allOccurences) {
                if (currInstance.getTemplateId().getLocalPart().equals(plan.getId())) {
                    iterateInstanceEventsForExecutionTimes(longestDurationMap, allOperationsInPlan, currInstance);
                }
            }
        }
        // add up the times of longest durations found for operations in plan
        for (final Long duration : longestDurationMap.values()) {
            calculatedTimeFromPreviousExecutions += duration;
        }
        return calculatedTimeFromPreviousExecutions;
    }

    /**
     * iterate through all PlanInstanceEvents of a PlanInstance and compare with matching operation from current Plan
     */
    private void iterateInstanceEventsForExecutionTimes(final Map<String, Long> longestDurationMap,
                                                        final List<String> allOperationsInPlan, final PlanInstance currInstance) {
        // iterate all operations from current plan
        for (final String oneOperationFromPlan : allOperationsInPlan) {
            // iterate all events from current PlanInstance
            for (final PlanInstanceEvent aEvent : currInstance.getEvents()) {
                if (Objects.nonNull(aEvent.getOperationName()) && Objects.nonNull(aEvent.getExecutionDuration())
                    && Objects.nonNull(aEvent.getNodeTemplateID())) {
                    if (oneOperationFromPlan.equals(aEvent.getNodeTemplateID() + aEvent.getOperationName())) {
                        checkIfCurrentOperationExecutionTimeIsLonger(longestDurationMap, aEvent);
                    }
                }
            }
        }
    }

    /**
     * if operation already contained in map, check if current execution duration is larger (replace) or smaller
     * (leave)
     */
    private void checkIfCurrentOperationExecutionTimeIsLonger(final Map<String, Long> longestDurationMap,
                                                              final PlanInstanceEvent aEvent) {
        // key already exists in map
        if (longestDurationMap.containsKey(aEvent.getNodeTemplateID() + aEvent.getOperationName())) {
            if (longestDurationMap.get(aEvent.getNodeTemplateID() + aEvent.getOperationName()) < aEvent
                .getExecutionDuration()) {
                longestDurationMap.put(aEvent.getNodeTemplateID() + aEvent.getOperationName(),
                    aEvent.getExecutionDuration());
            }
        } else {
            longestDurationMap.put(aEvent.getNodeTemplateID() + aEvent.getOperationName(),
                aEvent.getExecutionDuration());
        }
    }

    private class SituationTriggerInstanceObserver implements Runnable {

        final private Logger LOG = LoggerFactory.getLogger(SituationTriggerInstanceObserver.class);
        private final PlanInstanceRepository planRepository = new PlanInstanceRepository();
        private final SituationTriggerInstance instance;
        @Autowired
        private SituationTriggerInstanceRepository repo;
        @Autowired
        private IPlanInvocationEngine planInvocEngine;
        @Autowired
        private CsarStorageService storage;

        public SituationTriggerInstanceObserver(final SituationTriggerInstance instance) {
            this.instance = instance;
        }

        @Override
        public void run() {
            this.instance.setStarted(true);
            this.repo.save(this.instance);
            this.LOG.debug("Started SituationTriggerInstance " + this.instance.getId());

            final String interfaceName = this.instance.getSituationTrigger().getInterfaceName();
            final String operationName = this.instance.getSituationTrigger().getOperationName();
            final Set<SituationTriggerProperty> inputs = this.instance.getSituationTrigger().getInputs();

            long timeAvailableInSeconds = Long.MAX_VALUE;

            for (Situation sit : this.instance.getSituationTrigger().getSituations()) {
                if (sit.getEventTime() != null) {
                    long duration = Long.parseLong(sit.getEventTime());
                    if (duration < timeAvailableInSeconds) {
                        timeAvailableInSeconds = duration;
                    }
                }
            }

            final ServiceTemplateInstance servInstance = this.instance.getSituationTrigger().getServiceInstance();
            final NodeTemplateInstance nodeInstance = this.instance.getSituationTrigger().getNodeInstance();

            if (nodeInstance == null) {

                // plan invocation
                Csar csar = storage.findById(this.instance.getSituationTrigger().getCsarId());

                TExportedOperation op = null;
                try {
                    op = ToscaEngine.resolveBoundaryDefinitionOperation(csar.entryServiceTemplate(),
                        interfaceName, operationName);
                } catch (NotFoundException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

                // get info about current plan

                final TPlan plan = (TPlan) op.getPlan().getPlanRef();

                final TPlanDTO planDTO = new TPlanDTO(plan, csar.entryServiceTemplate().getTargetNamespace());

                final long calculatedTimeFromPreviousExecutions = Long.parseLong(
                    plan.getOtherAttributes().getOrDefault(new QName("http://opentosca.org"), String.valueOf(0)));

                if (calculatedTimeFromPreviousExecutions > 0) {
                    // check if time is shorter than timeAvailable
                    if (calculatedTimeFromPreviousExecutions > timeAvailableInSeconds) {
                        this.LOG.info("Update (WCET = %d ms) not completable in timeframe of %d ms. Aborting.",
                            calculatedTimeFromPreviousExecutions, timeAvailableInSeconds);
                        return;
                    } else {
                        this.LOG.info("Update (WCET = %d ms) is completable in timeframe of %d ms. Executing.",
                            calculatedTimeFromPreviousExecutions, timeAvailableInSeconds);
                    }
                }

                this.LOG.debug("Time: " + calculatedTimeFromPreviousExecutions);

                for (final TParameterDTO param : planDTO.getInputParameters()) {
                    if (servInstance != null && param.getName().equals("OpenTOSCAContainerAPIServiceInstanceURL")) {
                        String url = Settings.CONTAINER_INSTANCEDATA_API + "/" + servInstance.getId();
                        url = url.replace("{csarid}", csar.id().csarName());
                        url = url.replace("{servicetemplateid}", UriComponent
                            .encode(servInstance.getTemplateId(), UriComponent.Type.PATH_SEGMENT));

                        final URI uri = URI.create(UriComponent.encode(url, UriComponent.Type.PATH));
                        param.setValue(uri.toString());
                    }

                    if (param.getValue() == null) {
                        for (final SituationTriggerProperty val : inputs) {
                            if (param.getName().equals(val.getName())) {
                                param.setValue(val.getValue());
                            }
                        }
                    }
                }

                try {
                    final String correlationId = planInvocEngine.createCorrelationId();
                    // FIXME QName natural key migration to string leftover
                    if (servInstance != null) {
                        planInvocEngine.invokePlan(instance.getSituationTrigger().getCsarId(), new QName(csar.entryServiceTemplate().getTargetNamespace(), csar.entryServiceTemplate().getId()), servInstance.getId(),
                            planDTO, correlationId);
                    } else {
                        planInvocEngine.invokePlan(instance.getSituationTrigger().getCsarId(), new QName(csar.entryServiceTemplate().getTargetNamespace(), csar.entryServiceTemplate().getId()), -1,
                            planDTO, correlationId);
                    }

                    // now wait for finished execution
                    PlanInstance planInstance = planRepository.findByCorrelationId(correlationId);
                    while (!(planInstance.getState() == PlanInstanceState.FINISHED)
                        || planInstance.getState() == PlanInstanceState.FAILED) {
                        Thread.sleep(10000);
                        planInstance = planRepository.findByCorrelationId(correlationId);
                    }

                    // plan finished, write output to trigger instance
                    planInstance.getOutputs().forEach(x -> instance.getOutputs()
                        .add(new SituationTriggerInstanceProperty(x.getName(), x.getValue(), x.getType())));

                    instance.setFinished(true);
                    repo.save(instance);
                } catch (final InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
