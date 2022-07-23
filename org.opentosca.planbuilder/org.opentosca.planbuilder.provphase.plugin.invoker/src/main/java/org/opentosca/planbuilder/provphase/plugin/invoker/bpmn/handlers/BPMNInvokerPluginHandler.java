package org.opentosca.planbuilder.provphase.plugin.invoker.bpmn.handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.winery.model.tosca.TArtifactReference;
import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TParameter;

import org.opentosca.container.core.convention.Interfaces;
import org.opentosca.container.core.convention.Properties;
import org.opentosca.container.core.model.ModelUtils;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.bpmn.context.BPMNPlanContext;
import org.opentosca.planbuilder.core.bpmn.handlers.BPMNSubprocessHandler;
import org.opentosca.planbuilder.core.plugins.context.PropertyVariable;
import org.opentosca.planbuilder.core.plugins.context.Variable;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNDataObject;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNPlan;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNSubprocess;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNSubprocessType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class BPMNInvokerPluginHandler {

    private final static Logger LOG = LoggerFactory.getLogger(org.opentosca.planbuilder.provphase.plugin.invoker.bpmn.handlers.BPMNInvokerPluginHandler.class);

    private final BPMNSubprocessHandler bpmnSubprocessHandler;
    protected static final String ServiceInstanceURLVarKeyword = "ServiceInstanceURL";
    private static final String suffixActivity = "_provisioning_activity";

    public BPMNInvokerPluginHandler() {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            docFactory.setNamespaceAware(true);
            this.bpmnSubprocessHandler = new BPMNSubprocessHandler();
        } catch (final ParserConfigurationException e) {
            LOG.error("Couldn't initialize ResourceHandler", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * @param context                     Plan context
     * @param templateId                  template id
     * @param isNodeTemplate              Node template if true, Relationship template if false
     * @param operationName               operation
     * @param interfaceName               interface
     * @param internalExternalPropsInput  input params and values
     * @param internalExternalPropsOutput output params and values
     * @param elementToAppendTo           not used
     * @return true if successful
     */
    public boolean handle(final BPMNPlanContext context, final TNodeTemplate templateId, final boolean isNodeTemplate,
                          final String operationName, final String interfaceName,
                          final Map<String, Variable> internalExternalPropsInput,
                          final Map<String, Variable> internalExternalPropsOutput,
                          Element elementToAppendTo) throws IOException, SAXException {

        // Main execution of provisioning function inside this method
        if (isNodeTemplate) {

            // build param inputs for node operation
            StringBuilder inputParamNames = new StringBuilder();
            StringBuilder inputParamValues = new StringBuilder();
            StringBuilder outputParamNames = new StringBuilder();
            StringBuilder outputParamValues = new StringBuilder();
            BPMNSubprocess subprocess = context.getSubprocessElement();
            subprocess.setHostingNodeTemplate(templateId);
            BPMNPlan buildPlan = subprocess.getBuildPlan();
            // set input param names and values
            System.out.println("INVOKER" + templateId.getId());
            for (Map.Entry<String, Variable> entry : internalExternalPropsInput.entrySet()) {
                LOG.info(entry.getValue().getVariableName());
                System.out.println(entry.getKey());
                String parameterValue = entry.getValue().getVariableName();
                if (entry.getValue().getVariableName().contains("toscaProperty")) {
                    String removeToscaProperty = parameterValue.split("_toscaProperty")[0];
                    //LOG.info("removeToscaProperty: {}", removeToscaProperty);
                    //LOG.info("lastIndexCSAR: {}", parameterValue.lastIndexOf(context.getCSARFileName()) + 1);
                    //LOG.info("SeervicetemplateName: {}", context.getSubprocessElement().getBuildPlan().getServiceTemplate().getName());
                    String serviceTemplateName = context.getSubprocessElement().getBuildPlan().getServiceTemplate().getName();
                    //LOG.info("CSAR NAME {}", context.getCSARFileName());
                    String propertyToSearchFromDataObject = removeToscaProperty.substring(0, parameterValue.lastIndexOf(context.getSubprocessElement().getBuildPlan().getServiceTemplate().getName()) + serviceTemplateName.length() + 1);
                    // LOG.info("propertyToSearchFromDataObject: {}", propertyToSearchFromDataObject);
                    removeToscaProperty = removeToscaProperty.split(propertyToSearchFromDataObject)[1];
                    //LOG.info("split after CSAR: {}", removeToscaProperty);
                    //LOG.info("lastIndexOfUnderScore: {}", removeToscaProperty.lastIndexOf("_"));
                    String nodeTemplateIdToFetchDataObject = removeToscaProperty.substring(0, removeToscaProperty.lastIndexOf("_"));
                    LOG.info("nodeTemplateIdToFetchDataObject: {}", nodeTemplateIdToFetchDataObject);
                    String propertyOfDataObject = removeToscaProperty.substring(removeToscaProperty.lastIndexOf("_") + 1).trim();
                    LOG.info("propertyOfDataObject : {}", propertyOfDataObject);
                    for (BPMNDataObject nodeDataObject : buildPlan.getDataObjectsList()) {
                        LOG.info("DATAOBJECT ID");
                        LOG.info(nodeDataObject.getId());
                        if (nodeDataObject.getDataObjectType() == BPMNSubprocessType.DATA_OBJECT_NODE && nodeDataObject.getId().contains(nodeTemplateIdToFetchDataObject)) {
                            String lastSuffixCut = nodeDataObject.getId().split(nodeTemplateIdToFetchDataObject)[1];
                            LOG.info("lastSuffixCut : {}", lastSuffixCut);
                            if (lastSuffixCut.equals(suffixActivity)) {
                                for (String property : nodeDataObject.getProperties()) {
                                    String propertyName = property.split("#")[0];
                                    LOG.info("propertyName {}", propertyName);
                                    LOG.info("propertyName Size {}", propertyName.length());
                                    LOG.info("propertyOfDataObject {}", propertyOfDataObject);
                                    LOG.info("pD Size {}", propertyOfDataObject.length());
                                    if (propertyName.equals(propertyOfDataObject)) {
                                        LOG.info("GEHT HIER REIN");
                                        String propertyValue = property.split("#")[1];
                                        if (propertyValue.startsWith("G")) {
                                            LOG.info("GEHT HIER REING");
                                            propertyValue = propertyValue.replaceFirst("G", "");
                                        }
                                        // propertyValue = "String!" + propertyValue;
                                        propertyValue = "VALUE!" + "DataObjectReference_" + nodeDataObject.getId() + ".Properties." + propertyName;
                                        if (inputParamNames.toString().equals("") && inputParamValues.toString().equals("")) {
                                            LOG.info("GEHT HIER REIN3");
                                            inputParamNames.append(entry.getKey());
                                            inputParamValues.append(propertyValue);
                                        } else {
                                            LOG.info("GEHT HIER REIN4");
                                            inputParamNames.append(",").append(entry.getKey());
                                            inputParamValues.append(",").append(propertyValue);
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else if (inputParamNames.toString().equals("") && inputParamValues.toString().equals("")) {
                    inputParamNames.append(entry.getKey());
                    inputParamValues.append(entry.getValue().getVariableName());
                } else {
                    inputParamNames.append(",").append(entry.getKey());
                    inputParamValues.append(",").append(entry.getValue().getVariableName());
                }
            }

            //set output param names and values
            for (Map.Entry<String, Variable> entry : internalExternalPropsOutput.entrySet()) {
                if (outputParamNames.toString().equals("") && outputParamValues.toString().equals("")) {
                    outputParamNames.append(entry.getKey());
                    outputParamValues.append(entry.getValue().getVariableName());
                } else {
                    outputParamNames.append(",").append(entry.getKey());
                    outputParamValues.append(",").append(entry.getValue().getVariableName());
                }
            }

            String preState = InstanceStates.getOperationPreState(operationName);
            final BPMNSubprocess createNodeOperationTask = bpmnSubprocessHandler.createBPMNSubprocessWithinSubprocess(subprocess, BPMNSubprocessType.CALL_NODE_OPERATION_TASK);
            final BPMNSubprocess setPreState = bpmnSubprocessHandler.createBPMNSubprocessWithinSubprocess(subprocess, BPMNSubprocessType.SET_ST_STATE);
            setPreState.setInstanceState(preState);
            subprocess.addTaskToSubprocess(setPreState);
            subprocess.addTaskToSubprocess(createNodeOperationTask);
            boolean hasNodeOperation = false;

            for (BPMNSubprocess sub : context.getSubprocessElement().getSubprocessBPMNSubprocess()) {
                if (sub.getSubprocessType() == (BPMNSubprocessType.CALL_NODE_OPERATION_TASK)) {
                    sub.setInterfaceVariable(interfaceName);
                    sub.setOperation(operationName);
                    sub.setInputParameterNames(inputParamNames.toString());

                    sub.setInputParameterValues(inputParamValues.toString());
                    sub.setOutputParameterNames(outputParamNames.toString());
                    sub.setOutputParameterValues(outputParamValues.toString());
                    for (BPMNDataObject dataObject : buildPlan.getDataObjectsList()) {
                        if (dataObject.getDataObjectType() == BPMNSubprocessType.DATA_OBJECT_ST) {
                            for (String property : dataObject.getProperties()) {
                                if (property.contains(ServiceInstanceURLVarKeyword)) {
                                    sub.setServiceInstanceURL(property);
                                }
                            }
                        }
                    }
                    hasNodeOperation = true;
                }
            }
            if (!hasNodeOperation) {
                final BPMNSubprocess createNodeOperationTask2 = bpmnSubprocessHandler.createBPMNSubprocessWithinSubprocess(subprocess, BPMNSubprocessType.CALL_NODE_OPERATION_TASK);
                createNodeOperationTask2.setInterfaceVariable(interfaceName);
                createNodeOperationTask2.setOperation(operationName);
                createNodeOperationTask2.setInputParameterNames(inputParamNames.toString());
                createNodeOperationTask2.setInputParameterValues(inputParamValues.toString());
                createNodeOperationTask2.setOutputParameterNames(outputParamNames.toString());
                createNodeOperationTask2.setOutputParameterValues(outputParamValues.toString());
            }

            final BPMNSubprocess setPostState = bpmnSubprocessHandler.createBPMNSubprocessWithinSubprocess(subprocess, BPMNSubprocessType.SET_ST_STATE);
            String postState = InstanceStates.getOperationPostState(operationName);
            setPostState.setInstanceState(postState);
            subprocess.addTaskToSubprocess(setPostState);
        }

        return true;
    }

    /**
     *  add support for BPMN PrePhase plugin, currently not used / functional
     *  all important information gets conveyed to the invoker plugin handle method via the inputparameter map,
     *  may clash with current implementation due to differences compared to the current usage!!!
     *  case handling might be necessary ...
     */
    public boolean handleArtifactReferenceUpload(final TArtifactReference ref,
                                                 final BPMNPlanContext templateContext, final PropertyVariable serverIp,
                                                 final PropertyVariable sshUser, final PropertyVariable sshKey,
                                                 final TNodeTemplate infraTemplate,
                                                 final Element elementToAppendTo) throws Exception {
        LOG.debug("Handling DA " + ref.getReference());

        if (Objects.isNull(serverIp)) {
            LOG.error("Unable to upload artifact with server IP equal to null.");
            return false;
        }

        /*
         * Construct all needed data (paths, url, scripts)
         */
        // TODO /home/ec2-user/ or ~ is a huge assumption
        // the path to the file on the ubuntu vm being uploaded
        final String ubuntuFilePath = "~/" + templateContext.getCSARFileName() + "/" + ref.getReference();

        //final String ubuntuFilePathVarName = "ubuntuFilePathVar" + templateContext.getIdForNames();
        final Variable ubuntuFilePathVar = new Variable(ubuntuFilePath);
            //templateContext.createGlobalStringVariable(ubuntuFilePathVarName, ubuntuFilePath);
        // the folder which has to be created on the ubuntu vm
        final String ubuntuFolderPathScript = "sleep 1 && mkdir -p " + fileReferenceToFolder(ubuntuFilePath);

        //final String containerAPIAbsoluteURIXPathQuery =
        //    this.bpelFrags.createXPathQueryForURLRemoteFilePath(ref.getReference());
        //final String containerAPIAbsoluteURIVarName = "containerApiFileURL" + templateContext.getIdForNames();

        final String containerAPIAbsoluteURIXPathQuery = "string(concat(substring-before($input.payload//*[local-name()='instanceDataAPIUrl']/text(),'/servicetemplates'),'/content/"
            + ref.getReference() + "'))";
        /*
         * create a string variable with a complete URL to the file we want to upload
         */

        //todo check if this is correct and really works !!!
        final Variable containerAPIAbsoluteURIVar = new Variable(containerAPIAbsoluteURIXPathQuery);

        //    templateContext.createGlobalStringVariable(containerAPIAbsoluteURIVarName, "");

        /*
        try {
            Node assignNode =
                loadAssignXpathQueryToStringVarFragmentAsNode("assign" + templateContext.getIdForNames(),
                    containerAPIAbsoluteURIXPathQuery,
                    containerAPIAbsoluteURIVar.getVariableName());
            assignNode = templateContext.importNode(assignNode);

            elementToAppendTo.appendChild(assignNode);
        } catch (final IOException e) {
            LOG.error("Couldn't read internal file", e);
            return false;
        } catch (final SAXException e) {
            LOG.error("Couldn't parse internal xml file");
            return false;
        }
         */

        // create the folder the file must be uploaded into and upload the file afterwards
        //final String mkdirScriptVarName = "mkdirScript" + templateContext.getIdForNames();
        final Variable mkdirScriptVar = new Variable(ubuntuFolderPathScript);
            //templateContext.createGlobalStringVariable(mkdirScriptVarName, ubuntuFolderPathScript);
        final Map<String, Variable> runScriptRequestInputParams = new HashMap<>();
        runScriptRequestInputParams.put("Script", mkdirScriptVar);
        final List<String> runScriptInputParams = getRunScriptParams(infraTemplate, templateContext.getCsar());

        final Map<String, Variable> transferFileRequestInputParams = new HashMap<>();
        transferFileRequestInputParams.put("TargetAbsolutePath", ubuntuFilePathVar);
        transferFileRequestInputParams.put("SourceURLorLocalPath", containerAPIAbsoluteURIVar);
        final List<String> transferFileInputParams = getTransferFileParams(infraTemplate, templateContext.getCsar());

        switch (serverIp.getPropertyName()) {
            case Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_CONTAINERIP:

                // create the folder
                if (runScriptInputParams.contains(serverIp.getPropertyName())) {
                    runScriptRequestInputParams.put(serverIp.getPropertyName(), serverIp);
                }
                this.handle(templateContext, infraTemplate, true, "runScript", "ContainerManagementInterface",
                    runScriptRequestInputParams, new HashMap<String, Variable>(), elementToAppendTo);

                // transfer the file
                if (transferFileInputParams.contains(serverIp.getPropertyName())) {
                    transferFileRequestInputParams.put(serverIp.getPropertyName(), serverIp);
                }
                this.handle(templateContext, infraTemplate, true,
                    Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_TRANSFERFILE,
                    "ContainerManagementInterface", transferFileRequestInputParams,
                    new HashMap<>(), elementToAppendTo);
                break;
            case Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMIP:
            case Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_RASPBIANIP:
                // create the folder
                if (runScriptInputParams.contains(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMIP)) {
                    runScriptRequestInputParams.put(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMIP, serverIp);
                }
                if (sshUser != null && runScriptInputParams.contains("VMUserName")) {
                    runScriptRequestInputParams.put("VMUserName", sshUser);
                }
                if (sshKey != null && runScriptInputParams.contains("VMPrivateKey")) {
                    runScriptRequestInputParams.put("VMPrivateKey", sshKey);
                }
                this.handle(templateContext, infraTemplate, true, "runScript",
                    Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM, runScriptRequestInputParams,
                    new HashMap<>(), elementToAppendTo);

                // transfer the file
                if (transferFileInputParams.contains(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMIP)) {
                    transferFileRequestInputParams.put(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMIP, serverIp);
                }
                if (sshUser != null && transferFileInputParams.contains("VMUserName")) {
                    transferFileRequestInputParams.put("VMUserName", sshUser);
                }
                if (sshKey != null && transferFileInputParams.contains("VMPrivateKey")) {
                    transferFileRequestInputParams.put("VMPrivateKey", sshKey);
                }
                this.handle(templateContext, infraTemplate, true,
                    Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_TRANSFERFILE,
                    Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM, transferFileRequestInputParams,
                    new HashMap<>(), elementToAppendTo);
                break;
            default:
                return false;
        }

        return true;
    }

    /**
     * Removes trailing slashes
     *
     * @param ref a path
     * @return a String without trailing slashes
     */
    private String fileReferenceToFolder(String ref) {
        LOG.debug("Getting ref to change to folder ref: " + ref);

        final int lastIndexSlash = ref.lastIndexOf("/");
        final int lastIndexDot = ref.lastIndexOf(".");
        if (lastIndexSlash < lastIndexDot) {
            ref = ref.substring(0, lastIndexSlash);
        }
        LOG.debug("Returning ref: " + ref);
        return ref;
    }

    private List<String> getRunScriptParams(final TNodeTemplate nodeTemplate, Csar csar) {
        final List<String> inputParams = new ArrayList<>();
        List<TInterface> interfaces = ModelUtils.findNodeType(nodeTemplate, csar).getInterfaces();
        if (interfaces != null) {
            for (final TInterface tInterface : interfaces) {
                for (final TOperation op : tInterface.getOperations()) {
                    if (op.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_RUNSCRIPT)) {
                        for (final TParameter param : op.getInputParameters()) {
                            inputParams.add(param.getName());
                        }
                    }
                }
            }
        }

        return inputParams;
    }

    private List<String> getTransferFileParams(final TNodeTemplate nodeTemplate, Csar csar) {
        final List<String> inputParams = new ArrayList<>();
        List<TInterface> interfaces = ModelUtils.findNodeType(nodeTemplate, csar).getInterfaces();
        if (interfaces != null) {
            for (final TInterface tInterface : interfaces) {
                for (final TOperation op : tInterface.getOperations()) {
                    if (op.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_TRANSFERFILE)) {
                        for (final TParameter param : op.getInputParameters()) {
                            inputParams.add(param.getName());
                        }
                    }
                }
            }
        }

        return inputParams;
    }
}

