package org.opentosca.bus.management.service.impl.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.xml.namespace.QName;

import org.opentosca.bus.management.service.impl.servicehandler.ServiceHandler;
import org.opentosca.bus.management.utils.MBUtils;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.RelationshipTemplateInstance;
import org.opentosca.container.core.tosca.convention.Types;
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
 * Copyright 2019 IAAS University of Stuttgart
 */
public class ParameterHandler {

    private final static Logger LOG = LoggerFactory.getLogger(ParameterHandler.class);

    /**
     * Updates missing input parameters for a operation on a NodeTemplate or RelationshipTemplate with
     * instance data. The provided input parameters have priority, which means if one parameter is
     * provided and found in the instance data, then the provided parameter is used. <br>
     * <br>
     *
     * If nodeTemplateInstance and relationshipTemplateInstance are provided, the update will be
     * performed based on the RelationshipTemplate. If one of the parameters is <tt>null</tt> the other
     * one is used to perform the update. If both are <tt>null</tt> an update is not possible.
     *
     * @param inputParams the set of provided input parameters
     * @param csarID of the CSAR containing the NodeTemplate/RelationshipTemplate
     * @param nodeTemplateInstance the NodeTemplateInstance object which is used as entry point to the
     *        stored instance data. If it does not contain all needed parameters the search is continued
     *        downwards in the topology
     * @param relationshipTemplateInstance the RelationshipTemplateInstance object which is used as
     *        entry point to the stored instance data. The update is performed based on the
     *        RelationshipTemplate and the source/target stack of the topology
     * @param neededInterface the interface of the operation for which the update is performed
     * @param neededOperation the operation for which the update is performed
     *
     * @return the updated input parameters.
     */
    public static HashMap<String, String> updateInputParams(final HashMap<String, String> inputParams,
                                                            final CSARID csarID,
                                                            final NodeTemplateInstance nodeTemplateInstance,
                                                            final RelationshipTemplateInstance relationshipTemplateInstance,
                                                            final String neededInterface,
                                                            final String neededOperation) {

        if (Objects.nonNull(relationshipTemplateInstance)) {
            return updateInputParamsForRelationshipTemplate(inputParams, csarID, relationshipTemplateInstance,
                                                            neededInterface, neededOperation);

        } else if (Objects.nonNull(nodeTemplateInstance)) {
            return updateInputParamsForNodeTemplate(inputParams, csarID, nodeTemplateInstance, neededInterface,
                                                    neededOperation);
        } else {
            LOG.warn("Unable to update input parameters with nodeTemplateInstance and relationshipTemplateInstance equal to null!");
            return inputParams;
        }
    }

