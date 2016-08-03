package org.opentosca.toscaengine.service.impl.resolver;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.core.model.artifact.file.AbstractFile;
import org.opentosca.core.model.csar.CSARContent;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.exceptions.SystemException;
import org.opentosca.exceptions.UserException;
import org.opentosca.model.tosca.Definitions;
import org.opentosca.model.tosca.TImport;
import org.opentosca.toscaengine.service.impl.ToscaEngineServiceImpl;
import org.opentosca.toscaengine.service.impl.servicehandler.ServiceHandler;
import org.opentosca.toscaengine.service.impl.utils.PathResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * The DefinitionsResolver resolves references inside of TOSCA Definitions
 * according to the TOSCA specification wd14. The resolving reaches elements of
 * the Definitions Documents of the passed CSAR and elements inside of imported
 * files of Definitions Documents. Each found element and the document in which
 * the element is nested is stored by the
 * org.opentosca.toscaengine.service.impl.
 * toscareferencemapping.ToscaReferenceMapper.
 * 
 * Preconditions for resolving a Definitions: The Definitions has to be valid in
 * all kind of meanings.
 * 
 * Copyright 2012 Christian Endres
 * 
 * @author endrescn@fachschaft.informatik.uni-stuttgart.de
 * 
 */
public class DefinitionsResolver {

    private final Logger LOG = LoggerFactory.getLogger(DefinitionsResolver.class);

    private CSARContent csarContent = null;

    // list documents which are processed
    private LinkedList<Document> listOfTOSCA = null;
    private LinkedList<Definitions> listOfTOSCADefinitions = null;
    private LinkedList<Document> listOfXML = null;
    private LinkedList<Document> listOfWSDL = null;
    private Map<String, List<Document>> mapOfNSToDocuments = null;

    // list of TOSCA documents which imports are not processed
    private LinkedList<AbstractFile> listOfNewlyImportedDocuments = null;
    private Set<AbstractFile> alreadyImportedDocuments = null;


    private void init() {
	csarContent = null;
	listOfTOSCA = new LinkedList<>();
	listOfTOSCADefinitions = new LinkedList<>();
	listOfXML = new LinkedList<>();
	listOfWSDL = new LinkedList<>();
	mapOfNSToDocuments = new HashMap<String, List<Document>>();
	listOfNewlyImportedDocuments = new LinkedList<>();
	alreadyImportedDocuments = new HashSet<AbstractFile>();
    }

