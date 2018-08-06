package org.opentosca.container.core.next.repository;

import java.util.List;

import org.opentosca.container.core.next.model.Situation;
import org.opentosca.container.core.next.model.SituationTrigger;

import com.google.common.collect.Lists;

public class SituationTriggerRepository extends JpaRepository<SituationTrigger> {

    public SituationTriggerRepository() {
        super(SituationTrigger.class);
    }

    public List<SituationTrigger> findSituationTriggersBySituationId(final Long situationId) {
        final List<SituationTrigger> result = Lists.newArrayList();

        findAll().forEach(x -> {
            for (final Situation situation : x.getSituations()) {
                if (situation.getId() == situationId) {
                    result.add(x);
                }
            }
        });

        return result;
    }

}
