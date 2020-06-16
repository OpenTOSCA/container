package org.opentosca.container.core.next.repository;

import org.opentosca.container.core.next.model.Situation;

public class SituationRepository extends JpaRepository<Situation> {

    public SituationRepository() {
        super(Situation.class);
    }

    @Override
    protected void initializeInstance(Situation instance) {
        // no dependent bags or other stuff to be fetched
    }
}
