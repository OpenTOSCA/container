package org.opentosca.bus.management.invocation.plugin.script;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TArtifactReference;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TArtifactType;
import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TImplementationArtifact;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TParameter;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TServiceTemplate;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.opentosca.bus.management.header.MBHeader;
import org.opentosca.bus.management.invocation.plugin.IManagementBusInvocationPluginService;
import org.opentosca.bus.management.invocation.plugin.script.typeshandler.ArtifactTypesHandler;
import org.opentosca.bus.management.utils.MBUtils;
import org.opentosca.container.core.common.NotFoundException;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.convention.Interfaces;
import org.opentosca.container.core.convention.Types;
import org.opentosca.container.core.engine.ResolvedArtifacts;
import org.opentosca.container.core.engine.ResolvedArtifacts.ResolvedDeploymentArtifact;
import org.opentosca.container.core.engine.ToscaEngine;
import org.opentosca.container.core.engine.next.ContainerEngine;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.service.CsarStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

/**
 * Management Bus-Plug-in for Script IAs which have to be executed on a host machine.<br>
 * <br>
 * The Plugin gets needed information from the Management Bus and is responsible for the uploading of the files and the
 * installation of required packages on the target machine (if specified).
 *
 * @author Michael Zimmermann - michael.zimmermann@iaas.uni-stuttgart.de
 */
@Service
public class ManagementBusInvocationPluginScript extends IManagementBusInvocationPluginService {

    final private static String PLACEHOLDER_TARGET_FILE_PATH = "{TARGET_FILE_PATH}";
    final private static String PLACEHOLDER_TARGET_FILE_FOLDER_PATH = "{TARGET_FILE_FOLDER_PATH}";
    final private static String PLACEHOLDER_TARGET_FILE_NAME_WITH_EXTENSION = "{TARGET_FILE_NAME_WITH_E}";
    final private static String PLACEHOLDER_TARGET_FILE_NAME_WITHOUT_EXTENSION = "{TARGET_FILE_NAME_WITHOUT_E}";
    final private static String PLACEHOLDER_DA_NAME_PATH_MAP = "{DA_NAME_PATH_MAP}";
    final private static String PLACEHOLDER_DA_INPUT_PARAMETER = "{INPUT_PARAMETER}";

    final private static String RUN_SCRIPT_OUTPUT_PARAMETER_NAME = "ScriptResult";

    final private static Logger LOG = LoggerFactory.getLogger(ManagementBusInvocationPluginScript.class);

    private final ArtifactTypesHandler typesHandler;
    private final CsarStorageService storage;
    private final ContainerEngine containerEngine;

    private final CamelContext camelContext;

    private final MBUtils mbUtils;

    @Inject
    public ManagementBusInvocationPluginScript(ArtifactTypesHandler typesHandler, CsarStorageService storage,
                                               ContainerEngine containerEngine, MBUtils mbUtils) {
        this.typesHandler = typesHandler;
        this.storage = storage;
        this.containerEngine = containerEngine;
        this.camelContext = new DefaultCamelContext();
        this.mbUtils = mbUtils;
    }

