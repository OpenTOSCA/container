package org.opentosca.bus.management.utils;

import java.io.StringWriter;
import java.net.URI;
import java.util.HashMap;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.opentosca.bus.management.servicehandler.ServiceHandler;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.model.instance.NodeInstance;
import org.opentosca.container.core.model.instance.ServiceInstance;
import org.opentosca.container.core.tosca.convention.Interfaces;
import org.opentosca.container.core.tosca.convention.Types;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;

public class MBUtils {

    final private static Logger LOG = LoggerFactory.getLogger(MBUtils.class);


    /**
     *
     * Returns the OperatingSystem NodeTemplate.
     *
     * @param csarID
     * @param serviceTemplateID
     * @param nodeTemplateID
     *
     * @return name of the OperatingSystem NodeTemplate.
     */
    public static String getOperatingSystemNodeTemplateID(final CSARID csarID, final QName serviceTemplateID,
                    String nodeTemplateID) {

        MBUtils.LOG.debug("Searching the OperatingSystemNode of NodeTemplate: {}, ServiceTemplate: {} & CSAR: {} ...",
            nodeTemplateID, serviceTemplateID, csarID);

        QName nodeType = ServiceHandler.toscaEngineService.getNodeTypeOfNodeTemplate(csarID, serviceTemplateID,
            nodeTemplateID);

        while (!isOperatingSystemNodeType(csarID, nodeType) && nodeTemplateID != null) {

            MBUtils.LOG.debug("{} isn't the OperatingSystemNode.", nodeTemplateID);
            MBUtils.LOG.debug("Getting the underneath Node for checking if it is the OperatingSystemNode...");

            // try different relationshiptypes with priority on hostedOn
            nodeTemplateID = ServiceHandler.toscaEngineService.getRelatedNodeTemplateID(csarID, serviceTemplateID,
                nodeTemplateID, Types.hostedOnRelationType);

            if (nodeTemplateID == null) {
                nodeTemplateID = ServiceHandler.toscaEngineService.getRelatedNodeTemplateID(csarID, serviceTemplateID,
                    nodeTemplateID, Types.deployedOnRelationType);

                if (nodeTemplateID == null) {
                    nodeTemplateID = ServiceHandler.toscaEngineService.getRelatedNodeTemplateID(csarID,
                        serviceTemplateID, nodeTemplateID, Types.deployedOnRelationType);

                    if (nodeTemplateID == null) {
                        nodeTemplateID = ServiceHandler.toscaEngineService.getRelatedNodeTemplateID(csarID,
                            serviceTemplateID, nodeTemplateID, Types.dependsOnRelationType);
                    }
                }
            }

            if (nodeTemplateID != null) {
                MBUtils.LOG.debug("Checking if the underneath Node: {} is the OperatingSystemNode.", nodeTemplateID);
                nodeType = ServiceHandler.toscaEngineService.getNodeTypeOfNodeTemplate(csarID, serviceTemplateID,
                    nodeTemplateID);

            } else {
                MBUtils.LOG.debug("No underneath Node found.");
            }
        }

        if (nodeTemplateID != null) {
            MBUtils.LOG.debug("OperatingSystemNode found: {}", nodeTemplateID);
        }

        return nodeTemplateID;
    }

    /**
     *
     * Checks if the specified NodeType is the OperatingSystem NodeType.
     *
     * @param csarID
     * @param nodeType
     * @return true if the specified NodeType is the OperatingSystem NodeType. Otherwise false.
     */
    private static boolean isOperatingSystemNodeType(final CSARID csarID, final QName nodeType) {
        if (ServiceHandler.toscaEngineService.doesInterfaceOfNodeTypeContainOperation(csarID, nodeType,
            Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM,
            Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_RUNSCRIPT)
            && ServiceHandler.toscaEngineService.doesInterfaceOfNodeTypeContainOperation(csarID, nodeType,
                Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM,
                Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_TRANSFERFILE)
            && ServiceHandler.toscaEngineService.doesInterfaceOfNodeTypeContainOperation(csarID, nodeType,
                Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM,
                Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_INSTALLPACKAGE)) {
            return true;
        } else if (ServiceHandler.toscaEngineService.doesInterfaceOfNodeTypeContainOperation(csarID, nodeType,
            Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER,
            Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER_RUNSCRIPT)
            && ServiceHandler.toscaEngineService.doesInterfaceOfNodeTypeContainOperation(csarID, nodeType,
                Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER,
                Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER_TRANSFERFILE)) {
            return true;
        }
        return false;
    }

