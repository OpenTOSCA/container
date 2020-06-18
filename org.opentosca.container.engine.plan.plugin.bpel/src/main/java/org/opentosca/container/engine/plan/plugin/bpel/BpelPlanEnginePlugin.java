package org.opentosca.container.engine.plan.plugin.bpel;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;

import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.model.tosca.TPlan.PlanModelReference;
import org.eclipse.winery.model.tosca.TServiceTemplate;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.opentosca.container.connector.ode.OdeConnector;
import org.opentosca.container.core.common.NotFoundException;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.engine.ToscaEngine;
import org.opentosca.container.core.impl.service.FileSystem;
import org.opentosca.container.core.model.AbstractArtifact;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.model.csar.backwards.ArtifactResolver;
import org.opentosca.container.core.model.endpoint.wsdl.WSDLEndpoint;
import org.opentosca.container.core.service.CsarStorageService;
import org.opentosca.container.core.service.ICoreEndpointService;
import org.opentosca.container.engine.plan.plugin.IPlanEnginePlanRefPluginService;
import org.opentosca.container.engine.plan.plugin.bpel.util.BPELRESTLightUpdater;
import org.opentosca.container.engine.plan.plugin.bpel.util.ODEEndpointUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

/**
 * <p>
 * This class implements functionality for deployment of WS-BPEL 2.0 Processes through the {@link
 * IPlanEnginePlanRefPluginService} unto a WSO2 Business Process Server or Apache Orchestration Director Engine (ODE).
 * </p>
 * <p>
 * The class is the highlevel control of the plugin. It uses the classes {@link BPELRESTLightUpdater} to update
 * BPEL4RESTLight (see: OpenTOSCA/trunk/examples/org.opentosca.bpel4restlight.bpelextension) extension activities with
 * up-to-date endpoints. The plugin also uses {@link ODEEndpointUpdater} to update the bindings inside the used WSDL
 * Descriptions referenced in the BPEL process.
 * <p>
 * The endpoints for the update are retrieved through a service that implements the {@link ICoreEndpointService}
 * interface.
 * </p>
 * <p>
 * The actual deployment is done on the endpoint given in the properties. The plugin uses the {@link OdeConnector} class
 * to deploy the updated plan unto the Apache ODE behind the endpoint.
 * </p>
 *
 * @see BPELRESTLightUpdater
 * @see ODEEndpointUpdater
 * @see OdeConnector
 * @see ICoreEndpointService
 */
@NonNullByDefault
@Service
public class BpelPlanEnginePlugin implements IPlanEnginePlanRefPluginService {

    public static final String BPS_ENGINE = "BPS";

    private static final Logger LOG = LoggerFactory.getLogger(BpelPlanEnginePlugin.class);
    private static final String DEFAULT_ENGINE_URL = "http://localhost:9763/ode";
    private static final String DEFAULT_ENGINE = "ODE";
    private static final String DEFAULT_SERVICE_URL = "http://localhost:9763/ode/processes";
    private static final String DEFAULT_ENGINE_LANGUAGE = "http://docs.oasis-open.org/wsbpel/2.0/process/executable";

    private final String processEngine;
    private final String username;
    private final String password;
    private final String url;
    private final String servicesUrl;

    private final ICoreEndpointService endpointService;
    private final CsarStorageService storage;

    @Inject
    public BpelPlanEnginePlugin(ICoreEndpointService endpointService, CsarStorageService storage) {
        this.endpointService = endpointService;
        this.storage = storage;

        this.processEngine = Settings.getSetting("org.opentosca.container.engine.plan.plugin.bpel.engine", DEFAULT_ENGINE);
        this.url = Settings.getSetting("org.opentosca.container.engine.plan.plugin.bpel.url", DEFAULT_ENGINE_URL);
        this.servicesUrl = Settings.getSetting("org.opentosca.container.engine.plan.plugin.bpel.services.url", DEFAULT_SERVICE_URL);
        this.username = Settings.getSetting("org.opentosca.container.engine.plan.plugin.bpel.username", "");
        this.password = Settings.getSetting("org.opentosca.container.engine.plan.plugin.bpel.password", "");
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
        final List<String> capabilities = new ArrayList<>();
        for (final String capability : "http://docs.oasis-open.org/wsbpel/2.0/process/executable".split("[,;]")) {
            capabilities.add(capability.trim());
        }
        return capabilities;
    }

