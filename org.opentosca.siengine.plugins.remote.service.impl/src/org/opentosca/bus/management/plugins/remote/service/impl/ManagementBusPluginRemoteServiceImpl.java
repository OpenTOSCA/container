package org.opentosca.bus.management.plugins.remote.service.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.namespace.QName;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.io.FilenameUtils;
import org.opentosca.bus.management.model.header.MBHeader;
import org.opentosca.bus.management.plugins.remote.service.impl.servicehandler.ServiceHandler;
import org.opentosca.bus.management.plugins.remote.service.impl.typeshandler.ArtifactTypesHandler;
import org.opentosca.bus.management.plugins.service.IManagementBusPluginService;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.model.tosca.conventions.Interfaces;
import org.opentosca.model.tosca.conventions.Types;
import org.opentosca.settings.Settings;
import org.opentosca.toscaengine.service.ResolvedArtifacts;
import org.opentosca.toscaengine.service.ResolvedArtifacts.ResolvedDeploymentArtifact;
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
 * Management Bus-Plug-in for remoteIAs.<br>
 * <br>
 * 
 * 
 * 
 * The Plugin gets needed information from the ManagementBus and is responsible
 * to handle "remote IAs". Remote IAs are IAs such as scripts that needs to be
 * executed on the host machine. Therefore this plugin also is responsible for
 * the uploading of the files and the installation of required packages on the
 * target machine (if specified).
 * 
 * 
 * 
 * @author Michael Zimmermann - michael.zimmermann@iaas.uni-stuttgart.de
 * 
 * 
 */
public class ManagementBusPluginRemoteServiceImpl implements IManagementBusPluginService {

	final private static String PLACEHOLDER_TARGET_FILE_PATH = "{TARGET_FILE_PATH}";
	final private static String PLACEHOLDER_TARGET_FILE_FOLDER_PATH = "{TARGET_FILE_FOLDER_PATH}";
	final private static String PLACEHOLDER_TARGET_FILE_NAME_WITH_EXTENSION = "{TARGET_FILE_NAME_WITH_E}";
	final private static String PLACEHOLDER_TARGET_FILE_NAME_WITHOUT_EXTENSION = "{TARGET_FILE_NAME_WITHOUT_E}";
	final private static String PLACEHOLDER_DA_NAME_PATH_MAP = "{DA_NAME_PATH_MAP}";

	final private static Logger LOG = LoggerFactory.getLogger(ManagementBusPluginRemoteServiceImpl.class);

