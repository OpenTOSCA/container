package org.opentosca.siengine.plugins.remote.service.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.namespace.QName;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.model.tosca.conventions.Interfaces;
import org.opentosca.model.tosca.conventions.Types;
import org.opentosca.settings.Settings;
import org.opentosca.siengine.model.header.SIHeader;
import org.opentosca.siengine.plugins.remote.service.impl.servicehandler.ServiceHandler;
import org.opentosca.siengine.plugins.remote.service.impl.util.ArtifactTypesManager;
import org.opentosca.siengine.plugins.service.ISIEnginePluginService;
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
 * SIEngine-Plug-in for remoteIAs.<br>
 * <br>
 * 
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * The Plug-in gets needed information from the SI-Engine and is responsible to
 * handle "remote IAs". Remote IAs are IAs such as scripts that needs to be
 * executed on the host machine. Therefore this plugin also is responsible for
 * the uploading of the files and the installation of required packagers on the
 * target machine (if specified).
 * 
 * 
 * 
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 * 
 * 
 * 
 */
public class SIEnginePluginRemoteServiceImpl implements ISIEnginePluginService {

	final private static String PLACEHOLDER_TARGET_PATH_FOLDER = "{TARGET_PATH_FOLDER}";
	final private static String PLACEHOLDER_FILE_NAME_WITH_EXTENSION = "{FILE_NAME_WITH_E}";
	final private static String PLACEHOLDER_FILE_NAME_WITHOUT_EXTENSION = "{FILE_NAME_WITHOUT_E}";

	final private static Logger LOG = LoggerFactory.getLogger(SIEnginePluginRemoteServiceImpl.class);

