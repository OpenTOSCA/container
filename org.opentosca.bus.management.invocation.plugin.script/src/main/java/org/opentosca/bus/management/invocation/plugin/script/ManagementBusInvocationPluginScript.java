package org.opentosca.bus.management.invocation.plugin.script;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.io.FilenameUtils;
import org.opentosca.bus.management.header.MBHeader;
import org.opentosca.bus.management.invocation.plugin.IManagementBusInvocationPluginService;
import org.opentosca.bus.management.invocation.plugin.script.servicehandler.ServiceHandler;
import org.opentosca.bus.management.invocation.plugin.script.typeshandler.ArtifactTypesHandler;
import org.opentosca.bus.management.utils.MBUtils;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.engine.ResolvedArtifacts;
import org.opentosca.container.core.engine.ResolvedArtifacts.ResolvedDeploymentArtifact;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.tosca.convention.Interfaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Management Bus-Plug-in for Script IAs which have to be executed on a host machine.<br>
 * <br>
 * <p>
 * <p>
 * <p>
 * The Plugin gets needed information from the Management Bus and is responsible for the uploading
 * of the files and the installation of required packages on the target machine (if specified).
 *
 * @author Michael Zimmermann - michael.zimmermann@iaas.uni-stuttgart.de
 */
public class ManagementBusInvocationPluginScript implements IManagementBusInvocationPluginService {

  final private static String PLACEHOLDER_TARGET_FILE_PATH = "{TARGET_FILE_PATH}";
  final private static String PLACEHOLDER_TARGET_FILE_FOLDER_PATH = "{TARGET_FILE_FOLDER_PATH}";
  final private static String PLACEHOLDER_TARGET_FILE_NAME_WITH_EXTENSION = "{TARGET_FILE_NAME_WITH_E}";
  final private static String PLACEHOLDER_TARGET_FILE_NAME_WITHOUT_EXTENSION = "{TARGET_FILE_NAME_WITHOUT_E}";
  final private static String PLACEHOLDER_DA_NAME_PATH_MAP = "{DA_NAME_PATH_MAP}";
  final private static String PLACEHOLDER_DA_INPUT_PARAMETER = "{INPUT_PARAMETER}";

  final private static String RUN_SCRIPT_OUTPUT_PARAMETER_NAME = "ScriptResult";

  final private static Logger LOG = LoggerFactory.getLogger(ManagementBusInvocationPluginScript.class);

