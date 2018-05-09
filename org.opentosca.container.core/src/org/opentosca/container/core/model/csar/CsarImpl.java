package org.opentosca.container.core.model.csar;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.NotImplementedException;
import org.eclipse.winery.common.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.common.ids.definitions.NodeTypeId;
import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.csar.toscametafile.TOSCAMetaFile;
import org.eclipse.winery.model.csar.toscametafile.TOSCAMetaFileParser;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TExportedOperation;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.opentosca.container.core.model.AbstractFile;
import org.opentosca.container.core.model.csar.backwards.FileSystemFile;

public class CsarImpl implements Csar {

    private static final String RELATIVE_TOSCA_META_FILE = "";

    private final TOSCAMetaFileParser parser = new TOSCAMetaFileParser();
    
    private final CsarId id;
    
    private IRepository wineryRepo;
    
    public CsarImpl(CsarId id) {
        this.id = id;
        wineryRepo = RepositoryFactory.getRepository(id.getSaveLocation());
    }
    
    @Override
    public CsarId id() {
        return id;
    }

    private <T> Stream<T> childIdsOfType(Class<T> clazz) {
        return wineryRepo.getAllDefinitionsChildIds().stream()
            .filter(clazz::isInstance)
            .map(clazz::cast);
    }
    
    @Override
    public List<TArtifactTemplate> artifactTemplates() {
        return childIdsOfType(ArtifactTemplateId.class)
            .map(wineryRepo::getElement)
            .collect(Collectors.toList());
    }

    @Override
    public List<TServiceTemplate> serviceTemplates() {
        return  childIdsOfType(ServiceTemplateId.class)
            .map(wineryRepo::getElement)
            .collect(Collectors.toList());
    }

    @Override
    public TServiceTemplate entryServiceTemplate() {
        // FIXME check assumptions here!
        return serviceTemplates().get(0);
    }

    @Override
    public List<TDefinitions> definitions() {
        return wineryRepo.getAllDefinitionsChildIds().stream()
            .map(wineryRepo::getDefinitions)
            .collect(Collectors.toList());
    }

    @Override
    public List<TExportedOperation> exportedOperations() {
        // FIXME
        throw new NotImplementedException("not yet implemented");
    }

    @Override
    public List<TPlan> plans() {
        // FIXME 
        throw new NotImplementedException("not yet implemented");
    }

    @Override
    public List<TNodeType> nodeTypes() {
        return childIdsOfType(NodeTypeId.class)
        .map(wineryRepo::getElement)
        .collect(Collectors.toList());
    }

    @Override
    public TOSCAMetaFile toscaMetadata() {
        return parser.parse(id.getSaveLocation().resolve(RELATIVE_TOSCA_META_FILE));
    }

    @Override
    public AbstractFile getRootTosca() {
        String relativePath = toscaMetadata().getEntryDefinitions();
        return new FileSystemFile(id.getSaveLocation().resolve(relativePath));
    }

    @Override
    public String description() {
        return toscaMetadata().getDescription();
    }

    @Override
    public AbstractFile topologyPicture() {
        String relPicturePath = toscaMetadata().getTopology();
        return new FileSystemFile(id.getSaveLocation().resolve(relPicturePath));
    }

}
