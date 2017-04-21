package org.opentosca.container.core.impl.service.internal;

import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.osgi.framework.console.CommandProvider;
import org.opentosca.container.core.engine.IToscaEngineService;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.service.internal.ICoreInternalModelRepositoryService;
import org.opentosca.container.core.tosca.model.TDefinitions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * TODO: Completely remove Model Repository - needed Definitions should be
 * directly fetched from the TOSCA Reference Mapper in the TOSCA Engine.
 */
public class CoreInternalModelRepositoryServiceImpl implements ICoreInternalModelRepositoryService, CommandProvider {

	// private final IXMLSerializerService xmlSerializerService = null;

	// JDBC-Database-URL to store the Database to
	// private final String databaseURL = "jdbc:derby:" +
	// Settings.getSetting("databaseLocation") + ";create=true";
	// private final String tableName = "toscaFiles".toUpperCase();
	// private final Connection conn = null;
	// private final Statement stmt = null;
	// private PreparedStatement pstmt = null;

	private IToscaEngineService toscaEngineService;
	
	// /**
	// * SQL-Queries
	// */
	// private final String createTable = "CREATE TABLE " + this.tableName +
	// " (DefinitionsID VARCHAR(1000) NOT NULL, DefinitionsXML BLOB NOT NULL,
	// PRIMARY KEY (DefinitionsID))";
	// private final String storeDefinitions = "INSERT INTO " + this.tableName +
	// " (DefinitionsID, DefinitionsXML) VALUES (?, ?)";
	// private final String getDefinitionsByID = "SELECT DefinitionsXML FROM " +
	// this.tableName + " WHERE DefinitionsID = ?";
	// private final String getAllDefinitionsIDs = "SELECT DefinitionsID FROM "
	// + this.tableName;
	// private final String deleteAllDefinitions = "DELETE FROM " +
	// this.tableName;
	// private final String deleteDefinitionsByID = "DELETE FROM " +
	// this.tableName + " WHERE DefinitionsID = ?";
	// private final String dropTable = "DROP TABLE " + this.tableName;

	/**
	 * Logging
	 */
	private final static Logger LOG = LoggerFactory.getLogger(CoreInternalModelRepositoryServiceImpl.class);
	
	// public CoreInternalModelRepositoryServiceImpl() {
	// this.connectDatabase();
	// this.prepareDatabase();
	// }

	// /**
	// * This method connects to the Derby-Database
	// */
	// private void connectDatabase() {
	// try {
	// new org.apache.derby.jdbc.EmbeddedDriver();
	// this.conn = DriverManager.getConnection(this.databaseURL);
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// }

	// /**
	// * This method prepares the Database. It creates the tables if they're not
	// * already existing.
	// */
	// private void prepareDatabase() {
	// try {
	// CoreInternalModelRepositoryServiceImpl.LOG.info("Checking if Tables
	// already exist...");
	// ArrayList<String> tables = new ArrayList<String>();
	//
	// this.stmt = this.conn.createStatement();
	//
	// DatabaseMetaData metadata = null;
	// metadata = this.conn.getMetaData();
	// String[] names = {"TABLE"};
	//
	// // Get table Names, and add add them to a list.
	// ResultSet tableNames = metadata.getTables(null, null, null, names);
	// while (tableNames.next()) {
	// tables.add(tableNames.getString("TABLE_NAME"));
	// }
	//
	// // Check if our table already exists. If not, create it
	// if (!tables.contains(this.tableName)) {
	// CoreInternalModelRepositoryServiceImpl.LOG.info("Table did not exist.
	// Creating...");
	// this.stmt.execute(this.createTable);
	// }
	// this.stmt.close();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// }

