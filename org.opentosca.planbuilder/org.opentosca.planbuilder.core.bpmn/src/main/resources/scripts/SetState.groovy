def message = execution.getVariable("State")
def url = execution.getVariable("InstanceURL")
println "======== Executing SetState.groovy with exec ID: ${execution.getId()} for Instance ${url} ========"
if (message != null) {
    def put = new URL(url + "/state").openConnection()
    put.setRequestMethod("PUT")
    put.setDoOutput(true)
    put.setRequestProperty("Content-Type", "text/plain")
    put.getOutputStream().write(message.getBytes("UTF-8"))

    def status = put.getResponseCode()
    if (status != 200) {
        execution.setVariable("ErrorDescription", "Received status code " + status + " while updating state of Instance with URL: " + url)
        throw new org.camunda.bpm.engine.delegate.BpmnError("InvalidStatusCode")
    }
}
