package org.opentosca.container.core.impl.service.internal;

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
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.model.endpoint.rest.RESTEndpoint;
import org.opentosca.container.core.model.endpoint.rest.RESTEndpoint.restMethod;
import org.opentosca.container.core.model.endpoint.wsdl.WSDLEndpoint;
import org.opentosca.container.core.service.internal.ICoreInternalEndpointService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Class stores and retrieves Endpoint-Objects in the Database, using
 * Eclipse-JPA.
 *
 * For the JPA-Queries refer to: {@link RESTEndpoint}, {@link WSDLEndpoint}
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
	
	
	public CoreInternalEndpointServiceImpl() {
		this.init();
	}
	
	@Override
	/**
	 * Destructor. This method is called when the garbage collector destroys the
	 * class. We will then manually close the EntityManager/Factory, and pass
	 * control back.
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
			final Map<String, String> properties = new HashMap<>();
			properties.put(PersistenceUnitProperties.JDBC_URL, this.databaseURL);
			this.emf = Persistence.createEntityManagerFactory(Settings.PERSISTENCE_UNIT_NAME, properties);
			this.em = this.emf.createEntityManager();
		}
	}
	
	@Override
	/**
	 * {@Inheritdoc}
	 */
	public List<WSDLEndpoint> getWSDLEndpoints(final QName portType, final CSARID csarId) {
		final ArrayList<WSDLEndpoint> results = new ArrayList<>();
		
		/**
		 * Create Query to retrieve WSDL-Endpoints
		 *
		 * @see WSDLEndpoint#getWSDLEndpointByPortTyp
		 */
		final Query getWSDLEndpointsQuery = this.em.createNamedQuery(WSDLEndpoint.getWSDLEndpointByPortType);
		
		// Set Parameters for the Query
		getWSDLEndpointsQuery.setParameter("portType", portType);
		getWSDLEndpointsQuery.setParameter("csarId", csarId);
		
		// Get Query-Results (WSDLEndpoints) and add them to the result list.
		@SuppressWarnings("unchecked")
		final
		// Result can only be a WSDLEndpoint
		List<WSDLEndpoint> queryResults = getWSDLEndpointsQuery.getResultList();
		for (final WSDLEndpoint endpoint : queryResults) {
			results.add(endpoint);
		}
		
		// Hack, to get endpoints stored from the container e.g. the SI-Invoker
		// endpoint.
		// Set Parameters for the Query
		getWSDLEndpointsQuery.setParameter("portType", portType);
		getWSDLEndpointsQuery.setParameter("csarId", new CSARID("***"));
		
		// Get Query-Results (WSDLEndpoints) and add them to the result list.
		@SuppressWarnings("unchecked")
		final
		// Result can only be a WSDLEndpoint
		List<WSDLEndpoint> queryResults2 = getWSDLEndpointsQuery.getResultList();
		for (final WSDLEndpoint endpoint : queryResults2) {
			results.add(endpoint);
		}
		
		return results;
	}
	
	@Override
	/**
	 * {@Inheritdoc}
	 */
	public WSDLEndpoint getWSDLEndpoint(final QName portType, final CSARID csarId) {
		/**
		 * Create Query to retrieve WSDLEndpoint
		 *
		 * @see WSDLEndpoint#getWSDLEndpointByPortTypeAndAddressType
		 */
		final Query getWSDLEndpointQuery = this.em.createNamedQuery(WSDLEndpoint.getWSDLEndpointByPortType);
		
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
	public void storeWSDLEndpoint(final WSDLEndpoint endpoint) {
		CoreInternalEndpointServiceImpl.LOG.debug("Storing WSDL Endpoint with CSARID: \"" + endpoint.getCSARId() + "\", portType: \"" + endpoint.getPortType() + "\", IAName: \"" + endpoint.getIaName() + "\", NodeTypeImplementation: \"" + endpoint.getNodeTypeImplementation() + "\", URI: \"" + endpoint.getURI().toString() + "\"");
		
		// TODO this check is a hack because of the problem with deploying of
		// multiple deployment artifacts
		if (!this.existsWSDLEndpoint(endpoint)) {
			if (!this.em.getTransaction().isActive()) {
				this.em.getTransaction().begin();
			}
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
	private boolean existsWSDLEndpoint(final WSDLEndpoint endpoint) {
		final List<WSDLEndpoint> endpoints = this.getWSDLEndpointsForCSARID(endpoint.getCSARId());
		
		for (final WSDLEndpoint wsdlEndpoint : endpoints) {
			// (serviceURI, portType, csarID, null, nodeTypeImplementationID,
			// implementationArtifactName);
			
			if (!endpoint.getURI().equals(wsdlEndpoint.getURI())) {
				continue;
			}
			
			if (endpoint.getPortType() != null) {
				if (!endpoint.getPortType().equals(wsdlEndpoint.getPortType())) {
					continue;
				}
			} else if (wsdlEndpoint.getPortType() != null) {
				// at this point the given endpoint is null, if wsdlEndpoint is
				// != null -> not the same endpoint
				continue;
			}
			
			if (!endpoint.getCSARId().equals(wsdlEndpoint.getCSARId())) {
				continue;
			}
			
			if (endpoint.getNodeTypeImplementation() != null) {
				if (!endpoint.getNodeTypeImplementation().equals(wsdlEndpoint.getNodeTypeImplementation())) {
					continue;
				}
			} else if (wsdlEndpoint.getNodeTypeImplementation() != null) {
				// see above
				continue;
			}
			
			if (endpoint.getIaName() != null) {
				if (!endpoint.getIaName().equals(wsdlEndpoint.getIaName())) {
					continue;
				}
			} else if (wsdlEndpoint.getIaName() != null) {
				continue;
			}
			
			// if we didn't skip the endpoint until now, we found an equal
			// endpoint
			return true;
		}
		
		return false;
	}
	
	@Override
	/**
	 * {@Inheritdoc}
	 */
	public List<RESTEndpoint> getRestEndpoints(final URI anyURI, final CSARID csarId) {
		final ArrayList<RESTEndpoint> results = new ArrayList<>();
		
		/**
		 * Create Query to retrieve RESTEndpoints identified by a URI and thorID
		 *
		 * @see RESTEndpoint#getEndpointForPath
		 **/
		final Query getRestEndpointsQuery = this.em.createNamedQuery(RESTEndpoint.getEndpointForPath);
		
		// Set Parameters
		getRestEndpointsQuery.setParameter("path", anyURI.getPath());
		getRestEndpointsQuery.setParameter("csarId", csarId);
		
		// Get Query-Results and add them to the result list
		@SuppressWarnings("unchecked")
		final
		// Result can only be a RESTEndpoint
		List<RESTEndpoint> queryResults = getRestEndpointsQuery.getResultList();
		for (final RESTEndpoint endpoint : queryResults) {
			results.add(endpoint);
		}
		return results;
	}
	
	@Override
	/**
	 * {@Inheritdoc}
	 */
	public RESTEndpoint getRestEndpoint(final URI anyURI, final restMethod method, final CSARID csarId) {
		/**
		 * Create Query to retrieve a RestEndpoint
		 *
		 * @see RESTEndpoint#getEndpointForPathAndMethod
		 */
		final Query getRestEndpointQuery = this.em.createNamedQuery(RESTEndpoint.getEndpointForPathAndMethod);
		
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
	public void storeRESTEndpoint(final RESTEndpoint endpoint) {
		CoreInternalEndpointServiceImpl.LOG.debug("Storing REST Endpoint with Path : \"{}\", STID: \"{}\"", endpoint.getPath(), endpoint.getCSARId().getFileName());
		if (!this.em.getTransaction().isActive()) {
			this.em.getTransaction().begin();
		}
		this.em.persist(endpoint);
		this.em.getTransaction().commit();
	}
	
	@Override
	/**
	 * {@Inheritdoc}
	 */
	public boolean endpointExists(final URI uri, final CSARID csarId) {
		final Query existsRestEndpointQuery = this.em.createNamedQuery(RESTEndpoint.getEndpointForUri);
		existsRestEndpointQuery.setParameter("uri", uri);
		existsRestEndpointQuery.setParameter("csarId", csarId);
		
		final Query existsWsdlEndpointQuery = this.em.createNamedQuery(WSDLEndpoint.getWSDLEndpointByUri);
		existsWsdlEndpointQuery.setParameter("uri", uri);
		existsWsdlEndpointQuery.setParameter("csarId", csarId);
		
		return ((existsRestEndpointQuery.getResultList().size() != 0) || (existsWsdlEndpointQuery.getResultList().size() != 0));
	}
	
	/**
	 * The following methods provide OSGi-Console commands.
	 */
	
	@Override
	public String getHelp() {
		final StringBuffer buf = new StringBuffer();
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
	
	public void _endpoint_clear(final CommandInterpreter commandInterpreter) {
		this._endpoint_clear_rest(commandInterpreter);
		this._endpoint_clear_wsdl(commandInterpreter);
	}
	
	public void _endpoint_clear_wsdl(final CommandInterpreter commandInterpreter) {
		if (!this.em.getTransaction().isActive()) {
			this.em.getTransaction().begin();
		}
		final Query query = this.em.createQuery("DELETE FROM WSDLEndpoint");
		final int deletedWSDL = query.executeUpdate();
		commandInterpreter.println("Deleted " + deletedWSDL + " WSDLEndpoints.");
		this.em.getTransaction().commit();
	}
	
	public void _endpoint_clear_rest(final CommandInterpreter commandInterpreter) {
		if (!this.em.getTransaction().isActive()) {
			this.em.getTransaction().begin();
		}
		final Query query = this.em.createQuery("DELETE FROM RESTEndpoint");
		final int deletedREST = query.executeUpdate();
		commandInterpreter.println("Deleted " + deletedREST + " RESTEndpoints.");
		this.em.getTransaction().commit();
	}
	
	public void _endpoint_add_dummy_rest(final CommandInterpreter commandInterpreter) {
		try {
			final RESTEndpoint endpoint = new RESTEndpoint(new URI("http://www.balbla.com/xyz"), restMethod.GET, new CSARID("mockup.example.test"));
			this.storeRESTEndpoint(endpoint);
		} catch (final URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void _endpoint_add_dummy_wsdl(final CommandInterpreter commandInterpreter) {
		try {
			
			// URI uri, QName portType, CSARID csarId, String planid, QName
			// nodeTypeImplementation, String iaName
			final WSDLEndpoint endpoint = new WSDLEndpoint(new URI("http://blabla/"), new QName("somePort"), new CSARID("mockup.example.test"), new QName("{someNamespace}someplanid"), new QName("{someNamespace}someNodeTypeImplId"), "some ia name");
			this.storeWSDLEndpoint(endpoint);
		} catch (final URISyntaxException e) {
			e.printStackTrace();
		}
		
	}
	
	public void _endpoint_show_rest(final CommandInterpreter commandInterpreter) {
		
		final Query query = this.em.createQuery("SELECT e FROM RESTEndpoint e");
		@SuppressWarnings("unchecked")
		final List<RESTEndpoint> queryResults = query.getResultList();
		for (final RESTEndpoint e : queryResults) {
			commandInterpreter.println("SeriviceTemplateID: " + e.getCSARId().getFileName() + " URI: " + e.getURI());
		}
		
	}
	
	public void _endpoint_show_wsdl(final CommandInterpreter commandInterpreter) {
		final Query query = this.em.createQuery("SELECT e FROM WSDLEndpoint e");
		@SuppressWarnings("unchecked")
		final List<WSDLEndpoint> queryResults = query.getResultList();
		for (final WSDLEndpoint e : queryResults) {
			commandInterpreter.println("CSARId: " + e.getCSARId());
			
			if (e.getPortType() != null) {
				commandInterpreter.println("PortType: " + e.getPortType().toString());
			}
			commandInterpreter.println("PlanId: " + (e.getPlanId() == null ? "" : e.getPlanId().toString()));
			commandInterpreter.println("NodeTypeImpl: " + (e.getNodeTypeImplementation() == null ? "" : e.getNodeTypeImplementation().toString()));
			commandInterpreter.println("IaName: " + (e.getIaName() == null ? "" : e.getIaName()));
			commandInterpreter.println("URI: " + e.getURI().toString());
			commandInterpreter.println("");
		}
		
	}
	
	@Override
	public void removeEndpoints(final CSARID csarId) {
		if (!this.em.getTransaction().isActive()) {
			this.em.getTransaction().begin();
		}
		// FIXME if somebody deletes a csar before some endpoints where added,
		// some exceptions occur
		
		// get all rest endpoints for the given csarid
		final Query queryRestEndpoints = this.em.createQuery("SELECT e FROM RESTEndpoint e where e.csarId = :csarId");
		queryRestEndpoints.setParameter("csarId", csarId);
		@SuppressWarnings("unchecked")
		final List<RESTEndpoint> restEndpoints = queryRestEndpoints.getResultList();
		
		// get all wsdl endpoints for the given csarid
		final Query queryWsdlEndpoints = this.em.createQuery("SELECT e FROM WSDLEndpoint e where e.csarId = :csarId");
		queryWsdlEndpoints.setParameter("csarId", csarId);
		@SuppressWarnings("unchecked")
		final List<WSDLEndpoint> wsdlEndpoints = queryWsdlEndpoints.getResultList();
		
		// remove all found endpoints one by one
		for (final RESTEndpoint restEndpoint : restEndpoints) {
			this.em.remove(restEndpoint);
		}
		
		for (final WSDLEndpoint wsdlEndpoint : wsdlEndpoints) {
			this.em.remove(wsdlEndpoint);
		}
		
		this.em.getTransaction().commit();
		
	}
	
	@Override
	public WSDLEndpoint getWSDLEndpointForPlanId(final CSARID csarId, final QName planId) {
		WSDLEndpoint endpoint = null;
		final Query queryWSDLEndpoint = this.em.createQuery("SELECT e FROM WSDLEndpoint e where e.csarId= :csarId and e.PlanId = :planId");
		queryWSDLEndpoint.setParameter("csarId", csarId);
		queryWSDLEndpoint.setParameter("planId", planId);
		
		endpoint = (WSDLEndpoint) queryWSDLEndpoint.getSingleResult();
		return endpoint;
	}
	
	@Override
	public WSDLEndpoint getWSDLEndpointForIa(final CSARID csarId, final QName nodeTypeImpl, final String iaName) {
		WSDLEndpoint endpoint = null;
		final Query queryWSDLEndpoint = this.em.createQuery("SELECT e FROM WSDLEndpoint e where e.csarId= :csarId and e.IaName = :IaName and e.NodeTypeImplementation = :nodeTypeImpl");
		queryWSDLEndpoint.setParameter("csarId", csarId);
		queryWSDLEndpoint.setParameter("IaName", iaName);
		queryWSDLEndpoint.setParameter("nodeTypeImpl", nodeTypeImpl);
		try {
			endpoint = (WSDLEndpoint) queryWSDLEndpoint.getSingleResult();
			
		} catch (final NoResultException e) {
			CoreInternalEndpointServiceImpl.LOG.info("No endpoint stored for requested IA.");
		}
		return endpoint;
	}
	
	@Override
	public List<WSDLEndpoint> getWSDLEndpointsForCSARID(final CSARID csarId) {
		final ArrayList<WSDLEndpoint> endpoints = new ArrayList<>();
		final Query queryWSDLEndpoint = this.em.createQuery("SELECT e FROM WSDLEndpoint e where e.csarId= :csarId");
		queryWSDLEndpoint.setParameter("csarId", csarId);
		
		@SuppressWarnings("unchecked")
		final List<WSDLEndpoint> queryResults = queryWSDLEndpoint.getResultList();
		for (final WSDLEndpoint endpoint : queryResults) {
			endpoints.add(endpoint);
		}
		
		return endpoints;
	}
	
	@Override
	public List<WSDLEndpoint> getWSDLEndpointsForNTImplAndIAName(final QName nodeTypeImpl, final String iaName) {
		final ArrayList<WSDLEndpoint> endpoints = new ArrayList<>();
		final Query queryWSDLEndpoint = this.em.createQuery("SELECT e FROM WSDLEndpoint e where e.IaName = :IaName and e.NodeTypeImplementation = :nodeTypeImpl");
		queryWSDLEndpoint.setParameter("IaName", iaName);
		queryWSDLEndpoint.setParameter("nodeTypeImpl", nodeTypeImpl);
		
		@SuppressWarnings("unchecked")
		final List<WSDLEndpoint> queryResults = queryWSDLEndpoint.getResultList();
		for (final WSDLEndpoint endpoint : queryResults) {
			endpoints.add(endpoint);
		}
		
		return endpoints;
	}
	
	@Override
	public List<WSDLEndpoint> getWSDLEndpoints() {
		final ArrayList<WSDLEndpoint> endpoints = new ArrayList<>();
		final Query queryWSDLEndpoint = this.em.createQuery("SELECT e FROM WSDLEndpoint e");
		
		@SuppressWarnings("unchecked")
		final List<WSDLEndpoint> queryResults = queryWSDLEndpoint.getResultList();
		for (final WSDLEndpoint endpoint : queryResults) {
			endpoints.add(endpoint);
		}
		
		return endpoints;
	}
	
	@Override
	public void printPlanEndpoints() {
		List<WSDLEndpoint> endpoints = null;
		final Query queryWSDLEndpoint = this.em.createQuery("SELECT e FROM WSDLEndpoint e");
		
		endpoints = queryWSDLEndpoint.getResultList();
		
		final StringBuilder builder = new StringBuilder();
		final String ls = System.getProperty("line.separator");
		builder.append("debug output for stored endpoints of management plans, flags: csarid, planid, ianame, porttype " + ls);
		for (final WSDLEndpoint endpoint : endpoints) {
			builder.append("endpoint: " + endpoint.getCSARId() + " " + endpoint.getPlanId() + " " + endpoint.getIaName() + " " + endpoint.getPortType() + ls);
		}
		CoreInternalEndpointServiceImpl.LOG.debug(builder.toString());
	}
	
	@Override
	public boolean removeWSDLEndpoint(final CSARID csarId, final WSDLEndpoint endpoint) {
		// get all wsdlendpoints
		final List<WSDLEndpoint> endpoints = this.getWSDLEndpointsForCSARID(csarId);
		
		if (!this.em.getTransaction().isActive()) {
			this.em.getTransaction().begin();
		}
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
