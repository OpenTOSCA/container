package org.opentosca.container.core.next.model;

import java.util.Collection;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;


@Entity
@Table(name = SituationsMonitor.TABLE_NAME)
public class SituationsMonitor extends PersistenceObject {

    private static final long serialVersionUID = 6770816160173767058L;

    public static final String TABLE_NAME = "SITUATIONSMONITOR";
    
    
    @Column(name = "NODESITUATIONMAPPING")
    private Map<String,Collection<Long>> node2situations = Maps.newHashMap();

    @OneToOne
    @JoinColumn(name = "SERVICE_TEMPLATE_INSTANCE_ID", nullable = true)
    private ServiceTemplateInstance serviceInstance;

    public Map<String,Collection<Long>> getNode2Situations() {
        return node2situations;
    }

    public void setNode2Situations(Map<String,Collection<Long>> node2situations) {
        this.node2situations = node2situations;
    }

    public ServiceTemplateInstance getServiceInstance() {
        return serviceInstance;
    }

    public void setServiceInstance(ServiceTemplateInstance serviceInstance) {
        this.serviceInstance = serviceInstance;
    }
}
