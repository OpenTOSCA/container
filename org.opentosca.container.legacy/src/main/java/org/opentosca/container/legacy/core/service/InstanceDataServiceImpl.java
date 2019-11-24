package org.opentosca.container.legacy.core.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.container.core.impl.persistence.Converters;
import org.opentosca.container.core.impl.persistence.DaoUtil;
import org.opentosca.container.core.impl.persistence.NodeInstanceDAO;
import org.opentosca.container.core.impl.persistence.RelationInstanceDAO;
import org.opentosca.container.core.impl.persistence.ServiceInstanceDAO;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.model.instance.IdConverter;
import org.opentosca.container.core.model.instance.NodeInstance;
import org.opentosca.container.core.model.instance.RelationInstance;
import org.opentosca.container.core.model.instance.ServiceInstance;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.RelationshipTemplateInstance;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.repository.NodeTemplateInstanceRepository;
import org.opentosca.container.core.next.repository.RelationshipTemplateInstanceRepository;
import org.opentosca.container.core.next.repository.ServiceTemplateInstanceRepository;
import org.opentosca.container.core.service.IInstanceDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * The InstanceDataService.<br>
 * The Engine offers a service to manage InstanceData for existing ServiceTemplates inside the
 * CSARs. It relies on the ToscaEngine to get its information about existence of those and for
 * values for the default properties of created instances.
 */
@Deprecated
@Service
public class InstanceDataServiceImpl implements IInstanceDataService {
  private static final Logger LOG = LoggerFactory.getLogger(InstanceDataServiceImpl.class);

  private final ServiceTemplateInstanceRepository serviceRepository = new ServiceTemplateInstanceRepository();
  private final NodeTemplateInstanceRepository nodeRepository = new NodeTemplateInstanceRepository();
  private final RelationshipTemplateInstanceRepository relationshipRepository = new RelationshipTemplateInstanceRepository();

  // used for persistence
  private final ServiceInstanceDAO siDAO = new ServiceInstanceDAO();
  private final NodeInstanceDAO niDAO = new NodeInstanceDAO();
  private final RelationInstanceDAO riDAO = new RelationInstanceDAO();

  @Override
  public List<ServiceInstance> getServiceInstances(final URI serviceInstanceID, final String serviceTemplateName,
                                                   final QName serviceTemplateId) {

    LOG.info("getServiceInstances(): {}", serviceInstanceID);
    LOG.info("getServiceInstances(): {}", serviceTemplateName);
    LOG.info("getServiceInstances(): {}", serviceTemplateId);

    if (serviceInstanceID != null) {
      Integer id = IdConverter.serviceInstanceUriToID(serviceInstanceID);
      if (id == null) {
        final String[] segments = serviceInstanceID.getPath().split("/");
        id = Integer.valueOf(segments[segments.length - 1]);
      }
      LOG.info("Using ServiceTemplate Instance ID: {}", id);
      final Optional<ServiceTemplateInstance> sti = this.serviceRepository.find(DaoUtil.toLong(id));
      if (sti.isPresent()) {
        LOG.info("Single Result: {}", sti);
        return Collections.singletonList(Converters.convert(sti.get()));
      } else {
        LOG.info("NOT FOUND");
      }
    }

    if (serviceTemplateName != null) {
      LOG.info("Using serviceTemplateId: {}", serviceTemplateId);
      final Collection<ServiceTemplateInstance> result = this.serviceRepository.findByTemplateId(serviceTemplateName);
      if (result != null) {
        LOG.info("Result: {}", result.size());
        return result.stream().map(Converters::convert).collect(Collectors.toList());
      }
    }
    return this.siDAO.getServiceInstances(serviceInstanceID, serviceTemplateName, serviceTemplateId);
  }

  @Override
  public List<ServiceInstance> getServiceInstancesWithDetails(final CsarId csarId, final String serviceTemplateId,
                                                              final Integer serviceTemplateInstanceID) {
    LOG.info("getServiceInstancesWithDetails(): {}", csarId);
    LOG.info("getServiceInstancesWithDetails(): {}", serviceTemplateId);
    LOG.info("getServiceInstancesWithDetails(): {}", serviceTemplateInstanceID);

    if (serviceTemplateInstanceID != null) {
      final Optional<ServiceTemplateInstance> sti = this.serviceRepository.find(DaoUtil.toLong(serviceTemplateInstanceID));
      if (sti.isPresent()) {
        LOG.info("Single Result: {}", sti);
        return Collections.singletonList(Converters.convert(sti.get()));
      } else {
        LOG.info("NOT FOUND");
      }
    }

    if (serviceTemplateId != null) {
      final Collection<ServiceTemplateInstance> result = this.serviceRepository.findByTemplateId(serviceTemplateId);
      if (result != null) {
        LOG.info("Result: {}", result.size());
        return result.stream().map(Converters::convert).collect(Collectors.toList());
      }
    }

    return this.siDAO.getServiceInstances(csarId, serviceTemplateId, serviceTemplateInstanceID);
  }

