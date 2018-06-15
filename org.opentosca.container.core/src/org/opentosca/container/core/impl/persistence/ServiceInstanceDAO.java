package org.opentosca.container.core.impl.persistence;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.xml.namespace.QName;

import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.model.instance.ServiceInstance;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.model.ServiceTemplateInstanceState;
import org.opentosca.container.core.next.repository.ServiceTemplateInstanceRepository;
import org.opentosca.container.core.next.utils.Enums;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ServiceInstanceDAO {

    private static Logger logger = LoggerFactory.getLogger(ServiceInstanceDAO.class);

    ServiceTemplateInstanceRepository repository = new ServiceTemplateInstanceRepository();


    public void deleteServiceInstance(final ServiceInstance si) {
        try {
            logger.info("ServiceInstance: {}", si.toString());
            final Optional<ServiceTemplateInstance> o = this.repository.find(DaoUtil.toLong(si.getId()));
            if (o.isPresent()) {
                final ServiceTemplateInstance sti = o.get();
                sti.setState(ServiceTemplateInstanceState.DELETED);
                this.repository.update(sti);
                // repository.remove(sti);
                logger.debug("Deleted ServiceInstance with ID: " + si.getId());
            } else {
                logger.info("NOT FOUND");
            }
        }
        catch (final Exception e) {
            logger.error("Could not delete service instance: {}", e.getMessage(), e);
            e.printStackTrace();
        }
    }

    public ServiceInstance storeServiceInstance(final ServiceInstance serviceInstance) {
        try {
            logger.info("ServiceInstance: {}", serviceInstance.toString());
            final ServiceTemplateInstance sti = Converters.convert(serviceInstance);
            try {
                this.repository.add(sti);
            }
            catch (final Exception ex) {
                logger.info("Object already added, trying to update");
                this.repository.update(sti);
            }
            return Converters.convert(sti);
        }
        catch (final Exception e) {
            logger.error("Could not save node instance: {}", e.getMessage(), e);
            e.printStackTrace();
        }
        return serviceInstance;
    }

    public List<ServiceInstance> getServiceInstances(final URI serviceInstanceID, final String serviceTemplateName,
                                                     final QName serviceTemplateID) {

        logger.info("Not Implemented: Service instances cannot be queried");
        return new ArrayList<>();

        // this.init();
        //
        // // TODO: Use a query builder !!!
        // // final Query getServiceInstancesQuery =
        // // this.em.createNamedQuery(ServiceInstance.getServiceInstances);
        // final TypedQuery<ServiceInstance> getServiceInstancesQuery;
        //
        // Integer internalID = null;
        // if (serviceInstanceID != null) {
        // internalID = IdConverter.serviceInstanceUriToID(serviceInstanceID);
        // }
        //
        // // String serviceTemplateID_String = null;
        // // if (serviceTemplateID != null) {
        // // serviceTemplateID_String = serviceTemplateID.toString();
        // // }
        //
        // if (internalID == null) {
        // getServiceInstancesQuery = this.em.createQuery(
        // "FROM ServiceInstance s WHERE s.serviceTemplateName = :serviceTemplateName AND
        // s.serviceTemplateID = :serviceTemplateID",
        // ServiceInstance.class);
        // getServiceInstancesQuery.setParameter("serviceTemplateName", serviceTemplateName);
        // getServiceInstancesQuery.setParameter("serviceTemplateID", serviceTemplateID);
        // } else {
        // getServiceInstancesQuery =
        // this.em.createQuery("FROM ServiceInstance s WHERE s.id = :id", ServiceInstance.class);
        // getServiceInstancesQuery.setParameter("id", internalID);
        // }
        //
        // final List<ServiceInstance> queryResults = getServiceInstancesQuery.getResultList();
        //
        // return queryResults;
    }

    public List<ServiceInstance> getServiceInstances(final CSARID csarId, final QName serviceTemplateId,
                                                     final Integer serviceTemplateInstanceID) {

        logger.info("Not Implemented: Service instances cannot be queried");
        return new ArrayList<>();

        // this.init();
        //
        // logger.debug("Try to get Service Template instance objects from persistence for CSAR \"{}\"
        // Service Template \"{}\" Instance Id \"{}\"", csarId, serviceTemplateId,
        // serviceTemplateInstanceID);
        //
        // // final Query getServiceInstancesQuery =
        // // this.em.createNamedQuery(ServiceInstance.getServiceInstances);
        // // TODO: Use a query builder or something else, but please refactor
        // // this!!
        //
        // final String serviceTemplateName =
        // InstanceDataServiceImpl.toscaEngineService.getNameOfReference(csarId, serviceTemplateId);
        //
        // TypedQuery<ServiceInstance> getServiceInstancesQuery = null;
        //
        // if (serviceTemplateInstanceID == null) {
        // getServiceInstancesQuery = this.em.createQuery("FROM ServiceInstance s WHERE
        // s.serviceTemplateName = :serviceTemplateName AND s.serviceTemplateID = :serviceTemplateID",
        // ServiceInstance.class);
        // getServiceInstancesQuery.setParameter("serviceTemplateName", serviceTemplateName);
        // getServiceInstancesQuery.setParameter("serviceTemplateID", serviceTemplateId);
        // } else {
        // getServiceInstancesQuery = this.em.createQuery("FROM ServiceInstance s WHERE s.id = :id",
        // ServiceInstance.class);
        // getServiceInstancesQuery.setParameter("id", serviceTemplateInstanceID);
        // }
        //
        // final List<ServiceInstance> queryResults = getServiceInstancesQuery.getResultList();
        //
        // logger.debug("Found {} instance objects for Service Template instance of CSAR \"{}\" Service
        // Template \"{}\" Instance Id \"{}\"", queryResults.size(), csarId, serviceTemplateId,
        // serviceTemplateInstanceID);
        //
        // return queryResults;
    }

    /**
     * this method wraps the setting/saving of the state
     *
     * @param nodeInstance
     * @param state to be set
     */
    public void setState(final ServiceInstance serviceInstance, final String state) {

        try {
            logger.info("ServiceInstance: {}", serviceInstance.toString());
            final Optional<ServiceTemplateInstance> o = this.repository.find(DaoUtil.toLong(serviceInstance.getId()));
            if (o.isPresent()) {
                final ServiceTemplateInstance sti = o.get();
                sti.setState(Enums.valueOf(ServiceTemplateInstanceState.class, state,
                                           ServiceTemplateInstanceState.ERROR));
                this.repository.update(sti);
            } else {
                logger.info("NOT FOUND");
            }
        }
        catch (final Exception e) {
            logger.error("Could not update service instance: {}", e.getMessage(), e);
            e.printStackTrace();
        }
    }
}
