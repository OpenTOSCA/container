package org.opentosca.container.core.next.repository;

import java.util.Collection;import java.util.stream.Collectors;

import org.opentosca.container.core.next.model.SituationsMonitor;

import com.google.common.collect.Lists;

public class SituationsMonitorRepository extends JpaRepository<SituationsMonitor> {

    public SituationsMonitorRepository() {
        super(SituationsMonitor.class);
    }
    
    public Collection<SituationsMonitor> findSituationMonitorsBySituationId(Long situationId) {
        Collection<SituationsMonitor> result = Lists.newArrayList();                
        
        for(SituationsMonitor moni : this.findAll()) {
           for(Collection<Long> sits:moni.getNode2Situations().values()) {
               if(sits.contains(situationId)) {
                   result.add(moni);
               }
           }
        }
        
        return result;
    }

}
