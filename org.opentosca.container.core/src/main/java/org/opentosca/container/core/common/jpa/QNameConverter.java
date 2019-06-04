package org.opentosca.container.core.common.jpa;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import javax.xml.namespace.QName;

/**
 * This class is used to convert QNames to String, and Strings back to QNames when persisting QName
 * fields with JPA. The conversion needs to be done, as we cannot directly query for QNames in JPQL.
 */
@Converter
public class QNameConverter implements AttributeConverter<QName, String> {

  public static final String name = "QNameConverter";

  private static final long serialVersionUID = 5695923859083900495L;

  @Override
  public String convertToDatabaseColumn(QName qName) {
    return qName == null ? null : qName.toString();
  }

  @Override
  public QName convertToEntityAttribute(String s) {
    return s == null ? null : QName.valueOf(s);
  }
}
