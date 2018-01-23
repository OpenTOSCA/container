package org.opentosca.deployment.verification.job;

import java.net.InetAddress;
import java.util.Map;

import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.VerificationResult;
import org.opentosca.container.core.next.xml.DomUtil;
import org.opentosca.deployment.verification.VerificationContext;
import org.opentosca.deployment.verification.VerificationJob;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.google.common.base.Strings;

public class PingJob implements VerificationJob {

  private static Logger logger = LoggerFactory.getLogger(PingJob.class);

  @Override
  public VerificationResult execute(final VerificationContext context,
      final AbstractNodeTemplate nodeTemplate, final NodeTemplateInstance nodeTemplateInstance) {

    final VerificationResult result = new VerificationResult();
    result.setName(PingJob.class.getSimpleName());
    result.setNodeTemplateInstance(nodeTemplateInstance);
    result.start();

    final Map<String, String> properties = nodeTemplateInstance.getPlanProperties();

    String hostname = properties.get("hostname");
    if (Strings.isNullOrEmpty(hostname)) {
      hostname = properties.get("host");
    }
    if (Strings.isNullOrEmpty(hostname)) {
      hostname = properties.get("ipaddress");
    }
    if (Strings.isNullOrEmpty(hostname)) {
      hostname = properties.get("ip_address");
    }
    if (Strings.isNullOrEmpty(hostname)) {
      hostname = properties.get("containerip");
    }
    if (Strings.isNullOrEmpty(hostname)) {
      result.append("Could not determine appropriate hostname to verify.");
      result.failed();
      return result;
    }
    logger.info("Determined hostname: {}", hostname);

    try {
      boolean reachable = InetAddress.getByName(hostname).isReachable(1000);
      if (reachable) {
        result.append(String.format("Successfully pinged hostname \"%s\".", hostname));
        result.success();
      } else {
        result.append(String.format("Could not ping hostname \"%s\".", hostname));
        result.failed();
      }
    } catch (Exception e) {
      logger.info("Could not ping hostname \"{}\"", hostname, e);
      result.append(String.format("Could not ping hostname \"%s\": " + e.getMessage(), hostname));
      result.failed();
    }
    return result;
  }

  @Override
  public boolean canExecute(final AbstractNodeTemplate nodeTemplate) {
    final Element el = nodeTemplate.getProperties().getDOMElement();
    final NodeList nodes = el.getChildNodes();

    /*
     * This job can be executed if the node template contains a property regarding a hostname or IP
     * address.
     */

    if (DomUtil.matchesNodeName(".*host.*", nodes)) {
      return true;
    }
    if (DomUtil.matchesNodeName(".*ip.*", nodes)) {
      return true;
    }
    return false;
  }
}
