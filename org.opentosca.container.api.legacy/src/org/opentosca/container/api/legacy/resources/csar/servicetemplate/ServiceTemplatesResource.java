package org.opentosca.container.api.legacy.resources.csar.servicetemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.opentosca.container.api.legacy.osgi.servicegetter.FileRepositoryServiceHandler;
import org.opentosca.container.api.legacy.osgi.servicegetter.ToscaServiceHandler;
import org.opentosca.container.api.legacy.resources.utilities.ResourceConstants;
import org.opentosca.container.api.legacy.resources.utilities.Utilities;
import org.opentosca.container.api.legacy.resources.xlink.Reference;
import org.opentosca.container.api.legacy.resources.xlink.References;
import org.opentosca.container.api.legacy.resources.xlink.XLinkConstants;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.model.AbstractFile;
import org.opentosca.container.core.model.csar.CSARContent;
import org.opentosca.container.core.tosca.model.Definitions;
import org.opentosca.container.core.tosca.model.TExtensibleElements;
import org.opentosca.container.core.tosca.model.TServiceTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Copyright 2016 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author christian.endres@iaas.uni-stuttgart.de
 *
 */
public class ServiceTemplatesResource {

    private final Logger log = LoggerFactory.getLogger(ServiceTemplatesResource.class);
    private final CSARContent csarContent;
    UriInfo uriInfo;


    public ServiceTemplatesResource(final CSARContent csar) {

        this.csarContent = csar;
        this.log.info("{} created: {}", this.getClass(), this);
    }

    @GET
    @Produces(ResourceConstants.LINKED_XML)
    public Response getReferencesXML(@Context final UriInfo uriInfo,
                                     @DefaultValue("false") @QueryParam("main") final boolean onlyMainServiceTemplate) throws UnsupportedEncodingException,
                                                                                                                       UserException,
                                                                                                                       SystemException {
        this.uriInfo = uriInfo;
        return Response.ok(this.getRefs(onlyMainServiceTemplate).getXMLString()).build();
    }

    @GET
    @Produces(ResourceConstants.LINKED_JSON)
    public Response getReferencesJSON(@Context final UriInfo uriInfo,
                                      @QueryParam("main") final boolean onlyMainServiceTemplate) throws UnsupportedEncodingException,
                                                                                                 UserException,
                                                                                                 SystemException {
        this.uriInfo = uriInfo;
        return Response.ok(this.getRefs(onlyMainServiceTemplate).getJSONString()).build();
    }

    public References getRefs(final boolean onlyMainServiceTemplate) throws UnsupportedEncodingException, UserException,
                                                                     SystemException {

        if (this.csarContent == null) {
            return null;
        }

        final References refs = new References();

        if (onlyMainServiceTemplate) {
            this.log.debug("Only reference to main Service Template is requested.");
            final String st = this.getEntryServiceTemplateName();
            refs.getReference().add(new Reference(Utilities.buildURI(this.uriInfo.getAbsolutePath().toString(), st),
                XLinkConstants.SIMPLE, st));

        } else {
            for (final QName qname : ToscaServiceHandler.getToscaEngineService()
                                                        .getServiceTemplatesInCSAR(this.csarContent.getCSARID())) {
                final String name = URLEncoder.encode(qname.toString(), "UTF-8");
                refs.getReference().add(new Reference(
                    Utilities.buildURI(this.uriInfo.getAbsolutePath().toString(), name), XLinkConstants.SIMPLE, name));
            }
        }

        // selflink
        refs.getReference()
            .add(new Reference(this.uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));

        return refs;
    }

    private String getEntryServiceTemplateName() throws UserException, SystemException, UnsupportedEncodingException {

        final AbstractFile root =
            FileRepositoryServiceHandler.getFileHandler().getCSAR(this.csarContent.getCSARID()).getRootTOSCA();
        final Definitions def = ToscaServiceHandler.getIXMLSerializer().unmarshal(root.getFileAsInputStream());

        for (final TExtensibleElements el : def.getServiceTemplateOrNodeTypeOrNodeTypeImplementation()) {
            if (el instanceof TServiceTemplate) {
                final TServiceTemplate st = (TServiceTemplate) el;
                final QName qn = new QName(st.getTargetNamespace(), st.getId());
                return URLEncoder.encode(qn.toString(), "UTF-8");
            }
        }

        return null;
    }

    @Path("{qname}")
    public ServiceTemplateResource getServiceTemplate(@PathParam("qname") final String qname) throws UnsupportedEncodingException {
        this.log.debug("Create Service Template resource for {}", qname);
        return new ServiceTemplateResource(this.csarContent, URLDecoder.decode(qname, "UTF-8"));
    }
}
