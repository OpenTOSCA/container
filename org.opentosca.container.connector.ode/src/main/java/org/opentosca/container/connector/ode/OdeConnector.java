package org.opentosca.container.connector.ode;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;

import org.apache.axis.message.MessageElement;
import org.apache.www.ode.deployapi.DeployUnit;
import org.apache.www.ode.deployapi.DeploymentPortType;
import org.apache.www.ode.deployapi.DeploymentServiceLocator;
import org.apache.www.ode.deployapi._package;
import org.apache.www.ode.pmapi.ManagementFault;
import org.apache.www.ode.pmapi.ProcessManagementPortType;
import org.apache.www.ode.pmapi.ProcessManagementServiceLocator;
import org.apache.www.ode.pmapi.types._2006._08._02.TEndpointReferencesEndpointRef;
import org.apache.www.ode.pmapi.types._2006._08._02.TProcessInfo;
import org.apache.www.ode.pmapi.types._2006._08._02.TProcessStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3.www._2005._05.xmlmime.Base64Binary;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class is a connector to deploy and undeploy a
 * <a href="http://docs.oasis-open.org/wsbpel/2.0/wsbpel-v2.0.html">WS-BPEL 2.0 Processes</a> on a
 * <a href="http://ode.apache.org">Apache ODE BPEL engine.</a>
 * <p>
 * The class uses the generated stubs of: <br>
 * <ul>
 * <li>/META-INF/resources/deploy.wsdl</li>
 * </ul>
 * This .wsdl file is published by a Apache ODE and allows the deployment and undeployment of
 * process models.
 *
 * @see <a href="http://ode.apache.org">Apache ODE</a>
 * @see <a href="http://docs.oasis-open.org/wsbpel/2.0/wsbpel-v2.0.html">WS-BPEL 2.0 Processes</a>
 */

public class OdeConnector {

  private final static String NS_SERVICE_REF = "http://docs.oasis-open.org/wsbpel/2.0/serviceref";
  private final static String NS_WS_ADDRESSING = "http://www.w3.org/2005/08/addressing";

  private final static Logger LOG = LoggerFactory.getLogger(OdeConnector.class);

  private String address;

  /**
   * Sets the endpoint of this connector
   *
   * @param uri the uri to the endpoint of Apache ODE
   */
  private void setEndpoint(final String uri) {
    OdeConnector.LOG.debug("Setting address");
    this.address = uri;
  }

  /**
   * Deploys a WS-BPEL 2.0 process unto the referenced Apache ODE
   *
   * @param process the process to deploy packaged for a Apache ODE
   * @param uri     the URI of the Apache ODE
   * @return a string containing the PID (ProcessId) of the deployed process if everything was
   * successful, else null
   */
  public String deploy(final File process, final String uri) throws Exception {
    if (uri == null) {
      return null;
    }
    String pid = null;
    try {
      // Update the service endpoint
      setEndpoint(uri);

      final String fileName = process.getName();
      final String fileType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
      OdeConnector.LOG.debug("Trying to deploy file: {}", process.getAbsolutePath());
      final String packageId = deployFile(process, fileName, fileType);
      List<QName> pidsOfPackage = new ArrayList<>();
      // this is a "brutal" hack <=> pulling from server until a pid is
      // set
      int pullCount = 0;
      while (pidsOfPackage.isEmpty() & pullCount < 50) {
        OdeConnector.LOG.debug("Polling for pid with packageId " + packageId);
        pidsOfPackage = getPIDsForPackageId(packageId, uri);
        if (pidsOfPackage.isEmpty()) {
          // as we don't want ODE to be overworked we wait here
          try {
            Thread.sleep(2000);
          } catch (final InterruptedException e) {
            e.printStackTrace();
          }
        }
        // for safety
        pullCount++;
      }

      pid = calcHighestPid(pidsOfPackage, packageId);

      if (pid == null || pid.isEmpty()) {
        throw new Exception("Couldn't deploy plan " + fileName);
      }

      final ProcessManagementPortType client = getProcessManagementServiceClient();

      // request process info for pid
      final QName processId = QName.valueOf(pid);
      TProcessInfo info = client.getProcessInfo(processId);
      OdeConnector.LOG.debug("Checking packageName for Pid: " + pid);
      OdeConnector.LOG.debug("Package name of PID is: " + info.getDeploymentInfo().get_package());

      // check deployment state until its active
      while (!info.getStatus().equals(TProcessStatus.ACTIVE)) {
        info = client.getProcessInfo(processId);
        Thread.sleep(500);
      }

    } catch (final ManagementFault e) {
      OdeConnector.LOG.error("The Process isn't valid", e);
      return null;
    } catch (final RemoteException e) {
      OdeConnector.LOG.error("RemoteException: Server not available", e);
      return null;
    } catch (final UnknownHostException e) {
      OdeConnector.LOG.error("UnknownHostException: ", e);
      return null;
    } catch (final InterruptedException e) {
      OdeConnector.LOG.error("InterruptedException: ", e);
      return null;
    }
    return pid;
  }