    /**
     * Returns the OS interface of the given OS Node Type
     *
     * @param csarID the CSAR Id where the referenced Node Type is declared
     * @param nodeType a QName of the Node Type to check
     * @return a String containing the name of the OS interface, or if the given Node Type is not an OS
     *         Node Type null
     */
    public static String getInterfaceForOperatingSystemNodeType(final CSARID csarID, final QName nodeType) {
        if (ServiceHandler.toscaEngineService.doesInterfaceOfNodeTypeContainOperation(csarID, nodeType,
            Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM,
            Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_RUNSCRIPT)
            && ServiceHandler.toscaEngineService.doesInterfaceOfNodeTypeContainOperation(csarID, nodeType,
                Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM,
                Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_TRANSFERFILE)
            && ServiceHandler.toscaEngineService.doesInterfaceOfNodeTypeContainOperation(csarID, nodeType,
                Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM,
                Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_INSTALLPACKAGE)) {
            return Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM;
        } else if (ServiceHandler.toscaEngineService.doesInterfaceOfNodeTypeContainOperation(csarID, nodeType,
            Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER,
            Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER_RUNSCRIPT)
            && ServiceHandler.toscaEngineService.doesInterfaceOfNodeTypeContainOperation(csarID, nodeType,
                Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER,
                Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER_TRANSFERFILE)) {
            return Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER;
        }
        return null;
    }

