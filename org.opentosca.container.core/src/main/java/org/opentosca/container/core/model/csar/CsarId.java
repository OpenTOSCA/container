package org.opentosca.container.core.model.csar;

import java.nio.file.Path;

import org.opentosca.container.core.model.csar.id.CSARID;

public class CsarId implements Comparable<CsarId> {

  private String name;

  // TODO: should we expose this?
  public CsarId(String name) {
    this.name = name;
  }

  // FOR LEGACY ADAPTERING!! GET THIS REMOVED!
  @Deprecated
  public CsarId(CSARID storeCSAR) {
    name = storeCSAR.getFileName();
  }

  @Deprecated
  public CsarId(Path backwards) {
    this(backwards.getFileName().toString());
  }

  public String csarName() {
    return name;
  }

  @Override
  public int compareTo(CsarId other) {
    return name.compareTo(other.name);
  }

  @Deprecated
  public CSARID toOldCsarId() {
    return new CSARID(name);
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