  private String calcHighestPidForStrings(final List<String> pids, final String packageId) {
    final List<QName> qnamedPids = new ArrayList<>();

    pids.forEach(x -> qnamedPids.add(new QName(x)));

    return calcHighestPid(qnamedPids, packageId);
  }

  private String calcHighestPid(final List<QName> pids, final String packageId) {
    OdeConnector.LOG.debug("Starting to calculate highest PID number for package: " + packageId);
    if (pids.isEmpty()) {
      OdeConnector.LOG.warn("PID list is empty");
      return null;
    }
    final List<Integer> idNums = new ArrayList<>();
    String ns = null;
    for (final QName pid : pids) {
      if (ns == null) {
        ns = pid.getNamespaceURI();
        OdeConnector.LOG.debug("Found namespace for PIDs:" + ns);
      }

      final String localPart = pid.getLocalPart();
      OdeConnector.LOG.debug("PID has localPart " + localPart + " defined");

      final String idNum = localPart.substring(localPart.lastIndexOf("-") + 1);

      OdeConnector.LOG.debug("Trying to parse PID Number " + idNum);
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
   * Returns pids for the given package on the referenced ODE
   *
   * @param packageId a String representing the packageId on a ODE
   * @param uri       the uri to the ODE
   * @return a possibly empty List of QName denoting PIDs
   */
  public List<QName> getPIDsForPackageId(final String packageId, final String uri) {
    final List<QName> pids = new ArrayList<>();

    // Update the service endpoint
    setEndpoint(uri);

    OdeConnector.LOG.debug("Fetching pid for package: " + packageId);
    // Create a new deployment client
    final DeploymentPortType client = getDeploymentServiceClient();

    // Retrieve the process ids contained in the given package
    QName[] processIds;
    try {
      processIds = client.listProcesses(packageId);

      // this can happen if ODE has no process deployed
      if (processIds != null) {
        OdeConnector.LOG.debug("Found following PIDs:");
        for (final QName pid : processIds) {
          OdeConnector.LOG.debug("pid: " + pid.toString());

          pids.add(pid);
        }
      }
    } catch (final RemoteException e) {
      OdeConnector.LOG.error("Fetching process ids for package '" + packageId + "' caused an exception.", e);
    }

    return pids;
  }

  /**
   * Undeploys a package from the referenced ODE
   *
   * @param packageName The packageName (on a ODE) of the process deployment unit to undeploy
   * @param uri         the uri of the ODE to undeploy from
   * @return true if undeployment was successful
   */
  public boolean undeploy(final String packageName, final String uri) {
    if (uri == null) {
      return false;
    }

    // Update the service endpoint
    setEndpoint(uri);

    try {
      final DeploymentPortType client = getDeploymentServiceClient();

      final String[] deployedPackages = client.listDeployedPackages();

      final List<String> filteredPackages = new ArrayList<>();

      Arrays.asList(deployedPackages).forEach(x -> {
        if (x.contains(packageName)) {
          filteredPackages.add(x);
        }
      });

      final String pid = calcHighestPidForStrings(filteredPackages, packageName);

      client.undeploy(QName.valueOf(pid));
    } catch (final RemoteException e) {
      OdeConnector.LOG.error("Trying to undeploy package '" + packageName + "' caused an exception.", e);
    }

    return true;
  }

  /**
   * Undeploys a BPEL Process which is given as a zip file
   *
   * @param file the BPEL Process to undeploy as a zip file
   * @param uri  the URI of the ODE the process has to be undeployed
   * @return true iff undeployment was successful
   */
  public boolean undeploy(final File file, final String uri) {
    // check input
    if (file == null) {
      if (file == null) {
        OdeConnector.LOG.error("BPEL process file is null");
      }

      return false;
    }

    OdeConnector.LOG.debug("Path of process file: " + file.getAbsolutePath());
    OdeConnector.LOG.debug("File name: " + file.getName());

    if (!file.isFile()) {
      OdeConnector.LOG.error("Path of file doesn't denote to a file");
      return false;
    }

    if (!file.getName().endsWith(".zip")) {
      OdeConnector.LOG.error("File is not a .zip file");
      return false;
    }

    // setup request
    final String packageName = file.getName().substring(0, file.getName().length() - 4);
    OdeConnector.LOG.debug("Trying to undeploy with packageName: " + packageName);

    return this.undeploy(packageName, uri);
  }

  /**
   * Deploys process archive referenced in FileObject
   *
   * @param file     process archive to deploy
   * @param fileName name of the process archive file
   * @param fileType type of the process archive file
   * @return the packageName of the uploaded package
   * @throws IOException if the given file is not accessible
   */
  private String deployFile(final File file, final String fileName, final String fileType) throws IOException {
    final String fileNameshort = fileName.substring(0, fileName.indexOf("." + fileType));

    final DeploymentPortType client = getDeploymentServiceClient();

    byte[] data = null;
    if (fileType.equals("zip")) {
      data = Files.readAllBytes(Paths.get(file.toURI()));
    } else {
      OdeConnector.LOG.warn("Tried to deploy an non archive file: {}", file.getAbsolutePath());
    }

    final _package zipPackage = new _package();
    final Base64Binary zip = new Base64Binary();
    zip.set_value(data);
    zipPackage.setZip(zip);

    final DeployUnit dUnit = client.deploy(fileNameshort, zipPackage);

    return dUnit.getName();
  }

  /**
   * Returns the deployed packages on the given ODE
   *
   * @param uri the address to the bps
   * @return a list of strings containing the names of the deployed packages
   */
  public List<String> getDeployedPackages(final String uri) {
    final List<String> packageIds = new ArrayList<>();

    // Update the service endpoint
    setEndpoint(uri);

    final DeploymentPortType client = getDeploymentServiceClient();

    String[] packages = null;
    try {
      packages = client.listDeployedPackages();
    } catch (final RemoteException e) {
      OdeConnector.LOG.error("Trying to resolve all deployed packages caused an exception.", e);
    }

    if (packages != null) {
      for (final String packageName : packages) {
        packageIds.add(packageName);
      }
    }

    return packageIds;
  }

  /**
   * Returns a map from partnerLink as string to endpoints as URIs, denoting the service addresses of
   * the inbound partnerLinks', i.e., partnerLinks having a 'myRole' attribute which is implemented by
   * the process.
   *
   * @param pid the PID of the BPEL 2.0 Process, from which the partnerlink endpoints should be
   *            determined
   * @param uri the URI to ODE
   * @return a Map from String to URI denoting partnerLinks and their endpoints
   */
  public Map<String, URI> getEndpointsForPID(final String pid, final String uri) {
    final Map<String, URI> partnerLinkToEndpointURIs = new HashMap<>();
    if (pid == null) {
      OdeConnector.LOG.warn("PID is null! Not possible to find Endpoints on ODE");
      return partnerLinkToEndpointURIs;
    }

    if (uri == null) {
      OdeConnector.LOG.warn("URI for ODE is null");
      return partnerLinkToEndpointURIs;
    }

    OdeConnector.LOG.debug("Trying to get all endpoints");
    OdeConnector.LOG.debug("Using PID: " + pid);
    OdeConnector.LOG.debug("Using URI: " + uri);

    // Update the service endpoint
    setEndpoint(uri);

    final ProcessManagementPortType client = getProcessManagementServiceClient();

    try {
      final TProcessInfo info = client.getProcessInfo(QName.valueOf(pid));

      OdeConnector.LOG.debug("Looking for endpoint for process " + info.getDefinitionInfo().getProcessName());

      if (info.getEndpoints() != null) {
        // process response
        for (final TEndpointReferencesEndpointRef endpointRef : info.getEndpoints()) {

          OdeConnector.LOG.debug("Found partnerlink: " + endpointRef.getPartnerLink());

          // @hahnml: Handle the extraction of EPR data which is provided as WS-Addressing
          // EPR XML document embedded in the endpointRef element as XML extensibility
          // element (xsd:any)
          for (final MessageElement msgElement : endpointRef.get_any()) {
            final String endpointString = resolveServiceAddress(msgElement);
            // Only add valid EPRs
            if (endpointString != null) {
              try {
                final URI serviceURI =
                  new URI(endpointString.replace("localhost", URI.create(uri).getHost()));

                // @hahnml: Depending on the order of the partnerLinks (myRole vs. partnerRole)
                // the myRole or the partnerRole service address will be added to the HashMap,
                // if separate partnerLinks for each role exist.
                // Therefore, we only forward a service address if the
                // service is PROVIDED (partnerLink has a 'myRole' attribute, implemented by a
                // process). This is working since the BpelPlanEnginePlugin only requires the
                // address through which the service implemented by the underlying process model
                // is accessible.
                final String partnerLink = endpointRef.getPartnerLink();

                // Add the partnerLinks' service address to the map if it has a 'myRole' attribute
                if (endpointRef.getMyRole() != null) {
                  partnerLinkToEndpointURIs.put(partnerLink, serviceURI);
                }
              } catch (final URISyntaxException e) {
                OdeConnector.LOG.error("Trying to create a service URI for the endpoint {} caused a URISyntaxException.",
                  endpointString);
              }
            }
          }
        }
      }
    } catch (final ManagementFault e) {
      OdeConnector.LOG.error("Unable to resolve the list of endpoints for process model with pid={}", pid);
    } catch (final RemoteException e) {
      OdeConnector.LOG.error("Trying to resolve a list of endpoints for process model with pid '" + pid
        + "' caused an exception.", e);
    }

    return partnerLinkToEndpointURIs;
  }

  private String resolveServiceAddress(final MessageElement msgElement) {
    final String serviceAddress = null;

    if (msgElement != null) {
      // Check if the root element is a BPEL service reference
      if (msgElement.getNamespaceURI().equals(NS_SERVICE_REF)) {
        final NodeList nodeList = msgElement.getElementsByTagNameNS(NS_WS_ADDRESSING, "EndpointReference");

        if (nodeList != null && nodeList.getLength() > 0) {
          int index = 0;
          while (index < nodeList.getLength()) {
            final Node node = nodeList.item(index);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
              final Element epr = (Element) node;
              final NodeList addList = epr.getElementsByTagNameNS(NS_WS_ADDRESSING, "Address");
              if (addList != null && addList.getLength() > 0) {
                // By default there should be only one address element, therefore we take the
                // first node
                if (addList.item(0).getFirstChild() != null) {
                  return addList.item(0).getFirstChild().getNodeValue();
                }
              }
            }

            index++;
          }
        }
      }
    }

    return serviceAddress;
  }

  public List<String> getAllPIDs(final String uri) {
    final List<String> pidStringList = new ArrayList<>();

    // Update the service endpoint
    setEndpoint(uri);

    final ProcessManagementPortType client = getProcessManagementServiceClient();

    TProcessInfo[] processList;
    try {
      processList = client.listAllProcesses();

      // check for case when there are no process deployed anymore
      if (processList == null) {
        OdeConnector.LOG.debug("Returned list of processes from ODE is null, assuming no process is deployed on ODE");
        return new ArrayList<>();
      }

      // process response
      for (final TProcessInfo pinfo : processList) {
        pidStringList.add(pinfo.getPid());
      }
    } catch (final RemoteException e) {
      OdeConnector.LOG.error("Unable to resolve a list of all processes available at ODE", e);
    }

    return pidStringList;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "openTOSCA Apache ODE Connector v1.0";
  }

  /**
   * Initializes a ProcessManagementService client
   *
   * @return a ProcessManagementService client
   */
  private ProcessManagementPortType getProcessManagementServiceClient() {
    ProcessManagementPortType client = null;
    final String serviceLocation = this.address + "/processes/ProcessManagement";
    try {
      final URL url = new URL(serviceLocation);
      client = new ProcessManagementServiceLocator().getProcessManagementPort(url);
    } catch (final MalformedURLException e) {
      OdeConnector.LOG.error("Cannot resolve a URL from the service location {0}", serviceLocation);
    } catch (final ServiceException e) {
      OdeConnector.LOG.error("Initialization of a process management service client caused an exception.", e);
    }
    return client;
  }

  /**
   * Initializes a DeploymentService client
   *
   * @return a DeploymentService client
   */
  private DeploymentPortType getDeploymentServiceClient() {
    DeploymentPortType client = null;
    final String serviceLocation = this.address + "/processes/DeploymentService";
    try {
      final URL url = new URL(serviceLocation);
      client = new DeploymentServiceLocator().getDeploymentPort(url);
    } catch (final MalformedURLException e) {
      OdeConnector.LOG.error("Cannot resolve a URL from the service location {0}", serviceLocation);
    } catch (final ServiceException e) {
      OdeConnector.LOG.error("Initialization of a deployment service client caused an exception.", e);
    }
    return client;
  }
}
