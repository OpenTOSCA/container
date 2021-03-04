package org.opentosca.container.core.engine.xml;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.eclipse.winery.model.tosca.TDefinitions;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Interface of the XML Serialization for marshaling and unmarshaling objects of the TOSCA universe to the needed JAXB
 * or DOM objects as well as other XML content to DOM objects. Instances of this Interface can be created with the
 * SerializerFactory.
 * <p>
 * An implementation of this interface provides functionality for processing XML data in files and JAXB objects. Further
 * the Serializer can convert these data to DOM and String representations.
 * <p>
 * The instance of this interface is used by <br> - org.opentosca.containerapi<br> - org.opentosca.core.model<br> -
 * org.opentosca.toscaengine.service<br>
 *
 * @author Christian Endres - endrescn@studi.informatik.uni-stuttgart.de
 */
public interface IXMLSerializer {

    /**
     * This method marshals an object of the TOSCA model into a DOM Node representation.
     *
     * @param element The JAXBElement to serialize into a DOM representation.
     * @return The DOM node representation of the element.
     */
    Node marshalToNode(Object objToMarshal);

    /**
     * This method marshals a given ServiceTemplate into a DOM Document representation.
     *
     * @param objToMarshal The root element to serialize into a DOM representation.
     * @return The DOM Document representation of the element.
     */
    Document marshalToDocument(TDefinitions definitions);

    /**
     * This method marshals a passed JAXBElement into a String.
     *
     * @param objToMarshal The root element to serialize into a String representation.
     * @return The String representation of the object.
     */
    String marshalToString(Object objToMarshal);

    /**
     * This method unmarshals a ServiceTemplate of a passed XML File object. <br>
     *
     * @param file The File object which shall be unmarshaled.
     * @return ServiceTemplate The ServiceTemplate which contains the xml data of the File. The method returns null if
     * the file is empty or one or more errors occurs.
     */
    TDefinitions unmarshal(File fileToUnmarshal);

    /**
     * This method unmarshals a ServiceTemplate of a passed InputStream. <br>
     *
     * @param streamToUnmarshal The InputStream which contains the xml data.
     * @return The ServiceTemplate which contains the xml data. The method returns null if the stream is empty or one or
     * more errors occurs.
     */
    TDefinitions unmarshal(InputStream streamToUnmarshal);

    /**
     * This method unmarshals a ServiceTemplate of a passed DOM document. <br>
     *
     * @param The DOM Document which contains the data.
     * @return ServiceTemplate The ServiceTemplate which contains the xml data. The method returns a null if the file is
     * empty or one or more errors occurs.
     */
    TDefinitions unmarshal(Document doc);

    /**
     * This method unmarshals a object of a passed DOM Node. For the mapping you need to provide the destination class
     * with which a instance is generated and the data is stored.
     *
     * @param nodeToUnmarshal  The Node which shall be unmarshalled.
     * @param destinationClazz The class which represents the Node.
     * @return An Object of the type of the second parameter and the data of the first.
     */
    Object unmarshal(Node nodeToUnmarshal, Class<?> destinationClazz);

    /**
     * This method creates a JAXBElement object which contains the given object.
     *
     * @param obj The object which is needed in form of a JAXBElement.
     * @return The JAXBElement of the given object or null if the given object is not in the context of the TOSCA JAXB
     * classes.
     */
    @SuppressWarnings("rawtypes")
    JAXBElement createJAXBElement(Object obj);

    /**
     * This method puts a given DOM Element into a proper DOM Document structure. The root element in the new document
     * is a copy of the original one.
     *
     * @param Element the element which shall be the root element of a new DOM Document.
     * @return a new DOM Document or null in case of an error.
     */
    Document elementIntoDocument(Element element);

    /**
     * This method puts a given list of DOM Elements into a proper DOM Document structure. The original elements are
     * copied and the new elements are put into a new root element with the name depending on the second parameter.
     *
     * @param elements        list of elements which provide the content of the new document
     * @param rootElementName the name of the new root element
     * @return a new DOM Document or null in case of an error.
     */
    // TODO change type of rootElementName to QName to support namespaces
    Document elementsIntoDocument(List<Element> elements, String rootElementName);

    /**
     * This method sets the validation against the schema active or inactive. By default the validation is activated.
     *
     * @param bool True for activation and false for deactivation of the validation.
     */
    void setValidation(Boolean bool);

    /**
     * Serializes a DOM Node to a String representation.
     *
     * @param node              A node containing the data which to serialize to a String representation.
     * @param removeWhitespaces Flag for removing the whitespace.
     * @return formatted String
     */
    String docToString(Node node, boolean removeWhitespaces);
}
