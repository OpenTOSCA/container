package org.opentosca.bus.application.service.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;

import org.eclipse.jdt.annotation.Nullable;
import org.opentosca.container.core.common.NotFoundException;
import org.opentosca.container.core.convention.Utils;
import org.opentosca.container.core.engine.ToscaEngine;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.repository.NodeTemplateInstanceRepository;
import org.opentosca.container.core.next.repository.ServiceTemplateInstanceRepository;
import org.opentosca.container.core.service.CsarStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class is used as a proxy to the ToscaEngineService & InstanceDataService.
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 * @TODO prototype: refactoring needed, integrate new methods into the ToscaEngineService and use them instead of xml
 * parsing here.
 */
@Service
public class ContainerProxy {

    static final private String NAMESPACE = "http://www.uni-stuttgart.de/opentosca";
    static final private String INTERFACES_PROPERTIES_NAME = "ApplicationInterfacesProperties";
    static final private String INTERFACE_INFORMATIONS = "ApplicationInterfaceInformations";
    static final private String INTERFACE_INFORMATION = "ApplicationInterfaceInformation";
    static final private String RELATIVE_ENDPOINT = "Endpoint";
    static final private String PORT = "Port";
    static final private String INVOCATION_TYPE = "InvocationType";

    static final private String HOSTED_ON_NAMESPACE = "http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes";
    static final private String HOSTED_ON_LOCALPART = "HostedOn";

    private static final Logger LOG = LoggerFactory.getLogger(ContainerProxy.class);

    private final NodeTemplateInstanceRepository nodeInstanceRepo;
    private final ServiceTemplateInstanceRepository serviceTemplateInstanceRepository;
    private final CsarStorageService storageService;

    @Inject
    public ContainerProxy(CsarStorageService storageService) {
        this.storageService = storageService;
        this.nodeInstanceRepo = new NodeTemplateInstanceRepository();
        this.serviceTemplateInstanceRepository = new ServiceTemplateInstanceRepository();
    }

    private static String getIpProperty(Map<String, String> props) {
        final List<String> knownIpProperties = Utils.getSupportedVirtualMachineIPPropertyNames();
        for (final String ipProperty : knownIpProperties) {
            final String list = props.get(ipProperty);
            if (list != null) {
                LOG.debug("Property: {} is defined: {}", ipProperty, list);
                return list;
            }
        }
        return null;
    }

    /**
     * @param props to check
     * @return IP property, if exist. Otherwise null.
     */
    private static String getIpProperty(final Document props) {
        if (props == null) {
            return null;
        }
        LOG.trace("Checking if IP-Property is defined in the xml document: " + props.getTextContent());
        final List<String> knownIpProperties = Utils.getSupportedVirtualMachineIPPropertyNames();
        for (final String ipProperty : knownIpProperties) {
            final NodeList list = props.getElementsByTagName(ipProperty);
            if (list.getLength() > 0) {
                final String ip = list.item(0).getTextContent();
                LOG.debug("Property: {} is defined: {}", ipProperty, ip);
                return ip;
            }
        }
        LOG.debug("No IP-Property defined.");
        return null;
    }

    /**
     * @return NodeInstance with specified ID
     */
    @Nullable
    public NodeTemplateInstance getNodeInstance(final Integer serviceInstanceID, final Integer nodeInstanceID,
                                                final String nodeTemplateID) {
        LOG.debug("Searching NodeInstance with serviceInstanceID: " + serviceInstanceID + " nodeInstanceID: "
            + nodeInstanceID + " nodeTemplateID: " + nodeTemplateID);
        if (nodeInstanceID == null) {
            List<NodeTemplateInstance> nodeInstances =
                this.nodeInstanceRepo.findByTemplateId(nodeTemplateID).stream().filter(node -> node.getServiceTemplateInstance().getId().equals(Long.valueOf(serviceInstanceID))).collect(Collectors.toList());
            if (nodeInstances.size() > 0) {
                return nodeInstances.get(0);
            }
        } else {
            return this.nodeInstanceRepo.find(Long.valueOf(nodeInstanceID)).orElse(null);
        }
        LOG.warn("No matching NodeInstance found.");
        return null;
    }

