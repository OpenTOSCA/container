import groovy.xml.XmlUtil
def message2 = execution.getVariable("State");
def nodeInstance = execution.getVariable("NodeInstanceURL");
def nodeInstanceURL = execution.getVariable(nodeInstance); 

if(nodeInstanceURL != null){
    // change state if set
    if(message2 != null){
        def putState = new URL(nodeInstanceURL+ "/state").openConnection();
        putState.setRequestMethod("PUT");
        putState.setDoOutput(true);
        putState.setRequestProperty("Content-Type", "text/plain")
        putState.getOutputStream().write(message2.getBytes("UTF-8"));
        def statusState = putState.getResponseCode();
        if(statusState != 200){
            execution.setVariable("ErrorDescription", "Received status code " + statusState + " while updating state of Instance with URL: " + url2);
            throw new org.camunda.bpm.engine.delegate.BpmnError("InvalidStatusCode");
        }
    }

    def url = nodeInstanceURL + "/properties";
    def get = new URL(url).openConnection();
    get.setRequestProperty("accept", "application/xml")

    def status = get.getResponseCode();
    if(status != 200){
        execution.setVariable("ErrorDescription", "Received status code " + status + " while getting properties from instance with URL: " + url);
        throw new org.camunda.bpm.engine.delegate.BpmnError("InvalidStatusCode");
    }

    def properties = execution.getVariableNames();
    def propertiesNames = execution.getVariableNames();

    def newProperties = properties;

    for(int j in 0..properties.size()-1){
        for(int i in 0..propertiesNames.size()-1){
            if(propertiesNames[i].startsWith(nodeInstance) && !propertiesNames[i].endsWith(nodeInstance)){
                def temp = propertiesNames[i].split(nodeInstance)[1];
                if(temp == properties[j]){
                def value = execution.getVariable('Input_'+temp);
                if(value.contains('->')){
                    def port = value.split('->')[1];
                    value = port;
                }
                execution.setVariable(nodeInstance+properties[j], value);
                }
            }
        }
    }


    def xml = new XmlSlurper().parseText(get.getInputStream().getText());
    properties.eachWithIndex { property, index ->
        xml.'**'.findAll { if(it.name() == property) it.replaceBody execution.getVariable('Input_'+property) }
    }
    def put = new URL(url).openConnection();
    put.setRequestMethod("PUT");
    put.setDoOutput(true);
    put.setRequestProperty("Content-Type", "application/xml")
    put.setRequestProperty("accept", "application/xml")
    put.getOutputStream().write(XmlUtil.serialize(xml).getBytes("UTF-8"));

    status = put.getResponseCode();
    if(status != 200){
        execution.setVariable("ErrorDescription", "Received status code " + status + " while updating properties from instance with URL: " + url);
        throw new org.camunda.bpm.engine.delegate.BpmnError("InvalidStatusCode");
    }
}