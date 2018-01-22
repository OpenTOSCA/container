package org.opentosca.container.api.service;

import java.util.Collection;
import java.util.Optional;

import javax.ws.rs.NotFoundException;
import javax.xml.namespace.QName;

import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.repository.NodeTemplateInstanceRepository;
import org.opentosca.container.core.next.repository.ServiceTemplateInstanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Allows access to instance information for service templates and node templates.
 */
public class InstanceService {
	
	private static Logger logger = LoggerFactory.getLogger(InstanceService.class);
	
	private final ServiceTemplateInstanceRepository serviecTemplateInstanceRepository = new ServiceTemplateInstanceRepository();
	private final NodeTemplateInstanceRepository nodeTemplateInstanceRepository = new NodeTemplateInstanceRepository();
	
	public Collection<ServiceTemplateInstance> getServiceTemplateInstances(final String serviceTemplate) {
		return this.getServiceTemplateInstances(QName.valueOf(serviceTemplate));
	}

	public Collection<ServiceTemplateInstance> getServiceTemplateInstances(final QName serviceTemplate) {
		logger.debug("Requesting instances of ServiceTemplate \"{}\"...", serviceTemplate);
		return this.serviecTemplateInstanceRepository.findByTemplateId(serviceTemplate);
	}

	public ServiceTemplateInstance getServiceTemplateInstance(final Integer id) {
		return this.getServiceTemplateInstance(new Long(id.intValue()));
	}

	public ServiceTemplateInstance getServiceTemplateInstance(final Long id) {
		logger.debug("Requesting service template instance <{}>...", id);
		Optional<ServiceTemplateInstance> instance = this.serviecTemplateInstanceRepository.find(id);

		if (instance.isPresent()) {
			return instance.get();
		}
		
		logger.debug("Service Template Instance <" + id + "> not found.");
		throw new NotFoundException("Service Template Instance <" + id + "> not found.");
	}
	
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
	
	
}
