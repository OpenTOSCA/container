package org.opentosca.planbuilder.importer.winery.context.impl.impl;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TArtifactType;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.model.tosca.TPolicyTemplate;
import org.eclipse.winery.model.tosca.TPolicyType;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TRelationshipTypeImplementation;
import org.eclipse.winery.repository.backend.IRepository;

import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
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
    protected final org.eclipse.winery.model.tosca.TDefinitions definitions;
    private final IRepository repository;
    private final Set<AbstractDefinitions> referencedDefinitions;
    private final Set<org.eclipse.winery.model.tosca.TDefinitions> allDefs;
    private final Set<Path> filesInCsar;
    private final Set<AbstractServiceTemplate> serviceTemplates = new HashSet<>();
    private final Set<TNodeType> nodeTypes = new HashSet<>();
    private final Set<TNodeTypeImplementation> nodeTypeImpls = new HashSet<>();
    private final Set<TRelationshipType> relationshipTypes = new HashSet<>();
    private final Set<TRelationshipTypeImplementation> relationshipTypeImpls = new HashSet<>();
    private final Set<TArtifactTemplate> artifactTemplates = new HashSet<>();
    private final Set<TArtifactType> artifactTypes = new HashSet<>();
    private final Set<TPolicyType> policyTypes = new HashSet<>();
    private final Set<TPolicyTemplate> policyTemlates = new HashSet<>();

    /**
     * Constructor with a Definitions file as File Object and all referenced File Artifacts as a File List
     *
     * @param mainDef     the File of the TOSCA Definitions to load as DefinitionsImpl
     * @param filesInCsar a list of Files referenced by the given Definitions
     */
    public DefinitionsImpl(final org.eclipse.winery.model.tosca.TDefinitions mainDef, final Collection<org.eclipse.winery.model.tosca.TDefinitions> allDefs, Collection<Path> filesInCsar, IRepository repository) {
        DefinitionsImpl.LOG.debug("Initializing DefinitionsImpl");
        this.repository = repository;
        this.definitions = mainDef;
        this.filesInCsar = new HashSet<>(filesInCsar);
        this.allDefs = new HashSet<>(allDefs);

        // HUGE assumption
        if (this.definitions.getServiceTemplates().isEmpty()) {
            this.referencedDefinitions = new HashSet<>();
        } else {
            this.referencedDefinitions = new HashSet<>(this.repository.getReferencedDefinitionsChildIds(new ServiceTemplateId(new QName(this.definitions.getServiceTemplates().get(0).getTargetNamespace(), this.definitions.getServiceTemplates().get(0).getId()))).stream().map(x -> new DefinitionsImpl(this.repository.getDefinitions(x), allDefs, filesInCsar, this.repository)).collect(Collectors.toList()));
        }

        allDefs.forEach(definitions -> {
            this.serviceTemplates.addAll(definitions.getServiceTemplates().stream().map(x -> new ServiceTemplateImpl(x, this)).collect(Collectors.toList()));
            this.nodeTypes.addAll(definitions.getNodeTypes().stream().collect(Collectors.toList()));
            this.relationshipTypes.addAll(definitions.getRelationshipTypes().stream().collect(Collectors.toList()));
            this.nodeTypeImpls.addAll(definitions.getNodeTypeImplementations().stream().collect(Collectors.toList()));
            this.relationshipTypeImpls.addAll(definitions.getRelationshipTypeImplementations().stream().collect(Collectors.toList()));
            this.artifactTemplates.addAll(definitions.getArtifactTemplates().stream().collect(Collectors.toList()));
            this.artifactTypes.addAll(definitions.getArtifactTypes().stream().collect(Collectors.toList()));
            this.policyTypes.addAll(definitions.getPolicyTypes().stream().collect(Collectors.toList()));
            this.policyTemlates.addAll(definitions.getPolicyTemplates().stream().collect(Collectors.toList()));
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
    public Collection<TNodeType> getNodeTypes() {
        return this.nodeTypes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<TRelationshipType> getRelationshipTypes() {
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
    public Collection<TNodeTypeImplementation> getNodeTypeImplementations() {
        return this.nodeTypeImpls;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<TRelationshipTypeImplementation> getRelationshipTypeImplementations() {
        return this.relationshipTypeImpls;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<TArtifactTemplate> getArtifactTemplates() {
        return this.artifactTemplates;
    }

    /**
     * Returns a List of all nodeTypes in the current csar context of this definitions document
     *
     * @return a List of TNodeType
     */
    protected List<TNodeType> getAllNodeTypes() {
        final List<TNodeType> nodeTypes = new ArrayList<>();

        nodeTypes.addAll(this.getNodeTypes());

        this.referencedDefinitions.forEach(x -> nodeTypes.addAll(x.getNodeTypes()));

        return nodeTypes;
    }

    /**
     * Returns a List of all policyTypes in the current csar context of this definitions document
     *
     * @return a List of PolicyTypes
     */
    protected List<TPolicyType> getAllPolicyTypes() {
        final List<TPolicyType> policyTypes = new ArrayList<>();

        policyTypes.addAll(this.getPolicyTypes());
        this.referencedDefinitions.forEach(x -> policyTypes.addAll(x.getPolicyTypes()));

        return policyTypes;
    }

    protected List<TPolicyTemplate> getAllPolicyTemplates() {
        final List<TPolicyTemplate> policyTemplates = new ArrayList<>();

        policyTemplates.addAll(this.getPolicyTemplates());
        this.referencedDefinitions.forEach(x -> policyTemplates.addAll(x.getPolicyTemplates()));

        return policyTemplates;
    }

    /**
     * >>>>>>> master Returns a List of all nodeTypes in the current csar context of this definitions document
     *
     * @return a List of TNodeType
     */
    protected List<TRelationshipType> getAllRelationshipTypes() {
        final List<TRelationshipType> relationshipTypes = new ArrayList<>();

        relationshipTypes.addAll(this.getRelationshipTypes());
        this.referencedDefinitions.forEach(x -> relationshipTypes.addAll(x.getRelationshipTypes()));

        return relationshipTypes;
    }

    protected List<TArtifactType> getAllArtifactTypes() {
        final List<TArtifactType> artifactTypes = new ArrayList<>();

        artifactTypes.addAll(this.getArtifactTypes());
        this.referencedDefinitions.forEach(x -> artifactTypes.addAll(x.getArtifactTypes()));

        return artifactTypes;
    }

    @Override
    public Collection<TArtifactType> getArtifactTypes() {
        return this.artifactTypes;
    }

    @Override
    public Collection<TPolicyType> getPolicyTypes() {
        return this.policyTypes;
    }

    @Override
    public Collection<TPolicyTemplate> getPolicyTemplates() {
        return this.policyTemlates;
    }
}
