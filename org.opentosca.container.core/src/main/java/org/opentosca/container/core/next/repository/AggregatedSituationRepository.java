package org.opentosca.container.core.next.repository;

import java.util.List;
import com.google.common.collect.Lists;
import org.opentosca.container.core.next.model.Situation;
import org.hibernate.Hibernate;
import org.opentosca.container.core.next.model.AggregatedSituation;

public class AggregatedSituationRepository extends JpaRepository<AggregatedSituation> {

    public AggregatedSituationRepository() {
        super(AggregatedSituation.class);
    }

    public List<AggregatedSituation> findAggregatedSituationIdsBySituationId(final Long situationId) {
        final List<AggregatedSituation> result = Lists.newArrayList();
        findAll().forEach(x -> {
            for (final Situation situation : x.getSituations()) {
                if (situation.getId().equals(situationId)) {
                    result.add(x);
                }
            }
        });
        return result;
    }

    @Override
    protected void initializeInstance(AggregatedSituation instance) {
    	//Hibernate.initialize(instance.getSituations());
    }
}