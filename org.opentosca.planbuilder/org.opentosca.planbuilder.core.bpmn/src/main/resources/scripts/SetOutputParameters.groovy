import java.util.logging.Logger

Logger logger = Logger.getLogger("SetOutputParameters")
logger.info("======== Executing SetOutputParameters.groovy with exec ID: ${execution.id} ========")

def outputParameterNames = execution.getVariable('OutputParameterNames').split(",")
final String OUTPUT = "Output."
final String DATAOBJECTREFERENCE = "DataObjectReference_"

for (int i in 0..outputParameterNames.size() - 1) {
    // to skip the case when no output parameter exists
    if (!outputParameterNames[i].contains('OutputParameterNamesToSet')) {
        def outputParamName = OUTPUT + outputParameterNames[i]
        def value = execution.getVariable(outputParamName)
        def outputParameterValue = ""
        // this is a property mapping output parameter
        if (value.contains(DATAOBJECTREFERENCE) && value.contains("+")) {
            String[] parts = value.split("\\+")
            for (String part : parts) {
                String valueOfPart = ""
                part = part.replace("'", "")
                part = part.trim()
                if (part.contains(DATAOBJECTREFERENCE)) {
                    valueOfPart = execution.getVariable(part)
                    outputParameterValue += valueOfPart
                } else {
                    outputParameterValue += part
                }
            }
            logger.info("Set outputparameter " + outputParameterNames[i] + " to " + outputParameterValue)
            execution.setVariable(outputParameterNames[i], outputParameterValue)
        } else {
            logger.info("Set outputparameter " + outputParameterNames[i] + " to " + value)
            execution.setVariable(outputParameterNames[i], value)
        }
    }
}
