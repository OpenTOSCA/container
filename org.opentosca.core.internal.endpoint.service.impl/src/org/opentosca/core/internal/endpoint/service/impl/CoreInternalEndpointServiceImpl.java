package org.opentosca.core.internal.endpoint.service.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.xml.namespace.QName;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.opentosca.core.internal.endpoint.service.ICoreInternalEndpointService;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.core.model.endpoint.rest.RESTEndpoint;
import org.opentosca.core.model.endpoint.rest.RESTEndpoint.restMethod;
import org.opentosca.core.model.endpoint.wsdl.WSDLEndpoint;
import org.opentosca.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Class stores and retrieves Endpoint-Objects in the Database, using
 * Eclipse-JPA.
 * 
 * For the JPA-Queries refer to: {@link RESTEndpoint}, {@link WSDLEndpoint}
 * 
 * Copyright 2012 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Matthias Fetzer - fetzerms@studi.informatik.uni-stuttgart.de
 * 
 */
public class CoreInternalEndpointServiceImpl implements ICoreInternalEndpointService, CommandProvider {
	
	/**
	 * JDBC-Url to the Database. The Database to store Endpoints (Endpoints for
	 * SOAP and REST services) will reside at this spot. It will be created if
	 * it does not exist yet.
	 * 
	 * @see org.opentosca.settings
	 */
	private final String databaseURL = "jdbc:derby:" + Settings.getSetting("databaseLocation") + ";create=true";
	
	// Logging
	private final static Logger LOG = LoggerFactory.getLogger(CoreInternalEndpointServiceImpl.class);
	
	/**
	 * ORM EntityManager + Factory. These variables are global, as we do not
	 * want to create a new EntityManager/Factory each time a method is called.
	 */
	private EntityManagerFactory emf;
	private EntityManager em;
	
	
	@Override
	/**
	 * Destructor. This method is called when the garbage collector destroys the class.
	 * We will then manually close the EntityManager/Factory, and pass control back.
	 */
	protected void finalize() throws Throwable {
		this.em.close();
		this.emf.close();
		super.finalize();
	}
	
	/**
	 * This method initializes the EntityManager/Factory in case it is not
	 * connected/setup yet. It is called by each method, to ensure that a
	 * connection exists. (Robustness!)
	 */
	private void init() {
		if (this.emf == null) {
			Map<String, String> properties = new HashMap<String, String>();
			properties.put(PersistenceUnitProperties.JDBC_URL, this.databaseURL);
			this.emf = Persistence.createEntityManagerFactory("Endpoints", properties);
			this.em = this.emf.createEntityManager();
		}
	}
	
	@Override
	/**
	 * {@Inheritdoc}
	 */
	public List<WSDLEndpoint> getWSDLEndpoints(QName portType, CSARID csarId) {
		this.init();
		ArrayList<WSDLEndpoint> results = new ArrayList<WSDLEndpoint>();
		
		/**
		 * Create Query to retrieve WSDL-Endpoints
		 * 
		 * @see WSDLEndpoint#getWSDLEndpointByPortTyp
		 */
		Query getWSDLEndpointsQuery = this.em.createNamedQuery(WSDLEndpoint.getWSDLEndpointByPortType);
		
		// Set Parameters for the Query
		getWSDLEndpointsQuery.setParameter("portType", portType);
		getWSDLEndpointsQuery.setParameter("csarId", csarId);
		
		// Get Query-Results (WSDLEndpoints) and add them to the result list.
		@SuppressWarnings("unchecked")
		// Result can only be a WSDLEndpoint
		List<WSDLEndpoint> queryResults = getWSDLEndpointsQuery.getResultList();
		for (WSDLEndpoint endpoint : queryResults) {
			results.add(endpoint);
		}
		
		// Hack, to get endpoints stored from the container e.g. the SI-Invoker
		// endpoint.
		// Set Parameters for the Query
		getWSDLEndpointsQuery.setParameter("portType", portType);
		getWSDLEndpointsQuery.setParameter("csarId", new CSARID("***"));
		
		// Get Query-Results (WSDLEndpoints) and add them to the result list.
		@SuppressWarnings("unchecked")
		// Result can only be a WSDLEndpoint
		List<WSDLEndpoint> queryResults2 = getWSDLEndpointsQuery.getResultList();
		for (WSDLEndpoint endpoint : queryResults2) {
			results.add(endpoint);
		}
		
		return results;
	}
	
