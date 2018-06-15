package org.opentosca.container.api.legacy.resources.csar.content;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.opentosca.container.api.legacy.resources.utilities.ResourceConstants;
import org.opentosca.container.api.legacy.resources.xlink.Reference;
import org.opentosca.container.api.legacy.resources.xlink.References;
import org.opentosca.container.api.legacy.resources.xlink.XLinkConstants;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.model.AbstractFile;
import org.opentosca.container.core.model.csar.id.CSARID;
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
        LOG.info("{} created: {}", this.getClass(), this);
        LOG.info("File path: {}", csarFile.getPath());
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
            final Reference self =
                new Reference(this.uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF);
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
            LOG.info("Attempt to download file: \"{}\"", this.CSAR_FILE.getPath());
            InputStream inputStream;
            // try {
            inputStream = this.CSAR_FILE.getFileAsInputStream();
            // We add Content Disposition header, because file to download
            // should have the correct file name.
            return Response.ok(inputStream)
                           .header("Content-Disposition", "attachment; filename=\"" + this.CSAR_FILE.getName() + "\"")
                           .build();
        }
        return Response.status(Status.NOT_FOUND).build();
    }

    public String getAsJSONString() {
        try {
            LOG.trace("Attempt to download file: \"{}\"", this.CSAR_FILE.getPath());
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
        }
        catch (final SystemException e) {
            e.printStackTrace();
        }
        catch (final UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        catch (final IOException e) {
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
            LOG.info("Attempt to download image: \"{}\"", this.CSAR_FILE.getPath());
            // Check if file is a (supported) image
            if (this.CSAR_FILE.getName() != null) {
                // retrieve file extension
                final String ext = this.CSAR_FILE.getName().substring(this.CSAR_FILE.getName().lastIndexOf(".") + 1);
                // known?
                if (ResourceConstants.imageMediaTypes.containsKey(ext)) {
                    LOG.debug("Supported image file, *.{} maps to {}", ext,
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
}
