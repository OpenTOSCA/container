package org.opentosca.planbuilder.core.plugins.context;

import java.util.Collection;
import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;

import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;

public abstract class PlanContext {

    protected final AbstractPlan plan;
    protected final AbstractServiceTemplate serviceTemplate;
    protected final String serviceInstanceURLVarName;
    protected final String serviceInstanceIDVarName;
    protected final String serviceTemplateURLVarName;
    protected final String planInstanceUrlVarName;
    protected final Csar csar;
    protected final Property2VariableMapping propertyMap;

    public PlanContext(final AbstractPlan plan, final AbstractServiceTemplate serviceTemplate,
                       final Property2VariableMapping map, final String serviceInstanceURLVarName,
                       final String serviceInstanceIDVarName, final String serviceTemplateURLVarName, final String planInstanceUrlVarName,
                       final Csar csar) {
        this.plan = plan;
        this.serviceTemplate = serviceTemplate;
        this.serviceInstanceIDVarName = serviceInstanceIDVarName;
        this.serviceTemplateURLVarName = serviceTemplateURLVarName;
        this.serviceInstanceURLVarName = serviceInstanceURLVarName;
        this.planInstanceUrlVarName = planInstanceUrlVarName;
        this.csar = csar;
        this.propertyMap = map;
    }

    public Collection<PropertyVariable> getPropertyVariables(final TNodeTemplate nodeTemplate) {
        return this.propertyMap.getNodePropertyVariables(this.serviceTemplate, nodeTemplate);
    }

    public AbstractServiceTemplate getServiceTemplate() {
        return this.serviceTemplate;
    }

    public String getServiceTemplateURLVar() {
        return this.serviceTemplateURLVarName;
    }

    public String getServiceInstanceIDVarName() {
        return this.serviceInstanceIDVarName;
    }

    public String getServiceInstanceURLVarName() {
        return this.serviceInstanceURLVarName;
    }

    public String getPlanInstanceURLVarName() {
        return this.planInstanceUrlVarName;
    }

    /**
     * Returns the plan type of this context
     *
     * @return a TOSCAPlan.PlanType
     */
    public PlanType getPlanType() {
        return this.plan.getType();
    }

    /**
     * Returns a Variable object that represents a property inside the given nodeTemplate with the given name
     *
     * @param nodeTemplate a nodeTemplate to look for the property in
     * @param propertyName the name of the searched property
     * @return a Variable object representing the property
     */
    public PropertyVariable getPropertyVariable(final TNodeTemplate nodeTemplate, final String propertyName) {
        return this.propertyMap.getNodePropertyVariables(this.serviceTemplate, nodeTemplate).stream()
            .filter(var -> var.getPropertyName().equals(propertyName)).findFirst().orElse(null);
    }

    public PropertyVariable getPropertyVariable(final TRelationshipTemplate relationshipTemplate,
                                                final String propertyName) {
        return this.propertyMap.getRelationPropertyVariables(this.serviceTemplate, relationshipTemplate).stream()
            .filter(var -> var.getPropertyName().equals(propertyName)).findFirst().orElse(null);
    }

    /**
     * Looks for a Property with the same localName as the given toscaParameter. The search is on the whole
     * TopologyTemplate this TemplateContext belongs to.
     *
     * @param localName a String
     * @return a Variable Object with TemplateId and Name, if null the whole Topology has no Property with the specified
     * localName
     */
    public PropertyVariable getPropertyVariable(final String localName) {
        // then on everything else
        for (final TNodeTemplate infraNode : getNodeTemplates()) {
            if (this.getPropertyVariable(infraNode, localName) != null) {
                return this.getPropertyVariable(infraNode, localName);
            }
        }

        for (final TRelationshipTemplate infraEdge : getRelationshipTemplates()) {
            if (this.getPropertyVariable(infraEdge, localName) != null) {
                return this.getPropertyVariable(infraEdge, localName);
            }
        }
        return null;
    }

    /**
     * Returns the file name of the CSAR in which this Template resides
     *
     * @return a String with the file name of the CSAR
     */
    public String getCSARFileName() {
        return this.csar.id().csarName();
    }

    public Csar getCsar() {
        return this.csar;
    }

    /**
     * Returns an Integer which can be used as variable names etc. So that there are no collisions with other
     * declarations
     *
     * @return an Integer
     */
    public int getIdForNames() {
        return this.plan.getIdForNamesAndIncrement();
    }

    /**
     * <p>
     * Returns all NodeTemplates that are part of the ServiceTemplate this context belongs to.
     * </p>
     *
     * @return a List of TNodeTemplate
     */
    public List<TNodeTemplate> getNodeTemplates() {
        // find the serviceTemplate
        return this.plan.getServiceTemplate().getTopologyTemplate().getNodeTemplates();
    }

    /**
     * <p>
     * Returns all RelationshipTemplates that are part of the ServiceTemplate this context belongs to.
     * </p>
     *
     * @return a List of TRelationshipTemplate
     */
    public List<TRelationshipTemplate> getRelationshipTemplates() {
        return this.serviceTemplate.getTopologyTemplate().getRelationshipTemplates();
    }

    public QName getServiceTemplateId() {
        return this.serviceTemplate.getQName();
    }

    /**
     * Returns the variable name of the given template and property localName
     *
     * @param templateId   the Id of the Template to look in
     * @param propertyName the LocalName of a Template Property
     * @return a String containing the variable name, else null
     */
    public String getVariableNameOfProperty(final TNodeTemplate templateId, final String propertyName) {
        return this.propertyMap.getNodePropertyVariables(this.serviceTemplate, templateId).stream()
            .filter(var -> var.getPropertyName().equals(propertyName)).findFirst()
            .map(var -> var.getVariableName()).orElse(null);
    }

    public String getVariableNameOfProperty(final TRelationshipTemplate templateId, final String propertyName) {
        return this.propertyMap.getRelationPropertyVariables(this.serviceTemplate, templateId).stream()
            .filter(var -> var.getPropertyName().equals(propertyName)).findFirst()
            .map(var -> var.getVariableName()).orElse(null);
    }

    public enum Phase {
        PRE, PROV, POST
    }
}
