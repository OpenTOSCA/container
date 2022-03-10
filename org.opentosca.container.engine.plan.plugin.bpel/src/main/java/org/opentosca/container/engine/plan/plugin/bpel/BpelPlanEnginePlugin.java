package org.opentosca.container.engine.plan.plugin.bpel;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.ids.XmlId;
import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.ids.elements.PlanId;
import org.eclipse.winery.model.ids.elements.PlansId;
import org.eclipse.winery.model.tosca.TPlan.PlanModelReference;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.common.RepositoryFileReference;

import com.google.common.collect.Lists;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.opentosca.container.connector.ode.OdeConnector;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.impl.service.FileSystem;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.next.model.Endpoint;
import org.opentosca.container.core.service.CsarStorageService;
import org.opentosca.container.core.service.ICoreEndpointService;
import org.opentosca.container.engine.plan.plugin.IPlanEnginePlanRefPluginService;
import org.opentosca.container.engine.plan.plugin.bpel.util.ODEEndpointUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * <p>
 * This class implements functionality for deployment of WS-BPEL 2.0 Processes through the {@link
 * IPlanEnginePlanRefPluginService} unto a WSO2 Business Process Server or Apache Orchestration Director Engine (ODE).
 * </p>
 * <p>
 * The class is the highlevel control of the plugin. The plugin also uses {@link ODEEndpointUpdater} to update the
 * bindings inside the used WSDL Descriptions referenced in the BPEL process.
 * <p>
 * The endpoints for the update are retrieved through a service that implements the {@link ICoreEndpointService}
 * interface.
 * </p>
 * <p>
 * The actual deployment is done on the endpoint given in the properties. The plugin uses the {@link OdeConnector} class
 * to deploy the updated plan unto the Apache ODE behind the endpoint.
 * </p>
 *
 * @see ODEEndpointUpdater
 * @see OdeConnector
 * @see ICoreEndpointService
 */
@NonNullByDefault
@Service
public class BpelPlanEnginePlugin implements IPlanEnginePlanRefPluginService {

    public static final String BPS_ENGINE = "BPS";
    private static final String[] CAPABILITIES = {"http://docs.oasis-open.org/wsbpel/2.0/process/executable"};
    private static final Logger LOG = LoggerFactory.getLogger(BpelPlanEnginePlugin.class);
    private static final String DEFAULT_ENGINE_LANGUAGE = "http://docs.oasis-open.org/wsbpel/2.0/process/executable";

    private final ICoreEndpointService endpointService;
    private final CsarStorageService storage;

