package org.opentosca.bus.management.service.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.opentosca.bus.management.model.header.MBHeader;
import org.opentosca.bus.management.plugins.service.IManagementBusPluginService;
import org.opentosca.bus.management.service.IManagementBusService;
import org.opentosca.bus.management.service.impl.servicehandler.ServiceHandler;
import org.opentosca.core.endpoint.service.ICoreEndpointService;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.core.model.endpoint.wsdl.WSDLEndpoint;
import org.opentosca.model.instancedata.NodeInstance;
import org.opentosca.model.instancedata.ServiceInstance;
import org.opentosca.toscaengine.service.IToscaEngineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;

/**
 * Engine for delegating invoke-requests of implementation artifacts or plans to
 * matching plug-ins.<br>
 * <br>
 * 
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * The engine gets the invoke-request as a camel exchange object with all needed
 * parameters (e.g. CSARID, ServiceTemplateID,...) in the header and the actual
 * invoke message in the body of it. In case of invoking an operation of an
 * implementation artifact, the engine identify with help of the ToscaEngine and
 * the parameters from the header the right implementation artifact. Via
 * EndpointService the engine determine the endpoint of the implementation
 * artifact or the plan. The engine also handles the plug-ins. To determine
 * which plug-in can execute the invoke-request, the engine needs a specified
 * property like <tt>{@literal <}namespace:InvocationType{@literal >}...
 * {@literal <}/namespace:InvocationType{@literal >}</tt>. The engine also can
 * update request parameters from stored InstanceData.
 * 
 * @see IManagementBusPluginService
 * @see IToscaEngineService
 * @see ICoreEndpointService
 * 
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 * 
 * 
 */

public class ManagementBusServiceImpl implements IManagementBusService {

	private final static Logger LOG = LoggerFactory.getLogger(ManagementBusServiceImpl.class);

