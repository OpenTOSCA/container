package org.opentosca.container.core.next.repository;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.opentosca.container.core.next.model.Situation;
import org.opentosca.container.core.next.model.SituationsMonitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SituationsMonitorRepository extends JpaRepository<SituationsMonitor, Long> {

    default Collection<SituationsMonitor> findSituationMonitorsBySituationId(Long situationId) {
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
}
