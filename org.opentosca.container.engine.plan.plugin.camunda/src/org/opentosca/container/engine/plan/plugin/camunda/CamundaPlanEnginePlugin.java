package org.opentosca.container.engine.plan.plugin.camunda;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.engine.IToscaEngineService;
import org.opentosca.container.core.model.AbstractArtifact;
import org.opentosca.container.core.model.AbstractFile;
import org.opentosca.container.core.model.csar.CSARContent;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.model.endpoint.wsdl.WSDLEndpoint;
import org.opentosca.container.core.service.ICoreEndpointService;
import org.opentosca.container.core.service.ICoreFileService;
import org.opentosca.container.core.service.IFileAccessService;
import org.opentosca.container.core.service.IHTTPService;
import org.opentosca.container.core.tosca.model.TPlan.PlanModelReference;
import org.opentosca.container.engine.plan.plugin.IPlanEnginePlanRefPluginService;
import org.opentosca.container.engine.plan.plugin.camunda.iaenginecopies.CopyOfIAEnginePluginWarTomcatServiceImpl;
import org.opentosca.container.engine.plan.plugin.camunda.util.Messages;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CamundaPlanEnginePlugin implements IPlanEnginePlanRefPluginService {

    final private static Logger LOG = LoggerFactory.getLogger(CamundaPlanEnginePlugin.class);

    private ICoreFileService fileService = null;
    private IFileAccessService fileAccessService = null;
    private IToscaEngineService toscaEngineService;
    private ICoreEndpointService endpointService;


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
    public boolean deployPlanReference(final QName planId, final PlanModelReference planRef, final CSARID csarId) {

        this.bindServices();

        Path fetchedPlan;

        String planName = "";

        // retrieve process
        if (this.fileService != null) {

            CSARContent csar = null;

            try {
                csar = this.fileService.getCSAR(csarId);
            }
            catch (final UserException exc) {
                CamundaPlanEnginePlugin.LOG.error("Could not get the CSAR from file service. An User Exception occured.",
                                                  exc);
                return false;
            }

            AbstractArtifact planReference = null;

            planReference = this.toscaEngineService.getPlanModelReferenceAbstractArtifact(csar, planId);

            if (planReference == null) {
                CamundaPlanEnginePlugin.LOG.error("Plan reference '{}' resulted in a null ArtifactReference.",
                                                  planRef.getReference());
                return false;
            }

            if (!planReference.isFileArtifact()) {
                CamundaPlanEnginePlugin.LOG.warn("Only plan references pointing to a file are supported!");
                return false;
            }

            final AbstractFile plan = planReference.getFile("");

            if (plan == null) {
                CamundaPlanEnginePlugin.LOG.error("ArtifactReference resulted in null AbstractFile.");
                return false;
            }

            if (!plan.getName().substring(plan.getName().lastIndexOf('.') + 1).equals("war")) {
                CamundaPlanEnginePlugin.LOG.debug("Plan reference is not a WAR file. It was '{}'.", plan.getName());
                return false;
            }

            try {
                fetchedPlan = plan.getFile();

                if (null == fetchedPlan || fetchedPlan.equals("")) {
                    CamundaPlanEnginePlugin.LOG.error("No path for plan.");
                    return false;
                } else {
                    LOG.debug("Plan should be located at {}", fetchedPlan.toString());
                }

                if (fetchedPlan.toFile().exists()) {
                    LOG.debug("Plan file exists at {}", fetchedPlan.toString());
                }
            }
            catch (final SystemException exc) {
                CamundaPlanEnginePlugin.LOG.error("An System Exception occured. File could not be fetched.", exc);
                return false;
            }

        } else {
            CamundaPlanEnginePlugin.LOG.error("Can't fetch relevant files from FileService: FileService not available");
            return false;
        }

        // ##################################################################################################################################################
        // ### dirty copy of IAEngine War Tomcat Plugin
        // ### TODO make this pretty
        // ##################################################################################################################################################

        final CopyOfIAEnginePluginWarTomcatServiceImpl deployer = new CopyOfIAEnginePluginWarTomcatServiceImpl();
        deployer.deployImplementationArtifact(csarId, fetchedPlan.toFile());
        // POST http://localhost:8080/engine-rest/process-definition/{id}/start
        URI endpointURI = null;
        try {
            planName = this.toscaEngineService.getPlanName(csarId, planId);
            final int retries = 100;

            for (int iteration = retries; iteration > 0; iteration--) {
                endpointURI = this.searchForEndpoint(planName);

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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (final NullPointerException e) {

        }

        // ##################################################################################################################################################
        // ##################################################################################################################################################

        if (endpointURI == null) {
            CamundaPlanEnginePlugin.LOG.warn("No endpoint for Plan {} could be determined, container won't be able to instantiate it",
                                             planRef.getReference());
            return false;
        }

        if (null == this.endpointService) {
            LOG.error("Endpoint serivce is offline.");
        }

        final WSDLEndpoint point = new WSDLEndpoint();
        point.setCSARId(csarId);
        point.setPlanId(planId);
        // point.setIaName(planName);
        point.setURI(endpointURI);

        this.endpointService.storeWSDLEndpoint(point);

        return true;
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

    private void bindServices() {
        final BundleContext context = Activator.getContext();

        final ServiceReference<ICoreFileService> coreRef = context.getServiceReference(ICoreFileService.class);
        this.fileService = context.getService(coreRef);

        final ServiceReference<IFileAccessService> fileAccess = context.getServiceReference(IFileAccessService.class);
        this.fileAccessService = context.getService(fileAccess);

        final ServiceReference<IToscaEngineService> toscaEngine =
            context.getServiceReference(IToscaEngineService.class);
        this.toscaEngineService = context.getService(toscaEngine);

        final ServiceReference<ICoreEndpointService> endpointService =
            context.getServiceReference(ICoreEndpointService.class);
        this.endpointService = context.getService(endpointService);
    }

    @Override
    public boolean undeployPlanReference(final QName planId, final PlanModelReference planRef, final CSARID csarId) {
        LOG.warn("The undeploy method for the Camunda plan engine is not implemented yet.");
        return false;
    }

    @Override
    public String toString() {
        return Messages.CamundaPlanEnginePlugin_description;
    }
}
