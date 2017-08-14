package org.opentosca.planbuilder.selection.plugin.firstavailable;

import java.io.IOException;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.bpel.fragments.BPELProcessFragments;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.plugins.IPlanBuilderPostPhasePlugin;
import org.opentosca.planbuilder.plugins.IScalingPlanBuilderSelectionPlugin;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.opentosca.planbuilder.utils.Utils;
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
		// fetch instance variables
		String nodeTemplateInstanceVar = this.findInstanceVar(context, nodeTemplate.getId(), true);
		
		List<AbstractRelationshipTemplate> relations = Utils.getOutgoingInfrastructureEdges(nodeTemplate);
		
		if (relations.isEmpty()) {
			return false;
		}
		
		AbstractRelationshipTemplate relation = relations.get(0);
		String relationTemplateInstnaceVar = this.findInstanceVar(context, relation.getId(), false);
		
		String responseVarName = "selectFirstInstance_" + nodeTemplate.getId() + "_FetchRelationInstance_" + relation.getId() + "_" + System.currentTimeMillis();
		QName anyTypeDeclId = context.importQName(new QName("http://www.w3.org/2001/XMLSchema", "any", "xsd"));
		context.addVariable(responseVarName, BPELPlan.VariableType.MESSAGE, anyTypeDeclId);
		
		try {
			Node getRelationInstance = new BPELProcessFragments().generateBPEL4RESTLightGETonURLAsNode(relationTemplateInstnaceVar, responseVarName);
			getRelationInstance = context.importNode(getRelationInstance);
			context.getPrePhaseElement().appendChild(getRelationInstance);
			
			String xpath2Query = "//*[local-name()='Reference' and @*[local-name()='title' and string()='SourceInstanceId']]/@*[local-name()='href']/string()";
			Node fetchSourceInstance = new BPELProcessFragments().createAssignXpathQueryToStringVarFragmentAsNode("selectFirstInstance_" + nodeTemplate.getId() + "_FetchSourceNodeInstance_" + System.currentTimeMillis(), xpath2Query, nodeTemplateInstanceVar);
			fetchSourceInstance = context.importNode(fetchSourceInstance);
			context.getPrePhaseElement().appendChild(fetchSourceInstance);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
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
