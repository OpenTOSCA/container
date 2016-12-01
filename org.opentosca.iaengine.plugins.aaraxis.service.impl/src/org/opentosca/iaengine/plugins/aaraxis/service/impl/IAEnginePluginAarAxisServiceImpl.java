package org.opentosca.iaengine.plugins.aaraxis.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.message.BasicNameValuePair;
import org.opentosca.core.model.artifact.AbstractArtifact;
import org.opentosca.core.model.artifact.file.AbstractFile;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.exceptions.SystemException;
import org.opentosca.iaengine.plugins.aaraxis.service.impl.util.Messages;
import org.opentosca.iaengine.plugins.service.IIAEnginePluginService;
import org.opentosca.model.tosca.TImplementationArtifact;
import org.opentosca.model.tosca.TPropertyConstraint;
import org.opentosca.util.http.service.IHTTPService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * IAEnginePlugin for deploying/undeploying a AAR-File on/from Axis2.<br>
 * <br>
 *
 * Copyright 2012 IAAS University of Stuttgart <br>
 * <br>
 *
 * This plugin gets a {@link TImplementationArtifact} from the IAEngine,
 * searches for an any-element like
 * <tt>{@literal <}namespace:Path{@literal >}...
 * {@literal <}/namespace:Path{@literal >}</tt>, that identifies the file that
 * should be deployed, gets the file from the CoreFileService and tries to
 * deploy it via the IHTTPService. In case of ImplementationArtifacts with an
 * any-element like <tt>{@literal <}namespace:ServiceEndpoint{@literal >}...
 * {@literal <}/namespace:ServiceEndpoint{@literal >}</tt> (particularly used
 * with WSDL IAs) the plugin also adds the content of the any-element to the
 * previously generated endpoint.<br>
 * <br>
 * The undeployment process works similar.
 *
 * @see IHTTPService
 *
 * @TODO Since Axis2 uses the Service-Name of the WebService (defined in
 *       services.xml within the archive) as endpoint and not the name of the
 *       AAR-File, this causes problems if File-Name and Service-Name are not
 *       equal.
 *
 * @TODO Fix problems that occur when a THOR contains AAR-Files with same
 *       Service-Name but different functions, as Axis2 differs Services based
 *       on their Service-Names and not their File-Names.
 *
 * @TODO Fix endpoint handling at all.
 *
 *
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 *
 * @TODO: Comments!
 *
 */
public class IAEnginePluginAarAxisServiceImpl implements IIAEnginePluginService {
	
	
	// Hardcoded location of Axis2, username & password. Defined in
	// messages.properties.
	static private final String USERNAME = Messages.AarAxisIAEnginePlugin_axisUsername;
	static private final String PASSWORD = Messages.AarAxisIAEnginePlugin_axisPassword;
	static private final String URL = Messages.AarAxisIAEnginePlugin_url;
	static final private String TYPES = Messages.AarAxisIAEnginePlugin_types;
	static final private String CAPABILITIES = Messages.AarAxisIAEnginePlugin_capabilities;

	static final private Logger LOG = LoggerFactory.getLogger(IAEnginePluginAarAxisServiceImpl.class);

	private IHTTPService httpService;


	@Override
	/**
	 * {@inheritDoc}
	 *
	 * @TODO: Change to deployImplementationArtifact(QName artifactType,
	 *        Document artifactContent, Document properties, List
	 *        <PropertyConstraints> propertyConstraints, List<IFile> files, List
	 *        <URI> requiredFeatures) when IFile is created!
	 */
	public URI deployImplementationArtifact(CSARID csarID, QName nodeTypeImplementationID, QName artifactType, Document artifactContent, Document properties, List<TPropertyConstraint> propertyConstraints, List<AbstractArtifact> artifacts, List<String> requiredFeatures) {
		
		String endpoint = null;
		String endpointSuffix = null;
		Path aarFile = null;

		IAEnginePluginAarAxisServiceImpl.LOG.info("Searching for a deployable AAR-File...");

		aarFile = this.getAar(artifacts);

		// Check if a AAR-File was found.
		if (aarFile != null) {
			endpoint = this.deploy(aarFile);

			// Checks if a endpoint was set.
			if (endpoint != null) {
				
				endpointSuffix = this.getEndpointSuffix(properties);

				// Create final endpoint.
				endpoint = endpoint.concat(endpointSuffix);
				IAEnginePluginAarAxisServiceImpl.LOG.info("Complete endpoint of IA {}: {}", aarFile.getFileName(), endpoint);
			}

		} else {
			IAEnginePluginAarAxisServiceImpl.LOG.warn("No deployable AAR-File found.");
		}

		return this.getURI(endpoint);
	}

