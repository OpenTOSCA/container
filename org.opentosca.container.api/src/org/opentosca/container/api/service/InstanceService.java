package org.opentosca.container.api.service;

import java.util.Collection;
import java.util.Optional;

import javax.ws.rs.NotFoundException;
import javax.xml.namespace.QName;

import org.opentosca.container.api.dto.NodeTemplateDTO;
import org.opentosca.container.api.dto.RelationshipTemplateDTO;
import org.opentosca.container.core.common.jpa.DocumentConverter;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.NodeTemplateInstanceProperty;
import org.opentosca.container.core.next.model.NodeTemplateInstanceState;
import org.opentosca.container.core.next.model.Property;
import org.opentosca.container.core.next.model.RelationshipTemplateInstance;
import org.opentosca.container.core.next.model.RelationshipTemplateInstanceProperty;
import org.opentosca.container.core.next.model.RelationshipTemplateInstanceState;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.model.ServiceTemplateInstanceProperty;
import org.opentosca.container.core.next.model.ServiceTemplateInstanceState;
import org.opentosca.container.core.next.repository.NodeTemplateInstanceRepository;
import org.opentosca.container.core.next.repository.RelationshipTemplateInstanceRepository;
import org.opentosca.container.core.next.repository.ServiceTemplateInstanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 * Allows access to instance information for service templates and node
 * templates.
 */
public class InstanceService {

	private static Logger logger = LoggerFactory.getLogger(InstanceService.class);

	private final ServiceTemplateInstanceRepository serviceTemplateInstanceRepository = new ServiceTemplateInstanceRepository();
	private final NodeTemplateInstanceRepository nodeTemplateInstanceRepository = new NodeTemplateInstanceRepository();
	private final RelationshipTemplateInstanceRepository relationshipTemplateInstanceRepository = new RelationshipTemplateInstanceRepository();
	private RelationshipTemplateService relationshipTemplateService;
	private NodeTemplateService nodeTemplateService;
	private final DocumentConverter converter = new DocumentConverter();

	private Document convertPropertyToDocument(Property property) {
		return (Document) converter.convertDataValueToObjectValue(property.getValue(), null);
	}

	/**
	 * Converts an xml document to an xml-based property sui/table for service or node template instances
	 * @param propertyDoc
	 * @param type
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	private <T extends Property> T convertDocumentToProperty(Document propertyDoc, Class<T> type)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException {
		
		if(propertyDoc == null) {
			final String msg = String.format("The set of parameters of an instance cannot be null", type.getName());
			logger.debug(msg);
			throw new IllegalArgumentException(msg);
		}
		final String propertyAsString = (String) this.converter.convertObjectValueToDataValue(propertyDoc, null);
		final T property = type.newInstance();
		property.setName("xml");
		property.setType("xml");
		property.setValue(propertyAsString);

		return property;
	}

	/* Service Template Instances */
	/******************************/
	public Collection<ServiceTemplateInstance> getServiceTemplateInstances(final String serviceTemplate) {
		return this.getServiceTemplateInstances(QName.valueOf(serviceTemplate));
	}

	public Collection<ServiceTemplateInstance> getServiceTemplateInstances(final QName serviceTemplate) {
		logger.debug("Requesting instances of ServiceTemplate \"{}\"...", serviceTemplate);
		return this.serviceTemplateInstanceRepository.findByTemplateId(serviceTemplate);
	}

	public ServiceTemplateInstance getServiceTemplateInstance(final Long id) {
		logger.debug("Requesting service template instance <{}>...", id);
		Optional<ServiceTemplateInstance> instance = this.serviceTemplateInstanceRepository.find(id);

		if (instance.isPresent()) {
			return instance.get();
		}

		logger.debug("Service Template Instance <" + id + "> not found.");
		throw new NotFoundException("Service Template Instance <" + id + "> not found.");
	}

