package org.opentosca.planbuilder.generic.plugin.ec2vm.handler;

import java.io.IOException;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.model.plan.BuildPlan;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.plugins.constants.PluginConstants;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
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

	private ResourceHandler handler;
	private static final String[] amazonCredentials = {"instanceType", "sshKey", "ami", "region", "accessKey", "secretKey", "securityGroup", "keyPairName"};


	/**
	 * Constructor
	 */
	public Handler() {
		try {
			this.handler = new ResourceHandler();
		} catch (ParserConfigurationException e) {
			Handler.LOG.error("Couldn't initialize internal ResourceHandler", e);
		}
	}

	/**
	 * Adds fragments to provision a EC2 VM
	 *
	 * @param context a TemplatePlanContext for a EC2, VM or Ubuntu Node
	 * @param nodeTemplate the NodeTemplate on which the fragments are used
	 * @return true iff adding the fragments was successful
	 */
	public boolean handle(TemplatePlanContext context, AbstractNodeTemplate nodeTemplate) {
		// register portTypes
		QName ec2PortTypeQName = null;
		QName ec2CallbackPortTypeQName = null;
		try {
			ec2PortTypeQName = context.registerPortType(this.handler.getPortType(), this.handler.getEC2WSDLFile());
			ec2CallbackPortTypeQName = context.registerPortType(this.handler.getCallbackPortType(), this.handler.getEC2WSDLFile());
		} catch (IOException e) {
			Handler.LOG.error("Couldn't fetch internal WSDL file", e);
			return false;
		}

		// register partnerlinkType and partnerLink
		String partnerLinkTypeName = "ec2VmPLT" + context.getIdForNames();
		context.addPartnerLinkType(partnerLinkTypeName, "Requester", ec2CallbackPortTypeQName, "Requestee", ec2PortTypeQName);
		String partnerLinkName = "ec2VmPl" + context.getIdForNames();
		context.addPartnerLinkToTemplateScope(partnerLinkName, partnerLinkTypeName, "Requester", "Requestee", true);

		// register request and response variables
		String createEc2RequestVarName = "createEc2Request" + context.getIdForNames();
		context.addVariable(createEc2RequestVarName, BuildPlan.VariableType.MESSAGE, new QName("http://ec2vm.aws.ia.opentosca.org", "createEC2InstanceRequest"));
		String createEc2ResponseVarName = "createEc2Response" + context.getIdForNames();
		context.addVariable(createEc2ResponseVarName, BuildPlan.VariableType.MESSAGE, new QName("http://ec2vm.aws.ia.opentosca.org", "createEC2InstanceResponse"));

		String getPublicDNSRequestVarName = "getPublicDNSRequest" + context.getIdForNames();
		context.addVariable(getPublicDNSRequestVarName, BuildPlan.VariableType.MESSAGE, new QName("http://ec2vm.aws.ia.opentosca.org", "getPublicDNSRequest"));
		String getPublicDNSResponseVarName = "getPublicDNSResponse" + context.getIdForNames();
		context.addVariable(getPublicDNSResponseVarName, BuildPlan.VariableType.MESSAGE, new QName("http://ec2vm.aws.ia.opentosca.org", "getPublicDNSResponse"));

		// add properties for createEc2Request and getPublicDNS

		String createEc2Property = "createEC2Property" + context.getIdForNames();
		context.addProperty(createEc2Property, new QName("http://www.w3.org/2001/XMLSchema", "string", "xsd"));
		// for request
		QName createEc2ReqQName = context.importQName(new QName("http://ec2vm.aws.ia.opentosca.org", "createEC2InstanceRequest"));
		context.addPropertyAlias(createEc2Property, createEc2ReqQName, "parameters", "/" + createEc2ReqQName.getPrefix() + ":CorrelationId");
		// for response
		QName createEc2ResQName = context.importQName(new QName("http://ec2vm.aws.ia.opentosca.org", "createEC2InstanceResponse"));
		context.addPropertyAlias(createEc2Property, createEc2ResQName, "parameters", "/" + createEc2ResQName.getPrefix() + ":CorrelationId");

		String getPublicDNSProperty = "getPublicDNsProperty" + context.getIdForNames();
		context.addProperty(getPublicDNSProperty, new QName("http://www.w3.org/2001/XMLSchema", "string", "xsd"));
		// for request
		QName getPublicDNSReqQName = context.importQName(new QName("http://ec2vm.aws.ia.opentosca.org", "getPublicDNSRequest"));
		context.addPropertyAlias(getPublicDNSProperty, getPublicDNSReqQName, "parameters", "/" + getPublicDNSReqQName.getPrefix() + ":CorrelationId");
		// for respone
		QName getPublicDNSResQName = context.importQName(new QName("http://ec2vm.aws.ia.opentosca.org", "getPublicDNSResponse"));
		context.addPropertyAlias(getPublicDNSProperty, getPublicDNSResQName, "parameters", "/" + getPublicDNSResQName.getPrefix() + ":CorrelationId");

		// register correlationsets
		String ec2CorrelationSetName = "createEc2CorrelationSet" + context.getIdForNames();
		context.addCorrelationSet(ec2CorrelationSetName, createEc2Property);

		String getPublicDnsCorrelationSetName = "getPublicDnsCorrelationSet" + context.getIdForNames();
		context.addCorrelationSet(getPublicDnsCorrelationSetName, getPublicDNSProperty);

		// add amazon credentials to plan input message
		for (String credential : Handler.amazonCredentials) {
			context.addStringValueToPlanRequest(credential);
		}

		String serverIpToscaPropName = context.getVariableNameOfInfraNodeProperty(PluginConstants.OPENTOSCA_DECLARATIVE_PROPERTYNAME_SERVERIP);

		// add fragment
		try {
			Node fragment = this.handler.getBPELFragmentAsNode(createEc2RequestVarName, createEc2ResponseVarName, ec2CorrelationSetName, getPublicDNSRequestVarName, getPublicDNSResponseVarName, getPublicDnsCorrelationSetName, partnerLinkName, context.getPlanRequestMessageName(), "payload", ec2PortTypeQName.getPrefix(), serverIpToscaPropName);
			fragment = context.importNode(fragment);
			context.getProvisioningPhaseElement().appendChild(fragment);
		} catch (SAXException e) {
			Handler.LOG.error("Couldn't generate BPEL fragment to handle a EC2VM", e);
			return false;
		} catch (IOException e) {
			Handler.LOG.error("Couldn't generate BPEL fragment to handle a EC2VM", e);
			return false;
		}

		return true;
	}
}
