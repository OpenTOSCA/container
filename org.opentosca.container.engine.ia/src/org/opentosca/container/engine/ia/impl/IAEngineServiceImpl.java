package org.opentosca.container.engine.ia.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.opentosca.container.core.engine.IToscaEngineService;
import org.opentosca.container.core.model.AbstractArtifact;
import org.opentosca.container.core.model.capability.provider.ProviderType;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.model.endpoint.wsdl.WSDLEndpoint;
import org.opentosca.container.core.service.ICoreCapabilityService;
import org.opentosca.container.core.service.ICoreEndpointService;
import org.eclipse.winery.model.tosca.TPropertyConstraint;
import org.opentosca.container.engine.ia.IIAEngineService;
import org.opentosca.container.engine.ia.plugin.IIAEnginePluginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
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

public class IAEngineServiceImpl implements IIAEngineService {

    // HashMap that stores available plug-ins. First parameter of type String is
    // used as key value.
    private final Map<String, IIAEnginePluginService> pluginServices =
        Collections.synchronizedMap(new HashMap<String, IIAEnginePluginService>());
    private final List<String> failedIAList = new ArrayList<>();
    private final List<IIAEnginePluginService> cachedPluginsList = new ArrayList<>();

    private final static Logger LOG = LoggerFactory.getLogger(IAEngineServiceImpl.class);

    private ICoreEndpointService endpointService, oldEndpointService;
    private ICoreCapabilityService capabilityService, oldCapabilityService;
    private IToscaEngineService toscaEngineService, oldToscaEngineService;


    @Override
    /**
     * {@inheritDoc}
     *
     */
    public List<String> deployImplementationArtifacts(final CSARID csarID, final QName serviceTemplateID) {

        this.failedIAList.clear();
        this.deployServiceTemplate(csarID, serviceTemplateID);
        return this.failedIAList;
    }

    /**
     * @param csarID
     * @param serviceTemplateID
     */
    private void deployServiceTemplate(final CSARID csarID, final QName serviceTemplateID) {

        IAEngineServiceImpl.LOG.debug("Deploying ServiceTemplate: {} of CSAR: {} ...", serviceTemplateID,
                                      csarID.getFileName());

        final List<QName> nodeTypeIDs =
            this.toscaEngineService.getReferencedNodeTypesOfAServiceTemplate(csarID, serviceTemplateID);

        for (final QName nodeTypeID : nodeTypeIDs) {

            this.deployNodeType(csarID, nodeTypeID);

        }

    }

    /**
     * @param csarID
     * @param nodeTypeID
     */
    private void deployNodeType(final CSARID csarID, final QName nodeTypeID) {

        IAEngineServiceImpl.LOG.debug("Deploying NodeType: {} of CSAR: {} ...", nodeTypeID, csarID.getFileName());

        for (final QName nodeTypeHierarchyMember : this.toscaEngineService.getNodeTypeHierarchy(csarID, nodeTypeID)) {

            final List<QName> nodeTypeImplementationIDs =
                this.toscaEngineService.getNodeTypeImplementationsOfNodeType(csarID, nodeTypeHierarchyMember);

            for (final QName nodeTypeImplementationID : nodeTypeImplementationIDs) {

                this.deployNodeTypeImplementation(csarID, nodeTypeImplementationID);

            }
        }

    }

    /**
     * @param csarID
     * @param nodeTypeImplementationID
     */
    private void deployNodeTypeImplementation(final CSARID csarID, final QName nodeTypeImplementationID) {

        IAEngineServiceImpl.LOG.debug("Deploying NodeTypeImplementation: {} of CSAR: {} ...", nodeTypeImplementationID,
                                      csarID.getFileName());

        for (final QName nodeTypeImplHierarchyMember : this.toscaEngineService.getNodeTypeImplementationTypeHierarchy(csarID,
                                                                                                                      nodeTypeImplementationID)) {

            List<String> requiredFeatures =
                this.toscaEngineService.getRequiredContainerFeaturesOfANodeTypeImplementation(csarID,
                                                                                              nodeTypeImplHierarchyMember);

            requiredFeatures = IAEngineCapabilityChecker.removeConAndPlanCaps(this.capabilityService, requiredFeatures);

            final List<String> implementationArtifactNames =
                this.toscaEngineService.getImplementationArtifactNamesOfNodeTypeImplementation(csarID,
                                                                                               nodeTypeImplHierarchyMember);

            for (final String implementationArtifactName : implementationArtifactNames) {
                this.deployImplementationArtifact(csarID, nodeTypeImplHierarchyMember, implementationArtifactName,
                                                  requiredFeatures);
            }
        }

    }

