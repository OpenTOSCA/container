package org.opentosca.container.core.engine.xml.impl;

import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.util.ValidationEventCollector;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.repository.JAXBSupport;

import org.opentosca.container.core.engine.xml.IXMLSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Serializer for marshaling and unmarshaling objects of the TOSCA universe.
 * <p>
 * This class is represented by the interface org.opentosca.toscaengine.xmlserializer.service.IXMLSerializer.
 * <p>
 * Copyright 2012-2022 IAAS University of Stuttgart <br>
 * <br>
 * <p>
 * TODO JAXBIntrospector does not what expected ...
 *
 * @author Christian Endres - endrescn@studi.informatik.uni-stuttgart.de
 */
public class XMLSerializer extends FormatOutputUtil implements IXMLSerializer {
    private static final Logger LOG = LoggerFactory.getLogger(XMLSerializer.class);

    private SchemaFactory schemaFactory;
    private Schema schema = null;
    private ValidationEventCollector validationEventCollector;

    private Marshaller marshaller;
    // This marshaller is for internal marshalling of data which is validated
    // during the initial import process. This data is validated, therefore
    // there is no need to validate again. In the current version of JAXB
    // sometimes it is causing problems to serialize internal data with
    // validation.
    private Marshaller marshallerWithoutValidation;


    private DocumentBuilder documentBuilder;

