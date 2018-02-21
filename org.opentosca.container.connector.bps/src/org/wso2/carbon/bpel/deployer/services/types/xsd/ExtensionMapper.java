
/**
 * ExtensionMapper.java
 *
 * This file was auto-generated from WSDL by the Apache Axis2 version: SNAPSHOT Built on : Nov 10,
 * 2010 (06:34:21 UTC)
 */


package org.wso2.carbon.bpel.deployer.services.types.xsd;

/**
 * ExtensionMapper class
 */

public class ExtensionMapper {

    public static java.lang.Object getTypeObject(final java.lang.String namespaceURI, final java.lang.String typeName,
                    final javax.xml.stream.XMLStreamReader reader)
        throws java.lang.Exception {


        if ("http://types.services.deployer.bpel.carbon.wso2.org/xsd".equals(namespaceURI)
            && "UploadedFileItem".equals(typeName)) {

            return org.wso2.carbon.bpel.deployer.services.types.xsd.UploadedFileItem.Factory.parse(reader);


        }


        throw new org.apache.axis2.databinding.ADBException("Unsupported type " + namespaceURI + " " + typeName);
    }

}
