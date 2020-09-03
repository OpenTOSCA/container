package org.opentosca.bus.management.service.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.namespace.QName;

import org.eclipse.winery.common.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.model.tosca.TArtifactReference;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.TEntityTypeImplementation;
import org.eclipse.winery.model.tosca.TImplementationArtifact;
import org.eclipse.winery.model.tosca.TImplementationArtifacts;
import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TRequiredContainerFeatures;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTag;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultExchange;
import org.eclipse.jdt.annotation.Nullable;
import org.opentosca.bus.management.deployment.plugin.IManagementBusDeploymentPluginService;
import org.opentosca.bus.management.header.MBHeader;
import org.opentosca.bus.management.service.IManagementBusService;
import org.opentosca.bus.management.service.impl.collaboration.CollaborationContext;
import org.opentosca.bus.management.service.impl.collaboration.DeploymentDistributionDecisionMaker;
import org.opentosca.bus.management.service.impl.instance.plan.CorrelationIdAlreadySetException;
import org.opentosca.bus.management.service.impl.instance.plan.PlanInstanceHandler;
import org.opentosca.bus.management.service.impl.util.DeploymentPluginCapabilityChecker;
import org.opentosca.bus.management.service.impl.util.ParameterHandler;
import org.opentosca.bus.management.service.impl.util.PluginHandler;
import org.opentosca.bus.management.service.impl.util.Util;
import org.opentosca.bus.management.utils.MBUtils;
import org.opentosca.container.core.common.NotFoundException;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.common.xml.XMLHelper;
import org.opentosca.container.core.engine.ToscaEngine;
import org.opentosca.container.core.engine.next.ContainerEngine;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.model.endpoint.wsdl.WSDLEndpoint;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.PlanInstance;
import org.opentosca.container.core.next.model.PlanInstanceEvent;
import org.opentosca.container.core.next.model.PlanLanguage;
import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.container.core.next.model.RelationshipTemplateInstance;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.repository.PlanInstanceRepository;
import org.opentosca.container.core.next.trigger.SituationTriggerInstanceListener;
import org.opentosca.container.core.service.CsarStorageService;
import org.opentosca.container.core.service.ICoreEndpointService;
import org.opentosca.container.core.tosca.convention.Types;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Engine for delegating invoke-requests of implementation artifacts or plans to matching plug-ins.<br>
 * <br>
 * <p>
 * Copyright 2019 IAAS University of Stuttgart <br>
 * <br>
 * <p>
 * The engine gets the invoke-request as a camel exchange object with all needed parameters (e.g. CSARID,
 * ServiceTemplateID, CorrelationID...) in the header and the actual invoke message in the body of it. <br>
 * <br>
 * <p>
 * In case of invoking an operation of an implementation artifact, the engine identifies with help of the
 * <tt>ToscaEngine</tt> and the parameters from the header the right implementation artifact. Afterwards it checks if
 * the implementation artifact is already deployed by using the
 * <tt>EndpointService</tt>. If this is not the case it tries to deploy the implementation artifact
 * by using an available deployment plug-in and stores a corresponding endpoint. When an endpoint was found/created the
 * engine determines which invocation plug-in has to be used to call the operation. Therefore, the engine uses
 * information like the ArtifactType of the implementation artifact or a specified property like <tt>{@literal
 * <}namespace:InvocationType{@literal >}... {@literal <}/namespace:InvocationType{@literal >}</tt>. Finally, the engine
 * calls the implementation artifact operation by passing the exchange to the invocation plug-in. The engine is also
 * able to update request parameters from stored <tt>InstanceData</tt> before passing the request on.<br>
 * <br>
 * <p>
 * In case of invoking a plan no deployment is needed as this is already done when the corresponding CSAR is deployed on
 * the OpenTOSCA Container. The engine determines the invocation plug-in by checking the language of the plan and
 * afterwards invokes the plan via this plug-in.<br>
 * <br>
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 * @author Benjamin Weder - st100495@stud.uni-stuttgart.de
 * @see IManagementBusDeploymentPluginService
 * @see ICoreEndpointService
 */

@Service
@Named("managementBusService")
public class ManagementBusServiceImpl implements IManagementBusService {

    private final static Logger LOG = LoggerFactory.getLogger(ManagementBusServiceImpl.class);

    private final static Map<String, Object> locks = new HashMap<>();

    private final DeploymentDistributionDecisionMaker decisionMaker;
    private final CollaborationContext collaborationContext;
    private final ICoreEndpointService endpointService;
    private final ParameterHandler parameterHandler;
    private final PluginHandler pluginHandler;
    private final PluginRegistry pluginRegistry;
    private final DeploymentPluginCapabilityChecker capabilityChecker;
    private final ContainerEngine containerEngine;

    private final CsarStorageService storage;

    private static ConcurrentHashMap<String, List<String>> activePartners = new ConcurrentHashMap<>();

    @Inject
    public ManagementBusServiceImpl(DeploymentDistributionDecisionMaker decisionMaker,
                                    CollaborationContext collaborationContext,
                                    ICoreEndpointService endpointService,
                                    ParameterHandler parameterHandler,
                                    PluginHandler pluginHandler,
                                    PluginRegistry pluginRegistry,
                                    DeploymentPluginCapabilityChecker capabilityChecker,
                                    ContainerEngine containerEngine, CsarStorageService storage) {
        LOG.info("Instantiating ManagementBus Service");
        this.decisionMaker = decisionMaker;
        this.collaborationContext = collaborationContext;
        this.endpointService = endpointService;
        this.parameterHandler = parameterHandler;
        this.pluginHandler = pluginHandler;
        this.pluginRegistry = pluginRegistry;
        this.capabilityChecker = capabilityChecker;
        this.storage = storage;
        this.containerEngine = containerEngine;
    }

