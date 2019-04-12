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
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.opentosca.container.core.common.ReferenceNotFoundException;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.engine.NodeTemplateInstanceCounts;
import org.opentosca.container.core.engine.xml.IXMLSerializerService;
import org.opentosca.container.core.impl.persistence.Converters;
import org.opentosca.container.core.impl.persistence.DaoUtil;
import org.opentosca.container.core.impl.persistence.NodeInstanceDAO;
import org.opentosca.container.core.impl.persistence.RelationInstanceDAO;
import org.opentosca.container.core.impl.persistence.ServiceInstanceDAO;
import org.opentosca.container.core.model.csar.id.CSARID;
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
import org.eclipse.winery.model.tosca.TBoundaryDefinitions;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TPropertyMapping;
import org.opentosca.container.legacy.core.engine.IToscaEngineService;
import org.opentosca.container.legacy.core.engine.IToscaReferenceMapper;
import org.opentosca.container.legacy.core.engine.ToscaEngineServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * The InstanceDataService.<br>
 * The Engine offers a service to manage InstanceData for existing ServiceTemplates inside the
 * CSARs. It relies on the ToscaEngine to get its information about existence of those and for
 * values for the default properties of created instances.
 */
@Deprecated
@WebService(name = "InstanceDataService")
@SOAPBinding(style = SOAPBinding.Style.RPC)
@Service
public class InstanceDataServiceImpl implements IInstanceDataService {
  private static final Logger LOG = LoggerFactory.getLogger(InstanceDataServiceImpl.class);

  private final IToscaEngineService toscaEngineService;

  private final ServiceTemplateInstanceRepository serviceRepository = new ServiceTemplateInstanceRepository();
  private final NodeTemplateInstanceRepository nodeRepository = new NodeTemplateInstanceRepository();
  private final RelationshipTemplateInstanceRepository relationshipRepository = new RelationshipTemplateInstanceRepository();

  // used for persistence
  private final ServiceInstanceDAO siDAO = new ServiceInstanceDAO();
  private final NodeInstanceDAO niDAO = new NodeInstanceDAO();
  private final RelationInstanceDAO riDAO = new RelationInstanceDAO();

  @Inject
  public InstanceDataServiceImpl(IToscaEngineService toscaEngineService) {
    this.toscaEngineService = toscaEngineService;
  }

  @Override
  @WebMethod(exclude = true)
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

