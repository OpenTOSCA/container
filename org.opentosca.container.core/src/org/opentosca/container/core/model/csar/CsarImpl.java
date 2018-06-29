package org.opentosca.container.core.model.csar;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang3.NotImplementedException;
import org.eclipse.winery.common.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.common.ids.definitions.NodeTypeId;
import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.csar.toscametafile.TOSCAMetaFile;
import org.eclipse.winery.model.csar.toscametafile.TOSCAMetaFileParser;
import org.eclipse.winery.model.selfservice.Application;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CsarImpl implements Csar {

    private static final Logger LOGGER = LoggerFactory.getLogger(CsarImpl.class);
    
    private static final String RELATIVE_TOSCA_META_FILE = "TOSCA-Metadata/TOSCA.meta";

    private final TOSCAMetaFileParser parser = new TOSCAMetaFileParser();
    private final CsarId id;
    private IRepository wineryRepo;
    
    @Deprecated
    public CsarImpl(CsarId id) {
        this.id = id;
        wineryRepo = RepositoryFactory.getRepository(id.getSaveLocation());
    }
    
    public CsarImpl(CsarId id, Path location) {
        this.id = id;
        wineryRepo = RepositoryFactory.getRepository(location);
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
    public Application selfserviceMetadata() {
        try (final InputStream is = Files.newInputStream(id.getSaveLocation().resolve("SELFSERVICE-Metadata").resolve("data.xml"), StandardOpenOption.READ)) {
            final JAXBContext jaxbContext = JAXBContext.newInstance(Application.class);
            final Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            return (Application) jaxbUnmarshaller.unmarshal(is);
        } catch (final IOException e) {
            LOGGER.error("Could not serialize data.xml from CSAR", e);
            throw new UncheckedIOException(e);
        } catch (JAXBException e) {
            LOGGER.error("Could not parse data.xml from CSAR", e);
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public List<TNodeType> nodeTypes() {
        return wineryRepo.getAllDefinitionsChildIds(NodeTypeId.class).stream()
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
