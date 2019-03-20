package org.opentosca.bus.application.service.impl;

import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TDeploymentArtifacts;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.opentosca.bus.application.service.impl.servicehandler.InstanceDataServiceHandler;
import org.opentosca.bus.application.service.impl.servicehandler.ToscaServiceHandler;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.model.instance.NodeInstance;
import org.opentosca.container.core.model.instance.ServiceInstance;
import org.opentosca.container.core.tosca.convention.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class is used as a proxy to the ToscaEngineService & InstanceDataService.
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 * @TODO prototype: refactoring needed, integrate new methods into the ToscaEngineService and use
 * them instead of xml parsing here.
 */
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

  final private static Logger LOG = LoggerFactory.getLogger(ContainerProxy.class);


  /**
   * @param serviceInstanceID
   * @param nodeInstanceID
   * @param nodeTemplateID
   * @return NodeInstance with specified ID
   */
  @Nullable
  public static NodeInstance getNodeInstance(final Integer serviceInstanceID, final Integer nodeInstanceID,
                                             final String nodeTemplateID) {
    LOG.debug("Searching NodeInstance with serviceInstanceID: " + serviceInstanceID + " nodeInstanceID: "
      + nodeInstanceID + " nodeTemplateID: " + nodeTemplateID);
    if (nodeInstanceID == null) {
      final String namespace = getServiceInstance(serviceInstanceID).getServiceTemplateID().getNamespaceURI();
      final QName nodeTemplateQName = new QName(namespace, nodeTemplateID);
      URI serviceInstanceUri;
      try {
        serviceInstanceUri = new URI(serviceInstanceID.toString());
      } catch (URISyntaxException e) {
        LOG.warn("No matching NodeInstance found.", e);
        return null;
      }
      List<NodeInstance> nodeInstances =
        InstanceDataServiceHandler.getInstanceDataService().getNodeInstances(null, nodeTemplateQName, null,
          serviceInstanceUri);
      if (nodeInstances.size() > 0) {
        return nodeInstances.get(0);
      }
    } else {
      URI nodeInstanceUri;
      try {
        nodeInstanceUri = new URI(nodeInstanceID.toString());
      } catch (URISyntaxException e) {
        LOG.warn("No matching NodeInstance found.", e);
        return null;
      }
      List<NodeInstance> nodeInstances =
        InstanceDataServiceHandler.getInstanceDataService().getNodeInstances(nodeInstanceUri, null, null, null);
      for (final NodeInstance nodeInstance : nodeInstances) {
        if (nodeInstance.getId() == nodeInstanceID) {
          return nodeInstance;
        }
      }
    }
    LOG.warn("No matching NodeInstance found.");
    return null;
  }

  /**
   * @param id
   * @return ServiceInstance with specified ID
   */
  @Nullable
  protected static ServiceInstance getServiceInstance(final Integer id) {
    LOG.trace("Searching ServiceInstance with ID: {}", id);
    final URI serviceInstanceID;
    try {
      serviceInstanceID = new URI(id.toString());
    } catch (final URISyntaxException e) {
      LOG.warn("No ServiceInstance with matching ID found.", e);
      return null;
    }
    final List<ServiceInstance> instances =
      InstanceDataServiceHandler.getInstanceDataService().getServiceInstances(serviceInstanceID, null,
        null);
    for (final ServiceInstance instance : instances) {
      if (instance.getDBId() == id) {
        LOG.trace("ServiceInstance with matching ID found.");
        return instance;
      }
    }
    LOG.warn("No ServiceInstance with matching ID found.");
    return null;
  }

  /**
   * Searches for NodeTypeImplementations and their DeploymentArtifacts as well as their
   * ArtifactTemplates of the specified NodeType. If the needed properties are found, they are
   * returned.
   *
   * @param csarID
   * @param nodeTypeName
   * @param interfaceName
   * @return specified properties as Node
   */
  @Nullable
  public static Node getPropertiesNode(final CSARID csarID, final QName nodeTypeName, final String interfaceName) {
    LOG.trace("Searching ArtifactTemplate defining needed properties for Interface [{}] of NodeType [{}] inside Csar {}",
      interfaceName, nodeTypeName, csarID);
    final List<QName> nodeTypeImplementationsIDs =
      ToscaServiceHandler.getToscaEngineService().getTypeImplementationsOfType(csarID, nodeTypeName);
    LOG.trace("The NodeType [{}] has {} NodeTypeImplementations.", nodeTypeName, nodeTypeImplementationsIDs.size());

    for (final QName nodeTypeImplementationID : nodeTypeImplementationsIDs) {
      // get the NodeTypeImplementation
      final TNodeTypeImplementation nodeTypeImplementation =
        (TNodeTypeImplementation) ToscaServiceHandler.getToscaEngineService().getToscaReferenceMapper()
          .getJAXBReference(csarID, nodeTypeImplementationID);
      // if there are DAs
      final TDeploymentArtifacts deploymentArtifacts = nodeTypeImplementation.getDeploymentArtifacts();
      if (deploymentArtifacts == null) {
        LOG.warn("The NodeTypeImplementation {} has no DeploymentArtifacts.",
          nodeTypeImplementation.getName());
        return null;
      }
      LOG.trace("The NodeTypeImplementation [{}] has {} DeploymentArtifacts.",
        nodeTypeImplementation.getName(), deploymentArtifacts.getDeploymentArtifact().size());
      for (final TDeploymentArtifact da : deploymentArtifacts.getDeploymentArtifact()) {
        LOG.trace("- {}", da.getName());
        LOG.trace("Searching for ArtifactTemplates.");

        final QName artifactRef = da.getArtifactRef();

        if (artifactRef == null) {
          LOG.debug("No ArtifactTemplate for DA: {} found. Skipping DA.", da.getName());
          continue;
        }
        LOG.trace("ArtifactTemplate for DA [{}] found: {}. Getting the properties of it.", da.getName(), artifactRef);

        final Document properties =
          ToscaServiceHandler.getToscaEngineService().getPropertiesOfAArtifactTemplate(csarID, artifactRef);

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
            + interfaceName + " of NodeType: " + nodeTypeName + " inside CSAR: " + csarID + " found!");
          return propNode;
        }
      }
    }
    LOG.debug("No ArtifactTemplate with needed properties for interface: " + interfaceName + " of NodeType: "
      + nodeTypeName + " inside CSAR: " + csarID + " found!");
    return null;
  }

  /**
   * @param propNode
   * @return relative endpoint, specified in properties (as <tt>Endpoint</tt> property).
   */
  @Nullable
  public static String getRelativeEndpoint(final Node propNode) {

    // get properties like endpoint or
    // invocationType
    final NodeList appProps = propNode.getChildNodes();

    for (int i = 0; i < appProps.getLength(); i++) {

      final Node addProp = appProps.item(i);

      if (addProp.getNodeType() == Node.ELEMENT_NODE) {

        if (addProp.getLocalName().equals(RELATIVE_ENDPOINT)) {

          final String hostEndpoint = addProp.getTextContent().trim();
          LOG.debug("Endpoint property: {}", hostEndpoint);
          return hostEndpoint;

        }
      }
    }
    return null;
  }

  /**
   * @param propNode
   * @return port, specified in properties (as <tt>Port</tt> property).
   */
  @Nullable
  public static Integer getPort(final Node propNode) {

    // get properties like endpoint or
    // invocationType
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
   * @param propNode
   * @return invocationType, specified in properties (as <tt>InvocationType</tt> property).
   */
  @Nullable
  public static String getInvocationType(final Node propNode) {

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
   * @param propNode
   * @param interfaceName
   * @return implementing class specified in the properties of the specified interface
   */
  @Nullable
  public static String getClass(final Node propNode, final String interfaceName) {

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
   * @param csarID
   * @param serviceTemplateID
   * @param nodeTypeQName
   * @return name of a NodeTemplate of the specified NodeType inside of the specified
   * serviceTemplate & csar
   */
  @Nullable
  protected static String getANodeTemplateNameOfNodeType(final CSARID csarID, final QName serviceTemplateID,
                                                         final QName nodeTypeQName) {

    LOG.debug("Searching NodeTemplate of NodeType: " + nodeTypeQName + " in the ServiceTemplate: "
      + serviceTemplateID + " inside the CSAR: " + csarID);

    // get the ServiceTemplate
    final TServiceTemplate serviceTemplate =
      (TServiceTemplate) ToscaServiceHandler.getToscaEngineService().getToscaReferenceMapper()
        .getJAXBReference(csarID, serviceTemplateID);

    final TTopologyTemplate topologyTemplate = serviceTemplate.getTopologyTemplate();
    if (topologyTemplate == null) {
      LOG.warn("Topology template of service template [{}] was null, even though we are not in modeling mode",
        serviceTemplateID);
      return null;
    }
    for (final TEntityTemplate entity : topologyTemplate.getNodeTemplateOrRelationshipTemplate()) {

      TNodeTemplate nodeTemplate = new TNodeTemplate();

      // get NodeTemplate
      if (entity instanceof TNodeTemplate) {

        nodeTemplate = (TNodeTemplate) entity;

        if (nodeTemplate.getType() != null) {

          if (nodeTemplate.getType().equals(nodeTypeQName)) {

            final String nodeTemplateID = nodeTemplate.getId();
            LOG.debug("NodeTemplate of NodeType: " + nodeTypeQName + " in the ServiceTemplate: "
              + serviceTemplateID + " inside the CSAR: " + csarID + " found. NodeTemplateID: "
              + nodeTemplateID);
            return nodeTemplateID;
          }
        }
      }
    }
    LOG.debug("No NodeTemplate of NodeType: " + nodeTypeQName + " in the ServiceTemplate: " + serviceTemplateID
      + " inside the CSAR: " + csarID + " found.");
    return null;
  }

  /**
   * Returns the first NodeTemplate underneath the defined NodeTemplate containing the IP
   * property.
   *
   * @param csarID
   * @param serviceTemplateID
   * @param nodeTemplateID
   * @return name of the first NodeTemplate underneath the defined NodeTemplate containing the IP
   * property.
   */
  public static String getHostedOnNodeTemplateWithSpecifiedIPProperty(final CSARID csarID,
                                                                      final QName serviceTemplateID,
                                                                      String nodeTemplateID) {

    LOG.debug("Searching NodeTemplate with specified IP-Property underneath the NodeTemplate: " + nodeTemplateID
      + " of the ServiceTemplate :" + serviceTemplateID + " inside the CSAR: " + csarID);

    Document props = ToscaServiceHandler.getToscaEngineService().getPropertiesOfTemplate(csarID, serviceTemplateID,
      nodeTemplateID);

    final QName relationshipType = new QName(HOSTED_ON_NAMESPACE, HOSTED_ON_LOCALPART);

    while (nodeTemplateID != null && getIpProperty(props) == null) {

      LOG.debug("{} isn't the searched NodeTemplate.", nodeTemplateID);
      LOG.debug("Getting the underneath Node for checking if it is the searched NodeTemplate.");

      nodeTemplateID =
        ToscaServiceHandler.getToscaEngineService().getRelatedNodeTemplateID(csarID, serviceTemplateID,
          nodeTemplateID, relationshipType);

      if (nodeTemplateID != null) {
        LOG.debug("Checking if the underneath Node: {} is the searched NodeTemplate.", nodeTemplateID);

        props = ToscaServiceHandler.getToscaEngineService().getPropertiesOfTemplate(csarID, serviceTemplateID,
          nodeTemplateID);

      } else {
        LOG.debug("No underneath Node found.");
      }
    }

    if (nodeTemplateID != null) {
      LOG.debug("NodeTemplate with specified IP-Property in the ServiceTemplate: " + serviceTemplateID
        + " inside the CSAR: " + csarID + " found: " + nodeTemplateID);
    } else {
      LOG.debug("No NodeTemplate with specified IP-Property in the ServiceTemplate: " + serviceTemplateID
        + " inside the CSAR: " + csarID + " found.");
    }
    return nodeTemplateID;
  }

  /**
   * Returns the in the InstanceDataService stored IP property of the specified ServiceInstance &
   * NodeTemplate.
   *
   * @param serviceInstanceID
   * @param nodeTemplateQName
   * @return IP property
   */
  @Nullable
  public static URL getIpFromInstanceDataProperties(final URI serviceInstanceID, final QName nodeTemplateQName) {

    LOG.debug("Getting IP-Property from InstanceDataService of NodeTemplate: " + nodeTemplateQName
      + " of ServiceInstanceID: " + serviceInstanceID + ".");

    final List<NodeInstance> nodeInstances =
      InstanceDataServiceHandler.getInstanceDataService().getNodeInstances(null, nodeTemplateQName, null,
        serviceInstanceID);

    for (final NodeInstance nodeInstance : nodeInstances) {

      final Document props = nodeInstance.getProperties();

      final String ip = getIpProperty(props);

      if (ip != null) {
        LOG.debug("IP-Property from InstanceDataService of NodeTemplate: " + nodeTemplateQName
          + " ServiceInstanceID: " + serviceInstanceID + " found: " + ip);
        try {
          return new URL(ip);
        } catch (final MalformedURLException e) {
          e.printStackTrace();
        }
      }
    }
    LOG.debug("No IP-Property from InstanceDataService of NodeTemplate: " + nodeTemplateQName
      + " ServiceInstanceID: " + serviceInstanceID + " found.");
    return null;
  }

  /**
   * @param props to check
   * @return IP property, if exist. Otherwise null.
   */
  private static String getIpProperty(final Document props) {

    if (props != null) {

      LOG.debug("Checking if IP-Property is defined in the xml document: " + docToString(props));

      final List<String> knownIpProperties = Utils.getSupportedVirtualMachineIPPropertyNames();

      for (final String ipProperty : knownIpProperties) {

        final NodeList list = props.getElementsByTagName(ipProperty);

        if (list.getLength() > 0) {
          final String ip = list.item(0).getTextContent();
          LOG.debug("Property: {} is defined: {}", ipProperty, ip);
          return ip;
        }

      }

    }
    LOG.debug("No IP-Property defined.");
    return null;
  }

  /**
   * Transforms a document into a String.
   *
   * @param doc
   * @return document content as String.
   */
  private static String docToString(final Document doc) {
    String output = null;
    final TransformerFactory tf = TransformerFactory.newInstance();
    Transformer transformer;
    try {
      transformer = tf.newTransformer();
      transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
      final StringWriter writer = new StringWriter();
      transformer.transform(new DOMSource(doc), new StreamResult(writer));
      output = writer.getBuffer().toString().replaceAll("\n|\r", "");
    } catch (final TransformerException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return output;
  }

}
