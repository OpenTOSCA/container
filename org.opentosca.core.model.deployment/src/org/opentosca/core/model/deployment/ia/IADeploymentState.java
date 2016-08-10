package org.opentosca.core.model.deployment.ia;

/**
 * Deployment and undeployment states of a Implementation Artifact.<br />
 * <br />
 * Copyright 2012 IAAS University of Stuttgart <br />
 * <br />
 * 
 * @author Rene Trefft - trefftre@studi.informatik.uni-stuttgart.de
 * 
 */
public enum IADeploymentState {
	IA_DEPLOYING, IA_DEPLOYED, IA_DEPLOYMENT_FAILED, IA_UNDEPLOYING, IA_UNDEPLOYED, IA_UNDEPLOYMENT_FAILED;
}