    @Override
    public void invokeIA(final Exchange exchange) {
        final Message message = exchange.getIn();

        final URI serviceInstanceID = message.getHeader(MBHeader.SERVICEINSTANCEID_URI.toString(), URI.class);
        LOG.debug("ServiceInstanceID: {}", serviceInstanceID);
        // get the ServiceTemplateInstance ID Long from the serviceInstanceID URI
        final Long serviceTemplateInstanceID = Util.determineServiceTemplateInstanceId(serviceInstanceID);

        final CsarId csarID = message.getHeader(MBHeader.CSARID.toString(), CsarId.class);
        LOG.debug("CSARID: {}", csarID.toString());
        final QName serviceTemplateID = message.getHeader(MBHeader.SERVICETEMPLATEID_QNAME.toString(), QName.class);
        LOG.debug("serviceTemplateID: {}", serviceTemplateID);

        final String nodeTemplateID = message.getHeader(MBHeader.NODETEMPLATEID_STRING.toString(), String.class);
        LOG.debug("NodeTemplateID: {}", nodeTemplateID);

        final String relationship = message.getHeader(MBHeader.RELATIONSHIPTEMPLATEID_STRING.toString(), String.class);
        LOG.debug("RelationshipTemplateID: {}", relationship);

        final String neededInterface = message.getHeader(MBHeader.INTERFACENAME_STRING.toString(), String.class);
        LOG.debug("Interface: {}", neededInterface);

        final String neededOperation = message.getHeader(MBHeader.OPERATIONNAME_STRING.toString(), String.class);
        LOG.debug("Operation: {}", neededOperation);

        // log event to monitor the IA execution time
        final PlanInstanceEvent event;
        // operation invocation is only possible with retrieved ServiceTemplateInstance ID
        if (!serviceTemplateInstanceID.equals(Long.MIN_VALUE)) {

            final IAInvocationArguments arguments = new IAInvocationArguments(csarID, serviceInstanceID, serviceTemplateID, serviceTemplateInstanceID,
                nodeTemplateID, relationship, neededInterface, neededOperation);
            event = internalInvokeIA(arguments, exchange);
            LOG.info("IA execution duration: {}", event.getDuration());
        } else {
            LOG.error("Unable to invoke operation without ServiceTemplateInstance ID!");
            handleResponse(exchange);
            event = new PlanInstanceEvent("WARN", "IA_DURATION_LOG", "Unable to invoke operation without ServiceTemplateInstance ID!");
        }

        final String correlationID = message.getHeader(MBHeader.PLANCORRELATIONID_STRING.toString(), String.class);
        LOG.debug("Correlation ID: {}", correlationID);
        if (Objects.nonNull(correlationID)) {
            // update plan in repository with new log event
            final PlanInstanceRepository repo = new PlanInstanceRepository();
            final PlanInstance plan = repo.findByCorrelationId(correlationID);
            if (Objects.nonNull(plan)) {
                plan.addEvent(event);
                repo.update(plan);
            }
        }

        if (Objects.nonNull(correlationID)) {
            // add end timestamp and log message with duration
            event.setEndTimestamp(new Date());
            final long duration = event.getEndTimestamp().getTime() - event.getStartTimestamp().getTime();
            event.setMessage("Finished execution of IA for NodeTemplate '" + nodeTemplateID + "' interface '"
                + neededInterface + "' and operation '" + neededOperation + "' after " + duration + "ms");
            LOG.info("IA execution duration: {}ms", duration);
            event.setNodeTemplateID(nodeTemplateID);
            event.setInterfaceName(neededInterface);
            event.setOperationName(neededOperation);
            event.setExecutionDuration(duration);

            // update plan in repository with new log event
            final PlanInstanceRepository repo = new PlanInstanceRepository();
            final PlanInstance plan = repo.findByCorrelationId(correlationID);

            if (Objects.nonNull(plan)) {
                plan.addEvent(event);
                repo.update(plan);
            }
        }
    }

    private void respondViaMocking(final TOperation.@Nullable OutputParameters outputParameters, final Exchange exchange) {

        final long waitTime = System.currentTimeMillis() + 1000;
        while (System.currentTimeMillis() > waitTime) {
            // busy waiting here...
        }

        final Message message = exchange.getIn();
        final Map<String, String> responseMap = new HashMap<>();

        final Object params = message.getBody();
        if (params != null && params instanceof HashMap && ((HashMap) params).values().contains("fault")) {
            responseMap.put("Fault", "managementBusMockFaultValue");
        }

        if (outputParameters == null || outputParameters.getOutputParameter().isEmpty()) {
            handleResponse(exchange);
            return;
        }

        outputParameters.getOutputParameter()
            .forEach(param -> {
                responseMap.put(param.getName(), "managementBusMockValue");
            });

        exchange.getIn().setBody(responseMap);
        handleResponse(exchange);
    }

    /**
     * Searches for the NodeType/RelationshipType of the given operation, updates the input parameters and passes the
     * request on to invoke the corresponding IA.
     *
     * @param exchange  exchange containing the header fields which identify the current operation
     * @param arguments a bundle-object containing all relevant invocation arguments
     */
    private PlanInstanceEvent internalInvokeIA(IAInvocationArguments arguments, Exchange exchange) {
        LOG.debug("Starting Management Bus: InvokeIA");

        final Message message = exchange.getIn();

        // log event to monitor the IA execution time
        final PlanInstanceEvent event = new PlanInstanceEvent("INFO", "IA_DURATION_LOG",
            "Finished execution of IA for NodeTemplate '" + arguments.nodeTemplateId + "' interface '" + arguments.interfaceName + "' and operation '" + arguments.operationName + "'");

        final Csar csar = storage.findById(arguments.csarId);
        final TServiceTemplate serviceTemplate;
        try {
            serviceTemplate = ToscaEngine.resolveServiceTemplate(csar, arguments.serviceTemplateId);
        } catch (NotFoundException e) {
            LOG.error("ServiceTemplate {} does not exist within Csar {}. Aborting IA Invocation", arguments.serviceTemplateId, arguments.csarId.csarName());
            event.setEndTimestamp(new Date());
            return event;
        }

        QName typeID = null;
        if (Objects.nonNull(arguments.nodeTemplateId)) {
            Optional<TNodeTemplate> nodeTemplate = ToscaEngine.getNodeTemplate(serviceTemplate, arguments.nodeTemplateId);
            if (nodeTemplate.isPresent()) {
                typeID = nodeTemplate.get().getType();
            }
        } else if (Objects.nonNull(arguments.relationshipTemplateId)) {
            Optional<TRelationshipTemplate> relTemplate = ToscaEngine.getRelationshipTemplate(serviceTemplate, arguments.relationshipTemplateId);
            if (relTemplate.isPresent()) {
                typeID = relTemplate.get().getType();
            }
        }
        if (typeID == null) {
            LOG.error(String.format("Could not resolve a type for the given nodeTemplateId/relationshipTemplateId [%s/%s]",
                arguments.nodeTemplateId, arguments.relationshipTemplateId));
            handleResponse(exchange);
            event.setEndTimestamp(new Date());
            return event;
        }

        // invocation is only possible with retrieved type which contains the operation
        TEntityType type;
        try {
            type = ToscaEngine.resolveEntityTypeReference(csar, typeID);
        } catch (NotFoundException e) {
            type = null;
        }
        if (!Objects.nonNull(typeID) || !Objects.nonNull(type)) {
            LOG.error("Unable to retrieve the NodeType/RelationshipType for NodeTemplate: {} and RelationshipTemplate: {}",
                arguments.nodeTemplateId, arguments.relationshipTemplateId);
            handleResponse(exchange);
            event.setEndTimestamp(new Date());
            return event;
        }

        // get NodeTemplateInstance object for the deployment distribution decision
        NodeTemplateInstance nodeInstance;
        final RelationshipTemplateInstance relationshipInstance;
        if (Objects.nonNull(arguments.nodeTemplateId)) {
            nodeInstance = MBUtils.getNodeTemplateInstance(arguments.serviceTemplateInstanceId, arguments.nodeTemplateId);
            relationshipInstance = null;
        } else if (Objects.nonNull(arguments.relationshipTemplateId)) {
            relationshipInstance = MBUtils.getRelationshipTemplateInstance(arguments.serviceTemplateInstanceId, arguments.relationshipTemplateId);
            // assuming type is a TRelationshipType, because otherwise this should be unreachable
            TRelationshipType relationshipType = (TRelationshipType) type;
            if (Objects.nonNull(relationshipInstance) && Objects.nonNull(relationshipType)) {
                nodeInstance = containerEngine.resolveRelationshipOperationTarget(relationshipInstance,
                    relationshipType,
                    arguments.interfaceName, arguments.operationName);
            } else {
                nodeInstance = null;
            }
        } else {
            relationshipInstance = null;
            nodeInstance = null;
        }

        Csar replacementCsar = null;
        if (typeID.equals(Types.abstractOperatingSystemNodeType)) {
            // replace abstract operating system node instance
            nodeInstance = MBUtils.getAbstractOSReplacementInstance(nodeInstance);
            assert (nodeInstance != null); // if not, we're fucked anyways
            final ServiceTemplateInstance replacementSTI = nodeInstance.getServiceTemplateInstance();
            replacementCsar = storage.findById(replacementSTI.getCsarId());
            try {
                final TServiceTemplate replacementST = ToscaEngine.resolveServiceTemplate(replacementCsar, replacementSTI.getTemplateId());
                final TNodeTemplate replacementTemplate = ToscaEngine.resolveNodeTemplate(replacementST, nodeInstance.getTemplateId());
                type = ToscaEngine.resolveNodeType(replacementCsar, replacementTemplate);
            } catch (NotFoundException e) {
                LOG.error("Could not compute replacing type for abstract Operating System Node replacement. Aborting IA invocation.", e);
                handleResponse(exchange);
                event.setEndTimestamp(new Date());
                return event;
            }
        }

        // update input parameters for the operation call
        if (message.getBody() instanceof HashMap) {
            @SuppressWarnings("unchecked")
            Map<String, String> inputParams = (Map<String, String>) message.getBody();

            inputParams = parameterHandler.updateInputParams(inputParams, replacementCsar == null ? csar : replacementCsar,
                nodeInstance, relationshipInstance, arguments.interfaceName, arguments.operationName);
            message.setBody(inputParams);
        } else {
            LOG.warn("There are no input parameters specified.");
        }

        internalInvokeIA(exchange, replacementCsar != null ? replacementCsar : csar, arguments.serviceTemplateInstanceId, type, nodeInstance, arguments.interfaceName, arguments.operationName);
        event.setEndTimestamp(new Date());
        return event;
    }