	@Override
	public void invokeIA(Exchange exchange) {

		ManagementBusServiceImpl.LOG.debug("Starting Management Bus: InvokeIA");

		Message message = exchange.getIn();

		CSARID csarID = message.getHeader(MBHeader.CSARID.toString(), CSARID.class);
		ManagementBusServiceImpl.LOG.debug("CSARID: {}", csarID.toString());

		URI serviceInstanceID = message.getHeader(MBHeader.SERVICEINSTANCEID_URI.toString(), URI.class);
		ManagementBusServiceImpl.LOG.debug("serviceInstanceID: {}", serviceInstanceID);

		String nodeInstanceID = message.getHeader(MBHeader.NODEINSTANCEID_STRING.toString(), String.class);
		ManagementBusServiceImpl.LOG.debug("nodeInstanceID: {}", nodeInstanceID);

		QName serviceTemplateID = message.getHeader(MBHeader.SERVICETEMPLATEID_QNAME.toString(), QName.class);
		ManagementBusServiceImpl.LOG.debug("serviceTemplateID: {}", serviceTemplateID);

		String nodeTemplateID = message.getHeader(MBHeader.NODETEMPLATEID_STRING.toString(), String.class);
		ManagementBusServiceImpl.LOG.debug("nodeTemplateID: {}", nodeTemplateID);

		String relationshipTemplateID = message.getHeader(MBHeader.RELATIONSHIPTEMPLATEID_STRING.toString(),
				String.class);
		ManagementBusServiceImpl.LOG.debug("relationshipTemplateID: {}", relationshipTemplateID);

		String neededInterface = message.getHeader(MBHeader.INTERFACENAME_STRING.toString(), String.class);
		ManagementBusServiceImpl.LOG.debug("Interface: {}", neededInterface);

		String neededOperation = message.getHeader(MBHeader.OPERATIONNAME_STRING.toString(), String.class);
		ManagementBusServiceImpl.LOG.debug("Operation: {}", neededOperation);

		boolean wasFound = false;
		String invocationType = null;

		if (nodeTemplateID != null) {

			QName nodeTypeID = ServiceHandler.toscaEngineService.getNodeTypeOfNodeTemplate(csarID, serviceTemplateID,
					nodeTemplateID);

			if (message.getBody() instanceof HashMap) {

				@SuppressWarnings("unchecked")
				HashMap<String, String> inputParams = (HashMap<String, String>) message.getBody();

				// update inputParams with instance data
				inputParams = updateInputParams(inputParams, csarID, serviceTemplateID, nodeTypeID, nodeTemplateID,
						neededInterface, neededOperation, serviceInstanceID);
				message.setBody(inputParams);

			} else {
				ManagementBusServiceImpl.LOG.warn("There are no input parameters specified.");
			}

			ManagementBusServiceImpl.LOG.debug("Getting information about the ImplementationArtifact from TOSCA Engine...");

			ManagementBusServiceImpl.LOG.debug("Getting nodeTypeImplementationIDs of NodeType: {} from CSAR: {}", nodeTypeID,
					csarID);

			List<QName> nodeTypeImplementationIDs = ServiceHandler.toscaEngineService
					.getNodeTypeImplementationsOfNodeType(csarID, nodeTypeID);
			ManagementBusServiceImpl.LOG.debug("nodeTypeImplementationIDs: {}", nodeTypeImplementationIDs.toString());

			// Jump-Label to stop both loops at once
			searchIA: for (QName nodeTypeImplementationID : nodeTypeImplementationIDs) {

				List<String> implementationArtifactNames = ServiceHandler.toscaEngineService
						.getImplementationArtifactNamesOfNodeTypeImplementation(csarID, nodeTypeImplementationID);
				ManagementBusServiceImpl.LOG.debug("implementationArtifactNames: {}",
						implementationArtifactNames.toString());

				for (String implementationArtifactName : implementationArtifactNames) {

					// Check if needed interface/operation is provided
					if (this.isCorrectIA(csarID, nodeTypeID, nodeTypeImplementationID, null, null,
							implementationArtifactName, neededOperation, neededInterface)) {

						QName artifactTemplateID = ServiceHandler.toscaEngineService
								.getArtifactTemplateOfAImplementationArtifactOfANodeTypeImplementation(csarID,
										nodeTypeImplementationID, implementationArtifactName);
						ManagementBusServiceImpl.LOG.debug("artifactTemplateID: {}", artifactTemplateID.toString());

						String artifactType = ServiceHandler.toscaEngineService
								.getArtifactTypeOfAImplementationArtifactOfANodeTypeImplementation(csarID,
										nodeTypeImplementationID, implementationArtifactName)
								.toString();

						invocationType = this.isSupported(artifactType, csarID, artifactTemplateID);

						if (invocationType != null) {
							ManagementBusServiceImpl.LOG.debug("InvocationType found: {} ", invocationType);

							ManagementBusServiceImpl.LOG.debug(
									"Getting Endpoint for ImplementationArtifact: {} from NodeTypeImplementation: {}",
									implementationArtifactName, nodeTypeImplementationID);
							// EndpointService needs to be refactored.
							// Distinction of WSDL &
							// REST Endpoints is obsolete.
							WSDLEndpoint wsdlEndpoint = ServiceHandler.endpointService.getWSDLEndpointForIa(csarID,
									nodeTypeImplementationID, implementationArtifactName);

							// Check if implementation artifact has a stored
							// endpoint and thus was deployed
							if (wsdlEndpoint != null) {

								URI endpoint = wsdlEndpoint.getURI();
								ManagementBusServiceImpl.LOG.debug("Endpoint: " + endpoint.toString());

								message.setHeader(MBHeader.ENDPOINT_URI.toString(), endpoint);

								boolean hasInputParams = ServiceHandler.toscaEngineService
										.hasOperationOfANodeTypeSpecifiedInputParams(csarID, nodeTypeID,
												neededInterface, neededOperation);
								boolean hasOutputParams = ServiceHandler.toscaEngineService
										.hasOperationOfANodeTypeSpecifiedOutputParams(csarID, nodeTypeID,
												neededInterface, neededOperation);

								if (hasInputParams && !hasOutputParams) {
									message.setHeader(MBHeader.HASOUTPUTPARAMS_BOOLEAN.toString(), false);
								} else {
									message.setHeader(MBHeader.HASOUTPUTPARAMS_BOOLEAN.toString(), true);
								}

								Document specificContent = ServiceHandler.toscaEngineService
										.getArtifactSpecificContentOfAImplementationArtifactOfANodeTypeImplementation(
												csarID, nodeTypeImplementationID, implementationArtifactName);

								if (specificContent != null) {

									ManagementBusServiceImpl.LOG.debug("ArtifactSpecificContent specified!");
									message.setHeader(MBHeader.SPECIFICCONTENT_DOCUMENT.toString(), specificContent);
								}

								message.setHeader(MBHeader.ARTIFACTTEMPLATEID_QNAME.toString(), artifactTemplateID);

								message.setHeader(MBHeader.NODETYPEID_QNAME.toString(), nodeTypeID);

								wasFound = true;

								// Invokable implementation artifact that
								// provides correct
								// interface/operation found. Stop loops.
								break searchIA;

							}
						}
					}
				}
			}

		} else if (relationshipTemplateID != null) {

			ManagementBusServiceImpl.LOG.debug("Getting information about the ImplementationArtifact from TOSCA Engine...");
			QName relationshipTypeID = ServiceHandler.toscaEngineService
					.getRelationshipTypeOfRelationshipTemplate(csarID, serviceTemplateID, relationshipTemplateID);

			ManagementBusServiceImpl.LOG.debug(
					"Getting RelationshipTypeImplementationIDs of RelationshipType: {} from CSAR: {}",
					relationshipTypeID, csarID);

			List<QName> relationshipTypeImplementationIDs = ServiceHandler.toscaEngineService
					.getRelationshipTypeImplementationsOfRelationshipType(csarID, relationshipTypeID);
			ManagementBusServiceImpl.LOG.debug("relationshipTypeImplementationIDs: {}",
					relationshipTypeImplementationIDs.toString());

			// Jump-Label to stop both loops at once
			searchIA: for (QName relationshipTypeImplementationID : relationshipTypeImplementationIDs) {

				List<String> implementationArtifactNames = ServiceHandler.toscaEngineService
						.getImplementationArtifactNamesOfRelationshipTypeImplementation(csarID,
								relationshipTypeImplementationID);
				ManagementBusServiceImpl.LOG.debug("implementationArtifactNames: {}",
						implementationArtifactNames.toString());

				for (String implementationArtifactName : implementationArtifactNames) {

					// Check if needed interface/operation is provided
					if (this.isCorrectIA(csarID, null, null, relationshipTypeID, relationshipTypeImplementationID,
							implementationArtifactName, neededOperation, neededInterface)) {

						QName artifactTemplateID = ServiceHandler.toscaEngineService
								.getArtifactTemplateOfAImplementationArtifactOfARelationshipTypeImplementation(csarID,
										relationshipTypeImplementationID, implementationArtifactName);
						ManagementBusServiceImpl.LOG.debug("artifactTemplateID: {}", artifactTemplateID.toString());

						String artifactType = ServiceHandler.toscaEngineService
								.getArtifactTypeOfAImplementationArtifactOfARelationshipTypeImplementation(csarID,
										relationshipTypeImplementationID, implementationArtifactName)
								.toString();

						invocationType = this.isSupported(artifactType, csarID, artifactTemplateID);

						if (invocationType != null) {
							ManagementBusServiceImpl.LOG.debug("InvocationType found: {} ", invocationType);

							ManagementBusServiceImpl.LOG.debug(
									"Getting Endpoint for ImplementationArtifact: {} from RelationshipTypeImplementation: {}",
									implementationArtifactName, relationshipTypeImplementationID);
							// EndpointService needs to be refactored.
							// Distinction of WSDL &
							// REST Endpoints is obsolete.
							WSDLEndpoint wsdlEndpoint = ServiceHandler.endpointService.getWSDLEndpointForIa(csarID,
									relationshipTypeImplementationID, implementationArtifactName);

							// Check if implementation artifact has a stored
							// endpoint and thus was deployed
							if (wsdlEndpoint != null) {

								URI endpoint = wsdlEndpoint.getURI();
								ManagementBusServiceImpl.LOG.debug("Endpoint: " + endpoint.toString());

								message.setHeader(MBHeader.ENDPOINT_URI.toString(), endpoint);

								boolean hasInputParams = ServiceHandler.toscaEngineService
										.hasOperationOfARelationshipTypeSpecifiedInputParams(csarID, relationshipTypeID,
												neededInterface, neededOperation);
								boolean hasOutputParams = ServiceHandler.toscaEngineService
										.hasOperationOfARelationshipTypeSpecifiedOutputParams(csarID,
												relationshipTypeID, neededInterface, neededOperation);

								if (hasInputParams && !hasOutputParams) {
									message.setHeader(MBHeader.HASOUTPUTPARAMS_BOOLEAN.toString(), false);
								} else {
									message.setHeader(MBHeader.HASOUTPUTPARAMS_BOOLEAN.toString(), true);
								}

								Document specificContent = ServiceHandler.toscaEngineService
										.getArtifactSpecificContentOfAImplementationArtifactOfARelationshipTypeImplementation(
												csarID, relationshipTypeImplementationID, implementationArtifactName);

								if (specificContent != null) {

									ManagementBusServiceImpl.LOG.debug("ArtifactSpecificContent specified!");
									message.setHeader(MBHeader.SPECIFICCONTENT_DOCUMENT.toString(), specificContent);
								}

								ManagementBusServiceImpl.LOG.debug("ArtifactSpecificContent specified!");
								message.setHeader(MBHeader.ARTIFACTTEMPLATEID_QNAME.toString(), artifactTemplateID);

								message.setHeader(MBHeader.RELATIONSHIPTYPEID_QNAME.toString(), relationshipTypeID);

								wasFound = true;

								// Invokable implementation artifact that
								// provides correct
								// interface/operation found. Stop loops.
								break searchIA;

							}
						}
					}
				}
			}
		}

		if (wasFound) {

			exchange = this.callMatchingPlugin(exchange, invocationType);

		} else {
			ManagementBusServiceImpl.LOG
					.warn("No invokable implementation artifact found that provides required interface/operation");
		}

		this.handleResponse(exchange);
	}

