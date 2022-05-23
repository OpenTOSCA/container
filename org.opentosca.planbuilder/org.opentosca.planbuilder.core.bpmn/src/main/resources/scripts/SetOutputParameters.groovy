def outputParameterNames = execution.getVariable('OutputParameterNames').split(",")
final String OUTPUT = "Output."
final String DATAOBJECTREFRENCE = "DataObjectReference_"

for (int i in 0..outputParameterNames.size() - 1) {
    // to skip the case when no output parameter exists
    if (!outputParameterNames[i].contains('OutputParameterNamesToSet')) {
        def outputParamName = OUTPUT + outputParameterNames[i]
        def value = execution.getVariable(outputParamName)
        // this is a property mapping output parameter
        if (value.contains(DATAOBJECTREFRENCE) && value.contains("+")) {
            String[] parts = value.split("+")
            for (String part : parts) {
                if (part.contains(DATAOBJECTREFRENCE)) {
                    String valueOfPart = execution.getVariable(part)
                    value = value.replace(part, valueOfPart)
                }
            }
        }
        execution.setVariable(outputParameterNames[i], value)
    }
}
