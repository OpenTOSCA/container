package org.opentosca.deployment.verification.job;

import java.net.InetAddress;

import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.VerificationResult;
import org.opentosca.deployment.verification.VerificationContext;
import org.opentosca.deployment.verification.VerificationJob;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PingJob implements VerificationJob {

  private static Logger logger = LoggerFactory.getLogger(PingJob.class);

  @Override
  public VerificationResult execute(final VerificationContext context,
      final AbstractNodeTemplate nodeTemplate, final NodeTemplateInstance nodeTemplateInstance) {
    final VerificationResult result = new VerificationResult();
    result.setName(PingJob.class.getSimpleName());
    result.setNodeTemplateInstance(nodeTemplateInstance);
    result.start();
    try {
      boolean reachable = InetAddress.getByName("google.com").isReachable(1000);
      if (reachable) {
        result.append("Successfully reached hostname");
        result.success();
      } else {
        result.append("Could not reach hostname");
        result.failed();
      }
    } catch (Exception e) {
      logger.info("Could not reach hostname", e);
      result.append("Could not reach hostname: " + e.getMessage());
      result.failed();
    }
    return result;
  }

  @Override
  public boolean canExecute(final AbstractNodeTemplate nodeTemplate) {
    return true;
  }
}