    @Override
    public Exchange invoke(final Exchange exchange) {
        LOG.debug("Management Bus Script Plugin getting information...");

        final Message message = exchange.getIn();
        final CsarId csarID = message.getHeader(MBHeader.CSARID.toString(), CsarId.class);
        LOG.debug("CsarID: {}", csarID);
        final QName artifactTemplateID = message.getHeader(MBHeader.ARTIFACTTEMPLATEID_QNAME.toString(), QName.class);
        LOG.debug("ArtifactTemplateID: {}", artifactTemplateID);
        final String relationshipTemplateID = message.getHeader(MBHeader.RELATIONSHIPTEMPLATEID_STRING.toString(), String.class);
        LOG.debug("RelationshipTemplateID: {}", relationshipTemplateID);
        final QName serviceTemplateID = message.getHeader(MBHeader.SERVICETEMPLATEID_QNAME.toString(), QName.class);
        LOG.debug("ServiceTemplateID: {}", serviceTemplateID);

        final String interfaceName = message.getHeader(MBHeader.INTERFACENAME_STRING.toString(), String.class);
        LOG.debug("InterfaceName: {}", interfaceName);
        final String operationName = message.getHeader(MBHeader.OPERATIONNAME_STRING.toString(), String.class);
        LOG.debug("OperationName: {}", operationName);
        final Csar csar = storage.findById(csarID);
        try {
            final TServiceTemplate serviceTemplate = ToscaEngine.resolveServiceTemplate(csar, serviceTemplateID);
            final TArtifactTemplate artifactTemplate = ToscaEngine.resolveArtifactTemplate(csar, artifactTemplateID);
            final TArtifactType artifactType = ToscaEngine.resolveArtifactType(csar, artifactTemplate.getType());
            // the relationship template does not need to be present
            final TRelationshipTemplate relationshipTemplate = ToscaEngine.getRelationshipTemplate(serviceTemplate, relationshipTemplateID).orElse(null);
            final TNodeTemplate nodeTemplate = getNodeTemplate(message, csar, relationshipTemplate, serviceTemplate, interfaceName, operationName);
            final TNodeType nodeType = ToscaEngine.resolveNodeTypeReference(csar, nodeTemplate.getType());
            final TOperation operation = ToscaEngine.resolveOperation(csar, nodeType, interfaceName, operationName);

            return handleExchangeInternal(exchange, message, csarID, serviceTemplateID, csar, serviceTemplate,
                artifactTemplate, artifactType, nodeTemplate, nodeType, operation);
        } catch (NotFoundException e) {
            LOG.warn("Failed to resolve a strongly typed CSAR content reference, invocation failed!", e);
            return exchange;
        }
    }

