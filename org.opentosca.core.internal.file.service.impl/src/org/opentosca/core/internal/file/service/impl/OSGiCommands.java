package org.opentosca.core.internal.file.service.impl;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.exceptions.SystemException;
import org.opentosca.exceptions.UserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides OSGi console commands of the Core Internal File Service.<br />
 * <br />
 * Copyright 2013 IAAS University of Stuttgart<br />
 * <br />
 * 
 * @author Rene Trefft - rene.trefft@developers.opentosca.org
 * 
 * @see CoreInternalFileServiceImpl
 * 
 */
public class OSGiCommands implements CommandProvider {
	
	private final static Logger LOG = LoggerFactory.getLogger(OSGiCommands.class);
	
	private final CoreInternalFileServiceImpl INTERNAL_FILE_SERVICE = new CoreInternalFileServiceImpl();
	
	
	/**
	 * Prints the available OSGi commands.
	 */
	@Override
	public String getHelp() {
		StringBuilder help = new StringBuilder();
		help.append("--- Core File Service Management ---\n");
		help.append("\tstoreCSAR <absPathOfCSARFile> - Stores a CSAR. All files will be stored at the active storage provider respectively default storage provider.\n");
		help.append("\texportCSAR <csarID> - Exports a CSAR to a Temp directory.\n");
		help.append("\tmoveCSAR <csarID> - Moves a stored CSAR to the active storage provider respectively default storage provider.\n");
		help.append("\tmoveFileOrDirectoryOfCSAR <csarID> <relPathToCSARRoot> - Moves a file or directory of a stored CSAR to the active storage provider respectively default storage provider.\n");
		help.append("\tdeleteCSAR <csarID>  - Deletes a CSAR.\n");
		help.append("\tdeleteCSARs - Deletes all CSARs.\n");
		help.append("\tprintCSARIDs - Prints the IDs of all stored CSARs.\n");
		help.append("\tprintStorageProviders - Prints the IDs of all available storage providers.\n");
		help.append("\tprintReadyStorageProviders - Prints the IDs of all available storage providers with satisfied requirements.\n");
		help.append("\tprintDefaultStorageProvider - Prints the ID of the default storage provider.\n");
		help.append("\tprintActiveStorageProvider - Prints the active storage provider.\n");
		help.append("\tsetActiveStorageProvider <storageProviderID> - Sets a available storage provider as the active one.\n");
		return help.toString();
	}
	
	/**
	 * OSGi commands.
	 */
	
	public void _storeCSAR(CommandInterpreter ci) {
		
		String csarFile = ci.nextArgument();
		String tooManyArguments = ci.nextArgument();
		
		boolean inputInvalid = false;
		
		if (csarFile == null) {
			ci.println("Absolute path to a CSAR file not given.");
			inputInvalid = true;
		}
		if (tooManyArguments != null) {
			ci.println("Too many arguments were given. Arguments must be exactly one.");
			inputInvalid = true;
		}
		
		if (!inputInvalid) {
			try {
				this.INTERNAL_FILE_SERVICE.storeCSAR(Paths.get(csarFile));
				ci.println("Storing CSAR located at \"" + csarFile + "\" was succesfull.");
				return;
			} catch (UserException exc) {
				OSGiCommands.LOG.warn("An User Exception occured.", exc);
			} catch (SystemException exc) {
				OSGiCommands.LOG.warn("An System Exception occured.", exc);
			}
			
			ci.println("Storing CSAR located at \"" + csarFile + "\" failed.");
			
		}
		
	}
	
