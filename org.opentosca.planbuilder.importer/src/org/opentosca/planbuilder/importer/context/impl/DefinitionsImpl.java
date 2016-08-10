package org.opentosca.planbuilder.importer.context.impl;

import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.oasis_open.docs.tosca.ns._2011._12.Definitions;
import org.oasis_open.docs.tosca.ns._2011._12.TArtifactTemplate;
import org.oasis_open.docs.tosca.ns._2011._12.TExtensibleElements;
import org.oasis_open.docs.tosca.ns._2011._12.TImport;
import org.oasis_open.docs.tosca.ns._2011._12.TNodeType;
import org.oasis_open.docs.tosca.ns._2011._12.TNodeTypeImplementation;
import org.oasis_open.docs.tosca.ns._2011._12.TRelationshipType;
import org.oasis_open.docs.tosca.ns._2011._12.TRelationshipTypeImplementation;
import org.oasis_open.docs.tosca.ns._2011._12.TServiceTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractArtifactReference;
import org.opentosca.planbuilder.model.tosca.AbstractArtifactTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractNodeType;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTypeImplementation;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipType;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTypeImplementation;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.opentosca.core.model.artifact.file.AbstractFile;
import org.opentosca.exceptions.SystemException;

/**
 * <p>
 * This class implements AbstractDefinitions
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class DefinitionsImpl extends AbstractDefinitions {

	private final static Logger LOG = LoggerFactory.getLogger(DefinitionsImpl.class);

	private Definitions definitions;
	private List<DefinitionsImpl> referencedDefinitions = null;
	private Set<DefinitionsImpl> allFoundDefinitions = null;
	private Set<AbstractFile> filesInCsar = null;
	private List<AbstractServiceTemplate> serviceTemplates = null;
	private List<AbstractNodeType> nodeTypes = null;
	private List<AbstractNodeTypeImplementation> nodeTypeImpls = null;
	private List<AbstractRelationshipType> relationshipTypes = null;
	private List<AbstractRelationshipTypeImplementation> relationshipTypeImpls = null;
	private List<AbstractArtifactTemplate> artifactTemplates = null;


	/**
	 * Constructor with a Definitions file as File Object and all referenced
	 * File Artifacts as a File List
	 *
	 * @param mainDefFile the File of the TOSCA Definitions to load as
	 *            DefinitionsImpl
	 * @param filesInCsar a list of Files referenced by the given Definitions
	 * @param isEntryDefinitions gives information whether the given definitions
	 *            document is an entry definition
	 */
	public DefinitionsImpl(AbstractFile mainDefFile, Set<AbstractFile> filesInCsar, boolean isEntryDefinitions) {
		DefinitionsImpl.LOG.debug("Initializing DefinitionsImpl");
		this.definitions = this.parseDefinitionsFile(mainDefFile);
		this.filesInCsar = filesInCsar;
		this.referencedDefinitions = new ArrayList<DefinitionsImpl>();

		// resolve imported definitions
		// TODO XSD,WSDL they are just checked with the file ending
		for (AbstractFile def : this.resolveImportedDefinitions()) {
			if (def == null) {
				DefinitionsImpl.LOG.warn("Resolving of imported Definitions produced file which is null");
				continue;
			}
			DefinitionsImpl.LOG.debug("Adding DefintionsImpl with file location {}", def.getPath());
			this.referencedDefinitions.add(new DefinitionsImpl(def, this.filesInCsar, false));
		}

		this.allFoundDefinitions = this.findAllDefinitions();
		this.serviceTemplates = new ArrayList<AbstractServiceTemplate>();
		this.nodeTypes = new ArrayList<AbstractNodeType>();
		this.nodeTypeImpls = new ArrayList<AbstractNodeTypeImplementation>();
		this.relationshipTypes = new ArrayList<AbstractRelationshipType>();
		this.relationshipTypeImpls = new ArrayList<AbstractRelationshipTypeImplementation>();
		this.artifactTemplates = new ArrayList<AbstractArtifactTemplate>();
		this.initTypesAndTemplates();

		if (isEntryDefinitions) {
			this.updateDefinitionsReferences(this.allFoundDefinitions);
		}

	}

	/**
	 * Resolves TOSCA Definitions imports for this DefinitionsImpl by
	 * initializing imported Definitions as another DefinitionsImpl each.
	 *
	 * @return a List of Files of the resolved, referenced Definitions
	 */
	private List<AbstractFile> resolveImportedDefinitions() {
		List<AbstractFile> importedDefinitions = new ArrayList<AbstractFile>();
		DefinitionsImpl.LOG.debug("Checking import elements in JAXB Definitions object");
		if (this.definitions.getImport() != null) {
			for (TImport imported : this.definitions.getImport()) {
				DefinitionsImpl.LOG.debug("Check import element with namespace: {} location: {} importType: {}", imported.getNamespace(), imported.getLocation(), imported.getImportType());
				// check if importtype is tosca ns, the location is set (else
				// there's nothing to parse) and just for looks the string
				// shouldn't
				// be empty
				if (imported.getImportType().equals("http://docs.oasis-open.org/tosca/ns/2011/12") && (imported.getLocation() != null) && !imported.getLocation().equals("")) {
					// found definitions import
					// get it
					// parse it
					// add it
					DefinitionsImpl.LOG.debug("Trying to add Definitions import");
					importedDefinitions.add(this.getFileByLocation(imported.getLocation(), this.filesInCsar));

				}

			}
		}
		return importedDefinitions;
	}

	/**
	 * Searches through the given list of files, which contains the given
	 * location.
	 *
	 *
	 * @param location the location to look for as String
	 * @param files a List of Files to look trough
	 * @return if files.contains(file), where file.getPath().contains(location)
	 *         is true, file is returned, else null
	 */
	private AbstractFile getFileByLocation(String location, Set<AbstractFile> files) {
		DefinitionsImpl.LOG.debug("Looking trough files to for given location: {}", location);
		for (AbstractFile file : files) {
			DefinitionsImpl.LOG.debug("Check file with location: {}", location);
			// lazy check
			LOG.debug("File has location {}", file.getPath());
			if (file.getPath().contains(location)) {
				DefinitionsImpl.LOG.debug("Found Match");
				return file;
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<AbstractServiceTemplate> getServiceTemplates() {
		return this.serviceTemplates;
	}

	/**
	 * Adds an AbstractServiceTemplate to this DefinitionsImpl
	 *
	 * @param serviceTemplate an AbstractServiceTemplate to add to
	 */
	public void addServiceTemplate(AbstractServiceTemplate serviceTemplate) {
		this.serviceTemplates.add(serviceTemplate);
	}

	/**
	 * Adds a List of AbstractServiceTemplate to this DefinitionsImpl
	 *
	 * @param serviceTemplates a List of AbstractServiceTemplate to add to this
	 *            DefinitionsImpl
	 */
	public void addServiceTemplates(List<AbstractServiceTemplate> serviceTemplates) {
		this.serviceTemplates = serviceTemplates;
	}

	/**
	 * Adds an AbstractNodeType to this DefinitionsImpl
	 *
	 * @param nodeType a AbstractNodeType to add to this DefinitionsImpl
	 */
	public void addNodeType(AbstractNodeType nodeType) {
		this.nodeTypes.add(nodeType);
	}

	/**
	 * Adds an AbstractRelationshipType to this DefinitionsImpl
	 *
	 * @param relationshipType a AbstractRelationshipType to add to this
	 *            DefinitionsImpl
	 */
	public void addRelationshipType(AbstractRelationshipType relationshipType) {
		this.relationshipTypes.add(relationshipType);
	}

	/**
	 * Initializes the types and templates given by the internal JAXB model,
	 * into the higher level model of DefinitionsImpl
	 */
	private void initTypesAndTemplates() {
		for (TExtensibleElements element : this.definitions.getServiceTemplateOrNodeTypeOrNodeTypeImplementation()) {
			if (element instanceof TServiceTemplate) {
				this.addServiceTemplate(new ServiceTemplateImpl((TServiceTemplate) element, this));
			}
			if (element instanceof TNodeType) {
				this.addNodeType(new NodeTypeImpl((TNodeType) element, this));
			}
			if (element instanceof TRelationshipType) {
				this.addRelationshipType(new RelationshipTypeImpl((TRelationshipType) element, this));
			}
			if (element instanceof TNodeTypeImplementation) {
				this.addNodeTypeImplementation(new NodeTypeImplementationImpl((TNodeTypeImplementation) element, this));
			}
			if (element instanceof TRelationshipTypeImplementation) {
				this.addRelationshipTypeImplementation(new RelationshipTypeImplementationImpl((TRelationshipTypeImplementation) element, this));
			}
			if (element instanceof TArtifactTemplate) {
				this.addArtifactTemplate(new ArtifactTemplateImpl((TArtifactTemplate) element, this));
			}
		}

		if (this.definitions.getTypes() != null) {
			for (Object obj : this.definitions.getTypes().getAny()) {
				if (obj instanceof TNodeType) {
					this.addNodeType(new NodeTypeImpl((TNodeType) obj, this));
				}
			}
		}

	}

	/**
	 * Adds an AbstractArtifactTemplate to this DefinitionsImpl
	 *
	 * @param artifactTemplate an AbstractArtifactTemplate to add to this
	 *            DefinitionsImpl
	 */
	public void addArtifactTemplate(AbstractArtifactTemplate artifactTemplate) {
		this.artifactTemplates.add(artifactTemplate);
	}

	/**
	 * Adds an NodeTypeImplementationImpl to this DefinitionsImpl
	 *
	 * @param nodeTypeImplementationImpl an NodeTypeImplementationImpl to add to
	 *            this DefinitionsImpl
	 */
	public void addNodeTypeImplementation(NodeTypeImplementationImpl nodeTypeImplementationImpl) {
		this.nodeTypeImpls.add(nodeTypeImplementationImpl);
	}

	/**
	 * Adds an RelationshipTypeImplementationImpl to this DefinitionsImpl
	 *
	 * @param relationshipTypeImpl an RelationshipTypeImplementationImpl to add
	 *            to this DefinitionsImpl
	 */
	public void addRelationshipTypeImplementation(RelationshipTypeImplementationImpl relationshipTypeImpl) {
		this.relationshipTypeImpls.add(relationshipTypeImpl);
	}

	/**
	 * Returns the JAXB Definitions class
	 *
	 * @return the TOSCA Definitions of this DefinitionsImpl as JAXB class
	 */
	protected Definitions getDefinitions() {
		return this.definitions;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<AbstractNodeType> getNodeTypes() {
		return this.nodeTypes;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<AbstractRelationshipType> getRelationshipTypes() {
		return this.relationshipTypes;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTargetNamespace() {
		return this.definitions.getTargetNamespace();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<AbstractDefinitions> getImportedDefinitions() {
		return (List<AbstractDefinitions>) (List<?>) this.referencedDefinitions;
	}

	/**
	 * Parses the given file to a JAXB Definitions class
	 *
	 * @param file a File denoting to a TOSCA Definitions file
	 * @return a JAXB Definitions class object if parsing was without errors,
	 *         else null
	 */
	private Definitions parseDefinitionsFile(AbstractFile file) {
		Definitions def = null;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance("org.oasis_open.docs.tosca.ns._2011._12");
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			DefinitionsImpl.LOG.debug("Trying to parse file {} into JAXB object", file.getPath());
			def = (Definitions) unmarshaller.unmarshal(new InputStreamReader(file.getFileAsInputStream()));
		} catch (JAXBException e) {
			DefinitionsImpl.LOG.error("Error while parsing file, maybe file is not a TOSCA Defintions File", e);
			return null;
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			LOG.error("Exception within Core", e);
			return null;
		}
		return def;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getId() {
		return this.definitions.getId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return this.definitions.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<AbstractNodeTypeImplementation> getNodeTypeImplementations() {
		return this.nodeTypeImpls;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<AbstractRelationshipTypeImplementation> getRelationshipTypeImplementations() {
		return this.relationshipTypeImpls;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<AbstractArtifactTemplate> getArtifactTemplates() {
		return this.artifactTemplates;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public File getAbsolutePathOfArtifactReference(AbstractArtifactReference ref) {
		// TODO this is just a fast hack
		String path = ref.getReference();
		for (AbstractFile file : this.filesInCsar) {
			if (file.getPath().contains(path)) {
				try {
					return file.getFile().toFile();
				} catch (SystemException e) {
					LOG.error("Exception within core", e);
				}
			}
		}
		return null;
	}

	/**
	 * Tries to find all definitions recursively trough imported definitions by
	 * this definitions document
	 *
	 * @return a Set of DefinitionsImpl
	 */
	private Set<DefinitionsImpl> findAllDefinitions() {
		Set<DefinitionsImpl> foundDefs = new HashSet<DefinitionsImpl>();

		for (DefinitionsImpl def : this.referencedDefinitions) {
			foundDefs.add(def);
			foundDefs.addAll(def.findAllDefinitions());
		}

		foundDefs.add(this);
		return foundDefs;
	}

	/**
	 * Updates all allFoundDefinitions set recursively trough out the topology
	 * of the imports
	 *
	 * @param updateSet a Set of DefinitionsImpl
	 */
	private void updateDefinitionsReferences(Set<DefinitionsImpl> defs) {

		for (DefinitionsImpl def : this.referencedDefinitions) {
			def.updateDefinitionsReferences(defs);
		}
		this.allFoundDefinitions = defs;
	}

	/**
	 * Returns a List of all nodeTypes in the current csar context of this definitions document
	 * @return a List of AbstractNodeType
	 */
	protected List<AbstractNodeType> getAllNodeTypes() {
		List<AbstractNodeType> nodeTypes = new ArrayList<AbstractNodeType>();

		for (DefinitionsImpl def : this.allFoundDefinitions) {
			nodeTypes.addAll(def.getNodeTypes());
		}
		return nodeTypes;
	}

	/**
	 * Returns a List of all nodeTypes in the current csar context of this definitions document
	 * @return a List of AbstractNodeType
	 */
	protected List<AbstractRelationshipType> getAllRelationshipTypes() {
		List<AbstractRelationshipType> relationshipTypes = new ArrayList<AbstractRelationshipType>();

		for (DefinitionsImpl def : this.allFoundDefinitions) {
			relationshipTypes.addAll(def.getRelationshipTypes());
		}
		return relationshipTypes;
	}

}
