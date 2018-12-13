package org.opentosca.container.engine.ia.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TArtifactTemplate.ArtifactReferences;
import org.eclipse.winery.model.tosca.TEntityTemplate.Properties;
import org.eclipse.winery.model.tosca.TEntityTypeImplementation;
import org.eclipse.winery.model.tosca.TImplementationArtifact;
import org.eclipse.winery.model.tosca.TImplementationArtifacts;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.model.tosca.TPropertyConstraint;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTypeImplementation;
import org.eclipse.winery.model.tosca.TRequiredContainerFeature;
import org.eclipse.winery.model.tosca.TRequiredContainerFeatures;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.opentosca.container.core.common.NotFoundException;
import org.opentosca.container.core.common.xml.XMLHelper;
import org.opentosca.container.core.engine.IToscaEngineService;
import org.opentosca.container.core.engine.ToscaEngine;
import org.opentosca.container.core.model.AbstractArtifact;
import org.opentosca.container.core.model.capability.provider.ProviderType;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.model.endpoint.wsdl.WSDLEndpoint;
import org.opentosca.container.core.service.ICoreCapabilityService;
import org.opentosca.container.core.service.ICoreEndpointService;
import org.opentosca.container.engine.ia.IIAEngineService;
import org.opentosca.container.engine.ia.plugin.IIAEnginePluginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

/**
 * Engine for delegating Implementation Artifacts to matching plug-ins.
 *
 * After the IAEngine is called it grabs the corresponding Implementation Artifacts from the
 * ToscaService, sends them to the IAEngineCapabilityChecker, delegates the deployable Artifacts to
 * matching plug-ins and saves the returned Endpoint through the EndpointService.
 *
 * The deployment of Implementation Artifacts is based on two stages. First, the plug-in returns the
 * potential endpoint the Implementation Artifact would have if it would be deployed.
 *
 * The IAEngine will then ask the Endpoint Service if an entry with this endpoint already exists in
 * the Endpoint DB. If this is true, the Implementation Artifact was already deployed previously and
 * the potential endpoint is stored with new operation information. Otherwise, if no entry in the DB
 * is present, the current Implementation Artifact needs to be deployed.
 *
 * @TODO It would be wiser to save only the URL of the WSDL of a specific service, in case a service
 *       offers multiple ports. This would also simplify the work of the Plan Engine.
 */

