package org.opentosca.planbuilder.export.exporters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
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

/**
 * <p>
 * This class is used to export buildPlans on filesystems
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kálmán Képes - kepeskn@studi.informatik.uni-stuttgart.de
 * 
 */
public class SimpleFileExporter {
	
	private final static Logger LOG = LoggerFactory.getLogger(SimpleFileExporter.class);
	
	
	/**
	 * Exports the given BuildPlan to the given URI location
	 * 
	 * @param destination the URI to export to
	 * @param buildPlan the BuildPlan to export
	 * @return true iff exporting the BuildPlan was successful
	 * @throws IOException is thrown when reading/writing the file fails
	 * @throws JAXBException is thrown when writing with JAXB fails
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
				}
			}
		}
		
		// write deploy.xml
		SimpleFileExporter.LOG.debug("Starting marshalling");
		Deploy deployment = buildPlan.getDeploymentDeskriptor();
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
		ServiceReference<?> servRef = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getServiceReference(IFileAccessService.class.getName());
		IFileAccessService service = (IFileAccessService) FrameworkUtil.getBundle(this.getClass()).getBundleContext().getService(servRef);
		service.zip(tempFolder, new File(destination));
		return true;
	}
	
	/**
	 * Writes the given DOM Document to the location denoted by the given File
	 * 
	 * @param destination a File denoting the location to export to
	 * @param doc the Document to export
	 * @throws TransformerException is thrown when initializing a
	 *             TransformerFactory or writing the Document fails
	 * @throws FileNotFoundException is thrown when the File denoted by the File
	 *             Object doesn't exist
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
