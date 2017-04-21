package org.opentosca.container.connector.bps;

import java.io.File;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.xml.namespace.QName;

import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.client.Stub;
import org.apache.axis2.java.security.SSLProtocolSocketFactory;
import org.apache.axis2.java.security.TrustAllTrustManager;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.bps.management.schema.DeployedPackagesPaginated;
import org.wso2.bps.management.schema.EndpointRef_type0;
import org.wso2.bps.management.schema.GetAllProcesses;
import org.wso2.bps.management.schema.GetProcessInfoIn;
import org.wso2.bps.management.schema.ListDeployedPackagesPaginated;
import org.wso2.bps.management.schema.Package_type0;
import org.wso2.bps.management.schema.ProcessIDList;
import org.wso2.bps.management.schema.ProcessInfo;
import org.wso2.bps.management.schema.UndeployBPELPackage;
import org.wso2.bps.management.wsdl.bpelpackagemanagement.BPELPackageManagementServiceStub;
import org.wso2.bps.management.wsdl.bpelpackagemanagement.PackageManagementException;
import org.wso2.bps.management.wsdl.processmanagement.ProcessManagementException;
import org.wso2.bps.management.wsdl.processmanagement.ProcessManagementServiceStub;
import org.wso2.carbon.bpel.deployer.services.BPELUploaderStub;
import org.wso2.carbon.bpel.deployer.services.UploadService;
import org.wso2.carbon.bpel.deployer.services.types.xsd.UploadedFileItem;
import org.wso2.carbon.core.services.authentication.AuthenticationAdminAuthenticationExceptionException;
import org.wso2.carbon.core.services.authentication.AuthenticationAdminStub;
import org.wso2.carbon.core.services.authentication.Login;

/**
 * This class is a connector to deploy and undeploy a
 * <a href="http://docs.oasis-open.org/wsbpel/2.0/wsbpel-v2.0.html">WS-BPEL 2.0
 * Processes</a> on a
 * <a href="http://wso2.com/products/business-process-server/">WSO2 Business
 * Process Server.</a>
 *
 * The class uses the generated stubs of: <br>
 * <ul>
 * <li>/META-INF/resources/BPELUploader.wsdl</li>
 * <li>/META-INF/resources/AuthenticationAdmin.wsdl</li>
 * </ul>
 * These .wsdl files are published in the backend of a WSO2 BPS and allow
 * authentication plus upload.
 *
 * @see <a href="http://wso2.com/products/business-process-server/">WSO2
 *      Business Process Server</a>
 * @see <a href="http://docs.oasis-open.org/wsbpel/2.0/wsbpel-v2.0.html">WS-BPEL
 *      2.0 Processes</a>
 */

public class BpsConnector {
	
	private String adress;
	private String user;
	private String passwd;
	private String cookie;
	
	private final static Logger LOG = LoggerFactory.getLogger(BpsConnector.class);
	
	
	/**
	 * Sets the endpoint of this connector
	 *
	 * @param uri the uri to the endpoint of WSO2 BPS
	 */
	private void setEndpoint(final String uri) {
		BpsConnector.LOG.debug("Setting address");
		this.adress = uri;
	}
	
	/**
	 * Sets the login data of this connector
	 *
	 * @param user admin name
	 * @param pw admin password
	 */
	private void setLogin(final String user, final String pw) {
		BpsConnector.LOG.debug("Setting login data");
		this.user = user;
		this.passwd = pw;
	}
	
