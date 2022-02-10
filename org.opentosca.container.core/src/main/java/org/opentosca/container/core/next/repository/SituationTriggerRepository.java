package org.opentosca.container.core.next.repository;

import java.util.List;

import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.opentosca.container.core.next.model.Situation;
import org.opentosca.container.core.next.model.SituationTrigger;

public class SituationTriggerRepository extends JpaRepository2<SituationTrigger> {

    public SituationTriggerRepository() {
        super(SituationTrigger.class);
    }

    public List<SituationTrigger> findSituationTriggersBySituationId(final Long situationId) {
        final List<SituationTrigger> result = Lists.newArrayList();
        findAll().forEach(x -> {
            for (final Situation situation : x.getSituations()) {
                if (situation.getId().equals(situationId)) {
                    this.initializeInstance(x);
                    result.add(x);
                }
            }
        });
        return result;
    }

    @Override
    protected void initializeInstance(SituationTrigger instance) {
        Hibernate.initialize(instance);
        Hibernate.initialize(instance.getInputs());
        Hibernate.initialize(instance.getSituations());
        Hibernate.initialize(instance.getSituationTriggerInstances());
    }
}
