package org.opentosca.container.core.tosca.extension;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import org.eclipse.winery.model.tosca.TBoolean;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tParameterDTO")
public class TParameterDTO {

  @XmlValue
  protected String value;
  @XmlAttribute(name = "name", required = true)
  protected String name;
  @XmlAttribute(name = "type", required = true)
  protected String type;
  @XmlAttribute(name = "required")
  protected TBoolean required;


  public TParameterDTO() {

  }

  public TParameterDTO(final org.eclipse.winery.model.tosca.TParameter param) {
    this.name = param.getName();
    this.type = param.getType();
    this.required = param.getRequired();
  }

  public TParameterDTO(final org.opentosca.container.core.tosca.extension.TParameter param) {
    this.name = param.getName();
    this.type = param.getType();
    this.required = param.getRequired();
    this.value = param.getValue();
  }

  public String getValue() {
    return this.value;
  }

  public void setValue(final String value) {
    this.value = value;
  }

  /**
   * Gets the value of the name property.
   *
   * @return possible object is {@link String }
   */
  public String getName() {
    return this.name;
  }

  /**
   * Sets the value of the name property.
   *
   * @param value allowed object is {@link String }
   */
  public void setName(final String value) {
    this.name = value;
  }

  /**
   * Gets the value of the type property.
   *
   * @return possible object is {@link String }
   */
  public String getType() {
    return this.type;
  }

  /**
   * Sets the value of the type property.
   *
   * @param value allowed object is {@link String }
   */
  public void setType(final String value) {
    this.type = value;
  }

  /**
   * Gets the value of the required property.
   *
   * @return possible object is {@link TBoolean }
   */
  public TBoolean getRequired() {
    if (this.required == null) {
      return TBoolean.YES;
    } else {
      return this.required;
    }
  }

  /**
   * Sets the value of the required property.
   *
   * @param value allowed object is {@link TBoolean }
   */
  public void setRequired(final TBoolean value) {
    this.required = value;
  }

  public static class Converter {
    public static org.eclipse.winery.model.tosca.TParameter toToscaElement(TParameterDTO dto) {
      org.eclipse.winery.model.tosca.TParameter element = new org.eclipse.winery.model.tosca.TParameter();
      element.setName(dto.name);
      element.setRequired(dto.required);
      element.setType(dto.type);
      return element;
    }

    public static org.opentosca.container.core.tosca.extension.TParameter toEntity(TParameterDTO dto) {
      org.opentosca.container.core.tosca.extension.TParameter entity = new org.opentosca.container.core.tosca.extension.TParameter();
      entity.setName(dto.name);
      entity.setRequired(dto.required);
      entity.setType(dto.type);
      entity.setValue(dto.value);
      return entity;
    }
  }

}
