package org.opentosca.container.core.next.repository;

import org.opentosca.container.core.next.model.SituationTriggerInstance;

public class SituationTriggerInstanceRepository extends JpaRepository<SituationTriggerInstance> {

    public SituationTriggerInstanceRepository() {
        super(SituationTriggerInstance.class);
    }

}
