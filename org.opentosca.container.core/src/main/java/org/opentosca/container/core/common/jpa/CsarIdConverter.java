package org.opentosca.container.core.common.jpa;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.sessions.Session;
import org.opentosca.container.core.model.csar.CsarId;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class CsarIdConverter implements AttributeConverter<CsarId, String>, org.eclipse.persistence.mappings.converters.Converter {

  public static final String name = "CsarIdConverter";
  private static final long serialVersionUID = -2552365749611257786L;

  @Override
  public String convertToDatabaseColumn(CsarId csarId) {
    return csarId == null ? null : csarId.csarName();
  }

  @Override
  public CsarId convertToEntityAttribute(String s) {
    return s == null ? null : new CsarId(s);
  }

  @Override
  public Object convertObjectValueToDataValue(Object objectValue, Session session) {
    // input is the CsarId from the Entity
    return objectValue == null ? null : ((CsarId) objectValue).csarName();
  }

  @Override
  public Object convertDataValueToObjectValue(Object dataValue, Session session) {
    // input is the value from the database
    return dataValue == null ? null : new CsarId((String) dataValue);
  }

  @Override
  public boolean isMutable() {
    return false;
  }

  @Override
  public void initialize(DatabaseMapping mapping, Session session) {
  }
}