	// @Override
	// /**
	// * {@inheritDoc}
	// */
	// public QName storeTOSCA(File toscaFile) {
	//
	// FileInputStream fileInputStream = null;
	// BufferedInputStream bufferedInputStream = null;
	//
	// try {
	//
	// IXMLSerializer serializer = this.xmlSerializerService.getXmlSerializer();
	//
	// Definitions definitions = serializer.unmarshal(toscaFile);
	//
	// // unmarshalling failed
	// if (definitions == null) {
	// CoreInternalModelRepositoryServiceImpl.LOG.error("Unmarshalling TOSCA
	// file {} failed!",
	// toscaFile.getName());
	// return null;
	// }
	//
	// String definitionsID = definitions.getId();
	// String definitionsNS = definitions.getTargetNamespace();
	// QName definitionsIDQName = new QName(definitionsNS, definitionsID);
	//
	// CoreInternalModelRepositoryServiceImpl.LOG.info("Storing TOSCA file {}
	// with Definitions ID \"{}\"...",
	// toscaFile.getName(), definitionsIDQName.toString());
	//
	// this.pstmt = this.conn.prepareStatement(this.storeDefinitions);
	// this.pstmt.setString(1, definitionsIDQName.toString());
	//
	// fileInputStream = new FileInputStream(toscaFile);
	// bufferedInputStream = new BufferedInputStream(fileInputStream);
	// // this.pstmt.setString(2, this.fileToString(file));
	// // storing TOSCA XML file as BLOB
	// this.pstmt.setBinaryStream(2, bufferedInputStream);
	// this.pstmt.execute();
	// CoreInternalModelRepositoryServiceImpl.LOG.info("Storing TOSCA file {}
	// with Definitions ID \"{}\" completed.",
	// toscaFile.getName(), definitionsIDQName.toString());
	// return definitionsIDQName;
	//
	// } catch (SQLException e) {
	// if
	// (e.getLocalizedMessage().contains("duplicate key value in a unique or
	// primary key constraint or unique index"))
	// {
	// CoreInternalModelRepositoryServiceImpl.LOG.warn("Error while writing
	// TOSCA / Definitions to database. It already exists!",
	// e);
	// } else {
	// CoreInternalModelRepositoryServiceImpl.LOG.error("Error while writing
	// TOSCA / Definitions to database.",
	// e);
	// }
	// } catch (FileNotFoundException e) {
	// CoreInternalModelRepositoryServiceImpl.LOG.error("", e);
	//
	// } finally {
	// if (this.pstmt != null) {
	// try {
	// this.pstmt.close();
	// } catch (SQLException e) {
	// CoreInternalModelRepositoryServiceImpl.LOG.error("", e);
	// }
	// }
	// if (bufferedInputStream != null) {
	// try {
	// bufferedInputStream.close();
	// } catch (IOException e) {
	// CoreInternalModelRepositoryServiceImpl.LOG.error("", e);
	// }
	// }
	// if (fileInputStream != null) {
	// try {
	// fileInputStream.close();
	// } catch (IOException e) {
	// CoreInternalModelRepositoryServiceImpl.LOG.error("", e);
	// }
	// }
	// }
	//
	// return null;
	//
	// }
	
	
	// /**
	// * Reads a file to String
	// *
	// * @param filePath : Absolute path, pointing to the file, which needs to
	// be
	// * read
	// * @return String containing file contents
	// */
	// public String fileToString(File file) {
	// CoreInternalModelRepositoryServiceImpl.LOG.info("Reading file {} to
	// String",
	// file.getAbsolutePath());
	// StringBuilder contents = new StringBuilder();
	// try {
	// BufferedReader input = new BufferedReader(new FileReader(file));
	// try {
	// String line = null;
	// while ((line = input.readLine()) != null) {
	// contents.append(line);
	// contents.append(System.getProperty("line.separator"));
	// }
	// } finally {
	// input.close();
	// }
	// } catch (IOException ex) {
	// ex.printStackTrace();
	// }
	// return contents.toString();
	// }

