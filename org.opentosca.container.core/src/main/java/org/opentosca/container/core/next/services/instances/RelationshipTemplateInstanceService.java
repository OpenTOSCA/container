package org.opentosca.container.core.next.services.instances;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;

import org.opentosca.container.core.common.jpa.DocumentConverter;
import org.opentosca.container.core.engine.ToscaEngine;
import org.opentosca.container.core.model.ModelUtils;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.next.model.RelationshipTemplateInstance;
import org.opentosca.container.core.next.model.RelationshipTemplateInstanceProperty;
import org.opentosca.container.core.next.model.RelationshipTemplateInstanceState;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
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
 * Allows access to instance information for relationship templates.
 */
@Service
public class RelationshipTemplateInstanceService {

    private static final Logger logger = LoggerFactory.getLogger(RelationshipTemplateInstanceService.class);

    private final ServiceTemplateInstanceRepository serviceTemplateInstanceRepository;
    private final NodeTemplateInstanceRepository nodeTemplateInstanceRepository;
    private final RelationshipTemplateInstanceRepository relationshipTemplateInstanceRepository;

    private final RelationshipTemplateService relationshipTemplateService;
    private final CsarStorageService storage;

    private final DocumentConverter converter = new DocumentConverter();

    @Inject
    public RelationshipTemplateInstanceService(ServiceTemplateInstanceRepository serviceTemplateInstanceRepository,
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

    /**
     * Get all relationship template instances within the repository
     *
     * @return the collection of retrieved relationship template instances
     */
    public Collection<RelationshipTemplateInstance> getRelationshipTemplateInstances() {
        return this.relationshipTemplateInstanceRepository.findAll();
    }

    public Collection<RelationshipTemplateInstance> getRelationshipTemplateInstances(final String relationshipTemplateQName) {
        logger.debug("Requesting instances of RelationshipTemplate \"{}\"...", relationshipTemplateQName);
        return this.relationshipTemplateInstanceRepository.findByTemplateId(relationshipTemplateQName);
    }

    /**
     * Delete all relationship template instances for the given CSAR
     *
     * @param csar the CSAR to delete the relationship template instances for
     */
    public void deleteRelationshipTemplateInstances(final Csar csar) {
        relationshipTemplateInstanceRepository.deleteAll(getRelationshipTemplateInstances(csar));
    }

    /**
     * Get all relationship template instances for the given CSAR
     *
     * @param csar the CSAR to retrieve the relationship template instances for
     * @return the list of relationship template instances
     */
    public List<RelationshipTemplateInstance> getRelationshipTemplateInstances(final Csar csar) {
        final Collection<ServiceTemplateInstance> serviceInstances = serviceTemplateInstanceRepository.findWithRelationshipTemplateInstancesByCsarId(csar.id());
        return serviceInstances.stream().flatMap(sti -> sti.getRelationshipTemplateInstances().stream()).collect(Collectors.toList());
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
            return this.converter.convertToEntityAttribute(firstProp.get().getValue());
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
                                                                              final Long serviceInstanceId,
                                                                              final Long sourceNodeTemplateInstanceId,
                                                                              final Long targetNodeTemplateInstanceId) throws InstantiationException,
        IllegalAccessException,
        IllegalArgumentException {

        if (sourceNodeTemplateInstanceId == null || targetNodeTemplateInstanceId == null) {
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
        final Document propertiesAsDocument = ToscaEngine.getEntityTemplateProperties(relationshipTemplate);

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

        newInstance.setSource(this.nodeTemplateInstanceRepository.findWithOutgoingById(sourceNodeTemplateInstanceId).get());
        // Target node instance
        newInstance.setTarget(this.nodeTemplateInstanceRepository.findWithIncomingById(targetNodeTemplateInstanceId).get());
        newInstance.setServiceTemplateInstance(serviceTemplateInstanceRepository.findWithRelationshipTemplateInstancesById(serviceInstanceId).get());

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
