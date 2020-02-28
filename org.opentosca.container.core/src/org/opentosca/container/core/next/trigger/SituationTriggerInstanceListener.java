package org.opentosca.container.core.next.trigger;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.persistence.PostPersist;
import javax.xml.namespace.QName;

import org.glassfish.jersey.uri.UriComponent;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.engine.IToscaEngineService;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.PlanInstance;
import org.opentosca.container.core.next.model.PlanInstanceEvent;
import org.opentosca.container.core.next.model.PlanInstanceState;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.model.SituationTriggerInstance;
import org.opentosca.container.core.next.model.SituationTriggerInstanceProperty;
import org.opentosca.container.core.next.model.SituationTriggerProperty;
import org.opentosca.container.core.next.repository.PlanInstanceRepository;
import org.opentosca.container.core.next.repository.SituationTriggerInstanceRepository;
import org.opentosca.container.core.service.IPlanInvocationEngine;
import org.opentosca.container.core.tosca.extension.TParameterDTO;
import org.opentosca.container.core.tosca.extension.TPlanDTO;
import org.opentosca.container.core.tosca.model.TPlan;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class SituationTriggerInstanceListener {

    private static final List<SituationTriggerInstanceObserver> obs = Lists.newArrayList();

    private static Map<String, List<String>> planToOperationMap = new HashMap<>();

    @PostPersist
    public void startSituationTriggerInstanceObserver(final SituationTriggerInstance instance) {
        final SituationTriggerInstanceObserver obs = new SituationTriggerInstanceObserver(instance);
        SituationTriggerInstanceListener.obs.add(obs);
        new Thread(obs).start();
    }

    private class SituationTriggerInstanceObserver implements Runnable {

        final private Logger LOG = LoggerFactory.getLogger(SituationTriggerInstanceObserver.class);

        private final SituationTriggerInstanceRepository repo = new SituationTriggerInstanceRepository();

        private final IPlanInvocationEngine planInvocEngine;

        private final IToscaEngineService toscaEngine;

        private final PlanInstanceRepository planRepository = new PlanInstanceRepository();

        private final SituationTriggerInstance instance;


        public SituationTriggerInstanceObserver(final SituationTriggerInstance instance) {
            this.instance = instance;
            final BundleContext ctx = org.opentosca.container.core.Activator.getContext();

            ServiceReference<?> ref = ctx.getServiceReference(IPlanInvocationEngine.class.getName());
            this.planInvocEngine = (IPlanInvocationEngine) ctx.getService(ref);

            ref = ctx.getServiceReference(IToscaEngineService.class.getName());
            this.toscaEngine = (IToscaEngineService) ctx.getService(ref);
        }

        @Override
        public void run() {

            this.instance.setStarted(true);
            this.repo.update(this.instance);            

            this.LOG.debug("Started SituationTriggerInstance " + this.instance.getId());
            

            final String interfaceName = this.instance.getSituationTrigger().getInterfaceName();
            final String operationName = this.instance.getSituationTrigger().getOperationName();
            final Set<SituationTriggerProperty> inputs = this.instance.getSituationTrigger().getInputs();
            final Long timeAvailableInSeconds = this.instance.getSituationTrigger().getTimeAvailableInSeconds();

            final ServiceTemplateInstance servInstance = this.instance.getSituationTrigger().getServiceInstance();
            final NodeTemplateInstance nodeInstance = this.instance.getSituationTrigger().getNodeInstance();

            if (nodeInstance == null) {
                // plan invocation

                // get info about current plan
                final QName planId = this.toscaEngine.getToscaReferenceMapper()
                                                     .getBoundaryPlanOfCSARInterface(this.instance.getSituationTrigger().getCsarId(),
                                                                                     interfaceName, operationName);
                final TPlan plan = this.toscaEngine.getToscaReferenceMapper()
                                                   .getPlanForCSARIDAndPlanID(this.instance.getSituationTrigger().getCsarId(), planId);

                final TPlanDTO planDTO = new TPlanDTO(plan, planId.getNamespaceURI());



                final long calculatedTimeFromPreviousExecutions = plan.getCalculatedWCET();

                if (calculatedTimeFromPreviousExecutions > 0) {
                    // check if time is shorter than timeAvailable
                    if (calculatedTimeFromPreviousExecutions > timeAvailableInSeconds * 1000) {
                        this.LOG.debug("Update (WCET = %d ms) not completable in timeframe of %d ms. Aborting.",
                                       calculatedTimeFromPreviousExecutions, timeAvailableInSeconds);
                        return;
                    } else {
                        this.LOG.debug("Update (WCET = %d ms) is completable in timeframe of %d ms. Executing.",
                                       calculatedTimeFromPreviousExecutions, timeAvailableInSeconds);
                    }
                }


                this.LOG.debug("Time: " + calculatedTimeFromPreviousExecutions);



                for (final TParameterDTO param : planDTO.getInputParameters().getInputParameter()) {
                    if (servInstance != null && param.getName().equals("OpenTOSCAContainerAPIServiceInstanceURL")) {
                        String url = Settings.CONTAINER_INSTANCEDATA_API + "/" + servInstance.getId();
                        url = url.replace("{csarid}", servInstance.getCsarId().getFileName());
                        url = url.replace("{servicetemplateid}",
                                          UriComponent.encode(servInstance.getTemplateId().toString(),
                                                              UriComponent.Type.PATH_SEGMENT));

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

                    final String correlationId = this.planInvocEngine.createCorrelationId();
                    if(servInstance != null) {                        
                        this.planInvocEngine.invokePlan(servInstance.getCsarId(), servInstance.getTemplateId(),
                                                        servInstance.getId(), planDTO, correlationId);
                    } else {                        
                        this.planInvocEngine.invokePlan(this.instance.getSituationTrigger().getCsarId(), this.toscaEngine.getServiceTemplatesInCSAR(this.instance.getSituationTrigger().getCsarId()).get(0),
                                                        -1, planDTO, correlationId);
                    }
                    

                    // now wait for finished execution
                    PlanInstance planInstance = this.planRepository.findByCorrelationId(correlationId);
                    while (!(planInstance.getState().equals(PlanInstanceState.FINISHED)
                        || planInstance.getState().equals(PlanInstanceState.FAILED))) {
                        Thread.sleep(10000);
                        planInstance = this.planRepository.findByCorrelationId(correlationId);
                    }

                    // plan finished, write output to trigger instance
                    planInstance.getOutputs()
                                .forEach(x -> this.instance.getOutputs().add(new SituationTriggerInstanceProperty(
                                    x.getName(), x.getValue(), x.getType())));

                    this.instance.setFinished(true);
                    this.repo.update(this.instance);
                }

                catch (final UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
                catch (final InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else {
                // IA invocation
            }

        }

    }
    
    private boolean isPlanExecutionFinished(final TPlanDTO plan, final String correlationId) {

        for (final TParameterDTO param : plan.getOutputParameters().getOutputParameter()) {
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
     * calculate the WCET for the given Plan by summing up operation times in plan. Does not regard
     * parallel executions.
     *
     * @param plan
     * @return
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
     * iterate through all PlanInstanceEvents of a PlanInstance and compare with matching operation from
     * current Plan
     *
     * @param longestDurationMap
     * @param allOperationsInPlan
     * @param currInstance
     */
    private void iterateInstanceEventsForExecutionTimes(final Map<String, Long> longestDurationMap,
                                                        final List<String> allOperationsInPlan,
                                                        final PlanInstance currInstance) {
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
     * if operation already contained in map, check if current execution duration is larger (replace) or
     * smaller (leave)
     *
     * @param longestDurationMap
     * @param aEvent
     */
    private void checkIfCurrentOperationExecutionTimeIsLonger(final Map<String, Long> longestDurationMap,
                                                              final PlanInstanceEvent aEvent) {
        // key already exists in map
        if (longestDurationMap.containsKey(aEvent.getNodeTemplateID() + aEvent.getOperationName())) {
            if (longestDurationMap.get(aEvent.getNodeTemplateID()
                + aEvent.getOperationName()) < aEvent.getExecutionDuration()) {
                longestDurationMap.put(aEvent.getNodeTemplateID() + aEvent.getOperationName(),
                                       aEvent.getExecutionDuration());
            }
        } else {
            longestDurationMap.put(aEvent.getNodeTemplateID() + aEvent.getOperationName(),
                                   aEvent.getExecutionDuration());
        }
    }
}
