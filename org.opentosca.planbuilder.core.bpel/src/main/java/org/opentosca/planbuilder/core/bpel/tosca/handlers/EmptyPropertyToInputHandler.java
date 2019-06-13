package org.opentosca.planbuilder.core.bpel.tosca.handlers;

import java.io.IOException;
import java.util.Collection;

import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELScope;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.opentosca.planbuilder.plugins.context.Property2VariableMapping;
import org.opentosca.planbuilder.plugins.context.PropertyVariable;
import org.opentosca.planbuilder.plugins.context.Variable;
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
public class EmptyPropertyToInputHandler {

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
        final String bpelCopy =
            generateCopyFromInputToVariableAsString(createLocalNameXpathQuery(propLocalName),
                                                    createBPELVariableXpathQuery(var.getVariableName()));
        try {
            final Node bpelCopyNode = ModelUtils.string2dom(bpelCopy);
            appendToInitSequence(bpelCopyNode, buildPlan);
        }
        catch (ParserConfigurationException | SAXException | IOException e) {
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

        copyString +=
            "<bpel:from variable=\"input\" part=\"payload\"><bpel:query queryLanguage=\"urn:oasis:names:tc:wsbpel:2.0:sublang:xpath1.0\"><![CDATA["
                + inputQuery + "]]></bpel:query></bpel:from>";

        copyString += "<bpel:to expressionLanguage=\"urn:oasis:names:tc:wsbpel:2.0:sublang:xpath1.0\"><![CDATA[";
        copyString += variableQuery + "]]></bpel:to>";

        copyString += "</bpel:copy></bpel:assign>";

        return copyString;
    }

    public void initializeEmptyPropertiesAsInputParam(final BPELPlan buildPlan, final Property2VariableMapping propMap,
                                                      String serviceInstanceUrl, String serviceInstanceId,
                                                      String serviceTemplateUrl,
                                                      AbstractServiceTemplate serviceTemplate, String csarName) {
        this.initializeEmptyPropertiesAsInputParam(buildPlan.getTemplateBuildPlans(), buildPlan, propMap,
                                                   serviceInstanceUrl, serviceInstanceId, serviceTemplateUrl,
                                                   serviceTemplate, csarName);
    }

    public void initializeEmptyPropertiesAsInputParam(final Collection<BPELScope> bpelActivities, final BPELPlan plan,
                                                      final Property2VariableMapping propMap, String serviceInstanceUrl,
                                                      String serviceInstanceId, String serviceTemplateUrl,
                                                      AbstractServiceTemplate serviceTemplate, String csarName) {
        for (final BPELScope templatePlan : bpelActivities) {
            if (templatePlan.getNodeTemplate() != null) {
                final AbstractNodeTemplate nodeTemplate = templatePlan.getNodeTemplate();

                final BPELPlanContext context = new BPELPlanContext(plan,templatePlan, propMap, plan.getServiceTemplate(),
                    serviceInstanceUrl, serviceInstanceId, serviceTemplateUrl, csarName);

                if (propMap.getNodePropertyVariables(serviceTemplate, nodeTemplate).isEmpty()) {
                    // nodeTemplate doesn't have props defined
                    continue;
                }

                for (PropertyVariable var : propMap.getNodePropertyVariables(serviceTemplate, nodeTemplate)) {
                    if (var.getContent() != null && !var.getContent().isEmpty()) {
                        String content = var.getContent();
                        if (content.startsWith("get_input:")) {
                            content = content.replace("get_input:", "").trim();
                            addToPlanInput(plan, content, var, context);
                        }
                    }
                }
            }
        }

    }

}