	@Override
	public Exchange invoke(Exchange exchange) {

		Message message = exchange.getIn();

		// Object params = message.getBody();

		SIEnginePluginRemoteServiceImpl.LOG.debug("SIEngine Remote Plugin getting information...");

		CSARID csarID = message.getHeader(SIHeader.CSARID.toString(), CSARID.class);
		SIEnginePluginRemoteServiceImpl.LOG.debug("CsarID: {}", csarID);
		QName artifactTemplateID = message.getHeader(SIHeader.ARTIFACTTEMPLATEID_QNAME.toString(), QName.class);
		SIEnginePluginRemoteServiceImpl.LOG.debug("ArtifactTemplateID: {}", artifactTemplateID);
		String nodeTemplateID = message.getHeader(SIHeader.NODETEMPLATEID_STRING.toString(), String.class);
		SIEnginePluginRemoteServiceImpl.LOG.debug("NodeTemplateID: {}", nodeTemplateID);
		String relationshipTemplateID = message.getHeader(SIHeader.RELATIONSHIPTEMPLATEID_STRING.toString(),
				String.class);
		SIEnginePluginRemoteServiceImpl.LOG.debug("RelationshipTemplateID: {}", relationshipTemplateID);
		QName serviceTemplateID = message.getHeader(SIHeader.SERVICETEMPLATEID_QNAME.toString(), QName.class);
		SIEnginePluginRemoteServiceImpl.LOG.debug("ServiceTemplateID: {}", serviceTemplateID);
		QName nodeTypeID = message.getHeader(SIHeader.NODETYPEID_QNAME.toString(), QName.class);
		SIEnginePluginRemoteServiceImpl.LOG.debug("NodeTypeID: {}", nodeTypeID);
		QName relationshipTypeID = message.getHeader(SIHeader.RELATIONSHIPTYPEID_QNAME.toString(), QName.class);
		SIEnginePluginRemoteServiceImpl.LOG.debug("RelationshipTypeID: {}", relationshipTypeID);
		String interfaceName = message.getHeader(SIHeader.INTERFACENAME_STRING.toString(), String.class);
		SIEnginePluginRemoteServiceImpl.LOG.debug("InterfaceName: {}", interfaceName);
		String operationName = message.getHeader(SIHeader.OPERATIONNAME_STRING.toString(), String.class);
		SIEnginePluginRemoteServiceImpl.LOG.debug("OperationName: {}", operationName);
		URI serviceInstanceID = message.getHeader(SIHeader.SERVICEINSTANCEID_URI.toString(), URI.class);
		SIEnginePluginRemoteServiceImpl.LOG.debug("ServiceInstanceID: {}", serviceInstanceID);
		String nodeInstanceID = message.getHeader(SIHeader.NODEINSTANCEID_STRING.toString(), String.class);
		SIEnginePluginRemoteServiceImpl.LOG.debug("NodeInstanceID: {}", nodeInstanceID);

		QName artifactType = ServiceHandler.toscaEngineService.getArtifactTypeOfArtifactTemplate(csarID,
				artifactTemplateID);

		if (artifactType != null) {

			// search operating system ia to upload files and run scripts on
			// target
			// machine
			String osNodeTemplateID = getOperatingSystemNodeTemplateID(csarID, serviceTemplateID, nodeTemplateID);

			if (osNodeTemplateID != null) {
				QName osNodeTypeID = ServiceHandler.toscaEngineService.getNodeTypeOfNodeTemplate(csarID,
						serviceTemplateID, osNodeTemplateID);

				if (osNodeTypeID != null) {
					SIEnginePluginRemoteServiceImpl.LOG.debug("OperatingSystem-NodeType found: {}", osNodeTypeID);
					String osIAName = getOperatingSystemIA(csarID, serviceTemplateID, osNodeTemplateID);

					if (osIAName != null) {

						// create headers
						HashMap<String, Object> headers = new HashMap<>();

						headers.put(SIHeader.CSARID.toString(), csarID);
						headers.put(SIHeader.SERVICETEMPLATEID_QNAME.toString(), serviceTemplateID);
						headers.put(SIHeader.NODETEMPLATEID_STRING.toString(), osNodeTemplateID);
						headers.put(SIHeader.INTERFACENAME_STRING.toString(),
								Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM);
						headers.put(SIHeader.SERVICEINSTANCEID_URI.toString(), serviceInstanceID);

						// install packages
						SIEnginePluginRemoteServiceImpl.LOG.debug("Installing packages...");

						installPackages(artifactType, headers);

						SIEnginePluginRemoteServiceImpl.LOG.debug("Packages isntalled.");

						// upload files
						SIEnginePluginRemoteServiceImpl.LOG.debug("Uploading files...");

						List<String> artifactReferences = ServiceHandler.toscaEngineService
								.getArtifactReferenceWithinArtifactTemplate(csarID, artifactTemplateID);

						String source;
						String target = null;
						String targetFolder = null;

						for (String artifactRef : artifactReferences) {
							source = Settings.CONTAINER_API + "/CSARs/" + csarID.getFileName() + "/Content/"
									+ artifactRef;

							target = "~/" + csarID.getFileName() + "/" + artifactRef;

							targetFolder = target.substring(0, target.lastIndexOf("/"));

							// create directory before uploading file
							runScript("sleep 5 && mkdir -p " + targetFolder, headers);

							// upload file
							transferFile(csarID, artifactTemplateID, source, target, headers);
						}

						SIEnginePluginRemoteServiceImpl.LOG.debug("Files uploaded.");

						// run script
						SIEnginePluginRemoteServiceImpl.LOG.debug("Running scripts...");

						String targetFileWithE = target.substring(target.lastIndexOf("/"));
						String targetFileWithoutE = target.substring(target.lastIndexOf("/"), target.lastIndexOf("."));

						String artifactTypeSpecificCommand = createArtifcatTypeSpecificCommandString(csarID,
								artifactTemplateID, artifactType);

						// replace placeholders
						artifactTypeSpecificCommand.replace(PLACEHOLDER_TARGET_PATH_FOLDER, targetFolder);
						artifactTypeSpecificCommand.replace(PLACEHOLDER_FILE_NAME_WITH_EXTENSION, targetFileWithE);
						artifactTypeSpecificCommand.replace(PLACEHOLDER_FILE_NAME_WITHOUT_EXTENSION,
								targetFileWithoutE);

						runScript(artifactTypeSpecificCommand, headers);

						SIEnginePluginRemoteServiceImpl.LOG.debug("Scripts finished.");

					} else {
						SIEnginePluginRemoteServiceImpl.LOG.warn("No OperatingSystem-IA found!");
					}
				} else {
					SIEnginePluginRemoteServiceImpl.LOG.warn("No OperatingSystem-NodeType found!");
				}
			} else {
				SIEnginePluginRemoteServiceImpl.LOG.warn("No OperatingSystem-NodeTemplate found!");
			}
		} else {
			SIEnginePluginRemoteServiceImpl.LOG.warn("Could not determine ArtifactType of ArtifactTemplate: {}!",
					artifactTemplateID);
		}
		return exchange;
	}

