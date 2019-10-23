package org.opentosca.container.core.model.csar;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.winery.common.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.model.selfservice.Application;
import org.eclipse.winery.model.tosca.*;
import org.opentosca.container.core.model.AbstractFile;
import org.opentosca.container.core.model.csar.backwards.ToscaMetaFileReplacement;

@NonNullByDefault
public interface Csar {

  public CsarId id();

  public List<TArtifactTemplate> artifactTemplates();

  public List<TServiceTemplate> serviceTemplates();

  public List<TPolicyTemplate> policyTemplates();

  @Nullable
  public TServiceTemplate entryServiceTemplate();

  public List<TDefinitions> definitions();

  public List<TExportedOperation> exportedOperations();

  public List<TPlan> plans();

  public List<TNodeType> nodeTypes();

  public List<TNodeTypeImplementation> nodeTypeImplementations();

  public List<TRelationshipTypeImplementation> relationshipTypeImplementations();

  public String description();

  // FIXME decide on Path / File / Binary Representation / ??
  public AbstractFile topologyPicture();

  public Application selfserviceMetadata();

  @Nullable
  public TExtensibleElements queryRepository(DefinitionsChildId definitionId);

  void exportTo(Path targetPath) throws IOException;

  @Deprecated
  ToscaMetaFileReplacement metafileReplacement();

  @Deprecated
  Path getSaveLocation();

  String toString();
}
