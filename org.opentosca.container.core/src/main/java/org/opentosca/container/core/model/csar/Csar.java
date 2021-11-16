package org.opentosca.container.core.model.csar;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.model.selfservice.Application;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TArtifactType;
import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TExportedOperation;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.model.tosca.TPolicyTemplate;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TRelationshipTypeImplementation;
import org.eclipse.winery.model.tosca.TServiceTemplate;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

public interface Csar {

    CsarId id();

    List<TArtifactTemplate> artifactTemplates();

    List<TServiceTemplate> serviceTemplates();

    List<TPolicyTemplate> policyTemplates();

    @Nullable TServiceTemplate entryServiceTemplate();

    List<TDefinitions> definitions();

    List<TExportedOperation> exportedOperations();

    List<TPlan> plans();

    List<TNodeType> nodeTypes();

    Map<QName, TNodeType> nodeTypesMap();

    List<TNodeTypeImplementation> nodeTypeImplementations();

    Map<QName, TNodeTypeImplementation> nodeTypeImplementationsMap();

    List<TRelationshipType> relationshipTypes();

    List<TRelationshipTypeImplementation> relationshipTypeImplementations();

    String description();

    Application selfserviceMetadata();

    @Nullable TExtensibleElements queryRepository(DefinitionsChildId definitionId);

    void exportTo(Path targetPath) throws IOException;

    @Deprecated
    Path getSaveLocation();

    String toString();

    Map<QName, TArtifactType> artifactTypesMap();
}
