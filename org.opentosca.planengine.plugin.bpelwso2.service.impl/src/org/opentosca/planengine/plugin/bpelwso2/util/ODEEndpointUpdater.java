package org.opentosca.planengine.plugin.bpelwso2.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.apache.ode.schemas.dd._2007._03.TDeployment;
import org.apache.ode.schemas.dd._2007._03.TInvoke;
import org.opentosca.core.endpoint.service.ICoreEndpointService;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.core.model.endpoint.wsdl.WSDLEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.wsdl.extensions.http.HTTPConstants;
import com.ibm.wsdl.extensions.soap.SOAPConstants;

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
 * @see org.opentosca.core.endpoint.service.ICoreEndpointService
 * @see org.apache.ode.schemas.dd._2007._03.TDeployment
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 * 
 */
public class ODEEndpointUpdater {
	
	final private static Logger LOG = LoggerFactory.getLogger(ODEEndpointUpdater.class);
	private final WSDLFactory factory;
	private CSARID csarId;
	// the services are static (bind/unbind too), if not instantiation of
	// this class will not have the services
	private static ICoreEndpointService endpointService;
	private static ICoreEndpointService oldEndpointService;
	
	
	// private static IToscaEngineService toscaEngineService = null;
	
	/**
	 * Contructor
	 * 
	 * @throws WSDLException if no instance of WSDLFactory was found
	 */
	public ODEEndpointUpdater() throws WSDLException {
		this.factory = WSDLFactory.newInstance();
	}
	
