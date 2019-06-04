package org.opentosca.container.core.common.jpa;

import org.opentosca.container.core.model.csar.CsarId;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class CsarIdConverter implements AttributeConverter<CsarId, String> {

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
}
