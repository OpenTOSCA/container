/**
 * 
 */
package org.opentosca.instancedata.service.impl.persistence;

import java.net.URI;
import java.util.List;

import javax.persistence.Query;
import javax.xml.namespace.QName;

import org.opentosca.model.instancedata.IdConverter;
import org.opentosca.model.instancedata.ServiceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data Access Object for ServiceInstances
 * 
 * @author Marcus Eisele (marcus.eisele@gmail.com)
 * 
 */
public class ServiceInstanceDAO extends AbstractDAO {
	
	
	// Logging
	private final static Logger LOG = LoggerFactory.getLogger(ServiceInstanceDAO.class);
	
	
	public void deleteServiceInstance(ServiceInstance si) {
		init();
		em.getTransaction().begin();
		em.remove(si);
		em.getTransaction().commit();
		ServiceInstanceDAO.LOG.debug("Deleted ServiceInstance with ID: " + si.getServiceInstanceID());
		
	}
	
	public void storeServiceInstance(ServiceInstance serviceInstance) {
		init();
		
		if (null == em) {
			System.out.println("EM is null");
		}
		if (null == em.getTransaction()) {
			System.out.println("EM transaction is null");
		}
		
		em.getTransaction().begin();
		em.persist(serviceInstance);
		em.getTransaction().commit();
		ServiceInstanceDAO.LOG.debug("Stored ServiceInstance for Service Template: " + serviceInstance.getServiceTemplateID().getNamespaceURI() + " : " + serviceInstance.getServiceTemplateID().getLocalPart() + " successful!");
		
	}
	
	public List<ServiceInstance> getServiceInstances(URI serviceInstanceID, String serviceTemplateName, QName serviceTemplateID) {
		init();
		
		/**
		 * Create Query to retrieve ServiceInstances
		 * 
		 * @see ServiceInstance#getServiceInstances
		 */
		Query getServiceInstancesQuery = em.createNamedQuery(ServiceInstance.getServiceInstances);
		
		Integer internalID = null;
		if (serviceInstanceID != null) {
			internalID = IdConverter.serviceInstanceUriToID(serviceInstanceID);
		}
		
		String serviceTemplateID_String = null;
		if (serviceTemplateID != null) {
			serviceTemplateID_String = serviceTemplateID.toString();
		}
		
		// Set Parameters for the Query
		// getServiceInstancesQuery.setParameter("param", param);
		getServiceInstancesQuery.setParameter("id", internalID);
		getServiceInstancesQuery.setParameter("serviceTemplateName", serviceTemplateName);
		getServiceInstancesQuery.setParameter("serviceTemplateID", serviceTemplateID_String);
		
		// getServiceInstancesQuery.setParameter("serviceTemplateID",
		// serviceTemplateID);
		// getServiceInstancesQuery.setParameter("serviceTemplateNamespace",
		// serviceTemplateNamespace);
		// Get Query-Results (ServiceInstances) and add them to the result list.
		@SuppressWarnings("unchecked")
		// Result can only be a ServiceInstance
		List<ServiceInstance> queryResults = getServiceInstancesQuery.getResultList();
		return queryResults;
		
	}
	
}
