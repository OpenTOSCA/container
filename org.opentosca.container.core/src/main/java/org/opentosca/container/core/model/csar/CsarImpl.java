package org.opentosca.container.core.model.csar;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.winery.accountability.exceptions.AccountabilityException;
import org.eclipse.winery.common.ids.definitions.*;
import org.eclipse.winery.model.selfservice.Application;
import org.eclipse.winery.model.tosca.*;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.backend.SelfServiceMetaDataUtils;
import org.eclipse.winery.repository.datatypes.ids.elements.SelfServiceMetaDataId;
import org.eclipse.winery.repository.exceptions.RepositoryCorruptException;
import org.eclipse.winery.repository.export.CsarExporter;
import org.opentosca.container.core.model.AbstractFile;
import org.opentosca.container.core.model.csar.backwards.FileSystemFile;
import org.opentosca.container.core.model.csar.backwards.ToscaMetaFileReplacement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CsarImpl implements Csar {

  private static final Logger LOGGER = LoggerFactory.getLogger(CsarImpl.class);

  @NonNull
  private final CsarId id;
  private final Optional<ServiceTemplateId> entryServiceTemplate;
  // TODO evaluate putting the savelocation into an additional field here!
  private IRepository wineryRepo;

  // this is just for bridging purposes
  @Deprecated
  private @NonNull Path saveLocation;

  public CsarImpl(@NonNull CsarId id, @NonNull Path location) {
    this.id = id;
    this.saveLocation = location;
    wineryRepo = RepositoryFactory.getRepository(location);
    entryServiceTemplate = readEntryServiceTemplate(location);
  }

  private Optional<ServiceTemplateId> readEntryServiceTemplate(Path csarLocation) {
    String qname = null;
    try {
      // FIXME magic string constant
      qname = new String(Files.readAllBytes(csarLocation.resolve("EntryServiceTemplate")), StandardCharsets.UTF_8);
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
    return wineryRepo.getAllDefinitionsChildIds(ArtifactTemplateId.class).stream()
      .map(wineryRepo::getElement)
      .collect(Collectors.toList());
  }

  @Override
  public List<TServiceTemplate> serviceTemplates() {
    return wineryRepo.getAllDefinitionsChildIds(ServiceTemplateId.class).stream()
      .map(wineryRepo::getElement)
      .collect(Collectors.toList());
  }

  @Override
  public List<TPolicyTemplate> policyTemplates() {
    return wineryRepo.getAllDefinitionsChildIds(PolicyTemplateId.class).stream()
      .map(wineryRepo::getElement)
      .collect(Collectors.toList());
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
      .map(TBoundaryDefinitions.Interfaces::getInterface)
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
      .map(TPlans::getPlan)
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
    return SelfServiceMetaDataUtils.getApplication(metadata);
  }

  @Override
  public List<TNodeType> nodeTypes() {
    return wineryRepo.getAllDefinitionsChildIds(NodeTypeId.class).stream()
      .map(wineryRepo::getElement)
      .collect(Collectors.toList());
  }

  @Override
  public List<TNodeTypeImplementation> nodeTypeImplementations() {
    return wineryRepo.getAllDefinitionsChildIds(NodeTypeImplementationId.class).stream()
      .map(wineryRepo::getElement)
      .collect(Collectors.toList());
  }

  @Override
  public List<TRelationshipTypeImplementation> relationshipTypeImplementations() {
    return wineryRepo.getAllDefinitionsChildIds(RelationshipTypeImplementationId.class).stream()
      .map(wineryRepo::getElement)
      .collect(Collectors.toList());
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
  public AbstractFile topologyPicture() {
    final String imageUrl = selfserviceMetadata().getImageUrl();
    return new FileSystemFile(Paths.get(imageUrl));
  }

  @Override
  public void exportTo(Path targetPath) throws IOException {
    CsarExporter exporter = new CsarExporter();
    Map<String, Object> exportConfiguration = new HashMap<>();
    // Do not check hashes and do not store immutably => don't put anything into the export configuration
    try (OutputStream out = Files.newOutputStream(targetPath, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)) {
      try {
        // FIXME CsarExporter.addManifest throws NoClassDefFoundError for Environment's initialization.
        // Winery OSGI export configuration is probably broken
        exporter.writeCsar(wineryRepo, entryServiceTemplate.get(), out, exportConfiguration);
      } catch (RepositoryCorruptException | InterruptedException | AccountabilityException | ExecutionException e) {
        LOGGER.warn("Exporting the csar failed with an exception", e);
        throw new IOException("Failed to export CSAR", e);
      }
    }
  }

  @Override
  public ToscaMetaFileReplacement metafileReplacement() {
    return new ToscaMetaFileReplacement(this);
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
