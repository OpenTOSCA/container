package org.opentosca.toscaengine.xmlserializer.service.impl;

import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
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

import org.opentosca.model.tosca.Definitions;
import org.opentosca.toscaengine.xmlserializer.service.IXMLSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * 
 * Serializer for marshaling and unmarshaling objects of the TOSCA universe.
 * 
 * This class is represented by the interface
 * org.opentosca.toscaengine.xmlserializer.service.IXMLSerializer.
 * 
 * Copyright 2012 IAAS University of Stuttgart <br>
 * <br>
 * 
 * TODO JAXBIntrospector does not what expected ...
 * 
 * @author Christian Endres - endrescn@studi.informatik.uni-stuttgart.de
 * 
 */
public class XMLSerializer extends FormatOutputUtil implements IXMLSerializer {

	private JAXBContext jaxbContext;
	private SchemaFactory schemaFactory;
	private Schema schema = null;
	private ValidationEventCollector validationEventCollector;
	private boolean validationActive = false;

	private Marshaller marshaller;
	// This marshaller is for internal marshalling of data which is validated
	// during the initial import process. This data is validated, therefore
	// there is no need to validate again. In the current version of JAXB
	// sometimes it is causing problems to serialize internal data with
	// validation.
	private Marshaller marshallerWithoutValidation;

	private DocumentBuilderFactory documentBuilderFactory;
	private DocumentBuilder documentBuilder;

	// logger
	private Logger LOG = LoggerFactory.getLogger(XMLSerializer.class);

