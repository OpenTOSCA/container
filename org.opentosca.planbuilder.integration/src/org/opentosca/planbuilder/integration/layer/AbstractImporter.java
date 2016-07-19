package org.opentosca.planbuilder.integration.layer;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.opentosca.planbuilder.IPlanBuilder;
import org.opentosca.planbuilder.PlanBuilder;
import org.opentosca.planbuilder.TerminationPlanBuilder;
import org.opentosca.planbuilder.model.plan.BuildPlan;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;

/**
 * <p>
 * This abstract class is used to define importers
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 * 
 */
public abstract class AbstractImporter {
	
	/**
	 * Generates BuildPlans for ServiceTemplates, which haven't a BuildPlan
	 * defined, inside the given Definitions document
	 * 
	 * @param defs an AbstractDefinitions
	 * @param csarName the FileName of the CSAR the given Definitions is
	 *            contained in
	 * @return a List of BuildPlans
	 */
	public List<BuildPlan> buildPlans(AbstractDefinitions defs, String csarName) {
		List<BuildPlan> plans = new ArrayList<BuildPlan>();
		
		IPlanBuilder buildPlanBuilder = new PlanBuilder();
		IPlanBuilder terminationPlanBuilder = new TerminationPlanBuilder();
		
		plans.addAll(buildPlanBuilder.buildPlans(csarName, defs));
		plans.addAll(terminationPlanBuilder.buildPlans(csarName, defs));
		return plans;
	}
	
	/**
	 * Creates a BuildPlan for the given ServiceTemplate
	 * 
	 * @param defs an AbstractDefinitions
	 * @param csarName the File name of the CSAR the Definitions document is
	 *            defined in
	 * @param serviceTemplate a QName representing a ServiceTemplate inside the
	 *            given Definitions Document
	 * @return a BuildPlan if generating a BuildPlan was successful, else null
	 */
	public BuildPlan buildPlan(AbstractDefinitions defs, String csarName, QName serviceTemplate) {
		IPlanBuilder planBuilder = new PlanBuilder();
		return planBuilder.buildPlan(csarName, defs, serviceTemplate);
	}
	
}
