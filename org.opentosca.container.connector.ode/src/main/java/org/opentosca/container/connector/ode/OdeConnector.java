package org.opentosca.container.connector.ode;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.axis.pmapi.ManagementFault;
import org.apache.axis.pmapi.ProcessManagementStub;
import org.apache.axis2.AxisFault;
import org.apache.ode.deploy.DeploymentServiceStub;
import org.apache.www.ode.deployapi.Package;
import org.apache.www.ode.pmapi.DeployDocument;
import org.apache.www.ode.pmapi.DeployResponseDocument;
import org.apache.www.ode.pmapi.GetProcessInfoDocument;
import org.apache.www.ode.pmapi.GetProcessInfoResponseDocument;
import org.apache.www.ode.pmapi.ListAllProcessesDocument;
import org.apache.www.ode.pmapi.ListDeployedPackagesDocument;
import org.apache.www.ode.pmapi.ListDeployedPackagesResponseDocument;
import org.apache.www.ode.pmapi.ListProcessesDocument;
import org.apache.www.ode.pmapi.ListProcessesResponseDocument;
import org.apache.www.ode.pmapi.UndeployDocument;
import org.apache.www.ode.pmapi.types._2006._08._02.TEndpointReferences;
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

    /**
     * Deploys a WS-BPEL 2.0 process unto the referenced Apache ODE
     *
     * @param process the process to deploy packaged for a Apache ODE
     * @param uri     the URI of the Apache ODE
     * @return a string containing the PID (ProcessId) of the deployed process if everything was successful, else null
     */
    public QName deploy(final File process, final String uri) throws Exception {
        if (uri == null) {
            return null;
        }
        QName pid = null;
        try {

            final String fileName = process.getName();
            final String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
            OdeConnector.LOG.debug("Trying to deploy file: {}", process.getAbsolutePath());
            pid = deployFile(process, fileName, fileType, uri);

            if (pid == null) {
                throw new Exception("Couldn't deploy plan " + fileName);
            }

            final ProcessManagementStub client = getProcessManagementServiceClient(uri);

            // request process info for pid
            GetProcessInfoDocument processInfoDocument = GetProcessInfoDocument.Factory.newInstance();

            GetProcessInfoDocument.GetProcessInfo processInfo = GetProcessInfoDocument.GetProcessInfo.Factory.newInstance();
            processInfo.setPid(pid);
            processInfoDocument.setGetProcessInfo(processInfo);

            GetProcessInfoResponseDocument info = client.getProcessInfo(processInfoDocument);
            OdeConnector.LOG.debug("Checking packageName for Pid: " + pid);
            OdeConnector.LOG.debug("Package name of PID is: " + info.getGetProcessInfoResponse().getProcessInfo().getDeploymentInfo().getPackage());

            // check deployment state until its active
            while (!info.getGetProcessInfoResponse().getProcessInfo().getStatus().equals(TProcessStatus.ACTIVE)) {
                info = client.getProcessInfo(processInfoDocument);
                Thread.sleep(500);
            }
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
            if (pid.getLocalPart().endsWith("-" + highestNumber)) {
                return pid.toString();
            }
        }

        return null;
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

        try {
            final DeploymentServiceStub client = getDeploymentServiceClient(uri);
            ListDeployedPackagesDocument listDeployedPackagesDocument = ListDeployedPackagesDocument.Factory.newInstance();
            ListDeployedPackagesDocument.ListDeployedPackages listDeployedPackages = ListDeployedPackagesDocument.ListDeployedPackages.Factory.newInstance();

            listDeployedPackagesDocument.setListDeployedPackages(listDeployedPackages);
            ListDeployedPackagesResponseDocument listDeployedPackagesResponseDocument = client.listDeployedPackages(listDeployedPackagesDocument);

            final String[] deployedPackages = listDeployedPackagesResponseDocument.getListDeployedPackagesResponse().getDeployedPackages().getNameArray();

            final List<String> filteredPackages = new ArrayList<>();

            Arrays.asList(deployedPackages).forEach(x -> {
                if (x.contains(packageName)) {
                    filteredPackages.add(x);
                }
            });

            final String pid = calcHighestPidForStrings(filteredPackages, packageName);

            if (pid == null || pid.isEmpty()) {
                OdeConnector.LOG.error("PID was not correctly defined, aborting undeploy");
                return false;
            }

            UndeployDocument undeployDocument = UndeployDocument.Factory.newInstance();
            UndeployDocument.Undeploy undeploy = UndeployDocument.Undeploy.Factory.newInstance();
            undeploy.setPackageName(QName.valueOf(pid));
            undeployDocument.setUndeploy(undeploy);
            client.undeploy(undeployDocument);
        } catch (final RemoteException e) {
            OdeConnector.LOG.error("Trying to undeploy package '" + packageName + "' caused an exception.", e);
            return false;
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
            OdeConnector.LOG.error("BPEL process file is null");
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
    private QName deployFile(final File file, final String fileName, final String fileType, final String uri) throws IOException {
        final String fileNameshort = fileName.substring(0, fileName.indexOf("." + fileType));

        final DeploymentServiceStub client = getDeploymentServiceClient(uri);

        byte[] data = null;
        if (fileType.equals("zip")) {
            data = Files.readAllBytes(Paths.get(file.toURI()));
        } else {
            OdeConnector.LOG.warn("Tried to deploy an non archive file: {}", file.getAbsolutePath());
        }

        final Base64Binary zip = Base64Binary.Factory.newInstance();
        zip.setByteArrayValue(data);

        DeployDocument deployDocument = DeployDocument.Factory.newInstance();
        DeployDocument.Deploy deploy = DeployDocument.Deploy.Factory.newInstance();
        Package pckage = Package.Factory.newInstance();
        pckage.setZip(zip);
        deploy.setName(fileNameshort);
        deploy.setPackage(pckage);
        deployDocument.setDeploy(deploy);

        final DeployResponseDocument dUnit = client.deploy(deployDocument);

        return dUnit.getDeployResponse().getResponse().getIdArray(0);
    }

    /**
     * Returns a map from partnerLink as string to endpoints as URIs, denoting the service addresses of the inbound
     * partnerLinks', i.e., partnerLinks having a 'myRole' attribute which is implemented by the process.
     *
     * @param pid the PID of the BPEL 2.0 Process, from which the partnerlink endpoints should be determined
     * @param uri the URI to ODE
     * @return a Map from String to URI denoting partnerLinks and their endpoints
     */
    public Map<String, URI> getEndpointsForPID(final QName pid, final String uri) {
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

        final ProcessManagementStub client = getProcessManagementServiceClient(uri);

        GetProcessInfoDocument getProcessInfoDocument = GetProcessInfoDocument.Factory.newInstance();
        GetProcessInfoDocument.GetProcessInfo getProcessInfo = GetProcessInfoDocument.GetProcessInfo.Factory.newInstance();
        getProcessInfo.setPid(pid);
        getProcessInfoDocument.setGetProcessInfo(getProcessInfo);

        try {
            final TProcessInfo info = client.getProcessInfo(getProcessInfoDocument).getGetProcessInfoResponse().getProcessInfo();

            OdeConnector.LOG.debug("Looking for endpoint for process " + info.getDefinitionInfo().getProcessName());

            if (info.getEndpoints() != null) {
                // process response
                for (final TEndpointReferences.EndpointRef endpointRef : info.getEndpoints().getEndpointRefArray()) {

                    OdeConnector.LOG.debug("Found partnerlink: " + endpointRef.getPartnerLink());

                    // @hahnml: Handle the extraction of EPR data which is provided as WS-Addressing
                    // EPR XML document embedded in the endpointRef element as XML extensibility
                    // element (xsd:any)

                    for (int i = 0; i < endpointRef.getDomNode().getChildNodes().getLength(); i++) {

                        Node msgElement = endpointRef.getDomNode().getChildNodes().item(i);
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

    private String resolveServiceAddress(final Node msgElement) {
        final String serviceAddress = null;

        // Check if the root element is a BPEL service reference
        if (msgElement != null && msgElement.getNamespaceURI().equals(NS_SERVICE_REF) && msgElement.getNodeType() == Node.ELEMENT_NODE) {
            final NodeList nodeList = ((Element) msgElement).getElementsByTagNameNS(NS_WS_ADDRESSING, "EndpointReference");

            if (nodeList != null && nodeList.getLength() > 0) {
                int index = 0;
                while (index < nodeList.getLength()) {
                    final Node node = nodeList.item(index);

                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        final Element epr = (Element) node;
                        final NodeList addList = epr.getElementsByTagNameNS(NS_WS_ADDRESSING, "Address");
                        if (addList != null && addList.getLength() > 0 && addList.item(0).getFirstChild() != null) {
                            // By default there should be only one address element, therefore we take the
                            // first node
                                return addList.item(0).getFirstChild().getNodeValue();
                        }
                    }

                    index++;
                }
            }
        }

        return serviceAddress;
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
    private ProcessManagementStub getProcessManagementServiceClient(String address) {
        ProcessManagementStub client = null;
        final String serviceLocation = address + "/processes/ProcessManagement?wsdl";
        try {
            client = new ProcessManagementStub(serviceLocation);
        } catch (final AxisFault e) {
            OdeConnector.LOG.error("Cannot resolve a URL from the service location {0}", serviceLocation);
        }
        return client;
    }

    /**
     * Initializes a DeploymentService client
     *
     * @return a DeploymentService client
     */
    private DeploymentServiceStub getDeploymentServiceClient(String address) {
        DeploymentServiceStub client = null;
        final String serviceLocation = address + "/processes/DeploymentService?wsdl";
        try {
            client = new DeploymentServiceStub(serviceLocation);
        } catch (final AxisFault e) {
            OdeConnector.LOG.error("Cannot resolve a URL from the service location {0}", serviceLocation);
        }
        return client;
    }
}