    private Exchange handleExchangeInternal(Exchange exchange, Message message, CsarId csarID, QName serviceTemplateID,
                                            Csar csar, TServiceTemplate serviceTemplate, TArtifactTemplate artifactTemplate,
                                            TArtifactType artifactType, TNodeTemplate nodeTemplate, TNodeType nodeType,
                                            TOperation operation) throws NotFoundException {
        if (artifactType == null || nodeTemplate == null) {
            LOG.warn("Could not determine ArtifactType of ArtifactTemplate: {}!", artifactTemplate.getId());
            return exchange;
        }
        LOG.debug("ArtifactType of ArtifactTemplate {} : {}", artifactTemplate.getId(), artifactType.getQName());
        final URI serviceInstanceID = message.getHeader(MBHeader.SERVICEINSTANCEID_URI.toString(), URI.class);
        LOG.debug("ServiceInstanceID: {}", serviceInstanceID);
        // search operating system IA to upload files and run scripts on target machine
        final long serviceTemplateInstanceId = Long.parseLong(StringUtils.substringAfterLast(serviceInstanceID.toString(), "/"));
        TNodeTemplate osNodeTemplate = mbUtils.getOperatingSystemNodeTemplate(csar, serviceTemplate, nodeTemplate, true,
            serviceTemplateInstanceId);

        if (osNodeTemplate == null) {
            LOG.warn("No OperatingSystem-NodeTemplate found!");
            return exchange;
        }

        if (osNodeTemplate.getType().equals(Types.abstractOperatingSystemNodeType)) {
            final NodeTemplateInstance abstractOSInstance = mbUtils.getNodeTemplateInstance(serviceTemplateInstanceId, osNodeTemplate);
            if (abstractOSInstance != null) {
                final NodeTemplateInstance replacementInstance = mbUtils.getAbstractOSReplacementInstance(abstractOSInstance);
                if (replacementInstance != null) {
                    // overwrite computed intermediate result based on replacement
                    csar = storage.findById(replacementInstance.getServiceTemplateInstance().getCsarId());
                    serviceTemplate = ToscaEngine.resolveServiceTemplate(csar, replacementInstance.getServiceTemplateInstance().getTemplateId());
                    osNodeTemplate = ToscaEngine.resolveNodeTemplate(serviceTemplate, replacementInstance.getTemplateId());
                }
            }
        }
        final TNodeType osNodeType = ToscaEngine.resolveNodeTypeReference(csar, osNodeTemplate.getType());
        LOG.debug("OperatingSystem-NodeType found: {}", osNodeType.getQName());
        final TImplementationArtifact osIA = mbUtils.getOperatingSystemIA(csar, serviceTemplate, osNodeType);

        if (osIA == null) {
            LOG.warn("No OperatingSystem-IA found!");
            return exchange;
        }

        final String nodeInstanceID = message.getHeader(MBHeader.NODEINSTANCEID_STRING.toString(), String.class);
        LOG.debug("NodeInstanceID: {}", nodeInstanceID);

        final Object params = message.getBody();
        // create headers
        final Map<String, Object> headers = new HashMap<>();

        headers.put(MBHeader.CSARID.toString(), csarID);
        headers.put(MBHeader.SERVICETEMPLATEID_QNAME.toString(), serviceTemplateID);
        headers.put(MBHeader.NODETEMPLATEID_STRING.toString(), osNodeTemplate.getIdFromIdOrNameField());
        headers.put(MBHeader.INTERFACENAME_STRING.toString(), MBUtils.getInterfaceForOperatingSystemNodeType(csar, osNodeType));
        headers.put(MBHeader.SERVICEINSTANCEID_URI.toString(), serviceInstanceID);
        headers.put(MBHeader.NODEINSTANCEID_STRING.toString(), nodeInstanceID);

        // install packages
        LOG.debug("Installing packages...");
        installPackages(artifactType, headers);
        LOG.debug("Packages installed.");

        // get list of artifacts
        final List<TArtifactReference> artifactReferences = (artifactTemplate.getArtifactReferences() == null)
            ? Collections.emptyList()
            : artifactTemplate.getArtifactReferences();
        LOG.debug("{} contains {} artifacts. Uploading and executing them...", artifactTemplate.getId(), artifactReferences.size());

        // Map which contains the output parameters
        final Map<String, String> resultMap = new HashMap<>();
        final String targetBasePath = "~/" + csarID.csarName();

        // upload and execute all contained artifacts
        for (final TArtifactReference artifactRef : artifactReferences) {
            final String fileSource = Settings.CONTAINER_API + "/csars/" + csarID.csarName() + "/content/" + artifactRef.getReference();
            final String targetFilePath = targetBasePath + "/" + artifactRef.getReference();
            final String targetFileFolderPath = FilenameUtils.getFullPathNoEndSeparator(targetFilePath);
            final String createDirCommand = "sleep 1 && mkdir -p " + targetFileFolderPath;

            LOG.debug("Uploading file: {}", fileSource);
            // create directory before uploading file
            runScript(createDirCommand, headers);
            // upload file
            transferFile(fileSource, targetFilePath, headers);
            LOG.debug("File successfully uploaded.");

            // run script
            LOG.debug("Running script...");
            final String fileNameWithE = FilenameUtils.getName(targetFilePath);
            final String fileNameWithoutE = FilenameUtils.getBaseName(targetFilePath);

            String artifactTypeSpecificCommand = createArtifactTypeSpecificCommandString(artifactType, artifactTemplate, params);
            LOG.debug("Replacing further generic placeholder...");
            // replace placeholders
            artifactTypeSpecificCommand = artifactTypeSpecificCommand.replace(ManagementBusInvocationPluginScript.PLACEHOLDER_TARGET_FILE_PATH, targetFilePath);
            artifactTypeSpecificCommand = artifactTypeSpecificCommand.replace(ManagementBusInvocationPluginScript.PLACEHOLDER_TARGET_FILE_FOLDER_PATH, targetFileFolderPath);
            artifactTypeSpecificCommand = artifactTypeSpecificCommand.replace(ManagementBusInvocationPluginScript.PLACEHOLDER_TARGET_FILE_NAME_WITH_EXTENSION, fileNameWithE);
            artifactTypeSpecificCommand = artifactTypeSpecificCommand.replace(ManagementBusInvocationPluginScript.PLACEHOLDER_TARGET_FILE_NAME_WITHOUT_EXTENSION, fileNameWithoutE);
            artifactTypeSpecificCommand = artifactTypeSpecificCommand.replace(ManagementBusInvocationPluginScript.PLACEHOLDER_DA_NAME_PATH_MAP,
                createDANamePathMapEnvVar(csar, nodeType, nodeTemplate) + " CSAR='" + csarID + "' NodeInstanceID='" + nodeInstanceID + "' ServiceInstanceID='" + serviceInstanceID + "' ");
            artifactTypeSpecificCommand = artifactTypeSpecificCommand.replace(ManagementBusInvocationPluginScript.PLACEHOLDER_DA_INPUT_PARAMETER, createParamsString(params));

            if (!Boolean.parseBoolean(Settings.OPENTOSCA_ENGINE_IA_KEEPFILES)) {
                // delete the uploaded file on the remote site to save resources
                final String deleteFileCommand = "; rm -f " + targetFilePath;
                artifactTypeSpecificCommand = artifactTypeSpecificCommand + deleteFileCommand;
            }

            LOG.debug("Final command for the script execution: {}", artifactTypeSpecificCommand);
            final Object result = runScript(artifactTypeSpecificCommand, headers);
            LOG.debug("Script execution result: {}", result);

            // check for output parameters in the script result and add them to the
            // operation result
            addOutputParametersToResultMap(resultMap, result, operation);
        }

        if (!Boolean.parseBoolean(Settings.OPENTOSCA_ENGINE_IA_KEEPFILES)) {
            // remove the created directories
            LOG.debug("Deleting directories...");
            final String deleteDirsCommand = "find " + targetBasePath + " -empty -type d -delete";
            runScript(deleteDirsCommand, headers);
        }

        LOG.debug("All artifacts are executed. Returning result to the Management Bus...");

        // create dummy response in case there are no output parameters
        if (resultMap.isEmpty()) {
            resultMap.put("invocation", "finished");
        }

        exchange.getIn().setBody(resultMap);
        return exchange;
    }

