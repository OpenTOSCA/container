package org.opentosca.container.legacy.core.engine.resolver.resolver;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.*;

import javax.inject.Inject;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.engine.impl.ToscaReferenceMapper;
import org.opentosca.container.core.engine.xml.IXMLSerializerService;
import org.opentosca.container.core.model.AbstractFile;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.eclipse.winery.model.tosca.Definitions;
import org.eclipse.winery.model.tosca.TImport;
import org.opentosca.container.legacy.core.engine.resolver.PathResolver;
import org.opentosca.container.legacy.core.model.CSARContent;
import org.opentosca.container.legacy.core.service.ICoreFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * The DefinitionsResolver resolves references inside of TOSCA Definitions according to the TOSCA
 * specification wd14. The resolving reaches elements of the Definitions Documents of the passed
 * CSAR and elements inside of imported files of Definitions Documents. Each found element and the
 * document in which the element is nested is stored by the org.opentosca.toscaengine.service.impl.
 * toscareferencemapping.ToscaReferenceMapper.
 * <p>
 * Preconditions for resolving a Definitions: The Definitions has to be valid in all kind of
 * meanings.
 *
 * @author endrescn@fachschaft.informatik.uni-stuttgart.de
 */
@Deprecated
public class DefinitionsResolver {

  private static final Logger LOG = LoggerFactory.getLogger(DefinitionsResolver.class);

  @Inject
  private static ICoreFileService coreFileService;
  @Inject
  private static IXMLSerializerService xmlSerializerService;

  // list documents which are processed
  private List<Document> listOfTOSCA = new ArrayList<>();
  private List<Definitions> listOfTOSCADefinitions = new ArrayList<>();
  private List<Document> listOfXML = new ArrayList<>();
  private List<Document> listOfWSDL = new ArrayList<>();
  private Map<String, List<Document>> mapOfNSToDocuments = new HashMap<>();


  private final ToscaReferenceMapper toscaReferenceMapper;

  public DefinitionsResolver(ToscaReferenceMapper referenceMapper) {
    this.toscaReferenceMapper = referenceMapper;
  }

  /**
   * Resolves the referenced TOSCA files inside of a CSAR and stores the mapping into the
   * ToscaReferenceMapper.
   *
   * @param csarID The ID of the passed CSAR which shall be resolved.
   * @return true means no error, false one or more errors
   */
  public boolean resolveDefinitions(final CSARID csarID) {

    LOG.info("Start resolving of the CSAR \"" + csarID.getFileName() + "\".");

    // first of all search all documents
    boolean errorOccured = !this.resolveImports(csarID);
    if (errorOccured) {
      LOG.error("There was an error while searching for the imports of the TOSCA documents.");
      return false;
    }

    this.toscaReferenceMapper.storeListOfWSDLForCSAR(csarID, this.listOfWSDL);

    // if no error occurred, start the resolving of references
    LOG.info("All import elements are resolvable, now starting the resolving.");

    // initialize all needed resolver
    final ReferenceMapper referenceMapper = new ReferenceMapper(csarID, this.mapOfNSToDocuments, toscaReferenceMapper);
    final ExtensionsResolver extensionResolver = new ExtensionsResolver(referenceMapper);
    final TypesResolver typesResolver = new TypesResolver(referenceMapper);
    final ServiceTemplateResolver serviceTemplateResolver = new ServiceTemplateResolver(referenceMapper, toscaReferenceMapper);
    final NodeTypeResolver nodeTypeResolver = new NodeTypeResolver(referenceMapper);
    final NodeTypeImplementationResolver nodeTypeImplementationResolver =
      new NodeTypeImplementationResolver(referenceMapper, toscaReferenceMapper, csarID);
    final CapabilityTypeResolver capabilityTypeResolver = new CapabilityTypeResolver(referenceMapper);
    final RequirementTypeResolver requirementTypeResolver = new RequirementTypeResolver(referenceMapper);
    final RelationshipTypeResolver relationshipTypeResolver = new RelationshipTypeResolver(referenceMapper);
    final RelationshipTypeImplementationResolver relationshipTypeImplementationResolver =
      new RelationshipTypeImplementationResolver(referenceMapper);
    final ArtifactTypeResolver artifactTypeResolver = new ArtifactTypeResolver(referenceMapper);
    final ArtifactTemplateResolver artifactTemplateResolver = new ArtifactTemplateResolver(referenceMapper);
    final PolicyTypeResolver policyTypeResolver = new PolicyTypeResolver(referenceMapper);
    final PolicyTemplateResolver policyTemplateResolver = new PolicyTemplateResolver(referenceMapper);

    // resolve each Definitions content
    for (final Definitions definitionsToResolve : this.listOfTOSCADefinitions) {

      this.toscaReferenceMapper.storeDefinitions(csarID, definitionsToResolve);
      LOG.info("Start to resolve the Definitions \"{" + definitionsToResolve.getTargetNamespace() + "}"
        + definitionsToResolve.getId() + "\".");

      errorOccured |= extensionResolver.resolve(definitionsToResolve);
      errorOccured |= typesResolver.resolve(definitionsToResolve);
      errorOccured |= serviceTemplateResolver.resolve(definitionsToResolve, csarID);
      errorOccured |= nodeTypeResolver.resolve(definitionsToResolve);
      errorOccured |= nodeTypeImplementationResolver.resolve(definitionsToResolve);
      errorOccured |= relationshipTypeResolver.resolve(definitionsToResolve);
      errorOccured |= relationshipTypeImplementationResolver.resolve(definitionsToResolve);
      errorOccured |= requirementTypeResolver.resolve(definitionsToResolve);
      errorOccured |= capabilityTypeResolver.resolve(definitionsToResolve);
      errorOccured |= artifactTypeResolver.resolve(definitionsToResolve);
      errorOccured |= artifactTemplateResolver.resolve(definitionsToResolve);
      errorOccured |= policyTypeResolver.resolve(definitionsToResolve);
      errorOccured |= policyTemplateResolver.resolve(definitionsToResolve);

    }

    // if an error occurred the TOSCA Proccessing was not successfull, thus
    // delete the stored data
    if (errorOccured) {
      LOG.error("Resolving of the CSAR \"" + csarID.getFileName() + "\" was not successfull!");
      LOG.debug("Deleting stored references.");
      this.toscaReferenceMapper.clearCSARContent(csarID);
      return false;
    }

    LOG.info("Resolving of the CSAR \"" + csarID.getFileName() + "\" was successfull!");
    return true;
  }

