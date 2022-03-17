package org.opentosca.container.api.service;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.winery.model.tosca.TExportedOperation;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;

import org.opentosca.container.api.dto.RelationshipTemplateDTO;
import org.opentosca.container.api.dto.request.CreateRelationshipTemplateInstanceRequest;
import org.opentosca.container.core.common.jpa.DocumentConverter;
import org.opentosca.container.core.engine.ToscaEngine;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.NodeTemplateInstanceProperty;
import org.opentosca.container.core.next.model.NodeTemplateInstanceState;
import org.opentosca.container.core.next.model.PlanInstance;
import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.container.core.next.model.Property;
import org.opentosca.container.core.next.model.RelationshipTemplateInstance;
import org.opentosca.container.core.next.model.RelationshipTemplateInstanceProperty;
import org.opentosca.container.core.next.model.RelationshipTemplateInstanceState;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.model.ServiceTemplateInstanceProperty;
import org.opentosca.container.core.next.model.ServiceTemplateInstanceState;
import org.opentosca.container.core.next.model.Situation;
import org.opentosca.container.core.next.model.SituationTrigger;
import org.opentosca.container.core.next.model.SituationTriggerInstance;
import org.opentosca.container.core.next.model.SituationTriggerProperty;
import org.opentosca.container.core.next.model.SituationsMonitor;
import org.opentosca.container.core.next.repository.NodeTemplateInstanceRepository;
import org.opentosca.container.core.next.repository.PlanInstanceRepository;
import org.opentosca.container.core.next.repository.RelationshipTemplateInstanceRepository;
import org.opentosca.container.core.next.repository.ServiceTemplateInstanceRepository;
import org.opentosca.container.core.next.repository.SituationRepository;
import org.opentosca.container.core.next.repository.SituationTriggerInstanceRepository;
import org.opentosca.container.core.next.repository.SituationTriggerRepository;
import org.opentosca.container.core.next.repository.SituationsMonitorRepository;
import org.opentosca.container.core.service.CsarStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Allows access to instance information for service templates and node templates.
 */
@Service
public class InstanceService {

    private static final Logger logger = LoggerFactory.getLogger(InstanceService.class);

    private final ServiceTemplateInstanceRepository serviceTemplateInstanceRepository;
    private final NodeTemplateInstanceRepository nodeTemplateInstanceRepository;
    private final RelationshipTemplateInstanceRepository relationshipTemplateInstanceRepository;

    private final RelationshipTemplateService relationshipTemplateService;
    private final ServiceTemplateService serviceTemplateService;
    private final CsarStorageService storage;
    private final PlanInstanceRepository planInstanceRepository;
    private final PlanService planService;

    private final DocumentConverter converter = new DocumentConverter();

    @Inject
    public InstanceService(ServiceTemplateInstanceRepository serviceTemplateInstanceRepository,
                           NodeTemplateInstanceRepository nodeTemplateInstanceRepository,
                           RelationshipTemplateInstanceRepository relationshipTemplateInstanceRepository,
                           RelationshipTemplateService relationshipTemplateService,
                           ServiceTemplateService serviceTemplateService,
                           CsarStorageService storage, PlanInstanceRepository planInstanceRepository, PlanService planService) {
        this.serviceTemplateInstanceRepository = serviceTemplateInstanceRepository;
        this.nodeTemplateInstanceRepository = nodeTemplateInstanceRepository;
        this.relationshipTemplateInstanceRepository = relationshipTemplateInstanceRepository;
        this.relationshipTemplateService = relationshipTemplateService;
        this.serviceTemplateService = serviceTemplateService;
        this.storage = storage;
        this.planInstanceRepository = planInstanceRepository;
        this.planService = planService;
    }

    public Document convertPropertyToDocument(final Property property) {
        return this.converter.convertToEntityAttribute(property.getValue());
    }

    /* Service Template Instances */
    public Collection<ServiceTemplateInstance> getServiceTemplateInstances(final String serviceTemplate) {
        logger.debug("Requesting instances of ServiceTemplate \"{}\"...", serviceTemplate);
        return this.serviceTemplateInstanceRepository.findByTemplateId(serviceTemplate);
    }

