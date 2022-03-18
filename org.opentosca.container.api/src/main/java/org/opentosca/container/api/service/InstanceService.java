package org.opentosca.container.api.service;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;

import org.opentosca.container.api.dto.request.CreateRelationshipTemplateInstanceRequest;
import org.opentosca.container.core.common.jpa.DocumentConverter;
import org.opentosca.container.core.engine.ToscaEngine;
import org.opentosca.container.core.model.ModelUtils;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.NodeTemplateInstanceProperty;
import org.opentosca.container.core.next.model.NodeTemplateInstanceState;
import org.opentosca.container.core.next.model.Property;
import org.opentosca.container.core.next.model.RelationshipTemplateInstance;
import org.opentosca.container.core.next.model.RelationshipTemplateInstanceProperty;
import org.opentosca.container.core.next.model.RelationshipTemplateInstanceState;
import org.opentosca.container.core.next.repository.NodeTemplateInstanceRepository;
import org.opentosca.container.core.next.repository.RelationshipTemplateInstanceRepository;
import org.opentosca.container.core.next.repository.ServiceTemplateInstanceRepository;
import org.opentosca.container.core.next.services.templates.RelationshipTemplateService;
import org.opentosca.container.core.service.CsarStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

/**
 * Allows access to instance information for service templates and node templates.
 */
// TODO: split node template and relationship template handling in separate repos
@Service
public class InstanceService {

    private static final Logger logger = LoggerFactory.getLogger(InstanceService.class);

    private final ServiceTemplateInstanceRepository serviceTemplateInstanceRepository;
    private final NodeTemplateInstanceRepository nodeTemplateInstanceRepository;
    private final RelationshipTemplateInstanceRepository relationshipTemplateInstanceRepository;

    private final RelationshipTemplateService relationshipTemplateService;
    private final CsarStorageService storage;

    private final DocumentConverter converter = new DocumentConverter();

    @Inject
    public InstanceService(ServiceTemplateInstanceRepository serviceTemplateInstanceRepository,
                           NodeTemplateInstanceRepository nodeTemplateInstanceRepository,
                           RelationshipTemplateInstanceRepository relationshipTemplateInstanceRepository,
                           RelationshipTemplateService relationshipTemplateService,
                           CsarStorageService storage) {
        this.serviceTemplateInstanceRepository = serviceTemplateInstanceRepository;
        this.nodeTemplateInstanceRepository = nodeTemplateInstanceRepository;
        this.relationshipTemplateInstanceRepository = relationshipTemplateInstanceRepository;
        this.relationshipTemplateService = relationshipTemplateService;
        this.storage = storage;
    }

    public Document convertPropertyToDocument(final Property property) {
        return this.converter.convertToEntityAttribute(property.getValue());
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
        return resolveNodeTemplateInstance(serviceTemplateQName, nodeTemplateId, id).getState();
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

    public Document getNodeTemplateInstancePropertiesDocument(final Long id) throws NotFoundException {

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

    public void setNodeTemplateInstanceProperties(final Long id, final Document properties) throws ReflectiveOperationException {
        final NodeTemplateInstance node = this.nodeTemplateInstanceRepository.findWithPropertiesById(id).get();

        try {
            final NodeTemplateInstanceProperty property =
                ModelUtils.convertDocumentToProperty(properties, NodeTemplateInstanceProperty.class);
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

    public Document getRelationshipTemplateInstanceProperties(final Long id) throws NotFoundException {
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

    public void setRelationshipTemplateInstanceProperties(final Long id, final Document properties) throws ReflectiveOperationException {
        final RelationshipTemplateInstance relationship =
            this.relationshipTemplateInstanceRepository.findWithPropertiesById(id);

        try {
            final RelationshipTemplateInstanceProperty property =
                ModelUtils.convertDocumentToProperty(properties, RelationshipTemplateInstanceProperty.class);
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
        final QName relationshipTypeQName = this.relationshipTemplateService.getRelationshipTemplateById(csarId, serviceTemplateName,
            relationshipTemplateId).getType();

        // Properties
        // We set the properties of the template as initial properties
        final Document propertiesAsDocument =
            ToscaEngine.getEntityTemplateProperties(relationshipTemplate);

        if (propertiesAsDocument != null) {
            final RelationshipTemplateInstanceProperty properties =
                ModelUtils.convertDocumentToProperty(propertiesAsDocument, RelationshipTemplateInstanceProperty.class);
            newInstance.addProperty(properties);
        }
        // State
        newInstance.setState(RelationshipTemplateInstanceState.INITIAL);
        // Template
        newInstance.setTemplateId(relationshipTemplateId);
        // Type
        newInstance.setTemplateType(relationshipTypeQName);
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

    public void deleteInstancesForCsar(final CsarId csarId) {
        logger.debug("Deleting all instances related to CSAR with ID: {}", csarId);

        // TODO
    }
}