    /**
     * Searches the right IA for the given operation and invokes it with the given parameters.
     *
     * @param exchange                  exchange containing the input parameters of the operation
     * @param csar                      the CSAR
     * @param serviceTemplateInstanceID ID of the service instance
     * @param type                      NodeType/RelationshipType that implements the operation
     * @param nodeTemplateInstance      NodeTemplateInstance for the deployment distribution decision
     * @param neededInterface           the interface of the searched operation
     * @param neededOperation           the searched operation
     */
    private void internalInvokeIA(final Exchange exchange, final Csar csar, final Long serviceTemplateInstanceID,
                                  final TEntityType type, final NodeTemplateInstance nodeTemplateInstance,
                                  final String neededInterface, final String neededOperation) {

        LOG.debug("NodeType/RelationshipType: {}", type.getQName());
        final Message message = exchange.getIn();

        // check whether operation has output parameters
        final boolean hasOutputParams;
        try {
            TInterface nodeTypeInterface = ToscaEngine.resolveInterfaceAbstract(type, neededInterface);
            TOperation operation = ToscaEngine.resolveOperation(nodeTypeInterface, neededOperation);
            hasOutputParams = operation.getOutputParameters() != null && !operation.getOutputParameters().getOutputParameter().isEmpty();
            if (Boolean.parseBoolean(Settings.OPENTOSCA_BUS_MANAGEMENT_MOCK)) {
                respondViaMocking(operation.getOutputParameters(), exchange);
                return;
            }
        } catch (NotFoundException notFound) {
            LOG.warn("Tried to invoke an unknown operation on an IA");
            return;
        }
        message.setHeader(MBHeader.HASOUTPUTPARAMS_BOOLEAN.toString(), hasOutputParams);

        final List<? extends TEntityTypeImplementation> typeImplementations = ToscaEngine.getTypeImplementations(csar, type);

        LOG.debug("List of Node/RelationshipTypeImplementations: {}", typeImplementations.toString());

        // Search for an IA that implements the right operation and which is deployable and
        // invokable by available plug-ins
        for (TEntityTypeImplementation implementation : typeImplementations) {
            message.setHeader(MBHeader.TYPEIMPLEMENTATIONID_QNAME.toString(), implementation.getQName());
            final List<? extends TImplementationArtifact> ias = Optional.ofNullable(implementation.getImplementationArtifacts())
                .map(TImplementationArtifacts::getImplementationArtifact)
                .orElse(Collections.emptyList());
            LOG.debug("List of Implementation Artifacts: {}", ias.stream().map(ia -> {
                return String.format("{%s, %s %s}", ia.getIdFromIdOrNameField(), ia.getOperationName(), ia.getArtifactRef());
            }).collect(Collectors.joining(", ")));

            for (final TImplementationArtifact ia : ias) {
                // try to invoke the operation on the current IA
                if (invokeIAOperation(exchange, csar, serviceTemplateInstanceID, type, nodeTemplateInstance,
                    implementation, ia, neededInterface, neededOperation)) {
                    LOG.info("Successfully invoked Operation {} on IA {}", neededOperation, ia.getName());
                    return;
                }
            }
        }

        LOG.warn("No invokable implementation artifact found that provides required interface/operation.");
        handleResponse(exchange);
    }

