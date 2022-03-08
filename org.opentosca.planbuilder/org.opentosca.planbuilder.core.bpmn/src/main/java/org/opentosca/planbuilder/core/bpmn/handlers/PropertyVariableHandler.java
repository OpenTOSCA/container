package org.opentosca.planbuilder.core.bpmn.handlers;

import org.opentosca.planbuilder.model.plan.bpmn.BPMNScope;

import java.util.Map;

public class PropertyVariableHandler {

    // fixed with groovy script: "SetProperties"
    public static final String INPUT_PREFIX = "Input_";

    // fixed in service template
    public static final String SERVICETEMPLATE_GETINPUT = "get_input: ";

    private static final String INPUT_PROPERTIES_NAME = "Properties";


    /**
     * creates input parameter name, value pair by parsing all non-empty value map
     * add additional input parameter name INPUT_PROPERTIES_NAME by collecting all property name
     * @param propMap
     * @param bpmnScope
     */
    public void createInputParameterFromProperties(Map<String, String> propMap, BPMNScope bpmnScope) {
        StringBuilder sb = new StringBuilder();

        for (String propName : propMap.keySet()) {
            // collect all properties
            sb.append(propName + ",");

            // no need to avoid setting empty input because it may override runtime output variable
            if (!propMap.get(propName).isEmpty()) {
                String propValue = propMap.get(propName);
                String inputVariableName = INPUT_PREFIX + propName;
                String inputVariableValue = parsePropertyValueWithGetInput(propMap.get(propName));

                bpmnScope.addInputparameter(inputVariableName, inputVariableValue);
            }
        }

        // remove last ","
        sb.deleteCharAt(sb.length() - 1);
        bpmnScope.addInputparameter(INPUT_PROPERTIES_NAME, sb.toString());
    }

    // "get_input: DockerEngineURL" -> "${DockerEngineURL}"
    public static String parsePropertyValueWithGetInput(String propValue) {
        if (propValue.startsWith(SERVICETEMPLATE_GETINPUT)) {
            return "${" + propValue.substring(SERVICETEMPLATE_GETINPUT.length()) + "}";
        }
        return propValue;
    }
}
