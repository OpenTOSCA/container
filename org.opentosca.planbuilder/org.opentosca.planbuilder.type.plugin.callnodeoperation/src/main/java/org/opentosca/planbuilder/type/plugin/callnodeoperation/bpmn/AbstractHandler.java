package org.opentosca.planbuilder.type.plugin.callnodeoperation.bpmn;

import org.opentosca.planbuilder.model.plan.bpmn.BPMNScope;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// TODO: refactor with PropertyVariableHandler
public abstract class AbstractHandler {
    public static final String INPUT_PREFIX = "Input_";
    public static final String SERVICETEMPLATE_GETINPUT = "get_input:";
    public static final String OUTPUT_PARAM_NAMES = "OutputParamNames";
    public static final String INPUT_PARAM_NAMES = "InputParamNames";

    // "get_input: DockerEngineURL" or "get_input:DockerEngineURL" -> "${DockerEngineURL}"
    public static String parsePropertyValueWithGetInput(String propValue) {
        if (propValue.startsWith(SERVICETEMPLATE_GETINPUT)) {
            String postString = propValue.substring(SERVICETEMPLATE_GETINPUT.length());
            // space handling
            int i = 0;
            while (i < postString.length() && postString.charAt(i) == ' ') {
                i += 1;
            }
            return "${" + postString.substring(i) + "}";
        }
        return propValue;
    }

    /**
     * collecting the properties into a single string with delimiter ',' and adds as an input parameter
     * @param properties
     * @param bpmnTask
     */
    public static void createOutputParameterNamesFromProperties(String[] properties, BPMNScope bpmnTask) {
        StringBuilder sb = new StringBuilder();
        for (String outputName : properties) {
            // TODO: consider using hashset, since we are not setting output value
            sb.append(outputName + ",");
        }

        // remove last ","
        sb.deleteCharAt(sb.length() - 1);
        bpmnTask.addInputparameter(OUTPUT_PARAM_NAMES, sb.toString());
    }

    /**
     * creates input parameter name and value while collecting the non-null properties name
     * @param properties
     * @param propMap
     * @param bpmnTask
     * @return list of non-null properties name
     */
    public static List<String> createInputParameterFromProperties(String[] properties, Map<String, String> propMap, BPMNScope bpmnTask) {
        List<String> propList = new ArrayList<>();
        for (String propName : properties) {
            if (propMap.containsKey(propName) && propMap.get(propName) != null) {
                String inputValue = propMap.get(propName);
                // need to avoid setting empty input
                if (inputValue.isEmpty()) {
                    continue;
                }
                String inputVariableName = INPUT_PREFIX + propName;
                String inputVariableValue = parsePropertyValueWithGetInput(inputValue);
                bpmnTask.addInputparameter(inputVariableName, inputVariableValue);
                propList.add(propName);
            }
        }
        return propList;
    }

    public static void collectPropAsInputParameter(List<String> inputPropList, BPMNScope bpmnTask) {
        StringBuilder sb = new StringBuilder();
        for (String prop : inputPropList) {
            sb.append(prop);
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        bpmnTask.addInputparameter(INPUT_PARAM_NAMES, sb.toString());
    }
}