    /**
     * @return ServiceInstance with specified ID
     */
    @Nullable
    protected ServiceTemplateInstance getServiceInstance(final Integer id) {
        LOG.trace("Searching ServiceInstance with ID: {}", id);
        return this.serviceTemplateInstanceRepository.find(Long.valueOf(id)).orElse(null);
    }

    /**
     * Searches for NodeTypeImplementations and their DeploymentArtifacts as well as their ArtifactTemplates of the
     * specified NodeType. If the needed properties are found, they are returned.
     *
     * @return specified properties as Node
     */
    @Nullable
    public Node getPropertiesNode(final CsarId csarId, final QName nodeTypeName, final String interfaceName) {
        LOG.trace("Searching ArtifactTemplate defining needed properties for Interface [{}] of NodeType [{}] inside Csar {}",
            interfaceName, nodeTypeName, csarId);
        final Csar csar = storageService.findById(csarId);
        // FIXME not sure whether that's equivalent!
        final TNodeType nodeType;
        try {
            nodeType = ToscaEngine.resolveNodeTypeReference(csar, nodeTypeName);
        } catch (NotFoundException missing) {
            LOG.warn("Did not find NodeType requested with csarId {}, nodeTypeName: {}", csarId, nodeTypeName);
            return null;
        }
        final List<TNodeTypeImplementation> nodeTypeImplementations = ToscaEngine.nodeTypeImplementations(csar, nodeType);
        LOG.trace("The NodeType [{}] has {} NodeTypeImplementations.", nodeTypeName, nodeTypeImplementations.size());

        for (final TNodeTypeImplementation nodeTypeImplementation : nodeTypeImplementations) {
            // if there are DAs
            final List<TDeploymentArtifact> deploymentArtifacts = nodeTypeImplementation.getDeploymentArtifacts();
            if (deploymentArtifacts == null) {
                LOG.warn("The NodeTypeImplementation {} has no DeploymentArtifacts.",
                    nodeTypeImplementation.getName());
                return null;
            }
            LOG.trace("The NodeTypeImplementation [{}] has {} DeploymentArtifacts.",
                nodeTypeImplementation.getName(), deploymentArtifacts.size());
            for (final TDeploymentArtifact da : deploymentArtifacts) {
                LOG.trace("- {}", da.getName());
                LOG.trace("Searching for ArtifactTemplates.");

                final QName artifactRef = da.getArtifactRef();

                if (artifactRef == null) {
                    LOG.debug("No ArtifactTemplate for DA: {} found. Skipping DA.", da.getName());
                    continue;
                }
                LOG.trace("ArtifactTemplate for DA [{}] found: {}. Getting the properties of it.", da.getName(), artifactRef);

                final Document properties = ToscaEngine.getArtifactTemplateProperties(csar, artifactRef);
                if (properties == null) {
                    LOG.debug("ArtifactTemplate : {} has no specified properties. Skipping DA", artifactRef);
                    continue;
                }
                LOG.trace("Properties of ArtifactTemplate: {} found.", artifactRef);
                LOG.trace("Getting the {} elements if existing.", INTERFACES_PROPERTIES_NAME);

                // get ApplicationInterfacesProperties
                final NodeList appPropsList = properties.getElementsByTagNameNS(NAMESPACE, INTERFACES_PROPERTIES_NAME);

                LOG.trace("{} {} elements found.", appPropsList.getLength(), INTERFACES_PROPERTIES_NAME);

                boolean hostEndpointSpecified = false;
                boolean portSpecified = false;
                boolean invocationTypeSpecified = false;
                boolean interfaceFound = false;

                Node propNode = null;

                for (int i = 0; i < appPropsList.getLength(); i++) {
                    hostEndpointSpecified = false;
                    portSpecified = false;
                    invocationTypeSpecified = false;
                    interfaceFound = false;

                    LOG.debug("Check if information are specified for the correct Interface.");
                    propNode = appPropsList.item(i);
                    // get properties like endpoint or invocationType
                    final NodeList appProps = propNode.getChildNodes();
                    for (int i2 = 0; i2 < appProps.getLength(); i2++) {
                        final Node addProp = appProps.item(i2);
                        if (addProp.getNodeType() == Node.ELEMENT_NODE) {
                            if (addProp.getLocalName().equals(RELATIVE_ENDPOINT)) {
                                LOG.trace("Endpoint property found.");
                                hostEndpointSpecified = true;
                            } else if (addProp.getLocalName().equals(PORT)) {
                                LOG.trace("Port property found.");
                                portSpecified = true;
                            } else if (addProp.getLocalName().equals(INVOCATION_TYPE)) {
                                LOG.trace("InvocationType property found.");
                                invocationTypeSpecified = true;
                            } else if (addProp.getLocalName().equals(INTERFACE_INFORMATIONS)) {
                                // check if interface matches
                                final NodeList appInvInterfaceInfo =
                                    ((Element) addProp).getElementsByTagNameNS(NAMESPACE, INTERFACE_INFORMATION);

                                LOG.trace("{} for {} Interfaces found.", INTERFACE_INFORMATION, appInvInterfaceInfo.getLength());
                                for (int i3 = 0; i3 < appInvInterfaceInfo.getLength(); i3++) {
                                    final String interfName = appInvInterfaceInfo.item(i3).getAttributes()
                                        .getNamedItem("name").getNodeValue();
                                    if (interfName.equals(interfaceName)) {
                                        interfaceFound = true;
                                        LOG.trace("Properties for interface: {} found.", interfaceName);
                                    }
                                }
                            }
                        }
                    }
                }
                if (hostEndpointSpecified && portSpecified && invocationTypeSpecified && interfaceFound) {
                    LOG.debug("Properties with all needed information(Endpoint & InvocationType) for interface: "
                        + interfaceName + " of NodeType: " + nodeTypeName + " inside CSAR: " + csarId + " found!");
                    return propNode;
                }
            }
        }
        LOG.debug("No ArtifactTemplate with needed properties for interface: " + interfaceName + " of NodeType: "
            + nodeTypeName + " inside CSAR: " + csarId + " found!");
        return null;
    }

