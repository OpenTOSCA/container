import groovy.json.*
import java.util.logging.Logger

Logger logger = Logger.getLogger("CreateServiceInstance")
logger.info("======== Executing CreateServiceInstance.groovy with exec ID: ${execution.id} ========")
def post = new URL(execution.getVariable("instanceDataAPIUrl")).openConnection()
def message = '<correlationID xmlns="http://opentosca.org/api">' + execution.getVariable("CorrelationID") + '</correlationID>'
post.setRequestMethod("POST")
post.setDoOutput(true)
post.setRequestProperty("Content-Type", "application/xml")
post.setRequestProperty("accept", "application/json")
post.outputStream.write(message.getBytes("UTF-8"))
def resultVariableName = execution.getVariable('ResultVariableName')

def status = post.responseCode
if (status == 200) {
    def resultText = post.inputStream.text
    def slurper = new JsonSlurper()
    def json = slurper.parseText(resultText)
    def message2 = execution.getVariable("State")
    def url2 = json
    // if the state is specified we assign the state of the created instance to it
    if (message2 != null) {
        def put = new URL(url2 + "/state").openConnection()
        put.setRequestMethod("PUT")
        put.setDoOutput(true)
        put.setRequestProperty("Content-Type", "text/plain")
        put.outputStream.write(message2.getBytes("UTF-8"))

        def status2 = put.responseCode
        if (status2 != 200) {
            execution.setVariable("ErrorDescription", "Received status code " + status2 + " while updating state of Instance with URL: " + url)
            throw new org.camunda.bpm.engine.delegate.BpmnError("InvalidStatusCode")
        }
    }
    logger.info("ServiceInstanceURL: ${json}")
    execution.setVariable(resultVariableName, json)
    return json
} else {
    execution.setVariable("ErrorDescription", "Received status code " + status + " while creating ServiceTemplateInstance!")
    throw new org.camunda.bpm.engine.delegate.BpmnError("InvalidStatusCode")
}
