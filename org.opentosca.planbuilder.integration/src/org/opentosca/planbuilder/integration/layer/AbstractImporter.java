package org.opentosca.planbuilder.integration.layer;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.opentosca.planbuilder.AbstractPlanBuilder;
import org.opentosca.planbuilder.AbstractSimplePlanBuilder;
import org.opentosca.planbuilder.AbstractTransformingPlanbuilder;
import org.opentosca.planbuilder.core.bpel.typebasedplanbuilder.BPELBuildProcessBuilder;
import org.opentosca.planbuilder.core.bpel.typebasedplanbuilder.BPELDefrostProcessBuilder;
import org.opentosca.planbuilder.core.bpel.typebasedplanbuilder.BPELFreezeProcessBuilder;
import org.opentosca.planbuilder.core.bpel.typebasedplanbuilder.BPELScaleOutProcessBuilder;
import org.opentosca.planbuilder.core.bpel.typebasedplanbuilder.BPELTerminationProcessBuilder;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;

/**
 * <p>
 * This abstract class is used to define importers
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 * @author Jan Ruthardt - st107755@stud.uni-stuttgart.de
 *
 */
public abstract class AbstractImporter {

	/**
	 * Creates a BuildPlan for the given ServiceTemplate
	 *
	 * @param defs            an AbstractDefinitions
	 * @param csarName        the File name of the CSAR the Definitions document is
	 *                        defined in
	 * @param serviceTemplate a QName representing a ServiceTemplate inside the
	 *                        given Definitions Document
	 * @return a BuildPlan if generating a BuildPlan was successful, else null
	 */
	public AbstractPlan buildPlan(final AbstractDefinitions defs, final String csarName, final QName serviceTemplate) {
		final AbstractSimplePlanBuilder planBuilder = new BPELBuildProcessBuilder();
		return planBuilder.buildPlan(csarName, defs, serviceTemplate);
	}

	protected List<AbstractPlan> buildTransformationPlans(final String sourceCsarName, final AbstractDefinitions sourceDefinitions,final String targetCsarName, final AbstractDefinitions targetDefinitions) {
		final List<AbstractPlan> plans = new ArrayList<AbstractPlan>();
		
		final AbstractTransformingPlanbuilder transformPlanBuilder = new AbstractTransformingPlanbuilder() {
			
			@Override
			public List<AbstractPlan> buildPlans(String sourceCsarName, AbstractDefinitions sourceDefinitions,
					String targetCsarName, AbstractDefinitions targetDefinitions) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public AbstractPlan buildPlan(String sourceCsarName, AbstractDefinitions sourceDefinitions,
					QName sourceServiceTemplateId, String targetCsarName, AbstractDefinitions targetDefinitions,
					QName targetServiceTemplateId) {
				
				AbstractServiceTemplate sourceServTemp = null;				
				AbstractServiceTemplate targetServTemp = null;
				
				for(AbstractServiceTemplate servTemp : sourceDefinitions.getServiceTemplates()) {
					if(servTemp.getQName().equals(sourceServiceTemplateId)) {
						sourceServTemp = servTemp;
						break;
					}
				}
				
				for(AbstractServiceTemplate servTemp : targetDefinitions.getServiceTemplates()) {
					if(servTemp.getQName().equals(targetServiceTemplateId)) {
						targetServTemp = servTemp;
						break;
					}
				}
						
				return this.generateTFOG(sourceCsarName, sourceDefinitions, sourceServTemp, targetCsarName, targetDefinitions, targetServTemp);
			}
		};
		
		plans.add(transformPlanBuilder.buildPlan(sourceCsarName, sourceDefinitions, sourceDefinitions.getServiceTemplates().get(0).getQName(), targetCsarName, targetDefinitions, targetDefinitions.getServiceTemplates().get(0).getQName()));
		
		
		
		return plans;
	}
	
	/**
	 * Generates Plans for ServiceTemplates inside the given Definitions document
	 *
	 * @param defs     an AbstractDefinitions
	 * @param csarName the FileName of the CSAR the given Definitions is contained
	 *                 in
	 * @return a List of Plans
	 */
	public List<AbstractPlan> buildPlans(final AbstractDefinitions defs, final String csarName) {
		final List<AbstractPlan> plans = new ArrayList<>();

		final AbstractSimplePlanBuilder buildPlanBuilder = new BPELBuildProcessBuilder();

		// FIXME: This does not work for me (Michael W. - 2018-02-19)
		// if (!this.hasPolicies(defs)) {
		// buildPlanBuilder = new BPELBuildProcessBuildeplanr();
		// Because policies must be enforced when they are set on the the topology, if
		// the planbuilder doesn't understand them it doesn't generate a plan -> doesn't
		// work for you
		//
		// if (!this.hasPolicies(defs)) {
		// buildPlanBuilder = new BPELBuildProcessBuilder();
		// } else {
		// buildPlanBuilder = new PolicyAwareBPELBuildProcessBuilder();
		// }

		final AbstractSimplePlanBuilder terminationPlanBuilder = new BPELTerminationProcessBuilder();
		final AbstractSimplePlanBuilder scalingPlanBuilder = new BPELScaleOutProcessBuilder();

		final AbstractSimplePlanBuilder freezePlanBuilder = new BPELFreezeProcessBuilder();
		final AbstractSimplePlanBuilder defreezePlanBuilder = new BPELDefrostProcessBuilder();


		plans.addAll(scalingPlanBuilder.buildPlans(csarName, defs));
		plans.addAll(buildPlanBuilder.buildPlans(csarName, defs));
		plans.addAll(terminationPlanBuilder.buildPlans(csarName, defs));
		plans.addAll(freezePlanBuilder.buildPlans(csarName, defs));
		plans.addAll(defreezePlanBuilder.buildPlans(csarName, defs));

		return plans;
	}

	private boolean hasPolicies(final AbstractDefinitions defs) {
		for (final AbstractServiceTemplate serv : defs.getServiceTemplates()) {
			for (final AbstractNodeTemplate nodeTemplate : serv.getTopologyTemplate().getNodeTemplates()) {
				if (!nodeTemplate.getPolicies().isEmpty()) {
					return true;
				}
			}
		}
		return false;
	}

}
