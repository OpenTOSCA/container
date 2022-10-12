import java.util.logging.Logger

Logger logger = Logger.getLogger("ActivateDataObject")
def dataObject = execution.getVariable('DataObject')
logger.info("======== Executing DataObject.groovy with exec ID: ${execution.id} for DataObject ${dataObject} ========")
def propertiesNames = execution.getVariable('PropertiesNames').split(",")
final String propertiesAccessPrefix = 'Properties.'

for (int i in 0..propertiesNames.size() - 1) {
    // to skip the case when no properties exists
    if (!propertiesNames[i].contains('PropertiesNamesToSet')) {
        def property = propertiesAccessPrefix + propertiesNames[i]
        def value = execution.getVariable(property)
        if (propertiesNames[i] == "instanceDataAPIUrl" || propertiesNames[i] == "CorrelationID") {
            value = execution.getVariable(propertiesNames[i])
        }
        if (propertiesNames[i] == "containerApiAddress") {
            value = execution.getVariable("instanceDataAPIUrl").split('/csars')[0]
        }
        logger.info("======== Property " + property + "has value " + value)
        // schema: DataObjectReferenceId.Properties.PropertyName
        property = dataObject + "." + property
        execution.setVariable(property, value)
    }
}

logger.info("Created execution variables: " + execution.getVariableNames())