	@Override
	/**
	 * {@Inheritdoc}
	 */
	public WSDLEndpoint getWSDLEndpoint(QName portType, CSARID csarId) {
		this.init();
		
		/**
		 * Create Query to retrieve WSDLEndpoint
		 * 
		 * @see WSDLEndpoint#getWSDLEndpointByPortTypeAndAddressType
		 */
		Query getWSDLEndpointQuery = this.em.createNamedQuery(WSDLEndpoint.getWSDLEndpointByPortType);
		
		// Set Parameters for the Query
		getWSDLEndpointQuery.setParameter("portType", portType);
		getWSDLEndpointQuery.setParameter("csarId", csarId);
		
		// Return the retrieved WSDL-Endpoint. As WSDL-Endpoints are
		// identified/unique by portType, addressType and thorID, the
		// result can only be one WSDL-Endpoint.
		return (WSDLEndpoint) getWSDLEndpointQuery.getSingleResult();
	}
	
	@Override
	/**
	 * {@Inheritdoc}
	 */
	public void storeWSDLEndpoint(WSDLEndpoint endpoint) {
		this.init();
		CoreInternalEndpointServiceImpl.LOG.debug("Storing WSDL Endpoint with CSARID: \"" + endpoint.getCSARId() + "\", portType: \"" + endpoint.getPortType() + "\", IAName: \"" + endpoint.getIaName() + "\", NodeTypeImplementation: \"" + endpoint.getNodeTypeImplementation() + "\", URI: \"" + endpoint.getURI().toString() + "\"");
		
		// TODO this check is a hack because of the problem with deploying of
		// multiple deployment artifacts
		if (!this.existsWSDLEndpoint(endpoint)) {
			this.em.getTransaction().begin();
			CoreInternalEndpointServiceImpl.LOG.debug("The endpoint for \"{}\" is not stored. Thus store it.", endpoint.getPortType());
			this.em.persist(endpoint);
			this.em.getTransaction().commit();
		} else {
			CoreInternalEndpointServiceImpl.LOG.debug("The endpoint for \"{}\" is stored already.", endpoint.getPortType());
		}
	}
	
	/**
	 * Helper method to check if a given WSDLEndpoint is already stored in the
	 * database
	 * 
	 * @param endpoint to look for
	 * @return true, if the Endpoint already exists.
	 */
	private boolean existsWSDLEndpoint(WSDLEndpoint endpoint) {
		this.init();
		Query wsdlEndpointQuery = this.em.createNamedQuery(WSDLEndpoint.getWSDLEndpointByPortType);
		
		// Set parameters
		wsdlEndpointQuery.setParameter("portType", endpoint.getPortType());
		// wsdlEndpointQuery.setParameter("addressType",
		// endpoint.getAddressType());
		wsdlEndpointQuery.setParameter("csarId", endpoint.getCSARId());
		
		// Check if the result list is empty
		return wsdlEndpointQuery.getResultList().size() != 0;
	}
	
	@Override
	/**
	 * {@Inheritdoc}
	 */
	public List<RESTEndpoint> getRestEndpoints(URI anyURI, CSARID csarId) {
		this.init();
		ArrayList<RESTEndpoint> results = new ArrayList<RESTEndpoint>();
		
		/**
		 * Create Query to retrieve RESTEndpoints identified by a URI and thorID
		 * 
		 * @see RESTEndpoint#getEndpointForPath
		 **/
		Query getRestEndpointsQuery = this.em.createNamedQuery(RESTEndpoint.getEndpointForPath);
		
		// Set Parameters
		getRestEndpointsQuery.setParameter("path", anyURI.getPath());
		getRestEndpointsQuery.setParameter("csarId", csarId);
		
		// Get Query-Results and add them to the result list
		@SuppressWarnings("unchecked")
		// Result can only be a RESTEndpoint
		List<RESTEndpoint> queryResults = getRestEndpointsQuery.getResultList();
		for (RESTEndpoint endpoint : queryResults) {
			results.add(endpoint);
		}
		return results;
	}
	
