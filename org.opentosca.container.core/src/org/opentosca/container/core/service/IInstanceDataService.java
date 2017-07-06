package org.opentosca.container.core.service;

import java.net.URI;
import java.util.HashMap;
import java.util.List;

import javax.xml.namespace.QName;

import org.opentosca.container.core.common.ReferenceNotFoundException;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.model.instance.NodeInstance;
import org.opentosca.container.core.model.instance.RelationInstance;
import org.opentosca.container.core.model.instance.ServiceInstance;
import org.w3c.dom.Document;

/**
 * Interface of the InstanceDataService. The interface specifies methods to
 * manage instances of ServiceTemplates (=ServiceInstances) and NodeTemplates
 * (NodeInstances) and properties of NodeInstances.
 */
public interface IInstanceDataService {
	
	// TODO: remove deprecated methods!
	
	/**
	 * Sets a property.
	 *
	 * @param key of the property to identify it.
	 * @param value of the property.
	 *
	 *
	 *
	 */
	@Deprecated
	public void setProperty(String key, String value);
	
	/**
	 * Returns a property.
	 *
	 * @param key that identifies a property.
	 *
	 * @return a property as String.
	 *
	 */
	@Deprecated
	public String getProperty(String key);
	
	/**
	 * Returns a HashMap of properties.
	 *
	 * @param key that identifies the properties.
	 *
	 * @return properties in a HashMap.
	 *
	 */
	@Deprecated
	public HashMap<String, String> getProperties(String key);
	
	/**
	 * Creates a <code>ServiceInstance</code> of the specified serviceTemplate
	 * (specified by the given ID and TemplateNamespace)
	 *
	 * @param serviceTemplateNamespace
	 * @param ServiceTemplateID
	 * @return the new generated ServiceInstance
	 */
	public ServiceInstance createServiceInstance(CSARID csarID, QName serviceTemplateID) throws ReferenceNotFoundException;
	
	/**
	 * Deletes the serviceInstance represnted by the given
	 * <code>serviceInstanceID</code>
	 *
	 * @param serviceInstanceID - of the instance which will be deleted
	 */
	public void deleteServiceInstance(URI serviceInstanceID);
	
	/**
	 * Queries for all ServiceInstances identified by the given parameters. It
	 * then returns a List of the matching serviceInstances.
	 *
	 * @see serviceInstance
	 * @TODO: additional parameters in JDOC
	 * @param serviceInstanceID : ID to identify the serviceInstance
	 * @return List containing all corresponding ServiceInstances
	 */
	public List<ServiceInstance> getServiceInstances(URI serviceInstanceID, String serviceTemplateName, QName serviceTemplateID);
	
	/**
	 * Create a <code>NodeInstance</code>of the specified nodeTemplate of the
	 * given serviceInstanceID
	 *
	 * @param nodeTemplateID
	 * @param serviceInstanceID
	 * @return the new generated NodeInstance
	 */
	public NodeInstance createNodeInstance(CSARID csarId, QName serviceTemplateId, int serviceInstanceID, QName nodeTemplateID) throws ReferenceNotFoundException;
	
	/**
	 * Create a <code>RelationInstance</code>of the specified Relationship
	 * Template of the given serviceInstanceID
	 *
	 * @param csarId the Id of the CSAR the Relationship Template should belong
	 *            to
	 * @param serviceTemplateId the Service Template ID the Relationship
	 *            Template should belong to
	 * @param serviceTemplateInstanceID the Instance ID of the Service Template
	 * @param relationshipTemplateID the ID of the Relationship Template
	 * @param sourceInstanceId the id of the node instance which is the source
	 *            of this relationship instance
	 * @param targetInstanceId the id of the node instance which is the target
	 *            of this relationship instance
	 * @return a new RelationInstance Object
	 * @throws ReferenceNotFoundException
	 */
	public RelationInstance createRelationInstance(CSARID csarId, QName serviceTemplateId, int serviceTemplateInstanceID, QName relationshipTemplateID, String sourceInstanceId, String targetInstanceId) throws ReferenceNotFoundException;
	
	/**
	 * Deletes the specified NodeInstance
	 *
	 * @param nodeInstanceID
	 */
	public void deleteNodeInstance(URI nodeInstanceID);
	