	public ServiceTemplateInstanceState getServiceTemplateInstanceState(final Long id) {
		final ServiceTemplateInstance service = this.getServiceTemplateInstance(id);

		return service.getState();
	}

	public void setServiceTemplateInstanceState(final Long id, String state)
			throws NotFoundException, IllegalArgumentException {

		ServiceTemplateInstanceState newState;
		try {
			newState = ServiceTemplateInstanceState.valueOf(state);
		} catch (Exception e) {
			String msg = String.format("The given state {} is an illegal service template instance state.", state);
			logger.debug(msg);
			throw new IllegalArgumentException(msg, e);
		}

		final ServiceTemplateInstance service = this.getServiceTemplateInstance(id);
		service.setState(newState);
		this.serviceTemplateInstanceRepository.update(service);
	}

	public Document getServiceTemplateInstanceProperties(final Long id) throws NotFoundException {
		final ServiceTemplateInstance service = this.getServiceTemplateInstance(id);
		final Optional<ServiceTemplateInstanceProperty> firstProp = service.getProperties().stream().findFirst();

		if (firstProp.isPresent()) {
			return this.convertPropertyToDocument(firstProp.get());
		}

		String msg = String.format("No properties are found for the service template instance <{}>", id);
		logger.debug(msg);

		throw new NotFoundException(msg);
	}

	public void setServiceTemplateInstanceProperties(final Long id, Document properties) throws ReflectiveOperationException  {
		final ServiceTemplateInstance service = this.getServiceTemplateInstance(id);

		try {
			final ServiceTemplateInstanceProperty property = this.convertDocumentToProperty(properties,
					ServiceTemplateInstanceProperty.class);
			service.addProperty(property);
			this.serviceTemplateInstanceRepository.update(service);
		} catch (InstantiationException | IllegalAccessException e) {// This is not supposed to happen at all!
			final String msg = String.format("An error occurred while instantiating an instance of the {} class.",
					ServiceTemplateInstanceProperty.class);
			logger.debug(msg);
			throw e;
		}

	}

	public void deleteServiceTemplateInstance(final Long instanceId) {
		final ServiceTemplateInstance instance = this.getServiceTemplateInstance(instanceId); // throws exception if not found
		this.serviceTemplateInstanceRepository.remove(instance);
	}
	
	/* Node Template Instances */
	/******************************/
	public Collection<NodeTemplateInstance> getNodeTemplateInstances(final QName nodeTemplateQName) {
		logger.debug("Requesting instances of NodeTemplate \"{}\"...", nodeTemplateQName);
		return this.nodeTemplateInstanceRepository.findByTemplateId(nodeTemplateQName);
	}

	public NodeTemplateInstance resolveNodeTemplateInstance(final String serviceTemplateQName,
			final String nodeTemplateId, final Long id) {
		// We only need to check that the instance belongs to the template, the rest is
		// guaranteed while this is a sub-resource
		final QName nodeTemplateQName = new QName(QName.valueOf(serviceTemplateQName).getNamespaceURI(), nodeTemplateId);
		final NodeTemplateInstance instance = getNodeTemplateInstance(id);

		if (!instance.getTemplateId().equals(nodeTemplateQName)) {
			logger.info("Node template instance <{}> could not be found", id);
			throw new NotFoundException(String.format("Node template instance <{}> could not be found", id));
		}

		return instance;
	}
	
	private NodeTemplateInstance getNodeTemplateInstance(final Long id) {
		logger.debug("Requesting node template instance <{}>...", id);
		Optional<NodeTemplateInstance> instance = this.nodeTemplateInstanceRepository.find(id);

		if (instance.isPresent()) {
			return instance.get();
		}

		logger.debug("Node Template Instance <" + id + "> not found.");
		throw new NotFoundException("Node Template Instance <" + id + "> not found.");
	}

	public NodeTemplateInstanceState getNodeTemplateInstanceState(final String serviceTemplateQName,
			final String nodeTemplateId, final Long id) {
		final NodeTemplateInstance node = this.resolveNodeTemplateInstance(serviceTemplateQName, nodeTemplateId, id);

		return node.getState();
	}