	@Override
	public void invokePlan(Exchange exchange) {

		ManagementBusServiceImpl.LOG.debug("Starting Management Bus: InvokePlan");

		Message message = exchange.getIn();

		CSARID csarID = message.getHeader(MBHeader.CSARID.toString(), CSARID.class);
		ManagementBusServiceImpl.LOG.debug("CSARID: " + csarID.toString());

		URI serviceInstanceID = message.getHeader(MBHeader.SERVICEINSTANCEID_URI.toString(), URI.class);
		ManagementBusServiceImpl.LOG.debug("csarInstanceID: {}", serviceInstanceID);

		QName planID = message.getHeader(MBHeader.PLANID_QNAME.toString(), QName.class);
		ManagementBusServiceImpl.LOG.debug("planID: {}", planID.toString());

		String nodeTemplateID = message.getHeader(MBHeader.NODETEMPLATEID_STRING.toString(), String.class);
		ManagementBusServiceImpl.LOG.debug("nodeTemplateID: {}", nodeTemplateID);

		ManagementBusServiceImpl.LOG.debug("Getting Endpoint for Plan {} from CSAR: {}", planID, csarID);
		WSDLEndpoint WSDLendpoint = ServiceHandler.endpointService.getWSDLEndpointForPlanId(csarID, planID);

		if (WSDLendpoint != null) {
			URI endpoint = WSDLendpoint.getURI();
			ManagementBusServiceImpl.LOG.debug("Endpoint for Plan {} : {} ", planID, endpoint);

			message.setHeader(MBHeader.ENDPOINT_URI.toString(), endpoint);
			// Assumption. Should be checked with ToscaEngine
			message.setHeader(MBHeader.HASOUTPUTPARAMS_BOOLEAN.toString(), true);

			exchange = this.callMatchingPlugin(exchange, "SOAP/HTTP");
		} else {
			ManagementBusServiceImpl.LOG.warn("No endpoint found for specified plan: {} of csar: {}. Invoking aborted!",
					planID, csarID);
		}

		this.handleResponse(exchange);
	}

