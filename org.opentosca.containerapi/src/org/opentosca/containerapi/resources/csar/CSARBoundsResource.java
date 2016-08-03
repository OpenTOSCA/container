package org.opentosca.containerapi.resources.csar;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.opentosca.containerapi.osgi.servicegetter.ToscaServiceHandler;
import org.opentosca.containerapi.resources.utilities.ResourceConstants;
import org.opentosca.containerapi.resources.utilities.Utilities;
import org.opentosca.containerapi.resources.xlink.Reference;
import org.opentosca.containerapi.resources.xlink.References;
import org.opentosca.containerapi.resources.xlink.XLinkConstants;
import org.opentosca.core.model.csar.id.CSARID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CSARBoundsResource {

    private static final Logger LOG = LoggerFactory.getLogger(CSARBoundsResource.class);

    private CSARID csarID;

    public CSARBoundsResource(CSARID csarid) {

	csarID = csarid;

	if (null == ToscaServiceHandler.getToscaEngineService()) {
	    LOG.error("The ToscaEngineService is not alive.");
	}

	LOG.info("{} created: {}", this.getClass(), this);
	LOG.debug("Public Plans for requested CSAR: {}", csarID.getFileName());
    }

    /**
     * Builds the references of the Boundary Definitions of a CSAR.
     * 
     * @param uriInfo
     * @return Response
     */
    @GET
    @Produces(ResourceConstants.LINKED_XML)
    public Response getReferences(@Context UriInfo uriInfo) {

	if (csarID == null) {
	    return Response.status(404).build();
	}

	LOG.trace("Return Boundary Definitions for CSAR {}.", csarID);

	References refs = new References();

	refs.getReference().add(new Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(), "Properties"),
	    XLinkConstants.SIMPLE, "Properties"));
	refs.getReference()
	.add(new Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(), "PropertyConstraints"),
	    XLinkConstants.SIMPLE, "PropertyConstraints"));
	refs.getReference().add(new Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(), "Requirements"),
	    XLinkConstants.SIMPLE, "Requirements"));
	refs.getReference().add(new Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(), "Capabilities"),
	    XLinkConstants.SIMPLE, "Capabilities"));
	refs.getReference().add(new Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(), "Policies"),
	    XLinkConstants.SIMPLE, "Policies"));
	refs.getReference().add(new Reference(Utilities.buildURI(uriInfo.getAbsolutePath().toString(), "Interfaces"),
	    XLinkConstants.SIMPLE, "Interfaces"));

	// selflink
	refs.getReference()
	.add(new Reference(uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
	return Response.ok(refs.getXMLString()).build();
    }

    /**
     * Returns the Boundary Definitions Properties. TODO not yet implemented
     * yet, thus, just returns itself.
     * 
     * @param uriInfo
     * @return Response
     */
    @GET
    @Path("Properties")
    @Produces(ResourceConstants.LINKED_XML)
    public Response getProperties(@Context UriInfo uriInfo) {

	if (csarID == null) {
	    return Response.status(404).build();
	}

	LOG.trace("Return Boundary Definitions for CSAR {}.", csarID);

	References refs = new References();
	// selflink
	refs.getReference()
	.add(new Reference(uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
	return Response.ok(refs.getXMLString()).build();
    }

    /**
     * Returns the Boundary Definitions Properties. TODO not yet implemented
     * yet, thus, just returns itself.
     * 
     * @param uriInfo
     * @return Response
     */
    @GET
    @Path("PropertyConstraints")
    @Produces(ResourceConstants.LINKED_XML)
    public Response getPropertyConstraints(@Context UriInfo uriInfo) {

	if (csarID == null) {
	    return Response.status(404).build();
	}

	LOG.trace("Return Boundary Definitions for CSAR {}.", csarID);

	References refs = new References();
	// selflink
	refs.getReference()
	.add(new Reference(uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
	return Response.ok(refs.getXMLString()).build();
    }

    /**
     * Returns the Boundary Definitions Properties. TODO not yet implemented
     * yet, thus, just returns itself.
     * 
     * @param uriInfo
     * @return Response
     */
    @GET
    @Path("Requirements")
    @Produces(ResourceConstants.LINKED_XML)
    public Response getRequirements(@Context UriInfo uriInfo) {

	if (csarID == null) {
	    return Response.status(404).build();
	}

	LOG.trace("Return Boundary Definitions for CSAR {}.", csarID);

	References refs = new References();
	// selflink
	refs.getReference()
	.add(new Reference(uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
	return Response.ok(refs.getXMLString()).build();
    }

    /**
     * Returns the Boundary Definitions Properties. TODO not yet implemented
     * yet, thus, just returns itself.
     * 
     * @param uriInfo
     * @return Response
     */
    @GET
    @Path("Capabilities")
    @Produces(ResourceConstants.LINKED_XML)
    public Response getCapabilities(@Context UriInfo uriInfo) {

	if (csarID == null) {
	    return Response.status(404).build();
	}

	LOG.trace("Return Boundary Definitions for CSAR {}.", csarID);

	References refs = new References();
	// selflink
	refs.getReference()
	.add(new Reference(uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
	return Response.ok(refs.getXMLString()).build();
    }

    /**
     * Returns the Boundary Definitions Properties. TODO not yet implemented
     * yet, thus, just returns itself.
     * 
     * @param uriInfo
     * @return Response
     */
    @GET
    @Path("Policies")
    @Produces(ResourceConstants.LINKED_XML)
    public Response getPolicies(@Context UriInfo uriInfo) {

	if (csarID == null) {
	    return Response.status(404).build();
	}

	LOG.trace("Return Boundary Definitions for CSAR {}.", csarID);

	References refs = new References();
	// selflink
	refs.getReference()
	.add(new Reference(uriInfo.getAbsolutePath().toString(), XLinkConstants.SIMPLE, XLinkConstants.SELF));
	return Response.ok(refs.getXMLString()).build();
    }

    /**
     * Returns a PublicPlan for a given Index.
     * 
     * @param planName
     * @return the PublicPlan
     */
    @Path("Interfaces")
    public CSARBoundsInterfacesResource getInterfaces(@Context UriInfo uriInfo) {
	return new CSARBoundsInterfacesResource(csarID);
    }



}