	public void setNodeTemplateInstanceState(final String serviceTemplateQName,
			final String nodeTemplateId, final Long id, String state)
			throws NotFoundException, IllegalArgumentException {

		NodeTemplateInstanceState newState;
		try {
			newState = NodeTemplateInstanceState.valueOf(state);
		} catch (Exception e) {
			String msg = String.format("The given state {} is an illegal node template instance state.", state);
			logger.debug(msg);
			throw new IllegalArgumentException(msg, e);
		}

		final NodeTemplateInstance node = this.resolveNodeTemplateInstance(serviceTemplateQName, nodeTemplateId, id);
		node.setState(newState);
		this.nodeTemplateInstanceRepository.update(node);
	}

	public Document getNodeTemplateInstanceProperties(final String serviceTemplateQName,
			final String nodeTemplateId, final Long id) throws NotFoundException {
		final NodeTemplateInstance node = this.resolveNodeTemplateInstance(serviceTemplateQName, nodeTemplateId, id);
		final Optional<NodeTemplateInstanceProperty> firstProp = node.getProperties().stream().findFirst();

		if (firstProp.isPresent()) {
			return this.convertPropertyToDocument(firstProp.get());
		}

		String msg = String.format("No properties are found for the node template instance <{}>", id);
		logger.debug(msg);

		throw new NotFoundException(msg);
	}

	public void setNodeTemplateInstanceProperties(final String serviceTemplateQName,
			final String nodeTemplateId, final Long id, Document properties) throws ReflectiveOperationException  {
		final NodeTemplateInstance node = this.resolveNodeTemplateInstance(serviceTemplateQName, nodeTemplateId, id);

		try {
			final NodeTemplateInstanceProperty property = this.convertDocumentToProperty(properties,
					NodeTemplateInstanceProperty.class);
			node.addProperty(property);
			this.nodeTemplateInstanceRepository.update(node);
		} catch (InstantiationException | IllegalAccessException e) {// This is not supposed to happen at all!
			final String msg = String.format("An error occurred while instantiating an instance of the {} class.",
					NodeTemplateInstanceProperty.class);
			logger.debug(msg);
			throw e;
		}

	}
	
	public NodeTemplateInstance createNewNodeTemplateInstance(final String csarId, final String serviceTemplateQNameAsString, String nodeTemplateId, final Long serviceTemplateInstanceId) throws InstantiationException, IllegalAccessException, IllegalArgumentException {
		final QName serviceTemplateQName = QName.valueOf(serviceTemplateQNameAsString);
		final NodeTemplateInstance newInstance = new NodeTemplateInstance();
		final NodeTemplateDTO dto = this.nodeTemplateService.getNodeTemplateById(csarId, serviceTemplateQName, nodeTemplateId);
		
		// Properties
		// We set the properties of the template as initial properties
		final Document propertiesAsDocument = this.nodeTemplateService.getPropertiesOfNodeTemplate(csarId, serviceTemplateQName, nodeTemplateId);
		
		if(propertiesAsDocument != null) {
			final NodeTemplateInstanceProperty properties = this.convertDocumentToProperty(propertiesAsDocument, NodeTemplateInstanceProperty.class);
			newInstance.addProperty(properties);
		}
		// State
		newInstance.setState(NodeTemplateInstanceState.INITIAL);
		// Template
		newInstance.setTemplateId(new QName(serviceTemplateQName.getNamespaceURI(), nodeTemplateId));
		// Type
		newInstance.setTemplateType(QName.valueOf(dto.getNodeType()));
		// ServiceTemplateInstance
		final ServiceTemplateInstance serviceTemplateInstance = this.getServiceTemplateInstance(serviceTemplateInstanceId);
		
		if(!serviceTemplateInstance.getTemplateId().equals(serviceTemplateQName)) {
			final String msg = String.format("Service template instance id <{}> does not belong to service template: {}", serviceTemplateInstanceId, serviceTemplateQName);
			logger.debug(msg);
			throw new IllegalArgumentException(msg);
		}
		newInstance.setServiceTemplateInstance(serviceTemplateInstance);
		
		
		this.nodeTemplateInstanceRepository.add(newInstance);
		
		return newInstance;
	}

