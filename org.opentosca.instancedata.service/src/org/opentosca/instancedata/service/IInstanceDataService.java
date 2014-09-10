package org.opentosca.instancedata.service;

import java.net.URI;
import java.util.HashMap;
import java.util.List;

import javax.xml.namespace.QName;

import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.model.instancedata.NodeInstance;
import org.opentosca.model.instancedata.ServiceInstance;
import org.w3c.dom.Document;

/**
 * Interface of the InstanceDataService.<br>
 * <br>
 * 
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * The interface specifies methods to manage instances of ServiceTemplates (=ServiceInstances)
 * and NodeTemplates (NodeInstances) and properties of NodeInstances
 * 
 * 
 * 
 * @author Marcus Eisele - marcus.eisele@gmail.com
 * 
 */
public interface IInstanceDataService {
	
	//TODO: remove deprecated methods!
	
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
	public ServiceInstance createServiceInstance(CSARID csarID,
			QName serviceTemplateID) throws ReferenceNotFoundException;
	
	/**
	 * Deletes the serviceInstance represnted by the given
	 * <code>serviceInstanceID</code>
	 * 
	 * @param serviceInstanceID
	 *            - of the instance which will be deleted
	 */
	public void deleteServiceInstance(URI serviceInstanceID);
	
	/**
	 * Queries for all ServiceInstances identified by the given parameters. It
	 * then returns a List of the matching serviceInstances.
	 * 
	 * @see serviceInstance
	 * @TODO: additional parameters in JDOC
	 * @param serviceInstanceID
	 *            : ID to identify the serviceInstance
	 * @return List containing all corresponding ServiceInstances
	 */
	public List<ServiceInstance> getServiceInstances(URI serviceInstanceID,
			String serviceTemplateName, QName serviceTemplateID);
	
	/**
	 * Create a <code>NodeInstance</code>of the specified nodeTemplate of the
	 * given serviceInstanceID
	 * 
	 * @param nodeTemplateID
	 * @param serviceInstanceID
	 * @return the new generated NodeInstance
	 */
	public NodeInstance createNodeInstance(QName nodeTemplateID,
			URI serviceInstanceID) throws ReferenceNotFoundException;
	
	/**
	 * Deletes the specified NodeInstance
	 * 
	 * @param nodeInstanceID
	 */
	public void deleteNodeInstance(URI nodeInstanceID);
	
	/**
	 * returns all NodeInstances matching the given parameters the parameters are ANDed therefore a nodeInstance has to
	 * match all parameters to be returned
	 * 
	 * @param nodeInstanceID
	 * @param nodeTemplateID
	 * @param nodeTemplateName
	 * @param serviceInstanceID
	 * @return all matching nodeInstances
	 */
	public List<NodeInstance> getNodeInstances(URI nodeInstanceID,
			QName nodeTemplateID, String nodeTemplateName,
			URI serviceInstanceID);
	
	/**
	 * returns the state of the NodeInstance specified by
	 * <code>nodeInstanceID</code>
	 * 
	 * @param nodeInstanceID
	 * @return State
	 * @throws ReferenceNotFoundException
	 *             if nodeInstanceID doesn't exist
	 */
	public QName getState(URI nodeInstanceID) throws ReferenceNotFoundException;
	
	/**
	 * Sets the state of the specified nodeInstanceID
	 * 
	 * @param nodeInstanceID
	 * @param state
	 * @throws ReferenceNotFoundException
	 *             if nodeInstanceID doesn't exist
	 */
	public void setState(URI nodeInstanceID, QName state) throws ReferenceNotFoundException;
	
	/**
	 * returns a DOM structure containing all properties specified in the propertiesList
	 * <ul>
	 * <li>if propertiesList is <code>empty</code> all properties are returned</li>
	 * <li>if propertiesList is <code>null</code> no properties are returned</li>
	 * </ul>
	 * 
	 * @param nodeInstanceID
	 * @param propertiesList
	 * @return DOM
	 * @throws ReferenceNotFoundException
	 */
	public Document getProperties(URI nodeInstanceID, List<QName> propertiesList) throws ReferenceNotFoundException;
	
	public void setProperties(URI nodeInstanceID, Document properties) throws ReferenceNotFoundException;
}