    private TNodeTemplate getNodeTemplate(Message message, Csar csar, TRelationshipTemplate relationshipTemplate, TServiceTemplate serviceTemplate, String interfaceName, String operationName) throws NotFoundException {
        String nodeTemplateID = message.getHeader(MBHeader.NODETEMPLATEID_STRING.toString(), String.class);
        LOG.debug("NodeTemplateID: {}", nodeTemplateID);
        if (nodeTemplateID == null && relationshipTemplate != null) {
            // fill the node template from the relationship template
            final TRelationshipType relationshipType = ToscaEngine.resolveRelationshipTypeReference(csar, relationshipTemplate.getType());
            final boolean isBoundToSourceNode = ToscaEngine.isOperationBoundToSourceNode(relationshipType, interfaceName, operationName);
            return isBoundToSourceNode
                ? (TNodeTemplate) relationshipTemplate.getSourceElement().getRef()
                : (TNodeTemplate) relationshipTemplate.getTargetElement().getRef();
        }
        return ToscaEngine.resolveNodeTemplate(serviceTemplate, nodeTemplateID);
    }

    /**
     * Check if the output parameters for this script service operation are returned to the script result and add them
     * to the result map.
     *
     * @param resultMap The result map which is returned for the invocation of the script service operation
     * @param result    The returned result of the run script operation
     * @param operation The script service operation to check
     */
    private void addOutputParametersToResultMap(final Map<String, String> resultMap, final Object result, final TOperation operation) {
        final boolean hasOutputParams = operation.getOutputParameters() != null;
        if (!hasOutputParams) {
            return;
        }
        if (!(result instanceof HashMap<?, ?>)) {
            LOG.warn("Result of type {} not supported. The bus should return a HashMap as result class when it is used as input.", result.getClass());
            return;
        }
        LOG.debug("Adding output parameters to the response message.");
        final Map<?, ?> resultHashMap = (HashMap<?, ?>) result;

        // get ScriptResult part of the response which contains the parameters
        if (!resultHashMap.containsKey(ManagementBusInvocationPluginScript.RUN_SCRIPT_OUTPUT_PARAMETER_NAME)) {
            LOG.warn("Result contains no result entry '{}'", ManagementBusInvocationPluginScript.RUN_SCRIPT_OUTPUT_PARAMETER_NAME);
            return;
        }
        final Object scriptResult = resultHashMap.get(ManagementBusInvocationPluginScript.RUN_SCRIPT_OUTPUT_PARAMETER_NAME);
        if (scriptResult == null) {
            return;
        }

        final String scriptResultString = scriptResult.toString();
        LOG.debug("{}: {}", ManagementBusInvocationPluginScript.RUN_SCRIPT_OUTPUT_PARAMETER_NAME, scriptResultString);

        // split result in line breaks as every parameter is returned in a separate "echo" command
        final String[] resultParameters = scriptResultString.split("[\\r\\n]+");

        // add each parameter that is defined in the operation and passed back
        for (final TParameter outputParameter : operation.getOutputParameters()) {
            for (int i = resultParameters.length - 1; i >= 0; i--) {
                if (resultParameters[i].startsWith(outputParameter.getName())) {
                    final String value = resultParameters[i].substring(resultParameters[i].indexOf("=") + 1);

                    LOG.debug("Adding parameter {} with value: {}", outputParameter, value);
                    resultMap.put(outputParameter.getName(), value);
                }
            }
        }
    }