    public ServiceTemplateInstance getServiceTemplateInstance(final Long id, final boolean evaluatePropertyMappings) {
        logger.debug("Requesting service template instance <{}>...", id);
        final Optional<ServiceTemplateInstance> instance = this.serviceTemplateInstanceRepository.findWithNodeAndRelationshipTemplateInstancesById(id);

        if (instance.isPresent()) {
            final ServiceTemplateInstance result = instance.get();

            if (evaluatePropertyMappings) {
                final PropertyMappingsHelper helper = new PropertyMappingsHelper(this, storage);
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
                Utils.convertDocumentToProperty(properties, ServiceTemplateInstanceProperty.class);
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
            Utils.convertDocumentToProperty(propertiesAsDoc, ServiceTemplateInstanceProperty.class);

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

        PlanInstance pi = (PlanInstance) this.planService.waitForInstanceAvailable(correlationId).joinAndGet(30000);

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
            Utils.convertDocumentToProperty(propertiesAsDoc, ServiceTemplateInstanceProperty.class);

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
        }

        return null; // this should never happen
    }

    /* Node Template Instances */
    public Collection<NodeTemplateInstance> getNodeTemplateInstances(final String nodeTemplateName) {
        logger.debug("Requesting instances of NodeTemplate \"{}\"...", nodeTemplateName);
        return this.nodeTemplateInstanceRepository.findByTemplateId(nodeTemplateName);
    }

    public Collection<NodeTemplateInstance> getAllNodeTemplateInstances() {
        logger.debug("Requesting all NodeTemplate instances");
        return this.nodeTemplateInstanceRepository.findAll();
    }

    public NodeTemplateInstance resolveNodeTemplateInstance(final String serviceTemplateName,
                                                            final String nodeTemplateId, final Long id) {
        // We only need to check that the instance belongs to the template, the rest is
        // guaranteed while this is a sub-resource
        final NodeTemplateInstance instance = getNodeTemplateInstance(id);
        if (!(instance.getTemplateId().equals(nodeTemplateId)
            && instance.getServiceTemplateInstance().getTemplateId().equals(serviceTemplateName))) {
            logger.error("Node template instance <{}> could not be found", id);
            throw new NotFoundException(String.format("Node template instance <%s> could not be found", id));
        }

        return instance;
    }

    public NodeTemplateInstance getNodeTemplateInstance(final Long id) {
        logger.debug("Requesting node template instance <{}>...", id);
        final Optional<NodeTemplateInstance> instance = this.nodeTemplateInstanceRepository.findById(id);

        if (instance.isPresent()) {
            return instance.get();
        }

        logger.debug("Node Template Instance <" + id + "> not found.");
        throw new NotFoundException("Node Template Instance <" + id + "> not found.");
    }

    public NodeTemplateInstanceState getNodeTemplateInstanceState(final String serviceTemplateQName,
                                                                  final String nodeTemplateId, final Long id) {
        final NodeTemplateInstance node = resolveNodeTemplateInstance(serviceTemplateQName, nodeTemplateId, id);

        return node.getState();
    }

    public void setNodeTemplateInstanceState(final String serviceTemplateName, final String nodeTemplateId,
                                             final Long id,
                                             final String state) throws NotFoundException, IllegalArgumentException {

        NodeTemplateInstanceState newState;
        try {
            newState = NodeTemplateInstanceState.valueOf(state);
        } catch (final Exception e) {
            final String msg = String.format("The given state %s is an illegal node template instance state.", state);
            logger.error(msg, e);
            throw new IllegalArgumentException(msg, e);
        }

        final NodeTemplateInstance node = resolveNodeTemplateInstance(serviceTemplateName, nodeTemplateId, id);
        node.setState(newState);
        this.nodeTemplateInstanceRepository.save(node);
    }

    public Document getNodeTemplateInstanceProperties(final String serviceTemplateQName, final String nodeTemplateId,
                                                      final Long id) throws NotFoundException {

        final NodeTemplateInstance node = this.nodeTemplateInstanceRepository.findWithPropertiesById(id).get();
        final Optional<NodeTemplateInstanceProperty> firstProp = node.getProperties().stream().findFirst();

        if (firstProp.isPresent()) {
            return convertPropertyToDocument(firstProp.get());
        }

        final String msg = String.format("No properties are found for the node template instance <%s>", id);
        logger.debug(msg);
        return null;
    }

    public Map<String, String> getNodeTemplateInstanceProperties(final Long id) throws NotFoundException {
        final NodeTemplateInstance node = this.nodeTemplateInstanceRepository.findWithPropertiesById(id).get();
        final Optional<NodeTemplateInstanceProperty> firstProp = node.getProperties().stream().findFirst();

        if (firstProp.isPresent()) {
            return node.getPropertiesAsMap();
        }

        final String msg = String.format("No properties are found for the node template instance <%s>", id);
        logger.debug(msg);
        return null;
    }

    public void setNodeTemplateInstanceProperties(final String serviceTemplateQName, final String nodeTemplateId,
                                                  final Long id,
                                                  final Document properties) throws ReflectiveOperationException {
        final NodeTemplateInstance node = this.nodeTemplateInstanceRepository.findWithPropertiesById(id).get();

        try {
            final NodeTemplateInstanceProperty property =
                Utils.convertDocumentToProperty(properties, NodeTemplateInstanceProperty.class);
            node.addProperty(property);
            this.nodeTemplateInstanceRepository.save(node);
        } catch (InstantiationException | IllegalAccessException e) { // This is not supposed to happen at all!
            final String msg = String.format("An error occurred while instantiating an instance of the %s class.",
                NodeTemplateInstanceProperty.class);
            logger.error(msg, e);
            throw e;
        }
    }

    public void deleteNodeTemplateInstance(final String serviceTemplateQName, final String nodeTemplateId,
                                           final Long id) {
        // throws exception if not found
        final NodeTemplateInstance instance = resolveNodeTemplateInstance(serviceTemplateQName, nodeTemplateId, id);
        this.nodeTemplateInstanceRepository.delete(instance);
    }

    /* Relationship Template Instances */
    public Collection<RelationshipTemplateInstance> getRelationshipTemplateInstances(final String relationshipTemplateQName) {
        logger.debug("Requesting instances of RelationshipTemplate \"{}\"...", relationshipTemplateQName);
        return this.relationshipTemplateInstanceRepository.findByTemplateId(relationshipTemplateQName);
    }

    /**
     * Gets a reference to the relationship template instance. Ensures that the instance actually belongs to the
     * relationship template.
     *
     * @throws NotFoundException if the instance does not belong to the relationship template
     */
    public RelationshipTemplateInstance resolveRelationshipTemplateInstance(final String serviceTemplateName,
                                                                            final String relationshipTemplateId,
                                                                            final Long instanceId) throws NotFoundException {
        // We only need to check that the instance belongs to the template, the rest is
        // guaranteed while this is a sub-resource
        final RelationshipTemplateInstance instance = getRelationshipTemplateInstance(instanceId);
        if (!(instance.getTemplateId().equals(relationshipTemplateId)
            && instance.getServiceTemplateInstance().getTemplateId().equals(serviceTemplateName))) {
            logger.error("Relationship template instance <{}> could not be found", instanceId);
            throw new NotFoundException(
                String.format("Relationship template instance <%s> could not be found", instanceId));
        }

        return instance;
    }

    private RelationshipTemplateInstance getRelationshipTemplateInstance(final Long id) {
        logger.debug("Requesting relationship template instance <{}>...", id);
        final Optional<RelationshipTemplateInstance> instance = this.relationshipTemplateInstanceRepository.findById(id);

        if (instance.isPresent()) {
            return instance.get();
        }

        logger.debug("Relationship Template Instance <" + id + "> not found.");
        throw new NotFoundException("Relationship Template Instance <" + id + "> not found.");
    }

    public RelationshipTemplateInstanceState getRelationshipTemplateInstanceState(final String serviceTemplateQName,
                                                                                  final String relationshipTemplateId,
                                                                                  final Long id) {
        final RelationshipTemplateInstance relationship =
            resolveRelationshipTemplateInstance(serviceTemplateQName, relationshipTemplateId, id);

        return relationship.getState();
    }

    public void setRelationshipTemplateInstanceState(final String serviceTemplateQName,
                                                     final String relationshipTemplateId, final Long id,
                                                     final String state) throws NotFoundException,
        IllegalArgumentException {
        RelationshipTemplateInstanceState newState;
        try {
            newState = RelationshipTemplateInstanceState.valueOf(state);
        } catch (final Exception e) {
            final String msg =
                String.format("The given state %s is an illegal relationship template instance state.", state);
            logger.error(msg, e);
            throw new IllegalArgumentException(msg, e);
        }

        final RelationshipTemplateInstance relationship =
            resolveRelationshipTemplateInstance(serviceTemplateQName, relationshipTemplateId, id);
        relationship.setState(newState);
        this.relationshipTemplateInstanceRepository.save(relationship);
    }

    public Document getRelationshipTemplateInstanceProperties(final String serviceTemplateQName,
                                                              final String relationshipTemplateId,
                                                              final Long id) throws NotFoundException {
        final RelationshipTemplateInstance relationship =
            this.relationshipTemplateInstanceRepository.findWithPropertiesById(id);
        final Optional<RelationshipTemplateInstanceProperty> firstProp =
            relationship.getProperties().stream().findFirst();

        if (firstProp.isPresent()) {
            return convertPropertyToDocument(firstProp.get());
        }

        final String msg = String.format("No properties are found for the relationship template instance <%s>", id);
        logger.debug(msg);

        return null;
    }

    public void setRelationshipTemplateInstanceProperties(final String serviceTemplateQName,
                                                          final String relationshipTemplateId, final Long id,
                                                          final Document properties) throws ReflectiveOperationException {
        final RelationshipTemplateInstance relationship =
            this.relationshipTemplateInstanceRepository.findWithPropertiesById(id);

        try {
            final RelationshipTemplateInstanceProperty property =
                Utils.convertDocumentToProperty(properties, RelationshipTemplateInstanceProperty.class);
            relationship.addProperty(property);
            this.relationshipTemplateInstanceRepository.save(relationship);
        } catch (InstantiationException | IllegalAccessException e) { // This is not supposed to happen at all!
            final String msg = String.format("An error occurred while instantiating an instance of the %s class.",
                RelationshipTemplateInstanceProperty.class);
            logger.error(msg, e);
            throw e;
        }
    }

    public RelationshipTemplateInstance createNewRelationshipTemplateInstance(final String csarId,
                                                                              final String serviceTemplateName,
                                                                              final String relationshipTemplateId,
                                                                              final CreateRelationshipTemplateInstanceRequest request) throws InstantiationException,
        IllegalAccessException,
        IllegalArgumentException {

        if (request == null || request.getSourceNodeTemplateInstanceId() == null
            || request.getTargetNodeTemplateInstanceId() == null) {
            final String msg = "Relationship template instance creation request is empty or missing content";
            logger.error(msg);
            throw new IllegalArgumentException(msg);
        }

        final Csar csar = storage.findById(new CsarId(csarId));
        final TServiceTemplate serviceTemplate;
        final TRelationshipTemplate relationshipTemplate;
        try {
            serviceTemplate = ToscaEngine.resolveServiceTemplate(csar, serviceTemplateName);
            relationshipTemplate = ToscaEngine.resolveRelationshipTemplate(serviceTemplate, relationshipTemplateId);
        } catch (org.opentosca.container.core.common.NotFoundException e) {
            throw new NotFoundException(e.getMessage(), e);
        }

        RelationshipTemplateInstance newInstance = new RelationshipTemplateInstance();
        final RelationshipTemplateDTO dto =
            this.relationshipTemplateService.getRelationshipTemplateById(csarId, serviceTemplateName,
                relationshipTemplateId);

        // Properties
        // We set the properties of the template as initial properties
        final Document propertiesAsDocument =
            ToscaEngine.getEntityTemplateProperties(relationshipTemplate);

        if (propertiesAsDocument != null) {
            final RelationshipTemplateInstanceProperty properties =
                Utils.convertDocumentToProperty(propertiesAsDocument, RelationshipTemplateInstanceProperty.class);
            newInstance.addProperty(properties);
        }
        // State
        newInstance.setState(RelationshipTemplateInstanceState.INITIAL);
        // Template
        newInstance.setTemplateId(relationshipTemplateId);
        // Type
        newInstance.setTemplateType(QName.valueOf(dto.getRelationshipType()));
        // Source node instance

        newInstance.setSource(this.nodeTemplateInstanceRepository.findWithOutgoingById(request.getSourceNodeTemplateInstanceId()).get());
        // Target node instance
        newInstance.setTarget(this.nodeTemplateInstanceRepository.findWithIncomingById(request.getTargetNodeTemplateInstanceId()).get());
        newInstance.setServiceTemplateInstance(serviceTemplateInstanceRepository.findWithRelationshipTemplateInstancesById(request.getServiceInstanceId()).get());

        newInstance = this.relationshipTemplateInstanceRepository.save(newInstance);

        return newInstance;
    }

    public void deleteRelationshipTemplateInstance(final String serviceTemplateQName,
                                                   final String relationshipTemplateId, final Long instanceId) {
        // throws exception if not found
        final RelationshipTemplateInstance instance =
            resolveRelationshipTemplateInstance(serviceTemplateQName, relationshipTemplateId, instanceId);
        this.relationshipTemplateInstanceRepository.delete(instance);
    }
}