    if (serviceTemplateId != null) {
      LOG.info("Using serviceTemplateId: {}", serviceTemplateId);
      final Collection<ServiceTemplateInstance> result =
        this.serviceRepository.findByTemplateId(serviceTemplateId);
      if (result != null) {
        LOG.info("Result: {}", result.size());
        return result.stream().map(Converters::convert).collect(Collectors.toList());
      }
    }
    return this.siDAO.getServiceInstances(serviceInstanceID, serviceTemplateName, serviceTemplateId);
  }

  @Override
  @WebMethod(exclude = true)
  public List<ServiceInstance> getServiceInstancesWithDetails(final CSARID csarId, final QName serviceTemplateId,
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
      final Collection<ServiceTemplateInstance> result =
        this.serviceRepository.findByTemplateId(serviceTemplateId);
      if (result != null) {
        LOG.info("Result: {}", result.size());
        return result.stream().map(Converters::convert).collect(Collectors.toList());
      }
    }

    return this.siDAO.getServiceInstances(csarId, serviceTemplateId, serviceTemplateInstanceID);
  }

  @Override
  @WebMethod(exclude = true)
  public ServiceInstance createServiceInstance(final CSARID csarID,
                                               final QName serviceTemplateID) throws ReferenceNotFoundException {
    LOG.info("createServiceInstance(): {}", csarID);
    LOG.info("createServiceInstance(): {}", serviceTemplateID);

    LOG.debug("Starting creating ServiceInstance for " + serviceTemplateID + " in " + csarID);
    // TODO: boolean flag for cascading creation? cool or not?
    // check if serviceTemplate doesn't exist
    if (!doesServiceTemplateExist(toscaEngineService, csarID, serviceTemplateID)) {
      LOG.warn(String.format("Failed to create ServiceInstance for CSAR-ID: %s / serviceTemplateID: %s - was not found!", csarID, serviceTemplateID));
      throw new ReferenceNotFoundException("ServiceTemplate doesn't exist in the specified CSAR");
    }
    // retrieve serviceTemplateName
    final String serviceTemplateName = toscaEngineService.getNameOfReference(csarID, serviceTemplateID);
    // get all min and maxCounts from the ServiceTemplate and construct
    // nodeInstances from it automatically
    final NodeTemplateInstanceCounts instanceCounts = toscaEngineService.getInstanceCountsOfNodeTemplatesByServiceTemplateID(csarID, serviceTemplateID);

    // creation of real objects
    ServiceInstance serviceInstance = new ServiceInstance(csarID, serviceTemplateID, serviceTemplateName);

    // construct initial properties of serviceTemplate
    final Document properties = createServiceInstancePropertiesFromServiceTemplate(csarID, serviceTemplateID);

    serviceInstance.setProperties(properties);

    // store serviceInstance so we can use nodeInstanceDAO to create
    // nodeInstances (they need an existing object because its working in
    // another transaction)
    serviceInstance = this.siDAO.storeServiceInstance(serviceInstance);
    // TODO: or is it better to get the alternative route? to do it in one
    // transaction? and have duplicate code? need to fetch nodeTemplateName
    // from toscaEngine f.ex.?

    return serviceInstance;
  }

  @Override
  @WebMethod(exclude = true)
  public void deleteServiceInstance(final URI serviceInstanceID) {
    LOG.info("deleteServiceInstance(): {}", serviceInstanceID);
    final List<ServiceInstance> serviceInstances = getServiceInstances(serviceInstanceID, null, null);

    if (serviceInstances == null || serviceInstances.size() != 1) {
      LOG.warn(String.format("Failed to delete ServiceInstance: '%s' - could not be retrieved", serviceInstanceID));
      return;
    }
    this.siDAO.deleteServiceInstance(serviceInstances.get(0));
  }

  @Override
  @WebMethod(exclude = true)
  public List<NodeInstance> getNodeInstances(final URI nodeInstanceID, final QName nodeTemplateID,
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
        this.relationshipRepository.findByTemplateId(relationshipTemplateID);
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

  @Override
  @WebMethod(exclude = true)
  public NodeInstance createNodeInstance(final CSARID csarId, final QName serviceTemplateId,
                                         final int serviceTemplateInstanceID,
                                         final QName nodeTemplateID) throws ReferenceNotFoundException {
    LOG.info("createNodeInstance(): {}", csarId);
    LOG.info("createNodeInstance(): {}", serviceTemplateId);
    LOG.info("createNodeInstance(): {}", serviceTemplateInstanceID);
    LOG.info("createNodeInstance(): {}", nodeTemplateID);

    LOG.debug("Retrieve Node Template \"{{}}\":\"{}\" for csar \"{}\", Service Template \"{}\" instance \"{}\"",
      nodeTemplateID.getNamespaceURI(), nodeTemplateID.getLocalPart(), csarId, serviceTemplateId,
      serviceTemplateInstanceID);

    final List<ServiceInstance> serviceInstances =
      getServiceInstancesWithDetails(csarId, serviceTemplateId, serviceTemplateInstanceID);
    if (serviceInstances == null || serviceInstances.size() != 1) {
      final String msg =
        String.format("Failed to create NodeInstance: ServiceInstance: '%s' - could not be retrieved",
          serviceTemplateInstanceID);
      LOG.warn(msg);
      throw new ReferenceNotFoundException(msg);
    }
    final ServiceInstance serviceInstance = serviceInstances.get(0);

    // check if nodeTemplate exists
    if (!toscaEngineService.doesNodeTemplateExist(csarId, serviceTemplateId, nodeTemplateID.getLocalPart())) {
      final String msg = String.format("Failed to create NodeInstance: NodeTemplate: csar: %s serviceTemplateID: %s , nodeTemplateID: '%s' - could not be retrieved / does not exist",
          serviceInstance.getCSAR_ID(), serviceInstance.getServiceTemplateID(), nodeTemplateID);
      LOG.warn(msg);
      throw new ReferenceNotFoundException(msg);
    }

    final String nodeTemplateName = toscaEngineService.getNameOfReference(csarId, nodeTemplateID);

    // use localparts because serviceInstance QName namespace HAS to be the
    // same as the namespace of the nodeInstance
    final QName nodeTypeOfNodeTemplate = toscaEngineService.getNodeTypeOfNodeTemplate(csarId, serviceTemplateId,
        nodeTemplateID.getLocalPart());

    // use localparts because serviceInstance QName namespace HAS to be the
    // same as the namespace of the nodeInstance
    final Document propertiesOfNodeTemplate =
      toscaEngineService.getPropertiesOfTemplate(csarId, serviceTemplateId,
        nodeTemplateID.getLocalPart()
          .toString());

    NodeInstance nodeInstance =
      new NodeInstance(nodeTemplateID, nodeTemplateName, nodeTypeOfNodeTemplate, serviceInstance);
    // set default properties
    nodeInstance.setProperties(propertiesOfNodeTemplate);
    nodeInstance = this.niDAO.saveNodeInstance(nodeInstance);
    return nodeInstance;
  }

  @Override
  @WebMethod(exclude = true)
  public RelationInstance createRelationInstance(final CSARID csarId, final QName serviceTemplateId,
                                                 final int serviceTemplateInstanceID,
                                                 final QName relationshipTemplateID, final String sourceInstanceId,
                                                 final String targetInstanceId) {

    LOG.info("createRelationInstance(): {}", csarId);
    LOG.info("createRelationInstance(): {}", serviceTemplateId);
    LOG.info("createRelationInstance(): {}", serviceTemplateInstanceID);
    LOG.info("createRelationInstance(): {}", relationshipTemplateID);
    LOG.info("createRelationInstance(): {}", sourceInstanceId);
    LOG.info("createRelationInstance(): {}", targetInstanceId);

    final String relationshipTemplateName = toscaEngineService.getNameOfReference(csarId, relationshipTemplateID);

    // use localparts because serviceInstance QName namespace HAS to be the
    // same as the namespace of the nodeInstance
    final QName nodeTypeOfNodeTemplate = toscaEngineService.getRelationshipTypeOfRelationshipTemplate(csarId, serviceTemplateId, relationshipTemplateID.getLocalPart());

    // use localparts because serviceInstance QName namespace HAS to be the
    // same as the namespace of the nodeInstance
    final Document propertiesOfRelationshipTemplate = toscaEngineService.getPropertiesOfTemplate(csarId, serviceTemplateId, relationshipTemplateID.getLocalPart().toString());

    final NodeInstance sourceInstance = getNodeInstances(URI.create(sourceInstanceId), null, null, null).get(0);
    final NodeInstance targetInstance = getNodeInstances(URI.create(targetInstanceId), null, null, null).get(0);

    RelationInstance relationInstance = new RelationInstance(relationshipTemplateID, relationshipTemplateName, nodeTypeOfNodeTemplate, null, sourceInstance, targetInstance);

    // set default properties
    relationInstance.setProperties(propertiesOfRelationshipTemplate);
    relationInstance = this.riDAO.saveRelationInstance(relationInstance);

    return relationInstance;
  }

  /**
   * Yes, this method throws always an exception. Why? Do not use the method!
   */
  @Deprecated
  @Override
  public NodeInstance createNodeInstance(final QName nodeTemplateIDQName,
                                         final URI serviceInstanceIdURI) throws ReferenceNotFoundException {
    throw new ReferenceNotFoundException("DO NOT USE THIS METHOD!!!");
    // return null;
  }

  @Override
  @WebMethod(exclude = true)
  public void deleteNodeInstance(final URI nodeInstanceID) {
    LOG.info("deleteNodeInstance(): {}", nodeInstanceID);
    final List<NodeInstance> nodeInstances = getNodeInstances(nodeInstanceID, null, null, null);

    if (nodeInstances == null || nodeInstances.size() != 1) {
      LOG.warn(String.format("Failed to delete NodeInstance: '%s' - could not be retrieved",
        nodeInstanceID));
      return;
    }
    this.niDAO.deleteNodeInstance(nodeInstances.get(0));
  }

  @Override
  public void deleteRelationInstance(final URI relationInstanceID) {

    LOG.info("deleteRelationInstance(): {}", relationInstanceID);
    final List<RelationInstance> relationInstances = getRelationInstances(relationInstanceID, null, null, null);

    if (relationInstances == null || relationInstances.size() != 1) {
      LOG.warn(String.format("Failed to delete RelationInstance: '%s' - could not be retrieved",
        relationInstanceID));
      return;
    }
    this.riDAO.deleteRelationInstance(relationInstances.get(0));

  }

  @Override
  @WebMethod(exclude = true)
  public QName getRelationInstanceState(final URI relationInstanceID) throws ReferenceNotFoundException {
    LOG.info("getRelationInstanceState(): {}", relationInstanceID);
    final List<RelationInstance> relationInstances = getRelationInstances(relationInstanceID, null, null, null);
    if (relationInstances == null || relationInstances.size() != 1) {
      final String msg =
        String.format("Failed to get State of RelationInstance: '%s' - does it exist?", relationInstanceID);
      LOG.warn(msg);
      throw new ReferenceNotFoundException(msg);
    }
    return QName.valueOf(relationInstances.get(0).getState().toString());
  }

  @Override
  @WebMethod(exclude = true)
  public void setRelationInstanceState(final URI relationInstanceID,
                                       final String state) throws ReferenceNotFoundException {
    LOG.info("setRelationInstanceState(): {}", relationInstanceID);
    LOG.info("setRelationInstanceState(): {}", state);

    final List<RelationInstance> relationInstances = this.riDAO.getRelationInstances(null, null, null, relationInstanceID);

    if (relationInstances == null || relationInstances.size() != 1) {
      final String msg = String.format("Failed to set State of RelationInstance: '%s' - does it exist?", relationInstanceID);
      LOG.warn(msg);
      throw new ReferenceNotFoundException(msg);
    }
    this.riDAO.setState(relationInstances.get(0), state);
  }

  @Override
  @WebMethod(exclude = true)
  public QName getNodeInstanceState(final URI nodeInstanceID) throws ReferenceNotFoundException {
    LOG.info("getNodeInstanceState(): {}", nodeInstanceID);

    final List<NodeInstance> nodeInstances = getNodeInstances(nodeInstanceID, null, null, null);
    if (nodeInstances == null || nodeInstances.size() != 1) {
      final String msg = String.format("Failed to get State of NodeInstance: '%s' - does it exist?", nodeInstanceID);
      LOG.warn(msg);
      throw new ReferenceNotFoundException(msg);
    }
    return QName.valueOf(nodeInstances.get(0).getState().toString());
  }

  @Override
  public void setNodeInstanceState(final URI nodeInstanceID, final String state) throws ReferenceNotFoundException {
    LOG.info("setNodeInstanceState(): {}", nodeInstanceID);
    LOG.info("setNodeInstanceState(): {}", state);

    final List<NodeInstance> nodeInstances = getNodeInstances(nodeInstanceID, null, null, null);
    if (nodeInstances == null || nodeInstances.size() != 1) {
      final String msg = String.format("Failed to set State of NodeInstance: '%s' - does it exist?", nodeInstanceID);
      LOG.warn(msg);
      throw new ReferenceNotFoundException(msg);
    }
    this.niDAO.setState(nodeInstances.get(0), state);
  }

  @Override
  @WebMethod(exclude = true)
  public Document getServiceInstanceProperties(final URI serviceInstanceID,
                                               final List<QName> propertiesList) throws ReferenceNotFoundException {
    LOG.info("getServiceInstanceProperties(): {}", serviceInstanceID);
    LOG.info("getServiceInstanceProperties(): {}", propertiesList);

    final List<ServiceInstance> serviceInstances = getServiceInstances(serviceInstanceID, null, null);
    if (serviceInstances == null || serviceInstances.size() != 1) {
      final String msg = String.format("Failed to retrieve ServiceInstance: '%s'", serviceInstanceID);
      LOG.warn(msg);
      throw new ReferenceNotFoundException(msg);
    }

    final ServiceInstance serviceInstance = serviceInstances.get(0);
    updateServiceInstanceProperties(serviceInstance);
    return serviceInstance.getProperties();
  }

  @Override
  public Document getRelationInstanceProperties(final URI relationInstanceID,
                                                final List<QName> propertiesList) throws ReferenceNotFoundException {
    LOG.info("getRelationInstanceProperties(): {}", relationInstanceID);
    LOG.info("getRelationInstanceProperties(): {}", propertiesList);

    final List<RelationInstance> relationInstances = getRelationInstances(relationInstanceID, null, null, null);
    if (relationInstances == null || relationInstances.size() != 1) {
      final String msg = String.format("Failed to retrieve NodeInstance: '%s'", relationInstanceID);
      LOG.warn(msg);
      throw new ReferenceNotFoundException(msg);
    }
    final RelationInstance relationInstance = relationInstances.get(0);
    final Document retrievedProperties = relationInstance.getProperties();

    // start extracting relevant properties
    // if propertiesList == null return NO PROPERTIES
    // if propertiesList.isEmpty() return ALL PROPERTIES
    // if it contains values => filter and return them
    if (propertiesList == null) {
      return null;
    }
    if (propertiesList.isEmpty()) {
      return retrievedProperties;
    }

    final Element docElement = retrievedProperties.getDocumentElement();
    if (docElement == null) {
      return null;
    }

    // create new DOM-Document with new RootElement named like the old one
    final Document resultingProperties = InstanceDataServiceImpl.emptyDocument();
    final Element createElementNS = resultingProperties.createElement("Properties");
    resultingProperties.appendChild(createElementNS);

    // filter elements from the properties
    final NodeList childNodes = docElement.getChildNodes();
    for (int i = 0; i < childNodes.getLength(); i++) {
      final Node currentItem = childNodes.item(i);
      // this is a fix for empty text values due to a bug in the toscaReferenceMapper
      if (currentItem.getLocalName() == null) {
        // if QName can't be build skip this childNode (entry inside xml document)
        continue;
      }

      // calculate qName of the currentItem
      final QName currentItemQName = new QName(currentItem.getNamespaceURI(), currentItem.getLocalName());

      // match the item against the filters
      for (final QName qName : propertiesList) {
        if (qName.equals(currentItemQName)) {
          // match was found, add it to result
          // (first deep clone the element => then adopt to document and finally append to the documentElement)
          final Node cloneNode = currentItem.cloneNode(true);
          resultingProperties.adoptNode(cloneNode);
          resultingProperties.getDocumentElement().appendChild(cloneNode);
        }
      }
    }
    return resultingProperties;
  }

  @Override
  @WebMethod(exclude = true)
  // TODO: should it return a empty document when there aren't any properties
  // for the nodeinstance?
  public Document getNodeInstanceProperties(final URI nodeInstanceID,
                                            final List<QName> propertiesList) throws ReferenceNotFoundException {
    LOG.info("getNodeInstanceProperties(): {}", nodeInstanceID);
    LOG.info("getNodeInstanceProperties(): {}", propertiesList);

    final List<NodeInstance> nodeInstances = getNodeInstances(nodeInstanceID, null, null, null);
    if (nodeInstances == null || nodeInstances.size() != 1) {
      final String msg = String.format("Failed to retrieve NodeInstance: '%s'", nodeInstanceID);
      LOG.warn(msg);
      throw new ReferenceNotFoundException(msg);
    }
    final NodeInstance nodeInstance = nodeInstances.get(0);
    final Document retrievedProperties = nodeInstance.getProperties();

    // start extracting relevant properties
    // if propertiesList == null return NO PROPERTIES
    // if propertiesList.isEmpty() return ALL PROPERTIES
    // if it contains values => filter and return them
    if (propertiesList == null) {
      return null;
    }
    if (propertiesList.isEmpty()) {
      return retrievedProperties;
    }

    final Element docElement = retrievedProperties.getDocumentElement();
    if (docElement == null) {
      return null;
    }

    // create new DOM-Document with new RootElement named like the old one
    final Document resultingProperties = InstanceDataServiceImpl.emptyDocument();
    final Element createElementNS = resultingProperties.createElement("Properties");
    resultingProperties.appendChild(createElementNS);

    // filter elements from the properties
    final NodeList childNodes = docElement.getChildNodes();
    for (int i = 0; i < childNodes.getLength(); i++) {
      final Node currentItem = childNodes.item(i);
      // this is a fix for empty text values due to a bug in the toscaReferenceMapper
      if (currentItem.getLocalName() == null) {
        // if QName can't be build skip this childNode (entry inside xml document)
        continue;
      }

      final QName currentItemQName = new QName(currentItem.getNamespaceURI(), currentItem.getLocalName());
      for (final QName qName : propertiesList) {
        if (qName.equals(currentItemQName)) {
          // match was found, add it to result
          // (first deep clone the element => then adopt to document and finally append to the documentElement)
          final Node cloneNode = currentItem.cloneNode(true);
          resultingProperties.adoptNode(cloneNode);
          resultingProperties.getDocumentElement().appendChild(cloneNode);
        }
      }
    }
    return resultingProperties;
  }

  @Override
  public void setRelationInstanceProperties(final URI relationInstanceID,
                                            final Document properties) throws ReferenceNotFoundException {
    LOG.info("setRelationInstanceProperties(): {}", relationInstanceID);
    final List<RelationInstance> relationInstances = this.riDAO.getRelationInstances(null, null, null, relationInstanceID);
    if (relationInstances == null || relationInstances.size() != 1) {
      LOG.warn(String.format("Failed to set Properties of NodeInstance: '%s' - does it exist?",
        relationInstanceID));
      return;
    }

    this.riDAO.setProperties(relationInstances.get(0), properties);
    updateServiceInstanceProperties(relationInstances.get(0).getServiceInstance());
  }

  @Override
  @WebMethod(exclude = true)
  public void setServiceInstanceProperties(final URI serviceInstanceID,
                                           final Document properties) throws ReferenceNotFoundException {
    LOG.info("setServiceInstanceProperties(): {}", serviceInstanceID);

    final List<ServiceInstance> serviceInstances = getServiceInstances(serviceInstanceID, null, null);
    if (serviceInstances.size() != 1) {
      throw new ReferenceNotFoundException("Couldn't find serviceInstance");
    }

    final ServiceInstance serviceInstance = serviceInstances.get(0);
    serviceInstance.setProperties(properties);
    this.siDAO.storeServiceInstance(serviceInstance);
    updateServiceInstanceProperties(serviceInstance);
  }

  @Override
  public String getServiceInstanceState(final URI serviceInstanceID) throws ReferenceNotFoundException {
    LOG.info("getServiceInstanceState(): {}", serviceInstanceID);
    final List<ServiceInstance> serviceInstances = getServiceInstances(serviceInstanceID, null, null);
    if (serviceInstances == null || serviceInstances.size() != 1) {
      final String msg = String.format("Failed to get State of ServiceInstance: '%s' - does it exist?", serviceInstances);
      LOG.warn(msg);
      throw new ReferenceNotFoundException(msg);
    }
    return serviceInstances.get(0).getState().toString();
  }

  @Override
  public void setServiceInstanceState(final URI serviceInstanceIDtoURI,
                                      final String state) throws ReferenceNotFoundException {
    LOG.info("setServiceInstanceState(): {}", serviceInstanceIDtoURI);
    LOG.info("setServiceInstanceState(): {}", state);

    final List<ServiceInstance> serviceInstances = getServiceInstances(serviceInstanceIDtoURI, null, null);
    if (serviceInstances == null || serviceInstances.size() != 1) {
      final String msg = String.format("Failed to set State of NodeInstance: '%s' - does it exist?", serviceInstanceIDtoURI);
      LOG.warn(msg);
      throw new ReferenceNotFoundException(msg);
    }
    this.siDAO.setState(serviceInstances.get(0), state);
  }

  @Override
  @WebMethod(exclude = true)
  public void setNodeInstanceProperties(final URI nodeInstanceID,
                                        final Document properties) throws ReferenceNotFoundException {
    LOG.info("setNodeInstanceProperties(): {}", nodeInstanceID);
    final List<NodeInstance> nodeInstances = getNodeInstances(nodeInstanceID, null, null, null);

    if (nodeInstances == null || nodeInstances.size() != 1) {
      LOG.warn(String.format("Failed to set Properties of NodeInstance: '%s' - does it exist?",
        nodeInstanceID));
      return;
    }
    this.niDAO.setProperties(nodeInstances.get(0), properties);
    updateServiceInstanceProperties(nodeInstances.get(0).getServiceInstance());
  }

  @WebMethod(exclude = true)
  private void updateServiceInstanceProperties(final ServiceInstance serviceInstance) {
    // check if the serviceInstance has properties
    if (serviceInstance.getProperties() == null) {
      return;
    }

    // check if the serviceTemplate has propertyMappings
    final TBoundaryDefinitions boundaryDefs = toscaEngineService.getBoundaryDefinitionsOfServiceTemplate(serviceInstance.getCSAR_ID(), serviceInstance.getServiceTemplateID());
    if (boundaryDefs == null || boundaryDefs.getProperties() == null || boundaryDefs.getProperties().getPropertyMappings() == null) {
      // if there are no property mappings there is no need to update.
      // The properties can only be updated be external clients via setting properties by hand
      return;
    }

    final Element properties = (Element) serviceInstance.getProperties().getFirstChild();
    // cycle through mappings and update accordingly
    for (final TPropertyMapping mapping : boundaryDefs.getProperties().getPropertyMappings().getPropertyMapping()) {
      final String serviceTemplatePropertyQuery = mapping.getServiceTemplatePropertyRef();
      final List<Element> serviceTemplatePropertyElements = queryElementList(properties, serviceTemplatePropertyQuery);

      // fetch element from serviceTemplateProperties
      if (serviceTemplatePropertyElements.size() != 1) {
        // skip this property, we expect only one
        continue;
      }

      // check whether the targetRef is concat query
      if (isConcatQuery(mapping.getTargetPropertyRef())) {
        // this query needs possibly multiple properties from different nodeInstances
        final String propertyValue = generatePropertyValueFromConcatQuery(mapping.getTargetPropertyRef(), getNodeInstances(null, null, null, serviceInstance.getServiceInstanceID()));
        serviceTemplatePropertyElements.get(0).setTextContent(propertyValue);
      } else {
        // this query only fetches a SINGLE element on the properties of the referenced entity
        final NodeInstance nodeInstance = getNodeInstanceFromMappingObject(serviceInstance, mapping.getTargetObjectRef());
        if (nodeInstance == null) {
          continue;
        }

        final Document nodeProperties = nodeInstance.getProperties();
        if (nodeProperties == null) {
          // skip it, the mapping is invalid
          continue;
        }
        final Element nodePropertiesRoot = (Element) nodeProperties.getFirstChild();
        final String nodeTemplatePropertyQuery = mapping.getTargetPropertyRef();

        final List<Element> nodePropertyElements = queryElementList(nodePropertiesRoot, nodeTemplatePropertyQuery);
        if (nodePropertyElements.size() != 1) {
          // skip this property, we expect only one
          continue;
        }
        // change the serviceTemplateProperty
        serviceTemplatePropertyElements.get(0).setTextContent(nodePropertyElements.get(0).getTextContent());
      }
    }

    serviceInstance.setProperties(properties.getOwnerDocument());
    this.siDAO.storeServiceInstance(serviceInstance);
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

  /**
   * This method uses the toscaReferenceMapper of the given toscaEngineService to determine if the
   * given csarID contains the serviceTemplate specified by serviceTemplateID
   *
   * @param toscaEngineService
   * @param csarID
   * @param serviceTemplateID
   * @return true, if the given ServiceTemplate exists in the CSAR specified by the input parameter
   */
  private static boolean doesServiceTemplateExist(final IToscaEngineService toscaEngineService, final CSARID csarID,
                                                  final QName serviceTemplateID) {
    final List<QName> serviceTemplateIDsContainedInCSAR = toscaEngineService.getToscaReferenceMapper().getServiceTemplateIDsContainedInCSAR(csarID);
    if (serviceTemplateIDsContainedInCSAR == null) {
      return false;
    }
    return serviceTemplateIDsContainedInCSAR.contains(serviceTemplateID);
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

  private List<Element> queryElementList(final Element node, final String xpathQuery) {
    final List<Element> elements = new ArrayList<>();
    try {
      final XPath xPath = XPathFactory.newInstance().newXPath();
      final NodeList nodes = (NodeList) xPath.evaluate(xpathQuery, node, XPathConstants.NODESET);

      for (int index = 0; index < nodes.getLength(); index++) {
        if (nodes.item(index).getNodeType() == Node.ELEMENT_NODE) {
          elements.add((Element) nodes.item(index));
        }
      }
    } catch (final XPathExpressionException e) {
      LOG.error("XPATH NOT VALID!", e);
    }
    return elements;
  }

  private NodeInstance getNodeInstanceFromMappingObject(final ServiceInstance serviceInstance, final Object obj) {
    if (!(obj instanceof TNodeTemplate)) {
      return null;
    }
    final TNodeTemplate template = (TNodeTemplate) obj;
    final List<NodeInstance> nodeInstances = getNodeInstances(null, null, null, serviceInstance.getServiceInstanceID());
    if (nodeInstances == null) {
      return null;
    }

    for (final NodeInstance nodeInstance : nodeInstances) {
      if (nodeInstance.getNodeTemplateID().getLocalPart().equals(template.getId())) {
        return nodeInstance;
      }
    }
    return null;
  }

  private boolean isConcatQuery(final String xPathQuery) {
    final String testString = xPathQuery.trim();
    if (!testString.startsWith("concat(")) {
      return false;
    }

    String functionContent = testString.substring("concat(".length());
    functionContent = functionContent.substring(0, functionContent.length() - 1);
    final String[] functionParts = functionContent.split(",");

    for (final String functionPart : functionParts) {
      if (functionPart.startsWith("'") && !functionPart.endsWith("'")) {
        return false;
      }
    }
    return true;
  }

  /**
   * Creates a DOM Document containing only the properties of the given ServiceTemplate which are
   * declared. NodeInstance data is not considered as this method should be used to initialize the
   * properties
   *
   * @param csarId            the Id of the CSAR the serviceTemplate belongs to
   * @param serviceTemplateId the Id of the serviceTemplate
   * @return a DOM document containing elements representing properties of the serviceTemplate
   */
  private Document createServiceInstancePropertiesFromServiceTemplate(final CSARID csarId,
                                                                      final QName serviceTemplateId) {
    LOG.debug("Creating initial ServiceInstance Properties for " + serviceTemplateId + " in " + csarId);
    final TBoundaryDefinitions boundaryDefs = toscaEngineService.getBoundaryDefinitionsOfServiceTemplate(csarId, serviceTemplateId);

    Element propertiesElement = null;
    if (boundaryDefs != null && boundaryDefs.getProperties() != null) {
      LOG.debug("Properties found in Bounds for ST {}", serviceTemplateId);
      propertiesElement = (Element) boundaryDefs.getProperties().getAny();
      if (null == propertiesElement || null == propertiesElement.getOwnerDocument()) {
        LOG.debug("null pointer ahead!");
      }
    } else {
      LOG.debug("No Properties found in Bounds for ST {} thus create blank ones", serviceTemplateId);
      final Document emptyDoc = InstanceDataServiceImpl.emptyDocument();
      final Element createElementNS = emptyDoc.createElementNS("http://docs.oasis-open.org/tosca/ns/2011/12", "Properties");
      createElementNS.setAttribute("xmlns:tosca", "http://docs.oasis-open.org/tosca/ns/2011/12");
      createElementNS.setPrefix("tosca");
      emptyDoc.appendChild(createElementNS);
      propertiesElement = (Element) emptyDoc.getFirstChild();

    }
    return propertiesElement.getOwnerDocument();
  }
}
