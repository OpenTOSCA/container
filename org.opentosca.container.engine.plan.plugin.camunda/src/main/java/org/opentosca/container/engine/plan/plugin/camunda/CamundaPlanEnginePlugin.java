package org.opentosca.container.engine.plan.plugin.camunda;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.ids.XmlId;
import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.ids.elements.PlanId;
import org.eclipse.winery.model.ids.elements.PlansId;
import org.eclipse.winery.model.tosca.TPlan.PlanModelReference;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.common.RepositoryFileReference;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
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
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.impl.service.FileSystem;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.next.model.Endpoint;
import org.opentosca.container.core.next.model.PlanLanguage;
import org.opentosca.container.core.service.CsarStorageService;
import org.opentosca.container.core.service.ICoreEndpointService;
import org.opentosca.container.engine.plan.plugin.IPlanEnginePlanRefPluginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@NonNullByDefault
/**
 * This class implements functionality for the deployment and undeployment of
 * BPMN 2.0 Processes on the Camunda BPMN Engine.<br>
 * <br>
 *
 * Copyright 2019 IAAS University of Stuttgart <br>
 * <br>
 */
public class CamundaPlanEnginePlugin implements IPlanEnginePlanRefPluginService {

    final private static Logger LOG = LoggerFactory.getLogger(CamundaPlanEnginePlugin.class);

    private static final String CAMUNDA_DESCRIPTION = "OpenTOSCA PlanEngine Camunda BPMN 2.0 Plugin v1.0";
    private static final String DEPLOYMENT_SUFFIX = "/deployment";
    private static final String CREATE_SUFFIX = "/create";
    private static final String PROCESS_DEFINITION_SUFFIX = "/process-definition";
    private static final String INSTANCE_CREATION_SUFFIX = "/submit-form";

    private final JSONParser jsonParser = new JSONParser();
    private final ICoreEndpointService endpointService;
    private final CsarStorageService storage;

    @Inject
    public CamundaPlanEnginePlugin(ICoreEndpointService endpointService, CsarStorageService storage) {
        this.endpointService = endpointService;
        this.storage = storage;
    }

    @Override
    public boolean deployPlanReference(final QName planId, final PlanModelReference planRef, final CsarId csarId) {
        LOG.debug("Trying to deploy plan with ID {} on Camunda BPMN engine...", planId);
        Path fetchedPlan = planLocationOnDisk(csarId, planId, planRef);
        return deployPlanFile(fetchedPlan, csarId, planId);
    }

