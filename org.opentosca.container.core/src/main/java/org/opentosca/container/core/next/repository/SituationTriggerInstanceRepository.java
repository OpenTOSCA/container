package org.opentosca.container.core.next.repository;

import java.util.List;

import com.google.common.collect.Lists;
import org.opentosca.container.core.next.model.SituationTriggerInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SituationTriggerInstanceRepository extends JpaRepository<SituationTriggerInstance, Long> {

    default List<SituationTriggerInstance> findBySituationTriggerId(final Long situationTriggerId) {
        final List<SituationTriggerInstance> result = Lists.newArrayList();
        findAll().forEach(x -> {
            if (x.getSituationTrigger().getId().equals(situationTriggerId)) {
                result.add(x);
            }
        });
        return result;
    }
}
