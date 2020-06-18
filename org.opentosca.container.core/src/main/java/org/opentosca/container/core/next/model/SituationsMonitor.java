package org.opentosca.container.core.next.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = SituationsMonitor.TABLE_NAME)
public class SituationsMonitor extends PersistenceObject {

    private static final long serialVersionUID = 6770816160173767058L;

    public static final String TABLE_NAME = "SITUATIONSMONITOR";

    @Column(name = "NODESITUATIONMAPPING")
    @OneToMany
    private Set<SituationsMonitorIdCollection> node2situations = new HashSet<>();

    @OneToOne
    @JoinColumn(name = "SERVICE_TEMPLATE_INSTANCE_ID", nullable = true)
    private ServiceTemplateInstance serviceInstance;

    public Map<String, Collection<Long>> getNode2Situations() {
        return node2situations.stream()
            .collect(Collectors.toMap(SituationsMonitorIdCollection::getNodeId, SituationsMonitorIdCollection::getIds));
    }

    public void setNode2Situations(Map<String, Collection<Long>> node2situations) {
        this.node2situations = node2situations.entrySet().stream()
            .collect(Collectors.mapping(e -> {
                SituationsMonitorIdCollection collection = new SituationsMonitorIdCollection();
                collection.setNodeId(e.getKey());
                collection.setIds(e.getValue());
                return collection;
            }, Collectors.toSet()));
    }

    public ServiceTemplateInstance getServiceInstance() {
        return serviceInstance;
    }

    public void setServiceInstance(ServiceTemplateInstance serviceInstance) {
        this.serviceInstance = serviceInstance;
    }
}