	public void _exportCSAR(CommandInterpreter ci) {
		
		String csarID = ci.nextArgument();
		String tooManyArguments = ci.nextArgument();
		
		boolean inputInvalid = false;
		
		if (csarID == null) {
			ci.println("CSAR ID of CSAR not given.");
			inputInvalid = true;
		}
		if (tooManyArguments != null) {
			ci.println("Too many arguments were given. Arguments must be exactly one.");
			inputInvalid = true;
		}
		
		if (!inputInvalid) {
			try {
				Path exportedCSAR = this.INTERNAL_FILE_SERVICE.exportCSAR(new CSARID(csarID));
				ci.println("CSAR \"" + csarID + "\" was successfully exported to \"" + exportedCSAR + "\".");
				return;
			} catch (SystemException exc) {
				OSGiCommands.LOG.warn("An System Exception occured.", exc);
			} catch (UserException exc) {
				OSGiCommands.LOG.warn("An User Exception occured.", exc);
			}
			
			ci.println("Exporting CSAR \"" + csarID + "\" failed.");
			
		}
		
	}
	
	public void _printCSARIDs(CommandInterpreter ci) {
		
		Set<CSARID> csarIDs = this.INTERNAL_FILE_SERVICE.getCSARIDs();
		
		if (csarIDs.isEmpty()) {
			ci.println("No CSARs are currently stored.");
		} else {
			ci.println("CSAR ID(s) of stored CSAR(s):");
			for (CSARID csarID : csarIDs) {
				ci.println(csarID.toString());
			}
		}
		
	}
	
	public void _printReadyStorageProviders(CommandInterpreter ci) {
		
		Set<String> readyStorageProviders = this.INTERNAL_FILE_SERVICE.getReadyStorageProviders();
		
		if (readyStorageProviders.isEmpty()) {
			ci.println("No storage provider is currently ready.");
		} else {
			ci.println("IDs of ready storage providers:");
			for (String readyStorageProvider : readyStorageProviders) {
				ci.println(readyStorageProvider);
			}
		}
		
	}
	
	public void _printStorageProviders(CommandInterpreter ci) {
		
		Set<String> storageProviderIDs = this.INTERNAL_FILE_SERVICE.getStorageProviders();
		
		if (storageProviderIDs.isEmpty()) {
			ci.println("No storage provider is currently available.");
		} else {
			ci.println("Available storage providers:");
			ci.println("-------------------------------------");
			for (String storageProviderID : storageProviderIDs) {
				ci.println("ID:   " + storageProviderID);
				ci.println("Name: " + this.INTERNAL_FILE_SERVICE.getStorageProviderName(storageProviderID));
				ci.println("-------------------------------------");
			}
		}
		
	}
	
	public void _printDefaultStorageProvider(CommandInterpreter ci) {
		
		String defaultStorageProvider = this.INTERNAL_FILE_SERVICE.getDefaultStorageProvider();
		
		ci.print("ID of default storage provider: " + defaultStorageProvider);
		
	}
	
	public void _moveCSAR(CommandInterpreter ci) {
		
		String csarID = ci.nextArgument();
		String tooManyArguments = ci.nextArgument();
		
		boolean inputInvalid = false;
		
		if (csarID == null) {
			ci.println("CSAR ID of CSAR not given.");
			inputInvalid = true;
		}
		if (tooManyArguments != null) {
			ci.println("Too many arguments were given. Arguments must be exactly one.");
			inputInvalid = true;
		}
		
		if (!inputInvalid) {
			try {
				this.INTERNAL_FILE_SERVICE.moveCSAR(new CSARID(csarID));
				ci.println("Moving CSAR \"" + csarID + "\" was successfull.");
				return;
			} catch (SystemException exc) {
				OSGiCommands.LOG.warn("An System Exception occured.", exc);
			} catch (UserException exc) {
				OSGiCommands.LOG.warn("An User Exception occured.", exc);
			}
			
			ci.println("Moving CSAR \"" + csarID + "\" failed.");
			
		}
		
	}
	
