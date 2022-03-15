package org.opentosca.planbuilder.core.plugins.context;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;

/**
 * <p>
 * This class represents a mapping from TemplateId to Property LocalName and VariableName
 * </p>
 * Copyright 2013-2022 IAAS University of Stuttgart <br>
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

    public boolean addPropertyMapping(final TServiceTemplate serviceTemplate,
                                      final TNodeTemplate nodeTemplate, final String propertyName,
                                      final String propertyVariableName) {

        return this.propertyVariables.add(new PropertyVariable(serviceTemplate, nodeTemplate, propertyVariableName,
            propertyName));
    }

    public boolean addPropertyMapping(final TServiceTemplate serviceTemplate,
                                      final TRelationshipTemplate relationshipTemplate,
                                      final String propertyName, final String propertyVariableName) {

        return this.propertyVariables.add(new PropertyVariable(serviceTemplate, relationshipTemplate,
            propertyVariableName, propertyName));
    }

    public Collection<PropertyVariable> getPropertyVariables(TServiceTemplate serviceTemplate,
                                                             String templateId) {
        Collection<PropertyVariable> toReturn = new HashSet<PropertyVariable>();
        for (PropertyVariable var : this.propertyVariables) {
            if (var.getServiceTemplate().getId().equals(serviceTemplate.getId()) && var.isNodeTemplatePropertyVariable()
                && var.getNodeTemplate().getId().equals(templateId)) {
                toReturn.add(var);
            }

            if (var.getServiceTemplate().getId().equals(serviceTemplate.getId()) && !var.isNodeTemplatePropertyVariable()
                && var.getRelationshipTemplate().getId().equals(templateId)) {
                toReturn.add(var);
            }
        }
        return toReturn;
    }

    public Collection<PropertyVariable> getNodePropertyVariables(TServiceTemplate serviceTemplate,
                                                                 TNodeTemplate nodeTemplate) {
        Collection<PropertyVariable> toReturn = new HashSet<PropertyVariable>();

        for (PropertyVariable variable : this.propertyVariables) {
            if (variable.isNodeTemplatePropertyVariable() && variable.getServiceTemplate().getId().equals(serviceTemplate.getId())
                && variable.getNodeTemplate().getId().equals(nodeTemplate.getId())) {
                toReturn.add(variable);
            }
        }

        return toReturn;
    }

    public Collection<PropertyVariable> getRelationPropertyVariables(TServiceTemplate serviceTemplate,
                                                                     TRelationshipTemplate relationshipTemplate) {
        Collection<PropertyVariable> toReturn = new HashSet<PropertyVariable>();

        for (PropertyVariable variable : this.propertyVariables) {
            if (!variable.isNodeTemplatePropertyVariable() && variable.getServiceTemplate().getId().equals(serviceTemplate.getId())
                && variable.getRelationshipTemplate().getId().equals(relationshipTemplate.getId())) {
                toReturn.add(variable);
            }
        }

        return toReturn;
    }
}
