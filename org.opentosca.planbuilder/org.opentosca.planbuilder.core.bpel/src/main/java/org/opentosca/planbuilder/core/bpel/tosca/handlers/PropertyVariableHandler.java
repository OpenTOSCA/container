package org.opentosca.planbuilder.core.bpel.tosca.handlers;

import java.util.Collection;
import java.util.Map;

import org.opentosca.planbuilder.core.bpel.handlers.BPELPlanHandler;
import org.opentosca.planbuilder.core.plugins.context.Property2VariableMapping;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELScope;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.opentosca.planbuilder.model.utils.ModelUtils;
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
 */
public class PropertyVariableHandler {

    private final static Logger LOG = LoggerFactory.getLogger(PropertyVariableHandler.class);

    private final static String TOSCAPROPERTYSUFFIX = "toscaProperty";

    private final BPELPlanHandler planHandler;

    /**
     * Constructor
     *
     * @param planHandler     a BuildPlanHandler for the class
     * @param templateHandler a TemplateBuildPlanHandler for the class
     */
    public PropertyVariableHandler(final BPELPlanHandler planHandler) {
        this.planHandler = planHandler;
    }

    /**
     * Initializes the BuildPlan with variables for Template Properties and returns the Mappings for the Properties and
     * variables
     *
     * @param buildPlan the BuildPlan to initialize
     * @return a PropertyMap which holds mappings from Template to Template Property and BuildPlan variable
     */
    public Property2VariableMapping initializePropertiesAsVariables(final BPELPlan buildPlan,
                                                                    final AbstractServiceTemplate serviceTemplate) {
        return this.initializePropertiesAsVariables(buildPlan, serviceTemplate,
            serviceTemplate.getTopologyTemplate().getNodeTemplates(),
            serviceTemplate.getTopologyTemplate().getRelationshipTemplates());
    }

    public Property2VariableMapping initializePropertiesAsVariables(final BPELPlan plan,
                                                                    final AbstractServiceTemplate serviceTemplate,
                                                                    final Collection<AbstractNodeTemplate> nodes,
                                                                    final Collection<AbstractRelationshipTemplate> relations) {
        final Property2VariableMapping map = new Property2VariableMapping();

        for (final AbstractNodeTemplate nodeTemplate : nodes) {

            this.initializePropertiesAsVariables(map, plan.getTemplateBuildPlan(nodeTemplate), serviceTemplate);
        }

        for (final AbstractRelationshipTemplate relationshipTemplate : relations) {
            this.initializePropertiesAsVariables(map, plan.getTemplateBuildPlan(relationshipTemplate), serviceTemplate);
        }
        return map;
    }

    /**
     * Initializes Properties inside the given PropertyMap of the given TemplateBuildPlan
     *
     * @param map          a PropertyMap to save the mappings to
     * @param templatePlan the TemplateBuildPlan to initialize its properties
     */
    public void initializePropertiesAsVariables(final Property2VariableMapping map, final BPELScope templatePlan,
                                                final AbstractServiceTemplate serviceTemplate) {
        if (templatePlan.getRelationshipTemplate() != null) {
            // template corresponds to a relationshiptemplate
            initPropsAsVarsInRelationship(map, templatePlan, serviceTemplate);
        } else {
            initPropsAsVarsInNode(map, templatePlan, serviceTemplate);
        }
    }

    /**
     * Initializes Property variables and mappings for a TemplateBuildPlan which handles a RelationshipTemplate
     *
     * @param map          the PropertyMap to save the result to
     * @param templatePlan a TemplateBuildPlan which handles a RelationshipTemplate
     */
    private void initPropsAsVarsInRelationship(final Property2VariableMapping map, final BPELScope templatePlan,
                                               final AbstractServiceTemplate serviceTemplate) {
        final AbstractRelationshipTemplate relationshipTemplate = templatePlan.getRelationshipTemplate();
        if (relationshipTemplate.getProperties() != null) {

            Map<String,String> propMap = relationshipTemplate.getProperties().asMap();

            for(String propName : propMap.keySet()) {

                String propVarName = this.createPropertyVariableName(serviceTemplate, relationshipTemplate, propName);

                while (!this.planHandler.addStringVariable(propVarName, templatePlan.getBuildPlan())) {
                    propVarName = this.createPropertyVariableName(serviceTemplate, relationshipTemplate, propName);
                }

                map.addPropertyMapping(serviceTemplate, relationshipTemplate, propName, propVarName);

                String value = propMap.get(propName);

                PropertyVariableHandler.LOG.debug("Setting property variable " + propVarName);
                PropertyVariableHandler.LOG.debug("with value: " + value);

                if (!value.trim().isEmpty() && !value.trim().equals("")) {
                    // init the variable with the node value
                    this.planHandler.assignInitValueToVariable(propVarName, value, templatePlan.getBuildPlan());
                } else {
                    this.planHandler.assignInitValueToVariable(propVarName, "", templatePlan.getBuildPlan());
                }
            }

        }
    }

    private String createPropertyVariableName(final AbstractServiceTemplate serviceTemplate,
                                              final AbstractRelationshipTemplate relationshipTemplate,
                                              final String propertyName) {
        return ModelUtils.makeValidNCName(serviceTemplate.getQName().toString()) + "_"
            + ModelUtils.makeValidNCName(relationshipTemplate.getId()) + "_" + propertyName + "_" + TOSCAPROPERTYSUFFIX
            + System.currentTimeMillis();
    }

    private String createPropertyVariableName(final AbstractServiceTemplate serviceTemplate,
                                              final AbstractNodeTemplate nodeTemplate, final String propertyName) {
        return ModelUtils.makeValidNCName(serviceTemplate.getQName().toString()) + "_"
            + ModelUtils.makeValidNCName(nodeTemplate.getId()) + "_" + propertyName + "_" + TOSCAPROPERTYSUFFIX
            + System.currentTimeMillis();
    }

    /**
     * Initializes Property variables for the given TemplateBuildPlan which handles a NodeTemplate
     *
     * @param map          a PropertyMap to save the result/mappings to
     * @param templatePlan a TemplateBuildPlan which handles a NodeTemplate
     */
    private void initPropsAsVarsInNode(final Property2VariableMapping map, final BPELScope templatePlan,
                                       final AbstractServiceTemplate serviceTemplate) {
        final AbstractNodeTemplate nodeTemplate = templatePlan.getNodeTemplate();
        if (nodeTemplate.getProperties() != null) {
            Map<String,String> propMap = nodeTemplate.getProperties().asMap();
            for(String propName : propMap.keySet()){
                String propVarName = this.createPropertyVariableName(serviceTemplate, nodeTemplate, propName);

                while (!this.planHandler.addStringVariable(propVarName, templatePlan.getBuildPlan())) {
                    propVarName = this.createPropertyVariableName(serviceTemplate, nodeTemplate, propName);
                }

                map.addPropertyMapping(serviceTemplate, nodeTemplate, propName, propVarName);

                String value = propMap.get(propName);

                PropertyVariableHandler.LOG.debug("Setting property variable " + propVarName);
                PropertyVariableHandler.LOG.debug("with value: " + value);

                this.planHandler.assignInitValueToVariable(propVarName, value, templatePlan.getBuildPlan());
            }


        }
    }

}
