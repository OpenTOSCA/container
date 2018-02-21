package org.opentosca.container.api.legacy.resources.csar.content;

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
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.service.ICoreFileService;
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
public class DirectoryResource {

    private static Logger LOG = LoggerFactory.getLogger(DirectoryResource.class);

    private final AbstractDirectory CSAR_DIRECTORY;
    private final CSARID CSAR_ID;

    UriInfo uriInfo;


    /**
     *
     *
     * @param resourceFile
     */
    public DirectoryResource(final AbstractDirectory csarDirectory, final CSARID csarID) {
        this.CSAR_DIRECTORY = csarDirectory;
        this.CSAR_ID = csarID;
        DirectoryResource.LOG.info("{} created: {}", this.getClass(), this);
        DirectoryResource.LOG.info("Directory path: {}", csarDirectory.getPath());
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

        if (this.CSAR_DIRECTORY == null) {
            return null;
        }

        final References refs = new References();

        // References refs = new References();

        final Set<AbstractDirectory> directories = this.CSAR_DIRECTORY.getDirectories();
        for (final AbstractDirectory directory : directories) {
            refs.getReference().add(new Reference(Utilities.buildURI(this.uriInfo, directory.getName()),
                XLinkConstants.SIMPLE, directory.getName()));
        }

        final Set<AbstractFile> files = this.CSAR_DIRECTORY.getFiles();
        for (final AbstractFile file : files) {
            refs.getReference().add(
                new Reference(Utilities.buildURI(this.uriInfo, file.getName()), XLinkConstants.SIMPLE, file.getName()));
        }

        final Reference self = new Reference(this.uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE,
            XLinkConstants.SELF);
        refs.getReference().add(self);
        return refs;

    }

    @Path("{directoryOrFile}")
    public Object getDirectoryOrFile(@PathParam("directoryOrFile") String directoryOrFile) {

        directoryOrFile = Utilities.URLencode(directoryOrFile);
        DirectoryResource.LOG.debug("Checking if \"{}\" exists in directory \"{}\" of CSAR \"{}\"...", directoryOrFile,
            this.CSAR_DIRECTORY.getPath(), this.CSAR_ID);

        final Set<AbstractDirectory> directories = this.CSAR_DIRECTORY.getDirectories();

        for (final AbstractDirectory directory : directories) {
            if (directory.getName().equals(directoryOrFile)) {
                DirectoryResource.LOG.debug("\"{}\" is a directory in directory \"{}\" of CSAR \"{}\".",
                    directoryOrFile, this.CSAR_DIRECTORY.getPath(), this.CSAR_ID);
                return new DirectoryResource(directory, this.CSAR_ID);
            }
        }

        final Set<AbstractFile> files = this.CSAR_DIRECTORY.getFiles();

        for (final AbstractFile file : files) {
            if (file.getName().equals(directoryOrFile)) {
                DirectoryResource.LOG.debug("\"{}\" is a file in directory \"{}\" of CSAR \"{}\".", directoryOrFile,
                    this.CSAR_DIRECTORY.getPath(), this.CSAR_ID);
                return new FileResource(file, this.CSAR_ID);
            }
        }

        DirectoryResource.LOG.warn("\"{}\" does not exist in directory \"{}\" of CSAR \"{}\".", directoryOrFile,
            this.CSAR_DIRECTORY.getPath(), this.CSAR_ID);
        return null;

    }

    /**
     * Moves this directory of a CSAR to the active / default storage provider if {@code move} is passed
     * in {@code input} (body of a POST message).
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
    public Response moveDirectoryOfCSAR(final String input) throws UserException, SystemException {

        if (input.equalsIgnoreCase("move")) {

            // try {

            FileRepositoryServiceHandler.getFileHandler().moveFileOrDirectoryOfCSAR(this.CSAR_ID,
                Paths.get(this.CSAR_DIRECTORY.getPath()));

            return Response.ok("Moving directory \"" + this.CSAR_DIRECTORY.getPath() + "\" of CSAR \""
                + this.CSAR_ID.toString() + "\" was successful.").build();

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