  @Override
  public List<NodeInstance> getNodeInstances(final URI nodeInstanceID, final String nodeTemplateID,
                                             final String nodeTemplateName, final URI serviceInstanceID) {
    LOG.info("getNodeInstances(): {}", nodeInstanceID);
    LOG.info("getNodeInstances(): {}", nodeTemplateID);
    LOG.info("getNodeInstances(): {}", nodeTemplateName);
    LOG.info("getNodeInstances(): {}", serviceInstanceID);

    if (nodeInstanceID != null) {
      Integer id = IdConverter.nodeInstanceUriToID(nodeInstanceID);
      if (id == null) {
        final String[] segments = serviceInstanceID.getPath().split("/");
        id = Integer.valueOf(segments[segments.length - 1]);
      }
      LOG.info("Using NodeTemplate Instance ID: {}", id);
      final Optional<NodeTemplateInstance> nti = this.nodeRepository.find(DaoUtil.toLong(id));
      if (nti.isPresent()) {
        LOG.info("Single Result: {}", nti);
        return Collections.singletonList(Converters.convert(nti.get()));
      } else {
        LOG.info("NOT FOUND");
      }
    }

    if (nodeTemplateID != null) {
      final Collection<NodeTemplateInstance> result = this.nodeRepository.findByTemplateId(nodeTemplateID);
      if (result != null) {
        LOG.info("Result: {}", result.size());
        return result.stream().map(nti -> Converters.convert(nti)).collect(Collectors.toList());
      }
    }

    if (serviceInstanceID != null) {
      Integer id = IdConverter.serviceInstanceUriToID(serviceInstanceID);
      if (id == null) {
        final String[] segments = serviceInstanceID.getPath().split("/");
        id = Integer.valueOf(segments[segments.length - 1]);
      }
      LOG.info("Using ServiceTemplate Instance ID: {}", id);
      final Optional<ServiceTemplateInstance> sti = this.serviceRepository.find(DaoUtil.toLong(id));
      if (sti.isPresent()) {
        final ServiceTemplateInstance i = sti.get();
        final Collection<NodeTemplateInstance> result = i.getNodeTemplateInstances();
        if (result != null) {
          LOG.info("Result: {}", result.size());
          return result.stream().map(nti -> Converters.convert(nti)).collect(Collectors.toList());
        }
      } else {
        LOG.info("NOT FOUND");
      }
    }

    return this.niDAO.getNodeInstances(serviceInstanceID, nodeTemplateID, nodeTemplateName, nodeInstanceID);
  }

  @Override
  public List<RelationInstance> getRelationInstances(final URI relationInstanceID, final QName relationshipTemplateID,
                                                     final String relationshipTemplateName,
                                                     final URI serviceInstanceID) {

    LOG.info("getRelationInstances(): {}", relationInstanceID);
    LOG.info("getRelationInstances(): {}", relationshipTemplateID);
    LOG.info("getRelationInstances(): {}", relationshipTemplateName);
    LOG.info("getRelationInstances(): {}", serviceInstanceID);

    if (relationInstanceID != null) {
      Integer id = IdConverter.relationInstanceUriToID(relationInstanceID);
      if (id == null) {
        final String[] segments = relationInstanceID.getPath().split("/");
        id = Integer.valueOf(segments[segments.length - 1]);
      }
      LOG.info("Using RelationshipTemplate Instance ID: {}", id);
      final Optional<RelationshipTemplateInstance> nti = this.relationshipRepository.find(DaoUtil.toLong(id));
      if (nti.isPresent()) {
        LOG.info("Single Result: {}", nti);
        return Collections.singletonList(Converters.convert(nti.get()));
      } else {
        LOG.info("NOT FOUND");
      }
    }

    if (relationshipTemplateID != null) {
      final Collection<RelationshipTemplateInstance> result =
        this.relationshipRepository.findByTemplateId(relationshipTemplateID.getLocalPart());
      if (result != null) {
        LOG.info("Result: {}", result.size());
        return result.stream().map(Converters::convert).collect(Collectors.toList());
      }
    }

    if (serviceInstanceID != null) {
      final Set<RelationshipTemplateInstance> rels = new HashSet<>();
      Integer id = IdConverter.serviceInstanceUriToID(serviceInstanceID);
      if (id == null) {
        final String[] segments = serviceInstanceID.getPath().split("/");
        id = Integer.valueOf(segments[segments.length - 1]);
      }
      LOG.info("Using ServiceTemplate Instance ID: {}", id);
      final Optional<ServiceTemplateInstance> sti = this.serviceRepository.find(DaoUtil.toLong(id));
      if (sti.isPresent()) {
        final ServiceTemplateInstance i = sti.get();
        final Collection<NodeTemplateInstance> result = i.getNodeTemplateInstances();
        if (result != null) {
          for (final NodeTemplateInstance nti : result) {
            rels.addAll(nti.getIncomingRelations());
            rels.addAll(nti.getOutgoingRelations());
          }
          LOG.info("Result: {}", rels.size());
          return rels.stream().map(Converters::convert).collect(Collectors.toList());
        }
      } else {
        LOG.info("NOT FOUND");
      }
    }

    return this.riDAO.getRelationInstances(serviceInstanceID, relationshipTemplateID, relationshipTemplateName,
      relationInstanceID);
  }

