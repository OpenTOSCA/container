package org.opentosca.planbuilder.provphase.plugin.ansibleoperation.core;

import javax.xml.namespace.QName;

import org.opentosca.planbuilder.core.plugins.IPlanBuilderProvPhaseOperationPlugin;
import org.opentosca.planbuilder.core.plugins.IPlanBuilderProvPhaseParamOperationPlugin;
import org.opentosca.planbuilder.core.plugins.context.PlanContext;

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
public abstract class AnsibleOperationPlugin<T extends PlanContext>
		implements IPlanBuilderProvPhaseOperationPlugin<T>, IPlanBuilderProvPhaseParamOperationPlugin<T> {

	private static final String PLUGIN_ID = "OpenTOSCA ProvPhase AnsibleOperation Plugin v0.1";
	private static final QName ANSIBLE_ARTIFACTTYPE = new QName("http://opentosca.org/artifacttypes", "Ansible");

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canHandle(QName artifactType) {
		return AnsibleOperationPlugin.ANSIBLE_ARTIFACTTYPE.equals(artifactType);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getID() {
		return AnsibleOperationPlugin.PLUGIN_ID;
	}
}
