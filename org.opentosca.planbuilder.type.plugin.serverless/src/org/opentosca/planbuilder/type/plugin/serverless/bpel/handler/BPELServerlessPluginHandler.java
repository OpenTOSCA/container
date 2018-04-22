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
 *
 * @author Tobias Mathony - mathony.tobias@gmail.com
 *
 */
public class BPELServerlessPluginHandler implements ServerlessPluginHandler<BPELPlanContext> {

    private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(BPELServerlessPluginHandler.class);

    private final BPELInvokerPlugin invokerOpPlugin = new BPELInvokerPlugin();

    private final static String[] createFunctionInstanceExternalInputParams = { "FunctionName", "Runtime" };
    private final static String[] createHttpEventInstanceExternalInputParams = { "EventName", "HTTPMethod", "APIID",
	    "ResourceID", "CreateHTTPEvent", "AuthorizationType", "FunctionURI" };
    private final static String[] createTimerEventInstanceExternalInputParams = { "EventName", "CRON" };
    private final static String[] createDatabaseEventInstanceExternalInputParams = { "EventName", "DatabaseName",
	    "DatabaseHostUrl", "DatabaseUsername", "DatabasePassword", "ListenToWhatChanges", "StartingPosition" };
    private final static String[] createBlobstorageEventInstanceExternalInputParams = { "EventName", "BucketName",
	    "EventType" };
    private final static String[] createPubSubEventInstanceExternalInputParams = { "EventName", "TopicName",
	    "MessageHubInstanceName" };

    public String getFunctionCode(final String FunctionURL) {
	String functionCode = null;
	try {

	    URL url = null;

	    // get URL for DeploymentArtifact from HEADER
	    url = new URL(FunctionURL);
	    // get Filename of the DeploymentArtifact (remove namespace etc.)
	    final String daName = FunctionURL.substring(FunctionURL.lastIndexOf("/") + 1);

	    // establish a connection and get the DA
	    HttpURLConnection connection = null;

	    connection = (HttpURLConnection) url.openConnection();

	    connection.setRequestMethod("GET");

	    connection.setRequestProperty("Content-Type", "application/zip");

	    InputStream is = null;

	    is = connection.getInputStream();

	    FileOutputStream out = null;

	    out = new FileOutputStream(daName);

	    // call the copy method with downloaded DeploymentArtifact
	    copy(is, out, 1024);

	    out.close();

	    functionCode = encodeFileToBase64Binary(daName);
	    LOG.debug("Functioncode is here: " + functionCode);

	} catch (final Exception e) {
	    e.printStackTrace();
	}

	return functionCode;
    }

    public String getFunctionUrl(final BPELPlanContext context, final AbstractNodeTemplate nodeTemplate) {

	final String ArtifactTemplate = nodeTemplate.getDeploymentArtifacts().get(0).getArtifactRef()
		.getArtifactReferences().get(0).getReference();
	LOG.debug("Found Serverless Function with following deployment artifact: " + ArtifactTemplate);
	final String csarName = context.getCSARFileName();
	// this is a bit hacky rn
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

	return true;
    }

