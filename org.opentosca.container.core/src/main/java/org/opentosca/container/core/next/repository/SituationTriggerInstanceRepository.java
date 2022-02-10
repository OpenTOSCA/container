package org.opentosca.container.core.next.repository;

import java.util.List;

import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.opentosca.container.core.next.model.SituationTriggerInstance;

public class SituationTriggerInstanceRepository extends JpaRepository2<SituationTriggerInstance> {

    public SituationTriggerInstanceRepository() {
        super(SituationTriggerInstance.class);
    }

    public List<SituationTriggerInstance> findBySituationTriggerId(final Long situationTriggerId) {
        final List<SituationTriggerInstance> result = Lists.newArrayList();
        findAll().forEach(x -> {
            if (x.getSituationTrigger().getId().equals(situationTriggerId)) {
                result.add(x);
            }
        });
        return result;
    }

    @Override
    protected void initializeInstance(SituationTriggerInstance instance) {
        Hibernate.initialize(instance.getOutputs());
    }
}
