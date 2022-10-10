def dataObject = execution.getVariable('DataObject')
println "======== Executing DataObject.groovy with exec ID: ${execution.getId()} for DataObject ${dataObject} ========"
def propertiesNames = execution.getVariable('PropertiesNames').split(",");
final String PROPERTIES = 'Properties.'

for (int i in 0..propertiesNames.size() - 1) {
    // to skip the case when no properties exists
    if (!propertiesNames[i].contains('PropertiesNamesToSet')) {
        def property = PROPERTIES + propertiesNames[i]
        def value = execution.getVariable(property)
        if (propertiesNames[i].equals("instanceDataAPIUrl") || propertiesNames[i].equals("CorrelationID")) {
            value = execution.getVariable(propertiesNames[i])
        }
        if (propertiesNames[i].equals("containerApiAddress")) {
            value = execution.getVariable("instanceDataAPIUrl").split('/csars')[0]
        }
        println "======== Property " + property + "has value " + value
        // schema: DataObjectReferenceId.Properties.PropertyName
        property = dataObject + "." + property;
        execution.setVariable(property, value)
    }
}

println "CREATED EXECUTION VARIABLES:"
println execution.getVariableNames()
//execution.setVariable(dataObject, dataObject)
