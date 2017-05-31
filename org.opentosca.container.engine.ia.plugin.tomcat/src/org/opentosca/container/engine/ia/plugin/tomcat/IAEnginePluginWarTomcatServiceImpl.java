package org.opentosca.container.engine.ia.plugin.tomcat;

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
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.model.AbstractArtifact;
import org.opentosca.container.core.model.AbstractFile;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.service.IHTTPService;
import org.opentosca.container.core.tosca.model.TImplementationArtifact;
import org.opentosca.container.core.tosca.model.TPropertyConstraint;
import org.opentosca.container.engine.ia.plugin.IIAEnginePluginService;
import org.opentosca.container.engine.ia.plugin.tomcat.util.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * IAEnginePlugin for deploying/undeploying a WAR-File on/from a locale Tomcat.
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
 * @see IHTTPService
 */
public class IAEnginePluginWarTomcatServiceImpl implements IIAEnginePluginService {
	
	// Hardcoded location of Tomcat, username & password. Defined in
	// messages.properties.
	// Role "manager-script" has to be assigned in tomcat-user.xml.
	static final private String USERNAME = Messages.TomcatIAEnginePlugin_tomcatUsername;
	static final private String PASSWORD = Messages.TomcatIAEnginePlugin_tomcatPassword;
	static final private String URL = Messages.TomcatIAEnginePlugin_url;
	static final private String TYPES = Messages.TomcatIAEnginePlugin_types;
	static final private String CAPABILITIES = Messages.TomcatIAEnginePlugin_capabilities;
	
	static final private Logger LOG = LoggerFactory.getLogger(IAEnginePluginWarTomcatServiceImpl.class);
	
	private IHTTPService httpService;
	
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public URI deployImplementationArtifact(final CSARID csarID, final QName nodeTypeImplementationID, final QName artifactType, final Document artifactContent, final Document properties, final List<TPropertyConstraint> propertyConstraints, final List<AbstractArtifact> artifacts, final List<String> requiredFeatures) {

		String endpoint = null;
		String endpointSuffix = null;
		AbstractFile warFile = null;
		
		IAEnginePluginWarTomcatServiceImpl.LOG.info("Searching for a deployable WAR-File...");
		
		warFile = this.getWar(artifacts);
		
		// Check if a WAR-File was found.
		if (warFile != null) {

			IAEnginePluginWarTomcatServiceImpl.LOG.info("Deployable WAR-File found: {}", warFile.getName());
			
			endpointSuffix = this.getServiceEndpointProperty(properties);
			
			IAEnginePluginWarTomcatServiceImpl.LOG.debug("EndpointSuffix found: {}", endpointSuffix);
			
			// If placeholder is specified, the war file shouldn't be deployed
			// locally
			
			final String placeholderBegin = "/PLACEHOLDER_";
			final String placeholderEnd = "_PLACEHOLDER/";
			if (endpointSuffix.contains(placeholderBegin) && endpointSuffix.contains(placeholderEnd)) {

				final String placeholder = endpointSuffix.substring(endpointSuffix.indexOf(placeholderBegin), endpointSuffix.indexOf(placeholderEnd) + placeholderEnd.length());
				
				IAEnginePluginWarTomcatServiceImpl.LOG.debug("Placeholder {} defined. {} won't be deployed on local tomcat.", placeholder, warFile.getName());
				
				IAEnginePluginWarTomcatServiceImpl.LOG.debug("Specified ServiceEndpoint property of {}: {}", warFile.getName(), endpointSuffix);
				
				final String endpointBegin = endpointSuffix.substring(0, endpointSuffix.indexOf(placeholderBegin));
				final String endpointEnd = endpointSuffix.substring(endpointSuffix.lastIndexOf(placeholderEnd) + placeholderEnd.length());
				// Hack, port of tomcat hard-coded 8080. Find a better solution.
				endpoint = endpointBegin + placeholder + ":8080/" + warFile.getName().replace(".war", "") + "/" + endpointEnd;
				
				IAEnginePluginWarTomcatServiceImpl.LOG.debug("Endpoint with placeholder of IA {}: {}", warFile.getName(), endpoint);
				
			} else {
				endpoint = this.deploy(nodeTypeImplementationID, warFile);
				
				// Checks if a endpoint was set.
				if (endpoint != null) {

					// Create final endpoint.
					endpoint = endpoint.concat(endpointSuffix);
					IAEnginePluginWarTomcatServiceImpl.LOG.info("Complete endpoint of IA {}: {}", warFile.getName(), endpoint);
				}
			}
			
			IAEnginePluginWarTomcatServiceImpl.LOG.info("Complete endpoint of IA {}: {}", warFile.getName(), endpoint);
			
		} else {
			IAEnginePluginWarTomcatServiceImpl.LOG.warn("No deployable WAR-File found.");
		}
		
		return this.getURI(endpoint);
	}
	