    /**
     * @param csarID
     * @param nodeTypeImplementationID
     * @param implementationArtifactName
     * @param requiredFeatures
     */
    private void deployImplementationArtifact(final CSARID csarID, final QName nodeTypeImplementationID,
                                              final String implementationArtifactName,
                                              final List<String> requiredFeatures) {

        IAEngineServiceImpl.LOG.debug("Deploying ImplementationArtifact: {} of NodeTypeImplementation: {} of CSAR: "
            + csarID.getFileName() + " ...", implementationArtifactName, nodeTypeImplementationID);

        Document properties = null;
        List<TPropertyConstraint> propertyConstraints = null;
        List<AbstractArtifact> artifacts = null;

        final QName artifactType =
            this.toscaEngineService.getArtifactTypeOfAImplementationArtifactOfANodeTypeImplementation(csarID,
                                                                                                      nodeTypeImplementationID,
                                                                                                      implementationArtifactName);
        final QName artifactRef =
            this.toscaEngineService.getArtifactTemplateOfAImplementationArtifactOfANodeTypeImplementation(csarID,
                                                                                                          nodeTypeImplementationID,
                                                                                                          implementationArtifactName);
        final Document artifactSpecificContent =
            this.toscaEngineService.getArtifactSpecificContentOfAImplementationArtifactOfANodeTypeImplementation(csarID,
                                                                                                                 nodeTypeImplementationID,
                                                                                                                 implementationArtifactName);

        if (artifactRef != null) {
            properties = this.toscaEngineService.getPropertiesOfAArtifactTemplate(csarID, artifactRef);
            propertyConstraints =
                this.toscaEngineService.getPropertyConstraintsOfAArtifactTemplate(csarID, artifactRef);

            artifacts = this.toscaEngineService.getArtifactsOfAArtifactTemplate(csarID, artifactRef);

        }

        final List<WSDLEndpoint> endpoints =
            this.endpointService.getWSDLEndpointsForNTImplAndIAName(nodeTypeImplementationID,
                                                                    implementationArtifactName);

        URI serviceURI = null;

        // IA already deployed
        if (endpoints != null && endpoints.size() > 0) {

            IAEngineServiceImpl.LOG.debug("ImplementationArtifact: {} of NodeTypeImplementation: {} of CSAR: "
                + csarID.getFileName() + " is already deployed!", implementationArtifactName, nodeTypeImplementationID);

            serviceURI = endpoints.get(0).getURI();

        } else {
            serviceURI =
                this.deployThroughPlugin(csarID, nodeTypeImplementationID, artifactType, artifactSpecificContent,
                                         properties, propertyConstraints, artifacts, requiredFeatures);
        }

        if (serviceURI != null) {

            QName portType;
            // Maybe should be located somewhere else.
            portType = this.getPortType(properties);

            final WSDLEndpoint endpoint = new WSDLEndpoint(serviceURI, portType, csarID, null, nodeTypeImplementationID,
                implementationArtifactName);
            this.endpointService.storeWSDLEndpoint(endpoint);
            IAEngineServiceImpl.LOG.info("ImplementationArtifact: {} of NodeTypeImplementation: {} of CSAR: "
                + csarID.getFileName() + " successfully deployed!", implementationArtifactName,
                                         nodeTypeImplementationID);

        } else {
            this.failedIAList.add(implementationArtifactName);
            IAEngineServiceImpl.LOG.warn("Deployment of ImplementationArtifact {} failed!", implementationArtifactName);
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

        IAEngineServiceImpl.LOG.info("Searching for plugin supporting artifactType: {} ...", artifactType.toString());

        synchronized (this.pluginServices) {
            plugin = this.pluginServices.get(artifactType.toString());
        }

        if (plugin != null) {

            IAEngineServiceImpl.LOG.info("Plugin for artifactType: {} found: {}.", artifactType.toString(),
                                         plugin.toString());

            if (IAEngineCapabilityChecker.capabilitiesAreMet(requiredFeatures, plugin)) {

                serviceEndpoint = plugin.deployImplementationArtifact(csarID, nodeTypeImplementationID, artifactType,
                                                                      artifactSpecificContent, properties,
                                                                      propertyConstraints, artifacts, requiredFeatures);

            } else {
                IAEngineServiceImpl.LOG.warn("Required Features are not met! Deployment aborted.");
            }

        } else {
            IAEngineServiceImpl.LOG.warn("No matching Plug-in for type {} could be found! Deployment aborted.",
                                         artifactType.toString());
        }

        return serviceEndpoint;
    }

    @Override
    /**
     * {@inheritDoc}
     *
     */
    public boolean undeployImplementationArtifacts(final CSARID csarID) {

        IAEngineServiceImpl.LOG.debug("Undeploying all ImplementationArtifacts of CSAR: {} ...", csarID.getFileName());

        IIAEnginePluginService plugin;
        boolean allUndeployed = true;

        IAEngineServiceImpl.LOG.debug("Getting all stored endpoints of CSAR: {} ...", csarID.getFileName());
        final List<WSDLEndpoint> csarEndpoints = this.endpointService.getWSDLEndpointsForCSARID(csarID);

        for (final WSDLEndpoint endpoint : csarEndpoints) {
            final String iaName = endpoint.getIaName();
            final QName nodeTypeImpl = endpoint.getNodeTypeImplementation();
            final URI path = endpoint.getURI();

            if (iaName != null) {
                IAEngineServiceImpl.LOG.debug("- IA: {} ...", iaName);

                final List<WSDLEndpoint> endpoints =
                    this.endpointService.getWSDLEndpointsForNTImplAndIAName(nodeTypeImpl, iaName);

                // IA is used in multiple CSARs: just delete db entry, but do
                // not undeploy IA
                if (endpoints != null && endpoints.size() > 1) {

                    this.endpointService.removeWSDLEndpoint(csarID, endpoint);
                    IAEngineServiceImpl.LOG.debug("IA: {} was not undeployed because it is used in other CSARs too. Only its DB entry was removed.",
                                                  iaName);

                } else {

                    IAEngineServiceImpl.LOG.debug("Trying to undeploy IA: {} ...", iaName);

                    if (iaName != null) {

                        final QName artifactType =
                            this.toscaEngineService.getArtifactTypeOfAImplementationArtifactOfANodeTypeImplementation(csarID,
                                                                                                                      nodeTypeImpl,
                                                                                                                      iaName);

                        synchronized (this.pluginServices) {
                            plugin = this.pluginServices.get(artifactType.toString());
                        }

                        if (plugin != null) {

                            final boolean wasUndeployed =
                                plugin.undeployImplementationArtifact(iaName, nodeTypeImpl, csarID, path);

                            if (wasUndeployed) {

                                this.endpointService.removeWSDLEndpoint(csarID, endpoint);
                                IAEngineServiceImpl.LOG.debug("Undeploying of IA: {} was successful!", iaName);

                            } else {
                                IAEngineServiceImpl.LOG.debug("Undeploying of IA: {} failed!", iaName);
                                allUndeployed = false;
                            }

                        } else {
                            IAEngineServiceImpl.LOG.warn("No matching plugin found for ArtifactType: {}!",
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
    private QName getPortType(final Document properties) {

        // Checks if there are specified properties at all.
        if (properties != null) {

            final NodeList list = properties.getFirstChild().getChildNodes();

            for (int i = 0; i < list.getLength(); i++) {

                final Node propNode = list.item(i);

                if (this.containsPortType(propNode)) {
                    final QName portType = this.getPortType(propNode);
                    IAEngineServiceImpl.LOG.info("PortType found: {}", portType.toString());
                    return portType;
                }
            }
        }
        IAEngineServiceImpl.LOG.debug("No PortType found!");
        return null;
    }

    /**
     * Checks if the Node contains a PortType. A PortType has to be specified with
     * <tt>{@literal <}namespace:PortType{@literal >}...
     * {@literal <}/namespace:PortType{@literal >}</tt>.
     *
     * @param currentNode to check.
     * @return if currentNode contains a PortType.
     */
    private boolean containsPortType(final Node currentNode) {
        final String localName = currentNode.getLocalName();

        if (localName != null) {
            return localName.equals("PortType") || localName.equals("soapPortType");
        }
        return false;
    }

    /**
     * Gets PortType informations defined in current Node and creates a the PortType out of it.
     *
     * @param currentNode to check for PortType information.
     * @return PortType if specified. Otherwise <tt>null</tt>.
     */
    private QName getPortType(final Node currentNode) {
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
                IAEngineServiceImpl.LOG.debug("Bound IA-Plugin: {} for Type: {}", plugin.toString(), type);
            }

            // Store plugin capabilities or cache plugin if Capability Service
            // is not yet available.
            if (this.capabilityService != null) {
                this.capabilityService.storeCapabilities(plugin.getCapabilties(), plugin.toString(),
                                                         ProviderType.IA_PLUGIN);
            } else {
                this.cachedPluginsList.add(plugin);
            }

            IAEngineServiceImpl.LOG.debug("Bind Plugin Service: {} bound.", plugin.toString());
        } else {
            IAEngineServiceImpl.LOG.error("Bind Plugin Service: Supplied parameter is null!");
        }
    }

    /**
     * Unbind PluginService.
     *
     * @param plugin - A ImplementationArtifactEnginePlugin to unregister.
     */
    public void unbindPluginService(final IIAEnginePluginService plugin) {
        if (plugin != null) {

            final List<String> types = plugin.getSupportedTypes();

            for (final String type : types) {
                final Object deletedObject = this.pluginServices.remove(type);
                if (deletedObject != null) {
                    IAEngineServiceImpl.LOG.debug("Unbound IA-Plugin: {} for Type: {}", plugin.toString(), type);
                } else {
                    IAEngineServiceImpl.LOG.debug("IA-Plug-in {} could not be unbound, because it is not bound!",
                                                  plugin.toString());
                }
            }

            if (this.capabilityService != null) {
                this.capabilityService.deleteCapabilities(plugin.toString());
            }

        } else {
            IAEngineServiceImpl.LOG.error("Unbind Plugin Service: Supplied parameter is null!");
        }
    }

    /**
     * Bind EndpointService.
     *
     * @param endpointService - The endpointService to register.
     */
    public void bindEndpointService(final ICoreEndpointService endpointService) {
        if (endpointService != null) {
            if (this.endpointService == null) {
                this.endpointService = endpointService;
            } else {
                this.oldEndpointService = endpointService;
                this.endpointService = endpointService;
            }

            IAEngineServiceImpl.LOG.debug("Bind Endpoint Service: {} bound.", endpointService.toString());
        } else {
            IAEngineServiceImpl.LOG.error("Bind Endpoint Service: Supplied parameter is null!");
        }

    }

    /**
     * Unbind EndpointService.
     *
     * @param endpointService - The endpointService to unregister.
     */
    public void unbindEndpointService(ICoreEndpointService endpointService) {
        if (this.oldEndpointService == null) {
            endpointService = null;
        } else {
            this.oldEndpointService = null;
        }

        IAEngineServiceImpl.LOG.debug("Unbind Endpoint Service unbound.");
    }

    /**
     * Bind CapabilityService.
     *
     * @param capabilityService
     */
    public void bindCoreCapabilityService(final ICoreCapabilityService capabilityService) {
        if (capabilityService != null) {
            if (this.capabilityService == null) {
                this.capabilityService = capabilityService;
            } else {
                this.oldCapabilityService = capabilityService;
                this.capabilityService = capabilityService;
            }

            for (final IIAEnginePluginService plugin : this.cachedPluginsList) {
                this.capabilityService.storeCapabilities(plugin.getCapabilties(), plugin.toString(),
                                                         ProviderType.IA_PLUGIN);
            }
            this.cachedPluginsList.clear();

            IAEngineServiceImpl.LOG.debug("Bind CapabilityService: {} bound.", capabilityService.toString());
        } else {
            IAEngineServiceImpl.LOG.error("Bind CapabilityService: Supplied parameter is null!");
        }
    }

    /**
     * Unbind CapabilityService.
     *
     * @param capabilityService
     */
    public void unbindCoreCapabilityService(ICoreCapabilityService capabilityService) {
        if (this.oldCapabilityService == null) {
            capabilityService = null;
        } else {
            this.oldCapabilityService = null;
        }

        IAEngineServiceImpl.LOG.debug("Unbind CapabilityService unbound.");
    }

    /**
     * Bind ToscaService
     *
     * @param toscaEngineService
     */
    public void bindToscaService(final IToscaEngineService toscaEngineService) {
        if (toscaEngineService != null) {
            if (this.toscaEngineService == null) {
                this.toscaEngineService = toscaEngineService;
            } else {
                this.oldToscaEngineService = toscaEngineService;
                this.toscaEngineService = toscaEngineService;
            }

            IAEngineServiceImpl.LOG.debug("Bind ToscaService: {} bound.", toscaEngineService.toString());
        } else {
            IAEngineServiceImpl.LOG.error("Bind ToscaService: Supplied parameter is null!");
        }
    }

    /**
     * Unbind ToscaService
     *
     * @param toscaEngineService
     */
    public void unbindToscaService(IToscaEngineService toscaEngineService) {
        if (this.oldToscaEngineService == null) {
            toscaEngineService = null;
        } else {
            this.oldToscaEngineService = null;
        }

        IAEngineServiceImpl.LOG.debug("Unbind ToscaService unbound.");
    }
}
