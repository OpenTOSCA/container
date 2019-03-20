package org.opentosca.container.core.common.jpa;

import javax.persistence.AttributeConverter;
import javax.xml.namespace.QName;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.mappings.converters.Converter;

/**
 * This class is used to convert QNames to String, and Strings back to QNames when persisting QName
 * fields with JPA. The conversion needs to be done, as we cannot directly query for QNames in JPQL.
 */
@javax.persistence.Converter
public class QNameConverter implements Converter, AttributeConverter<QName, String> {

  public static final String name = "QNameConverter";

  private static final long serialVersionUID = 5695923859083900495L;

  @Override
  public Object convertDataValueToObjectValue(final Object arg0, final Session arg1) {
    return arg0 != null ? QName.valueOf((String) arg0) : null;
  }

  @Override
  public Object convertObjectValueToDataValue(final Object arg0, final Session arg1) {
    return arg0 != null ? ((QName) arg0).toString() : null;
  }

  @Override
  public void initialize(final DatabaseMapping arg0, final Session arg1) {
  }

  @Override
  public boolean isMutable() {
    return false;
  }

  @Override
  public String convertToDatabaseColumn(QName qName) {
    return qName == null ? null : qName.toString();
  }

  @Override
  public QName convertToEntityAttribute(String s) {
    return s == null ? null : QName.valueOf(s);
  }
}