	@Override
	public Exchange invoke(Exchange exchange) {

		Message message = exchange.getIn();

		ManagementBusPluginRemoteServiceImpl.LOG.debug("Management Bus Remote Plugin getting information...");

		CSARID csarID = message.getHeader(MBHeader.CSARID.toString(), CSARID.class);
		ManagementBusPluginRemoteServiceImpl.LOG.debug("CsarID: {}", csarID);
		QName artifactTemplateID = message.getHeader(MBHeader.ARTIFACTTEMPLATEID_QNAME.toString(), QName.class);
		ManagementBusPluginRemoteServiceImpl.LOG.debug("ArtifactTemplateID: {}", artifactTemplateID);
		String nodeTemplateID = message.getHeader(MBHeader.NODETEMPLATEID_STRING.toString(), String.class);
		ManagementBusPluginRemoteServiceImpl.LOG.debug("NodeTemplateID: {}", nodeTemplateID);
		String relationshipTemplateID = message.getHeader(MBHeader.RELATIONSHIPTEMPLATEID_STRING.toString(),
				String.class);
		ManagementBusPluginRemoteServiceImpl.LOG.debug("RelationshipTemplateID: {}", relationshipTemplateID);
		QName serviceTemplateID = message.getHeader(MBHeader.SERVICETEMPLATEID_QNAME.toString(), QName.class);
		ManagementBusPluginRemoteServiceImpl.LOG.debug("ServiceTemplateID: {}", serviceTemplateID);
		QName nodeTypeID = message.getHeader(MBHeader.NODETYPEID_QNAME.toString(), QName.class);
		ManagementBusPluginRemoteServiceImpl.LOG.debug("NodeTypeID: {}", nodeTypeID);
		QName relationshipTypeID = message.getHeader(MBHeader.RELATIONSHIPTYPEID_QNAME.toString(), QName.class);
		ManagementBusPluginRemoteServiceImpl.LOG.debug("RelationshipTypeID: {}", relationshipTypeID);
		String interfaceName = message.getHeader(MBHeader.INTERFACENAME_STRING.toString(), String.class);
		ManagementBusPluginRemoteServiceImpl.LOG.debug("InterfaceName: {}", interfaceName);
		String operationName = message.getHeader(MBHeader.OPERATIONNAME_STRING.toString(), String.class);
		ManagementBusPluginRemoteServiceImpl.LOG.debug("OperationName: {}", operationName);
		URI serviceInstanceID = message.getHeader(MBHeader.SERVICEINSTANCEID_URI.toString(), URI.class);
		ManagementBusPluginRemoteServiceImpl.LOG.debug("ServiceInstanceID: {}", serviceInstanceID);
		String nodeInstanceID = message.getHeader(MBHeader.NODEINSTANCEID_STRING.toString(), String.class);
		ManagementBusPluginRemoteServiceImpl.LOG.debug("NodeInstanceID: {}", nodeInstanceID);

		if (nodeTemplateID == null && relationshipTemplateID != null) {

			boolean isBoundToSourceNode = ServiceHandler.toscaEngineService.isOperationOfRelationshipBoundToSourceNode(
					csarID, relationshipTypeID, interfaceName, operationName);

			if (isBoundToSourceNode) {
				nodeTemplateID = ServiceHandler.toscaEngineService.getSourceNodeTemplateIDOfRelationshipTemplate(csarID,
						serviceTemplateID, relationshipTemplateID);
			} else {
				nodeTemplateID = ServiceHandler.toscaEngineService.getTargetNodeTemplateIDOfRelationshipTemplate(csarID,
						serviceTemplateID, relationshipTemplateID);
			}
		}

		QName artifactType = ServiceHandler.toscaEngineService.getArtifactTypeOfArtifactTemplate(csarID,
				artifactTemplateID);

		if (artifactType != null && nodeTemplateID != null) {

			// search operating system ia to upload files and run scripts on
			// target
			// machine
			String osNodeTemplateID = getOperatingSystemNodeTemplateID(csarID, serviceTemplateID, nodeTemplateID);

			if (osNodeTemplateID != null) {
				QName osNodeTypeID = ServiceHandler.toscaEngineService.getNodeTypeOfNodeTemplate(csarID,
						serviceTemplateID, osNodeTemplateID);

				if (osNodeTypeID != null) {
					ManagementBusPluginRemoteServiceImpl.LOG.debug("OperatingSystem-NodeType found: {}", osNodeTypeID);
					String osIAName = getOperatingSystemIA(csarID, serviceTemplateID, osNodeTemplateID);

					if (osIAName != null) {

						// create headers
						HashMap<String, Object> headers = new HashMap<>();

						headers.put(MBHeader.CSARID.toString(), csarID);
						headers.put(MBHeader.SERVICETEMPLATEID_QNAME.toString(), serviceTemplateID);
						headers.put(MBHeader.NODETEMPLATEID_STRING.toString(), osNodeTemplateID);
						headers.put(MBHeader.INTERFACENAME_STRING.toString(),
								Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM);
						headers.put(MBHeader.SERVICEINSTANCEID_URI.toString(), serviceInstanceID);

						// install packages
						ManagementBusPluginRemoteServiceImpl.LOG.debug("Installing packages...");

						installPackages(artifactType, headers);

						ManagementBusPluginRemoteServiceImpl.LOG.debug("Packages isntalled.");

						// upload files
						ManagementBusPluginRemoteServiceImpl.LOG.debug("Uploading files...");

						List<String> artifactReferences = ServiceHandler.toscaEngineService
								.getArtifactReferenceWithinArtifactTemplate(csarID, artifactTemplateID);

						String fileSource;
						String targetFilePath = null;
						String targetFileFolderPath = null;

						for (String artifactRef : artifactReferences) {

							fileSource = Settings.CONTAINER_API + "/CSARs/" + csarID.getFileName() + "/Content/"
									+ artifactRef;

							targetFilePath = "~/" + csarID.getFileName() + "/" + artifactRef;

							targetFileFolderPath = FilenameUtils.getFullPathNoEndSeparator(targetFilePath);

							String createDirCommand = "sleep 5 && mkdir -p " + targetFileFolderPath;

							// create directory before uploading file
							runScript(createDirCommand, headers);

							// upload file
							transferFile(csarID, artifactTemplateID, fileSource, targetFilePath, headers);
						}

						ManagementBusPluginRemoteServiceImpl.LOG.debug("Files uploaded.");

						// run script
						ManagementBusPluginRemoteServiceImpl.LOG.debug("Running scripts...");

						String fileNameWithE = FilenameUtils.getName(targetFilePath);
						String fileNameWithoutE = FilenameUtils.getBaseName(targetFilePath);

						String artifactTypeSpecificCommand = createArtifcatTypeSpecificCommandString(csarID,
								artifactType, artifactTemplateID, message.getBody());

						ManagementBusPluginRemoteServiceImpl.LOG.debug("Replacing further generic placeholder...");

						// replace placeholders
						artifactTypeSpecificCommand = artifactTypeSpecificCommand.replace(PLACEHOLDER_TARGET_FILE_PATH,
								targetFilePath);
						artifactTypeSpecificCommand = artifactTypeSpecificCommand
								.replace(PLACEHOLDER_TARGET_FILE_FOLDER_PATH, targetFileFolderPath);
						artifactTypeSpecificCommand = artifactTypeSpecificCommand
								.replace(PLACEHOLDER_TARGET_FILE_NAME_WITH_EXTENSION, fileNameWithE);
						artifactTypeSpecificCommand = artifactTypeSpecificCommand
								.replace(PLACEHOLDER_TARGET_FILE_NAME_WITHOUT_EXTENSION, fileNameWithoutE);
						artifactTypeSpecificCommand = artifactTypeSpecificCommand.replace(PLACEHOLDER_DA_NAME_PATH_MAP,
								"sudo -E " + createDANamePathMapEnvVar(csarID, serviceTemplateID, nodeTypeID,
										nodeTemplateID));

						ManagementBusPluginRemoteServiceImpl.LOG.debug("Final command for ArtifactType {} : {}",
								artifactType, artifactTypeSpecificCommand);

						runScript(artifactTypeSpecificCommand, headers);

						ManagementBusPluginRemoteServiceImpl.LOG.debug("Scripts finished.");

					} else {
						ManagementBusPluginRemoteServiceImpl.LOG.warn("No OperatingSystem-IA found!");
					}
				} else {
					ManagementBusPluginRemoteServiceImpl.LOG.warn("No OperatingSystem-NodeType found!");
				}
			} else {
				ManagementBusPluginRemoteServiceImpl.LOG.warn("No OperatingSystem-NodeTemplate found!");
			}
		} else {
			ManagementBusPluginRemoteServiceImpl.LOG.warn("Could not determine ArtifactType of ArtifactTemplate: {}!",
					artifactTemplateID);
		}
		return exchange;
	}

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
	private String getOperatingSystemNodeTemplateID(CSARID csarID, QName serviceTemplateID, String nodeTemplateID) {

		ManagementBusPluginRemoteServiceImpl.LOG.debug(
				"Searching the OperatingSystemNode of NodeTemplate: {}, ServiceTemplate: {} & CSAR: {} ...",
				nodeTemplateID, serviceTemplateID, csarID);

		QName nodeType = ServiceHandler.toscaEngineService.getNodeTypeOfNodeTemplate(csarID, serviceTemplateID,
				nodeTemplateID);

		while (!isOperatingSystemNodeType(csarID, nodeType) && (nodeTemplateID != null)) {

			ManagementBusPluginRemoteServiceImpl.LOG.debug("{} isn't the OperatingSystemNode.", nodeTemplateID);
			ManagementBusPluginRemoteServiceImpl.LOG
					.debug("Getting the underneath Node for checking if it is the OperatingSystemNode...");

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
				ManagementBusPluginRemoteServiceImpl.LOG
						.debug("Checking if the underneath Node: {} is the OperatingSystemNode.", nodeTemplateID);
				nodeType = ServiceHandler.toscaEngineService.getNodeTypeOfNodeTemplate(csarID, serviceTemplateID,
						nodeTemplateID);

			} else {
				ManagementBusPluginRemoteServiceImpl.LOG.debug("No underneath Node found.");
			}
		}