	/**
	 * Deploys a WS-BPEL 2.0 process unto the referenced WSO2 BPS
	 *
	 * @param process the process to deploy packaged for a WSO2 BPS
	 * @param uri the URI of the WSO2 BPS
	 * @param user the username for the WSO2 BPS
	 * @param pw the password for the WSO2 BPS
	 * @return a string containing the PID (ProcessId) of the deployed process
	 *         if everything was successful, else null
	 */
	public String deploy(final File process, final String uri, final String user, final String pw) {
		if ((uri == null) | (user == null) | (pw == null)) {
			return null;
		}
		String pid = null;
		try {
			this.setEndpoint(uri);
			this.setLogin(user, pw);
			this.login();
			final String fileName = process.getName();
			final String fileType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
			BpsConnector.LOG.debug("Trying to deploy file: {}", process.getAbsolutePath());
			final String packageId = this.deployFile(process, fileName, fileType);
			List<QName> pidsOfPackage = new ArrayList<>();
			// this is a "brutal" hack <=> pulling from server until a pid is
			// set
			int pullCount = 0;
			while (pidsOfPackage.isEmpty() & (pullCount < 50)) {
				BpsConnector.LOG.debug("Polling for pid with packageId " + packageId);
				pidsOfPackage = this.getPIDsForPackageId(packageId, uri, user, pw);
				if (pidsOfPackage.isEmpty()) {
					// as we don't want the bps to be overworked we wait here
					try {
						Thread.sleep(2000);
					} catch (final InterruptedException e) {
						e.printStackTrace();
					}
				}
				// for safety
				pullCount++;
			}
			
			pid = this.calcHighestPid(pidsOfPackage, packageId);
			
		} catch (final RemoteException e) {
			BpsConnector.LOG.error("RemoteException: Server not available", e);
			return null;
		} catch (final AuthenticationAdminAuthenticationExceptionException e) {
			BpsConnector.LOG.error("Auth at BPS failed", e);
			return null;
		} catch (final ProcessManagementException e) {
			BpsConnector.LOG.error("The Process isn't valid", e);
			return null;
		} catch (final UnknownHostException e) {
			BpsConnector.LOG.error("UnknownHostException: ", e);
			return null;
		}
		return pid;
	}
	
	private String calcHighestPid(final List<QName> pids, final String packageId) {
		BpsConnector.LOG.debug("Starting to calculate highest PID number for package: " + packageId);
		if (pids.isEmpty()) {
			BpsConnector.LOG.warn("PID list is empty");
			return null;
		}
		final List<Integer> idNums = new ArrayList<>();
		String ns = null;
		for (final QName pid : pids) {
			if (ns == null) {
				ns = pid.getNamespaceURI();
				BpsConnector.LOG.debug("Found namespace for PIDs:" + ns);
			}
			final String localPart = pid.getLocalPart();
			BpsConnector.LOG.debug("PID has localPart " + localPart + " defined");
			
			final String idNum = localPart.substring(localPart.lastIndexOf("-") + 1);
			
			BpsConnector.LOG.debug("Trying to parse PID Number " + idNum);
			idNums.add(Integer.valueOf(idNum));
		}
		Collections.sort(idNums);
		final int highestNumber = idNums.get(idNums.size() - 1);
		for (final QName pid : pids) {
			if (pid.getLocalPart().endsWith("-" + String.valueOf(highestNumber))) {
				return pid.toString();
			}
		}
		
		return null;
	}
	
