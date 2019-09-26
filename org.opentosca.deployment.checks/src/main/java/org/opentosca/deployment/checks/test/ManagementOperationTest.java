package org.opentosca.deployment.checks.test;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.xml.namespace.QName;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.eclipse.winery.common.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.tosca.*;
import org.opentosca.bus.management.header.MBHeader;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.next.model.DeploymentTestResult;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.utils.Types;
import org.opentosca.deployment.checks.TestContext;
import org.opentosca.deployment.checks.TestUtil;
import org.opentosca.planbuilder.model.tosca.AbstractInterface;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractNodeType;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.planbuilder.model.tosca.AbstractPolicyTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class ManagementOperationTest implements org.opentosca.deployment.checks.test.TestExecutionPlugin {

  public static final QName ANNOTATION =
    new QName("http://opentosca.org/policytypes/annotations/tests", "ManagementOperationTest");

  private static Logger logger = LoggerFactory.getLogger(ManagementOperationTest.class);

  private final ProducerTemplate producer;

  @Inject
  public ManagementOperationTest(CamelContext camelContext) {
    producer = camelContext.createProducerTemplate();
  }

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

    // Input properties
    final Map<String, String> inputProperties = policyTemplate.getProperties().getKVProperties();
    logger.debug("Input properties: {}", inputProperties);

    /*
     * Make sure Management Interface and Management Operations exist on related Node Type
     */
    final String interfaceName = inputProperties.get("InterfaceName");
    final String operationName = inputProperties.get("OperationName");
    final Csar csar = context.getCsar();
    final TNodeType nodeType = (TNodeType)csar.queryRepository(new NodeTypeId(nodeTemplate.getType()));
    if (!checkInterfaceOperationSpecification(nodeType.getInterfaces(), interfaceName, operationName)) {
      result.append("Wrong InterfaceName and/or OperationName specified");
      result.failed();
    }

    /*
     * Try to resolve input parameters from given Node Templates
     */
    final Map<String, String> resolvedInputParameters =
      resolveInputParameters(inputProperties.get("ResolveInputParametersFromNodeTemplate"), context);
    logger.debug("Resolved input parameters: {}", resolvedInputParameters);

    /*
     * Try to parse specified input parameters
     */
    final Map<String, String> parsedInputParameters =
      parseJsonParameters(inputProperties.get("TestInputParameters"));
    logger.debug("Parsed input parameters: {}", parsedInputParameters);

    /*
     * Try to parse expected output parameters
     */
    final Map<String, String> parsedExpectedOutputParameters =
      parseJsonParameters(inputProperties.get("ExpectedOutputParameters"));
    logger.debug("Parsed expected output parameters: {}", parsedExpectedOutputParameters);

    /*
     * Combine input parameters
     */
    final Map<String, String> inputParameters = Maps.newHashMap();
    inputParameters.putAll(resolvedInputParameters);
    inputParameters.putAll(parsedInputParameters);
    logger.debug("Merged input parameters: {}", inputParameters);

    // Filter input parameters so that only the required ones are submitted
    final Set<String> requiredInputParameters =
      getRequiredInputParameters(nodeType.getInterfaces(), interfaceName, operationName);
    final Map<String, Object> body =
      inputParameters.entrySet().stream().filter(e -> requiredInputParameters.contains(e.getKey()))
        .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
    logger.debug("Message body: {}", body);

    try {
      logger.debug("Invoke management operation...");
      final CompletableFuture<Map<String, String>> future =
        invoke(context, nodeTemplate, interfaceName, operationName, body);
      final Map<String, String> output = future.get();
      logger.debug("Received output: {}", output);
      result.success();
      parsedExpectedOutputParameters.entrySet().forEach(e -> {
        final String value = output.get(e.getKey());
        if (value == null) {
          result.failed();
          result.append(String.format("Test failed, expected parameter \"%s\" not present in output",
            e.getKey()));
        } else {
          final String test = value.trim().replace("\n", "").replace("\r", "");
          if (!test.equals(e.getValue())) {
            result.failed();
            result.append(String.format("Test failed, expected \"%s\" but got \"%s\"", e.getValue(),
              value));
          }
        }
      });
      result.append("Output: ");
      output.entrySet().stream().forEach(e -> result.append(e.toString()));
    } catch (final Exception e) {
      logger.error("Error executing test: {}", e.getMessage(), e);
      result.append(String.format("Error executing test: " + e.getMessage()));
      result.failed();
    }

    logger.info("Test executed: {}", result);
    return result;
  }

  private CompletableFuture<Map<String, String>> invoke(final TestContext context,
                                                        final TNodeTemplate nodeTemplate,
                                                        final String interfaceName, final String operationName,
                                                        final Map<String, Object> body) throws Exception {

    final Map<String, Object> headers = new HashMap<>();
    headers.put(MBHeader.CSARID.toString(), context.getServiceTemplateInstance().getCsarId());
    headers.put(MBHeader.SERVICETEMPLATEID_QNAME.toString(), context.getServiceTemplate().getId());
    headers.put(MBHeader.SERVICEINSTANCEID_URI.toString(), new URI(String.valueOf(context.getServiceTemplateInstance().getId())));
    headers.put(MBHeader.NODETEMPLATEID_STRING.toString(), nodeTemplate.getId());
    headers.put(MBHeader.NODEINSTANCEID_STRING.toString(), String.valueOf(context.getNodeTemplateInstance(nodeTemplate).getId()));
    headers.put(MBHeader.INTERFACENAME_STRING.toString(), interfaceName);
    headers.put(MBHeader.OPERATIONNAME_STRING.toString(), operationName);
    headers.put(MBHeader.HASOUTPUTPARAMS_BOOLEAN.toString(), true);
    headers.put(MBHeader.SYNCINVOCATION_BOOLEAN.toString(), true);

    return this.producer.asyncRequestBodyAndHeaders("direct:invokeIA", body, headers, Types.generify(Map.class));
  }

  private Map<String, String> parseJsonParameters(final String parameters) {
    if (parameters == null) {
      return new HashMap<>();
    }
    final Type type = new TypeToken<Map<String, String>>() {
    }.getType();
    final Gson gson = new Gson();
    try {
      return gson.fromJson(parameters, type);
    } catch (final JsonSyntaxException e) {
      logger.error("Could not parse JSON: {}", e.getMessage(), e);
      return new HashMap<>();
    }
  }

  private Map<String, String> resolveInputParameters(final String id, final TestContext context) {
    if (id == null || context == null) {
      return new HashMap<>();
    }
    final Collection<TNodeTemplate> nodeTemplates = context.getNodeTemplates();
    for (final TNodeTemplate nodeTemplate : nodeTemplates) {
      if (nodeTemplate.getId().equals(id)) {
        final NodeTemplateInstance instance = context.getNodeTemplateInstance(nodeTemplate);
        final Set<NodeTemplateInstance> nodes = Sets.newHashSet(instance);
        TestUtil.resolveChildNodes(instance, context, nodes);
        return TestUtil.map(nodes, n -> n.getPropertiesAsMap());
      }
    }
    logger.debug("Could not find Node Template with ID \"{}\"", id);
    return new HashMap<>();
  }

  private boolean checkInterfaceOperationSpecification(final TInterfaces interfaces, final String interfaceName,
                                                       final String operationName) {
    for (final TInterface i : interfaces.getInterface()) {
      if (i.getName().equals(interfaceName)) {
        for (final TOperation o : i.getOperation()) {
          if (o.getName().equals(operationName)) {
            logger.debug("Found specified operation \"{}\" on interface \"{}\"", operationName,
              interfaceName);
            return true;
          }
        }
      }
    }
    logger.debug("Could not find operation \"{}\" on interface \"{}\"", operationName, interfaceName);
    return false;
  }

  private Set<String> getRequiredInputParameters(final TInterfaces nodeTypeInterfaces, final String interfaceName,
                                                 final String operationName) {
    for (final TInterface i : nodeTypeInterfaces.getInterface()) {
      if (i.getName().equals(interfaceName)) {
        for (final TOperation o : i.getOperation()) {
          if (o.getName().equals(operationName)) {
            final Set<String> inputParameters =
              o.getInputParameters().getInputParameter().stream().map(p -> p.getName()).collect(Collectors.toSet());
            logger.debug("Required input parameters of operation \"{}\" ({}): {}", operationName,
              interfaceName, inputParameters);
            return inputParameters;
          }
        }
      }
    }
    logger.debug("Could not find operation \"{}\" on interface \"{}\"", operationName, interfaceName);
    return Sets.newHashSet();
  }

  @Override
  public boolean canExecute(final TNodeTemplate nodeTemplate, final TPolicyTemplate policyTemplate) {
    return policyTemplate.getType().equals(ANNOTATION);
  }
}
