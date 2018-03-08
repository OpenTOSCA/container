package org.opentosca.container.core.impl.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.jws.WebMethod;
import javax.jws.WebParam;
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
import org.opentosca.container.core.engine.IToscaEngineService;
import org.opentosca.container.core.engine.NodeTemplateInstanceCounts;
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
import org.opentosca.container.core.tosca.model.TBoundaryDefinitions;
import org.opentosca.container.core.tosca.model.TNodeTemplate;
import org.opentosca.container.core.tosca.model.TPropertyMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * The InstanceDataService.<br>
 * The Engine offers a service to manage InstanceData for existing ServiceTemplates inside the
 * CSARs. It relies on the ToscaEngine to get its information about existence of those and for
 * values for the default properties of created instances.
 */
@Deprecated
@WebService(name = "InstanceDataService")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class InstanceDataServiceImpl implements IInstanceDataService {

    final private static Logger logger = LoggerFactory.getLogger(InstanceDataServiceImpl.class);

    public static IToscaEngineService toscaEngineService;

    private final ServiceTemplateInstanceRepository serviceRepository = new ServiceTemplateInstanceRepository();
    private final NodeTemplateInstanceRepository nodeRepository = new NodeTemplateInstanceRepository();
    private final RelationshipTemplateInstanceRepository relationshipRepository =
        new RelationshipTemplateInstanceRepository();

    // used for persistence
    private final ServiceInstanceDAO siDAO = new ServiceInstanceDAO();
    private final NodeInstanceDAO niDAO = new NodeInstanceDAO();
    private final RelationInstanceDAO riDAO = new RelationInstanceDAO();


    @Override
    @WebMethod(exclude = true)
    public List<ServiceInstance> getServiceInstances(final URI serviceInstanceID, final String serviceTemplateName,
                                                     final QName serviceTemplateId) {

        logger.info("getServiceInstances(): {}", serviceInstanceID);
        logger.info("getServiceInstances(): {}", serviceTemplateName);
        logger.info("getServiceInstances(): {}", serviceTemplateId);

        if (serviceInstanceID != null) {
            Integer id = IdConverter.serviceInstanceUriToID(serviceInstanceID);
            if (id == null) {
                final String[] segments = serviceInstanceID.getPath().split("/");
                id = Integer.valueOf(segments[segments.length - 1]);
            }
            logger.info("Using ServiceTemplate Instance ID: {}", id);
            final Optional<ServiceTemplateInstance> sti = this.serviceRepository.find(DaoUtil.toLong(id));
            if (sti.isPresent()) {
                logger.info("Single Result: {}", sti);
                return Lists.newArrayList(Converters.convert(sti.get()));
            } else {
                logger.info("NOT FOUND");
            }
        }

        if (serviceTemplateId != null) {
            logger.info("Using serviceTemplateId: {}", serviceTemplateId);
            final Collection<ServiceTemplateInstance> result =
                this.serviceRepository.findByTemplateId(serviceTemplateId);
            if (result != null) {
                logger.info("Result: {}", result.size());
                return result.stream().map(sti -> Converters.convert(sti)).collect(Collectors.toList());
            }
        }

        return this.siDAO.getServiceInstances(serviceInstanceID, serviceTemplateName, serviceTemplateId);
    }

    @Override
    @WebMethod(exclude = true)
    public List<ServiceInstance> getServiceInstancesWithDetails(final CSARID csarId, final QName serviceTemplateId,
                                                                final Integer serviceTemplateInstanceID) {

        logger.info("getServiceInstancesWithDetails(): {}", csarId);
        logger.info("getServiceInstancesWithDetails(): {}", serviceTemplateId);
        logger.info("getServiceInstancesWithDetails(): {}", serviceTemplateInstanceID);

        if (serviceTemplateInstanceID != null) {
            final Optional<ServiceTemplateInstance> sti =
                this.serviceRepository.find(DaoUtil.toLong(serviceTemplateInstanceID));
            if (sti.isPresent()) {
                logger.info("Single Result: {}", sti);
                return Lists.newArrayList(Converters.convert(sti.get()));
            } else {
                logger.info("NOT FOUND");
            }
        }

        if (serviceTemplateId != null) {
            final Collection<ServiceTemplateInstance> result =
                this.serviceRepository.findByTemplateId(serviceTemplateId);
            if (result != null) {
                logger.info("Result: {}", result.size());
                return result.stream().map(sti -> Converters.convert(sti)).collect(Collectors.toList());
            }
        }

        return this.siDAO.getServiceInstances(csarId, serviceTemplateId, serviceTemplateInstanceID);
    }

    @Override
    @WebMethod(exclude = true)
    public ServiceInstance createServiceInstance(final CSARID csarID,
                                                 final QName serviceTemplateID) throws ReferenceNotFoundException {

        logger.info("createServiceInstance(): {}", csarID);
        logger.info("createServiceInstance(): {}", serviceTemplateID);

        InstanceDataServiceImpl.logger.debug("Starting creating ServiceInstance for " + serviceTemplateID + " in "
            + csarID);
        // TODO: boolean flag for cascading creation? cool or not?
        // check if serviceTemplate doesn't exist
        if (!ToscaEngineProxy.doesServiceTemplateExist(InstanceDataServiceImpl.toscaEngineService, csarID,
                                                       serviceTemplateID)) {
            InstanceDataServiceImpl.logger.warn(String.format("Failed to create ServiceInstance for CSAR-ID: %s / serviceTemplateID: %s - was not found!",
                                                              csarID, serviceTemplateID));
            throw new ReferenceNotFoundException("ServiceTemplate doesn't exist in the specified CSAR");
        }
        // retrieve serviceTemplateName
        final String serviceTemplateName =
            InstanceDataServiceImpl.toscaEngineService.getNameOfReference(csarID, serviceTemplateID);
        // get all min and maxCounts from the ServiceTemplate and construct
        // nodeInstances from it automatically
        final NodeTemplateInstanceCounts instanceCounts =
            InstanceDataServiceImpl.toscaEngineService.getInstanceCountsOfNodeTemplatesByServiceTemplateID(csarID,
                                                                                                           serviceTemplateID);

        // creation of real objects
        ServiceInstance serviceInstance = new ServiceInstance(csarID, serviceTemplateID, serviceTemplateName);

        // construct initial properties of serviceTemplate
        final Document properties = createServiceInstancePropertiesFromServiceTemplate(csarID, serviceTemplateID);

        serviceInstance.setProperties(properties);

        serviceInstance = this.siDAO.storeServiceInstance(serviceInstance);
        // store serviceInstance so we can use nodeInstanceDAO to create
        // nodeInstances (they need an existing object because its working in
        // another transaction)
        // TODO: or is it better to get the alternative route? to do it in one
        // transaction? and have duplicate code? need to fetch nodeTemplateName
        // from toscaEngine f.ex.?

        // this creates required Node Templates of a Service Template, but this
        // functionality is out dated
        // HashMap<QName, InstanceCount> occurenceInformationMap =
        // instanceCounts.getOccurenceInformationMap();
        // Set<QName> qNamesOfNodeTemplates = occurenceInformationMap.keySet();
        // // create for each nodeTemplate the minimum amount of instances
        // // specified
        // for (QName qName : qNamesOfNodeTemplates) {
        // InstanceCount instanceCount = occurenceInformationMap.get(qName);
        // // create "instanceCount".min instances
        // for (int i = 0; i < instanceCount.min; i++) {
        // // new nodeInstance
        // createNodeInstance(qName, serviceInstance.getServiceInstanceID());
        // }
        // }
        // create associated nodeInstances

        return serviceInstance;
    }

    @Override
    @WebMethod(exclude = true)
    public void deleteServiceInstance(final URI serviceInstanceID) {

        logger.info("deleteServiceInstance(): {}", serviceInstanceID);

        final List<ServiceInstance> serviceInstances = getServiceInstances(serviceInstanceID, null, null);

        if (serviceInstances == null || serviceInstances.size() != 1) {
            InstanceDataServiceImpl.logger.warn(String.format("Failed to delete ServiceInstance: '%s' - could not be retrieved",
                                                              serviceInstanceID));
            return;
        }
        this.siDAO.deleteServiceInstance(serviceInstances.get(0));
    }

    @Override
    @WebMethod(exclude = true)
    public List<NodeInstance> getNodeInstances(final URI nodeInstanceID, final QName nodeTemplateID,
                                               final String nodeTemplateName, final URI serviceInstanceID) {

        logger.info("getNodeInstances(): {}", nodeInstanceID);
        logger.info("getNodeInstances(): {}", nodeTemplateID);
        logger.info("getNodeInstances(): {}", nodeTemplateName);
        logger.info("getNodeInstances(): {}", serviceInstanceID);

        if (nodeInstanceID != null) {
            Integer id = IdConverter.nodeInstanceUriToID(nodeInstanceID);
            if (id == null) {
                final String[] segments = serviceInstanceID.getPath().split("/");
                id = Integer.valueOf(segments[segments.length - 1]);
            }
            logger.info("Using NodeTemplate Instance ID: {}", id);
            final Optional<NodeTemplateInstance> nti = this.nodeRepository.find(DaoUtil.toLong(id));
            if (nti.isPresent()) {
                logger.info("Single Result: {}", nti);
                return Lists.newArrayList(Converters.convert(nti.get()));
            } else {
                logger.info("NOT FOUND");
            }
        }

        if (nodeTemplateID != null) {
            final Collection<NodeTemplateInstance> result = this.nodeRepository.findByTemplateId(nodeTemplateID);
            if (result != null) {
                logger.info("Result: {}", result.size());
                return result.stream().map(nti -> Converters.convert(nti)).collect(Collectors.toList());
            }
        }

        if (serviceInstanceID != null) {
            Integer id = IdConverter.serviceInstanceUriToID(serviceInstanceID);
            if (id == null) {
                final String[] segments = serviceInstanceID.getPath().split("/");
                id = Integer.valueOf(segments[segments.length - 1]);
            }
            logger.info("Using ServiceTemplate Instance ID: {}", id);
            final Optional<ServiceTemplateInstance> sti = this.serviceRepository.find(DaoUtil.toLong(id));
            if (sti.isPresent()) {
                final ServiceTemplateInstance i = sti.get();
                final Collection<NodeTemplateInstance> result = i.getNodeTemplateInstances();
                if (result != null) {
                    logger.info("Result: {}", result.size());
                    return result.stream().map(nti -> Converters.convert(nti)).collect(Collectors.toList());
                }
            } else {
                logger.info("NOT FOUND");
            }
        }

        return this.niDAO.getNodeInstances(serviceInstanceID, nodeTemplateID, nodeTemplateName, nodeInstanceID);
    }

    @Override
    public List<RelationInstance> getRelationInstances(final URI relationInstanceID, final QName relationshipTemplateID,
                                                       final String relationshipTemplateName,
                                                       final URI serviceInstanceID) {

        logger.info("getRelationInstances(): {}", relationInstanceID);
        logger.info("getRelationInstances(): {}", relationshipTemplateID);
        logger.info("getRelationInstances(): {}", relationshipTemplateName);
        logger.info("getRelationInstances(): {}", serviceInstanceID);

        if (relationInstanceID != null) {
            Integer id = IdConverter.relationInstanceUriToID(relationInstanceID);
            if (id == null) {
                final String[] segments = relationInstanceID.getPath().split("/");
                id = Integer.valueOf(segments[segments.length - 1]);
            }
            logger.info("Using RelationshipTemplate Instance ID: {}", id);
            final Optional<RelationshipTemplateInstance> nti = this.relationshipRepository.find(DaoUtil.toLong(id));
            if (nti.isPresent()) {
                logger.info("Single Result: {}", nti);
                return Lists.newArrayList(Converters.convert(nti.get()));
            } else {
                logger.info("NOT FOUND");
            }
        }

        if (relationshipTemplateID != null) {
            final Collection<RelationshipTemplateInstance> result =
                this.relationshipRepository.findByTemplateId(relationshipTemplateID);
            if (result != null) {
                logger.info("Result: {}", result.size());
                return result.stream().map(nti -> Converters.convert(nti)).collect(Collectors.toList());
            }
        }

        if (serviceInstanceID != null) {
            final Set<RelationshipTemplateInstance> rels = Sets.newHashSet();
            Integer id = IdConverter.serviceInstanceUriToID(serviceInstanceID);
            if (id == null) {
                final String[] segments = serviceInstanceID.getPath().split("/");
                id = Integer.valueOf(segments[segments.length - 1]);
            }
            logger.info("Using ServiceTemplate Instance ID: {}", id);
            final Optional<ServiceTemplateInstance> sti = this.serviceRepository.find(DaoUtil.toLong(id));
            if (sti.isPresent()) {
                final ServiceTemplateInstance i = sti.get();
                final Collection<NodeTemplateInstance> result = i.getNodeTemplateInstances();
                if (result != null) {
                    for (final NodeTemplateInstance nti : result) {
                        rels.addAll(nti.getIncomingRelations());
                        rels.addAll(nti.getOutgoingRelations());
                    }
                    logger.info("Result: {}", rels.size());
                    return rels.stream().map(nti -> Converters.convert(nti)).collect(Collectors.toList());
                }
            } else {
                logger.info("NOT FOUND");
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

        logger.info("createNodeInstance(): {}", csarId);
        logger.info("createNodeInstance(): {}", serviceTemplateId);
        logger.info("createNodeInstance(): {}", serviceTemplateInstanceID);
        logger.info("createNodeInstance(): {}", nodeTemplateID);

        logger.debug("Retrieve Node Template \"{{}}\":\"{}\" for csar \"{}\", Service Template \"{}\" instance \"{}\"",
                     nodeTemplateID.getNamespaceURI(), nodeTemplateID.getLocalPart(), csarId, serviceTemplateId,
                     serviceTemplateInstanceID);

        final List<ServiceInstance> serviceInstances =
            getServiceInstancesWithDetails(csarId, serviceTemplateId, serviceTemplateInstanceID);
        if (serviceInstances == null || serviceInstances.size() != 1) {
            final String msg =
                String.format("Failed to create NodeInstance: ServiceInstance: '%s' - could not be retrieved",
                              serviceTemplateInstanceID);
            InstanceDataServiceImpl.logger.warn(msg);
            throw new ReferenceNotFoundException(msg);
        }
        final ServiceInstance serviceInstance = serviceInstances.get(0);

        // check if nodeTemplate exists
        if (!InstanceDataServiceImpl.toscaEngineService.doesNodeTemplateExist(csarId, serviceTemplateId,
                                                                              nodeTemplateID.getLocalPart())) {
            final String msg =
                String.format("Failed to create NodeInstance: NodeTemplate: csar: %s serviceTemplateID: %s , nodeTemplateID: '%s' - could not be retrieved / does not exists",
                              serviceInstance.getCSAR_ID(), serviceInstance.getServiceTemplateID(), nodeTemplateID);
            InstanceDataServiceImpl.logger.warn(msg);
            throw new ReferenceNotFoundException(msg);
        }

        final String nodeTemplateName =
            InstanceDataServiceImpl.toscaEngineService.getNameOfReference(csarId, nodeTemplateID);

        // use localparts because serviceInstance QName namespace HAS to be the
        // same as the namespace of the nodeInstance
        final QName nodeTypeOfNodeTemplate =
            InstanceDataServiceImpl.toscaEngineService.getNodeTypeOfNodeTemplate(csarId, serviceTemplateId,
                                                                                 nodeTemplateID.getLocalPart());

        // use localparts because serviceInstance QName namespace HAS to be the
        // same as the namespace of the nodeInstance
        final Document propertiesOfNodeTemplate =
            InstanceDataServiceImpl.toscaEngineService.getPropertiesOfNodeTemplate(csarId, serviceTemplateId,
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

        logger.info("createRelationInstance(): {}", csarId);
        logger.info("createRelationInstance(): {}", serviceTemplateId);
        logger.info("createRelationInstance(): {}", serviceTemplateInstanceID);
        logger.info("createRelationInstance(): {}", relationshipTemplateID);
        logger.info("createRelationInstance(): {}", sourceInstanceId);
        logger.info("createRelationInstance(): {}", targetInstanceId);

        final String relationshipTemplateName =
            InstanceDataServiceImpl.toscaEngineService.getNameOfReference(csarId, relationshipTemplateID);

        // use localparts because serviceInstance QName namespace HAS to be the
        // same as the namespace of the nodeInstance
        final QName nodeTypeOfNodeTemplate =
            InstanceDataServiceImpl.toscaEngineService.getRelationshipTypeOfRelationshipTemplate(csarId,
                                                                                                 serviceTemplateId,
                                                                                                 relationshipTemplateID.getLocalPart());

        // use localparts because serviceInstance QName namespace HAS to be the
        // same as the namespace of the nodeInstance
        final Document propertiesOfRelationshipTemplate =
            InstanceDataServiceImpl.toscaEngineService.getPropertiesOfRelationshipTemplate(csarId, serviceTemplateId,
                                                                                           relationshipTemplateID.getLocalPart()
                                                                                                                 .toString());

        final NodeInstance sourceInstance = getNodeInstances(URI.create(sourceInstanceId), null, null, null).get(0);
        final NodeInstance targetInstance = getNodeInstances(URI.create(targetInstanceId), null, null, null).get(0);

        RelationInstance relationInstance = new RelationInstance(relationshipTemplateID, relationshipTemplateName,
            nodeTypeOfNodeTemplate, null, sourceInstance, targetInstance);

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

        logger.info("deleteNodeInstance(): {}", nodeInstanceID);

        final List<NodeInstance> nodeInstances = getNodeInstances(nodeInstanceID, null, null, null);

        if (nodeInstances == null || nodeInstances.size() != 1) {
            InstanceDataServiceImpl.logger.warn(String.format("Failed to delete NodeInstance: '%s' - could not be retrieved",
                                                              nodeInstanceID));
            return;
        }
        this.niDAO.deleteNodeInstance(nodeInstances.get(0));

    }

    @Override
    public void deleteRelationInstance(final URI relationInstanceID) {

        logger.info("deleteRelationInstance(): {}", relationInstanceID);

        final List<RelationInstance> relationInstances = getRelationInstances(relationInstanceID, null, null, null);

        if (relationInstances == null || relationInstances.size() != 1) {
            InstanceDataServiceImpl.logger.warn(String.format("Failed to delete RelatioknInstance: '%s' - could not be retrieved",
                                                              relationInstanceID));
            return;
        }
        this.riDAO.deleteRelationInstance(relationInstances.get(0));

    }

    @Override
    @WebMethod(exclude = true)
    public QName getRelationInstanceState(final URI relationInstanceID) throws ReferenceNotFoundException {

        logger.info("getRelationInstanceState(): {}", relationInstanceID);

        final List<RelationInstance> relationInstances = getRelationInstances(relationInstanceID, null, null, null);
        if (relationInstances == null || relationInstances.size() != 1) {
            final String msg =
                String.format("Failed to get State of RelationInstance: '%s' - does it exist?", relationInstanceID);
            InstanceDataServiceImpl.logger.warn(msg);
            throw new ReferenceNotFoundException(msg);
        }
        return QName.valueOf(relationInstances.get(0).getState().toString());
    }

    @Override
    @WebMethod(exclude = true)
    public void setRelationInstanceState(final URI relationInstanceID,
                                         final String state) throws ReferenceNotFoundException {

        logger.info("setRelationInstanceState(): {}", relationInstanceID);
        logger.info("setRelationInstanceState(): {}", state);

        final List<RelationInstance> relationInstances =
            this.riDAO.getRelationInstances(null, null, null, relationInstanceID);

        if (relationInstances == null || relationInstances.size() != 1) {
            final String msg =
                String.format("Failed to set State of RelationInstance: '%s' - does it exist?", relationInstanceID);
            InstanceDataServiceImpl.logger.warn(msg);
            throw new ReferenceNotFoundException(msg);
        }
        this.riDAO.setState(relationInstances.get(0), state);
    }

    @Override
    @WebMethod(exclude = true)
    public QName getNodeInstanceState(final URI nodeInstanceID) throws ReferenceNotFoundException {

        logger.info("getNodeInstanceState(): {}", nodeInstanceID);

        final List<NodeInstance> nodeInstances = getNodeInstances(nodeInstanceID, null, null, null);
        if (nodeInstances == null || nodeInstances.size() != 1) {
            final String msg =
                String.format("Failed to get State of NodeInstance: '%s' - does it exist?", nodeInstanceID);
            InstanceDataServiceImpl.logger.warn(msg);
            throw new ReferenceNotFoundException(msg);
        }
        return QName.valueOf(nodeInstances.get(0).getState().toString());
    }

    @Override
    public void setNodeInstanceState(final URI nodeInstanceID, final String state) throws ReferenceNotFoundException {

        logger.info("setNodeInstanceState(): {}", nodeInstanceID);
        logger.info("setNodeInstanceState(): {}", state);

        final List<NodeInstance> nodeInstances = getNodeInstances(nodeInstanceID, null, null, null);

        if (nodeInstances == null || nodeInstances.size() != 1) {
            final String msg =
                String.format("Failed to set State of NodeInstance: '%s' - does it exist?", nodeInstanceID);
            InstanceDataServiceImpl.logger.warn(msg);
            throw new ReferenceNotFoundException(msg);
        }
        this.niDAO.setState(nodeInstances.get(0), state);
    }

    @Override
    @WebMethod(exclude = true)
    public Document getServiceInstanceProperties(final URI serviceInstanceID,
                                                 final List<QName> propertiesList) throws ReferenceNotFoundException {

        logger.info("getServiceInstanceProperties(): {}", serviceInstanceID);
        logger.info("getServiceInstanceProperties(): {}", propertiesList);

        final List<ServiceInstance> serviceInstances = getServiceInstances(serviceInstanceID, null, null);

        if (serviceInstances == null || serviceInstances.size() != 1) {
            final String msg = String.format("Failed to retrieve ServiceInstance: '%s'", serviceInstanceID);
            InstanceDataServiceImpl.logger.warn(msg);
            throw new ReferenceNotFoundException(msg);
        }

        final ServiceInstance serviceInstance = serviceInstances.get(0);

        updateServiceInstanceProperties(serviceInstance);

        return serviceInstance.getProperties();
    }

    @Override
    public Document getRelationInstanceProperties(final URI relationInstanceID,
                                                  final List<QName> propertiesList) throws ReferenceNotFoundException {

        logger.info("getRelationInstanceProperties(): {}", relationInstanceID);
        logger.info("getRelationInstanceProperties(): {}", propertiesList);

        final List<RelationInstance> relationInstances = getRelationInstances(relationInstanceID, null, null, null);

        if (relationInstances == null || relationInstances.size() != 1) {
            final String msg = String.format("Failed to retrieve NodeInstance: '%s'", relationInstanceID);
            InstanceDataServiceImpl.logger.warn(msg);
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

            // this is a fix for empty text values due to a bug in the
            // toscaReferenceMapper
            if (currentItem.getLocalName() == null) {
                // if QName can't be build skip this childNode (entry inside xml
                // document)
                continue;
            }

            // calculate qName of the currentItem
            final QName currentItemQName = new QName(currentItem.getNamespaceURI(), currentItem.getLocalName());

            // match the item against the filters
            for (final QName qName : propertiesList) {
                if (qName.equals(currentItemQName)) {
                    // match was found, add it to result (first deep clone the
                    // element => then adopt to document and finally append to
                    // the documentElement
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

        logger.info("getNodeInstanceProperties(): {}", nodeInstanceID);
        logger.info("getNodeInstanceProperties(): {}", propertiesList);

        final List<NodeInstance> nodeInstances = getNodeInstances(nodeInstanceID, null, null, null);

        if (nodeInstances == null || nodeInstances.size() != 1) {
            final String msg = String.format("Failed to retrieve NodeInstance: '%s'", nodeInstanceID);
            InstanceDataServiceImpl.logger.warn(msg);
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

            // this is a fix for empty text values due to a bug in the
            // toscaReferenceMapper
            if (currentItem.getLocalName() == null) {
                // if QName can't be build skip this childNode (entry inside xml
                // document)
                continue;
            }

            // calculate qName of the currentItem
            final QName currentItemQName = new QName(currentItem.getNamespaceURI(), currentItem.getLocalName());

            // match the item against the filters
            for (final QName qName : propertiesList) {
                if (qName.equals(currentItemQName)) {
                    // match was found, add it to result (first deep clone the
                    // element => then adopt to document and finally append to
                    // the documentElement
                    final Node cloneNode = currentItem.cloneNode(true);
                    resultingProperties.adoptNode(cloneNode);
                    resultingProperties.getDocumentElement().appendChild(cloneNode);
                }
            }
        }

        return resultingProperties;
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
        }
        catch (final ParserConfigurationException e) {
            InstanceDataServiceImpl.logger.error(e.getMessage());
        }
        return null;
    }

    @Override
    public void setRelationInstanceProperties(final URI relationInstanceID,
                                              final Document properties) throws ReferenceNotFoundException {

        logger.info("setRelationInstanceProperties(): {}", relationInstanceID);

        final List<RelationInstance> relationInstances =
            this.riDAO.getRelationInstances(null, null, null, relationInstanceID);

        if (relationInstances == null || relationInstances.size() != 1) {
            InstanceDataServiceImpl.logger.warn(String.format("Failed to set Properties of NodeInstance: '%s' - does it exist?",
                                                              relationInstanceID));
            return;
        }

        this.riDAO.setProperties(relationInstances.get(0), properties);

        updateServiceInstanceProperties(relationInstances.get(0).getServiceInstance());
        return;

    }

    @Override
    @WebMethod(exclude = true)
    public void setNodeInstanceProperties(final URI nodeInstanceID,
                                          final Document properties) throws ReferenceNotFoundException {

        logger.info("setNodeInstanceProperties(): {}", nodeInstanceID);

        final List<NodeInstance> nodeInstances = getNodeInstances(nodeInstanceID, null, null, null);

        if (nodeInstances == null || nodeInstances.size() != 1) {
            InstanceDataServiceImpl.logger.warn(String.format("Failed to set Properties of NodeInstance: '%s' - does it exist?",
                                                              nodeInstanceID));
            return;
        }

        this.niDAO.setProperties(nodeInstances.get(0), properties);

        updateServiceInstanceProperties(nodeInstances.get(0).getServiceInstance());
        return;

    }

    @WebMethod(exclude = true)
    public void bindToscaEngineService(final IToscaEngineService toscaEngineService) {
        if (toscaEngineService == null) {
            InstanceDataServiceImpl.logger.error("Can't bind ToscaEngine Service.");
        } else {
            InstanceDataServiceImpl.toscaEngineService = toscaEngineService;
            InstanceDataServiceImpl.logger.debug("ToscaEngine-Service bound.");
        }
    }

    @WebMethod(exclude = true)
    public void unbindToscaEngineService(final IToscaEngineService toscaEngineService) {
        InstanceDataServiceImpl.toscaEngineService = null;
        InstanceDataServiceImpl.logger.debug("ToscaEngine-Service unbound.");

    }


    // TODO: remove this when deprecated methods are removed
    static HashMap<String, String> instanceData = new HashMap<>();


    @Override
    @WebMethod
    public void setProperty(@WebParam(name = "key") final String key, @WebParam(name = "value") final String value) {
        System.out.println("Setting key: " + key + " with value: " + value);
        InstanceDataServiceImpl.instanceData.put(key, value);
    }

    @Override
    @WebMethod
    public String getProperty(@WebParam(name = "key") final String key) {
        System.out.println("Getting value for key: " + key);

        return InstanceDataServiceImpl.instanceData.get(key);
    }

    @Override
    public HashMap<String, String> getProperties(final String keyPrefix) {
        System.out.println("Getting values for key beginning with: " + keyPrefix);

        final HashMap<String, String> properties = new HashMap<>();

        for (final Map.Entry<String, String> entry : InstanceDataServiceImpl.instanceData.entrySet()) {

            final String key = entry.getKey();
            if (key.startsWith(keyPrefix)) {
                final String value = entry.getValue();
                properties.put(key, value);
            }
        }

        return properties;
    }

    @WebMethod(exclude = true)
    private void updateServiceInstanceProperties(final ServiceInstance serviceInstance) {
        // check if the serviceInstance has properties
        if (serviceInstance.getProperties() == null) {
            return;
        }

        // check if the serviceTemplate has propertyMappings
        final TBoundaryDefinitions boundaryDefs =
            InstanceDataServiceImpl.toscaEngineService.getBoundaryDefinitionsOfServiceTemplate(serviceInstance.getCSAR_ID(),
                                                                                               serviceInstance.getServiceTemplateID());

        if (boundaryDefs == null || boundaryDefs.getProperties() == null
            || boundaryDefs.getProperties().getPropertyMappings() == null) {
            // if there are no property mappings there is no need to update. The
            // properties can only be updated be external clients via setting
            // properties by hand
            return;
        }

        final Element properties = (Element) serviceInstance.getProperties().getFirstChild();

        // cycle through mappings and update accordingly
        for (final TPropertyMapping mapping : boundaryDefs.getProperties().getPropertyMappings().getPropertyMapping()) {
            final String serviceTemplatePropertyQuery = mapping.getServiceTemplatePropertyRef();
            final List<Element> serviceTemplatePropertyElements =
                queryElementList(properties, serviceTemplatePropertyQuery);

            // fetch element from serviceTemplateProperties

            if (serviceTemplatePropertyElements.size() != 1) {
                // skip this property, we expect only one
                continue;
            }

            // check whether the targetRef is concat query
            if (isConcatQuery(mapping.getTargetPropertyRef())) {
                // this query needs possibly multiple properties from different
                // nodeInstances

                final String propertyValue =
                    generatePropertyValueFromConcatQuery(mapping.getTargetPropertyRef(),
                                                         getNodeInstances(null, null, null,
                                                                          serviceInstance.getServiceInstanceID()));

                serviceTemplatePropertyElements.get(0).setTextContent(propertyValue);

            } else {
                // this query only fetches a SINGLE element on the properties of
                // the referenced entity

                final NodeInstance nodeInstance =
                    getNodeInstanceFromMappingObject(serviceInstance, mapping.getTargetObjectRef());

                if (nodeInstance == null) {
                    // skip it, the mapping is invalid
                    continue;
                }

                final Document nodeProperties = nodeInstance.getProperties();
                if (nodeProperties == null) {
                    // skip it, the mapping is invalid
                    continue;
                }
                final Element nodePropertiesRoot = (Element) nodeProperties.getFirstChild();
                final String nodeTemplatePropertyQuery = mapping.getTargetPropertyRef();

                final List<Element> nodePropertyElements =
                    queryElementList(nodePropertiesRoot, nodeTemplatePropertyQuery);

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

        // simple validity check as we only want to be able to concat strings,
        // but maybe more later
        if (!functionString.equals("concat")) {
            return null;
        }

        final String functionContent =
            testQuery.substring(functionOpeningBracket + 1, testQuery.lastIndexOf(")")).trim();

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

                if (getNodeInstanceWithName(nodeInstance, nodeTemplateName) != null) {

                    final String propValue =
                        fetchPropertyValueFromNodeInstance(getNodeInstanceWithName(nodeInstance, nodeTemplateName),
                                                           propertyName);

                    augmentedFunctionParts.add("'" + propValue + "'");
                }
            }
        }

        // now we have a string of the form:
        // concat('someString','somePropertyValue','someString',..)
        // just make the concat itself instead of running an XPath query

        String resultString = "";
        for (final String functionPart : augmentedFunctionParts) {
            resultString += functionPart.replace("'", "");
        }

        return resultString;
    }

    private NodeInstance getNodeInstanceWithName(final List<NodeInstance> nodeInstances, final String nodeTemplateId) {

        for (final NodeInstance nodeInstance : nodeInstances) {
            if (nodeInstance.getNodeTemplateID().getLocalPart().equals(nodeTemplateId)) {
                return nodeInstance;
            }
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

        }
        catch (final XPathExpressionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return elements;
    }

    private NodeInstance getNodeInstanceFromMappingObject(final ServiceInstance serviceInstance, final Object obj) {
        if (obj instanceof TNodeTemplate) {

            final TNodeTemplate template = (TNodeTemplate) obj;

            // service.getNodeInstances(null, null, null,
            // serviceInstanceIDtoURI);

            final List<NodeInstance> nodeInstances =
                getNodeInstances(null, null, null, serviceInstance.getServiceInstanceID());

            if (nodeInstances == null) {
                return null;
            }

            for (final NodeInstance nodeInstance : nodeInstances) {
                if (nodeInstance.getNodeTemplateID().getLocalPart().equals(template.getId())) {
                    return nodeInstance;
                }
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
     * @param csarId the Id of the CSAR the serviceTemplate belongs to
     * @param serviceTemplateId the Id of the serviceTemplate
     * @return a DOM document containing elements representing properties of the serviceTemplate
     */
    private Document createServiceInstancePropertiesFromServiceTemplate(final CSARID csarId,
                                                                        final QName serviceTemplateId) {

        InstanceDataServiceImpl.logger.debug("Creating initial ServiceInstance Properties for " + serviceTemplateId
            + " in " + csarId);
        final TBoundaryDefinitions boundaryDefs =
            InstanceDataServiceImpl.toscaEngineService.getBoundaryDefinitionsOfServiceTemplate(csarId,
                                                                                               serviceTemplateId);

        Element propertiesElement = null;

        if (boundaryDefs != null && boundaryDefs.getProperties() != null) {

            logger.debug("Properties found in Bounds for ST {}", serviceTemplateId);

            // Document emptyDoc = InstanceDataServiceImpl.emptyDocument();
            //
            // Element createElementNS =
            // emptyDoc.createElementNS("http://docs.oasis-open.org/tosca/ns/2011/12","Properties");
            // createElementNS.setAttribute("xmlns:tosca",
            // "http://docs.oasis-open.org/tosca/ns/2011/12");
            // createElementNS.setPrefix("tosca");
            //
            // emptyDoc.appendChild(createElementNS);
            //
            // Element properties = (Element)
            // boundaryDefs.getProperties().getAny();
            //
            // if(properties.getNamespaceURI() == null){
            // // set tosca namespace
            // Node clonedNode = properties.cloneNode(true);
            // Node renamedNode =
            // properties.getOwnerDocument().renameNode(clonedNode,
            // "http://docs.oasis-open.org/tosca/ns/2011/12",
            // properties.getLocalName());
            //
            // properties = (Element)renamedNode;
            // properties.setAttribute("xmlns:tosca",
            // "http://docs.oasis-open.org/tosca/ns/2011/12");
            // properties.setPrefix("tosca");
            // }
            //
            // properties = (Element) emptyDoc.importNode(properties, true);
            //
            // createElementNS.appendChild(properties);
            //
            // propertiesElement = (Element) emptyDoc.getFirstChild();
            propertiesElement = (Element) boundaryDefs.getProperties().getAny();

            if (null == propertiesElement || null == propertiesElement.getOwnerDocument()) {

                logger.debug("null pointer ahead!");

                // LOG.debug("No Properties found in Bounds for ST {} thus
                // create blank ones", serviceTemplateId);
                // Document emptyDoc = InstanceDataServiceImpl.emptyDocument();
                // Element createElementNS =
                // emptyDoc.createElementNS("http://docs.oasis-open.org/tosca/ns/2011/12",
                // "Properties");
                // createElementNS.setAttribute("xmlns:tosca",
                // "http://docs.oasis-open.org/tosca/ns/2011/12");
                // createElementNS.setPrefix("tosca");
                // emptyDoc.appendChild(createElementNS);
                // propertiesElement = (Element) emptyDoc.getFirstChild();
            }
        } else {

            logger.debug("No Properties found in Bounds for ST {} thus create blank ones", serviceTemplateId);
            final Document emptyDoc = InstanceDataServiceImpl.emptyDocument();
            final Element createElementNS =
                emptyDoc.createElementNS("http://docs.oasis-open.org/tosca/ns/2011/12", "Properties");
            createElementNS.setAttribute("xmlns:tosca", "http://docs.oasis-open.org/tosca/ns/2011/12");
            createElementNS.setPrefix("tosca");
            emptyDoc.appendChild(createElementNS);
            propertiesElement = (Element) emptyDoc.getFirstChild();

        }

        return propertiesElement.getOwnerDocument();
    }

    @Override
    @WebMethod(exclude = true)
    public void setServiceInstanceProperties(final URI serviceInstanceID,
                                             final Document properties) throws ReferenceNotFoundException {

        logger.info("setServiceInstanceProperties(): {}", serviceInstanceID);

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

        logger.info("getServiceInstanceState(): {}", serviceInstanceID);

        final List<ServiceInstance> serviceInstances = getServiceInstances(serviceInstanceID, null, null);

        if (serviceInstances == null || serviceInstances.size() != 1) {
            final String msg =
                String.format("Failed to get State of ServiceInstance: '%s' - does it exist?", serviceInstances);
            InstanceDataServiceImpl.logger.warn(msg);
            throw new ReferenceNotFoundException(msg);
        }
        return serviceInstances.get(0).getState().toString();
    }

    @Override
    public void setServiceInstanceState(final URI serviceInstanceIDtoURI,
                                        final String state) throws ReferenceNotFoundException {

        logger.info("setServiceInstanceState(): {}", serviceInstanceIDtoURI);
        logger.info("setServiceInstanceState(): {}", state);

        final List<ServiceInstance> serviceInstances = getServiceInstances(serviceInstanceIDtoURI, null, null);

        if (serviceInstances == null || serviceInstances.size() != 1) {
            final String msg =
                String.format("Failed to set State of NodeInstance: '%s' - does it exist?", serviceInstanceIDtoURI);
            InstanceDataServiceImpl.logger.warn(msg);
            throw new ReferenceNotFoundException(msg);
        }

        this.siDAO.setState(serviceInstances.get(0), state);

    }
}