	/**
	 * Deploys a WAR-File on Tomcat. As path on Tomcat the
	 * NodeTypeImplementationID with removed special characters (except '-') and
	 * the name of the WAR-File (without ".war") is used:
	 * <tt>[NodeTypeImplementationID]/[File-Name]</tt>
	 *
	 * @param warFile WAR-File that should be deployed.
	 * @param nodeTypeImplementationID for identifying the
	 *            nodeTypeImplementation.
	 * @return if deploying was successful. If <tt>null</tt> is returned,
	 *         deploying wasn't successful. Otherwise the endpoint will be
	 *         returned.
	 *
	 */
	private String deploy(final QName nodeTypeImplementationID, final AbstractFile warFile) {

		String endpoint = null;
		String convertedQname = null;
		final String filePath = warFile.getPath();
		final String fileName = warFile.getName().replace(".war", "");
		
		if (this.isRunning()) {

			IAEnginePluginWarTomcatServiceImpl.LOG.debug("URI of file {}.war: {}", fileName, filePath);
			
			try {
				// Needed, cause some characters are not correctly converted
				// in URIs/URLs
				convertedQname = this.getConvertedcsarID(nodeTypeImplementationID);
				final String deployPath = "/" + convertedQname + "/" + fileName;
				
				final String uri = IAEnginePluginWarTomcatServiceImpl.URL + "/manager/text/deploy?update=true&path=" + deployPath;
				
				IAEnginePluginWarTomcatServiceImpl.LOG.info("Tomcat command to deploy IA {}: {}", fileName, uri);
				
				final MultipartEntity uploadEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
				uploadEntity.addPart(fileName, new FileBody(warFile.getFile().toFile()));
				
				IAEnginePluginWarTomcatServiceImpl.LOG.info("Deploying {} ...", fileName);
				
				final HttpResponse httpResponse = this.httpService.Put(uri, uploadEntity, IAEnginePluginWarTomcatServiceImpl.USERNAME, IAEnginePluginWarTomcatServiceImpl.PASSWORD);
				final InputStream inputStream = httpResponse.getEntity().getContent();
				final String response = this.convertStreamToString(inputStream);
				
				IAEnginePluginWarTomcatServiceImpl.LOG.info("Tomcat response: {} ", response);
				
				// Check if WAR-File was deployed successfully.
				if (response.contains("OK - Deployed application at context path " + deployPath) | response.contains("OK - Deployed application at context path [" + deployPath + "]")) {
					IAEnginePluginWarTomcatServiceImpl.LOG.info("{} was deployed successfully.", fileName);
					
					endpoint = IAEnginePluginWarTomcatServiceImpl.URL + deployPath;
					
					IAEnginePluginWarTomcatServiceImpl.LOG.debug("Endpoint of {} : {}", fileName, endpoint);
					
				} else {
					IAEnginePluginWarTomcatServiceImpl.LOG.error("{} wasn't deployed successfully.", fileName);
				}
				
			} catch (final UnsupportedEncodingException e) {
				IAEnginePluginWarTomcatServiceImpl.LOG.error("UnsupportedEncodingException occured while deploying the WAR-File: {}!", fileName, e);
			} catch (final ClientProtocolException e) {
				IAEnginePluginWarTomcatServiceImpl.LOG.error("ClientProtocolException occured while deploying the WAR-File: {}!", fileName, e);
			} catch (final IOException e) {
				IAEnginePluginWarTomcatServiceImpl.LOG.error("IOException occured while deploying the WAR-File: {}!", fileName, e);
			} catch (final SystemException e) {
				IAEnginePluginWarTomcatServiceImpl.LOG.error("SystemException occured while deploying the WAR-File: {}!", fileName, e);
			}
			
		} else {
			IAEnginePluginWarTomcatServiceImpl.LOG.error("Tomcat isn't running or can't be accessed! Can't deploy {}!", fileName);
		}
		
		return endpoint;
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public boolean undeployImplementationArtifact(final String iaName, final QName nodeTypeImpl, final CSARID csarID, final URI path) {

		if (this.isRunning()) {

			String command = null;
			String convertedQname = null;
			String pathString;
			String tempPath;
			String fileName = null;
			String deployPath;
			
			// Needed, cause some characters are not correctly converted
			// in URIs/URLs
			convertedQname = this.getConvertedcsarID(nodeTypeImpl);
			pathString = path.toString();
			tempPath = pathString.replace(IAEnginePluginWarTomcatServiceImpl.URL + "/" + convertedQname + "/", "");
			fileName = tempPath.substring(0, tempPath.indexOf("/"));
			deployPath = "/" + convertedQname + "/" + fileName;
			
			IAEnginePluginWarTomcatServiceImpl.LOG.debug("Path of IA {} to undeploy: {}", iaName, pathString);
			IAEnginePluginWarTomcatServiceImpl.LOG.debug("Converted CsarID of IA to undeploy: {}", convertedQname);
			IAEnginePluginWarTomcatServiceImpl.LOG.debug("Path without Tomcat url and converted CsarID: {}", tempPath);
			IAEnginePluginWarTomcatServiceImpl.LOG.debug("Name of IA to undeploy: {}", fileName);
			
			// Tomcat command to undeploy a WAR-File.
			command = IAEnginePluginWarTomcatServiceImpl.URL + "/manager/text/undeploy?path=" + deployPath;
			
			IAEnginePluginWarTomcatServiceImpl.LOG.debug("Tomcat command: {}", command);
			IAEnginePluginWarTomcatServiceImpl.LOG.info("Undeploying {} ...", fileName);
			
			try {

				final HttpResponse httpResponse = this.httpService.Get(command, IAEnginePluginWarTomcatServiceImpl.USERNAME, IAEnginePluginWarTomcatServiceImpl.PASSWORD);
				final String response = this.convertStreamToString(httpResponse.getEntity().getContent());
				
				IAEnginePluginWarTomcatServiceImpl.LOG.info(response);
				
				// Check if WAR-File was deployed successfully.
				if (response.contains("OK - Undeployed application at context path " + deployPath)) {
					IAEnginePluginWarTomcatServiceImpl.LOG.info("{} was undeployed successfully.", iaName);
					
					return true;
					
				} else {
					IAEnginePluginWarTomcatServiceImpl.LOG.error("{} wasn't undeployed successfully", iaName);
				}
				
			} catch (final IOException e) {
				IAEnginePluginWarTomcatServiceImpl.LOG.error("A I/O Exception occurred while undeploying IA: {} ", iaName, e);
			}
			
		} else {
			IAEnginePluginWarTomcatServiceImpl.LOG.error("Tomcat isn't running or can't be accessed! Can't undeploy {}!", iaName);
		}
		
		return false;
	}
	
	/**
	 * Checks if the Artifacts contains a WAR-File and returns it if so.
	 *
	 * @param artifacts to check.
	 * @return WAR-File if available. Otherwise <tt>null</tt>.
	 */
	private AbstractFile getWar(final List<AbstractArtifact> artifacts) {

		// Check if there are artifacts
		if (artifacts != null) {
			// Check if artifacts contains a WAR-File.
			for (final AbstractArtifact artifact : artifacts) {
				final Set<AbstractFile> files = artifact.getFilesRecursively();
				for (final AbstractFile file : files) {
					// TODO maybe we should deploy all WARs in Artifact, not
					// only
					// the first one found
					if (this.isADeployableWar(file)) {
						IAEnginePluginWarTomcatServiceImpl.LOG.info("Deployable WAR-File with name {} found.", file.getName());
						
						return file;
						
					}
				}
			}
		}
		
		return null;
		
	}
	
	/**
	 *
	 * @param file to check.
	 * @return if file is a WAR-File that can be deployed.
	 */
	private boolean isADeployableWar(final AbstractFile file) {

		if (file.getName().toLowerCase().endsWith(".war")) {
			return true;
		} else {
			IAEnginePluginWarTomcatServiceImpl.LOG.warn("Although the plugin-type and the IA-type are matching, the file {} can't be un-/deployed from this plugin.", file.getName());
		}
		
		return false;
	}
	
	/**
	 * Checks if a "ServiceEndpoint" property was specified in the Tosca.xml and
	 * returns it if so.
	 *
	 * @param properties to check for endpoint information.
	 * @return serviceEndpoint if specified. Otherwise <tt>""</tt>.
	 */
	private String getServiceEndpointProperty(final Document properties) {

		String endpointSuffix = "";
		
		// Checks if there are specified properties at all.
		if (properties != null) {

			final NodeList list = properties.getFirstChild().getChildNodes();
			
			for (int i = 0; i < list.getLength(); i++) {

				final Node propNode = list.item(i);
				
				if (this.containsServiceEndpointProperty(propNode)) {
					endpointSuffix = this.getNodeContent(propNode);
					IAEnginePluginWarTomcatServiceImpl.LOG.info("ServiceEndpoint property found: {}", endpointSuffix);
					return endpointSuffix;
				}
			}
		}
		
		IAEnginePluginWarTomcatServiceImpl.LOG.debug("No ServiceEndpoint property found!");
		return endpointSuffix;
	}
	
	/**
	 * Checks if the Node contains endpoint information, that will be used to
	 * generate the complete endpoint. Endpoint information has to be specified
	 * with <tt>{@literal <}namespace:ServiceEndpoint{@literal >}...
	 * {@literal <}/namespace:ServiceEndpoint{@literal >}</tt>.
	 *
	 * @param currentNode to check.
	 * @return if currentNode contains endpoint information.
	 */
	private boolean containsServiceEndpointProperty(final Node currentNode) {
		final String localName = currentNode.getLocalName();
		
		if (localName != null) {
			return localName.equals("ServiceEndpoint");
		}
		return false;
	}
	
	/**
	 * @param currentNode where to get the content from.
	 * @return Content of currentNode as String.
	 */
	private String getNodeContent(final Node currentNode) {
		final String nodeContent = currentNode.getTextContent().trim();
		return nodeContent;
	}
	
	/**
	 * @param endpoint to create URI from.
	 * @return URI of endpoint.
	 */
	private URI getURI(final String endpoint) {
		URI endpointURI = null;
		if (endpoint != null) {
			try {
				endpointURI = new URI(endpoint);
			} catch (final URISyntaxException e) {
				IAEnginePluginWarTomcatServiceImpl.LOG.error("URISyntaxException occurred while creating endpoint URI: {} ", endpoint, e);
			}
		}
		return endpointURI;
	}
	
	/**
	 * @param qname
	 * @return String with removed special characters (except '-').
	 */
	private String getConvertedcsarID(final QName qname) {

		final String qnameString = qname.toString();
		
		IAEnginePluginWarTomcatServiceImpl.LOG.debug("Converting QName: {} ...", qnameString);
		
		// Remove all special characters except '-' and '_'
		final String convertedCsarID = qnameString.replaceAll("[^-a-zA-Z0-9_]", "");
		
		IAEnginePluginWarTomcatServiceImpl.LOG.debug("Converted QName: {}", convertedCsarID);
		
		return convertedCsarID;
	}
	
	/**
	 *
	 * @return If Tomcat is running and can be accessed.
	 */
	private boolean isRunning() {

		boolean isRunning = false;
		
		// URL to get serverinfo from Tomcat.
		final String url = IAEnginePluginWarTomcatServiceImpl.URL + "/manager/text/serverinfo";
		
		IAEnginePluginWarTomcatServiceImpl.LOG.info("Checking if Tomcat is running on '" + IAEnginePluginWarTomcatServiceImpl.URL + "' and can be accessed...");
		
		// Execute the Tomcat command and get response message back. If no
		// exception occurs, Tomcat is running.
		HttpResponse httpResponse;
		
		try {
			httpResponse = this.httpService.Get(url, IAEnginePluginWarTomcatServiceImpl.USERNAME, IAEnginePluginWarTomcatServiceImpl.PASSWORD);
			
			final String response = this.convertStreamToString(httpResponse.getEntity().getContent());
			
			IAEnginePluginWarTomcatServiceImpl.LOG.debug(response);
			
			if (response.contains("OK - Server info")) {
				IAEnginePluginWarTomcatServiceImpl.LOG.info("Tomcat is running and can be accessed!");
				isRunning = true;
			}
			
		} catch (final ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final IOException e) {
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
	private String convertStreamToString(final InputStream inputStream) {
		String theString = "";
		try {
			theString = IOUtils.toString(inputStream, "UTF-8");
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return theString;
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public List<String> getSupportedTypes() {
		IAEnginePluginWarTomcatServiceImpl.LOG.debug("Getting Types: {}.", IAEnginePluginWarTomcatServiceImpl.TYPES);
		final List<String> types = new ArrayList<>();
		
		for (final String type : IAEnginePluginWarTomcatServiceImpl.TYPES.split("[,;]")) {
			types.add(type.trim());
		}
		return types;
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public List<String> getCapabilties() {
		IAEnginePluginWarTomcatServiceImpl.LOG.debug("Getting Plugin-Capabilities: {}.", IAEnginePluginWarTomcatServiceImpl.CAPABILITIES);
		final List<String> capabilities = new ArrayList<>();
		
		for (final String capability : IAEnginePluginWarTomcatServiceImpl.CAPABILITIES.split("[,;]")) {
			capabilities.add(capability.trim());
		}
		return capabilities;
	}
	
	/**
	 * Register IHTTPService.
	 *
	 * @param service - A IHTTPService to register.
	 */
	public void bindHTTPService(final IHTTPService httpService) {
		if (httpService != null) {
			this.httpService = httpService;
			IAEnginePluginWarTomcatServiceImpl.LOG.debug("Register IHTTPService: {} registered.", httpService.toString());
		} else {
			IAEnginePluginWarTomcatServiceImpl.LOG.error("Register IHTTPService: Supplied parameter is null!");
		}
	}
	
	/**
	 * Unregister IHTTPService.
	 *
	 * @param service - A IHTTPService to unregister.
	 */
	public void unbindHTTPService(final IHTTPService httpService) {
		this.httpService = null;
		IAEnginePluginWarTomcatServiceImpl.LOG.debug("Unregister IHTTPService: {} unregistered.", httpService.toString());
	}
}
