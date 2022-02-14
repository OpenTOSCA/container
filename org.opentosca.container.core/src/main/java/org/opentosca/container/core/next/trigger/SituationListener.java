package org.opentosca.container.core.next.trigger;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.PostUpdate;
import javax.persistence.PreUpdate;

import com.google.common.collect.Lists;
import org.opentosca.container.core.engine.management.IManagementBus;
import org.opentosca.container.core.next.model.Situation;
import org.opentosca.container.core.next.model.SituationTrigger;
import org.opentosca.container.core.next.model.SituationTriggerInstance;
import org.opentosca.container.core.next.model.SituationsMonitor;
import org.opentosca.container.core.next.repository.SituationRepository;
import org.opentosca.container.core.next.repository.SituationTriggerInstanceRepository;
import org.opentosca.container.core.next.repository.SituationTriggerRepository;
import org.opentosca.container.core.next.repository.SituationsMonitorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

/**
 * @author kalmankepes
 */
public class SituationListener {

    final private static Logger LOG = LoggerFactory.getLogger(SituationListener.class);
    @Autowired
    private SituationTriggerRepository sitTrigRepo;
    @Autowired
    private SituationTriggerInstanceRepository sitTrigInstRepo;
    @Autowired
    private SituationsMonitorRepository sitMonRepo;
    @Autowired
    private SituationRepository sitRepo;
    // injection crutch to enable managementBus adaption
    @Autowired
    private IManagementBus managementBus;

    @PostUpdate
    void situationAfterUpdate(final Situation situation) {
        Collection<SituationsMonitor> monis = sitMonRepo.findSituationMonitorsBySituationId(situation.getId());

        // this SHOULD inject the managementBus dependency when we use it
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        for (SituationsMonitor moni : monis) {
            sendServiceInstanceAdaptionEvent(moni);
        }
    }

    @PreUpdate
    void situationBeforeUpdate(final Situation situation) {
        LOG.info("Updating situation with template " + situation.getSituationTemplateId() + " and thing "
            + situation.getThingId() + " with active state " + situation.isActive());

        final Situation sitInRepo = this.sitRepo.findById(situation.getId()).get();

        if (situation.isActive() == sitInRepo.isActive()) {
            // nothing changed => do nothing
            return;
        }

        // handling triggers on situation changes
        final List<SituationTrigger> triggers =
            this.sitTrigRepo.findSituationTriggersBySituationId(situation.getId());
        final List<SituationTriggerInstance> newInstances = Lists.newArrayList();

        if (situation.isActive()) {
            // fetch triggers that must be triggered on activation
            triggers.forEach(x -> {
                if (x.isTriggerOnActivation()) {
                    if (!x.isSingleInstance()) {
                        // if this is not a single instance we can just kick of another trigger
                        final SituationTriggerInstance newInstance = new SituationTriggerInstance();
                        newInstance.setSituationTrigger(x);
                        newInstance.setStarted(false);
                        newInstance.setFinished(false);
                        newInstances.add(newInstance);
                    } else {
                        // we have to check if there is already an instance of the trigger

                        final List<SituationTriggerInstance> singleInstanceTriggerInstances =
                            this.sitTrigInstRepo.findBySituationTriggerId(x.getId());

                        int count = 0;

                        for (final SituationTriggerInstance instance : singleInstanceTriggerInstances) {
                            if (instance.isFinished()) {
                                count++;
                            }
                        }

                        if (count == singleInstanceTriggerInstances.size()) {
                            // create new instance
                            final SituationTriggerInstance newInstance = new SituationTriggerInstance();
                            newInstance.setSituationTrigger(x);
                            newInstance.setStarted(false);
                            newInstance.setFinished(false);
                            newInstances.add(newInstance);
                        }
                    }
                }
            });
        } else {
            // fetch triggers that must kicked of on deactivation
            triggers.forEach(x -> {
                if (!x.isTriggerOnActivation()) {
                    if (!x.isSingleInstance()) {
                        // if this is not a single instance we can just kick of another trigger
                        final SituationTriggerInstance newInstance = new SituationTriggerInstance();
                        newInstance.setSituationTrigger(x);
                        newInstance.setStarted(false);
                        newInstance.setFinished(false);
                        newInstances.add(newInstance);
                    } else {
                        // we have to check if there is already an instance of the trigger

                        final List<SituationTriggerInstance> singleInstanceTriggerInstances =
                            this.sitTrigInstRepo.findBySituationTriggerId(x.getId());

                        int count = 0;

                        for (final SituationTriggerInstance instance : singleInstanceTriggerInstances) {
                            if (instance.isFinished()) {
                                count++;
                            }
                        }

                        if (count == singleInstanceTriggerInstances.size()) {
                            // create new instance
                            final SituationTriggerInstance newInstance = new SituationTriggerInstance();
                            newInstance.setSituationTrigger(x);
                            newInstance.setStarted(false);
                            newInstance.setFinished(false);
                            newInstances.add(newInstance);
                        }
                    }
                }
            });
        }
        newInstances.forEach(instance -> this.sitTrigInstRepo.save(instance));
    }

    private void sendServiceInstanceAdaptionEvent(SituationsMonitor monitor) {
        final Map<String, Object> eventProperties = new HashMap<>();
        eventProperties.put("SERVICEINSTANCE", monitor.getServiceInstance());
        eventProperties.put("NODE2SITUATIONS", monitor.getNode2Situations());

        managementBus.situationAdaption(eventProperties);
    }
}
