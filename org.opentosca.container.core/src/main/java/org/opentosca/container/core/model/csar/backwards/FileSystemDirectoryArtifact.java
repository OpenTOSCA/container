package org.opentosca.container.core.model.csar.backwards;

import org.opentosca.container.core.model.AbstractArtifact;
import org.opentosca.container.core.model.AbstractDirectory;

import java.util.Collections;

@Deprecated
public class FileSystemDirectoryArtifact extends AbstractArtifact {

  private final FileSystemDirectory artifactRoot;

  public FileSystemDirectoryArtifact(final FileSystemDirectory artifactRoot) {
    super(artifactRoot.getPath(), Collections.emptySet(), Collections.emptySet());
    this.artifactRoot = artifactRoot;
  }

  @Override
  protected AbstractDirectory getArtifactRoot() {
    return artifactRoot;
  }

  @Override
  public boolean isFileArtifact() {
    return false;
  }
}
