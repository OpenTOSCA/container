package org.opentosca.planbuilder.helpers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.helpers.PropertyVariableInitializer.PropertyMap;
import org.opentosca.planbuilder.model.plan.TOSCAPlan;
import org.opentosca.planbuilder.model.plan.TemplateBuildPlan;
import org.opentosca.planbuilder.model.tosca.AbstractInterface;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.planbuilder.model.tosca.AbstractParameter;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext.Variable;
import org.opentosca.planbuilder.utils.Utils;
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
	
	public void initializeEmptyPropertiesAsInputParam(TOSCAPlan buildPlan, PropertyMap propMap) {
		
		for (TemplateBuildPlan templatePlan : buildPlan.getTemplateBuildPlans()) {
			if (templatePlan.getNodeTemplate() != null) {
				AbstractNodeTemplate nodeTemplate = templatePlan.getNodeTemplate();
				List<AbstractNodeTemplate> hostingNodes = new ArrayList<AbstractNodeTemplate>();
				Utils.getNodesFromNodeToSink(nodeTemplate, hostingNodes);
				
				TemplatePlanContext context = new TemplatePlanContext(templatePlan, propMap, buildPlan.getServiceTemplate());
				
				if (propMap.getPropertyMappingMap(nodeTemplate.getId()) == null) {
					// nodeTemplate doesn't have props defined
					continue;
				}
				
				for (String propLocalName : propMap.getPropertyMappingMap(nodeTemplate.getId()).keySet()) {
					Variable var = context.getPropertyVariable(nodeTemplate, propLocalName);
					
					if (Utils.isVariableValueEmpty(var, context)) {
						// if the property is empty we have to check against the
						// hostingNodes' operations outputparams
						boolean matched = false;
						
						for (AbstractNodeTemplate hostingNode : hostingNodes) {
							for (AbstractInterface iface : hostingNode.getType().getInterfaces()) {
								for (AbstractOperation op : iface.getOperations()) {
									for (AbstractParameter param : op.getOutputParameters()) {
										if (param.getName().equals(propLocalName)) {
											matched = true;
										}
									}
								}
							}
						}
						
						if (!matched) {
							this.addToPlanInput(buildPlan, propLocalName, var, context);
						}
					} else {
						String content = Utils.getVariableContent(var, context);
						if (content.startsWith("get_input")) {
							if (content.contains("get_input:")) {
								content = content.replace("get_input:", "").trim();
								this.addToPlanInput(buildPlan, content, var, context);
							} else {
								this.addToPlanInput(buildPlan, propLocalName, var, context);
							}
						}
					}
				}
			}
		}
		
	}
	
	/**
	 * Adds an element to the plan input with the given namen and assign at
	 * runtime the value to the given variable
	 *
	 * @param buildPlan the plan to add the logic to
	 * @param propLocalName the name of the element added to the input
	 * @param var the variable to assign the value to
	 * @param context a context for the manipulation
	 */
	private void addToPlanInput(TOSCAPlan buildPlan, String propLocalName, Variable var, TemplatePlanContext context) {
		// add to input
		context.addStringValueToPlanRequest(propLocalName);
		
		// add copy from input local element to property
		// variable
		String bpelCopy = this.generateCopyFromInputToVariableAsString(this.createLocalNameXpathQuery(propLocalName), this.createBPELVariableXpathQuery(var.getName()));
		try {
			Node bpelCopyNode = Utils.string2dom(bpelCopy);
			this.appendToInitSequence(bpelCopyNode, buildPlan);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Appends the given node the the main sequence of the buildPlan this
	 * context belongs to
	 *
	 * @param node a XML DOM Node
	 * @return true if adding the node to the main sequence was successfull
	 */
	private boolean appendToInitSequence(Node node, TOSCAPlan buildPlan) {
		
		Element flowElement = buildPlan.getBpelMainFlowElement();
		
		Node mainSequenceNode = flowElement.getParentNode();
		
		Node importedNode = mainSequenceNode.getOwnerDocument().importNode(node, true);
		mainSequenceNode.insertBefore(importedNode, flowElement);
		
		return true;
	}
	
	private String createLocalNameXpathQuery(String localName) {
		return "//*[local-name()='" + localName + "']";
	}
	
	private String createBPELVariableXpathQuery(String variableName) {
		return "$" + variableName;
	}
	
	/**
	 * Generates a bpel copy element that queries from the plan input message to
	 * some xpath query
	 *
	 * @param inputQuery the query to a local element inside the input message
	 * @param variableQuery the query to set the value for
	 * @return a String containing a bpel copy
	 */
	private String generateCopyFromInputToVariableAsString(String inputQuery, String variableQuery) {
		String copyString = "<bpel:assign xmlns:bpel=\"" + TOSCAPlan.bpelNamespace + "\"><bpel:copy>";
		
		copyString += "<bpel:from variable=\"input\" part=\"payload\"><bpel:query queryLanguage=\"urn:oasis:names:tc:wsbpel:2.0:sublang:xpath1.0\"><![CDATA[" + inputQuery + "]]></bpel:query></bpel:from>";
		
		copyString += "<bpel:to expressionLanguage=\"urn:oasis:names:tc:wsbpel:2.0:sublang:xpath1.0\"><![CDATA[";
		copyString += variableQuery + "]]></bpel:to>";
		
		copyString += "</bpel:copy></bpel:assign>";
		
		return copyString;
	}
	
}
