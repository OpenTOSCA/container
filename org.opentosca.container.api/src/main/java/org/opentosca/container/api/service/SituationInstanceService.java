package org.opentosca.container.api.service;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.model.Situation;
import org.opentosca.container.core.next.model.SituationTrigger;
import org.opentosca.container.core.next.model.SituationTriggerInstance;
import org.opentosca.container.core.next.model.SituationTriggerProperty;
import org.opentosca.container.core.next.model.SituationsMonitor;
import org.opentosca.container.core.next.repository.SituationRepository;
import org.opentosca.container.core.next.repository.SituationTriggerInstanceRepository;
import org.opentosca.container.core.next.repository.SituationTriggerRepository;
import org.opentosca.container.core.next.repository.SituationsMonitorRepository;
import org.springframework.stereotype.Service;

/**
 * Allows access to information about situations
 */
@Service
public class SituationInstanceService {

    private final SituationRepository sitRepo;
    private final SituationTriggerRepository sitTrig;
    private final SituationTriggerInstanceRepository sitTrigInst;
    private final SituationsMonitorRepository situationsMonitorRepo;

    @Inject
    public SituationInstanceService(SituationRepository sitRepo, SituationTriggerRepository sitTrig,
                                    SituationTriggerInstanceRepository sitTrigInst,
                                    SituationsMonitorRepository situationsMonitorRepo) {
        this.sitRepo = sitRepo;
        this.sitTrig = sitTrig;
        this.sitTrigInst = sitTrigInst;
        this.situationsMonitorRepo = situationsMonitorRepo;
    }

    public Situation createNewSituation(final String thingId, final String situationTemplateId, final boolean active,
                                        final float eventProbability, final String eventTime) {
        final Situation newInstance = new Situation();

        newInstance.setSituationTemplateId(situationTemplateId);
        newInstance.setThingId(thingId);
        newInstance.setActive(active);
        newInstance.setEventProbability(eventProbability);
        newInstance.setEventTime(eventTime);

        this.sitRepo.save(newInstance);

        return newInstance;
    }

    public Situation getSituation(final Long id) {
        final Optional<Situation> instance = this.sitRepo.findById(id);
        if (instance.isPresent()) {
            return instance.get();
        }
        throw new NotFoundException("Situation <" + id + "> not found.");
    }

    public Collection<Situation> getSituations() {
        return this.sitRepo.findAll();
    }

    public boolean removeSituation(final Long situationId) {
        if (this.sitTrig.findSituationTriggersBySituationId(situationId).isEmpty()) {
            this.sitRepo.findById(situationId).ifPresent(x -> this.sitRepo.delete(x));
            return true;
        }
        return false;
    }

    public Collection<SituationTrigger> getSituationTriggers() {
        return this.sitTrig.findAll();
    }

    public SituationTrigger createNewSituationTrigger(SituationTrigger newInstance,
                                                      final ServiceTemplateInstance serviceInstance,
                                                      final NodeTemplateInstance nodeInstance,
                                                      final Set<SituationTriggerProperty> inputs,
                                                      final float eventProbability, final String eventTime) {
        if (serviceInstance != null) {
            newInstance.setServiceInstance(serviceInstance);
        }
        if (nodeInstance != null) {
            newInstance.setNodeInstance(nodeInstance);
        }

        for (SituationTriggerProperty input : inputs) {
            input.setSituationTrigger(newInstance);
        }

        newInstance.setInputs(inputs);

        if (eventProbability != -1.0f) {
            newInstance.setEventProbability(eventProbability);
        }

        if (eventTime != null) {
            newInstance.setEventTime(eventTime);
        }

        newInstance = this.sitTrig.save(newInstance);

        return newInstance;
    }

    public SituationTrigger getSituationTrigger(final Long id) {
        final Optional<SituationTrigger> opt = this.sitTrig.findById(id);

        if (opt.isPresent()) {
            return opt.get();
        }

        throw new NotFoundException("SituationTrigger <" + id + "> not found.");
    }

    public void removeSituationTrigger(Long situationTriggerId) {

        this.sitTrigInst.deleteAll(this.sitTrigInst.findBySituationTriggerId(situationTriggerId));

        this.sitTrig.findById(situationTriggerId).ifPresent(this.sitTrig::delete);
    }

    public void updateSituation(final Situation situation) {
        this.sitRepo.save(situation);
    }

    public SituationTriggerInstance getSituationTriggerInstance(final Long id) {
        return this.sitTrigInst.findById(id)
            .orElseThrow(() -> new RuntimeException("SituationTriggerInstance <" + id + "> not found."));
    }

    public SituationsMonitor createNewSituationsMonitor(final ServiceTemplateInstance instance,
                                                        final Map<String, Collection<Long>> situations) {
        SituationsMonitor monitor = new SituationsMonitor();

        monitor.setServiceInstance(instance);

        monitor.setNode2Situations(situations);

        monitor = this.situationsMonitorRepo.save(monitor);
        return monitor;
    }

    public Collection<SituationsMonitor> getSituationsMonitors() {
        return this.situationsMonitorRepo.findAll();
    }

    public Collection<SituationsMonitor> getSituationsMonitors(final Long serviceInstanceID) {
        return this.getSituationsMonitors().stream()
            .filter(monitor -> monitor.getServiceInstance() != null
                && monitor.getServiceInstance().getId().equals(serviceInstanceID))
            .collect(Collectors.toList());
    }
}
