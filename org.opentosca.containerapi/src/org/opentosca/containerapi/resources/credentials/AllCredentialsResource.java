package org.opentosca.containerapi.resources.credentials;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.opentosca.containerapi.osgi.servicegetter.CredentialsServiceHandler;
import org.opentosca.containerapi.resources.credentials.jaxb.AllCredentialsJaxb;
import org.opentosca.containerapi.resources.credentials.jaxb.JaxbFactory;
import org.opentosca.containerapi.resources.utilities.ResourceConstants;
import org.opentosca.containerapi.resources.utilities.Utilities;
import org.opentosca.core.credentials.service.ICoreCredentialsService;
import org.opentosca.core.model.credentials.Credentials;
import org.opentosca.exceptions.UserException;

/**
 * Resource of all credentials of the Core Credentials Service.<br />
 * <br />
 * Copyright 2013 IAAS University of Stuttgart<br />
 * <br />
 * 
 * @author Rene Trefft - rene.trefft@developers.opentosca.org
 * 
 */
@Path("/Credentials")
public class AllCredentialsResource {
	
	// private static Logger LOG =
	// LoggerFactory.getLogger(AllCredentialsResource.class);
	
	private final ICoreCredentialsService CREDENTIALS_SERVICE = CredentialsServiceHandler.getCredentialsService();
	
	@Context
	private UriInfo uriInfo;
	@Context
	private Request request;
	
	
	/**
	 * @return 200 (OK) with entity {@link AllCredentialsJaxb} containing all
	 *         credentials.
	 */
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Response getAllCredentialsXML(@QueryParam("storageProviderID") String storageProviderID) {
		return Response.ok(JaxbFactory.createAllCredentialsJaxb(storageProviderID)).build();
	}
	
	/**
	 * Creates a {@link CredentialsResource} of credentials
	 * {@code credentialsIDAsString}. If these credentials are not stored it
	 * will be created with {@code null}.<br/>
	 * <br />
	 * A resource of credentials is located at:<br />
	 * {@code ...\<credentialsID>}
	 * 
	 * @param credentialsIDAsString of credentials
	 * @return {@link CredentialsResource}
	 * @throws UserException
	 */
	@Path("{credentialsID}")
	public CredentialsResource getCredentialsResource(@PathParam("credentialsID") String credentialsIDAsString) throws UserException {
		
		Credentials credentials = null;
		
		try {
			long credentialsID = Long.parseLong(credentialsIDAsString);
			credentials = this.CREDENTIALS_SERVICE.getCredentials(credentialsID);
		} catch (NumberFormatException exc) {
			throw new UserException("Credentials ID must be a whole number.", exc);
		}
		
		return new CredentialsResource(credentials);
		
	}
	
	/**
	 * Stores the {@code credentials} given as XML in the body of a POST message
	 * in the Core Credentials Service.
	 * 
	 * @param credentials to store.
	 * @return 200 (OK) - credentials were successfully stored.<br />
	 *         500 (internal server error) - storing credentials failed.
	 * @throws UserException
	 * 
	 * @see Credentials
	 * @see ICoreCredentialsService#storeCredentials(Credentials)
	 */
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.TEXT_PLAIN)
	public Response storeCredentials(Credentials credentials) throws UserException {
		
		// try {
		
		Long credentialsID = this.CREDENTIALS_SERVICE.storeCredentials(credentials);
		
		return Response.created(URI.create(Utilities.buildURI(this.uriInfo.getAbsolutePath().toString(), Long.toString(credentialsID)))).build();
		
		// } catch (UserException exc) {
		// AllCredentialsResource.LOG.warn("An User Exception occured.", exc);
		// }
		
		// return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		
	}
	
	/**
	 * Deletes all credentials in the Core Credentials Service in case of a
	 * DELETE message (with arbitrary body).
	 * 
	 * @param input
	 * @return 200 (OK)
	 * 
	 * @see Credentials
	 * @see ICoreCredentialsService#deleteAllCredentials()
	 */
	@DELETE
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(ResourceConstants.TEXT_PLAIN)
	public Response deleteAllCredentials() {
		
		this.CREDENTIALS_SERVICE.deleteAllCredentials();
		return Response.ok("All credentials were deleted.").build();
		
	}
}
