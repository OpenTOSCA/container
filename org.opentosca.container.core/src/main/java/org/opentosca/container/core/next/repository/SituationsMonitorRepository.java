package org.opentosca.container.core.next.repository;

import java.util.Collection;

import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.opentosca.container.core.next.model.SituationsMonitor;

public class SituationsMonitorRepository extends JpaRepository<SituationsMonitor> {

    public SituationsMonitorRepository() {
        super(SituationsMonitor.class);
    }

    public Collection<SituationsMonitor> findSituationMonitorsBySituationId(Long situationId) {
        Collection<SituationsMonitor> result = Lists.newArrayList();
        for (SituationsMonitor moni : this.findAll()) {
            for (Collection<Long> sits : moni.getNode2Situations().values()) {
                if (sits.contains(situationId)) {
                    result.add(moni);
                }
            }
        }
        return result;
    }

    @Override
    protected void initializeInstance(SituationsMonitor instance) {
        Hibernate.initialize(instance.getNode2Situations());
    }
}
