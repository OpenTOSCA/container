package org.opentosca.container.api.controller;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.opentosca.container.api.planbuilder.PlanbuilderWorker;
import org.opentosca.container.api.planbuilder.RunningTasks;
import org.opentosca.container.api.planbuilder.model.GeneratePlanForTopology;
import org.opentosca.container.api.planbuilder.model.PlanGenerationState;
import org.opentosca.container.core.service.CsarStorageService;
import org.opentosca.container.core.service.IHTTPService;
import org.opentosca.planbuilder.importer.Importer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Copyright 2015 IAAS University of Stuttgart <br>
 * <br>
 * <p>
 * PlanBuilder Service RESTful Rootresource
 * </p>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
@Path("planbuilder")
@Component
@NonNullByDefault
public class PlanbuilderController {

    private static final ExecutorService backgroundWorker = Executors.newCachedThreadPool(r -> new Thread(r, "planbuilder-api-worker"));
    private static final Logger LOG = LoggerFactory.getLogger(PlanbuilderController.class);
    private final Importer importer;
    private final IHTTPService httpService;
    @Context
    UriInfo uriInfo;
    @Inject
    private CsarStorageService csarStorage;

    @Inject
    public PlanbuilderController(Importer importer, IHTTPService httpService) {
        this.httpService = httpService;
        this.importer = importer;
        //csarStorage = new CsarStorageServiceImpl(Settings.CONTAINER_STORAGE_BASEPATH.resolveSibling("planbuilder-application"));
    }

    @GET
    @Produces("text/html")
    public Response getRootPage() {
        return Response.ok("<html><body><h1>Hello to the PlanBuilder Service.</h1> <h2>To use the PlanBuilder Service send a POST Request with the following example body:</h2><textarea style=\"width:auto;height:auto;min-width:300px;min-height:200px\"> <generatePlanForTopology><CSARURL>http://<url-to-csar-file></CSARURL><PLANPOSTURL>http://<url-for-sending-plan-back-with-POST></PLANPOSTURL></generatePlanForTopology></textarea></body></html>")
            .build();
    }

    @Path("async/{taskId}")
    @GET
    @Produces("application/xml")
    public Response getTask(@PathParam("taskId") final String taskId) {
        if (RunningTasks.exists(taskId)) {
            return Response.ok(RunningTasks.get(taskId)).build();
        } else {
            return Response.status(Status.NOT_FOUND).build();
        }
    }

    @POST
    @Consumes("application/xml")
    @Produces("application/xml")
    @Path("async")
    public Response generateBuildPlanAsync(final GeneratePlanForTopology generatePlanForTopology) {
        final URL csarURL;
        final URL planPostURL;
        try {
            csarURL = new URL(generatePlanForTopology.CSARURL);
            planPostURL = new URL(generatePlanForTopology.PLANPOSTURL);
        } catch (final MalformedURLException e) {
            LOG.info("Failed to create csarURl or planPostURL for async build plan", e);
            return Response.status(Status.BAD_REQUEST).entity(e).build();
        }
        final PlanGenerationState newTaskState = new PlanGenerationState(csarURL, planPostURL);
        final String newId = RunningTasks.putSafe(newTaskState);
        LOG.info("Enqueueing PlanbuilderWorker for CsarUrl {} and planUrl {} with id [{}]", csarURL, planPostURL, newId);
        backgroundWorker.execute(new PlanbuilderWorker(newTaskState, httpService, csarStorage, importer)::doWork);
        return Response.created(URI.create(this.uriInfo.getAbsolutePath() + "/" + newId)).build();
    }

    /**
     * <p>
     * Given the paramaters CSARURL and PLANPOSTURL in the request, this method does the following: <br> - Check whether
     * the given parameters are URL's<br> - Download the CSAR denoted by the CSARURL parameter<br> - Load the CSAR into
     * the OpenTOSCA Core <br> - Generate BuildPlans for the given CSAR <br> - Send the the first generated BuildPlan to
     * the given PLANPOSTURL using a HTTP POST
     * </p>
     *
     * @param generatePlanForTopology a wrapper class for the parameters CSARURL and PLANPOSTURL
     * @return a HTTP Response appropriate to the situation (e.g. error, success,..)
     */
    @POST
    @Consumes("application/xml")
    @Produces("application/xml")
    @Path("sync")
    public Response generateBuildPlanSync(final GeneratePlanForTopology generatePlanForTopology) {
        final URL csarURL;
        final URL planPostURL;
        try {
            csarURL = new URL(generatePlanForTopology.CSARURL);
            planPostURL = new URL(generatePlanForTopology.PLANPOSTURL);
        } catch (final MalformedURLException e) {
            LOG.info("Failed to create csarURl or planPostURL for sync build plan", e);
            return Response.status(Status.BAD_REQUEST).entity(e).build();
        }

        final PlanGenerationState newTaskState = new PlanGenerationState(csarURL, planPostURL);
        final String newId = RunningTasks.putSafe(newTaskState);

        final PlanbuilderWorker worker = new PlanbuilderWorker(newTaskState, httpService, csarStorage, importer);
        LOG.info("Synchronously Running PlanbuilderWorker for CsarUrl {} and planUrl {} with id [{}]", csarURL, planPostURL, newId);
        worker.doWork();

        // if the worker doWork is finished, we're either in a failed state or everything worked
        switch (worker.getState().currentState) {
            case CSARDOWNLOADFAILED:
            case PLANGENERATIONFAILED:
            case PLANSENDINGFAILED:
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(worker.getState()).build();
            default:
                return Response.ok().entity(worker.getState()).build();
        }
    }
}