		if (nodeTemplateID != null) {
			ManagementBusPluginRemoteServiceImpl.LOG.debug("OperatingSystemNode found: {}", nodeTemplateID);
		}

		return nodeTemplateID;
	}

	/**
	 * 
	 * Checks if the specified NodeType is the OperatingSystem NodeType.
	 * 
	 * @param csarID
	 * @param nodeType
	 * @return true if the specified NodeType is the OperatingSystem NodeType.
	 *         Otherwise false.
	 */
	private boolean isOperatingSystemNodeType(CSARID csarID, QName nodeType) {

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
		}

		return false;
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
	private static String getOperatingSystemIA(CSARID csarID, QName serviceTemplateID, String osNodeTemplateID) {

		ManagementBusPluginRemoteServiceImpl.LOG.debug(
				"Searching the OperatingSystem-IA of NodeTemplate: {}, ServiceTemplate: {} & CSAR: {} ...",
				osNodeTemplateID, serviceTemplateID, csarID);

		QName osNodeType = ServiceHandler.toscaEngineService.getNodeTypeOfNodeTemplate(csarID, serviceTemplateID,
				osNodeTemplateID);

		List<QName> osNodeTypeImpls = ServiceHandler.toscaEngineService.getNodeTypeImplementationsOfNodeType(csarID,
				osNodeType);

		for (QName osNodeTypeImpl : osNodeTypeImpls) {

			ManagementBusPluginRemoteServiceImpl.LOG.debug("NodeTypeImpl: {} ", osNodeTypeImpl);

			List<String> osIANames = ServiceHandler.toscaEngineService
					.getImplementationArtifactNamesOfNodeTypeImplementation(csarID, osNodeTypeImpl);

			for (String osIAName : osIANames) {

				ManagementBusPluginRemoteServiceImpl.LOG.debug("IA: {} ", osIAName);

				String osIAInterface = ServiceHandler.toscaEngineService
						.getInterfaceOfAImplementationArtifactOfANodeTypeImplementation(csarID, osNodeTypeImpl,
								osIAName);

				ManagementBusPluginRemoteServiceImpl.LOG.debug("Interface: {} ", osIAInterface);

				if (osIAInterface.equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM)) {

					return osIAName;

				}
			}
		}
		return null;
	}

	/**
	 * @param csarID
	 * @param serviceTemplateID
	 * @param nodeTypeID
	 * @param nodeTemplateID
	 * 
	 * @return mapping with DeploymentArtifact names and their paths.
	 */
	private String createDANamePathMapEnvVar(CSARID csarID, QName serviceTemplateID, QName nodeTypeID,
			String nodeTemplateID) {

		ManagementBusPluginRemoteServiceImpl.LOG.debug("Checking if NodeTemplate {} has DAs...", nodeTemplateID);

		HashMap<String, String> daNameReferenceMapping = new HashMap<>();

		QName nodeTemplateQName = new QName(serviceTemplateID.getNamespaceURI(), nodeTemplateID);

		ResolvedArtifacts resolvedArtifacts = ServiceHandler.toscaEngineService
				.getResolvedArtifactsOfNodeTemplate(csarID, nodeTemplateQName);

		List<ResolvedDeploymentArtifact> resolvedDAs = resolvedArtifacts.getDeploymentArtifacts();

		List<String> daArtifactReferences;

		for (ResolvedDeploymentArtifact resolvedDA : resolvedDAs) {

			daArtifactReferences = resolvedDA.getReferences();

			for (String daArtifactReference : daArtifactReferences) {
				daNameReferenceMapping.put(resolvedDA.getName(), daArtifactReference);
			}
		}

		List<QName> nodeTypeImpls = ServiceHandler.toscaEngineService.getNodeTypeImplementationsOfNodeType(csarID,
				nodeTypeID);

		for (QName nodeTypeImpl : nodeTypeImpls) {
			List<String> daNames = ServiceHandler.toscaEngineService
					.getDeploymentArtifactNamesOfNodeTypeImplementation(csarID, nodeTypeImpl);

			for (String daName : daNames) {
				QName daArtifactTemplate = ServiceHandler.toscaEngineService
						.getArtifactTemplateOfADeploymentArtifactOfANodeTypeImplementation(csarID, nodeTypeImpl,
								daName);

				daArtifactReferences = ServiceHandler.toscaEngineService
						.getArtifactReferenceWithinArtifactTemplate(csarID, daArtifactTemplate);

				for (String daArtifactReference : daArtifactReferences) {
					daNameReferenceMapping.put(daName, daArtifactReference);
				}
			}
		}

		String daEnvMap = "";
		if (!daNameReferenceMapping.isEmpty()) {

			ManagementBusPluginRemoteServiceImpl.LOG.debug("NodeTemplate {} has {} DAs.", nodeTemplateID,
					daNameReferenceMapping.size());

			daEnvMap += "DAs=\"";
			for (Entry<String, String> da : daNameReferenceMapping.entrySet()) {

				String daName = da.getKey();
				String daRef = da.getValue();

				// FIXME / is a brutal assumption
				if (!daRef.startsWith("/")) {
					daRef = "/" + daRef;
				}

				daEnvMap += daName + "," + daRef + ";";
			}
			daEnvMap += "\" ";

			ManagementBusPluginRemoteServiceImpl.LOG.debug("Created DA-DANamePathMapEnvVar for NodeTemplate {} : {}",
					nodeTemplateID, daEnvMap);
		}

		return daEnvMap;
	}

	/**
	 * 
	 * Installs required and specified packages of the specified ArtifactType.
	 * Required packages are in defined the corresponding *.xml file.
	 * 
	 * @param artifactType
	 * @param headers
	 */
	private void installPackages(QName artifactType, HashMap<String, Object> headers) {

		List<String> requiredPackages = ArtifactTypesHandler.getRequiredPackages(artifactType);

		String requiredPackagesString = "";

		if (!requiredPackages.isEmpty()) {

			HashMap<String, String> inputParamsMap = new HashMap<>();

			for (String requiredPackage : requiredPackages) {
				requiredPackagesString += requiredPackage;
				requiredPackagesString += " ";
			}
			inputParamsMap.put(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_PARAMETER_PACKAGENAMES,
					requiredPackagesString);

			ManagementBusPluginRemoteServiceImpl.LOG.debug("Installing packages: {} for ArtifactType: {} ",
					requiredPackages, artifactType);

			headers.put(MBHeader.OPERATIONNAME_STRING.toString(),
					Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_INSTALLPACKAGE);

			invokeManagementBusEngine(inputParamsMap, headers);
		} else {
			ManagementBusPluginRemoteServiceImpl.LOG.debug("ArtifactType: {} needs no packages to install.",
					requiredPackages, artifactType);
		}
	}

	/**
	 * 
	 * For transferring files to the target machine.
	 * 
	 * @param csarID
	 * @param artifactTemplate
	 * @param source
	 * @param target
	 * @param headers
	 */
	private void transferFile(CSARID csarID, QName artifactTemplate, String source, String target,
			HashMap<String, Object> headers) {

		HashMap<String, String> inputParamsMap = new HashMap<>();

		inputParamsMap.put(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_PARAMETER_TARGETABSOLUTPATH,
				target);
		inputParamsMap.put(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_PARAMETER_SOURCEURLORLOCALPATH,
				source);

		ManagementBusPluginRemoteServiceImpl.LOG.debug("Uploading file. Source: {} Target: {} ", source, target);

		headers.put(MBHeader.OPERATIONNAME_STRING.toString(),
				Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_TRANSFERFILE);

		invokeManagementBusEngine(inputParamsMap, headers);

	}

	/**
	 * 
	 * For running scripts on the target machine. Commands to be executed are
	 * defined in the corresponding *.xml file.
	 * 
	 * @param commandsString
	 * @param headers
	 */
	private void runScript(String commandsString, HashMap<String, Object> headers) {

		HashMap<String, String> inputParamsMap = new HashMap<>();

		inputParamsMap.put(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_PARAMETER_SCRIPT, commandsString);

		ManagementBusPluginRemoteServiceImpl.LOG.debug("RunScript: {} ", commandsString);

		headers.put(MBHeader.OPERATIONNAME_STRING.toString(),
				Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_RUNSCRIPT);

		invokeManagementBusEngine(inputParamsMap, headers);
	}

	/**
	 * 
	 * Creates ArtifactType specific commands that should be executed on the
	 * target machine. Commands to be executed are defined in the corresponding
	 * *.xml file.
	 * 
	 * @param csarID
	 * @param artifactType
	 * @param artifactTemplateID
	 * @param params
	 * 
	 * @return the created command
	 */
	@SuppressWarnings("unchecked")
	private String createArtifcatTypeSpecificCommandString(CSARID csarID, QName artifactType, QName artifactTemplateID,
			Object params) {

		ManagementBusPluginRemoteServiceImpl.LOG.debug("Creating ArtifcatType specific command...");

		String commandsString = "";

		List<String> commands = ArtifactTypesHandler.getCommands(artifactType);

		for (String command : commands) {
			commandsString += command;
			commandsString += " && ";
		}

		if (commandsString.endsWith(" && ")) {
			commandsString = commandsString.substring(0, commandsString.length() - 4);
		}

		ManagementBusPluginRemoteServiceImpl.LOG.debug("Defined generic command for ArtifactType {} : {} ",
				artifactType, commandsString);

		// replace placeholder with data from inputParams and instance data

		if (commandsString.contains("{{") && commandsString.contains("}}")) {

			ManagementBusPluginRemoteServiceImpl.LOG.debug(
					"Replacing the placeholder of the generic command with properties data and/or provided input parameter...");

			HashMap<String, String> paramsMap = new HashMap<>();

			if (params instanceof HashMap) {
				paramsMap = (HashMap<String, String>) params;
			} else if (params instanceof Document) {
				Document paramsDoc = (Document) params;
				paramsMap = docToMap(paramsDoc);
			}

			Document propDoc = ServiceHandler.toscaEngineService.getPropertiesOfAArtifactTemplate(csarID,
					artifactTemplateID);

			if (propDoc != null) {

				paramsMap.putAll(docToMap(propDoc));

				for (Entry<String, String> prop : paramsMap.entrySet()) {
					commandsString = commandsString.replace("{{" + prop.getKey() + "}}",
							FilenameUtils.separatorsToUnix(prop.getValue()));
				}
			}

			ManagementBusPluginRemoteServiceImpl.LOG.debug("Generic command with replaced placeholder: {}",
					commandsString);

		}

		return commandsString;
	}

	/**
	 * 
	 * Invokes the Management Bus.
	 * 
	 * @param paramsMap
	 * @param headers
	 */
	private void invokeManagementBusEngine(HashMap<String, String> paramsMap, HashMap<String, Object> headers) {

		ManagementBusPluginRemoteServiceImpl.LOG.debug("Invoking the Management Bus...");

		ProducerTemplate template = Activator.camelContext.createProducerTemplate();

		String response = template.requestBodyAndHeaders(
				"bean:org.opentosca.bus.management.service.IManagementBusService?method=invokeIA", paramsMap, headers,
				String.class);

		ManagementBusPluginRemoteServiceImpl.LOG.debug("Invocation finished: {}", response);

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

	@Override
	public List<String> getSupportedTypes() {

		List<String> supportedTypes = new ArrayList<String>();

		List<QName> supportedTypesQName = ArtifactTypesHandler.getSupportedTypes();

		for (QName supportedTypeQName : supportedTypesQName) {
			supportedTypes.add(supportedTypeQName.toString());
		}

		return supportedTypes;
	}

}
