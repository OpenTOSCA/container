package org.opentosca.planbuilder.service.resources;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.JAXBException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.exceptions.SystemException;
import org.opentosca.exceptions.UserException;
import org.opentosca.planbuilder.export.Exporter;
import org.opentosca.planbuilder.importer.Importer;
import org.opentosca.planbuilder.model.plan.BuildPlan;
import org.opentosca.planbuilder.service.Activator;
import org.opentosca.planbuilder.service.RunningTasks;
import org.opentosca.planbuilder.service.ServiceRegistry;
import org.opentosca.planbuilder.service.TaskWorkerRunnable;
import org.opentosca.planbuilder.service.Util;
import org.opentosca.planbuilder.service.model.GeneratePlanForTopology;
import org.opentosca.planbuilder.service.model.PlanGenerationState;
import org.opentosca.util.http.service.IHTTPService;

/**
 * 
 * Copyright 2015 IAAS University of Stuttgart <br>
 * <br>
 * <p>
 * PlanBuilder Service RESTful Rootresource
 * </p>
 * 
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
@Path("")
public class RootResource {
	
	@Context
	UriInfo uriInfo;
	
	
	@GET
	@Produces("text/html")
	public Response getRootPage() {
		return Response.ok("<html><body><h1>Hello to the PlanBuilder Service.</h1> <h2>To use the PlanBuilder Service send a POST Request with the following example body:</h2><textarea style=\"width:auto;height:auto;min-width:300px;min-height:200px\"> <generatePlanForTopology><CSARURL>http://<url-to-csar-file></CSARURL><PLANPOSTURL>http://<url-for-sending-plan-back-with-POST></PLANPOSTURL></generatePlanForTopology></textarea></body></html>").build();
	}
	
	@Path("async/{taskId}")
	public TaskResource getTask(@PathParam("taskId") String taskId) {
		if (RunningTasks.tasks.containsKey(taskId)) {
			return new TaskResource(RunningTasks.tasks.get(taskId));
		} else {
			return null;
		}
		
	}
	
	@POST
	@Consumes("application/xml")
	@Produces("application/xml")
	@Path("async")
	public Response generateBuildPlanAsync(GeneratePlanForTopology generatePlanForTopology) {
		
		URL csarURL = null;
		URL planPostURL = null;
		
		try {
			csarURL = new URL(generatePlanForTopology.CSARURL);
			planPostURL = new URL(generatePlanForTopology.PLANPOSTURL);
		} catch (MalformedURLException e) {
			return Response.status(Status.BAD_REQUEST).entity(Util.getStacktrace(e)).build();
		}
		
		PlanGenerationState newTaskState = new PlanGenerationState(csarURL, planPostURL);
		
		String newId = RunningTasks.generateId();
		RunningTasks.tasks.put(newId, newTaskState);
		
		new Thread(new TaskWorkerRunnable(newTaskState)).start();
		
		return Response.created(URI.create(this.uriInfo.getAbsolutePath() + "/" + newId)).build();
	}
	
	/**
	 * <p>
	 * Given the paramaters CSARURL and PLANPOSTURL in the request, this method
	 * does the following: <br>
	 * - Check whether the given parameters are URL's<br>
	 * - Download the CSAR denoted by the CSARURL parameter<br>
	 * - Load the CSAR into the OpenTOSCA Core <br>
	 * - Generate BuildPlans for the given CSAR <br>
	 * - Send the the first generated BuildPlan to the given PLANPOSTURL using a
	 * HTTP POST
	 * </p>
	 * 
	 * @param generatePlanForTopology a wrapper class for the parameters CSARURL
	 *            and PLANPOSTURL
	 * @return a HTTP Response appropriate to the situation (e.g. error,
	 *         success,..)
	 */
	@POST
	@Consumes("application/xml")
	@Produces("application/xml")
	@Path("sync")
	public Response generateBuildPlanSync(GeneratePlanForTopology generatePlanForTopology) {
		
		URL csarURL = null;
		URL planPostURL = null;
		
		try {
			csarURL = new URL(generatePlanForTopology.CSARURL);
			planPostURL = new URL(generatePlanForTopology.PLANPOSTURL);
		} catch (MalformedURLException e) {
			return Response.status(Status.BAD_REQUEST).entity(Util.getStacktrace(e)).build();
		}
		
		PlanGenerationState newTaskState = new PlanGenerationState(csarURL, planPostURL);
		
		String newId = RunningTasks.generateId();
		RunningTasks.tasks.put(newId, newTaskState);
		
		TaskWorkerRunnable worker = new TaskWorkerRunnable(newTaskState);
		
		worker.run();
		
		// if the worker run is finished, we're either in a failed state or
		// everything worked
		switch (worker.getState().currentState) {
		case CSARDOWNLOADFAILED:
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(worker.getState()).build();
		case PLANGENERATIONFAILED:
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(worker.getState()).build();
		case PLANSENDINGFAILED:
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(worker.getState()).build();
		default:
			return Response.ok().entity(worker.getState()).build();
		}
	}
}
