package org.opentosca.bus.management.service.impl.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.opentosca.bus.management.service.impl.servicehandler.ServiceHandler;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.tosca.convention.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Utility class which contains methods to handle input parameters for operation invocations by the
 * Management Bus.<br>
 * <br>
 *
 * Copyright 2018 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 * @author Benjamin Weder - st100495@stud.uni-stuttgart.de
 *
 */
public class ParameterHandler {

    private final static Logger LOG = LoggerFactory.getLogger(ParameterHandler.class);

    /**
     * Updates missing input parameters with instance data. The provided input parameters have
     * priority, which means if one parameter is provided and found in the instance data, then the
     * provided parameter is used.
     *
     * @param inputParams the set of input parameters
     * @param csarID
     * @param nodeTemplateInstance
     * @param neededInterface
     * @param neededOperation
     *
     * @return the updated input parameters.
     */
    public static HashMap<String, String> updateInputParams(final HashMap<String, String> inputParams,
                                                            final CSARID csarID,
                                                            final NodeTemplateInstance nodeTemplateInstance,
                                                            final String neededInterface,
                                                            final String neededOperation) {

        ParameterHandler.LOG.debug("{} inital input parameters for operation: {} found: {}", inputParams.size(),
                                   neededOperation, inputParams.toString());

        final List<String> expectedParams =
            ParameterHandler.getExpectedInputParams(csarID, nodeTemplateInstance.getTemplateType(), neededInterface,
                                                    neededOperation);

        ParameterHandler.LOG.debug("Operation: {} expects {} parameters: {}", neededOperation, expectedParams.size(),
                                   expectedParams.toString());

        if (!expectedParams.isEmpty()) {

            ParameterHandler.LOG.debug("Getting instance data for NodeTemplateInstance ID: {} ...",
                                       nodeTemplateInstance.getId());

            final Map<String, String> propertiesMap = nodeTemplateInstance.getPropertiesAsMap();

            if (propertiesMap != null) {

                ParameterHandler.LOG.debug("Found following properties in the instance data:");

                for (final String key : propertiesMap.keySet()) {
                    ParameterHandler.LOG.debug("Prop: " + key + " Val: " + propertiesMap.get(key));
                }

                final List<String> supportedIPPropertyNames = Utils.getSupportedVirtualMachineIPPropertyNames();
                final List<String> supportedInstanceIdPropertyNames =
                    Utils.getSupportedVirtualMachineInstanceIdPropertyNames();
                final List<String> supportedPasswordPropertyNames =
                    Utils.getSupportedVirtualMachineLoginPasswordPropertyNames();
                final List<String> supportedUsernamePropertyNames =
                    Utils.getSupportedVirtualMachineLoginUserNamePropertyNames();

                String prop;
                // Check for property convention
                for (final String expectedParam : expectedParams) {

                    if (supportedIPPropertyNames.contains(expectedParam)) {
                        ParameterHandler.LOG.debug("Supported IP-Property found.");
                        prop = ParameterHandler.getSupportedProperty(supportedIPPropertyNames, propertiesMap);

                        if (prop != null) {
                            ParameterHandler.putOnlyIfNotSet(inputParams, expectedParam, prop);
                        }

                    } else if (supportedInstanceIdPropertyNames.contains(expectedParam)) {
                        ParameterHandler.LOG.debug("Supported InstanceID-Property found.");
                        prop = ParameterHandler.getSupportedProperty(supportedInstanceIdPropertyNames, propertiesMap);

                        if (prop != null) {
                            ParameterHandler.putOnlyIfNotSet(inputParams, expectedParam, prop);
                        }

                    } else if (supportedPasswordPropertyNames.contains(expectedParam)) {
                        ParameterHandler.LOG.debug("Supported Password-Property found.");
                        prop = ParameterHandler.getSupportedProperty(supportedPasswordPropertyNames, propertiesMap);

                        if (prop != null) {
                            ParameterHandler.putOnlyIfNotSet(inputParams, expectedParam, prop);
                        }

                    } else if (supportedUsernamePropertyNames.contains(expectedParam)) {
                        ParameterHandler.LOG.debug("Supported Username-Property found.");
                        prop = ParameterHandler.getSupportedProperty(supportedUsernamePropertyNames, propertiesMap);

                        if (prop != null) {
                            ParameterHandler.putOnlyIfNotSet(inputParams, expectedParam, prop);
                        }

                    } else {

                        for (final String propName : propertiesMap.keySet()) {
                            if (expectedParam.equals(propName)) {
                                ParameterHandler.putOnlyIfNotSet(inputParams, expectedParam,
                                                                 propertiesMap.get(propName));
                            }
                        }
                    }
                }

                ParameterHandler.LOG.debug("Final {} input parameters for operation {} : {}", inputParams.size(),
                                           neededOperation, inputParams.toString());
            } else {
                ParameterHandler.LOG.debug("No stored instance data found.");
            }
        }

        return inputParams;
    }

    /**
     * Adds an entry to the given map if it does not already contain an entry with the same key.
     *
     * @param map the map to update
     * @param key the key of the entry to update
     * @param value the value which is set if there is not yet an entry with the key
     */
    private static void putOnlyIfNotSet(final Map<String, String> map, final String key, final String value) {
        if (!map.containsKey(key)) {
            map.put(key, value);
        }
    }

    /**
     * Returns the input parameters of the given operation which are specified in the TOSCA
     * definitions.
     *
     * @param csarID ID of the CSAR which contains the NodeType with the operation
     * @param nodeTypeID ID of the NodeType which contains the operation
     * @param interfaceName the name of the interface which contains the operation
     * @param operationName the operation name for which the parameters are searched
     *
     * @return specified input parameters of the operation
     */
    private static List<String> getExpectedInputParams(final CSARID csarID, final QName nodeTypeID,
                                                       final String interfaceName, final String operationName) {

        ParameterHandler.LOG.debug("Fetching expected input params of " + operationName + " in interface "
            + interfaceName);
        final List<String> inputParams = new ArrayList<>();

        ParameterHandler.LOG.debug("Checking for params with NodeType " + nodeTypeID);
        if (ServiceHandler.toscaEngineService.hasOperationOfATypeSpecifiedInputParams(csarID, nodeTypeID, interfaceName,
                                                                                      operationName)) {

            final Node definedInputParameters =
                ServiceHandler.toscaEngineService.getInputParametersOfANodeTypeOperation(csarID, nodeTypeID,
                                                                                         interfaceName, operationName);

            if (definedInputParameters != null) {

                final NodeList definedInputParameterList = definedInputParameters.getChildNodes();

                for (int i = 0; i < definedInputParameterList.getLength(); i++) {

                    final Node currentNode = definedInputParameterList.item(i);

                    if (currentNode.getNodeType() == Node.ELEMENT_NODE) {

                        final String name = ((Element) currentNode).getAttribute("name");

                        inputParams.add(name);

                    }
                }
            }
        }
        return inputParams;
    }

    /**
     * @param supportedProperties
     * @param propertiesMap
     *
     * @return convention defined properties.
     */
    private static String getSupportedProperty(final List<String> supportedProperties,
                                               final Map<String, String> propertiesMap) {

        for (final String supportedProperty : supportedProperties) {

            if (propertiesMap.containsKey(supportedProperty)) {
                final String prop = propertiesMap.get(supportedProperty);
                ParameterHandler.LOG.debug("Supported convention property: {} found: {}", supportedProperty, prop);
                return prop;
            }
        }
        return null;
    }
}