    /**
     * Deploys the given plan into the Camunda BPMN engine
     *
     * @param planPath the path to the zip file containing the plan and its artifacts
     * @param csarId   the ID of the CSAR to which the plan belongs
     * @param planId   the QName to identify the plan
     * @return <code>true</code> if deployment is successful, <code>false</code>
     * otherwise
     */
    private boolean deployPlanFile(final Path planPath, final CsarId csarId, final QName planId) {
        LOG.debug("Starting to deploy plan from retrieved file...");

        // create temporary directory and unzip plan
        final List<Path> planContents;
        try {
            planContents = FileSystem.unzip(planPath, FileSystem.getTemporaryFolder());
        } catch (IOException e) {
            LOG.info("Failed to extract plan contents for plan {} in CSAR {}", planId, csarId.csarName());
            return false;
        }
        LOG.debug("Plan contains {} files.", planContents.size());

        // create Post request for the Camunda REST API
        final HttpPost deploymentRequest = new HttpPost(
            Settings.ENGINE_PLAN_BPMN_URL + DEPLOYMENT_SUFFIX + CREATE_SUFFIX);

        // only deploy if plan was not deployed before or files have changed
        final StringBody enableDuplicateFiltering = new StringBody("false", ContentType.TEXT_PLAIN);
        final StringBody deployChangedOnly = new StringBody("false", ContentType.TEXT_PLAIN);
        final StringBody deploymentName = new StringBody(planId.toString(), ContentType.TEXT_PLAIN);

        // add required meta data to the request
        final MultipartEntityBuilder builder = MultipartEntityBuilder.create()
            .addPart("deployment-name", deploymentName)
            .addPart("enable-duplicate-filtering", enableDuplicateFiltering)
            .addPart("deploy-changed-only", deployChangedOnly);

        // add all files contained in the plan to the request
        for (final Path file : planContents) {
            final FileBody fileBody = new FileBody(file.toFile());
            builder.addPart(file.getFileName().toString(), fileBody);
        }

        try (final CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // send Post request to the engine
            final HttpEntity httpEntity = builder.build();
            deploymentRequest.setEntity(httpEntity);
            HttpResponse response = httpClient.execute(deploymentRequest);

            if (response.getStatusLine().getStatusCode() != 200) {
                LOG.error("Response returned status code: {}", response.getStatusLine().getStatusCode());
                return false;
            }

            // get the ID of the created deployment
            final JSONObject json = (JSONObject) jsonParser.parse(EntityUtils.toString(response.getEntity()));
            if (!json.containsKey("id")) {
                LOG.error("Deployment response contains no ID for further processing!");
                return false;
            }

            final String id = json.get("id").toString();
            LOG.debug("Deployment has the following ID: {}", id);

            // get all process definition IDs of the created deployment
            final URIBuilder uriBuilder = new URIBuilder(Settings.ENGINE_PLAN_BPMN_URL + PROCESS_DEFINITION_SUFFIX);
            uriBuilder.setParameter("deploymentId", id);
            final HttpGet getProcessDefinition = new HttpGet(uriBuilder.build());
            response = httpClient.execute(getProcessDefinition);

            final JSONArray processDefinitions = (JSONArray) jsonParser
                .parse(EntityUtils.toString(response.getEntity()));
            if (processDefinitions.isEmpty()) {
                LOG.error("No process definitions contained in created deployment!");
                return false;
            }

            // get the first process definition and create corresponding endpoint
            final JSONObject planProcessDefinition = (JSONObject) processDefinitions.get(0);
            final String planDefinitionID = planProcessDefinition.get("id").toString();
            final URI endpointUri = new URI(Settings.ENGINE_PLAN_BPMN_URL + PROCESS_DEFINITION_SUFFIX + "/"
                + planDefinitionID + INSTANCE_CREATION_SUFFIX);


            Map<String,String> endpointMetadata = new HashMap<String, String>();
            endpointMetadata.put("PlanType", "BPMN");
            endpointMetadata.put("EndpointType", "Invoke");

            final Endpoint endpoint = new Endpoint(endpointUri, Settings.OPENTOSCA_CONTAINER_HOSTNAME,
                Settings.OPENTOSCA_CONTAINER_HOSTNAME, csarId, null, endpointMetadata, null, null, null, planId);
            endpointService.storeEndpoint(endpoint);

            return true;
        } catch (final ClientProtocolException e) {
            LOG.error("A ClientProtocolException occured while sending post to the engine: ", e);
            return false;
        } catch (final IOException e) {
            LOG.error("An IOException occured while sending post to the engine: ", e);
            return false;
        } catch (final ParseException e) {
            LOG.error("A ParseException occured while parsing response to Json: ", e);
            return false;
        } catch (final URISyntaxException e) {
            LOG.error("An URISyntaxException occured while creating URI to retrieve the process ID: ", e);
            return false;
        }
    }