	@Override
	/**
	 * {@Inheritdoc}
	 */
	public RESTEndpoint getRestEndpoint(URI anyURI, restMethod method, CSARID csarId) {
		this.init();
		
		/**
		 * Create Query to retrieve a RestEndpoint
		 * 
		 * @see RESTEndpoint#getEndpointForPathAndMethod
		 */
		Query getRestEndpointQuery = this.em.createNamedQuery(RESTEndpoint.getEndpointForPathAndMethod);
		
		// Set parameters
		getRestEndpointQuery.setParameter("path", anyURI.getPath());
		getRestEndpointQuery.setParameter("method", method);
		getRestEndpointQuery.setParameter("csarId", csarId);
		
		// As a RESTEndpoint identified by URI, RestMethod and thorID
		// is unique, we only return one result (there cannot be more)
		return (RESTEndpoint) getRestEndpointQuery.getSingleResult();
	}
	
	@Override
	/**
	 * {@Inheritdoc}
	 */
	public void storeRESTEndpoint(RESTEndpoint endpoint) {
		this.init();
		CoreInternalEndpointServiceImpl.LOG.debug("Storing REST Endpoint with Path : \"{}\", STID: \"{}\"", endpoint.getPath(), endpoint.getCSARId().getFileName());
		this.em.getTransaction().begin();
		this.em.persist(endpoint);
		this.em.getTransaction().commit();
	}
	
	@Override
	/**
	 * {@Inheritdoc}
	 */
	public boolean endpointExists(URI uri, CSARID csarId) {
		this.init();
		Query existsRestEndpointQuery = this.em.createNamedQuery(RESTEndpoint.getEndpointForUri);
		existsRestEndpointQuery.setParameter("uri", uri);
		existsRestEndpointQuery.setParameter("csarId", csarId);
		
		Query existsWsdlEndpointQuery = this.em.createNamedQuery(WSDLEndpoint.getWSDLEndpointByUri);
		existsWsdlEndpointQuery.setParameter("uri", uri);
		existsWsdlEndpointQuery.setParameter("csarId", csarId);
		
		return ((existsRestEndpointQuery.getResultList().size() != 0) || (existsWsdlEndpointQuery.getResultList().size() != 0));
	}
	
	/**
	 * The following methods provide OSGi-Console commands.
	 */
	
	@Override
	public String getHelp() {
		StringBuffer buf = new StringBuffer();
		buf.append("---OpenTOSCA Endpoint Management---\n");
		buf.append("\tendpoint_clear - Clears both endpoint Dbs\n");
		buf.append("\tendpoint_clear_wsdl - Clear wsdl endpoint db\n");
		buf.append("\tendpoint_clear_rest - Clear rest endpoint db\n");
		buf.append("\tendpoint_show_wsdl - Shows all WSDL-Endpoints\n");
		buf.append("\tendpoint_show_rest - Shows all REST-Endpoints\n");
		buf.append("\tendpoint_add_dummy_rest - Add dummy rest endpoint db\n");
		buf.append("\tendpoint_add_dummy_wsdl - Add dummy wsdl endpoint db\n");
		return buf.toString();
	}
	
	public void _endpoint_clear(CommandInterpreter commandInterpreter) {
		this._endpoint_clear_rest(commandInterpreter);
		this._endpoint_clear_wsdl(commandInterpreter);
	}
	
	public void _endpoint_clear_wsdl(CommandInterpreter commandInterpreter) {
		this.em.getTransaction().begin();
		Query query = this.em.createQuery("DELETE FROM WSDLEndpoint");
		int deletedWSDL = query.executeUpdate();
		commandInterpreter.println("Deleted " + deletedWSDL + " WSDLEndpoints.");
		this.em.getTransaction().commit();
	}
	
