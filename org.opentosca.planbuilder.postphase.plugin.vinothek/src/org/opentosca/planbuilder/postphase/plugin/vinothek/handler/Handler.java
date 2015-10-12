package org.opentosca.planbuilder.postphase.plugin.vinothek.handler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.FileLocator;
import org.opentosca.core.model.artifact.file.AbstractFile;
import org.opentosca.core.model.csar.CSARContent;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.exceptions.SystemException;
import org.opentosca.exceptions.UserException;
import org.opentosca.planbuilder.csarhandler.CSARHandler;
import org.opentosca.planbuilder.model.tosca.AbstractArtifactReference;
import org.opentosca.planbuilder.model.tosca.AbstractDeploymentArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTypeImplementation;
import org.opentosca.planbuilder.plugins.commons.Properties;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.opentosca.planbuilder.postphase.plugin.vinothek.Plugin;
import org.opentosca.planbuilder.utils.Utils;
import org.osgi.framework.FrameworkUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * 
 * Copyright 2014 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kalman Kepes - nyuuyn@googlemail.com
 *
 */
public class Handler {

	private final QName zipArtifactType = new QName(
			"http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes",
			"ArchiveArtifact");
	private final QName bpelArtifactType = new QName(
			"http://docs.oasis-open.org/wsbpel/2.0/process/executable", "BPEL");

	private final CSARHandler csarHandler = new CSARHandler();
	private DocumentBuilderFactory docFactory;
	private DocumentBuilder docBuilder;

	public Handler() throws ParserConfigurationException {
		this.docFactory = DocumentBuilderFactory.newInstance();
		this.docFactory.setNamespaceAware(true);
		this.docBuilder = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
	}

	public boolean handle(TemplatePlanContext context,
			AbstractNodeTemplate nodeTemplate,
			AbstractNodeTypeImplementation nodeImpl) {
		if (Utils.checkForTypeInHierarchy(nodeTemplate, Plugin.phpApp)) {
			return this.handlePhpApp(context, nodeTemplate, nodeImpl);
		} else if (Utils.checkForTypeInHierarchy(nodeTemplate,
				Plugin.bpelProcess)) {
			return this.handleBPELApp(context, nodeTemplate, nodeImpl);
		}
		return false;
	}

	private boolean handlePhpApp(TemplatePlanContext context,
			AbstractNodeTemplate nodeTemplate,
			AbstractNodeTypeImplementation nodeImpl) {

		// fetch the application zip file
		AbstractArtifactReference zipRef = null;

		if (nodeImpl == null) {
			zipRef = this.fetchPhpAppDA(nodeTemplate.getDeploymentArtifacts());
		} else {
			zipRef = this.fetchPhpAppDA(nodeImpl.getDeploymentArtifacts());
		}

		if (zipRef == null) {
			// didn't find appropiate artifact ref
			return false;
		}

		try {

			CSARContent content = this.csarHandler
					.getCSARContentForID(new CSARID(context.getCSARFileName()));
			String reference = zipRef.getReference();
			Set<AbstractFile> files = content.getFilesRecursively();
			AbstractFile daFile = null;

			for (AbstractFile file : files) {
				String path = file.getPath();

				// this decode is used as counter-measure against the double
				// encoding in winery
				if (file.getPath()
						.equals(URLDecoder.decode(reference, "UTF-8"))) {
					daFile = file;
				}
			}

			if (daFile == null) {
				return false;
			}

			// we'll try to find the root dir, which should resemble the
			// application path on an apache web server
			Path daPath = daFile.getFile();
			String absPath = daPath.toAbsolutePath().toString();

			ZipFile zipFile = new ZipFile(absPath);

			Enumeration<? extends ZipEntry> entries = zipFile.entries();

			List<ZipEntry> dirEntries = new ArrayList<ZipEntry>();

			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				if (entry.isDirectory()) {
					dirEntries.add(entry);
				}
			}

			// the entry with the shortest name should be our root dir
			ZipEntry rootDirEntry = dirEntries.get(0);

			for (ZipEntry entry : dirEntries) {
				if (entry.getName().length() <= rootDirEntry.getName().length()) {
					rootDirEntry = entry;
				}
			}

			// this value will be concatenated with the form
			// http://VMIP/applicationFolderName
			String applicationFolderName = "/" + rootDirEntry.getName();
			zipFile.close();

			// find serverip var name of the VM hosting the application
			String serverIpVarName = context
					.getVariableNameOfInfraNodeProperty(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_SERVERIP);

			// add selfserviceApplicationUrl to output
			context.addStringValueToPlanResponse("selfserviceApplicationUrl");

			Element postPhaseElement = context.getPostPhaseElement();

			Node assignNode = this.createSelfserviceApplicationUrlAssign(
					serverIpVarName, applicationFolderName,
					context.getPlanResponseMessageName(), "payload", "tns");
			assignNode = context.importNode(assignNode);

			postPhaseElement.appendChild(assignNode);

		} catch (UserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
	}