    @Inject
    public BpelPlanEnginePlugin(ICoreEndpointService endpointService, CsarStorageService storage) {
        this.endpointService = endpointService;
        this.storage = storage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLanguageUsed() {
        return DEFAULT_ENGINE_LANGUAGE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getCapabilties() {
        return Lists.newArrayList(CAPABILITIES);
    }

    public boolean deployPlanFile(final Path planLocation, final CsarId csarId, final QName planId, Map<String, String> endpointMetadata) {
        final List<File> planContents;
        Path tempDir = null;
        try {
            // creating temporary dir for update
            tempDir = FileSystem.getTemporaryFolder();
            LOG.debug("Unzipping Plan '{}' to '{}'.", planLocation.getFileName().toString(), tempDir.toAbsolutePath());
            planContents = FileSystem.unzip(planLocation, tempDir).parallelStream()
                .map(Path::toFile)
                .collect(Collectors.toList());
        } catch (IOException e) {
            LOG.warn("Could not unzip plan from {} to {} due to an exception", planLocation, tempDir, e);
            return false;
        }

        Path tempPlan = tempDir.resolve(planLocation.getFileName());
        // changing endpoints in WSDLs
        ODEEndpointUpdater odeUpdater;
        // variable for the (inbound) portType of the process, if this is null
        // till end the process can't be instantiated by the container
        QName portType = null;
        try {
            odeUpdater = new ODEEndpointUpdater(Settings.ENGINE_PLAN_BPEL_URL_SERVICES, Settings.ENGINE_PLAN_BPEL_ENGINE, endpointService);
            portType = odeUpdater.getPortType(planContents);
            if (!odeUpdater.changeEndpoints(planContents, csarId)) {
                LOG.error("Not all endpoints used by the plan {} have been changed",
                    planLocation);
            }
        } catch (final WSDLException e) {
            LOG.error("Couldn't load ODEEndpointUpdater", e);
        }

        // package process
        LOG.debug("Prepare deployment of PlanModelReference");

        try {
            Files.createFile(tempPlan);
            // package the updated files
            LOG.debug("Packaging plan to {} ", tempPlan.toAbsolutePath());
            FileSystem.zip(tempPlan, tempDir);
        } catch (final IOException e) {
            LOG.error("Can't package temporary plan for deployment", e);
            return false;
        }

        // deploy process
        LOG.info("Deploying Plan: {}", tempPlan.getFileName().toString());
        QName processId = null;
        Map<String, URI> endpoints = Collections.emptyMap();
        try {
            if (Settings.ENGINE_PLAN_BPEL_ENGINE.equalsIgnoreCase(BPS_ENGINE)) {
                LOG.error("BPS ENGINE IS NO LONGER SUPPORTED!!");
            } else {
                final OdeConnector connector = new OdeConnector();
                processId = connector.deploy(tempPlan.toFile(), Settings.ENGINE_PLAN_BPEL_URL);
                endpoints = connector.getEndpointsForPID(processId, Settings.ENGINE_PLAN_BPEL_URL);
            }
        } catch (final Exception e) {
            LOG.error("Deployment of the plan failed", e);
            return false;
        }

        // this will be the endpoint the container can use to instantiate the BPEL Process
        URI endpointUri = null;
        URI callbackEndpoint = null;
        if (endpoints.keySet().size() == 1) {
            endpointUri = (URI) endpoints.values().toArray()[0];
        } else {
            for (final String partnerLink : endpoints.keySet()) {
                if (partnerLink.equals("client")) {
                    endpointUri = endpoints.get(partnerLink);
                }

                // retrieve callback endpoint for the choreography execution
                if (endpoints.get(partnerLink).toString().contains("CallbackService")) {
                    callbackEndpoint = endpoints.get(partnerLink);
                }
            }
        }

        if (processId != null && endpointUri != null && portType != null && this.endpointService != null) {
            BpelPlanEnginePlugin.LOG.debug("Endpoint for ProcessID \"" + processId + "\" is \"" + endpoints + "\".");
            BpelPlanEnginePlugin.LOG.debug("Deployment of Plan was successfull: {}", planId);

            // save endpoint
            final String localContainer = Settings.OPENTOSCA_CONTAINER_HOSTNAME;
            final Endpoint endpoint = new Endpoint(endpointUri, localContainer, localContainer,
                csarId, null, endpointMetadata, portType, null, null, planId);
            this.endpointService.storeEndpoint(endpoint);

            if (Objects.nonNull(callbackEndpoint)) {
                final QName callbackPortType = QName.valueOf("{http://schemas.xmlsoap.org/wsdl/}CallbackPortType");
                LOG.debug("Storing callback endpoint: {}", callbackEndpoint);
                this.endpointService.storeEndpoint(new Endpoint(callbackEndpoint,
                    localContainer, localContainer, csarId, null, endpointMetadata, callbackPortType, null, null, planId));
            }
        } else {
            BpelPlanEnginePlugin.LOG.error("Error while processing plan");
            if (processId == null) {
                BpelPlanEnginePlugin.LOG.error("ProcessId is null");
            }
            if (endpointUri == null) {
                BpelPlanEnginePlugin.LOG.error("Endpoint for process is null");
            }
            if (portType == null) {
                BpelPlanEnginePlugin.LOG.error("PortType of process is null");
            }

            if (this.endpointService == null) {
                BpelPlanEnginePlugin.LOG.error("Endpoint Service is null");
            }
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deployPlanReference(final QName planId, final PlanModelReference planRef, final CsarId csarId) {

        if (storage == null) {
            LOG.error("Can't fetch relevant Csar from storage: StorageService not available");
            return false;
        }

        Path planLocation = planLocationOnDisk(csarId, planId, planRef);
        if (planLocation == null) {
            // diagnostics already in planLocationOnDisk
            return false;
        }

        return this.deployPlanFile(planLocation, csarId, planId, new HashMap<String, String>());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean undeployPlanReference(final QName planId, final PlanModelReference planRef, final CsarId csarId) {
        // retrieve process
        Path planLocation = planLocationOnDisk(csarId, planId, planRef);
        if (planLocation == null) {
            // diagnostics already in planLocationOnDisk
            return false;
        }

        LOG.info("Removing Plan: {}", planLocation.getFileName().toString());
        boolean wasUndeployed = false;
        if (Settings.ENGINE_PLAN_BPEL_ENGINE.equalsIgnoreCase(BPS_ENGINE)) {
            LOG.error("BPS Engine is no longer supported");
        } else {
            final OdeConnector connector = new OdeConnector();
            wasUndeployed = connector.undeploy(planLocation.toFile(), Settings.ENGINE_PLAN_BPEL_URL);
        }

        // remove endpoint from core
        if (this.endpointService != null) {
            LOG.debug("Starting to remove endpoint!");
            List<Endpoint> endpoints = this.endpointService.getEndpointsForPlanId(Settings.OPENTOSCA_CONTAINER_HOSTNAME, csarId, planId);
            if (endpoints.isEmpty()) {
                LOG.warn("Couldn't remove endpoint for plan {}, because endpoint service didn't find any endpoint associated with the plan to remove",
                    planRef.getReference());
            } else {

                for (Endpoint endpoint : endpoints) {
                    this.endpointService.removeEndpoint(endpoint);
                    LOG.debug("Removed endpoint {} for plan {}", endpoint.toString(),
                        planRef.getReference());
                }
            }
        } else {
            LOG.warn("Couldn't remove endpoint for plan {}, cause endpoint service is not available",
                planRef.getReference());
        }

        if (wasUndeployed) {
            LOG.debug("Undeployment of Plan " + planRef.getReference() + " was successful");
        } else {
            LOG.warn("Undeployment of Plan " + planRef.getReference() + " was unsuccessful");
        }
        return wasUndeployed;
    }

    @Nullable
    private Path planLocationOnDisk(CsarId csarId, QName planId, PlanModelReference planRef) {

        Csar csar = storage.findById(csarId);

        IRepository repository = RepositoryFactory.getRepository(csar.getSaveLocation());

        PlanId plan = new PlanId(new PlansId(new ServiceTemplateId(csar.entryServiceTemplate().getTargetNamespace(), csar.entryServiceTemplate().getId(), false)), new XmlId(planId.toString(), false));

        Collection<RepositoryFileReference> fileRefs = repository.getContainedFiles(plan);

        Path planPath = null;

        for (RepositoryFileReference ref : fileRefs) {
            if (ref.getFileName().endsWith(".zip")) {
                planPath = repository.ref2AbsolutePath(ref);
            }
        }

        return planPath;
    }

    @Override
    public String toString() {
        return "openTOSCA PlanEngine WS-BPEL 2.0 Plugin v1.0";
    }
}
