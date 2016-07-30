package org.opentosca.planbuilder.export.exporters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.wsdl.xml.WSDLWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.apache.ode.schemas.dd._2007._03.TInvoke;
import org.apache.ode.schemas.dd._2007._03.TProvide;
import org.apache.ode.schemas.dd._2007._03.TService;
import org.opentosca.planbuilder.model.plan.BuildPlan;
import org.opentosca.planbuilder.model.plan.Deploy;
import org.opentosca.planbuilder.model.plan.GenericWsdlWrapper;
import org.opentosca.util.fileaccess.service.IFileAccessService;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ibm.wsdl.ServiceImpl;

/**
 * <p>
 * This class is used to export buildPlans on filesystems
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 * 
 */
public class SimpleFileExporter {

	private final static Logger LOG = LoggerFactory.getLogger(SimpleFileExporter.class);

	// wrapper class for the rewriting of service names in WSDL's
	public class Service2ServiceEntry {

		public QName service0;
		public QName service1;

		public Service2ServiceEntry(QName service0, QName service1) {
			this.service0 = service0;
			this.service1 = service1;
		}
	}

	/**
	 * Exports the given BuildPlan to the given URI location
	 * 
	 * @param destination
	 *            the URI to export to
	 * @param buildPlan
	 *            the BuildPlan to export
	 * @return true iff exporting the BuildPlan was successful
	 * @throws IOException
	 *             is thrown when reading/writing the file fails
	 * @throws JAXBException
	 *             is thrown when writing with JAXB fails
	 */
	public boolean export(URI destination, BuildPlan buildPlan) throws IOException, JAXBException {
		if (!new File(destination).getName().contains("zip")) {
			return false;
		}
		// fetch imported files
		List<File> importedFiles = buildPlan.getImportedFiles();

		SimpleFileExporter.LOG.debug("BuildPlan has following files attached");
		for (File file : importedFiles) {
			SimpleFileExporter.LOG.debug(file.getAbsolutePath());
		}

		// fetch import elements
		List<Element> importElements = buildPlan.getBpelImportElements();

		SimpleFileExporter.LOG.debug("BuildPlan has following import elements");
		for (Element element : importElements) {
			SimpleFileExporter.LOG.debug("LocalName: " + element.getLocalName());
			SimpleFileExporter.LOG.debug("location:" + element.getAttribute("location"));
		}

		// fetch wsdl
		GenericWsdlWrapper wsdl = buildPlan.getWsdl();

		// generate temp folder
		File tempDir = FileUtils.getTempDirectory();
		SimpleFileExporter.LOG.debug("Trying to write files in system temp folder: " + tempDir.getAbsolutePath());
		File tempFolder = new File(tempDir, Long.toString(System.currentTimeMillis()));
		tempFolder.mkdir();
		SimpleFileExporter.LOG.debug("Trying to write files to temp folder: " + tempFolder.getAbsolutePath());

		List<File> exportedFiles = new ArrayList<File>();

		// match importedFiles with importElements, to change temporary paths
		// inside import elements to relative paths inside the generated zip
		for (File importedFile : importedFiles) {
			for (Element importElement : importElements) {
				String filePath = importedFile.getAbsolutePath();
				String locationPath = importElement.getAttribute("location");
				SimpleFileExporter.LOG.debug("checking filepath:");
				SimpleFileExporter.LOG.debug(filePath);
				SimpleFileExporter.LOG.debug("with: ");
				SimpleFileExporter.LOG.debug(locationPath);
				if (importedFile.getAbsolutePath().trim().equals(importElement.getAttribute("location").trim())) {
					// found the import element for the corresponding file
					// get file name
					String fileName = importedFile.getName();
					SimpleFileExporter.LOG.debug("Trying to reset path to: " + fileName);
					// change location attribute in import element
					importElement.setAttribute("location", fileName);
					// copy file to tempdir
					File fileLocationInDir = new File(tempFolder, fileName);
					FileUtils.copyFile(importedFile, fileLocationInDir);

					exportedFiles.add(fileLocationInDir);
				}
			}
		}

		// write deploy.xml
		SimpleFileExporter.LOG.debug("Starting marshalling");
		Deploy deployment = buildPlan.getDeploymentDeskriptor();

		// rewrite service names in deploy.xml and potential wsdl files
		try {
			this.rewriteServiceNames(deployment, exportedFiles, buildPlan.getCsarName());
		} catch (WSDLException e) {
			LOG.warn("Rewriting of Service names failed", e);
		} catch (FileNotFoundException e) {
			LOG.warn("Something went wrong with locating wsdl files that needed to be changed", e);
		}

		File deployXmlFile = new File(tempFolder, "deploy.xml");
		deployXmlFile.createNewFile();
		JAXBContext jaxbContext = JAXBContext.newInstance(Deploy.class);
		Marshaller m = jaxbContext.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		// output to console uncomment this: m.marshal(deployment, System.out);
		m.marshal(deployment, deployXmlFile);

		// save wsdl in tempfolder
		File wsdlFile = new File(tempFolder, wsdl.getFileName());
		FileUtils.writeStringToFile(wsdlFile, wsdl.getFinalizedWsdlAsString());

		// save bpel file in tempfolder
		File bpelFile = new File(tempFolder, wsdl.getFileName().replace(".wsdl", ".bpel"));
		try {
			this.writeBPELDocToFile(bpelFile, buildPlan.getBpelDocument());
		} catch (TransformerException e) {
			SimpleFileExporter.LOG.error("Error while writing BPEL Document to a file", e);
			return false;
		}

		// package temp dir and move to destination URI
		ServiceReference<?> servRef = FrameworkUtil.getBundle(this.getClass()).getBundleContext()
				.getServiceReference(IFileAccessService.class.getName());
		IFileAccessService service = (IFileAccessService) FrameworkUtil.getBundle(this.getClass()).getBundleContext()
				.getService(servRef);
		service.zip(tempFolder, new File(destination));
		return true;
	}

