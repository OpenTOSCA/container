package org.opentosca.container.core.model.csar.backwards;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Paths;

import org.eclipse.winery.model.csar.toscametafile.TOSCAMetaFile;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.opentosca.container.core.model.csar.Csar;

@SuppressWarnings("serial")
public class ToscaMetaFileReplacement extends TOSCAMetaFile {

    private final Csar wrappedCsar;

    public ToscaMetaFileReplacement(Csar wrappedCsar) {
        this.wrappedCsar = wrappedCsar;
    }

    @Override
    public String getEntryDefinitions() {
        final TServiceTemplate entryServiceTemplate = wrappedCsar.entryServiceTemplate();
        try {
            return "servicetemplates" + File.separator
                + URLEncoder.encode(entryServiceTemplate.getTargetNamespace(), "UTF-8")
                + File.separator + entryServiceTemplate.getId()
                + File.separator + "ServiceTemplate.tosca";
        } catch (UnsupportedEncodingException e) {
            // yea you deserve that one.
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getTopology() {
        return Paths.get(wrappedCsar.topologyPicture()
            .getPath())
            .relativize(wrappedCsar.getSaveLocation())
            .toString();
    }
}
