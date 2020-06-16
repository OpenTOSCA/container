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

import com.google.common.collect.Lists;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.opentosca.container.api.dto.NodeTemplateDTO;
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

    private static Logger logger = LoggerFactory.getLogger(InstanceService.class);

    private final ServiceTemplateInstanceRepository serviceTemplateInstanceRepository =
        new ServiceTemplateInstanceRepository();
    private final NodeTemplateInstanceRepository nodeTemplateInstanceRepository = new NodeTemplateInstanceRepository();
    private final RelationshipTemplateInstanceRepository relationshipTemplateInstanceRepository =
        new RelationshipTemplateInstanceRepository();

    // situations
    private final SituationRepository sitRepo = new SituationRepository();
    private final SituationTriggerRepository sitTrig = new SituationTriggerRepository();
    private final SituationTriggerInstanceRepository sitTrigInst = new SituationTriggerInstanceRepository();
    private final SituationsMonitorRepository situationsMonitorRepo = new SituationsMonitorRepository();

    private final RelationshipTemplateService relationshipTemplateService;
    private final NodeTemplateService nodeTemplateService;
    private final ServiceTemplateService serviceTemplateService;
    private final CsarStorageService storage;

    private final DocumentConverter converter = new DocumentConverter();

    @Inject
    public InstanceService(RelationshipTemplateService relationshipTemplateService, NodeTemplateService nodeTemplateService, ServiceTemplateService serviceTemplateService, CsarStorageService storage) {
        this.relationshipTemplateService = relationshipTemplateService;
        this.nodeTemplateService = nodeTemplateService;
        this.serviceTemplateService = serviceTemplateService;
        this.storage = storage;
    }

    public Document convertPropertyToDocument(final Property property) {
        return (Document) this.converter.convertToEntityAttribute(property.getValue());
    }

    /**
     * Converts an xml document to an xml-based property sui/table for service or node template instances
     */
    public <T extends Property> T convertDocumentToProperty(final Document propertyDoc,
                                                            final Class<T> type) throws InstantiationException,
        IllegalAccessException,
        IllegalArgumentException {

        if (propertyDoc == null) {
            final String msg =
                String.format("The set of parameters of an instance of type %s cannot be null", type.getName());
            logger.debug(msg);
            throw new IllegalArgumentException(msg);
        }
        final String propertyAsString = this.converter.convertToDatabaseColumn(propertyDoc);
        final T property = type.newInstance();
        property.setName("xml");
        property.setType("xml");
        property.setValue(propertyAsString);

        return property;
    }

    /* Service Template Instances */
    public Collection<ServiceTemplateInstance> getServiceTemplateInstances(final String serviceTemplate) {
        logger.debug("Requesting instances of ServiceTemplate \"{}\"...", serviceTemplate);
        return this.serviceTemplateInstanceRepository.findByTemplateId(serviceTemplate);
    }

    public ServiceTemplateInstance getServiceTemplateInstanceByCorrelationId(String correlationId) {
        return this.serviceTemplateInstanceRepository.findAll().stream()
            .filter(s -> s.getPlanInstances().stream()
                .anyMatch(p -> p.getCorrelationId().equals(correlationId)))
            .findFirst().get();
    }

    public ServiceTemplateInstance getServiceTemplateInstance(final Long id, final boolean evaluatePropertyMappings) {
        logger.debug("Requesting service template instance <{}>...", id);
        final Optional<ServiceTemplateInstance> instance = this.serviceTemplateInstanceRepository.find(id);

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
            logger.debug(msg);
            throw new IllegalArgumentException(msg, e);
        }

        final ServiceTemplateInstance service = getServiceTemplateInstance(id, false);
        service.setState(newState);
        this.serviceTemplateInstanceRepository.update(service);
    }

    public Document getServiceTemplateInstanceRawProperties(final Long id) throws NotFoundException {
        final ServiceTemplateInstance service = getServiceTemplateInstance(id, false);
        final Optional<ServiceTemplateInstanceProperty> firstProp = service.getProperties().stream().findFirst();

        if (firstProp.isPresent()) {
            return convertPropertyToDocument(firstProp.get());
        }

        final String msg = String.format("No properties are found for the service template instance <%s>", id);
        logger.debug(msg);

        return null;
    }

    public void setServiceTemplateInstanceProperties(final Long id,
                                                     final Document properties) throws ReflectiveOperationException {
        final ServiceTemplateInstance service = getServiceTemplateInstance(id, false);

        try {
            final ServiceTemplateInstanceProperty property =
                this.convertDocumentToProperty(properties, ServiceTemplateInstanceProperty.class);
            service.addProperty(property);
            this.serviceTemplateInstanceRepository.update(service);
        } catch (InstantiationException | IllegalAccessException e) { // This is not supposed to happen at all!
            final String msg = String.format("An error occurred while instantiating an instance of the %s class.",
                ServiceTemplateInstanceProperty.class);
            logger.debug(msg);
            throw e;
        }
    }

    public void deleteServiceTemplateInstance(final Long instanceId) {
        // throws exception if not found
        final ServiceTemplateInstance instance = getServiceTemplateInstance(instanceId, false);
        this.serviceTemplateInstanceRepository.remove(instance);
    }

    public ServiceTemplateInstance createServiceTemplateInstance(final String csarId, final String serviceTemplateName,
                                                                 final String correlationId) throws NotFoundException,
        InstantiationException,
        IllegalAccessException,
        IllegalArgumentException {
        final CsarId csar = this.serviceTemplateService.checkServiceTemplateExistence(csarId, serviceTemplateName);
        final PlanInstanceRepository repository = new PlanInstanceRepository();
        PlanInstance pi = null;

        try {
            pi = repository.findByCorrelationId(correlationId);
        } catch (final Exception e) {
            final String msg =
                String.format("The given correlation id %s is either malformed, does not belong to an existing plan instance",
                    correlationId);
            logger.info(msg);
            throw new NotFoundException(msg);
        }

        // If the found plan is a build plan there shouldn't be a service template instance available,
        // if it is a transformation plan the service instance mustn't be of the service template the new service instance should belong to
        if ((pi.getType().equals(PlanType.BUILD) && pi.getServiceTemplateInstance() == null)
            || (pi.getType().equals(PlanType.TRANSFORMATION) && !pi.getServiceTemplateInstance().getTemplateId().equals(serviceTemplateName))) {

            return this.createServiceTemplateInstance(csar, serviceTemplateName, pi);
        } else {
            final String msg = "The build plan instance is already associted with a service template instance!";
            logger.info(msg);
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
            convertDocumentToProperty(propertiesAsDoc, ServiceTemplateInstanceProperty.class);

        final ServiceTemplateInstance instance = new ServiceTemplateInstance();
        instance.setCsarId(csarId);
        instance.setTemplateId(serviceTemplateName);
        instance.setState(ServiceTemplateInstanceState.INITIAL);
        instance.addProperty(property);
        instance.addPlanInstance(buildPlanInstance);
        instance.setCreationCorrelationId(buildPlanInstance.getCorrelationId());
        this.serviceTemplateInstanceRepository.add(instance);
        new PlanInstanceRepository().update(buildPlanInstance);

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
            logger.info("Cannot create a new DocumentBuilder: {}", e.getMessage());
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
            logger.info("Node template instance <{}> could not be found", id);
            throw new NotFoundException(String.format("Node template instance <%s> could not be found", id));
        }

        return instance;
    }

    public NodeTemplateInstance getNodeTemplateInstance(final Long id) {
        logger.debug("Requesting node template instance <{}>...", id);
        final Optional<NodeTemplateInstance> instance = this.nodeTemplateInstanceRepository.find(id);

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
            logger.debug(msg);
            throw new IllegalArgumentException(msg, e);
        }

        final NodeTemplateInstance node = resolveNodeTemplateInstance(serviceTemplateName, nodeTemplateId, id);
        node.setState(newState);
        this.nodeTemplateInstanceRepository.update(node);
    }

    public Document getNodeTemplateInstanceProperties(final String serviceTemplateQName, final String nodeTemplateId,
                                                      final Long id) throws NotFoundException {
        final NodeTemplateInstance node = resolveNodeTemplateInstance(serviceTemplateQName, nodeTemplateId, id);
        final Optional<NodeTemplateInstanceProperty> firstProp = node.getProperties().stream().findFirst();

        if (firstProp.isPresent()) {
            return convertPropertyToDocument(firstProp.get());
        }

        final String msg = String.format("No properties are found for the node template instance <%s>", id);
        logger.debug(msg);
        return null;
    }

    public void setNodeTemplateInstanceProperties(final String serviceTemplateQName, final String nodeTemplateId,
                                                  final Long id,
                                                  final Document properties) throws ReflectiveOperationException {
        final NodeTemplateInstance node = resolveNodeTemplateInstance(serviceTemplateQName, nodeTemplateId, id);

        try {
            final NodeTemplateInstanceProperty property =
                this.convertDocumentToProperty(properties, NodeTemplateInstanceProperty.class);
            node.addProperty(property);
            this.nodeTemplateInstanceRepository.update(node);
        } catch (InstantiationException | IllegalAccessException e) { // This is not supposed to happen at all!
            final String msg = String.format("An error occurred while instantiating an instance of the %s class.",
                NodeTemplateInstanceProperty.class);
            logger.debug(msg);
            throw e;
        }
    }

    public NodeTemplateInstance createNewNodeTemplateInstance(final String csarId,
                                                              final String serviceTemplateNameAsString,
                                                              final String nodeTemplateId,
                                                              final Long serviceTemplateInstanceId) throws InstantiationException,
        IllegalAccessException,
        IllegalArgumentException {
        final Csar csar = storage.findById(new CsarId(csarId));
        final TServiceTemplate serviceTemplate;
        final TNodeTemplate nodeTemplate;
        try {
            serviceTemplate = ToscaEngine.resolveServiceTemplate(csar, serviceTemplateNameAsString);
            nodeTemplate = ToscaEngine.resolveNodeTemplate(serviceTemplate, nodeTemplateId);
        } catch (org.opentosca.container.core.common.NotFoundException e) {
            throw new NotFoundException(e.getMessage(), e);
        }
        final NodeTemplateDTO dto = nodeTemplateService.createNodeTemplate(nodeTemplate, csar);
        final Document propertiesAsDocument = ToscaEngine.getEntityTemplateProperties(nodeTemplate);

        // Properties
        // We set the properties of the template as initial properties
        final NodeTemplateInstance newInstance = new NodeTemplateInstance();
        if (propertiesAsDocument != null) {
            final NodeTemplateInstanceProperty properties =
                this.convertDocumentToProperty(propertiesAsDocument, NodeTemplateInstanceProperty.class);
            newInstance.addProperty(properties);
        }
        // State
        newInstance.setState(NodeTemplateInstanceState.INITIAL);
        // Template
        newInstance.setTemplateId(nodeTemplate.getIdFromIdOrNameField());
        // Type
        newInstance.setTemplateType(QName.valueOf(dto.getNodeType()));
        // ServiceTemplateInstance
        final ServiceTemplateInstance serviceTemplateInstance = getServiceTemplateInstance(serviceTemplateInstanceId, false);

        // only compare the local Id, because ServiceTemplateInstance does not keep the
        // fully namespaced QName as the parent Id (which sucks, but it is what it is for now)
        if (!serviceTemplateInstance.getTemplateId().equals(serviceTemplate.getIdFromIdOrNameField())) {
            final String msg =
                String.format("Service template instance id <%s> does not belong to service template: %s",
                    serviceTemplateInstanceId, serviceTemplate.getName());
            logger.debug(msg);
            throw new IllegalArgumentException(msg);
        }
        newInstance.setServiceTemplateInstance(serviceTemplateInstance);

        this.nodeTemplateInstanceRepository.add(newInstance);

        return newInstance;
    }

    public void deleteNodeTemplateInstance(final String serviceTemplateQName, final String nodeTemplateId,
                                           final Long id) {
        // throws exception if not found
        final NodeTemplateInstance instance = resolveNodeTemplateInstance(serviceTemplateQName, nodeTemplateId, id);
        this.nodeTemplateInstanceRepository.remove(instance);
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
            logger.info("Relationship template instance <{}> could not be found", instanceId);
            throw new NotFoundException(
                String.format("Relationship template instance <%s> could not be found", instanceId));
        }

        return instance;
    }

    private RelationshipTemplateInstance getRelationshipTemplateInstance(final Long id) {
        logger.debug("Requesting relationship template instance <{}>...", id);
        final Optional<RelationshipTemplateInstance> instance = this.relationshipTemplateInstanceRepository.find(id);

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
            logger.debug(msg);
            throw new IllegalArgumentException(msg, e);
        }

        final RelationshipTemplateInstance relationship =
            resolveRelationshipTemplateInstance(serviceTemplateQName, relationshipTemplateId, id);
        relationship.setState(newState);
        this.relationshipTemplateInstanceRepository.update(relationship);
    }

    public Document getRelationshipTemplateInstanceProperties(final String serviceTemplateQName,
                                                              final String relationshipTemplateId,
                                                              final Long id) throws NotFoundException {
        final RelationshipTemplateInstance relationship =
            resolveRelationshipTemplateInstance(serviceTemplateQName, relationshipTemplateId, id);
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
            resolveRelationshipTemplateInstance(serviceTemplateQName, relationshipTemplateId, id);

        try {
            final RelationshipTemplateInstanceProperty property =
                this.convertDocumentToProperty(properties, RelationshipTemplateInstanceProperty.class);
            relationship.addProperty(property);
            this.relationshipTemplateInstanceRepository.update(relationship);
        } catch (InstantiationException | IllegalAccessException e) { // This is not supposed to happen at all!
            final String msg = String.format("An error occurred while instantiating an instance of the %s class.",
                RelationshipTemplateInstanceProperty.class);
            logger.debug(msg);
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
            logger.info(msg);
            throw new IllegalArgumentException(msg);
        }

        final RelationshipTemplateInstance newInstance = new RelationshipTemplateInstance();
        final RelationshipTemplateDTO dto =
            this.relationshipTemplateService.getRelationshipTemplateById(csarId, serviceTemplateName,
                relationshipTemplateId);

        // Properties
        // We set the properties of the template as initial properties
        final Document propertiesAsDocument =
            this.relationshipTemplateService.getPropertiesOfRelationshipTemplate(csarId, serviceTemplateName,
                relationshipTemplateId);

        if (propertiesAsDocument != null) {
            final RelationshipTemplateInstanceProperty properties =
                this.convertDocumentToProperty(propertiesAsDocument, RelationshipTemplateInstanceProperty.class);
            newInstance.addProperty(properties);
        }
        // State
        newInstance.setState(RelationshipTemplateInstanceState.INITIAL);
        // Template
        newInstance.setTemplateId(relationshipTemplateId);
        // Type
        newInstance.setTemplateType(QName.valueOf(dto.getRelationshipType()));
        // Source node instance
        newInstance.setSource(getNodeTemplateInstance(request.getSourceNodeTemplateInstanceId()));
        // Target node instance
        newInstance.setTarget(getNodeTemplateInstance(request.getTargetNodeTemplateInstanceId()));
        newInstance.setServiceTemplateInstance(serviceTemplateInstanceRepository.find(request.getServiceInstanceId()).get());

        this.relationshipTemplateInstanceRepository.add(newInstance);

        return newInstance;
    }

    public void deleteRelationshipTemplateInstance(final String serviceTemplateQName,
                                                   final String relationshipTemplateId, final Long instanceId) {
        // throws exception if not found
        final RelationshipTemplateInstance instance =
            resolveRelationshipTemplateInstance(serviceTemplateQName, relationshipTemplateId, instanceId);
        this.relationshipTemplateInstanceRepository.remove(instance);
    }

    /* Situations */
    public Situation createNewSituation(final String thingId, final String situationTemplateId, final boolean active,
                                        final float eventProbability, final String eventTime) {
        final Situation newInstance = new Situation();

        newInstance.setSituationTemplateId(situationTemplateId);
        newInstance.setThingId(thingId);
        newInstance.setActive(active);
        newInstance.setEventProbability(eventProbability);
        newInstance.setEventTime(eventTime);

        this.sitRepo.add(newInstance);

        return newInstance;
    }

    public Situation getSituation(final Long id) {
        final Optional<Situation> instance = this.sitRepo.find(id);
        if (instance.isPresent()) {
            return instance.get();
        }
        throw new NotFoundException("Situation <" + id + "> not found.");
    }

    public Collection<Situation> getSituations() {
        return this.sitRepo.findAll();
    }

    public Collection<SituationTrigger> getSituationTriggers() {
        return this.sitTrig.findAll();
    }

    public Collection<SituationTrigger> getSituationTriggers(final Situation situation) {
        return this.sitTrig.findSituationTriggersBySituationId(situation.getId());
    }

    public SituationTrigger createNewSituationTrigger(final Collection<Situation> situations, final CsarId csarId,
                                                      final boolean triggerOnActivation, final boolean isSingleInstance,
                                                      final ServiceTemplateInstance serviceInstance,
                                                      final NodeTemplateInstance nodeInstance,
                                                      final String interfaceName, final String operationName,
                                                      final Set<SituationTriggerProperty> inputs,
                                                      final float eventProbability, final String eventTime) {
        final SituationTrigger newInstance = new SituationTrigger();

        newInstance.setSituations(situations);
        newInstance.setCsarId(csarId);
        newInstance.setTriggerOnActivation(triggerOnActivation);
        newInstance.setSingleInstance(isSingleInstance);
        newInstance.setServiceInstance(serviceInstance);
        newInstance.setInterfaceName(interfaceName);
        newInstance.setOperationName(operationName);
        if (nodeInstance != null) {
            newInstance.setNodeInstance(nodeInstance);
        }

        for (SituationTriggerProperty input : inputs) {
            input.setSituationTrigger(newInstance);
        }

        newInstance.setInputs(inputs);

        if (eventProbability != -1.0f) {
            newInstance.setEventProbability(eventProbability);
        }

        if (eventTime != null) {
            newInstance.setEventTime(eventTime);
        }

        this.sitTrig.add(newInstance);

        return newInstance;
    }

    public SituationTrigger getSituationTrigger(final Long id) {
        final Optional<SituationTrigger> opt = this.sitTrig.find(id);

        if (opt.isPresent()) {
            return opt.get();
        }

        throw new NotFoundException("SituationTrigger <" + id + "> not found.");
    }

    public Collection<SituationTriggerInstance> geSituationTriggerInstances(final SituationTrigger trigger) {
        final Collection<SituationTriggerInstance> triggerInstances = Lists.newArrayList();
        for (final SituationTriggerInstance triggerInstance : this.sitTrigInst.findAll()) {
            if (triggerInstance.getSituationTrigger().equals(trigger)) {
                triggerInstances.add(triggerInstance);
            }
        }
        return triggerInstances;
    }

    public void updateSituation(final Situation situation) {
        this.sitRepo.update(situation);
    }

    public SituationTriggerInstance getSituationTriggerInstance(final Long id) {
        return this.sitTrigInst.find(id)
            .orElseThrow(() -> new RuntimeException("SituationTriggerInstance <" + id + "> not found."));
    }

    public SituationsMonitor createNewSituationsMonitor(final ServiceTemplateInstance instance,
                                                        final Map<String, Collection<Long>> situations) {
        final SituationsMonitor monitor = new SituationsMonitor();

        monitor.setServiceInstance(instance);

        monitor.setNode2Situations(situations);

        this.situationsMonitorRepo.add(monitor);
        return monitor;
    }

    public Collection<SituationsMonitor> getSituationsMonitors() {
        return this.situationsMonitorRepo.findAll();
    }

    public Collection<SituationsMonitor> getSituationsMonitors(final Long serviceInstanceID) {
        return this.getSituationsMonitors().stream()
            .filter(monitor -> monitor.getServiceInstance() != null
                && monitor.getServiceInstance().getId().equals(serviceInstanceID))
            .collect(Collectors.toList());
    }
}