    @Override
    public boolean undeployPlanReference(final QName planId, final PlanModelReference planRef, final CsarId csarId) {
        LOG.debug("Trying to undeploy plan with ID {} from Camund BPMN engine...", planId);

        // get endpoint related to the plan and extract process definition ID from the URI
        final List<Endpoint> endpoints = endpointService.getEndpointsForPlanId(Settings.OPENTOSCA_CONTAINER_HOSTNAME, csarId, planId);

        final List<Endpoint> endpointsToRemove = new ArrayList<>();
        for (Endpoint endpoint : endpoints) {

            final String[] endpointParts = endpoint.getUri().toString().split("/");

            if (endpointParts.length < 2) {
                LOG.error("Unable to parse process definition ID for plan {} out of endpoint {}", planId,
                    endpoint.getUri());
                return false;
            }

            final String processDefinitionID = endpointParts[endpointParts.length - 2];
            LOG.debug("Extracted following process definition ID: {}", processDefinitionID);

            try (final CloseableHttpClient httpClient = HttpClients.createDefault()) {
                // get information for process definition to extract related deployment ID
                final HttpGet getProcessDefinition = new HttpGet(
                    Settings.ENGINE_PLAN_BPMN_URL + PROCESS_DEFINITION_SUFFIX + "/" + processDefinitionID);
                final HttpResponse processDefinitionResponse = httpClient.execute(getProcessDefinition);

                if (processDefinitionResponse.getStatusLine().getStatusCode() != 200) {
                    LOG.error("Request to retrieve process definition returned invalid status code {}",
                        processDefinitionResponse.getStatusLine().getStatusCode());
                    return false;
                }

                // extract deployment ID from Json response
                final String processDefinitionInformation = EntityUtils.toString(processDefinitionResponse.getEntity());
                final JSONObject json = (JSONObject) jsonParser.parse(processDefinitionInformation);
                if (!json.containsKey("deploymentId")) {
                    LOG.error("Deployment response contains no ID for further processing!");
                    return false;
                }
                final String deploymentID = json.get("deploymentId").toString();
                LOG.debug("Extracted following deployment ID for deletion {}", deploymentID);

                // delete the deployment and all related process definitions and instances
                final URIBuilder uriBuilder = new URIBuilder(
                    Settings.ENGINE_PLAN_BPMN_URL + DEPLOYMENT_SUFFIX + "/" + deploymentID);
                uriBuilder.setParameter("cascade", "true");
                final HttpDelete deleteDeployment = new HttpDelete(uriBuilder.build());
                final HttpResponse deletionResponse = httpClient.execute(deleteDeployment);

                // check success and reutrn to caller
                if (deletionResponse.getStatusLine().getStatusCode() == 204) {
                    LOG.debug("Deletion of plan deployment successful.");
                    endpointsToRemove.add(endpoint);
                } else {
                    LOG.error("Deletion response returned invalid status code {}",
                        deletionResponse.getStatusLine().getStatusCode());
                    return false;
                }
            } catch (final IOException e) {
                LOG.error("An IOException occured while sending post to the Camunda engine: ", e);
                return false;
            } catch (final ParseException e) {
                LOG.error("A ParseException occured while parsing response to Json: ", e);
                return false;
            } catch (final URISyntaxException e) {
                LOG.error("An URISyntaxException occured while building delete URL", e);
                return false;
            }
        }

        endpointsToRemove.forEach(this.endpointService::removeEndpoint);

        return true;
    }

    @Nullable
    private Path planLocationOnDisk(CsarId csarId, QName planId, PlanModelReference planRef) {
        if (storage == null) {
            return null;
        }

        Csar csar = storage.findById(csarId);

        IRepository repository = RepositoryFactory.getRepository(csar.getSaveLocation());

        PlanId plan = new PlanId(new PlansId(new ServiceTemplateId(csar.entryServiceTemplate().getTargetNamespace(), csar.entryServiceTemplate().getId(), false)), new XmlId(planId.toString(), false));

        Collection<RepositoryFileReference> fileRefs = repository.getContainedFiles(plan);

        Path planPath = null;

        for (RepositoryFileReference ref : fileRefs) {
            if (ref.getFileName().endsWith(".war")) {
                planPath = repository.ref2AbsolutePath(ref);
                break;
            }
            if (ref.getFileName().endsWith(".zip")) {
                planPath = repository.ref2AbsolutePath(ref);
                break;
            }
        }

        return planPath;
    }

    @Override
    public String getLanguageUsed() {
        return PlanLanguage.BPMN.toString();
    }

    @Override
    public List<String> getCapabilties() {
        return Collections.singletonList(PlanLanguage.BPMN.toString());
    }

    @Override
    public String toString() {
        return CAMUNDA_DESCRIPTION;
    }
}
