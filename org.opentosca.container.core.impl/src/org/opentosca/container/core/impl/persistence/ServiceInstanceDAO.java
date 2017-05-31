package org.opentosca.container.core.impl.persistence;

import java.net.URI;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.xml.namespace.QName;

import org.opentosca.container.core.impl.service.InstanceDataServiceImpl;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.model.instance.IdConverter;
import org.opentosca.container.core.model.instance.ServiceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data Access Object for ServiceInstances
 */
public class ServiceInstanceDAO extends AbstractDAO {
	
	// Logging
	private final static Logger LOG = LoggerFactory.getLogger(ServiceInstanceDAO.class);
	
	
	public void deleteServiceInstance(final ServiceInstance si) {
		this.init();
		this.em.getTransaction().begin();
		this.em.remove(si);
		this.em.getTransaction().commit();
		ServiceInstanceDAO.LOG.debug("Deleted ServiceInstance with ID: " + si.getServiceInstanceID());
		
	}
	
	public void storeServiceInstance(final ServiceInstance serviceInstance) {
		this.init();
		
		if (null == this.em) {
			System.out.println("EM is null");
		}
		try {
			if (null == this.em.getTransaction()) { // FIXME sometimes null
													// pointer
				// exception
				System.out.println("EM transaction is null");
			}
		} catch (final NullPointerException e) {
			// maybe it works a second later ... yes, i am serious
			try {
				Thread.sleep(1000);
			} catch (final InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		
		this.em.getTransaction().begin();
		this.em.persist(serviceInstance);
		this.em.getTransaction().commit();
		ServiceInstanceDAO.LOG.debug("Stored ServiceInstance for Service Template: " + serviceInstance.getServiceTemplateID().getNamespaceURI() + " : " + serviceInstance.getServiceTemplateID().getLocalPart() + " successful!");
		
	}
	
	public List<ServiceInstance> getServiceInstances(final URI serviceInstanceID, final String serviceTemplateName, final QName serviceTemplateID) {
		this.init();
		
		// TODO: Use a query builder !!!
		// final Query getServiceInstancesQuery =
		// this.em.createNamedQuery(ServiceInstance.getServiceInstances);
		final TypedQuery<ServiceInstance> getServiceInstancesQuery;
		
		Integer internalID = null;
		if (serviceInstanceID != null) {
			internalID = IdConverter.serviceInstanceUriToID(serviceInstanceID);
		}

		// String serviceTemplateID_String = null;
		// if (serviceTemplateID != null) {
		// serviceTemplateID_String = serviceTemplateID.toString();
		// }
		
		if (internalID == null) {
			getServiceInstancesQuery = this.em.createQuery("FROM ServiceInstance s WHERE s.serviceTemplateName = :serviceTemplateName AND s.serviceTemplateID = :serviceTemplateID", ServiceInstance.class);
			getServiceInstancesQuery.setParameter("serviceTemplateName", serviceTemplateName);
			getServiceInstancesQuery.setParameter("serviceTemplateID", serviceTemplateID);
		} else {
			getServiceInstancesQuery = this.em.createQuery("FROM ServiceInstance s WHERE s.id = :id", ServiceInstance.class);
			getServiceInstancesQuery.setParameter("id", internalID);
		}
		
		final List<ServiceInstance> queryResults = getServiceInstancesQuery.getResultList();
		
		return queryResults;
		
	}
	
	public List<ServiceInstance> getServiceInstances(final CSARID csarId, final QName serviceTemplateId, final Integer serviceTemplateInstanceID) {
		
		this.init();
		
		LOG.debug("Try to get Service Template instance objects from persistence for CSAR \"{}\" Service Template \"{}\" Instance Id \"{}\"", csarId, serviceTemplateId, serviceTemplateInstanceID);
		
		// final Query getServiceInstancesQuery =
		// this.em.createNamedQuery(ServiceInstance.getServiceInstances);
		// TODO: Use a query builder or something else, but please refactor
		// this!!
		
		final String serviceTemplateName = InstanceDataServiceImpl.toscaEngineService.getNameOfReference(csarId, serviceTemplateId);
		
		TypedQuery<ServiceInstance> getServiceInstancesQuery = null;
		
		if (serviceTemplateInstanceID == null) {
			getServiceInstancesQuery = this.em.createQuery("FROM ServiceInstance s WHERE s.serviceTemplateName = :serviceTemplateName AND s.serviceTemplateID = :serviceTemplateID", ServiceInstance.class);
			getServiceInstancesQuery.setParameter("serviceTemplateName", serviceTemplateName);
			getServiceInstancesQuery.setParameter("serviceTemplateID", serviceTemplateId);
		} else {
			getServiceInstancesQuery = this.em.createQuery("FROM ServiceInstance s WHERE s.id = :id", ServiceInstance.class);
			getServiceInstancesQuery.setParameter("id", serviceTemplateInstanceID);
		}

		final List<ServiceInstance> queryResults = getServiceInstancesQuery.getResultList();
		
		LOG.debug("Found {} instance objects for Service Template instance of CSAR \"{}\" Service Template \"{}\" Instance Id \"{}\"", queryResults.size(), csarId, serviceTemplateId, serviceTemplateInstanceID);
		
		return queryResults;
	}
	
}
