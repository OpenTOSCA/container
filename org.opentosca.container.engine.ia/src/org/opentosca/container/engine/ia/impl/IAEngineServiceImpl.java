package org.opentosca.container.engine.ia.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TEntityTemplate.Properties;
import org.eclipse.winery.model.tosca.TEntityTemplate.PropertyConstraints;
import org.eclipse.winery.model.tosca.TEntityTypeImplementation;
import org.eclipse.winery.model.tosca.TImplementationArtifact;
import org.eclipse.winery.model.tosca.TImplementationArtifacts;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.model.tosca.TPropertyConstraint;
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
@NonNullByDefault
public class IAEngineServiceImpl implements IIAEngineService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IAEngineServiceImpl.class);
    
    // HashMap that stores available plug-ins. First parameter of type String is
    // used as key value.
    private final Map<String, IIAEnginePluginService> pluginServices =
        Collections.synchronizedMap(new HashMap<String, IIAEnginePluginService>());

    private final List<String> failedIAList = new ArrayList<>();
    private final List<IIAEnginePluginService> cachedPluginsList = new ArrayList<>();


    @Nullable
    private ICoreEndpointService endpointService;
    @Nullable
    private ICoreCapabilityService capabilityService;
    @Nullable
    private IToscaEngineService toscaEngineService;


    @Override
    // FIXME service template is completely ignored!!
    public List<String> deployImplementationArtifacts(Csar csar, TServiceTemplate serviceTemplate) {
        failedIAList.clear();
        deployServiceTemplate(csar, serviceTemplate);
        // return defensive copy
        return new ArrayList<>(failedIAList);
    }
    
    // FIXME service template is completely ignored
    private void deployServiceTemplate(final Csar csar, TServiceTemplate serviceTemplate) {
        LOGGER.trace("Deploying ServiceTemplate [{}] of Csar {}", serviceTemplate.getId(), csar.id().csarName());
        for (final TNodeTypeImplementation nti : csar.nodeTypeImplementations()) {
            TImplementationArtifacts artifacts = nti.getImplementationArtifacts();
            if (artifacts == null) { continue; }
            for (final TImplementationArtifact ia : artifacts.getImplementationArtifact()) {
                // filtering is performed by plugin
                deployImplementationArtifact(csar, nti, ia, Collections.emptyList());
            }
        }
        for (final TRelationshipTypeImplementation typeImpl : csar.relationshipTypeImplementations()) {
            TImplementationArtifacts artifacts = typeImpl.getImplementationArtifacts();
            if (artifacts == null) { continue; }
            for (final TImplementationArtifact ia : artifacts.getImplementationArtifact()) {
                // filtering is performed by plugin
                deployImplementationArtifact(csar, typeImpl, ia, Collections.emptyList());
            }
        }
    }
    
    private void deployNodeType(final Csar csar, final TNodeType nodeType) {
        LOGGER.trace("Deploying NodeType [{}] of Csar {}", nodeType.getIdFromIdOrNameField(), csar.id().csarName());
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
        LOGGER.trace("Deploying NodeTypeImplementation [{}] of Csar {}", impl.getIdFromIdOrNameField(), csar.id().csarName());
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
        LOGGER.trace("Deploying IA [{}] for TypeImplementation [{}] of Csar {}", artifact.getName(), typeImplementation.getIdFromIdOrNameField(), csar.id().csarName());
        final QName artifactType = artifact.getArtifactType();
        final QName artifactRef = artifact.getArtifactRef();
        
        Properties properties = null;
        List<TPropertyConstraint> propertyConstraints = new ArrayList<>();
        List<AbstractArtifact> artifacts = new ArrayList<>();
        
        if (artifactRef != null) {
            TArtifactTemplate artifactTemplate = csar.artifactTemplates().stream()
                .filter(template -> template.getId().equals(artifactRef.getLocalPart()))
                .findFirst()
                .orElse(null);
            if (artifactTemplate != null) {
                properties = artifactTemplate.getProperties();
                PropertyConstraints templateConstraints = artifactTemplate.getPropertyConstraints(); 
                propertyConstraints = templateConstraints == null 
                    ? Collections.emptyList()
                    : templateConstraints.getPropertyConstraint();
                artifacts = ToscaEngine.artifactsOfTemplate(artifactTemplate, csar);
            }
            final List<WSDLEndpoint> endpoints =
                this.endpointService.getWSDLEndpointsForNTImplAndIAName(QName.valueOf(typeImplementation.getIdFromIdOrNameField()),
                                                                        artifact.getName());


            // properties may as well be null, we apparently don't care
            final Document propertiesDocument = properties == null ? null 
                                                                   : XMLHelper.fromRootNode((Element)properties.getAny());
            URI serviceURI = null;
            if (endpoints != null && endpoints.size() > 0) {
                // IA already deployed
                LOGGER.debug("ImplementationArtifact [{}] of NodeTypeImplementation [{}] of Csar {} is already deployed",
                          artifact.getName(), typeImplementation.getIdFromIdOrNameField(), csar.id().csarName());
                serviceURI = endpoints.get(0).getURI();
            } else {
                final Document artifactSpecificContent = XMLHelper.withRootNode(artifact.getAny().stream().filter(o -> o instanceof Element).map(o -> (Element) o).collect(Collectors.toList()), "ImplementationArtifactSpecificContent");
                serviceURI = deployThroughPlugin(csar.id().toOldCsarId(), QName.valueOf(typeImplementation.getIdFromIdOrNameField()),
                                                 artifactType, artifactSpecificContent, propertiesDocument, 
                                                 propertyConstraints, artifacts,
                                                 // FIXME eww
                                                 containerRequirements.stream().map(TRequiredContainerFeature::getFeature).collect(Collectors.toList()));
            }
            if (serviceURI != null) {
                // Maybe should be located somewhere else.
                QName portType = this.getPortType(propertiesDocument);
                final WSDLEndpoint endpoint = new WSDLEndpoint(serviceURI, portType, csar.id(), null, QName.valueOf(typeImplementation.getIdFromIdOrNameField()),
                                                               artifact.getName());
                this.endpointService.storeWSDLEndpoint(endpoint);
                LOGGER.info("ImplementationArtifact: {} of NodeTypeImplementation: {} of CSAR: "
                    + csar.id().csarName() + " successfully deployed!", artifact.getName(),
                    typeImplementation.getIdFromIdOrNameField());
            } else {
                this.failedIAList.add(artifact.getName());
                LOGGER.warn("Deployment of ImplementationArtifact {} failed!", artifact.getName());
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
    @Nullable
    private URI deployThroughPlugin(final CSARID csarID, final QName nodeTypeImplementationID, final QName artifactType,
                                    final Document artifactSpecificContent, final @Nullable Document properties,
                                    final List<TPropertyConstraint> propertyConstraints,
                                    final List<AbstractArtifact> artifacts, final List<String> requiredFeatures) {
        LOGGER.info("Searching for plugin supporting artifactType: {} ...", artifactType.toString());
        final @Nullable IIAEnginePluginService plugin;
        synchronized (this.pluginServices) {
            plugin = this.pluginServices.get(artifactType.toString());
        }
        if (plugin == null) {
            LOGGER.warn("No matching Plug-in for type {} could be found! Deployment aborted.", artifactType.toString());
            return null;
        } 
        LOGGER.info("Plugin for artifactType: {} found: {}.", artifactType.toString(), plugin.toString());

        if (!IAEngineCapabilityChecker.capabilitiesAreMet(requiredFeatures, plugin)) {
            LOGGER.warn("Required Features are not met! Deployment aborted.");
            return null;
        } 
        return plugin.deployImplementationArtifact(csarID, nodeTypeImplementationID, artifactType,
                                                   artifactSpecificContent, properties,
                                                   propertyConstraints, artifacts, requiredFeatures);
    }

    @Override
    public boolean undeployImplementationArtifacts(Csar csar) {
        LOGGER.debug("Undeploying all ImplementationArtifacts of CSAR: {}", csar.id().csarName());
        LOGGER.trace("Getting stored endpoints of CSAR: {}", csar.id().csarName());
        CSARID bridge = csar.id().toOldCsarId();
        final List<WSDLEndpoint> csarEndpoints = this.endpointService.getWSDLEndpointsForCsarId(csar.id());

        boolean allUndeployed = true;
        for (final WSDLEndpoint endpoint : csarEndpoints) {
            final String iaName = endpoint.getIaName();
            if (iaName == null) {
                continue;
            }

            final QName nodeTypeImplQname = endpoint.getNodeTypeImplementation();
            final TNodeTypeImplementation nti;
            try {
                nti = ToscaEngine.findNodeTypeImplementation(csar, nodeTypeImplQname);
            }
            catch (NotFoundException e) {
                LOGGER.warn("Could not find NodeTypeImplementation for stored WSDL Endpoint {}", endpoint);
                continue;
            }
            final URI path = endpoint.getURI();
            LOGGER.debug("- IA: {} ...", iaName);

            final List<WSDLEndpoint> endpoints =
                this.endpointService.getWSDLEndpointsForNTImplAndIAName(nodeTypeImplQname, iaName);

            // IA is used in multiple CSARs: just delete db entry, but do
            // not undeploy IA
            if (endpoints != null && endpoints.size() > 1) {
                this.endpointService.removeWSDLEndpoint(csar.id(), endpoint);
                LOGGER.debug("IA: {} was not undeployed because it is used in other CSARs too. Only its DB entry was removed.",
                                              iaName);
            } else {
                LOGGER.debug("Trying to undeploy IA: {} ...", iaName);
                TImplementationArtifact artifact;
                try {
                    artifact = ToscaEngine.implementationArtifact(nti, iaName);
                }
                catch (NotFoundException e) {
                    LOGGER.warn(e.getMessage());
                    allUndeployed = false;
                    continue;
                }

                final QName artifactType = artifact.getArtifactType();
                final @Nullable IIAEnginePluginService plugin;
                synchronized (this.pluginServices) {
                    plugin = this.pluginServices.get(artifactType.toString());
                }
                if (plugin == null) {
                    LOGGER.warn("No matching plugin found for ArtifactType: {}!", artifactType);
                    allUndeployed = false;
                    continue;
                } 
                final boolean wasUndeployed =
                    plugin.undeployImplementationArtifact(iaName, nodeTypeImplQname, bridge, path);
                if (wasUndeployed) {
                    this.endpointService.removeWSDLEndpoint(csar.id(), endpoint);
                    LOGGER.debug("Undeploying of IA: {} was successful!", iaName);
                } else {
                    LOGGER.debug("Undeploying of IA: {} failed!", iaName);
                    allUndeployed = false;
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
                    LOGGER.info("PortType found: {}", portType.toString());
                    return portType;
                }
            }
        }
        LOGGER.debug("No PortType found!");
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
    public void bindPluginService(final @Nullable IIAEnginePluginService plugin) {
        if (plugin == null) {
            LOGGER.error("Bind Plugin Service: Supplied parameter is null!");
            return;
        }

        final List<String> types = plugin.getSupportedTypes();
        for (final String type : types) {
            this.pluginServices.put(type, plugin);
            LOGGER.debug("Bound IA-Plugin: {} for Type: {}", plugin.toString(), type);
        }

        // Store plugin capabilities or cache plugin if Capability Service
        // is not yet available.
        if (this.capabilityService != null) {
            this.capabilityService.storeCapabilities(plugin.getCapabilties(), plugin.toString(),
                                                     ProviderType.IA_PLUGIN);
        } else {
            this.cachedPluginsList.add(plugin);
        }
        LOGGER.debug("Bind Plugin Service: {} bound.", plugin.toString());
    }

    /**
     * Unbind PluginService.
     *
     * @param plugin - A ImplementationArtifactEnginePlugin to unregister.
     */
    public void unbindPluginService(final @Nullable IIAEnginePluginService plugin) {
        if (plugin == null) {
            LOGGER.error("Unbind Plugin Service: Supplied parameter is null!");
            return;
        }
        final List<String> types = plugin.getSupportedTypes();
        for (final String type : types) {
            final @Nullable Object deletedObject = this.pluginServices.remove(type);
            if (deletedObject != null) {
                LOGGER.debug("Unbound IA-Plugin: {} for Type: {}", plugin.toString(), type);
            } else {
                LOGGER.debug("IA-Plug-in {} could not be unbound, because it is not bound!", plugin.toString());
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
    public void bindEndpointService(final @Nullable ICoreEndpointService endpointService) {
        if (endpointService == null) {
            LOGGER.error("Bind Endpoint Service: Supplied parameter is null!");
            return;
        }
        this.endpointService = endpointService;
        LOGGER.debug("Bind Endpoint Service: {} bound.", endpointService.toString());
    }

    /**
     * Unbind EndpointService.
     *
     * @param endpointService - The endpointService to unregister.
     */
    public void unbindEndpointService(final @Nullable ICoreEndpointService endpointService) {
        this.endpointService = null;
        LOGGER.debug("Unbind Endpoint Service unbound.");
    }

    /**
     * Bind CapabilityService.
     *
     * @param capabilityService
     */
    public void bindCoreCapabilityService(final @Nullable ICoreCapabilityService capabilityService) {
        if (capabilityService == null) {
            LOGGER.error("Bind CapabilityService: Supplied parameter is null!");
            return;
        }
        this.capabilityService = capabilityService;
        for (final IIAEnginePluginService plugin : this.cachedPluginsList) {
            this.capabilityService.storeCapabilities(plugin.getCapabilties(), plugin.toString(),
                                                     ProviderType.IA_PLUGIN);
        }
        this.cachedPluginsList.clear();
        LOGGER.debug("Bind CapabilityService: {} bound.", capabilityService.toString());
    }

    /**
     * Unbind CapabilityService.
     *
     * @param capabilityService
     */
    public void unbindCoreCapabilityService(final @Nullable ICoreCapabilityService capabilityService) {
        this.capabilityService = null;
        LOGGER.debug("Unbind CapabilityService unbound.");
    }

    /**
     * Bind ToscaService
     *
     * @param toscaEngineService
     */
    public void bindToscaService(final @Nullable IToscaEngineService toscaEngineService) {
        if (toscaEngineService == null) {
            LOGGER.error("Bind ToscaService: Supplied parameter is null!");
            return;
        } 
        this.toscaEngineService = toscaEngineService;
        LOGGER.debug("Bind ToscaService: {} bound.", toscaEngineService.toString());
    }

    /**
     * Unbind ToscaService
     *
     * @param toscaEngineService
     */
    public void unbindToscaService(final @Nullable IToscaEngineService toscaEngineService) {
        this.toscaEngineService = null;
        LOGGER.debug("Unbind ToscaService unbound.");
    }
}
