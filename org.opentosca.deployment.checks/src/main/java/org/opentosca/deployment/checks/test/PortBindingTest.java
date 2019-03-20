package org.opentosca.deployment.checks.test;

import javax.xml.namespace.QName;

import org.opentosca.container.core.next.model.DeploymentTestResult;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.deployment.checks.TestContext;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractPolicyTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PortBindingTest implements TestExecutionPlugin {

  public static final QName ANNOTATION =
    new QName("http://opentosca.org/policytypes/annotations/tests", "PortBindingTest");

  private static Logger logger = LoggerFactory.getLogger(PortBindingTest.class);

  @Override
  public DeploymentTestResult execute(final TestContext context, final AbstractNodeTemplate nodeTemplate,
                                      final NodeTemplateInstance nodeTemplateInstance,
                                      final AbstractPolicyTemplate policyTemplate) {

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
  public boolean canExecute(final AbstractNodeTemplate nodeTemplate, final AbstractPolicyTemplate policyTemplate) {

    if (policyTemplate.getType().getId().equals(ANNOTATION)) {
      return true;
    }

    return false;
  }
}