  private String generatePropertyValueFromConcatQuery(final String targetPropertyRef,
                                                      final List<NodeInstance> nodeInstance) {
    final String testQuery = targetPropertyRef.trim();
    if (!testQuery.endsWith(")")) {
      return null;
    }

    final int functionOpeningBracket = testQuery.indexOf("(");
    final String functionString = testQuery.substring(0, functionOpeningBracket);
    // simple validity check as we only want to be able to concat strings, but maybe more later
    if (!functionString.equals("concat")) {
      return null;
    }

    final String functionContent = testQuery.substring(functionOpeningBracket + 1, testQuery.lastIndexOf(")")).trim();
    final String[] functionParts = functionContent.split(",");
    final List<String> augmentedFunctionParts = new ArrayList<>();

    for (final String functionPart : functionParts) {
      if (functionPart.trim().startsWith("'")) {
        // string function part, just add to list
        augmentedFunctionParts.add(functionPart.trim());
      } else if (functionPart.trim().split("\\.").length == 3) {
        // "DSL" Query
        final String[] queryParts = functionPart.trim().split("\\.");
        // fast check for validity
        if (!queryParts[1].equals("Properties")) {
          return null;
        }

        final String nodeTemplateName = queryParts[0];
        final String propertyName = queryParts[2];
        NodeInstance namedInstance = getNodeInstanceWithName(nodeInstance, nodeTemplateName);
        if (namedInstance != null) {
          final String propValue = fetchPropertyValueFromNodeInstance(namedInstance, propertyName);
          augmentedFunctionParts.add("'" + propValue + "'");
        }
      }
    }
    return augmentedFunctionParts.stream().map(part -> part.replace("'", "")).collect(Collectors.joining());
  }

  private NodeInstance getNodeInstanceWithName(final List<NodeInstance> nodeInstances, final String nodeTemplateId) {
    for (final NodeInstance nodeInstance : nodeInstances) {
      if (nodeInstance.getNodeTemplateID().getLocalPart().equals(nodeTemplateId)) {
        return nodeInstance;
      }
    }
    return null;
  }

  /**
   * Creates an empty DOM document.
   *
   * @return An empty DOM document.
   */
  private static Document emptyDocument() {
    final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setNamespaceAware(true);
    try {
      final DocumentBuilder db = dbf.newDocumentBuilder();
      final Document doc = db.newDocument();
      return doc;
    } catch (final ParserConfigurationException e) {
      LOG.error(e.getMessage());
    }
    return null;
  }

  private String fetchPropertyValueFromNodeInstance(final NodeInstance nodeInstance, final String propertyLocalName) {
    if (nodeInstance.getProperties() == null) {
      return null;
    }

    final NodeList childNodes = nodeInstance.getProperties().getFirstChild().getChildNodes();
    for (int index = 0; index < childNodes.getLength(); index++) {
      final Node childNode = childNodes.item(index);
      if (childNode.getNodeType() == Node.ELEMENT_NODE && childNode.getLocalName().equals(propertyLocalName)) {
        return childNode.getTextContent();
      }
    }
    return null;
  }
}
