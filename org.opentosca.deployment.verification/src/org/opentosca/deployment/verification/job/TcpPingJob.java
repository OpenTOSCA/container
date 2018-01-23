package org.opentosca.deployment.verification.job;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.VerificationResult;
import org.opentosca.deployment.verification.VerificationContext;
import org.opentosca.deployment.verification.VerificationJob;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;

public class TcpPingJob implements VerificationJob {

  @Override
  public VerificationResult execute(final VerificationContext context,
      final AbstractNodeTemplate nodeTemplate, final NodeTemplateInstance nodeTemplateInstance) {
    final VerificationResult result = new VerificationResult();
    result.setName(TcpPingJob.class.getSimpleName());
    result.setNodeTemplateInstance(nodeTemplateInstance);
    result.start();
    try (Socket socket = new Socket()) {
      socket.connect(new InetSocketAddress("google.com", 80), 1000);
      result.success();
    } catch (IOException e) {
      result.append("Could not reach hostname " + e.getMessage());
      result.failed();
    }
    return result;
  }

  @Override
  public boolean canExecute(AbstractNodeTemplate nodeTemplate) {
    return false;
  }
}
