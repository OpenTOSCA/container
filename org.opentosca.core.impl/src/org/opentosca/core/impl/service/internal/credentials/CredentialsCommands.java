package org.opentosca.core.impl.service.internal.credentials;

import java.util.Set;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.opentosca.core.common.SystemException;
import org.opentosca.core.common.UserException;
import org.opentosca.core.impl.service.internal.CoreInternalCredentialsServiceImpl;
import org.opentosca.core.model.credentials.Credentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides OSGi console commands of the Core Internal Credentials Service.
 *
 * @see CoreInternalCredentialsServiceImpl
 */
public class CredentialsCommands implements CommandProvider {
	
	private final static Logger LOG = LoggerFactory.getLogger(CredentialsCommands.class);
	
	private final CoreInternalCredentialsServiceImpl INTERNAL_CREDENTIALS_SERVICE = new CoreInternalCredentialsServiceImpl();
	
	
	/**
	 * Prints the available OSGi commands.
	 */
	@Override
	public String getHelp() {
		final StringBuilder help = new StringBuilder();
		help.append("--- Core Credentials Service Management ---\n");
		help.append("\tstoreCredentials <storageProviderID> <identity> <key> <optional description> - Stores credentials for a storage provider.\n");
		help.append("\tprintCredentialsIDs - Prints IDs of all stored credentials.\n");
		help.append("\tprintAllCredentials - Prints all stored credentials.\n");
		help.append("\tprintAllCredentialsIdentityAndKeyNames  - Prints the credentials identity and key names of all available storage providers.\n");
		help.append("\tdeleteCredentials <storageProviderID> <identity> - Deletes credentials for a storage provider.\n");
		help.append("\tdeleteAllCredentials - Deletes all stored credentials.\n");
		help.append("\tsetCredentialsInStorageProvider <storageProviderID> <identity> - Sets already stored credentials in the associated storage provider.\n");
		help.append("\tdeleteCredentialsInStorageProvider <storageProviderID> - Deletes credentials in the storage provider.\n");
		return help.toString();
	}
	
	/**
	 * OSGi commands.
	 */
	
	public void _storeCredentials(final CommandInterpreter ci) {
		
		final String storageProviderID = ci.nextArgument();
		final String identity = ci.nextArgument();
		final String key = ci.nextArgument();
		String description = ci.nextArgument();
		
		if (description != null) {
			
			final StringBuilder descriptionStringBuilder = new StringBuilder(description);
			
			// If description consists of more than one word (arguments) we must
			// separate them by spaces.
			while ((description = ci.nextArgument()) != null) {
				descriptionStringBuilder.append(" " + description);
			}
			
			description = descriptionStringBuilder.toString();
			
		}
		
		boolean inputInvalid = false;
		
		if (storageProviderID == null) {
			ci.println("Storage provider ID not given.");
			inputInvalid = true;
		}
		if (identity == null) {
			ci.println("Identity not given.");
			inputInvalid = true;
		}
		if (key == null) {
			ci.println("Key not given.");
			inputInvalid = true;
		}
		
		if (!inputInvalid) {
			final Credentials credentials = new Credentials(storageProviderID, identity, key, description);
			try {
				this.INTERNAL_CREDENTIALS_SERVICE.storeCredentials(credentials);
				ci.println("Storing credentials for storage provider \"" + storageProviderID + "\" was successfull.");
			} catch (final UserException exc) {
				CredentialsCommands.LOG.warn("An User Exception occured.", exc);
				ci.println("Storing credentials for storage provider \"" + storageProviderID + "\" failed.");
			}
		}
		
	}
	
	public void _printCredentialsIDs(final CommandInterpreter ci) {
		
		final Set<Long> credentialsIDs = this.INTERNAL_CREDENTIALS_SERVICE.getCredentialsIDs();
		
		if (credentialsIDs.isEmpty()) {
			
			ci.print("No credentials are stored.");
			
		} else {
			
			for (final Long credentialsID : credentialsIDs) {
				ci.println("ID: " + credentialsID);
			}
			
		}
		
	}
	
	public void _printAllCredentials(final CommandInterpreter ci) {
		
		final Set<Credentials> allCredentials = this.INTERNAL_CREDENTIALS_SERVICE.getAllCredentials();
		
		if (allCredentials.isEmpty()) {
			
			ci.print("No credentials are stored.");
			
		} else {
			
			for (final Credentials credentials : allCredentials) {
				
				final long credentialsID = credentials.getID();
				final String storageProviderID = credentials.getStorageProviderID();
				final String identity = credentials.getIdentity();
				final String key = credentials.getKey();
				final String description = credentials.getDescription();
				
				boolean injected = false;
				
				try {
					injected = this.INTERNAL_CREDENTIALS_SERVICE.hasStorageProviderCredentials(credentialsID);
				} catch (final UserException exc) {
					CredentialsCommands.LOG.warn("An User Exception occured.", exc);
				}
				
				ci.println("-------------------------------------");
				ci.println("ID:                           " + credentialsID);
				ci.println("Storage Provider ID:          " + storageProviderID);
				ci.println("Identity:                     " + identity);
				ci.println("Key:                          " + key);
				if ((description != null) && !description.trim().isEmpty()) {
					ci.println("Description:                  " + credentials.getDescription());
				}
				
				ci.println("Injected in storage provider: " + injected);
				
			}
			
		}
		
	}
	
