package org.opentosca.container.api.legacy.resources.csar.content;

import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.opentosca.container.api.legacy.resources.utilities.ResourceConstants;
import org.opentosca.container.api.legacy.resources.utilities.Utilities;
import org.opentosca.container.api.legacy.resources.xlink.Reference;
import org.opentosca.container.api.legacy.resources.xlink.References;
import org.opentosca.container.api.legacy.resources.xlink.XLinkConstants;
import org.opentosca.container.core.model.AbstractDirectory;
import org.opentosca.container.core.model.AbstractFile;
import org.opentosca.container.core.model.csar.id.CSARID;
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
            refs.getReference().add(new Reference(Utilities.buildURI(this.uriInfo, file.getName()),
                XLinkConstants.SIMPLE, file.getName()));
        }

        final Reference self =
            new Reference(this.uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF);
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

}