	/**
	 * Deletes the specified RelationInstance
	 *
	 * @param relationInstanceID the RelationInstance Id as URI
	 */
	public void deleteRelationInstance(URI relationInstanceID);
	
	/**
	 * returns all NodeInstances matching the given parameters the parameters
	 * are ANDed therefore a nodeInstance has to match all parameters to be
	 * returned
	 *
	 * @param nodeInstanceID
	 * @param nodeTemplateID
	 * @param nodeTemplateName
	 * @param serviceInstanceID
	 * @return all matching nodeInstances
	 */
	public List<NodeInstance> getNodeInstances(URI nodeInstanceID, QName nodeTemplateID, String nodeTemplateName, URI serviceInstanceID);
	
	/**
	 * returns all RelationInstances matching the given parameters the
	 * parameters are ANDed therefore a relationInstance has to match all
	 * parameters to be returned
	 *
	 * @param relationInstanceID the relationInstanceId
	 * @param relationshipTemplateID
	 * @param relationshipTemplateName
	 * @param serviceInstanceID
	 * @return all matching nodeInstances
	 */
	public List<RelationInstance> getRelationInstances(URI relationInstanceID, QName relationshipTemplateID, String relationshipTemplateName, URI serviceInstanceID);
	
	/**
	 * returns the state of the NodeInstance specified by
	 * <code>nodeInstanceID</code>
	 *
	 * @param nodeInstanceID
	 * @return State
	 * @throws ReferenceNotFoundException if nodeInstanceID doesn't exist
	 */
	public QName getNodeInstanceState(URI nodeInstanceID) throws ReferenceNotFoundException;
	
	/**
	 * Sets the state of the specified nodeInstanceID
	 *
	 * @param nodeInstanceID
	 * @param state
	 * @throws ReferenceNotFoundException if nodeInstanceID doesn't exist
	 */
	public void setNodeInstanceState(URI nodeInstanceID, String state) throws ReferenceNotFoundException;
	
	/**
	 * returns a DOM structure containing all properties specified in the
	 * propertiesList
	 * <ul>
	 * <li>if propertiesList is <code>empty</code> all properties are returned
	 * </li>
	 * <li>if propertiesList is <code>null</code> no properties are returned
	 * </li>
	 * </ul>
	 *
	 * @param serviceInstanceID
	 * @param propertiesList
	 * @return DOM
	 * @throws ReferenceNotFoundException
	 */
	public Document getServiceInstanceProperties(URI serviceInstanceID, List<QName> propertiesList) throws ReferenceNotFoundException;
	
	/**
	 * returns a DOM structure containing all properties specified in the
	 * propertiesList
	 * <ul>
	 * <li>if propertiesList is <code>empty</code> all properties are returned
	 * </li>
	 * <li>if propertiesList is <code>null</code> no properties are returned
	 * </li>
	 * </ul>
	 *
	 * @param nodeInstanceID
	 * @param propertiesList
	 * @return DOM
	 * @throws ReferenceNotFoundException
	 */
	public Document getNodeInstanceProperties(URI nodeInstanceID, List<QName> propertiesList) throws ReferenceNotFoundException;
	
	public void setNodeInstanceProperties(URI nodeInstanceID, Document properties) throws ReferenceNotFoundException;
	
	public void setServiceInstanceProperties(URI serviceInstanceID, Document properties) throws ReferenceNotFoundException;
	
	public NodeInstance createNodeInstance(QName nodeTemplateIDQName, URI serviceInstanceIdURI) throws ReferenceNotFoundException;
	
	public List<ServiceInstance> getServiceInstancesWithDetails(CSARID csarId, QName serviceTemplateId, Integer serviceTemplateInstanceID);
	
	public QName getRelationInstanceState(URI relationInstanceID) throws ReferenceNotFoundException;
	
	public void setRelationInstanceState(URI relationInstanceID, String state) throws ReferenceNotFoundException;
	
	public void setRelationInstanceProperties(URI relationInstanceID, Document properties) throws ReferenceNotFoundException;
	
	public Document getRelationInstanceProperties(URI relationInstanceID, List<QName> propertiesList) throws ReferenceNotFoundException;

	public String getServiceInstanceState(URI serviceInstanceID) throws ReferenceNotFoundException;

	public void setServiceInstanceState(URI serviceInstanceIDtoURI, String state) throws ReferenceNotFoundException;
	
}
