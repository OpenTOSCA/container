package org.opentosca.planbuilder.core.plugins.context;

import java.util.Collection;
import java.util.HashSet;

import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;

/**
 * <p>
 * This class represents a mapping from TemplateId to Property LocalName and VariableName
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public class Property2VariableMapping {

    private final Collection<PropertyVariable> propertyVariables;

    /**
     * Constructor
     */
    public Property2VariableMapping() {
        this.propertyVariables = new HashSet<PropertyVariable>();
    }

    public boolean addPropertyMapping(final AbstractServiceTemplate serviceTemplate,
                                      final AbstractNodeTemplate nodeTemplate, final String propertyName,
                                      final String propertyVariableName) {

        return this.propertyVariables.add(new PropertyVariable(serviceTemplate, nodeTemplate, propertyVariableName,
            propertyName));
    }

    public boolean addPropertyMapping(final AbstractServiceTemplate serviceTemplate,
                                      final AbstractRelationshipTemplate relationshipTemplate,
                                      final String propertyName, final String propertyVariableName) {

        return this.propertyVariables.add(new PropertyVariable(serviceTemplate, relationshipTemplate,
            propertyVariableName, propertyName));
    }

    public Collection<PropertyVariable> getPropertyVariables(AbstractServiceTemplate serviceTemplate,
                                                             String templateId) {
        Collection<PropertyVariable> toReturn = new HashSet<PropertyVariable>();
        for (PropertyVariable var : this.propertyVariables) {
            if (var.getServiceTemplate().equals(serviceTemplate) && var.isNodeTemplatePropertyVariable()
                && var.getNodeTemplate().getId().equals(templateId)) {
                toReturn.add(var);
            }

            if (var.getServiceTemplate().equals(serviceTemplate) && !var.isNodeTemplatePropertyVariable()
                && var.getRelationshipTemplate().getId().equals(templateId)) {
                toReturn.add(var);
            }
        }
        return toReturn;
    }

    public Collection<PropertyVariable> getNodePropertyVariables(AbstractServiceTemplate serviceTemplate,
                                                                 AbstractNodeTemplate nodeTemplate) {
        Collection<PropertyVariable> toReturn = new HashSet<PropertyVariable>();

        for (PropertyVariable variable : this.propertyVariables) {
            if (variable.isNodeTemplatePropertyVariable() && variable.getServiceTemplate().equals(serviceTemplate)
                && variable.getNodeTemplate().equals(nodeTemplate)) {
                toReturn.add(variable);
            }
        }

        return toReturn;
    }

    public Collection<PropertyVariable> getRelationPropertyVariables(AbstractServiceTemplate serviceTemplate,
                                                                     AbstractRelationshipTemplate relationshipTemplate) {
        Collection<PropertyVariable> toReturn = new HashSet<PropertyVariable>();

        for (PropertyVariable variable : this.propertyVariables) {
            if (!variable.isNodeTemplatePropertyVariable() && variable.getServiceTemplate().equals(serviceTemplate)
                && variable.getRelationshipTemplate().equals(relationshipTemplate)) {
                toReturn.add(variable);
            }
        }

        return toReturn;
    }
}
