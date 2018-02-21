package org.opentosca.container.api.legacy.resources.csar.content;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.opentosca.container.api.legacy.osgi.servicegetter.FileRepositoryServiceHandler;
import org.opentosca.container.api.legacy.resources.utilities.ResourceConstants;
import org.opentosca.container.api.legacy.resources.xlink.Reference;
import org.opentosca.container.api.legacy.resources.xlink.References;
import org.opentosca.container.api.legacy.resources.xlink.XLinkConstants;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.model.AbstractFile;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.service.ICoreFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resource represents a file of a CSAR.<br />
 * <br />
 * Copyright 2013 IAAS University of Stuttgart<br />
 * <br />
 *
 * @author Rene Trefft - rene.trefft@developers.opentosca.org
 *
 */
public class FileResource {

    private static Logger LOG = LoggerFactory.getLogger(FileResource.class);

    private final AbstractFile CSAR_FILE;
    private final CSARID CSAR_ID;

    UriInfo uriInfo;


    /**
     *
     *
     * @param resourceFile
     */
    public FileResource(final AbstractFile csarFile, final CSARID csarID) {
        this.CSAR_FILE = csarFile;
        this.CSAR_ID = csarID;
        FileResource.LOG.info("{} created: {}", this.getClass(), this);
        FileResource.LOG.info("File path: {}", csarFile.getPath());
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

        final String json = this.getAsJSONString();

        if (null != json && !json.equals("")) {
            return Response.ok(json).build();
        }

        return Response.ok(this.getReferences().getJSONString()).build();
    }

    public References getReferences() {
        if (this.CSAR_FILE != null) {
            final References refs = new References();
            final Reference self = new Reference(this.uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE,
                XLinkConstants.SELF);
            refs.getReference().add(self);
            return refs;
        }
        return null;
    }

    /**
     * @return
     * @throws SystemException
     */
    @GET
    @Produces(ResourceConstants.OCTET_STREAM)
    public Response downloadFile() throws SystemException {

        if (this.CSAR_FILE != null) {

            FileResource.LOG.info("Attempt to download file: \"{}\"", this.CSAR_FILE.getPath());

            InputStream inputStream;
            // try {
            inputStream = this.CSAR_FILE.getFileAsInputStream();
            // We add Content Disposition header, because file to download
            // should have the correct file name.
            return Response.ok(inputStream)
                           .header("Content-Disposition", "attachment; filename=\"" + this.CSAR_FILE.getName() + "\"")
                           .build();
            // } catch (SystemException exc) {
            // CSARFileResource.LOG.warn("An System Exception occured.", exc);
            // }

            // return Response.status(Status.INTERNAL_SERVER_ERROR).build();

        }

        return Response.status(Status.NOT_FOUND).build();

    }

    public String getAsJSONString() {

        try {

            FileResource.LOG.trace("Attempt to download file: \"{}\"", this.CSAR_FILE.getPath());

            final InputStream inputStream = this.CSAR_FILE.getFileAsInputStream();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            final StringBuilder strBuilder = new StringBuilder();

            String inputStr;
            do {
                inputStr = reader.readLine();
                if (null == inputStr) {
                    break;
                }
                strBuilder.append(inputStr);
            } while (true);

            reader.close();

            LOG.trace("Found a json file and parsed it, contents are:\n   {}", strBuilder.toString());
            return strBuilder.toString();

        } catch (final SystemException e) {
            e.printStackTrace();
        } catch (final UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        }

        return "";

    }