    /**
     * @return mapping with DeploymentArtifact names and their paths.
     */
    private String createDANamePathMapEnvVar(final Csar csar, final TNodeType nodeType, final TNodeTemplate nodeTemplate) {
        LOG.debug("Checking if NodeTemplate {} has DAs...", nodeTemplate.getName());
        List<String> daArtifactReferences;

        final Map<String, List<String>> daNameReferenceMapping = new HashMap<>();

        final ResolvedArtifacts resolvedArtifacts = containerEngine.resolvedDeploymentArtifacts(csar, nodeTemplate);
        for (final ResolvedDeploymentArtifact resolvedDA : resolvedArtifacts.getDeploymentArtifacts()) {
            daArtifactReferences = resolvedDA.getReferences();

            for (final String daArtifactReference : daArtifactReferences) {
                LOG.debug("Artifact reference for DA: {} found: {} .", resolvedDA.getName(), daArtifactReference);
                List<String> currentValue = daNameReferenceMapping.computeIfAbsent(resolvedDA.getName(), k -> new ArrayList<>());
                currentValue.add(daArtifactReference);
            }
        }

        final List<TNodeTypeImplementation> nodeTypeImpls = ToscaEngine.getNodeTypeImplementations(csar, nodeType);
        for (final TNodeTypeImplementation nodeTypeImpl : nodeTypeImpls) {
            List<TDeploymentArtifact> das = nodeTypeImpl.getDeploymentArtifacts();
            if (das != null) {
                for (final TDeploymentArtifact da : das) {
                    final TArtifactTemplate daArtifactTemplate;
                    try {
                        daArtifactTemplate = ToscaEngine.resolveArtifactTemplate(csar, da.getArtifactRef());
                    } catch (NotFoundException e) {
                        LOG.warn("Failed to find ArtifactTemplate with reference [{}] for DeploymentArtifact {}", da.getArtifactRef(), da.getName());
                        continue;
                    }
                    if (daArtifactTemplate.getArtifactReferences() == null) {
                        continue;
                    }
                    for (final TArtifactReference daArtifactReference : daArtifactTemplate.getArtifactReferences()) {
                        LOG.debug("Artifact reference for DA: {} found: {} .", da.getName(), daArtifactReference);

                        List<String> currentValue = daNameReferenceMapping.computeIfAbsent(da.getName(), k -> new ArrayList<>());
                        currentValue.add(daArtifactReference.getReference());
                    }
                }
            }
        }
        StringBuilder daEnvMap = new StringBuilder();
        if (!daNameReferenceMapping.isEmpty()) {
            LOG.debug("NodeTemplate {} has {} DAs.", nodeTemplate.getName(), daNameReferenceMapping.size());
            daEnvMap.append("DAs=\"");
            for (final Entry<String, List<String>> da : daNameReferenceMapping.entrySet()) {
                final String daName = da.getKey();
                final List<String> daRefs = da.getValue();
                for (String daRef : daRefs) {
                    // FIXME / is a brutal assumption
                    if (!daRef.startsWith("/")) {
                        daRef = "/" + daRef;
                    }
                    daEnvMap.append(daName).append(",").append(daRef).append(";");
                }
            }
            daEnvMap.append("\" ");
            LOG.debug("Created DA-DANamePathMapEnvVar for NodeTemplate {} : {}",
                nodeTemplate.getName(), daEnvMap);
        }

        return daEnvMap.toString();
    }

