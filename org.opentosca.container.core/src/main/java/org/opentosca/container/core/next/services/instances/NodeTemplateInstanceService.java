package org.opentosca.container.core.next.services.instances;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ws.rs.NotFoundException;

import org.opentosca.container.core.common.jpa.DocumentConverter;
import org.opentosca.container.core.model.ModelUtils;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.NodeTemplateInstanceProperty;
import org.opentosca.container.core.next.model.NodeTemplateInstanceState;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.repository.NodeTemplateInstanceRepository;
import org.opentosca.container.core.next.repository.ServiceTemplateInstanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

@Service
public class NodeTemplateInstanceService {

    private static final Logger logger = LoggerFactory.getLogger(NodeTemplateInstanceService.class);

    private final NodeTemplateInstanceRepository nodeTemplateInstanceRepository;
    private final ServiceTemplateInstanceRepository serviceTemplateInstanceRepository;

    public NodeTemplateInstanceService(NodeTemplateInstanceRepository nodeTemplateInstanceRepository, ServiceTemplateInstanceRepository serviceTemplateInstanceRepository) {
        this.nodeTemplateInstanceRepository = nodeTemplateInstanceRepository;
        this.serviceTemplateInstanceRepository = serviceTemplateInstanceRepository;
    }

    public Collection<NodeTemplateInstance> getNodeTemplateInstances(final String nodeTemplateName) {
        logger.debug("Requesting instances of NodeTemplate \"{}\"...", nodeTemplateName);
        return this.nodeTemplateInstanceRepository.findByTemplateId(nodeTemplateName);
    }

    public Collection<NodeTemplateInstance> getAllNodeTemplateInstances() {
        logger.debug("Requesting all NodeTemplate instances");
        return this.nodeTemplateInstanceRepository.findAll();
    }

    /**
     * Delete all node template instances for the given CSAR
     *
     * @param csar the CSAR to delete the node template instances for
     */
    public void deleteNodeTemplateInstances(final Csar csar) {
        this.nodeTemplateInstanceRepository.deleteAll(getNodeTemplateInstances(csar));
    }

    /**
     * Get all node template instances for the given CSAR
     *
     * @param csar the CSAR to retrieve the node template instances for
     * @return the list of node template instances
     */
    public List<NodeTemplateInstance> getNodeTemplateInstances(final Csar csar) {
        final Collection<ServiceTemplateInstance> serviceInstances = serviceTemplateInstanceRepository.findWithNodeTemplateInstancesByCsarId(csar.id());
        return serviceInstances.stream().flatMap(sti -> sti.getNodeTemplateInstances().stream()).collect(Collectors.toList());
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
            return new DocumentConverter().convertToEntityAttribute(firstProp.get().getValue());
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
}