	/**
	 * Returns pids for the given package on the referenced bps
	 *
	 * @param packageId a String representing the packageId on a bps
	 * @param uri the uri to the bps
	 * @param user the user account
	 * @param pw the password for the user account
	 * @return a possibly empty List of QName denoting PIDs
	 */
	public List<QName> getPIDsForPackageId(final String packageId, final String uri, final String user, final String pw) {
		final List<QName> pids = new ArrayList<>();
		
		try {
			// set up authentication
			this.setEndpoint(uri);
			this.setLogin(user, pw);
			this.login();
			
			BpsConnector.LOG.debug("Fetching pid for package: " + packageId);
			// set up service stub and data for request
			final ProcessManagementServiceStub stub = this.getProcessManagementServiceStub();
			final GetAllProcesses processReq = new GetAllProcesses();
			
			// takes all processes, taken from
			// http://wso2.org/project/bps/2.1.0/docs/management_api.html
			processReq.setGetAllProcesses("name}}* namespace=*");
			
			// another pulling hack
			
			ProcessIDList pidList = null;
			while (pidList == null) {
				// send request
				pidList = ((ProcessManagementServiceStub) this.setCookie(stub)).getAllProcesses(processReq);
			}
			
			// this can happen if the bps has no process deployed
			if ((pidList != null) && (pidList.getPid() != null)) {
				BpsConnector.LOG.debug("Found following PIDs:");
				for (final String pid : pidList.getPid()) {
					BpsConnector.LOG.debug("pid: " + pid);
				}
				
				// get ProcessInfo per pid
				final GetProcessInfoIn req2 = new GetProcessInfoIn();
				for (final String pid : pidList.getPid()) {
					req2.setPid(QName.valueOf(pid));
					// request process info for pid
					final ProcessInfo info = ((ProcessManagementServiceStub) this.setCookie(stub)).getProcessInfo(req2);
					BpsConnector.LOG.debug("Checking packageName for Pid: " + pid);
					BpsConnector.LOG.debug("Package name of PID is: " + info.getProcessInfo().getDeploymentInfo().getPackageName());
					if (info.getProcessInfo().getDeploymentInfo().getPackageName().startsWith(packageId + "-")) {
						pids.add(QName.valueOf(pid));
					}
				}
			}
		} catch (final UnknownHostException e1) {
			BpsConnector.LOG.error("Host address not reachable", e1);
		} catch (final AuthenticationAdminAuthenticationExceptionException e1) {
			BpsConnector.LOG.error("Error with request-processing at AdminAuthenticationService", e1);
		} catch (final RemoteException e) {
			BpsConnector.LOG.error("Error while sending Request", e);
		} catch (final ProcessManagementException e) {
			BpsConnector.LOG.error("Error with request-processing at ProcessManagementService", e);
		}
		return pids;
	}
	
	/**
	 * Undeploys processes from the referenced WSO2 BPS
	 *
	 * @param PID The ProcessId (on a WSO2 BPS) of the WS-BPEL 2.0 process to
	 *            undeploy
	 * @param uri the uri of the WSO2 BPS to undeploy from
	 * @param user the username for the WSO2 BPS
	 * @param pw the password for the WSO2 BPS
	 * @return true if undeployment was successful
	 */
	public boolean undeploy(final String PID, final String uri, final String user, final String pw) {
		if ((uri == null) | (user == null) | (pw == null)) {
			return false;
		}
		final UndeployBPELPackage request = new UndeployBPELPackage();
		request.set_package(PID);
		try {
			this.setEndpoint(uri);
			this.setLogin(user, pw);
			this.login();
			final BPELPackageManagementServiceStub stub = this.getBPELPackageManagementServiceStub();
			((BPELPackageManagementServiceStub) this.setCookie(stub)).undeployBPELPackage(request);
		} catch (final AxisFault e) {
			BpsConnector.LOG.error("AxisFault: ", e);
			return false;
		} catch (final RemoteException e) {
			BpsConnector.LOG.error("Remote Exception: Server not available: ", e);
			return false;
		} catch (final PackageManagementException e) {
			BpsConnector.LOG.error("PackageManagementException: Package wasn't valid: ", e);
			return false;
		} catch (final AuthenticationAdminAuthenticationExceptionException e) {
			BpsConnector.LOG.error("AuthenticationAdminAuthenticationException: Logindata wasn't valid", e);
			return false;
		} catch (final UnknownHostException e) {
			BpsConnector.LOG.error("UnknownHostException: System can't determine localhost", e);
			return false;
		}
		return true;
	}
	
