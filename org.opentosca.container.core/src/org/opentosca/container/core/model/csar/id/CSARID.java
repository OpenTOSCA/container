package org.opentosca.container.core.model.csar.id;

import java.io.Serializable;

/**
 * Identification of a CSAR file in OpenTOSCA.
 */
// TODO: Rename to CsarId
public class CSARID implements Comparable<CSARID>, Serializable {

    private static final long serialVersionUID = 1889149925607823116L;

    protected String fileName;

    public CSARID() {

    }

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
	return getFileName();
    }

    @Override
    public int compareTo(final CSARID csarID) {
	return toString().compareTo(csarID.toString());
    }

    @Override
    public boolean equals(final Object id) {
	if (id instanceof CSARID) {
	    return toString().equals(((CSARID) id).toString());
	}
	return false;
    }

    @Override
    public int hashCode() {
	return toString().hashCode();
    }
}