    /**
     * @return relative endpoint, specified in properties (as <tt>Endpoint</tt> property).
     */
    @Nullable
    public String getRelativeEndpoint(final Node propNode) {
        // get properties like endpoint or invocationType
        final NodeList appProps = propNode.getChildNodes();
        for (int i = 0; i < appProps.getLength(); i++) {
            final Node addProp = appProps.item(i);
            if (addProp.getNodeType() == Node.ELEMENT_NODE && addProp.getLocalName().equals(RELATIVE_ENDPOINT)) {
                final String hostEndpoint = addProp.getTextContent().trim();
                LOG.debug("Endpoint property: {}", hostEndpoint);
                return hostEndpoint;
            }
        }
        return null;
    }

    /**
     * @return port, specified in properties (as <tt>Port</tt> property).
     */
    @Nullable
    public Integer getPort(final Node propNode) {
        // get properties like endpoint or invocationType
        final NodeList appProps = propNode.getChildNodes();
        for (int i = 0; i < appProps.getLength(); i++) {
            final Node addProp = appProps.item(i);
            if (addProp.getNodeType() == Node.ELEMENT_NODE) {
                if (addProp.getLocalName().equals(PORT)) {
                    final Integer port = Integer.parseInt(addProp.getTextContent().trim());
                    LOG.debug("Port property: {}", port);
                    return port;
                }
            }
        }
        return null;
    }

