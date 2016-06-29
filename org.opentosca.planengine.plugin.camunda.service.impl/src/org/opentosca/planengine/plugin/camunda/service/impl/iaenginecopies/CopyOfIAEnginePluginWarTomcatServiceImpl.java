package org.opentosca.planengine.plugin.camunda.service.impl.iaenginecopies;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.opentosca.core.model.artifact.AbstractArtifact;
import org.opentosca.core.model.artifact.file.AbstractFile;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.model.tosca.TImplementationArtifact;
import org.opentosca.model.tosca.TPropertyConstraint;
import org.opentosca.planengine.plugin.camunda.service.impl.Activator;
import org.opentosca.util.http.service.IHTTPService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * !!!!!!!!!!!!!!!!!!!!! This is a dirty adapted copy of code of the IAEngine!
 * Instructed by Uwe. !!!!!!!!!!!!!!!!!!!!!
 * 
 * IAEnginePlugin for deploying/undeploying a WAR-File on/from a locale Tomcat.
 * <br>
 * <br>
 * 
 * Copyright 2012 IAAS University of Stuttgart <br>
 * <br>
 * 
 * This plugin gets a {@link TImplementationArtifact} from the IAEngine,
 * searches for an any-element like
 * <tt>{@literal <}namespace:Path{@literal >}...
 * {@literal <}/namespace:Path{@literal >}</tt>, that identifies the WAR-File
 * that should be deployed, gets the file from the CoreFileService and tries to
 * deploy it via a Tomcat-manager HTTP request. In case of
 * ImplementationArtifacts with an any-element like
 * <tt>{@literal <}namespace:ServiceEndpoint{@literal >}...
 * {@literal <}/namespace:ServiceEndpoint{@literal >}</tt> (particularly used
 * with WSDL IAs) the plugin also adds the content of the any-element to the
 * previously generated endpoint.<br>
 * <br>
 * The undeployment process works similar.
 * 
 * 
 * @see IHTTPService
 * 
 * @author original: Michael Zimmermann -
 *         zimmerml@studi.informatik.uni-stuttgart.de
 * 
 * 
 */
public class CopyOfIAEnginePluginWarTomcatServiceImpl {

	// Hardcoded location of Tomcat, username & password. Defined in
	// messages.properties.
	// Role "manager-script" has to be assigned in tomcat-user.xml.
	static final private String USERNAME = CopyOfMessages.TomcatIAEnginePlugin_tomcatUsername;
	static final private String PASSWORD = CopyOfMessages.TomcatIAEnginePlugin_tomcatPassword;
	static final private String URL = CopyOfMessages.TomcatIAEnginePlugin_url;
	static final private String TYPES = CopyOfMessages.TomcatIAEnginePlugin_types;
	static final private String CAPABILITIES = CopyOfMessages.TomcatIAEnginePlugin_capabilities;

	static final private Logger LOG = LoggerFactory.getLogger(CopyOfIAEnginePluginWarTomcatServiceImpl.class);

	private IHTTPService httpService;
	
	public CopyOfIAEnginePluginWarTomcatServiceImpl() {
		LOG.trace("Created instance of class");
	}
	
	private void bindServices(){
		BundleContext context = Activator.getContext();
		ServiceReference<IHTTPService> httpService = context.getServiceReference(IHTTPService.class);
		this.httpService = context.getService(httpService);
	}