	public void _printAllCredentialsIdentityAndKeyNames(final CommandInterpreter ci) {
		
		final Set<String> storageProviders = this.INTERNAL_CREDENTIALS_SERVICE.getStorageProviders();
		
		if (storageProviders.isEmpty()) {
			
			ci.print("No storage providers are available.");
			
		} else {
			
			ci.println("Credentials identity and key names of all available storage providers:");
			for (final String storageProviderID : storageProviders) {
				ci.println("----- " + storageProviderID + " -----");
				
				try {
					
					if (this.INTERNAL_CREDENTIALS_SERVICE.needsStorageProviderCredentials(storageProviderID)) {
						
						ci.println("Identity name: " + this.INTERNAL_CREDENTIALS_SERVICE.getCredentialsIdentityName(storageProviderID));
						ci.println("Key name:      " + this.INTERNAL_CREDENTIALS_SERVICE.getCredentialsKeyName(storageProviderID));
						
					} else {
						
						ci.println("Needs no credentials.");
						
					}
					
				} catch (final SystemException exc) {
					CredentialsCommands.LOG.warn("A System Exception occured.", exc);
				}
				
			}
			
		}
		
	}
	
	public void _deleteCredentials(final CommandInterpreter ci) {
		
		final String credentialsIDAsString = ci.nextArgument();
		final String tooManyArguments = ci.nextArgument();
		
		boolean inputInvalid = false;
		
		if (credentialsIDAsString == null) {
			ci.println("Credentials ID not given.");
			inputInvalid = true;
		}
		
		long credentialsID = 0;
		
		try {
			credentialsID = Long.parseLong(credentialsIDAsString);
		} catch (final NumberFormatException exc) {
			CredentialsCommands.LOG.warn("Credentials ID must be a whole number.", exc);
			inputInvalid = true;
		}
		
		if (tooManyArguments != null) {
			ci.println("Too many arguments were given. Arguments must be exactly one.");
			inputInvalid = true;
		}
		
		if (!inputInvalid) {
			try {
				this.INTERNAL_CREDENTIALS_SERVICE.deleteCredentials(credentialsID);
				ci.println("Deleting credentials \"" + credentialsID + "\" was successfull.");
			} catch (final UserException exc) {
				CredentialsCommands.LOG.warn("An User Exception occured.", exc);
				ci.println("Deleting credentials \"" + credentialsID + "\" failed.");
			}
		}
		
	}
	
	public void _deleteAllCredentials(final CommandInterpreter ci) {
		this.INTERNAL_CREDENTIALS_SERVICE.deleteAllCredentials();
		ci.println("Deleting all credentials was successfull.");
	}
	
	public void _setCredentialsInStorageProvider(final CommandInterpreter ci) {
		
		final String credentialsIDAsString = ci.nextArgument();
		final String tooManyArguments = ci.nextArgument();
		
		boolean inputInvalid = false;
		
		if (credentialsIDAsString == null) {
			ci.println("Credentials ID not given.");
			inputInvalid = true;
		}
		
		long credentialsID = 0;
		
		try {
			credentialsID = Long.parseLong(credentialsIDAsString);
		} catch (final NumberFormatException exc) {
			CredentialsCommands.LOG.warn("Credentials ID must be a whole number.", exc);
			inputInvalid = true;
		}
		
		if (tooManyArguments != null) {
			ci.println("Too many arguments were given. Arguments must be exactly one.");
			inputInvalid = true;
		}
		
		if (!inputInvalid) {
			try {
				this.INTERNAL_CREDENTIALS_SERVICE.setCredentialsInStorageProvider(credentialsID);
				ci.println("Setting / injecting credentials \"" + credentialsID + "\" was successfull.");
				return;
			} catch (final SystemException exc) {
				CredentialsCommands.LOG.warn("A System Exception occured.", exc);
			} catch (final UserException exc) {
				CredentialsCommands.LOG.warn("An User Exception occured.", exc);
			}
			
			ci.println("Setting / injecting credentials \"" + credentialsID + "\" failed.");
		}
		
	}
	
	public void _deleteCredentialsInStorageProvider(final CommandInterpreter ci) {
		
		final String storageProviderID = ci.nextArgument();
		final String tooManyArguments = ci.nextArgument();
		
		boolean inputInvalid = false;
		
		if (storageProviderID == null) {
			ci.println("Storage provider ID not given.");
			inputInvalid = true;
		}
		if (tooManyArguments != null) {
			ci.println("Too many arguments were given. Arguments must be exactly one.");
			inputInvalid = true;
		}
		
		if (!inputInvalid) {
			try {
				this.INTERNAL_CREDENTIALS_SERVICE.deleteCredentialsInStorageProvider(storageProviderID);
				ci.println("Deleting credentials in storage provider \"" + storageProviderID + "\" was successfull.");
			} catch (final SystemException exc) {
				CredentialsCommands.LOG.warn("A System Exception occured.", exc);
				ci.println("Deleting credentials in storage provider \"" + storageProviderID + "\" failed.");
			}
		}
		
	}
	
}