	public void _endpoint_clear_rest(CommandInterpreter commandInterpreter) {
		this.em.getTransaction().begin();
		Query query = this.em.createQuery("DELETE FROM RESTEndpoint");
		int deletedREST = query.executeUpdate();
		commandInterpreter.println("Deleted " + deletedREST + " RESTEndpoints.");
		this.em.getTransaction().commit();
	}
	
	public void _endpoint_add_dummy_rest(CommandInterpreter commandInterpreter) {
		try {
			RESTEndpoint endpoint = new RESTEndpoint(new URI("http://www.balbla.com/xyz"), restMethod.GET, new CSARID("mockup.example.test"));
			this.storeRESTEndpoint(endpoint);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void _endpoint_add_dummy_wsdl(CommandInterpreter commandInterpreter) {
		try {
			
			// URI uri, QName portType, CSARID csarId, String planid, QName
			// nodeTypeImplementation, String iaName
			WSDLEndpoint endpoint = new WSDLEndpoint(new URI("http://blabla/"), new QName("somePort"), new CSARID("mockup.example.test"), new QName("{someNamespace}someplanid"), new QName("{someNamespace}someNodeTypeImplId"), "some ia name");
			this.storeWSDLEndpoint(endpoint);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void _endpoint_show_rest(CommandInterpreter commandInterpreter) {
		this.em.getTransaction().begin();
		Query query = this.em.createQuery("SELECT e FROM RESTEndpoint e");
		@SuppressWarnings("unchecked")
		List<RESTEndpoint> queryResults = query.getResultList();
		for (RESTEndpoint e : queryResults) {
			commandInterpreter.println("SeriviceTemplateID: " + e.getCSARId().getFileName() + " URI: " + e.getURI());
		}
		this.em.getTransaction().commit();
		
	}
	
	public void _endpoint_show_wsdl(CommandInterpreter commandInterpreter) {
		this.em.getTransaction().begin();
		Query query = this.em.createQuery("SELECT e FROM WSDLEndpoint e");
		@SuppressWarnings("unchecked")
		List<WSDLEndpoint> queryResults = query.getResultList();
		for (WSDLEndpoint e : queryResults) {
			commandInterpreter.println("CSARId: " + e.getCSARId());
			
			if(e.getPortType() != null){
			commandInterpreter.println("PortType: " + e.getPortType().toString());}
			commandInterpreter.println("PlanId: " + (e.getPlanId() == null ? "" : e.getPlanId().toString()));
			commandInterpreter.println("NodeTypeImpl: " + (e.getNodeTypeImplementation() == null ? "" : e.getNodeTypeImplementation().toString()));
			commandInterpreter.println("IaName: " + (e.getIaName() == null ? "" : e.getIaName()));
			commandInterpreter.println("URI: " + e.getURI().toString());
			commandInterpreter.println("");
		}
		this.em.getTransaction().commit();
		
	}
	
	@Override
	public void removeEndpoints(CSARID csarId) {
		this.init();
		this.em.getTransaction().begin();
		
		// FIXME if somebody deletes a csar before some endpoints where added,
		// some exceptions occur
		
		// get all rest endpoints for the given csarid
		Query queryRestEndpoints = this.em.createQuery("SELECT e FROM RESTEndpoint e where e.csarId = :csarId");
		queryRestEndpoints.setParameter("csarId", csarId);
		@SuppressWarnings("unchecked")
		List<RESTEndpoint> restEndpoints = queryRestEndpoints.getResultList();
		
		// get all wsdl endpoints for the given csarid
		Query queryWsdlEndpoints = this.em.createQuery("SELECT e FROM WSDLEndpoint e where e.csarId = :csarId");
		queryWsdlEndpoints.setParameter("csarId", csarId);
		@SuppressWarnings("unchecked")
		List<WSDLEndpoint> wsdlEndpoints = queryWsdlEndpoints.getResultList();
		
		// remove all found endpoints one by one
		for (RESTEndpoint restEndpoint : restEndpoints) {
			this.em.remove(restEndpoint);
		}
		
		for (WSDLEndpoint wsdlEndpoint : wsdlEndpoints) {
			this.em.remove(wsdlEndpoint);
		}
		
		this.em.getTransaction().commit();
		
	}
	
	@Override
	public WSDLEndpoint getWSDLEndpointForPlanId(CSARID csarId, QName planId) {
		this.init();
		this.em.getTransaction().begin();
		WSDLEndpoint endpoint = null;
		Query queryWSDLEndpoint = this.em.createQuery("SELECT e FROM WSDLEndpoint e where e.csarId= :csarId and e.PlanId = :planId");
		queryWSDLEndpoint.setParameter("csarId", csarId);
		queryWSDLEndpoint.setParameter("planId", planId);
		
		endpoint = (WSDLEndpoint) queryWSDLEndpoint.getSingleResult();
		this.em.getTransaction().commit();
		return endpoint;
	}
	
	@Override
	public WSDLEndpoint getWSDLEndpointForIa(CSARID csarId, QName nodeTypeImpl, String iaName) {
		this.init();
		this.em.getTransaction().begin();
		WSDLEndpoint endpoint = null;
		Query queryWSDLEndpoint = this.em.createQuery("SELECT e FROM WSDLEndpoint e where e.csarId= :csarId and e.IaName = :IaName and e.NodeTypeImplementation = :nodeTypeImpl");
		queryWSDLEndpoint.setParameter("csarId", csarId);
		queryWSDLEndpoint.setParameter("IaName", iaName);
		queryWSDLEndpoint.setParameter("nodeTypeImpl", nodeTypeImpl);
		try {
			endpoint = (WSDLEndpoint) queryWSDLEndpoint.getSingleResult();
			
		} catch (NoResultException e) {
			CoreInternalEndpointServiceImpl.LOG.info("No endpoint stored for requested IA.");
		} finally {
			this.em.getTransaction().commit();
		}
		return endpoint;
	}
	
	@Override
	public List<WSDLEndpoint> getWSDLEndpointsForCSARID(CSARID csarId) {
		this.init();
		this.em.getTransaction().begin();
		ArrayList<WSDLEndpoint> endpoints = new ArrayList<WSDLEndpoint>();
		Query queryWSDLEndpoint = this.em.createQuery("SELECT e FROM WSDLEndpoint e where e.csarId= :csarId");
		queryWSDLEndpoint.setParameter("csarId", csarId);
		
		@SuppressWarnings("unchecked")
		List<WSDLEndpoint> queryResults = queryWSDLEndpoint.getResultList();
		for (WSDLEndpoint endpoint : queryResults) {
			endpoints.add(endpoint);
		}
		
		this.em.getTransaction().commit();
		return endpoints;
	}
	
	@Override
	public void printPlanEndpoints() {
		this.init();
		this.em.getTransaction().begin();
		List<WSDLEndpoint> endpoints = null;
		Query queryWSDLEndpoint = this.em.createQuery("SELECT e FROM WSDLEndpoint e");
		
		endpoints = queryWSDLEndpoint.getResultList();
		this.em.getTransaction().commit();
		
		StringBuilder builder = new StringBuilder();
		String ls = System.getProperty("line.separator");
		builder.append("debug output for stored endpoints of management plans, flags: csarid, planid, ianame, porttype " + ls);
		for (WSDLEndpoint endpoint : endpoints) {
			builder.append("endpoint: " + endpoint.getCSARId() + " " + endpoint.getPlanId() + " " + endpoint.getIaName() + " " + endpoint.getPortType() + ls);
		}
		CoreInternalEndpointServiceImpl.LOG.debug(builder.toString());
	}
	
	@Override
	public boolean removeWSDLEndpoint(CSARID csarId, WSDLEndpoint endpoint) {
		this.init();
		
		// get all wsdlendpoints
		List<WSDLEndpoint> endpoints = this.getWSDLEndpointsForCSARID(csarId);
		
		this.em.getTransaction().begin();
		boolean check = false;
		
		// check if given endpoint is in the list and remove
		if (endpoints.contains(endpoint)) {
			this.em.remove(endpoint);
			check = true;
		}
		this.em.getTransaction().commit();
		return check;
	}
	
}
