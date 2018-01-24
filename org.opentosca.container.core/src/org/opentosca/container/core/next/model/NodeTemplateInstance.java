package org.opentosca.container.core.next.model;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.xml.namespace.QName;

import org.eclipse.persistence.annotations.Convert;
import org.opentosca.container.core.next.xml.PropertyParser;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@Entity
@Table(name = NodeTemplateInstance.TABLE_NAME)
public class NodeTemplateInstance extends PersistenceObject {

  private static final long serialVersionUID = 6596755785422340480L;

  public static final String TABLE_NAME = "NODE_TEMPLATE_INSTANCE";

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private NodeTemplateInstanceState state;

  @OrderBy("createdAt DESC")
  @OneToMany(mappedBy = "nodeTemplateInstance", cascade = {CascadeType.ALL})
  private Set<NodeTemplateInstanceProperty> properties = Sets.newHashSet();

  @ManyToOne
  @JoinColumn(name = "SERVICE_TEMPLATE_INSTANCE_ID")
  private ServiceTemplateInstance serviceTemplateInstance;

  @OneToMany(mappedBy = "target")
  private Collection<RelationshipTemplateInstance> incomingRelations = Lists.newArrayList();

  @OneToMany(mappedBy = "source")
  private Collection<RelationshipTemplateInstance> outgoingRelations = Lists.newArrayList();

  @Convert("QNameConverter")
  @Column(name = "TEMPLATE_ID", nullable = false)
  private QName templateId;

  @Convert("QNameConverter")
  @Column(name = "TEMPLATE_TYPE", nullable = false)
  private QName templateType;

  @OrderBy("createdAt DESC")
  @OneToMany(mappedBy = "nodeTemplateInstance", cascade = {CascadeType.ALL})
  private List<VerificationResult> verificationResults = Lists.newArrayList();


  public NodeTemplateInstance() {}


  public NodeTemplateInstanceState getState() {
    return this.state;
  }

  public void setState(final NodeTemplateInstanceState state) {
    this.state = state;
  }

  public Collection<NodeTemplateInstanceProperty> getProperties() {
    return this.properties;
  }

  public void setProperties(final Set<NodeTemplateInstanceProperty> properties) {
    this.properties = properties;
  }

  public void addProperty(final NodeTemplateInstanceProperty property) {
    if (!this.properties.add(property)) {
      this.properties.remove(property);
      this.properties.add(property);
    }
    if (property.getNodeTemplateInstance() != this) {
      property.setNodeTemplateInstance(this);
    }
  }

  /*
   * Currently, the plan writes all properties as one XML document into the database. Therefore, we
   * parse this XML and return a Map<String, String>.
   */
  public Map<String, String> getPropertiesAsMap() {
    Map<String, String> properties = Maps.newHashMap();
    final NodeTemplateInstanceProperty prop =
        this.getProperties().stream().filter(p -> p.getType().equalsIgnoreCase("xml"))
            .collect(Collectors.reducing((a, b) -> null)).orElse(null);
    if (prop != null) {
      final PropertyParser parser = new PropertyParser();
      properties = parser.parse(prop.getValue());
    }
    return properties;
  }

  public ServiceTemplateInstance getServiceTemplateInstance() {
    return this.serviceTemplateInstance;
  }

  public void setServiceTemplateInstance(final ServiceTemplateInstance serviceTemplateInstance) {
    this.serviceTemplateInstance = serviceTemplateInstance;
    if (!serviceTemplateInstance.getNodeTemplateInstances().contains(this)) {
      serviceTemplateInstance.getNodeTemplateInstances().add(this);
    }
  }

  public Collection<RelationshipTemplateInstance> getIncomingRelations() {
    return this.incomingRelations;
  }

  public void setIncomingRelations(
      final Collection<RelationshipTemplateInstance> incomingRelations) {
    this.incomingRelations = incomingRelations;
  }

  public void addIncomingRelation(final RelationshipTemplateInstance incomingRelation) {
    this.incomingRelations.add(incomingRelation);
    if (incomingRelation.getTarget() != this) {
      incomingRelation.setTarget(this);
    }
  }

  public Collection<RelationshipTemplateInstance> getOutgoingRelations() {
    return this.outgoingRelations;
  }

  public void setOutgoingRelations(
      final Collection<RelationshipTemplateInstance> outgoingRelations) {
    this.outgoingRelations = outgoingRelations;
  }

  public void addOutgoingRelation(final RelationshipTemplateInstance outgoingRelation) {
    this.outgoingRelations.add(outgoingRelation);
    if (outgoingRelation.getSource() != this) {
      outgoingRelation.setSource(this);
    }
  }

  public QName getTemplateId() {
    return templateId;
  }

  public void setTemplateId(final QName templateId) {
    this.templateId = templateId;
  }

  public QName getTemplateType() {
    return templateType;
  }

  public void setTemplateType(final QName templateType) {
    this.templateType = templateType;
  }

  public List<VerificationResult> getVerificationResults() {
    return verificationResults;
  }

  public void setVerificationResults(final List<VerificationResult> verificationResults) {
    this.verificationResults = verificationResults;
  }

  public void addVerificationResult(final VerificationResult verificationResult) {
    this.verificationResults.add(verificationResult);
    if (verificationResult.getNodeTemplateInstance() != this) {
      verificationResult.setNodeTemplateInstance(this);
    }
  }
}