    /**
     * Invokes the given operation on the given IA if it implements it. If the IA is not yet deployed, the deployment is
     * performed before the invocation.
     *
     * @param exchange                  exchange containing the input parameters of the operation
     * @param csar                      The CSAR
     * @param serviceTemplateInstanceID ID of the service instance
     * @param type                      NodeType/RelationshipType that implements the operation
     * @param nodeTemplateInstance      NodeTemplateInstance for the deployment distribution decision
     * @param typeImplementation        NodeTypeImpl/RelationshipTypeImpl containing the IA
     * @param ia                        the Implementation Artifact itself
     * @param neededInterface           the interface of the searched operation
     * @param neededOperation           the searched operation
     * @return <tt>true</tt> if the IA implements the given operation and it was invoked
     * successfully, <tt>false</tt> otherwise
     */
    private boolean invokeIAOperation(Exchange exchange, final Csar csar, final Long serviceTemplateInstanceID,
                                      final TEntityType type, final NodeTemplateInstance nodeTemplateInstance,
                                      final TEntityTypeImplementation typeImplementation, final TImplementationArtifact ia,
                                      final String neededInterface, final String neededOperation) {
        LOG.debug("Trying to invoke Implementation Artifact: {}", ia.getName());
        final Message message = exchange.getIn();

        // host name of the container which triggered the IA invocation
        final String triggeringContainer = Settings.OPENTOSCA_CONTAINER_HOSTNAME;
        message.setHeader(MBHeader.TRIGGERINGCONTAINER_STRING.toString(), triggeringContainer);

        // check if requested interface/operation is provided
        if (!iaProvidesRequestedOperation(csar, ia, type, neededInterface, neededOperation)) {
            LOG.debug("Implementation Artifact does not provide the requested operation.");
            return false;
        }

        // get ArtifactTemplate and ArtifactType of the IA
        ArtifactTemplateId artifactTemplateId = new ArtifactTemplateId(ia.getArtifactRef());
        TArtifactTemplate artifactTemplate = (TArtifactTemplate) csar.queryRepository(artifactTemplateId);
        LOG.debug("ArtifactTemplate: {}", artifactTemplate.toString());

        final QName artifactTypeQName = ia.getArtifactType();
        LOG.debug("ArtifactType: {}", artifactTypeQName);

        // retrieve deployment type for the IA
        final String deploymentType = pluginHandler.getSupportedDeploymentType(artifactTypeQName);
        if (Objects.isNull(deploymentType)) {
            LOG.debug("No deployment plug-in found which supports the deployment of ArtifactType {}", artifactTypeQName);
            return false;
        }

        // retrieve invocation type for the IA
        final String invocationType = pluginHandler.getSupportedInvocationType(artifactTypeQName, artifactTemplate);
        if (Objects.isNull(invocationType)) {
            LOG.debug("No invocation plug-in found which supports the invocation of ArtifactType {} and ArtifactTemplate {}",
                artifactTypeQName, artifactTemplate.getId());
            return false;
        }

        LOG.debug("Deployment type {} and invocation type {} are supported.", deploymentType, invocationType);

        // retrieve portType property if specified
        final QName portType = Util.getPortTypeQName(artifactTemplate);

        // retrieve specific content for the IA if defined and add to the headers
        exchange = addSpecificContent(exchange, ia);

        // host name of the container where the IA has to be deployed
        final String deploymentLocation = decisionMaker.getDeploymentLocation(nodeTemplateInstance);
        LOG.debug("Host name of responsible OpenTOSCA Container: {}", deploymentLocation);

        // set needed header fields for the invocation/deployment plug-ins
        message.setHeader(MBHeader.DEPLOYMENTLOCATION_STRING.toString(), deploymentLocation);
        message.setHeader(MBHeader.PORT_TYPE_QNAME.toString(), portType);
        message.setHeader(MBHeader.INVOCATIONTYPE_STRING.toString(), invocationType);
        message.setHeader(MBHeader.IMPLEMENTATION_ARTIFACT_NAME_STRING.toString(), ia.getName());
        message.setHeader(MBHeader.ARTIFACTTEMPLATEID_QNAME.toString(), artifactTemplateId.getQName());
        message.setHeader(MBHeader.ARTIFACTTYPEID_STRING.toString(), artifactTypeQName);

        // Prevent two threads from trying to deploy the same IA concurrently and avoid the deletion
        // of an IA after successful checking that an IA is already deployed.
        final String identifier = getUniqueSynchronizationString(triggeringContainer, deploymentLocation, typeImplementation.getQName(), ia.getName(), serviceTemplateInstanceID.toString());
        synchronized (getLockForString(identifier)) {

            LOG.debug("Checking whether IA [{}] was already deployed", ia.getName());

            // check whether there are already stored endpoints for this IA
            final List<WSDLEndpoint> endpoints = endpointService.getWSDLEndpointsForNTImplAndIAName(triggeringContainer, deploymentLocation, typeImplementation.getQName(), ia.getName());

            if (Objects.nonNull(endpoints) && !endpoints.isEmpty()) {
                LOG.debug("IA is already deployed.");

                URI endpointURI = endpoints.get(0).getURI();
                message.setHeader(MBHeader.ENDPOINT_URI.toString(), endpointURI);

                Optional<WSDLEndpoint> currentEndpoint = endpoints.stream()
                    .filter(wsdlEndpoint -> wsdlEndpoint.getServiceTemplateInstanceID().equals(serviceTemplateInstanceID))
                    .findFirst();

                if (!currentEndpoint.isPresent()) {
                    // store new endpoint for the IA
                    final WSDLEndpoint endpoint = new WSDLEndpoint(endpointURI, portType, triggeringContainer,
                        deploymentLocation, csar.id(), serviceTemplateInstanceID, null, typeImplementation.getQName(), ia.getName(), new HashMap<>());
                    endpointService.storeWSDLEndpoint(endpoint);
                }

                // Call IA, send response to caller and terminate bus
                LOG.debug("Trying to invoke the operation on the deployed implementation artifact.");
                handleResponse(pluginHandler.callMatchingInvocationPlugin(exchange, invocationType, deploymentLocation));
                return true;
            }
            LOG.debug("IA not yet deployed. Trying to deploy...");
            LOG.debug("Checking if all required features are met by the deployment plug-in or the environment.");

            final IManagementBusDeploymentPluginService deploymentPlugin = pluginRegistry.getDeploymentPluginServices().get(deploymentType);
            // retrieve required features for the TypeImplementation
            final TRequiredContainerFeatures requiredFeatures = typeImplementation.getRequiredContainerFeatures();

            // check whether all features are met and abort deployment otherwise
            if (!capabilityChecker.capabilitiesAreMet(requiredFeatures, deploymentPlugin)) {
                LOG.debug("Required features not completely satisfied by the plug-in.");
                return false;
            }

            // get all artifact references for this ArtifactTemplate
            final List<TArtifactReference> artifacts = Optional.ofNullable(artifactTemplate.getArtifactReferences())
                .map(TArtifactTemplate.ArtifactReferences::getArtifactReference)
                .orElse(Collections.emptyList());

            // convert relative references to absolute references to enable access to the IA
            // files from other OpenTOSCA Container nodes
            LOG.debug("Searching for artifact references for ArtifactTemplate {}", artifactTemplate.getIdFromIdOrNameField());
            final List<String> artifactReferences = new ArrayList<>();
            for (final TArtifactReference artifact : artifacts) {
                // XML validated to be anyUri, therefore must be parsable as URI
                URI reference = URI.create(artifact.getReference().trim());
                if (reference.getScheme() != null) {
                    LOG.warn("ArtifactReference {} of Csar {} is not supported", artifact.getReference(), csar.id());
                    continue;
                }
                // artifact is exposed via the content endpoint
                String absoluteArtifactReference = Settings.OPENTOSCA_CONTAINER_CONTENT_API
                    .replace("{csarid}", csar.id().csarName())
                    // reference here is relative to CSAR basedirectory, with spaces being URLEncoded
                    .replace("{artifactreference}", artifact.getReference().trim().replaceAll(" ", "%20"));

                artifactReferences.add(absoluteArtifactReference);
                LOG.debug("Found reference: {} ", absoluteArtifactReference);
            }

            if (artifactReferences.isEmpty()) {
                LOG.debug("No artifact references found. No deployment and invocation possible for this ArtifactTemplate.");
                return false;
            }
            // add references list to header to enable access from the deployment plug-ins
            message.setHeader(MBHeader.ARTIFACTREFERENCES_LISTSTRING.toString(), artifactReferences);

            // search ServiceEndpoint property for the artifact
            final String serviceEndpoint = Util.getProperty(artifactTemplate, "ServiceEndpoint");
            message.setHeader(MBHeader.ARTIFACTSERVICEENDPOINT_STRING.toString(), serviceEndpoint);

            if (Objects.nonNull(serviceEndpoint)) {
                LOG.debug("ServiceEndpoint property: {}", serviceEndpoint);
            } else {
                LOG.debug("No ServiceEndpoint property defined!");
            }

            // invoke deployment
            exchange = pluginHandler.callMatchingDeploymentPlugin(exchange, deploymentType, deploymentLocation);
            URI endpointURI = message.getHeader(MBHeader.ENDPOINT_URI.toString(), URI.class);

            if (!Objects.nonNull(endpointURI)) {
                LOG.debug("IA deployment failed.");
                return false;
            }
            if (endpointURI.toString().contains(Constants.PLACEHOLDER_START)
                && endpointURI.toString().contains(Constants.PLACEHOLDER_END)) {

                // If a placeholder is specified, the service is part of the topology.
                // We do not store this endpoints as they are not part of the management environment.
                LOG.debug("Received endpoint contains placeholders. Service is part of the topology and called without deployment.");
                endpointURI = replacePlaceholderWithInstanceData(endpointURI, nodeTemplateInstance);
                message.setHeader(MBHeader.ENDPOINT_URI.toString(), endpointURI);
            } else {
                LOG.debug("IA successfully deployed. Storing endpoint...");

                // store new endpoint for the IA
                final WSDLEndpoint endpoint =
                    new WSDLEndpoint(endpointURI, portType, triggeringContainer, deploymentLocation,
                        csar.id(), serviceTemplateInstanceID, null, typeImplementation.getQName(), ia.getName(), new HashMap<>());
                endpointService.storeWSDLEndpoint(endpoint);
            }
            LOG.debug("Endpoint: {}", endpointURI.toString());

            // Call IA, send response to caller and terminate bus
            LOG.debug("Trying to invoke the operation on the deployed implementation artifact.");
            handleResponse(pluginHandler.callMatchingInvocationPlugin(exchange, invocationType, deploymentLocation));
            return true;
        }
    }

