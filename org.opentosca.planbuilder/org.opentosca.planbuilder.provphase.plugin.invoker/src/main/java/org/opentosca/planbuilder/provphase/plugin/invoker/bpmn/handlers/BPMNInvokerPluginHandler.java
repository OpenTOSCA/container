package org.opentosca.planbuilder.provphase.plugin.invoker.bpmn.handlers;

import java.io.IOException;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.winery.model.tosca.TNodeTemplate;

import org.opentosca.planbuilder.core.bpmn.context.BPMNPlanContext;
import org.opentosca.planbuilder.core.bpmn.handlers.BPMNSubprocessHandler;
import org.opentosca.planbuilder.core.plugins.context.Variable;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNDataObject;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNPlan;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNSubprocess;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNSubprocessType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class BPMNInvokerPluginHandler {

    private final static Logger LOG = LoggerFactory.getLogger(org.opentosca.planbuilder.provphase.plugin.invoker.bpmn.handlers.BPMNInvokerPluginHandler.class);

    private final BPMNSubprocessHandler bpmnSubprocessHandler;
    protected static final String ServiceInstanceURLVarKeyword = "ServiceInstanceURL";

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

            // set input param names and values
            for (Map.Entry<String, Variable> entry : internalExternalPropsInput.entrySet()) {
                if (inputParamNames.toString().equals("") && inputParamValues.toString().equals("")) {
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

            BPMNSubprocess subprocess = context.getSubprocessElement();
            subprocess.setHostingNodeTemplate(templateId);
            BPMNPlan buildPlan = subprocess.getBuildPlan();
            String preState = InstanceStates.getOperationPreState(operationName);
            final BPMNSubprocess createNodeOperationTask = bpmnSubprocessHandler.createBPMNSubprocessWithinSubprocess(subprocess, BPMNSubprocessType.CALL_NODE_OPERATION_TASK);
            final BPMNSubprocess setPreState = bpmnSubprocessHandler.createBPMNSubprocessWithinSubprocess(subprocess, BPMNSubprocessType.SET_ST_STATE);
            setPreState.setInstanceState(preState);
            subprocess.addTaskToSubproces(setPreState);
            subprocess.addTaskToSubproces(createNodeOperationTask);
            boolean hasNodeOperation = false;

            for (BPMNSubprocess sub : context.getSubprocessElement().getSubprocessBPMNSubprocess()) {
                if (sub.getSubprocessType() == (BPMNSubprocessType.CALL_NODE_OPERATION_TASK)) {
                    sub.setInterfaceVariable(interfaceName);
                    sub.setOperation(operationName);
                    sub.setInputparamnames(inputParamNames.toString());

                    sub.setInputparamvalues(inputParamValues.toString());
                    sub.setOutputparamnames(outputParamNames.toString());
                    sub.setOutputparamvalues(outputParamValues.toString());
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
                createNodeOperationTask2.setInputparamnames(inputParamNames.toString());
                createNodeOperationTask2.setInputparamvalues(inputParamValues.toString());
                createNodeOperationTask2.setOutputparamnames(outputParamNames.toString());
                createNodeOperationTask2.setOutputparamvalues(outputParamValues.toString());
            }

            final BPMNSubprocess setPostState = bpmnSubprocessHandler.createBPMNSubprocessWithinSubprocess(subprocess, BPMNSubprocessType.SET_ST_STATE);
            String postState = InstanceStates.getOperationPostState(operationName);
            setPostState.setInstanceState(postState);
            subprocess.addTaskToSubproces(setPostState);
        }

        return true;
    }
}

