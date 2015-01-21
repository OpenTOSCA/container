package org.opentosca.planbuilder.service.resources;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
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
		System.out.println("Tasks in the system");
		for (String id : RunningTasks.tasks.keySet()) {
			System.out.println("Task Id: " + id);
		}
		
		if (RunningTasks.tasks.containsKey(taskId)) {
			System.out.println("Found task");
			return new TaskResource(RunningTasks.tasks.get(taskId));
		} else {
			System.out.println("No task with given id found");
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
			return Response.status(Status.BAD_REQUEST).entity(this.getStacktrace(e)).build();
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
	 * @return a HTTP Response appropiate to the situation (e.g. error,
	 *         success,..)
	 */
	@POST
	@Consumes("application/xml")
	@Produces("application/xml")
	@Path("sync")
	public Response generateBuildPlan(GeneratePlanForTopology generatePlanForTopology) {
		URL csarURL = null;
		URL planPostURL = null;
		
		try {
			csarURL = new URL(generatePlanForTopology.CSARURL);
			planPostURL = new URL(generatePlanForTopology.PLANPOSTURL);
		} catch (MalformedURLException e) {
			return Response.status(Status.BAD_REQUEST).entity(this.getStacktrace(e)).build();
		}
		
		// download csar
		IHTTPService openToscaHttpService = ServiceRegistry.getHTTPService();
		
		if (openToscaHttpService == null) {
			return Response.status(Status.SERVICE_UNAVAILABLE).entity("Internal Service not available (HttpService)").build();
		}
		
		InputStream csarInputStream = null;
		try {
			HttpResponse csarResponse = openToscaHttpService.Get(csarURL.toString());
			csarInputStream = csarResponse.getEntity().getContent();
		} catch (ClientProtocolException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(this.getStacktrace(e)).build();
		} catch (IOException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(this.getStacktrace(e)).build();
		}
		
		if (csarInputStream == null) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Couldn't fetch CSAR from given URL: " + csarURL.toString()).build();
		}
		
		// generate plan (assumption: the send csar contains only one
		// topologytemplate => only one buildPlan will be generated
		CSARID csarId = this.storeCSAR(System.currentTimeMillis() + ".csar", csarInputStream);
		List<BuildPlan> buildPlans = this.startPlanBuilder(csarId);
		
		// write to tmp dir
		
		File planTmpFile = this.writePlan2TmpFolder(buildPlans.get(0));
		
		// send plan back
		FileBody bin = new FileBody(planTmpFile);
		MultipartEntity mpEntity = new MultipartEntity();
		ContentBody cb = (ContentBody) bin;
		mpEntity.addPart("planfile", cb);
		
		try {
			HttpResponse uploadResponse = openToscaHttpService.Post(planPostURL.toString(), mpEntity);
			if (uploadResponse.getStatusLine().getStatusCode() >= 300) {
				// we assume ,if the status code ranges from 300 to 5xx , an
				// error happend
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Couldn't upload plan to given URL: " + planPostURL.toString()).build();
			}
		} catch (ClientProtocolException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(this.getStacktrace(e)).build();
		} catch (IOException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(this.getStacktrace(e)).build();
		}
		
		try {
			ServiceRegistry.getCoreFileService().deleteCSAR(csarId);
		} catch (SystemException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(this.getStacktrace(e)).build();
		} catch (UserException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(this.getStacktrace(e)).build();
		}
		
		return Response.ok("<finished/>").build();
	}
	
	/**
	 * Stores the given InputStream under the given file name
	 * 
	 * @param fileName the file name to store the csar under
	 * @param uploadedInputStream an InputStream to the csar file to store
	 * @return the CSARID of the stored CSAR
	 */
	private CSARID storeCSAR(String fileName, InputStream uploadedInputStream) {
		File tmpDir = new File(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + Long.toString(System.currentTimeMillis()));
		tmpDir.mkdir();
		
		File uploadFile = new File(tmpDir.getAbsoluteFile() + System.getProperty("file.separator") + fileName);
		
		OutputStream out;
		
		try {
			out = new FileOutputStream(uploadFile);
			int read = 0;
			byte[] bytes = new byte[1024];
			
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			
			uploadedInputStream.close();
			
			out.flush();
			out.close();
			
			return ServiceRegistry.getCoreFileService().storeCSAR(uploadFile.toPath());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (UserException e) {
			e.printStackTrace();
			return null;
		} catch (SystemException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	/**
	 * Generates for the given CSAR (denoted by it's id) BuildPlans
	 * 
	 * @param csarId the Id of the CSAR to generate plans for
	 * @return a List of BuildPlans containing the generated BuildPlans
	 */
	private List<BuildPlan> startPlanBuilder(CSARID csarId) {
		Importer planBuilderImporter = new Importer();
		
		List<BuildPlan> buildPlans = planBuilderImporter.importDefs(csarId);
		
		return buildPlans;
	}
	
	/**
	 * Writes given BuildPlan to temporary folder.
	 * 
	 * @param buildPlan a BuildPlan
	 * @return a File denoting the export location
	 */
	private File writePlan2TmpFolder(BuildPlan buildPlan) {
		Exporter planBuilderExporter = new Exporter();
		File tmpDir = new File(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + Long.toString(System.currentTimeMillis()));
		tmpDir.mkdir();
		
		File uploadFile = new File(tmpDir.getAbsoluteFile() + System.getProperty("file.separator") + System.currentTimeMillis() + ".zip");
		
		try {
			planBuilderExporter.export(uploadFile.toURI(), buildPlan);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (JAXBException e) {
			e.printStackTrace();
			return null;
		}
		
		return uploadFile;
	}
	
	private String getStacktrace(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}
}
