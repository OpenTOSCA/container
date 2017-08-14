package org.opentosca.planbuilder.selection.plugin.input;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.bpel.fragments.BPELProcessFragments;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.plugins.IPlanBuilderPostPhasePlugin;
import org.opentosca.planbuilder.plugins.IScalingPlanBuilderSelectionPlugin;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * <p>
 * This class represents a POST-Phase Plugin which sends runtime values of
 * NodeTemplate Instances to the OpenTOSCA Container InstanceData API
 * </p>
 * Copyright 2014 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 * 
 */
public class Plugin implements IScalingPlanBuilderSelectionPlugin {
	
	private static final String inputSelectionStrategy = "UserProvided";
	
	
	@Override
	public String getID() {
		return "OpenTOSCA Input Selection Plugin";
	}
	
	@Override
	public boolean canHandle(AbstractNodeTemplate nodeTemplate, List<String> selectionStrategies) {
		// we can basically handle every type with this strategy
		if (selectionStrategies.contains(Plugin.inputSelectionStrategy)) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean handle(TemplatePlanContext context, AbstractNodeTemplate nodeTemplate, List<String> selectionStrategies) {
		// add input field
		String inputFieldName = nodeTemplate.getId() + "_InstanceID";
		context.addStringValueToPlanRequest(inputFieldName);
		
		// fetch nodeInstanceVar
		String nodeInstanceVarName = this.findInstanceVar(context, nodeTemplate.getId(), true);				
		
		// add assign from input to nodeInstanceVar
		try {
			Node assignFromInputToNodeInstanceIdVar = new BPELProcessFragments().generateAssignFromInputMessageToStringVariableAsNode(inputFieldName, nodeInstanceVarName);
			assignFromInputToNodeInstanceIdVar = context.importNode(assignFromInputToNodeInstanceIdVar);
			context.getPrePhaseElement().appendChild(assignFromInputToNodeInstanceIdVar);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	
	private String findInstanceVar(TemplatePlanContext context, String templateId, boolean isNode) {
		String instanceURLVarName = ((isNode) ? "node" : "relationship") + "InstanceURL_" + templateId + "_";
		for (String varName : context.getMainVariableNames()) {
			if (varName.contains(instanceURLVarName)) {
				return varName;
			}
		}
		return null;
	}
	
}