	public void _moveFileOrDirectoryOfCSAR(CommandInterpreter ci) {
		
		String csarID = ci.nextArgument();
		String fileOrDirectory = ci.nextArgument();
		String tooManyArguments = ci.nextArgument();
		
		boolean inputInvalid = false;
		
		if (csarID == null) {
			ci.println("CSAR ID of CSAR not given.");
			inputInvalid = true;
		}
		
		if (fileOrDirectory == null) {
			ci.println("Directory / file to move not given.");
			inputInvalid = true;
		}
		
		if (tooManyArguments != null) {
			ci.println("Too many arguments were given. Arguments must be exactly two.");
			inputInvalid = true;
		}
		
		if (!inputInvalid) {
			
			try {
				this.INTERNAL_FILE_SERVICE.moveFileOrDirectoryOfCSAR(new CSARID(csarID), Paths.get(fileOrDirectory));
				ci.println("Moving directory / file \"" + fileOrDirectory + "\" of CSAR \"" + csarID + "\" was successfull.");
				return;
			} catch (SystemException exc) {
				OSGiCommands.LOG.warn("An System Exception occured.", exc);
			} catch (UserException exc) {
				OSGiCommands.LOG.warn("An User Exception occured.", exc);
			}
			
			ci.println("Moving directory / file \"" + fileOrDirectory + "\" of CSAR \"" + csarID + "\" failed.");
			
		}
		
	}
	
	public void _printActiveStorageProvider(CommandInterpreter ci) {
		
		String activeStorageProviderID = this.INTERNAL_FILE_SERVICE.getActiveStorageProvider();
		
		if (activeStorageProviderID != null) {
			ci.println("ID of active storage provider: " + activeStorageProviderID);
		} else {
			ci.println("No active storage provider is currently set.");
		}
		
	}
	
	public void _setActiveStorageProvider(CommandInterpreter ci) {
		
		String activeStorageProviderID = ci.nextArgument();
		String tooManyArguments = ci.nextArgument();
		
		boolean inputInvalid = false;
		
		if (activeStorageProviderID == null) {
			ci.println("ID of storage provider to set as active not given.");
			inputInvalid = true;
		}
		if (tooManyArguments != null) {
			ci.println("Too many arguments were given. Arguments must be exactly one.");
			inputInvalid = true;
		}
		
		if (!inputInvalid) {
			try {
				this.INTERNAL_FILE_SERVICE.setActiveStorageProvider(activeStorageProviderID);
				ci.println("Setting \"" + activeStorageProviderID + "\" as active storage provider was successfull.");
				return;
			} catch (UserException exc) {
				OSGiCommands.LOG.warn("An User Exception occured.", exc);
			}
			
			ci.println("Setting \"" + activeStorageProviderID + "\" as active storage provider failed.");
			
		}
		
	}
	
	public void _deleteCSAR(CommandInterpreter ci) {
		
		String csarID = ci.nextArgument();
		String tooManyArguments = ci.nextArgument();
		
		boolean inputInvalid = false;
		
		if (csarID == null) {
			ci.println("CSAR ID of CSAR not given.");
			inputInvalid = true;
		}
		if (tooManyArguments != null) {
			ci.println("Too many arguments were given. Arguments must be exactly one.");
			inputInvalid = true;
		}
		
		if (!inputInvalid) {
			try {
				this.INTERNAL_FILE_SERVICE.deleteCSAR(new CSARID(csarID));
				ci.println("Deleting CSAR \"" + csarID + "\" was successfull.");
				return;
			} catch (SystemException exc) {
				OSGiCommands.LOG.warn("An System Exception occured.", exc);
			} catch (UserException exc) {
				OSGiCommands.LOG.warn("An User Exception occured.", exc);
			}
			
			ci.println("Deleting CSAR \"" + csarID + "\" failed.");
			
		}
		
	}
	
	public void _deleteCSARs(CommandInterpreter ci) {
		
		try {
			this.INTERNAL_FILE_SERVICE.deleteCSARs();
			ci.println("Deleting all CSARs was successfull.");
			return;
		} catch (SystemException exc) {
			OSGiCommands.LOG.warn("An System Exception occured.", exc);
		}
		
		ci.println("Deleting all CSARs failed.");
		
	}
	
}
