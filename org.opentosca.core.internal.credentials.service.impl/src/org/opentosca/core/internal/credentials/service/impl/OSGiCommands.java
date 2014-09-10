package org.opentosca.core.internal.credentials.service.impl;

import java.util.Set;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.opentosca.core.model.credentials.Credentials;
import org.opentosca.exceptions.SystemException;
import org.opentosca.exceptions.UserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides OSGi console commands of the Core Internal Credentials Service.<br />
 * <br />
 * Copyright 2013 IAAS University of Stuttgart<br />
 * <br />
 * 
 * @author Rene Trefft - rene.trefft@developers.opentosca.org
 * 
 * @see CoreInternalCredentialsServiceImpl
 * 
 */
public class OSGiCommands implements CommandProvider {
	
	private final static Logger LOG = LoggerFactory.getLogger(OSGiCommands.class);
	
	private final CoreInternalCredentialsServiceImpl INTERNAL_CREDENTIALS_SERVICE = new CoreInternalCredentialsServiceImpl();
	
	
	/**
	 * Prints the available OSGi commands.
	 */
	@Override
	public String getHelp() {
		StringBuilder help = new StringBuilder();
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
	
	public void _storeCredentials(CommandInterpreter ci) {
		
		String storageProviderID = ci.nextArgument();
		String identity = ci.nextArgument();
		String key = ci.nextArgument();
		String description = ci.nextArgument();
		
		if (description != null) {
			
			StringBuilder descriptionStringBuilder = new StringBuilder(description);
			
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
			Credentials credentials = new Credentials(storageProviderID, identity, key, description);
			try {
				this.INTERNAL_CREDENTIALS_SERVICE.storeCredentials(credentials);
				ci.println("Storing credentials for storage provider \"" + storageProviderID + "\" was successfull.");
			} catch (UserException exc) {
				OSGiCommands.LOG.warn("An User Exception occured.", exc);
				ci.println("Storing credentials for storage provider \"" + storageProviderID + "\" failed.");
			}
		}
		
	}
	
	public void _printCredentialsIDs(CommandInterpreter ci) {
		
		Set<Long> credentialsIDs = this.INTERNAL_CREDENTIALS_SERVICE.getCredentialsIDs();
		
		if (credentialsIDs.isEmpty()) {
			
			ci.print("No credentials are stored.");
			
		} else {
			
			for (Long credentialsID : credentialsIDs) {
				ci.println("ID: " + credentialsID);
			}
			
		}
		
	}
	
	public void _printAllCredentials(CommandInterpreter ci) {
		
		Set<Credentials> allCredentials = this.INTERNAL_CREDENTIALS_SERVICE.getAllCredentials();
		
		if (allCredentials.isEmpty()) {
			
			ci.print("No credentials are stored.");
			
		} else {
			
			for (Credentials credentials : allCredentials) {
				
				long credentialsID = credentials.getID();
				String storageProviderID = credentials.getStorageProviderID();
				String identity = credentials.getIdentity();
				String key = credentials.getKey();
				String description = credentials.getDescription();
				
				boolean injected = false;
				
				try {
					injected = this.INTERNAL_CREDENTIALS_SERVICE.hasStorageProviderCredentials(credentialsID);
				} catch (UserException exc) {
					OSGiCommands.LOG.warn("An User Exception occured.", exc);
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
	
	public void _printAllCredentialsIdentityAndKeyNames(CommandInterpreter ci) {
		
		Set<String> storageProviders = this.INTERNAL_CREDENTIALS_SERVICE.getStorageProviders();
		
		if (storageProviders.isEmpty()) {
			
			ci.print("No storage providers are available.");
			
		} else {
			
			ci.println("Credentials identity and key names of all available storage providers:");
			for (String storageProviderID : storageProviders) {
				ci.println("----- " + storageProviderID + " -----");
				
				try {
					
					if (this.INTERNAL_CREDENTIALS_SERVICE.needsStorageProviderCredentials(storageProviderID)) {
						
						ci.println("Identity name: " + this.INTERNAL_CREDENTIALS_SERVICE.getCredentialsIdentityName(storageProviderID));
						ci.println("Key name:      " + this.INTERNAL_CREDENTIALS_SERVICE.getCredentialsKeyName(storageProviderID));
						
					} else {
						
						ci.println("Needs no credentials.");
						
					}
					
				} catch (SystemException exc) {
					OSGiCommands.LOG.warn("A System Exception occured.", exc);
				}
				
			}
			
		}
		
	}
	
	public void _deleteCredentials(CommandInterpreter ci) {
		
		String credentialsIDAsString = ci.nextArgument();
		String tooManyArguments = ci.nextArgument();
		
		boolean inputInvalid = false;
		
		if (credentialsIDAsString == null) {
			ci.println("Credentials ID not given.");
			inputInvalid = true;
		}
		
		long credentialsID = 0;
		
		try {
			credentialsID = Long.parseLong(credentialsIDAsString);
		} catch (NumberFormatException exc) {
			OSGiCommands.LOG.warn("Credentials ID must be a whole number.", exc);
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
			} catch (UserException exc) {
				OSGiCommands.LOG.warn("An User Exception occured.", exc);
				ci.println("Deleting credentials \"" + credentialsID + "\" failed.");
			}
		}
		
	}
	
	public void _deleteAllCredentials(CommandInterpreter ci) {
		this.INTERNAL_CREDENTIALS_SERVICE.deleteAllCredentials();
		ci.println("Deleting all credentials was successfull.");
	}
	
	public void _setCredentialsInStorageProvider(CommandInterpreter ci) {
		
		String credentialsIDAsString = ci.nextArgument();
		String tooManyArguments = ci.nextArgument();
		
		boolean inputInvalid = false;
		
		if (credentialsIDAsString == null) {
			ci.println("Credentials ID not given.");
			inputInvalid = true;
		}
		
		long credentialsID = 0;
		
		try {
			credentialsID = Long.parseLong(credentialsIDAsString);
		} catch (NumberFormatException exc) {
			OSGiCommands.LOG.warn("Credentials ID must be a whole number.", exc);
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
			} catch (SystemException exc) {
				OSGiCommands.LOG.warn("A System Exception occured.", exc);
			} catch (UserException exc) {
				OSGiCommands.LOG.warn("An User Exception occured.", exc);
			}
			
			ci.println("Setting / injecting credentials \"" + credentialsID + "\" failed.");
		}
		
	}
	
	public void _deleteCredentialsInStorageProvider(CommandInterpreter ci) {
		
		String storageProviderID = ci.nextArgument();
		String tooManyArguments = ci.nextArgument();
		
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
			} catch (SystemException exc) {
				OSGiCommands.LOG.warn("A System Exception occured.", exc);
				ci.println("Deleting credentials in storage provider \"" + storageProviderID + "\" failed.");
			}
		}
		
	}
	
}
