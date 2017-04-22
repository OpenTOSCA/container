package org.opentosca.instancedata.service.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.instancedata.service.IInstanceDataService;
import org.opentosca.instancedata.service.ReferenceNotFoundException;
import org.opentosca.instancedata.service.impl.persistence.NodeInstanceDAO;
import org.opentosca.instancedata.service.impl.persistence.RelationInstanceDAO;
import org.opentosca.instancedata.service.impl.persistence.ServiceInstanceDAO;
import org.opentosca.model.instancedata.NodeInstance;
import org.opentosca.model.instancedata.RelationInstance;
import org.opentosca.model.instancedata.ServiceInstance;
import org.opentosca.model.tosca.TBoundaryDefinitions;
import org.opentosca.model.tosca.TNodeTemplate;
import org.opentosca.model.tosca.TPropertyMapping;
import org.opentosca.toscaengine.service.IToscaEngineService;
import org.opentosca.toscaengine.service.NodeTemplateInstanceCounts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The InstanceDataService.<br>
 * The Engine offers a service to manage InstanceData for existing
 * ServiceTemplates inside the CSARs. It relies on the ToscaEngine to get its
 * information about existence of those and for values for the default
 * properties of created instances. <br>
 *
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Marcus Eisele - marcus.eisele@gmail.com
 *
 */

