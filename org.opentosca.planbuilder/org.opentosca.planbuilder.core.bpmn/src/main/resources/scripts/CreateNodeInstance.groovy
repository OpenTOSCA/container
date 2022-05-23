import groovy.json.*

def template = execution.getVariable("NodeTemplate")
println "======== Executing CreateNodeInstance.groovy with exec ID: ${execution.getId()} for NodeTemplate ${template} ========"
def resultVariableName = execution.getVariable("ResultVariableName")
// create TemplateInstance URL from instance data API URL
def url = execution.getVariable("instanceDataAPIUrl").minus("instances")
url = url + "nodetemplates/" + template + "/instances"
def post = new URL(url).openConnection()

// get ServiceTemplateInstance ID and add it to the request body
def serviceInstanceURL = execution.getVariable("ServiceInstanceURL")
def valueOfServiceInstanceURL = execution.getVariable(serviceInstanceURL)
def message = valueOfServiceInstanceURL.substring(valueOfServiceInstanceURL.lastIndexOf('/') + 1)

// send Post to instance data API
post.setRequestMethod("POST")
post.setDoOutput(true)
post.setRequestProperty("Content-Type", "text/plain")
post.setRequestProperty("accept", "application/json")
post.getOutputStream().write(message.getBytes("UTF-8"))

println "message: $message"
def status = post.getResponseCode()
if (status == 200) {
    def resultText = post.getInputStream().getText()
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
        put.getOutputStream().write(message2.getBytes("UTF-8"))

        def status2 = put.getResponseCode()
        if (status2 != 200) {
            execution.setVariable("ErrorDescription", "Received status code " + status2 + " while updating state of Instance with URL: " + url);
            throw new org.camunda.bpm.engine.delegate.BpmnError("InvalidStatusCode")
        }
    }
    println "ResultVariable: $json"
    execution.setVariable(resultVariableName, json)
    return json
} else {
    execution.setVariable("ErrorDescription", "Received status code " + status + " while creating Instance of NodeTemplate with ID: " + template)
    throw new org.camunda.bpm.engine.delegate.BpmnError("InvalidStatusCode")
}