	public void deleteNodeTemplateInstance(final String serviceTemplateQName,
			final String nodeTemplateId, final Long id) {
		final NodeTemplateInstance instance = this.resolveNodeTemplateInstance(serviceTemplateQName, nodeTemplateId, id); // throws exception if not found
		this.nodeTemplateInstanceRepository.remove(instance);
	}
	
	/* Relationship Template Instances */
	/***********************************/
	public Collection<RelationshipTemplateInstance> getRelationshipTemplateInstances(final QName relationshipTemplateQName) {
		logger.debug("Requesting instances of RelationshipTemplate \"{}\"...", relationshipTemplateQName);
		return this.relationshipTemplateInstanceRepository.findByTemplateId(relationshipTemplateQName);
	}
	
	/**
	 * Gets a reference to the relationship template instance. Ensures that the
	 * instance actually belongs to the relationship template.
	 * 
	 * @param instanceId
	 * @param templateId
	 * @return
	 * @throws NotFoundException
	 *             if the instance does not belong to the relationship template
	 */
	public RelationshipTemplateInstance resolveRelationshipTemplateInstance(final String serviceTemplateQName, final String relationshipTemplateId, final Long instanceId) throws NotFoundException {
		// We only need to check that the instance belongs to the template, the rest is
		// guaranteed while this is a sub-resource
		final RelationshipTemplateInstance instance = getRelationshipTemplateInstanc(instanceId);
		final QName relationshipTemplateQName = new QName(QName.valueOf(serviceTemplateQName).getNamespaceURI(), relationshipTemplateId);
		if (!instance.getTemplateId().equals(relationshipTemplateQName)) {
			logger.info("Relationship template instance <{}> could not be found", instanceId);
			throw new NotFoundException(
					String.format("Relationship template instance <{}> could not be found", instanceId));
		}

		return instance;
	}

	private RelationshipTemplateInstance getRelationshipTemplateInstanc(final Long id) {
		logger.debug("Requesting relationship template instance <{}>...", id);
		Optional<RelationshipTemplateInstance> instance = this.relationshipTemplateInstanceRepository.find(id);

		if (instance.isPresent()) {
			return instance.get();
		}

		logger.debug("Relationship Template Instance <" + id + "> not found.");
		throw new NotFoundException("Relationship Template Instance <" + id + "> not found.");
	}

	
	public RelationshipTemplateInstanceState getRelationshipTemplateInstanceState(final String serviceTemplateQName, final String relationshipTemplateId, final Long id) {
		final RelationshipTemplateInstance relationship = this.resolveRelationshipTemplateInstance(serviceTemplateQName, relationshipTemplateId, id);

		return relationship.getState();
	}

	public void setRelationshipTemplateInstanceState(final String serviceTemplateQName, final String relationshipTemplateId, final Long id, String state)
			throws NotFoundException, IllegalArgumentException {

		RelationshipTemplateInstanceState newState;
		try {
			newState = RelationshipTemplateInstanceState.valueOf(state);
		} catch (Exception e) {
			String msg = String.format("The given state {} is an illegal relationship template instance state.", state);
			logger.debug(msg);
			throw new IllegalArgumentException(msg, e);
		}

		final RelationshipTemplateInstance relationship = this.resolveRelationshipTemplateInstance(serviceTemplateQName, relationshipTemplateId, id);
		relationship.setState(newState);
		this.relationshipTemplateInstanceRepository.update(relationship);
	}

