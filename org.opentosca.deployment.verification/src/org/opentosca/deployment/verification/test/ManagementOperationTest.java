package org.opentosca.deployment.verification.test;

import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.VerificationResult;
import org.opentosca.deployment.verification.VerificationContext;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractPolicyTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManagementOperationTest implements TestExecutionPlugin {

  private static Logger logger = LoggerFactory.getLogger(ManagementOperationTest.class);

  @Override
  public VerificationResult execute(VerificationContext context, AbstractNodeTemplate nodeTemplate,
      NodeTemplateInstance nodeTemplateInstance, AbstractPolicyTemplate policyTemplate) {

    final VerificationResult result = new VerificationResult();
    result.setName(ManagementOperationTest.class.getSimpleName());
    result.setNodeTemplateInstance(nodeTemplateInstance);
    result.start();

    try {
      // TODO
    } catch (Exception e) {
      logger.error("Error executing test: {}", e.getMessage(), e);
      result.append(String.format("Error executing test: " + e.getMessage()));
      result.failed();
    }

    logger.info("Test executed: {}", result);
    return result;
  }

  @Override
  public boolean canExecute(AbstractNodeTemplate nodeTemplate,
      AbstractPolicyTemplate policyTemplate) {
    // TODO
    return false;
  }
}
