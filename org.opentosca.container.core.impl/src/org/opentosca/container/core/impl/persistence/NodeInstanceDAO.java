package org.opentosca.container.core.impl.persistence;

import java.net.URI;
import java.util.List;

import javax.persistence.Query;
import javax.xml.namespace.QName;

import org.opentosca.container.core.model.instance.IdConverter;
import org.opentosca.container.core.model.instance.NodeInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 * Data Access Object for NodeInstances
 */
public class NodeInstanceDAO extends AbstractDAO {
	
	// Logging
	private final static Logger LOG = LoggerFactory.getLogger(NodeInstanceDAO.class);


	public void deleteNodeInstance(final NodeInstance si) {
		this.init();
		this.em.getTransaction().begin();
		this.em.remove(si);
		this.em.getTransaction().commit();
		NodeInstanceDAO.LOG.debug("Deleted NodeInstance with ID: " + si.getNodeInstanceID());
		
	}
	
	public void saveNodeInstance(final NodeInstance nodeInstance) {
		this.init();
		
		this.em.getTransaction().begin();
		this.em.persist(nodeInstance);
		this.em.getTransaction().commit();
		NodeInstanceDAO.LOG.debug("Stored NodeInstance: " + nodeInstance + " successful!");
		
	}
	
	/**
	 * this method wraps the setting/saving of the properties
	 *
	 * @param nodeInstance
	 * @param properties
	 */
	public void setProperties(final NodeInstance nodeInstance, final Document properties) {
		this.init();
		nodeInstance.setProperties(properties);
		NodeInstanceDAO.LOG.debug("Invoke of saving nodeInstance: " + nodeInstance.getNodeInstanceID() + " to update properties");
		this.saveNodeInstance(nodeInstance);
	}
	
	/**
	 * this method wraps the setting/saving of the state
	 *
	 * @param nodeInstance
	 * @param state to be set
	 */
	public void setState(final NodeInstance nodeInstance, final QName state) {
		this.init();
		nodeInstance.setState(state);
		NodeInstanceDAO.LOG.debug("Invoke of saving nodeInstance: " + nodeInstance.getNodeInstanceID() + " to update state");
		this.saveNodeInstance(nodeInstance);
	}
	
	public List<NodeInstance> getNodeInstances(final URI serviceInstanceID, final QName nodeTemplateID, final String nodeTemplateName, final URI nodeInstanceID) {
		this.init();
		
		/**
		 * Create Query to retrieve NodeInstances
		 *
		 * @see NodeInstance#getNodeInstances
		 */
		final Query getNodeInstancesQuery = this.em.createNamedQuery(NodeInstance.getNodeInstances);
		
		Integer internalID = null;
		if (nodeInstanceID != null) {
			internalID = IdConverter.nodeInstanceUriToID(nodeInstanceID);
		}
		
		Integer internalServiceInstanceID = null;
		if (serviceInstanceID != null) {
			// The serviceInstanceID in this case has the following format:
			// http://{hostname}:1337/containerapi/CSARs/{csar}/ServiceTemplates/{template}/Instances/{id}
			// We gonna split the string on character "/" in order to extract
			// the instance ID out of it, which is stored at the end of the
			// resulting string array.
			final String[] parts = serviceInstanceID.getPath().split("/");
			internalServiceInstanceID = Integer.valueOf(parts[parts.length - 1]);
			
			// This won't work since IdConverter expects a different URL
			// pattern (/instancedata/serviceInstances), which isn't given in
			// this case.
			// internalServiceInstanceID =
			// IdConverter.serviceInstanceUriToID(serviceInstanceID);
		}
		
		// Set Parameters for the Query
		getNodeInstancesQuery.setParameter("internalID", internalID);
		getNodeInstancesQuery.setParameter("nodeTemplateID", ((nodeTemplateID != null) ? nodeTemplateID.toString() : null));
		getNodeInstancesQuery.setParameter("nodeTemplateName", nodeTemplateName);
		getNodeInstancesQuery.setParameter("internalServiceInstanceID", internalServiceInstanceID);
		@SuppressWarnings("unchecked")
		final List<NodeInstance> queryResults = getNodeInstancesQuery.getResultList();
		
		return queryResults;
	}
	
}