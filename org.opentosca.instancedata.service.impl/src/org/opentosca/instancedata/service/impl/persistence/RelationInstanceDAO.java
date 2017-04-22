package org.opentosca.instancedata.service.impl.persistence;

import java.net.URI;
import java.util.List;

import javax.persistence.Query;
import javax.xml.namespace.QName;

import org.opentosca.model.instancedata.IdConverter;
import org.opentosca.model.instancedata.NodeInstance;
import org.opentosca.model.instancedata.RelationInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 * Data Access Object for NodeInstances
 *
 * @author Marcus Eisele (marcus.eisele@gmail.com)
 *
 */
public class RelationInstanceDAO extends AbstractDAO {

	// Logging
	private final static Logger LOG = LoggerFactory.getLogger(RelationInstanceDAO.class);
	
	
	public void deleteRelationInstance(RelationInstance si) {
		this.init();
		this.em.getTransaction().begin();
		this.em.remove(si);
		this.em.getTransaction().commit();
		RelationInstanceDAO.LOG.debug("Deleted NodeInstance with ID: " + si.getRelationInstanceID());

	}

	public void saveRelationInstance(RelationInstance relationInstance) {
		this.init();

		this.em.getTransaction().begin();
		this.em.persist(relationInstance);
		this.em.getTransaction().commit();
		RelationInstanceDAO.LOG.debug("Stored NodeInstance: " + relationInstance + " successful!");

	}

	/**
	 * this method wraps the setting/saving of the properties
	 *
	 * @param nodeInstance
	 * @param properties
	 */
	public void setProperties(RelationInstance relationInstance, Document properties) {
		this.init();
		relationInstance.setProperties(properties);
		RelationInstanceDAO.LOG.debug("Invoke of saving nodeInstance: " + relationInstance.getRelationInstanceID() + " to update properties");
		this.saveRelationInstance(relationInstance);
	}

	/**
	 * this method wraps the setting/saving of the state
	 *
	 * @param relationInstance
	 * @param state to be set
	 */
	public void setState(RelationInstance relationInstance, QName state) {
		this.init();
		relationInstance.setState(state);
		RelationInstanceDAO.LOG.debug("Invoke of saving nodeInstance: " + relationInstance.getRelationInstanceID() + " to update state");
		this.saveRelationInstance(relationInstance);
	}

	public List<RelationInstance> getRelationInstances(URI serviceInstanceID, QName relationshipTemplateID, String relationshipTemplateName, URI relationInstanceID) {
		this.init();

		/**
		 * Create Query to retrieve NodeInstances
		 *
		 * @see NodeInstance#getNodeInstances
		 */
		Query getRelationInstancesQuery = this.em.createNamedQuery(RelationInstance.getRelationInstances);

		Integer internalID = null;
		if (relationInstanceID != null) {
			internalID = IdConverter.relationInstanceUriToID(relationInstanceID);
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
		getRelationInstancesQuery.setParameter("internalID", internalID);
		getRelationInstancesQuery.setParameter("relationshipTemplateID", ((relationshipTemplateID != null) ? relationshipTemplateID.toString() : null));
		getRelationInstancesQuery.setParameter("relationshipTemplateName", relationshipTemplateName);
		getRelationInstancesQuery.setParameter("internalServiceInstanceID", internalServiceInstanceID);
		@SuppressWarnings("unchecked")
		List<RelationInstance> queryResults = getRelationInstancesQuery.getResultList();

		return queryResults;
	}

}