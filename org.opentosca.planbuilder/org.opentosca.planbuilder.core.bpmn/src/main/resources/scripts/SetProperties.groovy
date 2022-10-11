import groovy.xml.XmlUtil
import java.util.logging.Logger

Logger logger = Logger.getLogger("SetProperties")
def message2 = execution.getVariable("State")
def nodeInstance = execution.getVariable("NodeInstanceURL")
logger.info("======== Executing SetProperties.groovy with exec ID: ${execution.id} for NodeInstance ${nodeInstance} ========")
def nodeProperties = execution.getVariable("Properties").split(",")

if (nodeInstance != null) {
    // if the state is specified we assign the state of the instance to it & exclude the default case
    if (message2 != null && !message2.startsWith("StateToSet")) {
        def url2 = nodeInstance + "/state"
        def putState = new URL(nodeInstance + "/state").openConnection()
        putState.setRequestMethod("PUT")
        putState.setDoOutput(true)
        putState.setRequestProperty("Content-Type", "text/plain")
        putState.outputStream.write(message2.getBytes("UTF-8"))
        def statusState = putState.responseCode
        if (statusState != 200) {
            execution.setVariable("ErrorDescription", "Received status code " + statusState + " while updating state of Instance with URL: " + url2)
            throw new org.camunda.bpm.engine.delegate.BpmnError("InvalidStatusCode")
        }
    }
    def url = nodeInstance + "/properties"
    logger.info("url: ${url}")
    def get = new URL(url).openConnection()
    get.setRequestProperty("accept", "application/xml")

    def status = get.getResponseCode()
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

    def proptext = get.getInputStream().getText()
    logger.info("This is the response from API: ${proptext}")
    logger.info("We try to update the following list of properties in the xml: ${nodeProperties}")
    def xml = new XmlSlurper().parseText(proptext)

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

    logger.info("The xml we send:")
    // XmlUtil.serialize(xml)

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
}