    /**
     * Updates missing input parameters for a operation on a NodeTemplate with instance data. The
     * provided input parameters have priority, which means if one parameter is provided and found in
     * the instance data, then the provided parameter is used.
     *
     * @param inputParams the set of provided input parameters
     * @param csarID of the CSAR containing the NodeTemplate
     * @param nodeTemplateInstance the NodeTemplate instance object
     * @param neededInterface the interface of the operation for which the update is performed
     * @param neededOperation the operation for which the update is performed
     *
     * @return the updated input parameters.
     */
    private static HashMap<String, String> updateInputParamsForNodeTemplate(final HashMap<String, String> inputParams,
                                                                            final CSARID csarID,
                                                                            NodeTemplateInstance nodeTemplateInstance,
                                                                            final String neededInterface,
                                                                            final String neededOperation) {

        Objects.requireNonNull(nodeTemplateInstance);

        LOG.debug("Updating input params for NodeTemplateInstance ID: {}", nodeTemplateInstance.getId());

        LOG.debug("{} inital input parameters for operation: {} found: {}", inputParams.size(), neededOperation,
                  inputParams.toString());

        // check if operation has input params at all
        final Set<String> unsetParameters =
            getExpectedInputParams(csarID, nodeTemplateInstance.getTemplateType(), neededInterface, neededOperation);
        if (unsetParameters.isEmpty()) {
            LOG.debug("No input params defined for this operation.");
            return inputParams;
        }

        LOG.debug("Operation: {} expects {} parameters: {}", neededOperation, unsetParameters.size(),
                  unsetParameters.toString());

        // use convention names for properties
        final List<String> supportedIPPropertyNames = Utils.getSupportedVirtualMachineIPPropertyNames();
        final List<String> supportedInstanceIdPropertyNames = Utils.getSupportedVirtualMachineInstanceIdPropertyNames();
        final List<String> supportedPasswordPropertyNames =
            Utils.getSupportedVirtualMachineLoginPasswordPropertyNames();
        final List<String> supportedUsernamePropertyNames =
            Utils.getSupportedVirtualMachineLoginUserNamePropertyNames();

        // remove already defined properties from the set
        inputParams.keySet().stream().forEach(param -> unsetParameters.remove(param));

        // search for parameters downwards in the topology until all are set
        while (!unsetParameters.isEmpty()) {
            if (nodeTemplateInstance.getTemplateType().equals(Types.abstractOperatingSystemNodeType)) {
                final Map<String, String> propMap = nodeTemplateInstance.getPropertiesAsMap();
                if (Objects.nonNull(propMap)) {
                    for (final String key : propMap.keySet()) {
                        if (key.equals("instanceRef")) {
                            final String value = propMap.get(key);
                            final String[] setOfValues = value.split(",");
                            try {
                                final Long serviceTemplateInstanceId = Long.parseLong(setOfValues[0]);
                                final String nodeTemplateId = setOfValues[1];
                                // final Long nodeTemplateInstanceId = Long.parseLong(setOfValues[2]);
                                LOG.debug("Found instanceRef Property: " + key + " with value: " + propMap.get(key));

                                // TODO: try to fetch node instance by node instance id
                                // replace OS node template instance by selected node template instance
                                nodeTemplateInstance =
                                    MBUtils.getNodeTemplateInstance(serviceTemplateInstanceId, nodeTemplateId);
                            }
                            catch (final NumberFormatException e) {
                                LOG.error(e.getMessage());
                            }

                        }
                    }
                }
            }

            // retrieve stored instance data for current node
            final Map<String, String> propertiesMap = nodeTemplateInstance.getPropertiesAsMap();
            if (Objects.nonNull(propertiesMap)) {

                LOG.debug("Found following properties in the instance data:");
                for (final String key : propertiesMap.keySet()) {
                    LOG.debug("Prop: " + key + " Val: " + propertiesMap.get(key));
                }

                // update currently not set input parameters if possible
                unsetParameters.stream().forEach(param -> {
                    if (supportedIPPropertyNames.contains(param)) {
                        LOG.debug("Supported IP-Property found.");
                        getSupportedProperty(supportedIPPropertyNames,
                                             propertiesMap).ifPresent(foundValue -> inputParams.put(param, foundValue));

                    } else if (supportedInstanceIdPropertyNames.contains(param)) {
                        LOG.debug("Supported InstanceID-Property found.");
                        getSupportedProperty(supportedInstanceIdPropertyNames,
                                             propertiesMap).ifPresent(foundValue -> inputParams.put(param, foundValue));

                    } else if (supportedPasswordPropertyNames.contains(param)) {
                        LOG.debug("Supported Password-Property found.");
                        getSupportedProperty(supportedPasswordPropertyNames,
                                             propertiesMap).ifPresent(foundValue -> inputParams.put(param, foundValue));

                    } else if (supportedUsernamePropertyNames.contains(param)) {
                        LOG.debug("Supported Username-Property found.");
                        getSupportedProperty(supportedUsernamePropertyNames,
                                             propertiesMap).ifPresent(foundValue -> inputParams.put(param, foundValue));

                    } else {
                        propertiesMap.keySet().stream().filter(name -> name.equals(param)).findFirst()
                                     .ifPresent(name -> inputParams.put(param, propertiesMap.get(name)));
                    }
                });

                // remove found properties
                inputParams.keySet().stream().forEach(param -> unsetParameters.remove(param));
            } else {
                LOG.debug("No stored instance data found for current node: {}", nodeTemplateInstance.getId());
            }

            // get next node downwards in the topology
            final Optional<NodeTemplateInstance> nextNode = MBUtils.getNextNodeTemplateInstance(nodeTemplateInstance);
            if (nextNode.isPresent()) {
                nodeTemplateInstance = nextNode.get();
                LOG.debug("Next node for parameter search: {}", nodeTemplateInstance.getId());
            } else {
                LOG.warn("No next node found for parameter search. Terminating with {} unsatisfied expected parameters",
                         unsetParameters.size());
                break;
            }
        }

        LOG.debug("Final {} input parameters for operation {} : {}", inputParams.size(), neededOperation,
                  inputParams.toString());

        return inputParams;
    }