	/**
	 * Calls the plug-in that supports the specific invocation-type.
	 * 
	 * @param exchange
	 *            to be given the plug-in.
	 * @param invokeType
	 *            that a plug-in is searched for.
	 * 
	 * @return the response of the called plug-in.
	 * 
	 */
	private Exchange callMatchingPlugin(Exchange exchange, String invokeType) {

		ManagementBusServiceImpl.LOG.debug("Searching a matching SI-Plug-in for InvocationType: {}...", invokeType);

		ManagementBusServiceImpl.LOG.debug("Available plug-ins: {}", ServiceHandler.pluginServices.toString());

		IManagementBusPluginService plugin;
		synchronized (ServiceHandler.pluginServices) {
			plugin = ServiceHandler.pluginServices.get(invokeType);
		}

		if (plugin != null) {
			ManagementBusServiceImpl.LOG.debug("Matching SI-Plug-in found: {}. Calling it.", plugin.toString());
			exchange = plugin.invoke(exchange);

		} else {
			ManagementBusServiceImpl.LOG.warn("No matching plugin found!");
		}
		return exchange;
	}

	/**
	 * Checks if the defined implementation artifact provides the needed
	 * interface/operation.
	 * 
	 * @param csarID
	 *            of the implementation artifact to check
	 * @param nodeTypeID
	 *            of the implementation artifact to check
	 * @param nodeTypeImplementationID
	 *            of the implementation artifact to check
	 * @param relationshipTypeID
	 *            of the implementation artifact to check
	 * @param relationshipTypeImplementationID
	 *            of the implementation artifact to check
	 * @param implementationArtifactName
	 *            of the implementation artifact to check
	 * @param neededOperation
	 *            specifies the operation the implementation artifact should
	 *            provide
	 * @param neededInterface
	 *            specifies the interface the implementation artifact should
	 *            provide
	 * 
	 * @return <code>true</code> if the specified implementation artifact
	 *         provides needed interface/operation. Otherwise <code>false</code>
	 *         .
	 */
	private boolean isCorrectIA(CSARID csarID, QName nodeTypeID, QName nodeTypeImplementationID,
			QName relationshipTypeID, QName relationshipTypeImplementationID, String implementationArtifactName,
			String neededOperation, String neededInterface) {

		String providedInterface = null;
		String providedOperation = null;

		if ((nodeTypeID != null) && (nodeTypeImplementationID != null)) {

			ManagementBusServiceImpl.LOG.debug("Checking if IA: {} of NodeTypeImpl: {} is the correct one.",
					implementationArtifactName, nodeTypeImplementationID);

			providedInterface = ServiceHandler.toscaEngineService
					.getInterfaceOfAImplementationArtifactOfANodeTypeImplementation(csarID, nodeTypeImplementationID,
							implementationArtifactName);

			providedOperation = ServiceHandler.toscaEngineService
					.getOperationOfAImplementationArtifactOfANodeTypeImplementation(csarID, nodeTypeImplementationID,
							implementationArtifactName);

		} else if ((relationshipTypeID != null) && (relationshipTypeImplementationID != null)) {

			ManagementBusServiceImpl.LOG.debug("Checking if IA: {} of RelationshipTypeImpl: {} is the correct one.",
					implementationArtifactName, relationshipTypeImplementationID);

			providedInterface = ServiceHandler.toscaEngineService
					.getInterfaceOfAImplementationArtifactOfARelationshipTypeImplementation(csarID,
							relationshipTypeImplementationID, implementationArtifactName);

			providedOperation = ServiceHandler.toscaEngineService
					.getOperationOfAImplementationArtifactOfARelationshipTypeImplementation(csarID,
							relationshipTypeImplementationID, implementationArtifactName);
		}

		ManagementBusServiceImpl.LOG.debug("Needed interface: {}. Provided interface: {}", neededInterface,
				providedInterface);
		ManagementBusServiceImpl.LOG.debug("Needed operation: {}. Provided operation: {}", neededOperation,
				providedOperation);

		// IA implements all operations of all interfaces defined in NodeType
		if ((providedInterface == null) && (providedOperation == null)) {
			ManagementBusServiceImpl.LOG.debug(
					"Correct IA found. IA: {} implements all operations of all interfaces defined in NodeType.",
					implementationArtifactName);
			return true;
		}

		// IA implements all operations of one interface defined in NodeType
		if ((providedInterface != null) && (providedOperation == null) && providedInterface.equals(neededInterface)) {
			ManagementBusServiceImpl.LOG.debug(
					"Correct IA found. IA: {} implements all operations of one interface defined in NodeType.",
					implementationArtifactName);
			return true;
		}

		// IA implements one operation of an interface defined in NodeType
		if ((providedInterface != null) && (providedOperation != null) && providedInterface.equals(neededInterface)
				&& providedOperation.equals(neededOperation)) {
			ManagementBusServiceImpl.LOG.debug(
					"Correct IA found. IA: {} implements one operation of an interface defined in NodeType.",
					implementationArtifactName);
			return true;
		}

		// In this case - if there is no interface specified - the operation
		// should be unique within the NodeType
		if ((neededInterface == null) && (neededOperation != null) && (providedInterface != null)
				&& (providedOperation == null)) {

			if (nodeTypeID != null) {
				return ServiceHandler.toscaEngineService.doesInterfaceOfNodeTypeContainOperation(csarID, nodeTypeID,
						providedInterface, neededOperation);
			}
			if (relationshipTypeID != null) {
				return ServiceHandler.toscaEngineService.doesInterfaceOfRelationshipTypeContainOperation(csarID,
						relationshipTypeID, providedInterface, neededOperation);
			}
		}

		ManagementBusServiceImpl.LOG.debug("ImplementationArtifact {} does not provide needed interface/operation",
				implementationArtifactName);
		return false;
	}