    /**
     * Constructor for XML serialization of TOSCA Definitions.
     *
     * @param context    The context of the JAXB classes - the package in which all related files are.
     * @param schemaFile File of the Schema. If null, no validation will be instantiated.
     */
    public XMLSerializer(final Class<?> context, final URL schemaFile) {

        LOG.debug("Start the initiation of the JAXB objects for context \"" + context.getPackage().getName()
            + "\".");

        try {

            this.validationEventCollector = new ValidationEventCollector();

            this.marshaller = JAXBSupport.createMarshaller(true);
            this.marshaller.setEventHandler(this.validationEventCollector);

            this.marshallerWithoutValidation = JAXBSupport.createMarshaller(false);
            this.marshallerWithoutValidation.setEventHandler(this.validationEventCollector);

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);

            // if the Schema object is null no validation is set
            if (schemaFile != null) {
                LOG.info("There is a given Schema at \"" + schemaFile + "\".");
                this.schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                this.schema = this.schemaFactory.newSchema(schemaFile);

                // set the validation
                LOG.debug("Activate validation for serialization to JAXB classes.");
                this.setValidation(true);
                documentBuilderFactory.setSchema(this.schema);
            } else {
                LOG.info("Initialize without a Schema.");
            }

            this.documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (final JAXBException e) {
            LOG.error(e.getMessage());
        } catch (final SAXException e) {
            LOG.error(e.getMessage());
        } catch (final ParserConfigurationException e) {
            LOG.error(e.getMessage());
        }

        LOG.debug("Initialization of the JAXB objects completed.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Node marshalToNode(final Object objToMarshal) {

        LOG.debug("JAXBElement " + objToMarshal.getClass().getName() + " shall be unmarshalled to a DOM Node!");

        // Check if the given object is in the same package as the JAXB Element
        // Definitions. This is done to reduce the amount of classes passing
        // this if which would cause a JAXB failure.
        if (TDefinitions.class.getPackage().equals(objToMarshal.getClass().getPackage())) {

            final JAXBElement<?> elementToMarshal = this.createJAXBElement(objToMarshal);

            LOG.debug("The JAXBElement \"" + elementToMarshal.getName() + "\" seems to be a legal element.");
            try {

                final Document result = this.documentBuilder.newDocument();
                this.marshallerWithoutValidation.marshal(elementToMarshal, result);

                return result.getFirstChild();
            } catch (final JAXBException e) {
            } finally {
                this.printErrorsWhileSerialization();
            }
        } else {
            LOG.error("The Object can not be marshalled because it is not a JAXBElement of TOSCA.");
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Document marshalToDocument(final TDefinitions definitions) {

        LOG.debug("Marshal the Definitions \"" + definitions.getId() + "\".");

        Document result = null;
        try {

            result = this.documentBuilder.newDocument();
            this.marshaller.marshal(definitions, result);

            return result;
        } catch (final JAXBException e) {
        } finally {
            this.printErrorsWhileSerialization();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String marshalToString(final Object objToMarshal) {

        final StringWriter writer = new StringWriter();

        // Check if the given object is in the same package as the JAXB Element
        // Definitions. This is done to reduce the amount of classes passing
        // this if which would cause a JAXB failure.
        if (TDefinitions.class.getPackage().equals(objToMarshal.getClass().getPackage())) {

            final JAXBElement<?> elementToMarshal = this.createJAXBElement(objToMarshal);

            LOG.debug("The JAXBElement \"" + elementToMarshal.getName() + "\" seems to be a legal element.");
            try {

                this.marshallerWithoutValidation.marshal(elementToMarshal, writer);
                return writer.toString();
            } catch (final JAXBException e) {
            } finally {
                this.printErrorsWhileSerialization();
            }
        } else {
            LOG.error("The Object can not be marshalled because it is not a JAXBElement of TOSCA.");
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TDefinitions unmarshal(final File fileToUnmarshal) {

        LOG.debug("Start the unmarshalling of file \"" + fileToUnmarshal.toString() + "\".");
        try {
            // return the unmarshaled data
            return (TDefinitions) JAXBSupport.createUnmarshaller().unmarshal(fileToUnmarshal);
        } catch (final JAXBException e) {
        } finally {
            this.printErrorsWhileSerialization();
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TDefinitions unmarshal(final InputStream streamToUnmarshal) {

        LOG.debug("Start the unmarshalling of an InputStream.");
        try {
            // return the unmarshaled data
            return (TDefinitions) JAXBSupport.createUnmarshaller().unmarshal(streamToUnmarshal);
        } catch (final JAXBException e) {
        } finally {
            this.printErrorsWhileSerialization();
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TDefinitions unmarshal(final Document doc) {

        LOG.debug("Start the unmarshalling of a DOM Document.");
        LOG.trace(this.docToString(doc.getFirstChild(), true));
        try {
            return (TDefinitions) JAXBSupport.createUnmarshaller().unmarshal(doc.getFirstChild());
        } catch (final JAXBException e) {
        } finally {
            this.printErrorsWhileSerialization();
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object unmarshal(final Node nodeToUnmarshal, final Class<?> destinationClazz) {

        LOG.trace("Start the unmarshalling of the node: " + nodeToUnmarshal.toString() + " to clazz: "
            + destinationClazz.toString());

        try {
            final Unmarshaller u = JAXBSupport.createUnmarshaller();
            final JAXBElement<?> jaxbElement = u.unmarshal(nodeToUnmarshal, destinationClazz);
            if (jaxbElement != null) {
                return jaxbElement.getValue();
            }
        } catch (final JAXBException e) {
            e.printStackTrace();
        } finally {
            this.printErrorsWhileSerialization();
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( {"rawtypes", "unchecked"})
    @Override
    public JAXBElement createJAXBElement(final Object obj) {
        // Check if the given object is in the same package as the JAXB Element
        // Definitions. This is done to reduce the amount of classes passing
        // this if which would cause a JAXB failure.
        if (TDefinitions.class.getPackage().equals(obj.getClass().getPackage())) {

            // get the name of the element
            String elementName = obj.getClass().getSimpleName();
            if (!(elementName.equals("IToscaModelFactory") || elementName.equals("ObjectFactory")
                || elementName.equals("package-info.java"))) {
                // classes inside of the model package which do not represent an
                // element of TOSCA
                if (!elementName.equals("Definitions") && Character.isUpperCase(elementName.charAt(1))) {
                    elementName = elementName.substring(1);
                }

                return new JAXBElement(new QName("http://docs.oasis-open.org/tosca/ns/2011/12", elementName),
                    obj.getClass(), obj);
            }
        }
        LOG.error("The Object can not be marshalled because it is not a JAXBElement of TOSCA.");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Document elementIntoDocument(final Element element) {

        final Document returnDoc = this.documentBuilder.newDocument();

        final Node node = returnDoc.importNode(element, true);

        if (node == null) {
            // return null for easier checking of an error.
            // if the return is not null, an empty but valid document without
            // content would be returned.
            return null;
        }

        returnDoc.appendChild(node);

        return returnDoc;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Document elementsIntoDocument(final List<Element> elements, final String rootElementName) {

        final Document returnDoc = this.documentBuilder.newDocument();

        final Element root = returnDoc.createElement(rootElementName);
        returnDoc.appendChild(root);

        for (final Element element : elements) {
            final Node node = returnDoc.importNode(element, true);
            if (node == null) {
                // return null for easier checking of an error.
                // if the return is not null, an empty or incomplete but valid
                // document
                // without
                // content would be returned.
                return null;
            }
            root.appendChild(node);
        }

        return returnDoc;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValidation(final Boolean bool) {

        /*
         * if true give the Schema to the marshaller and unmarshaller if false delete the reference to the
         * Schema
         */
        if (bool) {
            this.marshaller.setSchema(this.schema);
        } else {
            this.marshaller.setSchema(null);
        }
    }

    /**
     * Method for printing errors stored in the validationEventCollector. For each error the logger gets one error
     * message.
     */
    private void printErrorsWhileSerialization() {
        // print the errors occurred
        if (this.validationEventCollector != null && this.validationEventCollector.hasEvents()) {
            LOG.error("One or more errors occured while marshalling.");
            for (final ValidationEvent event : this.validationEventCollector.getEvents()) {
                LOG.error("XML processing error: {} \n at {}", event.getMessage(), event.getLocator());
            }
        }
        this.validationEventCollector.reset();
    }
}
