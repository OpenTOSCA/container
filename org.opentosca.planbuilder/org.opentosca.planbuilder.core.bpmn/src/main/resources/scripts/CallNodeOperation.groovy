import groovy.json.*

println "======== Executing CallNodeOperation.groovy with exec ID: ${execution.getId()} ========"
def csarID = execution.getVariable("CsarID");
def serviceTemplateID = execution.getVariable("ServiceTemplateID")
def serviceInstanceURL = execution.getVariable("ServiceInstanceURL")
def serviceInstanceID = serviceInstanceURL.split("/")[serviceInstanceURL.split("/").length - 1];
def ip = serviceInstanceURL.substring(7).split("/")[0].split(":")[0];

// Host <-- Target, operation is on Host while property on target
def hostNodeTemplateID = execution.getVariable("NodeTemplate");
def targetNodeTemplateID = "MyTinyToDoDockerContainer";
def nodeInterface = execution.getVariable("Interface");
def operation = execution.getVariable("Operation");

println "Service Instance $serviceInstanceURL of $serviceTemplateID in CSAR $csarID"

def inputParamNames = execution.getVariable("InputParamNames");
def outputParamNames = execution.getVariable("OutputParamNames");


def invokeParams = "{";
if (inputParamNames != null) {
    inputParamNames = inputParamNames.split(",");
    for (int i in 0..inputParamNames.size() - 1) {
        if (inputParamNames[i] != null) {
            def paramName = 'Input_' + inputParamNames[i];
            def paramValue = execution.getVariable(paramName);

            if (paramValue != null) {
                def type = paramValue.split("!")[0];
                if (type == 'String') {
                    paramValue = paramValue.split("!")[1];
                    paramValue = paramValue.replace('->', ',');
                }

                // special handling with DA ex MyTinyToDo_DA#tinytodo.zip
                if (type == 'DA') {
                    def paramDA = execution.getVariable("instanceDataAPIUrl").split("/servicetemplates")[0];
                    def fileName = paramValue.split("!")[1];
                    paramValue = paramDA + fileName;
                }

                if (type == 'VALUE') {
                    def propertyValue = paramValue.split("!")[1];
                    paramValue = execution.getVariable(propertyValue);
                }
                println "Parameter ${inputParamNames[i]} is (maybe) assigned with value $paramValue from $paramName: "
                if (paramValue != "LEER" && paramValue != null) {
                    println "Parameter ${inputParamNames[i]} is assigned with value $paramValue from $paramName: "
                    invokeParams = invokeParams + '"' + inputParamNames[i] + '" : "' + paramValue + '",';
                }
            }
        }
    }
}

invokeParams = invokeParams + '}';
invokeParams = invokeParams.replace(',}', '}');

println "invokeParams: $invokeParams"

def template = '{"invocation-information" : {"csarID" : "$csarID", "serviceTemplateID" : "$serviceTemplateID", "serviceInstanceID" : "$serviceInstanceID", "nodeTemplateID" : "$nodeTemplateID", "interface" : "$nodeInterface", "operation" : "$operation"} , "params" : $params}';
def binding = ["csarID": csarID, "serviceTemplateID": serviceTemplateID, "serviceInstanceID": serviceInstanceID, "nodeTemplateID": hostNodeTemplateID, "nodeInterface": nodeInterface, "operation": operation, "params": invokeParams];
def engine = new groovy.text.SimpleTemplateEngine();
def message = engine.createTemplate(template).make(binding).toString();

println "message: $message"
def url = "http://" + ip + ":8086/ManagementBus/v1/invoker"

println "url: $url"

def post = new URL(url).openConnection();
post.setRequestMethod("POST");
post.setDoOutput(true);
post.setRequestProperty("Content-Type", "application/xml")
post.setRequestProperty("accept", "application/xml")
post.getOutputStream().write(message.getBytes("UTF-8"));

def status = post.getResponseCode();

if (status != 202) {
    execution.setVariable("ErrorDescription", "Received status code " + status + " while invoking interface: " + nodeInterface + " operation: " + operation + " on NodeTemplate with ID: " + hostNodeTemplateID + "ip: " + ip);
    throw new org.camunda.bpm.engine.delegate.BpmnError("InvalidStatusCode")
}

def taskURL = post.getHeaderField("Location")

def dataObject = execution.getVariable("DataObject")
final String PROPERTIES = '.Properties.'
// Polling until invocation task is finished and set output variable
while (true) {
    def get = new URL(taskURL).openConnection()

    if (get.getResponseCode() != 200) {
        execution.setVariable("ErrorDescription", "Received status code " + status + " while polling for NodeTemplate operation result!");
        throw new org.camunda.bpm.engine.delegate.BpmnError("InvalidStatusCode")
    }
    def pollingResult = get.getInputStream().getText();
    def slurper = new JsonSlurper();
    def pollingResultJSON = slurper.parseText(pollingResult);

    if (!pollingResultJSON.status.equals("PENDING")) {
        def responseJSON = pollingResultJSON.response;

        println "Response of polling:"
        println responseJSON
        // in this step we write the output parameter of the operation back to the corresponding data object
        // this is necessary to build the output parameter of the plan
        if (outputParamNames != null) {
            outputParamNames = outputParamNames.split(",");
            outputParamNames.each { outputParam ->
                String name = dataObject + PROPERTIES + outputParam
                String value = responseJSON.get(outputParam)
                execution.setVariable(name, value);
                println "Set variable $name: $value"
            }
        }
        return;
    }

    sleep(10000);
}