  /**
   * This method traverses the tree of imports of TOSCA documents starting in the main TOSCA defined
   * in the TOSCA meta file of the CSAR.
   * <p>
   * TODO prevent cycles in the imports of other TOSCA documents
   *
   * @param csarID of the CSAR
   * @return true means no error, false one or more errors
   */
  private boolean resolveImports(final CSARID csarID) {

    // DocumentBuilder for parsing the files
    final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder;
    try {
      dbf.setNamespaceAware(true);
      dbf.setIgnoringComments(true);
      builder = dbf.newDocumentBuilder();
    } catch (final ParserConfigurationException e) {
      e.printStackTrace();
      return false;
    }

    if (coreFileService == null) {
      LOG.error("The CoreFileService is null!");
      return false;
    }

    // get the csarContent
    CSARContent csarContent = null;
    try {
      csarContent = coreFileService.getCSAR(csarID);
    } catch (final UserException exc) {
      LOG.warn("An User Exception occured.", exc);
      return false;
    }

    // get the main TOSCA
    final Set<AbstractFile> alreadyImportedDocuments = new HashSet<>();
    final Deque<AbstractFile> listOfNewlyImportedDocuments = new LinkedList<>();
    listOfNewlyImportedDocuments.add(csarContent.getRootTOSCA());

    // while there are TOSCA files to process
    while (!listOfNewlyImportedDocuments.isEmpty()) {

      // remove the current TOSCA file from the list
      final AbstractFile file = listOfNewlyImportedDocuments.remove();
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

      final Node root = doc.getFirstChild();

      // some error checking and getting the namespace
      if (null == root) {
        LOG.warn("An imported XML document has no content.");
        continue;
      }

      String ns = root.getNamespaceURI();
      if (null == ns || ns.equals("")) {
        if (null != root.getAttributes() && null != root.getAttributes().getNamedItem("xmlns")) {
          ns = root.getAttributes().getNamedItem("xmlns").getTextContent();
        }
      }

      if (null == ns || ns.equals("")) {
        LOG.error("An imported XML document has no namespace.");
        return false;
      }

      if (ns.endsWith("/")) {
        ns = ns.substring(0, ns.length() - 1);
      }

      // add the document for further processing
      if (!this.mapOfNSToDocuments.containsKey(ns)) {
        this.mapOfNSToDocuments.put(ns, new ArrayList<Document>());
      }
      this.mapOfNSToDocuments.get(ns).add(doc);

      // distinguish between TOSCA, WSLD and other XML
      // TOSCA
      if (ns.equals("http://docs.oasis-open.org/tosca/ns/2011/12") || ns.equals("http://docs.oasis-open.org/tosca/ns/2011/12/")) {
        LOG.trace("Found document is a TOSCA document.");
        this.listOfTOSCA.add(doc);

        final Definitions def = xmlSerializerService.getXmlSerializer().unmarshal(doc);
        this.listOfTOSCADefinitions.add(def);

        final QName defID = new QName(def.getTargetNamespace(), def.getId());
        final String loc = file.getPath();
        toscaReferenceMapper.storeDefinitionsLocation(csarID, defID, loc);

        // resolve the imports of the TOSCA
        for (final TImport imp : def.getImport()) {

          final String oldLocation = imp.getLocation();

          if (null == oldLocation || oldLocation.trim().equals("")) {
            LOG.error("One import has no or an empty location attribute.");
            return false;
          }

          final String location = PathResolver.resolveRelativePath(file.getPath(), oldLocation, csarContent);
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
            if (!this.listOfWSDL.contains(doc)) {
              this.listOfWSDL.add(doc);
            }
          } else {
            if (!this.listOfXML.contains(doc)) {
              this.listOfXML.add(doc);
            }
          }
        }
      } else if (ns.equals("http://schemas.xmlsoap.org/wsdl") || ns.equals("http://schemas.xmlsoap.org/wsdl/")) {
        // WSDL
        LOG.trace("Found document is a WSDL document.");
        this.listOfWSDL.add(doc);
      } else {
        // other XML
        LOG.trace("Found document is a XML document.");
        this.listOfXML.add(doc);
      }
    }
    LOG.debug("TOSCA:" + this.listOfTOSCA.size() + " WSDL:" + this.listOfWSDL.size() + " XML:" + this.listOfXML.size());
    return true;
  }

}
