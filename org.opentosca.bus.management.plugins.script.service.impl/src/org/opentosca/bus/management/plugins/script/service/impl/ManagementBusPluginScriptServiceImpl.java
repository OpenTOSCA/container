package org.opentosca.bus.management.plugins.script.service.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.opentosca.bus.management.model.header.MBHeader;
import org.opentosca.bus.management.plugins.script.service.impl.servicehandler.ServiceHandler;
import org.opentosca.bus.management.plugins.script.service.impl.util.Messages;
import org.opentosca.bus.management.plugins.service.IManagementBusPluginService;
import org.opentosca.core.model.artifact.AbstractArtifact;
import org.opentosca.core.model.artifact.file.AbstractFile;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.model.instancedata.NodeInstance;
import org.opentosca.model.instancedata.ServiceInstance;
import org.opentosca.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Management Bus-Plug-in for scripts.<br>
 * <br>
 * 
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * The Plug-in gets needed information from the Management Bus and creates a HTTP
 * message for communicating with the ScriptInvoker. It also can communicate
 * with the ToscaEngine as well as the InstanceService.
 * 
 * 
 * 
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 * 
 * 
 * @TODO: maybe use xml schema and less dom api. refactoring needed.
 * 
 */
public class ManagementBusPluginScriptServiceImpl implements IManagementBusPluginService {
	
	final private static Logger LOG = LoggerFactory.getLogger(ManagementBusPluginScriptServiceImpl.class);
	
