package org.opentosca.planbuilder.selection.plugin.firstavailable;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.fragments.Fragments;
import org.opentosca.planbuilder.model.plan.TOSCAPlan;
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
	
	private static final String firstAvailableSelectionStrategy = "FirstInstance";
	
	
	@Override
	public String getID() {
		return "OpenTOSCA First Available Selection Plugin";
	}
	
	@Override
	public boolean canHandle(AbstractNodeTemplate nodeTemplate, List<String> selectionStrategies) {
		// we can basically handle every type with this strategy
		if (selectionStrategies.contains(Plugin.firstAvailableSelectionStrategy)) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean handle(TemplatePlanContext context, AbstractNodeTemplate nodeTemplate, List<String> selectionStrategies) {		
		// TODO
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
