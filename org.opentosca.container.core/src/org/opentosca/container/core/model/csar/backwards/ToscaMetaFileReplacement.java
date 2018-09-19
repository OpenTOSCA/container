package org.opentosca.container.core.model.csar.backwards;

import java.nio.file.Paths;

import org.eclipse.winery.model.csar.toscametafile.TOSCAMetaFile;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.opentosca.container.core.model.csar.Csar;

public class ToscaMetaFileReplacement extends TOSCAMetaFile {

    private final Csar wrappedCsar;
    
    public ToscaMetaFileReplacement(Csar wrappedCsar) {
        this.wrappedCsar = wrappedCsar;
    }

    @Override
    public String getEntryDefinitions() {
        final TServiceTemplate entryServiceTemplate = wrappedCsar.entryServiceTemplate();
        return "servicetemplates/" 
            + entryServiceTemplate.getTargetNamespace() 
            + "/" + entryServiceTemplate.getId();
    }

    @Override
    public String getTopology() {
        return Paths.get(wrappedCsar.topologyPicture()
            .getPath())
            .relativize(wrappedCsar.id().getSaveLocation())
            .toString();
    }
}
