package org.opentosca.container.core.model.csar.id;

import java.io.Serializable;

/**
 * Identification of a CSAR file in OpenTOSCA.
 */
// FIXME: check whether Serializability is cargo-cult programming
// TODO: 983 references work with this. We shouldn't break it!
@Deprecated
public class CSARID implements Comparable<CSARID>, Serializable {

    private static final long serialVersionUID = 1889149925607823116L;

    protected String fileName;

    protected CSARID() {}

    public CSARID(final String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return this.getFileName();
    }

    @Override
    public int compareTo(final CSARID csarID) {
        return this.toString().compareTo(csarID.toString());
    }

    @Override
    public boolean equals(final Object id) {
        if (id instanceof CSARID) {
            return this.toString().equals(((CSARID) id).toString());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
}
