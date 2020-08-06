package org.opentosca.container.core.impl.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.xml.namespace.QName;

import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.model.endpoint.rest.RESTEndpoint;
import org.opentosca.container.core.model.endpoint.rest.RESTEndpoint.restMethod;
import org.opentosca.container.core.model.endpoint.wsdl.WSDLEndpoint;
import org.opentosca.container.core.next.jpa.EntityManagerProvider;
import org.opentosca.container.core.service.ICoreEndpointService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * This Class stores and retrieves Endpoint-Objects in the Database, using Eclipse-JPA.
 * <p>
 * For the JPA-Queries refer to: {@link RESTEndpoint}, {@link WSDLEndpoint}
 */
@Service
public class CoreEndpointServiceImpl implements ICoreEndpointService, AutoCloseable {
    private final static Logger LOG = LoggerFactory.getLogger(CoreEndpointServiceImpl.class);

    private EntityManager em;

    public CoreEndpointServiceImpl() {
        em = EntityManagerProvider.createEntityManager();
    }

    @Override
    /**
     * {@Inheritdoc}
     */
    public List<WSDLEndpoint> getWSDLEndpoints(final QName portType, final String triggeringContainer,
                                               final CsarId csarId) {
        final ArrayList<WSDLEndpoint> results = new ArrayList<>();

        final TypedQuery<WSDLEndpoint> getWSDLEndpointsQuery = em.createQuery(
            "SELECT e FROM WSDLEndpoint e where e.triggeringContainer = :triggeringContainer and e.csarId = :csarId and e.PortType = :portType",
            WSDLEndpoint.class);

        // Set Parameters for the Query
        getWSDLEndpointsQuery.setParameter("portType", portType);
        getWSDLEndpointsQuery.setParameter("triggeringContainer", triggeringContainer);
        getWSDLEndpointsQuery.setParameter("csarId", csarId);

        // Get Query-Results (WSDLEndpoints) and add them to the result list.
        final List<WSDLEndpoint> queryResults = getWSDLEndpointsQuery.getResultList();
        for (final WSDLEndpoint endpoint : queryResults) {
            results.add(endpoint);
        }

        // Hack, to get endpoints stored from the container e.g. the SI-Invoker
        // endpoint.
        // Set Parameters for the Query
        getWSDLEndpointsQuery.setParameter("portType", portType);
        getWSDLEndpointsQuery.setParameter("csarId", new CsarId(""));

        // Get Query-Results (WSDLEndpoints) and add them to the result list.
        final List<WSDLEndpoint> queryResults2 = getWSDLEndpointsQuery.getResultList();
        for (final WSDLEndpoint endpoint : queryResults2) {
            results.add(endpoint);
        }

        return results;
    }

    @Override
    /**
     * {@Inheritdoc}
     */
    public void storeWSDLEndpoint(final WSDLEndpoint endpoint) {

        // TODO this check is a hack because of the problem with deploying of multiple
        // deployment artifacts
        if (!existsWSDLEndpoint(endpoint)) {
            if (!this.em.getTransaction().isActive()) {
                this.em.getTransaction().begin();
            }
            LOG.debug("The endpoint for \"{}\" is not stored. Thus store it.", endpoint.getPortType());
            this.em.persist(endpoint);
            this.em.getTransaction().commit();
        } else {
            LOG.debug("The endpoint for \"{}\" is stored already.", endpoint.getPortType());
        }
    }

    /**
     * Helper method to check if a given WSDLEndpoint is already stored in the database
     *
     * @param endpoint to look for
     * @return true, if the Endpoint already exists.
     */
    private boolean existsWSDLEndpoint(final WSDLEndpoint endpoint) {
        TypedQuery<WSDLEndpoint> findQuery = em.createQuery(
            "SELECT e from WSDLEndpoint e where e.PortType = :portType "
                + "and e.csarId = :csarId and e.managingContainer = :managingContainer "
                + "and e.serviceTemplateInstanceID = :serviceTemplateInstanceID and e.PlanId = :planId",
            WSDLEndpoint.class);
        findQuery.setParameter("portType", endpoint.getPortType());
        findQuery.setParameter("csarId", endpoint.getCsarId());
        findQuery.setParameter("managingContainer", endpoint.getManagingContainer());
        findQuery.setParameter("serviceTemplateInstanceID", endpoint.getServiceTemplateInstanceID());
        findQuery.setParameter("planId", endpoint.getPlanId());

        try {
            @SuppressWarnings("unused")
            WSDLEndpoint dbResult = findQuery.getSingleResult();
            return true;
        } catch (NoResultException | NonUniqueResultException umm) {
            // maybe return true if result is not unique?
            return false;
        }
    }