	/**
	 * Checks if a plugin is available that supports the specified artifact and
	 * returns the invocationType.
	 * 
	 * @param artifactType
	 *            to check if supported.
	 * @param csarID
	 *            to get properties to check for InvocationType.
	 * @param artifactTemplateID
	 *            to get properties to check for InvocationTyp.
	 * @return the invocationType or otherwise <tt>null</tt>.
	 */
	private String isSupported(String artifactType, CSARID csarID, QName artifactTemplateID) {

		ManagementBusServiceImpl.LOG.debug("Searching if a plugin supports the type {}", artifactType);

		ManagementBusServiceImpl.LOG.debug("All supported Types: {}", ServiceHandler.pluginServices.toString());

		// First check if a plugin is registered that supports the
		// ArtifactType.
		if (ServiceHandler.pluginServices.containsKey(artifactType)) {

			return artifactType;

		} else {

			Document properties = ServiceHandler.toscaEngineService.getPropertiesOfAArtifactTemplate(csarID,
					artifactTemplateID);

			// Second check if a invocation-type is specified in
			// TOSCA definition
			String invocationType = this.getInvocationType(properties);

			if (invocationType != null) {

				if (ServiceHandler.pluginServices.containsKey(invocationType)) {

					return invocationType;

				}
			}

		}
		return null;
	}

