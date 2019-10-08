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
import org.opentosca.container.core.model.instance.ServiceTemplateInstanceID;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.PlanInstance;
import org.opentosca.container.core.next.model.PlanInstanceEvent;
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

    // public SituationTriggerInstanceListener(Map<String, List<String>> map) {
    // this.planToOperationMap = map;
    // }

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
            final Long wcetInSeconds = this.instance.getSituationTrigger().getWcetInSeconds();

            final ServiceTemplateInstance servInstance = this.instance.getSituationTrigger().getServiceInstance();
            final NodeTemplateInstance nodeInstance = this.instance.getSituationTrigger().getNodeInstance();



            if (nodeInstance == null) {
                // plan invocation
                if (wcetInSeconds > timeAvailableInSeconds) {
                    System.out.printf("Update (WCET = %d s) not completable in timeframe of %d s. Aborting.",
                                      wcetInSeconds, timeAvailableInSeconds);
                    return;
                } else {
                    System.out.printf("Update (WCET = %d s) is completable in timeframe of %d s. Executing.",
                                      wcetInSeconds, timeAvailableInSeconds);
                }

                long calculatedTimeFromPreviousExecutions = 0;

                // contains mapping of Planname to its contained operations
                final Map<String, List<String>> planNameToOperationsMap = getPlanToOperationMap();

                // get info about current plan
                final QName planId = this.toscaEngine.getToscaReferenceMapper()
                                                     .getBoundaryPlanOfCSARInterface(servInstance.getCsarId(),
                                                                                     interfaceName, operationName);
                final TPlan plan = this.toscaEngine.getToscaReferenceMapper()
                                                   .getPlanForCSARIDAndPlanID(servInstance.getCsarId(), planId);

                final TPlanDTO planDTO = new TPlanDTO(plan, planId.getNamespaceURI());

                // find all operations contained in current plan
                final List<String> allOperationsInPlan = planNameToOperationsMap.get(plan.getId());

                // get all previously completed operations from DB
                final PlanInstanceRepository planRepo = new PlanInstanceRepository();
                final Collection<PlanInstance> allOccurences = planRepo.findAll();

                if (allOperationsInPlan != null) {
                    // iterate through all InstanceEvents searching for execution times of previous events for same Plan
                    for (final PlanInstance currInstance : allOccurences) {
                        if (currInstance.getTemplateId().getLocalPart().equals(plan.getId())) {
                            for (final String oneOperation : allOperationsInPlan) {
                                for (final PlanInstanceEvent aEvent : currInstance.getEvents()) {
                                    if (Objects.nonNull(aEvent.getOperationName())
                                        && Objects.nonNull(aEvent.getExecutionDuration())) {
                                        if (aEvent.getOperationName().equals(oneOperation)) {
                                            calculatedTimeFromPreviousExecutions += aEvent.getExecutionDuration();
                                            // TODO: Prevent adding time up when same plan has already been executed 2
                                            // times
                                        }
                                    }
                                }
                            }
                        }
                    }
                }



                for (final TParameterDTO param : planDTO.getInputParameters().getInputParameter()) {
                    if (param.getName().equals("OpenTOSCAContainerAPIServiceInstanceURL")) {
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
                    final String correlationId =
                        this.planInvocEngine.invokePlan(servInstance.getCsarId(), servInstance.getTemplateId(),
                                                        servInstance.getId(), planDTO);

                    // now wait for finished execution


                    final ServiceTemplateInstanceID servInstanceId = new ServiceTemplateInstanceID(
                        servInstance.getCsarId(), servInstance.getTemplateId(), servInstance.getId().intValue());

                    TPlanDTO runningPlan =
                        this.planInvocEngine.getActivePublicPlanOfInstance(servInstanceId, correlationId);

                    while (!isPlanExecutionFinished(runningPlan, correlationId)) {
                        Thread.sleep(10000);
                        runningPlan = this.planInvocEngine.getActivePublicPlanOfInstance(servInstanceId, correlationId);
                    }


                    // plan finished, write output to triggerinstance

                    runningPlan.getOutputParameters().getOutputParameter()
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

        private boolean isPlanExecutionFinished(final TPlanDTO plan, final String correlationId) {

            for (final TParameterDTO param : plan.getOutputParameters().getOutputParameter()) {
                if (param.getName().equalsIgnoreCase("correlationid") && param.getValue() != null
                    && param.getValue().equals(correlationId)) {
                    return true;
                }
            }

            return false;
        }

    }

    public Map<String, List<String>> getPlanToOperationMap() {
        return this.planToOperationMap;
    }

    public void setPlanToOperationMap(Map<String, List<String>> planToOperationMap) {
        this.planToOperationMap = planToOperationMap;
    }

}
