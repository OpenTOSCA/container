package org.opentosca.container.engine.plan.plugin.camunda;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import javax.inject.Inject;
import javax.xml.namespace.QName;

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
import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.model.tosca.TPlan.PlanModelReference;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.opentosca.container.core.common.NotFoundException;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.engine.ToscaEngine;
import org.opentosca.container.core.impl.service.FileSystem;
import org.opentosca.container.core.impl.service.ZipManager;
import org.opentosca.container.core.model.AbstractArtifact;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.model.csar.backwards.ArtifactResolver;
import org.opentosca.container.core.model.endpoint.wsdl.WSDLEndpoint;
import org.opentosca.container.core.next.model.PlanLanguage;
import org.opentosca.container.core.service.CsarStorageService;
import org.opentosca.container.core.service.ICoreEndpointService;
import org.opentosca.container.core.service.IHTTPService;
import org.opentosca.container.engine.plan.plugin.IPlanEnginePlanRefPluginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@NonNullByDefault
/**
 * This class implements functionality for the deployment and undeployment of BPMN 2.0 Processes on
 * the Camunda BPMN Engine.<br>
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
  private final IHTTPService httpService;

  @Inject
  public CamundaPlanEnginePlugin(ICoreEndpointService endpointService, CsarStorageService storage, IHTTPService httpService) {
    this.endpointService = endpointService;
    this.storage = storage;
    this.httpService = httpService;
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
   * @param csarId the ID of the CSAR to which the plan belongs
   * @param planId the QName to identify the plan
   * @return <code>true</code> if deployment is successful, <code>false</code> otherwise
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
    final HttpPost deploymentRequest = new HttpPost(Settings.ENGINE_PLAN_BPMN_URL + DEPLOYMENT_SUFFIX + CREATE_SUFFIX);

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

      final JSONArray processDefinitions = (JSONArray) jsonParser.parse(EntityUtils.toString(response.getEntity()));
      if (processDefinitions.isEmpty()) {
        LOG.error("No process definitions contained in created deployment!");
        return false;
      }

      // get the first process definition and create corresponding endpoint
      final JSONObject planProcessDefinition = (JSONObject) processDefinitions.get(0);
      final String planDefinitionID = planProcessDefinition.get("id").toString();
      final URI endpoint = new URI(Settings.ENGINE_PLAN_BPMN_URL + PROCESS_DEFINITION_SUFFIX +
        "/" + planDefinitionID + INSTANCE_CREATION_SUFFIX);
      final WSDLEndpoint wsdlEndpoint = new WSDLEndpoint(endpoint, null, Settings.OPENTOSCA_CONTAINER_HOSTNAME,
        Settings.OPENTOSCA_CONTAINER_HOSTNAME, csarId, null, planId, null, null, Collections.emptyMap());
      endpointService.storeWSDLEndpoint(wsdlEndpoint);
      return true;
    } catch (final ClientProtocolException e) {
      LOG.error("A ClientProtocolException occured while sending post to the engine: ", e);
      return false;
    } catch (final IOException e){
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
    final WSDLEndpoint endpoint = endpointService.getWSDLEndpointForPlanId(Settings.OPENTOSCA_CONTAINER_HOSTNAME, csarId, planId);
    final String[] endpointParts = endpoint.getURI().toString().split("/");

    if (endpointParts.length < 2) {
      LOG.error("Unable to parse process definition ID for plan {} out of endpoint {}", planId, endpoint.getURI());
      return false;
    }

    final String processDefinitionID = endpointParts[endpointParts.length - 2];
    LOG.debug("Extracted following process definition ID: {}", processDefinitionID);

    try (final CloseableHttpClient httpClient = HttpClients.createDefault()) {
      // get information for process definition to extract related deployment ID
      final HttpGet getProcessDefinition = new HttpGet(Settings.ENGINE_PLAN_BPMN_URL + PROCESS_DEFINITION_SUFFIX
        + "/" + processDefinitionID);
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
      final URIBuilder uriBuilder = new URIBuilder(Settings.ENGINE_PLAN_BPMN_URL + DEPLOYMENT_SUFFIX + "/" + deploymentID);
      uriBuilder.setParameter("cascade", "true");
      final HttpDelete deleteDeployment = new HttpDelete(uriBuilder.build());
      final HttpResponse deletionResponse = httpClient.execute(deleteDeployment);

      // check success and reutrn to caller
      if (deletionResponse.getStatusLine().getStatusCode() == 204) {
        LOG.debug("Deletion of plan deployment successful.");
        return true;
      } else {
        LOG.error("Deletion response returned invalid status code {}",
          deletionResponse.getStatusLine().getStatusCode());
        return false;
      }
    } catch (final IOException e){
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
    if (!artifact.getFileName().toString().endsWith(".war")) {
      LOG.debug("Plan reference is not a WAR file. It was '{}'.", artifact.getFileName());
      return null;
    }
    return artifact;
  }

  private URI searchForEndpoint(final String planName) throws URISyntaxException {
    URI endpointURI;
    LOG.debug("Search for Plan Endpoint");

    final String processDefinitions = "http://localhost:8080/engine-rest/process-definition/";

    HttpResponse response;
    String output = null;

    LOG.debug("Retrieve list of deployed plans");
    try {
      response = httpService.Get(processDefinitions);
      output = EntityUtils.toString(response.getEntity(), "UTF-8");
      output = output.substring(1, output.length() - 1);
    } catch (final IOException e) {
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