	/**
	 * Checks if a InvocationType was specified in the Tosca.xml and returns it
	 * if so.
	 * 
	 * @param properties
	 *            to check for InvocationType.
	 * @return InvocationType if specified. Otherwise <tt>null</tt>.
	 */
	private String getInvocationType(Document properties) {

		// Checks if there are specified properties at all.
		if (properties != null) {

			NodeList list = properties.getFirstChild().getChildNodes();

			for (int i = 0; i < list.getLength(); i++) {

				Node propNode = list.item(i);

				if (this.containsInvocationType(propNode)) {
					String invocationType = propNode.getTextContent().trim();
					return invocationType;
				}
			}
		}
		ManagementBusServiceImpl.LOG.debug("No InvocationType found!");
		return null;
	}

	/**
	 * Checks if the Node contains a InvocationType. A InvocationType has to be
	 * specified within <tt>{@literal <}namespace:InvocationType{@literal >}...
	 * {@literal <}/namespace:InvocationType{@literal >}</tt>.
	 * 
	 * @param currentNode
	 *            to check.
	 * @return if currentNode contains a InvocationType.
	 */
	private boolean containsInvocationType(Node currentNode) {
		String localName = currentNode.getLocalName();

		if (localName != null) {
			return localName.equals("InvocationType");
		}
		return false;
	}

