package org.opentosca.instancedata.service.impl;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.instancedata.service.IInstanceDataService;
import org.opentosca.instancedata.service.ReferenceNotFoundException;
import org.opentosca.instancedata.service.impl.persistence.NodeInstanceDAO;
import org.opentosca.instancedata.service.impl.persistence.ServiceInstanceDAO;
import org.opentosca.model.instancedata.NodeInstance;
import org.opentosca.model.instancedata.ServiceInstance;
import org.opentosca.toscaengine.service.IToscaEngineService;
import org.opentosca.toscaengine.service.NodeTemplateInstanceCounts;
import org.opentosca.toscaengine.service.NodeTemplateInstanceCounts.InstanceCount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The InstanceDataService.<br>
 * The Engine offers a service to manage InstanceData for existing ServiceTemplates inside the CSARs.
 * It relies on the ToscaEngine to get its information about existence of those and for values for the default properties
 * of created instances.
 * <br>
 * 
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Marcus Eisele - marcus.eisele@gmail.com
 * 
 */

//TODO: should this be moved to own package to encapsulate WebService from internal service? Think so!
@WebService(name = "InstanceDataService")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class InstanceDataServiceImpl implements IInstanceDataService {
	
	final private static Logger LOG = LoggerFactory.getLogger(InstanceDataServiceImpl.class);
	
	private static IToscaEngineService toscaEngineService;
	
	//used for persistence
	private final ServiceInstanceDAO siDAO = new ServiceInstanceDAO();
	private final NodeInstanceDAO niDAO = new NodeInstanceDAO();
	
	@Override
	@WebMethod(exclude = true)
	public List<ServiceInstance> getServiceInstances(URI serviceInstanceID, String serviceTemplateName,
			QName serviceTemplateID) {
		return this.siDAO.getServiceInstances(serviceInstanceID, serviceTemplateName, serviceTemplateID);
	}
	
	@Override
	@WebMethod(exclude = true)
	public ServiceInstance createServiceInstance(CSARID csarID, QName serviceTemplateID)
			throws ReferenceNotFoundException {
		//TODO: boolean flag for cascading creation? cool or not?
		// check if serviceTemplate doesn't exist
		if (!ToscaEngineProxy.doesServiceTemplateExist(toscaEngineService, csarID, serviceTemplateID)) {
			LOG.warn(String.format(
					"Failed to create ServiceInstance for CSAR-ID: %s / serviceTemplateID: %s - was not found!",
					csarID, serviceTemplateID));
			throw new ReferenceNotFoundException("ServiceTemplate doesn't exist in the specified CSAR");
		}
		// retrieve serviceTemplateName
		String serviceTemplateName = toscaEngineService.getNameOfReference(csarID, serviceTemplateID);
		//get all min and maxCounts from the ServiceTemplate and construct nodeInstances from it automatically
		NodeTemplateInstanceCounts instanceCounts = toscaEngineService.getInstanceCountsOfNodeTemplatesByServiceTemplateID(csarID, serviceTemplateID);
		
		//creation of real objects
		ServiceInstance serviceInstance = new ServiceInstance(csarID, serviceTemplateID, serviceTemplateName);
		this.siDAO.storeServiceInstance(serviceInstance);
		//store serviceInstance so we can use nodeInstanceDAO to create nodeInstances (they need an existing object because its working in another transaction)
		//TODO: or is it better to get the alternative route? to do it in one transaction? and have duplicate code? need to fetch nodeTemplateName from toscaEngine f.ex.?
		
		HashMap<QName, InstanceCount> occurenceInformationMap = instanceCounts.getOccurenceInformationMap();
		Set<QName> qNamesOfNodeTemplates = occurenceInformationMap.keySet();
		//create for each nodeTemplate the minimum amount of instances specified
		for (QName qName : qNamesOfNodeTemplates) {
			InstanceCount instanceCount = occurenceInformationMap.get(qName);
			//create "instanceCount".min instances
			for (int i = 0; i < instanceCount.min; i++) {
				//new nodeInstance
				this.createNodeInstance(qName, serviceInstance.getServiceInstanceID());
			}
		}
		//create associated nodeInstances
		
		return serviceInstance;
	}
	
	@Override
	@WebMethod(exclude = true)
	public void deleteServiceInstance(URI serviceInstanceID) {
		
		List<ServiceInstance> serviceInstances = this.siDAO.getServiceInstances(serviceInstanceID, null, null);
		
		if (serviceInstances == null || serviceInstances.size() != 1) {
			LOG.warn(String
					.format("Failed to delete ServiceInstance: '%s' - could not be retrieved", serviceInstanceID));
			return;
		}
		this.siDAO.deleteServiceInstance(serviceInstances.get(0));
	}
	
	@Override
	@WebMethod(exclude = true)
	public List<NodeInstance> getNodeInstances(URI nodeInstanceID, QName nodeTemplateID, String nodeTemplateName,
			URI serviceInstanceID) {
		return this.niDAO.getNodeInstances(serviceInstanceID, nodeTemplateID, nodeTemplateName, nodeInstanceID);
	}
	
	@Override
	@WebMethod(exclude = true)
	public NodeInstance createNodeInstance(QName nodeTemplateID, URI serviceInstanceID)
			throws ReferenceNotFoundException {
		
		List<ServiceInstance> serviceInstances = this.siDAO.getServiceInstances(serviceInstanceID, null, null);
		if (serviceInstances == null || serviceInstances.size() != 1) {
			String msg = String.format("Failed to create NodeInstance: ServiceInstance: '%s' - could not be retrieved",
					serviceInstanceID);
			LOG.warn(msg);
			throw new ReferenceNotFoundException(msg);
		}
		ServiceInstance serviceInstance = serviceInstances.get(0);
		
		//check if nodeTemplate exists
		if (!toscaEngineService.doesNodeTemplateExist(serviceInstance.getCSAR_ID(), serviceInstance.getServiceTemplateID(), nodeTemplateID.getLocalPart())) {
			String msg = String.format("Failed to create NodeInstance: NodeTemplate: csar: %s serviceTemplateID: %s , nodeTemplateID: '%s' - could not be retrieved / does not exists",
					serviceInstance.getCSAR_ID(), serviceInstance.getServiceTemplateID(), nodeTemplateID);
			LOG.warn(msg);
			throw new ReferenceNotFoundException(msg);
		}
		
		String nodeTemplateName = toscaEngineService.getNameOfReference(serviceInstance.getCSAR_ID(), nodeTemplateID);
		
		//use localparts because serviceInstance QName namespace HAS to be the same as the namespace of the nodeInstance
		QName nodeTypeOfNodeTemplate = toscaEngineService.getNodeTypeOfNodeTemplate(serviceInstance.getCSAR_ID(), serviceInstance.getServiceTemplateID(), nodeTemplateID.getLocalPart());
		
		//use localparts because serviceInstance QName namespace HAS to be the same as the namespace of the nodeInstance
		Document propertiesOfNodeTemplate = toscaEngineService.getPropertiesOfNodeTemplate(
				serviceInstance.getCSAR_ID(), serviceInstance.getServiceTemplateID(), nodeTemplateID.getLocalPart()
				.toString());
		
		NodeInstance nodeInstance = new NodeInstance(nodeTemplateID, nodeTemplateName, nodeTypeOfNodeTemplate, serviceInstance);
		// set default properties
		nodeInstance.setProperties(propertiesOfNodeTemplate);
		this.niDAO.saveNodeInstance(nodeInstance);
		return nodeInstance;
	}
	
	@Override
	@WebMethod(exclude = true)
	public void deleteNodeInstance(URI nodeInstanceID) {
		List<NodeInstance> nodeInstances = this.niDAO.getNodeInstances(null, null, null, nodeInstanceID);
		
		if (nodeInstances == null || nodeInstances.size() != 1) {
			LOG.warn(String.format("Failed to delete NodeInstance: '%s' - could not be retrieved", nodeInstanceID));
			return;
		}
		this.niDAO.deleteNodeInstance(nodeInstances.get(0));
		
	}
	
	@Override
	@WebMethod(exclude = true)
	public QName getState(URI nodeInstanceID) throws ReferenceNotFoundException {
		List<NodeInstance> nodeInstances = niDAO.getNodeInstances(null, null, null, nodeInstanceID);
		if (nodeInstances == null || nodeInstances.size() != 1) {
			String msg = String.format("Failed to get State of NodeInstance: '%s' - does it exist?", nodeInstanceID);
			LOG.warn(msg);
			throw new ReferenceNotFoundException(msg);
		}
		return nodeInstances.get(0).getState();
	}
	
	@Override
	@WebMethod(exclude = true)
	public void setState(URI nodeInstanceID, QName state) throws ReferenceNotFoundException {
		List<NodeInstance> nodeInstances = niDAO.getNodeInstances(null, null, null, nodeInstanceID);
		
		if (nodeInstances == null || nodeInstances.size() != 1) {
			String msg = String.format("Failed to set State of NodeInstance: '%s' - does it exist?", nodeInstanceID);
			LOG.warn(msg);
			throw new ReferenceNotFoundException(msg);
		}
		niDAO.setState(nodeInstances.get(0), state);
	}
	
	@Override
	@WebMethod(exclude = true)
	// TODO: should it return a empty document when there aren't any properties for the nodeinstance?
	public Document getProperties(URI nodeInstanceID, List<QName> propertiesList) throws ReferenceNotFoundException {
		List<NodeInstance> nodeInstances = getNodeInstances(nodeInstanceID, null, null, null);
		
		if (nodeInstances == null || nodeInstances.size() != 1) {
			String msg = String.format("Failed to retrieve NodeInstance: '%s'", nodeInstanceID);
			LOG.warn(msg);
			throw new ReferenceNotFoundException(msg);
		}
		NodeInstance nodeInstance = nodeInstances.get(0);
		Document retrievedProperties = nodeInstance.getProperties();
		
		
		// start extracting relevant properties
		// if propertiesList == null return NO PROPERTIES
		// if propertiesList.isEmpty() return ALL PROPERTIES
		// if it contains values => filter and return them
		if (propertiesList == null) {
			return null;
		}
		if (propertiesList.isEmpty()) {
			return retrievedProperties;
		}
		
		Element docElement = retrievedProperties.getDocumentElement();
		if (docElement == null) {
			return null;
		}
		
		//create new DOM-Document with new RootElement named like the old one
		Document resultingProperties = emptyDocument();
		Element createElementNS = resultingProperties.createElement("Properties");
		resultingProperties.appendChild(createElementNS);
		
		// filter elements from the properties
		NodeList childNodes = docElement.getChildNodes();
		
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node currentItem = childNodes.item(i);
			
			// this is a fix for empty text values due to a bug in the toscaReferenceMapper
			if (currentItem.getLocalName() == null) {
				// if QName can't be build skip this childNode (entry inside xml document)
				continue;
			}
			
			// calculate qName of the currentItem
			QName currentItemQName = new QName(currentItem.getNamespaceURI(), currentItem.getLocalName());
			
			// match the item against the filters
			for (QName qName : propertiesList) {
				if (qName.equals(currentItemQName)) {
					// match was found, add it to result (first deep clone the element => then adopt to document and finally append to the documentElement
					Node cloneNode = currentItem.cloneNode(true);
					resultingProperties.adoptNode(cloneNode);
					resultingProperties.getDocumentElement().appendChild(cloneNode);
				}
			}
		}
		
		return resultingProperties;
	}
	
	/**
	 * Creates an empty DOM document.
	 * 
	 * @return An empty DOM document.
	 */
	private static Document emptyDocument() {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.newDocument();
			return doc;
		} catch (ParserConfigurationException e) {
			LOG.error(e.getMessage());
		}
		return null;
	}
	
	@Override
	@WebMethod(exclude = true)
	public void setProperties(URI nodeInstanceID, Document properties) throws ReferenceNotFoundException {
		
		List<NodeInstance> nodeInstances = this.niDAO.getNodeInstances(null, null, null, nodeInstanceID);
		
		if (nodeInstances == null || nodeInstances.size() != 1) {
			LOG.warn(String.format("Failed to set Properties of NodeInstance: '%s' - does it exist?", nodeInstanceID));
			return;
		}
		
		this.niDAO.setProperties(nodeInstances.get(0), properties);
		
		return;
		
	}
	
	@WebMethod(exclude = true)
	public void bindToscaEngineService(IToscaEngineService toscaEngineService) {
		if (toscaEngineService == null) {
			InstanceDataServiceImpl.LOG.error("Can't bind ToscaEngine Service.");
		} else {
			InstanceDataServiceImpl.toscaEngineService = toscaEngineService;
			InstanceDataServiceImpl.LOG.debug("ToscaEngine-Service bound.");
		}
	}
	
	@WebMethod(exclude = true)
	public void unbindToscaEngineService(IToscaEngineService toscaEngineService) {
		InstanceDataServiceImpl.toscaEngineService = null;
		InstanceDataServiceImpl.LOG.debug("ToscaEngine-Service unbound.");
		
	}
	
	//TODO: remove this when deprecated methods are removed
	static HashMap<String, String> instanceData = new HashMap<String, String>();
	
	
	@Override
	@WebMethod
	public void setProperty(@WebParam(name = "key") String key, @WebParam(name = "value") String value) {
		System.out.println("Setting key: " + key + " with value: " + value);
		InstanceDataServiceImpl.instanceData.put(key, value);
	}
	
	@Override
	@WebMethod
	public String getProperty(@WebParam(name = "key") String key) {
		System.out.println("Getting value for key: " + key);
		
		return InstanceDataServiceImpl.instanceData.get(key);
	}
	
	@Override
	public HashMap<String, String> getProperties(String keyPrefix) {
		System.out.println("Getting values for key beginning with: " + keyPrefix);
		
		HashMap<String, String> properties = new HashMap<String, String>();
		
		for (Map.Entry<String, String> entry : InstanceDataServiceImpl.instanceData.entrySet()) {
			
			String key = entry.getKey();
			if (key.startsWith(keyPrefix)) {
				String value = entry.getValue();
				properties.put(key, value);
			}
		}
		
		return properties;
	}
	
}