package org.opentosca.container.api.service;

import java.util.Collection;
import java.util.Optional;

import javax.ws.rs.NotFoundException;
import javax.xml.namespace.QName;

import org.opentosca.container.core.common.jpa.DocumentConverter;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.NodeTemplateInstanceProperty;
import org.opentosca.container.core.next.model.NodeTemplateInstanceState;
import org.opentosca.container.core.next.model.Property;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.model.ServiceTemplateInstanceProperty;
import org.opentosca.container.core.next.model.ServiceTemplateInstanceState;
import org.opentosca.container.core.next.repository.NodeTemplateInstanceRepository;
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
	private final DocumentConverter converter = new DocumentConverter();

	private Document convertPropertyToDocument(Property property) {
		return (Document) converter.convertDataValueToObjectValue(property.getValue(), null);
	}

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

	public ServiceTemplateInstance getServiceTemplateInstance(final Integer id) {
		return this.getServiceTemplateInstance(new Long(id.intValue()));
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

	public ServiceTemplateInstanceState getServiceTemplateInstanceState(final Integer id) {
		final ServiceTemplateInstance service = this.getServiceTemplateInstance(id);

		return service.getState();
	}

	public void setServiceTemplateInstanceState(final Integer id, String state)
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

	public Document getServiceTemplateInstanceProperties(final Integer id) throws NotFoundException {
		final ServiceTemplateInstance service = this.getServiceTemplateInstance(id);
		final Optional<ServiceTemplateInstanceProperty> firstProp = service.getProperties().stream().findFirst();

		if (firstProp.isPresent()) {
			return this.convertPropertyToDocument(firstProp.get());
		}

		String msg = String.format("No properties are found for the service template instance <{}>", id);
		logger.debug(msg);

		throw new NotFoundException(msg);
	}

	public void setServiceTemplateInstanceProperties(final Integer id, Document properties) throws ReflectiveOperationException  {
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

	/* Node Template Instances */
	/******************************/
	public Collection<NodeTemplateInstance> getNodeTemplateInstances(final String nodeTemplateQName) {
		return this.getNodeTemplateInstances(QName.valueOf(nodeTemplateQName));
	}

	public Collection<NodeTemplateInstance> getNodeTemplateInstances(final QName nodeTemplateQName) {
		logger.debug("Requesting instances of NodeTemplate \"{}\"...", nodeTemplateQName);
		return this.nodeTemplateInstanceRepository.findByTemplateId(nodeTemplateQName);
	}

	public NodeTemplateInstance getNodeTemplateInstance(final Integer id) {
		return this.getNodeTemplateInstance(new Long(id.intValue()));
	}

	public NodeTemplateInstance getNodeTemplateInstance(final Long id) {
		logger.debug("Requesting node template instance <{}>...", id);
		Optional<NodeTemplateInstance> instance = this.nodeTemplateInstanceRepository.find(id);

		if (instance.isPresent()) {
			return instance.get();
		}

		logger.debug("Node Template Instance <" + id + "> not found.");
		throw new NotFoundException("Node Template Instance <" + id + "> not found.");
	}

	public NodeTemplateInstanceState getNodeTemplateInstanceState(final Integer id) {
		final NodeTemplateInstance node = this.getNodeTemplateInstance(id);

		return node.getState();
	}

	public void setNodeTemplateInstanceState(final Integer id, String state)
			throws NotFoundException, IllegalArgumentException {

		NodeTemplateInstanceState newState;
		try {
			newState = NodeTemplateInstanceState.valueOf(state);
		} catch (Exception e) {
			String msg = String.format("The given state {} is an illegal node template instance state.", state);
			logger.debug(msg);
			throw new IllegalArgumentException(msg, e);
		}

		final NodeTemplateInstance node = this.getNodeTemplateInstance(id);
		node.setState(newState);
		this.nodeTemplateInstanceRepository.update(node);
	}

	public Document getNodeTemplateInstanceProperties(final Integer id) throws NotFoundException {
		final NodeTemplateInstance node = this.getNodeTemplateInstance(id);
		final Optional<NodeTemplateInstanceProperty> firstProp = node.getProperties().stream().findFirst();

		if (firstProp.isPresent()) {
			return this.convertPropertyToDocument(firstProp.get());
		}

		String msg = String.format("No properties are found for the node template instance <{}>", id);
		logger.debug(msg);

		throw new NotFoundException(msg);
	}

	public void setNodeTemplateInstanceProperties(final Integer id, Document properties) throws ReflectiveOperationException  {
		final NodeTemplateInstance node = this.getNodeTemplateInstance(id);

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
}