	/**
	 * 
	 * Updates the input parameters. If instance data are available the provided
	 * input parameters will be overwritten with them.
	 * 
	 * @param inputParams
	 * @param csarID
	 * @param serviceTemplateID
	 * @param nodeTypeID
	 * @param nodeTemplateID
	 * @param neededInterface
	 * @param neededOperation
	 * @param serviceInstanceID
	 * 
	 * 
	 * @return the updated input parameters.
	 */
	private HashMap<String, String> updateInputParams(HashMap<String, String> inputParams, CSARID csarID,
			QName serviceTemplateID, QName nodeTypeID, String nodeTemplateID, String neededInterface,
			String neededOperation, URI serviceInstanceID) {

		ManagementBusServiceImpl.LOG.debug("{} inital input parameters for operation: {} found: {}", inputParams.size(),
				neededOperation, inputParams.toString());

		nodeTypeID = ServiceHandler.toscaEngineService.getNodeTypeOfNodeTemplate(csarID, serviceTemplateID,
				nodeTemplateID);

		List<String> expectedParams = getExpectedInputParams(csarID, nodeTypeID, neededInterface, neededOperation);

		ManagementBusServiceImpl.LOG.debug("Operation: {} expects {} parameters: {}", neededOperation, expectedParams.size(),
				expectedParams.toString());

		// Check if instanceID is set and merge input params with
		// instance data. Priority on instance data.
		if ((serviceInstanceID != null) && (!serviceInstanceID.toString().equals("?"))) {

			ManagementBusServiceImpl.LOG.debug("Getting InstanceData from InstanceDataService for ServiceInstanceID: {} ...",
					serviceInstanceID);

			String serviceTemplateName = ServiceHandler.toscaEngineService.getNameOfReference(csarID,
					serviceTemplateID);

			HashMap<String, String> propertiesMap = this.getInstanceDataProperties(csarID, serviceTemplateID,
					serviceTemplateName.trim(), nodeTemplateID.trim(), serviceInstanceID);

			if (propertiesMap != null) {
				ManagementBusServiceImpl.LOG.debug(
						"The stored properties from InstanceDataService for ServiceInstanceID: {} and NodeTemplateID: {} are: {}",
						serviceInstanceID, nodeTemplateID, propertiesMap.toString());

				for (String expectedParam : expectedParams) {
					if (propertiesMap.containsKey(expectedParam)) {
						inputParams.put(expectedParam, propertiesMap.get(expectedParam));
					}
				}

				ManagementBusServiceImpl.LOG.debug("Final {} input parameters for operation {} : {}", inputParams.size(),
						neededOperation, inputParams.toString());

			} else {
				ManagementBusServiceImpl.LOG.debug("No stored InstanceData found.");
			}
		} else {
			ManagementBusServiceImpl.LOG.debug("No InstanceDataID specified.");
		}

		return inputParams;
	}

