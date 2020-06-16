package org.opentosca.container.core.next.model;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.Collection;

// FIXME this is a messy workaround for embedding a collection as the value type of an @ElementCollection
@Entity
public class SituationsMonitorIdCollection {
  @Id
  @Column
  private String nodeId;

  @Column
  @ElementCollection
  private Collection<Long> ids = new ArrayList<>();

  public String getNodeId() {
    return nodeId;
  }

  public void setNodeId(String nodeId) {
    this.nodeId = nodeId;
  }

  public Collection<Long> getIds() {
    return ids;
  }

  public void setIds(Collection<Long> ids) {
    this.ids = ids;
  }
}