	@Override
	/**
	 * {@inheritDoc}
	 */
	public List<QName> getAllDefinitionsIDs(final CSARID csarID) {
		CoreInternalModelRepositoryServiceImpl.LOG.info("Getting IDs of all Definitions in CSAR \"{}\"...", csarID);

		if (this.toscaEngineService != null) {
			return this.toscaEngineService.getToscaReferenceMapper().getDefinitionIDsOfCSAR(csarID);
		}

		CoreInternalModelRepositoryServiceImpl.LOG.error("TOSCA Engine Service is not available! Can't get Definitions IDs of CSAR \"{}\"", csarID);
		return null;

		// ArrayList<QName)> definitionIDs = new ArrayList<QName>();
		// try {
		// this.pstmt = this.conn.prepareStatement(this.getAllDefinitionsIDs);
		// ResultSet result = this.pstmt.executeQuery();
		// while (result.next()) {
		// String definitionID = result.getString(1);
		// definitionIDs.add(QName.valueOf(definitionID));
		// }
		// } catch (SQLException e) {
		// CoreInternalModelRepositoryServiceImpl.LOG.error("Getting IDs of all
		// stored Definitions failed!",
		// e);
		// } finally {
		// if (this.pstmt != null) {
		// try {
		// this.pstmt.close();
		// } catch (SQLException e) {
		// CoreInternalModelRepositoryServiceImpl.LOG.error("", e);
		// }
		// }
		// }
		// return definitionIDs;
	}

	@Override
	/**
	 * {@inheritDoc}
	 */
	public TDefinitions getDefinitions(final CSARID csarID, final QName definitionsID) {

		// Definitions definitions = null;
		// InputStream inputStream = null;
		CoreInternalModelRepositoryServiceImpl.LOG.info("Getting Definitions with ID \"{}\" in CSAR \"{}\"...", definitionsID.toString(), csarID.toString());

		if (this.toscaEngineService != null) {
			final Object definitions = this.toscaEngineService.getToscaReferenceMapper().getJAXBReference(csarID, definitionsID);

			if (definitions instanceof TDefinitions) {
				return (TDefinitions) definitions;
			} else {
				CoreInternalModelRepositoryServiceImpl.LOG.error("Definitions with ID \"{}\" was not found in CSAR \"{}\"!", definitionsID.toString(), csarID.toString());
				return null;
			}

		}

		CoreInternalModelRepositoryServiceImpl.LOG.error("TOSCA Engine Service is not available! Can't get Definitions with ID \"{}\" of CSAR \"{}\"", definitionsID.toString(), csarID.toString());
		return null;

		// try {
		//
		// this.pstmt = this.conn.prepareStatement(this.getDefinitionsByID);
		// this.pstmt.setString(1, definitionsID.toString());
		// ResultSet result = this.pstmt.executeQuery();
		// // String ToscaXML = null;
		// // Reader reader;
		// // Iterate over the results. This should only be <br>one</br>
		// // iteration.
		// // while (result.next()) {
		// // ToscaXML = result.getString(1);
		// if (result.next()) {
		// inputStream = result.getBinaryStream(1);
		// } else {
		// CoreInternalModelRepositoryServiceImpl.LOG.error("Definitions with ID
		// \"{}\" is not stored!",
		// definitionsID.toString());
		// return null;
		// }
		// // }
		//
		// // if (ToscaXML == null) {
		// // return null;
		// // }
		//
		// // Read bytes to inputstream, and unmarshall
		// // InputStream in = new ByteArrayInputStream(ToscaXML.getBytes());
		// IXMLSerializer serializer =
		// this.xmlSerializerService.getXmlSerializer();
		// definitions = serializer.unmarshal(inputStream);
		// } catch (SQLException e) {
		// CoreInternalModelRepositoryServiceImpl.LOG.error("Getting Definitions
		// with ID \"{}\" failed!",
		// definitionsID.toString(), e);
		// } finally {
		//
		// if (inputStream != null) {
		// try {
		// inputStream.close();
		// } catch (IOException e) {
		// CoreInternalModelRepositoryServiceImpl.LOG.error("", e);
		// }
		// }
		//
		// if (this.pstmt != null) {
		// try {
		// this.pstmt.close();
		// } catch (SQLException e) {
		// CoreInternalModelRepositoryServiceImpl.LOG.error("", e);
		// }
		// }
		//
		// }
		//
		// return definitions;

	}

