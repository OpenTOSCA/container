import groovy.xml.XmlUtil
def message2 = execution.getVariable("State");
def nodeInstance = execution.getVariable("NodeInstanceURL");
def nodeInstanceURL = execution.getVariable(nodeInstance); 
def nodeProperties = execution.getVariable("Properties");

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

	println 'This is the content of nodeProperties:'
	println nodeProperties
	
	println 'These are all variables in the current context:'
	def varNamesInContext = execution.getVariableNames();
	varNamesInContext.each{
		varName -> println 'VarName: ' + varName + ' VarValue: ' + execution.getVariable(varName)
	}
	
	
	def proptext = get.getInputStream().getText();
	println 'This is the response from API:'
	println proptext
	println 'We try to update the following list of properties in the xml:'
	println nodeProperties
    def xml = new XmlSlurper().parseText(proptext);

    println 'These are the properties';
    println properties;
    println 'These are the propNames';
    println propertiesNames;
    nodeProperties.split(',').each{property ->
		println 'Trying to find property:' + property + '.';
        if (xml.'**'.find{it -> it.name() == property} != null) {
			println 'Found property in xml with name: ' + property;
			def val = execution.getVariable(property);
            def propertyWithPrefix = 'Input_' + property;
            def valueInputProperty = execution.getVariable('Input_' + property);
            // this is true when it is set in this task
            def alreadySet = false;
            if (valueInputProperty) {
                    for(int i in 0..propertiesNames.size() - 1) {
                        // case for data object
                        if (propertiesNames[i].startsWith(nodeInstance) && !propertiesNames[i].endsWith(nodeInstance)) {
                            def temp = propertiesNames[i].split(nodeInstance)[1];
                            if(temp == property && !alreadySet) {
                                def value = execution.getVariable('Input_'+ temp);
                                if (value.contains('->')) {
                                    def index = value.indexOf('->');
                                    def substring = value.substring(index+2, value.length()-1);
                                    value = substring;
                                }
                            execution.setVariable(nodeInstance+property, value);
                            println 'Writing valuewritedataobject: ' + value;
			                xml.'**'.find{it -> it.name() == property}.replaceBody(value);
                            alreadySet = true;
                            }
                        }else {
                            def temp = propertiesNames[i];
                            if (temp == propertyWithPrefix && !alreadySet) {
                                def value = execution.getVariable(temp);
                                if (value.contains('->')) {
                                    def index = value.indexOf('->');
                                    def substring = value.substring(index+2, value.length()-1);
                                    value = substring;
                                }
                                println 'Writing valuenotdataobject: ' + value;
			                    xml.'**'.find{it -> it.name() == property}.replaceBody(value);
                                alreadySet = true;
                            }
                        }
                }
            }else {
                println 'Writing value: ' + val;
			    xml.'**'.find{it -> it.name() == property}.replaceBody(val);
            }
			
		}
    }
	
	println 'The xml we send:'
	println XmlUtil.serialize(xml)
	
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