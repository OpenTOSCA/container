package org.opentosca.planbuilder.type.plugin.serverless.bpel.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.geronimo.mail.util.Base64;
import org.opentosca.container.core.tosca.convention.Interfaces;
import org.opentosca.container.core.tosca.convention.Properties;
import org.opentosca.container.core.tosca.convention.Types;
import org.opentosca.container.core.tosca.convention.Utils;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.plugins.context.Variable;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.provphase.plugin.invoker.bpel.BPELInvokerPlugin;
import org.opentosca.planbuilder.type.plugin.serverless.core.handler.ServerlessPluginHandler;
import org.slf4j.LoggerFactory;

/**
 * This class contains the BPEL Handler to handle incoming ServerlessFunction
 * NodeTemplates. It executes the respective operation to deploy a serverless
 * function on the underlying ServerlessPlatform NodeTemplate. In order to do
 * this, the plugin gathers the properties of the overlying ServerlessFunction
 * NodeTemplate and maps them on the inputparameters of the
 * management-operations of the ServerlessPlatform Node Templates. Furthermore,
 * if any events are connected to the ServerlessFunction NodeTemplate on the
 * TopologyTemplate, these get deployed, too.
 *
 * @author Tobias Mathony - mathony.tobias@gmail.com
 *
 */
public class BPELServerlessPluginHandler implements ServerlessPluginHandler<BPELPlanContext> {

    private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(BPELServerlessPluginHandler.class);

    private final BPELInvokerPlugin invokerOpPlugin = new BPELInvokerPlugin();

    // parameter to map
    private final static String[] createFunctionInstanceExternalInputParams = { "FunctionName", "Runtime" };
    private final static String[] createHttpEventInstanceExternalInputParams = { "FunctionName", "EventName", "APIID",
	    "ResourceID", "FunctionURI", "HTTPMethod", "CreateHTTPEvent", "AuthorizationType" };
    private final static String[] createTimerEventInstanceExternalInputParams = { "FunctionName", "EventName", "CRON" };
    private final static String[] createDatabaseEventInstanceExternalInputParams = { "FunctionName", "EventName",
	    "DatabaseName", "DatabaseHostUrl", "DatabaseUsername", "DatabasePassword", "ListenToWhatChanges",
	    "StartingPosition" };
    private final static String[] createBlobstorageEventInstanceExternalInputParams = { "FunctionName", "EventName",
	    "BucketName", "EventType" };
    private final static String[] createPubSubEventInstanceExternalInputParams = { "FunctionName", "EventName",
	    "TopicName", "MessageHubUsername", "MessageHubPassword", "KafkaAdminUrl", "BrokerArray" };

    /*
     * This method downloads the DeploymentArtifact of the ServerlessFunction
     * NodeTemplate and encodes it as Base64 String to pass it to the FunctionCode
     * Inputparameter of the ServerlessFunction NodeTemplate.
     */
    public String getFunctionCode(final String FunctionURL) {
	String functionCode = null;
	try {

	    URL url = null;

	    // get URL for DeploymentArtifact from HEADER
	    url = new URL(FunctionURL);
	    // get Filename of the DeploymentArtifact (remove namespace etc.)
	    final String daName = FunctionURL.substring(FunctionURL.lastIndexOf("/") + 1);

	    HttpURLConnection connection = null;

	    // open the connection
	    connection = (HttpURLConnection) url.openConnection();

	    // GET as we want to download the DeploymentArtifact
	    connection.setRequestMethod("GET");

	    // DeploymentArtifact is in a ZIP format (Zip Serverless Function)
	    connection.setRequestProperty("Content-Type", "application/zip");

	    InputStream is = null;

	    is = connection.getInputStream();

	    FileOutputStream out = null;

	    out = new FileOutputStream(daName);

	    // call the copy method with downloaded DeploymentArtifact
	    copy(is, out, 1024);

	    out.close();

	    // for OpenWhisk especially: OpenWhisk deploys zip-packaged functions as a
	    // base64 encoded string of the file
	    functionCode = encodeFileToBase64Binary(daName);
	    LOG.debug("Functioncode is here: " + functionCode);

	} catch (final Exception e) {
	    e.printStackTrace();
	}

	return functionCode;
    }

    /*
     * This method determines the URL of the attached DeploymentArtifact of the
     * ServerlessFunction NodeTemplate.
     */
    public String getFunctionUrl(final BPELPlanContext context, final AbstractNodeTemplate nodeTemplate) {

	// get DeploymentArtifact of the serverlessFunction nodeTemplate
	final String ArtifactTemplate = nodeTemplate.getDeploymentArtifacts().get(0).getArtifactRef()
		.getArtifactReferences().get(0).getReference();
	LOG.debug("Found Serverless Function with following deployment artifact: " + ArtifactTemplate);
	// get CSAR name to build-up the URL to the DeploymentArtifact
	final String csarName = context.getCSARFileName();
	// this is a bit hacky
	final String URL = "http://localhost:1337/csars/" + csarName + "/content/" + ArtifactTemplate;
	LOG.debug("Following is the URL to the DeploymentArtifact of the Serverless Function Node Template: " + URL);

	return URL;
    }

    /*
     * this method encodes the file loaded in the loadFile-method to Base64 this is
     * neccessary due to a missing OpenWhisk Client SDK for Java so the zip-action
     * has to be encoded to Base64 to be able to upload it
     */
    private static String encodeFileToBase64Binary(final String fileName) throws IOException {

	final File file = new File(fileName);

	final byte[] bytes = loadFile(file);

	final byte[] encoded = Base64.encode(bytes);

	final String encodedString = new String(encoded);

	System.out.println("ENCODED STRING: " + encodedString);

	return encodedString;

    }

    /*
     * this method loads the downloaded zip-action in bytes format
     */
    @SuppressWarnings("resource")
    private static byte[] loadFile(final File file) throws IOException {

	final InputStream is = new FileInputStream(file);

	final long length = file.length();
	if (length > Integer.MAX_VALUE) {
	    // File is too large
	}
	final byte[] bytes = new byte[(int) length];

	int offset = 0;
	int numRead = 0;
	while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
	    offset += numRead;
	}

	if (offset < bytes.length) {
	    throw new IOException("Could not completely read file " + file.getName());
	}

	is.close();