    public boolean deployPlanFile(final Path planLocation, final CsarId csarId, final QName planId, Map<String, String> endpointMetadata) {
        final List<File> planContents;
        Path tempDir = null;
        try {
            // creating temporary dir for update
            tempDir = FileSystem.getTemporaryFolder();
            LOG.debug("Unzipping Plan '{}' to '{}'.", planLocation.getFileName().toString(), tempDir.toAbsolutePath().toString());
            planContents = FileSystem.unzip(planLocation, tempDir).parallelStream()
                .map(Path::toFile)
                .collect(Collectors.toList());
        } catch (IOException e) {
            LOG.warn("Could not unzip plan from {} to {} due to an exception", planLocation.toString(), tempDir, e);
            return false;
        }

        Path tempPlan = tempDir.resolve(planLocation.getFileName());
        // changing endpoints in WSDLs
        ODEEndpointUpdater odeUpdater;
        // variable for the (inbound) portType of the process, if this is null
        // till end the process can't be instantiated by the container
        QName portType = null;
        try {
            odeUpdater = new ODEEndpointUpdater(servicesUrl, processEngine, endpointService);
            portType = odeUpdater.getPortType(planContents);
            if (!odeUpdater.changeEndpoints(planContents, csarId)) {
                LOG.error("Not all endpoints used by the plan {} have been changed",
                    planLocation);
            }
        } catch (final WSDLException e) {
            LOG.error("Couldn't load ODEEndpointUpdater", e);
        }

        // update the bpel and bpel4restlight elements (ex.: GET, PUT,..)
        BPELRESTLightUpdater bpelRestUpdater;
        try {
            bpelRestUpdater = new BPELRESTLightUpdater(endpointService);
            if (!bpelRestUpdater.changeEndpoints(planContents, csarId)) {
                // we don't abort deployment here
                LOG.warn("Couldn't change all endpoints inside BPEL4RESTLight Elements in the given process {}", planLocation);
            }
        } catch (final TransformerConfigurationException e) {
            LOG.error("Couldn't load BPELRESTLightUpdater transformer", e);
        } catch (final ParserConfigurationException e) {
            LOG.error("Couldn't load BPELRESTLightUpdaters parser", e);
        } catch (final SAXException e) {
            LOG.error("ParseError: Couldn't parse .bpel file", e);
        } catch (final IOException e) {
            LOG.error("IOError: Couldn't access .bpel file", e);
        }

        // package process
        LOG.info("Prepare deployment of PlanModelReference");

        try {
            Files.createFile(tempPlan);
            // package the updated files
            LOG.debug("Packaging plan to {} ", tempPlan.toAbsolutePath().toString());
            FileSystem.zip(tempPlan, tempDir);
        } catch (final IOException e) {
            LOG.error("Can't package temporary plan for deployment", e);
            return false;
        }

        // deploy process
        LOG.info("Deploying Plan: {}", tempPlan.getFileName().toString());
        String processId = "";
        Map<String, URI> endpoints = Collections.emptyMap();
        try {
            if (processEngine.equalsIgnoreCase(BPS_ENGINE)) {
                LOG.error("BPS ENGINE IS NO LONGER SUPPORTED!!");
            } else {
                final OdeConnector connector = new OdeConnector();
                processId = connector.deploy(tempPlan.toFile(), url);
                endpoints = connector.getEndpointsForPID(processId, url);
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }

        // this will be the endpoint the container can use to instantiate the
        // BPEL Process
        URI endpoint = null;
        if (endpoints.keySet().size() == 1) {
            endpoint = (URI) endpoints.values().toArray()[0];
        } else {
            for (final String partnerLink : endpoints.keySet()) {
                if (partnerLink.equals("client")) {
                    endpoint = endpoints.get(partnerLink);
                }
            }
        }

        if (endpoint == null) {
            return false;
        }

        if (processId == null || endpoint == null || portType == null || this.endpointService == null) {
            LOG.error("Error while processing plan");
            if (processId == null) {
                LOG.error("ProcessId is null");
            }
            if (endpoint == null) {
                LOG.error("Endpoint for process is null");
            }
            if (portType == null) {
                LOG.error("PortType of process is null");
            }

            if (this.endpointService == null) {
                LOG.error("Endpoint Service is null");
            }
            return false;
        }
        LOG.debug("Endpoint for ProcessID \"" + processId + "\" is \"" + endpoints + "\".");
        LOG.info("Deployment of Plan was successfull: {}", tempPlan.getFileName().toString());

        // save endpoint
        final String localContainer = Settings.OPENTOSCA_CONTAINER_HOSTNAME;
        final WSDLEndpoint wsdlEndpoint =
            new WSDLEndpoint(endpoint, portType, localContainer, localContainer, csarId, null, planId, null, null, endpointMetadata);
        this.endpointService.storeWSDLEndpoint(wsdlEndpoint);

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

        boolean wasUndeployed = false;
        if (processEngine.equalsIgnoreCase(BPS_ENGINE)) {
            LOG.error("BPS Engine is no longer supported");
        } else {
            final OdeConnector connector = new OdeConnector();
            wasUndeployed = connector.undeploy(planLocation.toFile(), url);
        }

        // remove endpoint from core
        if (this.endpointService != null) {
            LOG.debug("Starting to remove endpoint!");
            WSDLEndpoint endpoint = this.endpointService.getWSDLEndpointForPlanId(Settings.OPENTOSCA_CONTAINER_HOSTNAME, csarId, planId);
            if (endpoint == null) {
                LOG.warn("Couldn't remove endpoint for plan {}, because endpoint service didn't find any endpoint associated with the plan to remove",
                    planRef.getReference());
            } else if (this.endpointService.removeWSDLEndpoint(endpoint)) {
                LOG.debug("Removed endpoint {} for plan {}", endpoint.toString(),
                    planRef.getReference());
            }
        } else {
            LOG.warn("Couldn't remove endpoint for plan {}, cause endpoint service is not available",
                planRef.getReference());
        }

        if (wasUndeployed) {
            LOG.info("Undeployment of Plan " + planRef.getReference() + " was successful");
        } else {
            LOG.warn("Undeployment of Plan " + planRef.getReference() + " was unsuccessful");
        }
        return wasUndeployed;
    }

    @Nullable
    private Path planLocationOnDisk(CsarId csarId, QName planId, PlanModelReference planRef) {
        if (storage == null) {
            return null;
        }
        @SuppressWarnings("null") // ignore MT implications
            Csar csar = storage.findById(csarId);
        TPlan toscaPlan;
        try {
            toscaPlan = ToscaEngine.resolvePlanReference(csar, planId);
        } catch (NotFoundException e) {
            LOG.error("Plan [{}] could not be found in csar {}", planId, csarId.csarName());
            return null;
        }
        TServiceTemplate containingServiceTemplate = ToscaEngine.getContainingServiceTemplate(csar, toscaPlan);
        assert (containingServiceTemplate != null); // shouldn't be null, since we have a plan from it

        // planRef.getReference() is overencoded. It's also not relative to the Csar root (but to one level below it)
        Path planLocation = ArtifactResolver.resolvePlan.apply(containingServiceTemplate, toscaPlan);
        // FIXME get rid of AbstractArtifact!
        AbstractArtifact planReference = ArtifactResolver.resolveArtifact(csar, planLocation,
            // just use the last segment, determining the filename.
            Paths.get(planRef.getReference().substring(planRef.getReference().lastIndexOf('/') + 1)));
        if (planReference == null) {
            LOG.error("Plan reference '{}' resulted in a null ArtifactReference.",
                planRef.getReference());
            return null;
        }
        if (!planReference.isFileArtifact()) {
            LOG.warn("Only plan references pointing to a file are supported!");
            return null;
        }
        Path artifact;
        try {
            artifact = planReference.getFile("").getFile();
        } catch (SystemException e) {
            LOG.warn("ugh... SystemException when getting a path we already had", e);
            return null;
        }
        if (!artifact.getFileName().toString().endsWith(".zip")) {
            LOG.debug("Plan reference is not a ZIP file. It was '{}'.", artifact.getFileName());
            return null;
        }
        return artifact;
    }

    @Override
    public String toString() {
        return "openTOSCA PlanEngine WS-BPEL 2.0 Plugin v1.0";
    }
}