	/**
	 * Undeploys a BPEL Process which is given as a zip file
	 *
	 * @param file the BPEL Process to undeploy as a zip file
	 * @param uri the URI of the BPS the process has to be undeployed
	 * @param user the userId to authenticate with the BPS
	 * @param pw the password to for the given user account
	 * @return true iff undeployment was successful
	 */
	public boolean undeploy(final File file, final String uri, final String user, final String pw) {
		// check input
		if ((file == null) | (user == null) | (pw == null)) {
			BpsConnector.LOG.error("Some parameter is null!");
			if (file == null) {
				BpsConnector.LOG.error("BPEL process file is null");
			}
			if (user == null) {
				BpsConnector.LOG.error("UserId is null");
			}
			if (pw == null) {
				BpsConnector.LOG.error("Password is null");
			}
			return false;
		}
		
		BpsConnector.LOG.debug("Path of process file: " + file.getAbsolutePath());
		BpsConnector.LOG.debug("File name: " + file.getName());
		BpsConnector.LOG.debug("UserName: " + user);
		BpsConnector.LOG.debug("UserPw: " + pw);
		
		if (!file.isFile()) {
			BpsConnector.LOG.error("Path of file doesn't denote to a file");
			return false;
		}
		
		if (!file.getName().endsWith(".zip")) {
			BpsConnector.LOG.error("File is not a .zip file");
			return false;
		}
		
		// setup request message
		final UndeployBPELPackage request = new UndeployBPELPackage();
		final String packageName = file.getName().substring(0, file.getName().length() - 4);
		BpsConnector.LOG.debug("Trying to undeploy with packageName: " + packageName);
		request.set_package(packageName);
		
		try {
			// authenticate with bps
			this.setEndpoint(uri);
			this.setLogin(user, pw);
			this.login();
		} catch (final RemoteException e) {
			BpsConnector.LOG.error("Some remote exception occured while calling the BPS", e);
			return false;
		} catch (final UnknownHostException e) {
			BpsConnector.LOG.error("The given URI doesn't point to a BPS", e);
			return false;
		} catch (final AuthenticationAdminAuthenticationExceptionException e) {
			BpsConnector.LOG.error("Some error at the BPS occured", e);
			return false;
		}
		
		try {
			// Init axis2 stub
			final BPELPackageManagementServiceStub stub = this.getBPELPackageManagementServiceStub();
			// set the cookie from authentication and call BPS backend
			((BPELPackageManagementServiceStub) this.setCookie(stub)).undeployBPELPackage(request);
		} catch (final AxisFault e) {
			BpsConnector.LOG.error("Initializing Axis2 Stub for BPS access failed", e);
			return false;
		} catch (final RemoteException e) {
			BpsConnector.LOG.error("Some remote exception occured while calling the BPS", e);
			return false;
		} catch (final PackageManagementException e) {
			BpsConnector.LOG.error("Some error at the BPS occured", e);
			return false;
		}
		
		return true;
	}
	
	/**
	 * Logs into the WSO2 BPS declared in the address field
	 *
	 * @throws RemoteException is thrown when connection failed
	 * @throws AuthenticationAdminAuthenticationExceptionException is thrown
	 *             when login was wrong
	 * @throws UnknownHostException is thrown when no connection can be
	 *             established
	 */
	private void login() throws RemoteException, AuthenticationAdminAuthenticationExceptionException, UnknownHostException {
		BpsConnector.LOG.debug("Logging in to BPS");
		final Login login = new Login();
		login.setUsername(this.user);
		login.setPassword(this.passwd);
		login.setRemoteAddress(InetAddress.getLocalHost().getHostAddress());
		final AuthenticationAdminStub stub = this.getAuthenticationAdminStub();
		stub._getServiceClient().getOptions().setManageSession(true);
		stub.login(login);
		this.cookie = (String) stub._getServiceClient().getLastOperationContext().getProperty(HTTPConstants.COOKIE_STRING);
	}
	
