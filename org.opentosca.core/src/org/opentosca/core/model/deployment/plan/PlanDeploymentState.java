package org.opentosca.core.model.deployment.plan;

/**
 * Deployment and undeployment states of a Plan.
 */
public enum PlanDeploymentState {
	PLAN_DEPLOYING, PLAN_DEPLOYED, PLAN_DEPLOYMENT_FAILED, PLAN_UNDEPLOYING, PLAN_UNDEPLOYED, PLAN_UNDEPLOYMENT_FAILED;
}
