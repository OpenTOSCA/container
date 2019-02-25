package org.opentosca.container.core.next.repository;

import java.util.List;

import org.opentosca.container.core.next.model.SituationTriggerInstance;

import com.google.common.collect.Lists;

public class SituationTriggerInstanceRepository extends JpaRepository<SituationTriggerInstance> {

    public SituationTriggerInstanceRepository() {
        super(SituationTriggerInstance.class);
    }

    public List<SituationTriggerInstance> findBySituationTriggerId(final Long situationTriggerId) {
        final List<SituationTriggerInstance> result = Lists.newArrayList();

        findAll().forEach(x -> {
            if (x.getSituationTrigger().getId() == situationTriggerId) {
                result.add(x);
            }
        });

        return result;
    }

}