	// @Override
	// public List<QName> getServiceTemplateIDs(CSARID csarID, QName
	// definitionsID) {
	//
	// CoreInternalModelRepositoryServiceImpl.LOG.info("Getting service template
	// IDs of Definitions with ID \"{}\"...",
	// definitionsID.toString());
	//
	// List<QName> serviceTemplateIDs = new ArrayList<QName>();
	//
	// TDefinitions definitions = this.getDefinitions(csarID, definitionsID);
	//
	// if (definitions != null) {
	// for (TExtensibleElements extElements :
	// definitions.getServiceTemplateOrNodeTypeOrNodeTypeImplementation()) {
	// if (extElements instanceof TServiceTemplate) {
	// TServiceTemplate serviceTemplate = (TServiceTemplate) extElements;
	// String serviceTemplateNS = serviceTemplate.getTargetNamespace();
	// String serviceTemplateID = serviceTemplate.getId();
	// QName serviceTemplateIDQName = new QName(serviceTemplateNS,
	// serviceTemplateID);
	// serviceTemplateIDs.add(serviceTemplateIDQName);
	// }
	// }
	//
	// CoreInternalModelRepositoryServiceImpl.LOG.info("Definitions with ID
	// \"{}\" consists of {} service templates.",
	// definitionsID.toString(), serviceTemplateIDs.size());
	//
	// return serviceTemplateIDs;
	// }
	//
	// return null;
	//
	// }

	// @Override
	// /**
	// * {@inheritDoc}
	// */
	// public int deleteAllDefinitions() {
	//
	// int num = 0;
	// CoreInternalModelRepositoryServiceImpl.LOG.info("Deleting all Definitions
	// stored in Core Model Repository...");
	// try {
	// this.pstmt = this.conn.prepareStatement(this.deleteAllDefinitions);
	// num = this.pstmt.executeUpdate();
	// CoreInternalModelRepositoryServiceImpl.LOG.info(num +
	// " Definitions were deleted in Core Model Repository.");
	// } catch (SQLException e) {
	// CoreInternalModelRepositoryServiceImpl.LOG.error("Deleting Definitions in
	// Core Model Repository failed!",
	// e);
	// } finally {
	//
	// if (this.pstmt != null) {
	// try {
	// this.pstmt.close();
	// } catch (SQLException e) {
	// CoreInternalModelRepositoryServiceImpl.LOG.error("", e);
	// }
	// }
	//
	// }
	//
	// return num;
	//
	// }

	// /**
	// * {@inheritDoc}
	// */
	// @Override
	// public boolean deleteDefinitions(QName definitionsID) {
	//
	// CoreInternalModelRepositoryServiceImpl.LOG.info("Deleting Definitions
	// with ID \"{}\"...",
	// definitionsID.toString());
	// try {
	// this.pstmt = this.conn.prepareStatement(this.deleteDefinitionsByID);
	// this.pstmt.setString(1, definitionsID.toString());
	// if (this.pstmt.executeUpdate() > 0) {
	// CoreInternalModelRepositoryServiceImpl.LOG.info("Deleting Definitions
	// with ID \"{}\" completed.",
	// definitionsID.toString());
	// return true;
	// } else {
	// CoreInternalModelRepositoryServiceImpl.LOG.error("Definitions with ID
	// \"{}\" doesn't exist.",
	// definitionsID.toString());
	// }
	// } catch (SQLException e) {
	// CoreInternalModelRepositoryServiceImpl.LOG.info("Deleting Definitions
	// with ID \"{}\" failed.",
	// definitionsID.toString(), e);
	// } finally {
	// if (this.pstmt != null) {
	// try {
	// this.pstmt.close();
	// } catch (SQLException e) {
	// CoreInternalModelRepositoryServiceImpl.LOG.error("", e);
	// }
	// }
	// }
	//
	// return false;
	//
	// }

	// public void bindIXMLSerializerService(IXMLSerializerService
	// xmlSerializerService) {
	//
	// this.xmlSerializerService = xmlSerializerService;
	// CoreInternalModelRepositoryServiceImpl.LOG.info("Binding XML Serializer
	// Service bound.");
	// }
	//
	// public void unbindIXMLSerializerService(IXMLSerializerService
	// xmlSerializerService) {
	//
	// this.xmlSerializerService = xmlSerializerService;
	// CoreInternalModelRepositoryServiceImpl.LOG.info("XML Serializer Service
	// unbound.");
	// }