    /**
     * Updates missing input parameters for a operation on a RelationshipTemplate with instance data.
     * The provided input parameters have priority, which means if one parameter is provided and found
     * in the instance data, then the provided parameter is used. <br>
     * <br>
     *
     * <b>Convention:</b><br>
     * Input parameters without prefix are searched on the RelationshipTemplateInstance. <br>
     * Input parameters with prefix "SRC_" are searched on the NodeTemplateInstance which is the source
     * of the RelationshipTemplate. <br>
     * Input parameters with prefix "TRG_" are searched on the NodeTemplateInstance which is the target
     * of the RelationshipTemplate.
     *
     * @param inputParams the set of provided input parameters
     * @param csarID of the CSAR containing the RelationshipTemplate
     * @param relationshipTemplateInstance the RelationshipTemplate instance object
     * @param neededInterface the interface of the operation for which the update is performed
     * @param neededOperation the operation for which the update is performed
     *
     * @return the updated input parameters.
     */
    private static HashMap<String, String> updateInputParamsForRelationshipTemplate(final HashMap<String, String> inputParams,
                                                                                    final CSARID csarID,
                                                                                    final RelationshipTemplateInstance relationshipTemplateInstance,
                                                                                    final String neededInterface,
                                                                                    final String neededOperation) {

        Objects.requireNonNull(relationshipTemplateInstance);

        LOG.debug("Updating input params for RelationshipTemplate ID: {}", relationshipTemplateInstance.getId());

        LOG.debug("{} inital input parameters for operation: {} found: {}", inputParams.size(), neededOperation,
                  inputParams.toString());

        // check if operation has input params at all
        final Set<String> expectedParams =
            getExpectedInputParams(csarID, relationshipTemplateInstance.getTemplateType(), neededInterface,
                                   neededOperation);
        if (expectedParams.isEmpty()) {
            LOG.debug("No input params defined for this operation.");
            return inputParams;
        }

        LOG.debug("Operation: {} expects {} parameters: {}", neededOperation, expectedParams.size(),
                  expectedParams.toString());

        // update params with instance data
        for (final String expectedParam : expectedParams) {
            LOG.debug("Expected parameter: {}", expectedParam);

            if (expectedParam.startsWith("SRC_")) {
                LOG.debug("Parameter is defined at the topology stack of the source NodeTemplate.");
                // TODO: search on source stack
            } else if (expectedParam.startsWith("TRG_")) {
                LOG.debug("Parameter is defined at the topology stack of the target NodeTemplate.");
                // TODO: search on target stack
            } else {
                LOG.debug("Parameter is defined at the RelationshipTemplate.");
                // TODO: search on RelationshipTemplateInstance properties
            }
        }

        return inputParams;
    }

    /**
     * Returns the input parameters of the given operation which are specified in the TOSCA definitions
     * of the NodeType or RelationshipType.
     *
     * @param csarID ID of the CSAR which contains the NodeType or RelationshipType with the operation
     * @param typeID ID of the NodeType or RelationshipType which contains the operation
     * @param interfaceName the name of the interface which contains the operation
     * @param operationName the operation name for which the parameters are searched
     *
     * @return specified input parameters of the operation
     */
    private static Set<String> getExpectedInputParams(final CSARID csarID, final QName typeID,
                                                      final String interfaceName, final String operationName) {

        final Node definedInputParameters =
            ServiceHandler.toscaEngineService.getInputParametersOfATypeOperation(csarID, typeID, interfaceName,
                                                                                 operationName);

        if (Objects.isNull(definedInputParameters)) {
            return Collections.emptySet();
        }

        final Set<String> inputParams = new HashSet<>();

        final NodeList definedInputParameterList = definedInputParameters.getChildNodes();
        for (int i = 0; i < definedInputParameterList.getLength(); i++) {

            final Node currentNode = definedInputParameterList.item(i);

            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {

                final String name = ((Element) currentNode).getAttribute("name");

                inputParams.add(name);
            }
        }

        return inputParams;
    }

    /**
     * Checks if one of the supported properties is defined in the Map and returns an optional with the
     * corresponding value.
     *
     * @param supportedProperties a List of supported properties
     * @param propertiesMap a Map containing properties of a NodeTemplateInstance
     * @return an Optional with the value of a supported property if one is found, an empty Optional
     *         otherwise
     */
    private static Optional<String> getSupportedProperty(final List<String> supportedProperties,
                                                         final Map<String, String> propertiesMap) {

        return supportedProperties.stream().filter(propertiesMap::containsKey).findFirst().map(propertiesMap::get);
    }
}
