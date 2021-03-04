package org.opentosca.planbuilder.importer.winery.context.impl.impl;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.repository.backend.IRepository;

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

    private final IRepository repository;
    protected final org.eclipse.winery.model.tosca.TDefinitions definitions;
    private final Set<AbstractDefinitions> referencedDefinitions;
    private final Set<org.eclipse.winery.model.tosca.TDefinitions> allDefs;
    private final Set<Path> filesInCsar;
    private final Set<AbstractServiceTemplate> serviceTemplates = new HashSet<>();
    private final Set<AbstractNodeType> nodeTypes = new HashSet<>();
    private final Set<AbstractNodeTypeImplementation> nodeTypeImpls = new HashSet<>();
    private final Set<AbstractRelationshipType> relationshipTypes = new HashSet<>();
    private final Set<AbstractRelationshipTypeImplementation> relationshipTypeImpls= new HashSet<>();
    private final Set<AbstractArtifactTemplate> artifactTemplates= new HashSet<>();
    private final Set<AbstractArtifactType> artifactTypes= new HashSet<>();
    private final Set<AbstractPolicyType> policyTypes= new HashSet<>();
    private final Set<AbstractPolicyTemplate> policyTemlates= new HashSet<>();



    /**
     * Constructor with a Definitions file as File Object and all referenced File Artifacts as a File List
     *
     * @param mainDef        the File of the TOSCA Definitions to load as DefinitionsImpl
     * @param filesInCsar        a list of Files referenced by the given Definitions
     */
    public DefinitionsImpl(final org.eclipse.winery.model.tosca.TDefinitions mainDef, final Collection<org.eclipse.winery.model.tosca.TDefinitions> allDefs, Collection<Path> filesInCsar, IRepository repository) {
        DefinitionsImpl.LOG.debug("Initializing DefinitionsImpl");
        this.repository = repository;
        this.definitions = mainDef;
        this.filesInCsar = new HashSet<>(filesInCsar);
        this.allDefs = new HashSet<>(allDefs);



        // HUGE assumption
        if(this.definitions.getServiceTemplates().isEmpty()){
            this.referencedDefinitions = new HashSet<>();
        } else {
            this.referencedDefinitions = new HashSet<>(this.repository.getReferencedDefinitionsChildIds(new ServiceTemplateId(new QName(this.definitions.getServiceTemplates().get(0).getTargetNamespace(), this.definitions.getServiceTemplates().get(0).getId()))).stream().map(x -> new DefinitionsImpl(this.repository.getDefinitions(x), allDefs, filesInCsar, this.repository)).collect(Collectors.toList()));
        }

        allDefs.forEach(definitions ->{
            this.serviceTemplates.addAll(definitions.getServiceTemplates().stream().map(x -> new ServiceTemplateImpl(x, this)).collect(Collectors.toList()));
            this.nodeTypes.addAll(definitions.getNodeTypes().stream().map(x -> new NodeTypeImpl(x, this)).collect(Collectors.toList()));
            this.relationshipTypes.addAll(definitions.getRelationshipTypes().stream().map(x -> new RelationshipTypeImpl(x, this)).collect(Collectors.toList()));
            this.nodeTypeImpls.addAll(definitions.getNodeTypeImplementations().stream().map(x -> new NodeTypeImplementationImpl(x, this)).collect(Collectors.toList()));
            this.relationshipTypeImpls.addAll(definitions.getRelationshipTypeImplementations().stream().map(x -> new RelationshipTypeImplementationImpl(x, this)).collect(Collectors.toList()));
            this.artifactTemplates.addAll(definitions.getArtifactTemplates().stream().map(x -> new ArtifactTemplateImpl(x, this)).collect(Collectors.toList()));
            this.artifactTypes.addAll(definitions.getArtifactTypes().stream().map(x -> new ArtifactTypeImpl(x, this)).collect(Collectors.toList()));
            this.policyTypes.addAll(definitions.getPolicyTypes().stream().map(x -> new PolicyTypeImpl(x, this)).collect(Collectors.toList()));
            this.policyTemlates.addAll(definitions.getPolicyTemplates().stream().map(x -> new PolicyTemplateImpl(x, this)).collect(Collectors.toList()));
        });



    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<AbstractServiceTemplate> getServiceTemplates() {
        return this.serviceTemplates;
    }

    /**
     * Returns the JAXB Definitions class
     *
     * @return the TOSCA Definitions of this DefinitionsImpl as JAXB class
     */
    protected org.eclipse.winery.model.tosca.TDefinitions getDefinitions() {
        return this.definitions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<AbstractNodeType> getNodeTypes() {
        return this.nodeTypes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<AbstractRelationshipType> getRelationshipTypes() {
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
    public Collection<? extends AbstractDefinitions> getImportedDefinitions() {
        return this.referencedDefinitions;
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
    public Collection<AbstractNodeTypeImplementation> getNodeTypeImplementations() {
        return this.nodeTypeImpls;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<AbstractRelationshipTypeImplementation> getRelationshipTypeImplementations() {
        return this.relationshipTypeImpls;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<AbstractArtifactTemplate> getArtifactTemplates() {
        return this.artifactTemplates;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getAbsolutePathOfArtifactReference(final AbstractArtifactReference ref) {
        // TODO this is just a fast hack
        final String path = ref.getReference();
        for (final Path file : this.filesInCsar) {
            if (file.toString().contains(path)) {
                    return file.toFile();
            }
        }
        return null;
    }



    /**
     * Returns a List of all nodeTypes in the current csar context of this definitions document
     *
     * @return a List of AbstractNodeType
     */
    protected List<AbstractNodeType> getAllNodeTypes() {
        final List<AbstractNodeType> nodeTypes = new ArrayList<>();

        nodeTypes.addAll(this.getNodeTypes());

        this.referencedDefinitions.forEach(x -> nodeTypes.addAll(x.getNodeTypes()));

        return nodeTypes;
    }

    /**
     * Returns a List of all policyTypes in the current csar context of this definitions document
     *
     * @return a List of PolicyTypes
     */
    protected List<AbstractPolicyType> getAllPolicyTypes() {
        final List<AbstractPolicyType> policyTypes = new ArrayList<>();

        policyTypes.addAll(this.getPolicyTypes());
        this.referencedDefinitions.forEach(x -> policyTypes.addAll(x.getPolicyTypes()));

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

        policyTemplates.addAll(this.getPolicyTemplates());
        this.referencedDefinitions.forEach(x -> policyTemplates.addAll(x.getPolicyTemplates()));

        return policyTemplates;
    }

    /**
     * >>>>>>> master Returns a List of all nodeTypes in the current csar context of this definitions document
     *
     * @return a List of AbstractNodeType
     */
    protected List<AbstractRelationshipType> getAllRelationshipTypes() {
        final List<AbstractRelationshipType> relationshipTypes = new ArrayList<>();

        relationshipTypes.addAll(this.getRelationshipTypes());
        this.referencedDefinitions.forEach(x -> relationshipTypes.addAll(x.getRelationshipTypes()));

        return relationshipTypes;
    }

    protected List<AbstractArtifactType> getAllArtifactTypes() {
        final List<AbstractArtifactType> artifactTypes = new ArrayList<>();

        artifactTypes.addAll(this.getArtifactTypes());
        this.referencedDefinitions.forEach(x -> artifactTypes.addAll(x.getArtifactTypes()));

        return artifactTypes;
    }

    @Override
    public Collection<AbstractArtifactType> getArtifactTypes() {
        return this.artifactTypes;
    }

    @Override
    public Collection<AbstractPolicyType> getPolicyTypes() {
        return this.policyTypes;
    }

    @Override
    public Collection<AbstractPolicyTemplate> getPolicyTemplates() {
        return this.policyTemlates;
    }
}