	/**
	 * Deploys process referenced in FileObject
	 *
	 * @param file process to deploy
	 * @param fileName name of the process file
	 * @param fileType type of the process file
	 * @return Id Id of the uploaded package
	 * @throws RemoteException is thrown when no connection is available
	 * @throws ProcessManagementException is thrown when deployment had a
	 *             failure
	 */
	private String deployFile(final File file, final String fileName, final String fileType) throws RemoteException, ProcessManagementException {
		final String fileNameshort = fileName.substring(0, fileName.indexOf("." + fileType));
		
		final UploadedFileItem tempFile = new UploadedFileItem();
		tempFile.setDataHandler(new DataHandler(new FileDataSource(file)));
		tempFile.setFileName(fileName);
		tempFile.setFileType(fileType);
		final UploadService upload = new UploadService();
		upload.addFileItems(tempFile);
		
		final BPELUploaderStub stub = this.getBPELUploaderStub();
		
		((BPELUploaderStub) this.setCookie(stub)).uploadService(upload);
		return fileNameshort;
	}
	
	/**
	 * Returns the deployed packages on the given BPS
	 *
	 * @param uri the address to the bps
	 * @param user user account
	 * @param pw user account password
	 * @return a list of strings containing the names of the deployed packages
	 */
	public List<String> getDeployedPackages(final String uri, final String user, final String pw) {
		final List<String> packageIds = new ArrayList<>();
		
		try {
			this.setEndpoint(uri);
			this.setLogin(user, pw);
			this.login();
			
			// set up service stub and data for request
			final BPELPackageManagementServiceStub stub = this.getBPELPackageManagementServiceStub();
			
			final ListDeployedPackagesPaginated processReq = new ListDeployedPackagesPaginated();
			int count = 0;
			int pages = 1;
			
			while (count < pages) {
				processReq.setPage(count);
				// make request
				final DeployedPackagesPaginated result = ((BPELPackageManagementServiceStub) this.setCookie(stub)).listDeployedPackagesPaginated(processReq);
				
				// process response
				pages = result.getPages();
				for (final Package_type0 packageInResult : result.get_package()) {
					packageIds.add(packageInResult.getName());
				}
				count++;
			}
		} catch (final AxisFault e) {
			BpsConnector.LOG.error("Error with Axis2 Framework", e);
		} catch (final RemoteException e) {
			BpsConnector.LOG.error("Error while sending request", e);
		} catch (final PackageManagementException e) {
			BpsConnector.LOG.error("Error with request-processing at PackageManagementService", e);
		} catch (final UnknownHostException e) {
			BpsConnector.LOG.error("Error with host address", e);
		} catch (final AuthenticationAdminAuthenticationExceptionException e) {
			BpsConnector.LOG.error("Error with request-processing at AdminAuthenticationService", e);
		}
		return packageIds;
	}
	
	/**
	 * Sets the cookie for a given axis2 client request
	 *
	 * @param stub org.apache.axis2.client.Stub Object of the Axis2 library
	 * @return the changed stub
	 */
	private Stub setCookie(final Stub stub) {
		stub._getServiceClient().getOptions().setProperty(Constants.Configuration.ENABLE_MTOM, Constants.VALUE_TRUE);
		stub._getServiceClient().getOptions().setManageSession(true);
		stub._getServiceClient().getOptions().setTimeOutInMilliSeconds(60000000);
		stub._getServiceClient().getOptions().setProperty(HTTPConstants.COOKIE_STRING, this.cookie);
		return stub;
	}
	
