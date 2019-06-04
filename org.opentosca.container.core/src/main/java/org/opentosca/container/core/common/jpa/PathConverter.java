package org.opentosca.container.core.common.jpa;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.persistence.AttributeConverter;

/**
 * This class is used to convert {@link Path} to String, and {@link String} back to {@link Path}
 * when persisting {@link Path} fields with JPA. The conversion needs to be done, as we cannot
 * directly query for {@link Path} in JPQL.
 */
@javax.persistence.Converter
public class PathConverter implements AttributeConverter<Path, String> {

  public static final String name = "PathConverter";

  private static final long serialVersionUID = 3747978557147488965L;

  @Override
  public String convertToDatabaseColumn(Path path) {
    return path == null ? null : path.toString();
  }

  @Override
  public Path convertToEntityAttribute(String s) {
    return s == null ? null : Paths.get(s);
  }
}
