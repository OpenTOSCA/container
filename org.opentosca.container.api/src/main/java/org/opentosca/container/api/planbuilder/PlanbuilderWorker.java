package org.opentosca.container.api.planbuilder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TPlan;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicHeader;
import org.opentosca.container.api.planbuilder.Util.SelfServiceOptionWrapper;
import org.opentosca.container.api.planbuilder.model.PlanGenerationState;
import org.opentosca.container.api.planbuilder.model.PlanGenerationState.PlanGenerationStates;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.service.CsarStorageService;
import org.opentosca.container.core.service.IHTTPService;
import org.opentosca.planbuilder.importer.Importer;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The worker process instance for one single plan generation.
 * <br>
 * Copyright 2015 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public class PlanbuilderWorker {

    final private static Logger LOG = LoggerFactory.getLogger(PlanbuilderWorker.class);

    private final PlanGenerationState state;
    private final IHTTPService httpService;
    private final CsarStorageService csarStorage;
    private final Importer planBuilderImporter;

    public PlanbuilderWorker(final PlanGenerationState state, IHTTPService httpService, CsarStorageService csarStorage,
                             Importer importer) {
        this.state = state;
        this.httpService = httpService;
        this.csarStorage = csarStorage;
        this.planBuilderImporter = importer;
    }

    public PlanGenerationState getState() {
        return state;
    }

    public void doWork() {
        if (csarStorage == null || httpService == null) {
            LOG.error("Required services for planbuilder worker are not available. Aborting invocation");
            state.currentMessage = "Services were not avaiable. Invocation failed";
            state.currentState = PlanGenerationStates.FAILED;
        }

        LOG.debug("Starting to download CSAR");
        state.currentState = PlanGenerationState.PlanGenerationStates.CSARDOWNLOADING;
        LOG.debug("Downloading CSAR " + state.getCsarUrl());

        CsarId csarId = null;
        try {
            final HttpResponse csarResponse = httpService.Get(state.getCsarUrl().toString(), Collections.singletonMap("Accept", "application/zip"));
            final InputStream csarInputStream = csarResponse.getEntity().getContent();

            if (csarInputStream == null) {
                state.currentState = PlanGenerationStates.CSARDOWNLOADFAILED;
                state.currentMessage = "Couldn't download CSAR";
                LOG.error("Couldn't download CSAR");
                return;
            }

            String fileName = null;
            for (final org.apache.http.Header header : csarResponse.getAllHeaders()) {
                if (!header.getName().contains("Content-Disposition")) {
                    continue;
                }
                for (final HeaderElement elem : header.getElements()) {
                    if (!elem.getName().equals("attachment")) {
                        continue;
                    }
                    for (final NameValuePair nameValuePair : elem.getParameters()) {
                        if (nameValuePair.getName().equals("filename")) {
                            fileName = nameValuePair.getValue();
                        }
                    }
                }
            }

            if (fileName == null) {
                // robustness hack (*g*)
                fileName = state.getCsarUrl().toString().replace("?csar", "");
                if (fileName.endsWith("/")) {
                    fileName = fileName.substring(0, fileName.length() - 1);
                }
                fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
            }

            state.currentState = PlanGenerationStates.CSARDOWNLOADED;
            state.currentMessage = "Downloaded CSAR";
            LOG.debug("CSAR download finished");

            if (fileName == null) {
                LOG.debug("CSAR Filename couldn't be determined");
                state.currentState = PlanGenerationStates.CSARDOWNLOADFAILED;
                state.currentMessage = "CSAR Filename couldn't be determined";
                return;
            }

            fileName = fileName.replace(".csar", "") + ".planbuilder" + System.currentTimeMillis() + ".csar";
            // generate plan (assumption: the send csar contains only one topologytemplate => only one buildPlan will be generated)
            LOG.debug("Storing CSAR");

            Path tempCsarLocation = csarStorage.storeCSARTemporarily(fileName, csarInputStream);
            csarId = csarStorage.storeCSAR(tempCsarLocation);
        } catch (final IOException|SystemException|UserException e) {
            state.currentState = PlanGenerationStates.CSARDOWNLOADFAILED;
            state.currentMessage = "Couldn't download CSAR";
            LOG.error("Couldn't download CSAR");
            return;
        }

        if (csarId == null) {
            state.currentState = PlanGenerationStates.CSARDOWNLOADFAILED;
            state.currentMessage = "Couldn't store CSAR";
            LOG.error("Couldn't store CSAR");
            forceDelete(csarId);
            return;
        }

        state.currentState = PlanGenerationStates.PLANGENERATING;
        state.currentMessage = "Generating Plan";
        LOG.debug("Starting to generate Plan");

        final List<AbstractPlan> buildPlans = planBuilderImporter.generatePlans(csarId);

        if (buildPlans.size() <= 0) {
            state.currentState = PlanGenerationStates.PLANGENERATIONFAILED;
            state.currentMessage = "No plans could be generated";
            forceDelete(csarId);
            LOG.error("No plans could be generated");
            return;
        }

        state.currentState = PlanGenerationStates.PLANSGENERATED;
        state.currentMessage = "Stored and generated Plans";
        LOG.debug("Stored and generated Plans");

        final Map<BPELPlan, File> plansToUpload = new HashMap<>();

        for (final AbstractPlan buildPlan : buildPlans) {
            final File planTmpFile = Util.writePlan2TmpFolder((BPELPlan) buildPlan);
            plansToUpload.put((BPELPlan) buildPlan, planTmpFile);
        }

        LOG.debug("Plans to upload: " + buildPlans.size());

        for (final AbstractPlan buildPlan : plansToUpload.keySet()) {

            // write to tmp dir, only generating one plan
            final File planTmpFile = plansToUpload.get(buildPlan);


            final List<String> inputParameters = ((BPELPlan) buildPlan).getWsdl().getInputMessageLocalNames();
            final List<String> outputParameters = ((BPELPlan) buildPlan).getWsdl().getOuputMessageLocalNames();

            TPlan plan = new TPlan();


            final JsonObject obj = new JsonObject();
            obj.addProperty("name", QName.valueOf(buildPlan.getId()).getLocalPart());
            obj.addProperty("id", QName.valueOf(buildPlan.getId()).getLocalPart());
            obj.addProperty("planType", buildPlan.getType().toString());
            obj.addProperty("planLanguage", BPELPlan.bpelNamespace);

            JsonArray inputParamList = new JsonArray();
            createParameters(inputParameters).forEach(inputParamList::add);

            JsonObject inputParametersJson = new JsonObject();
            inputParametersJson.add("inputParameter",inputParamList);
            obj.add("inputParameters", inputParametersJson);

            JsonArray outputParamList = new JsonArray();
            createParameters(outputParameters).forEach(outputParamList::add);

            JsonObject outputParametersJson = new JsonObject();
            outputParametersJson.add("outputParameter", outputParamList);
            obj.add("outputParameters", outputParametersJson);

            System.out.println(obj.toString());

            plan.setId(QName.valueOf(buildPlan.getId()).getLocalPart());
            plan.setName(QName.valueOf(buildPlan.getId()).getLocalPart());
            plan.setPlanType(buildPlan.getType().toString());
            plan.setPlanLanguage(BPELPlan.bpelNamespace);

            // TODO INPUT AND OUTPUT PARAMS


            final HttpEntity ent =
               // EntityBuilder.create().setSerializable(plan).setContentType(ContentType.APPLICATION_JSON).build();
                EntityBuilder.create().setText(obj.toString()).setContentType(ContentType.APPLICATION_JSON).build();



            HttpResponse createPlanResponse = null;
            try {
                createPlanResponse = httpService.Post(getState().getPostUrl().toString(), ent,
                    new BasicHeader("Accept", "application/json"),
                    new BasicHeader("Content-Type", "application/json"));
            } catch (final Exception e) {
                state.currentState = PlanGenerationStates.PLANSENDINGFAILED;
                state.currentMessage =
                    "Couldn't send plan. Server send status " + createPlanResponse.getStatusLine().getStatusCode();
                LOG.error("[{}] {}", state.currentState, state.currentMessage);
                forceDelete(csarId);
                return;
            }

            if(createPlanResponse.getStatusLine().getStatusCode() >= 300){
                state.currentState = PlanGenerationStates.PLANSENDINGFAILED;
                state.currentMessage =
                    "Couldn't send plan. Server send status " + createPlanResponse.getStatusLine();
                LOG.error("[{}] {}", state.currentState, state.currentMessage);
                forceDelete(csarId);
                return;
            }

            try{
                String response = IOUtils.toString(createPlanResponse.getEntity().getContent(),"UTF-8");
                System.out.println(response);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String planLocation = state.getPostUrl() + "/" + QName.valueOf(buildPlan.getId()).getLocalPart();

            try {
                state.currentState = PlanGenerationStates.PLANSENDING;
                state.currentMessage = "Sending Plan";
                LOG.debug("Sending Plan");

                // send file
                final FileBody bin = new FileBody(planTmpFile);
                final ContentBody cb = bin;
                final MultipartEntityBuilder mpEntity = MultipartEntityBuilder.create();
                mpEntity.addPart("file", cb);

                final HttpResponse uploadResponse = httpService.Put(planLocation + "/file", mpEntity.build());
                if (uploadResponse.getStatusLine().getStatusCode() >= 300) {
                    // we assume ,if the status code ranges from 300 to 5xx , that
                    // an error occured
                    state.currentState = PlanGenerationStates.PLANSENDINGFAILED;
                    state.currentMessage =
                        "Couldn't send plan. Server send status " + uploadResponse.getStatusLine().getStatusCode();
                    forceDelete(csarId);
                    LOG.error("Couldn't send plan. Server send status "
                        + uploadResponse.getStatusLine().getStatusCode());
                    return;
                }

                LOG.debug("Starting to send Options");
                state.currentState = PlanGenerationStates.OPTIONSENDING;
                state.currentMessage = "Sending SelfService Option";

                try {
                    final URL optionsUrl = new URL(state.getCsarUrl(), "selfserviceportal/options/");
                    LOG.debug("Sending options to " + optionsUrl.toString());

                    final SelfServiceOptionWrapper option = Util.generateSelfServiceOption((BPELPlan) buildPlan);
                    LOG.debug("Sending the following option: " + option.toString());

                    // send plan back
                    final MultipartEntityBuilder multipartBuilder = MultipartEntityBuilder.create();
                    try {
                        multipartBuilder.addPart("name",
                            new StringBody(option.option.getName(), ContentType.TEXT_PLAIN));
                        multipartBuilder.addPart("description",
                            new StringBody(option.option.getDescription(), ContentType.TEXT_PLAIN));
                        multipartBuilder.addPart("planServiceName",
                            new StringBody(option.option.getPlanServiceName(), ContentType.TEXT_PLAIN));
                        multipartBuilder.addPart("planInputMessage",
                            new StringBody(FileUtils.readFileToString(option.planInputMessageFile), ContentType.TEXT_PLAIN));
                    } catch (final UnsupportedEncodingException e1) {
                        state.currentState = PlanGenerationStates.OPTIONSENDINGFAILED;
                        state.currentMessage = "Couldn't generate option to send to winery";
                        forceDelete(csarId);
                        LOG.error("Couldn't generate option request to " + optionsUrl.toString());
                        return;
                    }

                    // TODO here we should send a default image, instead of the message..
                    final FileBody fileBody = new FileBody(option.planInputMessageFile);
                    final ContentBody contentBody = fileBody;
                    multipartBuilder.addPart("file", contentBody);

                    final HttpResponse optionsResponse = httpService.Post(optionsUrl.toString(), multipartBuilder.build());

                    if (optionsResponse.getStatusLine().getStatusCode() >= 300) {
                        // we assume ,if the status code ranges from 300 to 5xx , that an error occured
                        state.currentState = PlanGenerationStates.OPTIONSENDINGFAILED;
                        state.currentMessage = "Couldn't send option to winery. Response: \n  StatusCode: "
                            + optionsResponse.getStatusLine().getStatusCode() + " \n Reason Phrase: \n"
                            + optionsResponse.getStatusLine().getReasonPhrase();
                        forceDelete(csarId);
                        return;
                    } else {
                        state.currentState = PlanGenerationStates.OPTIONSENT;
                        state.currentMessage = "Sent option. Everythings okay.";
                    }
                } catch (final IOException e) {
                    state.currentState = PlanGenerationStates.OPTIONSENDINGFAILED;
                    state.currentMessage = "Couldn't send option to winery.";
                    forceDelete(csarId);
                    return;
                }

                state.currentState = PlanGenerationStates.PLANSSENT;
                state.currentMessage = "Sent plan.";
                LOG.debug("Sent plan.");
            } catch (final IOException e) {
                state.currentState = PlanGenerationStates.PLANSENDINGFAILED;
                state.currentMessage = "Couldn't send plan.";
                forceDelete(csarId);
                LOG.error("Couldn't send plan.");
                return;
            }
        }
        state.currentState = PlanGenerationStates.FINISHED;
        state.currentMessage = "Plans where successfully sent.";
        forceDelete(csarId);
    }

    private void forceDelete(CsarId csarId) {
        try {
            csarStorage.deleteCSAR(csarId);
        } catch (UserException | SystemException e) {
            LOG.warn("Failed to delete csar {} for planbuilder worker [{}:{}] with exception", csarId.csarName(), state.currentState, state.currentMessage, e);
        }
    }

    private List<JsonObject> createParameters(final List<String> parameters) {
        return parameters.stream().map(p -> {
            JsonObject paramObject = new JsonObject();
            paramObject.addProperty("name", p);
            paramObject.addProperty("type", "xsd:string");
            paramObject.addProperty("required", "NO");
            return paramObject;
        })
            .collect(Collectors.toList());
    }
}
