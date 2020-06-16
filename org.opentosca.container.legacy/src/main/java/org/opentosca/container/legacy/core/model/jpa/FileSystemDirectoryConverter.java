package org.opentosca.container.legacy.core.model.jpa;

import org.opentosca.container.core.model.csar.backwards.FileSystemDirectory;

import javax.persistence.AttributeConverter;
import java.nio.file.Paths;

@javax.persistence.Converter
public class FileSystemDirectoryConverter implements AttributeConverter<FileSystemDirectory, String> {
  @Override
  public String convertToDatabaseColumn(FileSystemDirectory fileSystemDirectory) {
    return fileSystemDirectory == null ? null : fileSystemDirectory.getPath();
  }

  @Override
  public FileSystemDirectory convertToEntityAttribute(String s) {
    return s == null ? null : new FileSystemDirectory(Paths.get(s));
  }
}