    /**
     * Installs required and specified packages of the specified ArtifactType. Required packages are in defined the
     * corresponding *.xml file.
     */
    private void installPackages(final TArtifactType artifactType, final Map<String, Object> headers) {
        final List<String> requiredPackages = typesHandler.getRequiredPackages(artifactType.getQName());
        if (requiredPackages.isEmpty()) {
            LOG.debug("ArtifactType: {} needs no packages to install.", artifactType);
            return;
        }

        final String requiredPackagesString = String.join(" ", requiredPackages);
        final String commandsString = "apt update && export DEBIAN_FRONTEND=noninteractive && apt install -y -q " + requiredPackagesString;

        this.runScript(commandsString, headers);
    }

    /**
     * For transferring files to the target machine.
     */
    private void transferFile(final String source, final String target, final Map<String, Object> headers) {
        final Map<String, String> inputParamsMap = new HashMap<>();
        inputParamsMap.put(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_PARAMETER_TARGETABSOLUTPATH, target);
        inputParamsMap.put(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_PARAMETER_SOURCEURLORLOCALPATH, source);

        LOG.debug("Uploading file. Source: {} Target: {} ", source, target);
        headers.put(MBHeader.OPERATIONNAME_STRING.toString(), Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_TRANSFERFILE);
        LOG.debug("Invoking ManagementBus for transferFile with the following headers:");

        for (final String key : headers.keySet()) {
            if (headers.get(key) != null && headers.get(key) instanceof String) {
                LOG.debug("Header: " + key + " Value: " + headers.get(key));
            }
        }
        invokeManagementBusEngine(inputParamsMap, headers);
    }

    /**
     * For running scripts on the target machine. Commands to be executed are defined in the corresponding *.xml file.
     */
    private Object runScript(final String commandsString, final Map<String, Object> headers) {
        LOG.debug("RunScript: {} ", commandsString);
        final HashMap<String, String> inputParamsMap = new HashMap<>();
        inputParamsMap.put(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_PARAMETER_SCRIPT, commandsString);
        headers.put(MBHeader.OPERATIONNAME_STRING.toString(), Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_RUNSCRIPT);

        LOG.debug("Invoking ManagementBus for runScript with the following headers:");
        for (final String key : headers.keySet()) {
            if (headers.get(key) != null && headers.get(key) instanceof String) {
                LOG.debug("Header: " + key + " Value: " + headers.get(key));
            }
        }
        return invokeManagementBusEngine(inputParamsMap, headers);
    }