	/**
	 * Constructor for XML serialization of TOSCA Definitions. Instances are
	 * created via the org.opentosca.core.xmlserializer.SerializerFactory.
	 * 
	 * @param context
	 *            The context of the JAXB classes - the package in which all
	 *            related files are.
	 * @param schemaFile
	 *            File of the Schema. If null, no validation will be
	 *            instantiated.
	 */
	public XMLSerializer(Class<?> context, File schemaFile) {

		this.LOG.debug(
				"Start the initiation of the JAXB objects for context \"" + context.getPackage().getName() + "\".");

		try {

			// setup of the Serializer
			this.jaxbContext = JAXBContext.newInstance(context.getPackage().getName());

			this.validationEventCollector = new ValidationEventCollector();

			this.marshaller.setEventHandler(this.validationEventCollector);

			this.marshallerWithoutValidation.setEventHandler(this.validationEventCollector);

			this.documentBuilderFactory.setNamespaceAware(true);

			// if the Schema object is null no validation is set
			if (schemaFile != null) {
				this.schema = this.schemaFactory.newSchema(schemaFile);

				// set the validation
				this.documentBuilderFactory.setSchema(this.schema);

			} else {
				this.LOG.info("Initialize without a Schema.");
			}

			this.documentBuilder = this.documentBuilderFactory.newDocumentBuilder();

		} catch (JAXBException e) {
			this.LOG.error(e.getMessage());
		} catch (SAXException e) {
			this.LOG.error(e.getMessage());
		} catch (ParserConfigurationException e) {
			this.LOG.error(e.getMessage());
		}

		this.LOG.debug("Initialization of the JAXB objects completed.");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Node marshalToNode(Object objToMarshal) {

		this.LOG.debug("JAXBElement " + objToMarshal.getClass().getName() + " shall be unmarshalled to a DOM Node!");

		// Check if the given object is in the same package as the JAXB Element
		// Definitions. This is done to reduce the amount of classes passing
		// this if which would cause a JAXB failure.
		if (Definitions.class.getPackage().equals(objToMarshal.getClass().getPackage())) {

			JAXBElement<?> elementToMarshal = this.createJAXBElement(objToMarshal);

			this.LOG.debug("The JAXBElement \"" + elementToMarshal.getName() + "\" seems to be a legal element.");
			try {

				Document result = this.documentBuilder.newDocument();
				this.marshallerWithoutValidation.marshal(elementToMarshal, result);

				return result.getFirstChild();

			} catch (JAXBException e) {
			} finally {
				this.printErrorsWhileSerialization();
			}
		} else {
			this.LOG.error("The Object can not be marshalled because it is not a JAXBElement of TOSCA.");
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Document marshalToDocument(Definitions definitions) {

		this.LOG.debug("Marshal the Definitions \"" + definitions.getId() + "\".");

		Document result = null;
		try {

			this.marshaller.marshal(definitions, result);

			return result;

		} catch (JAXBException e) {
		} finally {
			this.printErrorsWhileSerialization();
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String marshalToString(Object objToMarshal) {

		StringWriter writer = new StringWriter();

		// Check if the given object is in the same package as the JAXB Element
		// Definitions. This is done to reduce the amount of classes passing
		// this if which would cause a JAXB failure.
		if (Definitions.class.getPackage().equals(objToMarshal.getClass().getPackage())) {

			JAXBElement<?> elementToMarshal = this.createJAXBElement(objToMarshal);

			this.LOG.debug("The JAXBElement \"" + elementToMarshal.getName() + "\" seems to be a legal element.");
			try {

				this.marshallerWithoutValidation.marshal(elementToMarshal, writer);
				return writer.toString();

			} catch (JAXBException e) {
			} finally {
				this.printErrorsWhileSerialization();
			}
		} else {
			this.LOG.error("The Object can not be marshalled because it is not a JAXBElement of TOSCA.");
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Definitions unmarshal(File fileToUnmarshal) {

		this.LOG.debug("Start the unmarshalling of file \"" + fileToUnmarshal.toString() + "\".");
		try {
			// return the unmarshaled data
			return (Definitions) this.createUnmarshaller().unmarshal(fileToUnmarshal);

		} catch (JAXBException e) {
		} finally {
			this.printErrorsWhileSerialization();
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Definitions unmarshal(InputStream streamToUnmarshal) {

		this.LOG.debug("Start the unmarshalling of an InputStream.");
		try {
			// return the unmarshaled data
			return (Definitions) this.createUnmarshaller().unmarshal(streamToUnmarshal);

		} catch (JAXBException e) {
		} finally {
			this.printErrorsWhileSerialization();
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Definitions unmarshal(Document doc) {

		this.LOG.trace(this.docToString(doc.getFirstChild(), true));
		try {
			return (Definitions) this.createUnmarshaller().unmarshal(doc.getFirstChild());
		} catch (JAXBException e) {
		} finally {
			this.printErrorsWhileSerialization();
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object unmarshal(Node nodeToUnmarshal, Class<?> destinationClazz) {


	LOG.trace("Start the unmarshalling of the node: " + nodeToUnmarshal.toString() + " to clazz: " + destinationClazz.toString());

		try {
			Unmarshaller u = this.createUnmarshaller();
			JAXBElement<?> jaxbElement = u.unmarshal(nodeToUnmarshal, destinationClazz);
			if (jaxbElement != null) {
				return jaxbElement.getValue();
			}
		} catch (JAXBException e) {
			e.printStackTrace();
		} finally {
			this.printErrorsWhileSerialization();
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public JAXBElement createJAXBElement(Object obj) {
		// Check if the given object is in the same package as the JAXB Element
		// Definitions. This is done to reduce the amount of classes passing
		// this if which would cause a JAXB failure.
		if (Definitions.class.getPackage().equals(obj.getClass().getPackage())) {

			// get the name of the element
			String elementName = obj.getClass().getSimpleName();
			if (elementName.equals("IToscaModelFactory") || elementName.equals("ObjectFactory")
					|| elementName.equals("package-info.java")) {
				// classes inside of the model package which do not represent an
				// element of TOSCA
			} else {
				// All classes except the one for Definitions begin with an
				// leading "T" because of the typing inside of the TOSCA xsd.
				// Thus the legal name is the name without the leading "T".
				if (!elementName.equals("Definitions") && Character.isUpperCase(elementName.charAt(1))) {
					elementName = elementName.substring(1);
				}

				return new JAXBElement(new QName("http://docs.oasis-open.org/tosca/ns/2011/12", elementName),
						obj.getClass(), obj);
			}
		}
		this.LOG.error("The Object can not be marshalled because it is not a JAXBElement of TOSCA.");
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Document elementIntoDocument(Element element) {

		Document returnDoc = this.documentBuilder.newDocument();

		Node node = returnDoc.importNode(element, true);

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
	public Document elementsIntoDocument(List<Element> elements, String rootElementName) {

		Document returnDoc = this.documentBuilder.newDocument();

		Element root = returnDoc.createElement(rootElementName);
		returnDoc.appendChild(root);

		for (Element element : elements) {
			Node node = returnDoc.importNode(element, true);
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
	public void setValidation(Boolean bool) {

		/*
		 * if true give the Schema to the marshaller and unmarshaller if false
		 * delete the reference to the Schema
		 */
		this.validationActive = bool;
		if (this.validationActive == true) {
			this.marshaller.setSchema(this.schema);
		} else {
			this.marshaller.setSchema(null);
		}
	}

	/**
	 * Method for printing errors stored in the validationEventCollector. For
	 * each error the logger gets one error message.
	 */
	private void printErrorsWhileSerialization() {
		// print the errors occurred
		if ((this.validationEventCollector != null) && this.validationEventCollector.hasEvents()) {
			this.LOG.error("One or more errors occured while marshalling.");
			for (final ValidationEvent event : this.validationEventCollector.getEvents()) {
				this.LOG.error("XML processing error: {} \n at {}", event.getMessage(), event.getLocator());
			}
		}
		this.validationEventCollector.reset();
	}

	private Unmarshaller createUnmarshaller() {
		try {
			Unmarshaller u;
			u = this.jaxbContext.createUnmarshaller();
			
			if(this.validationActive){
				u.setSchema(this.schema);
			}
			u.setEventHandler(this.validationEventCollector);
			return u;
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
