package ba.prephase.plugin.linuxpackageda.handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.model.plan.TOSCAPlan;
import org.opentosca.planbuilder.model.tosca.AbstractDeploymentArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.model.tosca.conventions.Properties;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * <p>
 * This class handles installing Linux packages on an InfrastructureNode using
 * the EC2 Linux IA Service and appropiate BPEL Fragments
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class Handler {

	private final static Logger LOG = LoggerFactory.getLogger(Handler.class);
	private BPELFragments res;


	/**
	 * Contructor
	 */
	public Handler() {
		try {
			this.res = new BPELFragments();
		} catch (ParserConfigurationException e) {
			Handler.LOG.error("Initializing Handler failed", e);
		}

	}

	/**
	 * Adds BPEL Fragments to deploy the defined packages inside the given DA to
	 * the given InfrastructureNode
	 *
	 * @param context a TemplateContext
	 * @param da an AbstractDeploymentArtifact containing PackageInformations
	 * @param infraNodeTemplate an InfrastructureNodeTemplate containing an
	 *            ServerIp Property
	 * @return true iff adding the fragments was successful
	 */
	public boolean handle(TemplatePlanContext context, AbstractDeploymentArtifact da, AbstractNodeTemplate infraNodeTemplate) {
		List<String> packageNames = this.fetchPackageNames(da);

		// register linux file upload webservice in plan
		QName portType = null;
		try {
			portType = context.registerPortType(this.res.getPortTypeFromLinuxUploadWSDL(), this.res.getLinuxFileUploadWSDLFile());
		} catch (IOException e) {
			Handler.LOG.error("Reading File from FragmentHandler failed", e);
			return false;
		}

		// register partnerlink
		String partnerLinkTypeName = "ec2linuxPLT" + context.getIdForNames();
		context.addPartnerLinkType(partnerLinkTypeName, "server", portType);
		String partnerLinkName = "ec2linuxPL" + context.getIdForNames();
		context.addPartnerLinkToTemplateScope(partnerLinkName, partnerLinkTypeName, null, "server", true);

		// register request- and response-message variables
		// {http://ec2linux.aws.ia.opentosca.org}installPackageRequest
		// {http://ec2linux.aws.ia.opentosca.org}installPackageResponse
		String requestVarName = "local_linuxEc2InstallPackageRequest" + context.getIdForNames();
		context.addVariable(requestVarName, TOSCAPlan.VariableType.MESSAGE, new QName("http://ec2linux.aws.ia.opentosca.org", "installPackageRequest", "ec2linuxIA"));
		String responseVarName = "local_linuxEc2InstallPackageResponse" + context.getIdForNames();
		context.addVariable(responseVarName, TOSCAPlan.VariableType.MESSAGE, new QName("http://ec2linux.aws.ia.opentosca.org", "installPackageResponse", "ec2linuxIA"));

		context.addStringValueToPlanRequest("sshKey");
		// fetch serverip property variable name, planrequestmsgname and
		// assemble remotefilepath
		String varNameServerIp = context.getVariableNameOfProperty(infraNodeTemplate.getId(), Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_SERVERIP);

		String packages = "";
		for (String packageName : packageNames) {
			packages += packageName + " ";
		}
		packages = packages.trim();

		// generate assign
		Node assignNode = null;
		try {
			assignNode = this.res.generateRequestAssignAsNode("assign_local_linuxEc2InstallPackageRequest" + context.getIdForNames(), requestVarName, varNameServerIp, portType.getPrefix(), packages, "input", "payload", "tns");
		} catch (IOException e) {
			Handler.LOG.error("Reading File from FragmentHandler failed", e);
			return false;
		} catch (SAXException e) {
			Handler.LOG.error("Reading Fragment from FragmentHandler failed", e);
			return false;
		}

		assignNode = context.importNode(assignNode);
		context.getPrePhaseElement().appendChild(assignNode);

		Node invokeNode = null;
		try {
			invokeNode = this.res.getPackageInstallInvokeAsNode("invoke_PackageInstall_" + packages.replace(" ", "_") + context.getIdForNames(), partnerLinkName, portType.getPrefix(), requestVarName, responseVarName);
		} catch (SAXException e) {
			Handler.LOG.error("Reading Fragment from FragmentHandler failed", e);
			return false;
		} catch (IOException e) {
			Handler.LOG.error("Reading File from FragmentHandler failed", e);
			return false;
		}
		invokeNode = context.importNode(invokeNode);
		context.getPrePhaseElement().appendChild(invokeNode);

		return true;
	}

	/**
	 * Returns a List of Strings containing package names from the given
	 * DeploymentArtifact
	 *
	 * @param da an AbstractDeploymentArtifact containing PackageInformation
	 * @return a List of Strings
	 */
	private List<String> fetchPackageNames(AbstractDeploymentArtifact da) {
		List<String> packageNames = new ArrayList<String>();
		Element domElement = da.getArtifactRef().getProperties().getDOMElement();
		NodeList childNodes = domElement.getChildNodes();
		for (int index = 0; index < childNodes.getLength(); index++) {
			Node childNode = childNodes.item(index);

			if ((childNode.getLocalName() != null) && childNode.getLocalName().equals("PackageInformation")) {
				Node packageNameAttr = childNode.getAttributes().getNamedItem("packageName");
				packageNames.add(packageNameAttr.getTextContent());
			}
		}
		return packageNames;
	}
}
