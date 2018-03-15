package org.opentosca.container.api.legacy.resources.csar.servicetemplate.boundarydefinitions;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.container.api.legacy.osgi.servicegetter.ToscaServiceHandler;
import org.opentosca.container.api.legacy.resources.utilities.JSONUtils;
import org.opentosca.container.api.legacy.resources.utilities.ResourceConstants;
import org.opentosca.container.api.legacy.resources.utilities.Utilities;
import org.opentosca.container.api.legacy.resources.xlink.Reference;
import org.opentosca.container.api.legacy.resources.xlink.References;
import org.opentosca.container.api.legacy.resources.xlink.XLinkConstants;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.tosca.model.TBoundaryDefinitions.Properties.PropertyMappings;
import org.opentosca.container.core.tosca.model.TPropertyMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class BoundsProperties {

    private static final Logger LOG = LoggerFactory.getLogger(BoundsInterfacesResource.class);
    CSARID csarID = null;

    UriInfo uriInfo;


    public BoundsProperties(final CSARID csarID, final QName serviceTemplateID) {
        this.csarID = csarID;

        if (null == ToscaServiceHandler.getToscaEngineService()) {
            LOG.error("The ToscaEngineService is not alive.");
        }
    }

    @GET
    @Produces(ResourceConstants.LINKED_XML)
    public Response getPropertiesXML(@Context final UriInfo uriInfo) {

        if (this.csarID == null) {
            return Response.status(404).build();
        }

        LOG.trace("Return Boundary Definitions Properties for CSAR {}.", this.csarID);

        final References refs = this.getRefs(uriInfo);

        return Response.ok(refs.getXMLString()).build();
    }

    @GET
    @Produces(ResourceConstants.LINKED_JSON)
    public Response getPropertiesJSON(@Context final UriInfo uriInfo) {

        if (this.csarID == null) {
            return Response.status(404).build();
        }

        LOG.trace("Return Boundary Definitions Properties for CSAR {}.", this.csarID);

        final References refs = this.getRefs(uriInfo);

        return Response.ok(refs.getJSONString()).build();
    }

    private References getRefs(final UriInfo uriInfo) {
        final References refs = new References();
        // selflink
        refs.getReference()
            .add(new Reference(uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
        refs.getReference()
            .add(new Reference(Utilities.buildURI(uriInfo, "XMLFragments"), XLinkConstants.SIMPLE, "XMLFragments"));
        refs.getReference().add(new Reference(Utilities.buildURI(uriInfo, "PropertyMappings"), XLinkConstants.SIMPLE,
            "PropertyMappings"));
        return refs;
    }

    /**
     * Returns the Boundary Definitions Properties XML fragment content as XML. TODO This resource is
     * not scoped under a Service Template, thus, return all Bounds Properties of all STs.
     *
     * @param uriInfo
     * @return Response
     */
    @GET
    @Path("XMLFragments")
    @Produces(ResourceConstants.TOSCA_XML)
    public Response getPropertiesContentXML(@Context final UriInfo uriInfo) {

        if (this.csarID == null) {
            return Response.status(404).build();
        }

        LOG.trace("Return Boundary Definitions Properties XML for CSAR {}.", this.csarID);

        final StringBuilder builder =
            new StringBuilder().append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><XMLFragments>");
        final List<String> props = ToscaServiceHandler.getToscaEngineService().getToscaReferenceMapper()
                                                      .getServiceTemplateBoundsPropertiesContent(this.csarID);

        for (final String str : props) {
            builder.append(str.replace("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>", ""));
        }

        builder.append("</XMLFragments>");

        return Response.ok(builder.toString()).build();
    }

    /**
     * Returns the Boundary Definitions Properties JSON fragment content as XML. TODO This resource is
     * not scoped under a Service Template, thus, return all Bounds Properties of all STs.
     *
     * @param uriInfo
     * @return Response
     */
    @GET
    @Path("XMLFragments")
    @Produces(ResourceConstants.TOSCA_JSON)
    public Response getPropertiesContentJSON(@Context final UriInfo uriInfo) {

        if (this.csarID == null) {
            return Response.status(404).build();
        }

        LOG.trace("Return Boundary Definitions Properties XML for CSAR {}.", this.csarID);

        final JsonObject ret = new JsonObject();
        final JsonArray array = new JsonArray();
        ret.add("XMLFragments", array);
        final List<String> props = ToscaServiceHandler.getToscaEngineService().getToscaReferenceMapper()
                                                      .getServiceTemplateBoundsPropertiesContent(this.csarID);

        for (final String xml : props) {

            try {
                final DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                final InputSource source = new InputSource();
                source.setCharacterStream(new StringReader(xml));
                final Document doc = db.parse(source);

                array.addAll(new JSONUtils().xmlToJsonArray(doc.getElementsByTagName("Properties").item(0)
                                                               .getChildNodes()));

            }
            catch (final ParserConfigurationException e) {
                e.printStackTrace();
            }
            catch (final SAXException e) {
                e.printStackTrace();
            }
            catch (final IOException e) {
                e.printStackTrace();
            }

        }

        return Response.ok(ret.toString()).build();
    }

    @GET
    @Path("PropertyMappings")
    @Produces(ResourceConstants.TOSCA_XML)
    public PropertyMappings getMappingsXML() {
        return ToscaServiceHandler.getToscaEngineService().getToscaReferenceMapper()
                                  .getServiceTemplateBoundsPropertyMappings(this.csarID).get(0);
    }

    @GET
    @Path("PropertyMappings")
    @Produces(ResourceConstants.TOSCA_JSON)
    public Response getMappingsJSON() {

        final List<PropertyMappings> mappingsList =
            ToscaServiceHandler.getToscaEngineService().getToscaReferenceMapper()
                               .getServiceTemplateBoundsPropertyMappings(this.csarID);
        final JsonObject ret = new JsonObject();
        final JsonArray array = new JsonArray();
        ret.add("XMLFragments", array);

        for (final PropertyMappings mappings : mappingsList) {
            final JsonObject jMappings = new JsonObject();
            final JsonArray mappingArray = new JsonArray();
            jMappings.add("PropertyMappings", mappingArray);
            for (final TPropertyMapping mapping : mappings.getPropertyMapping()) {
                final JsonObject mappingObj = new JsonObject();
                mappingObj.addProperty("serviceTemplatePropertyRef", mapping.getServiceTemplatePropertyRef());
                mappingObj.addProperty("targetObjectRef",
                                       ToscaServiceHandler.getIXMLSerializer()
                                                          .marshalToString(mapping.getTargetObjectRef()));
                mappingObj.addProperty("targetPropertyRef", mapping.getTargetPropertyRef());
                mappingArray.add(mappingObj);
            }
            array.add(jMappings);
        }

        return Response.ok(ret.toString()).build();
    }

}