	public Document getRelationshipTemplateInstanceProperties(final String serviceTemplateQName, final String relationshipTemplateId, final Long id) throws NotFoundException {
		final RelationshipTemplateInstance relationship = this.resolveRelationshipTemplateInstance(serviceTemplateQName, relationshipTemplateId, id);
		final Optional<RelationshipTemplateInstanceProperty> firstProp = relationship.getProperties().stream().findFirst();

		if (firstProp.isPresent()) {
			return this.convertPropertyToDocument(firstProp.get());
		}

		String msg = String.format("No properties are found for the relationship template instance <{}>", id);
		logger.debug(msg);

		throw new NotFoundException(msg);
	}

	public void setRelationshipTemplateInstanceProperties(final String serviceTemplateQName, final String relationshipTemplateId, final Long id, Document properties) throws ReflectiveOperationException  {
		final RelationshipTemplateInstance relationship = this.resolveRelationshipTemplateInstance(serviceTemplateQName, relationshipTemplateId, id);

		try {
			final RelationshipTemplateInstanceProperty property = this.convertDocumentToProperty(properties,
					RelationshipTemplateInstanceProperty.class);
			relationship.addProperty(property);
			this.relationshipTemplateInstanceRepository.update(relationship);
		} catch (InstantiationException | IllegalAccessException e) {// This is not supposed to happen at all!
			final String msg = String.format("An error occurred while instantiating an instance of the {} class.",
					RelationshipTemplateInstanceProperty.class);
			logger.debug(msg);
			throw e;
		}

	}

	public RelationshipTemplateInstance createNewRelationshipTemplateInstance(final String csarId, final String serviceTemplateId, final String relationshipTemplateId, final Long sourceNodeInstanceId, final Long targetNodeInstanceId ) throws InstantiationException, IllegalAccessException, IllegalArgumentException {
		final QName serviceTemplateQName = QName.valueOf(serviceTemplateId);
		final RelationshipTemplateInstance newInstance = new RelationshipTemplateInstance();
		final RelationshipTemplateDTO dto = this.relationshipTemplateService.getRelationshipTemplateById(csarId, serviceTemplateQName, relationshipTemplateId);
		
		// Properties
		// We set the properties of the template as initial properties
		final Document propertiesAsDocument = this.relationshipTemplateService.getPropertiesOfRelationshipTemplate(csarId, serviceTemplateQName, relationshipTemplateId);
		
		if(propertiesAsDocument != null) {
			final RelationshipTemplateInstanceProperty properties = this.convertDocumentToProperty(propertiesAsDocument, RelationshipTemplateInstanceProperty.class);
			newInstance.addProperty(properties);
		}
		// State
		newInstance.setState(RelationshipTemplateInstanceState.INITIAL);
		// Template
		newInstance.setTemplateId(new QName(serviceTemplateQName.getNamespaceURI(), relationshipTemplateId));
		// Type
		newInstance.setTemplateType(QName.valueOf(dto.getRelationshipType()));
		// Source node instance
		newInstance.setSource(this.getNodeTemplateInstance(sourceNodeInstanceId));
		// Target node instance
		newInstance.setTarget(this.getNodeTemplateInstance(targetNodeInstanceId));
		
		this.relationshipTemplateInstanceRepository.add(newInstance);
		
		return newInstance;
	}

	public void deleteRelationshipTemplateInstance(final String serviceTemplateQName, final String relationshipTemplateId, final Long instanceId) {
		final RelationshipTemplateInstance instance = this.resolveRelationshipTemplateInstance(serviceTemplateQName, relationshipTemplateId, instanceId); // throws exception if not found
		this.relationshipTemplateInstanceRepository.remove(instance);
	}
	/* Service Injection */
	/*********************/
	
	public void setRelationshipTemplateService(RelationshipTemplateService relationshipTemplateService) {
		this.relationshipTemplateService = relationshipTemplateService;
	}

	public void setNodeTemplateService(NodeTemplateService nodeTemplateService) {
		this.nodeTemplateService = nodeTemplateService;
	}
	
	
	
}
