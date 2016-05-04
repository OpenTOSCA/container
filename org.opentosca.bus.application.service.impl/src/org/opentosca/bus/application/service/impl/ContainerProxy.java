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

import org.opentosca.bus.application.service.impl.servicehandler.InstanceDataServiceHandler;
import org.opentosca.bus.application.service.impl.servicehandler.ToscaServiceHandler;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.model.instancedata.NodeInstance;
import org.opentosca.model.instancedata.ServiceInstance;
import org.opentosca.model.tosca.TDeploymentArtifact;
import org.opentosca.model.tosca.TEntityTemplate;
import org.opentosca.model.tosca.TNodeTemplate;
import org.opentosca.model.tosca.TNodeTypeImplementation;
import org.opentosca.model.tosca.TServiceTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * This class is used as a proxy to the ToscaEngineService &
 * InstanceDataService.
 * 
 * 
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 *
 * @TODO prototype: refactoring needed, integrate new methods into the
 *       ToscaEngineService and use them instead of xml parsing here.
 *
 */
public class ContainerProxy {

	static final private String NAMESPACE = "http://www.uni-stuttgart.de/opentosca";
	static final private String INTERFACES_PROPERTIES_NAME = "ApplicationInterfacesProperties";
	static final private String INTERFACE_INFORMATIONS = "ApplicationInterfaceInformations";
	static final private String INTERFACE_INFORMATION = "ApplicationInterfaceInformation";
	static final private String RELATIVE_ENDPOINT = "Endpoint";
	static final private String INVOCATION_TYPE = "InvocationType";

	static final private String HOSTED_ON_NAMESPACE = "http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes";
	static final private String HOSTED_ON_LOCALPART = "HostedOn";

	static final private String IP_KEY = "Ip-Address";

	final private static Logger LOG = LoggerFactory.getLogger(ContainerProxy.class);

