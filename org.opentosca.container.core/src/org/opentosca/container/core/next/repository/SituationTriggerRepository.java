package org.opentosca.container.core.next.repository;

import org.opentosca.container.core.next.model.SituationTrigger;

public class SituationTriggerRepository extends JpaRepository<SituationTrigger> {

    public SituationTriggerRepository() {
        super(SituationTrigger.class);
    }

}
