package org.opentosca.core.model.deployment.process;

/**
 * Deployment states of a THOR file.<br />
 * <br />
 * Copyright 2012 IAAS University of Stuttgart <br />
 * <br />
 * 
 * @author Markus Fischer fischema@studi.informatik.uni-stuttgart.de
 * 
 */
public enum DeploymentProcessState {
	STORED, TOSCAPROCESSING_ACTIVE, TOSCA_PROCESSED, IA_DEPLOYMENT_ACTIVE, IAS_DEPLOYED, PLAN_DEPLOYMENT_ACTIVE, PLANS_DEPLOYED
}
