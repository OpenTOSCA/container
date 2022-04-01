/* groovylint-disable NoDef, UnnecessaryGString, UnnecessaryGetter, UnnecessarySemicolon, VariableTypeRequired */
import groovy.json.*

/*
Input

<camunda:inputParameter name="CsarID">MyTinyToDo_Bare_Docker.csar</camunda:inputParameter>
<camunda:inputParameter name="Interface">InterfaceDockerEngine</camunda:inputParameter>
<camunda:inputParameter name="Operation">startContainer</camunda:inputParameter>
<camunda:inputParameter name="TargetNodeTemplate">MyTinyToDoDockerContainer</camunda:inputParameter>
<camunda:inputParameter name="HostNodeTemplate">DockerEngine</camunda:inputParameter>
<camunda:inputParameter name="ServiceTemplateID">MyTinyToDo_Bare_Docker</camunda:inputParameter>
<camunda:inputParameter name="ServiceInstanceID">${ServiceInstanceURL}</camunda:inputParameter>

<camunda:inputParameter name="OutputParamNames">ContainerPort,ContainerID,ContainerIP</camunda:inputParameter>
<camunda:inputParameter name="InputParamNames">ContainerPort,Port,DockerEngineURL</camunda:inputParameter>

<camunda:inputParameter name="Input_ContainerPort">80</camunda:inputParameter>
<camunda:inputParameter name="Input_ImageLocation">DA!MyTinyToDo_DA#tinytodo.zip</camunda:inputParameter>
<camunda:inputParameter name="Input_DockerEngineURL">${DockerEngineURL}</camunda:inputParameter>
<camunda:inputParameter name="Input_Port">${ApplicationPort}</camunda:inputParameter>

// visualization
Output:
<camunda:outputParameter name="Output_ContainerPorts" />
<camunda:outputParameter name="Output_ContainerID" />
<camunda:outputParameter name="Output_ContainerIP" />

*/
println "======== Executing CallNodeOperation.groovy with exec ID: ${execution.getId()} ========"

def csarID = execution.getVariable("CsarID");
def serviceTemplateID = execution.getVariable("ServiceTemplateID")
def serviceInstanceURL = execution.getVariable("ServiceInstanceURL")
def serviceInstanceID = serviceInstanceURL.split("/")[serviceInstanceURL.split("/").length - 1];
def ip = serviceInstanceURL.substring(7).split("/")[0].split(":")[0];

// Host <-- Target, operation is on Host while property on target
def hostNodeTemplateID = execution.getVariable("HostNodeTemplate");
def targetNodeTemplateID = execution.getVariable("TargetNodeTemplate");
def nodeInterface = execution.getVariable("Interface");
def operation = execution.getVariable("Operation");

println "Service Instance $serviceInstanceURL of $serviceTemplateID in CSAR $csarID"

//  "inputParamNames">DockerEngineURL,ContainerImage,ContainerPorts
def inputParamNames = execution.getVariable("InputParamNames").split(",");
// "OutputParamNames">ContainerPorts,ContainerID,ContainerIP
def outputParamNames = execution.getVariable("OutputParamNames").split(",");
def invokeParams = "{";

// Prepare Managebus POST request body
for (int i in 0..inputParamNames.size() - 1) {
    if (inputParamNames[i] != null) {

        def paramName = 'Input_' + inputParamNames[i];
        def paramValue = execution.getVariable(paramName);

        if (paramValue != null) {
            def type = paramValue.split("!")[0];

            /*
            name="Input_DockerEngineURL">VALUE!DockerEngineDataObject#DockerEngineURL
            name="Input_ContainerPorts">String!${ApplicationPort}
            name="Input_ImageLocation">DA!MyTinyToDo_DA#tinytodo.zip
            */
            // special handling with DA ex MyTinyToDo_DA#tinytodo.zip
            if (type == 'DA') {
                paramValue = paramValue.split("!")[1];

                // MyTinyToDo_DA
                def da = paramValue.split("#")[0];
                // tinytodo.zip
                def fileName = paramValue.split("#")[1];
                def namespace = URLEncoder.encode('http://opentosca.org/artifacttemplates', "UTF-8");
                namespace = URLEncoder.encode(namespace, "UTF-8");

                def paramDA = execution.getVariable("instanceDataAPIUrl").split("/servicetemplates")[0];

                paramValue = paramDA + '/content/artifacttemplates/' + namespace + '/' + da + '/files/' + fileName;
            }

            println "Parameter ${inputParamNames[i]} is assigned with value $paramValue from $paramName: "
            invokeParams = invokeParams + '"' + inputParamNames[i] + '" : "' + paramValue + '",';
        }
    }
}

invokeParams = invokeParams + '}';
invokeParams = invokeParams.replace(',}', '}');

println "invokeParams: $invokeParams"
/* Management Bus API reference:
 https://github.com/OpenTOSCA/container/blob/
 org.opentosca.bus/org.opentosca.bus.management.api.resthttp/src/main/resources/META-INF/swagger.json#L14
*/
def template = '{"invocation-information" : {"csarID" : "$csarID", "serviceTemplateID" : "$serviceTemplateID", "serviceInstanceID" : "$serviceInstanceID", "nodeTemplateID" : "$nodeTemplateID", "interface" : "$nodeInterface", "operation" : "$operation"} , "params" : $params}';
def binding = ["csarID":csarID, "serviceTemplateID":serviceTemplateID, "serviceInstanceID":serviceInstanceID, "nodeTemplateID":hostNodeTemplateID, "nodeInterface":nodeInterface, "operation":operation, "params":invokeParams];
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
    throw new org.camunda.bpm.engine.delegate.BpmnError("InvalidStatusCode");
}

def taskURL = post.getHeaderField("Location");

// Polling until invocation task is finished and set output variable
while (true) {
    def get = new URL(taskURL).openConnection();

    if (get.getResponseCode() != 200) {
        execution.setVariable("ErrorDescription", "Received status code " + status + " while polling for NodeTemplate operation result!");
        throw new org.camunda.bpm.engine.delegate.BpmnError("InvalidStatusCode");
    }
    def pollingResult = get.getInputStream().getText();
    def slurper = new JsonSlurper();
    def pollingResultJSON = slurper.parseText(pollingResult);

    if (!pollingResultJSON.status.equals("PENDING")) {
        def responseJSON = pollingResultJSON.response;
        // ex. [ContainerPorts, ContainerID, ContainerIP] -> MyTinyToDoDockerContainer_0ContainerPorts, MyTinyToDoDockerContainer_0ContainerID
        outputParamNames.each { outputParam ->
            String name = targetNodeTemplateID + '_' + outputParam
            String value = responseJSON.get(outputParam)
            execution.setVariable(name, value);
            println "Set variable $name: $value"
        }
        return;
    }

    sleep(10000);
}
