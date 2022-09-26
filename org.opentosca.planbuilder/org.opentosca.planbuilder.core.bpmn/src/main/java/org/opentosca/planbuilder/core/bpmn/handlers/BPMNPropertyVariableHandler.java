package org.opentosca.planbuilder.core.bpmn.handlers;

import java.util.Collection;
import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;

import org.opentosca.planbuilder.core.plugins.context.Property2VariableMapping;
import org.opentosca.container.core.model.ModelUtils;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNPlan;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNSubprocess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used to initialize Template properties as variables in BuildPlans.
 */
public class BPMNPropertyVariableHandler {

    private final static Logger LOG = LoggerFactory.getLogger(BPMNPropertyVariableHandler.class);

    private final static String TOSCAPROPERTYSUFFIX = "toscaProperty";

    /**
     * Constructor
     */
    public BPMNPropertyVariableHandler() {
    }

    /**
     * Initializes the BuildPlan with variables for Template Properties and returns the Mappings for the Properties and
     * variables
     *
     * @param buildPlan the BuildPlan to initialize
     * @return a PropertyMap which holds mappings from Template to Template Property and BuildPlan variable
     */
    public Property2VariableMapping initializePropertiesAsVariables(final BPMNPlan buildPlan,
                                                                    final TServiceTemplate serviceTemplate) {
        return this.initializePropertiesAsVariables(buildPlan, serviceTemplate,
            serviceTemplate.getTopologyTemplate().getNodeTemplates(),
            serviceTemplate.getTopologyTemplate().getRelationshipTemplates());
    }

    public Property2VariableMapping initializePropertiesAsVariables(final BPMNPlan plan,
                                                                    final TServiceTemplate serviceTemplate,
                                                                    final Collection<TNodeTemplate> nodes,
                                                                    final Collection<TRelationshipTemplate> relations) {
        final Property2VariableMapping map = new Property2VariableMapping();

        for (final TNodeTemplate nodeTemplate : nodes) {

            this.initializePropertiesAsVariables(map, plan.getTemplateBuildPlan(nodeTemplate), serviceTemplate);
        }

        for (final TRelationshipTemplate relationshipTemplate : relations) {
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
    public void initializePropertiesAsVariables(final Property2VariableMapping map, final BPMNSubprocess templatePlan,
                                                final TServiceTemplate serviceTemplate) {
        if (templatePlan.getRelationshipTemplate() != null) {
            // template corresponds to a relationship template
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
    private void initPropsAsVarsInRelationship(final Property2VariableMapping map, final BPMNSubprocess templatePlan,
                                               final TServiceTemplate serviceTemplate) {
        final TRelationshipTemplate relationshipTemplate = templatePlan.getRelationshipTemplate();
        if (relationshipTemplate.getProperties() != null) {

            Map<String, String> propMap = ModelUtils.asMap(relationshipTemplate.getProperties());

            for (String propName : propMap.keySet()) {

                String propVarName = this.createPropertyVariableName(serviceTemplate, relationshipTemplate, propName);

                /*
                while (!this.planHandler.addStringVariable(propVarName, templatePlan.getBuildPlan())) {
                    propVarName = this.createPropertyVariableName(serviceTemplate, relationshipTemplate, propName);
                }
                 */

                map.addPropertyMapping(serviceTemplate, relationshipTemplate, propName, propVarName);

                String value = propMap.get(propName);

                BPMNPropertyVariableHandler.LOG.debug("Setting property variable " + propVarName);
                BPMNPropertyVariableHandler.LOG.debug("with value: " + value);

                if (!value.trim().isEmpty() && !value.trim().equals("")) {
                    // init the variable with the node value
                    // this.planHandler.assignInitValueToVariable(propVarName, value, templatePlan.getBuildPlan());
                } else {
                    //this.planHandler.assignInitValueToVariable(propVarName, "", templatePlan.getBuildPlan());
                }
            }
        }
    }

    private String createPropertyVariableName(final TServiceTemplate serviceTemplate,
                                              final TRelationshipTemplate relationshipTemplate,
                                              final String propertyName) {
        return ModelUtils.makeValidNCName(new QName(serviceTemplate.getTargetNamespace(), serviceTemplate.getId()).toString()) + "_"
            + ModelUtils.makeValidNCName(relationshipTemplate.getId()) + "_" + propertyName + "_" + TOSCAPROPERTYSUFFIX
            + System.currentTimeMillis();
    }

    private String createPropertyVariableName(final TServiceTemplate serviceTemplate,
                                              final TNodeTemplate nodeTemplate, final String propertyName) {
        return ModelUtils.makeValidNCName(new QName(serviceTemplate.getTargetNamespace(), serviceTemplate.getId()).toString()) + "_"
            + ModelUtils.makeValidNCName(nodeTemplate.getId()) + "_" + propertyName + "_" + TOSCAPROPERTYSUFFIX
            + System.currentTimeMillis();
    }

    /**
     * Initializes Property variables for the given TemplateBuildPlan which handles a NodeTemplate
     *
     * @param map          a PropertyMap to save the result/mappings to
     * @param templatePlan a TemplateBuildPlan which handles a NodeTemplate
     */
    private void initPropsAsVarsInNode(final Property2VariableMapping map, final BPMNSubprocess templatePlan,
                                       final TServiceTemplate serviceTemplate) {
        final TNodeTemplate nodeTemplate = templatePlan.getNodeTemplate();
        if (nodeTemplate.getProperties() != null) {
            Map<String, String> propMap = ModelUtils.asMap(nodeTemplate.getProperties());
            for (String propName : propMap.keySet()) {
                String propVarName = this.createPropertyVariableName(serviceTemplate, nodeTemplate, propName);

                /*
                while (!this.planHandler.addStringVariable(propVarName, templatePlan.getBuildPlan())) {
                    propVarName = this.createPropertyVariableName(serviceTemplate, nodeTemplate, propName);
                }
                 */
                map.addPropertyMapping(serviceTemplate, nodeTemplate, propName, propVarName);

                String value = propMap.get(propName);

                BPMNPropertyVariableHandler.LOG.debug("Setting property variable " + propVarName);
                BPMNPropertyVariableHandler.LOG.debug("with value: " + value);

                //this.planHandler.assignInitValueToVariable(propVarName, value, templatePlan.getBuildPlan());
            }
        }
    }
}