    /**
     * Retrieval of image files. Is handled separately from the generic file retrieval as the
     * corresponding mime type has to be set.
     *
     * TODO Can we set the supported media types depending on the imageMediaTypes map in
     * ResourceConstants? (String[])ResourceConstants.imageMediaTypes.values().toArray(new
     * String[ResourceConstants.imageMediaTypes.values().size()]);
     *
     * @return
     * @throws SystemException
     */
    @GET
    @Produces(ResourceConstants.IMAGE)
    public Response getImage() throws SystemException {
        if (this.CSAR_FILE != null) {

            FileResource.LOG.info("Attempt to download image: \"{}\"", this.CSAR_FILE.getPath());

            // Check if file is a (supported) image
            if (this.CSAR_FILE.getName() != null) {
                // retrieve file extension
                final String ext = this.CSAR_FILE.getName().substring(this.CSAR_FILE.getName().lastIndexOf(".") + 1);
                // known?
                if (ResourceConstants.imageMediaTypes.containsKey(ext)) {
                    FileResource.LOG.debug("Supported image file, *.{} maps to {}", ext,
                        ResourceConstants.imageMediaTypes.get(ext));
                    final InputStream inputStream = this.CSAR_FILE.getFileAsInputStream();
                    // set matching media type and return
                    return Response.ok(inputStream, ResourceConstants.imageMediaTypes.get(ext)).build();
                } else {
                    // file is no (supported) image
                    return Response.status(Status.NOT_ACCEPTABLE).build();
                }
            } else {
                // file without name, should not happen...
                return Response.serverError().build();
            }
        }

        return Response.status(Status.NOT_FOUND).build();

    }

    // @Produces("image/*;qs=2")
    // // "image/*" will be preferred over "text/xml" when requesting an image.
    // // This is a fix for Webkit Browsers who are too dumb for content
    // // negotiation.
    // public Response getImage(@PathParam("image") String imageRelativePath) {
    //
    // // File f = this.resourceFile;
    // //
    // // if ((f == null) || !f.isFile()) {
    // // throw new WebApplicationException(404);
    // // }
    // MediaType mt = new MediaType("image", "*");
    //
    // // new
    // // MimetypesFileTypeMap().getContentType(f);
    // // System.out.println("####### MIMETYPE: " + mt);
    // return Response.ok(f, mt).build();
    //
    // // Response.ok()
    //
    // }

    // @GET
    // @Produces(ResourceConstants.TEXT_PLAIN)
    // public Response getAbsolutePath() {
    // if (this.fileInCSAR == null) {
    // return Response.status(404).build();
    // } else {
    // return Response.ok(this.fileInCSAR.getAbsolutePath()).build();
    // }
    // }

    // @Path("{path}")
    // public FileDirectoryResource getDirectory(@PathParam("path") String path)
    // {
    // CSARFileResource.LOG.info("Trying to open directory or file: {}", path);
    // File returnFile = null;
    // if (this.fileInCSAR.isDirectory()) {
    // for (File file : this.fileInCSAR.listFiles()) {
    // if (file.getName().equals(path)) {
    // CSARFileResource.LOG.info("Requested resource found: {}", path);
    // returnFile = file;
    // }
    // }
    // }
    // return new FileDirectoryResource(returnFile);
    // }

    /**
     * Moves this file of a CSAR to the active / default storage provider if {@code move} is passed in
     * {@code input} (body of a POST message).
     *
     * @param input
     * @return 200 (OK) - file was moved successful.<br />
     *         500 (internal server error) - moving file failed.<br />
     *         400 (bad request) - {@code move} was not passed.
     * @throws @throws UserException
     *
     * @see ICoreFileService#moveFileOrDirectoryOfCSAR(CSARID, File)
     */
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response moveFileOfCSAR(final String input) throws UserException, SystemException {

        if (input.equalsIgnoreCase("move")) {

            // try {

            FileRepositoryServiceHandler.getFileHandler().moveFileOrDirectoryOfCSAR(this.CSAR_ID,
                Paths.get(this.CSAR_FILE.getPath()));

            return Response.ok("Moving file \"" + this.CSAR_FILE.getPath() + "\" of CSAR \"" + this.CSAR_ID.toString()
                + "\" was successful.").build();

            // } catch (UserException exc) {
            // CSARFileResource.LOG.warn("An User Exception occured.", exc);
            // } catch (SystemException exc) {
            // CSARFileResource.LOG.warn("An System Exception occured.", exc);
            // }

            // return Response.status(Status.INTERNAL_SERVER_ERROR).build();

        }

        return Response.status(Status.BAD_REQUEST).build();

    }

}