	/**
	 * Changes the endpoints of all WSDL files used by the given WS-BPEL 2.0
	 * Process
	 * 
	 * @param processFiles a list of files containing the complete content of a
	 *            Apache ODE WS-BPEL 2.0 zip file
	 * @param csarId the identifier of the CSAR where this process/plan is
	 *            declared
	 * @return true if every WSDL file used by the process was updated (if
	 *         needed) with endpoints from the openTOSCA Core, else false
	 */
	public boolean changeEndpoints(List<File> processFiles, CSARID csarId) {
		this.csarId = csarId;
		Map<QName, List<File>> unchangedFiles = null;
		
		try {
			List<QName> portsInDeployXml = this.getDeployXMLPorts(this.getDeployXMl(processFiles));
			// check with modelrepo if any of the qnames have to be thrown out
			// cause they aren't referenced in the CSAR/TOSCA
			
			// quick fix,until we know how to "add" porttypes to tosca again
			// if (ODEEndpointUpdater.modelRepoService != null) {
			// List<QName> csarPortTypeReferences =
			// ODEEndpointUpdater.toscaEngineService.getToscaReferenceMapper().getAllWSDLPortTypeReferencesInsideTHOR(csarId);
			// List<QName> toRemove = new LinkedList<QName>();
			// for (QName portType : portsInDeployXml) {
			// if (!csarPortTypeReferences.contains(portType)) {
			// toRemove.add(portType);
			// }
			// }
			// portsInDeployXml.removeAll(toRemove);
			// } else {
			// ODEEndpointUpdater.LOG.warn("No ModelRepositoryService is bound, may corrupt private wsdl files");
			// }
			
			if (!portsInDeployXml.isEmpty()) {
				for (QName portType : portsInDeployXml) {
					ODEEndpointUpdater.LOG.debug("Proceeding to update address for portType: {}", portType);
				}
			} else {
				ODEEndpointUpdater.LOG.debug("No PortTypes to change were found: No portType in plan is referenced in ServiceTemplate");
				return true;
			}
			Map<QName, List<File>> changeMap = this.getWSDLtoChange(portsInDeployXml, this.getAllWSDLFiles(processFiles));
			unchangedFiles = this.updateWSDLAddresses(changeMap);
		} catch (JAXBException e) {
			ODEEndpointUpdater.LOG.error("Deploy.xml file in process isn't valid", e);
		} catch (WSDLException e) {
			ODEEndpointUpdater.LOG.error("Couldn't access wsdl files of process", e);
		}
		
		if (unchangedFiles == null) {
			ODEEndpointUpdater.LOG.warn("No changes were made to the given WSDL files!");
		} else {
			for (QName portType : unchangedFiles.keySet()) {
				ODEEndpointUpdater.LOG.warn("Following files weren't changed for PortType {}", portType.toString());
				for (File file : unchangedFiles.get(portType)) {
					ODEEndpointUpdater.LOG.warn("WSDL file {} which contained portType {} and could'nt be updated", file.toPath().toString(), portType.toString());
				}
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
	 * @return a file object of a deploy.xml (can be invalid) file if it was
	 *         found in the given list, else null
	 */
	private File getDeployXMl(List<File> files) {
		for (File file : files) {
			if (file.getName().equals("deploy.xml")) {
				ODEEndpointUpdater.LOG.debug("Found deploy.xml file");
				return file;
			}
		}
		ODEEndpointUpdater.LOG.debug("Didn't find deploy.xml file");
		return null;
	}
	
	/**
	 * Returns a list of QName's which are referenced in the ODE deploy.xml
	 * File.<br>
	 * 
	 * @param deployXML a file object of a valid deploy.xml File
	 * @return a list of QNames which represent the PortTypes used by the BPEL
	 *         process to invoke operations
	 * @throws JAXBException if the JAXB parser couldn't work properly
	 */
	private List<QName> getDeployXMLPorts(File deployXML) throws JAXBException {
		// http://svn.apache.org/viewvc/ode/trunk/bpel-schemas/src/main/xsd/
		// grabbed that and using jaxb
		List<QName> qnames = new LinkedList<QName>();
		JAXBContext context = JAXBContext.newInstance("org.apache.ode.schemas.dd._2007._03", this.getClass().getClassLoader());
		Unmarshaller unmarshaller = context.createUnmarshaller();
		TDeployment deploy = unmarshaller.unmarshal(new StreamSource(deployXML), TDeployment.class).getValue();
		for (org.apache.ode.schemas.dd._2007._03.TDeployment.Process process : deploy.getProcess()) {
			for (TInvoke invoke : process.getInvoke()) {
				QName serviceName = invoke.getService().getName();
				// add only qnames which aren't from the plan itself
				if (!serviceName.getNamespaceURI().equals(process.getName().getNamespaceURI())) {
					qnames.add(new QName(serviceName.getNamespaceURI(), invoke.getService().getPort()));
				}
			}
		}
		return qnames;
	}
	
	/**
	 * Returns all WSDL files of the given List
	 * 
	 * @param files a list of files
	 * @return a list of WSDL files if there are any
	 */
	private List<File> getAllWSDLFiles(List<File> files) {
		List<File> tempFiles = new LinkedList<File>();
		for (File file : files) {
			if (file.isDirectory()) {
				// recursive call to allow searching in directories
				ODEEndpointUpdater.LOG.debug("Found directory inside bpel archive: {}", file.getAbsolutePath());
				File[] subFiles = file.listFiles();
				// this is just here to transform the array to a list
				List<File> temp = new LinkedList<File>();
				for (int i = 0; i < subFiles.length; i++) {
					temp.add(subFiles[i]);
				}
				tempFiles.addAll(this.getAllWSDLFiles(temp));
			}
			int pos = file.getName().lastIndexOf('.');
			if ((pos > 0) && (pos < (file.getName().length() - 1))) {
				if (file.getName().substring(pos + 1).equals("wsdl")) {
					ODEEndpointUpdater.LOG.debug("Adding .wsdl file {} ", file.getName());
					tempFiles.add(file);
				}
			}
		}
		return tempFiles;
	}
	
	/**
	 * Returns a map with QNames as keys and list of files as values, where the
	 * QNames are taken out of the given list of portTypes and the files from
	 * the other given List
	 * 
	 * @param ports a list of portType QName's
	 * @param wsdlFiles a list of wsdl Files
	 * @return a Map<QName, List<File>> containing information which porttype is
	 *         in which wsdl file
	 * @throws WSDLException
	 */
	private Map<QName, List<File>> getWSDLtoChange(List<QName> ports, List<File> wsdlFiles) throws WSDLException {
		Map<QName, List<File>> portTypeToFileMap = new HashMap<QName, List<File>>();
		// we check if we have any porttypes which isn't in the endpoint db
		for (QName port : ports) {
			ODEEndpointUpdater.LOG.debug("Searching through wsdls for porttype: {}", port.toString());
			List<File> filesContainingPortType = new LinkedList<File>();
			QName portType = null;
			for (File wsdlFile : wsdlFiles) {
				ODEEndpointUpdater.LOG.debug("Checking if wsdl file {} contains portType {}", wsdlFile.getAbsolutePath(), port.toString());
				Definition wsdlDef = this.factory.newWSDLReader().readWSDL(wsdlFile.getAbsolutePath());
				// check if port is in wsdl file
				if (!this.checkIfPortIsInWsdlDef(port, wsdlDef)) {
					continue;
				} else {
					portType = this.getPortTypeFromPort(port, wsdlDef);
				}
				
				for (Object obj : wsdlDef.getPortTypes().values()) {
					PortType portTypeInWsdl = (PortType) obj;
					// TODO when axis1 service port and porttype have the same
					// name,
					// still don't know what the problem will be if it happens,
					// cause i check only portTypes here
					// please send me an email with the problem
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
	
	/**
	 * Returns a PortType as QName if the given port is defined inside the given
	 * WSDL Definition
	 * 
	 * @param port the Port to check with as QName
	 * @param wsdlDef the WSDL Definition to look trough
	 * @return a QName representing the PortType implemented by the given Port
	 *         if it was found inside the WSDL Definition, else null
	 */
	private QName getPortTypeFromPort(QName port, Definition wsdlDef) {
		for (Object serviceObj : wsdlDef.getServices().values()) {
			Service service = (Service) serviceObj;
			for (Object portObj : service.getPorts().values()) {
				Port wsdlPort = (Port) portObj;
				if (wsdlPort.getName().equals(port.getLocalPart()) && wsdlDef.getTargetNamespace().equals(port.getNamespaceURI())) {
					return wsdlPort.getBinding().getPortType().getQName();
				}
			}
		}
		return null;
	}
	
	/**
	 * Checks whether the given Port is defined inside the given WSDL Definition
	 * 
	 * @param port the Port to check with as QName
	 * @param wsdlDef the WSDL Definition to check in
	 * @return true if the Port is found inside the given WSDL Definition, else
	 *         false
	 */
	private boolean checkIfPortIsInWsdlDef(QName port, Definition wsdlDef) {
		for (Object serviceObj : wsdlDef.getServices().values()) {
			Service service = (Service) serviceObj;
			for (Object portObj : service.getPorts().values()) {
				Port wsdlPort = (Port) portObj;
				String namespace = wsdlDef.getTargetNamespace();
				String name = wsdlPort.getName();
				ODEEndpointUpdater.LOG.debug("Checking if port {} matches port with name {} and namespace {} ", port.toString(), name, namespace);
				if (name.equals(port.getLocalPart()) && namespace.equals(port.getNamespaceURI())) {
					return true;
				}
				
			}
		}
		return false;
	}
	
	/**
	 * Updates the addresses in the given WSDL files
	 * 
	 * @param map a map containing <QName,List<File>> pairs. A QName here
	 *            represents a portType that is inside the files
	 * @return returns a map <QName,List<File>> containing all the files which
	 *         weren't changed
	 * @throws WSDLException
	 */
	private Map<QName, List<File>> updateWSDLAddresses(Map<QName, List<File>> map) throws WSDLException {
		Map<QName, List<File>> notChanged = new HashMap<QName, List<File>>();
		for (QName portType : map.keySet()) {
			List<File> notUpdatedWSDLs = new LinkedList<File>();
			// update wsdl files associated with the given porttype
			for (File wsdlFile : map.get(portType)) {
				if (!this.updateWSDLAddresses(portType, wsdlFile)) {
					ODEEndpointUpdater.LOG.error("Unable to update '{}' for porttype '{}'.", wsdlFile.toString(), portType.toString());
					notUpdatedWSDLs.add(wsdlFile);
				}
			}
			if (!notUpdatedWSDLs.isEmpty()) {
				// if empty, nothing was changed
				ODEEndpointUpdater.LOG.debug("Couldn't update address for porttype: {}", portType.toString());
				notChanged.put(portType, notUpdatedWSDLs);
			}
		}
		return notChanged;
	}
	
	/**
	 * Updates the addresses inside the given WSDL file
	 * 
	 * @param portType a QName which represents a PortType
	 * @param wsdl a File which is from type .wsdl
	 * @throws WSDLException if the WSDL parser couldn't parse
	 */
	private boolean updateWSDLAddresses(QName portType, File wsdl) throws WSDLException {
		boolean changed = false;
		ODEEndpointUpdater.LOG.debug("Trying to change WSDL file {} ", wsdl.getName());
		Definition wsdlDef = this.factory.newWSDLReader().readWSDL(wsdl.getAbsolutePath());
		for (Object o : wsdlDef.getAllServices().values()) {
			// get the services
			Service service = (Service) o;
			for (Object obj : service.getPorts().values()) {
				// get the ports of the service
				Port port = (Port) obj;
				if (port.getBinding().getPortType().getQName().equals(portType)) {
					// get binding and its porttype
					// get the extensible elements out of wsdl and check them
					// with endpointservice
					
					ODEEndpointUpdater.LOG.debug("Found matching porttype for WSDL file {} ", wsdl.getName());
					if (this.changePortAddress(port)) {
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
		} catch (FileNotFoundException e) {
			ODEEndpointUpdater.LOG.debug("Couldn't locate wsdl file", e);
			changed = false;
		}
		return changed;
	}
	
	/**
	 * Changes address in the given port if endpoint in the endpoint service is
	 * available
	 * 
	 * @param port the Port to update
	 * @return true if change was made, else false
	 */
	private boolean changePortAddress(Port port) {
		boolean changed = false;
		
		ODEEndpointUpdater.LOG.debug("Trying to match address element with available endpoints for port {} ", port.getName());
		for (Object obj : port.getExtensibilityElements()) {
			// in the wsdl spec they use the extensibility mechanism
			ExtensibilityElement element = (ExtensibilityElement) obj;
			for (WSDLEndpoint endpoint : this.getWSDLEndpoints(port)) {
				// this is a quickfix until service bus or another solution is
				// available to change addresses without this hack
				
				// ODEEndpointUpdater.LOG.debug(
				// "Trying to match adresstype {} and elementtype {}",
				// endpoint.getAddressType().toString(), element
				// .getElementType().toString());
				// in short: check the QNames of the address elements
				// if
				// (element.getElementType().equals(endpoint.getAddressType()))
				// {
				// ODEEndpointUpdater.LOG
				// .debug("Found matching address element and endpoint for port {} ",
				// port.getName());
				if (this.changeAddress(element, endpoint)) {
					changed = true;
				}
				// }
			}
		}
		return changed;
	}
	
	/**
	 * Returns a list of WSDLEndpoints for the specific Port
	 * 
	 * @param port the Port to check for
	 * @return a list containing all WSDLEndpoints that matches the portTypes of
	 *         the given Port
	 */
	private List<WSDLEndpoint> getWSDLEndpoints(Port port) {
		List<WSDLEndpoint> endpoints = new LinkedList<WSDLEndpoint>();
		if (ODEEndpointUpdater.endpointService != null) {
			ODEEndpointUpdater.LOG.debug("Fetching Endpoints for PortType {} ", port.getBinding().getPortType().getQName().toString());
			List<WSDLEndpoint> temp = ODEEndpointUpdater.endpointService.getWSDLEndpoints(port.getBinding().getPortType().getQName(), this.csarId);
			for (WSDLEndpoint endpoint : temp) {
				ODEEndpointUpdater.LOG.debug("Found endpoint: {}", endpoint.getURI().toString());
				endpoints.add(endpoint);
			}
		} else {
			ODEEndpointUpdater.LOG.debug("Endpoint service not available");
		}
		return endpoints;
	}
	
	/**
	 * Changes the address in the given ExtensibilityElement to address given in
	 * the given WSDLEndpoint
	 * 
	 * @param element the ExtensibilityElement to change
	 * @param endpoint the WSDLEndpoint containing the address
	 * @return true if changing was successful, this means the
	 *         ExtensibilityElement had the type
	 *         {@link com.ibm.wsdl.extensions.soap.SOAPConstants.Q_ELEM_SOAP_ADDRESS}
	 *         or
	 *         {@link com.ibm.wsdl.extensions.http.HTTPConstants.Q_ELEM_HTTP_ADDRESS}
	 *         , else false
	 */
	private boolean changeAddress(ExtensibilityElement element, WSDLEndpoint endpoint) {
		// TODO check if we could generalize this, we did once, but after
		// looking at it again it seems not right enough
		if (element.getElementType().equals(SOAPConstants.Q_ELEM_SOAP_ADDRESS)) {
			ODEEndpointUpdater.LOG.debug("Changing the SOAP-Address Element inside for porttype {} ", endpoint.getPortType().toString());
			SOAPAddress address = (SOAPAddress) element;
			address.setLocationURI(endpoint.getURI().toString());
		} else if (element.getElementType().equals(HTTPConstants.Q_ELEM_HTTP_ADDRESS)) {
			ODEEndpointUpdater.LOG.debug("Changing the HTTP-Address Element inside for porttype {} ", endpoint.getPortType().toString());
			HTTPAddress address = (HTTPAddress) element;
			address.setLocationURI(endpoint.getURI().toString());
		} else {
			ODEEndpointUpdater.LOG.debug("Address element inside WSDL isn't supported");
			return false;
		}
		return true;
	}
	
	/**
	 * Bind method for EndpointService
	 * 
	 * @param endpointService the EndpointService to bind
	 */
	protected static void bindEndpointService(ICoreEndpointService endpointService) {
		if (endpointService != null) {
			ODEEndpointUpdater.LOG.debug("Registering EndpointService {}", endpointService.toString());
			if (ODEEndpointUpdater.endpointService == null) {
				ODEEndpointUpdater.endpointService = endpointService;
			} else {
				ODEEndpointUpdater.oldEndpointService = endpointService;
				ODEEndpointUpdater.endpointService = endpointService;
			}
			ODEEndpointUpdater.LOG.debug("Registered EndpointService {}", endpointService.toString());
		}
	}
	
	/**
	 * Unbind method for EndpointService
	 * 
	 * @param endpointService the EndpointService to unbind
	 */
	protected static void unbindEndpointService(ICoreEndpointService endpointService) {
		ODEEndpointUpdater.LOG.debug("Unregistering EndpointService {}", endpointService.toString());
		if (ODEEndpointUpdater.oldEndpointService == null) {
			ODEEndpointUpdater.endpointService = null;
		} else {
			ODEEndpointUpdater.oldEndpointService = null;
		}
		ODEEndpointUpdater.LOG.debug("Unregistered EndpointService {}", endpointService.toString());
	}
	
	/**
	 * Returns PortType of the bpel process composed of the given files list
	 * 
	 * @param planContents List of Files which make up the BPEL Process
	 * @return QName which should be exactly the PortType of the given BPEL
	 *         Process
	 */
	public QName getPortType(List<File> planContents) {
		try {
			File deployXML = this.getDeployXMl(planContents);
			JAXBContext context = JAXBContext.newInstance("org.apache.ode.schemas.dd._2007._03", this.getClass().getClassLoader());
			Unmarshaller unmarshaller = context.createUnmarshaller();
			TDeployment deploy = unmarshaller.unmarshal(new StreamSource(deployXML), TDeployment.class).getValue();
			for (TDeployment.Process process : deploy.getProcess()) {
				return process.getName();
			}
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	// /**
	// * Bind method for ModelRepositoryServices
	// *
	// * @param modelRepoService
	// * the ModelRepositoryService to bind
	// */
	// protected static void bindModelRepositoryService(
	// ICoreModelRepositoryService modelRepoService) {
	// if (modelRepoService != null) {
	// ODEEndpointUpdater.LOG.info(
	// "Registering ModelRepositoryService {}",
	// modelRepoService.toString());
	// if (ODEEndpointUpdater.modelRepoService == null) {
	// ODEEndpointUpdater.modelRepoService = modelRepoService;
	// } else {
	// ODEEndpointUpdater.oldModelRepoService = modelRepoService;
	// ODEEndpointUpdater.modelRepoService = modelRepoService;
	// }
	// ODEEndpointUpdater.LOG.info("Registered ModelRepositoryService {}",
	// modelRepoService.toString());
	// }
	// }
	//
	// /**
	// * Unbind method for ModelRepositoryServices
	// *
	// * @param modelRepoService
	// * the ModelRepositoryService to unbind
	// */
	// protected static void unbindModelRepositoryService(
	// ICoreModelRepositoryService modelRepoService) {
	// ODEEndpointUpdater.LOG.info("Unregistering ModelRepositoryService {}",
	// modelRepoService.toString());
	// if (ODEEndpointUpdater.oldModelRepoService == null) {
	// ODEEndpointUpdater.modelRepoService = null;
	// } else {
	// ODEEndpointUpdater.oldModelRepoService = null;
	// }
	// ODEEndpointUpdater.LOG.info("Unregistered ModelRepositoryService {}",
	// modelRepoService.toString());
	// }
	
	// /**
	// * Bind method for IToscaEngineService
	// *
	// * @param toscaEngine
	// * the IToscaEngineService to bind
	// */
	// // protected void bindIToscaEngineService(IToscaEngineService
	// toscaEngine) {
	// // if (toscaEngine != null) {
	// // ODEEndpointUpdater.LOG.info("Registering IToscaEngineService {}",
	// // toscaEngine.toString());
	// // ODEEndpointUpdater.toscaEngineService = toscaEngine;
	// // ODEEndpointUpdater.LOG.info("Registered IToscaEngineService {}",
	// // toscaEngine.toString());
	// // }
	// // }
	//
	// /**
	// * Unbind method for IToscaEngineService
	// *
	// * @param toscaEngienService
	// * the IToscaEngineService to unbind
	// */
	// // protected void unbindIToscaEngineService(
	// IToscaEngineService toscaEngienService) {
	// ODEEndpointUpdater.LOG.info("Unregistering IToscaEngineService {}",
	// toscaEngienService.toString());
	// ODEEndpointUpdater.toscaEngineService = null;
	// ODEEndpointUpdater.LOG.info("Unregistered IToscaEngineService {}",
	// toscaEngienService.toString());
	// }
}