public class IAEngineServiceImpl implements IIAEngineService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IAEngineServiceImpl.class);
    
    // HashMap that stores available plug-ins. First parameter of type String is
    // used as key value.
    private final Map<String, IIAEnginePluginService> pluginServices =
        Collections.synchronizedMap(new HashMap<String, IIAEnginePluginService>());

    @NonNull
    private final List<String> failedIAList = new ArrayList<>();
    @NonNull
    private final List<IIAEnginePluginService> cachedPluginsList = new ArrayList<>();

    private final static Logger LOG = LoggerFactory.getLogger(IAEngineServiceImpl.class);

    private ICoreEndpointService endpointService;
    private ICoreCapabilityService capabilityService;
    private IToscaEngineService toscaEngineService;


    @Override
    public List<String> deployImplementationArtifacts(Csar csar, TServiceTemplate serviceTemplate) {
        failedIAList.clear();
        deployServiceTemplate(csar, serviceTemplate);
        // return defensive copy
        return new ArrayList<>(failedIAList);
    }
    
    private void deployServiceTemplate(final Csar csar, TServiceTemplate serviceTemplate) {
        LOG.trace("Deploying ServiceTemplate [{}] of Csar {}", serviceTemplate.getId(), csar.id().csarName());
        for (final TNodeTypeImplementation nti : csar.nodeTypeImplementations()) {
            TImplementationArtifacts artifacts = nti.getImplementationArtifacts();
            if (artifacts == null) { continue; }
            for (final TImplementationArtifact ia : artifacts.getImplementationArtifact()) {
                // filtering is performed by plugin
                deployImplementationArtifact(csar, nti, ia, Collections.emptyList());
            }
        }
        for (final TRelationshipTypeImplementation typeImpl :csar.relationshipTypeImplementations()) {
            TImplementationArtifacts artifacts = typeImpl.getImplementationArtifacts();
            if (artifacts == null) { continue; }
            for (final TImplementationArtifact ia : artifacts.getImplementationArtifact()) {
                // filtering is performed by plugin
                deployImplementationArtifact(csar, typeImpl, ia, Collections.emptyList());
            }
        }
    }
    
    private void deployNodeType(final Csar csar, final TNodeType nodeType) {
        LOG.trace("Deploying NodeType [{}] of Csar {}", nodeType.getIdFromIdOrNameField(), csar.id().csarName());
        List<TNodeType> nodeTypeHierarchy;
        try {
            nodeTypeHierarchy = ToscaEngine.getNodeTypeHierarchy(csar, nodeType.getIdFromIdOrNameField());
        }
        catch (NotFoundException e) {
            LOGGER.error("Hierarchy of NodeType to deploy was corrupt", e);
            return;
        }
        for (final TNodeType superType : nodeTypeHierarchy) {
            final List<TEntityTypeImplementation> implementations = new ArrayList<>();
            implementations.addAll(ToscaEngine.nodeTypeImplementations(csar, superType));
            implementations.addAll(ToscaEngine.relationshipTypeImplementations(csar, superType));
            for (TEntityTypeImplementation impl : implementations) {
                deployNodeTypeImplementation(csar, impl);
            }
        }
    }

    private void deployNodeTypeImplementation(final Csar csar, TEntityTypeImplementation impl) {
        LOG.trace("Deploying NodeTypeImplementation [{}] of Csar {}", impl.getIdFromIdOrNameField(), csar.id().csarName());
        List<TEntityTypeImplementation> implementationTypeHierarchy;
        try {
            implementationTypeHierarchy = ToscaEngine.implementationTypeHierarchy(impl, csar);
        }
        catch (NotFoundException e) {
            
            return;
        }
        for (final TEntityTypeImplementation superImplementation : implementationTypeHierarchy) {
            TRequiredContainerFeatures requires = superImplementation.getRequiredContainerFeatures();
            IAEngineCapabilityChecker.removeConAndPlanCaps(capabilityService, 
                                                           // FIXME eww
                                                           requires.getRequiredContainerFeature().stream().map(TRequiredContainerFeature::getFeature).collect(Collectors.toList()));
            final TImplementationArtifacts implementationArtifacts = superImplementation.getImplementationArtifacts();
            if (implementationArtifacts == null) { continue; }
            for (final TImplementationArtifact artifact : implementationArtifacts.getImplementationArtifact()) {
                deployImplementationArtifact(csar, superImplementation, artifact, requires == null ? Collections.emptyList() : requires.getRequiredContainerFeature());
            }
        }
    }

    private void deployImplementationArtifact(final Csar csar, final TEntityTypeImplementation typeImplementation,
                                              final TImplementationArtifact artifact, final List<TRequiredContainerFeature> containerRequirements) {
        LOG.trace("Deploying IA [{}] for TypeImplementation [{}] of Csar {}", artifact.getName(), typeImplementation.getIdFromIdOrNameField(), csar.id().csarName());
        final QName artifactType = artifact.getArtifactType();
        final QName artifactRef = artifact.getArtifactRef();
        
        Properties properties = null;
        List<TPropertyConstraint> propertyConstraints = new ArrayList<>();
        List<AbstractArtifact> artifacts = new ArrayList<>();
        
        if (artifactRef != null) {
            TArtifactTemplate artifactTemplate = csar.artifactTemplates().stream()
                .filter(template -> {
                    ArtifactReferences nullable = template.getArtifactReferences();
                    return nullable != null && nullable.getArtifactReference().stream()
                        .anyMatch(ref -> ref.getReference().equals(artifactRef.toString()));
                })
                .findFirst()
                .orElse(null);
            if (artifactTemplate != null) {
                properties = artifactTemplate.getProperties();
                propertyConstraints = artifactTemplate.getPropertyConstraints() == null 
                    ? Collections.emptyList()
                    : artifactTemplate.getPropertyConstraints().getPropertyConstraint();
                artifacts = ToscaEngine.artifactsOfTemplate(artifactTemplate, csar);
            }
            final List<WSDLEndpoint> endpoints =
                this.endpointService.getWSDLEndpointsForNTImplAndIAName(QName.valueOf(typeImplementation.getIdFromIdOrNameField()),
                                                                        artifact.getName());

            URI serviceURI = null;

            // IA already deployed
            if (endpoints != null && endpoints.size() > 0) {
                LOG.debug("ImplementationArtifact [{}] of NodeTypeImplementation [{}] of Csar {} is already deployed",
                          artifact.getName(), typeImplementation.getIdFromIdOrNameField(), csar.id().csarName());
                serviceURI = endpoints.get(0).getURI();
            } else {
                final Document artifactSpecificContent =
                    this.toscaEngineService.getArtifactSpecificContentOfAImplementationArtifactOfANodeTypeImplementation(csar.id().toOldCsarId(),
                                                                                                                         QName.valueOf(typeImplementation.getIdFromIdOrNameField()),
                                                                                                                         artifact.getName());
                serviceURI = deployThroughPlugin(csar.id().toOldCsarId(), QName.valueOf(typeImplementation.getIdFromIdOrNameField()),
                                                 artifactType, artifactSpecificContent, XMLHelper.fromRootNode((Element)properties.getAny()), 
                                                 propertyConstraints, artifacts,
                                                 // FIXME eww
                                                 containerRequirements.stream().map(TRequiredContainerFeature::getFeature).collect(Collectors.toList()));
            }
            if (serviceURI != null) {
                // Maybe should be located somewhere else.
                QName portType = this.getPortType(XMLHelper.fromRootNode((Element)properties.getAny()));
                final WSDLEndpoint endpoint = new WSDLEndpoint(serviceURI, portType, csar.id(), null, QName.valueOf(typeImplementation.getIdFromIdOrNameField()),
                                                               artifact.getName());
                this.endpointService.storeWSDLEndpoint(endpoint);
                LOG.info("ImplementationArtifact: {} of NodeTypeImplementation: {} of CSAR: "
                    + csar.id().csarName() + " successfully deployed!", artifact.getName(),
                    typeImplementation.getIdFromIdOrNameField());
            } else {
                this.failedIAList.add(artifact.getName());
                LOG.warn("Deployment of ImplementationArtifact {} failed!", artifact.getName());
            }
        }
    }
    
    /**
     * Calls a registered plug-in to deploy a ImplementationArtifact.
     *
     * @param artifactType
     * @param artifactSpecificContent
     * @param properties
     * @param propertyConstraints
     * @param files
     * @param requiredFeatures
     * @return Endpoint of the deployed ImplementationArtifact or <tt>null</tt> if deployment failed.
     */
    private URI deployThroughPlugin(final CSARID csarID, final QName nodeTypeImplementationID, final QName artifactType,
                                    final Document artifactSpecificContent, final Document properties,
                                    final List<TPropertyConstraint> propertyConstraints,
                                    final List<AbstractArtifact> artifacts, final List<String> requiredFeatures) {
        URI serviceEndpoint = null;
        IIAEnginePluginService plugin;

        LOG.info("Searching for plugin supporting artifactType: {} ...", artifactType.toString());

        synchronized (this.pluginServices) {
            plugin = this.pluginServices.get(artifactType.toString());
        }

        if (plugin != null) {

            LOG.info("Plugin for artifactType: {} found: {}.", artifactType.toString(),
                                         plugin.toString());

            if (IAEngineCapabilityChecker.capabilitiesAreMet(requiredFeatures, plugin)) {

                serviceEndpoint = plugin.deployImplementationArtifact(csarID, nodeTypeImplementationID, artifactType,
                                                                      artifactSpecificContent, properties,
                                                                      propertyConstraints, artifacts, requiredFeatures);

            } else {
                LOG.warn("Required Features are not met! Deployment aborted.");
            }

        } else {
            LOG.warn("No matching Plug-in for type {} could be found! Deployment aborted.",
                                         artifactType.toString());
        }

        return serviceEndpoint;
    }

    @Override
    public boolean undeployImplementationArtifacts(Csar csar) {
        LOG.debug("Undeploying all ImplementationArtifacts of CSAR: {}", csar.id().csarName());
        LOG.trace("Getting stored endpoints of CSAR: {}", csar.id().csarName());
        CSARID bridge = csar.id().toOldCsarId();
        final List<WSDLEndpoint> csarEndpoints = this.endpointService.getWSDLEndpointsForCsarId(csar.id());

        boolean allUndeployed = true;
        IIAEnginePluginService plugin;
        for (final WSDLEndpoint endpoint : csarEndpoints) {
            final String iaName = endpoint.getIaName();
            final QName nodeTypeImpl = endpoint.getNodeTypeImplementation();
            final URI path = endpoint.getURI();

            if (iaName != null) {
                LOG.debug("- IA: {} ...", iaName);

                final List<WSDLEndpoint> endpoints =
                    this.endpointService.getWSDLEndpointsForNTImplAndIAName(nodeTypeImpl, iaName);

                // IA is used in multiple CSARs: just delete db entry, but do
                // not undeploy IA
                if (endpoints != null && endpoints.size() > 1) {

                    this.endpointService.removeWSDLEndpoint(csar.id(), endpoint);
                    LOG.debug("IA: {} was not undeployed because it is used in other CSARs too. Only its DB entry was removed.",
                                                  iaName);

                } else {

                    LOG.debug("Trying to undeploy IA: {} ...", iaName);

                    if (iaName != null) {

                        final QName artifactType =
                            this.toscaEngineService.getArtifactTypeOfAImplementationArtifactOfANodeTypeImplementation(bridge,
                                                                                                                      nodeTypeImpl,
                                                                                                                      iaName);

                        synchronized (this.pluginServices) {
                            plugin = this.pluginServices.get(artifactType.toString());
                        }

                        if (plugin != null) {

                            final boolean wasUndeployed =
                                plugin.undeployImplementationArtifact(iaName, nodeTypeImpl, bridge, path);

                            if (wasUndeployed) {

                                this.endpointService.removeWSDLEndpoint(csar.id(), endpoint);
                                LOG.debug("Undeploying of IA: {} was successful!", iaName);

                            } else {
                                LOG.debug("Undeploying of IA: {} failed!", iaName);
                                allUndeployed = false;
                            }

                        } else {
                            LOG.warn("No matching plugin found for ArtifactType: {}!",
                                                         artifactType);
                            allUndeployed = false;
                        }
                    }
                }
            }
        }
        return allUndeployed;
    }

    /**
     * Checks if a PortType was specified in the Tosca.xml and returns it if so.
     *
     * @param properties to check for PortType.
     * @return PortType if specified. Otherwise <tt>null</tt>.
     */
    @Nullable
    private QName getPortType(final @Nullable Document properties) {
        // Checks if there are specified properties at all.
        if (properties != null) {
            final NodeList list = properties.getFirstChild().getChildNodes();
            for (int i = 0; i < list.getLength(); i++) {
                final Node propNode = list.item(i);
                if (containsPortType(propNode)) {
                    final QName portType = this.getPortType(propNode);
                    LOG.info("PortType found: {}", portType.toString());
                    return portType;
                }
            }
        }
        LOG.debug("No PortType found!");
        return null;
    }

    /**
     * Checks if the Node contains a PortType. A PortType has to be specified with
     * <tt>{@literal <}namespace:PortType{@literal >}...
     * {@literal <}/namespace:PortType{@literal >}</tt>.
     *
     * @param currentNode to check.
     * @return whether currentNode contains a PortType.
     */
    private boolean containsPortType(final @NonNull Node currentNode) {
        final String localName = currentNode.getLocalName();
        return localName != null && (localName.equals("PortType") || localName.equals("soapPortType"));
    }

    /**
     * Gets PortType informations defined in current Node and creates a the PortType out of it.
     *
     * @param currentNode to check for PortType information.
     * @return PortType if specified. Otherwise <tt>null</tt>.
     */
    @NonNull
    private QName getPortType(final @NonNull Node currentNode) {
        final QName portType = QName.valueOf(currentNode.getTextContent().trim());
        return portType;
    }

    /**
     * Bind PluginService and store it in local HashMap.
     *
     * @param plugin - A ImplementationArtifactEnginePlugin to register.
     */
    public void bindPluginService(final IIAEnginePluginService plugin) {
        if (plugin != null) {

            final List<String> types = plugin.getSupportedTypes();

            for (final String type : types) {
                this.pluginServices.put(type, plugin);
                LOG.debug("Bound IA-Plugin: {} for Type: {}", plugin.toString(), type);
            }

            // Store plugin capabilities or cache plugin if Capability Service
            // is not yet available.
            if (this.capabilityService != null) {
                this.capabilityService.storeCapabilities(plugin.getCapabilties(), plugin.toString(),
                                                         ProviderType.IA_PLUGIN);
            } else {
                this.cachedPluginsList.add(plugin);
            }

            LOG.debug("Bind Plugin Service: {} bound.", plugin.toString());
        } else {
            LOG.error("Bind Plugin Service: Supplied parameter is null!");
        }
    }

    /**
     * Unbind PluginService.
     *
     * @param plugin - A ImplementationArtifactEnginePlugin to unregister.
     */
    public void unbindPluginService(final IIAEnginePluginService plugin) {
        if (plugin == null) {
            LOG.error("Unbind Plugin Service: Supplied parameter is null!");
            return;
        }
        final List<String> types = plugin.getSupportedTypes();
        for (final String type : types) {
            final Object deletedObject = this.pluginServices.remove(type);
            if (deletedObject != null) {
                LOG.debug("Unbound IA-Plugin: {} for Type: {}", plugin.toString(), type);
            } else {
                LOG.debug("IA-Plug-in {} could not be unbound, because it is not bound!", plugin.toString());
            }
        }
        if (this.capabilityService != null) {
            this.capabilityService.deleteCapabilities(plugin.toString());
        }
    }

    /**
     * Bind EndpointService.
     *
     * @param endpointService - The endpointService to register.
     */
    public void bindEndpointService(final ICoreEndpointService endpointService) {
        if (endpointService == null) {
            LOG.error("Bind Endpoint Service: Supplied parameter is null!");
            return;
        }
        this.endpointService = endpointService;
        LOG.debug("Bind Endpoint Service: {} bound.", endpointService.toString());
    }

    /**
     * Unbind EndpointService.
     *
     * @param endpointService - The endpointService to unregister.
     */
    public void unbindEndpointService(ICoreEndpointService endpointService) {
        this.endpointService = null;
        LOG.debug("Unbind Endpoint Service unbound.");
    }

    /**
     * Bind CapabilityService.
     *
     * @param capabilityService
     */
    public void bindCoreCapabilityService(final ICoreCapabilityService capabilityService) {
        if (capabilityService == null) {
            LOG.error("Bind CapabilityService: Supplied parameter is null!");
            return;
        }
        this.capabilityService = capabilityService;
        for (final IIAEnginePluginService plugin : this.cachedPluginsList) {
            this.capabilityService.storeCapabilities(plugin.getCapabilties(), plugin.toString(),
                                                     ProviderType.IA_PLUGIN);
        }
        this.cachedPluginsList.clear();
        LOG.debug("Bind CapabilityService: {} bound.", capabilityService.toString());
    }

    /**
     * Unbind CapabilityService.
     *
     * @param capabilityService
     */
    public void unbindCoreCapabilityService(ICoreCapabilityService capabilityService) {
        this.capabilityService = null;
        LOG.debug("Unbind CapabilityService unbound.");
    }

    /**
     * Bind ToscaService
     *
     * @param toscaEngineService
     */
    public void bindToscaService(final IToscaEngineService toscaEngineService) {
        if (toscaEngineService == null) {
            LOG.error("Bind ToscaService: Supplied parameter is null!");
            return;
        } 
        this.toscaEngineService = toscaEngineService;
        LOG.debug("Bind ToscaService: {} bound.", toscaEngineService.toString());
    }

    /**
     * Unbind ToscaService
     *
     * @param toscaEngineService
     */
    public void unbindToscaService(IToscaEngineService toscaEngineService) {
        this.toscaEngineService = null;
        LOG.debug("Unbind ToscaService unbound.");
    }
}
