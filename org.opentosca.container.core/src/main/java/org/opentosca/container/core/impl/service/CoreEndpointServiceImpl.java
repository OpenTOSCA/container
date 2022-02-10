package org.opentosca.container.core.impl.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.xml.namespace.QName;

import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.model.endpoint.rest.RESTEndpoint;
import org.opentosca.container.core.next.jpa.EntityManagerProvider;
import org.opentosca.container.core.next.model.Endpoint;
import org.opentosca.container.core.next.repository.EndpointRepository;
import org.opentosca.container.core.service.ICoreEndpointService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * This Class stores and retrieves Endpoint-Objects in the Database
 */
@Service
public class CoreEndpointServiceImpl implements ICoreEndpointService, AutoCloseable {
    private final static Logger LOG = LoggerFactory.getLogger(CoreEndpointServiceImpl.class);

    private final EntityManager em;

    private final EndpointRepository endpointRepository;

    public CoreEndpointServiceImpl(EndpointRepository endpointRepository) {
        this.endpointRepository = endpointRepository;
        em = EntityManagerProvider.createEntityManager();
    }

    @Override
    /**
     * {@Inheritdoc}
     */
    public List<Endpoint> getEndpoints(final QName portType, final String triggeringContainer, final CsarId csarId) {
        return endpointRepository.findByPortTypeAndTriggeringContainerAndCsarId(portType, triggeringContainer, csarId);
    }

    @Override
    /**
     * {@Inheritdoc}
     */
    public void storeEndpoint(final Endpoint endpoint) {

        // TODO this check is a hack because of the problem with deploying of multiple deployment artifacts
        if (!existsEndpoint(endpoint)) {
            LOG.debug("The endpoint for \"{}\" is not stored. Thus store it.", endpoint.getPortType());
            endpointRepository.save(endpoint);
        } else {
            LOG.debug("The endpoint for \"{}\" is stored already.", endpoint.getPortType());
        }
    }

    /**
     * Helper method to check if a given Endpoint is already stored in the database
     *
     * @param endpoint to look for
     * @return true, if the Endpoint already exists.
     */
    private boolean existsEndpoint(final Endpoint endpoint) {
        return endpointRepository.findById(endpoint.getId()).isPresent();
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
    public List<Endpoint> getEndpointsForPlanId(String triggeringContainer, final CsarId csarId, final QName planId) {
        return endpointRepository.findByTriggeringContainerAndCsarIdAndPlanId(triggeringContainer, csarId, planId);
    }

    @Override
    public List<Endpoint> getEndpointsForNTImplAndIAName(String triggeringContainer, String managingContainer,
                                                         final QName nodeTypeImpl, final String iaName) {
        return endpointRepository.findByTriggeringContainerAndManagingContainerAndTypeImplementationAndIaName(triggeringContainer, managingContainer, nodeTypeImpl, iaName);
    }

    @Override
    public List<Endpoint> getEndpoints() {
        return endpointRepository.findAll();
    }

    @Override
    public void removeEndpoint(final Endpoint endpoint) {
        endpointRepository.delete(endpoint);
    }

    @Override
    public List<Endpoint> getEndpointsForSTID(String triggeringContainer, Long serviceTemplateInstanceID) {
        return endpointRepository.findByTriggeringContainerAndServiceTemplateInstanceID(triggeringContainer, serviceTemplateInstanceID);
    }

    @Override
    public void close() {
        em.close();
    }
}