	public void bindToscaEngineService(final IToscaEngineService toscaEngineService) {

		this.toscaEngineService = toscaEngineService;
		CoreInternalModelRepositoryServiceImpl.LOG.debug("Tosca Engine Service bound.");
	}

	public void unbindToscaEngineService(final IToscaEngineService toscaEngineService) {

		this.toscaEngineService = toscaEngineService;
		CoreInternalModelRepositoryServiceImpl.LOG.debug("Tosca Engine Service unbound.");
	}

	/**
	 * The following methods are OSGi-Console-Commands
	 */
	@Override
	public String getHelp() {
		final StringBuilder help = new StringBuilder();
		help.append("--- OpenTOSCA Core Model Repository Management ---\n");
		// help.append("\tstoreTOSCA - Stores the TOSCA / Definitions at the
		// given file path.\n");
		help.append("\tgetAllDefinitionsIDs - Gets the IDs of all stored Definitions.\n");
		// help.append("\tgetServiceTemplateIDs - Gets the IDs of all service
		// templates contained in the Definitions with the given ID.\n");
		// help.append("\tdeleteAllDefinitions - Deletes ALL stored
		// Definitions.\n");
		// help.append("\tdeleteDefinitions - Deletes the Definitions with the
		// given ID.\n");
		// help.append("\treset - Drops and recreates database table that stores
		// the Definitions. Everything will be deleted!\n");
		return help.toString();
	}

	// public void _storeTOSCA(CommandInterpreter ci) {
	// String arg = ci.nextArgument();
	//
	// this.storeTOSCA(new File(arg));

	// if (this.storeToscaXML(new File(arg)) != null) {
	// ci.println("TOSCA-File " + arg +
	// " stored in Core Model Repository.");
	// } else {
	// ci.println("Storing TOSCA-File " + arg +
	// " in Core Model Repository failed!");
	// }
	// }

	// public void _getAllDefinitionsIDs(CommandInterpreter ci) {
	//
	// List<QName> definitionsIDs = this.getAllDefinitionsIDs();
	//
	// if (definitionsIDs.isEmpty()) {
	// ci.println("No Definitions / TOSCAs stored in Core Model Repository.");
	// } else {
	// ci.println("ID(s) of stored Definitions:");
	// for (QName definitionID : definitionsIDs) {
	// ci.println(definitionID.toString());
	// }
	// }
	//
	// }

	// public void _getServiceTemplateIDs(CommandInterpreter ci) {
	//
	// String arg = ci.nextArgument();
	//
	// if (arg == null) {
	// arg = "";
	// }
	//
	// List<QName> serviceTemplateIDs =
	// this.getServiceTemplateIDs(QName.valueOf(arg));
	//
	// if ((serviceTemplateIDs != null) && !serviceTemplateIDs.isEmpty()) {
	// ci.println("ID(s) of service template(s):");
	// for (QName serviceTemplateID : serviceTemplateIDs) {
	// ci.println(serviceTemplateID.toString());
	// }
	// }
	//
	// }

	// public void _deleteAllDefinitions(CommandInterpreter ci) {
	// ci.println("Deleted " + this.deleteAllDefinitions() +
	// " Definitions / TOSCA(s) from Core Model Repository.");
	// }

	// public void _deleteDefinitions(CommandInterpreter ci) {
	//
	// String arg = ci.nextArgument();
	//
	// if (arg == null) {
	// arg = "";
	// }
	//
	// this.deleteDefinitions(QName.valueOf(arg));
	//
	// }

	// public void _reset(CommandInterpreter ci) {
	// try {
	// this.pstmt = this.conn.prepareStatement(this.dropTable);
	// this.prepareDatabase();
	// ci.println("Dropped and recreated Core Model Repository Table.");
	// } catch (SQLException e) {
	// CoreInternalModelRepositoryServiceImpl.LOG.error("", e);
	// } finally {
	// if (this.pstmt != null) {
	// try {
	// this.pstmt.close();
	// } catch (SQLException e) {
	// CoreInternalModelRepositoryServiceImpl.LOG.error("", e);
	// }
	// }
	// }
	// }

}