	private String getOperatingSystemNodeTemplateID(CSARID csarID, QName serviceTemplateID, String nodeTemplateID) {

		SIEnginePluginRemoteServiceImpl.LOG.debug(
				"Searching the OperatingSystemNode of NodeTemplate: {}, ServiceTemplate: {} & CSAR: {} ...",
				nodeTemplateID, serviceTemplateID, csarID);

		QName nodeType = ServiceHandler.toscaEngineService.getNodeTypeOfNodeTemplate(csarID, serviceTemplateID,
				nodeTemplateID);

		while (!isOperatingSystemNodeType(csarID, nodeType) && (nodeTemplateID != null)) {

			SIEnginePluginRemoteServiceImpl.LOG.debug("{} isn't the OperatingSystemNode.", nodeTemplateID);
			SIEnginePluginRemoteServiceImpl.LOG
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
				SIEnginePluginRemoteServiceImpl.LOG
						.debug("Checking if the underneath Node: {} is the OperatingSystemNode.", nodeTemplateID);
				nodeType = ServiceHandler.toscaEngineService.getNodeTypeOfNodeTemplate(csarID, serviceTemplateID,
						nodeTemplateID);

			} else {
				SIEnginePluginRemoteServiceImpl.LOG.debug("No underneath Node found.");
			}
		}

		if (nodeTemplateID != null) {
			SIEnginePluginRemoteServiceImpl.LOG.debug("OperatingSystemNode found: {}", nodeTemplateID);
		}