    /**
     * Resolves the referenced TOSCA files inside of a CSAR and stores the
     * mapping into the ToscaReferenceMapper.
     * 
     * @param csarID The ID of the passed CSAR which shall be resolved.
     * @return true means no error, false one or more errors
     */
    public boolean resolveDefinitions(CSARID csarID) {

	LOG.info("Start resolving of the CSAR \"" + csarID.getFileName() + "\".");

	init();

	// first of all search all documents
	boolean errorOccured = !resolveImports(csarID);
	if (errorOccured) {
	    LOG.error("There was an error while searching for the imports of the TOSCA documents.");
	    return false;
	}

	ToscaEngineServiceImpl.toscaReferenceMapper.storeListOfWSDLForCSAR(csarID, listOfWSDL);

	// if no error occurred, start the resolving of references
	LOG.info("All import elements are resolvable, now starting the resolving.");

	// initialize all needed resolver
	ReferenceMapper referenceMapper = new ReferenceMapper(csarID, mapOfNSToDocuments);
	ExtensionsResolver extensionResolver = new ExtensionsResolver(referenceMapper);
	TypesResolver typesResolver = new TypesResolver(referenceMapper);
	ServiceTemplateResolver serviceTemplateResolver = new ServiceTemplateResolver(referenceMapper);
	NodeTypeResolver nodeTypeResolver = new NodeTypeResolver(referenceMapper);
	NodeTypeImplementationResolver nodeTypeImplementationResolver = new NodeTypeImplementationResolver(referenceMapper, csarID);
	CapabilityTypeResolver capabilityTypeResolver = new CapabilityTypeResolver(referenceMapper);
	RequirementTypeResolver requirementTypeResolver = new RequirementTypeResolver(referenceMapper);
	RelationshipTypeResolver relationshipTypeResolver = new RelationshipTypeResolver(referenceMapper);
	RelationshipTypeImplementationResolver relationshipTypeImplementationResolver = new RelationshipTypeImplementationResolver(referenceMapper);
	ArtifactTypeResolver artifactTypeResolver = new ArtifactTypeResolver(referenceMapper);
	ArtifactTemplateResolver artifactTemplateResolver = new ArtifactTemplateResolver(referenceMapper);
	PolicyTypeResolver policyTypeResolver = new PolicyTypeResolver(referenceMapper);
	PolicyTemplateResolver policyTemplateResolver = new PolicyTemplateResolver(referenceMapper);

	// resolve each Definitions content
	for (Definitions definitionsToResolve : listOfTOSCADefinitions) {

	    ToscaEngineServiceImpl.toscaReferenceMapper.storeDefinitions(csarID, definitionsToResolve);
	    LOG.info("Start to resolve the Definitions \"{" + definitionsToResolve.getTargetNamespace() + "}" + definitionsToResolve.getId() + "\".");

	    errorOccured = errorOccured || extensionResolver.resolve(definitionsToResolve);
	    errorOccured = errorOccured || typesResolver.resolve(definitionsToResolve);
	    errorOccured = errorOccured || serviceTemplateResolver.resolve(definitionsToResolve, csarID);
	    errorOccured = errorOccured || nodeTypeResolver.resolve(definitionsToResolve);
	    errorOccured = errorOccured || nodeTypeImplementationResolver.resolve(definitionsToResolve);
	    errorOccured = errorOccured || relationshipTypeResolver.resolve(definitionsToResolve);
	    errorOccured = errorOccured || relationshipTypeImplementationResolver.resolve(definitionsToResolve);
	    errorOccured = errorOccured || requirementTypeResolver.resolve(definitionsToResolve);
	    errorOccured = errorOccured || capabilityTypeResolver.resolve(definitionsToResolve);
	    errorOccured = errorOccured || artifactTypeResolver.resolve(definitionsToResolve);
	    errorOccured = errorOccured || artifactTemplateResolver.resolve(definitionsToResolve);
	    errorOccured = errorOccured || policyTypeResolver.resolve(definitionsToResolve);
	    errorOccured = errorOccured || policyTemplateResolver.resolve(definitionsToResolve);

	}

	// if an error occurred the TOSCA Proccessing was not successfull, thus
	// delete the stored data
	if (errorOccured) {
	    LOG.error("Resolving of the CSAR \"" + csarID.getFileName() + "\" was not successfull!");
	    LOG.debug("Deleting stored references.");
	    ToscaEngineServiceImpl.toscaReferenceMapper.clearCSARContent(csarID);
	    return false;
	}

	LOG.info("Resolving of the CSAR \"" + csarID.getFileName() + "\" was successfull!");
	return true;
    }