    @Override
    public boolean handleWithServerlessInterface(final BPELPlanContext context,
	    final AbstractNodeTemplate nodeTemplate) {

	LOG.debug("Found following Serverless Node " + nodeTemplate.getId() + " of Type "
		+ nodeTemplate.getType().getId().toString());

	if (nodeTemplate.getType().getId().toString().equals(Types.serverlessFunctionNodeType.toString())) {
	    LOG.debug("Serverless Function Node found, now check for properties");

	    getFunctionUrl(context, nodeTemplate);

	    final Variable functionUrlVar = context.createGlobalStringVariable("FunctionURL",
		    getFunctionUrl(context, nodeTemplate));

	    final Variable functionCodeVar = context.createGlobalStringVariable("FunctionCode",
		    getFunctionCode(getFunctionUrl(context, nodeTemplate)));

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
		return false;
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
		return false;
	    }

	    LOG.debug("Adding plan callback address field to plan input");
	    context.addStringValueToPlanRequest("planCallbackAddress_invoker");

	    // add csarEntryPoint to plan input message
	    LOG.debug("Adding csarEntryPoint field to plan input");
	    context.addStringValueToPlanRequest("csarEntrypoint");

	    final Map<String, Variable> createFunctionInternalExternalPropsInput = new HashMap<>();
	    final Map<String, Variable> createFunctionInternalExternalPropsOutput = new HashMap<>();

	    for (final String externalParameter : BPELServerlessPluginHandler.createFunctionInstanceExternalInputParams) {
		// find variable for input param

		LOG.debug("External parameter to map is: " + externalParameter);

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
		createFunctionInternalExternalPropsInput.put(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_FUNCTIONURL,
			functionUrlVar);
		createFunctionInternalExternalPropsInput.put(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_FUNCTIONCODE,
			functionCodeVar);
		createFunctionInternalExternalPropsOutput
			.put(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_FUNCTIONNAME, functionNamePropWrapper);
		createFunctionInternalExternalPropsOutput.put(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_RUNTIME,
			runtimePropWrapper);
		createFunctionInternalExternalPropsOutput.put(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_FUNCTIONURL,
			functionUrlVar);
		createFunctionInternalExternalPropsOutput
			.put(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_FUNCTIONCODE, functionCodeVar);

	    }
	    LOG.debug("Now it should invoke serverless function deployment");
	    this.invokerOpPlugin.handle(context, "OpenWhiskPlatform", true,
		    Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_SERVERLESS_DEPLOYFUNCTION,
		    Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_SERVERLESS, "planCallbackAddress_invoker",
		    createFunctionInternalExternalPropsInput, createFunctionInternalExternalPropsOutput, false);

	    for (final AbstractRelationshipTemplate triggers : nodeTemplate.getIngoingRelations()) {
		if (Utils.isSupportedServerlessEventNodeType(triggers.getSource().getType().getId())) {
		    if (triggers.getSource().getType().getId().equals(Types.timerEventNodeType)) {
			Variable eventNamePropWrapper = null;

			for (final String eventName : org.opentosca.container.core.tosca.convention.Utils
				.getSupportedEventNamePropertyNames()) {

			    eventNamePropWrapper = context.getPropertyVariable(nodeTemplate, eventName);
			    if (eventNamePropWrapper == null) {
				eventNamePropWrapper = context.getPropertyVariable(eventName, true);
			    } else {
				break;
			    }
			}

			if (eventNamePropWrapper == null) {
			    BPELServerlessPluginHandler.LOG.warn(
				    "Timer EVENT Node doesn't have EventName property, altough it has the proper NodeType");
			    return false;
			}

			Variable cronPropWrapper = null;

			for (final String cron : org.opentosca.container.core.tosca.convention.Utils
				.getSupportedCRONPropertyNames()) {

			    cronPropWrapper = context.getPropertyVariable(nodeTemplate, cron);
			    if (cronPropWrapper == null) {
				cronPropWrapper = context.getPropertyVariable(cron, true);
			    } else {
				break;
			    }
			}

			if (cronPropWrapper == null) {
			    BPELServerlessPluginHandler.LOG.warn(
				    "Serverless Node doesn't have CRON property, altough it has the proper NodeType");
			    return false;
			}
			context.addStringValueToPlanRequest("EventName");
			context.addStringValueToPlanRequest("CRON");
			context.addStringValueToPlanRequest("OpenWhiskNamespace");
			context.addStringValueToPlanRequest("OpenWhiskEndpoint");
			context.addStringValueToPlanRequest("APIKey");
			LOG.debug("Adding plan callback address field to plan input");
			context.addStringValueToPlanRequest("planCallbackAddress_invoker");

			// add csarEntryPoint to plan input message
			LOG.debug("Adding csarEntryPoint field to plan input");
			context.addStringValueToPlanRequest("csarEntrypoint");

			final Map<String, Variable> createTimerEventInternalExternalPropsInput = new HashMap<>();
			final Map<String, Variable> createTimerEventInternalExternalPropsOutput = new HashMap<>();

			for (final String externalParameter : BPELServerlessPluginHandler.createTimerEventInstanceExternalInputParams) {
			    // find variable for input param
			    Variable variable = context.getPropertyVariable(nodeTemplate, externalParameter);
			    if (variable == null) {
				variable = context.getPropertyVariable(externalParameter, true);
			    }
			    createTimerEventInternalExternalPropsInput.put(externalParameter, variable);

			    if (BPELPlanContext.isVariableValueEmpty(variable, context)) {
				context.addStringValueToPlanRequest(externalParameter);
				context.addAssignFromInput2VariableToMainAssign(externalParameter, variable);
				createTimerEventInternalExternalPropsInput.put(externalParameter, variable);
			    } else {
				createTimerEventInternalExternalPropsInput.put(externalParameter, variable);
			    }
			    createTimerEventInternalExternalPropsOutput
				    .put(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_EVENTNAME, eventNamePropWrapper);
			    createTimerEventInternalExternalPropsOutput
				    .put(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_CRON, cronPropWrapper);

			}

			this.invokerOpPlugin.handle(context, nodeTemplate.getId(), true,
				Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_SERVERLESS_DEPLOYTIMEREVENT,
				Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_SERVERLESS, "planCallbackAddress_invoker",
				createTimerEventInternalExternalPropsInput, createTimerEventInternalExternalPropsOutput,
				false);
		    } else if (triggers.getSource().getType().getId().equals(Types.httpEventNodeType)) {

			Variable eventNamePropWrapper = null;

			for (final String eventName : org.opentosca.container.core.tosca.convention.Utils
				.getSupportedEventNamePropertyNames()) {

			    eventNamePropWrapper = context.getPropertyVariable(nodeTemplate, eventName);
			    if (eventNamePropWrapper == null) {
				eventNamePropWrapper = context.getPropertyVariable(eventName, true);
			    } else {
				break;
			    }
			}

			if (eventNamePropWrapper == null) {
			    BPELServerlessPluginHandler.LOG.warn(
				    "HTTP EVENT Node doesn't have EventName property, altough it has the proper NodeType");
			    return false;
			}

			Variable createHttpEventPropWrapper = null;

			for (final String createHttpEvent : org.opentosca.container.core.tosca.convention.Utils
				.getSupportedhttpEventCreateHTTPEventPropertyNames()) {

			    createHttpEventPropWrapper = context.getPropertyVariable(nodeTemplate, createHttpEvent);
			    if (createHttpEventPropWrapper == null) {
				createHttpEventPropWrapper = context.getPropertyVariable(createHttpEvent, true);
			    } else {
				break;
			    }
			}

			if (createHttpEventPropWrapper == null) {
			    BPELServerlessPluginHandler.LOG.warn(
				    "Serverless Node doesn't have CreateHttpEvent property, altough it has the proper NodeType");
			    return false;
			}

			Variable apiIDPropWrapper = null;

			for (final String apiID : org.opentosca.container.core.tosca.convention.Utils
				.getSupportedhttpEventAPIIDPropertyNames()) {

			    apiIDPropWrapper = context.getPropertyVariable(nodeTemplate, apiID);
			    if (apiIDPropWrapper == null) {
				apiIDPropWrapper = context.getPropertyVariable(apiID, true);
			    } else {
				break;
			    }
			}

			if (apiIDPropWrapper == null) {
			    BPELServerlessPluginHandler.LOG.warn(
				    "Serverless Node doesn't have API_ID property, altough it has the proper NodeType");
			    return false;
			}

			Variable resourceIDPropWrapper = null;

			for (final String resourceID : org.opentosca.container.core.tosca.convention.Utils
				.getSupportedhttpEventResourceIDPropertyNames()) {

			    resourceIDPropWrapper = context.getPropertyVariable(nodeTemplate, resourceID);
			    if (resourceIDPropWrapper == null) {
				resourceIDPropWrapper = context.getPropertyVariable(resourceID, true);
			    } else {
				break;
			    }
			}

			if (resourceIDPropWrapper == null) {
			    BPELServerlessPluginHandler.LOG.warn(
				    "Serverless Node doesn't have Resource_ID property, altough it has the proper NodeType");
			    return false;
			}

			Variable httpMethodPropWrapper = null;

			for (final String httpMethod : org.opentosca.container.core.tosca.convention.Utils
				.getSupportedhttpEventHttpMethodPropertyNames()) {

			    httpMethodPropWrapper = context.getPropertyVariable(nodeTemplate, httpMethod);
			    if (httpMethodPropWrapper == null) {
				httpMethodPropWrapper = context.getPropertyVariable(httpMethod, true);
			    } else {
				break;
			    }
			}

			if (httpMethodPropWrapper == null) {
			    BPELServerlessPluginHandler.LOG.warn(
				    "Serverless Node doesn't have HTTPMethod property, altough it has the proper NodeType");
			    return false;
			}

			Variable authTypePropWrapper = null;

			for (final String authType : org.opentosca.container.core.tosca.convention.Utils
				.getSupportedhttpEventAuthTypePropertyNames()) {

			    authTypePropWrapper = context.getPropertyVariable(nodeTemplate, authType);
			    if (authTypePropWrapper == null) {
				authTypePropWrapper = context.getPropertyVariable(authType, true);
			    } else {
				break;
			    }
			}

			if (authTypePropWrapper == null) {
			    BPELServerlessPluginHandler.LOG.warn(
				    "Serverless Node doesn't have AuthType property, altough it has the proper NodeType");
			    return false;
			}

			Variable functionURIPropWrapper = null;

			for (final String functionURI : org.opentosca.container.core.tosca.convention.Utils
				.getSupportedhttpEventFunctionURIPropertyNames()) {

			    functionURIPropWrapper = context.getPropertyVariable(nodeTemplate, functionURI);
			    if (functionURIPropWrapper == null) {
				functionURIPropWrapper = context.getPropertyVariable(functionURI, true);
			    } else {
				break;
			    }
			}

			if (functionURIPropWrapper == null) {
			    BPELServerlessPluginHandler.LOG.warn(
				    "Serverless Node doesn't have FunctionURI property, altough it has the proper NodeType");
			    return false;
			}
			context.addStringValueToPlanRequest("EventName");
			context.addStringValueToPlanRequest("APIID");
			context.addStringValueToPlanRequest("OpenWhiskNamespace");
			context.addStringValueToPlanRequest("OpenWhiskEndpoint");
			context.addStringValueToPlanRequest("APIKey");
			context.addStringValueToPlanRequest("ResourceID");
			context.addStringValueToPlanRequest("CreateHTTPEvent");
			context.addStringValueToPlanRequest("AuthorizationType");
			context.addStringValueToPlanRequest("FunctionURI");
			LOG.debug("Adding plan callback address field to plan input");
			context.addStringValueToPlanRequest("planCallbackAddress_invoker");

			// add csarEntryPoint to plan input message
			LOG.debug("Adding csarEntryPoint field to plan input");
			context.addStringValueToPlanRequest("csarEntrypoint");

			final Map<String, Variable> createHttpEventInternalExternalPropsInput = new HashMap<>();
			final Map<String, Variable> createHttpEventInternalExternalPropsOutput = new HashMap<>();

			for (final String externalParameter : BPELServerlessPluginHandler.createHttpEventInstanceExternalInputParams) {
			    // find variable for input param
			    Variable variable = context.getPropertyVariable(nodeTemplate, externalParameter);
			    if (variable == null) {
				variable = context.getPropertyVariable(externalParameter, true);
			    }
			    createHttpEventInternalExternalPropsInput.put(externalParameter, variable);

			    if (BPELPlanContext.isVariableValueEmpty(variable, context)) {
				context.addStringValueToPlanRequest(externalParameter);
				context.addAssignFromInput2VariableToMainAssign(externalParameter, variable);
				createHttpEventInternalExternalPropsInput.put(externalParameter, variable);
			    } else {
				createHttpEventInternalExternalPropsInput.put(externalParameter, variable);
			    }
			    createHttpEventInternalExternalPropsOutput
				    .put(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_EVENTNAME, eventNamePropWrapper);
			    createHttpEventInternalExternalPropsOutput
				    .put(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_APIID, apiIDPropWrapper);
			    createHttpEventInternalExternalPropsOutput.put(
				    Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_RESOURCEID, resourceIDPropWrapper);
			    createHttpEventInternalExternalPropsOutput.put(
				    Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_CREATEHTTPEVENT,
				    createHttpEventPropWrapper);
			    createHttpEventInternalExternalPropsOutput.put(
				    Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_HTTPMETHOD, httpMethodPropWrapper);
			    createHttpEventInternalExternalPropsOutput.put(
				    Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_FUNCTIONURI, functionURIPropWrapper);
			    createHttpEventInternalExternalPropsOutput
				    .put(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_AUTHTYPE, authTypePropWrapper);
			}
			this.invokerOpPlugin.handle(context, nodeTemplate.getId(), true,
				Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_SERVERLESS_DEPLOYHTTPEVENT,
				Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_SERVERLESS, "planCallbackAddress_invoker",
				createHttpEventInternalExternalPropsInput, createHttpEventInternalExternalPropsOutput,
				false);

		    } else if (triggers.getSource().getType().getId().equals(Types.databaseEventNodeType)) {
			Variable eventNamePropWrapper = null;

			for (final String eventName : org.opentosca.container.core.tosca.convention.Utils
				.getSupportedEventNamePropertyNames()) {

			    eventNamePropWrapper = context.getPropertyVariable(nodeTemplate, eventName);
			    if (eventNamePropWrapper == null) {
				eventNamePropWrapper = context.getPropertyVariable(eventName, true);
			    } else {
				break;
			    }
			}

			if (eventNamePropWrapper == null) {
			    BPELServerlessPluginHandler.LOG.warn(
				    "Timer EVENT Node doesn't have EventName property, altough it has the proper NodeType");
			    return false;
			}

			Variable databaseNamePropWrapper = null;

			for (final String databaseName : org.opentosca.container.core.tosca.convention.Utils
				.getSupporteddatabaseEventdatabaseNamePropertyNames()) {

			    databaseNamePropWrapper = context.getPropertyVariable(nodeTemplate, databaseName);
			    if (databaseNamePropWrapper == null) {
				databaseNamePropWrapper = context.getPropertyVariable(databaseName, true);
			    } else {
				break;
			    }
			}

			if (databaseNamePropWrapper == null) {
			    BPELServerlessPluginHandler.LOG.warn(
				    "Serverless Node doesn't have Databasename property, altough it has the proper NodeType");
			    return false;
			}

			Variable databaseHostPropWrapper = null;

			for (final String databaseHost : org.opentosca.container.core.tosca.convention.Utils
				.getSupporteddatabaseEventdatabaseHostUrlPropertyNames()) {

			    databaseHostPropWrapper = context.getPropertyVariable(nodeTemplate, databaseHost);
			    if (databaseHostPropWrapper == null) {
				databaseHostPropWrapper = context.getPropertyVariable(databaseHost, true);
			    } else {
				break;
			    }
			}

			if (databaseHostPropWrapper == null) {
			    BPELServerlessPluginHandler.LOG.warn(
				    "Serverless Node doesn't have DatabaseHostURL property, altough it has the proper NodeType");
			    return false;
			}

			Variable databaseUserPropWrapper = null;

			for (final String databaseUser : org.opentosca.container.core.tosca.convention.Utils
				.getSupporteddatabaseEventdatabaseUsernamePropertyNames()) {

			    databaseUserPropWrapper = context.getPropertyVariable(nodeTemplate, databaseUser);
			    if (databaseUserPropWrapper == null) {
				databaseUserPropWrapper = context.getPropertyVariable(databaseUser, true);
			    } else {
				break;
			    }
			}

			if (databaseUserPropWrapper == null) {
			    BPELServerlessPluginHandler.LOG.warn(
				    "Serverless Node doesn't have Database User property, altough it has the proper NodeType");
			    return false;
			}

			Variable databasePwPropWrapper = null;

			for (final String databasePw : org.opentosca.container.core.tosca.convention.Utils
				.getSupporteddatabaseEventdatabasePasswordPropertyNames()) {

			    databasePwPropWrapper = context.getPropertyVariable(nodeTemplate, databasePw);
			    if (databasePwPropWrapper == null) {
				databasePwPropWrapper = context.getPropertyVariable(databasePw, true);
			    } else {
				break;
			    }
			}

			if (databasePwPropWrapper == null) {
			    BPELServerlessPluginHandler.LOG.warn(
				    "Serverless Node doesn't have Database Password property, altough it has the proper NodeType");
			    return false;
			}

			Variable typeOfChangePropWrapper = null;

			for (final String typeOfChange : org.opentosca.container.core.tosca.convention.Utils
				.getSupporteddatabaeEventTypeOfChangePropertyNames()) {

			    typeOfChangePropWrapper = context.getPropertyVariable(nodeTemplate, typeOfChange);
			    if (typeOfChangePropWrapper == null) {
				typeOfChangePropWrapper = context.getPropertyVariable(typeOfChange, true);
			    } else {
				break;
			    }
			}

			if (typeOfChangePropWrapper == null) {
			    BPELServerlessPluginHandler.LOG.warn(
				    "Serverless Node doesn't have Type of change property, altough it has the proper NodeType");
			    return false;
			}

			Variable startPosPropWrapper = null;

			for (final String startPos : org.opentosca.container.core.tosca.convention.Utils
				.getSupporteddatabaseEventStartPosPropertyNames()) {

			    startPosPropWrapper = context.getPropertyVariable(nodeTemplate, startPos);
			    if (startPosPropWrapper == null) {
				startPosPropWrapper = context.getPropertyVariable(startPos, true);
			    } else {
				break;
			    }
			}

			if (startPosPropWrapper == null) {
			    BPELServerlessPluginHandler.LOG.warn(
				    "Serverless Node doesn't have starting position property, altough it has the proper NodeType");
			    return false;
			}
			context.addStringValueToPlanRequest("EventName");
			context.addStringValueToPlanRequest("DatabaseName");
			context.addStringValueToPlanRequest("OpenWhiskNamespace");
			context.addStringValueToPlanRequest("OpenWhiskEndpoint");
			context.addStringValueToPlanRequest("APIKey");
			context.addStringValueToPlanRequest("DatabaseHostUrl");
			context.addStringValueToPlanRequest("DatabaseUsername");
			context.addStringValueToPlanRequest("DatabasePassword");
			context.addStringValueToPlanRequest("StartingPosition");
			context.addStringValueToPlanRequest("ListenToWhatChanges");
			LOG.debug("Adding plan callback address field to plan input");
			context.addStringValueToPlanRequest("planCallbackAddress_invoker");

			// add csarEntryPoint to plan input message
			LOG.debug("Adding csarEntryPoint field to plan input");
			context.addStringValueToPlanRequest("csarEntrypoint");

			final Map<String, Variable> createDatabaseEventInternalExternalPropsInput = new HashMap<>();
			final Map<String, Variable> createDatabaseEventInternalExternalPropsOutput = new HashMap<>();

			for (final String externalParameter : BPELServerlessPluginHandler.createDatabaseEventInstanceExternalInputParams) {
			    // find variable for input param
			    Variable variable = context.getPropertyVariable(nodeTemplate, externalParameter);
			    if (variable == null) {
				variable = context.getPropertyVariable(externalParameter, true);
			    }
			    createDatabaseEventInternalExternalPropsInput.put(externalParameter, variable);

			    if (BPELPlanContext.isVariableValueEmpty(variable, context)) {
				context.addStringValueToPlanRequest(externalParameter);
				context.addAssignFromInput2VariableToMainAssign(externalParameter, variable);
				createDatabaseEventInternalExternalPropsInput.put(externalParameter, variable);
			    } else {
				createDatabaseEventInternalExternalPropsInput.put(externalParameter, variable);
			    }
			    createDatabaseEventInternalExternalPropsOutput
				    .put(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_EVENTNAME, eventNamePropWrapper);
			    createDatabaseEventInternalExternalPropsOutput.put(
				    Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_DATABASENAME,
				    databaseNamePropWrapper);
			    createDatabaseEventInternalExternalPropsOutput.put(
				    Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_DATABASEHOSTURL,
				    databaseHostPropWrapper);
			    createDatabaseEventInternalExternalPropsOutput.put(
				    Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_DATABASEUSER,
				    databaseUserPropWrapper);
			    createDatabaseEventInternalExternalPropsOutput.put(
				    Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_DATABASEPW, databasePwPropWrapper);
			    createDatabaseEventInternalExternalPropsOutput.put(
				    Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_TYPEOFCHANGE,
				    typeOfChangePropWrapper);
			    createDatabaseEventInternalExternalPropsOutput
				    .put(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_STARTPOS, startPosPropWrapper);
			}
			this.invokerOpPlugin.handle(context, nodeTemplate.getId(), true,
				Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_SERVERLESS_DEPLOYDATABASEEVENT,
				Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_SERVERLESS, "planCallbackAddress_invoker",
				createDatabaseEventInternalExternalPropsInput,
				createDatabaseEventInternalExternalPropsOutput, false);
		    } else if (triggers.getSource().getType().getId().equals(Types.blobstorageEventNodeType)) {
			Variable eventNamePropWrapper = null;

			for (final String eventName : org.opentosca.container.core.tosca.convention.Utils
				.getSupportedEventNamePropertyNames()) {

			    eventNamePropWrapper = context.getPropertyVariable(nodeTemplate, eventName);
			    if (eventNamePropWrapper == null) {
				eventNamePropWrapper = context.getPropertyVariable(eventName, true);
			    } else {
				break;
			    }
			}

			if (eventNamePropWrapper == null) {
			    BPELServerlessPluginHandler.LOG.warn(
				    "Timer EVENT Node doesn't have EventName property, altough it has the proper NodeType");
			    return false;
			}

			Variable bucketNamePropWrapper = null;

			for (final String bucketName : org.opentosca.container.core.tosca.convention.Utils
				.getSupportedblobstorageEventBucketNamePropertyNames()) {

			    bucketNamePropWrapper = context.getPropertyVariable(nodeTemplate, bucketName);
			    if (bucketNamePropWrapper == null) {
				bucketNamePropWrapper = context.getPropertyVariable(bucketName, true);
			    } else {
				break;
			    }
			}

			if (bucketNamePropWrapper == null) {
			    BPELServerlessPluginHandler.LOG.warn(
				    "Serverless Node doesn't have Bucket Name property, altough it has the proper NodeType");
			    return false;
			}

			Variable eventTypePropWrapper = null;

			for (final String eventType : org.opentosca.container.core.tosca.convention.Utils
				.getSupportedblobstorageEventEventTypePropertyNames()) {

			    eventTypePropWrapper = context.getPropertyVariable(nodeTemplate, eventType);
			    if (eventTypePropWrapper == null) {
				eventTypePropWrapper = context.getPropertyVariable(eventType, true);
			    } else {
				break;
			    }
			}

			if (eventTypePropWrapper == null) {
			    BPELServerlessPluginHandler.LOG.warn(
				    "Serverless Node doesn't have event type property, altough it has the proper NodeType");
			    return false;
			}
			context.addStringValueToPlanRequest("EventName");
			context.addStringValueToPlanRequest("BucketName");
			context.addStringValueToPlanRequest("OpenWhiskNamespace");
			context.addStringValueToPlanRequest("OpenWhiskEndpoint");
			context.addStringValueToPlanRequest("APIKey");
			context.addStringValueToPlanRequest("EventType");
			LOG.debug("Adding plan callback address field to plan input");
			context.addStringValueToPlanRequest("planCallbackAddress_invoker");

			// add csarEntryPoint to plan input message
			LOG.debug("Adding csarEntryPoint field to plan input");
			context.addStringValueToPlanRequest("csarEntrypoint");

			final Map<String, Variable> createBlobstorageEventInternalExternalPropsInput = new HashMap<>();
			final Map<String, Variable> createBlobstorageEventInternalExternalPropsOutput = new HashMap<>();

			for (final String externalParameter : BPELServerlessPluginHandler.createBlobstorageEventInstanceExternalInputParams) {
			    // find variable for input param
			    Variable variable = context.getPropertyVariable(nodeTemplate, externalParameter);
			    if (variable == null) {
				variable = context.getPropertyVariable(externalParameter, true);
			    }
			    createBlobstorageEventInternalExternalPropsInput.put(externalParameter, variable);

			    if (BPELPlanContext.isVariableValueEmpty(variable, context)) {
				context.addStringValueToPlanRequest(externalParameter);
				context.addAssignFromInput2VariableToMainAssign(externalParameter, variable);
				createBlobstorageEventInternalExternalPropsInput.put(externalParameter, variable);
			    } else {
				createBlobstorageEventInternalExternalPropsInput.put(externalParameter, variable);
			    }
			    createBlobstorageEventInternalExternalPropsOutput
				    .put(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_EVENTNAME, eventNamePropWrapper);
			    createBlobstorageEventInternalExternalPropsOutput.put(
				    Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_BUCKETNAME, bucketNamePropWrapper);
			    createBlobstorageEventInternalExternalPropsOutput
				    .put(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_EVENTTYPE, eventTypePropWrapper);
			}
			this.invokerOpPlugin.handle(context, nodeTemplate.getId(), true,
				Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_SERVERLESS_DEPLOYBLOBSTORAGEEVENT,
				Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_SERVERLESS, "planCallbackAddress_invoker",
				createBlobstorageEventInternalExternalPropsInput,
				createBlobstorageEventInternalExternalPropsOutput, false);

		    } else if (triggers.getSource().getType().getId().equals(Types.pubsubEventNodeType)) {
			Variable eventNamePropWrapper = null;

			for (final String eventName : org.opentosca.container.core.tosca.convention.Utils
				.getSupportedEventNamePropertyNames()) {

			    eventNamePropWrapper = context.getPropertyVariable(nodeTemplate, eventName);
			    if (eventNamePropWrapper == null) {
				eventNamePropWrapper = context.getPropertyVariable(eventName, true);
			    } else {
				break;
			    }
			}

			if (eventNamePropWrapper == null) {
			    BPELServerlessPluginHandler.LOG.warn(
				    "Timer EVENT Node doesn't have EventName property, altough it has the proper NodeType");
			    return false;
			}

			Variable topicPropWrapper = null;

			for (final String topic : org.opentosca.container.core.tosca.convention.Utils
				.getSupportedpubsubEventTopicNamePropertyNames()) {

			    topicPropWrapper = context.getPropertyVariable(nodeTemplate, topic);
			    if (topicPropWrapper == null) {
				topicPropWrapper = context.getPropertyVariable(topic, true);
			    } else {
				break;
			    }
			}

			if (topicPropWrapper == null) {
			    BPELServerlessPluginHandler.LOG.warn(
				    "Serverless Node doesn't have Topic property, altough it has the proper NodeType");
			    return false;
			}

			Variable messageHubInstancePropWrapper = null;

			for (final String messageHubInstance : org.opentosca.container.core.tosca.convention.Utils
				.getSupportedpubsubEventMessageHubInstanceNamePropertyNames()) {

			    messageHubInstancePropWrapper = context.getPropertyVariable(nodeTemplate,
				    messageHubInstance);
			    if (messageHubInstancePropWrapper == null) {
				messageHubInstancePropWrapper = context.getPropertyVariable(messageHubInstance, true);
			    } else {
				break;
			    }
			}

			if (messageHubInstancePropWrapper == null) {
			    BPELServerlessPluginHandler.LOG.warn(
				    "Serverless Node doesn't have MessageHub Instance name property, altough it has the proper NodeType");
			    return false;
			}
			context.addStringValueToPlanRequest("EventName");
			context.addStringValueToPlanRequest("TopicName");
			context.addStringValueToPlanRequest("OpenWhiskNamespace");
			context.addStringValueToPlanRequest("OpenWhiskEndpoint");
			context.addStringValueToPlanRequest("APIKey");
			context.addStringValueToPlanRequest("MessageHubInstanceName");
			LOG.debug("Adding plan callback address field to plan input");
			context.addStringValueToPlanRequest("planCallbackAddress_invoker");

			// add csarEntryPoint to plan input message
			LOG.debug("Adding csarEntryPoint field to plan input");
			context.addStringValueToPlanRequest("csarEntrypoint");

			final Map<String, Variable> createPubSubEventInternalExternalPropsInput = new HashMap<>();
			final Map<String, Variable> createPubSubEventInternalExternalPropsOutput = new HashMap<>();

			for (final String externalParameter : BPELServerlessPluginHandler.createPubSubEventInstanceExternalInputParams) {
			    // find variable for input param
			    Variable variable = context.getPropertyVariable(nodeTemplate, externalParameter);
			    if (variable == null) {
				variable = context.getPropertyVariable(externalParameter, true);
			    }
			    createPubSubEventInternalExternalPropsInput.put(externalParameter, variable);

			    if (BPELPlanContext.isVariableValueEmpty(variable, context)) {
				context.addStringValueToPlanRequest(externalParameter);
				context.addAssignFromInput2VariableToMainAssign(externalParameter, variable);
				createPubSubEventInternalExternalPropsInput.put(externalParameter, variable);
			    } else {
				createPubSubEventInternalExternalPropsInput.put(externalParameter, variable);
			    }
			    createPubSubEventInternalExternalPropsOutput
				    .put(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_EVENTNAME, eventNamePropWrapper);
			    createPubSubEventInternalExternalPropsOutput
				    .put(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_TOPIC, topicPropWrapper);
			    createPubSubEventInternalExternalPropsOutput.put(
				    Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_MESSAGEHUBINSTANCE,
				    messageHubInstancePropWrapper);
			}
			this.invokerOpPlugin.handle(context, nodeTemplate.getId(), true,
				Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_SERVERLESS_DEPLOYPUBSUBEVENT,
				Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_SERVERLESS, "planCallbackAddress_invoker",
				createPubSubEventInternalExternalPropsInput,
				createPubSubEventInternalExternalPropsOutput, false);
		    } else {
			LOG.debug("Serverless Function is connected with unknown node type.");
		    }
		}

	    }
	} else {
	    return false;
	}
	return true;
    }
}