		return nodeTemplateID;
	}

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

	private static String getOperatingSystemIA(CSARID csarID, QName serviceTemplateID, String osNodeTemplateID) {

		SIEnginePluginRemoteServiceImpl.LOG.debug(
				"Searching the OperatingSystemm-IA of NodeTemplate: {}, ServiceTemplate: {} & CSAR: {} ...",
				osNodeTemplateID, serviceTemplateID, csarID);

		QName osNodeType = ServiceHandler.toscaEngineService.getNodeTypeOfNodeTemplate(csarID, serviceTemplateID,
				osNodeTemplateID);

		List<QName> osNodeTypeImpls = ServiceHandler.toscaEngineService.getNodeTypeImplementationsOfNodeType(csarID,
				osNodeType);

		for (QName osNodeTypeImpl : osNodeTypeImpls) {

			SIEnginePluginRemoteServiceImpl.LOG.debug("NodeTypeImpl: {} ", osNodeTypeImpl);

			List<String> osIANames = ServiceHandler.toscaEngineService
					.getImplementationArtifactNamesOfNodeTypeImplementation(csarID, osNodeTypeImpl);

			for (String osIAName : osIANames) {

				SIEnginePluginRemoteServiceImpl.LOG.debug("IA: {} ", osIAName);

				String osIAInterface = ServiceHandler.toscaEngineService
						.getInterfaceOfAImplementationArtifactOfANodeTypeImplementation(csarID, osNodeTypeImpl,
								osIAName);

				SIEnginePluginRemoteServiceImpl.LOG.debug("Interface: {} ", osIAInterface);

				if (osIAInterface.equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM)) {

					return osIAName;

				}
			}
		}
		return null;
	}

	private void installPackages(QName artifactType, HashMap<String, Object> headers) {

		HashMap<String, String> inputParamsMap = new HashMap<>();

		String requiredPackagesString = "";
		List<String> requiredPackages = ArtifactTypesManager.getRequiredPackages(artifactType);
		for (String requiredPackage : requiredPackages) {
			requiredPackagesString += requiredPackage;
			requiredPackagesString += " ";
		}
		inputParamsMap.put(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_PARAMETER_PACKAGENAMES,
				requiredPackagesString);

		SIEnginePluginRemoteServiceImpl.LOG.debug("Installing packages: {} for ArtifactType: {} ", requiredPackages,
				artifactType);

		headers.put(SIHeader.OPERATIONNAME_STRING.toString(),
				Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_INSTALLPACKAGE);

		invokeManagementBusEngine(inputParamsMap, headers);
	}

	private void transferFile(CSARID csarID, QName artifactTemplate, String source, String target,
			HashMap<String, Object> headers) {

		HashMap<String, String> inputParamsMap = new HashMap<>();

		inputParamsMap.put(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_PARAMETER_TARGETABSOLUTPATH,
				target);
		inputParamsMap.put(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_PARAMETER_SOURCEURLORLOCALPATH,
				source);

		SIEnginePluginRemoteServiceImpl.LOG.debug("Uploading file. Source: {} Target: {} ", source, target);

		headers.put(SIHeader.OPERATIONNAME_STRING.toString(),
				Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_TRANSFERFILE);

		invokeManagementBusEngine(inputParamsMap, headers);

	}

	private void runScript(String commandsString, HashMap<String, Object> headers) {

		HashMap<String, String> inputParamsMap = new HashMap<>();

		inputParamsMap.put(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_PARAMETER_SCRIPT, commandsString);

		SIEnginePluginRemoteServiceImpl.LOG.debug("RunScript: {} ", commandsString);

		headers.put(SIHeader.OPERATIONNAME_STRING.toString(),
				Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_RUNSCRIPT);

		invokeManagementBusEngine(inputParamsMap, headers);
	}

	private String createArtifcatTypeSpecificCommandString(CSARID csarID, QName artifactTemplateID,
			QName artifactType) {

		SIEnginePluginRemoteServiceImpl.LOG.debug("Creating scripts...");

		String commandsString = "";

		List<String> commands = ArtifactTypesManager.getCommands(artifactType);
		for (String command : commands) {
			commandsString += command;
			commandsString += " && ";
		}

		SIEnginePluginRemoteServiceImpl.LOG.debug("Defined script for ArtifactType {} : {} ", artifactType,
				commandsString);

		commandsString = commandsString.substring(0, commandsString.length() - 4);

		Document propDoc = ServiceHandler.toscaEngineService.getPropertiesOfAArtifactTemplate(csarID,
				artifactTemplateID);

		if (propDoc != null) {

			HashMap<String, String> propertiesMap = docToMap(propDoc);

			for (Entry<String, String> prop : propertiesMap.entrySet()) {
				commandsString = commandsString.replace("{{" + prop.getKey() + "}}", prop.getValue());
			}
		}

		return commandsString;
	}

	private void invokeManagementBusEngine(HashMap<String, String> paramsMap, HashMap<String, Object> headers) {

		SIEnginePluginRemoteServiceImpl.LOG.debug("Invoking the Management Bus...");

		ProducerTemplate template = Activator.camelContext.createProducerTemplate();

		String response = template.requestBodyAndHeaders(
				"bean:org.opentosca.siengine.service.ISIEngineService?method=invokeIA", paramsMap, headers,
				String.class);

		SIEnginePluginRemoteServiceImpl.LOG.debug("Response received: {}", response);

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

		List<QName> supportedTypesQName = ArtifactTypesManager.getSupportedTypes();

		for (QName supportedTypeQName : supportedTypesQName) {
			supportedTypes.add(supportedTypeQName.toString());
		}

		return supportedTypes;
	}

}
