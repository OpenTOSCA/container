package org.opentosca.container.core.engine.impl.resolver.data;

/**
 * This enum maps URIs of specifications to an enum value. The URIs are representing types of
 * imported resources due a ServiceTemplate.
 *
 * @author Christian Endres - endrescn@studi.informatik.uni-stuttgart.de
 */
public enum ImportType {
  TOSCA, WSDL, SCHEMA;

  /**
   * @param type Type of an import element inside of a ServiceTemplate.
   * @return
   */
  public static ImportType getImportType(final String type) {

    if (type.equals("http://docs.oasis-open.org/tosca/ns/2011/12")) {
      return TOSCA;
    } else if (type.equals("http://schemas.xmlsoap.org/wsdl/")) {
      return WSDL;
    } else if (type.equals("http://www.w3.org/2001/XMLSchema")) {
      return SCHEMA;
    }

    return null;
  }
}
