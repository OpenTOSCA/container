package org.opentosca.planbuilder.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.service.IHTTPService;
import org.opentosca.planbuilder.model.plan.TOSCAPlan;
import org.opentosca.planbuilder.service.Util.SelfServiceOptionWrapper;
import org.opentosca.planbuilder.service.model.PlanGenerationState;
import org.opentosca.planbuilder.service.model.PlanGenerationState.PlanGenerationStates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Copyright 2015 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class TaskWorkerRunnable implements Runnable {

	private final PlanGenerationState state;

	final private static Logger LOG = LoggerFactory.getLogger(TaskWorkerRunnable.class);


	public TaskWorkerRunnable(final PlanGenerationState state) {
		this.state = state;
	}

	public PlanGenerationState getState() {
		return this.state;
	}

	@Override
	public void run() {

		LOG.debug("Starting to download CSAR");
		this.state.currentState = PlanGenerationState.PlanGenerationStates.CSARDOWNLOADING;
		// download csar
		final IHTTPService openToscaHttpService = ServiceRegistry.getHTTPService();
		
		if (openToscaHttpService == null) {
			this.state.currentState = PlanGenerationStates.CSARDOWNLOADFAILED;
			this.state.currentMessage = "Couldn't aquire internal HTTP Service to download CSAR";
			LOG.error("Couldn't aquire internal HTTP Service to download CSAR");
			return;
		}
		CSARID csarId = null;
		InputStream csarInputStream = null;
		try {
			LOG.debug("Downloading CSAR " + this.state.getCsarUrl());
			final HttpResponse csarResponse = openToscaHttpService.Get(this.state.getCsarUrl().toString());
			csarInputStream = csarResponse.getEntity().getContent();

			String fileName = null;
			for (final org.apache.http.Header header : csarResponse.getAllHeaders()) {
				if (header.getName().contains("Content-Disposition")) {
					for (final HeaderElement elem : header.getElements()) {
						if (elem.getName().equals("attachment")) {
							for (final NameValuePair nameValuePair : elem.getParameters()) {
								if (nameValuePair.getName().equals("filename")) {
									fileName = nameValuePair.getValue();
								}
							}
						}
					}

				}
			}

			this.state.currentState = PlanGenerationStates.CSARDOWNLOADED;
			this.state.currentMessage = "Downloaded CSAR";
			LOG.debug("CSAR download finished");

			if (fileName == null) {
				LOG.debug("CSAR Filename couldn't be determined");
				this.state.currentState = PlanGenerationStates.CSARDOWNLOADFAILED;
				this.state.currentMessage = "CSAR Filename couldn't be determined";
				return;
			}

			fileName = fileName.replace(".csar", "") + ".planbuilder" + System.currentTimeMillis() + ".csar";
			// generate plan (assumption: the send csar contains only one
			// topologytemplate => only one buildPlan will be generated
			LOG.debug("Storing CSAR");
			csarId = Util.storeCSAR(fileName, csarInputStream);
		} catch (final ClientProtocolException e) {
			this.state.currentState = PlanGenerationStates.CSARDOWNLOADFAILED;
			this.state.currentMessage = "Couldn't download CSAR";
			LOG.error("Couldn't download CSAR");
			return;
		} catch (final IOException e) {
			this.state.currentState = PlanGenerationStates.CSARDOWNLOADFAILED;
			this.state.currentMessage = "Couldn't download CSAR";
			LOG.error("Couldn't download CSAR");
			return;
		}

		if (csarInputStream == null) {
			this.state.currentState = PlanGenerationStates.CSARDOWNLOADFAILED;
			this.state.currentMessage = "Couldn't download CSAR";
			LOG.error("Couldn't download CSAR");
			return;
		}

		if (csarId != null) {
			this.state.currentState = PlanGenerationStates.PLANGENERATING;
			this.state.currentMessage = "Generating Plan";
			LOG.debug("Starting to generate Plan");
		} else {
			this.state.currentState = PlanGenerationStates.CSARDOWNLOADFAILED;
			this.state.currentMessage = "Couldn't store CSAR";
			LOG.error("Couldn't store CSAR");
			Util.deleteCSAR(csarId);
			return;
		}
		
		List<TOSCAPlan> buildPlans = Util.startPlanBuilder(csarId);
		
		if (buildPlans.size() <= 0) {
			this.state.currentState = PlanGenerationStates.PLANGENERATIONFAILED;
			this.state.currentMessage = "No plans could be generated";
			Util.deleteCSAR(csarId);
			LOG.error("No plans could be generated");
			return;
		}

		// write to tmp dir, only generating one plan
		final File planTmpFile = Util.writePlan2TmpFolder(buildPlans.get(0));

		this.state.currentState = PlanGenerationStates.PLANGENERATED;
		this.state.currentMessage = "Stored and generated Plan";
		LOG.debug("Stored and generated Plan");

		// send plan back
		final MultipartEntity mpEntity = new MultipartEntity();

		try {
			mpEntity.addPart("planName", new StringBody(planTmpFile.getName()));
			mpEntity.addPart("planType", new StringBody("http://docs.oasis-open.org/tosca/ns/2011/12/PlanTypes/BuildPlan"));
			mpEntity.addPart("planLanguage", new StringBody("http://docs.oasis-open.org/wsbpel/2.0/process/executable"));
		} catch (final UnsupportedEncodingException e1) {
			this.state.currentState = PlanGenerationStates.PLANGENERATIONFAILED;
			this.state.currentMessage = "Couldn't generate Upload request to PLANPOSTURL";
			Util.deleteCSAR(csarId);
			LOG.error("Couldn't generate Upload request to PLANPOSTURL");
			return;
		}

		final FileBody bin = new FileBody(planTmpFile);
		final ContentBody cb = bin;
		mpEntity.addPart("file", cb);

		try {

			this.state.currentState = PlanGenerationStates.PLANSENDING;
			this.state.currentMessage = "Sending Plan";
			LOG.debug("Sending Plan");

			final HttpResponse uploadResponse = openToscaHttpService.Post(this.state.getPostUrl().toString(), mpEntity);
			if (uploadResponse.getStatusLine().getStatusCode() >= 300) {
				// we assume ,if the status code ranges from 300 to 5xx , that
				// an error occured
				this.state.currentState = PlanGenerationStates.PLANSENDINGFAILED;
				this.state.currentMessage = "Couldn't send plan. Server send status " + uploadResponse.getStatusLine().getStatusCode();
				Util.deleteCSAR(csarId);
				LOG.error("Couldn't send plan. Server send status " + uploadResponse.getStatusLine().getStatusCode());
				return;
			} else {
				// still need to send the parameters

				// fetch location header from plan upload response
				final org.apache.http.Header locationHeader = uploadResponse.getHeaders("Location")[0];
				String planLocation = "";

				if (locationHeader == null) {
					// no location header sent back, construct inputParam url
					planLocation = this.state.getPostUrl() + planTmpFile.getName();
				} else {
					planLocation = locationHeader.getValue();
				}
				
				TOSCAPlan buildPlan = buildPlans.get(0);
				/*
				 * http://localhost:8080/winery/servicetemplates/http%253A%252F%
				 * 252F example
				 * .com%252Fhello/HelloAppTest/plans/HelloAppTest_buildPlan
				 * .zip/inputparameters/?name=test&required=on&type=String *
				 */

				// post the parameters
				for (final String inputParam : buildPlan.getWsdl().getInputMessageLocalNames()) {
					final String inputParamPostUrl = planLocation + "/inputparameters/";

					final List<NameValuePair> params = new ArrayList<>();
					params.add(Util.createNameValuePair("name", inputParam));
					params.add(Util.createNameValuePair("type", "String"));
					params.add(Util.createNameValuePair("required", "on"));

					final UrlEncodedFormEntity encodedForm = new UrlEncodedFormEntity(params);

					final HttpResponse inputParamPostResponse = openToscaHttpService.Post(inputParamPostUrl, encodedForm);
					if (inputParamPostResponse.getStatusLine().getStatusCode() >= 300) {
						this.state.currentState = PlanGenerationStates.PLANSENDINGFAILED;
						this.state.currentMessage = "Couldn't set inputParameters. Setting InputParam (postURL: " + inputParamPostUrl + ") " + inputParam + " failed, Service for Plan Upload sent statusCode " + inputParamPostResponse.getStatusLine().getStatusCode();
						Util.deleteCSAR(csarId);
						LOG.error("Couldn't set inputParameters. Setting InputParam (postURL: " + inputParamPostUrl + ") " + inputParam + " failed, Service for Plan Upload sent statusCode " + inputParamPostResponse.getStatusLine().getStatusCode());
						return;
					}
					LOG.debug("Sent inputParameter " + inputParam);
				}

				for (final String outputParam : buildPlan.getWsdl().getOuputMessageLocalNames()) {
					final String outputParamPostUrl = planLocation + "/outputparameters/";

					final List<NameValuePair> params = new ArrayList<>();
					params.add(Util.createNameValuePair("name", outputParam));
					params.add(Util.createNameValuePair("type", "String"));
					params.add(Util.createNameValuePair("required", "on"));

					final UrlEncodedFormEntity encodedForm = new UrlEncodedFormEntity(params);

					final HttpResponse outputParamPostResponse = openToscaHttpService.Post(outputParamPostUrl, encodedForm);
					if (outputParamPostResponse.getStatusLine().getStatusCode() >= 300) {
						this.state.currentState = PlanGenerationStates.PLANSENDINGFAILED;
						this.state.currentMessage = "Couldn't set outputParameters. Setting OutputParam (postURL: " + outputParamPostUrl + ") " + outputParam + " failed, Service for Plan Upload sent statusCode " + outputParamPostResponse.getStatusLine().getStatusCode();
						Util.deleteCSAR(csarId);
						LOG.error("Couldn't set outputParameters. Setting OutputParam (postURL: " + outputParamPostUrl + ") " + outputParam + " failed, Service for Plan Upload sent statusCode " + outputParamPostResponse.getStatusLine().getStatusCode());
						return;
					}
					LOG.debug("Sent outputParameter " + outputParam);
				}

				this.state.currentState = PlanGenerationStates.PLANSENT;
				this.state.currentMessage = "Sent plan.";
				LOG.debug("Sent plan.");
			}
		} catch (final ClientProtocolException e) {
			this.state.currentState = PlanGenerationStates.PLANSENDINGFAILED;
			this.state.currentMessage = "Couldn't send plan.";
			Util.deleteCSAR(csarId);
			LOG.error("Couldn't send plan.");
			return;
		} catch (final IOException e) {
			this.state.currentState = PlanGenerationStates.PLANSENDINGFAILED;
			this.state.currentMessage = "Couldn't send plan.";
			Util.deleteCSAR(csarId);
			LOG.error("Couldn't send plan.");
			return;
		}

		// TODO send self-service option

		/*
		 * @FormDataParam("name") String name,
		 *
		 * @FormDataParam("description") String description,
		 *
		 * @FormDataParam("planServiceName") String planServiceName,
		 *
		 * @FormDataParam("planInputMessage") String planInputMessage,
		 *
		 * @FormDataParam("file") InputStream uploadedInputStream,
		 *
		 * @FormDataParam("file") FormDataContentDisposition fileDetail,
		 *
		 * @FormDataParam("file") FormDataBodyPart body
		 */
		try {
			LOG.debug("Starting to send Options");
			this.state.currentState = PlanGenerationStates.OPTIONSENDING;
			this.state.currentMessage = "Sending SelfService Option";

			final URL optionsUrl = new URL(this.state.getCsarUrl(), "selfserviceportal/options/");
			LOG.debug("Sending options to " + optionsUrl.toString());

			final SelfServiceOptionWrapper option = Util.generateSelfServiceOption(buildPlans.get(0));

			// send plan back
			final MultipartEntity mpOptionEntity = new MultipartEntity();

			try {
				mpOptionEntity.addPart("name", new StringBody(option.option.getName()));
				mpOptionEntity.addPart("description", new StringBody(option.option.getDescription()));
				mpOptionEntity.addPart("planServiceName", new StringBody(option.option.getPlanServiceName()));
				mpOptionEntity.addPart("planInputMessage", new StringBody(FileUtils.readFileToString(option.planInputMessageFile)));
			} catch (final UnsupportedEncodingException e1) {
				this.state.currentState = PlanGenerationStates.OPTIONSENDINGFAILED;
				this.state.currentMessage = "Couldn't generate option to send to winery";
				Util.deleteCSAR(csarId);
				LOG.error("Couldn't generate option request to " + optionsUrl.toString());
				return;
			}

			// TODO here we should send a default image, instead of the
			// message..
			final FileBody fileBody = new FileBody(option.planInputMessageFile);
			final ContentBody contentBody = fileBody;
			mpOptionEntity.addPart("file", contentBody);

			final HttpResponse optionsResponse = openToscaHttpService.Post(optionsUrl.toString(), mpOptionEntity);

			if (optionsResponse.getStatusLine().getStatusCode() >= 300) {
				// we assume ,if the status code ranges from 300 to 5xx , that
				// an error occured
				this.state.currentState = PlanGenerationStates.OPTIONSENDINGFAILED;
				this.state.currentMessage = "Couldn't send option to winery. Response: \n  StatusCode: " + optionsResponse.getStatusLine().getStatusCode() + " \n Reason Phrase: \n" + optionsResponse.getStatusLine().getReasonPhrase();
				Util.deleteCSAR(csarId);
				return;
			} else {
				this.state.currentState = PlanGenerationStates.OPTIONSENT;
				this.state.currentMessage = "Sent option. Everythings okay.";
				Util.deleteCSAR(csarId);
				return;
			}
		} catch (final MalformedURLException e) {
			this.state.currentState = PlanGenerationStates.OPTIONSENDINGFAILED;
			this.state.currentMessage = "Couldn't send option to winery.";
			Util.deleteCSAR(csarId);
			return;
		} catch (final IOException e) {
			this.state.currentState = PlanGenerationStates.OPTIONSENDINGFAILED;
			this.state.currentMessage = "Couldn't send option to winery.";
			Util.deleteCSAR(csarId);
			return;
		}
	}
}