  @Override
  public Exchange invoke(final Exchange exchange) {

    final Message message = exchange.getIn();

    ManagementBusInvocationPluginScript.LOG.debug("Management Bus Script Plugin getting information...");

    final CSARID csarID = message.getHeader(MBHeader.CSARID.toString(), CSARID.class);
    ManagementBusInvocationPluginScript.LOG.debug("CsarID: {}", csarID);
    final QName artifactTemplateID = message.getHeader(MBHeader.ARTIFACTTEMPLATEID_QNAME.toString(), QName.class);
    ManagementBusInvocationPluginScript.LOG.debug("ArtifactTemplateID: {}", artifactTemplateID);
    String nodeTemplateID = message.getHeader(MBHeader.NODETEMPLATEID_STRING.toString(), String.class);
    ManagementBusInvocationPluginScript.LOG.debug("NodeTemplateID: {}", nodeTemplateID);
    final String relationshipTemplateID =
      message.getHeader(MBHeader.RELATIONSHIPTEMPLATEID_STRING.toString(), String.class);
    ManagementBusInvocationPluginScript.LOG.debug("RelationshipTemplateID: {}", relationshipTemplateID);
    final QName serviceTemplateID = message.getHeader(MBHeader.SERVICETEMPLATEID_QNAME.toString(), QName.class);
    ManagementBusInvocationPluginScript.LOG.debug("ServiceTemplateID: {}", serviceTemplateID);
    final String interfaceName = message.getHeader(MBHeader.INTERFACENAME_STRING.toString(), String.class);
    ManagementBusInvocationPluginScript.LOG.debug("InterfaceName: {}", interfaceName);
    final String operationName = message.getHeader(MBHeader.OPERATIONNAME_STRING.toString(), String.class);
    ManagementBusInvocationPluginScript.LOG.debug("OperationName: {}", operationName);
    final URI serviceInstanceID = message.getHeader(MBHeader.SERVICEINSTANCEID_URI.toString(), URI.class);
    ManagementBusInvocationPluginScript.LOG.debug("ServiceInstanceID: {}", serviceInstanceID);
    final String nodeInstanceID = message.getHeader(MBHeader.NODEINSTANCEID_STRING.toString(), String.class);
    ManagementBusInvocationPluginScript.LOG.debug("NodeInstanceID: {}", nodeInstanceID);

    if (nodeTemplateID == null && relationshipTemplateID != null) {

      final QName relationshipTypeID =
        ServiceHandler.toscaEngineService.getRelationshipTypeOfRelationshipTemplate(csarID, serviceTemplateID,
          relationshipTemplateID);

      final boolean isBoundToSourceNode =
        ServiceHandler.toscaEngineService.isOperationOfRelationshipBoundToSourceNode(csarID, relationshipTypeID,
          interfaceName, operationName);

      if (isBoundToSourceNode) {
        nodeTemplateID =
          ServiceHandler.toscaEngineService.getSourceNodeTemplateIDOfRelationshipTemplate(csarID,
            serviceTemplateID,
            relationshipTemplateID);
      } else {
        nodeTemplateID =
          ServiceHandler.toscaEngineService.getTargetNodeTemplateIDOfRelationshipTemplate(csarID,
            serviceTemplateID,
            relationshipTemplateID);
      }
    }

    final QName nodeTypeID =
      ServiceHandler.toscaEngineService.getNodeTypeOfNodeTemplate(csarID, serviceTemplateID, nodeTemplateID);

    ManagementBusInvocationPluginScript.LOG.debug("NodeType: {}", nodeTypeID);

    // Determine output parameters of the current operation
    final List<String> outputParameters = new LinkedList<>();
    final boolean hasOutputParams =
      ServiceHandler.toscaEngineService.hasOperationOfATypeSpecifiedOutputParams(csarID, nodeTypeID,
        interfaceName, operationName);
    if (hasOutputParams) {
      final Node outputParametersNode =
        ServiceHandler.toscaEngineService.getOutputParametersOfATypeOperation(csarID, nodeTypeID, interfaceName,
          operationName);
      if (outputParametersNode != null) {
        final NodeList children = outputParametersNode.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
          final Node child = children.item(i);

          if (child.getNodeType() == Node.ELEMENT_NODE) {
            final String name = ((Element) child).getAttribute("name");
            outputParameters.add(name);
          }
        }
      }
    }
    for (final String param : outputParameters) {
      ManagementBusInvocationPluginScript.LOG.debug("Output parameter: {}", param);
    }

    final QName artifactType =
      ServiceHandler.toscaEngineService.getArtifactTypeOfArtifactTemplate(csarID, artifactTemplateID);

    ManagementBusInvocationPluginScript.LOG.debug("ArtifactType of ArtifactTemplate {} : {}", artifactTemplateID,
      artifactType);

