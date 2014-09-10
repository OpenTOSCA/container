package org.opentosca.containerapi.resources.credentials;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.opentosca.containerapi.osgi.servicegetter.CredentialsServiceHandler;
import org.opentosca.containerapi.resources.credentials.jaxb.CredentialsJaxb;
import org.opentosca.containerapi.resources.credentials.jaxb.JaxbFactory;
import org.opentosca.core.credentials.service.ICoreCredentialsService;
import org.opentosca.core.model.credentials.Credentials;
import org.opentosca.exceptions.SystemException;
import org.opentosca.exceptions.UserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resource of credentials of the Core Credentials Service.<br />
 * <br />
 * Copyright 2013 IAAS University of Stuttgart<br />
 * <br />
 * 
 * @author Rene Trefft - rene.trefft@developers.opentosca.org
 * 
 */
public class CredentialsResource {
	
	private static Logger LOG = LoggerFactory.getLogger(CredentialsResource.class);
	
	private final ICoreCredentialsService CREDENTIALS_SERVICE = CredentialsServiceHandler.getCredentialsService();
	
	/**
	 * If {@code null} it's a not existing credentials and every method returns
	 * 404 (not found).
	 */
	private final Credentials CREDENTIALS;
	
	
	/**
	 * Creates a {@link CredentialsResource} of {@code credentials}.
	 * 
	 * @param credentials
	 */
	public CredentialsResource(Credentials credentials) {
		CredentialsResource.LOG.debug("{} created: {}", this.getClass(), this);
		this.CREDENTIALS = credentials;
	}
	
	/**
	 * @return 200 (OK) with entity {@link CredentialsJaxb} containing the
	 *         credentials to show. If credentials not exist 404 (not found).
	 */
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Response getCredentials() {
		
		if (this.CREDENTIALS == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		
		return Response.ok(JaxbFactory.createCredentialsJaxb(this.CREDENTIALS)).build();
		
	}
	
	/**
	 * Deletes this credentials in the Core Credentials Service.
	 * 
	 * @return 200 (OK) - credentials were deleted.<br />
	 *         404 (not found) - credentials don't exist.<br />
	 *         500 (internal server error) - deleting failed.
	 * @throws UserException
	 * 
	 * @see Credentials
	 * @see ICoreCredentialsService#deleteCredentials(String, String)
	 */
	@DELETE
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public Response deleteCredentials() throws UserException {
		
		if (this.CREDENTIALS == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		
		// try {
		
		this.CREDENTIALS_SERVICE.deleteCredentials(this.CREDENTIALS.getID());
		return Response.ok("Credentials for storage provider \"" + this.CREDENTIALS.getStorageProviderID() + "\" were deleted.").build();
		
		// } catch (UserException exc) {
		// CredentialsResource.LOG.warn("An User Exception occured.", exc);
		// }
		
		// return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		
	}
	
	/**
	 * Sets or deletes these credentials in their storage provider if
	 * {@code set} respectively {@code unset} is passed in {@code input} (body
	 * of a POST message).
	 * 
	 * @param input
	 * @return 200 (OK) - credentials were set respectively deleted in their
	 *         storage provider.<br />
	 *         400 (bad request) - credentials are currently not set in their
	 *         storage provider or neither {@code set} nor {@code unset} were
	 *         passed.<br />
	 *         404 (not found) - credentials don't exist.<br />
	 *         500 (internal server error) - setting / deleting credentials
	 *         failed.
	 * @throws SystemException
	 * @throws UserException
	 * 
	 * @see ICoreCredentialsService#setCredentialsInStorageProvider(String,
	 *      String)
	 * @see ICoreCredentialsService#hasStorageProviderCredentials(Long)
	 * @see ICoreCredentialsService#deleteCredentialsInStorageProvider(String)
	 */
	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public Response setOrUnsetCredentialsInStorageProvider(String input) throws SystemException, UserException {
		
		if (this.CREDENTIALS == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		
		long credentialsID = this.CREDENTIALS.getID();
		String storageProviderID = this.CREDENTIALS.getStorageProviderID();
		
		// try {
		
		if (input.equalsIgnoreCase("set")) {
			
			this.CREDENTIALS_SERVICE.setCredentialsInStorageProvider(credentialsID);
			return Response.ok("Credentials \"" + credentialsID + "\" were set in their storage provider \"" + storageProviderID + "\".").build();
			
		} else if (input.equalsIgnoreCase("unset")) {
			
			// we must check at first if storage provider has currently
			// these credentials
			boolean hasThisCredentials = this.CREDENTIALS_SERVICE.hasStorageProviderCredentials(credentialsID);
			
			if (hasThisCredentials) {
				this.CREDENTIALS_SERVICE.deleteCredentialsInStorageProvider(storageProviderID);
				return Response.ok("Credentials \"" + credentialsID + "\" were deleted in their storage provider \"" + storageProviderID + "\".").build();
			} else {
				return Response.status(Status.BAD_REQUEST).entity("Credentials \"" + credentialsID + "\" are not set in their storage provider \"" + storageProviderID + "\".").build();
			}
			
		} else {
			
			return Response.status(Status.BAD_REQUEST).build();
			
		}
		
		// } catch (UserException exc) {
		// CredentialsResource.LOG.warn("A User exception occured.", exc);
		// } catch (SystemException exc) {
		// CredentialsResource.LOG.warn("A System exception occured.", exc);
		// }
		
		// return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		
	}
}
