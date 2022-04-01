import groovy.xml.XmlUtil

println "======== Executing SetProperties.groovy with exec ID: ${execution.getId()} ========"

def nodeTemplateID = execution.getVariable("NodeTemplate");
def message2 = execution.getVariable("State");
def nodeInstance = execution.getVariable("NodeInstanceURL");
// get the actual value of node instance url
def nodeInstanceURL = execution.getVariable(nodeInstance);

println "NodeTemplate: $nodeTemplateID with $nodeInstance: $nodeInstanceURL"

if (nodeInstanceURL != null) {
    // change state if set
    if (message2 != null) {
        def putState = new URL(nodeInstanceURL+ "/state").openConnection();
        putState.setRequestMethod("PUT");
        putState.setDoOutput(true);
        putState.setRequestProperty("Content-Type", "text/plain")
        putState.getOutputStream().write(message2.getBytes("UTF-8"));
        def statusState = putState.getResponseCode();
        if (statusState != 200) {
            execution.setVariable("ErrorDescription", "Received status code " + statusState + " while updating state of Instance with URL: " + url2);
            throw new org.camunda.bpm.engine.delegate.BpmnError("InvalidStatusCode");
        }
    }

    /*
     url example:
     http://192.168.0.100:1337/csars/MyTinyToDo_Bare_Docker.csar/servicetemplates/
     MyTinyToDo_Bare_Docker/nodetemplates/DockerEngine/instances/1/properties
    */
    def url = nodeInstanceURL + "/properties";
    def get = new URL(url).openConnection();
    get.setRequestProperty("accept", "application/xml")

    def status = get.getResponseCode();
    if (status != 200) {
        execution.setVariable("ErrorDescription", "Received status code " + status + " while getting properties from instance with URL: " + url);
        throw new org.camunda.bpm.engine.delegate.BpmnError("InvalidStatusCode");
    }

    // "Properties">Port,ContainerPort,ContainerID,ContainerIP,ImageID,ContainerMountPath,HostMountFiles
    String[] properties = ((String) execution.getVariable("Properties")).split(",")

    // some output
    /*
    def propertiesNames = execution.getVariableNames();

    def newProperties = properties;

    for (int j in 0..properties.size()-1) {
        for (int i in 0..propertiesNames.size()-1) {
            if (propertiesNames[i].startsWith(nodeInstance) && !propertiesNames[i].endsWith(nodeInstance)) {
                def temp = propertiesNames[i].split(nodeInstance)[1];
                if (temp == properties[j]) {
                    def value = execution.getVariable('Input_'+temp);
                    if (value.contains('->')) {
                        def port = value.split('->')[1];
                        value = port;
                    }
                    execution.setVariable(nodeInstance+properties[j], value);
                }
            }
        }
    }
    */
    // making PUT request to set nodeInstance/properties

    def xmlResponse = new XmlSlurper().parseText(get.getInputStream().getText());

    println XmlUtil.serialize(xmlResponse)

    // 1. iterating through the all properties [DockerEngineURL,DockerEngineCertificate]
    // 2. setting its value from input variable with prefix and replace with value

    for (int i in 0..properties.size() - 1) {
        println "properties[$i]:  ${properties[i]}"
        String propertyName = properties[i]
        String propertyValue = execution.getVariable("Input_${properties[i]}")
        // propertyValue may only be set as global variable in runtime
        if (propertyValue == null) {
            String propertyGlobalVarName = nodeTemplateID + '_' + propertyName
            propertyValue = execution.getVariable(propertyGlobalVarName)
            println "Retrieveing ${propertyName} from ${propertyGlobalVarName} with ${propertyValue}"
        } else {
            println "Retrieveing ${propertyName} from Input_${propertyName} with ${propertyValue}"
        }

        if (propertyValue == null) {
            continue
        }

        xmlResponse.'**'.findAll { if (it.name() == propertyName) it.replaceBody(propertyValue)}
    }

    println XmlUtil.serialize(xmlResponse)

    def put = new URL(url).openConnection();
    put.setRequestMethod("PUT");
    put.setDoOutput(true);
    put.setRequestProperty("Content-Type", "application/xml")
    put.setRequestProperty("accept", "application/xml")
    put.getOutputStream().write(XmlUtil.serialize(xmlResponse).getBytes("UTF-8"));

    status = put.getResponseCode();
    if (status != 200) {
        execution.setVariable("ErrorDescription", "Received status code " + status + " while updating properties from instance with URL: " + url);
        throw new org.camunda.bpm.engine.delegate.BpmnError("InvalidStatusCode");
    }
}


/* Ex. NodeTemplate: DockerEngine
<camunda:inputParameter name="State" />
<camunda:inputParameter name="NodeInstanceURL">DockerEngineNodeInstanceURL</camunda:inputParameter>
<camunda:inputParameter name="NodeTemplate">DockerEngine</camunda:inputParameter>
<camunda:inputParameter name="Properties">DockerEngineURL,DockerEngineCertificate</camunda:inputParameter>
<camunda:inputParameter name="Values" />
<camunda:inputParameter name="Input_DockerEngineURL">${DockerEngineURL}</camunda:inputParameter>
<camunda:inputParameter name="Input_DockerEngineCertificate" />
*/

/* Ex. NodeTemplate: MyTinyToDoDockerContainer
<camunda:inputParameter name="State">STARTED</camunda:inputParameter>
<camunda:inputParameter name="NodeInstanceURL">MyTinyToDoDockerContainerNodeInstanceURL</camunda:inputParameter>
<camunda:inputParameter name="NodeTemplate">MyTinyToDoDockerContainer</camunda:inputParameter>
<camunda:inputParameter name="Properties">Port,ContainerPort,ContainerID,ContainerIP,ImageID,ContainerMountPath,HostMountFiles</camunda:inputParameter>
<camunda:inputParameter name="Values" />
<camunda:inputParameter name="Input_Port">${ApplicationPort}</camunda:inputParameter>
<camunda:inputParameter name="Input_ContainerPort">80</camunda:inputParameter>
<camunda:inputParameter name="Input_ContainerID" />
<camunda:inputParameter name="Input_ContainerIP" />
<camunda:inputParameter name="Input_ImageID" />
<camunda:inputParameter name="Input_ContainerMountPath" />
<camunda:inputParameter name="Input_HostMountFiles" />

*/