    @Override
    public void invokePlan(Exchange exchange) {
        LOG.debug("Parsing Camel Exchange message to PlanInvocationArguments");

        final Message message = exchange.getIn();
        String correlationID = message.getHeader(MBHeader.PLANCORRELATIONID_STRING.toString(), String.class);
        LOG.trace("Correlation ID: {}", correlationID);
        // generate new unique correlation ID if no ID is passed
        if (Objects.isNull(correlationID)) {
            correlationID = PlanInstanceHandler.createCorrelationId();
            message.setHeader(MBHeader.PLANCORRELATIONID_STRING.toString(), correlationID);
        }

        final CsarId csarID = new CsarId(message.getHeader(MBHeader.CSARID.toString(), String.class));
        LOG.trace("CSARID: " + csarID.csarName());

        final URI serviceInstanceID = message.getHeader(MBHeader.SERVICEINSTANCEID_URI.toString(), URI.class);
        LOG.trace("csarInstanceID: {}", serviceInstanceID);

        final QName serviceTemplateID = message.getHeader(MBHeader.SERVICETEMPLATEID_QNAME.toString(), QName.class);
        LOG.debug("serviceTemplateID: {}", serviceTemplateID);

        final QName planID = message.getHeader(MBHeader.PLANID_QNAME.toString(), QName.class);
        LOG.debug("planID: {}", planID);

        final String operationName = message.getHeader(MBHeader.OPERATIONNAME_STRING.toString(), String.class);
        LOG.debug("operationName: {}", operationName);

        // get the ServiceTemplateInstance ID Long from the serviceInstanceID URI
        final Long serviceTemplateInstanceID = Util.determineServiceTemplateInstanceId(serviceInstanceID);
        final Csar csar = storage.findById(csarID);

        internalInvokePlan(new PlanInvocationArguments(csar, serviceTemplateID, serviceTemplateInstanceID, planID, operationName, correlationID), exchange);
    }

