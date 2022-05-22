def properties = execution.getVariableNames()
//def nodeInstance = execution.getVariable("NodeInstanceURL");
def dataObject = execution.getVariable('DataObject')
def propertiesNames = execution.getVariable('PropertiesNames').split(",");
final String PROPERTIES = 'Properties.'
println properties
// mappt dataobject auf nodeinstances
//def nodeInstanceValue = execution.getVariable(nodeInstance)
print "dataobject task execution"
for (int i in 0..properties.size() - 1) {
    if (properties[i].startsWith('Input_')) {
        def property = properties[i]
        def value = execution.getVariable(property)
        property = property.split('_')[1]
        if (property.startsWith("ContainerPort")) {
            def value2 = execution.getVariable(properties[i])
            execution.setVariable(property, value2)
        }
        //execution.setVariable(nodeInstance+property, value);
        execution.setVariable(dataObject + property, value)
    }
}

for (int i in 0..propertiesNames.size() - 1) {
    def property = PROPERTIES + propertiesNames[i]
    def value = execution.getVariable(property)
    //execution.setVariable(nodeInstance+property, value);
    println "wir setzen die Variable"
    println dataObject + property
    println value
    execution.setVariable(dataObject + property, value)

}
execution.setVariable(dataObject, dataObject)
