package org.opentosca.siengine.plugins.remote.service.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

/**
 * SIEngine-Plug-in for remoteIAs.<br>
 * <br>
 * 
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * The Plug-in gets needed information from the SI-Engine and is responsible to
 * invoke "remote IAs". Remote IAs are IAs such as scripts that needs to be
 * executed on the host machine.
 * 
 * 
 * 
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 * 
 * 
 * 
 */
public class SIEnginePluginRemoteServiceImpl implements ISIEnginePluginService {

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
		Document artifactSpecificContent = message.getHeader(SIHeader.SPECIFICCONTENT_DOCUMENT.toString(),
				Document.class);
		URI serviceInstanceID = message.getHeader(SIHeader.SERVICEINSTANCEID_URI.toString(), URI.class);
		SIEnginePluginRemoteServiceImpl.LOG.debug("ServiceInstanceID: {}", serviceInstanceID);
		String nodeInstanceID = message.getHeader(SIHeader.NODEINSTANCEID_STRING.toString(), String.class);
		SIEnginePluginRemoteServiceImpl.LOG.debug("NodeInstanceID: {}", nodeInstanceID);

		QName artifactType = ServiceHandler.toscaEngineService.getArtifactTypeOfArtifactTemplate(csarID,
				artifactTemplateID);

		// search operating system ia to upload files and run scripts on target
		// machine
		String osNodeTemplateID = getOperatingSystemNodeTemplateID(csarID, serviceTemplateID, nodeTemplateID);

		if (osNodeTemplateID != null) {
			QName osNodeTypeID = ServiceHandler.toscaEngineService.getNodeTypeOfNodeTemplate(csarID, serviceTemplateID,
					osNodeTemplateID);

			if (osNodeTypeID != null) {
				SIEnginePluginRemoteServiceImpl.LOG.debug("OperatingSystem-NodeType found: {}", osNodeTypeID);
				String osIAName = getOperatingSystemIA(csarID, serviceTemplateID, osNodeTemplateID);

				if (osIAName != null) {

					QName osNodeTypeImpl = getNodeTypeImplIDWithSpecifiedIA(csarID, osNodeTypeID, osIAName);

					SIEnginePluginRemoteServiceImpl.LOG.debug(
							"OperatingSystem-IA: {} found in NodeTypeImplementation: {}", osIAName, osNodeTypeImpl);

					// set new values
					exchange.getIn().setHeader(SIHeader.NODETEMPLATEID_STRING.toString(), osNodeTemplateID);
					exchange.getIn().setHeader(SIHeader.INTERFACENAME_STRING.toString().toString(),
							Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM);

					// install packages
					installPackages(exchange, csarID, artifactType);

					// Upload file
					transferFiles(exchange, csarID, artifactTemplateID);

					// Run script
					runScript(exchange, csarID, artifactType);

				} else {
					SIEnginePluginRemoteServiceImpl.LOG.warn("No OperatingSystem-IA found!");
				}
			} else {
				SIEnginePluginRemoteServiceImpl.LOG.warn("No OperatingSystem-NodeType found!");
			}
		} else {
			SIEnginePluginRemoteServiceImpl.LOG.warn("No OperatingSystem-NodeTemplate found!");
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

	private QName getNodeTypeImplIDWithSpecifiedIA(CSARID csarID, QName nodeTypeID, String iaName) {

		List<QName> nodeTypeImpls = ServiceHandler.toscaEngineService.getNodeTypeImplementationsOfNodeType(csarID,
				nodeTypeID);

		for (QName nodeTypeImpl : nodeTypeImpls) {
			List<String> ias = ServiceHandler.toscaEngineService
					.getImplementationArtifactNamesOfNodeTypeImplementation(csarID, nodeTypeImpl);
			if (ias.contains(iaName)) {
				return nodeTypeImpl;
			}
		}
		return null;
	}

	private void installPackages(Exchange exchange, CSARID csarID, QName artifactType) {

		exchange.getIn().setHeader(SIHeader.OPERATIONNAME_STRING.toString().toString(),
				Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_INSTALLPACKAGE);

		HashMap<String, String> inputParamsMap = new HashMap<>();

		String requiredPackagesString = "";
		List<String> requiredPackages = ArtifactTypesManager.getRequiredPackages(artifactType);
		for (String requiredPackage : requiredPackages) {
			requiredPackagesString += requiredPackage;
			requiredPackagesString += " ";
		}
		inputParamsMap.put(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_PARAMETER_PACKAGENAMES,
				requiredPackagesString);

		exchange.getIn().setBody(inputParamsMap);

		SIEnginePluginRemoteServiceImpl.LOG.debug("Installing packages: {} for ArtifactType: {} ", requiredPackages,
				artifactType);

		invokeManagementBusEngine(exchange);
	}

	private void transferFiles(Exchange exchange, CSARID csarID, QName artifactTemplate) {

		exchange.getIn().setHeader(SIHeader.OPERATIONNAME_STRING.toString().toString(),
				Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_TRANSFERFILE);

		HashMap<String, String> inputParamsMap = new HashMap<>();

		List<String> artifactReferences = ServiceHandler.toscaEngineService
				.getArtifactReferenceWithinArtifactTemplate(csarID, artifactTemplate);

		for (String artifactRef : artifactReferences) {

			String target = "~/" + csarID.getFileName() + "/" + artifactRef;

			String source = Settings.CONTAINER_API + "/CSARs/" + csarID.getFileName() + "/Content/" + artifactRef;

			inputParamsMap.put(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_PARAMETER_TARGETABSOLUTPATH,
					target);
			inputParamsMap.put(
					Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_PARAMETER_SOURCEURLORLOCALPATH, source);

			exchange.getIn().setBody(inputParamsMap);

			SIEnginePluginRemoteServiceImpl.LOG.debug("Uploading file. Source: {} Target: {} ", source, target);

			invokeManagementBusEngine(exchange);
		}
	}

	private void runScript(Exchange exchange, CSARID csarID, QName artifactType) {

		exchange.getIn().setHeader(SIHeader.OPERATIONNAME_STRING.toString().toString(),
				Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_RUNSCRIPT);

		HashMap<String, String> inputParamsMap = new HashMap<>();

		String commandsString = "";
		List<String> commands = ArtifactTypesManager.getCommands(artifactType);
		for (String command : commands) {
			commandsString += command;
			commandsString += " ";
		}
		inputParamsMap.put(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_PARAMETER_SCRIPT, commandsString);

		exchange.getIn().setBody(inputParamsMap);

		SIEnginePluginRemoteServiceImpl.LOG.debug("RunScript: {} ", commandsString);

		invokeManagementBusEngine(exchange);
	}

	private void invokeManagementBusEngine(Exchange exchange) {

		SIEnginePluginRemoteServiceImpl.LOG.debug("Invoking the Management Bus...");

		ProducerTemplate template = Activator.camelContext.createProducerTemplate();

		Exchange responseExchange = template
				.send("bean:org.opentosca.siengine.service.ISIEngineService?method=invokeIA", exchange);

		SIEnginePluginRemoteServiceImpl.LOG.debug("Response received: {}",
				responseExchange.getIn().getBody(String.class));

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
