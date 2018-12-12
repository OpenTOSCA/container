package org.opentosca.container.engine.plan.plugin.camunda;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.model.tosca.TPlan.PlanModelReference;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.opentosca.container.core.common.NotFoundException;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.engine.ToscaEngine;
import org.opentosca.container.core.model.AbstractArtifact;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.model.csar.backwards.ArtifactResolver;
import org.opentosca.container.core.model.endpoint.wsdl.WSDLEndpoint;
import org.opentosca.container.core.service.CsarStorageService;
import org.opentosca.container.core.service.ICoreEndpointService;
import org.opentosca.container.core.service.IHTTPService;
import org.opentosca.container.engine.plan.plugin.IPlanEnginePlanRefPluginService;
import org.opentosca.container.engine.plan.plugin.camunda.iaenginecopies.CopyOfIAEnginePluginWarTomcatServiceImpl;
import org.opentosca.container.engine.plan.plugin.camunda.util.Messages;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CamundaPlanEnginePlugin implements IPlanEnginePlanRefPluginService {

    final private static Logger LOG = LoggerFactory.getLogger(CamundaPlanEnginePlugin.class);

    private ICoreEndpointService endpointService;
    private CsarStorageService storage;

    @Override
    public String getLanguageUsed() {
        return Messages.CamundaPlanEnginePlugin_language;
    }

    @Override
    public List<String> getCapabilties() {
        final List<String> capabilities = new ArrayList<>();
        for (final String capability : Messages.CamundaPlanEnginePlugin_capabilities.split("[,;]")) {
            capabilities.add(capability.trim());
        }
        return capabilities;
    }

    @Override
    public boolean deployPlanReference(final QName planId, final PlanModelReference planRef, final CsarId csarId) {
        Path fetchedPlan = planLocationOnDisk(csarId, planId, planRef);

        final CopyOfIAEnginePluginWarTomcatServiceImpl deployer = new CopyOfIAEnginePluginWarTomcatServiceImpl();
        deployer.deployImplementationArtifact(csarId.toOldCsarId(), fetchedPlan.toFile());
        // POST http://localhost:8080/engine-rest/process-definition/{id}/start
        URI endpointURI = null;
        try {
            final int retries = 100;
            for (int iteration = retries; iteration > 0; iteration--) {
                endpointURI = this.searchForEndpoint(planId.getLocalPart());
                if (null == endpointURI) {
                    try {
                        LOG.debug("Endpoint not set yet, Camunda might be still processing it.");
                        Thread.sleep(1000);
                    }
                    catch (final InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    break;
                }
            }
            LOG.debug("Endpoint URI is {}", endpointURI.getPath());
        }
        catch (final URISyntaxException e) {
            e.printStackTrace();
        }

        if (endpointURI == null) {
            LOG.warn("No endpoint for Plan {} could be determined, container won't be able to instantiate it",
                                             planRef.getReference());
            return false;
        }

        if (null == this.endpointService) {
            LOG.error("Endpoint serivce is offline.");
        }

        final WSDLEndpoint point = new WSDLEndpoint();
        point.setCsarId(csarId);
        point.setPlanId(planId);
        point.setIaName(fetchedPlan.getFileName().toString());
        point.setURI(endpointURI);

        this.endpointService.storeWSDLEndpoint(point);

        return true;
    }
    
    @Nullable
    private Path planLocationOnDisk(CsarId csarId, QName planId, PlanModelReference planRef) {
        if (storage == null) { return null; }
        @SuppressWarnings("null") // ignore MT implications
        Csar csar = storage.findById(csarId);
        TPlan toscaPlan;
        try {
            toscaPlan = ToscaEngine.resolvePlanReference(csar, planId);
        }
        catch (NotFoundException e) {
            LOG.error("Plan [{}] could not be found in csar {}", planId, csarId.csarName());
            return null;
        }
        TServiceTemplate containingServiceTemplate = ToscaEngine.containingServiceTemplate(csar, toscaPlan);
        assert(containingServiceTemplate != null); // shouldn't be null, since we have a plan from it
        
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
        }
        catch (SystemException e) {
            LOG.warn("ugh... SystemException when getting a path we already had", e);
            return null;
        }
        if (!artifact.getFileName().endsWith(".war")) {
            LOG.debug("Plan reference is not a WAR file. It was '{}'.", artifact.getFileName());
            return null;
        }
        return artifact;
    }

    private URI searchForEndpoint(final String planName) throws URISyntaxException {
        URI endpointURI;
        LOG.debug("Search for Plan Endpoint");

        final String processDefinitions = "http://localhost:8080/engine-rest/process-definition/";

        IHTTPService httpService;
        final BundleContext context = Activator.getContext();
        final ServiceReference<IHTTPService> tmpHttpService = context.getServiceReference(IHTTPService.class);
        httpService = context.getService(tmpHttpService);

        HttpResponse response;
        String output = null;

        LOG.debug("Retrieve list of deployed plans");
        try {
            response = httpService.Get(processDefinitions);
            output = EntityUtils.toString(response.getEntity(), "UTF-8");
            output = output.substring(1, output.length() - 1);
        }
        catch (final IOException e) {
            LOG.error("An error occured while retrieving the deployed plan list from camunda: ",
                      e.getLocalizedMessage());
            e.printStackTrace();
            return null;
        }
        final String json = output;

        LOG.trace("Response json: {}", json);

        final String[] list = json.split("\\{");

        final HashMap<String, String> ids = new HashMap<>();

        for (final String entry : list) {
            if (null != entry && !entry.equals("")) {
                final String[] fields = entry.split(",");

                final String id = fields[0].substring(6, fields[0].length() - 1);
                final String key = fields[1].substring(7, fields[1].length() - 1);

                ids.put(id, key);
                LOG.trace("ID {} KEY {}", id, key);
            }
        }

        String planID = "";

        if (ids.containsValue(planName)) {
            for (final String id : ids.keySet()) {
                if (ids.get(id).equals(planName)) {
                    planID = ids.get(id);
                }
            }
        }

        if (planID.equals("")) {
            LOG.warn("No endpoint found for plan {}!", planName);
            return null;
        }

        endpointURI = new URI(processDefinitions + "key/" + planID + "/start");
        return endpointURI;
    }
    
    public void bindStorageService(CsarStorageService storageService) {
        if (storageService == null) {
            LOG.warn("Trying to bind null storage service. Aborting binding");
            return;
        }
        this.storage = storageService;
        LOG.info("Bound storage service");
    }
    
    
    public void unbindStorageService(CsarStorageService storageService) {
        LOG.info("Unbinding storage service");
        this.storage = null;
    }

    public void bindEndpointService(ICoreEndpointService endpointService) {
        if (endpointService == null) {
            LOG.error("Attempted to bind null endpoint service. Aborting");
        }
        this.endpointService = endpointService;
        LOG.info("Bound endpoint service");
    }
    
    public void unbindEndpointService(ICoreEndpointService endpointService) {
        LOG.info("Unbinding endpoint service");
        this.endpointService = null;
    }

    @Override
    public boolean undeployPlanReference(final QName planId, final PlanModelReference planRef, final CsarId csarId) {
        LOG.warn("The undeploy method for the Camunda plan engine is not implemented yet.");
        return false;
    }

    @Override
    public String toString() {
        return Messages.CamundaPlanEnginePlugin_description;
    }
}
