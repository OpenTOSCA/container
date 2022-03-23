package org.opentosca.container.core.model.csar;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.eclipse.winery.accountability.exceptions.AccountabilityException;
import org.eclipse.winery.model.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.model.ids.definitions.ArtifactTypeId;
import org.eclipse.winery.model.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.model.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.ids.definitions.NodeTypeImplementationId;
import org.eclipse.winery.model.ids.definitions.PolicyTemplateId;
import org.eclipse.winery.model.ids.definitions.RelationshipTypeId;
import org.eclipse.winery.model.ids.definitions.RelationshipTypeImplementationId;
import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.selfservice.Application;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TArtifactType;
import org.eclipse.winery.model.tosca.TBoundaryDefinitions;
import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TExportedInterface;
import org.eclipse.winery.model.tosca.TExportedOperation;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.model.tosca.TPolicyTemplate;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TRelationshipTypeImplementation;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.backend.SelfServiceMetaDataUtils;
import org.eclipse.winery.repository.common.RepositoryFileReference;
import org.eclipse.winery.repository.datatypes.ids.elements.ArtifactTemplateFilesDirectoryId;
import org.eclipse.winery.repository.datatypes.ids.elements.SelfServiceMetaDataId;
import org.eclipse.winery.repository.exceptions.RepositoryCorruptException;
import org.eclipse.winery.repository.export.CsarExporter;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.tika.mime.MediaType;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CsarImpl implements Csar {

    public static final String ENTRY_SERVICE_TEMPLATE_LOCATION = "EntryServiceTemplate";
    private static final Logger LOGGER = LoggerFactory.getLogger(CsarImpl.class);

    @NonNull
    private final CsarId id;
    // TODO evaluate putting the save-location into an additional field here!
    private final Path location;
    private final IRepository wineryRepo;
    private Optional<ServiceTemplateId> entryServiceTemplate;
    private TServiceTemplate entryServiceTemplateModel;
    private Map<QName, TDefinitions> definitions;
    private Map<QName, TArtifactTemplate> artifactTemplates;
    private Map<QName, TArtifactType> artifactTypes;
    private Map<QName, TNodeType> nodeTypes;
    private Map<QName, TNodeTypeImplementation> nodeTypeImplementations;
    private Map<QName, TPolicyTemplate> policyTemplates;
    private Map<QName, TRelationshipType> relationshipTypes;
    private Map<QName, TRelationshipTypeImplementation> relationshipTypeImplementations;

    public CsarImpl(@NonNull CsarId id, @NonNull Path location) {
        this.id = id;
        this.location = location;
        this.wineryRepo = RepositoryFactory.getRepository(location);
        this.loadContents();
    }

    private void loadContents() {
        this.entryServiceTemplate = readEntryServiceTemplate(this.location);
        this.entryServiceTemplateModel = this.entryServiceTemplate();
        this.definitions = this.getQNameToDefinitionsMap();
        this.artifactTemplates = this.wineryRepo.getQNameToElementMapping(ArtifactTemplateId.class);
        this.artifactTypes = this.wineryRepo.getQNameToElementMapping(ArtifactTypeId.class);
        this.nodeTypes = this.wineryRepo.getQNameToElementMapping(NodeTypeId.class);
        this.nodeTypeImplementations = this.wineryRepo.getQNameToElementMapping(NodeTypeImplementationId.class);
        this.policyTemplates = this.wineryRepo.getQNameToElementMapping(PolicyTemplateId.class);
        this.relationshipTypes = this.wineryRepo.getQNameToElementMapping(RelationshipTypeId.class);
        this.relationshipTypeImplementations = this.wineryRepo.getQNameToElementMapping(RelationshipTypeImplementationId.class);
    }

    private Optional<ServiceTemplateId> readEntryServiceTemplate(Path csarLocation) {
        String qname = null;
        try {
            qname = Files.readString(csarLocation.resolve(ENTRY_SERVICE_TEMPLATE_LOCATION));
        } catch (IOException e) {
            // Swallow, no helping this
            //
            // How about instead of swallowing, we throw something more useful?
            throw new RuntimeException("Couldn't find entryServiceTemplate", e);
        }
        return qname == null ? Optional.empty()
            : Optional.of(new ServiceTemplateId(QName.valueOf(qname)));
    }

    @Override
    public CsarId id() {
        return id;
    }

    @Override
    public List<TArtifactTemplate> artifactTemplates() {
        return new ArrayList<>(this.artifactTemplates.values());
    }

    public void addArtifactTemplate(InputStream inputStream, ServiceTemplateId serviceTemplateId, String nodeTemplateId) throws IOException {

        final String artifactTypeNamespace = "http://opentosca.org/artifacttypes";
        final String artifactTypeName = "State";
        //final QName artifactTypeQName = new QName (artifactTypeNamespace, artifactTypeName);

        final String artifactTemplateNamespace = "http://opentosca.org/stateartifacttemplates";
        final String artifactTemplateName = serviceTemplateId.getQName().getLocalPart() + "_" + nodeTemplateId + "_StateArtifactTemplate";
        final QName artifactTemplateQName = new QName(artifactTemplateNamespace, artifactTemplateName);

        // ArtifactType handling
        TArtifactType artifactType = new TArtifactType.Builder(artifactTypeName)
            .setTargetNamespace(artifactTypeNamespace)
            .build();
        ArtifactTypeId artTypeId = new ArtifactTypeId(artifactType.getQName());

        this.wineryRepo.setElement(artTypeId, artifactType);

        // ArtifactTemplate handling
        TArtifactTemplate artifactTemplate =
            new TArtifactTemplate.Builder(artifactTemplateName, artifactType.getQName())
                .setName(artifactTemplateName)
                .build();
        ArtifactTemplateId artTemplateId = new ArtifactTemplateId(artifactTemplateQName);

        this.wineryRepo.setElement(artTemplateId, artifactTemplate);
        ArtifactTemplateFilesDirectoryId artFileId = new ArtifactTemplateFilesDirectoryId(artTemplateId);
        RepositoryFileReference fileRef = new RepositoryFileReference(artFileId, "stateArtifact.state");
        this.wineryRepo.putContentToFile(fileRef, inputStream, MediaType.parse("application/x-state"));
        BackendUtils.synchronizeReferences(this.wineryRepo, artTemplateId);

        TServiceTemplate servTemp = this.wineryRepo.getElement(serviceTemplateId);
        for (TNodeTemplate nestedNodeTemplate : BackendUtils.getAllNestedNodeTemplates(servTemp)) {
            if (nestedNodeTemplate.getId().equals(nodeTemplateId)) {
                TDeploymentArtifact deploymentArtifact =
                    new TDeploymentArtifact.Builder(nodeTemplateId + "_StateArtifact", artifactType.getQName())
                        .setArtifactRef(artTemplateId.getQName())
                        .setArtifactRef(artTemplateId.getQName())
                        .build();

                if (nestedNodeTemplate.getDeploymentArtifacts() == null) {
                    nestedNodeTemplate.setDeploymentArtifacts(new ArrayList<>());
                }
                nestedNodeTemplate.getDeploymentArtifacts().add(deploymentArtifact);

                this.wineryRepo.setElement(serviceTemplateId, servTemp);
                break;
            }
        }

        // update the ArtifactTemplates list
        this.artifactTemplates.putAll(this.wineryRepo.getQNameToElementMapping(ArtifactTemplateId.class));
    }

    @Override
    public @NonNull Map<QName, TArtifactType> artifactTypesMap() {
        return this.artifactTypes;
    }

    @Override
    public @NonNull List<TServiceTemplate> serviceTemplates() {
        return wineryRepo.getAllDefinitionsChildIds(ServiceTemplateId.class).stream()
            .map(wineryRepo::getElement)
            .collect(Collectors.toList());
    }

    @Override
    public @NonNull List<TPolicyTemplate> policyTemplates() {
        return new ArrayList<>(this.policyTemplates.values());
    }

    @Override
    public TServiceTemplate entryServiceTemplate() {
        // FIXME stop mapping between Optional and nullable.
        if (this.entryServiceTemplateModel == null && entryServiceTemplate.isPresent()) {
            return wineryRepo.getElement(entryServiceTemplate.get());
        }
        return this.entryServiceTemplateModel;
    }

    @Override
    public @NonNull List<TDefinitions> definitions() {
        return Lists.newArrayList(this.definitions.values());
    }

    @Override
    public @NonNull List<TExportedOperation> exportedOperations() {
        return serviceTemplates().stream()
            .map(TServiceTemplate::getBoundaryDefinitions)
            .filter(Objects::nonNull)
            .map(TBoundaryDefinitions::getInterfaces)
            .flatMap(Collection::stream)
            .map(TExportedInterface::getOperation)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

    @Override
    public @NonNull List<TPlan> plans() {
        return Optional.ofNullable(entryServiceTemplate())
            .map(TServiceTemplate::getPlans)
            .orElse(Collections.emptyList());
    }

    @Override
    @Nullable
    public Application selfserviceMetadata() {
        // FIXME stop bridging optional to null
        if (this.entryServiceTemplate.isEmpty()) {
            return null;
        }
        SelfServiceMetaDataId metadata = new SelfServiceMetaDataId(entryServiceTemplate.get());
        return SelfServiceMetaDataUtils.getApplication(this.wineryRepo, metadata);
    }

    @Override
    public @NonNull List<TNodeType> nodeTypes() {
        return new ArrayList<>(this.nodeTypes.values());
    }

    @Override
    public @NonNull Map<QName, TNodeType> nodeTypesMap() {
        return this.nodeTypes;
    }

    @Override
    public @NonNull List<TNodeTypeImplementation> nodeTypeImplementations() {
        return new ArrayList<>(this.nodeTypeImplementations.values());
    }

    @Override
    public @NonNull Map<QName, TNodeTypeImplementation> nodeTypeImplementationsMap() {
        return this.nodeTypeImplementations;
    }

    @Override
    public @NonNull List<TRelationshipType> relationshipTypes() {
        return new ArrayList<>(this.relationshipTypes.values());
    }

    @Override
    public @NonNull List<TRelationshipTypeImplementation> relationshipTypeImplementations() {
        return new ArrayList<>(this.relationshipTypeImplementations.values());
    }

    @Override
    public TExtensibleElements queryRepository(@NonNull DefinitionsChildId id) {
        return wineryRepo.getElement(id);
    }

    @Override
    public @NonNull String description() {
        Application metadata = selfserviceMetadata();
        return metadata == null ? "" : metadata.getDescription();
    }

    @Override
    public void exportTo(@NonNull Path targetPath) throws IOException {
        CsarExporter exporter = new CsarExporter(this.wineryRepo);
        Map<String, Object> exportConfiguration = new HashMap<>();
        // Do not check hashes and do not store immutably => don't put anything into the export configuration
        try (OutputStream out = Files.newOutputStream(targetPath, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)) {
            try {
                exporter.writeCsar(entryServiceTemplate.get(), out, exportConfiguration);
            } catch (RepositoryCorruptException | InterruptedException | AccountabilityException | ExecutionException e) {
                LOGGER.warn("Exporting the csar failed with an exception", e);
                throw new IOException("Failed to export CSAR", e);
            }
        }
    }

    @Override
    public @NonNull Path getSaveLocation() {
        return this.wineryRepo.getRepositoryRoot();
    }

    @Override
    public void reload() {
        this.entryServiceTemplateModel = null;
        this.loadContents();
    }

    @Override
    public @NonNull String toString() {
        return id().csarName();
    }

    private Map<QName, TDefinitions> getQNameToDefinitionsMap() {
        Map<QName, TDefinitions> result = Maps.newHashMap();
        Collection<DefinitionsChildId> ids = this.wineryRepo.getAllDefinitionsChildIds();
        ids.forEach(x -> result.put(x.getQName(), this.wineryRepo.getDefinitions(x)));
        return result;
    }
}
