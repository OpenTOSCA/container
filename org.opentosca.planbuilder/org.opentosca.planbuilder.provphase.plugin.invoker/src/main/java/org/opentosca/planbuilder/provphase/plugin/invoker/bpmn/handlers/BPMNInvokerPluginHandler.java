package org.opentosca.planbuilder.provphase.plugin.invoker.bpmn.handlers;

import java.io.IOException;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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
import org.opentosca.planbuilder.core.bpmn.fragments.BPMNProcessFragments;

public class BPMNInvokerPluginHandler {

    private final static Logger LOG = LoggerFactory.getLogger(org.opentosca.planbuilder.provphase.plugin.invoker.bpmn.handlers.BPMNInvokerPluginHandler.class);

    private final BPMNProcessFragments processFragments;
    private BPMNSubprocessHandler bpmnSubprocessHandler;
    protected static final String ServiceInstanceURLVarKeyword = "OpenTOSCAContainerAPIServiceInstanceURL";

    // @todo hier die inhalte der scopes zusammenbauen?
    public BPMNInvokerPluginHandler() {
        try {

            this.processFragments = new BPMNProcessFragments();
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            docFactory.setNamespaceAware(true);
            this.bpmnSubprocessHandler = new BPMNSubprocessHandler();
        } catch (final ParserConfigurationException e) {
            LOG.error("Couldn't initialize ResourceHandler", e);
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param context Plan context
     * @param templateId template id
     * @param isNodeTemplate Nodetemplate if true, Relationshiptemplate if false
     * @param operationName operation
     * @param interfaceName interface
     * @param internalExternalPropsInput input params and values
     * @param internalExternalPropsOutput ouput params and values
     * @param elementToAppendTo not used
     * @return true if successful
     * @throws IOException
     * @throws SAXException
     */
    public boolean handle(final BPMNPlanContext context, final String templateId, final boolean isNodeTemplate,
                          final String operationName, final String interfaceName,
                          final Map<String, Variable> internalExternalPropsInput,
                          final Map<String, Variable> internalExternalPropsOutput,
                          Element elementToAppendTo) throws IOException, SAXException {

        // Main execution of provisioning function inside this method
        // Die "große Hauptmethode in die alles rein muss und wo alles geregelt wird"

        // handle node template
        if (isNodeTemplate) {

            // build param inputs for nodeoperation
            String inputParamNames = "";
            String inputParamValues = "";
            String outputParamNames = "";
            String outputParamValues = "";

            // set input param names and values
            for (Map.Entry<String, Variable> entry : internalExternalPropsInput.entrySet()) {
                if(inputParamNames.equals("") && inputParamValues.equals("")){
                    inputParamNames = inputParamNames + entry.getKey();
                    inputParamValues = inputParamValues + entry.getValue().getVariableName();
                }else{
                    inputParamNames = inputParamNames + "," + entry.getKey();
                    inputParamValues = inputParamValues + "," + entry.getValue().getVariableName();
                }
            }

            //set output param names and values
            for (Map.Entry<String, Variable> entry : internalExternalPropsOutput.entrySet()) {
                if(outputParamNames.equals("") && outputParamValues.equals("")){
                    outputParamNames = outputParamNames + entry.getKey();
                    outputParamValues = outputParamValues + entry.getValue().getVariableName();
                }else{
                    outputParamNames = outputParamNames + "," + entry.getKey();
                    outputParamValues = outputParamValues + "," + entry.getValue().getVariableName();
                }
            }

            LOG.info("kurz vor ersetzen im invoker");

            BPMNSubprocess subprocess = context.getSubprocessElement();
            BPMNPlan buildPlan = ((BPMNSubprocess) subprocess).getBuildPlan();
            String preState = InstanceStates.getOperationPreState(operationName);
            final BPMNSubprocess setPreState = bpmnSubprocessHandler.createBPMNSubprocessWithinSubprocess(subprocess, BPMNSubprocessType.SET_ST_STATE);
            setPreState.setInstanceState(preState);
            subprocess.addTaskToSubproces(setPreState);
            final BPMNSubprocess createNodeOperationTask = bpmnSubprocessHandler.createBPMNSubprocessWithinSubprocess((BPMNSubprocess) subprocess, BPMNSubprocessType.CALL_NODE_OPERATION_TASK);
            //final BPMNSubprocess setStateToNodeOperationflow = bpmnSubprocessHandler.createBPMNSubprocessWithinSubprocess((BPMNSubprocess) subprocess, BPMNSubprocessType.SEQUENCE_FLOW2);
            //setStateToNodeOperationflow.setSourceflow(setPreState);
            //setStateToNodeOperationflow.setTargetflow(createNodeOperationTask);

            boolean hasnodeoperation = false;

            for (BPMNSubprocess sub : context.getSubprocessElement().getSubprocessBPMNSubprocess()) {
                LOG.info("in schleife");
                LOG.info(sub.getId());
                if(sub.getSubprocessType() == (BPMNSubprocessType.CALL_NODE_OPERATION_TASK)) {
                    sub.setInterfaceVariable(interfaceName);
                    sub.setOperation(operationName);
                    sub.setInputparamnames(inputParamNames);
                    sub.setInputparamvalues(inputParamValues);
                    sub.setOutputparamnames(outputParamNames);
                    sub.setOutputparamvalues(outputParamValues);
                    for(BPMNDataObject dataObject: buildPlan.getDataObjectsList()){
                        if(dataObject.getDataObjectType()==BPMNSubprocessType.DATA_OBJECT_ST){
                            for(String property : dataObject.getProperties()){
                                if(property.contains(ServiceInstanceURLVarKeyword)){
                                    sub.setServiceInstanceURL(property);
                                }
                            }
                        }
                    }
                    hasnodeoperation = true;
                }
            }
            if(!hasnodeoperation) {
                //BPMNSubprocess subprocess = context.getSubprocessElement();
                //BPMNPlan buildPlan = ((BPMNSubprocess) sub).getBuildPlan();
                // eventuell unnötig
                final BPMNSubprocess createNodeOperationTask2 = bpmnSubprocessHandler.createBPMNSubprocessWithinSubprocess((BPMNSubprocess) subprocess, BPMNSubprocessType.CALL_NODE_OPERATION_TASK);

                createNodeOperationTask2.setInterfaceVariable(interfaceName);
                createNodeOperationTask2.setOperation(operationName);
                createNodeOperationTask2.setInputparamnames(inputParamNames);
                createNodeOperationTask2.setInputparamvalues(inputParamValues);
                createNodeOperationTask2.setOutputparamnames(outputParamNames);
                createNodeOperationTask2.setOutputparamvalues(outputParamValues);
            }

            final BPMNSubprocess setPostState = bpmnSubprocessHandler.createBPMNSubprocessWithinSubprocess(subprocess, BPMNSubprocessType.SET_ST_STATE);
            String postState = InstanceStates.getOperationPostState(operationName);
            setPostState.setInstanceState(postState);
            subprocess.addTaskToSubproces(setPostState);

            //final BPMNSubprocess nodeOperationToSetPoststateflow = bpmnSubprocessHandler.createBPMNSubprocessWithinSubprocess((BPMNSubprocess) subprocess, BPMNSubprocessType.SEQUENCE_FLOW2);
            //nodeOperationToSetPoststateflow.setSourceflow(createNodeOperationTask);
            //setStateToNodeOperationflow.setTargetflow(setPostState);

                /*
                try {

                    //Node setPreStateNode = this.processFragments.createSetStateTask(subprocess, preState);

                    Node childCreateNodeOperation = this.processFragments.createNodeOperation(context.getSubprocessElement(),
                        interfaceName,operationName, inputParamNames, inputParamValues, outputParamNames, outputParamValues);

                    //Node setPostStateNode = this.processFragments.createSetStateTask(subprocess, postState);
                    NodeList subprocesses = context.getTemplateBuildPlan().getBuildPlan().getBpmnDocument().getElementsByTagName("bpmn:subProcess");
                    for (int i = 0; i < subprocesses.getLength(); i++) {
                        Node element = subprocesses.item(i);
                        for (int j = 0; j < element.getAttributes().getLength(); j++) {
                            String id = element.getAttributes().item(j).getTextContent();
                            if (id.equals(subprocess.getId())) {
                                LOG.info("ICH GEH HIER REIN IM DOCKER CONTAINER PLUGIN");
                                Node parent2 = subprocesses.item(i);
                                createNodeOperationTask.setParentProcess(context.getSubprocessElement());
                                //processFragments.addNodeInsideSubprocess(setPreStateNode, parent2, buildPlan);
                                processFragments.addNodeInsideSubprocess(childCreateNodeOperation, parent2, buildPlan);
                                //processFragments.addNodeInsideSubprocess(setPostStateNode, parent2, buildPlan);
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                }

                 */

        // handle relationship template
        } else {

        }

        return true;
    }
}

