package org.opentosca.deployment.checks.test;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TPolicyTemplate;

import org.opentosca.container.core.next.model.DeploymentTestResult;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.deployment.checks.TestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlConnectionTest implements TestExecutionPlugin {

    public static final QName ANNOTATION =
        new QName("http://opentosca.org/policytypes/annotations/tests", "SqlConnectionTest");

    private static final Logger logger = LoggerFactory.getLogger(SqlConnectionTest.class);

    @Override
    public DeploymentTestResult execute(final TestContext context, final TNodeTemplate nodeTemplate,
                                        final NodeTemplateInstance nodeTemplateInstance,
                                        final TPolicyTemplate policyTemplate) {

        logger.debug("Execute test \"{}\" for node template \"{}\" (instance={}) based on policy template \"{}\"",
            this.getClass().getSimpleName(), nodeTemplate.getId(), nodeTemplateInstance.getId(),
            policyTemplate.getId());

        final DeploymentTestResult result = new DeploymentTestResult();
        result.setName(policyTemplate.getId());
        result.setNodeTemplateInstance(nodeTemplateInstance);
        result.start();

        // TODO

        result.failed();
        logger.info("Test executed: {}", result);
        return result;
    }

    @Override
    public boolean canExecute(final TNodeTemplate nodeTemplate, final TPolicyTemplate policyTemplate) {

        return policyTemplate.getType().equals(ANNOTATION);
    }
}
