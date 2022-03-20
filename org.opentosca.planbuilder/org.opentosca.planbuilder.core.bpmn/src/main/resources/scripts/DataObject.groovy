println "======== Executing DataObject.groovy with exec ID: ${execution.getId()} ========"

def properties = execution.getVariableNames()
def nodeInstance = execution.getVariable("NodeInstanceURL")
def dataObject = execution.getVariable('DataObject')
// mappt dataobject auf nodeinstances
def nodeInstanceValue = execution.getVariable(nodeInstance)

for (int i in 0..properties.size() - 1) {
    if (properties[i].startsWith('Input_')) {
        def property = properties[i]
        def value = execution.getVariable(property)
        property = property.split('_')[1]
        println "Parsing  ${property}: ${value} from ${properties[i]}"
        execution.setVariable(nodeInstance + property, value)
    }
}
execution.setVariable(dataObject, nodeInstance)
