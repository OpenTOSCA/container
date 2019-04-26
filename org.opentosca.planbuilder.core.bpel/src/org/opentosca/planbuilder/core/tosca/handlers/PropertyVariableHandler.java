package org.opentosca.planbuilder.core.tosca.handlers;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.opentosca.planbuilder.core.bpel.handlers.BPELPlanHandler;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELScope;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.opentosca.planbuilder.plugins.context.PropertyVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * <p>
 * This class is used to initialize Template properties as variables in BuildPlans.
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class PropertyVariableHandler {

    private final static Logger LOG = LoggerFactory.getLogger(PropertyVariableHandler.class);

    private final BPELPlanHandler planHandler;

    private final static String TOSCAPROPERTYSUFFIX = "toscaProperty";

    /**
     * <p>
     * This class represents a mapping from TemplateId to Property LocalName and VariableName
     * </p>
     * Copyright 2013 IAAS University of Stuttgart <br>
     * <br>
     *
     * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
     *
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

    /**
     * Constructor
     *
     * @param planHandler a BuildPlanHandler for the class
     * @param templateHandler a TemplateBuildPlanHandler for the class
     */
    public PropertyVariableHandler(final BPELPlanHandler planHandler) {
        this.planHandler = planHandler;
    }

    /**
     * Initializes the BuildPlan with variables for Template Properties and returns the Mappings for the
     * Properties and variables
     *
     * @param buildPlan the BuildPlan to initialize
     * @return a PropertyMap which holds mappings from Template to Template Property and BuildPlan
     *         variable
     */
    public Property2VariableMapping initializePropertiesAsVariables(final BPELPlan buildPlan,
                                                                    AbstractServiceTemplate serviceTemplate) {
        final Property2VariableMapping map = new Property2VariableMapping();

        for (AbstractNodeTemplate nodeTemplate : serviceTemplate.getTopologyTemplate().getNodeTemplates()) {

            this.initializePropertiesAsVariables(map, buildPlan.getTemplateBuildPlan(nodeTemplate), serviceTemplate);
        }

        for (AbstractRelationshipTemplate relationshipTemplate : serviceTemplate.getTopologyTemplate()
                                                                                .getRelationshipTemplates()) {
            this.initializePropertiesAsVariables(map, buildPlan.getTemplateBuildPlan(relationshipTemplate),
                                                 serviceTemplate);
        }
        return map;
    }

    /**
     * Initializes Properties inside the given PropertyMap of the given TemplateBuildPlan
     *
     * @param map a PropertyMap to save the mappings to
     * @param templatePlan the TemplateBuildPlan to initialize its properties
     */
    public void initializePropertiesAsVariables(final Property2VariableMapping map, final BPELScope templatePlan,
                                                AbstractServiceTemplate serviceTemplate) {
        if (templatePlan.getRelationshipTemplate() != null) {
            // template corresponds to a relationshiptemplate
            initPropsAsVarsInRelationship(map, templatePlan, serviceTemplate);
        } else {
            initPropsAsVarsInNode(map, templatePlan, serviceTemplate);
        }
    }

    /**
     * Initializes Property variables and mappings for a TemplateBuildPlan which handles a
     * RelationshipTemplate
     *
     * @param map the PropertyMap to save the result to
     * @param templatePlan a TemplateBuildPlan which handles a RelationshipTemplate
     */
    private void initPropsAsVarsInRelationship(final Property2VariableMapping map, final BPELScope templatePlan,
                                               AbstractServiceTemplate serviceTemplate) {
        final AbstractRelationshipTemplate relationshipTemplate = templatePlan.getRelationshipTemplate();
        if (relationshipTemplate.getProperties() != null) {
            final Element propertyElement = relationshipTemplate.getProperties().getDOMElement();
            for (int i = 0; i < propertyElement.getChildNodes().getLength(); i++) {

                if (propertyElement.getChildNodes().item(i).getNodeType() == Node.TEXT_NODE) {
                    continue;
                }

                final String propName = propertyElement.getChildNodes().item(i).getLocalName();
                String propVarName = this.createPropertyVariableName(serviceTemplate, relationshipTemplate, propName);

                while (!this.planHandler.addStringVariable(propVarName, templatePlan.getBuildPlan())) {
                    propVarName = this.createPropertyVariableName(serviceTemplate, relationshipTemplate, propName);
                }

                map.addPropertyMapping(serviceTemplate, relationshipTemplate, propName, propVarName);
                // String value =
                // propertyElement.getChildNodes().item(i).getFirstChild().getNodeValue();
                String value = "";

                for (int j = 0; j < propertyElement.getChildNodes().item(i).getChildNodes().getLength(); j++) {
                    if (propertyElement.getChildNodes().item(i).getChildNodes().item(j)
                                       .getNodeType() == Node.TEXT_NODE) {
                        value += propertyElement.getChildNodes().item(i).getChildNodes().item(j).getNodeValue();
                    }
                }

                PropertyVariableHandler.LOG.debug("Setting property variable " + propVarName);
                PropertyVariableHandler.LOG.debug("with value: " + value);

                // tempID_PropLocalName as property variable name

                if (!value.trim().isEmpty() && !value.trim().equals("")) {
                    // init the variable with the node value
                    this.planHandler.assignInitValueToVariable(propVarName, value, templatePlan.getBuildPlan());
                }

            }
        }
    }

    private String createPropertyVariableName(AbstractServiceTemplate serviceTemplate,
                                              AbstractRelationshipTemplate relationshipTemplate, String propertyName) {
        return ModelUtils.makeValidNCName(serviceTemplate.getQName().toString()) + "_"
            + ModelUtils.makeValidNCName(relationshipTemplate.getId()) + "_" + propertyName + "_" + TOSCAPROPERTYSUFFIX
            + System.currentTimeMillis();
    }

    private String createPropertyVariableName(AbstractServiceTemplate serviceTemplate,
                                              AbstractNodeTemplate nodeTemplate, String propertyName) {
        return ModelUtils.makeValidNCName(serviceTemplate.getQName().toString()) + "_"
            + ModelUtils.makeValidNCName(nodeTemplate.getId()) + "_" + propertyName + "_" + TOSCAPROPERTYSUFFIX
            + System.currentTimeMillis();
    }

    /**
     * Initializes Property variables for the given TemplateBuildPlan which handles a NodeTemplate
     *
     * @param map a PropertyMap to save the result/mappings to
     * @param templatePlan a TemplateBuildPlan which handles a NodeTemplate
     */
    private void initPropsAsVarsInNode(final Property2VariableMapping map, final BPELScope templatePlan,
                                       AbstractServiceTemplate serviceTemplate) {
        final AbstractNodeTemplate nodeTemplate = templatePlan.getNodeTemplate();
        if (nodeTemplate.getProperties() != null) {
            final Element propertyElement = nodeTemplate.getProperties().getDOMElement();
            for (int i = 0; i < propertyElement.getChildNodes().getLength(); i++) {

                if (propertyElement.getChildNodes().item(i).getNodeType() == Node.TEXT_NODE) {
                    continue;
                }

                final String propName = propertyElement.getChildNodes().item(i).getLocalName();
                String propVarName = this.createPropertyVariableName(serviceTemplate, nodeTemplate, propName);


                while (!this.planHandler.addStringVariable(propVarName, templatePlan.getBuildPlan())) {
                    propVarName = this.createPropertyVariableName(serviceTemplate, nodeTemplate, propName);
                }

                map.addPropertyMapping(serviceTemplate, nodeTemplate, propName, propVarName);

                String value = "";

                for (int j = 0; j < propertyElement.getChildNodes().item(i).getChildNodes().getLength(); j++) {
                    if (propertyElement.getChildNodes().item(i).getChildNodes().item(j)
                                       .getNodeType() == Node.TEXT_NODE) {
                        value += propertyElement.getChildNodes().item(i).getChildNodes().item(j).getNodeValue();
                    }
                }

                PropertyVariableHandler.LOG.debug("Setting property variable " + propVarName);
                PropertyVariableHandler.LOG.debug("with value: " + value);

                // init the variable with the node value
                this.planHandler.assignInitValueToVariable(propVarName, value, templatePlan.getBuildPlan());
            }
        }
    }

}
