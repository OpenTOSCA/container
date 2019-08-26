package org.opentosca.container.core.next.trigger;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.persistence.PostLoad;
import javax.persistence.PostUpdate;
import javax.persistence.PreUpdate;

import org.opentosca.container.core.next.model.Situation;
import org.opentosca.container.core.next.model.SituationTrigger;
import org.opentosca.container.core.next.model.SituationTriggerInstance;
import org.opentosca.container.core.next.model.SituationsMonitor;
import org.opentosca.container.core.next.repository.SituationRepository;
import org.opentosca.container.core.next.repository.SituationTriggerInstanceRepository;
import org.opentosca.container.core.next.repository.SituationTriggerRepository;
import org.opentosca.container.core.next.repository.SituationsMonitorRepository;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author kalmankepes
 *
 */
public class SituationListener {

    final private static Logger LOG = LoggerFactory.getLogger(SituationListener.class);

    final SituationRepository sitRepo = new SituationRepository();

    final SituationTriggerRepository sitTrigRepo = new SituationTriggerRepository();

    final SituationTriggerInstanceRepository sitTrigInstRepo = new SituationTriggerInstanceRepository();

    final SituationsMonitorRepository sitMonRepo = new SituationsMonitorRepository();

    @PostUpdate
    void situationAfterUpdate(final Situation situation) {
        Collection<SituationsMonitor> monis = this.sitMonRepo.findSituationMonitorsBySituationId(situation.getId());

        for (SituationsMonitor moni : monis) {
            this.sendServiceInstanceAdaptationEvent(moni);
        }
    }

    @PreUpdate
    void situationBeforeUpdate(final Situation situation) {
        LOG.info("Updating situation with template " + situation.getSituationTemplateId() + " and thing "
            + situation.getThingId() + " with active state " + situation.isActive());

        final Situation sitInRepo = this.sitRepo.find(situation.getId()).get();

        if (situation.isActive() == sitInRepo.isActive()) {
            // nothing changed => do nothing
            return;
        } else {

            // handling triggers on situation changes
            final List<SituationTrigger> triggers =
                this.sitTrigRepo.findSituationTriggersBySituationId(situation.getId());
            final List<SituationTriggerInstance> newInstances = Lists.newArrayList();

            if (situation.isActive()) {
                // fetch triggers that must be triggered on activation
                triggers.forEach(x -> {
                    if (x.isTriggerOnActivation()) {


                        boolean allActive = true;

                        for (Situation sit : x.getSituations()) {
                            if(!sit.equals(situation)) {                                
                                allActive &= sit.isActive();
                            }
                        }

                        if (allActive) {


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

                    }
                });

            } else {
                // fetch triggers that must kicked of on deactivation
                triggers.forEach(x -> {
                    if (!x.isTriggerOnActivation()) {

                        boolean allInactive = false;

                        for (Situation sit : x.getSituations()) {
                            allInactive &= sit.isActive();
                        }

                        if (!allInactive) {


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
                    }
                });
            }
            this.sitTrigInstRepo.add(newInstances);
        }
    }

    private void sendServiceInstanceAdaptationEvent(SituationsMonitor monitor) {
        final Map<String, Object> eventProperties = Maps.newHashMap();
        eventProperties.put("SERVICEINSTANCE", monitor.getServiceInstance());
        // eventProperties.put("SITUATIONS", monitor.getSituations());
        eventProperties.put("NODE2SITUATIONS", monitor.getNode2Situations());


        Event situationAdaptationEvent = new Event("org_opentosca_situationadaptation/requests", eventProperties);
        this.getEventAdminService().postEvent(situationAdaptationEvent);
    }



    private EventAdmin getEventAdminService() {
        BundleContext ctx = org.opentosca.container.core.Activator.getContext();
        ServiceReference<?> ref = ctx.getServiceReference(EventAdmin.class.getName());
        EventAdmin eventAdmin = (EventAdmin) ctx.getService(ref);
        return eventAdmin;
    }

}
