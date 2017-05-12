package org.opentosca.container.api.service;

import java.util.List;

import javax.ws.rs.NotFoundException;
import javax.xml.namespace.QName;

import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.model.instance.ServiceInstance;
import org.opentosca.container.core.service.IInstanceDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InstanceService {
	
	private static Logger logger = LoggerFactory.getLogger(InstanceService.class);
	
	private IInstanceDataService instanceDataService;
	
	
	public List<ServiceInstance> getServiceTemplateInstances(final CSARID id, final String serviceTemplateName) {
		return this.getServiceTemplateInstances(id, QName.valueOf(serviceTemplateName));
	}

	public List<ServiceInstance> getServiceTemplateInstances(final CSARID id, final QName serviceTemplateName) {
		logger.debug("Requesting instances of ServiceTemplate \"{}\" in CSAR \"{}\"...", serviceTemplateName, id);
		return this.instanceDataService.getServiceInstancesWithDetails(id, serviceTemplateName, null);
	}

	public ServiceInstance getServiceTemplateInstance(final Integer id, final CSARID csarId, final String serviceTemplateName) {
		return this.getServiceTemplateInstance(id, csarId, QName.valueOf(serviceTemplateName));
	}

	public ServiceInstance getServiceTemplateInstance(final Integer id, final CSARID csarId, final QName serviceTemplateName) {
		logger.debug("Requesting instance <{}> of ServiceTemplate \"{}\" in CSAR \"{}\"...", id, serviceTemplateName, csarId);
		final List<ServiceInstance> instances = this.instanceDataService.getServiceInstancesWithDetails(csarId, serviceTemplateName, id);
		if (instances.size() == 1) {
			return instances.get(0);
		}
		throw new NotFoundException("Instance <" + id + "> of ServiceTemplate \"" + serviceTemplateName + "\" in CSAR \"" + csarId + "\" not found");
	}

	public void setInstanceDataService(final IInstanceDataService instanceDataService) {
		this.instanceDataService = instanceDataService;
	}
}