    /**
     *
     * Returns the name of the OperatingSystem ImplementationArtifact.
     *
     * @param csarID
     * @param serviceTemplateID
     * @param osNodeTemplateID
     *
     *
     * @return name of the OperatingSystem ImplementationArtifact.
     */
    public static String getOperatingSystemIA(final CSARID csarID, final QName serviceTemplateID,
                    final String osNodeTemplateID) {

        MBUtils.LOG.debug("Searching the OperatingSystem-IA of NodeTemplate: {}, ServiceTemplate: {} & CSAR: {} ...",
            osNodeTemplateID, serviceTemplateID, csarID);

        final QName osNodeType = ServiceHandler.toscaEngineService.getNodeTypeOfNodeTemplate(csarID, serviceTemplateID,
            osNodeTemplateID);

        final List<QName> osNodeTypeImpls = ServiceHandler.toscaEngineService.getNodeTypeImplementationsOfNodeType(
            csarID, osNodeType);

        for (final QName osNodeTypeImpl : osNodeTypeImpls) {

            MBUtils.LOG.debug("NodeTypeImpl: {} ", osNodeTypeImpl);

            final List<String> osIANames = ServiceHandler.toscaEngineService.getImplementationArtifactNamesOfNodeTypeImplementation(
                csarID, osNodeTypeImpl);

            for (final String osIAName : osIANames) {

                MBUtils.LOG.debug("IA: {} ", osIAName);

                final String osIAInterface = ServiceHandler.toscaEngineService.getInterfaceOfAImplementationArtifactOfANodeTypeImplementation(
                    csarID, osNodeTypeImpl, osIAName);

                MBUtils.LOG.debug("Interface: {} ", osIAInterface);

                if (osIAInterface.equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM)
                    | osIAInterface.equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER)) {

                    return osIAName;

                }
            }
        }

        return null;
    }

    /**
     *
     * Traverses the topology and searches for the specified property. If found, the value from the
     * instance data is returned.
     *
     * @param property
     * @param csarID
     * @param serviceTemplateID
     * @param nodeTemplateID
     * @param serviceInstanceID
     *
     *
     * @return instance data value of searched property if found. Otherwise null.
     */
    public static String searchProperty(final String property, final CSARID csarID, final QName serviceTemplateID,
                    String nodeTemplateID, final URI serviceInstanceID) {

        MBUtils.LOG.debug(
            "Searching the Property: {} in or under the NodeTemplateID: {} ServiceTemplate: {} & CSAR: {} ...",
            property, nodeTemplateID, serviceTemplateID, csarID);

        String propertyValue = getInstanceDataPropertyValue(property, csarID, serviceTemplateID, nodeTemplateID,
            serviceInstanceID);

        while (propertyValue == null && nodeTemplateID != null) {

            MBUtils.LOG.debug("{} hasn't the searched property: {}.", nodeTemplateID, property);
            MBUtils.LOG.debug("Getting the underneath Node for checking if it has the searched property...");

            // try different relationshiptypes with priority on hostedOn
            nodeTemplateID = ServiceHandler.toscaEngineService.getRelatedNodeTemplateID(csarID, serviceTemplateID,
                nodeTemplateID, Types.hostedOnRelationType);

            if (nodeTemplateID == null) {
                nodeTemplateID = ServiceHandler.toscaEngineService.getRelatedNodeTemplateID(csarID, serviceTemplateID,
                    nodeTemplateID, Types.deployedOnRelationType);

                if (nodeTemplateID == null) {
                    nodeTemplateID = ServiceHandler.toscaEngineService.getRelatedNodeTemplateID(csarID,
                        serviceTemplateID, nodeTemplateID, Types.dependsOnRelationType);
                }
            }

            if (nodeTemplateID != null) {
                MBUtils.LOG.debug("Checking if the Node: {} has the searched property: {}.", nodeTemplateID, property);

                propertyValue = getInstanceDataPropertyValue(property, csarID, serviceTemplateID, nodeTemplateID,
                    serviceInstanceID);

            } else {
                MBUtils.LOG.debug("No underneath Node found.");
            }
        }
        if (propertyValue != null) {
            MBUtils.LOG.debug("Searched property: {} with value: {} found in NodeTemplate: {}.", property,
                propertyValue, nodeTemplateID);

        } else {
            MBUtils.LOG.debug("Searched property: {} not found!", property);
        }

        return propertyValue;
    }

    /**
     * @param property
     * @param csarID
     * @param serviceTemplateID
     * @param nodeTemplateID
     * @param serviceInstanceID
     *
     * @return instance data value of searched property if found. Otherwise null.
     */
    public static String getInstanceDataPropertyValue(final String property, final CSARID csarID,
                    final QName serviceTemplateID, final String nodeTemplateID, final URI serviceInstanceID) {

        final HashMap<String, String> propertiesMap = getInstanceDataProperties(csarID, serviceTemplateID,
            nodeTemplateID, serviceInstanceID);

        return propertiesMap.get(property);
    }

    /**
     * @param csarID
     * @param serviceTemplateID
     * @param nodeTemplateID
     * @param serviceInstanceID
     * @return the in the InstanceService stored properties for the specified parameters or null if it
     *         can not be found.
     */
    public static HashMap<String, String> getInstanceDataProperties(final CSARID csarID, final QName serviceTemplateID,
                    final String nodeTemplateID, final URI serviceInstanceID) {

        final String serviceTemplateName = ServiceHandler.toscaEngineService.getNameOfReference(csarID,
            serviceTemplateID);

        HashMap<String, String> propertiesMap = new HashMap<>();

        if (serviceInstanceID != null) {

            final List<ServiceInstance> serviceInstanceList = ServiceHandler.instanceDataService.getServiceInstances(
                serviceInstanceID, serviceTemplateName, serviceTemplateID);

            final QName nodeTemplateQName = new QName(serviceTemplateID.getNamespaceURI(), nodeTemplateID);

            for (final ServiceInstance serviceInstance : serviceInstanceList) {

                if (serviceInstance.getCSAR_ID().toString().equals(csarID.toString())) {
                    /**
                     * This is a workaround. The first statement should work, but unfortunately does not (the list is
                     * null / empty). We were not able to identify the root of the error, in debug mode it seemed to
                     * work but in "production" mode not. Somehow the lazy loading mechanism of JPA / EclipseLink seems
                     * to not work properly.
                     */
                    // List<NodeInstance> nodeInstanceList =
                    // serviceInstance.getNodeInstances();
                    final List<NodeInstance> nodeInstanceList = ServiceHandler.instanceDataService.getNodeInstances(
                        null, null, null, serviceInstanceID);

                    for (final NodeInstance nodeInstance : nodeInstanceList) {

                        if (nodeInstance.getNodeTemplateID().equals(nodeTemplateQName)) {

                            final Document doc = nodeInstance.getProperties();

                            if (doc != null) {
                                propertiesMap = docToMap(doc, false);
                            }

                            return propertiesMap;

                        }
                    }

                }

                MBUtils.LOG.debug("No InstanceData found for CsarID: " + csarID + ", ServiceTemplateID: "
                    + serviceTemplateID + ", ServiceTemplateName: " + serviceTemplateName + " and ServiceInstanceID: "
                    + serviceInstanceID);
            }

        }
        return null;
    }

    /**
     * Transfers the properties document to a map.
     *
     * @param propertiesDocument to be transfered to a map.
     * @return transfered map.
     */
    public static HashMap<String, String> docToMap(final Document propertiesDocument, final boolean allowEmptyEntries) {
        final HashMap<String, String> reponseMap = new HashMap<>();

        final DocumentTraversal traversal = (DocumentTraversal) propertiesDocument;
        final NodeIterator iterator = traversal.createNodeIterator(propertiesDocument.getDocumentElement(),
            NodeFilter.SHOW_ELEMENT, null, true);

        for (Node node = iterator.nextNode(); node != null; node = iterator.nextNode()) {

            final String name = ((Element) node).getLocalName();
            final StringBuilder content = new StringBuilder();
            final NodeList children = node.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                final Node child = children.item(i);
                if (child.getNodeType() == Node.TEXT_NODE) {
                    content.append(child.getTextContent());
                }
            }

            if (allowEmptyEntries) {
                reponseMap.put(name, content.toString());
            } else {
                if (!content.toString().trim().isEmpty()) {
                    reponseMap.put(name, content.toString());
                }
            }

        }

        return reponseMap;
    }

    public static String toString(final Document doc) {
        try {
            final StringWriter sw = new StringWriter();
            final TransformerFactory tf = TransformerFactory.newInstance();
            final Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.transform(new DOMSource(doc), new StreamResult(sw));
            return sw.toString();
        } catch (final Exception e) {
            throw new RuntimeException("Error converting Document to String: " + e.getMessage(), e);
        }
    }
}
