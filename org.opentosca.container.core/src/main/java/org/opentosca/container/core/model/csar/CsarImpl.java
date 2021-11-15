package org.opentosca.container.core.model.csar;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.eclipse.winery.model.ids.definitions.PolicyTypeId;
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
import org.eclipse.winery.model.tosca.TPolicyType;
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
    private final Optional<ServiceTemplateId> entryServiceTemplate;
    // TODO evaluate putting the save-location into an additional field here!
    private final IRepository wineryRepo;

    private final Map<QName, TArtifactTemplate> artifactTemplates;
    private final Map<QName, TArtifactType> artifactTypes;
    private final Map<QName, TNodeType> nodeTypes;
    private final Map<QName, TNodeTypeImplementation> nodeTypeImplementations;
    private final Map<QName, TPolicyTemplate> policyTemplates;
    private final Map<QName, TPolicyType> policyTypes;
    private final Map<QName, TRelationshipType> relationshipTypes;
    private final Map<QName, TRelationshipTypeImplementation> relationshipTypeImplementations;
    private final Map<QName, TServiceTemplate> serviceTemplates;

    // this is just for bridging purposes
    @Deprecated
    private @NonNull
    final Path saveLocation;

    public CsarImpl(@NonNull CsarId id, @NonNull Path location) {
        this.id = id;
        this.saveLocation = location;
        this.wineryRepo = RepositoryFactory.getRepository(location);
        entryServiceTemplate = readEntryServiceTemplate(location);
        this.artifactTemplates = this.wineryRepo.getQNameToElementMapping(ArtifactTemplateId.class);
        this.artifactTypes = this.wineryRepo.getQNameToElementMapping(ArtifactTypeId.class);
        this.nodeTypes = this.wineryRepo.getQNameToElementMapping(NodeTypeId.class);
        this.nodeTypeImplementations = this.wineryRepo.getQNameToElementMapping(NodeTypeImplementationId.class);
        this.policyTemplates = this.wineryRepo.getQNameToElementMapping(PolicyTemplateId.class);
        this.policyTypes = this.wineryRepo.getQNameToElementMapping(PolicyTypeId.class);
        this.relationshipTypes = this.wineryRepo.getQNameToElementMapping(RelationshipTypeId.class);
        this.relationshipTypeImplementations = this.wineryRepo.getQNameToElementMapping(RelationshipTypeImplementationId.class);
        this.serviceTemplates = this.wineryRepo.getQNameToElementMapping(ServiceTemplateId.class);
    }

    private Optional<ServiceTemplateId> readEntryServiceTemplate(Path csarLocation) {
        String qname = null;
        try {
            qname = new String(Files.readAllBytes(csarLocation.resolve(ENTRY_SERVICE_TEMPLATE_LOCATION)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            // Swallow, no helping this
        }
        return qname == null ? Optional.empty()
            : Optional.ofNullable(new ServiceTemplateId(QName.valueOf(qname)));
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
        TArtifactType artifactType = new TArtifactType();
        artifactType.setId(artifactTypeName);
        artifactType.setTargetNamespace(artifactTypeNamespace);
        ArtifactTypeId artTypeId = new ArtifactTypeId(artifactType.getQName());

        this.wineryRepo.setElement(artTypeId, artifactType);

        // ArtifactTemplate handling
        TArtifactTemplate artifactTemplate = new TArtifactTemplate();

        artifactTemplate.setId(artifactTemplateName);
        artifactTemplate.setName(artifactTemplateName);
        ArtifactTemplateId artTemplateId = new ArtifactTemplateId(artifactTemplateQName);

        // hier artifactType verwenden
        artifactTemplate.setType(new QName(artifactTypeNamespace, artifactTypeName));

        this.wineryRepo.setElement(artTemplateId, artifactTemplate);
        ArtifactTemplateFilesDirectoryId artFileId = new ArtifactTemplateFilesDirectoryId(artTemplateId);
        RepositoryFileReference fileRef = new RepositoryFileReference(artFileId, "stateArtifact.state");
        this.wineryRepo.putContentToFile(fileRef, inputStream, MediaType.parse("application/x-state"));
        BackendUtils.synchronizeReferences(this.wineryRepo, artTemplateId);

        TServiceTemplate servTemp = this.wineryRepo.getElement(serviceTemplateId);
        for (TNodeTemplate allNestedNodeTemplate : BackendUtils.getAllNestedNodeTemplates(servTemp)) {
            if (allNestedNodeTemplate.getId().equals(nodeTemplateId)) {
                TDeploymentArtifact deplArt = new TDeploymentArtifact();
                // von oben
                deplArt.setArtifactType(artifactType.getQName());
                deplArt.setArtifactRef(artTemplateId.getQName());

                deplArt.setId(nodeTemplateId + "_StateArtifact");
                allNestedNodeTemplate.getDeploymentArtifacts().add(deplArt);

                this.wineryRepo.setElement(serviceTemplateId, servTemp);
                break;
            }
        }

        // update the ArtifactTemplates list
        this.artifactTemplates.putAll(this.wineryRepo.getQNameToElementMapping(ArtifactTemplateId.class));
    }

    @Override
    public Map<QName, TArtifactType> artifactTypesMap() {
        return this.artifactTypes;
    }

    @Override
    public List<TServiceTemplate> serviceTemplates() {
        return wineryRepo.getAllDefinitionsChildIds(ServiceTemplateId.class).stream()
            .map(wineryRepo::getElement)
            .collect(Collectors.toList());
    }

    @Override
    public List<TPolicyTemplate> policyTemplates() {
        return new ArrayList<>(this.policyTemplates.values());
    }

    @Override
    public TServiceTemplate entryServiceTemplate() {
        // FIXME stop mapping between Optional and nullable.
        if (entryServiceTemplate.isPresent()) {
            return wineryRepo.getElement(entryServiceTemplate.get());
        }
        return null;
    }

    @Override
    public List<TDefinitions> definitions() {
        return wineryRepo.getAllDefinitionsChildIds().stream()
            .map(wineryRepo::getDefinitions)
            .collect(Collectors.toList());
    }

    @Override
    public List<TExportedOperation> exportedOperations() {
        return serviceTemplates().stream()
            .map(TServiceTemplate::getBoundaryDefinitions)
            .map(TBoundaryDefinitions::getInterfaces)
            .flatMap(Collection::stream)
            .map(TExportedInterface::getOperation)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

    @Override
    public List<TPlan> plans() {
        @SuppressWarnings("null")
        List<TPlan> plans = Optional.ofNullable(entryServiceTemplate())
            .map(TServiceTemplate::getPlans)
            .orElse(Collections.emptyList());
        return plans;
    }

    @Override
    @Nullable
    public Application selfserviceMetadata() {
        // FIXME stop bridging optional to null
        if (!entryServiceTemplate.isPresent()) {
            return null;
        }
        SelfServiceMetaDataId metadata = new SelfServiceMetaDataId(entryServiceTemplate.get());
        return SelfServiceMetaDataUtils.getApplication(this.wineryRepo, metadata);
    }

    @Override
    public List<TNodeType> nodeTypes() {
        return new ArrayList<>(this.nodeTypes.values());
    }

    @Override
    public Map<QName, TNodeType> nodeTypesMap() {
        return this.nodeTypes;
    }

    @Override
    public List<TNodeTypeImplementation> nodeTypeImplementations() {
        return new ArrayList<>(this.nodeTypeImplementations.values());
    }

    @Override
    public Map<QName, TNodeTypeImplementation> nodeTypeImplementationsMap() {
        return this.nodeTypeImplementations;
    }

    @Override
    public List<TRelationshipType> relationshipTypes() {
        return new ArrayList<>(this.relationshipTypes.values());
    }

    @Override
    public List<TRelationshipTypeImplementation> relationshipTypeImplementations() {
        return new ArrayList<>(this.relationshipTypeImplementations.values());
    }

    @Override
    public TExtensibleElements queryRepository(DefinitionsChildId id) {
        return wineryRepo.getElement(id);
    }

    @Override
    public String description() {
        Application metadata = selfserviceMetadata();
        return metadata == null ? "" : metadata.getDescription();
    }

    @Override
    public void exportTo(Path targetPath) throws IOException {
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
        return this.saveLocation;
    }

    @Override
    public String toString() {
        return id().csarName();
    }
}
