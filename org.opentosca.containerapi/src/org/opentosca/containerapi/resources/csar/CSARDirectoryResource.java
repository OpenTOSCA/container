package org.opentosca.containerapi.resources.csar;

import java.io.File;
import java.nio.file.Paths;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.opentosca.containerapi.osgi.servicegetter.FileRepositoryServiceHandler;
import org.opentosca.containerapi.resources.utilities.ResourceConstants;
import org.opentosca.containerapi.resources.utilities.Utilities;
import org.opentosca.containerapi.resources.xlink.Reference;
import org.opentosca.containerapi.resources.xlink.References;
import org.opentosca.containerapi.resources.xlink.XLinkConstants;
import org.opentosca.core.file.service.ICoreFileService;
import org.opentosca.core.model.artifact.directory.AbstractDirectory;
import org.opentosca.core.model.artifact.file.AbstractFile;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.exceptions.SystemException;
import org.opentosca.exceptions.UserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resource represents a directory of a CSAR.<br />
 * <br />
 * Copyright 2013 IAAS University of Stuttgart<br />
 * <br />
 * 
 * @author Rene Trefft - rene.trefft@developers.opentosca.org
 * 
 */
public class CSARDirectoryResource {
	
	
	private static Logger LOG = LoggerFactory.getLogger(CSARDirectoryResource.class);
	
	private final AbstractDirectory CSAR_DIRECTORY;
	private final CSARID CSAR_ID;
	
	
	/**
	 * 
	 * 
	 * @param resourceFile
	 */
	public CSARDirectoryResource(AbstractDirectory csarDirectory, CSARID csarID) {
		CSAR_DIRECTORY = csarDirectory;
		CSAR_ID = csarID;
		CSARDirectoryResource.LOG.info("{} created: {}", this.getClass(), this);
		CSARDirectoryResource.LOG.info("Directory path: {}", csarDirectory.getPath());
	}
	
	/**
	 * Answers Get-Requests with media type Constants.Text_XML
	 * 
	 * @param uriInfo
	 * @return
	 */
	@GET
	@Produces(ResourceConstants.LINKED_XML)
	public Response getReferences(@Context UriInfo uriInfo) {
		
		if (CSAR_DIRECTORY == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		
		References refs = new References();
		
		// References refs = new References();
		
		Set<AbstractDirectory> directories = CSAR_DIRECTORY.getDirectories();
		for (AbstractDirectory directory : directories) {
			refs.getReference().add(new Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(), directory.getName()), XLinkConstants.SIMPLE, directory.getName()));
		}
		
		Set<AbstractFile> files = CSAR_DIRECTORY.getFiles();
		for (AbstractFile file : files) {
			refs.getReference().add(new Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(), file.getName()), XLinkConstants.SIMPLE, file.getName()));
		}
		
		Reference self = new Reference(uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF);
		refs.getReference().add(self);
		return Response.ok(refs.getXMLString()).build();
		
	}
	
	@Path("{directoryOrFile}")
	public Object getDirectoryOrFile(@PathParam("directoryOrFile") String directoryOrFile) {
		
		CSARDirectoryResource.LOG.debug("Checking if \"{}\" exists in directory \"{}\" of CSAR \"{}\"...", directoryOrFile, CSAR_DIRECTORY.getPath(), CSAR_ID);
		
		Set<AbstractDirectory> directories = CSAR_DIRECTORY.getDirectories();
		
		for (AbstractDirectory directory : directories) {
			if (directory.getName().equals(directoryOrFile)) {
				CSARDirectoryResource.LOG.debug("\"{}\" is a directory in directory \"{}\" of CSAR \"{}\".", directoryOrFile, CSAR_DIRECTORY.getPath(), CSAR_ID);
				return new CSARDirectoryResource(directory, CSAR_ID);
			}
		}
		
		Set<AbstractFile> files = CSAR_DIRECTORY.getFiles();
		
		for (AbstractFile file : files) {
			if (file.getName().equals(directoryOrFile)) {
				CSARDirectoryResource.LOG.debug("\"{}\" is a file in directory \"{}\" of CSAR \"{}\".", directoryOrFile, CSAR_DIRECTORY.getPath(), CSAR_ID);
				return new CSARFileResource(file, CSAR_ID);
			}
		}
		
		CSARDirectoryResource.LOG.warn("\"{}\" does not exist in directory \"{}\" of CSAR \"{}\".", directoryOrFile, CSAR_DIRECTORY.getPath(), CSAR_ID);
		return null;
		
	}
	
	/**
	 * Moves this directory of a CSAR to the active / default storage provider
	 * if {@code move} is passed in {@code input} (body of a POST message).
	 * 
	 * @param input
	 * @return 200 (OK) - directory was moved successful.<br />
	 *         400 (bad request) - {@code move} was not passed.<br />
	 *         500 (internal server error) - moving directory failed.
	 * @throws SystemException
	 * @throws UserException
	 * 
	 * 
	 * @see ICoreFileService#moveFileOrDirectoryOfCSAR(CSARID, File)
	 */
	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public Response moveDirectoryOfCSAR(String input) throws UserException, SystemException {
		
		if (input.equalsIgnoreCase("move")) {
			
			// try {
			
			FileRepositoryServiceHandler.getFileHandler().moveFileOrDirectoryOfCSAR(CSAR_ID, Paths.get(CSAR_DIRECTORY.getPath()));
			
			return Response.ok("Moving directory \"" + CSAR_DIRECTORY.getPath() + "\" of CSAR \"" + CSAR_ID.toString() + "\" was successful.").build();
			
			// } catch (UserException exc) {
			// CSARDirectoryResource.LOG.warn("An User Exception occured.",
			// exc);
			// } catch (SystemException exc) {
			// CSARDirectoryResource.LOG.warn("An System Exception occured.",
			// exc);
			// }
			//
			// return Response.status(Status.INTERNAL_SERVER_ERROR).build();
			
		}
		
		return Response.status(Status.BAD_REQUEST).build();
		
	}
}