	// Supported types defined in messages.properties.
	static final private String TYPES = Messages.ScriptSIEnginePlugin_types;
	static final private String HOSTED_ON_NAMESPACE = Messages.ScriptSIEnginePlugin_hosted_on_namespace;
	static final private String HOSTED_ON_LOCALPART = Messages.ScriptSIEnginePlugin_hosted_on_localpart;
	static final private String ADDRESS = Messages.ScriptSIEnginePlugin_address;
	static final private String SSHUSER = Messages.ScriptSIEnginePlugin_user;
	static final private String SSHPRIVATEKEY = Messages.ScriptSIEnginePlugin_key;
	static final private String SCRIPT_INVOKER_URI = Messages.ScriptSIEnginePlugin_script_invoker_uri;
	static final private String CONTAINERA_API_URL = Settings.getSetting("containerUri");
	
	
	@Override
	public Exchange invoke(Exchange exchange) {
		
		ManagementBusPluginScriptServiceImpl.LOG.debug("Getting stored endpoint of script invoker ...");
		String endpoint = System.getenv("ARTIFACT_MANAGER_URL");
		
		if (endpoint == null) {
			ManagementBusPluginScriptServiceImpl.LOG.debug("No endpoint of script invoker stored. Getting default endpoint ...");
			endpoint = ManagementBusPluginScriptServiceImpl.SCRIPT_INVOKER_URI;
		}
		ManagementBusPluginScriptServiceImpl.LOG.debug("Endpoint of script invoker: {}", endpoint);
		
		Message message = exchange.getIn();
		
		Object params = message.getBody();
		
		ManagementBusPluginScriptServiceImpl.LOG.debug("SIEngine Script Plugin getting information...");
		
		CSARID csarID = message.getHeader(MBHeader.CSARID.toString(), CSARID.class);
		ManagementBusPluginScriptServiceImpl.LOG.debug("CsarID: {}", csarID);
		QName artifactTemplateID = message.getHeader(MBHeader.ARTIFACTTEMPLATEID_QNAME.toString(), QName.class);
		ManagementBusPluginScriptServiceImpl.LOG.debug("ArtifactTemplateID: {}", artifactTemplateID);
		String nodeTemplateID = message.getHeader(MBHeader.NODETEMPLATEID_STRING.toString(), String.class);
		ManagementBusPluginScriptServiceImpl.LOG.debug("NodeTemplateID: {}", nodeTemplateID);
		String relationshipTemplateID = message.getHeader(MBHeader.RELATIONSHIPTEMPLATEID_STRING.toString(), String.class);
		ManagementBusPluginScriptServiceImpl.LOG.debug("RelationshipTemplateID: {}", relationshipTemplateID);
		QName serviceTemplateID = message.getHeader(MBHeader.SERVICETEMPLATEID_QNAME.toString(), QName.class);
		ManagementBusPluginScriptServiceImpl.LOG.debug("ServiceTemplateID: {}", serviceTemplateID);
		QName nodeTypeID = message.getHeader(MBHeader.NODETYPEID_QNAME.toString(), QName.class);
		ManagementBusPluginScriptServiceImpl.LOG.debug("NodeTypeID: {}", nodeTypeID);
		QName relationshipTypeID = message.getHeader(MBHeader.RELATIONSHIPTYPEID_QNAME.toString(), QName.class);
		ManagementBusPluginScriptServiceImpl.LOG.debug("RelationshipTypeID: {}", relationshipTypeID);
		String interfaceName = message.getHeader(MBHeader.INTERFACENAME_STRING.toString(), String.class);
		ManagementBusPluginScriptServiceImpl.LOG.debug("InterfaceName: {}", interfaceName);
		String operationName = message.getHeader(MBHeader.OPERATIONNAME_STRING.toString(), String.class);
		ManagementBusPluginScriptServiceImpl.LOG.debug("OperationName: {}", operationName);
		Document artifactSpecificContent = message.getHeader(MBHeader.SPECIFICCONTENT_DOCUMENT.toString(), Document.class);
		URI serviceInstanceID = message.getHeader(MBHeader.SERVICEINSTANCEID_URI.toString(), URI.class);
		ManagementBusPluginScriptServiceImpl.LOG.debug("ServiceInstanceID: {}", serviceInstanceID);
		
		Node artifactTemplateNode = null;
		if (artifactTemplateID != null) {
			artifactTemplateNode = ServiceHandler.toscaEngineService.getReferenceAsNode(csarID, artifactTemplateID);
		}
		
		Document relationshipTemplatePropertiesDoc = null;
		
		if (relationshipTemplateID != null) {
			
			relationshipTemplatePropertiesDoc = ServiceHandler.toscaEngineService.getPropertiesOfRelationshipTemplate(csarID, serviceTemplateID, relationshipTemplateID);
		}
		
		String serviceTemplateName = ServiceHandler.toscaEngineService.getNameOfReference(csarID, serviceTemplateID);
		
		if (params instanceof HashMap) {
			
			@SuppressWarnings("unchecked")
			HashMap<String, String> paramsMap = (HashMap<String, String>) params;
			
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder;
			
			Document definitions = null;
			
			try {
				
				ManagementBusPluginScriptServiceImpl.LOG.debug("Creating the xml request...");
				
				docBuilder = docFactory.newDocumentBuilder();
				definitions = docBuilder.newDocument();
				Element rootElement = definitions.createElementNS("http://docs.oasis-open.org/tosca/ns/2011/12", "Definitions");
				rootElement.setAttribute("id", serviceTemplateID.getLocalPart());
				rootElement.setAttribute("name", serviceTemplateName);
				rootElement.setAttribute("targetNamespace", "http://www.opentosca.org/script");
				rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:toscaBase", "http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes");
				rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:s", "http://www.opentosca.org/script");
				rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
				rootElement.setAttribute("xsi:schemaLocation", "http://docs.oasis-open.org/tosca/ns/2011/12 TOSCA-v1.0-cs02.xsd");
				definitions.appendChild(rootElement);
				
				// add ArtifactTemplate or ArtifactSpecificContent
				if (artifactTemplateNode != null) {
					
					artifactTemplateNode = this.getArtifactTemplateNodeWithResolvedReferences(definitions, artifactTemplateNode, csarID, artifactTemplateID);
					
					ManagementBusPluginScriptServiceImpl.LOG.debug("Adding the ArtifactTemplate...");
					
					rootElement.appendChild(artifactTemplateNode);
					
				} else if (artifactSpecificContent != null) {
					
					ManagementBusPluginScriptServiceImpl.LOG.debug("ArtifactTemplate not specified. Adding ArtifactSpecificContent instead...");
					
					Element artifactTemplateElement = definitions.createElement("ArtifactTemplate");
					rootElement.appendChild(artifactTemplateElement);
					Element content = artifactSpecificContent.getDocumentElement();
					definitions.adoptNode(content);
					artifactTemplateElement.appendChild(content);
				}
				
				// artifactInstanceContext
				ManagementBusPluginScriptServiceImpl.LOG.debug("Creating ArtifactContext element...");
				
				Element artifactContext = definitions.createElementNS("http://www.opentosca.org/script", "ArtifactContext");
				artifactContext.setPrefix("s");
				
				// Files
				ManagementBusPluginScriptServiceImpl.LOG.debug("Creating files element ...");
				
				Element files = definitions.createElement("Files");
				String url = (ManagementBusPluginScriptServiceImpl.CONTAINERA_API_URL + "/CSARs/" + csarID.getFileName() + "/Content/");
				files.setAttribute("url", url);
				
				ManagementBusPluginScriptServiceImpl.LOG.debug("Files URL: {}", url);
				ManagementBusPluginScriptServiceImpl.LOG.debug("Adding files element to ArtifactContext...");
				
				artifactContext.appendChild(files);
				
				// Create OperationParameters element
				Element operationParameters = null;
				if (nodeTemplateID != null) {
					
					ManagementBusPluginScriptServiceImpl.LOG.debug("Creating Operation element of NodeType: {} ...", nodeTypeID);
					
					operationParameters = this.getOperationParametersOfANodeTypeElement(csarID, nodeTypeID, interfaceName, operationName, paramsMap, definitions);
					
				} else if (relationshipTemplateID != null) {
					
					ManagementBusPluginScriptServiceImpl.LOG.debug("Creating Operation element of RelationshipType: {} ...", relationshipTypeID);
					
					operationParameters = this.getOperationParametersOfARelationshipTypeElement(csarID, relationshipTypeID, interfaceName, operationName, paramsMap, definitions);
				}
				
				ManagementBusPluginScriptServiceImpl.LOG.debug("Adding Operation element to ArtifactContext...");
				
				artifactContext.appendChild(operationParameters);
				
				if (nodeTemplateID != null) {
					
					// Node element
					ManagementBusPluginScriptServiceImpl.LOG.debug("Creating Node element ...");
					
					Element node = definitions.createElement("Node");
					
					Element nodeProperties = this.getNodePropertiesElement(csarID, serviceTemplateID, serviceTemplateName, nodeTemplateID, definitions, serviceInstanceID);
					
					ManagementBusPluginScriptServiceImpl.LOG.debug("Adding Node element to ArtifactContext...");
					
					node.appendChild(nodeProperties);
					
					// HostProperties element
					ManagementBusPluginScriptServiceImpl.LOG.debug("Creating HostProperties element ...");
					
					Element hostElement = this.getHostPropertiesElement(csarID, serviceTemplateID, serviceTemplateName, nodeTemplateID, definitions, serviceInstanceID);
					
					ManagementBusPluginScriptServiceImpl.LOG.debug("Adding host element to ArtifactContext...");
					
					node.appendChild(hostElement);
					artifactContext.appendChild(node);
					
				} else if (relationshipTypeID != null) {
					
					// Relationship
					ManagementBusPluginScriptServiceImpl.LOG.debug("Creating Relationship element ...");
					
					Element relationshipElement = definitions.createElement("Relationship");
					
					Element boundToElement = definitions.createElement("OperationBoundTo");
					
					boolean isBoundToSourceNode = ServiceHandler.toscaEngineService.isOperationOfRelationshipBoundToSourceNode(csarID, relationshipTypeID, interfaceName, operationName);
					
					// If not bound to source, the operation has to be bound to
					// target node.
					// @TODO: If SourceInterface & TargetInterface both have an
					// operation with the same name, it is unclear if it is
					// bound to source or target node. New InputParameter for
					// SI-Interface needed which specifies if operation is bound
					// to source or target node.
					if (isBoundToSourceNode) {
						boundToElement.setTextContent("source");
					} else {
						boundToElement.setTextContent("target");
					}
					
					Element relationshipTemplateProperties = relationshipTemplatePropertiesDoc.getDocumentElement();
					
					ManagementBusPluginScriptServiceImpl.LOG.debug("Adding Relationship element to ArtifactContext...");
					
					definitions.adoptNode(relationshipTemplateProperties);
					relationshipElement.appendChild(boundToElement);
					relationshipElement.appendChild(relationshipTemplateProperties);
					artifactContext.appendChild(relationshipElement);
					
					// SourceNode
					ManagementBusPluginScriptServiceImpl.LOG.debug("Creating SourceNode element to ArtifactContext...");
					
					Element sourceNode = definitions.createElement("SourceNode");
					
					String sourceNodeTemplate = ServiceHandler.toscaEngineService.getSourceNodeTemplateIDOfRelationshipTemplate(csarID, serviceTemplateID, relationshipTemplateID);
					
					// SourceNode: NodeProperties
					ManagementBusPluginScriptServiceImpl.LOG.debug("Creating NodeProperties element of the SourceNode...");
					
					Element sourceNodeProperties = this.getNodePropertiesElement(csarID, serviceTemplateID, serviceTemplateName, sourceNodeTemplate, definitions, serviceInstanceID);
					
					ManagementBusPluginScriptServiceImpl.LOG.debug("Adding NodeProperties element of the SourceNode to SourceNode element...");
					
					sourceNode.appendChild(sourceNodeProperties);
					
					// SourceNode: HostProperties
					ManagementBusPluginScriptServiceImpl.LOG.debug("Creating HostProperties element of the SourceNode...");
					
					Element sourceHostProperties = this.getHostPropertiesElement(csarID, serviceTemplateID, serviceTemplateName, sourceNodeTemplate, definitions, serviceInstanceID);
					
					ManagementBusPluginScriptServiceImpl.LOG.debug("Adding HostProperties element of the SourceNode to SourceNode element...");
					
					sourceNode.appendChild(sourceHostProperties);
					
					ManagementBusPluginScriptServiceImpl.LOG.debug("Adding SourceNode element to ArtifactContext...");
					
					artifactContext.appendChild(sourceNode);
					
					// TargetNode
					ManagementBusPluginScriptServiceImpl.LOG.debug("Creating TargetNode element to ArtifactContext...");
					
					Element targetNode = definitions.createElement("TargetNode");
					
					String targetNodeTemplate = ServiceHandler.toscaEngineService.getTargetNodeTemplateIDOfRelationshipTemplate(csarID, serviceTemplateID, relationshipTemplateID);
					
					// SourceNode: NodeProperties
					ManagementBusPluginScriptServiceImpl.LOG.debug("Creating NodeProperties element of the TargetNode...");
					
					Element targetNodeProperties = this.getNodePropertiesElement(csarID, serviceTemplateID, serviceTemplateName, targetNodeTemplate, definitions, serviceInstanceID);
					
					ManagementBusPluginScriptServiceImpl.LOG.debug("Adding NodeProperties element of the TargetNode to TargetNode element...");
					
					targetNode.appendChild(targetNodeProperties);
					
					// SourceNode: HostProperties
					ManagementBusPluginScriptServiceImpl.LOG.debug("Creating HostProperties element of the TargetNode...");
					
					Element targetHostProperties = this.getHostPropertiesElement(csarID, serviceTemplateID, serviceTemplateName, targetNodeTemplate, definitions, serviceInstanceID);
					
					ManagementBusPluginScriptServiceImpl.LOG.debug("Adding HostProperties element of the TargetNode to TargetNode element...");
					
					targetNode.appendChild(targetHostProperties);
					
					ManagementBusPluginScriptServiceImpl.LOG.debug("Adding TargetNode element to ArtifactContext...");
					
					artifactContext.appendChild(targetNode);
					
				}
				
				ManagementBusPluginScriptServiceImpl.LOG.debug("Adding ArtifactContext element to Definition...");
				
				rootElement.appendChild(artifactContext);
				
				ManagementBusPluginScriptServiceImpl.LOG.debug("Created xml: ");
				
				TransformerFactory.newInstance().newTransformer().transform(new DOMSource(definitions), new StreamResult(System.out));
				
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerFactoryConfigurationError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Map<String, Object> headers = new HashMap<String, Object>();
			headers.put(Exchange.HTTP_URI, endpoint);
			headers.put(Exchange.CONTENT_TYPE, "application/xml");
			headers.put(Exchange.HTTP_METHOD, "POST");
			
			ProducerTemplate template = Activator.camelContext.createProducerTemplate();
			
			ManagementBusPluginScriptServiceImpl.LOG.debug("Sending the request to the ScriptInvoker...");
			
			Document response = template.requestBodyAndHeaders("direct:RequestResponseRoute", definitions, headers, Document.class);
			
			if (response != null) {
				
				HashMap<String, String> responseMap = this.docToMap(response);
				
				exchange.getIn().setBody(responseMap);
				
			} else {
				exchange.getIn().setBody(response);
			}
			
		}
		
		return exchange;
	}
	
	/**
	 * @param csarID
	 * @param serviceTemplateID
	 * @param serviceTemplateName
	 * @param nodeTemplateID
	 * @param definitions
	 * @param serviceInstanceID
	 * @return
	 */
	private Element getNodePropertiesElement(CSARID csarID, QName serviceTemplateID, String serviceTemplateName, String nodeTemplateID, Document definitions, URI serviceInstanceID) {
		
		String nodeTypeName = ServiceHandler.toscaEngineService.getNodeTypeOfNodeTemplate(csarID, serviceTemplateID, nodeTemplateID).getLocalPart();
		
		Element nodePropertiesElement = definitions.createElement("NodeProperties");
		nodePropertiesElement.setAttribute("nodeTypeName", nodeTypeName);
		
		Element propsElement = null;
		
		ManagementBusPluginScriptServiceImpl.LOG.debug("Requesting stored InstanceData for NodeTemplate: {} ", nodeTemplateID);
		Document props = this.getInstanceDataProperties(csarID, serviceTemplateID, serviceTemplateName, nodeTemplateID, serviceInstanceID);
		
		if (props != null) {
			ManagementBusPluginScriptServiceImpl.LOG.debug("No InstanceData stored for NodeTemplate {}. Using default properties.", nodeTemplateID);
			propsElement = props.getDocumentElement();
			
		} else {
			
			props = ServiceHandler.toscaEngineService.getPropertiesOfNodeTemplate(csarID, serviceTemplateID, nodeTemplateID);
			
			if (props != null) {
				propsElement = props.getDocumentElement();
			}
		}
		
		if (propsElement != null) {
			definitions.adoptNode(props.getDocumentElement());
			nodePropertiesElement.appendChild(propsElement);
		}
		
		return nodePropertiesElement;
	}
	
	/**
	 * Returns the "HostProperties " element containing the address as attribute
	 * as well as the platform, sshUser & sshPrivateKey as sub-nodes.
	 * 
	 * @param csarID
	 * @param serviceTemplateID
	 * @param serviceTemplateName
	 * @param nodeTemplateID
	 * @param definitions
	 * @param serviceInstanceID
	 * @param paramsMap
	 * 
	 * 
	 * @return host element.
	 */
	private Element getHostPropertiesElement(CSARID csarID, QName serviceTemplateID, String serviceTemplateName, String nodeTemplateID, Document definitions, URI serviceInstanceID) {
		
		String hostNodeTemplateID = this.getOperatingSystemNodeTemplateID(csarID, serviceTemplateID, serviceTemplateName, nodeTemplateID, serviceInstanceID);
		
		Element hostPropertiesElement = definitions.createElement("HostProperties");
		
		if (hostNodeTemplateID != null) {
			
			String nodeTypeName = ServiceHandler.toscaEngineService.getNodeTypeOfNodeTemplate(csarID, serviceTemplateID, hostNodeTemplateID).getLocalPart();
			
			hostPropertiesElement.setAttribute("nodeTypeName", nodeTypeName);
			
			Element propsElement;
			
			ManagementBusPluginScriptServiceImpl.LOG.debug("Requesting stored InstanceData for NodeTemplate: {} ", hostNodeTemplateID);
			
			Document props = this.getInstanceDataProperties(csarID, serviceTemplateID, serviceTemplateName, hostNodeTemplateID, serviceInstanceID);
			
			if (props != null) {
				ManagementBusPluginScriptServiceImpl.LOG.debug("No InstanceData stored for NodeTemplate {}. Using default properties.", hostNodeTemplateID);
				propsElement = props.getDocumentElement();
				
			} else {
				
				props = ServiceHandler.toscaEngineService.getPropertiesOfNodeTemplate(csarID, serviceTemplateID, hostNodeTemplateID);
				propsElement = props.getDocumentElement();
			}
			
			definitions.adoptNode(props.getDocumentElement());
			hostPropertiesElement.appendChild(propsElement);
		}
		
		return hostPropertiesElement;
	}
	
	/**
	 * Returns the OperatingSystemNodeTemplate which is the first Node
	 * underneath the defined nodeTemplate containing the three properties
	 * address, SSSHUser & SSHPrivateKey.
	 * 
	 * @param csarID
	 * @param serviceTemplateID
	 * @param serviceTemplateName
	 * @param nodeTemplateID
	 * @param serviceInstanceID
	 * @return
	 */
	private String getOperatingSystemNodeTemplateID(CSARID csarID, QName serviceTemplateID, String serviceTemplateName, String nodeTemplateID, URI serviceInstanceID) {
		
		ManagementBusPluginScriptServiceImpl.LOG.debug("Searching the OperatingSystemNode of NodeTemplate: {} ...", nodeTemplateID);
		
		Document propsDefaults = ServiceHandler.toscaEngineService.getPropertiesOfNodeTemplate(csarID, serviceTemplateID, nodeTemplateID);
		Document propsInstanceData = this.getInstanceDataProperties(csarID, serviceTemplateID, serviceTemplateName, nodeTemplateID, serviceInstanceID);
		
		QName relationshipType = new QName(ManagementBusPluginScriptServiceImpl.HOSTED_ON_NAMESPACE, ManagementBusPluginScriptServiceImpl.HOSTED_ON_LOCALPART);
		
		while ((!this.isOperatingSystemNode(propsDefaults)) && (!this.isOperatingSystemNode(propsInstanceData)) && (nodeTemplateID != null)) {
			
			ManagementBusPluginScriptServiceImpl.LOG.debug("{} isn't the OperatingSystemNode.", nodeTemplateID);
			ManagementBusPluginScriptServiceImpl.LOG.debug("Getting the underneath Node for checking if it is the OperatingSystemNode...");
			
			nodeTemplateID = ServiceHandler.toscaEngineService.getRelatedNodeTemplateID(csarID, serviceTemplateID, nodeTemplateID, relationshipType);
			
			if (nodeTemplateID != null) {
				ManagementBusPluginScriptServiceImpl.LOG.debug("Checking if the underneath Node: {} is the OperatingSystemNode.", nodeTemplateID);
				
				propsDefaults = ServiceHandler.toscaEngineService.getPropertiesOfNodeTemplate(csarID, serviceTemplateID, nodeTemplateID);
				propsInstanceData = this.getInstanceDataProperties(csarID, serviceTemplateID, serviceTemplateName, nodeTemplateID, serviceInstanceID);
				
			} else {
				ManagementBusPluginScriptServiceImpl.LOG.debug("No underneath Node found.");
			}
			
		}
		
		if (nodeTemplateID != null) {
			ManagementBusPluginScriptServiceImpl.LOG.debug("OperatingSystemNode found: {}", nodeTemplateID);
		}
		
		return nodeTemplateID;
		
	}
	
	/**
	 * Checks if the passed xml document contains the needed properties
	 * identifying an OperationSystem Node.
	 * 
	 * @param props to check
	 * @return if the xml contains needed properties.
	 */
	private boolean isOperatingSystemNode(Document props) {
		
		boolean address = false;
		boolean user = false;
		boolean key = false;
		
		if (props != null) {
			
			if (props.getElementsByTagName(ManagementBusPluginScriptServiceImpl.ADDRESS).getLength() > 0) {
				address = true;
				ManagementBusPluginScriptServiceImpl.LOG.debug("Property {} is defined.", ManagementBusPluginScriptServiceImpl.ADDRESS);
				
			}
			if (props.getElementsByTagName(ManagementBusPluginScriptServiceImpl.SSHUSER).getLength() > 0) {
				user = true;
				ManagementBusPluginScriptServiceImpl.LOG.debug("Property {} is defined.", ManagementBusPluginScriptServiceImpl.SSHUSER);
				
			}
			if (props.getElementsByTagName(ManagementBusPluginScriptServiceImpl.SSHPRIVATEKEY).getLength() > 0) {
				key = true;
				ManagementBusPluginScriptServiceImpl.LOG.debug("Property {} is defined.", ManagementBusPluginScriptServiceImpl.SSHPRIVATEKEY);
			}
			
		}
		
		return (address && user && key);
		
	}
	
	/**
	 * Returns the OperationParameters Element containing In-& Output parameters
	 * for the specified operation defined in the tosca.
	 * 
	 * @param csarID
	 * @param nodeTypeID
	 * @param interfaceName
	 * @param operationName
	 * @param paramsMap
	 * @param defintions
	 * @return Element containing In-& Output Parameters and their values if
	 *         available.
	 */
	private Element getOperationParametersOfANodeTypeElement(CSARID csarID, QName nodeTypeID, String interfaceName, String operationName, HashMap<String, String> paramsMap, Document defintions) {
		
		Element operationParameters = defintions.createElement("Operation");
		
		if (ServiceHandler.toscaEngineService.hasOperationOfANodeTypeSpecifiedInputParams(csarID, nodeTypeID, interfaceName, operationName)) {
			
			Node definedInputParameters = ServiceHandler.toscaEngineService.getInputParametersOfANodeTypeOperation(csarID, nodeTypeID, interfaceName, operationName);
			NodeList definedInputParameterList = definedInputParameters.getChildNodes();
			
			for (int i = 0; i < definedInputParameterList.getLength(); i++) {
				
				Node currentNode = definedInputParameterList.item(i);
				
				if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
					
					String name = ((Element) currentNode).getAttribute("name");
					
					if (paramsMap.containsKey(name)) {
						currentNode.setTextContent(paramsMap.get(name));
					}
					
					Node copyNode = defintions.importNode(currentNode, true);
					operationParameters.appendChild(copyNode);
					
				}
			}
		}
		
		if (ServiceHandler.toscaEngineService.hasOperationOfANodeTypeSpecifiedOutputParams(csarID, nodeTypeID, interfaceName, operationName)) {
			
			Node definedOutputParameters = ServiceHandler.toscaEngineService.getOutputParametersOfANodeTypeOperation(csarID, nodeTypeID, interfaceName, operationName);
			NodeList definedOutputParameterList = definedOutputParameters.getChildNodes();
			
			for (int i = 0; i < definedOutputParameterList.getLength(); i++) {
				
				Node currentNode = definedOutputParameterList.item(i);
				
				if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
					
					Node copyNode = defintions.importNode(currentNode, true);
					operationParameters.appendChild(copyNode);
					
				}
			}
		}
		return operationParameters;
	}
	
	/**
	 * Returns the OperationParameters Element containing In-& Output parameters
	 * for the specified operation defined in the tosca.
	 * 
	 * @param csarID
	 * @param nodeTypeID
	 * @param interfaceName
	 * @param operationName
	 * @param paramsMap
	 * @param defintions
	 * @return Element containing In-& Output Parameters and their values if
	 *         available.
	 */
	private Element getOperationParametersOfARelationshipTypeElement(CSARID csarID, QName relationshipTypeID, String interfaceName, String operationName, HashMap<String, String> paramsMap, Document defintions) {
		
		Element operationParameters = defintions.createElement("Operation");
		
		if (ServiceHandler.toscaEngineService.hasOperationOfARelationshipTypeSpecifiedInputParams(csarID, relationshipTypeID, interfaceName, operationName)) {
			
			Node definedInputParameters = ServiceHandler.toscaEngineService.getInputParametersOfARelationshipTypeOperation(csarID, relationshipTypeID, interfaceName, operationName);
			NodeList definedInputParameterList = definedInputParameters.getChildNodes();
			
			for (int i = 0; i < definedInputParameterList.getLength(); i++) {
				
				Node currentNode = definedInputParameterList.item(i);
				
				if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
					
					String name = ((Element) currentNode).getAttribute("name");
					
					if (paramsMap.containsKey(name)) {
						currentNode.setTextContent(paramsMap.get(name));
					}
					
					Node copyNode = defintions.importNode(currentNode, true);
					operationParameters.appendChild(copyNode);
					
				}
			}
		}
		
		if (ServiceHandler.toscaEngineService.hasOperationOfARelationshipTypeSpecifiedOutputParams(csarID, relationshipTypeID, interfaceName, operationName)) {
			
			Node definedOutputParameters = ServiceHandler.toscaEngineService.getOutputParametersOfARelationshipTypeOperation(csarID, relationshipTypeID, interfaceName, operationName);
			NodeList definedOutputParameterList = definedOutputParameters.getChildNodes();
			
			for (int i = 0; i < definedOutputParameterList.getLength(); i++) {
				
				Node currentNode = definedOutputParameterList.item(i);
				
				if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
					
					Node copyNode = defintions.importNode(currentNode, true);
					operationParameters.appendChild(copyNode);
					
				}
			}
		}
		return operationParameters;
	}
	
	private Node getArtifactTemplateNodeWithResolvedReferences(Document doc, Node artifactTemplateNode, CSARID csarID, QName artifactTemplateID) {
		
		ManagementBusPluginScriptServiceImpl.LOG.debug("Resolving the artifact references of ArtifactTemplate: {} ...", artifactTemplateID);
		
		List<AbstractArtifact> artifacts = ServiceHandler.toscaEngineService.getArtifactsOfAArtifactTemplate(csarID, artifactTemplateID);
		
		Element referencesElement = doc.createElement("ArtifactReferences");
		
		doc.adoptNode(artifactTemplateNode);
		
		for (AbstractArtifact artifact : artifacts) {
			
			Set<AbstractFile> files = artifact.getFilesRecursively();
			
			for (AbstractFile file : files) {
				Element referenceElement = doc.createElement("ArtifactReference");
				referenceElement.setAttribute("reference", file.getPath().replace('\\', '/'));
				referencesElement.appendChild(referenceElement);
			}
		}
		
		NodeList nodeList = artifactTemplateNode.getChildNodes();
		
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node currentNode = nodeList.item(i);
			if ((currentNode.getNodeType() == Node.ELEMENT_NODE) && (currentNode.getLocalName() != null)) {
				if (currentNode.getLocalName().equals("ArtifactReferences")) {
					artifactTemplateNode.removeChild(currentNode);
					artifactTemplateNode.appendChild(referencesElement);
				}
			}
		}
		
		return artifactTemplateNode;
		
	}
	
	/**
	 * @param csarID
	 * @param serviceTemplateID
	 * @param serviceTemplateName
	 * @param nodeTemplateID
	 * @param serviceInstanceID
	 * @return the in the InstanceService stored properties for the specified
	 *         parameters or null if it can not be found.
	 */
	private Document getInstanceDataProperties(CSARID csarID, QName serviceTemplateID, String serviceTemplateName, String nodeTemplateID, URI serviceInstanceID) {
		
		if (serviceInstanceID != null) {
			
			List<ServiceInstance> serviceInstanceList = ServiceHandler.instanceDataService.getServiceInstances(serviceInstanceID, serviceTemplateName, serviceTemplateID);
			
			QName nodeTemplateQName = new QName(serviceTemplateID.getNamespaceURI(), nodeTemplateID);
			
			for (ServiceInstance serviceInstance : serviceInstanceList) {
				
				if (serviceInstance.getCSAR_ID().toString().equals(csarID.toString())) {
					
					List<NodeInstance> nodeInstanceList = serviceInstance.getNodeInstances();
					
					for (NodeInstance nodeInstance : nodeInstanceList) {
						
						if (nodeInstance.getNodeTemplateID().equals(nodeTemplateQName)) {
							
							return nodeInstance.getProperties();
							
						} else {
							ManagementBusPluginScriptServiceImpl.LOG.debug("No InstanceData found for NodeTemplate: {}.", nodeTemplateQName);
						}
						
					}
					
				} else {
					ManagementBusPluginScriptServiceImpl.LOG.debug("No InstanceData found for CsarID: " + csarID + ", ServiceTemplateID: " + serviceTemplateID + ", ServiceTemplateName: " + serviceTemplateName + " and ServiceInstanceID: " + serviceInstanceID);
				}
			}
			
		}
		return null;
	}
	
	/**
	 * Transfers the response document to a map.
	 * 
	 * @param doc to be transfered to a map.
	 * @return transfered map.
	 */
	private HashMap<String, String> docToMap(Document doc) {
		
		HashMap<String, String> map = new HashMap<String, String>();
		
		NodeList paramList = doc.getElementsByTagName("param");
		
		String paramName = null;
		String paramValue = null;
		
		for (int i = 0; i < paramList.getLength(); i++) {
			
			Node paramNode = paramList.item(i);
			
			NodeList paramAttList = paramNode.getChildNodes();
			
			for (int i2 = 0; i2 < paramAttList.getLength(); i2++) {
				
				Node paramAtt = paramAttList.item(i2);
				
				if (paramAtt.getLocalName() != null) {
					
					if (paramAtt.getLocalName().equals("name")) {
						paramName = paramAtt.getTextContent();
					}
					
					if (paramAtt.getLocalName().equals("value")) {
						paramValue = paramAtt.getTextContent();
					}
				}
			}
			
			map.put(paramName, paramValue);
		}
		
		return map;
	}
	
	@Override
	public List<String> getSupportedTypes() {
		ManagementBusPluginScriptServiceImpl.LOG.debug("Getting Types: {}.", ManagementBusPluginScriptServiceImpl.TYPES);
		List<String> types = new ArrayList<String>();
		
		for (String type : ManagementBusPluginScriptServiceImpl.TYPES.split("[,;]")) {
			types.add(type.trim());
		}
		return types;
	}
	
}
