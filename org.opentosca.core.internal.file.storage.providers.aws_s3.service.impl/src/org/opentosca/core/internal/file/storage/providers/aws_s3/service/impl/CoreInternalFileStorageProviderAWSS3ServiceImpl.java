package org.opentosca.core.internal.file.storage.providers.aws_s3.service.impl;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.jclouds.aws.domain.Region;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.opentosca.core.internal.file.storage.providers.service.AbstractJCloudsFileStorageProvider;
import org.opentosca.core.internal.file.storage.providers.service.ICoreInternalFileStorageProviderService;

/**
 * Amazon Simple Storage Service (S3) storage provider.<br />
 * It's implemented by extending from {@link AbstractJCloudsFileStorageProvider}
 * , so it uses jclouds to access the provider. This class primarily contains
 * meta data of the storage provider and some necessary jclouds configuration.<br />
 * <br />
 * A CSAR file will be stored as follows on Amazon S3:<br />
 * {@code <bucketName>/<csarID>/<relPathOfFileToCSARRoot>} <br />
 * <br />
 * By default, the bucket name is {@code org.opentosca.csars}. Setting a new
 * bucket name or getting the current one is possible via OSGi console commands.<br />
 * <br />
 * Note: If you have stored files on this storage provider and changes the
 * bucket name (creates a new bucket) these files can't be found anymore,
 * because the storage provider searches for the files in the new bucket. Thus,
 * for retrieving the files again, you must switch back to the previous bucket.<br />
 * <br />
 * Copyright 2013 IAAS University of Stuttgart<br />
 * <br />
 * 
 * @author Rene Trefft - rene.trefft@developers.opentosca.org
 * 
 */
public class CoreInternalFileStorageProviderAWSS3ServiceImpl extends AbstractJCloudsFileStorageProvider implements ICoreInternalFileStorageProviderService, CommandProvider {
	
	/**
	 * Storage provider ID.<br />
	 * It's a storage provider based on jclouds, so the ID must be equal to the
	 * jclouds Amazon S3 Provider ID.
	 */
	private final String STORAGE_PROVIDER_ID = "aws-s3";
	
	/**
	 * Storage provider name.
	 */
	private final String STORAGE_PROVIDER_NAME = "Amazon Simple Storage Service (S3)";
	
	/**
	 * Location of the bucket. It's hard-coded to EU (Ireland).
	 */
	private final Location CONTAINER_LOCATION = new LocationBuilder().id(Region.EU_WEST_1).description(Region.EU_WEST_1).scope(LocationScope.REGION).build();
	
	/**
	 * Name of the identity as part of the credentials for this storage
	 * provider.
	 */
	private final String IDENTITY_NAME = "Access Key ID";
	
	/**
	 * Name of the key as part of the credentials for this storage provider.
	 */
	private final String KEY_NAME = "Secret Access Key";
	
	
	@Override
	public String getStorageProviderID() {
		return this.STORAGE_PROVIDER_ID;
	}
	
	@Override
	public String getStorageProviderName() {
		return this.STORAGE_PROVIDER_NAME;
	}
	
	@Override
	public boolean needsCredentials() {
		return true;
	}
	
	@Override
	public String getCredentialsIdentityName() {
		return this.IDENTITY_NAME;
	}
	
	@Override
	public String getCredentialsKeyName() {
		return this.KEY_NAME;
	}
	
	@Override
	protected Location getContainerLocation() {
		return this.CONTAINER_LOCATION;
	}
	
	/**
	 * Prints the available OSGi commands.
	 */
	@Override
	public String getHelp() {
		StringBuilder help = new StringBuilder();
		help.append("--- Amazon S3 Storage Provider Management ---\n");
		help.append("\tsetAWSS3BucketName <bucketName> - Sets a new name for the S3 bucket in that the files will be stored / retrieved from.\n");
		help.append("\tprintAWSS3BucketName - Prints the name of the S3 bucket in that the files will be stored / retrieved from.\n");
		return help.toString();
	}
	
	/**
	 * OSGi commands.
	 */
	
	public void _setAWSS3BucketName(CommandInterpreter ci) {
		
		String bucketName = ci.nextArgument();
		String tooManyArguments = ci.nextArgument();
		
		boolean inputInvalid = false;
		
		if (bucketName == null) {
			ci.println("Bucket name not given.");
			inputInvalid = true;
		}
		if (tooManyArguments != null) {
			ci.println("Too many arguments were given. Arguments must be exactly one.");
			inputInvalid = true;
		}
		
		if (!inputInvalid) {
			this.setContainerName(bucketName);
			ci.println("Bucket name \"" + bucketName + "\" was set in storage provider \"" + this.getStorageProviderID() + "\".");
		}
		
	}
	
	public void _printAWSS3BucketName(CommandInterpreter ci) {
		String bucketName = this.getContainerName();
		ci.println("Bucket name of storage provider \"" + this.getStorageProviderID() + "\": " + bucketName);
	}
	
}
