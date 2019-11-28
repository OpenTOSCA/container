package org.opentosca.container.engine.plan.plugin.bpel.util;

import com.ibm.wsdl.extensions.http.HTTPConstants;
import com.ibm.wsdl.extensions.soap.SOAPConstants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.http.HTTPAddress;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.apache.ode.schemas.dd._2007._03.TDeployment;
import org.apache.ode.schemas.dd._2007._03.TInvoke;
import org.apache.ode.schemas.dd._2007._03.TProvide;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.model.endpoint.wsdl.WSDLEndpoint;
import org.opentosca.container.core.service.ICoreEndpointService;
import org.opentosca.container.engine.plan.plugin.bpel.BpelPlanEnginePlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This class implements functionality for updating bindings inside wsdl files
 * which are referenced inside a Apache ODE deloy.xml file.
 * </p>
 * <p>
 * The update is done on a list of files which must include one deploy.xml file
 * (schema: http://svn.apache.org/viewvc/ode/trunk/bpel-schemas/src/main/xsd/)
 * and wsdl files which are referenced inside the deploy.xml.
 * </p>
 * <p>
 * This class uses the ICoreEndpointService to get the up-to-date endpoints from
 * the openTOSCA Core
 * </p>
 *
 * <br>
 * Copyright 2012 IAAS University of Stuttgart <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 * @see org.opentosca.container.core.service.ICoreEndpointService
 * @see org.apache.ode.schemas.dd._2007._03.TDeployment
 */
public class ODEEndpointUpdater {

  private static final Logger LOG = LoggerFactory.getLogger(ODEEndpointUpdater.class);

  private final WSDLFactory factory;

  private CsarId csarId;
  private final String servicesRoot;
  private ICoreEndpointService endpointService;

  // @hahnml: The type of plan engine used (BPS or ODE)
  private String engineType;

  // private static IToscaEngineService toscaEngineService = null;

  /**
   * Contructor *
   *
   * @throws WSDLException if no instance of WSDLFactory was found
   */
  public ODEEndpointUpdater(final String servicesRoot, final String engineType, ICoreEndpointService endpointService) throws WSDLException {
    this.factory = WSDLFactory.newInstance();
    this.servicesRoot = servicesRoot;
    this.engineType = engineType;
    this.endpointService = endpointService;
  }

  /**
   * Changes the endpoints of all WSDL files used by the given WS-BPEL 2.0 Process
   *
   * @param processFiles a list of files containing the complete content of a Apache ODE WS-BPEL
   *                     2.0 zip file
   * @param csarId       the identifier of the CSAR where this process/plan is declared
   * @return true if every WSDL file used by the process was updated (if needed) with endpoints
   * from the openTOSCA Core, else false
   */
  public boolean changeEndpoints(final List<File> processFiles, final CsarId csarId) {
    this.csarId = csarId;

    final Map<QName, List<File>> unchangedFiles = new HashMap<>();
    final File deployXml = getDeployXML(processFiles);

    if (deployXml == null) {
      LOG.error("Given BPEL Plan has no deploy.xml file! Can't change addresses!");
      return false;
    }

    // update addresses inside the process archive that are invoked by the plan
    try {
      final List<QName> portsInDeployXml = getInvokedDeployXMLPorts(deployXml);
      // check with modelrepo if any of the qnames have to be thrown out
      // cause they aren't referenced in the CSAR/TOSCA
      if (!portsInDeployXml.isEmpty()) {
        for (final QName portType : portsInDeployXml) {
          LOG.debug("Proceeding to update address for portType: {}", portType);
        }
        final Map<QName, List<File>> changeMap =
          getWSDLtoChange(portsInDeployXml, getAllWSDLFiles(processFiles));
        unchangedFiles.putAll(this.updateInvokedWSDLAddresses(changeMap));
      } else {
        LOG.debug("No PortTypes to change were found: No portType in plan is referenced in ServiceTemplate");
      }
    } catch (final JAXBException e) {
      LOG.error("Deploy.xml file in process isn't valid", e);
    } catch (final WSDLException e) {
      LOG.error("Couldn't access wsdl files of process", e);
    }

    // update addresses in bpel archive that are provided by the plan
    try {
      final List<QName> portsInDeployXml = getProvidedDeployXMLPorts(deployXml);

      final Map<QName, List<File>> changeMap = getWSDLtoChange(portsInDeployXml, getAllWSDLFiles(processFiles));
      unchangedFiles.putAll(this.updateProvidedWSDLAddresses(changeMap));

    } catch (final JAXBException e) {
      e.printStackTrace();
    } catch (final WSDLException e) {
      e.printStackTrace();
    }

    for (final QName portType : unchangedFiles.keySet()) {
      LOG.warn("Following files weren't changed for PortType {}", portType.toString());
      for (final File file : unchangedFiles.get(portType)) {
        LOG.warn("WSDL file {} which contained portType {} and couldn't be updated",
          file.toPath().toString(), portType.toString());
      }
    }

    // as of recent events, when some address couldn't be changed we return
    // true, even if nothing was changed
    return true;
  }

  /**
   * Returns a file named deploy.xml,if it is in the list of files
   *
   * @param files a list of files
   * @return a file object of a deploy.xml (can be invalid) file if it was found in the given
   * list, else null
   */
  private File getDeployXML(final List<File> files) {
    for (final File file : files) {
      if (file.getName().equals("deploy.xml")) {
        LOG.debug("Found deploy.xml file");
        return file;
      }
    }
    LOG.debug("Didn't find deploy.xml file");
    return null;
  }

  /**
   * Returns a list of QName's which are referenced in the ODE deploy.xml File as invoked
   * service.<br>
   *
   * @param deployXML a file object of a valid deploy.xml File
   * @return a list of QNames which represent the PortTypes used by the BPEL process to invoke
   * operations
   * @throws JAXBException if the JAXB parser couldn't work properly
   */
  private List<QName> getInvokedDeployXMLPorts(final File deployXML) throws JAXBException {
    // http://svn.apache.org/viewvc/ode/trunk/bpel-schemas/src/main/xsd/
    // grabbed that and using jaxb
    final List<QName> qnames = new LinkedList<>();
    final JAXBContext context =
      JAXBContext.newInstance("org.apache.ode.schemas.dd._2007._03", this.getClass().getClassLoader());
    final Unmarshaller unmarshaller = context.createUnmarshaller();
    final TDeployment deploy = unmarshaller.unmarshal(new StreamSource(deployXML), TDeployment.class).getValue();
    for (final org.apache.ode.schemas.dd._2007._03.TDeployment.Process process : deploy.getProcess()) {
      for (final TInvoke invoke : process.getInvoke()) {
        final QName serviceName = invoke.getService().getName();
        // add only qnames which aren't from the plan itself
        if (!serviceName.getNamespaceURI().equals(process.getName().getNamespaceURI())) {
          qnames.add(new QName(serviceName.getNamespaceURI(), invoke.getService().getPort()));
        }
      }
    }
    return qnames;
  }

  /**
   * Returns a List of Services referenced in the provide elements of the given deploy.xml file
   *
   * @param deployXML a Apache ODE deploy.xml file
   * @return a List of QNames denoting services
   * @throws JAXBException is thrown when the given file can't be parsed
   */
  private List<QName> getProvidedDeployXMLPorts(final File deployXML) throws JAXBException {
    final List<QName> ports = new ArrayList<>();
    final JAXBContext context =
      JAXBContext.newInstance("org.apache.ode.schemas.dd._2007._03", this.getClass().getClassLoader());
    final Unmarshaller unmarshaller = context.createUnmarshaller();
    final TDeployment deploy = unmarshaller.unmarshal(new StreamSource(deployXML), TDeployment.class).getValue();
    for (final org.apache.ode.schemas.dd._2007._03.TDeployment.Process process : deploy.getProcess()) {
      for (final TProvide provide : process.getProvide()) {
        final QName serviceName = provide.getService().getName();
        // add only qnames which aren't from the plan itself

        // @hahnml: The plan generator assigns to provided services addresses like
        // http://[IP]:8080
        // which is fine for WSO2 BPS but won't work for Apache ODE. ODE rejects the
        // deployment if the service addresses do not follow the following schema:
        // http://[IP]:[Port]/ode/processes/[ServiceName].
        // Added an engine type check so that for ODE, also the provided process service
        // ports are added.
        if (this.engineType.equals(BpelPlanEnginePlugin.BPS_ENGINE)) {
          if (!serviceName.getNamespaceURI().equals(process.getName().getNamespaceURI())) {
            ports.add(new QName(serviceName.getNamespaceURI(), provide.getService().getPort()));
          }
        } else {
          ports.add(new QName(serviceName.getNamespaceURI(), provide.getService().getPort()));
        }
      }
    }
    return ports;
  }

  /**
   * Returns all WSDL files of the given List
   *
   * @param files a list of files
   * @return a list of WSDL files if there are any
   */
  private List<File> getAllWSDLFiles(final List<File> files) {
    final List<File> tempFiles = new LinkedList<>();
    for (final File file : files) {
      if (file.isDirectory()) {
        // recursive call to allow searching in directories
        LOG.debug("Found directory inside bpel archive: {}", file.getAbsolutePath());
        final File[] subFiles = file.listFiles();
        // this is just here to transform the array to a list
        final List<File> temp = new LinkedList<>();
        for (final File subFile : subFiles) {
          temp.add(subFile);
        }
        tempFiles.addAll(getAllWSDLFiles(temp));
      }
      final int pos = file.getName().lastIndexOf('.');
      if (pos > 0 && pos < file.getName().length() - 1) {
        if (file.getName().substring(pos + 1).equals("wsdl")) {
          LOG.debug("Adding .wsdl file {} ", file.getName());
          tempFiles.add(file);
        }
      }
    }
    return tempFiles;
  }

  /**
   * Returns a map with QNames as keys and list of files as values, where the QNames are taken out
   * of the given list of portTypes and the files from the other given List
   *
   * @param ports     a list of portType QName's
   * @param wsdlFiles a list of wsdl Files
   * @return a Map<QName, List<File>> containing information which porttype is in which wsdl file
   * @throws WSDLException
   */
  private Map<QName, List<File>> getWSDLtoChange(final List<QName> ports,
                                                 final List<File> wsdlFiles) throws WSDLException {
    final Map<QName, List<File>> portTypeToFileMap = new HashMap<>();
    // we check if we have any porttypes which isn't in the endpoint db
    for (final QName port : ports) {
      LOG.debug("Searching through wsdls for porttype: {}", port.toString());
      final List<File> filesContainingPortType = new LinkedList<>();
      QName portType = null;
      for (final File wsdlFile : wsdlFiles) {
        LOG.debug("Checking if wsdl file {} contains portType {}",
          wsdlFile.getAbsolutePath(), port.toString());
        final Definition wsdlDef = getWsdlReader().readWSDL(wsdlFile.getAbsolutePath());
        // check if port is in wsdl file
        if (!checkIfPortIsInWsdlDef(port, wsdlDef)) {
          continue;
        } else {
          portType = getPortTypeFromPort(port, wsdlDef);
        }

        for (final Object obj : wsdlDef.getPortTypes().values()) {
          final PortType portTypeInWsdl = (PortType) obj;
          // TODO when axis1 service port and porttype have the same name,
          //  still don't know what the problem will be if it happens,
          //  cause i check only portTypes here
          //  please send me an email with the problem
          if (portTypeInWsdl.getQName().toString().equals(portType.toString())) {
            // this wsdl file contains the porttype
            filesContainingPortType.add(wsdlFile);
          }
        }
      }
      if (!filesContainingPortType.isEmpty() && portType != null) {
        // found wsdl files with this porttype
        portTypeToFileMap.put(portType, filesContainingPortType);
      }
    }
    return portTypeToFileMap;
  }

  private WSDLReader getWsdlReader() {
    WSDLReader reader = factory.newWSDLReader();
    reader.setFeature("javax.wsdl.verbose", false);
    return reader;
  }

  /**
   * Returns a PortType as QName if the given port is defined inside the given WSDL Definition
   *
   * @param port    the Port to check with as QName
   * @param wsdlDef the WSDL Definition to look trough
   * @return a QName representing the PortType implemented by the given Port if it was found
   * inside the WSDL Definition, else null
   */
  private QName getPortTypeFromPort(final QName port, final Definition wsdlDef) {
    for (final Object serviceObj : wsdlDef.getServices().values()) {
      final Service service = (Service) serviceObj;
      for (final Object portObj : service.getPorts().values()) {
        final Port wsdlPort = (Port) portObj;
        if (wsdlPort.getName().equals(port.getLocalPart())
          && wsdlDef.getTargetNamespace().equals(port.getNamespaceURI())) {
          return wsdlPort.getBinding().getPortType().getQName();
        }
      }
    }
    return null;
  }

  /**
   * Checks whether the given Port is defined inside the given WSDL Definition
   *
   * @param port    the Port to check with as QName
   * @param wsdlDef the WSDL Definition to check in
   * @return true if the Port is found inside the given WSDL Definition, else false
   */
  private boolean checkIfPortIsInWsdlDef(final QName port, final Definition wsdlDef) {
    for (final Object serviceObj : wsdlDef.getServices().values()) {
      final Service service = (Service) serviceObj;
      for (final Object portObj : service.getPorts().values()) {
        final Port wsdlPort = (Port) portObj;
        final String namespace = wsdlDef.getTargetNamespace();
        final String name = wsdlPort.getName();
        LOG.debug("Checking if port {} matches port with name {} and namespace {} ",
          port.toString(), name, namespace);
        if (name.equals(port.getLocalPart()) && namespace.equals(port.getNamespaceURI())) {
          return true;
        }

      }
    }
    return false;
  }

  private Map<QName, List<File>> updateProvidedWSDLAddresses(final Map<QName, List<File>> changeMap) throws WSDLException {
    final Map<QName, List<File>> notChanged = new HashMap<>();
    for (final QName portType : changeMap.keySet()) {
      final List<File> notUpdateWSDLs = new ArrayList<>();

      for (final File wsdlFile : changeMap.get(portType)) {
        if (!this.updateProvidedWSDLAddresses(portType, wsdlFile)) {
          notUpdateWSDLs.add(wsdlFile);
        }
      }
      if (!notUpdateWSDLs.isEmpty()) {
        notChanged.put(portType, notUpdateWSDLs);
      }

    }
    return notChanged;
  }

  /**
   * Updates the addresses in the given WSDL files by using endpoints added in the endpoint db
   *
   * @param map a map containing <QName,List<File>> pairs. A QName here represents a portType that
   *            is inside the files
   * @return returns a map <QName,List<File>> containing all the files which weren't changed
   * @throws WSDLException
   */
  private Map<QName, List<File>> updateInvokedWSDLAddresses(final Map<QName, List<File>> map) throws WSDLException {
    final Map<QName, List<File>> notChanged = new HashMap<>();
    for (final QName portType : map.keySet()) {
      final List<File> notUpdatedWSDLs = new LinkedList<>();
      // update wsdl files associated with the given porttype
      for (final File wsdlFile : map.get(portType)) {
        if (!this.updateInvokedWSDLAddresses(portType, wsdlFile)) {
          LOG.error("Unable to update '{}' for porttype '{}'.", wsdlFile.toString(),
            portType.toString());
          notUpdatedWSDLs.add(wsdlFile);
        }
      }
      if (!notUpdatedWSDLs.isEmpty()) {
        // if empty, nothing was changed
        LOG.debug("Couldn't update address for porttype: {}", portType.toString());
        notChanged.put(portType, notUpdatedWSDLs);
      }
    }
    return notChanged;
  }

  private boolean updateProvidedWSDLAddresses(final QName portType, final File wsdlFile) throws WSDLException {
    boolean changed = false;
    final Definition wsdlDef = getWsdlReader().readWSDL(wsdlFile.getAbsolutePath());
    for (final Object o : wsdlDef.getAllServices().values()) {
      final Service service = (Service) o;
      for (final Object obj : service.getPorts().values()) {
        final Port port = (Port) obj;
        if (port.getBinding().getPortType().getQName().equals(portType)) {
          if (changePortAddressWithBpelEngineEndpoints(service, port)) {
            changed = true;
          }
        }
      }
    }
    try {
      // if we changed something, rewrite the the wsdl
      if (changed) {
        this.factory.newWSDLWriter().writeWSDL(wsdlDef, new FileOutputStream(wsdlFile));
      }
    } catch (final FileNotFoundException e) {
      LOG.debug("Couldn't locate wsdl file", e);
      changed = false;
    }
    return changed;
  }

  /**
   * Updates the addresses inside the given WSDL file by using endpoints inside the endpoint db
   *
   * @param portType a QName which represents a PortType
   * @param wsdl     a File which is from type .wsdl
   * @throws WSDLException if the WSDL parser couldn't parse
   */
  private boolean updateInvokedWSDLAddresses(final QName portType, final File wsdl) throws WSDLException {
    boolean changed = false;
    LOG.debug("Trying to change WSDL file {} ", wsdl.getName());
    final Definition wsdlDef = getWsdlReader().readWSDL(wsdl.getAbsolutePath());
    for (final Object o : wsdlDef.getAllServices().values()) {
      // get the services
      final Service service = (Service) o;
      for (final Object obj : service.getPorts().values()) {
        // get the ports of the service
        final Port port = (Port) obj;
        if (port.getBinding().getPortType().getQName().equals(portType)) {
          // get binding and its porttype
          // get the extensible elements out of wsdl and check them
          // with endpointservice

          LOG.debug("Found matching porttype for WSDL file {} ", wsdl.getName());
          if (changePortAddressWithEndpointDB(port)) {
            // changing -> success
            changed = true;
          }
        }
      }
    }
    try {
      // if we changed something, rewrite the the wsdl
      if (changed) {
        this.factory.newWSDLWriter().writeWSDL(wsdlDef, new FileOutputStream(wsdl));
      }
    } catch (final FileNotFoundException e) {
      LOG.debug("Couldn't locate wsdl file", e);
      changed = false;
    }
    return changed;
  }

  private boolean changePortAddressWithBpelEngineEndpoints(final Service service, final Port port) {
    boolean changed = false;

    for (final Object obj : port.getExtensibilityElements()) {
      final ExtensibilityElement element = (ExtensibilityElement) obj;
      for (final WSDLEndpoint endpoint : getWSDLEndpointForBpelEngineCallback(service, port)) {
        if (changeAddress(element, endpoint)) {
          changed = true;
        }
      }
    }

    return changed;
  }

  /**
   * Changes address in the given port if endpoint in the endpoint service is available
   *
   * @param port the Port to update
   * @return true if change was made, else false
   */
  private boolean changePortAddressWithEndpointDB(final Port port) {
    boolean changed = false;

    LOG.debug("Trying to match address element with available endpoints for port {} ",
      port.getName());
    for (final Object obj : port.getExtensibilityElements()) {
      // in the wsdl spec they use the extensibility mechanism
      final ExtensibilityElement element = (ExtensibilityElement) obj;
      for (final WSDLEndpoint endpoint : getWSDLEndpointsFromEndpointDB(port)) {
        LOG.debug("Changing address for endpoint: {}", endpoint.getURI());
        if (changeAddress(element, endpoint)) {
          changed = true;
        }

      }
    }
    return changed;
  }

  private List<WSDLEndpoint> getWSDLEndpointForBpelEngineCallback(final Service service, final Port port) {
    final List<WSDLEndpoint> endpoints = new ArrayList<>();

    /*
     * The WSO2 BPS and Apache ODE are creating addresses by using the Service Name
     * OpenTOSCA_On_VSphere.csarInvokerService
     * location="http://10.0.2.15:9763/services/OpenTOSCA_On_VSphere. csarInvokerService/
     *
     * The only difference is the servicesRoot which is '/services/..' for BPS and
     * '/ode/processes/..' for ODE.
     */
    String callbackEndpoint = "";
    if (this.servicesRoot.endsWith("/")) {
      callbackEndpoint += this.servicesRoot + service.getQName().getLocalPart();
    } else {
      callbackEndpoint += this.servicesRoot + "/" + service.getQName().getLocalPart();
    }

    try {
      final String localContainer = Settings.OPENTOSCA_CONTAINER_HOSTNAME;
      endpoints.add(new WSDLEndpoint(new URI(callbackEndpoint), port.getBinding().getPortType().getQName(),
        localContainer, localContainer, null, null, null, null, null, new HashMap<>()));
    } catch (final URISyntaxException e) {
      e.printStackTrace();
    }

    return endpoints;
  }

  /**
   * Returns a list of WSDLEndpoints for the specific Port from the endpoint DB
   *
   * @param port the Port to check for
   * @return a list containing all WSDLEndpoints that matches the portTypes of the given Port
   */
  private List<WSDLEndpoint> getWSDLEndpointsFromEndpointDB(final Port port) {
    final List<WSDLEndpoint> endpoints = new LinkedList<>();
    if (endpointService != null) {
      LOG.debug("Fetching Endpoints for PortType {} ",
        port.getBinding().getPortType().getQName().toString());
      final List<WSDLEndpoint> temp = endpointService.getWSDLEndpoints();
      for (final WSDLEndpoint endpoint : temp) {
        if (endpoint.getPortType().equals(port.getBinding().getPortType().getQName())
          && endpoint.getManagingContainer().equals(Settings.OPENTOSCA_CONTAINER_HOSTNAME)) {
          LOG.debug("Found endpoint: {}", endpoint.getURI().toString());
          endpoints.add(endpoint);
        }
      }
    } else {
      LOG.debug("Endpoint service not available");
    }
    LOG.debug("{} endpoints found for PortType {}", endpoints.size(), port.getBinding().getPortType().getQName().toString());
    return endpoints;
  }

  /**
   * Changes the address in the given ExtensibilityElement to address given in the given
   * WSDLEndpoint
   *
   * @param element  the ExtensibilityElement to change
   * @param endpoint the WSDLEndpoint containing the address
   * @return true if changing was successful, this means the ExtensibilityElement had the type
   * {@link com.ibm.wsdl.extensions.soap.SOAPConstants.Q_ELEM_SOAP_ADDRESS} or
   * {@link com.ibm.wsdl.extensions.http.HTTPConstants.Q_ELEM_HTTP_ADDRESS} , else false
   */
  private boolean changeAddress(final ExtensibilityElement element, final WSDLEndpoint endpoint) {
    // TODO check if we could generalize this, we did once, but after
    //  looking at it again it seems not right enough
    if (element.getElementType().equals(SOAPConstants.Q_ELEM_SOAP_ADDRESS)) {
      LOG.debug("Changing the SOAP-Address Element inside for porttype {} ",
        endpoint.getPortType().toString());
      final SOAPAddress address = (SOAPAddress) element;
      address.setLocationURI(endpoint.getURI().toString());
    } else if (element.getElementType().equals(HTTPConstants.Q_ELEM_HTTP_ADDRESS)) {
      LOG.debug("Changing the HTTP-Address Element inside for porttype {} ",
        endpoint.getPortType().toString());
      final HTTPAddress address = (HTTPAddress) element;
      address.setLocationURI(endpoint.getURI().toString());
    } else {
      LOG.debug("Address element inside WSDL isn't supported");
      return false;
    }
    return true;
  }

  /**
   * Returns PortType of the bpel process composed of the given files list
   *
   * @param planContents List of Files which make up the BPEL Process
   * @return QName which should be exactly the PortType of the given BPEL Process
   */
  public QName getPortType(final List<File> planContents) {
    try {
      final File deployXML = getDeployXML(planContents);
      final JAXBContext context =
        JAXBContext.newInstance("org.apache.ode.schemas.dd._2007._03", this.getClass().getClassLoader());
      final Unmarshaller unmarshaller = context.createUnmarshaller();
      final TDeployment deploy =
        unmarshaller.unmarshal(new StreamSource(deployXML), TDeployment.class).getValue();
      for (final TDeployment.Process process : deploy.getProcess()) {
        return process.getName();
      }
    } catch (final JAXBException e) {
      e.printStackTrace();
    }
    return null;
  }
}
