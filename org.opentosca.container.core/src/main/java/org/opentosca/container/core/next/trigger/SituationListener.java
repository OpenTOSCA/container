package org.opentosca.container.core.next.trigger;

import java.util.List;

import javax.persistence.PreUpdate;

import org.opentosca.container.core.next.model.Situation;
import org.opentosca.container.core.next.model.SituationTrigger;
import org.opentosca.container.core.next.model.SituationTriggerInstance;
import org.opentosca.container.core.next.repository.SituationRepository;
import org.opentosca.container.core.next.repository.SituationTriggerInstanceRepository;
import org.opentosca.container.core.next.repository.SituationTriggerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/**
 * @author kalmankepes
 */
public class SituationListener {

  final private static Logger LOG = LoggerFactory.getLogger(SituationListener.class);

  final SituationRepository sitRepo = new SituationRepository();

  final SituationTriggerRepository sitTrigRepo = new SituationTriggerRepository();

  final SituationTriggerInstanceRepository sitTrigInstRepo = new SituationTriggerInstanceRepository();

  @PreUpdate
  void situationBeforeUpdate(final Situation situation) {
    LOG.info("Updating situation with template " + situation.getSituationTemplateId() + " and thing "
      + situation.getThingId() + " with active state " + situation.isActive());

    final Situation sitInRepo = this.sitRepo.find(situation.getId()).get();

    if (situation.isActive() == sitInRepo.isActive()) {
      // nothing changed => do nothing
      return;
    } else {
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
      this.sitTrigInstRepo.add(newInstances);
    }
  }

}