    /**
     * @return invocationType, specified in properties (as <tt>InvocationType</tt> property).
     */
    @Nullable
    public String getInvocationType(final Node propNode) {

        // get properties like endpoint or
        // invocationType
        final NodeList appProps = propNode.getChildNodes();

        for (int i = 0; i < appProps.getLength(); i++) {

            final Node addProp = appProps.item(i);

            if (addProp.getNodeType() == Node.ELEMENT_NODE) {

                if (addProp.getLocalName().equals(INVOCATION_TYPE)) {
                    final String invocationType = addProp.getTextContent().trim();
                    LOG.debug("InvocationType property: {}", invocationType);
                    return invocationType;
                }
            }
        }
        return null;
    }

    /**
     * @return implementing class specified in the properties of the specified interface
     */
    @Nullable
    public String getClass(final Node propNode, final String interfaceName) {

        // get properties like endpoint or
        // invocationType
        final NodeList appProps = propNode.getChildNodes();

        for (int i = 0; i < appProps.getLength(); i++) {

            final Node addProp = appProps.item(i);

            if (addProp.getNodeType() == Node.ELEMENT_NODE) {

                if (addProp.getLocalName().equals(INTERFACE_INFORMATIONS)) {

                    // check if interface matches
                    final NodeList appInvInterfaceInfo =
                        ((Element) addProp).getElementsByTagNameNS(NAMESPACE, INTERFACE_INFORMATION);

                    LOG.debug(INTERFACE_INFORMATIONS + " for " + +appInvInterfaceInfo.getLength()
                        + " Interfaces found.");

                    for (int i2 = 0; i2 < appInvInterfaceInfo.getLength(); i2++) {

                        final String interfName =
                            appInvInterfaceInfo.item(i2).getAttributes().getNamedItem("name").getNodeValue();

                        if (interfName.equals(interfaceName)) {
                            final String className =
                                appInvInterfaceInfo.item(i2).getAttributes().getNamedItem("class").getNodeValue();
                            LOG.debug("Class property: {}", className);
                            return className;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * @return name of a NodeTemplate of the specified NodeType inside of the specified serviceTemplate & csar
     */
    @Nullable
    protected String getANodeTemplateNameOfNodeType(final CsarId csarId, final QName serviceTemplateID, final QName nodeTypeQName) {

        LOG.debug("Searching NodeTemplate of NodeType: " + nodeTypeQName + " in the ServiceTemplate: "
            + serviceTemplateID + " inside the CSAR: " + csarId);

        // get the ServiceTemplate
        Csar csar = storageService.findById(csarId);
        final TServiceTemplate serviceTemplate;
        try {
            serviceTemplate = ToscaEngine.resolveServiceTemplate(csar, serviceTemplateID);
        } catch (NotFoundException e) {
            LOG.warn("Could not find containing serviceTemplate for NodeTemplate name request with arguments csarId: {}, serviceTemplateId: {}", csarId, serviceTemplateID);
            return null;
        }

        final TTopologyTemplate topologyTemplate = serviceTemplate.getTopologyTemplate();
        if (topologyTemplate == null) {
            LOG.warn("Topology template of service template [{}] was null, even though we are not in modeling mode", serviceTemplateID);
            return null;
        }
        for (final TEntityTemplate entity : topologyTemplate.getNodeTemplateOrRelationshipTemplate()) {
            TNodeTemplate nodeTemplate = new TNodeTemplate();
            // get NodeTemplate
            if (!(entity instanceof TNodeTemplate)) {
                continue;
            }
            nodeTemplate = (TNodeTemplate) entity;
            if (nodeTemplate.getType() == null || !nodeTemplate.getType().equals(nodeTypeQName)) {
                continue;
            }
            final String nodeTemplateID = nodeTemplate.getId();
            LOG.debug("NodeTemplate of NodeType: " + nodeTypeQName + " in the ServiceTemplate: "
                + serviceTemplateID + " inside the CSAR: " + csarId + " found. NodeTemplateID: "
                + nodeTemplateID);
            return nodeTemplateID;
        }
        LOG.debug("No NodeTemplate of NodeType: " + nodeTypeQName + " in the ServiceTemplate: " + serviceTemplateID
            + " inside the CSAR: " + csarId + " found.");
        return null;
    }

    /**
     * Returns the first NodeTemplate underneath the defined NodeTemplate containing the IP property.
     *
     * @return name of the first NodeTemplate underneath the defined NodeTemplate containing the IP property.
     */
    public String getHostedOnNodeTemplateWithSpecifiedIPProperty(final CsarId csarId,
                                                                 final QName serviceTemplateId,
                                                                 final String nodeTemplateId) {
        LOG.debug("Searching NodeTemplate with specified IP-Property underneath the NodeTemplate: " + nodeTemplateId
            + " of the ServiceTemplate :" + serviceTemplateId + " inside the CSAR: " + csarId);
        Csar csar = storageService.findById(csarId);

        final TServiceTemplate context;
        TNodeTemplate nodeTemplate;
        try {
            context = ToscaEngine.resolveServiceTemplate(csar, serviceTemplateId);
            nodeTemplate = ToscaEngine.resolveNodeTemplate(context, nodeTemplateId);
        } catch (NotFoundException e) {
            LOG.warn("Could not find service template {} or node template {} within csar {}", serviceTemplateId, nodeTemplateId, csar);
            return null;
        }
        Document props = ToscaEngine.getNodeTemplateProperties(nodeTemplate);
        final QName relationshipType = new QName(HOSTED_ON_NAMESPACE, HOSTED_ON_LOCALPART);
        while (nodeTemplate != null && getIpProperty(props) == null) {

            LOG.trace("{} isn't the searched NodeTemplate.", nodeTemplate.getId());
            LOG.trace("Getting the underneath Node for checking if it is the searched NodeTemplate.");

            nodeTemplate = ToscaEngine.getRelatedNodeTemplate(context, nodeTemplate, relationshipType);
            if (nodeTemplate != null) {
                LOG.trace("Checking if the underneath Node: {} is the searched NodeTemplate.", nodeTemplateId);
                props = ToscaEngine.getNodeTemplateProperties(nodeTemplate);
            }
        }

        if (nodeTemplate != null) {
            LOG.debug("NodeTemplate with specified IP-Property in the ServiceTemplate: " + serviceTemplateId
                + " inside the CSAR: " + csarId + " found: " + nodeTemplate.getId());
        } else {
            LOG.debug("No NodeTemplate with specified IP-Property in the ServiceTemplate: " + serviceTemplateId
                + " inside the CSAR: " + csarId + " found.");
        }
        return nodeTemplate.getId();
    }

    /**
     * Returns the in the InstanceDataService stored IP property of the specified ServiceInstance & NodeTemplate.
     *
     * @return IP property
     */
    @Nullable
    public URL getIpFromInstanceDataProperties(final Long serviceInstanceID, final String nodeTemplateID) {

        LOG.debug("Getting IP-Property from InstanceDataService of NodeTemplate: " + nodeTemplateID + " of ServiceInstanceID: " + serviceInstanceID + ".");

        final List<NodeTemplateInstance> nodeInstances = this.nodeInstanceRepo.findAll().stream().filter(x -> x.getServiceTemplateInstance().getId().equals(Long.valueOf(serviceInstanceID.toString())) && x.getTemplateId().equals(nodeTemplateID)).collect(Collectors.toList());
        for (final NodeTemplateInstance nodeInstance : nodeInstances) {

            final String ip = getIpProperty(nodeInstance.getPropertiesAsMap());
            if (ip != null) {
                LOG.debug("IP-Property from InstanceDataService of NodeTemplate: " + nodeTemplateID + " ServiceInstanceID: " + serviceInstanceID + " found: " + ip);
                try {
                    return new URL(ip);
                } catch (final MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }
        LOG.debug("No IP-Property from InstanceDataService of NodeTemplate: " + nodeTemplateID + " ServiceInstanceID: " + serviceInstanceID + " found.");
        return null;
    }
}