	return bytes;

    }

    /*
     * this method copies the downloaded zip-action to be able to upload it to the
     * OpenWhisk API
     */
    public static void copy(final InputStream input, final OutputStream output, final int bufferSize)
	    throws IOException {

	final byte[] buf = new byte[bufferSize];
	int n = input.read(buf);
	while (n >= 0) {
	    output.write(buf, 0, n);
	    n = input.read(buf);
	}
	output.flush();
    }

    @Override
    public boolean handle(final BPELPlanContext context, final AbstractNodeTemplate nodeTemplate) {
	// do nothing here when ServerlessFunction nodeType is hosted on an unknown
	// ServerlessPlatform nodeType
	return true;
    }

    /*
     * this method handles the invocation of the deployment of a ServerlessFunction
     * nodeTemplate
     *
     * @param context: the respective BPELPlanContext of the ServiceTemplate
     *
     * @param nodeTemplate: the nodeTemplate of the ServerlessFunction nodeType
     * which should be handled
     */
    public void handleFunctionDeploymentInvocation(final BPELPlanContext context,
	    final AbstractNodeTemplate nodeTemplate) {

	// create a global variable for the URL of the DeploymentArtifact to downlaod
	// the zip-packaged serverless function
	final Variable functionUrlVar = context.createGlobalStringVariable("FunctionURL",
		getFunctionUrl(context, nodeTemplate));

	/*
	 * create a global variable for the Base64 encoded functioncode of the
	 * zip-packaged serverless function
	 */

	final Variable functionCodeVar = context.createGlobalStringVariable("FunctionCode",
		getFunctionCode(getFunctionUrl(context, nodeTemplate)));

	/*
	 * wrap the properties of a ServerlessFunction nodeTemplate which are later
	 * mapped onto the input parameters of the management operations of the
	 * underlying ServerlessPlatform nodeTempalte
	 */

	Variable functionNamePropWrapper = null;

	for (final String functionName : org.opentosca.container.core.tosca.convention.Utils
		.getSupportedServerlessFunctionNamePropertyNames()) {

	    functionNamePropWrapper = context.getPropertyVariable(nodeTemplate, functionName);
	    if (functionNamePropWrapper == null) {
		functionNamePropWrapper = context.getPropertyVariable(functionName, true);
	    } else {
		break;
	    }

	}

	if (functionNamePropWrapper == null) {
	    BPELServerlessPluginHandler.LOG
		    .warn("Serverless Node doesn't have FunctionName property, altough it has the proper NodeType");
	}

	Variable runtimePropWrapper = null;

	for (final String runtime : org.opentosca.container.core.tosca.convention.Utils
		.getSupportedServerlessFunctionRuntimePropertyNames()) {

	    runtimePropWrapper = context.getPropertyVariable(nodeTemplate, runtime);
	    if (runtimePropWrapper == null) {
		runtimePropWrapper = context.getPropertyVariable(runtime, true);
	    } else {
		break;
	    }
	}

	if (runtimePropWrapper == null) {
	    BPELServerlessPluginHandler.LOG
		    .warn("Serverless Node doesn't have Runtime property, altough it has the proper NodeType");
	}

	// add plan callback adress to plan input message
	LOG.debug("Adding plan callback address field to plan input");
	context.addStringValueToPlanRequest("planCallbackAddress_invoker");

	// add csarEntryPoint to plan input message
	LOG.debug("Adding csarEntryPoint field to plan input");
	context.addStringValueToPlanRequest("csarEntrypoint");

	// create hashmaps for the mapping of the parameters
	final Map<String, Variable> createFunctionInternalExternalPropsInput = new HashMap<>();
	final Map<String, Variable> createFunctionInternalExternalPropsOutput = new HashMap<>();

	/*
	 * iterate over every neccesary parameter to map onto the input parameters of
	 * the management operations of the underlying ServerlessPlatform nodeTemplate
	 */

	for (final String externalParameter : BPELServerlessPluginHandler.createFunctionInstanceExternalInputParams) {

	    LOG.debug("External parameter to map is: " + externalParameter);
	    // find variable for input param
	    Variable variable = context.getPropertyVariable(nodeTemplate, externalParameter);

	    if (variable == null) {
		variable = context.getPropertyVariable(externalParameter, true);
		LOG.debug("Property variable is: " + variable.toString());
	    } else {
		BPELServerlessPluginHandler.LOG.debug("Found property variable " + externalParameter);
	    }

	    createFunctionInternalExternalPropsInput.put(externalParameter, variable);

	    if (BPELPlanContext.isVariableValueEmpty(variable, context)) {
		LOG.debug("Variable value is empty for variable: " + variable);
		context.addStringValueToPlanRequest(externalParameter);
		LOG.debug("Add external string value to plan request: " + externalParameter.toString());
		context.addAssignFromInput2VariableToMainAssign(externalParameter, variable);
		createFunctionInternalExternalPropsInput.put(externalParameter, variable);
	    } else {
		createFunctionInternalExternalPropsInput.put(externalParameter, variable);
	    }
	    // add function url and function code
	    createFunctionInternalExternalPropsInput.put(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_FUNCTIONURL,
		    functionUrlVar);
	    createFunctionInternalExternalPropsInput.put(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_FUNCTIONCODE,
		    functionCodeVar);
	    // add the found properties of the ServerlessFunction nodeTemplate
	    createFunctionInternalExternalPropsOutput.put(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_FUNCTIONNAME,
		    functionNamePropWrapper);
	    createFunctionInternalExternalPropsOutput.put(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_RUNTIME,
		    runtimePropWrapper);
	    createFunctionInternalExternalPropsOutput.put(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_FUNCTIONURL,
		    functionUrlVar);
	    createFunctionInternalExternalPropsOutput.put(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_FUNCTIONCODE,
		    functionCodeVar);

	}
	LOG.debug("Now it should invoke serverless function deployment");
	/*
	 * invoke the operation to deploy a function on the underlying
	 * ServerlessPlatform nodeTempalte
	 */
	this.invokerOpPlugin.handle(context, "OpenWhiskPlatform", true,
		Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_SERVERLESS_DEPLOYFUNCTION,
		Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_SERVERLESS, "planCallbackAddress_invoker",
		createFunctionInternalExternalPropsInput, createFunctionInternalExternalPropsOutput, false);
	LOG.debug("Invocation of deployment of serverless function was successful!");

    }

    /*
     * this method handles the invocation of the deployment of a http event
     * nodeTemplate
     *
     * @param context: the respective BPELPlanContext of the ServiceTemplate
     *
     * @param nodeTemplate: the nodeTemplate of the HTTPEvent nodeType which should
     * be handled
     */
    public void handleHttpEventDeploymentInvocation(final BPELPlanContext context,
	    final AbstractNodeTemplate nodeTemplate, final AbstractRelationshipTemplate eventCon) {

	/*
	 * wrap the properties of a HTTPEvent nodeTemplate which are later mapped onto
	 * the input parameters of the management operations of the underlying
	 * ServerlessPlatform nodeTempalte
	 */

	Variable toTriggerFunctionNamePropWrapper = null;

	for (final String functionName : org.opentosca.container.core.tosca.convention.Utils
		.getSupportedServerlessFunctionNamePropertyNames()) {

	    for (final AbstractRelationshipTemplate triggers : eventCon.getSource().getOutgoingRelations()) {
		if (triggers.getTarget().getType().getId().equals(Types.serverlessFunctionNodeType)) {
		    LOG.debug("Serverless Function which is connected with HTTP EVENT found!");

		    for (final String toTriggerFunctionName : org.opentosca.container.core.tosca.convention.Utils
			    .getSupportedServerlessFunctionNamePropertyNames()) {
			toTriggerFunctionNamePropWrapper = context.getPropertyVariable(nodeTemplate,
				toTriggerFunctionName);
			LOG.debug("Name of serverless function is as follows: " + functionName);
			if (toTriggerFunctionNamePropWrapper == null) {
			    toTriggerFunctionNamePropWrapper = context.getPropertyVariable(toTriggerFunctionName, true);
			} else {
			    break;
			}

			if (toTriggerFunctionNamePropWrapper == null) {
			    BPELServerlessPluginHandler.LOG.warn("Serverless Function to be triggered is not defined");

			}

		    }
		}
	    }
	}

	Variable eventNamePropWrapper = null;

	for (final String eventName : org.opentosca.container.core.tosca.convention.Utils
		.getSupportedEventNamePropertyNames()) {

	    eventNamePropWrapper = context.getPropertyVariable(eventCon.getSource(), eventName);
	    if (eventNamePropWrapper == null) {
		eventNamePropWrapper = context.getPropertyVariable(eventName, true);
	    } else {
		break;
	    }
	}

	if (eventNamePropWrapper == null) {
	    BPELServerlessPluginHandler.LOG
		    .warn("HTTP EVENT Node doesn't have EventName property, altough it has the proper NodeType");
	    // return false;
	}

	Variable createHttpEventPropWrapper = null;

	for (final String createHttpEvent : org.opentosca.container.core.tosca.convention.Utils
		.getSupportedhttpEventCreateHTTPEventPropertyNames()) {

	    createHttpEventPropWrapper = context.getPropertyVariable(eventCon.getSource(), createHttpEvent);
	    if (createHttpEventPropWrapper == null) {
		createHttpEventPropWrapper = context.getPropertyVariable(createHttpEvent, true);
	    } else {
		break;
	    }
	}

	if (createHttpEventPropWrapper == null) {
	    BPELServerlessPluginHandler.LOG
		    .warn("HTTP EVENT Node doesn't have CreateHttpEvent property, altough it has the proper NodeType");
	    // return false;
	}

	Variable apiIDPropWrapper = null;

	for (final String apiID : org.opentosca.container.core.tosca.convention.Utils
		.getSupportedhttpEventAPIIDPropertyNames()) {

	    apiIDPropWrapper = context.getPropertyVariable(eventCon.getSource(), apiID);
	    if (apiIDPropWrapper == null) {
		apiIDPropWrapper = context.getPropertyVariable(apiID, true);
	    } else {
		break;
	    }
	}

	if (apiIDPropWrapper == null) {
	    BPELServerlessPluginHandler.LOG
		    .warn("HTTP EVENT Node doesn't have API_ID property, altough it has the proper NodeType");
	    // return false;
	}

	Variable resourceIDPropWrapper = null;

	for (final String resourceID : org.opentosca.container.core.tosca.convention.Utils
		.getSupportedhttpEventResourceIDPropertyNames()) {

	    resourceIDPropWrapper = context.getPropertyVariable(eventCon.getSource(), resourceID);
	    if (resourceIDPropWrapper == null) {
		resourceIDPropWrapper = context.getPropertyVariable(resourceID, true);
	    } else {
		break;
	    }
	}

	if (resourceIDPropWrapper == null) {
	    BPELServerlessPluginHandler.LOG
		    .warn("HTTP EVENT Node doesn't have Resource_ID property, altough it has the proper NodeType");
	    // return false;
	}

	Variable httpMethodPropWrapper = null;

	for (final String httpMethod : org.opentosca.container.core.tosca.convention.Utils
		.getSupportedhttpEventHttpMethodPropertyNames()) {

	    httpMethodPropWrapper = context.getPropertyVariable(eventCon.getSource(), httpMethod);
	    if (httpMethodPropWrapper == null) {
		httpMethodPropWrapper = context.getPropertyVariable(httpMethod, true);
	    } else {
		break;
	    }
	}

	if (httpMethodPropWrapper == null) {
	    BPELServerlessPluginHandler.LOG
		    .warn("HTTP EVENT Node doesn't have HTTPMethod property, altough it has the proper NodeType");
	    // return false;
	}

	Variable authTypePropWrapper = null;

	for (final String authType : org.opentosca.container.core.tosca.convention.Utils
		.getSupportedhttpEventAuthTypePropertyNames()) {

	    authTypePropWrapper = context.getPropertyVariable(eventCon.getSource(), authType);
	    if (authTypePropWrapper == null) {
		authTypePropWrapper = context.getPropertyVariable(authType, true);
	    } else {
		break;
	    }
	}

	if (authTypePropWrapper == null) {
	    BPELServerlessPluginHandler.LOG
		    .warn("HTTP EVENT Node doesn't have AuthType property, altough it has the proper NodeType");
	    // return false;
	}

	Variable functionURIPropWrapper = null;

	for (final String functionURI : org.opentosca.container.core.tosca.convention.Utils
		.getSupportedhttpEventFunctionURIPropertyNames()) {

	    functionURIPropWrapper = context.getPropertyVariable(eventCon.getSource(), functionURI);
	    if (functionURIPropWrapper == null) {
		functionURIPropWrapper = context.getPropertyVariable(functionURI, true);
	    } else {
		break;
	    }
	}

	if (functionURIPropWrapper == null) {
	    BPELServerlessPluginHandler.LOG
		    .warn("HTTP EVENT Node doesn't have FunctionURI property, altough it has the proper NodeType");
	    // return false;
	}
	// add plan callback address to plan input message
	LOG.debug("Adding plan callback address field to plan input");
	context.addStringValueToPlanRequest("planCallbackAddress_invoker");

	// add csarEntryPoint to plan input message
	LOG.debug("Adding csarEntryPoint field to plan input");
	context.addStringValueToPlanRequest("csarEntrypoint");

	final Map<String, Variable> createHttpEventInternalExternalPropsInput = new HashMap<>();
	final Map<String, Variable> createHttpEventInternalExternalPropsOutput = new HashMap<>();

	/*
	 * iterate over every neccesary parameter to map onto the input parameters of
	 * the management operations of the underlying ServerlessPlatform nodeTemplate
	 */
	for (final String externalParameter : BPELServerlessPluginHandler.createHttpEventInstanceExternalInputParams) {

	    LOG.debug("External parameter to map is: " + externalParameter);
	    Variable variable = null;

	    // get the name of the serverless function to connect the trigger with
	    if (!externalParameter.equals("FunctionName")) {
		variable = context.getPropertyVariable(eventCon.getSource(), externalParameter);

		if (variable == null) {
		    variable = context.getPropertyVariable(externalParameter, true);
		    LOG.debug("Property variable is: " + variable.toString());
		} else {
		    BPELServerlessPluginHandler.LOG.debug("Found property variable " + externalParameter);
		}
	    } else {
		// handle the other properties
		variable = context.getPropertyVariable(nodeTemplate, externalParameter);
		if (variable == null) {
		    variable = context.getPropertyVariable(externalParameter, true);
		    LOG.debug("Property variable is: " + variable.toString());
		} else {
		    BPELServerlessPluginHandler.LOG.debug("Found property variable " + externalParameter);
		}
	    }

	    createHttpEventInternalExternalPropsInput.put(externalParameter, variable);

	    if (BPELPlanContext.isVariableValueEmpty(variable, context)) {
		context.addStringValueToPlanRequest(externalParameter);
		context.addAssignFromInput2VariableToMainAssign(externalParameter, variable);
		createHttpEventInternalExternalPropsInput.put(externalParameter, variable);
	    } else {
		createHttpEventInternalExternalPropsInput.put(externalParameter, variable);
	    }
	    // add the found properties of the HttpEvent nodeTemplate
	    createHttpEventInternalExternalPropsOutput.put(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_EVENTNAME,
		    eventNamePropWrapper);
	    createHttpEventInternalExternalPropsOutput.put(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_APIID,
		    apiIDPropWrapper);
	    createHttpEventInternalExternalPropsOutput.put(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_RESOURCEID,
		    resourceIDPropWrapper);
	    createHttpEventInternalExternalPropsOutput
		    .put(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_CREATEHTTPEVENT, createHttpEventPropWrapper);
	    createHttpEventInternalExternalPropsOutput.put(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_HTTPMETHOD,
		    httpMethodPropWrapper);
	    createHttpEventInternalExternalPropsOutput.put(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_FUNCTIONURI,
		    functionURIPropWrapper);
	    createHttpEventInternalExternalPropsOutput.put(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_AUTHTYPE,
		    authTypePropWrapper);
	}
	/*
	 * invoke the operation to deploy a function on the underlying
	 * ServerlessPlatform nodeTempalte
	 */
	this.invokerOpPlugin.handle(context, "OpenWhiskPlatform", true,
		Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_SERVERLESS_DEPLOYHTTPEVENT,
		Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_SERVERLESS, "planCallbackAddress_invoker",
		createHttpEventInternalExternalPropsInput, createHttpEventInternalExternalPropsOutput, false);
	LOG.debug("Invocation of deployment of http event was successful!");
    }

    /*
     * this method handles the invocation of the deployment of a timer event
     * nodeTemplate
     *
     * @param context: the respective BPELPlanContext of the ServiceTemplate
     *
     * @param nodeTemplate: the nodeTemplate of the TimerEvent nodeType which should
     * be handled
     */
    public void handleTimerEventDeploymentInvocation(final BPELPlanContext context,
	    final AbstractNodeTemplate nodeTemplate, final AbstractRelationshipTemplate eventCon) {

	/*
	 * wrap the properties of a TimerEvent nodeTemplate which are later mapped onto
	 * the input parameters of the management operations of the underlying
	 * ServerlessPlatform nodeTempalte
	 */
	Variable toTriggerFunctionNamePropWrapper = null;

	for (final String functionName : org.opentosca.container.core.tosca.convention.Utils
		.getSupportedServerlessFunctionNamePropertyNames()) {

	    for (final AbstractRelationshipTemplate triggers : eventCon.getSource().getOutgoingRelations()) {
		if (triggers.getTarget().getType().getId().equals(Types.serverlessFunctionNodeType)) {
		    LOG.debug("Serverless Function which is connected with Timer EVENT found!");

		    for (final String toTriggerFunctionName : org.opentosca.container.core.tosca.convention.Utils
			    .getSupportedServerlessFunctionNamePropertyNames()) {
			toTriggerFunctionNamePropWrapper = context.getPropertyVariable(nodeTemplate,
				toTriggerFunctionName);
			LOG.debug("Name of serverless function is as follows: " + functionName);
			if (toTriggerFunctionNamePropWrapper == null) {
			    toTriggerFunctionNamePropWrapper = context.getPropertyVariable(toTriggerFunctionName, true);
			} else {
			    break;
			}

			if (toTriggerFunctionNamePropWrapper == null) {
			    BPELServerlessPluginHandler.LOG.warn("Serverless Function to be triggered is not defined");

			}

		    }
		}
	    }
	}

	Variable eventNamePropWrapper = null;

	for (final String eventName : org.opentosca.container.core.tosca.convention.Utils
		.getSupportedEventNamePropertyNames()) {

	    eventNamePropWrapper = context.getPropertyVariable(eventCon.getSource(), eventName);
	    if (eventNamePropWrapper == null) {
		eventNamePropWrapper = context.getPropertyVariable(eventName, true);
	    } else {
		break;
	    }
	}

	if (eventNamePropWrapper == null) {
	    BPELServerlessPluginHandler.LOG
		    .warn("Timer EVENT Node doesn't have EventName property, altough it has the proper NodeType");

	}

	Variable cronPropWrapper = null;

	for (final String cron : org.opentosca.container.core.tosca.convention.Utils.getSupportedCRONPropertyNames()) {

	    cronPropWrapper = context.getPropertyVariable(eventCon.getSource(), cron);
	    if (cronPropWrapper == null) {
		cronPropWrapper = context.getPropertyVariable(cron, true);
	    } else {
		break;
	    }
	}

	if (cronPropWrapper == null) {
	    BPELServerlessPluginHandler.LOG
		    .warn("Timer EVENT Node doesn't have CRON property, altough it has the proper NodeType");

	}
	// add plan callback address field to input message
	LOG.debug("Adding plan callback address field to plan input");
	context.addStringValueToPlanRequest("planCallbackAddress_invoker");

	// add csarEntryPoint to plan input message
	LOG.debug("Adding csarEntryPoint field to plan input");
	context.addStringValueToPlanRequest("csarEntrypoint");

	final Map<String, Variable> createTimerEventInternalExternalPropsInput = new HashMap<>();
	final Map<String, Variable> createTimerEventInternalExternalPropsOutput = new HashMap<>();

	/*
	 * iterate over every neccesary parameter to map onto the input parameters of
	 * the management operations of the underlying ServerlessPlatform nodeTemplate
	 */
	for (final String externalParameter : BPELServerlessPluginHandler.createTimerEventInstanceExternalInputParams) {
	    // find variable for input param

	    LOG.debug("External parameter to map is: " + externalParameter);
	    Variable variable = null;
	    // get the name of the serverless function to connect the trigger with
	    if (!externalParameter.equals("FunctionName")) {
		variable = context.getPropertyVariable(eventCon.getSource(), externalParameter);

		if (variable == null) {
		    variable = context.getPropertyVariable(externalParameter, true);
		    LOG.debug("Property variable is: " + variable.toString());
		} else {
		    BPELServerlessPluginHandler.LOG.debug("Found property variable " + externalParameter);
		}
		// handle the other properties
	    } else {
		variable = context.getPropertyVariable(nodeTemplate, externalParameter);
		if (variable == null) {
		    variable = context.getPropertyVariable(externalParameter, true);
		    LOG.debug("Property variable is: " + variable.toString());
		} else {
		    BPELServerlessPluginHandler.LOG.debug("Found property variable " + externalParameter);
		}
	    }
	    createTimerEventInternalExternalPropsInput.put(externalParameter, variable);

	    if (BPELPlanContext.isVariableValueEmpty(variable, context)) {
		context.addStringValueToPlanRequest(externalParameter);
		context.addAssignFromInput2VariableToMainAssign(externalParameter, variable);
		createTimerEventInternalExternalPropsInput.put(externalParameter, variable);
	    } else {
		createTimerEventInternalExternalPropsInput.put(externalParameter, variable);
	    }
	    // add the found properties of the TimerEvent nodeTemplate
	    createTimerEventInternalExternalPropsOutput.put(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_FUNCTIONNAME,
		    toTriggerFunctionNamePropWrapper);
	    createTimerEventInternalExternalPropsOutput.put(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_EVENTNAME,
		    eventNamePropWrapper);
	    createTimerEventInternalExternalPropsOutput.put(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_CRON,
		    cronPropWrapper);
	}
	/*
	 * invoke the operation to deploy a function on the underlying
	 * ServerlessPlatform nodeTempalte
	 */
	this.invokerOpPlugin.handle(context, "OpenWhiskPlatform", true,
		Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_SERVERLESS_DEPLOYTIMEREVENT,
		Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_SERVERLESS, "planCallbackAddress_invoker",
		createTimerEventInternalExternalPropsInput, createTimerEventInternalExternalPropsOutput, false);
	LOG.debug("Invocation of deployment of timer event was successful!");
    }

    /*
     * this method handles the invocation of the deployment of a database event
     * nodeTemplate
     *
     * @param context: the respective BPELPlanContext of the ServiceTemplate
     *
     * @param nodeTemplate: the nodeTemplate of the DatabaseEvent nodeType which
     * should be handled
     */
    public void handleDatabaseEventDeploymentInvocation(final BPELPlanContext context,
	    final AbstractNodeTemplate nodeTemplate, final AbstractRelationshipTemplate eventCon) {

	/*
	 * wrap the properties of a DatabaseEvent nodeTemplate which are later mapped
	 * onto the input parameters of the management operations of the underlying
	 * ServerlessPlatform nodeTempalte
	 */
	Variable toTriggerFunctionNamePropWrapper = null;

	for (final String functionName : org.opentosca.container.core.tosca.convention.Utils
		.getSupportedServerlessFunctionNamePropertyNames()) {

	    for (final AbstractRelationshipTemplate triggers : eventCon.getSource().getOutgoingRelations()) {
		if (triggers.getTarget().getType().getId().equals(Types.serverlessFunctionNodeType)) {
		    LOG.debug("Serverless Function which is connected with Database EVENT found!");

		    for (final String toTriggerFunctionName : org.opentosca.container.core.tosca.convention.Utils
			    .getSupportedServerlessFunctionNamePropertyNames()) {
			toTriggerFunctionNamePropWrapper = context.getPropertyVariable(nodeTemplate,
				toTriggerFunctionName);
			LOG.debug("Name of serverless function is as follows: " + functionName);
			if (toTriggerFunctionNamePropWrapper == null) {
			    toTriggerFunctionNamePropWrapper = context.getPropertyVariable(toTriggerFunctionName, true);
			} else {
			    break;
			}

			if (toTriggerFunctionNamePropWrapper == null) {
			    BPELServerlessPluginHandler.LOG.warn("Serverless Function to be triggered is not defined");

			}

		    }
		}
	    }
	}

	Variable eventNamePropWrapper = null;

	for (final String eventName : org.opentosca.container.core.tosca.convention.Utils
		.getSupportedEventNamePropertyNames()) {

	    eventNamePropWrapper = context.getPropertyVariable(eventCon.getSource(), eventName);
	    if (eventNamePropWrapper == null) {
		eventNamePropWrapper = context.getPropertyVariable(eventName, true);
	    } else {
		break;
	    }
	}

	if (eventNamePropWrapper == null) {
	    BPELServerlessPluginHandler.LOG
		    .warn("Database EVENT Node doesn't have EventName property, altough it has the proper NodeType");

	}

	Variable databaseNamePropWrapper = null;

	for (final String databaseName : org.opentosca.container.core.tosca.convention.Utils
		.getSupporteddatabaseEventdatabaseNamePropertyNames()) {

	    databaseNamePropWrapper = context.getPropertyVariable(eventCon.getSource(), databaseName);
	    if (databaseNamePropWrapper == null) {
		databaseNamePropWrapper = context.getPropertyVariable(databaseName, true);
	    } else {
		break;
	    }
	}

	if (databaseNamePropWrapper == null) {
	    BPELServerlessPluginHandler.LOG
		    .warn("Database EVENT Node doesn't have Databasename property, altough it has the proper NodeType");
	}

	Variable databaseHostPropWrapper = null;

	for (final String databaseHost : org.opentosca.container.core.tosca.convention.Utils
		.getSupporteddatabaseEventdatabaseHostUrlPropertyNames()) {

	    databaseHostPropWrapper = context.getPropertyVariable(eventCon.getSource(), databaseHost);
	    if (databaseHostPropWrapper == null) {
		databaseHostPropWrapper = context.getPropertyVariable(databaseHost, true);
	    } else {
		break;
	    }
	}

	if (databaseHostPropWrapper == null) {
	    BPELServerlessPluginHandler.LOG.warn(
		    "Database EVENT Node doesn't have DatabaseHostURL property, altough it has the proper NodeType");

	}

	Variable databaseUserPropWrapper = null;

	for (final String databaseUser : org.opentosca.container.core.tosca.convention.Utils
		.getSupporteddatabaseEventdatabaseUsernamePropertyNames()) {

	    databaseUserPropWrapper = context.getPropertyVariable(eventCon.getSource(), databaseUser);
	    if (databaseUserPropWrapper == null) {
		databaseUserPropWrapper = context.getPropertyVariable(databaseUser, true);
	    } else {
		break;
	    }
	}

	if (databaseUserPropWrapper == null) {
	    BPELServerlessPluginHandler.LOG.warn(
		    "Database EVENT Node doesn't have Database User property, altough it has the proper NodeType");

	}

	Variable databasePwPropWrapper = null;

	for (final String databasePw : org.opentosca.container.core.tosca.convention.Utils
		.getSupporteddatabaseEventdatabasePasswordPropertyNames()) {

	    databasePwPropWrapper = context.getPropertyVariable(eventCon.getSource(), databasePw);
	    if (databasePwPropWrapper == null) {
		databasePwPropWrapper = context.getPropertyVariable(databasePw, true);
	    } else {
		break;
	    }
	}

	if (databasePwPropWrapper == null) {
	    BPELServerlessPluginHandler.LOG.warn(
		    "Database EVENT Node doesn't have Database Password property, altough it has the proper NodeType");

	}

	Variable typeOfChangePropWrapper = null;

	for (final String typeOfChange : org.opentosca.container.core.tosca.convention.Utils
		.getSupporteddatabaeEventTypeOfChangePropertyNames()) {

	    typeOfChangePropWrapper = context.getPropertyVariable(eventCon.getSource(), typeOfChange);
	    if (typeOfChangePropWrapper == null) {
		typeOfChangePropWrapper = context.getPropertyVariable(typeOfChange, true);
	    } else {
		break;
	    }
	}

	if (typeOfChangePropWrapper == null) {
	    BPELServerlessPluginHandler.LOG.warn(
		    "Database EVENT Node doesn't have Type of change property, altough it has the proper NodeType");
	}

	Variable startPosPropWrapper = null;

	for (final String startPos : org.opentosca.container.core.tosca.convention.Utils
		.getSupporteddatabaseEventStartPosPropertyNames()) {

	    startPosPropWrapper = context.getPropertyVariable(eventCon.getSource(), startPos);
	    if (startPosPropWrapper == null) {
		startPosPropWrapper = context.getPropertyVariable(startPos, true);
	    } else {
		break;
	    }
	}

	if (startPosPropWrapper == null) {
	    BPELServerlessPluginHandler.LOG.warn(
		    "Database EVENT Node doesn't have starting position property, altough it has the proper NodeType");

	}
	// add plan callback address to plan input message
	LOG.debug("Adding plan callback address field to plan input");
	context.addStringValueToPlanRequest("planCallbackAddress_invoker");

	// add csarEntryPoint to plan input message
	LOG.debug("Adding csarEntryPoint field to plan input");
	context.addStringValueToPlanRequest("csarEntrypoint");

	final Map<String, Variable> createDatabaseEventInternalExternalPropsInput = new HashMap<>();
	final Map<String, Variable> createDatabaseEventInternalExternalPropsOutput = new HashMap<>();

	for (final String externalParameter : BPELServerlessPluginHandler.createDatabaseEventInstanceExternalInputParams) {

	    LOG.debug("External parameter to map is: " + externalParameter);
	    Variable variable = null;
	    // get the name of the serverless function to connect the trigger with
	    if (!externalParameter.equals("FunctionName")) {
		variable = context.getPropertyVariable(eventCon.getSource(), externalParameter);

		if (variable == null) {
		    variable = context.getPropertyVariable(externalParameter, true);
		    LOG.debug("Property variable is: " + variable.toString());
		} else {
		    BPELServerlessPluginHandler.LOG.debug("Found property variable " + externalParameter);
		}
	    } else {
		// handle the other properties
		variable = context.getPropertyVariable(nodeTemplate, externalParameter);
		if (variable == null) {
		    variable = context.getPropertyVariable(externalParameter, true);
		    LOG.debug("Property variable is: " + variable.toString());
		} else {
		    BPELServerlessPluginHandler.LOG.debug("Found property variable " + externalParameter);
		}
	    }

	    createDatabaseEventInternalExternalPropsInput.put(externalParameter, variable);

	    if (BPELPlanContext.isVariableValueEmpty(variable, context)) {
		context.addStringValueToPlanRequest(externalParameter);
		context.addAssignFromInput2VariableToMainAssign(externalParameter, variable);
		createDatabaseEventInternalExternalPropsInput.put(externalParameter, variable);
	    } else {
		createDatabaseEventInternalExternalPropsInput.put(externalParameter, variable);
	    }
	    // add the found properties of the DatabaseEvent nodeTemplate
	    createDatabaseEventInternalExternalPropsOutput.put(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_EVENTNAME,
		    eventNamePropWrapper);
	    createDatabaseEventInternalExternalPropsOutput
		    .put(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_DATABASENAME, databaseNamePropWrapper);
	    createDatabaseEventInternalExternalPropsOutput
		    .put(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_DATABASEHOSTURL, databaseHostPropWrapper);
	    createDatabaseEventInternalExternalPropsOutput
		    .put(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_DATABASEUSER, databaseUserPropWrapper);
	    createDatabaseEventInternalExternalPropsOutput.put(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_DATABASEPW,
		    databasePwPropWrapper);
	    createDatabaseEventInternalExternalPropsOutput
		    .put(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_TYPEOFCHANGE, typeOfChangePropWrapper);
	    createDatabaseEventInternalExternalPropsOutput.put(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_STARTPOS,
		    startPosPropWrapper);
	}
	/*
	 * invoke the operation to deploy a function on the underlying
	 * ServerlessPlatform nodeTempalte
	 */
	this.invokerOpPlugin.handle(context, "OpenWhiskPlatform", true,
		Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_SERVERLESS_DEPLOYDATABASEEVENT,
		Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_SERVERLESS, "planCallbackAddress_invoker",
		createDatabaseEventInternalExternalPropsInput, createDatabaseEventInternalExternalPropsOutput, false);
	LOG.debug("Invocation of deployment of databae event was successful!");
    }

    /*
     * this method handles the invocation of the deployment of a database event
     * nodeTemplate
     *
     * @param context: the respective BPELPlanContext of the ServiceTemplate
     *
     * @param nodeTemplate: the nodeTemplate of the BloblstorageEvent nodeType which
     * should be handled
     */
    public void handleBlobstorageEventDeploymentInvocation(final BPELPlanContext context,
	    final AbstractNodeTemplate nodeTemplate, final AbstractRelationshipTemplate eventCon) {

	/*
	 * wrap the properties of a BloblstorageEvent nodeTemplate which are later
	 * mapped onto the input parameters of the management operations of the
	 * underlying ServerlessPlatform nodeTempalte
	 */

	Variable toTriggerFunctionNamePropWrapper = null;

	for (final String functionName : org.opentosca.container.core.tosca.convention.Utils
		.getSupportedServerlessFunctionNamePropertyNames()) {

	    for (final AbstractRelationshipTemplate triggers : eventCon.getSource().getOutgoingRelations()) {
		if (triggers.getTarget().getType().getId().equals(Types.serverlessFunctionNodeType)) {
		    LOG.debug("Serverless Function which is connected with HTTP EVENT found!");

		    for (final String toTriggerFunctionName : org.opentosca.container.core.tosca.convention.Utils
			    .getSupportedServerlessFunctionNamePropertyNames()) {
			toTriggerFunctionNamePropWrapper = context.getPropertyVariable(nodeTemplate,
				toTriggerFunctionName);
			LOG.debug("Name of serverless function is as follows: " + functionName);
			if (toTriggerFunctionNamePropWrapper == null) {
			    toTriggerFunctionNamePropWrapper = context.getPropertyVariable(toTriggerFunctionName, true);
			} else {
			    break;
			}

			if (toTriggerFunctionNamePropWrapper == null) {
			    BPELServerlessPluginHandler.LOG.warn("Serverless Function to be triggered is not defined");

			}

		    }
		}
	    }
	}

	Variable eventNamePropWrapper = null;

	for (final String eventName : org.opentosca.container.core.tosca.convention.Utils
		.getSupportedEventNamePropertyNames()) {

	    eventNamePropWrapper = context.getPropertyVariable(eventCon.getSource(), eventName);
	    if (eventNamePropWrapper == null) {
		eventNamePropWrapper = context.getPropertyVariable(eventName, true);
	    } else {
		break;
	    }
	}

	if (eventNamePropWrapper == null) {
	    BPELServerlessPluginHandler.LOG
		    .warn("Blobstorage EVENT Node doesn't have EventName property, altough it has the proper NodeType");
	}

	Variable bucketNamePropWrapper = null;

	for (final String bucketName : org.opentosca.container.core.tosca.convention.Utils
		.getSupportedblobstorageEventBucketNamePropertyNames()) {

	    bucketNamePropWrapper = context.getPropertyVariable(eventCon.getSource(), bucketName);
	    if (bucketNamePropWrapper == null) {
		bucketNamePropWrapper = context.getPropertyVariable(bucketName, true);
	    } else {
		break;
	    }
	}

	if (bucketNamePropWrapper == null) {
	    BPELServerlessPluginHandler.LOG.warn(
		    "Blobstorage EVENT Node doesn't have Bucket Name property, altough it has the proper NodeType");
	}

	Variable eventTypePropWrapper = null;

	for (final String eventType : org.opentosca.container.core.tosca.convention.Utils
		.getSupportedblobstorageEventEventTypePropertyNames()) {

	    eventTypePropWrapper = context.getPropertyVariable(eventCon.getSource(), eventType);
	    if (eventTypePropWrapper == null) {
		eventTypePropWrapper = context.getPropertyVariable(eventType, true);
	    } else {
		break;
	    }
	}

	if (eventTypePropWrapper == null) {
	    BPELServerlessPluginHandler.LOG.warn(
		    "Blobstorage EVENT Node doesn't have event type property, altough it has the proper NodeType");

	}

	// add plan callback address to plan input message
	LOG.debug("Adding plan callback address field to plan input");
	context.addStringValueToPlanRequest("planCallbackAddress_invoker");

	// add csarEntryPoint to plan input message
	LOG.debug("Adding csarEntryPoint field to plan input");
	context.addStringValueToPlanRequest("csarEntrypoint");

	final Map<String, Variable> createBlobstorageEventInternalExternalPropsInput = new HashMap<>();
	final Map<String, Variable> createBlobstorageEventInternalExternalPropsOutput = new HashMap<>();

	for (final String externalParameter : BPELServerlessPluginHandler.createBlobstorageEventInstanceExternalInputParams) {
	    // find variable for input param
	    LOG.debug("External parameter to map is: " + externalParameter);
	    Variable variable = null;
	    // get the name of the serverless function to connect the trigger with
	    if (!externalParameter.equals("FunctionName")) {
		variable = context.getPropertyVariable(eventCon.getSource(), externalParameter);

		if (variable == null) {
		    variable = context.getPropertyVariable(externalParameter, true);
		    LOG.debug("Property variable is: " + variable.toString());
		} else {
		    BPELServerlessPluginHandler.LOG.debug("Found property variable " + externalParameter);
		}
	    } else {
		// handle the other properties
		variable = context.getPropertyVariable(nodeTemplate, externalParameter);
		if (variable == null) {
		    variable = context.getPropertyVariable(externalParameter, true);
		    LOG.debug("Property variable is: " + variable.toString());
		} else {
		    BPELServerlessPluginHandler.LOG.debug("Found property variable " + externalParameter);
		}
	    }

	    createBlobstorageEventInternalExternalPropsInput.put(externalParameter, variable);

	    if (BPELPlanContext.isVariableValueEmpty(variable, context)) {
		context.addStringValueToPlanRequest(externalParameter);
		context.addAssignFromInput2VariableToMainAssign(externalParameter, variable);
		createBlobstorageEventInternalExternalPropsInput.put(externalParameter, variable);
	    } else {
		createBlobstorageEventInternalExternalPropsInput.put(externalParameter, variable);
	    }
	    // add the found properties of the BlobstorageEvent nodeTemplate
	    createBlobstorageEventInternalExternalPropsOutput
		    .put(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_EVENTNAME, eventNamePropWrapper);
	    createBlobstorageEventInternalExternalPropsOutput
		    .put(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_BUCKETNAME, bucketNamePropWrapper);
	    createBlobstorageEventInternalExternalPropsOutput
		    .put(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_EVENTTYPE, eventTypePropWrapper);
	}
	/*
	 * invoke the operation to deploy a function on the underlying
	 * ServerlessPlatform nodeTempalte
	 */
	this.invokerOpPlugin.handle(context, "OpenWhiskPlatform", true,
		Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_SERVERLESS_DEPLOYBLOBSTORAGEEVENT,
		Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_SERVERLESS, "planCallbackAddress_invoker",
		createBlobstorageEventInternalExternalPropsInput, createBlobstorageEventInternalExternalPropsOutput,
		false);
	LOG.debug("Invocation of deployment of blobstorage event was successful!");
    }

    /*
     * this method handles the invocation of the deployment of a database event
     * nodeTemplate
     *
     * @param context: the respective BPELPlanContext of the ServiceTemplate
     *
     * @param nodeTemplate: the nodeTemplate of the PubSubEvent nodeType which
     * should be handled
     */
    public void handlePubSubEventDeploymentInvocation(final BPELPlanContext context,
	    final AbstractNodeTemplate nodeTemplate, final AbstractRelationshipTemplate eventCon) {

	/*
	 * wrap the properties of a BloblstorageEvent nodeTemplate which are later
	 * mapped onto the input parameters of the management operations of the
	 * underlying ServerlessPlatform nodeTempalte
	 */
	Variable toTriggerFunctionNamePropWrapper = null;

	for (final String functionName : org.opentosca.container.core.tosca.convention.Utils
		.getSupportedServerlessFunctionNamePropertyNames()) {

	    for (final AbstractRelationshipTemplate triggers : eventCon.getSource().getOutgoingRelations()) {
		if (triggers.getTarget().getType().getId().equals(Types.serverlessFunctionNodeType)) {
		    LOG.debug("Serverless Function which is connected with HTTP EVENT found!");

		    for (final String toTriggerFunctionName : org.opentosca.container.core.tosca.convention.Utils
			    .getSupportedServerlessFunctionNamePropertyNames()) {
			toTriggerFunctionNamePropWrapper = context.getPropertyVariable(nodeTemplate,
				toTriggerFunctionName);
			LOG.debug("Name of serverless function is as follows: " + functionName);
			if (toTriggerFunctionNamePropWrapper == null) {
			    toTriggerFunctionNamePropWrapper = context.getPropertyVariable(toTriggerFunctionName, true);
			} else {
			    break;
			}

			if (toTriggerFunctionNamePropWrapper == null) {
			    BPELServerlessPluginHandler.LOG.warn("Serverless Function to be triggered is not defined");

			}

		    }
		}
	    }
	}

	Variable eventNamePropWrapper = null;

	for (final String eventName : org.opentosca.container.core.tosca.convention.Utils
		.getSupportedEventNamePropertyNames()) {

	    eventNamePropWrapper = context.getPropertyVariable(eventCon.getSource(), eventName);
	    if (eventNamePropWrapper == null) {
		eventNamePropWrapper = context.getPropertyVariable(eventName, true);
	    } else {
		break;
	    }
	}

	if (eventNamePropWrapper == null) {
	    BPELServerlessPluginHandler.LOG
		    .warn("PubSub EVENT Node doesn't have EventName property, altough it has the proper NodeType");

	}

	Variable topicPropWrapper = null;

	for (final String topic : org.opentosca.container.core.tosca.convention.Utils
		.getSupportedpubsubEventTopicNamePropertyNames()) {

	    topicPropWrapper = context.getPropertyVariable(eventCon.getSource(), topic);
	    if (topicPropWrapper == null) {
		topicPropWrapper = context.getPropertyVariable(topic, true);
	    } else {
		break;
	    }
	}

	if (topicPropWrapper == null) {
	    BPELServerlessPluginHandler.LOG
		    .warn("PubSub EVENT Node doesn't have Topic property, altough it has the proper NodeType");

	}

	Variable messageHubInstancePropWrapper = null;

	for (final String messageHubInstance : org.opentosca.container.core.tosca.convention.Utils
		.getSupportedpubsubEventMessageHubInstanceNamePropertyNames()) {

	    messageHubInstancePropWrapper = context.getPropertyVariable(eventCon.getSource(), messageHubInstance);
	    if (messageHubInstancePropWrapper == null) {
		messageHubInstancePropWrapper = context.getPropertyVariable(messageHubInstance, true);
	    } else {
		break;
	    }
	}

	if (messageHubInstancePropWrapper == null) {
	    BPELServerlessPluginHandler.LOG.warn(
		    "PubSub EVENT Node doesn't have MessageHub Instance name property, altough it has the proper NodeType");

	}

	// add plan callback address to input message
	LOG.debug("Adding plan callback address field to plan input");
	context.addStringValueToPlanRequest("planCallbackAddress_invoker");

	// add csarEntryPoint to plan input message
	LOG.debug("Adding csarEntryPoint field to plan input");
	context.addStringValueToPlanRequest("csarEntrypoint");

	final Map<String, Variable> createPubSubEventInternalExternalPropsInput = new HashMap<>();
	final Map<String, Variable> createPubSubEventInternalExternalPropsOutput = new HashMap<>();

	for (final String externalParameter : BPELServerlessPluginHandler.createPubSubEventInstanceExternalInputParams) {

	    LOG.debug("External parameter to map is: " + externalParameter);
	    Variable variable = null;
	    // get the name of the serverless function to connect the trigger with
	    if (!externalParameter.equals("FunctionName")) {
		variable = context.getPropertyVariable(eventCon.getSource(), externalParameter);

		if (variable == null) {
		    variable = context.getPropertyVariable(externalParameter, true);
		    LOG.debug("Property variable is: " + variable.toString());
		} else {
		    BPELServerlessPluginHandler.LOG.debug("Found property variable " + externalParameter);
		}
	    } else {
		// handle other properties
		variable = context.getPropertyVariable(nodeTemplate, externalParameter);
		if (variable == null) {
		    variable = context.getPropertyVariable(externalParameter, true);
		    LOG.debug("Property variable is: " + variable.toString());
		} else {
		    BPELServerlessPluginHandler.LOG.debug("Found property variable " + externalParameter);
		}
	    }

	    createPubSubEventInternalExternalPropsInput.put(externalParameter, variable);

	    if (BPELPlanContext.isVariableValueEmpty(variable, context)) {
		context.addStringValueToPlanRequest(externalParameter);
		context.addAssignFromInput2VariableToMainAssign(externalParameter, variable);
		createPubSubEventInternalExternalPropsInput.put(externalParameter, variable);
	    } else {
		createPubSubEventInternalExternalPropsInput.put(externalParameter, variable);
	    }
	    // add the found properties of the PubSubEvent nodeTemplate
	    createPubSubEventInternalExternalPropsOutput.put(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_EVENTNAME,
		    eventNamePropWrapper);
	    createPubSubEventInternalExternalPropsOutput.put(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_TOPIC,
		    topicPropWrapper);
	    createPubSubEventInternalExternalPropsOutput.put(
		    Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_MESSAGEHUBINSTANCE, messageHubInstancePropWrapper);
	}
	/*
	 * invoke the operation to deploy a function on the underlying
	 * ServerlessPlatform nodeTempalte
	 */
	this.invokerOpPlugin.handle(context, "OpenWhiskPlatform", true,
		Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_SERVERLESS_DEPLOYPUBSUBEVENT,
		Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_SERVERLESS, "planCallbackAddress_invoker",
		createPubSubEventInternalExternalPropsInput, createPubSubEventInternalExternalPropsOutput, false);
	LOG.debug("Invocation of deployment of pubsub event was successful!");
    }

    /*
     * This method handles incoming ServerlessFunction NodeTemplates and invokes the
     * deployFunction of the underlying ServerlessPlatform NodeTemplate.
     * Furthermore, it checks for connected events and invokes the deployment of
     * them.
     */
    @Override
    public boolean handleWithServerlessInterface(final BPELPlanContext context,
	    final AbstractNodeTemplate nodeTemplate) {

	LOG.debug("Found following Serverless Node " + nodeTemplate.getId() + " of Type "
		+ nodeTemplate.getType().getId().toString());

	// check if the incoming nodeTemplate is of the type of a ServerlessFunction
	if (nodeTemplate.getType().getId().toString().equals(Types.serverlessFunctionNodeType.toString())) {
	    LOG.debug("Serverless Function Node found, now check for properties");
	    // call the method to invoke the deployment of the found serverless function
	    handleFunctionDeploymentInvocation(context, nodeTemplate);

	    // if the serverless function has no connected events, finish here
	    if (nodeTemplate.getIngoingRelations() == null) {
		LOG.debug("Serverless Function is not connected with any event, end here!");
		return true;
	    }
	    // check if serverless function is connected to any nodeTemplate
	    for (final AbstractRelationshipTemplate eventCon : nodeTemplate.getOutgoingRelations()) {
		// check if the serverless function is connected with a supported event
		if (Utils.isSupportedServerlessEventNodeType(eventCon.getTarget().getType().getId())) {
		    // check if it is connected to a http event
		    if (eventCon.getTarget().getType().getId().equals(Types.httpEventNodeType)) {
			LOG.debug("HTTP EVENT found!");
			// call the method to invoke the deployment of the found http event
			handleHttpEventDeploymentInvocation(context, nodeTemplate, eventCon);
			// check if it is connected to a timer event
		    } else if (eventCon.getTarget().getType().getId().equals(Types.timerEventNodeType)) {
			LOG.debug("Timer Event found!");
			// call the method to invoke the deployment of the found timer event
			handleTimerEventDeploymentInvocation(context, nodeTemplate, eventCon);
			// check if it is connected to a database event
		    } else if (eventCon.getTarget().getType().getId().equals(Types.databaseEventNodeType)) {
			LOG.debug("Database Event found!");
			// call the method to invoke the deployment of the found database event
			handleDatabaseEventDeploymentInvocation(context, nodeTemplate, eventCon);

		    } else if (eventCon.getTarget().getType().getId().equals(Types.blobstorageEventNodeType)) {
			LOG.debug("Blobstorage Event found!");
			// call the method to invoke the deployment of the found blobstorage event
			handleBlobstorageEventDeploymentInvocation(context, nodeTemplate, eventCon);
			// check if it is connected to a blobstorage event
		    } else if (eventCon.getTarget().getType().getId().equals(Types.pubsubEventNodeType)) {
			LOG.debug("PubSub Event found");
			// call the method to invoke the deployment of the found pubsub event
			handlePubSubEventDeploymentInvocation(context, nodeTemplate, eventCon);
		    } else {
			// finish here
			return true;
		    }
		}
	    }
	    return true;
	} else {
	    return false;
	}
    }
}
