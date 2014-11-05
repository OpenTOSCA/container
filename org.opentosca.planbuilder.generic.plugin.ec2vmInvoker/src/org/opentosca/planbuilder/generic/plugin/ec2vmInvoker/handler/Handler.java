package org.opentosca.planbuilder.generic.plugin.ec2vmInvoker.handler;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.plugins.constants.PluginConstants;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext.Variable;
import org.opentosca.planbuilder.provphase.plugin.invoker.Plugin;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * <p>
 * This class implements the logic to provision an EC2VM Stack, consisting of
 * the NodeTypes {http://www.example.com/tosca/ServiceTemplates/EC2VM}EC2,
 * {http://www.example.com/tosca/ServiceTemplates/EC2VM}VM,
 * {http://www.example.com/tosca/ServiceTemplates/EC2VM}Ubuntu.
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class Handler {

	private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(Handler.class);

	private DocumentBuilderFactory docFactory;
	private DocumentBuilder docBuilder;

	/*
	 * "instanceType" "AMIid","regionEndpoint", "accessKey",
	 * "secretKey","securityGroup", "keyPairName"
	 */

	// createEC2Instance external input parameters without CorrelationId
	private final static String[] createEC2InstanceExternalInputParams = {"securityGroup", "keyPairName", "secretKey", "accessKey", "regionEndpoint", "AMIid", "instanceType"};

	// getPublicDNS external input parameters without CorrelationId and
	// instanceId
	private final static String[] getPublicDNSExternalInputParams = {"sshKey", "region", "accessKey", "secretKey"};


	/**
	 * Constructor
	 *
	 * @throws ParserConfigurationException is thrown when initializing the DOM
	 *             Parsers fails
	 */
	public Handler() throws ParserConfigurationException {
		this.docFactory = DocumentBuilderFactory.newInstance();
		this.docFactory.setNamespaceAware(true);
		this.docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

	}

	/**
	 * Adds fragments to provision a EC2 VM
	 *
	 * @param context a TemplatePlanContext for a EC2, VM or Ubuntu Node
	 * @param nodeTemplate the NodeTemplate on which the fragments are used
	 * @return true iff adding the fragments was successful
	 */
	public boolean handle(TemplatePlanContext context, AbstractNodeTemplate nodeTemplate) {
		Plugin invokerOpPlugin = new Plugin();

		// find InstanceId Property inside ubuntu nodeTemplate

		Variable instanceIdPropWrapper = context.getInternalPropertyVariable(PluginConstants.OPENTOSCA_DECLARATIVE_PROPERTYNAME_INSTANCEID);
		if (instanceIdPropWrapper == null) {
			instanceIdPropWrapper = context.getInternalPropertyVariable(PluginConstants.OPENTOSCA_DECLARATIVE_PROPERTYNAME_INSTANCEID, true);
			if (instanceIdPropWrapper == null) {
				instanceIdPropWrapper = context.getInternalPropertyVariable(PluginConstants.OPENTOSCA_DECLARATIVE_PROPERTYNAME_INSTANCEID, false);
			}
		}

		if (instanceIdPropWrapper == null) {
			Handler.LOG.warn("Ubuntu Node doesn't have InstanceId property, altough it has the proper NodeType");
			return false;
		}

		// find ServerIp Property inside ubuntu nodeTemplate

		Variable serverIpPropWrapper = context.getInternalPropertyVariable(PluginConstants.OPENTOSCA_DECLARATIVE_PROPERTYNAME_SERVERIP);
		if (serverIpPropWrapper == null) {
			serverIpPropWrapper = context.getInternalPropertyVariable(PluginConstants.OPENTOSCA_DECLARATIVE_PROPERTYNAME_SERVERIP, true);
			if (serverIpPropWrapper == null) {
				serverIpPropWrapper = context.getInternalPropertyVariable(PluginConstants.OPENTOSCA_DECLARATIVE_PROPERTYNAME_SERVERIP, false);
			}
		}

		if (serverIpPropWrapper == null) {
			Handler.LOG.warn("Ubuntu Node doesn't have ServerIp property, altough it has the proper NodeType");
			return false;
		}

		// add logic for the invoker calls

		/*
		 * <Interface name="EC2VMInvokerInterface"> <Operation
		 * name="createEC2Instance"> <InputParameters> <InputParameter
		 * name="CorrelationId" type="String" required="yes"/> <InputParameter
		 * name="instanceType" type="String" required="yes" /> <InputParameter
		 * name="ami" type="String" required="yes" /> <InputParameter
		 * name="region" type="String" required="yes" /> <InputParameter
		 * name="accessKey" type="String" required="yes" /> <InputParameter
		 * name="secretKey" type="String" required="yes" /> <InputParameter
		 * name="securityGroup" type="String" required="yes" /> <InputParameter
		 * name="keyPairName" type="String" required="yes" /> </InputParameters>
		 * <OutputParameters> <OutputParameter name="CorrelationId"
		 * type="String" required="yes"/> <OutputParameter
		 * name="createEC2InstanceReturn" type="String" required="yes" />
		 * </OutputParameters> </Operation> <Operation name="getPublicDNS">
		 * <InputParameters>
		 */

		/* setup input mappings for createEC2Instance call */

		/*
		 * create Ec2 Instance Operation input parameters: CorrelationId
		 * instanceType ami region accessKey secretKey securityGroup keyPairName
		 *
		 * create Ec2 Instance Operation output parameters: CorrelationId
		 * getPublicDNSReturn
		 */

		Map<String, Variable> createEC2InternalExternalPropsInput = new HashMap<String, Variable>();

		// set external parameters
		for (String externalParameter : Handler.createEC2InstanceExternalInputParams) {
			createEC2InternalExternalPropsInput.put(externalParameter, null);
		}

		// generate var with random value for the correlation id
		// Variable ec2CorrelationIdVar =
		// context.generateVariableWithRandomValue();
		// createEC2InternalExternalPropsInput.put("CorrelationId",
		// ec2CorrelationIdVar);

		/* setup output mappings */
		Map<String, Variable> createEC2InternalExternalPropsOutput = new HashMap<String, Variable>();

		// with this the invoker plugin should write the value of
		// getPublicDNSReturn into the InstanceId Property of the Ubuntu
		// Node
		createEC2InternalExternalPropsOutput.put("instanceId", instanceIdPropWrapper);
		createEC2InternalExternalPropsOutput.put("publicDNS", serverIpPropWrapper);

		// generate plan input message element for the plan address, this is
		// needed as BPS 2.1.2 fails at returning addresses appropiate for
		// callback
		// TODO maybe do a check with BPS Connector for BPS version, because
		// since vers. 3 retrieving the address of the plan works
		context.addStringValueToPlanRequest("planCallbackAddress_invoker");

		// we'll add the logic to VM Nodes Prov phase, as we need proper updates
		// of properties at the InstanceDataAPI

		invokerOpPlugin.handle(context, "create", "InterfaceAmazonEC2VM", "planCallbackAddress_invoker", createEC2InternalExternalPropsInput, createEC2InternalExternalPropsOutput);

		/* setup getPublicDNS call */

		/*
		 * <InputParameter name="CorrelationId" type="String" required="yes"/>
		 * <InputParameter name="sshKey" type="String" required="yes"/>
		 * <InputParameter name="instanceId" type="String" required="yes" />
		 * <InputParameter name="region" type="String" required="yes" />
		 * <InputParameter name="accessKey" type="String" required="yes" />
		 * <InputParameter name="secretKey" type="String" required="yes" />
		 * </InputParameters> <OutputParameters> <OutputParameter
		 * name="CorrelationId" type="String" required="yes"/> <OutputParameter
		 * name="getPublicDNSReturn" type="String" required="yes" />
		 * </OutputParameters> </Operation> </Interface>
		 */

		/*
		 * Map<String, Variable>
		 * getPublicDNSInternalExternalPropsInput = new HashMap<String,
		 * Variable>();
		 *
		 * // set every external parameter for (String parameter :
		 * Handler.getPublicDNSExternalInputParams) {
		 * getPublicDNSInternalExternalPropsInput.put(parameter, null); }
		 *
		 * // generate randomVar for CorrelationId Variable
		 * publicDnsCorrelationIdVar =
		 * context.generateVariableWithRandomValue();
		 * getPublicDNSInternalExternalPropsInput.put("CorrelationId",
		 * publicDnsCorrelationIdVar);
		 *
		 * // take the same propWrapper as before, just take it as input
		 * getPublicDNSInternalExternalPropsInput.put("instanceId",
		 * instanceIdPropWrapper);
		 *
		 * // fetch ServerIp Property Variable name
		 * getPublicDNSInternalExternalPropsOutput.put("getPublicDNSReturn",
		 * serverIpPropWrapper);
		 *
		 * invokerOpPlugin.handle(context, "getPublicDNS", null,
		 * getPublicDNSInternalExternalPropsInput,
		 * getPublicDNSInternalExternalPropsOutput);
		 */
		return true;
	}

	private String generateAssignEc2InstanceReturnToInstanceIdProp(TemplatePlanContext context) {
		/*
		 * <bpel:copy> <bpel:from part="invokeResponse"
		 * variable="CallbackPortTypeinvokeResponseResponse3"> <bpel:query
		 * queryLanguage
		 * ="urn:oasis:names:tc:wsbpel:2.0:sublang:xpath1.0"><![CDATA
		 * [//*[local-name()="Param" and
		 * namespace-uri()="http://siserver.org/schema"]/*[local-name()="key"
		 * and
		 * text()="createEC2InstanceReturn"]/following-sibling::*[local-name(
		 * )="value"]]]></bpel:query> </bpel:from> <bpel:to part="payload"
		 * variable="output"> <bpel:query
		 * queryLanguage="urn:oasis:names:tc:wsbpel:2.0:sublang:xpath1.0"
		 * ><![CDATA[//*[local-name()="createEC2InstanceReturn"]]]></bpel:query>
		 * </bpel:to> </bpel:copy> </bpel:assign>
		 */


		String instanceIdVarName = context.getVariableNameOfInfraNodeProperty("instanceId");
		String assignString = "<bpel:assign xmlns:bpel=\"http://docs.oasis-open.org/wsbpel/2.0/process/executable\" name=\"assignInstanceIDFromResponse" + context.getIdForNames() + "\"><bpel:copy><bpel:from part=\"payload\" variable=\"output\"> <bpel:query queryLanguage=\"urn:oasis:names:tc:wsbpel:2.0:sublang:xpath1.0\"><![CDATA[//*[local-name()=\"createEC2InstanceReturn\"]]]></bpel:query></bpel:from> ";
		assignString += "<bpel:to variable=\"" + instanceIdVarName + "\"/>";
		assignString += "</bpel:copy> </bpel:assign>";
		return assignString;
	}

	private String generateAssignServerIpWithGetPublicDNSReturn(TemplatePlanContext context) {
		String serverIpVarName = context.getVariableNameOfInfraNodeProperty(PluginConstants.OPENTOSCA_DECLARATIVE_PROPERTYNAME_SERVERIP);
		String assignString = "<bpel:assign xmlns:bpel=\"http://docs.oasis-open.org/wsbpel/2.0/process/executable\" name=\"assignServerIpFromResponse" + context.getIdForNames() + "\"><bpel:copy><bpel:from part=\"payload\" variable=\"output\"> <bpel:query queryLanguage=\"urn:oasis:names:tc:wsbpel:2.0:sublang:xpath1.0\"><![CDATA[//*[local-name()=\"getPublicDNSReturn\"]]]></bpel:query></bpel:from> ";
		assignString += "<bpel:to variable=\"" + serverIpVarName + "\"/>";
		assignString += "</bpel:copy> </bpel:assign>";
		return assignString;
	}

	private Node generateAssignEc2InstanceReturnInstanceIdPropAsNode(TemplatePlanContext context) throws SAXException, IOException {
		String assignString = this.generateAssignEc2InstanceReturnToInstanceIdProp(context);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(assignString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}

	private Node generateAssignServerIpFromGetPublicDNSReturnAsNode(TemplatePlanContext context) throws SAXException, IOException {
		String assignString = this.generateAssignServerIpWithGetPublicDNSReturn(context);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(assignString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}

}
