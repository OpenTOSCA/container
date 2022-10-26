import groovy.xml.XmlUtil
import java.util.logging.Logger

Logger logger = Logger.getLogger("SetProperties")
def nodeInstance = execution.getVariable("NodeInstanceURL")
logger.info("======== Executing SetProperties.groovy with exec ID: ${execution.id} for NodeInstance ${nodeInstance} ========")
def nodeProperties = execution.getVariable("Properties").split(",")

def url = nodeInstance + "/properties"
logger.info("url: ${url}")
def get = new URL(url).openConnection()
get.setRequestProperty("accept", "application/xml")

def status = get.responseCode
if (status != 200) {
    execution.setVariable("ErrorDescription", "Received status code " + status + " while getting properties from instance with URL: " + url)
    throw new org.camunda.bpm.engine.delegate.BpmnError("InvalidStatusCode")
}

logger.info("This is the content of nodeProperties: ${nodeProperties}")
logger.info("These are all variables in the current context:")
def varNamesInContext = execution.getVariableNames()
varNamesInContext.each {
    varName -> logger.info("VarName: " + varName + " VarValue: " + execution.getVariable(varName))
}

def response = get.inputStream.text
logger.info("This is the response from API: ${response}")
logger.info("We try to update the following list of properties in the xml: ${nodeProperties}")
def xml = new XmlSlurper().parseText(response)

// the value of each property can be found in the corresponding dataobject
nodeProperties.each { property ->
    logger.info("Trying to find property:" + property + ".")
    if (xml.'**'.find { it -> it.name() == property } != null) {
        logger.info("Found property in xml with name: " + property)
        // this should return something like DataObjectReference_Id.Properties.PropertyName
        def value = execution.getVariable("Input_" + property)
        execution.setVariable(value, execution.getVariable(value))
        xml.'**'.find { it -> it.name() == property }.replaceBody(execution.getVariable(value))
        if (execution.getVariable(value).contains("LEER")) {
            xml.'**'.find { it -> it.name() == property }.replaceBody("")
        }
    }
}

logger.info("The xml we send: ${xml}")

def put = new URL(url).openConnection()
put.setRequestMethod("PUT")
put.setDoOutput(true)
put.setRequestProperty("Content-Type", "application/xml")
put.setRequestProperty("accept", "application/xml")
put.outputStream.write(XmlUtil.serialize(xml).getBytes("UTF-8"))

status = put.responseCode
if (status != 200) {
    execution.setVariable("ErrorDescription", "Received status code " + status + " while updating properties from instance with URL: " + url)
    throw new org.camunda.bpm.engine.delegate.BpmnError("InvalidStatusCode")
}
