package org.opentosca.planbuilder.bpel;

import java.util.ArrayList;
import java.util.List;

import org.opentosca.planbuilder.model.tosca.AbstractDeploymentArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTypeImplementation;
import org.opentosca.planbuilder.plugins.IPlanBuilderPrePhaseDAPlugin;

/**
 * <p>
 * This Class represents a Mapping of DA's of an Implementation Plugins
 * which can handle that with matching InfrastructureNode
 * </p>
 * 
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 * 
 */
class DANodeTypeImplCandidate {
	
	AbstractNodeTypeImplementation impl;
	private AbstractNodeTemplate nodeTemplate;
	List<AbstractDeploymentArtifact> das = new ArrayList<AbstractDeploymentArtifact>();
	List<AbstractNodeTemplate> infraNodes = new ArrayList<AbstractNodeTemplate>();
	List<IPlanBuilderPrePhaseDAPlugin> plugins = new ArrayList<IPlanBuilderPrePhaseDAPlugin>();
	
	
	/**
	 * Constructor determines which NodeTypeImplementation is used
	 * 
	 * @param impl an AbstractNodeTypeImplementation with a DA
	 */
	DANodeTypeImplCandidate(AbstractNodeTemplate nodeTemplate, AbstractNodeTypeImplementation impl) {
		this.impl = impl;
		this.nodeTemplate = nodeTemplate;
	}
	
	/**
	 * Adds a mapping from DA to NodeTemplate with a PrePhaseDAPlugin
	 * 
	 * @param da the DeploymentArtifact which should be provisioned
	 * @param nodeTemplate an InfrastructureNode on which the DA should be
	 *            deployed
	 * @param plugin the PrePhaseDAPlugin which can deploy the DA unto the
	 *            given NodeTemplate
	 */
	void add(AbstractDeploymentArtifact da, AbstractNodeTemplate nodeTemplate, IPlanBuilderPrePhaseDAPlugin plugin) {
		this.das.add(da);
		this.infraNodes.add(nodeTemplate);
		this.plugins.add(plugin);
	}
	
	/**
	 * Checks whether the mappings are valid
	 * 
	 * @return true if all DA's of the NodeTypeImplementation can be
	 *         deployed, else false
	 */
	boolean isValid() {
		return BPELScopeBuilder.calculateEffectiveDAs(this.nodeTemplate, this.impl).size() == this.das.size();
	}
}