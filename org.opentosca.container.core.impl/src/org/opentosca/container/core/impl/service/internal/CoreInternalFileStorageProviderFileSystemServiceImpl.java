package org.opentosca.container.core.impl.service.internal;

import java.util.Properties;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.jclouds.filesystem.reference.FilesystemConstants;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.service.internal.AbstractJCloudsFileStorageProvider;
import org.opentosca.container.core.service.internal.ICoreInternalFileStorageProviderService;

/**
 * File system storage provider. It's implemented by extending from
 * {@link AbstractJCloudsFileStorageProvider}, so it uses jclouds to access the
 * file system. This class primarily contains meta data of the storage provider
 * and some necessary jclouds configuration like the location of the file
 * system-based blobstore.
 *
 * A CSAR file will be stored at the following path:
 * {@code <BlobStorePath>/<containerName>/<csarID>/<relPathOfFileToCSARRoot>}
 *
 * The blobstore path is defined in {@link #CSAR_STORE_PATH}. By default, the
 * container name is {@code org.opentosca.csars}. Setting a new container name
 * or getting the current one is possible via OSGi console commands.
 *
 * Note: If you have stored files on this storage provider and changes the
 * container name (creates new container) these files can't be found anymore,
 * because the storage provider searches for the files in the new container.
 * Thus, for retrieving the files again you must switch back to the previous
 * container.
 */
public class CoreInternalFileStorageProviderFileSystemServiceImpl extends AbstractJCloudsFileStorageProvider implements ICoreInternalFileStorageProviderService, CommandProvider {
	
	/**
	 * Storage provider ID.<br />
	 * It's a storage provider based on jclouds, so the ID must be equal to the
	 * jclouds file system API ID.
	 */
	private final String STORAGE_PROVIDER_ID = "filesystem";
	
	/**
	 * Storage provider Name.
	 */
	private final String STORAGE_PROVIDER_NAME = "Filesystem-based BlobStore";
	
	/**
	 * Absolute path where the file system-based blobstore should be located.
	 *
	 * @see org.opentosca.settings.Settings
	 */
	private final String CSAR_STORE_PATH = Settings.getSetting("csarStorePath");
	
	
	@Override
	public String getStorageProviderID() {
		return this.STORAGE_PROVIDER_ID;
	}
	
	@Override
	public String getStorageProviderName() {
		return this.STORAGE_PROVIDER_NAME;
	}
	
	@Override
	protected Properties overwriteJCloudsProperties() {
		final Properties props = super.overwriteJCloudsProperties();
		// location of blobstore must be set as a jclouds property.
		props.setProperty(FilesystemConstants.PROPERTY_BASEDIR, this.CSAR_STORE_PATH);
		return props;
	}
	
	@Override
	public boolean needsCredentials() {
		return false;
	}
	
	@Override
	public String getCredentialsIdentityName() {
		return null;
	}
	
	@Override
	public String getCredentialsKeyName() {
		return null;
	}
	
	/**
	 * Prints the available OSGi commands.
	 */
	@Override
	public String getHelp() {
		final StringBuilder help = new StringBuilder();
		help.append("--- File System Storage Provider Management ---\n");
		help.append("\tsetFilesystemContainerName <containerName> - Sets a new name for the container in that the files will be stored / retrieved from.\n");
		help.append("\tprintFilesystemContainerName - Prints the name of the container in that the files will be stored / retrieved from.\n");
		return help.toString();
	}
	
	/**
	 * OSGi commands.
	 */
	
	public void _setFilesystemContainerName(final CommandInterpreter ci) {
		
		final String containerName = ci.nextArgument();
		final String tooManyArguments = ci.nextArgument();
		
		boolean inputInvalid = false;
		
		if (containerName == null) {
			ci.println("Container name not given.");
			inputInvalid = true;
		}
		if (tooManyArguments != null) {
			ci.println("Too many arguments were given. Arguments must be exactly one.");
			inputInvalid = true;
		}
		
		if (!inputInvalid) {
			this.setContainerName(containerName);
			ci.println("Container name \"" + containerName + "\" was set in storage provider \"" + this.getStorageProviderID() + "\".");
		}
		
	}
	
	public void _printFilesystemContainerName(final CommandInterpreter ci) {
		final String containerName = this.getContainerName();
		ci.println("Container name of storage provider \"" + this.getStorageProviderID() + "\": " + containerName);
	}
	
}