	/**
	 * @param serviceInstanceID
	 * @param nodeInstanceID
	 * @param nodeTemplateID
	 * @return NodeInstance with specified ID
	 */
	public static NodeInstance getNodeInstance(Integer serviceInstanceID, Integer nodeInstanceID,
			String nodeTemplateID) {

		ContainerProxy.LOG.debug("Searching NodeInstance with serviceInstanceID: " + serviceInstanceID
				+ " nodeInstanceID: " + nodeInstanceID + " nodeTemplateID: " + nodeTemplateID);

		try {

			List<NodeInstance> nodeInstances;

			if (nodeInstanceID == null) {

				String namespace = getServiceInstance(serviceInstanceID).getServiceTemplateID().getNamespaceURI();
				QName nodeTemplateQName = new QName(namespace, nodeTemplateID);
				nodeInstances = InstanceDataServiceHandler.getInstanceDataService().getNodeInstances(null,
						nodeTemplateQName, null, new URI(serviceInstanceID.toString()));

				if (nodeInstances.size() > 0) {
					return nodeInstances.get(0);
				}

			} else {

				nodeInstances = InstanceDataServiceHandler.getInstanceDataService()
						.getNodeInstances(new URI(nodeInstanceID.toString()), null, null, null);

				for (NodeInstance nodeInstance : nodeInstances) {
					if (nodeInstance.getId() == nodeInstanceID) {
						return nodeInstance;
					}
				}
			}

		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		ContainerProxy.LOG.warn("No matching NodeInstance found.");
		return null;
	}

	/**
	 * @param id
	 * @return ServiceInstance with specified ID
	 */
	protected static ServiceInstance getServiceInstance(Integer id) {

		ContainerProxy.LOG.debug("Searching ServiceInstance with ID: {}", id);

		try {
			List<ServiceInstance> instances = InstanceDataServiceHandler.getInstanceDataService()
					.getServiceInstances(new URI(id.toString()), null, null);

			for (ServiceInstance instance : instances) {
				if (instance.getDBId() == id) {
					ContainerProxy.LOG.debug("ServiceInstance with matching ID found.");
					return instance;
				}
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		ContainerProxy.LOG.warn("No ServiceInstance with matching ID found.");
		return null;
	}

	/**
	 * Searches for NodeTypeImplementations and their DeploymentArtifacts as
	 * well as their ArtifactTemplates of the specified NodeType. If the needed
	 * properties are found, they are returned.
	 * 
	 * 
	 * @param csarID
	 * @param nodeTypeName
	 * @param interfaceName
	 * @return specified properties as Node
	 */
	public static Node getPropertiesNode(CSARID csarID, QName nodeTypeName, String interfaceName) {

		ContainerProxy.LOG.debug("Searching ArtifactTemplate defining needed properties for Interface: " + interfaceName
				+ " of NodeType: " + nodeTypeName + "inside of CSAR: " + csarID);

		List<QName> nodeTypeImplementationsIDs = ToscaServiceHandler.getToscaEngineService()
				.getNodeTypeImplementationsOfNodeType(csarID, nodeTypeName);

		ContainerProxy.LOG.debug("The NodeType: " + nodeTypeName + " has " + nodeTypeImplementationsIDs.size()
				+ " NodeTypeImplementations.");

		for (QName nodeTypeImplementationID : nodeTypeImplementationsIDs) {

			// get the NodeTypeImplementation
			TNodeTypeImplementation nodeTypeImplementation = (TNodeTypeImplementation) ToscaServiceHandler
					.getToscaEngineService().getToscaReferenceMapper()
					.getJAXBReference(csarID, nodeTypeImplementationID);

			// if there are DAs
			if (nodeTypeImplementation.getDeploymentArtifacts() != null) {

				ContainerProxy.LOG.debug("The NodeTypeImplementation: " + nodeTypeImplementation.getName() + " has "
						+ nodeTypeImplementation.getDeploymentArtifacts().getDeploymentArtifact().size()
						+ " DeploymentArtifacts.");

				for (TDeploymentArtifact da : nodeTypeImplementation.getDeploymentArtifacts().getDeploymentArtifact()) {

					ContainerProxy.LOG.debug("- {}", da.getName());
					ContainerProxy.LOG.debug("Searching for ArtifactTemplates.");

					QName artifactRef = da.getArtifactRef();

					if (artifactRef != null) {

						ContainerProxy.LOG.debug("ArtifactTemplate for DA: " + da.getName() + " found: " + artifactRef
								+ ". Getting the properties of it.");

						Document properties = ToscaServiceHandler.getToscaEngineService()
								.getPropertiesOfAArtifactTemplate(csarID, artifactRef);

						if (properties != null) {

							ContainerProxy.LOG.debug("Properties of ArtifactTemplate: {} found.", artifactRef);
							ContainerProxy.LOG.debug("Getting the {} elements if existing.",
									INTERFACES_PROPERTIES_NAME);

							// get ApplicationInterfacesProperties
							NodeList appPropsList = properties.getElementsByTagNameNS(NAMESPACE,
									INTERFACES_PROPERTIES_NAME);

							ContainerProxy.LOG.debug(
									appPropsList.getLength() + " " + INTERFACES_PROPERTIES_NAME + " elements found.");

							boolean hostEndpointSpecified = false;
							boolean invocationTypeSpecified = false;
							boolean interfaceFound = false;

							Node propNode = null;

							for (int i = 0; i < appPropsList.getLength(); i++) {

								hostEndpointSpecified = false;
								invocationTypeSpecified = false;
								interfaceFound = false;

								ContainerProxy.LOG
										.debug("Check if information are specified for the correct Interface.");

								propNode = appPropsList.item(i);

								// get properties like endpoint or
								// invocationType
								NodeList appProps = propNode.getChildNodes();

								for (int i2 = 0; i2 < appProps.getLength(); i2++) {

									Node addProp = appProps.item(i2);

									if (addProp.getNodeType() == Node.ELEMENT_NODE) {

										if (addProp.getLocalName().equals(RELATIVE_ENDPOINT)) {
											ContainerProxy.LOG.debug("Endpoint property found.");
											hostEndpointSpecified = true;

										} else if (addProp.getLocalName().equals(INVOCATION_TYPE)) {
											ContainerProxy.LOG.debug("InvocationType property found.");
											invocationTypeSpecified = true;

										} else if (addProp.getLocalName().equals(INTERFACE_INFORMATIONS)) {

											// check if interface matches
											NodeList appInvInterfaceInfo = ((Element) addProp)
													.getElementsByTagNameNS(NAMESPACE, INTERFACE_INFORMATION);

											ContainerProxy.LOG.debug(INTERFACE_INFORMATION + " for "
													+ +appInvInterfaceInfo.getLength() + " Interfaces found.");

											for (int i3 = 0; i3 < appInvInterfaceInfo.getLength(); i3++) {

												String interfName = appInvInterfaceInfo.item(i3).getAttributes()
														.getNamedItem("name").getNodeValue();

												if (interfName.equals(interfaceName)) {
													interfaceFound = true;
													ContainerProxy.LOG.debug("Properties for interface: {} found.",
															interfaceName);

												}
											}
										}
									}
								}
							}
							if (hostEndpointSpecified && invocationTypeSpecified && interfaceFound) {
								ContainerProxy.LOG
										.debug("Properties with all needed information(Endpoint & InvocationType) for interface: "
												+ interfaceName + " of NodeType: " + nodeTypeName + " inside CSAR: "
												+ csarID + " found!");
								return propNode;
							}
						} else {
							ContainerProxy.LOG.debug("ArtifactTemplate : {} has no specified properties.", artifactRef);
						}
					} else {
						ContainerProxy.LOG.debug("No ArtifactTemplate for DA: {} found.", da.getName());
					}
				}
			} else {
				ContainerProxy.LOG.debug("The NodeTypeImplementation {} has no DeploymentArtifacts.",
						nodeTypeImplementation.getName());
			}
		}
		ContainerProxy.LOG.debug("No ArtifactTemplate with needed properties for interface: " + interfaceName
				+ " of NodeType: " + nodeTypeName + " inside CSAR: " + csarID + " found!");
		return null;
	}

	/**
	 * @param propNode
	 * @return relative endpoint, specified in properties (as <tt>Endpoint</tt>
	 *         property).
	 */
	public static String getRelativeEndpoint(Node propNode) {

		// get properties like endpoint or
		// invocationType
		NodeList appProps = propNode.getChildNodes();

		for (int i = 0; i < appProps.getLength(); i++) {

			Node addProp = appProps.item(i);

			if (addProp.getNodeType() == Node.ELEMENT_NODE) {

				if (addProp.getLocalName().equals(RELATIVE_ENDPOINT)) {

					String hostEndpoint = addProp.getTextContent().trim();
					ContainerProxy.LOG.debug("Endpoint property: {}", hostEndpoint);
					return hostEndpoint;

				}
			}
		}
		return null;
	}

	/**
	 * @param propNode
	 * @return invocationType, specified in properties (as
	 *         <tt>InvocationType</tt> property).
	 */
	public static String getInvocationType(Node propNode) {

		// get properties like endpoint or
		// invocationType
		NodeList appProps = propNode.getChildNodes();

		for (int i = 0; i < appProps.getLength(); i++) {

			Node addProp = appProps.item(i);

			if (addProp.getNodeType() == Node.ELEMENT_NODE) {

				if (addProp.getLocalName().equals(INVOCATION_TYPE)) {
					String invocationType = addProp.getTextContent().trim();
					ContainerProxy.LOG.debug("InvocationType property: {}", invocationType);
					return invocationType;

				}
			}
		}
		return null;
	}

	/**
	 * @param propNode
	 * @param interfaceName
	 * @return implementing class specified in the properties of the specified
	 *         interface
	 */
	public static String getClass(Node propNode, String interfaceName) {

		// get properties like endpoint or
		// invocationType
		NodeList appProps = propNode.getChildNodes();

		for (int i = 0; i < appProps.getLength(); i++) {

			Node addProp = appProps.item(i);

			if (addProp.getNodeType() == Node.ELEMENT_NODE) {

				if (addProp.getLocalName().equals(INTERFACE_INFORMATIONS)) {

					// check if interface matches
					NodeList appInvInterfaceInfo = ((Element) addProp).getElementsByTagNameNS(NAMESPACE,
							INTERFACE_INFORMATION);

					ContainerProxy.LOG.debug(
							INTERFACE_INFORMATIONS + " for " + +appInvInterfaceInfo.getLength() + " Interfaces found.");

					for (int i2 = 0; i2 < appInvInterfaceInfo.getLength(); i2++) {

						String interfName = appInvInterfaceInfo.item(i2).getAttributes().getNamedItem("name")
								.getNodeValue();

						if (interfName.equals(interfaceName)) {
							String className = appInvInterfaceInfo.item(i2).getAttributes().getNamedItem("class")
									.getNodeValue();
							ContainerProxy.LOG.debug("Class property: {}", className);
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
	 * @return name of a NodeTemplate of the specified NodeType inside of the
	 *         specified serviceTemplate & csar
	 */
	protected static String getANodeTemplateNameOfNodeType(CSARID csarID, QName serviceTemplateID,
			QName nodeTypeQName) {

		ContainerProxy.LOG.debug("Searching NodeTemplate of NodeType: " + nodeTypeQName + " in the ServiceTemplate: "
				+ serviceTemplateID + " inside the CSAR: " + csarID);

		// get the ServiceTemplate
		TServiceTemplate serviceTemplate = (TServiceTemplate) ToscaServiceHandler.getToscaEngineService()
				.getToscaReferenceMapper().getJAXBReference(csarID, serviceTemplateID);

		for (TEntityTemplate entity : serviceTemplate.getTopologyTemplate().getNodeTemplateOrRelationshipTemplate()) {

			TNodeTemplate nodeTemplate = new TNodeTemplate();

			// get NodeTemplate
			if (entity instanceof TNodeTemplate) {

				nodeTemplate = (TNodeTemplate) entity;

				if (nodeTemplate.getType() != null) {

					if (nodeTemplate.getType().equals(nodeTypeQName)) {

						String nodeTemplateID = nodeTemplate.getId();
						ContainerProxy.LOG.debug("NodeTemplate of NodeType: " + nodeTypeQName
								+ " in the ServiceTemplate: " + serviceTemplateID + " inside the CSAR: " + csarID
								+ " found. NodeTemplateID: " + nodeTemplateID);
						return nodeTemplateID;
					}
				}
			}
		}
		ContainerProxy.LOG.debug("No NodeTemplate of NodeType: " + nodeTypeQName + " in the ServiceTemplate: "
				+ serviceTemplateID + " inside the CSAR: " + csarID + " found.");
		return null;
	}

	/**
	 * Returns the first NodeTemplate underneath the defined NodeTemplate
	 * containing the <tt>Ip-Address</tt> property.
	 * 
	 * @param csarID
	 * @param serviceTemplateID
	 * @param nodeTemplateID
	 * @return name of the first NodeTemplate underneath the defined
	 *         NodeTemplate containing the <tt>Ip-Address</tt> property.
	 * 
	 */
	public static String getHostedOnNodeTemplateWithSpecifiedIPProperty(CSARID csarID, QName serviceTemplateID,
			String nodeTemplateID) {

		ContainerProxy.LOG.debug("Searching NodeTemplate with specified IP-Property underneath the NodeTemplate: "
				+ nodeTemplateID + " of the ServiceTemplate :" + serviceTemplateID + " inside the CSAR: " + csarID);

		Document props = ToscaServiceHandler.getToscaEngineService().getPropertiesOfNodeTemplate(csarID,
				serviceTemplateID, nodeTemplateID);

		QName relationshipType = new QName(HOSTED_ON_NAMESPACE, HOSTED_ON_LOCALPART);

		while ((nodeTemplateID != null) && (getIpProperty(props) == null)) {

			ContainerProxy.LOG.debug("{} isn't the searched NodeTemplate.", nodeTemplateID);
			ContainerProxy.LOG.debug("Getting the underneath Node for checking if it is the searched NodeTemplate.");

			nodeTemplateID = ToscaServiceHandler.getToscaEngineService().getRelatedNodeTemplateID(csarID,
					serviceTemplateID, nodeTemplateID, relationshipType);

			if (nodeTemplateID != null) {
				ContainerProxy.LOG.debug("Checking if the underneath Node: {} is the searched NodeTemplate.",
						nodeTemplateID);

				props = ToscaServiceHandler.getToscaEngineService().getPropertiesOfNodeTemplate(csarID,
						serviceTemplateID, nodeTemplateID);

			} else {
				ContainerProxy.LOG.debug("No underneath Node found.");
			}
		}

		if (nodeTemplateID != null) {
			ContainerProxy.LOG.debug("NodeTemplate with specified IP-Property in the ServiceTemplate: "
					+ serviceTemplateID + " inside the CSAR: " + csarID + " found: " + nodeTemplateID);
		} else {
			ContainerProxy.LOG.debug("No NodeTemplate with specified IP-Property in the ServiceTemplate: "
					+ serviceTemplateID + " inside the CSAR: " + csarID + " found.");
		}
		return nodeTemplateID;
	}

	/**
	 * 
	 * Returns the in the InstanceDataService stored <tt>Ip-Address</tt>
	 * property of the specified ServiceInstance & NodeTemplate.
	 * 
	 * 
	 * @param serviceInstanceID
	 * @param nodeTemplateQName
	 * @return <tt>Ip-Address</tt> property
	 */
	public static URL getIpFromInstanceDataProperties(URI serviceInstanceID, QName nodeTemplateQName) {

		ContainerProxy.LOG.debug("Getting IP-Property from InstanceDataService of NodeTemplate: " + nodeTemplateQName
				+ " of ServiceInstanceID: " + serviceInstanceID + ".");

		List<NodeInstance> nodeInstances = InstanceDataServiceHandler.getInstanceDataService().getNodeInstances(null,
				nodeTemplateQName, null, serviceInstanceID);

		for (NodeInstance nodeInstance : nodeInstances) {

			Document props = nodeInstance.getProperties();

			String ip = getIpProperty(props);

			if (ip != null) {
				ContainerProxy.LOG.debug("IP-Property from InstanceDataService of NodeTemplate: " + nodeTemplateQName
						+ " ServiceInstanceID: " + serviceInstanceID + " found: " + ip);
				try {
					return new URL(ip);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		}
		ContainerProxy.LOG.debug("No IP-Property from InstanceDataService of NodeTemplate: " + nodeTemplateQName
				+ " ServiceInstanceID: " + serviceInstanceID + " found.");
		return null;
	}

	/**
	 * @param props
	 *            to check
	 * @return IP property, if exist. Otherwise null.
	 */
	private static String getIpProperty(Document props) {

		if (props != null) {

			ContainerProxy.LOG.debug(
					"Checking if IP-Property: " + IP_KEY + " is defined in the xml document: " + docToString(props));

			NodeList list = props.getElementsByTagName(IP_KEY);

			if (list.getLength() > 0) {
				String ip = list.item(0).getTextContent();
				ContainerProxy.LOG.debug("Property: {} is defined.", IP_KEY);
				return ip;
			}
		}
		ContainerProxy.LOG.debug("Property: {} is not defined.", IP_KEY);
		return null;
	}

	/**
	 * Transforms a document into a String.
	 * 
	 * @param doc
	 * @return document content as String.
	 */
	private static String docToString(Document doc) {
		String output = null;
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(doc), new StreamResult(writer));
			output = writer.getBuffer().toString().replaceAll("\n|\r", "");
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return output;
	}

}
