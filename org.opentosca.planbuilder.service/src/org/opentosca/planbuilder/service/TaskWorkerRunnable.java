package org.opentosca.planbuilder.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.ws.rs.core.Response;
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
import org.opentosca.planbuilder.service.model.PlanGenerationState;
import org.opentosca.planbuilder.service.model.PlanGenerationState.PlanGenerationStates;
import org.opentosca.util.http.service.IHTTPService;

/**
 * Copyright 2015 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class TaskWorkerRunnable implements Runnable {
	
	private PlanGenerationState state;
	
	
	public TaskWorkerRunnable(PlanGenerationState state) {
		this.state = state;
	}
	
	@Override
	public void run() {
		this.state.currentState = PlanGenerationState.PlanGenerationStates.CSARDOWNLOADING;
		// download csar
		IHTTPService openToscaHttpService = ServiceRegistry.getHTTPService();
		
		if (openToscaHttpService == null) {
			this.state.currentState = PlanGenerationStates.CSARDOWNLOADFAILED;
			this.state.currentMessage = "Couldn't aquire internal HTTP Service to download CSAR";
			return;
		}
		
		InputStream csarInputStream = null;
		try {
			HttpResponse csarResponse = openToscaHttpService.Get(this.state.getCsarUrl().toString());
			csarInputStream = csarResponse.getEntity().getContent();
		} catch (ClientProtocolException e) {
			this.state.currentState = PlanGenerationStates.CSARDOWNLOADFAILED;
			this.state.currentMessage = "Couldn't download CSAR";
			return;
		} catch (IOException e) {
			this.state.currentState = PlanGenerationStates.CSARDOWNLOADFAILED;
			this.state.currentMessage = "Couldn't download CSAR";
			return;
		}
		
		if (csarInputStream == null) {
			this.state.currentState = PlanGenerationStates.CSARDOWNLOADFAILED;
			this.state.currentMessage = "Couldn't download CSAR";
			return;
		}
		
		this.state.currentState = PlanGenerationStates.CSARDOWNLOADED;
		this.state.currentMessage = "Downloaded CSAR";
		
		// generate plan (assumption: the send csar contains only one
		// topologytemplate => only one buildPlan will be generated
		CSARID csarId = this.storeCSAR(System.currentTimeMillis() + ".csar", csarInputStream);
		
		if (csarId != null) {
			this.state.currentState = PlanGenerationStates.PLANGENERATING;
			this.state.currentMessage = "Generating Plan";
		} else {
			this.state.currentState = PlanGenerationStates.CSARDOWNLOADFAILED;
			this.state.currentMessage = "Couldn't download CSAR";
			this.deleteCSAR(csarId);
			return;
		}
		
		List<BuildPlan> buildPlans = this.startPlanBuilder(csarId);
		
		if (buildPlans.size() <= 0) {
			this.state.currentState = PlanGenerationStates.PLANGENERATIONFAILED;
			this.state.currentMessage = "No plans could be generated";
			this.deleteCSAR(csarId);
			return;
		}
		
		// write to tmp dir
		File planTmpFile = this.writePlan2TmpFolder(buildPlans.get(0));
		
		this.state.currentState = PlanGenerationStates.PLANGENERATED;
		this.state.currentMessage = "Stored and generated Plan";
		
		// send plan back
		FileBody bin = new FileBody(planTmpFile);
		MultipartEntity mpEntity = new MultipartEntity();
		ContentBody cb = (ContentBody) bin;
		mpEntity.addPart("planfile", cb);
		
		try {
			
			this.state.currentState = PlanGenerationStates.PLANSENDING;
			this.state.currentMessage = "Sending Plan";
			
			HttpResponse uploadResponse = openToscaHttpService.Post(this.state.getPostUrl().toString(), mpEntity);
			if (uploadResponse.getStatusLine().getStatusCode() >= 300) {
				// we assume ,if the status code ranges from 300 to 5xx , an
				// error happend
				this.state.currentState = PlanGenerationStates.PLANSENDINGFAILED;
				this.state.currentMessage = "Couldn't send plan. Server send status " + uploadResponse.getStatusLine().getStatusCode();
				this.deleteCSAR(csarId);
				return;
			} else {
				this.state.currentState = PlanGenerationStates.PLANSENT;
				this.state.currentMessage = "Sent plan. Everythings okay";
			}
		} catch (ClientProtocolException e) {
			this.state.currentState = PlanGenerationStates.PLANSENDINGFAILED;
			this.state.currentMessage = "Couldn't send plan.";
			this.deleteCSAR(csarId);
			return;
		} catch (IOException e) {
			this.state.currentState = PlanGenerationStates.PLANSENDINGFAILED;
			this.state.currentMessage = "Couldn't send plan.";
			this.deleteCSAR(csarId);
			return;
		}
	}
	
	private void deleteCSAR(CSARID csarId) {
		try {
			ServiceRegistry.getCoreFileService().deleteCSAR(csarId);
		} catch (SystemException e) {
			e.printStackTrace();
		} catch (UserException e) {
			e.printStackTrace();
		}
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
}