// TODO: should this be moved to own package to encapsulate WebService from
// internal service? Think so!
@WebService(name = "InstanceDataService")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class InstanceDataServiceImpl implements IInstanceDataService {

	final private static Logger LOG = LoggerFactory.getLogger(InstanceDataServiceImpl.class);

	public static IToscaEngineService toscaEngineService;

	// used for persistence
	private final ServiceInstanceDAO siDAO = new ServiceInstanceDAO();
	private final NodeInstanceDAO niDAO = new NodeInstanceDAO();
	private final RelationInstanceDAO riDAO = new RelationInstanceDAO();

	@Override
	@WebMethod(exclude = true)
	public List<ServiceInstance> getServiceInstances(URI serviceInstanceID, String serviceTemplateName,
			QName serviceTemplateID) {
		return siDAO.getServiceInstances(serviceInstanceID, serviceTemplateName, serviceTemplateID);
	}

	@Override
	@WebMethod(exclude = true)
	public List<ServiceInstance> getServiceInstancesWithDetails(CSARID csarId, QName serviceTemplateId,
			Integer serviceTemplateInstanceID) {
		return siDAO.getServiceInstances(csarId, serviceTemplateId, serviceTemplateInstanceID);
	}

	@Override
	@WebMethod(exclude = true)
	public ServiceInstance createServiceInstance(CSARID csarID, QName serviceTemplateID)
			throws ReferenceNotFoundException {
		InstanceDataServiceImpl.LOG
				.debug("Starting creating ServiceInstance for " + serviceTemplateID + " in " + csarID);
		// TODO: boolean flag for cascading creation? cool or not?
		// check if serviceTemplate doesn't exist
		if (!ToscaEngineProxy.doesServiceTemplateExist(InstanceDataServiceImpl.toscaEngineService, csarID,
				serviceTemplateID)) {
			InstanceDataServiceImpl.LOG.warn(String.format(
					"Failed to create ServiceInstance for CSAR-ID: %s / serviceTemplateID: %s - was not found!", csarID,
					serviceTemplateID));
			throw new ReferenceNotFoundException("ServiceTemplate doesn't exist in the specified CSAR");
		}
		// retrieve serviceTemplateName
		String serviceTemplateName = InstanceDataServiceImpl.toscaEngineService.getNameOfReference(csarID,
				serviceTemplateID);
		// get all min and maxCounts from the ServiceTemplate and construct
		// nodeInstances from it automatically
		NodeTemplateInstanceCounts instanceCounts = InstanceDataServiceImpl.toscaEngineService
				.getInstanceCountsOfNodeTemplatesByServiceTemplateID(csarID, serviceTemplateID);

		// creation of real objects
		ServiceInstance serviceInstance = new ServiceInstance(csarID, serviceTemplateID, serviceTemplateName);

		// construct initial properties of serviceTemplate
		Document properties = createServiceInstancePropertiesFromServiceTemplate(csarID, serviceTemplateID);

		serviceInstance.setProperties(properties);

		siDAO.storeServiceInstance(serviceInstance);
		// store serviceInstance so we can use nodeInstanceDAO to create
		// nodeInstances (they need an existing object because its working in
		// another transaction)
		// TODO: or is it better to get the alternative route? to do it in one
		// transaction? and have duplicate code? need to fetch nodeTemplateName
		// from toscaEngine f.ex.?

		// this creates required Node Templates of a Service Template, but this
		// functionality is out dated
		// HashMap<QName, InstanceCount> occurenceInformationMap =
		// instanceCounts.getOccurenceInformationMap();
		// Set<QName> qNamesOfNodeTemplates = occurenceInformationMap.keySet();
		// // create for each nodeTemplate the minimum amount of instances
		// // specified
		// for (QName qName : qNamesOfNodeTemplates) {
		// InstanceCount instanceCount = occurenceInformationMap.get(qName);
		// // create "instanceCount".min instances
		// for (int i = 0; i < instanceCount.min; i++) {
		// // new nodeInstance
		// createNodeInstance(qName, serviceInstance.getServiceInstanceID());
		// }
		// }
		// create associated nodeInstances

		return serviceInstance;
	}

	@Override
	@WebMethod(exclude = true)
	public void deleteServiceInstance(URI serviceInstanceID) {

		List<ServiceInstance> serviceInstances = siDAO.getServiceInstances(serviceInstanceID, null, null);

		if ((serviceInstances == null) || (serviceInstances.size() != 1)) {
			InstanceDataServiceImpl.LOG.warn(String
					.format("Failed to delete ServiceInstance: '%s' - could not be retrieved", serviceInstanceID));
			return;
		}
		siDAO.deleteServiceInstance(serviceInstances.get(0));
	}

	@Override
	@WebMethod(exclude = true)
	public List<NodeInstance> getNodeInstances(URI nodeInstanceID, QName nodeTemplateID, String nodeTemplateName,
			URI serviceInstanceID) {
		return niDAO.getNodeInstances(serviceInstanceID, nodeTemplateID, nodeTemplateName, nodeInstanceID);
	}

	@Override
	public List<RelationInstance> getRelationInstances(URI relationInstanceID, QName relationshipTemplateID,
			String relationshipTemplateName, URI serviceInstanceID) {
		return riDAO.getRelationInstances(serviceInstanceID, relationshipTemplateID, relationshipTemplateName,
				relationInstanceID);
	}

	@Override
	@WebMethod(exclude = true)
	public NodeInstance createNodeInstance(CSARID csarId, QName serviceTemplateId, int serviceTemplateInstanceID,
			QName nodeTemplateID) throws ReferenceNotFoundException {

		LOG.debug("Retrieve Node Template \"{{}}\":\"{}\" for csar \"{}\", Service Template \"{}\" instance \"{}\"",
				nodeTemplateID.getNamespaceURI(), nodeTemplateID.getLocalPart(), csarId, serviceTemplateId,
				serviceTemplateInstanceID);

		List<ServiceInstance> serviceInstances = siDAO.getServiceInstances(csarId, serviceTemplateId,
				serviceTemplateInstanceID);
		if ((serviceInstances == null) || (serviceInstances.size() != 1)) {
			String msg = String.format("Failed to create NodeInstance: ServiceInstance: '%s' - could not be retrieved",
					serviceTemplateInstanceID);
			InstanceDataServiceImpl.LOG.warn(msg);
			throw new ReferenceNotFoundException(msg);
		}
		ServiceInstance serviceInstance = serviceInstances.get(0);

		// check if nodeTemplate exists
		if (!InstanceDataServiceImpl.toscaEngineService.doesNodeTemplateExist(csarId, serviceTemplateId,
				nodeTemplateID.getLocalPart())) {
			String msg = String.format(
					"Failed to create NodeInstance: NodeTemplate: csar: %s serviceTemplateID: %s , nodeTemplateID: '%s' - could not be retrieved / does not exists",
					serviceInstance.getCSAR_ID(), serviceInstance.getServiceTemplateID(), nodeTemplateID);
			InstanceDataServiceImpl.LOG.warn(msg);
			throw new ReferenceNotFoundException(msg);
		}

		String nodeTemplateName = InstanceDataServiceImpl.toscaEngineService.getNameOfReference(csarId, nodeTemplateID);

		// use localparts because serviceInstance QName namespace HAS to be the
		// same as the namespace of the nodeInstance
		QName nodeTypeOfNodeTemplate = InstanceDataServiceImpl.toscaEngineService.getNodeTypeOfNodeTemplate(csarId,
				serviceTemplateId, nodeTemplateID.getLocalPart());

		// use localparts because serviceInstance QName namespace HAS to be the
		// same as the namespace of the nodeInstance
		Document propertiesOfNodeTemplate = InstanceDataServiceImpl.toscaEngineService
				.getPropertiesOfNodeTemplate(csarId, serviceTemplateId, nodeTemplateID.getLocalPart().toString());

		NodeInstance nodeInstance = new NodeInstance(nodeTemplateID, nodeTemplateName, nodeTypeOfNodeTemplate,
				serviceInstance);
		// set default properties
		nodeInstance.setProperties(propertiesOfNodeTemplate);
		niDAO.saveNodeInstance(nodeInstance);
		return nodeInstance;
	}
	
	@Override
	@WebMethod(exclude = true)
	public RelationInstance createRelationInstance(CSARID csarId, QName serviceTemplateId, int serviceTemplateInstanceID,
			QName relationshipTemplateID) throws ReferenceNotFoundException {

		LOG.debug("Retrieve Node Template \"{{}}\":\"{}\" for csar \"{}\", Service Template \"{}\" instance \"{}\"",
				relationshipTemplateID.getNamespaceURI(), relationshipTemplateID.getLocalPart(), csarId, serviceTemplateId,
				serviceTemplateInstanceID);

		List<ServiceInstance> serviceInstances = siDAO.getServiceInstances(csarId, serviceTemplateId,
				serviceTemplateInstanceID);
		if ((serviceInstances == null) || (serviceInstances.size() != 1)) {
			String msg = String.format("Failed to create NodeInstance: ServiceInstance: '%s' - could not be retrieved",
					serviceTemplateInstanceID);
			InstanceDataServiceImpl.LOG.warn(msg);
			throw new ReferenceNotFoundException(msg);
		}
		ServiceInstance serviceInstance = serviceInstances.get(0);

		// check if nodeTemplate exists
		
		if (!InstanceDataServiceImpl.toscaEngineService.doesRelationshipTemplateExist(csarId, serviceTemplateId,
				relationshipTemplateID.getLocalPart())) {
			String msg = String.format(
					"Failed to create RelationInstance: RelationshipTemplate: csar: %s serviceTemplateID: %s , relationshipTemplateID: '%s' - could not be retrieved / does not exists",
					serviceInstance.getCSAR_ID(), serviceInstance.getServiceTemplateID(), relationshipTemplateID);
			InstanceDataServiceImpl.LOG.warn(msg);
			throw new ReferenceNotFoundException(msg);
		}

		String relationshipTemplateName = InstanceDataServiceImpl.toscaEngineService.getNameOfReference(csarId, relationshipTemplateID);

		// use localparts because serviceInstance QName namespace HAS to be the
		// same as the namespace of the nodeInstance
		QName nodeTypeOfNodeTemplate = InstanceDataServiceImpl.toscaEngineService.getRelationshipTypeOfRelationshipTemplate(csarId,
				serviceTemplateId, relationshipTemplateID.getLocalPart());

		// use localparts because serviceInstance QName namespace HAS to be the
		// same as the namespace of the nodeInstance
		Document propertiesOfNodeTemplate = InstanceDataServiceImpl.toscaEngineService
				.getPropertiesOfRelationshipTemplate(csarId, serviceTemplateId, relationshipTemplateID.getLocalPart().toString());

		RelationInstance relationInstance = new RelationInstance(relationshipTemplateID, relationshipTemplateName, nodeTypeOfNodeTemplate,
				serviceInstance);
		// set default properties
		relationInstance.setProperties(propertiesOfNodeTemplate);
		riDAO.saveRelationInstance(relationInstance);
		return relationInstance;
	}

	/**
	 * Yes, this method throws always an exception. Why? Do not use the method!
	 */
	@Deprecated
	@Override
	public NodeInstance createNodeInstance(QName nodeTemplateIDQName, URI serviceInstanceIdURI)
			throws ReferenceNotFoundException {
		throw new ReferenceNotFoundException("DO NOT USE THIS METHOD!!!");
		// return null;
	}

	@Override
	@WebMethod(exclude = true)
	public void deleteNodeInstance(URI nodeInstanceID) {
		List<NodeInstance> nodeInstances = niDAO.getNodeInstances(null, null, null, nodeInstanceID);

		if ((nodeInstances == null) || (nodeInstances.size() != 1)) {
			InstanceDataServiceImpl.LOG.warn(
					String.format("Failed to delete NodeInstance: '%s' - could not be retrieved", nodeInstanceID));
			return;
		}
		niDAO.deleteNodeInstance(nodeInstances.get(0));

	}
	
	@Override
	@WebMethod(exclude = true)
	public void deleteRelationInstance(URI relationInstanceID) {
		List<RelationInstance> relationInstances = riDAO.getRelationInstances(null, null, null, relationInstanceID);

		if ((relationInstances == null) || (relationInstances.size() != 1)) {
			InstanceDataServiceImpl.LOG.warn(
					String.format("Failed to delete RelatioknInstance: '%s' - could not be retrieved", relationInstanceID));
			return;
		}
		riDAO.deleteRelationInstance(relationInstances.get(0));

	}

	@Override
	@WebMethod(exclude = true)
	public QName getRelationInstanceState(URI relationInstanceID) throws ReferenceNotFoundException {
		List<RelationInstance> relationInstances = riDAO.getRelationInstances(null, null, null, relationInstanceID);
		if ((relationInstances == null) || (relationInstances.size() != 1)) {
			String msg = String.format("Failed to get State of RelationInstance: '%s' - does it exist?", relationInstanceID);
			InstanceDataServiceImpl.LOG.warn(msg);
			throw new ReferenceNotFoundException(msg);
		}
		return relationInstances.get(0).getState();
	}

	@Override
	@WebMethod(exclude = true)
	public void setRelationInstanceState(URI relationInstanceID, QName state) throws ReferenceNotFoundException {
		List<RelationInstance> relationInstances = riDAO.getRelationInstances(null, null, null, relationInstanceID);

		if ((relationInstances == null) || (relationInstances.size() != 1)) {
			String msg = String.format("Failed to set State of RelationInstance: '%s' - does it exist?", relationInstanceID);
			InstanceDataServiceImpl.LOG.warn(msg);
			throw new ReferenceNotFoundException(msg);
		}
		riDAO.setState(relationInstances.get(0), state);
	}
	
	@Override
	@WebMethod(exclude = true)
	public QName getNodeInstanceState(URI nodeInstanceID) throws ReferenceNotFoundException {
		List<NodeInstance> nodeInstances = niDAO.getNodeInstances(null, null, null, nodeInstanceID);
		if ((nodeInstances == null) || (nodeInstances.size() != 1)) {
			String msg = String.format("Failed to get State of NodeInstance: '%s' - does it exist?", nodeInstanceID);
			InstanceDataServiceImpl.LOG.warn(msg);
			throw new ReferenceNotFoundException(msg);
		}
		return nodeInstances.get(0).getState();
	}

	@Override
	@WebMethod(exclude = true)
	public void setNodeInstanceState(URI nodeInstanceID, QName state) throws ReferenceNotFoundException {
		List<NodeInstance> nodeInstances = niDAO.getNodeInstances(null, null, null, nodeInstanceID);

		if ((nodeInstances == null) || (nodeInstances.size() != 1)) {
			String msg = String.format("Failed to set State of NodeInstance: '%s' - does it exist?", nodeInstanceID);
			InstanceDataServiceImpl.LOG.warn(msg);
			throw new ReferenceNotFoundException(msg);
		}
		niDAO.setState(nodeInstances.get(0), state);
	}
	
	
	

	@Override
	@WebMethod(exclude = true)
	public Document getServiceInstanceProperties(URI serviceInstanceID, List<QName> propertiesList)
			throws ReferenceNotFoundException {
		List<ServiceInstance> serviceInstances = getServiceInstances(serviceInstanceID, null, null);

		if ((serviceInstances == null) || (serviceInstances.size() != 1)) {
			String msg = String.format("Failed to retrieve ServiceInstance: '%s'", serviceInstanceID);
			InstanceDataServiceImpl.LOG.warn(msg);
			throw new ReferenceNotFoundException(msg);
		}

		ServiceInstance serviceInstance = serviceInstances.get(0);

		updateServiceInstanceProperties(serviceInstance);

		return serviceInstance.getProperties();
	}

	@Override
	@WebMethod(exclude = true)
	// TODO: should it return a empty document when there aren't any properties
	// for the nodeinstance?
	public Document getRelationInstanceProperties(URI relationInstanceID, List<QName> propertiesList)
			throws ReferenceNotFoundException {
		List<RelationInstance> relationInstances = getRelationInstances(relationInstanceID, null, null, null);

		if ((relationInstances == null) || (relationInstances.size() != 1)) {
			String msg = String.format("Failed to retrieve NodeInstance: '%s'", relationInstanceID);
			InstanceDataServiceImpl.LOG.warn(msg);
			throw new ReferenceNotFoundException(msg);
		}
		RelationInstance relationInstance = relationInstances.get(0);
		Document retrievedProperties = relationInstance.getProperties();

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

		// create new DOM-Document with new RootElement named like the old one
		Document resultingProperties = InstanceDataServiceImpl.emptyDocument();
		Element createElementNS = resultingProperties.createElement("Properties");
		resultingProperties.appendChild(createElementNS);

		// filter elements from the properties
		NodeList childNodes = docElement.getChildNodes();

		for (int i = 0; i < childNodes.getLength(); i++) {
			Node currentItem = childNodes.item(i);

			// this is a fix for empty text values due to a bug in the
			// toscaReferenceMapper
			if (currentItem.getLocalName() == null) {
				// if QName can't be build skip this childNode (entry inside xml
				// document)
				continue;
			}

			// calculate qName of the currentItem
			QName currentItemQName = new QName(currentItem.getNamespaceURI(), currentItem.getLocalName());

			// match the item against the filters
			for (QName qName : propertiesList) {
				if (qName.equals(currentItemQName)) {
					// match was found, add it to result (first deep clone the
					// element => then adopt to document and finally append to
					// the documentElement
					Node cloneNode = currentItem.cloneNode(true);
					resultingProperties.adoptNode(cloneNode);
					resultingProperties.getDocumentElement().appendChild(cloneNode);
				}
			}
		}

		return resultingProperties;
	}
	
	@Override
	@WebMethod(exclude = true)
	// TODO: should it return a empty document when there aren't any properties
	// for the nodeinstance?
	public Document getNodeInstanceProperties(URI nodeInstanceID, List<QName> propertiesList)
			throws ReferenceNotFoundException {
		List<NodeInstance> nodeInstances = getNodeInstances(nodeInstanceID, null, null, null);

		if ((nodeInstances == null) || (nodeInstances.size() != 1)) {
			String msg = String.format("Failed to retrieve NodeInstance: '%s'", nodeInstanceID);
			InstanceDataServiceImpl.LOG.warn(msg);
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

		// create new DOM-Document with new RootElement named like the old one
		Document resultingProperties = InstanceDataServiceImpl.emptyDocument();
		Element createElementNS = resultingProperties.createElement("Properties");
		resultingProperties.appendChild(createElementNS);

		// filter elements from the properties
		NodeList childNodes = docElement.getChildNodes();

		for (int i = 0; i < childNodes.getLength(); i++) {
			Node currentItem = childNodes.item(i);

			// this is a fix for empty text values due to a bug in the
			// toscaReferenceMapper
			if (currentItem.getLocalName() == null) {
				// if QName can't be build skip this childNode (entry inside xml
				// document)
				continue;
			}

			// calculate qName of the currentItem
			QName currentItemQName = new QName(currentItem.getNamespaceURI(), currentItem.getLocalName());

			// match the item against the filters
			for (QName qName : propertiesList) {
				if (qName.equals(currentItemQName)) {
					// match was found, add it to result (first deep clone the
					// element => then adopt to document and finally append to
					// the documentElement
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
			InstanceDataServiceImpl.LOG.error(e.getMessage());
		}
		return null;
	}

	@Override
	@WebMethod(exclude = true)
	public void setRelationInstanceProperties(URI relationInstanceID, Document properties) throws ReferenceNotFoundException {

		List<RelationInstance> relationInstances = riDAO.getRelationInstances(null, null, null, relationInstanceID);

		if ((relationInstances == null) || (relationInstances.size() != 1)) {
			InstanceDataServiceImpl.LOG.warn(
					String.format("Failed to set Properties of NodeInstance: '%s' - does it exist?", relationInstanceID));
			return;
		}

		riDAO.setProperties(relationInstances.get(0), properties);

		updateServiceInstanceProperties(relationInstances.get(0).getServiceInstance());
		return;

	}
	
	@Override
	@WebMethod(exclude = true)
	public void setNodeInstanceProperties(URI nodeInstanceID, Document properties) throws ReferenceNotFoundException {

		List<NodeInstance> nodeInstances = niDAO.getNodeInstances(null, null, null, nodeInstanceID);

		if ((nodeInstances == null) || (nodeInstances.size() != 1)) {
			InstanceDataServiceImpl.LOG.warn(
					String.format("Failed to set Properties of NodeInstance: '%s' - does it exist?", nodeInstanceID));
			return;
		}

		niDAO.setProperties(nodeInstances.get(0), properties);

		updateServiceInstanceProperties(nodeInstances.get(0).getServiceInstance());
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

	// TODO: remove this when deprecated methods are removed
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

	@WebMethod(exclude = true)
	private void updateServiceInstanceProperties(ServiceInstance serviceInstance) {
		// check if the serviceInstance has properties
		if (serviceInstance.getProperties() == null) {
			return;
		}

		// check if the serviceTemplate has propertyMappings
		TBoundaryDefinitions boundaryDefs = InstanceDataServiceImpl.toscaEngineService
				.getBoundaryDefinitionsOfServiceTemplate(serviceInstance.getCSAR_ID(),
						serviceInstance.getServiceTemplateID());

		if ((boundaryDefs == null) || (boundaryDefs.getProperties() == null)
				|| (boundaryDefs.getProperties().getPropertyMappings() == null)) {
			// if there are no property mappings there is no need to update. The
			// properties can only be updated be external clients via setting
			// properties by hand
			return;
		}

		Element properties = (Element) serviceInstance.getProperties().getFirstChild();

		// cycle through mappings and update accordingly
		for (TPropertyMapping mapping : boundaryDefs.getProperties().getPropertyMappings().getPropertyMapping()) {
			String serviceTemplatePropertyQuery = mapping.getServiceTemplatePropertyRef();
			List<Element> serviceTemplatePropertyElements = queryElementList(properties, serviceTemplatePropertyQuery);

			// fetch element from serviceTemplateProperties

			if (serviceTemplatePropertyElements.size() != 1) {
				// skip this property, we expect only one
				continue;
			}

			// check whether the targetRef is concat query
			if (isConcatQuery(mapping.getTargetPropertyRef())) {
				// this query needs possibly multiple properties from different
				// nodeInstances

				String propertyValue = generatePropertyValueFromConcatQuery(mapping.getTargetPropertyRef(),
						getNodeInstances(null, null, null, serviceInstance.getServiceInstanceID()));

				serviceTemplatePropertyElements.get(0).setTextContent(propertyValue);

			} else {
				// this query only fetches a SINGLE element on the properties of
				// the referenced entity

				NodeInstance nodeInstance = getNodeInstanceFromMappingObject(serviceInstance,
						mapping.getTargetObjectRef());

				if (nodeInstance == null) {
					continue;
				}

				Document nodeProperties = nodeInstance.getProperties();
				if (nodeProperties == null) {
					// skip it, the mapping is invalid
					continue;
				}
				Element nodePropertiesRoot = (Element) nodeProperties.getFirstChild();
				String nodeTemplatePropertyQuery = mapping.getTargetPropertyRef();

				List<Element> nodePropertyElements = queryElementList(nodePropertiesRoot, nodeTemplatePropertyQuery);

				if (nodePropertyElements.size() != 1) {
					// skip this property, we expect only one
					continue;
				}

				// change the serviceTemplateProperty
				serviceTemplatePropertyElements.get(0).setTextContent(nodePropertyElements.get(0).getTextContent());

			}
		}

		serviceInstance.setProperties(properties.getOwnerDocument());
		siDAO.storeServiceInstance(serviceInstance);
	}

	private String generatePropertyValueFromConcatQuery(String targetPropertyRef, List<NodeInstance> nodeInstance) {
		String testQuery = targetPropertyRef.trim();

		if (!testQuery.endsWith(")")) {
			return null;
		}

		int functionOpeningBracket = testQuery.indexOf("(");

		String functionString = testQuery.substring(0, functionOpeningBracket);

		// simple validity check as we only want to be able to concat strings,
		// but maybe more later
		if (!functionString.equals("concat")) {
			return null;
		}

		String functionContent = testQuery.substring(functionOpeningBracket + 1, testQuery.lastIndexOf(")")).trim();

		String[] functionParts = functionContent.split(",");

		List<String> augmentedFunctionParts = new ArrayList<String>();

		for (String functionPart : functionParts) {
			if (functionPart.trim().startsWith("'")) {
				// string function part, just add to list
				augmentedFunctionParts.add(functionPart.trim());
			} else if (functionPart.trim().split("\\.").length == 3) {
				// "DSL" Query
				String[] queryParts = functionPart.trim().split("\\.");
				// fast check for validity
				if (!queryParts[1].equals("Properties")) {
					return null;
				}

				String nodeTemplateName = queryParts[0];
				String propertyName = queryParts[2];

				if (getNodeInstanceWithName(nodeInstance, nodeTemplateName) != null) {

					String propValue = fetchPropertyValueFromNodeInstance(
							getNodeInstanceWithName(nodeInstance, nodeTemplateName), propertyName);

					augmentedFunctionParts.add("'" + propValue + "'");
				}
			}
		}

		// now we have a string of the form:
		// concat('someString','somePropertyValue','someString',..)
		// just make the concat itself instead of running an XPath query

		String resultString = "";
		for (String functionPart : augmentedFunctionParts) {
			resultString += functionPart.replace("'", "");
		}

		return resultString;
	}

	private NodeInstance getNodeInstanceWithName(List<NodeInstance> nodeInstances, String nodeTemplateId) {

		for (NodeInstance nodeInstance : nodeInstances) {
			if (nodeInstance.getNodeTemplateID().getLocalPart().equals(nodeTemplateId)) {
				return nodeInstance;
			}
		}

		return null;
	}

	private String fetchPropertyValueFromNodeInstance(NodeInstance nodeInstance, String propertyLocalName) {

		if (nodeInstance.getProperties() == null) {
			return null;
		}

		NodeList childNodes = nodeInstance.getProperties().getFirstChild().getChildNodes();

		for (int index = 0; index < childNodes.getLength(); index++) {
			Node childNode = childNodes.item(index);
			if ((childNode.getNodeType() == Node.ELEMENT_NODE) && childNode.getLocalName().equals(propertyLocalName)) {
				return childNode.getTextContent();
			}

		}

		return null;
	}

	private List<Element> queryElementList(Element node, String xpathQuery) {
		List<Element> elements = new ArrayList<>();
		try {
			XPath xPath = XPathFactory.newInstance().newXPath();

			NodeList nodes = (NodeList) xPath.evaluate(xpathQuery, node, XPathConstants.NODESET);

			for (int index = 0; index < nodes.getLength(); index++) {
				if (nodes.item(index).getNodeType() == Node.ELEMENT_NODE) {
					elements.add((Element) nodes.item(index));
				}

			}

		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return elements;
	}

	private NodeInstance getNodeInstanceFromMappingObject(ServiceInstance serviceInstance, Object obj) {
		if (obj instanceof TNodeTemplate) {

			TNodeTemplate template = (TNodeTemplate) obj;

			// service.getNodeInstances(null, null, null,
			// serviceInstanceIDtoURI);

			List<NodeInstance> nodeInstances = getNodeInstances(null, null, null,
					serviceInstance.getServiceInstanceID());

			if (nodeInstances == null) {
				return null;
			}

			for (NodeInstance nodeInstance : nodeInstances) {
				if (nodeInstance.getNodeTemplateID().getLocalPart().equals(template.getId())) {
					return nodeInstance;
				}
			}
		}
		return null;
	}

	private boolean isConcatQuery(String xPathQuery) {
		String testString = xPathQuery.trim();

		if (!testString.startsWith("concat(")) {
			return false;
		}

		String functionContent = testString.substring("concat(".length());
		functionContent = functionContent.substring(0, functionContent.length() - 1);

		String[] functionParts = functionContent.split(",");

		for (String functionPart : functionParts) {
			if ((functionPart.startsWith("'") && !functionPart.endsWith("'"))) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Creates a DOM Document containing only the properties of the given
	 * ServiceTemplate which are declared. NodeInstance data is not considered
	 * as this method should be used to initialize the properties
	 *
	 * @param csarId
	 *            the Id of the CSAR the serviceTemplate belongs to
	 * @param serviceTemplateId
	 *            the Id of the serviceTemplate
	 * @return a DOM document containing elements representing properties of the
	 *         serviceTemplate
	 */
	private Document createServiceInstancePropertiesFromServiceTemplate(CSARID csarId, QName serviceTemplateId) {

		InstanceDataServiceImpl.LOG
				.debug("Creating initial ServiceInstance Properties for " + serviceTemplateId + " in " + csarId);
		TBoundaryDefinitions boundaryDefs = InstanceDataServiceImpl.toscaEngineService
				.getBoundaryDefinitionsOfServiceTemplate(csarId, serviceTemplateId);

		Element propertiesElement = null;

		if ((boundaryDefs != null) && (boundaryDefs.getProperties() != null)) {

			LOG.debug("Properties found in Bounds for ST {}", serviceTemplateId);

			// Document emptyDoc = InstanceDataServiceImpl.emptyDocument();
			//
			// Element createElementNS =
			// emptyDoc.createElementNS("http://docs.oasis-open.org/tosca/ns/2011/12","Properties");
			// createElementNS.setAttribute("xmlns:tosca",
			// "http://docs.oasis-open.org/tosca/ns/2011/12");
			// createElementNS.setPrefix("tosca");
			//
			// emptyDoc.appendChild(createElementNS);
			//
			// Element properties = (Element)
			// boundaryDefs.getProperties().getAny();
			//
			// if(properties.getNamespaceURI() == null){
			// // set tosca namespace
			// Node clonedNode = properties.cloneNode(true);
			// Node renamedNode =
			// properties.getOwnerDocument().renameNode(clonedNode,
			// "http://docs.oasis-open.org/tosca/ns/2011/12",
			// properties.getLocalName());
			//
			// properties = (Element)renamedNode;
			// properties.setAttribute("xmlns:tosca",
			// "http://docs.oasis-open.org/tosca/ns/2011/12");
			// properties.setPrefix("tosca");
			// }
			//
			// properties = (Element) emptyDoc.importNode(properties, true);
			//
			// createElementNS.appendChild(properties);
			//
			// propertiesElement = (Element) emptyDoc.getFirstChild();
			propertiesElement = (Element) boundaryDefs.getProperties().getAny();

			if (null == propertiesElement || null == propertiesElement.getOwnerDocument()) {

				LOG.debug("null pointer ahead!");

				// LOG.debug("No Properties found in Bounds for ST {} thus
				// create blank ones", serviceTemplateId);
				// Document emptyDoc = InstanceDataServiceImpl.emptyDocument();
				// Element createElementNS =
				// emptyDoc.createElementNS("http://docs.oasis-open.org/tosca/ns/2011/12",
				// "Properties");
				// createElementNS.setAttribute("xmlns:tosca",
				// "http://docs.oasis-open.org/tosca/ns/2011/12");
				// createElementNS.setPrefix("tosca");
				// emptyDoc.appendChild(createElementNS);
				// propertiesElement = (Element) emptyDoc.getFirstChild();
			}
		} else {

			LOG.debug("No Properties found in Bounds for ST {} thus create blank ones", serviceTemplateId);
			Document emptyDoc = InstanceDataServiceImpl.emptyDocument();
			Element createElementNS = emptyDoc.createElementNS("http://docs.oasis-open.org/tosca/ns/2011/12",
					"Properties");
			createElementNS.setAttribute("xmlns:tosca", "http://docs.oasis-open.org/tosca/ns/2011/12");
			createElementNS.setPrefix("tosca");
			emptyDoc.appendChild(createElementNS);
			propertiesElement = (Element) emptyDoc.getFirstChild();

		}

		return propertiesElement.getOwnerDocument();
	}

	@Override
	@WebMethod(exclude = true)
	public void setServiceInstanceProperties(URI serviceInstanceID, Document properties)
			throws ReferenceNotFoundException {
		List<ServiceInstance> serviceInstances = getServiceInstances(serviceInstanceID, null, null);

		if (serviceInstances.size() != 1) {
			throw new ReferenceNotFoundException("Couldn't find serviceInstance");
		}

		ServiceInstance serviceInstance = serviceInstances.get(0);

		serviceInstance.setProperties(properties);

		siDAO.storeServiceInstance(serviceInstance);

		updateServiceInstanceProperties(serviceInstance);
	}
}