package org.opentosca.planbuilder.integration.layer;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.opentosca.planbuilder.AbstractPlanBuilder;
import org.opentosca.planbuilder.core.bpel.BPELBuildProcessBuilder;
import org.opentosca.planbuilder.core.bpel.BPELScaleOutProcessBuilder;
import org.opentosca.planbuilder.core.bpel.BPELTerminationProcessBuilder;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
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
	 * Creates a BuildPlan for the given ServiceTemplate
	 *
	 * @param defs
	 *            an AbstractDefinitions
	 * @param csarName
	 *            the File name of the CSAR the Definitions document is defined in
	 * @param serviceTemplate
	 *            a QName representing a ServiceTemplate inside the given
	 *            Definitions Document
	 * @return a BuildPlan if generating a BuildPlan was successful, else null
	 */
	public AbstractPlan buildPlan(AbstractDefinitions defs, String csarName, QName serviceTemplate) {
		AbstractPlanBuilder planBuilder = new BPELBuildProcessBuilder();
		return planBuilder.buildPlan(csarName, defs, serviceTemplate);
	}

	/**
	 * Generates Plans for ServiceTemplates inside the given Definitions document
	 *
	 * @param defs
	 *            an AbstractDefinitions
	 * @param csarName
	 *            the FileName of the CSAR the given Definitions is contained in
	 * @return a List of Plans
	 */
	public List<AbstractPlan> buildPlans(AbstractDefinitions defs, String csarName) {
		List<AbstractPlan> plans = new ArrayList<AbstractPlan>();

		AbstractPlanBuilder buildPlanBuilder = new BPELBuildProcessBuilder();
		AbstractPlanBuilder terminationPlanBuilder = new BPELTerminationProcessBuilder();
		AbstractPlanBuilder scalingPlanBuilder = new BPELScaleOutProcessBuilder();

		plans.addAll(scalingPlanBuilder.buildPlans(csarName, defs));
		plans.addAll(buildPlanBuilder.buildPlans(csarName, defs));
		plans.addAll(terminationPlanBuilder.buildPlans(csarName, defs));
		return plans;
	}

}
