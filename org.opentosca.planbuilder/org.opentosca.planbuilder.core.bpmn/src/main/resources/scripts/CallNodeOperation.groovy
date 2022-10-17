import groovy.json.*
import java.util.logging.Logger

Logger logger = Logger.getLogger("CallNodeOperation")
logger.info("======== Executing CallNodeOperation.groovy with exec ID: ${execution.id} ========")
def csarID = execution.getVariable("CsarID")
def serviceTemplateID = execution.getVariable("ServiceTemplateID")
def serviceInstanceURL = execution.getVariable("ServiceInstanceURL")
def ip = serviceInstanceURL.substring(7).split("/")[0].split(":")[0]

// Host <-- Target, operation is on Host while property on target
def hostNodeTemplateID = execution.getVariable("NodeTemplate")
def nodeInterface = execution.getVariable("Interface")
def operation = execution.getVariable("Operation")

//logger.info("Service Instance ${serviceInstanceURL} of ${serviceTemplateID} in CSAR ${csarID}")

def inputParamNames = execution.getVariable("InputParamNames")
def outputParamNames = execution.getVariable("OutputParamNames")


def invokeParams = "{"
if (inputParamNames != null) {
    inputParamNames = inputParamNames.split(",")
    for (int i in 0..inputParamNames.size() - 1) {
        if (inputParamNames[i] != null) {
            def paramName = 'Input_' + inputParamNames[i]
            def paramValue = execution.getVariable(paramName)
            def compositeParameterValue = ""
            if (paramValue != null) {
                def type = paramValue.split("!")[0]
                if (paramValue.contains("#")) {
                    compositeParameterValue = paramValue.split("#")[1].split("!")[1]
                    type = type.split("#")[0]
                }
                if (type == 'String') {
                    paramValue = paramValue.split("!")[1]
                    paramValue = paramValue.replaceAll('u0026', '&')
                    paramValue = paramValue.replace('->', ',')
                }

                // special handling with DA ex MyTinyToDo_DA#tinytodo.zip
                if (type == 'DA') {
                    def paramDA = execution.getVariable("instanceDataAPIUrl").split("/servicetemplates")[0]
                    def fileName = paramValue.split("!")[1]
                    paramValue = paramDA + fileName
                }

                if (type == 'VALUE') {
                    def propertyValue = paramValue.split("!")[1].split("#")[0]
                    paramValue = execution.getVariable(propertyValue)
                }
                //logger.info("Parameter ${inputParamNames[i]} is (maybe) assigned with value ${paramValue} from ${paramName}: ")
                if (paramValue != "LEER" && paramValue != null) {
                    //logger.info("Parameter ${inputParamNames[i]} is assigned with value ${paramValue} from ${paramName}: ")
                    invokeParams = invokeParams + '"' + inputParamNames[i] + '" : "' + paramValue + compositeParameterValue + '",'
                }
            }
        }
    }
}

invokeParams = invokeParams + '}'
invokeParams = invokeParams.replace(',}', '}')
hostNodeTemplate = hostNodeTemplateID.replace('-', '_').replace('.', '_')
dataObjectOfNodeTemplate = 'ResultVariable' + hostNodeTemplate + '_provisioning_activity'
nodeInstanceURL = execution.getVariable(dataObjectOfNodeTemplate)
nodeInstanceID = nodeInstanceURL[nodeInstanceURL.lastIndexOf("/") + 1, -1]

//logger.info("invokeParams: ${invokeParams}")

def template = '{"invocation-information" : {"csarID" : "$csarID", "serviceTemplateID" : "$serviceTemplateID", "serviceInstanceID" : "$serviceInstanceID", "nodeInstanceID" : "$nodeInstanceID", "nodeTemplateID" : "$nodeTemplateID", "interface" : "$nodeInterface", "operation" : "$operation"} , "params" : $params}'
def binding = ["csarID": csarID, "serviceTemplateID": serviceTemplateID, "serviceInstanceID": serviceInstanceURL, "nodeInstanceID": nodeInstanceID, "nodeTemplateID": hostNodeTemplateID, "nodeInterface": nodeInterface, "operation": operation, "params": invokeParams]
def engine = new groovy.text.SimpleTemplateEngine()
def message = engine.createTemplate(template).make(binding).toString()

//logger.info("message: ${message}")
print "message: $message"
def url = "http://" + ip + ":8086/ManagementBus/v1/invoker"
def post = new URL(url).openConnection()
post.setRequestMethod("POST")
post.setDoOutput(true)
post.setRequestProperty("Content-Type", "application/xml")
post.setRequestProperty("accept", "application/xml")
post.outputStream.write(message.getBytes("UTF-8"))

def status = post.responseCode

if (status != 202) {
    execution.setVariable("ErrorDescription", "Received status code " + status + " while invoking interface: " + nodeInterface + " operation: " + operation + " on NodeTemplate with ID: " + hostNodeTemplateID + "ip: " + ip)
    throw new org.camunda.bpm.engine.delegate.BpmnError("InvalidStatusCode")
}

def taskURL = post.getHeaderField("Location")
def dataObject = execution.getVariable("DataObject")
final String propertiesAccess = '.Properties.'
// Polling until invocation task is finished and set output variable
while (true) {
    def get = new URL(taskURL).openConnection()

    if (get.responseCode != 200) {
        execution.setVariable("ErrorDescription", "Received status code " + status + " while polling for NodeTemplate operation result!")
        throw new org.camunda.bpm.engine.delegate.BpmnError("InvalidStatusCode")
    }
    if (get.responseCode == 200) {
        def pollingResult = get.inputStream.text
        def slurper = new JsonSlurper()
        def pollingResultJSON = slurper.parseText(pollingResult)

        if (pollingResultJSON.status != "PENDING") {
            def responseJSON = pollingResultJSON.response

            //logger.info("Response of polling: ${responseJSON}")
            // in this step we write the output parameter of the operation back to the corresponding data object
            // this is necessary to build the output parameter of the plan
            if (outputParamNames != null) {
                outputParamNames = outputParamNames.split(",")
                outputParamNames.each { outputParam ->
                    String name = dataObject + propertiesAccess + outputParam
                    String value = responseJSON.get(outputParam)
                    execution.setVariable(name, value)
                    logger.info("Set variable ${name}: ${value}")
                }
            }
            return
        }
    }
    sleep(10000)
}
