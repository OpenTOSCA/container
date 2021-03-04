package org.opentosca.container.core.model.csar;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.eclipse.winery.model.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.model.selfservice.Application;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TExportedOperation;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.model.tosca.TPolicyTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTypeImplementation;
import org.eclipse.winery.model.tosca.TServiceTemplate;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

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

    public Application selfserviceMetadata();

    @Nullable
    public TExtensibleElements queryRepository(DefinitionsChildId definitionId);

    void exportTo(Path targetPath) throws IOException;

    @Deprecated
    Path getSaveLocation();

    String toString();
}
