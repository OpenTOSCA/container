package org.opentosca.deployment.checks.test;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TPolicyTemplate;

import org.opentosca.container.core.next.model.DeploymentTestResult;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.deployment.checks.TestContext;

public interface TestExecutionPlugin {

    DeploymentTestResult execute(final TestContext context, final TNodeTemplate nodeTemplate,
                                 final NodeTemplateInstance nodeTemplateInstance,
                                 final TPolicyTemplate policyTemplate);

    boolean canExecute(final TNodeTemplate nodeTemplate, final TPolicyTemplate policyTemplate);
}