	/**
	 * 
	 * Returns the input parameters that are specified in the TOSCA of the
	 * definied operation.
	 * 
	 * @param csarID
	 * @param nodeTypeID
	 * @param interfaceName
	 * @param operationName
	 * 
	 * 
	 * @return specified input parameters of the operation
	 */
	private List<String> getExpectedInputParams(CSARID csarID, QName nodeTypeID, String interfaceName,
			String operationName) {

		List<String> inputParams = new ArrayList<String>();

		if (ServiceHandler.toscaEngineService.hasOperationOfANodeTypeSpecifiedInputParams(csarID, nodeTypeID,
				interfaceName, operationName)) {

			Node definedInputParameters = ServiceHandler.toscaEngineService
					.getInputParametersOfANodeTypeOperation(csarID, nodeTypeID, interfaceName, operationName);

			if (definedInputParameters != null) {

				NodeList definedInputParameterList = definedInputParameters.getChildNodes();

				for (int i = 0; i < definedInputParameterList.getLength(); i++) {

					Node currentNode = definedInputParameterList.item(i);

					if (currentNode.getNodeType() == Node.ELEMENT_NODE) {

						String name = ((Element) currentNode).getAttribute("name");

						inputParams.add(name);

					}
				}
			}
		}
		return inputParams;
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
	private HashMap<String, String> getInstanceDataProperties(CSARID csarID, QName serviceTemplateID,
			String serviceTemplateName, String nodeTemplateID, URI serviceInstanceID) {

		HashMap<String, String> propertiesMap = new HashMap<String, String>();

		if (serviceInstanceID != null) {

			List<ServiceInstance> serviceInstanceList = ServiceHandler.instanceDataService
					.getServiceInstances(serviceInstanceID, serviceTemplateName, serviceTemplateID);

			QName nodeTemplateQName = new QName(serviceTemplateID.getNamespaceURI(), nodeTemplateID);

			for (ServiceInstance serviceInstance : serviceInstanceList) {

				if (serviceInstance.getCSAR_ID().toString().equals(csarID.toString())) {
					/**
					 * This is a workaround. The first statement should work,
					 * but unfortunately does not (the list is null / empty). We
					 * were not able to identify the root of the error, in debug
					 * mode it seemed to work but in "production" mode not.
					 * Somehow the lazy loading mechanism of JPA / EclipseLink
					 * seems to not work properly.
					 */
					// List<NodeInstance> nodeInstanceList =
					// serviceInstance.getNodeInstances();
					List<NodeInstance> nodeInstanceList = ServiceHandler.instanceDataService.getNodeInstances(null,
							null, null, serviceInstanceID);

					for (NodeInstance nodeInstance : nodeInstanceList) {

						if (nodeInstance.getNodeTemplateID().equals(nodeTemplateQName)) {

							Document doc = nodeInstance.getProperties();

							if (doc != null) {
								propertiesMap = this.docToMap(doc);
							}

							return propertiesMap;

						}
					}

				}

				ManagementBusServiceImpl.LOG.debug("No InstanceData found for CsarID: " + csarID + ", ServiceTemplateID: "
						+ serviceTemplateID + ", ServiceTemplateName: " + serviceTemplateName
						+ " and ServiceInstanceID: " + serviceInstanceID);
			}

		}
		return null;
	}

	/**
	 * Transfers the properties document to a map.
	 * 
	 * @param propertiesDocument
	 *            to be transfered to a map.
	 * @return transfered map.
	 */
	private HashMap<String, String> docToMap(Document propertiesDocument) {
		HashMap<String, String> reponseMap = new HashMap<String, String>();

		DocumentTraversal traversal = (DocumentTraversal) propertiesDocument;
		NodeIterator iterator = traversal.createNodeIterator(propertiesDocument.getDocumentElement(),
				NodeFilter.SHOW_ELEMENT, null, true);

		for (Node node = iterator.nextNode(); node != null; node = iterator.nextNode()) {

			String name = ((Element) node).getLocalName();
			StringBuilder content = new StringBuilder();
			NodeList children = node.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);
				if (child.getNodeType() == Node.TEXT_NODE) {
					content.append(child.getTextContent());
				}
			}

			if (!content.toString().trim().isEmpty()) {
				reponseMap.put(name, content.toString());
			}
		}

		return reponseMap;
	}

	/**
	 * Handles the response from the plug-in. If needed the response is sent
	 * back to the api.
	 * 
	 * 
	 * @param exchange
	 *            to handle.
	 */
	private void handleResponse(Exchange exchange) {

		if (exchange != null) {

			// Response message back to caller.
			ProducerTemplate template = Activator.camelContext.createProducerTemplate();

			String caller = exchange.getIn().getHeader(MBHeader.APIID_STRING.toString(), String.class);

			if (caller != null) {

				ManagementBusServiceImpl.LOG.debug("Sending response message back to api: {}", caller);

				exchange = template.send("direct-vm:" + caller, exchange);

				if (exchange.isFailed()) {
					ManagementBusServiceImpl.LOG.error("Sending exchange message failed! {}",
							exchange.getException().getMessage());
				}
			} else {
				ManagementBusServiceImpl.LOG.debug("Invocation was InOnly. No response message will be sent to the caller.");
			}
		}

	}

}