	/**
	 * Returns a map from partnerLink as string to an endpoint as URI, denoting
	 * the partnerLink
	 *
	 * @param pid the PID of the BPEL 2.0 Process, from which the partnerlink
	 *            endpoints should be determined
	 * @param uri the URI to WSO2 BPS (2.1.2 - 3.0.0)
	 * @param user the login username for the WSO2 BPS
	 * @param pw the login password for the WSO2 BPS
	 * @return a Map from String to URI denoting partnerLinks and their
	 *         endpoints
	 */
	public Map<String, URI> getEndpointsForPID(final String pid, final String uri, final String user, final String pw) {
		final Map<String, URI> partnerLinkToEndpointURIs = new HashMap<>();
		if (pid == null) {
			BpsConnector.LOG.warn("PID is null! Not possible to find Endpoints on BPS");
			return partnerLinkToEndpointURIs;
		}
		
		if (uri == null) {
			BpsConnector.LOG.warn("URI for BPS is null");
			return partnerLinkToEndpointURIs;
		}
		
		if (user == null) {
			BpsConnector.LOG.warn("User for BPS is null");
			return partnerLinkToEndpointURIs;
		}
		
		if (pw == null) {
			BpsConnector.LOG.warn("Password for BPS is null");
			return partnerLinkToEndpointURIs;
		}
		
		BpsConnector.LOG.debug("Trying to get all endpoints");
		BpsConnector.LOG.debug("Using PID: " + pid);
		BpsConnector.LOG.debug("Using URI: " + uri);
		BpsConnector.LOG.debug("Using User: " + user);
		BpsConnector.LOG.debug("Using Password: " + pw);
		
		// set up authentication
		try {
			this.setEndpoint(uri);
			this.setLogin(user, pw);
			this.login();
			
			// set up service stub and data for request
			final ProcessManagementServiceStub stub = this.getProcessManagementServiceStub();
			final GetProcessInfoIn processReq = new GetProcessInfoIn();
			processReq.setPid(QName.valueOf(pid));
			
			// make request
			final ProcessInfo info = ((ProcessManagementServiceStub) this.setCookie(stub)).getProcessInfo(processReq);
			
			BpsConnector.LOG.debug("Looking for endpoint for process " + info.getProcessInfo().getDefinitionInfo().getProcessName());
			
			// process response
			for (final EndpointRef_type0 endpointRef : info.getProcessInfo().getEndpoints().getEndpointRef()) {
				
				BpsConnector.LOG.debug("Found partnerlink: " + endpointRef.getPartnerLink());
				BpsConnector.LOG.debug("Found service: " + endpointRef.getService().toString());
				
				for (final String endpointString : endpointRef.getServiceLocations().getServiceLocation()) {
					try {
						partnerLinkToEndpointURIs.put(endpointRef.getPartnerLink(), new URI(endpointString.replace("?tryit", "")));
					} catch (final URISyntaxException e) {
						e.printStackTrace();
					}
				}
			}
			
		} catch (final UnknownHostException e1) {
			BpsConnector.LOG.error("Error with host address", e1);
		} catch (final AuthenticationAdminAuthenticationExceptionException e1) {
			BpsConnector.LOG.error("Error with request-processing at AdminAuthenticationService", e1);
		} catch (final AxisFault e1) {
			BpsConnector.LOG.error("Error in Axis2 Framework", e1);
		} catch (final RemoteException e1) {
			BpsConnector.LOG.error("Error while sending request", e1);
		} catch (final ProcessManagementException e1) {
			BpsConnector.LOG.error("Error with request-processing at ProcessManagementService", e1);
		}
		
		return partnerLinkToEndpointURIs;
	}
	
	public List<String> getAllPIDs(final String uri, final String user, final String pw) {
		final List<String> pidStringList = new ArrayList<>();
		
		try {
			// set up authentication
			this.setEndpoint(uri);
			this.setLogin(user, pw);
			this.login();
			
			// set up service stub and data for request
			final ProcessManagementServiceStub stub = this.getProcessManagementServiceStub();
			final GetAllProcesses processReq = new GetAllProcesses();
			
			// takes all processes, taken from
			// http://wso2.org/project/bps/2.1.0/docs/management_api.html
			processReq.setGetAllProcesses("name}}* namespace=*");
			
			// send request
			final ProcessIDList pidList = ((ProcessManagementServiceStub) this.setCookie(stub)).getAllProcesses(processReq);
			
			// check for case when there are no process deployed anymore
			if (pidList.getPid() == null) {
				BpsConnector.LOG.debug("Returned ProcessIDList from BPS is null, assuming no process is deployed on BPS");
				return new ArrayList<>();
			}
			
			// process response
			for (final String pid : pidList.getPid()) {
				pidStringList.add(pid);
			}
		} catch (final UnknownHostException e1) {
			e1.printStackTrace();
		} catch (final AuthenticationAdminAuthenticationExceptionException e1) {
			e1.printStackTrace();
		} catch (final RemoteException e) {
			e.printStackTrace();
		} catch (final ProcessManagementException e) {
			e.printStackTrace();
		}
		
		return pidStringList;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "openTOSCA WSO2 BPS Connector v1.0";
	}
	