	/**
	 * {@inheritDoc}
	 */
	public void deployImplementationArtifact(CSARID csarID, QName artifactType, Document artifactContent,
			Document properties, List<TPropertyConstraint> propertyConstraints, File warFile,
			List<String> requiredFeatures) {
		
		bindServices();

		String deployPath = null;
		String endpointSuffix = null;
		// AbstractFile warFile = null;

		CopyOfIAEnginePluginWarTomcatServiceImpl.LOG.info("Searching for a deployable WAR-File...");

		// warFile = this.getWar(artifacts);

		// Check if a WAR-File was found.
		if (warFile != null && warFile.exists()) {
			deployPath = this.deploy(csarID, warFile);

			// Checks if a endpoint was set.
			if (deployPath != null) {

				// endpointSuffix = this.getEndpointSuffix(properties);

				// Create final endpoint.
				// deployPath = deployPath.concat(endpointSuffix);
				CopyOfIAEnginePluginWarTomcatServiceImpl.LOG.info("Complete war deployment of {} to the path {}", warFile.getName(),
						deployPath);
			}

		} else {
			CopyOfIAEnginePluginWarTomcatServiceImpl.LOG.warn("No deployable WAR-File found.");
		}
	}

	/**
	 * Deploys a WAR-File on Tomcat. As path on Tomcat the CSAR-ID with removed
	 * special characters (except '-') and the name of the WAR-File (without
	 * ".war") is used: <tt>[CSAR-ID]/[File-Name]</tt>
	 * 
	 * @param warFile
	 *            WAR-File that should be deployed.
	 * @param csarID
	 *            for identifying the CSAR-File.
	 * @return if deploying was successful. If <tt>null</tt> is returned,
	 *         deploying wasn't successful. Otherwise the endpoint will be
	 *         returned.
	 * 
	 */
	private String deploy(CSARID csarID, File warFile) {

		String endpoint = null;
		String convertedQname = null;
		String filePath = warFile.getPath();
		String fileName = warFile.getName().replace(".war", "");

		if (this.isRunning()) {

			CopyOfIAEnginePluginWarTomcatServiceImpl.LOG.debug("URI of file {}.war: {}", fileName, filePath);

			try {
				// Needed, cause some characters are not correctly converted
				// in URIs/URLs
				convertedQname = this.getConvertedcsarID(csarID);
				String deployPath = "/" + convertedQname + "/" + fileName;

				String uri = CopyOfIAEnginePluginWarTomcatServiceImpl.URL + "/manager/text/deploy?update=true&path="
						+ deployPath;

				CopyOfIAEnginePluginWarTomcatServiceImpl.LOG.info("Tomcat command to deploy IA {}: {}", fileName, uri);

				MultipartEntity uploadEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
				uploadEntity.addPart(fileName, new FileBody(warFile));

				CopyOfIAEnginePluginWarTomcatServiceImpl.LOG.info("Deploying {} ...", fileName);

				HttpResponse httpResponse = this.httpService.Put(uri, uploadEntity,
						CopyOfIAEnginePluginWarTomcatServiceImpl.USERNAME,
						CopyOfIAEnginePluginWarTomcatServiceImpl.PASSWORD);
				InputStream inputStream = httpResponse.getEntity().getContent();
				String response = this.convertStreamToString(inputStream);

				CopyOfIAEnginePluginWarTomcatServiceImpl.LOG.info("Tomcat response: {} ", response);

				// Check if WAR-File was deployed successfully.
				if (response.contains("OK - Deployed application at context path " + deployPath)) {
					CopyOfIAEnginePluginWarTomcatServiceImpl.LOG.info("{} was deployed successfully.", fileName);

					endpoint = CopyOfIAEnginePluginWarTomcatServiceImpl.URL + deployPath;

					CopyOfIAEnginePluginWarTomcatServiceImpl.LOG.debug("Endpoint of {} : {}", fileName, endpoint);

				} else {
					CopyOfIAEnginePluginWarTomcatServiceImpl.LOG.error("{} wasn't deployed successfully.", fileName);
				}

			} catch (UnsupportedEncodingException e) {
				CopyOfIAEnginePluginWarTomcatServiceImpl.LOG
						.error("UnsupportedEncodingException occured while deploying the WAR-File: {}!", fileName, e);
			} catch (ClientProtocolException e) {
				CopyOfIAEnginePluginWarTomcatServiceImpl.LOG
						.error("ClientProtocolException occured while deploying the WAR-File: {}!", fileName, e);
			} catch (IOException e) {
				CopyOfIAEnginePluginWarTomcatServiceImpl.LOG
						.error("IOException occured while deploying the WAR-File: {}!", fileName, e);
			}

		} else {
			CopyOfIAEnginePluginWarTomcatServiceImpl.LOG
					.error("Tomcat isn't running or can't be accessed! Can't deploy {}!", fileName);
		}

		return endpoint;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean undeployImplementationArtifact(String iaName, QName nodeTypeImpl, CSARID csarID, URI path) {

		LOG.warn("Not implemented to undeploy a BPMN Plan!");

		return false;
	}

	/**
	 * Checks if the Artifacts contains a WAR-File and returns it if so.
	 * 
	 * @param artifacts
	 *            to check.
	 * @return WAR-File if available. Otherwise <tt>null</tt>.
	 */
	private AbstractFile getWar(List<AbstractArtifact> artifacts) {

		// Check if there are artifacts
		if (artifacts != null) {
			// Check if artifacts contains a WAR-File.
			for (AbstractArtifact artifact : artifacts) {
				Set<AbstractFile> files = artifact.getFilesRecursively();
				for (AbstractFile file : files) {
					// TODO maybe we should deploy all WARs in Artifact, not
					// only
					// the first one found
					if (this.isADeployableWar(file)) {
						CopyOfIAEnginePluginWarTomcatServiceImpl.LOG.info("Deployable WAR-File with name {} found.",
								file.getName());

						return file;

					}
				}
			}
		}

		return null;

	}

	/**
	 * 
	 * @param file
	 *            to check.
	 * @return if file is a WAR-File that can be deployed.
	 */
	private boolean isADeployableWar(AbstractFile file) {

		if (file.getName().toLowerCase().endsWith(".war")) {
			return true;
		} else {
			CopyOfIAEnginePluginWarTomcatServiceImpl.LOG.warn(
					"Although the plugin-type and the IA-type are matching, the file {} can't be un-/deployed from this plugin.",
					file.getName());
		}

		return false;
	}

	/**
	 * Checks if a endpointSuffix was specified in the Tosca.xml and returns it
	 * if so.
	 * 
	 * @param properties
	 *            to check for endpoint information.
	 * @return endpointSuffix if specified. Otherwise <tt>""</tt>.
	 */
	private String getEndpointSuffix(Document properties) {

		String endpointSuffix = "";

		// Checks if there are specified properties at all.
		if (properties != null) {

			NodeList list = properties.getFirstChild().getChildNodes();

			for (int i = 0; i < list.getLength(); i++) {

				Node propNode = list.item(i);

				if (this.containsEndpointSuffix(propNode)) {
					endpointSuffix = this.getNodeContent(propNode);
					CopyOfIAEnginePluginWarTomcatServiceImpl.LOG.info("ServiceEndpointSuffix found: {}",
							endpointSuffix);
					return endpointSuffix;
				}
			}
		}

		CopyOfIAEnginePluginWarTomcatServiceImpl.LOG.debug("No ServiceEndpointSuffix found!");
		return endpointSuffix;
	}

	/**
	 * Checks if the Node contains endpoint information, that will be used to
	 * generate the complete endpoint. Endpoint information has to be specified
	 * with <tt>{@literal <}namespace:ServiceEndpoint{@literal >}...
	 * {@literal <}/namespace:ServiceEndpoint{@literal >}</tt>.
	 * 
	 * @param currentNode
	 *            to check.
	 * @return if currentNode contains endpoint information.
	 */
	private boolean containsEndpointSuffix(Node currentNode) {
		String localName = currentNode.getLocalName();

		if (localName != null) {
			return localName.equals("ServiceEndpoint");
		}
		return false;
	}

	/**
	 * @param currentNode
	 *            where to get the content from.
	 * @return Content of currentNode as String.
	 */
	private String getNodeContent(Node currentNode) {
		String nodeContent = currentNode.getTextContent().trim();
		return nodeContent;
	}

	/**
	 * @param endpoint
	 *            to create URI from.
	 * @return URI of endpoint.
	 */
	private URI getURI(String endpoint) {
		URI endpointURI = null;
		if (endpoint != null) {
			try {
				endpointURI = new URI(endpoint);
			} catch (URISyntaxException e) {
				CopyOfIAEnginePluginWarTomcatServiceImpl.LOG
						.error("URISyntaxException occurred while creating endpoint URI: {} ", endpoint, e);
			}
		}
		return endpointURI;
	}

	/**
	 * @param csarID
	 *            for identifying the CSAR-File.
	 * @return csarID as String with removed special characters (except '-').
	 */
	private String getConvertedcsarID(CSARID csarID) {

		String qname = csarID.toString();
		CopyOfIAEnginePluginWarTomcatServiceImpl.LOG.debug("Converting QName: {} ...", qname);

		// Remove all special characters except '-'
		String convertedCsarID = qname.replaceAll("[^-a-zA-Z0-9]", "");

		CopyOfIAEnginePluginWarTomcatServiceImpl.LOG.debug("Converted QName: {}", convertedCsarID);

		return convertedCsarID;
	}

	/**
	 * 
	 * @return If Tomcat is running and can be accessed.
	 */
	private boolean isRunning() {

		boolean isRunning = false;

		// URL to get serverinfo from Tomcat.
		String url = CopyOfIAEnginePluginWarTomcatServiceImpl.URL + "/manager/text/serverinfo";

		CopyOfIAEnginePluginWarTomcatServiceImpl.LOG.info("Checking if Tomcat is running on '"
				+ CopyOfIAEnginePluginWarTomcatServiceImpl.URL + "' and can be accessed...");

		// Execute the Tomcat command and get response message back. If no
		// exception occurs, Tomcat is running.
		HttpResponse httpResponse;

		try {
			httpResponse = this.httpService.Get(url, CopyOfIAEnginePluginWarTomcatServiceImpl.USERNAME,
					CopyOfIAEnginePluginWarTomcatServiceImpl.PASSWORD);

			String response = this.convertStreamToString(httpResponse.getEntity().getContent());

			CopyOfIAEnginePluginWarTomcatServiceImpl.LOG.debug(response);

			if (response.contains("OK - Server info")) {
				CopyOfIAEnginePluginWarTomcatServiceImpl.LOG.info("Tomcat is running and can be accessed!");
				isRunning = true;
			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return isRunning;
	}

	/**
	 * Converts incoming InputStream from TOmcat into a String.
	 * 
	 * @param inputStream
	 * @return converted String
	 */
	private String convertStreamToString(InputStream inputStream) {
		String theString = "";
		try {
			theString = IOUtils.toString(inputStream, "UTF-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return theString;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<String> getSupportedTypes() {

		bindServices();
		
		CopyOfIAEnginePluginWarTomcatServiceImpl.LOG.debug("Getting Types: {}.",
				CopyOfIAEnginePluginWarTomcatServiceImpl.TYPES);
		List<String> types = new ArrayList<String>();

		for (String type : CopyOfIAEnginePluginWarTomcatServiceImpl.TYPES.split("[,;]")) {
			types.add(type.trim());
		}
		return types;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<String> getCapabilties() {
		
		bindServices();
		
		CopyOfIAEnginePluginWarTomcatServiceImpl.LOG.debug("Getting Plugin-Capabilities: {}.",
				CopyOfIAEnginePluginWarTomcatServiceImpl.CAPABILITIES);
		List<String> capabilities = new ArrayList<String>();

		for (String capability : CopyOfIAEnginePluginWarTomcatServiceImpl.CAPABILITIES.split("[,;]")) {
			capabilities.add(capability.trim());
		}
		return capabilities;
	}
}
