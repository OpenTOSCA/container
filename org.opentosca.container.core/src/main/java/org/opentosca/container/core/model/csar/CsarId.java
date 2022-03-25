package org.opentosca.container.core.model.csar;

public class CsarId implements Comparable<CsarId> {

    private final String name;
    private String language;

    public CsarId(String name) {
        this.name = name;
        this.language = "XML";
    }

    public String csarName() {
        return name;
    }

    public String csarLanguage() { return language; }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public int compareTo(CsarId other) {
        return name.compareTo(other.name);
    }

    @Override
    public String toString() {
        return csarName();
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof CsarId &&
            name.equals(((CsarId) o).name);
    }
}