	private boolean handleBPELApp(TemplatePlanContext context,
			AbstractNodeTemplate nodeTemplate,
			AbstractNodeTypeImplementation nodeImpl) {
		// FIXME: this will be working under many assumptions (bpel-engine: wso2
		// bps.., no port reconfigs,..)
		// we try to construct an endpoint of the form
		// ServerIP:9763/services/BPELStack_buildPlanService/

		List<AbstractDeploymentArtifact> das = new ArrayList<AbstractDeploymentArtifact>();

		das.addAll(nodeTemplate.getDeploymentArtifacts());

		if (nodeImpl != null) {
			das.addAll(nodeImpl.getDeploymentArtifacts());
		}

		AbstractArtifactReference bpelRef = this.fetchBPELAppDA(das);

		if (bpelRef == null) {
			return false;
		}

		CSARContent content;
		try {
			content = this.csarHandler.getCSARContentForID(new CSARID(context
					.getCSARFileName()));
			String reference = bpelRef.getReference();
			Set<AbstractFile> files = content.getFilesRecursively();
			AbstractFile daFile = null;

			for (AbstractFile file : files) {
				String path = file.getPath();

				// this decode is used as counter-measure against the double
				// encoding in winery
				if (file.getPath()
						.equals(URLDecoder.decode(reference, "UTF-8"))) {
					daFile = file;
				}
			}

			if (daFile == null) {
				return false;
			}

			// we'll try to find the root dir, which should resemble the
			// application path on an apache web server
			Path daPath = daFile.getFile();
			String absPath = daPath.toAbsolutePath().toString();

			ZipFile zipFile = new ZipFile(absPath);

			Enumeration<? extends ZipEntry> entries = zipFile.entries();

			List<ZipEntry> fileEntries = new ArrayList<ZipEntry>();

			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				if (!entry.isDirectory()) {
					fileEntries.add(entry);
				}
			}

			// the entry with the shortest name should be our root dir
			ZipEntry deployXmlEntry = null;

			for (ZipEntry entry : fileEntries) {

				if (entry.getName().equals("deploy.xml")) {
					deployXmlEntry = entry;
				}
			}

			InputStream is = zipFile.getInputStream(deployXmlEntry);

			XPathFactory factory = XPathFactory.newInstance();

			XPath xpath = factory.newXPath();

			xpath.setNamespaceContext(new NamespaceContext() {

				private String ns = "http://www.apache.org/ode/schemas/dd/2007/03";

				@Override
				public Iterator getPrefixes(String namespaceURI) {
					return null;
				}

				@Override
				public String getPrefix(String namespaceURI) {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public String getNamespaceURI(String prefix) {
					return this.ns;
				}
			});

			InputSource inputSource = new InputSource(is);

			String value = (String) xpath
					.evaluate(
							"/ns:deploy/ns:process/ns:provide[@partnerLink='client']/ns:service/@name",
							inputSource, XPathConstants.STRING);

			String serviceName = value.split(":")[1];
			String applicationFolderName = "/services/" + serviceName;

			zipFile.close();

			// find serverip var name of the VM hosting the application
			String serverIpVarName = context
					.getVariableNameOfInfraNodeProperty(Properties.OPENTOSCA_DECLARATIVE_PROPERTYNAME_SERVERIP);

			// add selfserviceApplicationUrl to output
			context.addStringValueToPlanResponse("selfserviceApplicationUrl");

			Element postPhaseElement = context.getPostPhaseElement();

			Node assignNode = this.createSelfserviceApplicationUrlAssign(
					serverIpVarName, applicationFolderName,
					context.getPlanResponseMessageName(), "payload", "tns");
			assignNode = context.importNode(assignNode);

			postPhaseElement.appendChild(assignNode);

		} catch (UserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	private AbstractArtifactReference fetchPhpAppDA(
			List<AbstractDeploymentArtifact> das) {
		for (AbstractDeploymentArtifact da : das) {
			if (da.getArtifactType().equals(this.zipArtifactType)) {
				for (AbstractArtifactReference ref : da.getArtifactRef()
						.getArtifactReferences()) {
					if (ref.getReference().endsWith(".zip")) {
						return ref;
					}
				}
			}
		}
		return null;
	}

	private AbstractArtifactReference fetchBPELAppDA(
			List<AbstractDeploymentArtifact> das) {
		for (AbstractDeploymentArtifact da : das) {
			if (da.getArtifactType().equals(this.bpelArtifactType)) {
				for (AbstractArtifactReference ref : da.getArtifactRef()
						.getArtifactReferences()) {
					if (ref.getReference().endsWith(".zip")) {
						return ref;
					}
				}
			}
		}

		return null;
	}

	private Node createSelfserviceApplicationUrlAssign(String serverIpVarName,
			String applicationName, String outputVarName,
			String outputVarPartName, String outputVarPrefix)
			throws IOException, SAXException {
		// <!--{serverIpVarName} {appName} {outputVarName} {outputVarPartName}
		// {outputVarPrefix} -->

		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext()
				.getBundle().getResource("assignSelfserviceApplicationUrl.xml");
		File bpelfragmentfile = new File(FileLocator.toFileURL(url).getPath());
		String template = FileUtils.readFileToString(bpelfragmentfile);
		template = template.replace("{serverIpVarName}", serverIpVarName);
		template = template.replace("{appName}", applicationName);
		template = template.replace("{outputVarName}", outputVarName);
		template = template.replace("{outputVarPartName}", outputVarPartName);
		template = template.replace("{outputVarPrefix}", outputVarPrefix);

		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(template));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}

}
