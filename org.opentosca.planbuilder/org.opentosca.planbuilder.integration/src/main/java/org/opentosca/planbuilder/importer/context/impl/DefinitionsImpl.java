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
import org.oasis_open.docs.tosca.ns._2011._12.ObjectFactory;
import org.oasis_open.docs.tosca.ns._2011._12.TArtifactTemplate;
import org.oasis_open.docs.tosca.ns._2011._12.TArtifactType;
import org.oasis_open.docs.tosca.ns._2011._12.TExtensibleElements;
import org.oasis_open.docs.tosca.ns._2011._12.TImport;
import org.oasis_open.docs.tosca.ns._2011._12.TNodeType;
import org.oasis_open.docs.tosca.ns._2011._12.TNodeTypeImplementation;
import org.oasis_open.docs.tosca.ns._2011._12.TPolicyTemplate;
import org.oasis_open.docs.tosca.ns._2011._12.TPolicyType;
import org.oasis_open.docs.tosca.ns._2011._12.TRelationshipType;
import org.oasis_open.docs.tosca.ns._2011._12.TRelationshipTypeImplementation;
import org.oasis_open.docs.tosca.ns._2011._12.TServiceTemplate;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.model.AbstractFile;
import org.opentosca.planbuilder.model.tosca.AbstractArtifactReference;
import org.opentosca.planbuilder.model.tosca.AbstractArtifactTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractArtifactType;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractNodeType;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTypeImplementation;
import org.opentosca.planbuilder.model.tosca.AbstractPolicyTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractPolicyType;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipType;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTypeImplementation;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This class implements AbstractDefinitions
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public class DefinitionsImpl extends AbstractDefinitions {

    private final static Logger LOG = LoggerFactory.getLogger(DefinitionsImpl.class);

    private final Definitions definitions;
    private List<DefinitionsImpl> referencedDefinitions = null;
    private Set<DefinitionsImpl> allFoundDefinitions = null;
    private Set<AbstractFile> filesInCsar = null;
    private List<AbstractServiceTemplate> serviceTemplates = null;
    private List<AbstractNodeType> nodeTypes = null;
    private List<AbstractNodeTypeImplementation> nodeTypeImpls = null;
    private List<AbstractRelationshipType> relationshipTypes = null;
    private List<AbstractRelationshipTypeImplementation> relationshipTypeImpls = null;
    private List<AbstractArtifactTemplate> artifactTemplates = null;
    private List<AbstractArtifactType> artifactTypes = null;
    private List<AbstractPolicyType> policyTypes = null;
    private List<AbstractPolicyTemplate> policyTemlates = null;

    /**
     * Constructor with a Definitions file as File Object and all referenced File Artifacts as a File List
     *
     * @param mainDefFile        the File of the TOSCA Definitions to load as DefinitionsImpl
     * @param filesInCsar        a list of Files referenced by the given Definitions
     * @param isEntryDefinitions gives information whether the given definitions document is an entry definition
     */
    public DefinitionsImpl(final AbstractFile mainDefFile, final Set<AbstractFile> filesInCsar,
                           final boolean isEntryDefinitions) {
        DefinitionsImpl.LOG.debug("Initializing DefinitionsImpl");
        this.definitions = parseDefinitionsFile(mainDefFile);
        this.filesInCsar = filesInCsar;
        this.referencedDefinitions = new ArrayList<>();

        // resolve imported definitions
        // TODO XSD,WSDL they are just checked with the file ending
        for (final AbstractFile def : resolveImportedDefinitions()) {
            if (def == null) {
                DefinitionsImpl.LOG.warn("Resolving of imported Definitions produced file which is null");
                continue;
            }
            DefinitionsImpl.LOG.debug("Adding DefintionsImpl with file location {}", def.getPath());
            this.referencedDefinitions.add(new DefinitionsImpl(def, this.filesInCsar, false));
        }

        this.allFoundDefinitions = findAllDefinitions();
        this.serviceTemplates = new ArrayList<>();
        this.nodeTypes = new ArrayList<>();
        this.nodeTypeImpls = new ArrayList<>();
        this.relationshipTypes = new ArrayList<>();
        this.relationshipTypeImpls = new ArrayList<>();
        this.artifactTemplates = new ArrayList<>();
        this.artifactTypes = new ArrayList<>();
        this.policyTypes = new ArrayList<>();
        this.policyTemlates = new ArrayList<>();
        initTypesAndTemplates();

        if (isEntryDefinitions) {
            updateDefinitionsReferences(this.allFoundDefinitions);
        }
    }

    /**
     * Resolves TOSCA Definitions imports for this DefinitionsImpl by initializing imported Definitions as another
     * DefinitionsImpl each.
     *
     * @return a List of Files of the resolved, referenced Definitions
     */
    private List<AbstractFile> resolveImportedDefinitions() {
        final List<AbstractFile> importedDefinitions = new ArrayList<>();
        DefinitionsImpl.LOG.debug("Checking import elements in JAXB Definitions object");
        if (this.definitions.getImport() != null) {
            for (final TImport imported : this.definitions.getImport()) {
                DefinitionsImpl.LOG.debug("Check import element with namespace: {} location: {} importType: {}",
                    imported.getNamespace(), imported.getLocation(), imported.getImportType());
                // check if importtype is tosca ns, the location is set (else
                // there's nothing to parse) and just for looks the string
                // shouldn't
                // be empty
                if (imported.getImportType().equals("http://docs.oasis-open.org/tosca/ns/2011/12")
                    && imported.getLocation() != null && !imported.getLocation().equals("")) {
                    // found definitions import
                    // get it
                    // parse it
                    // add it
                    DefinitionsImpl.LOG.debug("Trying to add Definitions import");
                    importedDefinitions.add(getFileByLocation(imported.getLocation(), this.filesInCsar));
                }
            }
        }
        return importedDefinitions;
    }

    /**
     * Searches through the given list of files, which contains the given location.
     *
     * @param location the location to look for as String
     * @param files    a List of Files to look trough
     * @return if files.contains(file), where file.getPath().contains(location) is true, file is returned, else null
     */
    private AbstractFile getFileByLocation(final String location, final Set<AbstractFile> files) {
        DefinitionsImpl.LOG.debug("Looking trough files to for given location: {}", location);
        for (final AbstractFile file : files) {
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
    public void addServiceTemplate(final AbstractServiceTemplate serviceTemplate) {
        this.serviceTemplates.add(serviceTemplate);
    }

    /**
     * Adds a List of AbstractServiceTemplate to this DefinitionsImpl
     *
     * @param serviceTemplates a List of AbstractServiceTemplate to add to this DefinitionsImpl
     */
    public void addServiceTemplates(final List<AbstractServiceTemplate> serviceTemplates) {
        this.serviceTemplates = serviceTemplates;
    }

    /**
     * Adds an AbstractNodeType to this DefinitionsImpl
     *
     * @param nodeType a AbstractNodeType to add to this DefinitionsImpl
     */
    public void addNodeType(final AbstractNodeType nodeType) {
        this.nodeTypes.add(nodeType);
    }

    /**
     * Adds an AbstractRelationshipType to this DefinitionsImpl
     *
     * @param relationshipType a AbstractRelationshipType to add to this DefinitionsImpl
     */
    public void addRelationshipType(final AbstractRelationshipType relationshipType) {
        this.relationshipTypes.add(relationshipType);
    }

    /**
     * Initializes the types and templates given by the internal JAXB model, into the higher level model of
     * DefinitionsImpl
     */
    private void initTypesAndTemplates() {
        for (final TExtensibleElements element : this.definitions.getServiceTemplateOrNodeTypeOrNodeTypeImplementation()) {
            if (element instanceof TServiceTemplate) {
                addServiceTemplate(new ServiceTemplateImpl((TServiceTemplate) element, this));
            }
            if (element instanceof TNodeType) {
                addNodeType(new NodeTypeImpl((TNodeType) element, this));
            }
            if (element instanceof TRelationshipType) {
                addRelationshipType(new RelationshipTypeImpl((TRelationshipType) element, this));
            }
            if (element instanceof TNodeTypeImplementation) {
                addNodeTypeImplementation(new NodeTypeImplementationImpl((TNodeTypeImplementation) element, this));
            }
            if (element instanceof TRelationshipTypeImplementation) {
                addRelationshipTypeImplementation(new RelationshipTypeImplementationImpl(
                    (TRelationshipTypeImplementation) element, this));
            }
            if (element instanceof TArtifactTemplate) {
                addArtifactTemplate(new ArtifactTemplateImpl((TArtifactTemplate) element, this));
            }
            if (element instanceof TArtifactType) {
                addArtifactType(new ArtifactTypeImpl(this, (TArtifactType) element));
            }
            if (element instanceof TPolicyType) {
                addPolicyType(new PolicyTypeImpl((TPolicyType) element, this));
            }
            if (element instanceof TPolicyTemplate) {
                addPolicyTemplate(new PolicyTemplateImpl((TPolicyTemplate) element, this));
            }
        }

        if (this.definitions.getTypes() != null) {
            for (final Object obj : this.definitions.getTypes().getAny()) {
                if (obj instanceof TNodeType) {
                    addNodeType(new NodeTypeImpl((TNodeType) obj, this));
                }
            }
        }
    }

    /**
     * Adds an AbstractPolicyType to this DefinitionsImpl
     *
     * @param policyType an AbstractPolicyType to add to this DefinitionsImpl
     */
    public void addPolicyType(final AbstractPolicyType policyType) {
        this.policyTypes.add(policyType);
    }

    /**
     * Adds an AbstractPolicyTemplate to this DefinitionsImpl
     *
     * @param policyTemplate an AbstractPolicyTemplate to add to this DefinitionsImpl
     */
    public void addPolicyTemplate(final AbstractPolicyTemplate policyTemplate) {
        this.policyTemlates.add(policyTemplate);
    }

    /**
     * Adds an AbstractArtifactTemplate to this DefinitionsImpl
     *
     * @param artifactTemplate an AbstractArtifactTemplate to add to this DefinitionsImpl
     */
    public void addArtifactTemplate(final AbstractArtifactTemplate artifactTemplate) {
        this.artifactTemplates.add(artifactTemplate);
    }

    /**
     * Adds the given {@link AbstractArtifactType} to this {@link DefinitionsImpl}
     *
     * @param artifactType an {@link AbstractArtifactType}
     */
    public void addArtifactType(final AbstractArtifactType artifactType) {
        this.artifactTypes.add(artifactType);
    }

    /**
     * Adds an NodeTypeImplementationImpl to this DefinitionsImpl
     *
     * @param nodeTypeImplementationImpl an NodeTypeImplementationImpl to add to this DefinitionsImpl
     */
    public void addNodeTypeImplementation(final NodeTypeImplementationImpl nodeTypeImplementationImpl) {
        this.nodeTypeImpls.add(nodeTypeImplementationImpl);
    }

    /**
     * Adds an RelationshipTypeImplementationImpl to this DefinitionsImpl
     *
     * @param relationshipTypeImpl an RelationshipTypeImplementationImpl to add to this DefinitionsImpl
     */
    public void addRelationshipTypeImplementation(final RelationshipTypeImplementationImpl relationshipTypeImpl) {
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
    public List<? extends AbstractDefinitions> getImportedDefinitions() {
        return this.referencedDefinitions;
    }

    /**
     * Parses the given file to a JAXB Definitions class
     *
     * @param file a File denoting to a TOSCA Definitions file
     * @return a JAXB Definitions class object if parsing was without errors, else null
     */
    private Definitions parseDefinitionsFile(final AbstractFile file) {
        Definitions def = null;
        try {
            final JAXBContext jaxbContext =
                JAXBContext.newInstance("org.oasis_open.docs.tosca.ns._2011._12", ObjectFactory.class.getClassLoader());
            final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            DefinitionsImpl.LOG.debug("Trying to parse file {} into JAXB object", file.getPath());
            def = (Definitions) unmarshaller.unmarshal(new InputStreamReader(file.getFileAsInputStream()));
        } catch (final JAXBException e) {
            DefinitionsImpl.LOG.error("Error while parsing file, maybe file is not a TOSCA Defintions File", e);
            return null;
        } catch (final SystemException e) {
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
    public File getAbsolutePathOfArtifactReference(final AbstractArtifactReference ref) {
        // TODO this is just a fast hack
        final String path = ref.getReference();
        for (final AbstractFile file : this.filesInCsar) {
            if (file.getPath().contains(path)) {
                try {
                    return file.getFile().toFile();
                } catch (final SystemException e) {
                    LOG.error("Exception within core", e);
                }
            }
        }
        return null;
    }

    /**
     * Tries to find all definitions recursively trough imported definitions by this definitions document
     *
     * @return a Set of DefinitionsImpl
     */
    private Set<DefinitionsImpl> findAllDefinitions() {
        final Set<DefinitionsImpl> foundDefs = new HashSet<>();

        for (final DefinitionsImpl def : this.referencedDefinitions) {
            foundDefs.add(def);
            foundDefs.addAll(def.findAllDefinitions());
        }

        foundDefs.add(this);
        return foundDefs;
    }

    /**
     * Updates all allFoundDefinitions set recursively trough out the topology of the imports
     *
     * @param updateSet a Set of DefinitionsImpl
     */
    private void updateDefinitionsReferences(final Set<DefinitionsImpl> defs) {

        for (final DefinitionsImpl def : this.referencedDefinitions) {
            def.updateDefinitionsReferences(defs);
        }
        this.allFoundDefinitions = defs;
    }

    /**
     * Returns a List of all nodeTypes in the current csar context of this definitions document
     *
     * @return a List of AbstractNodeType
     */
    protected List<AbstractNodeType> getAllNodeTypes() {
        final List<AbstractNodeType> nodeTypes = new ArrayList<>();

        for (final DefinitionsImpl def : this.allFoundDefinitions) {
            nodeTypes.addAll(def.getNodeTypes());
        }
        return nodeTypes;
    }

    /**
     * Returns a List of all policyTypes in the current csar context of this definitions document
     *
     * @return a List of PolicyTypes
     */
    protected List<AbstractPolicyType> getAllPolicyTypes() {
        final List<AbstractPolicyType> policyTypes = new ArrayList<>();

        for (final DefinitionsImpl def : this.allFoundDefinitions) {
            policyTypes.addAll(def.getPolicyTypes());
        }
        return policyTypes;
    }

    /**
     * <<<<<<< HEAD ======= Returns a List of all policyTemplates in the current csar context of this definitions
     * document
     *
     * @return a List of PolicyTemplates
     */
    protected List<AbstractPolicyTemplate> getAllPolicyTemplates() {
        final List<AbstractPolicyTemplate> policyTemplates = new ArrayList<>();
        for (final DefinitionsImpl def : this.allFoundDefinitions) {
            policyTemplates.addAll(def.getPolicyTemplates());
        }
        return policyTemplates;
    }

    /**
     * >>>>>>> master Returns a List of all nodeTypes in the current csar context of this definitions document
     *
     * @return a List of AbstractNodeType
     */
    protected List<AbstractRelationshipType> getAllRelationshipTypes() {
        final List<AbstractRelationshipType> relationshipTypes = new ArrayList<>();

        for (final DefinitionsImpl def : this.allFoundDefinitions) {
            relationshipTypes.addAll(def.getRelationshipTypes());
        }

        return relationshipTypes;
    }

    protected List<AbstractArtifactType> getAllArtifactTypes() {
        final List<AbstractArtifactType> artifactTypes = new ArrayList<>();

        for (final DefinitionsImpl def : this.allFoundDefinitions) {
            artifactTypes.addAll(def.getArtifactTypes());
        }

        return artifactTypes;
    }

    @Override
    public List<AbstractArtifactType> getArtifactTypes() {
        return this.artifactTypes;
    }

    @Override
    public List<AbstractPolicyType> getPolicyTypes() {
        return this.policyTypes;
    }

    @Override
    public List<AbstractPolicyTemplate> getPolicyTemplates() {
        return this.policyTemlates;
    }
}
