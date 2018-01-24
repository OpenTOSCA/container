package org.opentosca.deployment.verification.job;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Map;
import java.util.Set;

import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.VerificationResult;
import org.opentosca.container.core.next.xml.DomUtil;
import org.opentosca.deployment.verification.VerificationContext;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

public class TcpPingJob implements NodeTemplateJob {

  @Override
  public VerificationResult execute(final VerificationContext context,
      final AbstractNodeTemplate nodeTemplate, final NodeTemplateInstance nodeTemplateInstance) {

    final VerificationResult result = new VerificationResult();
    result.setName(TcpPingJob.class.getSimpleName());
    result.setNodeTemplateInstance(nodeTemplateInstance);
    result.start();

    final Set<NodeTemplateInstance> stackNodes = Sets.newHashSet(nodeTemplateInstance);
    // Resolve all instances underneath the current instance
    Jobs.resolveInfrastructureNodes(nodeTemplateInstance, context, stackNodes);

    final Map<String, String> properties = Jobs.mergePlanProperties(stackNodes);

    final Integer port = Jobs.resolvePort(properties);
    final String hostname = Jobs.resolveHostname(properties);
    if (port == null || port <= 0 || port > 65535) {
      result.append("Could not determine appropriate port.");
      result.failed();
      return result;
    }
    if (Strings.isNullOrEmpty(hostname)) {
      result.append("Could not determine appropriate hostname.");
      result.failed();
      return result;
    }

    try (Socket socket = new Socket()) {
      socket.connect(new InetSocketAddress(hostname, port), 1000);
      result.append(
          String.format("Successfully pinged hostname \"%s\" on port \"%s\".", hostname, port));
      result.success();
    } catch (IOException e) {
      result
          .append(String.format("Could not ping hostname \"%s\" on port \"%s\".", hostname, port));
      result.failed();
    }
    return result;
  }

  @Override
  public boolean canExecute(final AbstractNodeTemplate nodeTemplate) {

    final Element el = nodeTemplate.getProperties().getDOMElement();
    final NodeList nodes = el.getChildNodes();

    /*
     * This job can be executed if the node template contains a property defining a port. The
     * required hostname is determined from the topology graph during runtime.
     */

    if (DomUtil.matchesNodeName(".*port.*", nodes)) {
      return true;
    }
    return false;
  }
}