	private void rewriteServiceNames(Deploy deploy, List<File> referencedFiles, String csarName)
			throws WSDLException, FileNotFoundException {
		WSDLFactory factory = WSDLFactory.newInstance();
		WSDLReader reader = factory.newWSDLReader();
		WSDLWriter writer = factory.newWSDLWriter();

		// first fetch all provide and invoke element which aren't using the
		// 'client' partnerLink
		// single process only
		List<TInvoke> invokes = deploy.getProcess().get(0).getInvoke();
		List<TProvide> provides = deploy.getProcess().get(0).getProvide();

		// the services and their new name the dd uses, excluding the client services, will be added here
		Map<QName, QName> servicesToRewrite = new HashMap<QName,QName>();
		
		for (TInvoke invoke : invokes) {
			if (invoke.getPartnerLink().equals("client")) {
				continue;
			}

			TService service = invoke.getService();
			QName serviceName = service.getName();

			QName renamedServiceName = new QName(serviceName.getNamespaceURI(), csarName + serviceName.getLocalPart() + System.currentTimeMillis());

			servicesToRewrite.put(serviceName, renamedServiceName);

			service.setName(renamedServiceName);

			invoke.setService(service);
		}

		for (TProvide provide : provides) {
			if (provide.getPartnerLink().equals("client")) {
				continue;
			}

			TService service = provide.getService();
			QName serviceName = service.getName();

			QName renamedServiceName = new QName(serviceName.getNamespaceURI(), csarName + serviceName.getLocalPart() + System.currentTimeMillis());

			servicesToRewrite.put(serviceName, renamedServiceName);

			service.setName(renamedServiceName);

			provide.setService(service);
		}

		// and now for the killer part..
		for (QName serviceName : servicesToRewrite.keySet()) {

			for (File file : referencedFiles) {
				if (!file.getAbsolutePath().endsWith(".wsdl")) {
					continue;
				}

				Definition def = reader.readWSDL(file.getAbsolutePath());

				List<QName> servicesToRemove = new ArrayList<QName>();
				// fetch defined services
				for (Object obj : def.getAllServices().values()) {
					Service service = (Service) obj;

					if (serviceName.equals(service.getQName())) {
						// found wsdl with service we have to rewrite
						servicesToRemove.add(service.getQName());

						Service newService = new ServiceImpl();

						for (Object o : service.getPorts().values()) {
							Port port = (Port) o;
							newService.addPort(port);
						}

						newService.setQName(servicesToRewrite.get(serviceName));

						def.addService(newService);

					}
				}

				for (QName serviceToRemove : servicesToRemove) {
					def.removeService(serviceToRemove);
				}

				writer.writeWSDL(def, new FileOutputStream(file));
			}
		}

	}

	/**
	 * Writes the given DOM Document to the location denoted by the given File
	 * 
	 * @param destination
	 *            a File denoting the location to export to
	 * @param doc
	 *            the Document to export
	 * @throws TransformerException
	 *             is thrown when initializing a TransformerFactory or writing
	 *             the Document fails
	 * @throws FileNotFoundException
	 *             is thrown when the File denoted by the File Object doesn't
	 *             exist
	 */
	private void writeBPELDocToFile(File destination, Document doc) throws TransformerException, FileNotFoundException {
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer();

		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new FileOutputStream(destination));
		transformer.transform(source, result);
	}
}
