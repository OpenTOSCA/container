package org.opentosca.container.core.model.csar;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.model.csar.id.CSARID;

public class CsarId implements Comparable<CsarId> {

    // FIXME check whether we can fall back to a String instead of a file as in id.CSARID

    private static final Path CSAR_BASE_PATH = Paths.get(Settings.getSetting(Settings.CONTAINER_STORAGE_BASEPATH));
    
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

    
    protected void setSaveLocation(Path saveLocation) {
        this.saveLocation = saveLocation;
    }
    
    public Path getSaveLocation() {
        return saveLocation;
    }

    @Override
    public int compareTo(CsarId other) {
        return saveLocation.compareTo(other.saveLocation);
    }
    
    @Deprecated
    public CSARID toOldCsarId() {
        return new CSARID(saveLocation.toString());
    }
}
