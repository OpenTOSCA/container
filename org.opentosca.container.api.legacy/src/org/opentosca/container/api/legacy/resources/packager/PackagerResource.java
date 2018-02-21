package org.opentosca.container.api.legacy.resources.packager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.opentosca.container.api.legacy.resources.utilities.ResourceConstants;
import org.opentosca.container.api.legacy.resources.utilities.Utilities;
import org.opentosca.container.api.legacy.resources.xlink.Reference;
import org.opentosca.container.api.legacy.resources.xlink.References;
import org.opentosca.container.api.legacy.resources.xlink.XLinkConstants;
import org.opentosca.container.connector.winery.WineryConnector;

/**
 * Copyright 2017 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kálmán Képes - kalman.kepes@iaas.uni-stuttgart.de
 *
 */
@Path("/packager")
public class PackagerResource {

    @Context
    UriInfo uriInfo;

    private final WineryConnector connector = new WineryConnector();


    @GET
    @Produces(ResourceConstants.LINKED_XML)
    public Response getReferencesXML() {
        return Response.ok(this.getRefs().getXMLString()).build();
    }

    @GET
    @Produces(ResourceConstants.LINKED_JSON)
    public Response getReferencesJSON() {
        return Response.ok(this.getRefs().getJSONString()).build();
    }

    public References getRefs() {
        final References refs = new References();

        if (this.connector.isWineryRepositoryAvailable()) {
            refs.getReference()
                .add(new Reference(Utilities.buildURI(this.uriInfo.getAbsolutePath().toString(), "packages"),
                    XLinkConstants.SIMPLE, "servicetemplates"));
        }
        refs.getReference()
            .add(new Reference(this.uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
        return refs;
    }

    @Path("/packages")
    public PackagerPackagesResource getPackages() {
        return new PackagerPackagesResource();
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response createFromArtefact(@FormDataParam("file") final InputStream uploadedInputStream,
                    @FormDataParam("file") final FormDataContentDisposition fileDetail,
                    @FormDataParam("file") final FormDataBodyPart body,
                    @FormDataParam("artefactType") final QName artifactType,
                    @FormDataParam("nodeTypes") final Set<QName> nodeTypes,
                    @FormDataParam("infrastructureNodeType") final QName infrastructureNodeType,
                    @FormDataParam("tags") final Set<String> tags, @Context final UriInfo uriInfo)
        throws IllegalArgumentException, JAXBException, IOException {

        if (!this.connector.isWineryRepositoryAvailable()) {
            return Response.status(Status.SERVICE_UNAVAILABLE).build();
        }

        final File tempFile = this.inputStream2File(uploadedInputStream, fileDetail.getFileName());

        try {
            final QName xaasServiceTemplate = this.connector.createServiceTemplateFromXaaSPackage(tempFile,
                artifactType, nodeTypes, infrastructureNodeType, this.createTagMapFromTagSet(tags));
            final String redirectUrl = Utilities.buildURI(this.uriInfo.getAbsolutePath().toString(),
                "servicetemplates/" + Utilities.URLencode(xaasServiceTemplate.toString())).replace("packager",
                    "marketplace");
            return Response.created(URI.create(redirectUrl)).build();
        } catch (final URISyntaxException e) {
            e.printStackTrace();
        }
        return Response.serverError().build();
    }

    private Map<String, String> createTagMapFromTagSet(final Set<String> tags) {
        final Map<String, String> tagMap = new HashMap<>();

        for (final String tag : tags) {
            if (tag.contains(":")) {
                final String key = tag.split(":")[0];
                final String value = tag.split(":")[1];
                tagMap.put(key, value);
            } else {
                tagMap.put(tag, null);
            }
        }

        return tagMap;
    }

    private File inputStream2File(final InputStream is, final String fileName) {
        OutputStream out = null;
        File tempFile = null;
        try {
            ;

            tempFile = new File(Files.createTempDirectory("XaaSPackager").toFile(), fileName);
            tempFile.createNewFile();
            out = new FileOutputStream(tempFile);

            int read = 0;
            final byte[] bytes = new byte[1024];

            while ((read = is.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }

            is.close();
            out.close();
        } catch (final FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return tempFile;
    }

}
