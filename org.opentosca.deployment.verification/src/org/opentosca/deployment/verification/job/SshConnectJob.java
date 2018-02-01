package org.opentosca.deployment.verification.job;

import java.io.File;
import java.nio.file.Files;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.VerificationResult;
import org.opentosca.container.core.next.xml.DomUtil;
import org.opentosca.deployment.verification.VerificationContext;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.google.common.base.Strings;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

public class SshConnectJob implements NodeTemplateJob {

  private static Logger logger = LoggerFactory.getLogger(SshConnectJob.class);

  @Override
  public synchronized VerificationResult execute(final VerificationContext context,
      final AbstractNodeTemplate nodeTemplate, final NodeTemplateInstance nodeTemplateInstance) {

    final VerificationResult result = new VerificationResult();
    result.setName(SshConnectJob.class.getSimpleName());
    result.setNodeTemplateInstance(nodeTemplateInstance);
    result.start();

    final Map<String, String> properties = nodeTemplateInstance.getPropertiesAsMap();

    final String hostname = Jobs.resolveHostname(properties);
    if (Strings.isNullOrEmpty(hostname)) {
      result.append("Could not determine appropriate hostname.");
      result.failed();
      return result;
    }
    logger.info("Hostname for SSH connection: {}", hostname);

    final String privateKey = properties.get("vmprivatekey");
    if (Strings.isNullOrEmpty(privateKey)) {
      result.append("Could not determine appropriate private key.");
      result.failed();
      return result;
    }
    logger.info("Found valid private key");

    final String publicKey = properties.get("vmpublickey");
    if (Strings.isNullOrEmpty(publicKey)) {
      result.append("Could not determine appropriate public key.");
      result.failed();
      return result;
    }
    logger.info("Found valid public key");

    final String username = properties.get("vmusername");
    if (Strings.isNullOrEmpty(username)) {
      result.append("Could not determine appropriate username.");
      result.failed();
      return result;
    }
    logger.info("Username for SSH connection: {}", username);

    try {
      final JSch jsch = new JSch();
      final File file = Files.createTempFile("job", ".key").toFile();
      FileUtils.writeStringToFile(file, privateKey);
      jsch.addIdentity(file.getAbsolutePath());
      final Session session = jsch.getSession(username, hostname, 22);
      session.setUserInfo(new DefaultUserInfo());
      session.connect(5000);
      final Channel channel = session.openChannel("exec");
      ((ChannelExec) channel).setCommand("ls");
      channel.connect(1000);
      channel.disconnect();
      session.disconnect();
      FileUtils.deleteQuietly(file);
      result.append(String.format("Successfully connected with SSH to hostname \"%s\".", hostname));
      result.success();
    } catch (Exception e) {
      logger.info("Could not connect with SSH to hostname \"{}\"", hostname, e);
      result.append(String
          .format("Could not connect with SSH to hostname \"%s\": " + e.getMessage(), hostname));
      result.failed();
    }

    logger.info("Job executed: {}", result);
    return result;
  }

  @Override
  public boolean canExecute(final AbstractNodeTemplate nodeTemplate) {

    if (nodeTemplate.getProperties() != null
        && nodeTemplate.getProperties().getDOMElement() != null) {

      final Element el = nodeTemplate.getProperties().getDOMElement();
      final NodeList nodes = el.getChildNodes();

      /*
       * If a node template contains properties to specify a private and public key we derive that
       * we can try connect via SSH.
       */

      if (DomUtil.matchesNodeName(".*privatekey.*", nodes)
          && DomUtil.matchesNodeName(".*publickey.*", nodes)) {
        return true;
      }
    }
    return false;
  }

  private static class DefaultUserInfo implements UserInfo {

    @Override
    public String getPassphrase() {
      return null;
    }

    @Override
    public String getPassword() {
      return null;
    }

    @Override
    public boolean promptPassword(final String message) {
      return true;
    }

    @Override
    public boolean promptPassphrase(final String message) {
      return true;
    }

    @Override
    public boolean promptYesNo(final String message) {
      return true;
    }

    @Override
    public void showMessage(final String message) {
      // not used
    }
  }
}
