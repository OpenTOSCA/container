package org.opentosca.container.core.engine.xml;

import org.opentosca.container.core.tosca.model.IToscaModelFactory;

/**
 * This interface describes the XMLSerializerService which provides the IXMLSerializer which
 * serializes data of the TOSCA universe and related data like WSDL or XML Schema. Furthermore the
 * service provides access to the ObjectFactory of JAXB objects of TOSCA.
 *
 * @author Christian Endres - endrescn@studi.informatik.uni-stuttgart.de
 */
public interface IXMLSerializerService {

    /**
     * Getter for the XMLSerializer which handles data of TOSCA or is used by TOSCA.
     *
     * @return The xmlSerializer for data which is TOSCA or is used by TOSCA.
     */
    public abstract IXMLSerializer getXmlSerializer();

    /**
     * Getter for the ToscaModelFactory which is a ObjectFactory for JAXB objects of TOSCA.
     *
     * @return The ObjectFactory which creates JAXB objects of TOSCA.
     */
    public abstract IToscaModelFactory getToscaModelFactory();

}
