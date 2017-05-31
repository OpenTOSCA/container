package org.opentosca.container.api.legacy.resources.csar.content;

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

import org.opentosca.container.api.legacy.osgi.servicegetter.FileRepositoryServiceHandler;
import org.opentosca.container.api.legacy.resources.utilities.ResourceConstants;
import org.opentosca.container.api.legacy.resources.utilities.Utilities;
import org.opentosca.container.api.legacy.resources.xlink.Reference;
import org.opentosca.container.api.legacy.resources.xlink.References;
import org.opentosca.container.api.legacy.resources.xlink.XLinkConstants;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.model.AbstractDirectory;
import org.opentosca.container.core.model.AbstractFile;
import org.opentosca.container.core.model.csar.CSARContent;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.service.ICoreFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resource represents the root of a CSAR.
 *
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Markus Fischer - fischema@studi.informatik.uni-stuttgart.de
 * @author Rene Trefft - rene.trefft@developers.opentosca.org
 *
 */
public class ContentResource {

	private static final Logger LOG = LoggerFactory.getLogger(ContentResource.class);
	// If the CSAR is null, CSAR does not exist in the Container
	private final CSARContent CSAR;
	
	UriInfo uriInfo;
	
	
	public ContentResource(final CSARContent csar) {
		this.CSAR = csar;
		ContentResource.LOG.debug("{} created: {}", this.getClass(), this);
		
		if (csar != null) {
			ContentResource.LOG.debug("Accessing content of CSAR \"{}\".", csar.getCSARID());
		} else {
			ContentResource.LOG.error("Requested CSAR is not stored!");
			System.out.println(csar);
		}
		
	}
	
	@GET
	@Produces(ResourceConstants.LINKED_XML)
	public Response getReferencesXML(@Context final UriInfo uriInfo) {
		this.uriInfo = uriInfo;
		return Response.ok(this.getReferences().getXMLString()).build();
	}
	
	@GET
	@Produces(ResourceConstants.LINKED_JSON)
	public Response getReferencesJSON(@Context final UriInfo uriInfo) {
		this.uriInfo = uriInfo;
		return Response.ok(this.getReferences().getJSONString()).build();
	}
	
	public References getReferences() {
		
		if (this.CSAR == null) {
			ContentResource.LOG.info("CSAR is not stored.");
			return null;
		}
		
		final References refs = new References();
		
		final Set<AbstractDirectory> directories = this.CSAR.getDirectories();
		for (final AbstractDirectory directory : directories) {
			refs.getReference().add(new Reference(Utilities.buildURI(this.uriInfo.getAbsolutePath().toString(), directory.getName()), XLinkConstants.SIMPLE, directory.getName()));
		}
		
		final Set<AbstractFile> files = this.CSAR.getFiles();
		for (final AbstractFile file : files) {
			refs.getReference().add(new Reference(Utilities.buildURI(this.uriInfo.getAbsolutePath().toString(), file.getName()), XLinkConstants.SIMPLE, file.getName()));
		}
		
		refs.getReference().add(new Reference(this.uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
		
		return refs;
		
	}
	
	@Path("{directoryOrFile}")
	public Object getDirectoryOrFile(@PathParam("directoryOrFile") final String directoryOrFile) {
		
		ContentResource.LOG.debug("Checking if \"{}\" exists in CSAR \"{}\"...", directoryOrFile, this.CSAR.getCSARID());
		
		final Set<AbstractDirectory> directories = this.CSAR.getDirectories();
		
		for (final AbstractDirectory directory : directories) {
			if (directory.getName().equals(directoryOrFile)) {
				ContentResource.LOG.debug("\"{}\" is a directory of CSAR \"{}\".", directoryOrFile, this.CSAR.getCSARID());
				return new DirectoryResource(directory, this.CSAR.getCSARID());
				
			}
		}
		
		final Set<AbstractFile> files = this.CSAR.getFiles();
		
		for (final AbstractFile file : files) {
			if (file.getName().equals(directoryOrFile)) {
				ContentResource.LOG.debug("\"{}\" is a file of CSAR \"{}\".", directoryOrFile, this.CSAR.getCSARID());
				return new FileResource(file, this.CSAR.getCSARID());
			}
		}
		
		ContentResource.LOG.error("\"{}\" does not exist in CSAR \"{}\"!", directoryOrFile, this.CSAR.getCSARID());
		
		return null;
		
	}
	
	/**
	 * Moves this CSAR to the active / default storage provider if {@code move}
	 * is passed in {@code input} (body of a POST message).
	 *
	 * @param input
	 * @return 200 (OK) - CSAR was moved successful.<br />
	 *         400 (bad request) - {@code move} was not passed.<br />
	 *         404 (not found) - CSAR is not stored.<br />
	 *         500 (internal server error) - moving CSAR failed.
	 * @throws SystemException
	 * @throws UserException
	 *
	 * @see ICoreFileService#moveCSAR(CSARID)
	 */
	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public Response moveCSAR(final String input) throws UserException, SystemException {
		
		if (this.CSAR == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		
		if (input.equalsIgnoreCase("move")) {
			
			// try {
			
			FileRepositoryServiceHandler.getFileHandler().moveCSAR(this.CSAR.getCSARID());
			
			return Response.ok("Moving CSAR \"" + this.CSAR.getCSARID() + "\" was successful.").build();
			
			// } catch (UserException exc) {
			// CSARContentResource.LOG.warn("An User Exception occured.", exc);
			// } catch (SystemException exc) {
			// CSARContentResource.LOG.warn("A System Exception occured.", exc);
			// }
			
			// return Response.status(Status.INTERNAL_SERVER_ERROR).build();
			
		}
		
		return Response.status(Status.BAD_REQUEST).build();
		
	}
	
}
