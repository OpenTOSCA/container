println "======== Executing SetState.groovy with exec ID: ${execution.getId()} ========"

def message = execution.getVariable("State");
def url = execution.getVariable("InstanceURL");

println "Sending PUT request $message to $url/state"
if (message != null) {
    def put = new URL(url + "/state").openConnection();
    put.setRequestMethod("PUT");
    put.setDoOutput(true);
    put.setRequestProperty("Content-Type", "text/plain")
    put.getOutputStream().write(message.getBytes("UTF-8"));

    def status = put.getResponseCode();
    if (status != 200) {
        execution.setVariable("ErrorDescription", "Received status code " + status + " while updating state of Instance with URL: " + url);
        throw new org.camunda.bpm.engine.delegate.BpmnError("InvalidStatusCode");
    }
}

def varNames = execution.getVariableNames()
println "Printing all variable names and values to make sure camunda closing execution safety"
for (int i in 0..varNames.size() - 1) {
    def varValue = execution.getVariable(varNames[i])
    println "${varNames[i]}: $varValue"
    // set it to empty string to avoid camunda database insert issue
    if (varValue == null) {
        execution.setVariable(varNames[i], '')
    }
}
