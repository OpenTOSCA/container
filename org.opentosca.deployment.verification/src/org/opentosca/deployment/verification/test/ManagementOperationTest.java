package org.opentosca.deployment.verification.test;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import javax.xml.namespace.QName;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.opentosca.bus.management.header.MBHeader;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.VerificationResult;
import org.opentosca.container.core.next.utils.Types;
import org.opentosca.deployment.verification.Activator;
import org.opentosca.deployment.verification.VerificationContext;
import org.opentosca.deployment.verification.VerificationUtil;
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

public class ManagementOperationTest implements TestExecutionPlugin {

    public static final QName ANNOTATION_MANAGEMENT_OPERATION_TEST = new QName(
            "http://opentosca.org/policytypes/annotations/tests", "ManagementOperationTest");

    private static Logger logger = LoggerFactory.getLogger(ManagementOperationTest.class);

    private final ProducerTemplate producer;


    public ManagementOperationTest() {
        final DefaultCamelContext camelContext = Activator.getCamelContext();
        this.producer = camelContext.createProducerTemplate();
    }

    @Override
    public VerificationResult execute(final VerificationContext context,
            final AbstractNodeTemplate nodeTemplate,
            final NodeTemplateInstance nodeTemplateInstance,
            final AbstractPolicyTemplate policyTemplate) {

        final VerificationResult result = new VerificationResult();
        result.setName(policyTemplate.getId());
        result.setNodeTemplateInstance(nodeTemplateInstance);
        result.start();

        if (policyTemplate.getProperties() == null) {
            throw new IllegalStateException("Properties of policy template not initialized");
        }

        // Input properties
        final Map<String, String> inputProperties = policyTemplate.getProperties().asMap();
        logger.debug("Input properties: {}", inputProperties);

        /*
         * Make sure Management Interface and Management Operations exist on related Node Type
         */
        final String interfaceName = inputProperties.get("InterfaceName");
        final String operationName = inputProperties.get("OperationName");
        if (!checkInterfaceOperationSpecification(nodeTemplate.getType(), interfaceName,
                operationName)) {
            result.append("Wrong InterfaceName and/or OperationName specified");
            result.failed();
        }

        /*
         * Try to resolve input parameters from given Node Templates
         */
        final Map<String, String> resolvedInputParameters =
                resolveInputParameters(inputProperties.get("ResolveInputParameters"), context);
        logger.debug("Resolved input parameters: {}", resolvedInputParameters);

        /*
         * Try to parse specified input parameters
         */
        final Map<String, String> parsedInputParameters =
                parseInputParameters(inputProperties.get("TestInputParameters"));
        logger.debug("Parsed input parameters: {}", parsedInputParameters);

        /*
         * Combine input parameters
         */
        final Map<String, String> inputParameters = Maps.newHashMap();
        inputParameters.putAll(resolvedInputParameters);
        inputParameters.putAll(parsedInputParameters);
        logger.debug("Merged input parameters: {}", inputParameters);

        try {
            logger.debug("Invoke management operation...");
            final CompletableFuture<Map<String, String>> future =
                    invoke(context, nodeTemplate, interfaceName, operationName, inputParameters);
            final Map<String, String> output = future.get();
            logger.debug("Received output: {}", output);
            output.entrySet().stream().forEach(e -> result.append(e.toString()));
            result.success();
        } catch (Exception e) {
            logger.error("Error executing test: {}", e.getMessage(), e);
            result.append(String.format("Error executing test: " + e.getMessage()));
            result.failed();
        }

        logger.info("Test executed: {}", result);
        return result;
    }

    private CompletableFuture<Map<String, String>> invoke(final VerificationContext context,
            final AbstractNodeTemplate nodeTemplate, final String interfaceName,
            final String operationName, final Map<String, String> body) throws Exception {

        final Map<String, Object> headers = new HashMap<>();
        headers.put(MBHeader.CSARID.toString(), context.getServiceTemplateInstance().getCsarId());
        headers.put(MBHeader.SERVICETEMPLATEID_QNAME.toString(),
                context.getServiceTemplate().getQName());
        headers.put(MBHeader.SERVICEINSTANCEID_URI.toString(),
                new URI(String.valueOf(context.getServiceTemplateInstance().getId())));
        headers.put(MBHeader.NODETEMPLATEID_STRING.toString(), nodeTemplate.getId());
        headers.put(MBHeader.NODEINSTANCEID_STRING.toString(),
                String.valueOf(context.getNodeTemplateInstance(nodeTemplate).getId()));
        headers.put(MBHeader.INTERFACENAME_STRING.toString(), interfaceName);
        headers.put(MBHeader.OPERATIONNAME_STRING.toString(), operationName);
        headers.put(MBHeader.HASOUTPUTPARAMS_BOOLEAN.toString(), true);
        headers.put(MBHeader.SYNCINVOCATION_BOOLEAN.toString(), true);

        return producer.asyncRequestBodyAndHeaders("direct:invokeIA", body, headers,
                Types.generify(Map.class));
    }

    private Map<String, String> parseInputParameters(final String parameters) {
        final Type type = new TypeToken<Map<String, String>>() {}.getType();
        final Gson gson = new Gson();
        try {
            return gson.fromJson(parameters, type);
        } catch (JsonSyntaxException e) {
            logger.error("Could not parse JSON: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    private Map<String, String> resolveInputParameters(final String id,
            final VerificationContext context) {
        final Collection<AbstractNodeTemplate> nodeTemplates = context.getNodeTemplates();
        for (AbstractNodeTemplate nodeTemplate : nodeTemplates) {
            if (nodeTemplate.getId().equals(id)) {
                final NodeTemplateInstance instance = context.getNodeTemplateInstance(nodeTemplate);
                final Set<NodeTemplateInstance> nodes = Sets.newHashSet(instance);
                VerificationUtil.resolveChildNodes(instance, context, nodes);
                return VerificationUtil.map(nodes, n -> n.getPropertiesAsMap());
            }
        }
        logger.debug("Could not find Node Template with ID \"{}\"", id);
        return new HashMap<>();
    }

    private boolean checkInterfaceOperationSpecification(final AbstractNodeType nodeType,
            final String interfaceName, final String operationName) {
        for (AbstractInterface i : nodeType.getInterfaces()) {
            if (i.getName().equals(interfaceName)) {
                for (AbstractOperation o : i.getOperations()) {
                    if (o.getName().equals(operationName)) {
                        logger.debug("Found specified operation \"{}\" on interface \"{}\"",
                                operationName, interfaceName);
                        return true;
                    }
                }
            }
        }
        logger.debug(
                "Could not find operation \"{}\" on interface \"{}\", not specified in Node Type {}",
                operationName, interfaceName, nodeType.getId());
        return false;
    }

    @Override
    public boolean canExecute(final AbstractNodeTemplate nodeTemplate,
            final AbstractPolicyTemplate policyTemplate) {

        if (policyTemplate.getType().getId().equals(ANNOTATION_MANAGEMENT_OPERATION_TEST)) {
            return true;
        }

        return false;
    }
}
