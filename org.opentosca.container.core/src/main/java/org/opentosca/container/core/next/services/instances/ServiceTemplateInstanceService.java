package org.opentosca.container.core.next.services.instances;

import java.util.Collection;
import java.util.Optional;

import javax.ws.rs.NotFoundException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.container.core.model.ModelUtils;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.PlanInstance;
import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.model.ServiceTemplateInstanceProperty;
import org.opentosca.container.core.next.model.ServiceTemplateInstanceState;
import org.opentosca.container.core.next.repository.PlanInstanceRepository;
import org.opentosca.container.core.next.repository.ServiceTemplateInstanceRepository;
import org.opentosca.container.core.next.services.templates.ServiceTemplateService;
import org.opentosca.container.core.next.utils.PropertyMappingsHelper;
import org.opentosca.container.core.service.CsarStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@Service
public class ServiceTemplateInstanceService {

    private static final Logger logger = LoggerFactory.getLogger(ServiceTemplateInstanceService.class);

    private final ServiceTemplateInstanceRepository serviceTemplateInstanceRepository;
    private final ServiceTemplateService serviceTemplateService;
    private final PlanInstanceRepository planInstanceRepository;
    private final PlanInstanceService planInstanceService;

    private final PropertyMappingsHelper helper;

    public ServiceTemplateInstanceService(ServiceTemplateInstanceRepository serviceTemplateInstanceRepository,
                                          ServiceTemplateService serviceTemplateService,
                                          PlanInstanceRepository planInstanceRepository,
                                          PlanInstanceService planInstanceService, CsarStorageService storage) {
        this.serviceTemplateInstanceRepository = serviceTemplateInstanceRepository;
        this.serviceTemplateService = serviceTemplateService;
        this.planInstanceRepository = planInstanceRepository;
        this.planInstanceService = planInstanceService;

        helper = new PropertyMappingsHelper(storage);
    }

    /**
     * Get all service template instances within the repository
     *
     * @return the collection of retrieved service template instances
     */
    public Collection<ServiceTemplateInstance> getServiceTemplateInstances() {
        return this.serviceTemplateInstanceRepository.findAll();
    }

    public Collection<ServiceTemplateInstance> getServiceTemplateInstances(final String serviceTemplate) {
        logger.debug("Requesting instances of ServiceTemplate \"{}\"...", serviceTemplate);
        return this.serviceTemplateInstanceRepository.findByTemplateId(serviceTemplate);
    }

    /**
     * Delete all service template instances for the given CSAR
     *
     * @param csar the CSAR to delete the service template instances for
     */
    public void deleteServiceTemplateInstances(final Csar csar) {
        this.serviceTemplateInstanceRepository.deleteAll(serviceTemplateInstanceRepository.findByCsarId(csar.id()));
    }

    public ServiceTemplateInstance getServiceTemplateInstance(final Long id, final boolean evaluatePropertyMappings) {
        logger.debug("Requesting service template instance <{}>...", id);
        final Optional<ServiceTemplateInstance> instance = this.serviceTemplateInstanceRepository.findWithNodeAndRelationshipTemplateInstancesById(id);

        if (instance.isPresent()) {
            final ServiceTemplateInstance result = instance.get();

            if (evaluatePropertyMappings) {
                helper.evaluatePropertyMappings(result);
            }

            return result;
        }

        logger.debug("Service Template Instance <" + id + "> not found.");
        throw new NotFoundException("Service Template Instance <" + id + "> not found.");
    }

    public ServiceTemplateInstanceState getServiceTemplateInstanceState(final Long id) {
        final ServiceTemplateInstance service = getServiceTemplateInstance(id, false);

        return service.getState();
    }

    public void setServiceTemplateInstanceState(final Long id, final String state) throws NotFoundException,
        IllegalArgumentException {

        ServiceTemplateInstanceState newState;
        try {
            newState = ServiceTemplateInstanceState.valueOf(state);
        } catch (final Exception e) {
            final String msg =
                String.format("The given state %s is an illegal service template instance state.", state);
            logger.error(msg, e);
            throw new IllegalArgumentException(msg, e);
        }

        final ServiceTemplateInstance service = getServiceTemplateInstance(id, false);
        service.setState(newState);
        this.serviceTemplateInstanceRepository.save(service);
    }

    public void setServiceTemplateInstanceProperties(final Long id,
                                                     final Document properties) throws ReflectiveOperationException {
        final ServiceTemplateInstance service = getServiceTemplateInstance(id, false);

        try {
            final ServiceTemplateInstanceProperty property =
                ModelUtils.convertDocumentToProperty(properties, ServiceTemplateInstanceProperty.class);
            service.addProperty(property);
            this.serviceTemplateInstanceRepository.save(service);
        } catch (InstantiationException | IllegalAccessException e) { // This is not supposed to happen at all!
            final String msg = String.format("An error occurred while instantiating an instance of the %s class.",
                ServiceTemplateInstanceProperty.class);
            logger.error(msg, e);
            throw e;
        }
    }

