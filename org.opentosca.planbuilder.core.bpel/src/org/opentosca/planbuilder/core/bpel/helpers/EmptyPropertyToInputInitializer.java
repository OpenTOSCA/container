package org.opentosca.planbuilder.core.bpel.helpers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.bpel.helpers.PropertyVariableInitializer.PropertyMap;
import org.opentosca.planbuilder.core.plugins.context.Variable;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELScopeActivity;
import org.opentosca.planbuilder.model.tosca.AbstractInterface;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.planbuilder.model.tosca.AbstractParameter;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Copyright 2016 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kálmán Képes - kalman.kepes@iaas.uni-stuttgart.de
 *
 */
public class EmptyPropertyToInputInitializer {

    /**
     * Adds an element to the plan input with the given namen and assign at runtime the value to the
     * given variable
     *
     * @param buildPlan the plan to add the logic to
     * @param propLocalName the name of the element added to the input
     * @param var the variable to assign the value to
     * @param context a context for the manipulation
     */
    private void addToPlanInput(final BPELPlan buildPlan, final String propLocalName, final Variable var,
                    final BPELPlanContext context) {
        // add to input
        context.addStringValueToPlanRequest(propLocalName);

        // add copy from input local element to property
        // variable
        final String bpelCopy = this.generateCopyFromInputToVariableAsString(
            this.createLocalNameXpathQuery(propLocalName), this.createBPELVariableXpathQuery(var.getName()));
        try {
            final Node bpelCopyNode = ModelUtils.string2dom(bpelCopy);
            this.appendToInitSequence(bpelCopyNode, buildPlan);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Appends the given node the the main sequence of the buildPlan this context belongs to
     *
     * @param node a XML DOM Node
     * @return true if adding the node to the main sequence was successfull
     */
    private boolean appendToInitSequence(final Node node, final BPELPlan buildPlan) {

        final Element flowElement = buildPlan.getBpelMainFlowElement();

        final Node mainSequenceNode = flowElement.getParentNode();

        final Node importedNode = mainSequenceNode.getOwnerDocument().importNode(node, true);
        mainSequenceNode.insertBefore(importedNode, flowElement);

        return true;
    }

    private String createBPELVariableXpathQuery(final String variableName) {
        return "$" + variableName;
    }

    private String createLocalNameXpathQuery(final String localName) {
        return "//*[local-name()='" + localName + "']";
    }

    /**
     * Generates a bpel copy element that queries from the plan input message to some xpath query
     *
     * @param inputQuery the query to a local element inside the input message
     * @param variableQuery the query to set the value for
     * @return a String containing a bpel copy
     */
    private String generateCopyFromInputToVariableAsString(final String inputQuery, final String variableQuery) {
        String copyString = "<bpel:assign xmlns:bpel=\"" + BPELPlan.bpelNamespace + "\"><bpel:copy>";

        copyString += "<bpel:from variable=\"input\" part=\"payload\"><bpel:query queryLanguage=\"urn:oasis:names:tc:wsbpel:2.0:sublang:xpath1.0\"><![CDATA["
            + inputQuery + "]]></bpel:query></bpel:from>";

        copyString += "<bpel:to expressionLanguage=\"urn:oasis:names:tc:wsbpel:2.0:sublang:xpath1.0\"><![CDATA[";
        copyString += variableQuery + "]]></bpel:to>";

        copyString += "</bpel:copy></bpel:assign>";

        return copyString;
    }

    public void initializeEmptyPropertiesAsInputParam(final BPELPlan buildPlan, final PropertyMap propMap) {
        this.initializeEmptyPropertiesAsInputParam(buildPlan.getTemplateBuildPlans(), buildPlan, propMap);
    }

    public void initializeEmptyPropertiesAsInputParam(final List<BPELScopeActivity> bpelActivities, final BPELPlan plan,
                    final PropertyMap propMap) {
        for (final BPELScopeActivity templatePlan : bpelActivities) {
            if (templatePlan.getNodeTemplate() != null) {
                final AbstractNodeTemplate nodeTemplate = templatePlan.getNodeTemplate();
                final List<AbstractNodeTemplate> hostingNodes = new ArrayList<>();
                ModelUtils.getNodesFromNodeToSink(nodeTemplate, hostingNodes);

                final BPELPlanContext context = new BPELPlanContext(templatePlan, propMap, plan.getServiceTemplate());

                if (propMap.getPropertyMappingMap(nodeTemplate.getId()) == null) {
                    // nodeTemplate doesn't have props defined
                    continue;
                }

                for (final String propLocalName : propMap.getPropertyMappingMap(nodeTemplate.getId()).keySet()) {
                    final Variable var = context.getPropertyVariable(nodeTemplate, propLocalName);

                    if (BPELPlanContext.isVariableValueEmpty(var, context)) {
                        // if the property is empty we have to check against the
                        // hostingNodes' operations outputparams
                        boolean matched = false;

                        for (final AbstractNodeTemplate hostingNode : hostingNodes) {
                            for (final AbstractInterface iface : hostingNode.getType().getInterfaces()) {
                                for (final AbstractOperation op : iface.getOperations()) {
                                    for (final AbstractParameter param : op.getOutputParameters()) {
                                        if (param.getName().equals(propLocalName)) {
                                            matched = true;
                                        }
                                    }
                                }
                            }
                        }

                        if (!matched) {
                            this.addToPlanInput(plan, propLocalName, var, context);
                        }
                    } else {
                        String content = BPELPlanContext.getVariableContent(var, context);
                        if (content.startsWith("get_input")) {
                            if (content.contains("get_input:")) {
                                content = content.replace("get_input:", "").trim();
                                this.addToPlanInput(plan, content, var, context);
                            } else {
                                this.addToPlanInput(plan, propLocalName, var, context);
                            }
                        }
                    }
                }
            }
        }

    }

}