	/**
	 * Initializes a ProcessManagementServiceStub with added SSL Options
	 *
	 * @return a ProcessManagementServiceStub with added SSL Options
	 * @throws AxisFault is thrown when initalizing the Axis2 stub fails
	 */
	private ProcessManagementServiceStub getProcessManagementServiceStub() throws AxisFault {
		final ProcessManagementServiceStub stub = new ProcessManagementServiceStub(this.adress + "/services/ProcessManagementService");
		this.setSSLTrustManager(stub._getServiceClient());
		return stub;
	}
	
	/**
	 * Initializes a BPELPackageManagementStub with added SSL Options
	 *
	 * @return a BPELPackageManagementStub with added SSL Options
	 * @throws AxisFault is thrown when initalizing the Axis2 stub fails
	 */
	private BPELPackageManagementServiceStub getBPELPackageManagementServiceStub() throws AxisFault {
		final BPELPackageManagementServiceStub stub = new BPELPackageManagementServiceStub(this.adress + "/services/BPELPackageManagementService");
		this.setSSLTrustManager(stub._getServiceClient());
		return stub;
	}
	
	/**
	 * Initializes a BPELUploaderStub with added SSL Options
	 *
	 * @return a BPELUploaderStub with added SSL Options
	 * @throws AxisFault is thrown when initalizing the Axis2 stub fails
	 */
	private BPELUploaderStub getBPELUploaderStub() throws AxisFault {
		final BPELUploaderStub stub = new BPELUploaderStub(this.adress + "/services/BPELUploader");
		this.setSSLTrustManager(stub._getServiceClient());
		return stub;
	}
	
	/**
	 * Initializes an AuthenticationAdminStub with added SSL Options
	 *
	 * @return an AuthenticationAdminStub with added SSL Options
	 * @throws AxisFault is thrown when initalizing the Axis2 stub fails
	 */
	private AuthenticationAdminStub getAuthenticationAdminStub() throws AxisFault {
		final AuthenticationAdminStub stub = new AuthenticationAdminStub(this.adress + "/services/AuthenticationAdmin");
		this.setSSLTrustManager(stub._getServiceClient());
		return stub;
	}
	
	/**
	 * Sets options on given serviceClient to accept all SSL certificates
	 *
	 * @param serviceClient an Axis2 serviceClient
	 */
	private void setSSLTrustManager(final ServiceClient serviceClient) {
		
		try {
			// following excerpt is taken from here:
			// http://axis.apache.org/axis2/java/core/api/org/apache/axis2/java/security/TrustAllTrustManager.html
			final SSLContext sslCtx = SSLContext.getInstance("SSL");
			sslCtx.init(null, new TrustManager[] {new TrustAllTrustManager()}, null);
			serviceClient.getOptions().setProperty(HTTPConstants.CUSTOM_PROTOCOL_HANDLER, new Protocol("https", (ProtocolSocketFactory) new SSLProtocolSocketFactory(sslCtx), 443));
			
		} catch (final NoSuchAlgorithmException e) {
			BpsConnector.LOG.warn("Couldn't load SSLContext", e);
		} catch (final KeyManagementException e) {
			BpsConnector.LOG.warn("Couldn't load TrustManager into SSLContext", e);
		}
	}
	
}