    @Override
    public void notifyPartner(final Exchange exchange) {

        final Message message = exchange.getIn();
        final String correlationID = message.getHeader(MBHeader.PLANCORRELATIONID_STRING.toString(), String.class);
        final CsarId csarID = message.getHeader(MBHeader.CSARID.toString(), CsarId.class);
        final QName serviceTemplateID = message.getHeader(MBHeader.SERVICETEMPLATEID_QNAME.toString(), QName.class);

        if (!(exchange.getIn().getBody() instanceof HashMap)) {
            LOG.error("Message to notify partner with Correlation ID {}, CSARID {} and ServiceTemplate ID {} contains no parameters. Aborting!",
                correlationID, csarID, serviceTemplateID);
            return;
        }

        // retrieve parameters defining the partner and RelationshipTemplate from the exchange body
        @SuppressWarnings("unchecked") final HashMap<String, String> params = (HashMap<String, String>) exchange.getIn().getBody();
        final String connectingRelationshipTemplate = params.get(Constants.RELATIONSHIP_TEMPLATE_PARAM);
        final String receivingPartner = params.get(Constants.RECEIVING_PARTNER_PARAM);

        LOG.debug("Notifying partner {} for connectsTo with ID {} for choreography with correlation ID {}, CsarID {}, and ServiceTemplateID {}",
            receivingPartner, connectingRelationshipTemplate, correlationID, csarID, serviceTemplateID);

        // wait until other partner is ready to receive notify
        while (!this.isPartnerAvailable(correlationID, receivingPartner)) {
            LOG.debug("Waiting for partner: {}", receivingPartner);
            try {
                Thread.sleep(10000);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            Thread.sleep(10000);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }

        // retrieve ServiceTemplate related to the notification request
        final TServiceTemplate serviceTemplate = this.storage.findById(csarID).entryServiceTemplate();
        if (Objects.isNull(serviceTemplate)) {
            LOG.error("Unable to retrieve ServiceTemplate for the notification request.");
            return;
        }

        // get tag defining the endpoint of the partner
        final Optional<TTag> endpointTagOptional =
            Util.getPartnerEndpoints(serviceTemplate).stream().filter(tag -> tag.getName().equals(receivingPartner))
                .findFirst();
        if (!endpointTagOptional.isPresent()) {
            LOG.error("No endpoint tag available for partner {}", receivingPartner);
            return;
        }

        final String endpoint = endpointTagOptional.get().getValue();
        LOG.debug("Notifying partner {} on endpoint: {}", receivingPartner, endpoint);

        message.setHeader(MBHeader.HASOUTPUTPARAMS_BOOLEAN.toString(), false);
        message.setHeader(MBHeader.ENDPOINT_URI.toString(), endpoint);
        message.setHeader(MBHeader.OPERATIONNAME_STRING.toString(), Constants.RECEIVE_NOTIFY_PARTNER_OPERATION);

        // create message body
        final HashMap<String, String> inputMap = new HashMap<>();
        inputMap.put(Constants.PLAN_CORRELATION_PARAM, correlationID);
        inputMap.put(Constants.CSARID_PARAM, csarID.toString());
        inputMap.put(Constants.SERVICE_TEMPLATE_NAMESPACE_PARAM, serviceTemplateID.getNamespaceURI());
        inputMap.put(Constants.SERVICE_TEMPLATE_LOCAL_PARAM, serviceTemplateID.getLocalPart());
        inputMap.put(Constants.MESSAGE_ID_PARAM, String.valueOf(System.currentTimeMillis()));

        // parse to doc and add input parameters
        final Document inputDoc =
            MBUtils.mapToDoc(Constants.BUS_WSDL_NAMESPACE, Constants.RECEIVE_NOTIFY_PARTNER_OPERATION, inputMap);

        final Element root = inputDoc.getDocumentElement();
        final Element paramsWrapper = inputDoc.createElement(Constants.PARAMS_PARAM);
        root.appendChild(paramsWrapper);
        for (final Entry<String, String> entry : params.entrySet()) {
            final Element paramElement = inputDoc.createElement("Param");
            paramsWrapper.appendChild(paramElement);

            final Element keyElement = inputDoc.createElement("key");
            keyElement.setTextContent(entry.getKey());
            paramElement.appendChild(keyElement);

            final Element valueElement = inputDoc.createElement("value");
            valueElement.setTextContent(entry.getValue());
            paramElement.appendChild(valueElement);
        }
        message.setBody(inputDoc);

        this.pluginHandler.callMatchingInvocationPlugin(exchange, "SOAP/HTTP", Settings.OPENTOSCA_CONTAINER_HOSTNAME);
    }

    @Override
    public void notifyPartners(final Exchange exchange) {

        final Message message = exchange.getIn();
        final String correlationID = message.getHeader(MBHeader.PLANCORRELATIONID_STRING.toString(), String.class);
        final CsarId csarID = message.getHeader(MBHeader.CSARID.toString(), CsarId.class);
        final QName serviceTemplateID = message.getHeader(MBHeader.SERVICETEMPLATEID_QNAME.toString(), QName.class);

        LOG.debug("Notifying partners to start their plans for choreography with correlation ID {}, CsarID {}, and ServiceTemplateID {}",
            correlationID, csarID, serviceTemplateID);

        // retrieve ServiceTemplate related to the notification request
        final TServiceTemplate serviceTemplate = this.storage.findById(csarID).entryServiceTemplate();
        if (Objects.isNull(serviceTemplate)) {
            LOG.error("Unable to retrieve ServiceTemplate for the notification request.");
            return;
        }

        // get the tags enpoints of the partners
        final List<TTag> partnerTags = Util.getPartnerEndpoints(serviceTemplate);
        if (Objects.isNull(partnerTags)) {
            LOG.error("Unable to retrieve partners for ServiceTemplate with ID {}.", serviceTemplate.getId());
            return;
        }

        @SuppressWarnings("unchecked") final HashMap<String, String> params = (HashMap<String, String>) exchange.getIn().getBody();

        // notify all partners
        LOG.error("Number of partners to notify: {}", partnerTags.size());
        for (final TTag endpointTag : partnerTags) {
            LOG.debug("Notifying partner {} on endpoint: {}", endpointTag.getName(), endpointTag.getValue());

            message.setHeader(MBHeader.HASOUTPUTPARAMS_BOOLEAN.toString(), false);
            message.setHeader(MBHeader.ENDPOINT_URI.toString(), endpointTag.getValue());
            message.setHeader(MBHeader.OPERATIONNAME_STRING.toString(), Constants.RECEIVE_NOTIFY_PARTNERS_OPERATION);

            // create message body
            final HashMap<String, String> input = new HashMap<>();
            input.put(Constants.PLAN_CORRELATION_PARAM, correlationID);
            input.put(Constants.CSARID_PARAM, csarID.toString());
            input.put(Constants.SERVICE_TEMPLATE_NAMESPACE_PARAM, serviceTemplateID.getNamespaceURI());
            input.put(Constants.SERVICE_TEMPLATE_LOCAL_PARAM, serviceTemplateID.getLocalPart());
            input.put(Constants.MESSAGE_ID_PARAM, String.valueOf(System.currentTimeMillis()));

            // parse to doc and add input parameters
            final Document inputDoc =
                MBUtils.mapToDoc(Constants.BUS_WSDL_NAMESPACE, Constants.RECEIVE_NOTIFY_PARTNERS_OPERATION, input);

            final Element root = inputDoc.getDocumentElement();
            final Element paramsWrapper = inputDoc.createElement(Constants.PARAMS_PARAM);
            root.appendChild(paramsWrapper);
            for (final Entry<String, String> entry : params.entrySet()) {
                final Element paramElement = inputDoc.createElement("Param");
                paramsWrapper.appendChild(paramElement);

                final Element keyElement = inputDoc.createElement("key");
                keyElement.setTextContent(entry.getKey());
                paramElement.appendChild(keyElement);

                final Element valueElement = inputDoc.createElement("value");
                valueElement.setTextContent(entry.getValue());
                paramElement.appendChild(valueElement);
            }
            message.setBody(inputDoc);

            this.pluginHandler.callMatchingInvocationPlugin(exchange, "SOAP/HTTP", Settings.OPENTOSCA_CONTAINER_HOSTNAME);
        }
    }

    private void internalInvokePlan(PlanInvocationArguments arguments, Exchange exchange) {
        LOG.debug("Running Management Bus: InvokePlan");
        // log event to monitor the plan execution time
        final PlanInstanceEvent event = new PlanInstanceEvent("INFO", "PLAN_DURATION_LOG", "Plan execution with correlation id " + arguments.correlationId + ".");

        // create the instance data for the plan instance to be started

        Message message = exchange.getIn();

        final Boolean callbackInvocation = message.getHeader(MBHeader.CALLBACK_BOOLEAN.toString(), Boolean.class);
        LOG.debug("CallbackInvocation: {}", callbackInvocation);

        PlanInstance plan = null;
        try {
            plan = PlanInstanceHandler.createPlanInstance(arguments.csar, arguments.serviceTemplateId,
                arguments.serviceTemplateInstanceId, arguments.planId, arguments.operationName, arguments.correlationId, exchange.getIn().getBody());
        } catch (CorrelationIdAlreadySetException e) {
            LOG.warn(e.getMessage() + " Skipping the plan invocation!");
            return;
        }

        if (plan == null) {
            LOG.warn("Unable to get plan for CorrelationID {}. Invocation aborted!", arguments.correlationId);
            handleResponse(exchange);
            return;
        }
        LOG.debug("Plan ID: {}", plan.getTemplateId());
        LOG.debug("Plan language: {}", plan.getLanguage().toString());

        LOG.debug("Getting endpoint for the plan...");
        endpointService.printPlanEndpoints();
        final List<WSDLEndpoint> WSDLendpoints =
            endpointService.getWSDLEndpointsForPlanId(Settings.OPENTOSCA_CONTAINER_HOSTNAME, arguments.csar.id(),
                plan.getTemplateId());

        // choose WSDL endpoint depending on the invokation of the invoker or callback port type
        WSDLEndpoint WSDLendpoint = null;
        if (Objects.isNull(callbackInvocation) || !callbackInvocation) {
            WSDLendpoint =
                WSDLendpoints.stream()
                    .filter(endpoint -> !endpoint.getPortType().equals(Constants.CALLBACK_PORT_TYPE))
                    .findFirst().orElse(null);
        } else {
            LOG.debug("Invokation using callback.");
            WSDLendpoint =
                WSDLendpoints.stream()
                    .filter(endpoint -> endpoint.getPortType().equals(Constants.CALLBACK_PORT_TYPE))
                    .findFirst().orElse(null);
        }

        if (WSDLendpoint != null) {

            final URI endpoint = WSDLendpoint.getURI();
            LOG.debug("Endpoint for Plan {} : {} ", plan.getTemplateId(), endpoint);

            // Assumption. Should be checked with ToscaEngine
            message.setHeader(MBHeader.HASOUTPUTPARAMS_BOOLEAN.toString(), true);
            message.setHeader(MBHeader.ENDPOINT_URI.toString(), endpoint);

            if (plan.getLanguage().equals(PlanLanguage.BPMN)) {
                exchange = pluginHandler.callMatchingInvocationPlugin(exchange, "REST",
                    Settings.OPENTOSCA_CONTAINER_HOSTNAME);
            } else {
                exchange = pluginHandler.callMatchingInvocationPlugin(exchange, "SOAP/HTTP",
                    Settings.OPENTOSCA_CONTAINER_HOSTNAME);
            }

            // Undeploy IAs for the related ServiceTemplateInstance if a termination plan
            // was executed.
            if (plan.getType().equals(PlanType.TERMINATION)) {
                LOG.debug("Executed plan was a termination plan. Removing endpoints...");

                final ServiceTemplateInstance serviceInstance = plan.getServiceTemplateInstance();

                if (serviceInstance != null) {
                    deleteEndpointsForServiceInstance(arguments.csar.id(), serviceInstance);
                } else {
                    LOG.warn("Unable to retrieve ServiceTemplateInstance related to the plan.");
                }
            }
        } else {
            LOG.warn("No endpoint found for specified plan: {} of csar: {}. Invocation aborted!",
                plan.getTemplateId(), arguments.csar.id());
        }

        // write WCET back to Plan
        TPlan currentPlan = null;
        try {
            currentPlan = ToscaEngine.resolvePlanReference(arguments.csar, arguments.planId);
        } catch (NotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // add end timestamp and log message with duration
        event.setEndTimestamp(new Date());
        final long duration = event.getEndTimestamp().getTime() - event.getStartTimestamp().getTime();
        event.setMessage("Finished plan execution with correlation id " + arguments.correlationId + " after "
            + duration + "ms");
        LOG.info("Plan execution duration: {}ms", duration);

        final SituationTriggerInstanceListener instanceListener = new SituationTriggerInstanceListener();
        final long calculatedWCET = instanceListener.calculateWCETForPlan(currentPlan);
        // if total duration larger than calculatedWCET, use duration
        if (calculatedWCET > 0 && calculatedWCET < duration) {
            currentPlan.getOtherAttributes().put(new QName("http://opentosca.org", "WCET"), String.valueOf(duration));
        }
        // if newly calculated WCET is larger than previous WCET, update

        long currentPlanWCET = Long.valueOf(currentPlan.getOtherAttributes().getOrDefault(new QName("http://opentosca.org", "WCET"), String.valueOf(0)));

        if (calculatedWCET > currentPlanWCET) {
            currentPlan.getOtherAttributes().put(new QName("http://opentosca.org", "WCET"), String.valueOf(calculatedWCET));
        }

        // update plan in repository with new log event
        final PlanInstanceRepository repo = new PlanInstanceRepository();
        plan = repo.findByCorrelationId(arguments.correlationId);
        plan.addEvent(event);
        repo.update(plan);

        // Undeploy IAs for the related ServiceTemplateInstance if a termination plan
        // was executed.
        if (plan.getType().equals(PlanType.TERMINATION)) {
            LOG.debug("Executed plan was a termination plan. Removing endpoints...");
            final ServiceTemplateInstance serviceInstance = plan.getServiceTemplateInstance();
            if (serviceInstance != null) {
                deleteEndpointsForServiceInstance(arguments.csar.id(), serviceInstance);
            } else {
                LOG.warn("Unable to retrieve ServiceTemplateInstance related to the plan.");
            }
        }
        // add end timestamp and log message with duration
        event.setEndTimestamp(new Date());
        LOG.info("Plan execution duration: {}ms", event.getDuration());

        // update plan in repository with new log event

        plan = repo.findByCorrelationId(arguments.correlationId);
        plan.addEvent(event);
        repo.update(plan);

        // update the output parameters in the plan instance
        PlanInstanceHandler.updatePlanInstanceOutput(plan, arguments.csar, exchange.getIn().getBody());

        handleResponse(exchange);
    }

    private boolean iaProvidesRequestedOperation(Csar csar, TImplementationArtifact ia, TEntityType type, String neededInterface, String neededOperation) {
        final String providedOperation = ia.getOperationName();
        final String providedInterface = ia.getInterfaceName();

        LOG.debug("Needed interface: {}. Provided interface: {}", neededInterface, providedInterface);
        LOG.debug("Needed operation: {}. Provided operation: {}", neededOperation, providedOperation);

        if (providedInterface == null && providedOperation == null) {
            // IA implements all operations of all interfaces defined in the node type
            LOG.debug("Correct IA found. IA: {} implements all operations of all interfaces defined in NodeType.", ia.getName());
            return true;
        }

        // IA implements all operations of one interface defined in NodeType
        if (providedInterface != null && providedOperation == null && providedInterface.equals(neededInterface)) {
            LOG.debug("Correct IA found. IA: {} implements all operations of one interface defined in NodeType.", ia.getName());
            return true;
        }

        // IA implements one operation of an interface defined in NodeType
        if (providedInterface != null && providedOperation != null
            && providedInterface.equals(neededInterface) && providedOperation.equals(neededOperation)) {
            LOG.debug("Correct IA found. IA: {} implements one operation of an interface defined in NodeType.", ia.getName());
            return true;
        }

        // In this case - if there is no interface specified - the operation
        // should be unique within the NodeType
        if (neededInterface == null && neededOperation != null
            && providedInterface != null && providedOperation == null) {
            return ToscaEngine.isOperationUniqueInType(csar, type, providedInterface, neededOperation);
        }

        LOG.debug("ImplementationArtifact {} does not provide needed interface/operation", ia.getName());
        return false;
    }

    /**
     * Delete all endpoints for the given ServiceTemplateInstance from the <tt>EndpointService</tt>. In case an endpoint
     * is the only one for a certain implementation artifact, it is undeployed too.
     *
     * @param csarID          The CSAR to which the ServiceTemplateInstance belongs.
     * @param serviceInstance The ServiceTemplateInstance for which the endpoints have to be removed.
     */
    private void deleteEndpointsForServiceInstance(final CsarId csarID, final ServiceTemplateInstance serviceInstance) {
        final Long instanceID = serviceInstance.getId();
        LOG.debug("Deleting endpoints for ServiceTemplateInstance with ID: {}", instanceID);

        final Csar csar = storage.findById(csarID);

        final List<WSDLEndpoint> serviceEndpoints =
            endpointService.getWSDLEndpointsForSTID(Settings.OPENTOSCA_CONTAINER_HOSTNAME, instanceID);
        LOG.debug("Found {} endpoints to delete...", serviceEndpoints.size());

        for (final WSDLEndpoint serviceEndpoint : serviceEndpoints) {

            final String triggeringContainer = serviceEndpoint.getTriggeringContainer();
            final String deploymentLocation = serviceEndpoint.getManagingContainer();
            final QName typeImpl = serviceEndpoint.getTypeImplementation();
            final String iaName = serviceEndpoint.getIaName();

            LOG.debug("Deleting endpoint: Triggering Container: {}; "
                    + "Managing Container: {}; NodeTypeImplementation: {}; IA name: {}", triggeringContainer,
                deploymentLocation, typeImpl, iaName);

            final String identifier =
                getUniqueSynchronizationString(triggeringContainer, deploymentLocation, typeImpl, iaName, instanceID.toString());

            // synchronize deletion to avoid concurrency issues
            synchronized (getLockForString(identifier)) {

                // get number of endpoints for the same IA
                final int count = endpointService
                    .getWSDLEndpointsForNTImplAndIAName(triggeringContainer,
                        deploymentLocation,
                        typeImpl, iaName)
                    .size();

                // only undeploy the IA if this is the only endpoint
                if (count == 1) {
                    LOG.debug("Undeploying corresponding IA...");
                    final TImplementationArtifact ia;
                    try {
                        TEntityTypeImplementation typeImplementation = ToscaEngine.resolveTypeImplementation(csar, typeImpl);
                        ia = ToscaEngine.resolveImplementationArtifact(typeImplementation, iaName);
                    } catch (NotFoundException e) {
                        LOG.warn("Could not find ImplementationArtifact {} for existing WSDLEndpoint  [{}] in Csar [{}]", iaName, serviceEndpoint, csar.id());
                        continue;
                    }
                    final String artifactType = ia.getArtifactType().toString();

                    // create exchange for the undeployment plug-in invocation
                    Exchange exchange = new DefaultExchange(collaborationContext.getCamelContext());
                    exchange.getIn().setHeader(MBHeader.ENDPOINT_URI.toString(), serviceEndpoint.getURI());

                    // get plug-in for the undeployment
                    IManagementBusDeploymentPluginService deploymentPlugin;
                    if (deploymentLocation.equals(Settings.OPENTOSCA_CONTAINER_HOSTNAME)) {
                        LOG.debug("Undeployment is done locally.");
                        deploymentPlugin = pluginRegistry.getDeploymentPluginServices().get(artifactType);
                    } else {
                        LOG.debug("Undeployment is done on a remote Container.");
                        deploymentPlugin = pluginRegistry.getDeploymentPluginServices().get(Constants.REMOTE_TYPE);

                        // add header fields that are needed for the undeployment on a
                        // remote OpenTOSCA Container
                        exchange.getIn().setHeader(MBHeader.DEPLOYMENTLOCATION_STRING.toString(), deploymentLocation);
                        exchange.getIn().setHeader(MBHeader.TRIGGERINGCONTAINER_STRING.toString(), triggeringContainer);
                        exchange.getIn().setHeader(MBHeader.TYPEIMPLEMENTATIONID_QNAME.toString(), typeImpl.toString());
                        exchange.getIn().setHeader(MBHeader.IMPLEMENTATION_ARTIFACT_NAME_STRING.toString(), iaName);
                        exchange.getIn().setHeader(MBHeader.ARTIFACTTYPEID_STRING.toString(), artifactType);
                    }

                    exchange = deploymentPlugin.invokeImplementationArtifactUndeployment(exchange);

                    // print the undeployment result state
                    if (exchange.getIn().getHeader(MBHeader.OPERATIONSTATE_BOOLEAN.toString(), boolean.class)) {
                        LOG.debug("Undeployed IA successfully!");
                    } else {
                        LOG.warn("Undeployment of IA failed!");
                    }
                } else {
                    LOG.debug("Found further endpoints for the IA. No undeployment!");
                }

                // delete the endpoint
                endpointService.removeWSDLEndpoint(serviceEndpoint);
                LOG.debug("Endpoint deleted.");
            }
        }

        LOG.debug("Endpoint deletion terminated.");
    }

    /**
     * Creates a unique String which identifies an IA on a certain OpenTOSCA Container node. The String can be used to
     * synchronize the access to the management infrastructure (e.g. tomcat).
     *
     * @param triggeringContainer OpenTOSCA Container that triggered the deployment
     * @param deploymentLocation  OpenTOSCA Container where the IA is managed
     * @param typeImpl            QName of the NodeType/RelationshipType the IA belongs to
     * @param iaName              the name of the IA
     * @return a unique String consisting of the given information or <tt>null</tt> if some needed information is
     * missing
     */
    public static String getUniqueSynchronizationString(final String triggeringContainer,
                                                        final String deploymentLocation, final QName typeImpl,
                                                        final String iaName, final String serviceInstanceId) {

        if (Objects.isNull(triggeringContainer) || Objects.isNull(deploymentLocation) || Objects.isNull(typeImpl)
            || Objects.isNull(iaName) || Objects.isNull(serviceInstanceId)) {
            return null;
        }

        return String.join("/", triggeringContainer, deploymentLocation, typeImpl.toString(), iaName, serviceInstanceId);
    }

    /**
     * Returns an Object which can be used to synchronize all actions related to a certain String value.
     *
     * @return the object which can be used for synchronization
     */
    public static Object getLockForString(final String lockString) {
        Objects.requireNonNull(lockString);

        synchronized (locks) {
            return locks.computeIfAbsent(lockString, (i) -> new Object());
        }
    }

    /**
     * Add the specific content of the ImplementationArtifact to the Exchange headers if defined.
     */
    private Exchange addSpecificContent(final Exchange exchange,
                                        final TImplementationArtifact implementationArtifact) {
        Object any = implementationArtifact.getAny();
        Document specificContent = any instanceof Element ? XMLHelper.fromRootNode((Element) any) : null;
        if (specificContent != null) {
            LOG.debug("ArtifactSpecificContent specified!");
            exchange.getIn().setHeader(MBHeader.SPECIFICCONTENT_DOCUMENT.toString(), specificContent);
        }
        return exchange;
    }

    /**
     * Replaces placeholder with a matching instance data value. Placeholder is defined like
     * "/PLACEHOLDER_VMIP_IP_PLACEHOLDER/"
     *
     * @param endpoint             the endpoint URI containing the placeholder
     * @param nodeTemplateInstance the NodeTemplateInstance where the endpoint belongs to
     * @return the endpoint URI with replaced placeholder if matching instance data was found, the unchanged endpoint
     * URI otherwise
     */
    private URI replacePlaceholderWithInstanceData(URI endpoint, final NodeTemplateInstance nodeTemplateInstance) {

        if (nodeTemplateInstance == null) {
            LOG.warn("NodeTemplateInstance is null. Unable to replace placeholders!");
            return endpoint;
        }
        final String placeholder =
            endpoint.toString()
                .substring(endpoint.toString().lastIndexOf(Constants.PLACEHOLDER_START),
                    endpoint.toString().lastIndexOf(Constants.PLACEHOLDER_END) + Constants.PLACEHOLDER_END.length());

        LOG.debug("Placeholder: {} detected in Endpoint: {}", placeholder, endpoint.toString());
        final String[] placeholderProperties =
            placeholder.replace(Constants.PLACEHOLDER_START, "").replace(Constants.PLACEHOLDER_END, "").split("_");

        for (final String placeholderProperty : placeholderProperties) {
            LOG.debug("Searching instance data value for property {} ...", placeholderProperty);
            String propertyValue = MBUtils.searchProperty(nodeTemplateInstance, placeholderProperty);
            if (propertyValue == null) {
                LOG.warn("Value for property {} not found.", placeholderProperty);
                continue;
            }
            LOG.debug("Value for property {} found: {}.", placeholderProperty, propertyValue);
            try {
                endpoint = new URI(endpoint.toString().replace(placeholder, propertyValue));
            } catch (final URISyntaxException e) {
                e.printStackTrace();
            }
            break;
        }

        return endpoint;
    }

    /**
     * Handles the response from the plug-in. If needed the response is sent back to the API.
     *
     * @param exchange to handle.
     */
    private void handleResponse(Exchange exchange) {
        if (exchange == null) {
            return;
        }
        // Response message back to caller.
        final ProducerTemplate template = collaborationContext.getProducer();
        final String caller = exchange.getIn().getHeader(MBHeader.APIID_STRING.toString(), String.class);

        if (caller == null) {
            // notably the Java API does not set the APIID, because it never uses the information returned.
            LOG.debug("Invocation was InOnly. No response message will be sent to the caller.");
            return;
        }

        LOG.debug("Sending response message back to api: {}", caller);
        exchange = template.send("direct-vm:" + caller, exchange);
        if (exchange.isFailed()) {
            LOG.error("Sending exchange message failed! {}", exchange.getException().getMessage());
        }
    }

    @Override
    public synchronized void addPartnerToReadyList(final String correlationID, final String partnerID) {
        activePartners.putIfAbsent(correlationID, new LinkedList<String>());
        activePartners.get(correlationID).add(partnerID);
    }

    @Override
    public synchronized boolean isPartnerAvailable(final String correlationID, final String partnerID) {
        if (Objects.nonNull(activePartners.get(correlationID))) {
            return activePartners.get(correlationID).contains(partnerID);
        }
        return false;
    }

    private static class PlanInvocationArguments {
        public final Csar csar;
        public final QName serviceTemplateId;
        public final Long serviceTemplateInstanceId;
        public final QName planId;
        public final String correlationId;
        public final String operationName;

        public PlanInvocationArguments(Csar csar, QName serviceTemplateID, Long serviceTemplateInstanceID, QName planID, String operationName, String correlationID) {
            this.csar = csar;
            this.serviceTemplateId = serviceTemplateID;
            this.serviceTemplateInstanceId = serviceTemplateInstanceID;
            this.planId = planID;
            this.operationName = operationName;
            this.correlationId = correlationID;
        }
    }

    private static class IAInvocationArguments {
        public final CsarId csarId;
        public final URI serviceInstanceId;
        public final QName serviceTemplateId;
        public final long serviceTemplateInstanceId;
        public final String nodeTemplateId;
        public final String relationshipTemplateId;
        public final String interfaceName;
        public final String operationName;

        public IAInvocationArguments(CsarId csarId, URI serviceInstanceId, QName serviceTemplateId, long serviceTemplateInstanceId, String nodeTemplateId, String relationshipTemplateId, String interfaceName, String operationName) {
            this.csarId = csarId;
            this.serviceInstanceId = serviceInstanceId;
            this.serviceTemplateId = serviceTemplateId;
            this.serviceTemplateInstanceId = serviceTemplateInstanceId;
            this.nodeTemplateId = nodeTemplateId;
            this.relationshipTemplateId = relationshipTemplateId;
            this.interfaceName = interfaceName;
            this.operationName = operationName;
        }
    }
}
