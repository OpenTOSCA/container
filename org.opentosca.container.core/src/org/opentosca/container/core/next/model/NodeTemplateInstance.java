package org.opentosca.container.core.next.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.xml.namespace.QName;

import org.eclipse.persistence.annotations.Convert;
import org.opentosca.container.core.next.xml.PropertyParser;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

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
    @JsonIgnore
    private Set<NodeTemplateInstanceProperty> properties = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "SERVICE_TEMPLATE_INSTANCE_ID")
    private ServiceTemplateInstance serviceTemplateInstance;

    @OneToMany(mappedBy = "target")
    private Collection<RelationshipTemplateInstance> incomingRelations = new ArrayList<>();

    @OneToMany(mappedBy = "source")
    private Collection<RelationshipTemplateInstance> outgoingRelations = new ArrayList<>();

    @Convert("QNameConverter")
    @Column(name = "TEMPLATE_ID", nullable = false)
    private QName templateId;

    @Convert("QNameConverter")
    @Column(name = "TEMPLATE_TYPE", nullable = false)
    private QName templateType;

    @OrderBy("createdAt DESC")
    @OneToMany(mappedBy = "nodeTemplateInstance", fetch = FetchType.EAGER)
    @JsonIgnore
    private List<DeploymentTestResult> deploymentTestResults = new ArrayList<>();

    public NodeTemplateInstance() {}


    public String getName() {
        return this.templateId.getLocalPart();
    }

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
    @JsonProperty("properties")
    public Map<String, String> getPropertiesAsMap() {
        final PropertyParser parser = new PropertyParser();
        final NodeTemplateInstanceProperty prop =
            getProperties().stream().filter(p -> p.getType().equalsIgnoreCase("xml"))
                           .collect(Collectors.reducing((a, b) -> null)).orElse(null);
        if (prop != null) {
            return parser.parse(prop.getValue());
        }
        return null;
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

    public void setIncomingRelations(final Collection<RelationshipTemplateInstance> incomingRelations) {
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

    public void setOutgoingRelations(final Collection<RelationshipTemplateInstance> outgoingRelations) {
        this.outgoingRelations = outgoingRelations;
    }

    public void addOutgoingRelation(final RelationshipTemplateInstance outgoingRelation) {
        this.outgoingRelations.add(outgoingRelation);
        if (outgoingRelation.getSource() != this) {
            outgoingRelation.setSource(this);
        }
    }

    public QName getTemplateId() {
        return this.templateId;
    }

    public void setTemplateId(final QName templateId) {
        this.templateId = templateId;
    }

    public QName getTemplateType() {
        return this.templateType;
    }

    public void setTemplateType(final QName templateType) {
        this.templateType = templateType;
    }

    public List<DeploymentTestResult> getDeploymentTestResults() {
        return this.deploymentTestResults;
    }

    public void setDeploymentTestResults(final List<DeploymentTestResult> deploymentTestResult) {
        this.deploymentTestResults = deploymentTestResult;
    }

    public void addDeploymentTestResult(final DeploymentTestResult deploymentTestResult) {
        this.deploymentTestResults.add(deploymentTestResult);
        if (deploymentTestResult.getNodeTemplateInstance() != this) {
            deploymentTestResult.setNodeTemplateInstance(this);
        }
    }
}
