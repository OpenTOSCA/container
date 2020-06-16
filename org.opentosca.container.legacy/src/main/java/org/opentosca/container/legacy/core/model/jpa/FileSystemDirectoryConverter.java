package org.opentosca.container.legacy.core.model.jpa;

import java.nio.file.Paths;

import javax.persistence.AttributeConverter;

import org.opentosca.container.core.model.csar.backwards.FileSystemDirectory;

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
