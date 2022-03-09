package org.opentosca.container.core.next.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.opentosca.container.core.common.jpa.CsarIdConverter;
import org.opentosca.container.core.common.jpa.DocumentConverter;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.next.xml.PropertyParser;
import org.w3c.dom.Document;

@Entity
@Table(name = ServiceTemplateInstance.TABLE_NAME)
public class ServiceTemplateInstance extends PersistenceObject {

    public static final String TABLE_NAME = "SERVICE_TEMPLATE_INSTANCE";

    private static final long serialVersionUID = 6652347924001914320L;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ServiceTemplateInstanceState state;

    @OneToMany(mappedBy = "serviceTemplateInstance", fetch = FetchType.EAGER)
    private Set<PlanInstance> planInstances = new HashSet<>();

    @OneToMany(mappedBy = "serviceTemplateInstance", fetch = FetchType.EAGER)
    private Collection<NodeTemplateInstance> nodeTemplateInstances = new HashSet<>();

    @OneToMany(mappedBy = "serviceTemplateInstance", fetch = FetchType.EAGER)
    private Collection<RelationshipTemplateInstance> relationshipTemplateInstances = new HashSet<>();

    @Convert(converter = CsarIdConverter.class)
    @Column(name = "CSAR_ID", nullable = false)
    private CsarId csarId;

    @Column(name = "TEMPLATE_ID", nullable = false)
    private String templateId;

    @Column(name = "CREATION_CORRELATION_ID", nullable = true)
    private String creationCorrelationId;

    @OrderBy("createdAt DESC")
    @OneToMany(mappedBy = "serviceTemplateInstance", fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
    @JsonIgnore
    private Set<ServiceTemplateInstanceProperty> properties = new HashSet<>();

    @OrderBy("createdAt DESC")
    @OneToMany(mappedBy = "serviceTemplateInstance", fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<DeploymentTest> deploymentTests = new HashSet<>();

    // 0-args constructor for JPA
    public ServiceTemplateInstance() {
    }

    public ServiceTemplateInstanceState getState() {
        return this.state;
    }

    public void setState(final ServiceTemplateInstanceState state) {
        this.state = state;
    }

    public Set<PlanInstance> getPlanInstances() {
        return this.planInstances;
    }

    public void setPlanInstances(final Set<PlanInstance> planInstances) {
        this.planInstances = planInstances;
    }

    public void addPlanInstance(final PlanInstance planInstance) {
        if (!planInstance.getType().equals(PlanType.BUILD)) {
            return;
        }
        this.planInstances.add(planInstance);
        if (planInstance.getServiceTemplateInstance() != this) {
            planInstance.setServiceTemplateInstance(this);
        }
    }

    public Collection<NodeTemplateInstance> getNodeTemplateInstances() {
        return this.nodeTemplateInstances;
    }

    public void setNodeTemplateInstances(final Collection<NodeTemplateInstance> nodeTemplateInstances) {
        this.nodeTemplateInstances = nodeTemplateInstances;
    }

    public void addNodeTemplateInstance(final NodeTemplateInstance nodeTemplateInstance) {
        this.nodeTemplateInstances.add(nodeTemplateInstance);
        if (nodeTemplateInstance.getServiceTemplateInstance() != this) {
            nodeTemplateInstance.setServiceTemplateInstance(this);
        }
    }

    public Collection<RelationshipTemplateInstance> getRelationshipTemplateInstances() {
        return this.relationshipTemplateInstances;
    }

    public void setRelationshipTemplateInstances(final Collection<RelationshipTemplateInstance> relationshipTemplateInstances) {
        this.relationshipTemplateInstances = relationshipTemplateInstances;
    }

    public void addRelationshipTemplateInstance(final RelationshipTemplateInstance relationshipTemplateInstance) {
        this.relationshipTemplateInstances.add(relationshipTemplateInstance);
        if (relationshipTemplateInstance.getServiceTemplateInstance() != this) {
            relationshipTemplateInstance.setServiceTemplateInstance(this);
        }
    }

    public CsarId getCsarId() {
        return this.csarId;
    }

    public void setCsarId(final CsarId csarId) {
        this.csarId = csarId;
    }

    public String getTemplateId() {
        return this.templateId;
    }

    public void setTemplateId(final String templateId) {
        this.templateId = templateId;
    }

    public String getCreationCorrelationId() {
        return this.creationCorrelationId;
    }

    public void setCreationCorrelationId(String creationCorrelationId) {
        this.creationCorrelationId = creationCorrelationId;
    }

    public Collection<ServiceTemplateInstanceProperty> getProperties() {
        return this.properties;
    }

    public void setProperties(final Set<ServiceTemplateInstanceProperty> properties) {
        this.properties = properties;
    }

    public void addProperty(final ServiceTemplateInstanceProperty property) {
        if (!this.properties.add(property)) {
            this.properties.remove(property);
            this.properties.add(property);
        }
        if (property.getServiceTemplateInstance() != this) {
            property.setServiceTemplateInstance(this);
        }
    }

    /*
     * Currently, the plan writes all properties as one XML document into the database. Therefore, we
     * parse this XML and return a Map<String, String>.
     */
    @JsonProperty("properties")
    public Map<String, String> getPropertiesAsMap() {
        final PropertyParser parser = new PropertyParser();
        final ServiceTemplateInstanceProperty prop =
            getProperties().stream().filter(p -> p.getType().equalsIgnoreCase("xml"))
                .collect(Collectors.reducing((a, b) -> null)).orElse(null);
        if (prop != null) {
            return parser.parse(prop.getValue());
        }
        return null;
    }

    public Document getPropertiesAsDocument() {
        final DocumentConverter converter = new DocumentConverter();
        final ServiceTemplateInstanceProperty prop =
            getProperties().stream().filter(p -> p.getType().equalsIgnoreCase("xml"))
                .collect(Collectors.reducing((a, b) -> null)).orElse(null);
        if (prop != null) {
            return converter.convertToEntityAttribute(prop.getValue());
        }

        return null;
    }

    public Set<DeploymentTest> getDeploymentTests() {
        return this.deploymentTests;
    }

    public void setDeploymentTests(final Set<DeploymentTest> deploymentTests) {
        this.deploymentTests = deploymentTests;
    }

    public void addDeploymentTest(final DeploymentTest deploymentTest) {
        this.deploymentTests.add(deploymentTest);
        if (deploymentTest.getServiceTemplateInstance() != this) {
            deploymentTest.setServiceTemplateInstance(this);
        }
    }
}
