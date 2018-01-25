package org.opentosca.planbuilder.provphase.plugin.ansibleoperation.bpel;

import java.util.Map;

import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.plugins.context.PlanContext;
import org.opentosca.planbuilder.core.plugins.context.Variable;
import org.opentosca.planbuilder.model.tosca.AbstractImplementationArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.planbuilder.model.tosca.AbstractParameter;
import org.opentosca.planbuilder.provphase.plugin.ansibleoperation.bpel.handler.BPELAnsibleOperationPluginHandler;
import org.opentosca.planbuilder.provphase.plugin.ansibleoperation.core.AnsibleOperationPlugin;

/**
 * <p>
 * This class implements a ProvPhase Plugin, in particular to enable
 * provisioning with ansible
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 * @author Michael Zimmermann - michael.zimmermann@iaas.uni-stuttgart.de
 *
 */
public class BPELAnsibleOperationPlugin extends AnsibleOperationPlugin<BPELPlanContext> {

	private BPELAnsibleOperationPluginHandler handler = new BPELAnsibleOperationPluginHandler();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean handle(BPELPlanContext context, AbstractOperation operation, AbstractImplementationArtifact ia) {
		return this.handler.handle(context, operation, ia);
	}

	@Override
	public boolean handle(BPELPlanContext context, AbstractOperation operation, AbstractImplementationArtifact ia,
			Map<AbstractParameter, Variable> param2propertyMapping) {
		return this.handler.handle(context, operation, ia, param2propertyMapping);
	}

	@Override
	public boolean handle(BPELPlanContext context, AbstractOperation operation, AbstractImplementationArtifact ia,
			Map<AbstractParameter, Variable> param2propertyMapping, boolean appendToPrePhase) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean handle(BPELPlanContext context, AbstractOperation operation, AbstractImplementationArtifact ia,
			Map<AbstractParameter, Variable> param2propertyMapping,
			Map<AbstractParameter, Variable> param2PropertyOutputMapping) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean handle(BPELPlanContext context, AbstractOperation operation, AbstractImplementationArtifact ia,
			Map<AbstractParameter, Variable> param2propertyMapping,
			Map<AbstractParameter, Variable> param2PropertyOutputMapping, boolean appendToPrePhase) {
		// TODO Auto-generated method stub
		return false;
	}


}