	/**
	 * Deploys a AAR-File on Axis2. Axis2 takes the Service-Name within the
	 * AAR-File as path.
	 *
	 * @param aarFile AAR-File that should be deployed.
	 * @return if deploying was successful. If <tt>null</tt> is returned,
	 *         deploying wasn't successful. Otherwise the endpoint will be
	 *         returned.
	 */
	private String deploy(Path aarFile) {
		
		String endpoint = null;
		String fileName = null;

		if (this.isRunning()) {
			
			String filePath = aarFile.toString();
			fileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1, filePath.lastIndexOf("."));

			try {
				
				IAEnginePluginAarAxisServiceImpl.LOG.debug("URI of file {}.aar: {}", fileName, filePath);

				IAEnginePluginAarAxisServiceImpl.LOG.info("Deploying {} ...", fileName);

				// Create POST request with needed cookies and AAR-File for
				// deployment.
				List<Cookie> cookies = this.getCookies();
				MultipartEntity uploadEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
				uploadEntity.addPart("filename", new FileBody(aarFile.toFile()));
				String url = IAEnginePluginAarAxisServiceImpl.URL + "/axis2-admin/upload";
				HttpResponse response = this.httpService.Post(url, uploadEntity, cookies);

				IAEnginePluginAarAxisServiceImpl.LOG.debug("Axis2 uploadresponse: " + response.getStatusLine().toString());
				IAEnginePluginAarAxisServiceImpl.LOG.info("Deploying finished.");
				IAEnginePluginAarAxisServiceImpl.LOG.info("Checking if {} was deployed successfully...", fileName);

				if (this.isDeployed(fileName)) {
					
					IAEnginePluginAarAxisServiceImpl.LOG.info("{} was deployed successfully.", fileName);

					endpoint = IAEnginePluginAarAxisServiceImpl.URL + "/services/" + fileName;

					IAEnginePluginAarAxisServiceImpl.LOG.debug("Endpoint of {} : {}", fileName, endpoint);

				} else {
					IAEnginePluginAarAxisServiceImpl.LOG.warn("{} wasn't deployed successfully", fileName);
				}

			} catch (UnsupportedEncodingException e) {
				IAEnginePluginAarAxisServiceImpl.LOG.error("UnsupportedEncodingException occured: ", e);
			} catch (ClientProtocolException e) {
				IAEnginePluginAarAxisServiceImpl.LOG.error("ClientProtocolException occured: ", e);
			} catch (IOException e) {
				IAEnginePluginAarAxisServiceImpl.LOG.error("IOException occured: ", e);
			}

		} else {
			IAEnginePluginAarAxisServiceImpl.LOG.warn("Axis2 isn't running or can't be accessed! Can't deploy {}!", fileName);

		}
		return endpoint;
	}

	/**
	 * Checks if the artifact contains a AAR-File and returns it if so.
	 *
	 * @param files to check.
	 * @return AAR-File if available. Otherwise <tt>null</tt>.
	 */
	private Path getAar(List<AbstractArtifact> artifacts) {
		
		// Check if there are artifacts
		if (artifacts != null) {
			// Check if artifacts contains a AAR-File.
			for (AbstractArtifact artifact : artifacts) {
				Set<AbstractFile> files = artifact.getFilesRecursively();
				for (AbstractFile file : files) {
					if (this.isADeployableAar(file)) {
						
						IAEnginePluginAarAxisServiceImpl.LOG.info("Deployable AAR-File with name {} found.", file.getName());

						try {
							return file.getFile();
						} catch (SystemException exc) {
							IAEnginePluginAarAxisServiceImpl.LOG.warn("An System Exception occured.", exc);
						}

						return null;

					}
				}
			}
		}
		return null;
	}

	/**
	 *
	 * @param file to check.
	 * @return if file is a AAR-File that can be deployed.
	 */
	private boolean isADeployableAar(AbstractFile file) {
		
		if (file.getName().toLowerCase().endsWith(".aar")) {
			return true;
		} else {
			IAEnginePluginAarAxisServiceImpl.LOG.warn("Although the plugin-type and the IA-type are matching, the file {} can't be un-/deployed from this plugin.", file.getName());
		}

		return false;
	}

	/**
	 * Checks if a endpointSuffix was specified in the Tosca.xml and returns it
	 * if so.
	 *
	 * @param properties to check for endpoint information.
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
					IAEnginePluginAarAxisServiceImpl.LOG.info("ServiceEndpointSuffix found: {}", endpointSuffix);
					return endpointSuffix;
				}
			}
		}
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
	private boolean containsEndpointSuffix(Node currentNode) {
		String localName = currentNode.getLocalName();
		IAEnginePluginAarAxisServiceImpl.LOG.debug(localName);
		if (localName != null) {
			return localName.equals("ServiceEndpoint");
		}
		return false;
	}

	// /**
	// * @param filePath where to get the File-Name from.
	// * @return Name of the file.
	// */
	// private String getFileName(String filePath) {
	// String fileName = filePath.substring(filePath.lastIndexOf('/') + 1,
	// filePath.lastIndexOf("."));
	// return fileName;
	// }

	/**
	 * @param currentNode where to get the content from.
	 * @return Content of currentNode as String.
	 */
	private String getNodeContent(Node currentNode) {
		String nodeContent = currentNode.getTextContent().trim();
		return nodeContent;
	}

	/**
	 * @param endpoint to create URI from.
	 * @return URI of endpoint.
	 */
	private URI getURI(String endpoint) {
		URI endpointURI = null;
		if (endpoint != null) {
			try {
				endpointURI = new URI(endpoint);
			} catch (URISyntaxException e) {
				IAEnginePluginAarAxisServiceImpl.LOG.error("URISyntaxException occurred while creating endpoint URI: {} ", endpoint, e);
			}
		}
		return endpointURI;
	}

	/**
	 *
	 * @return if Axis2 is running.
	 */
	private boolean isRunning() {
		
		boolean isRunning = false;
		IAEnginePluginAarAxisServiceImpl.LOG.info("Checking if Axis2 is running and can be accessed...");

		try {
			HttpResponse response = this.httpService.Head(IAEnginePluginAarAxisServiceImpl.URL);

			if (response.getStatusLine().toString().contains("200 OK")) {
				isRunning = true;

				IAEnginePluginAarAxisServiceImpl.LOG.info("Axis2 is running and can be accessed!");
			}
		} catch (ClientProtocolException e) {
			IAEnginePluginAarAxisServiceImpl.LOG.error("ClientProtocolException occured:", e);
		} catch (IOException e) {
			IAEnginePluginAarAxisServiceImpl.LOG.error("IOException occured:", e);
		}
		return isRunning;
	}

	/**
	 * Handles the cookies needed for login to Axis2.
	 *
	 * @return cookies needed for accessing Axis2.
	 */
	private List<Cookie> getCookies() {
		
		List<NameValuePair> nvps = new ArrayList<NameValuePair>(2);
		nvps.add(new BasicNameValuePair("userName", IAEnginePluginAarAxisServiceImpl.USERNAME));
		nvps.add(new BasicNameValuePair("password", IAEnginePluginAarAxisServiceImpl.PASSWORD));

		List<Cookie> cookies = null;

		try {
			HttpEntity loginEntity = new UrlEncodedFormEntity(nvps);
			String url = IAEnginePluginAarAxisServiceImpl.URL + "/axis2-admin/login";
			cookies = this.httpService.PostCookies(url, loginEntity);

		} catch (ClientProtocolException e) {
			IAEnginePluginAarAxisServiceImpl.LOG.error("ClientProtocolException occured: ", e);
		} catch (IOException e) {
			IAEnginePluginAarAxisServiceImpl.LOG.error("IOException occured: ", e);
		}

		return cookies;
	}

	/**
	 * Checks if a WebService with given name is deployed and running.
	 *
	 * @param fileName of WebService to check.
	 * @return if WebService is deployed and running.
	 *
	 */
	private boolean isDeployed(String fileName) {
		
		boolean isDeployed = false;

		try {
			// "Needed" for checking if WS was un/deployed successfully because
			// Axis2 needs a moment to update WSs.
			Thread.sleep(10000);

			List<Cookie> cookies = this.getCookies();
			String url = IAEnginePluginAarAxisServiceImpl.URL + "/axis2-admin/ListSingleService?serviceName=" + fileName;
			HttpResponse response = this.httpService.Get(url, cookies);

			HttpEntity entity = response.getEntity();
			InputStream is = entity.getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));

			String str;
			// Parse html if WebService is marked as "Active" to check if iit is
			// running.
			while ((str = br.readLine()) != null) {
				if (str.contains("<i><font color=\"blue\">Service Status : Active</font></i><br>")) {
					isDeployed = true;
				}
				if (str.contains("<font color=\"red\">") && !str.contains("No services found in this location")) {
					IAEnginePluginAarAxisServiceImpl.LOG.warn("The WebService {} has deployment faults: {}", fileName, str);
				}
			}

		} catch (ClientProtocolException e) {
			IAEnginePluginAarAxisServiceImpl.LOG.error("ClientProtocolException occured:", e);
		} catch (IOException e) {
			IAEnginePluginAarAxisServiceImpl.LOG.error("IOException occured:", e);
		} catch (InterruptedException e) {
			IAEnginePluginAarAxisServiceImpl.LOG.error("InterruptedException occured:", e);
		}

		return isDeployed;
	}

	@Override
	/**
	 * {@inheritDoc}
	 */
	public List<String> getSupportedTypes() {
		IAEnginePluginAarAxisServiceImpl.LOG.debug("Getting Types: {}.", IAEnginePluginAarAxisServiceImpl.TYPES);
		List<String> types = new ArrayList<String>();

		for (String type : IAEnginePluginAarAxisServiceImpl.TYPES.split("[,;]")) {
			types.add(type.trim());
		}
		return types;
	}

	@Override
	/**
	 * {@inheritDoc}
	 */
	public List<String> getCapabilties() {
		IAEnginePluginAarAxisServiceImpl.LOG.debug("Getting Plugin-Capabilities: {}.", IAEnginePluginAarAxisServiceImpl.CAPABILITIES);
		List<String> capabilities = new ArrayList<String>();

		for (String capability : IAEnginePluginAarAxisServiceImpl.CAPABILITIES.split("[,;]")) {
			capabilities.add(capability.trim());
		}
		return capabilities;
	}

	/**
	 * Register IHTTPService.
	 *
	 * @param service - A IHTTPService to register.
	 */
	public void bindHTTPService(IHTTPService httpService) {
		if (httpService != null) {
			this.httpService = httpService;
			IAEnginePluginAarAxisServiceImpl.LOG.debug("Register IHTTPService: {} registered.", httpService.toString());
		} else {
			IAEnginePluginAarAxisServiceImpl.LOG.error("Register IHTTPService: Supplied parameter is null!");
		}
	}

	/**
	 * Unregister IHTTPService.
	 *
	 * @param service - A IHTTPService to unregister.
	 */
	public void unbindHTTPService(IHTTPService httpService) {
		this.httpService = null;
		IAEnginePluginAarAxisServiceImpl.LOG.debug("Unregister IHTTPService: {} unregistered.", httpService.toString());
	}

	@Override
	public boolean undeployImplementationArtifact(String iaName, QName nodeTypeImpl, CSARID csarID, URI path) {
		// TODO
		return false;
	}

}
