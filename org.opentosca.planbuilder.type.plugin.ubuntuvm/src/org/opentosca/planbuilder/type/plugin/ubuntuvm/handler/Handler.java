package org.opentosca.planbuilder.type.plugin.ubuntuvm.handler;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.plugins.constants.PluginConstants;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext.Variable;
import org.opentosca.planbuilder.provphase.plugin.invoker.Plugin;
import org.opentosca.planbuilder.utils.Utils;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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
	
	// create method external input parameters without CorrelationId
	private final static String[] createEC2InstanceExternalInputParams = {"securityGroup", "keyPairName", "secretKey", "accessKey", "regionEndpoint", "AMIid", "instanceType"};
	
	
	/**
	 * Adds fragments to provision a EC2 VM
	 *
	 * @param context a TemplatePlanContext for a EC2, VM or Ubuntu Node
	 * @param nodeTemplate the NodeTemplate on which the fragments are used
	 * @return true iff adding the fragments was successful
	 */
	public boolean handle(TemplatePlanContext context, AbstractNodeTemplate nodeTemplate) {
		Plugin invokerOpPlugin = new Plugin();
		
		// we check if the ubuntu which must be connected to this node (if not
		// directly then trough some vm nodetemplate) is a nodetype with a
		// ubuntu version e.g. Ubuntu_13.10 and stuff
		AbstractNodeTemplate ubuntuNodeTemplate = null;
		Variable ubuntuAMIIdVar = null;
		for (AbstractRelationshipTemplate relationTemplate : nodeTemplate.getIngoingRelations()) {
			if (relationTemplate.getSource().getType().getId().toString().equals(org.opentosca.planbuilder.type.plugin.ubuntuvm.Plugin.ubuntu1310ServerNodeType.toString())) {
				ubuntuNodeTemplate = relationTemplate.getSource();
			}
			
			for (AbstractRelationshipTemplate relationTemplate2 : relationTemplate.getSource().getIngoingRelations()) {
				if (relationTemplate2.getSource().getType().getId().toString().equals(org.opentosca.planbuilder.type.plugin.ubuntuvm.Plugin.ubuntu1310ServerNodeType.toString())) {
					ubuntuNodeTemplate = relationTemplate.getSource();
				}
			}
		}
		
		// here either the ubuntu connected to the provider this handler is
		// working on hasn't a version in the ID (ubuntu version must be written
		// in AMIId property then) or something went really wrong
		if (ubuntuNodeTemplate != null) {
			// we'll set a global variable with the necessary ubuntu image
			ubuntuAMIIdVar = context.createGlobalStringVariable("ubuntu_AMIId", "ubuntu-13.10-server-cloudimg-amd64");
		}
		
		// find InstanceId Property inside ubuntu nodeTemplate
		Variable instanceIdPropWrapper = context.getPropertyVariable(PluginConstants.OPENTOSCA_DECLARATIVE_PROPERTYNAME_INSTANCEID);
		if (instanceIdPropWrapper == null) {
			instanceIdPropWrapper = context.getPropertyVariable(PluginConstants.OPENTOSCA_DECLARATIVE_PROPERTYNAME_INSTANCEID, true);
			if (instanceIdPropWrapper == null) {
				instanceIdPropWrapper = context.getPropertyVariable(PluginConstants.OPENTOSCA_DECLARATIVE_PROPERTYNAME_INSTANCEID, false);
			}
		}
		
		if (instanceIdPropWrapper == null) {
			Handler.LOG.warn("Ubuntu Node doesn't have InstanceId property, altough it has the proper NodeType");
			return false;
		}
		
		// find ServerIp Property inside ubuntu nodeTemplate
		
		Variable serverIpPropWrapper = context.getPropertyVariable(PluginConstants.OPENTOSCA_DECLARATIVE_PROPERTYNAME_SERVERIP);
		if (serverIpPropWrapper == null) {
			serverIpPropWrapper = context.getPropertyVariable(PluginConstants.OPENTOSCA_DECLARATIVE_PROPERTYNAME_SERVERIP, true);
			if (serverIpPropWrapper == null) {
				serverIpPropWrapper = context.getPropertyVariable(PluginConstants.OPENTOSCA_DECLARATIVE_PROPERTYNAME_SERVERIP, false);
			}
		}
		
		if (serverIpPropWrapper == null) {
			Handler.LOG.warn("Ubuntu Node doesn't have ServerIp property, altough it has the proper NodeType");
			return false;
		}
		
		Map<String, Variable> createEC2InternalExternalPropsInput = new HashMap<String, Variable>();
		
		// set external parameters
		for (String externalParameter : Handler.createEC2InstanceExternalInputParams) {
			// find the variable for the inputparam
			
			Variable variable = context.getPropertyVariable(externalParameter);
			if (variable == null) {
				variable = context.getPropertyVariable(externalParameter, true);
				if (variable == null) {
					variable = context.getPropertyVariable(externalParameter, false);
				}
			}
			
			// if we use ubuntu image version etc. from the nodeType not some
			// property/parameter
			if (externalParameter.equals("AMIid") && ubuntuAMIIdVar != null) {
				createEC2InternalExternalPropsInput.put(externalParameter, ubuntuAMIIdVar);
				continue;
			}
			
			// if the variable is still null, something was not specified
			// properly
			if (variable == null) {
				Handler.LOG.warn("Didn't find  property variable for parameter " + externalParameter);
				return false;
			}
			
			if (Utils.isVariableValueEmpty(variable, context)) {
				createEC2InternalExternalPropsInput.put(externalParameter, null);
			} else {
				createEC2InternalExternalPropsInput.put(externalParameter, variable);
			}
			
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
		
		return true;
	}
	
}
