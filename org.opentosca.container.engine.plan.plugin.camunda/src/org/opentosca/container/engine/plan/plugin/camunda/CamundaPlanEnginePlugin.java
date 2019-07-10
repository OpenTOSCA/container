package org.opentosca.container.engine.plan.plugin.camunda;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.xml.namespace.QName;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.engine.IToscaEngineService;
import org.opentosca.container.core.model.AbstractArtifact;
import org.opentosca.container.core.model.AbstractFile;
import org.opentosca.container.core.model.csar.CSARContent;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.next.model.PlanLanguage;
import org.opentosca.container.core.service.ICoreFileService;
import org.opentosca.container.core.service.IFileAccessService;
import org.opentosca.container.core.tosca.model.TPlan.PlanModelReference;
import org.opentosca.container.engine.plan.plugin.IPlanEnginePlanRefPluginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CamundaPlanEnginePlugin implements IPlanEnginePlanRefPluginService {

    final private static Logger LOG = LoggerFactory.getLogger(CamundaPlanEnginePlugin.class);

    private final String CAMUNDA_DESCRIPTION = "OpenTOSCA PlanEngine Camunda BPMN 2.0 Plugin v1.0";
    private final String DEPLOYMENT_SUFFIX = "/deployment/create";
    private final String PROCESS_DEFINITION_SUFFIX = "process-definition";

    private ICoreFileService fileService = null;
    private IToscaEngineService toscaEngine = null;
    private IFileAccessService fileAccessService = null;

    @Override
    public boolean deployPlanReference(final QName planId, final PlanModelReference planRef, final CSARID csarId) {
        LOG.debug("Trying to deploy plan with ID {} on Camunda BPMN engine...", planId);

        if (Objects.isNull(this.fileService)) {
            LOG.error("Unable to deploy plan reference with file service equal to null!");
            return false;
        }

        // get CSAR containing the plan
        CSARContent csar = null;
        try {
            csar = this.fileService.getCSAR(csarId);
        }
        catch (final UserException exc) {
            LOG.error("An User Exception occured while retrieving the CSAR: {}", exc);
            return false;
        }

        final AbstractArtifact planReference = this.toscaEngine.getPlanModelReferenceAbstractArtifact(csar, planId);
        if (Objects.isNull(planReference) || !planReference.isFileArtifact()) {
            LOG.error("Referenced artifact is invalid!");
            return false;
        }

        final AbstractFile plan = planReference.getFile("");
        if (Objects.isNull(plan) || !plan.getName().substring(plan.getName().lastIndexOf('.') + 1).equals("zip")) {
            LOG.debug("Plan reference is not a ZIP file!");
            return false;
        }

        Path fetchedPlan = null;
        try {
            fetchedPlan = plan.getFile();
        }
        catch (final SystemException exc) {
            LOG.error("An System Exception occured while fetching the plan file: {}", exc);
            return false;
        }

        return deployPlanFile(fetchedPlan, csarId, planId);
    }

    private boolean deployPlanFile(final Path planPath, final CSARID csarId, final QName planId) {
        LOG.debug("Starting to deploy plan from retrieved file...");

        if (Objects.isNull(this.fileAccessService)) {
            LOG.error("FileAccessService is not available, can't create needed temporary space on disk");
            return false;
        }

        // create temporary directory and unzip plan
        final File tempDir = this.fileAccessService.getTemp();
        final List<File> planContents = this.fileAccessService.unzip(planPath.toFile(), tempDir);
        LOG.debug("Plan contains {} files.", planContents.size());

        // create Post request for the Camunda REST API
        final CloseableHttpClient httpClient = HttpClients.createDefault();
        final HttpPost deploymentRequest = new HttpPost(Settings.ENGINE_PLAN_BPMN_URL + this.DEPLOYMENT_SUFFIX);

        // only deploy if plan was not deployed before or files have changed
        final StringBody enableDuplicateFiltering = new StringBody("true", ContentType.TEXT_PLAIN);
        final StringBody deployChangedOnly = new StringBody("true", ContentType.TEXT_PLAIN);
        final StringBody deploymentName = new StringBody(planId.toString(), ContentType.TEXT_PLAIN);

        // add required meta data to the request
        final MultipartEntityBuilder builder =
            MultipartEntityBuilder.create().addPart("deployment-name", deploymentName)
                                  .addPart("enable-duplicate-filtering", enableDuplicateFiltering)
                                  .addPart("deploy-changed-only", deployChangedOnly);

        // add all files contained in the plan to the request
        for (final File file : planContents) {
            final FileBody fileBody = new FileBody(file);
            builder.addPart(file.getName(), fileBody);
        }

        // send Post request to the engine
        final HttpEntity httpEntity = builder.build();
        deploymentRequest.setEntity(httpEntity);
        try {
            HttpResponse response = httpClient.execute(deploymentRequest);

            if (response.getStatusLine().getStatusCode() != 200) {
                LOG.error("Response returned status code: {}", response.getStatusLine().getStatusCode());
                return false;
            }

            // get the ID of the created deployment
            final JSONParser parser = new JSONParser();
            final JSONObject json = (JSONObject) parser.parse(EntityUtils.toString(response.getEntity()));
            if (!json.containsKey("id")) {
                LOG.error("Deployment response contains no ID for further processing!");
                return false;
            }
            final String id = json.get("id").toString();
            LOG.debug("Deployment has the following ID: {}", id);

            // get process definition ID to create a corresponding endpoint
            final URIBuilder uriBuilder =
                new URIBuilder(Settings.ENGINE_PLAN_BPMN_URL + this.PROCESS_DEFINITION_SUFFIX);
            uriBuilder.setParameter("deploymentId", id);
            final HttpGet getProcessDefinition = new HttpGet(uriBuilder.build());
            response = httpClient.execute(getProcessDefinition);

            // TODO: parse process definition ID from response and store endpoint in endpoint DB
        }
        catch (final ClientProtocolException e) {
            LOG.error("An ClientProtocolException occured while sending post to the engine: {}", e);
            return false;
        }
        catch (final IOException e) {
            LOG.error("An IOException occured while sending post to the engine: {}", e);
            return false;
        }
        catch (final org.json.simple.parser.ParseException e) {
            LOG.error("An ParseException occured while parsing response to Json: {}", e);
            return false;
        }
        catch (final URISyntaxException e) {
            LOG.error("An URISyntaxException occured while creating URI to retrieve the process ID: {}", e);
            return false;
        }

        return true;
    }

    @Override
    public boolean undeployPlanReference(final QName planId, final PlanModelReference planRef, final CSARID csarId) {
        // TODO
        LOG.warn("The undeploy method for the Camunda plan engine is not implemented yet.");
        return false;
    }

    @Override
    public String getLanguageUsed() {
        return PlanLanguage.BPMN.toString();
    }

    @Override
    public List<String> getCapabilties() {
        return Arrays.asList(PlanLanguage.BPMN.toString());
    }

    @Override
    public String toString() {
        return this.CAMUNDA_DESCRIPTION;
    }

    /**
     * Bind method for IFileServices
     *
     * @param fileService the file service to bind
     */
    public void registerFileService(final ICoreFileService fileService) {

        if (Objects.nonNull(fileService)) {
            this.fileService = fileService;
        }
    }

    /**
     * Unbind method for IFileServices
     *
     * @param fileService the file service to unbind
     */
    protected void unregisterFileService(final ICoreFileService fileService) {
        LOG.debug("Unregistering FileService {}", fileService.toString());
        this.fileService = null;
    }

    /**
     * Bind method for IToscaEngineService
     *
     * @param service the IToscaEngineService to bind
     */
    public void registerToscaEngine(final IToscaEngineService engineService) {
        LOG.debug("Registering IToscaEngineService {}", engineService.toString());
        if (Objects.nonNull(engineService)) {
            this.toscaEngine = engineService;
        }
    }

    /**
     * Unbind method for IToscaEngineService
     *
     * @param endpointService the IToscaEngineService to unbind
     */
    protected void unregisterToscaEngine(final IToscaEngineService engineService) {
        LOG.debug("Unregistering IToscaEngineService {}", engineService.toString());
        this.toscaEngine = null;
    }

    /**
     * Bind method for IFileAccessServices
     *
     * @param fileAccessService the fileAccessService to bind
     */
    public void registerFileAccessService(final IFileAccessService fileAccessService) {
        LOG.debug("Registering FileAccessService {}", fileAccessService.toString());
        if (Objects.nonNull(fileAccessService)) {
            this.fileAccessService = fileAccessService;
        }
    }

    /**
     * Unbind method for IFileAccessServices
     *
     * @param fileAccessService the fileAccessService to unbind
     */
    protected void unregisterFileAccessService(final IFileAccessService fileAccessService) {
        LOG.debug("Unregistering IFileAccessService {}", fileAccessService.toString());
        this.fileAccessService = null;
    }
}
