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

import org.apache.http.NameValuePair;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.exceptions.SystemException;
import org.opentosca.exceptions.UserException;
import org.opentosca.planbuilder.export.Exporter;
import org.opentosca.planbuilder.importer.Importer;
import org.opentosca.planbuilder.model.plan.BuildPlan;
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
	
	private static class NameValuePairUtils implements NameValuePair {
		
		private String name;
		private String value;
		
		
		public NameValuePairUtils(String name, String value) {
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
	
	
	public static NameValuePair createNameValuePair(String name, String value) {
		return new Util.NameValuePairUtils(name, value);
	}
	
	/**
	 * Generates for the given CSAR (denoted by it's id) BuildPlans
	 * 
	 * @param csarId the Id of the CSAR to generate plans for
	 * @return a List of BuildPlans containing the generated BuildPlans
	 */
	public static List<BuildPlan> startPlanBuilder(CSARID csarId) {
		Importer planBuilderImporter = new Importer();
		List<BuildPlan> plans = new ArrayList<BuildPlan>();
		try {
			AbstractDefinitions defs = planBuilderImporter.createContext(ServiceRegistry.getCoreFileService().getCSAR(csarId));
			
			for (AbstractServiceTemplate serviceTemplate : defs.getServiceTemplates()) {
				plans.add(planBuilderImporter.buildPlan(defs, csarId.getFileName(), serviceTemplate.getQName()));
			}
						
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return plans;
	}
	
	public static void deleteCSAR(CSARID csarId) {
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
	public static CSARID storeCSAR(String fileName, InputStream uploadedInputStream) {
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
	 * Writes given BuildPlan to temporary folder.
	 * 
	 * @param buildPlan a BuildPlan
	 * @return a File denoting the export location
	 */
	public static File writePlan2TmpFolder(BuildPlan buildPlan) {
		Exporter planBuilderExporter = new Exporter();
		File tmpDir = new File(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + Long.toString(System.currentTimeMillis()));
		tmpDir.mkdir();
		
		File uploadFile = new File(tmpDir.getAbsoluteFile() + System.getProperty("file.separator") + buildPlan.getBpelProcessElement().getAttribute("name") + ".zip");
		
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
	
	public static String getStacktrace(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}
	
}
