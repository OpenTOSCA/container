package org.opentosca.container.core.common.jpa;

import java.net.URI;
import java.net.URISyntaxException;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * This class is used to convert URIs to String, and Strings back to URIs when persisting URI fields
 * with JPA. The conversion needs to be done, as we cannot directly query for URI in JPQL.
 */
@Converter
public class UriConverter implements AttributeConverter<URI, String> {

  public static final String name = "URIConverter";
  private static final long serialVersionUID = 5695923859083900495L;

  @Override
  public String convertToDatabaseColumn(URI uri) {
    return uri == null ? null : uri.toString();
  }

  @Override
  public URI convertToEntityAttribute(String s) {
    try {
      return s == null ? null : new URI(s);
    } catch (URISyntaxException e) {
      return null;
    }
  }
}
