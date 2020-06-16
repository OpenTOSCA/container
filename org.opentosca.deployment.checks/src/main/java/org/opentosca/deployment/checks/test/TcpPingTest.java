package org.opentosca.deployment.checks.test;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TPolicyTemplate;
import org.opentosca.container.core.next.model.DeploymentTestResult;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.deployment.checks.TestContext;
import org.opentosca.deployment.checks.TestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

public class TcpPingTest implements TestExecutionPlugin {

  public static final QName ANNOTATION =
    new QName("http://opentosca.org/policytypes/annotations/tests", "TcpPingTest");

  private static Logger logger = LoggerFactory.getLogger(TcpPingTest.class);

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

    if (policyTemplate.getProperties() == null) {
      throw new IllegalStateException("Properties of policy template not initialized");
    }

    Set<NodeTemplateInstance> nodes;

    // Input properties
    final Map<String, String> inputProperties = policyTemplate.getProperties().getKVProperties();
    logger.debug("Input properties: {}", inputProperties);

    final String hostnameProperty = "VMIP";
    String port = inputProperties.get("Port");
    final String portProperty = inputProperties.get("PortPropertyName");

    nodes = Sets.newHashSet(nodeTemplateInstance);
    TestUtil.resolveInfrastructureNodes(nodeTemplateInstance, context, nodes);
    final Map<String, String> nodeProperties = TestUtil.map(nodes, n -> n.getPropertiesAsMap());
    logger.debug("Node stack properties: {}", nodeProperties);

    /*
     * Resolve hostname
     */
    final String hostname = nodeProperties.get(hostnameProperty);
    if (Strings.isNullOrEmpty(hostname)) {
      result.append(String.format("Could not determine hostname by property \"%s\".", hostnameProperty));
      result.failed();
      return result;
    }

    /*
     * Resolve port
     */
    if (Strings.isNullOrEmpty(port)) {
      logger.debug("Port not specified, try resolve it by property name...");
      nodes = Sets.newHashSet(nodeTemplateInstance);
      TestUtil.resolveChildNodes(nodeTemplateInstance, context, nodes);
      final Map<String, String> p = TestUtil.map(nodes, n -> n.getPropertiesAsMap());
      port = p.get(portProperty);
      if (Strings.isNullOrEmpty(port)) {
        result.append(String.format("Could not determine port by property \"%s\".", portProperty));
        result.failed();
        return result;
      }
    }

    logger.debug("hostname={}, port={}", hostname, port);
    try (Socket socket = new Socket()) {
      socket.connect(new InetSocketAddress(hostname, Integer.parseInt(port)), 1000);
      result.append(String.format("Successfully pinged hostname \"%s\" on port \"%s\".", hostname, port));
      result.success();
    } catch (final Exception e) {
      logger.error("Error executing test: {}", e.getMessage(), e);
      result.append("Error executing test: " + e.getMessage());
      result.failed();
    }

    logger.info("Test executed: {}", result);
    return result;
  }

  @Override
  public boolean canExecute(final TNodeTemplate nodeTemplate, final TPolicyTemplate policyTemplate) {
    return policyTemplate.getType().equals(ANNOTATION);
  }
}
