package org.opentosca.container.core.next.model;

import java.util.*;
import java.util.stream.Collectors;

import javax.persistence.*;

@Entity
@Table(name = SituationsMonitor.TABLE_NAME)
public class SituationsMonitor extends PersistenceObject {

  private static final long serialVersionUID = 6770816160173767058L;

  public static final String TABLE_NAME = "SITUATIONSMONITOR";


  @Column(name = "NODESITUATIONMAPPING")
  @ElementCollection
  private Map<String, IdCollection> node2situations = new HashMap<>();

  @OneToOne
  @JoinColumn(name = "SERVICE_TEMPLATE_INSTANCE_ID", nullable = true)
  private ServiceTemplateInstance serviceInstance;

  public Map<String, Collection<Long>> getNode2Situations() {
    return node2situations.entrySet().stream()
      .collect(Collectors.toMap(Map.Entry::getKey, (Map.Entry<String, IdCollection> e) -> e.getValue().getIds()));
  }

  public void setNode2Situations(Map<String, Collection<Long>> node2situations) {
    this.node2situations = node2situations.entrySet().stream()
      .collect(Collectors.toMap(Map.Entry::getKey, e -> {
        IdCollection collection = new IdCollection();
        collection.setIds(e.getValue());
        return collection;
      }));
  }

  public ServiceTemplateInstance getServiceInstance() {
    return serviceInstance;
  }

  public void setServiceInstance(ServiceTemplateInstance serviceInstance) {
    this.serviceInstance = serviceInstance;
  }

  // FIXME this is a messy workaround for embedding a collection as the value type of an @ElementCollection
  @Embeddable
  class IdCollection {
    @Column
    @ElementCollection
    private Collection<Long> ids = new ArrayList<>();

    public Collection<Long> getIds() {
      return ids;
    }

    public void setIds(Collection<Long> ids) {
      this.ids = ids;
    }
  }
}
