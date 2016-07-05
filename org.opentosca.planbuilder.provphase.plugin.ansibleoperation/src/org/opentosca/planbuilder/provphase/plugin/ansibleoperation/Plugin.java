package org.opentosca.planbuilder.provphase.plugin.ansibleoperation;

import java.util.Map;

import javax.xml.namespace.QName;

import org.opentosca.planbuilder.model.tosca.AbstractImplementationArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.planbuilder.model.tosca.AbstractParameter;
import org.opentosca.planbuilder.plugins.IPlanBuilderProvPhaseOperationPlugin;
import org.opentosca.planbuilder.plugins.IPlanBuilderProvPhaseParamOperationPlugin;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext.Variable;
import org.opentosca.planbuilder.provphase.plugin.ansibleoperation.handler.Handler;

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
public class Plugin implements IPlanBuilderProvPhaseOperationPlugin, IPlanBuilderProvPhaseParamOperationPlugin {

	private QName ansibleArtifactType = new QName("http://opentosca.org/artifacttypes", "Ansible");
	private Handler handler = new Handler();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getID() {
		return "OpenTOSCA ProvPhase AnsibleOperation Plugin v0.1";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canHandle(QName artifactType) {
		return artifactType.equals(this.ansibleArtifactType);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean handle(TemplatePlanContext context, AbstractOperation operation, AbstractImplementationArtifact ia) {
		return this.handler.handle(context, operation, ia);
	}

	@Override
	public boolean handle(TemplatePlanContext context, AbstractOperation operation, AbstractImplementationArtifact ia,
			Map<AbstractParameter, Variable> param2propertyMapping) {
		return this.handler.handle(context, operation, ia, param2propertyMapping);
	}

}