    /**
     * Creates ArtifactType specific commands that should be executed on the target machine. Commands to be executed are
     * defined in the corresponding *.xml file.
     *
     * @return the created command
     */
    private String createArtifactTypeSpecificCommandString(final TArtifactType artifactType,
                                                           final TArtifactTemplate artifactTemplate,
                                                           final Object params) {
        LOG.debug("Creating ArtifactType specific command for artifactType {}:...", artifactType);

        final List<String> commands = typesHandler.getCommands(artifactType.getQName());
        String commandsString = String.join(" && ", commands);
        LOG.debug("Defined generic command for ArtifactType {} : {} ", artifactType, commandsString);

        // replace placeholder with data from inputParams and/or instance data
        if (commandsString.contains("{{") && commandsString.contains("}}")) {
            LOG.debug("Replacing the placeholder of the generic command with properties data and/or provided input parameter...");

            final Map<String, String> paramsMap;
            if (params instanceof HashMap) {
                paramsMap = (HashMap<String, String>) params;
            } else if (params instanceof Document) {
                final Document paramsDoc = (Document) params;
                paramsMap = MBUtils.docToMap(paramsDoc, true);
            } else {
                paramsMap = new HashMap<>();
            }

            final Document propDoc = ToscaEngine.getEntityTemplateProperties(artifactTemplate);
            if (propDoc != null) {
                paramsMap.putAll(MBUtils.docToMap(propDoc, true));
            }

            for (final Entry<String, String> prop : paramsMap.entrySet()) {
                commandsString = commandsString.replace("{{" + prop.getKey() + "}}", prop.getValue());
            }
            // delete not replaced placeholder
            commandsString = commandsString.replaceAll("\\{\\{.*?\\}\\}", "");
            LOG.debug("Generic command with replaced placeholder: {}", commandsString);
        }
        return commandsString;
    }

    /**
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

        StringBuilder paramsString = new StringBuilder();
        for (final Entry<String, String> param : paramsMap.entrySet()) {
            // info:
            // https://stackoverflow.com/questions/3005963/how-can-i-have-a-newline-in-a-string-in-sh
            // https://stackoverflow.com/questions/1250079/how-to-escape-single-quotes-within-single-quoted-strings
            // we have to escape single quotes in the parameter values and properly pipe newlines
            // TODO(?) There is still the issue if you use commands in script which don't interpret backslashes
            paramsString.append(param.getKey()).append("=$'").append(escapeSpecialCharacters(param.getValue())).append("' ");
        }

        return paramsString.toString();
    }

    /**
     * Escapes special characters inside the given string conforming to bash argument values.
     * <p>
     * See e.g. <a href="https://stackoverflow.com/questions/1250079/how-to-escape-single-quotes-within-single-quoted-strings">Stackoverflow:
     * Escape single quites within single quoted string</a>
     *
     * @return a String with escaped singles quotes
     */
    private String escapeSpecialCharacters(final String unescapedString) {
        return unescapedString.replace("'", "'\"'\"'")
            .replace("\n", "'\"\\n\"'")
            .replace("\t", "'\"\\t\"'")
            .replace(" ", "'\" \"'");
    }

    /**
     * Invokes the Management Bus.
     */
    private Object invokeManagementBusEngine(final Map<String, String> paramsMap,
                                             final Map<String, Object> headers) {
        LOG.debug("Invoking the Management Bus...");

        final ProducerTemplate template = camelContext.createProducerTemplate();
        final Object response = template.requestBodyAndHeaders("bean:managementBusService?method=invokeIA", paramsMap, headers);
        LOG.debug("Invocation finished: {}", response);
        return response;
    }

    @Override
    public List<String> getSupportedTypes() {
        return typesHandler.getSupportedTypes().stream().map(QName::toString).collect(Collectors.toList());
    }
}