    /**
     * This method traverses the tree of imports of TOSCA documents starting in
     * the main TOSCA defined in the TOSCA meta file of the CSAR.
     * 
     * TODO prevent cycles in the imports of other TOSCA documents
     * 
     * @param csarID of the CSAR
     * @return true means no error, false one or more errors
     */
    private boolean resolveImports(CSARID csarID) {

	// DocumentBuilder for parsing the files
	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	DocumentBuilder builder;
	try {
	    dbf.setNamespaceAware(true);
	    dbf.setIgnoringComments(true);
	    builder = dbf.newDocumentBuilder();
	} catch (ParserConfigurationException e) {
	    e.printStackTrace();
	    return false;
	}

	if (ServiceHandler.coreFileService == null) {
	    LOG.error("The CoreFileService is null!");
	    return false;
	}

	// get the csarContent
	try {
	    csarContent = ServiceHandler.coreFileService.getCSAR(csarID);
	} catch (UserException exc) {
	    LOG.warn("An User Exception occured.", exc);
	    return false;
	}

	// get the main TOSCA
	listOfNewlyImportedDocuments.add(csarContent.getRootTOSCA());

	// while there are TOSCA files to process
	while (!listOfNewlyImportedDocuments.isEmpty()) {

	    // remove the current TOSCA file from the list
	    AbstractFile file = listOfNewlyImportedDocuments.remove();

	    LOG.trace("File is at \"" + file.getPath() + "\".");

	    // parse the file
	    Document doc = null;
	    try {
		doc = builder.parse(file.getFileAsInputStream());
		doc.getDocumentElement().normalize();
	    } catch (SAXException | IOException | SystemException e) {
		e.printStackTrace();
		LOG.error("There was an error while parsing a XML file.");
		return false;
	    }

	    Node root = doc.getFirstChild();

	    // some error checking and getting the namespace
	    if (null == root) {
		LOG.warn("An imported XML document has no content.");
		continue;
	    }

	    String ns = root.getNamespaceURI();
	    if ((null == ns) || ns.equals("")) {
		if ((null != root.getAttributes()) && (null != root.getAttributes().getNamedItem("xmlns"))) {
		    ns = root.getAttributes().getNamedItem("xmlns").getTextContent();
		}
	    }

	    if ((null == ns) || ns.equals("")) {
		LOG.error("An imported XML document has no namespace.");
		return false;
	    }

	    if (ns.endsWith("/")) {
		ns = ns.substring(0, ns.length() - 1);
	    }

	    // add the document for further processing
	    if (!mapOfNSToDocuments.containsKey(ns)) {
		mapOfNSToDocuments.put(ns, new ArrayList<Document>());
	    }
	    mapOfNSToDocuments.get(ns).add(doc);

	    // distinguish between TOSCA, WSLD and other XML
	    // TOSCA
	    if (ns.equals("http://docs.oasis-open.org/tosca/ns/2011/12") || ns.equals("http://docs.oasis-open.org/tosca/ns/2011/12/")) {
		LOG.trace("Found document is a TOSCA document.");
		listOfTOSCA.add(doc);

		Definitions def = ServiceHandler.xmlSerializerService.getXmlSerializer().unmarshal(doc);
		listOfTOSCADefinitions.add(def);

		QName defID = new QName(def.getTargetNamespace(), def.getId());
		String loc = file.getPath();
		ToscaEngineServiceImpl.toscaReferenceMapper.storeDefinitionsLocation(csarID, defID, loc);

		// resolve the imports of the TOSCA
		for (TImport imp : def.getImport()) {

		    String oldLocation = imp.getLocation();

		    if ((null == oldLocation) || oldLocation.trim().equals("")) {
			LOG.error("One import has no or an empty location attribute.");
			return false;
		    }

		    // try {
		    // location = URLDecoder.decode(location, "UTF-8");
		    // } catch (UnsupportedEncodingException e1) {
		    // this.LOG.error("The decoding of the location attribute of an import failed: {}",
		    // e1.getLocalizedMessage());
		    // e1.printStackTrace();
		    // continue;
		    // }

		    String location = PathResolver.resolveRelativePath(file.getPath(), oldLocation, csarContent);

		    LOG.trace("Import (at \"" + oldLocation + "\") should be at \"" + location + "\".");

		    AbstractFile newFile;
		    try {
			newFile = csarContent.getFile(URLDecoder.decode(location, "UTF-8"));
			if (null == newFile) {
			    LOG.error("The file at \"" + location + "\" does not exit");
			    return false;
			}

			doc = builder.parse(newFile.getFileAsInputStream());
			doc.getDocumentElement().normalize();
		    } catch (SAXException | IOException | SystemException e) {
			e.printStackTrace();
			LOG.error("There was an error while parsing a XML file.");
			return false;
		    }

		    // add the documents to the according lists
		    if (imp.getImportType().equals("http://docs.oasis-open.org/tosca/ns/2011/12") || imp.getImportType().equals("http://docs.oasis-open.org/tosca/ns/2011/12/")) {
			if (!alreadyImportedDocuments.contains(newFile)) {
			    listOfNewlyImportedDocuments.add(newFile);
			    alreadyImportedDocuments.add(newFile);
			}
		    } else if (imp.getImportType().equals("http://schemas.xmlsoap.org/wsdl") || imp.getImportType().equals("http://schemas.xmlsoap.org/wsdl/")) {
			if (!listOfWSDL.contains(doc)) {
			    listOfWSDL.add(doc);
			}
		    } else {
			if (!listOfXML.contains(doc)) {
			    listOfXML.add(doc);
			}
		    }
		}

	    }
	    // WSDL
	    else if (ns.equals("http://schemas.xmlsoap.org/wsdl") || ns.equals("http://schemas.xmlsoap.org/wsdl/")) {
		LOG.trace("Found document is a WSDL document.");
		listOfWSDL.add(doc);
	    }
	    // other XML
	    else {
		LOG.trace("Found document is a XML document.");
		listOfXML.add(doc);
	    }
	}

	LOG.debug("TOSCA:" + listOfTOSCA.size() + " WSDL:" + listOfWSDL.size() + " XML:" + listOfXML.size());
	return true;
    }

}
