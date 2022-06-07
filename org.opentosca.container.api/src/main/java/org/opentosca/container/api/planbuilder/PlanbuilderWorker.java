package org.opentosca.container.api.planbuilder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TExportedInterface;
import org.eclipse.winery.model.tosca.TExportedOperation;
import org.eclipse.winery.model.tosca.TParameter;
import org.eclipse.winery.model.tosca.TPlan;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.message.BasicHeader;
import org.opentosca.container.api.planbuilder.model.PlanGenerationState;
import org.opentosca.container.api.planbuilder.model.PlanGenerationState.PlanGenerationStates;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.next.model.PlanLanguage;
import org.opentosca.container.core.service.CsarStorageService;
import org.opentosca.container.core.service.IHTTPService;
import org.opentosca.planbuilder.importer.Importer;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNPlan;
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
        Csar csar = null;
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

            if (fileName.isBlank()) {
                LOG.debug("CSAR Filename couldn't be determined");
                state.currentState = PlanGenerationStates.CSARDOWNLOADFAILED;
                state.currentMessage = "CSAR Filename couldn't be determined";
                return;
            }

            fileName = fileName.replace(".csar", "") + ".planbuilder" + System.currentTimeMillis() + ".csar";
            // generate plan (assumption: the csar send contains only one topologyTemplate => only one buildPlan will be generated)
            LOG.debug("Storing CSAR");

            Path tempCsarLocation = csarStorage.storeCSARTemporarily(fileName, csarInputStream);
            csarId = csarStorage.storeCSAR(tempCsarLocation);
            csar = csarStorage.findById(csarId);
        } catch (final IOException | SystemException | UserException e) {
            state.currentState = PlanGenerationStates.CSARDOWNLOADFAILED;
            state.currentMessage = "Couldn't download CSAR";
            LOG.error("Couldn't download CSAR");
            return;
        }

        state.currentState = PlanGenerationStates.PLANGENERATING;
        state.currentMessage = "Generating Plan";
        LOG.debug("Starting to generate Plan");

        final List<AbstractPlan> buildPlans = planBuilderImporter.generatePlans(csar);

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

        final Map<AbstractPlan, File> plansToUpload = new HashMap<>();

        for (final AbstractPlan buildPlan : buildPlans) {
            final File planTmpFile;
            if (buildPlan.getLanguage() != null && buildPlan.getLanguage() == PlanLanguage.BPMN) {
                planTmpFile = Util.writeBPMNPlan2TmpFolder((BPMNPlan) buildPlan);
            } else {
                planTmpFile = Util.writePlan2TmpFolder((BPELPlan) buildPlan);
            }
            plansToUpload.put(buildPlan, planTmpFile);
        }

        LOG.debug("Plans to upload: " + buildPlans.size());

        ArrayList<TExportedInterface> serviceTemplateInterfaces = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        for (final AbstractPlan plan : plansToUpload.keySet()) {
            // write to tmp dir, only generating one plan
            final File planTmpFile = plansToUpload.get(plan);

            final List<String> inputParameters;
            final List<String> outputParameters;

            if (plan.getLanguage() != null && plan.getLanguage() == PlanLanguage.BPMN) {
                inputParameters = new ArrayList(((BPMNPlan) plan).getInputParameters());
                outputParameters = new ArrayList(((BPMNPlan) plan).getOutputParameters());
            } else {
                inputParameters = ((BPELPlan) plan).getWsdl().getInputMessageLocalNames();
                outputParameters = ((BPELPlan) plan).getWsdl().getOuputMessageLocalNames();
            }

            String planId = QName.valueOf(plan.getId()).getLocalPart();
            TPlan tPlan = new TPlan.Builder(planId, plan.getType().toString(),
                (plan.getLanguage() == PlanLanguage.BPMN) ? BPMNPlan.bpmnNamespace
                    : BPELPlan.bpelNamespace)
                .setName(planId)
                .setInputParameters(
                    inputParameters.stream()
                        .map(input -> new TParameter.Builder(input, "xsd:string").build())
                        .collect(Collectors.toList()))
                .setOutputParameters(
                    outputParameters.stream()
                        .map(output -> new TParameter.Builder(output, "xsd:string").build())
                        .collect(Collectors.toList())
                )
                .build();

            final HttpEntity ent;
            try {
                ent = EntityBuilder.create()
                    .setText(objectMapper.writeValueAsString(tPlan))
                    .setContentType(ContentType.APPLICATION_JSON)
                    .build();
            } catch (JsonProcessingException e) {
                LOG.error("Could not create json entity to create plan in Winery!", e);
                forceDelete(csarId);
                return;
            }

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

            if (createPlanResponse.getStatusLine().getStatusCode() >= 300) {
                state.currentState = PlanGenerationStates.PLANSENDINGFAILED;
                state.currentMessage =
                    "Couldn't send plan. Server send status " + createPlanResponse.getStatusLine();
                LOG.error("[{}] {}", state.currentState, state.currentMessage);
                forceDelete(csarId);
                return;
            }

            String planLocation = state.getPostUrl() + "/" + planId;

            try {
                state.currentState = PlanGenerationStates.PLANSENDING;
                state.currentMessage = "Sending Plan";
                LOG.debug("Sending Plan");

                // send file
                final ContentBody cb = new FileBody(planTmpFile);
                final MultipartEntityBuilder mpEntity = MultipartEntityBuilder.create();
                mpEntity.addPart("file", cb);

                final HttpResponse uploadResponse = httpService.Put(planLocation + "/file", mpEntity.build());
                if (uploadResponse.getStatusLine().getStatusCode() >= 300) {
                    // we assume if the status code ranges from 300 to 5xx , that an error occurred
                    state.currentState = PlanGenerationStates.PLANSENDINGFAILED;
                    state.currentMessage =
                        "Couldn't send plan. Server send status " + uploadResponse.getStatusLine().getStatusCode();
                    forceDelete(csarId);
                    LOG.error("Couldn't send plan. Server send status "
                        + uploadResponse.getStatusLine().getStatusCode());
                    return;
                }

                state.currentState = PlanGenerationStates.PLANSSENT;
                state.currentMessage = "Sent plan.";
                LOG.debug("Sent plan.");

                LOG.debug("Adding interface and operation to the list of the ServiceTemplate");
                TExportedOperation.Plan exportedPlan = new TExportedOperation.Plan();
                exportedPlan.setPlanRef(planId);
                TExportedOperation exportedOperation = new TExportedOperation(plan.getTOSCAOperationName());
                exportedOperation.setPlan(exportedPlan);

                Optional<TExportedInterface> exportedInterfaceOptional = serviceTemplateInterfaces.stream()
                    .filter(iFace -> iFace.getName().equals(plan.getTOSCAInterfaceName()))
                    .findFirst();
                if (exportedInterfaceOptional.isPresent()) {
                    exportedInterfaceOptional.get().getOperation().add(exportedOperation);
                } else {
                    serviceTemplateInterfaces.add(
                        // DO NOT use only List.of() as it creates an unmodifiable list!
                        new TExportedInterface(plan.getTOSCAInterfaceName(), new ArrayList<>(List.of(exportedOperation)))
                    );
                }
            } catch (final IOException e) {
                state.currentState = PlanGenerationStates.PLANSENDINGFAILED;
                state.currentMessage = "Couldn't send plan.";
                forceDelete(csarId);
                LOG.error("Couldn't send plan.");
                return;
            }
        }

        LOG.debug("Send mapping of operations and plans to Winery...");
        try {
            HttpResponse createPlanResponse = httpService.Post(
                new URL(state.getCsarUrl(), "boundarydefinitions/interfaces/").toString(),
                EntityBuilder.create()
                    .setText(objectMapper.writeValueAsString(serviceTemplateInterfaces))
                    .setContentType(ContentType.APPLICATION_JSON)
                    .build(),
                new BasicHeader("Accept", "application/json"),
                new BasicHeader("Content-Type", "application/json")
            );

            if (createPlanResponse.getStatusLine().getStatusCode() > 300) {
                LOG.error("Posting interface information to Winery was not successful!\n{}", createPlanResponse);
            }
        } catch (IOException e) {
            LOG.error("Could not send operations to plans mapping to Winery...");
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
}
