package org.opentosca.container.core.next.repository;

import java.util.List;

import com.google.common.collect.Lists;
import org.opentosca.container.core.next.model.Situation;
import org.opentosca.container.core.next.model.SituationTrigger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SituationTriggerRepository extends JpaRepository<SituationTrigger, Long> {

    default List<SituationTrigger> findSituationTriggersBySituationId(final Long situationId) {
        final List<SituationTrigger> result = Lists.newArrayList();
        findAll().forEach(x -> {
            for (final Situation situation : x.getSituations()) {
                if (situation.getId().equals(situationId)) {
                    result.add(x);
                }
            }
        });
        return result;
    }
}