    if (artifactType != null && nodeTemplateID != null) {

      // search operating system IA to upload files and run scripts on
      // target machine
      final String osNodeTemplateID =
        MBUtils.getOperatingSystemNodeTemplateID(csarID, serviceTemplateID, nodeTemplateID);

      if (osNodeTemplateID != null) {
        final QName osNodeTypeID =
          ServiceHandler.toscaEngineService.getNodeTypeOfNodeTemplate(csarID, serviceTemplateID,
            osNodeTemplateID);

        if (osNodeTypeID != null) {
          ManagementBusInvocationPluginScript.LOG.debug("OperatingSystem-NodeType found: {}", osNodeTypeID);
          final String osIAName = MBUtils.getOperatingSystemIA(csarID, serviceTemplateID, osNodeTemplateID);

          if (osIAName != null) {

            final Object params = message.getBody();

            // create headers
            final HashMap<String, Object> headers = new HashMap<>();

            headers.put(MBHeader.CSARID.toString(), csarID);
            headers.put(MBHeader.SERVICETEMPLATEID_QNAME.toString(), serviceTemplateID);
            headers.put(MBHeader.NODETEMPLATEID_STRING.toString(), osNodeTemplateID);
            headers.put(MBHeader.INTERFACENAME_STRING.toString(),
              MBUtils.getInterfaceForOperatingSystemNodeType(csarID, osNodeTypeID));
            headers.put(MBHeader.SERVICEINSTANCEID_URI.toString(), serviceInstanceID);
            headers.put(MBHeader.NODEINSTANCEID_STRING.toString(), nodeInstanceID);

            // install packages
            ManagementBusInvocationPluginScript.LOG.debug("Installing packages...");

            installPackages(artifactType, headers);

            ManagementBusInvocationPluginScript.LOG.debug("Packages installed.");

            // get list of artifacts
            final List<String> artifactReferences =
              ServiceHandler.toscaEngineService.getArtifactReferenceWithinArtifactTemplate(csarID,
                artifactTemplateID);

            ManagementBusInvocationPluginScript.LOG.debug("{} contains {} artifacts. Uploading and executing them...",
              artifactTemplateID, artifactReferences.size());

            // Map which contains the output parameters
            final Map<String, String> resultMap = new HashMap<>();

            final String targetBasePath = "~/" + csarID.getFileName();

            // upload and execute all contained artifacts
            for (final String artifactRef : artifactReferences) {

              final String fileSource =
                Settings.CONTAINER_API + "/csars/" + csarID.getFileName() + "/content/" + artifactRef;

              final String targetFilePath = targetBasePath + "/" + artifactRef;

              final String targetFileFolderPath = FilenameUtils.getFullPathNoEndSeparator(targetFilePath);

              final String createDirCommand = "sleep 1 && mkdir -p " + targetFileFolderPath;

              ManagementBusInvocationPluginScript.LOG.debug("Uploading file: {}", fileSource);

              // create directory before uploading file
              runScript(createDirCommand, headers);

              // upload file
              transferFile(csarID, artifactTemplateID, fileSource, targetFilePath, headers);

              ManagementBusInvocationPluginScript.LOG.debug("File successfully uploaded.");

              // run script
              ManagementBusInvocationPluginScript.LOG.debug("Running script...");

              final String fileNameWithE = FilenameUtils.getName(targetFilePath);
              final String fileNameWithoutE = FilenameUtils.getBaseName(targetFilePath);

              String artifactTypeSpecificCommand =
                createArtifcatTypeSpecificCommandString(csarID, artifactType, artifactTemplateID,
                  params);

              ManagementBusInvocationPluginScript.LOG.debug("Replacing further generic placeholder...");

              // replace placeholders
              artifactTypeSpecificCommand =
                artifactTypeSpecificCommand.replace(ManagementBusInvocationPluginScript.PLACEHOLDER_TARGET_FILE_PATH,
                  targetFilePath);
              artifactTypeSpecificCommand =
                artifactTypeSpecificCommand.replace(ManagementBusInvocationPluginScript.PLACEHOLDER_TARGET_FILE_FOLDER_PATH,
                  targetFileFolderPath);
              artifactTypeSpecificCommand =
                artifactTypeSpecificCommand.replace(ManagementBusInvocationPluginScript.PLACEHOLDER_TARGET_FILE_NAME_WITH_EXTENSION,
                  fileNameWithE);
              artifactTypeSpecificCommand =
                artifactTypeSpecificCommand.replace(ManagementBusInvocationPluginScript.PLACEHOLDER_TARGET_FILE_NAME_WITHOUT_EXTENSION,
                  fileNameWithoutE);
              artifactTypeSpecificCommand =
                artifactTypeSpecificCommand.replace(ManagementBusInvocationPluginScript.PLACEHOLDER_DA_NAME_PATH_MAP,
                  createDANamePathMapEnvVar(csarID, serviceTemplateID,
                    nodeTypeID, nodeTemplateID)
                    + " CSAR='" + csarID + "' NodeInstanceID='"
                    + nodeInstanceID + "' ServiceInstanceID='"
                    + serviceInstanceID + "' ");
              artifactTypeSpecificCommand =
                artifactTypeSpecificCommand.replace(ManagementBusInvocationPluginScript.PLACEHOLDER_DA_INPUT_PARAMETER,
                  createParamsString(params));

              // delete the uploaded file on the remote site to save resources
              final String deleteFileCommand = "; rm -f " + targetFilePath;
              artifactTypeSpecificCommand = artifactTypeSpecificCommand + deleteFileCommand;

              ManagementBusInvocationPluginScript.LOG.debug("Final command for the script execution: {}",
                artifactTypeSpecificCommand);

              final Object result = runScript(artifactTypeSpecificCommand, headers);

              ManagementBusInvocationPluginScript.LOG.debug("Script execution result: {}", result);

              // check for output parameters in the script result and add them to the
              // operation result
              addOutputParametersToResultMap(resultMap, result, outputParameters);
            }

            // remove the created directories
            ManagementBusInvocationPluginScript.LOG.debug("Deleting directories...");
            final String deleteDirsCommand = "find " + targetBasePath + " -empty -type d -delete";
            runScript(deleteDirsCommand, headers);

            ManagementBusInvocationPluginScript.LOG.debug("All artifacts are executed. Returning result to the Management Bus...");

            // create dummy response in case there are no output parameters
            if (resultMap.isEmpty()) {
              resultMap.put("invocation", "finished");
            }

            exchange.getIn().setBody(resultMap);
          } else {
            ManagementBusInvocationPluginScript.LOG.warn("No OperatingSystem-IA found!");
          }
        } else {
          ManagementBusInvocationPluginScript.LOG.warn("No OperatingSystem-NodeType found!");
        }
      } else {
        ManagementBusInvocationPluginScript.LOG.warn("No OperatingSystem-NodeTemplate found!");
      }
    } else {
      ManagementBusInvocationPluginScript.LOG.warn("Could not determine ArtifactType of ArtifactTemplate: {}!",
        artifactTemplateID);
    }
    return exchange;
  }

  /**
   * Check if the output parameters for this script service operation are returned in the script
   * result and add them to the result map.
   *
   * @param resultMap        The result map which is returned for the invocation of the script service
   *                         operation
   * @param result           The returned result of the run script operation
   * @param outputParameters The output parameters that are expected for the operation
   */
  private void addOutputParametersToResultMap(final Map<String, String> resultMap, final Object result,
                                              final List<String> outputParameters) {

    ManagementBusInvocationPluginScript.LOG.debug("Adding output parameters to the response message.");

    if (!outputParameters.isEmpty()) {
      // process result as HashMap
      if (result instanceof HashMap<?, ?>) {
        final HashMap<?, ?> resultHashMap = (HashMap<?, ?>) result;

        // get ScriptResult part of the response which contains the parameters
        if (resultHashMap.containsKey(ManagementBusInvocationPluginScript.RUN_SCRIPT_OUTPUT_PARAMETER_NAME)) {
          final Object scriptResult =
            resultHashMap.get(ManagementBusInvocationPluginScript.RUN_SCRIPT_OUTPUT_PARAMETER_NAME);

          if (scriptResult != null) {
            final String scriptResultString = scriptResult.toString();

            ManagementBusInvocationPluginScript.LOG.debug("{}: {}",
              ManagementBusInvocationPluginScript.RUN_SCRIPT_OUTPUT_PARAMETER_NAME,
              scriptResultString);

            // split result on line breaks as every parameter is returned in a separate
            // "echo" command
            final String[] resultParameters = scriptResultString.split("[\\r\\n]+");

            // add each parameter that is defined in the operation and passed back
            for (final String outputParameter : outputParameters) {
              for (int i = resultParameters.length - 1; i >= 0; i--) {
                if (resultParameters[i].startsWith(outputParameter)) {
                  final String value =
                    resultParameters[i].substring(resultParameters[i].indexOf("=") + 1);

                  ManagementBusInvocationPluginScript.LOG.debug("Adding parameter {} with value: {}",
                    outputParameter, value);
                  resultMap.put(outputParameter, value);
                }
              }
            }
          }

        } else {
          ManagementBusInvocationPluginScript.LOG.warn("Result contains no result entry '{}'",
            ManagementBusInvocationPluginScript.RUN_SCRIPT_OUTPUT_PARAMETER_NAME);
        }

      } else {
        ManagementBusInvocationPluginScript.LOG.warn("Result of type {} not supported. The bus should return a HashMap as result class when it is used as input.",
          result.getClass());
      }
    }
  }

  /**
   * @param csarID
   * @param serviceTemplateID
   * @param nodeTypeID
   * @param nodeTemplateID
   * @return mapping with DeploymentArtifact names and their paths.
   */
  private String createDANamePathMapEnvVar(final CSARID csarID, final QName serviceTemplateID, final QName nodeTypeID,
                                           final String nodeTemplateID) {

    ManagementBusInvocationPluginScript.LOG.debug("Checking if NodeTemplate {} has DAs...", nodeTemplateID);

    final HashMap<String, List<String>> daNameReferenceMapping = new HashMap<>();

    final QName nodeTemplateQName = new QName(serviceTemplateID.getNamespaceURI(), nodeTemplateID);

    final ResolvedArtifacts resolvedArtifacts =
      ServiceHandler.toscaEngineService.getResolvedArtifactsOfNodeTemplate(csarID, nodeTemplateQName);

    final List<ResolvedDeploymentArtifact> resolvedDAs = resolvedArtifacts.getDeploymentArtifacts();

    List<String> daArtifactReferences;

    for (final ResolvedDeploymentArtifact resolvedDA : resolvedDAs) {

      daArtifactReferences = resolvedDA.getReferences();

      for (final String daArtifactReference : daArtifactReferences) {

        ManagementBusInvocationPluginScript.LOG.debug("Artifact reference for DA: {} found: {} .",
          resolvedDA.getName(), daArtifactReference);

        List<String> currentValue = daNameReferenceMapping.get(resolvedDA.getName());
        if (currentValue == null) {
          currentValue = new ArrayList<>();
          daNameReferenceMapping.put(resolvedDA.getName(), currentValue);
        }
        currentValue.add(daArtifactReference);
      }
    }

    final List<QName> nodeTypeImpls =
      ServiceHandler.toscaEngineService.getTypeImplementationsOfType(csarID, nodeTypeID);

    for (final QName nodeTypeImpl : nodeTypeImpls) {
      final List<String> daNames =
        ServiceHandler.toscaEngineService.getDeploymentArtifactNamesOfNodeTypeImplementation(csarID,
          nodeTypeImpl);

      for (final String daName : daNames) {
        final QName daArtifactTemplate =
          ServiceHandler.toscaEngineService.getArtifactTemplateOfADeploymentArtifactOfANodeTypeImplementation(csarID,
            nodeTypeImpl, daName);

        daArtifactReferences =
          ServiceHandler.toscaEngineService.getArtifactReferenceWithinArtifactTemplate(csarID,
            daArtifactTemplate);

        for (final String daArtifactReference : daArtifactReferences) {

          ManagementBusInvocationPluginScript.LOG.debug("Artifact reference for DA: {} found: {} .", daName,
            daArtifactReference);

          List<String> currentValue = daNameReferenceMapping.get(daName);
          if (currentValue == null) {
            currentValue = new ArrayList<>();
            daNameReferenceMapping.put(daName, currentValue);
          }
          currentValue.add(daArtifactReference);
        }
      }
    }

    String daEnvMap = "";
    if (!daNameReferenceMapping.isEmpty()) {

      ManagementBusInvocationPluginScript.LOG.debug("NodeTemplate {} has {} DAs.", nodeTemplateID,
        daNameReferenceMapping.size());

      daEnvMap += "DAs=\"";
      for (final Entry<String, List<String>> da : daNameReferenceMapping.entrySet()) {

        final String daName = da.getKey();
        final List<String> daRefs = da.getValue();

        for (String daRef : daRefs) {

          // FIXME / is a brutal assumption
          if (!daRef.startsWith("/")) {
            daRef = "/" + daRef;
          }

          daEnvMap += daName + "," + daRef + ";";
        }
      }
      daEnvMap += "\" ";

      ManagementBusInvocationPluginScript.LOG.debug("Created DA-DANamePathMapEnvVar for NodeTemplate {} : {}",
        nodeTemplateID, daEnvMap);
    }

    return daEnvMap;
  }

  /**
   * Installs required and specified packages of the specified ArtifactType. Required packages are
   * in defined the corresponding *.xml file.
   *
   * @param artifactType
   * @param headers
   */
  private void installPackages(final QName artifactType, final HashMap<String, Object> headers) {

    final List<String> requiredPackages = ArtifactTypesHandler.getRequiredPackages(artifactType);

    String requiredPackagesString = "";

    if (!requiredPackages.isEmpty()) {

      final HashMap<String, String> inputParamsMap = new HashMap<>();

      for (final String requiredPackage : requiredPackages) {
        requiredPackagesString += requiredPackage;
        requiredPackagesString += " ";
      }
      inputParamsMap.put(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_PARAMETER_PACKAGENAMES,
        requiredPackagesString);

      ManagementBusInvocationPluginScript.LOG.debug("Installing packages: {} for ArtifactType: {} ",
        requiredPackages, artifactType);

      headers.put(MBHeader.OPERATIONNAME_STRING.toString(),
        Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_INSTALLPACKAGE);

      invokeManagementBusEngine(inputParamsMap, headers);
    } else {
      ManagementBusInvocationPluginScript.LOG.debug("ArtifactType: {} needs no packages to install.",
        requiredPackages, artifactType);
    }
  }

  /**
   * For transferring files to the target machine.
   *
   * @param csarID
   * @param artifactTemplate
   * @param source
   * @param target
   * @param headers
   */
  private void transferFile(final CSARID csarID, final QName artifactTemplate, final String source,
                            final String target, final HashMap<String, Object> headers) {

    final HashMap<String, String> inputParamsMap = new HashMap<>();

    inputParamsMap.put(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_PARAMETER_TARGETABSOLUTPATH,
      target);
    inputParamsMap.put(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_PARAMETER_SOURCEURLORLOCALPATH,
      source);

    ManagementBusInvocationPluginScript.LOG.debug("Uploading file. Source: {} Target: {} ", source, target);

    headers.put(MBHeader.OPERATIONNAME_STRING.toString(),
      Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_TRANSFERFILE);

    ManagementBusInvocationPluginScript.LOG.debug("Invoking ManagementBus for transferFile with the following headers:");

    for (final String key : headers.keySet()) {
      if (headers.get(key) != null && headers.get(key) instanceof String) {
        ManagementBusInvocationPluginScript.LOG.debug("Header: " + key + " Value: " + headers.get(key));
      }
    }

    invokeManagementBusEngine(inputParamsMap, headers);

  }

  /**
   * For running scripts on the target machine. Commands to be executed are defined in the
   * corresponding *.xml file.
   *
   * @param commandsString
   * @param headers
   */
  private Object runScript(final String commandsString, final HashMap<String, Object> headers) {

    final HashMap<String, String> inputParamsMap = new HashMap<>();

    inputParamsMap.put(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_PARAMETER_SCRIPT, commandsString);

    ManagementBusInvocationPluginScript.LOG.debug("RunScript: {} ", commandsString);

    headers.put(MBHeader.OPERATIONNAME_STRING.toString(),
      Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_RUNSCRIPT);

    ManagementBusInvocationPluginScript.LOG.debug("Invoking ManagementBus for runScript with the following headers:");

    for (final String key : headers.keySet()) {
      if (headers.get(key) != null && headers.get(key) instanceof String) {
        ManagementBusInvocationPluginScript.LOG.debug("Header: " + key + " Value: " + headers.get(key));
      }
    }

    return invokeManagementBusEngine(inputParamsMap, headers);
  }

  /**
   * Creates ArtifactType specific commands that should be executed on the target machine.
   * Commands to be executed are defined in the corresponding *.xml file.
   *
   * @param csarID
   * @param artifactType
   * @param artifactTemplateID
   * @param params
   * @return the created command
   */
  @SuppressWarnings("unchecked")
  private String createArtifcatTypeSpecificCommandString(final CSARID csarID, final QName artifactType,
                                                         final QName artifactTemplateID, final Object params) {

    ManagementBusInvocationPluginScript.LOG.debug("Creating ArtifcatType specific command for artifactType {}:...",
      artifactType);

    String commandsString = "";

    final List<String> commands = ArtifactTypesHandler.getCommands(artifactType);

    for (final String command : commands) {
      commandsString += command;
      commandsString += " && ";
    }

    if (commandsString.endsWith(" && ")) {
      commandsString = commandsString.substring(0, commandsString.length() - 4);
    }

    ManagementBusInvocationPluginScript.LOG.debug("Defined generic command for ArtifactType {} : {} ", artifactType,
      commandsString);

    // replace placeholder with data from inputParams and/or instance data

    if (commandsString.contains("{{") && commandsString.contains("}}")) {

      ManagementBusInvocationPluginScript.LOG.debug("Replacing the placeholder of the generic command with properties data and/or provided input parameter...");

      HashMap<String, String> paramsMap = new HashMap<>();

      if (params instanceof HashMap) {
        paramsMap = (HashMap<String, String>) params;
      } else if (params instanceof Document) {
        final Document paramsDoc = (Document) params;
        paramsMap = MBUtils.docToMap(paramsDoc, true);
      }

      final Document propDoc =
        ServiceHandler.toscaEngineService.getPropertiesOfAArtifactTemplate(csarID, artifactTemplateID);

      if (propDoc != null) {
        paramsMap.putAll(MBUtils.docToMap(propDoc, true));
      }

      for (final Entry<String, String> prop : paramsMap.entrySet()) {
        commandsString = commandsString.replace("{{" + prop.getKey() + "}}", prop.getValue());
      }

      // delete not replaced placeholder
      commandsString = commandsString.replaceAll("\\{\\{.*?\\}\\}", "");

      ManagementBusInvocationPluginScript.LOG.debug("Generic command with replaced placeholder: {}",
        commandsString);
    }

    return commandsString;
  }

  /**
   * @param params
   * @return whitespace separated String with parameter keys and values
   */
  @SuppressWarnings("unchecked")
  private String createParamsString(final Object params) {
    HashMap<String, String> paramsMap = new HashMap<>();

    if (params instanceof HashMap) {
      paramsMap = (HashMap<String, String>) params;
    } else if (params instanceof Document) {
      final Document paramsDoc = (Document) params;
      paramsMap = MBUtils.docToMap(paramsDoc, true);
    }

    String paramsString = "";
    for (final Entry<String, String> param : paramsMap.entrySet()) {
      // info:
      // https://stackoverflow.com/questions/3005963/how-can-i-have-a-newline-in-a-string-in-sh
      // https://stackoverflow.com/questions/1250079/how-to-escape-single-quotes-within-single-quoted-strings
      // we have to escape single quotes in the parameter values and properly pipe newlines
      // TODO(?) There is still the issue if you use commands in scipt which don't interpret
      // backslashes
      paramsString += param.getKey() + "=$'" + escapeSingleQuotes(param.getValue()) + "' ";
    }

    return paramsString;
  }

  /**
   * Escapes single quotes (') inside the given string conforming to bash argument values. Each '
   * gets transformed to '"'"'
   *
   * @return a String with escaped singles quotes
   * @see https://stackoverflow.com/questions/1250079/how-to-escape-single-quotes-within-single-quoted-strings
   */
  private String escapeSingleQuotes(final String unenscapedString) {
    return unenscapedString.replace("'", "'\"'\"'").replace("\n", "'\"\\n\"'");
  }

  /**
   * Invokes the Management Bus.
   *
   * @param paramsMap
   * @param headers
   */
  private Object invokeManagementBusEngine(final HashMap<String, String> paramsMap,
                                           final HashMap<String, Object> headers) {

    ManagementBusInvocationPluginScript.LOG.debug("Invoking the Management Bus...");

    final ProducerTemplate template = Activator.camelContext.createProducerTemplate();

    final Object response =
      template.requestBodyAndHeaders("bean:org.opentosca.bus.management.service.IManagementBusService?method=invokeIA",
        paramsMap, headers);

    ManagementBusInvocationPluginScript.LOG.debug("Invocation finished: {}", response);

    return response;
  }

  @Override
  public List<String> getSupportedTypes() {
    return ArtifactTypesHandler.getSupportedTypes().stream().map(QName::toString).collect(Collectors.toList());
  }
}