    @Override
    /**
     * {@Inheritdoc}
     */
    public List<RESTEndpoint> getRestEndpoints(final URI anyURI, String triggeringContainer, final CsarId csarId) {
        final ArrayList<RESTEndpoint> results = new ArrayList<>();

        /**
         * Create Query to retrieve RESTEndpoints identified by a URI and thorID
         *
         * @see RESTEndpoint#getEndpointForPath
         **/
        final TypedQuery<RESTEndpoint> getRestEndpointsQuery = this.em.createNamedQuery(RESTEndpoint.getEndpointForPath,
            RESTEndpoint.class);

        // Set Parameters
        getRestEndpointsQuery.setParameter("path", anyURI.getPath());
        getRestEndpointsQuery.setParameter("triggeringContainer", triggeringContainer);
        getRestEndpointsQuery.setParameter("csarId", csarId);

        // Get Query-Results and add them to the result list
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
    public RESTEndpoint getRestEndpoint(final URI anyURI, final restMethod method, String triggeringContainer,
                                        final CsarId csarId) {
        /**
         * Create Query to retrieve a RestEndpoint
         *
         * @see RESTEndpoint#getEndpointForPathAndMethod
         */
        final TypedQuery<RESTEndpoint> getRestEndpointQuery = this.em
            .createNamedQuery(RESTEndpoint.getEndpointForPathAndMethod, RESTEndpoint.class);

        // Set parameters
        getRestEndpointQuery.setParameter("path", anyURI.getPath());
        getRestEndpointQuery.setParameter("triggeringContainer", triggeringContainer);
        getRestEndpointQuery.setParameter("method", method);
        getRestEndpointQuery.setParameter("csarId", csarId);

        // As a RESTEndpoint identified by URI, RestMethod and thorID
        // is unique, we only return one result (there cannot be more)
        return getRestEndpointQuery.getSingleResult();
    }

    @Override
    /**
     * {@Inheritdoc}
     */
    public void storeRESTEndpoint(final RESTEndpoint endpoint) {
        LOG.debug("Storing REST Endpoint with Path : \"{}\", STID: \"{}\"", endpoint.getPath(), endpoint.getCsarId());
        if (!this.em.getTransaction().isActive()) {
            this.em.getTransaction().begin();
        }
        this.em.persist(endpoint);
        this.em.getTransaction().commit();
    }

    @Override
    public WSDLEndpoint getWSDLEndpointForPlanId(String triggeringContainer, final CsarId csarId, final QName planId) {
        WSDLEndpoint endpoint = null;
        final TypedQuery<WSDLEndpoint> queryWSDLEndpoint = em.createQuery(
            "SELECT e FROM WSDLEndpoint e where e.csarId= :csarId and e.PlanId = :planId and e.triggeringContainer = :triggeringContainer",
            WSDLEndpoint.class);
        queryWSDLEndpoint.setParameter("csarId", csarId);
        queryWSDLEndpoint.setParameter("triggeringContainer", triggeringContainer);
        queryWSDLEndpoint.setParameter("planId", planId);

        try {
            endpoint = queryWSDLEndpoint.getSingleResult();
        } catch (final NoResultException e) {
            LOG.error("Query in database didn't return a result", e);
            return null;
        }

        return endpoint;
    }

    @Override
    public WSDLEndpoint getWSDLEndpointForIa(final CsarId csarId, final QName nodeTypeImpl, final String iaName) {
        WSDLEndpoint endpoint = null;
        final TypedQuery<WSDLEndpoint> queryWSDLEndpoint = em.createQuery(
            "SELECT e FROM WSDLEndpoint e where e.csarId= :csarId and e.IaName = :IaName and e.TypeImplementation = :nodeTypeImpl",
            WSDLEndpoint.class);
        queryWSDLEndpoint.setParameter("csarId", csarId);
        queryWSDLEndpoint.setParameter("IaName", iaName);
        queryWSDLEndpoint.setParameter("nodeTypeImpl", nodeTypeImpl);

        try {
            endpoint = (WSDLEndpoint) queryWSDLEndpoint.getSingleResult();
        } catch (final NoResultException e) {
            LOG.info("No endpoint stored for requested IA.");
        }
        return endpoint;
    }

    @Override
    public List<WSDLEndpoint> getWSDLEndpointsForCsarId(String triggeringContainer, final CsarId csarId) {
        final List<WSDLEndpoint> endpoints = new ArrayList<>();
        final TypedQuery<WSDLEndpoint> queryWSDLEndpoint = this.em.createQuery(
            "SELECT e FROM WSDLEndpoint e where e.csarId= :csarId and e.triggeringContainer = :triggeringContainer",
            WSDLEndpoint.class);
        queryWSDLEndpoint.setParameter("csarId", csarId);
        queryWSDLEndpoint.setParameter("triggeringContainer", triggeringContainer);

        final List<WSDLEndpoint> queryResults = queryWSDLEndpoint.getResultList();
        for (final WSDLEndpoint endpoint : queryResults) {
            endpoints.add(endpoint);
        }

        return endpoints;
    }

    @Override
    public List<WSDLEndpoint> getWSDLEndpointsForNTImplAndIAName(String triggeringContainer, String managingContainer,
                                                                 final QName nodeTypeImpl, final String iaName) {
        final List<WSDLEndpoint> endpoints = new ArrayList<>();
        final TypedQuery<WSDLEndpoint> queryWSDLEndpoint = this.em.createQuery(
            "SELECT e FROM WSDLEndpoint e where e.IaName = :IaName and e.TypeImplementation = :nodeTypeImpl and e.triggeringContainer = :triggeringContainer and e.managingContainer = :managingContainer",
            WSDLEndpoint.class);
        queryWSDLEndpoint.setParameter("IaName", iaName);
        queryWSDLEndpoint.setParameter("nodeTypeImpl", nodeTypeImpl);
        queryWSDLEndpoint.setParameter("triggeringContainer", triggeringContainer);
        queryWSDLEndpoint.setParameter("managingContainer", managingContainer);

        final List<WSDLEndpoint> queryResults = queryWSDLEndpoint.getResultList();
        for (final WSDLEndpoint endpoint : queryResults) {
            endpoints.add(endpoint);
        }

        return endpoints;
    }

    @Override
    public List<WSDLEndpoint> getWSDLEndpoints() {
        final List<WSDLEndpoint> endpoints = new ArrayList<>();
        final TypedQuery<WSDLEndpoint> queryWSDLEndpoint = this.em.createQuery("SELECT e FROM WSDLEndpoint e",
            WSDLEndpoint.class);

        final List<WSDLEndpoint> queryResults = queryWSDLEndpoint.getResultList();
        for (final WSDLEndpoint endpoint : queryResults) {
            endpoints.add(endpoint);
        }

        return endpoints;
    }

    @Override
    public void printPlanEndpoints() {
        List<WSDLEndpoint> endpoints = null;
        final TypedQuery<WSDLEndpoint> queryWSDLEndpoint = this.em.createQuery("SELECT e FROM WSDLEndpoint e",
            WSDLEndpoint.class);

        endpoints = queryWSDLEndpoint.getResultList();

        final StringBuilder builder = new StringBuilder();
        final String ls = System.getProperty("line.separator");
        builder.append(
            "debug output for stored endpoints of management plans, flags: csarid, planid, ianame, porttype " + ls);
        for (final WSDLEndpoint endpoint : endpoints) {
            builder.append("endpoint: " + endpoint.getCsarId() + " " + endpoint.getPlanId() + " " + endpoint.getIaName()
                + " " + endpoint.getPortType() + ls);
        }
        LOG.debug(builder.toString());
    }

    @Override
    public boolean removeWSDLEndpoint(final WSDLEndpoint endpoint) {
        if (!this.em.getTransaction().isActive()) {
            this.em.getTransaction().begin();
        }
        this.em.remove(endpoint);
        this.em.getTransaction().commit();
        return true;
    }

    @Override
    public List<WSDLEndpoint> getWSDLEndpointsForSTID(String triggeringContainer, Long serviceTemplateInstanceID) {
        final List<WSDLEndpoint> endpoints = new ArrayList<>();
        final TypedQuery<WSDLEndpoint> queryWSDLEndpoint = this.em.createQuery(
            "SELECT e FROM WSDLEndpoint e where e.triggeringContainer = :triggeringContainer and e.serviceTemplateInstanceID= :serviceTemplateInstanceID",
            WSDLEndpoint.class);
        queryWSDLEndpoint.setParameter("triggeringContainer", triggeringContainer);
        queryWSDLEndpoint.setParameter("serviceTemplateInstanceID", serviceTemplateInstanceID);

        final List<WSDLEndpoint> queryResults = queryWSDLEndpoint.getResultList();
        for (final WSDLEndpoint endpoint : queryResults) {
            endpoints.add(endpoint);
        }

        return endpoints;
    }

    @Override
    public void close() {
        em.close();
    }

    @Override
    public void removePlanEndpoints(String triggeringContainer, CsarId csarId) {
        if (!this.em.getTransaction().isActive()) {
            this.em.getTransaction().begin();
        }

        // get all plan endpoints (plan ID set) for the given csarid
        final Query queryWsdlEndpoints = this.em.createQuery(
            "SELECT e FROM WSDLEndpoint e where e.triggeringContainer = :triggeringContainer and e.csarId = :csarId and e.PlanId is not null");
        queryWsdlEndpoints.setParameter("triggeringContainer", triggeringContainer);
        queryWsdlEndpoints.setParameter("csarId", csarId);
        @SuppressWarnings("unchecked") final List<WSDLEndpoint> wsdlEndpoints = queryWsdlEndpoints.getResultList();

        // remove all found plan endpoints one by one
        for (final WSDLEndpoint wsdlEndpoint : wsdlEndpoints) {
            this.em.remove(wsdlEndpoint);
        }

        this.em.getTransaction().commit();
    }
}
