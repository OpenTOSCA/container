package org.opentosca.planbuilder.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.apache.commons.io.FileUtils;
import org.apache.http.NameValuePair;
import org.apache.ode.schemas.dd._2007._03.TProvide;
import org.eclipse.winery.model.selfservice.ApplicationOption;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.planbuilder.export.Exporter;
import org.opentosca.planbuilder.export.VinothekKnownParameters;
import org.opentosca.planbuilder.importer.Importer;
import org.opentosca.planbuilder.model.plan.BuildPlan;
import org.opentosca.planbuilder.model.plan.Deploy;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;

/**
 * Copyright 2015 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class Util {
	
	public static class SelfServiceOptionWrapper {

		public ApplicationOption option;
		public File planInputMessageFile;


		public SelfServiceOptionWrapper(final ApplicationOption option, final File planInputMessageFile) {
			this.option = option;
			this.planInputMessageFile = planInputMessageFile;
		}
	}

	private static class NameValuePairUtils implements NameValuePair {

		private final String name;
		private final String value;


		public NameValuePairUtils(final String name, final String value) {
			this.name = name;
			this.value = value;
		}

		@Override
		public String getName() {
			return this.name;
		}

		@Override
		public String getValue() {
			return this.value;
		}
	}


	public static NameValuePair createNameValuePair(final String name, final String value) {
		return new Util.NameValuePairUtils(name, value);
	}

	/**
	 * Generates for the given CSAR (denoted by it's id) BuildPlans
	 *
	 * @param csarId the Id of the CSAR to generate plans for
	 * @return a List of BuildPlans containing the generated BuildPlans
	 */
	public static List<BuildPlan> startPlanBuilder(final CSARID csarId) {
		final Importer planBuilderImporter = new Importer();
		final List<BuildPlan> plans = new ArrayList<>();
		try {
			final AbstractDefinitions defs = planBuilderImporter.createContext(ServiceRegistry.getCoreFileService().getCSAR(csarId));

			for (final AbstractServiceTemplate serviceTemplate : defs.getServiceTemplates()) {
				plans.add(planBuilderImporter.buildPlan(defs, csarId.getFileName(), serviceTemplate.getQName()));
			}

		} catch (final SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final UserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return plans;
	}

	public static void deleteCSAR(final CSARID csarId) {
		try {
			ServiceRegistry.getCoreFileService().deleteCSAR(csarId);
		} catch (final SystemException e) {
			e.printStackTrace();
		} catch (final UserException e) {
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
	public static CSARID storeCSAR(final String fileName, final InputStream uploadedInputStream) {
		final File tmpDir = new File(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + Long.toString(System.currentTimeMillis()));
		tmpDir.mkdir();

		final File uploadFile = new File(tmpDir.getAbsoluteFile() + System.getProperty("file.separator") + fileName);

		OutputStream out;

		try {
			out = new FileOutputStream(uploadFile);
			int read = 0;
			final byte[] bytes = new byte[1024];

			while ((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}

			uploadedInputStream.close();

			out.flush();
			out.close();

			return ServiceRegistry.getCoreFileService().storeCSAR(uploadFile.toPath());
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (final IOException e) {
			e.printStackTrace();
			return null;
		} catch (final UserException e) {
			e.printStackTrace();
			return null;
		} catch (final SystemException e) {
			e.printStackTrace();
			return null;
		}

	}

	public static SelfServiceOptionWrapper generateSelfServiceOption(final BuildPlan buildPlan) throws IOException {
		final String id = String.valueOf(System.currentTimeMillis());
		final ApplicationOption option = new ApplicationOption();

		final File tmpDir = new File(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + Long.toString(System.currentTimeMillis()));
		tmpDir.mkdir();

		final File planInputMessageFile = new File(tmpDir, "plan.input.default." + id + ".xml");

		option.setName("Default_PlanBuilderGenerated");
		option.setId(id);
		option.setIconUrl("");
		option.setDescription("N/A");
		option.setPlanServiceName(Util.getBuildPlanServiceName(buildPlan.getDeploymentDeskriptor()).getLocalPart());
		option.setPlanInputMessageUrl("plan.input.default." + id + ".xml");
		Util.writePlanInputMessageInstance(buildPlan, planInputMessageFile);

		return new SelfServiceOptionWrapper(option, planInputMessageFile);
	}

	/**
	 * Writes given BuildPlan to temporary folder.
	 *
	 * @param buildPlan a BuildPlan
	 * @return a File denoting the export location
	 */
	public static File writePlan2TmpFolder(final BuildPlan buildPlan) {
		final Exporter planBuilderExporter = new Exporter();
		final File tmpDir = new File(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + Long.toString(System.currentTimeMillis()));
		tmpDir.mkdir();

		final File uploadFile = new File(tmpDir.getAbsoluteFile() + System.getProperty("file.separator") + buildPlan.getBpelProcessElement().getAttribute("name") + ".zip");

		try {
			planBuilderExporter.export(uploadFile.toURI(), buildPlan);
		} catch (final IOException e) {
			e.printStackTrace();
			return null;
		} catch (final JAXBException e) {
			e.printStackTrace();
			return null;
		}

		return uploadFile;
	}

	public static String getStacktrace(final Exception e) {
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}

	private static QName getBuildPlanServiceName(final Deploy deploy) {
		// generated buildplans have only one process!
		for (final TProvide provide : deploy.getProcess().get(0).getProvide()) {
			// "client" is a convention
			if (provide.getPartnerLink().equals("client")) {
				return provide.getService().getName();
			}
		}
		return null;
	}

	private static void writePlanInputMessageInstance(final BuildPlan buildPlan, final File xmlFile) throws IOException {
		final String messageNs = buildPlan.getWsdl().getTargetNamespace();
		final String requestMessageLocalName = buildPlan.getWsdl().getRequestMessageLocalName();
		final List<String> inputParamNames = buildPlan.getWsdl().getInputMessageLocalNames();

		final VinothekKnownParameters paramMappings = new VinothekKnownParameters();
		final String soapMessagePrefix = Util.createPrefixPartOfSoapMessage(messageNs, requestMessageLocalName);
		final String soapMessageSuffix = Util.createSuffixPartOfSoapMessage(requestMessageLocalName);

		String soapMessage = soapMessagePrefix;
		for (final String inputParamName : inputParamNames) {
			soapMessage += paramMappings.createXmlElement(inputParamName);
		}
		soapMessage += soapMessageSuffix;

		FileUtils.write(xmlFile, soapMessage);
	}

	private static String createPrefixPartOfSoapMessage(final String namespace, final String messageBodyRootLocalName) {
		final String soapEnvelopePrefix = "<soapenv:Envelope xmlns:wsa=\"http://www.w3.org/2005/08/addressing\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:org=\"" + namespace + "\"><soapenv:Header><wsa:ReplyTo><wsa:Address>%CALLBACK-URL%</wsa:Address></wsa:ReplyTo><wsa:Action>" + namespace + "/initiate</wsa:Action><wsa:MessageID>%CORRELATION-ID%</wsa:MessageID></soapenv:Header><soapenv:Body><org:" + messageBodyRootLocalName + ">";
		return soapEnvelopePrefix;
	}

	private static String createSuffixPartOfSoapMessage(final String messageBodyRootLocalName) {
		final String soapEnvelopeSuffix = "</org:" + messageBodyRootLocalName + "></soapenv:Body></soapenv:Envelope>";
		return soapEnvelopeSuffix;
	}

}
