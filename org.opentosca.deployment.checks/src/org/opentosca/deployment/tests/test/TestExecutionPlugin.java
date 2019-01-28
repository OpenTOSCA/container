package org.opentosca.deployment.tests.test;

import org.opentosca.container.core.next.model.DeploymentTestResult;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.deployment.tests.TestContext;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractPolicyTemplate;

public interface TestExecutionPlugin {

    DeploymentTestResult execute(final TestContext context, final AbstractNodeTemplate nodeTemplate,
                                 final NodeTemplateInstance nodeTemplateInstance,
                                 final AbstractPolicyTemplate policyTemplate);

    boolean canExecute(final AbstractNodeTemplate nodeTemplate, final AbstractPolicyTemplate policyTemplate);
}