    public void deleteServiceTemplateInstance(final Long instanceId) {
        // throws exception if not found
        final ServiceTemplateInstance instance = getServiceTemplateInstance(instanceId, false);
        this.serviceTemplateInstanceRepository.delete(instance);
    }

    public ServiceTemplateInstance createServiceTemplateInstance(final String csarId, final String serviceTemplateName) throws InstantiationException, IllegalAccessException, IllegalArgumentException {
        final CsarId csar = this.serviceTemplateService.checkServiceTemplateExistence(csarId, serviceTemplateName);
        final Document propertiesAsDoc =
            createServiceInstanceInitialPropertiesFromServiceTemplate(csar, serviceTemplateName);
        final ServiceTemplateInstanceProperty property =
            ModelUtils.convertDocumentToProperty(propertiesAsDoc, ServiceTemplateInstanceProperty.class);

        final ServiceTemplateInstance instance = new ServiceTemplateInstance();
        instance.setCsarId(csar);
        instance.setTemplateId(serviceTemplateName);
        instance.setState(ServiceTemplateInstanceState.INITIAL);
        instance.addProperty(property);

        this.serviceTemplateInstanceRepository.save(instance);

        return instance;
    }

    public ServiceTemplateInstance createServiceTemplateInstance(final String csarId, final String serviceTemplateName,
                                                                 final String correlationId) throws NotFoundException,
        InstantiationException,
        IllegalAccessException,
        IllegalArgumentException {
        final CsarId csar = this.serviceTemplateService.checkServiceTemplateExistence(csarId, serviceTemplateName);

        PlanInstance pi = (PlanInstance) this.planInstanceService.waitForInstanceAvailable(correlationId).joinAndGet(30000);

        // if no instance was found it is possible that live-modeling was started, just create an empty instance
        if (pi == null) {
            return this.createServiceTemplateInstance(csarId, serviceTemplateName);
        }

        // If the found plan is a build plan there shouldn't be a service template instance available,
        // if it is a transformation plan the service instance mustn't be of the service template the new service instance should belong to
        if ((pi.getType().equals(PlanType.BUILD) && pi.getServiceTemplateInstance() == null)
            || (pi.getType().equals(PlanType.TRANSFORMATION) && !pi.getServiceTemplateInstance().getTemplateId().equals(serviceTemplateName))) {

            return this.createServiceTemplateInstance(csar, serviceTemplateName, pi);
        } else {
            final String msg = "The build plan instance is already associted with a service template instance!";
            logger.error(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    private ServiceTemplateInstance createServiceTemplateInstance(final CsarId csarId, final String serviceTemplateName,
                                                                  final PlanInstance buildPlanInstance) throws InstantiationException,
        IllegalAccessException,
        IllegalArgumentException {
        final Document propertiesAsDoc =
            createServiceInstanceInitialPropertiesFromServiceTemplate(csarId, serviceTemplateName);
        final ServiceTemplateInstanceProperty property =
            ModelUtils.convertDocumentToProperty(propertiesAsDoc, ServiceTemplateInstanceProperty.class);

        ServiceTemplateInstance instance = new ServiceTemplateInstance();
        instance.setCsarId(csarId);
        instance.setTemplateId(serviceTemplateName);
        instance.setState(ServiceTemplateInstanceState.INITIAL);
        instance.addProperty(property);
        instance.addPlanInstance(buildPlanInstance);
        instance.setCreationCorrelationId(buildPlanInstance.getCorrelationId());

        instance = this.serviceTemplateInstanceRepository.save(instance);

        if (buildPlanInstance.getServiceTemplateInstance() == null) {
            buildPlanInstance.setServiceTemplateInstance(instance);
        }
        planInstanceRepository.save(buildPlanInstance);

        return instance;
    }

    private Document createServiceInstanceInitialPropertiesFromServiceTemplate(final CsarId csarId,
                                                                               final String serviceTemplateId) {

        final Document existingProperties =
            this.serviceTemplateService.getPropertiesOfServiceTemplate(csarId, serviceTemplateId);

        if (existingProperties != null) {
            return existingProperties;
        }

        logger.debug("No Properties found in BoundaryDefinitions for ST {} thus creating blank ones",
            serviceTemplateId);
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db;
        try {
            db = dbf.newDocumentBuilder();
            final Document doc = db.newDocument();
            final Element createElementNS =
                doc.createElementNS("http://docs.oasis-open.org/tosca/ns/2011/12", "Properties");
            createElementNS.setAttribute("xmlns:tosca", "http://docs.oasis-open.org/tosca/ns/2011/12");
            createElementNS.setPrefix("tosca");
            doc.appendChild(createElementNS);

            return doc;
        } catch (final ParserConfigurationException e) {
            logger.error("Cannot create a new DocumentBuilder: {}", e.getMessage());
            return null;
        }
    }
}
