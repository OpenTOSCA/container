package org.opentosca.planbuilder.type.plugin.callnodeoperation.bpmn;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.opentosca.container.core.model.ModelUtils;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNPlan;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * handle BPMN Activity for Docker Engine NodeTemplate
 * specifically  generating Input/Output Parameter for script "DataObject.groovy"
 */
public class DockerEngineHandler {

    private static final Logger LOG = LoggerFactory.getLogger(DockerEngineHandler.class);

    public static final String INPUT_PREFIX = "Input_";
    public static final String SERVICETEMPLATE_GETINPUT = "get_input:";
    /**
     * check the nodetemplate is a docker engine
     * @param nodeTemplate
     * @param csar
     * @return
     */
    public boolean isProvisionableByDockerEngine(TNodeTemplate nodeTemplate, Csar csar) {
        return org.opentosca.container.core.convention.Utils.isSupportedDockerEngineNodeType(nodeTemplate.getType());
    }

    /**
     * create BPMN script task associated DataObject.groovy
     * the main purpose of script is to declare the properties of docker engine as global variable
     * @param activateDataObjectTask
     * @param csar
     * @return
     */
    public boolean handleCreate(BPMNScope activateDataObjectTask, Csar csar) {
        // Step-1: Read all property map
        BPMNPlan plan = activateDataObjectTask.getBuildPlan();
        TNodeTemplate nodeTemplate = activateDataObjectTask.getNodeTemplate();
        // default input variableName: NodeInstanceURL, DataObject
        activateDataObjectTask.addInputparameter("NodeInstanceURL", plan.getNodeTemplateInstanceUrlVariableName(nodeTemplate));

        // make the key with prefix Input -> create as VariableName
        Map<String, String> propMap = ModelUtils.asMap(nodeTemplate.getProperties());
        // make the value VariableValue

        String[] engineProperties = new String[] {
            DockerEngineConstants.PROPERTY_DOCKER_ENGINE_URL,
            DockerEngineConstants.PROPERTY_DOCKER_ENGINE_CERTIFICATE
        };

        createInputParameterFromProperties(engineProperties, propMap, activateDataObjectTask);

        return true;
    }

    private void createInputParameterFromProperties(String[] properties, Map<String, String> propMap, BPMNScope activateDataObjectTask) {
        for (String propName : properties) {
            if (propMap.containsKey(propName)) {
                String inputVariableName = INPUT_PREFIX + propName;
                String inputVariableValue = parsePropertyValueWithGetInput(propMap.get(propName));
                // no need to avoid setting empty input
                activateDataObjectTask.addInputparameter(inputVariableName, inputVariableValue);
            }
        }
    }

    // "get_input: DockerEngineURL" -> "${DockerEngineURL}"
    private String parsePropertyValueWithGetInput(String propValue) {
        if (propValue.startsWith(SERVICETEMPLATE_GETINPUT)) {
            return "${" + propValue.substring(SERVICETEMPLATE_GETINPUT.length()) + "}";
        }
        return propValue;
    }
}

