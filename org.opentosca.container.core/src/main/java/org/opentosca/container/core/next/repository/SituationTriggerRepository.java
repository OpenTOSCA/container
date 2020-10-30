package org.opentosca.container.core.next.repository;

import java.util.List;

import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.opentosca.container.core.next.model.AggregatedSituation;
import org.opentosca.container.core.next.model.Situation;
import org.opentosca.container.core.next.model.SituationTrigger;

public class SituationTriggerRepository extends JpaRepository<SituationTrigger> {

    public SituationTriggerRepository() {
        super(SituationTrigger.class);
    }

    public List<SituationTrigger> findSituationTriggersBySituationId(final Long situationId) {
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
    
    public List<SituationTrigger> findSituationTriggersByAggregatedSituationId(final Long aggrSituationId) {
        final List<SituationTrigger> result = Lists.newArrayList();
        findAll().forEach(x -> {
            for (final AggregatedSituation situation : x.getAggregatedSituations()) {
                if (situation.getId().equals(aggrSituationId)) {
                    result.add(x);
                }
            }
        });
        return result;
    }

    @Override
    protected void initializeInstance(SituationTrigger instance) {
        Hibernate.initialize(instance.getInputs());
        Hibernate.initialize(instance.getSituations());
        //Hibernate.initialize(instance.getAggregatedSituations());
        Hibernate.initialize(instance.getSituationTriggerInstances());
    }
}
