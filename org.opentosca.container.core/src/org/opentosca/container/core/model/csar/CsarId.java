package org.opentosca.container.core.model.csar;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.model.csar.id.CSARID;

public class CsarId implements Comparable<CsarId> {

    // FIXME check whether we can fall back to a String instead of a file as in id.CSARID

    // FIXME move this out of CsarId, and don't make it public
    public static final Path CSAR_BASE_PATH = Paths.get(Settings.getSetting(Settings.CONTAINER_STORAGE_BASEPATH));
    
    private Path saveLocation;
    
    // TODO: should we expose this?
    public CsarId(Path directSaveLocation) {
        this.saveLocation = directSaveLocation;
    }
    
    // FOR LEGACY ADAPTERING!! GET THIS REMOVED!
    @Deprecated
    public CsarId(CSARID storeCSAR) {
        saveLocation = CSAR_BASE_PATH.resolve(storeCSAR.getFileName());
    }

    public String csarName() {
        return CSAR_BASE_PATH.relativize(saveLocation).toString();
    }
    
    // seems better, but still somewhat ugly
    public CsarId(String id) {
        this(CSAR_BASE_PATH.resolve(id));
    }

    @Deprecated
    protected void setSaveLocation(Path saveLocation) {
        this.saveLocation = saveLocation;
    }
    
    @Deprecated
    public Path getSaveLocation() {
        return saveLocation;
    }

    @Override
    public int compareTo(CsarId other) {
        return saveLocation.compareTo(other.saveLocation);
    }
    
    @Deprecated
    public CSARID toOldCsarId() {
        return new CSARID(saveLocation.getFileName().toString());
    }
}
