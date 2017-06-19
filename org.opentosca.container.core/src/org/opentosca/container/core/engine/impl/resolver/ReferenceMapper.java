package org.opentosca.container.core.engine.impl.resolver;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.opentosca.container.core.engine.impl.ServiceHandler;
import org.opentosca.container.core.engine.impl.ToscaEngineServiceImpl;
import org.opentosca.container.core.engine.impl.resolver.data.ElementNamesEnum;
import org.opentosca.container.core.engine.impl.resolver.data.ReferenceResultWrapper;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.tosca.model.TBoundaryDefinitions.Properties.PropertyMappings;
import org.opentosca.container.core.tosca.model.TCapability;
import org.opentosca.container.core.tosca.model.TExportedInterface;
import org.opentosca.container.core.tosca.model.TNodeTemplate;
import org.opentosca.container.core.tosca.model.TRequirement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The ReferenceMapper provides functionality for searching of specific elements
 * inside of the imported documents of TOSCA and mapping the found data to the
 * reference.
 * 
 * TODO This class can be refactored to reduce amount of code.
 * 
 * @author Christian Endres - endrescn@studi.informatik.uni-stuttgart.de
 * 
 */
public class ReferenceMapper {
	
	
	private XPath xpath = XPathFactory.newInstance().newXPath();
	private CSARID csarID = null;
	private Map<String, List<Document>> mapOfNSToDocuments = null;
	
	private Logger LOG = LoggerFactory.getLogger(ReferenceMapper.class);
	
	/**
	 * This defines some default namespaces in which elements can be nested.
	 */
	NamespaceContext nsContext = new NamespaceContext() {
		
		
		@Override
		public String getNamespaceURI(String prefix) {
			String uri;
			if (prefix.equals("wsdl")) {
				uri = "http://schemas.xmlsoap.org/wsdl/";
			} else if (prefix.equals("xs")) {
				uri = "http://www.w3.org/2001/XMLSchema";
			} else if (prefix.equals("tosca")) {
				uri = "http://docs.oasis-open.org/tosca/ns/2011/12";
			} else {
				uri = null;
			}
			return uri;
		}
		
		// Dummy implementation
		// Suppress warnings because of this method is auto generated and not
		// used.
		@SuppressWarnings("rawtypes")
		@Override
		public Iterator getPrefixes(String val) {
			return null;
		}
		
		// Dummy implemenation - not used!
		@Override
		public String getPrefix(String uri) {
			return null;
		}
	};
	
	
	/**
	 * Initialize a new ReferenceMapper.
	 * 
	 * @param csarID The identification of the CSAR.
	 * @param mapOfNSToDocuments the data structure containing the DOM Documents
	 *            in which an instance of this ReferenceMapper searches.
	 */
	public ReferenceMapper(CSARID csarID, Map<String, List<Document>> mapOfNSToDocuments) {
		this.csarID = csarID;
		this.mapOfNSToDocuments = mapOfNSToDocuments;
		xpath.setNamespaceContext(nsContext);
	}
	
	/**
	 * This method stores a object into the ToscaReferenceMapper for future use.
	 * 
	 * @param csarID the ID of the CSAR containing the object.
	 * @param reference the reference which shall describe the object.
	 * @param objectToStore the object to store.
	 */
	public void storeJAXBObjectIntoToscaReferenceMapper(QName reference, Object objectToStore) {
		Node node = ServiceHandler.xmlSerializerService.getXmlSerializer().marshalToNode(objectToStore);
		ToscaEngineServiceImpl.toscaReferenceMapper.storeReference(csarID, reference, node);
	}
	
	/**
	 * This method stores a certain Node which was requested by the
	 * ReferenceResolver. The Node is attached to the passed THOR ID and the
	 * passed QName which references the Node in a TOSCA document of the
	 * referenced THOR.
	 * 
	 * @param csarID The ID of the current THOR environment.
	 * @param nodeReference Reference of the Node to store.
	 * @param nodeToStore Node to store.
	 */
	private void storeNodeIntoReferenceMapper(QName nodeReference, Node nodeToStore) {
		ToscaEngineServiceImpl.toscaReferenceMapper.storeReference(csarID, nodeReference, nodeToStore);
	}
	
	/**
	 * This method stores a certain Document which nests a requested Node by the
	 * ReferenceResolver. The Document is attached to the passed THOR ID and the
	 * passed QName of the requested Node which references the Node in a TOSCA
	 * document of the referenced THOR.
	 * 
	 * @param csarID The ID of the current THOR environment.
	 * @param nodeReference Reference of the Node which is nested by the passed
	 *            Document.
	 * @param documentToStore Document to store.
	 */
	protected void storeDocumentIntoReferenceMapper(QName nodeReference, Document documentToStore) {
		ToscaEngineServiceImpl.toscaReferenceMapper.storeDocument(csarID, nodeReference, documentToStore);
	}
	
	/**
	 * Stores an exported interface of a service template into the
	 * ToscaReferenceMapper.
	 * 
	 * @param csarID CSARID of the owning CSAR
	 * @param serviceTemplateID ServiceTemplateID for whicht the interface is
	 *            exported
	 * @param iface the exported interface JAXB object
	 */
	protected void storeExportedInterface(CSARID csarID, QName serviceTemplateID, TExportedInterface iface) {
		ToscaEngineServiceImpl.toscaReferenceMapper.storeExportedInterface(csarID, serviceTemplateID, iface);
	}
	
	/**
	 * Searches for elements inside of DOM documents which have an ID (note that
	 * an attribute called id is not an xsd:ID necessarily).
	 * 
	 * @param elementReference The QName which references the element.
	 * @param documentType The Type of document in which shall be searched.
	 * @return the ReferenceResultWrapper in case of success, otherwise null
	 */
	private ReferenceResultWrapper searchElementWithID(QName elementReference, String documentType) {
		
		LOG.debug("Search for an ID for the QName \"" + elementReference.toString() + "\".");
		
		// if there are no documents
		if (mapOfNSToDocuments.isEmpty()) {
			LOG.error("There are no known documents.");
			return null;
		}
		
		// if there is no document list defined for the namespace of the element
		// reference there cannot be searched
		if (!mapOfNSToDocuments.containsKey(elementReference.getNamespaceURI())) {
			LOG.warn("The namespace \"" + elementReference.getNamespaceURI() + "\" was not found inside the data structure.");
			return null;
		}
		
		// xpath expression which selects by ID
		String exprString = "//*[@id=\"" + elementReference.getLocalPart() + "\"]";
		
		// search inside of the documents of the passed reference namespace
		for (Document doc : mapOfNSToDocuments.get(elementReference.getNamespaceURI())) {
			
			// checks if the document type is the passed one
			if (doc.getFirstChild().getLocalName().equals(documentType)) {
				
				// search and wrap the result
				try {
					XPathExpression expr = xpath.compile(exprString);
					Object result = expr.evaluate(doc, XPathConstants.NODESET);
					NodeList list = (NodeList) result;
					if (list.getLength() > 0) {
						ReferenceResultWrapper wrapper = new ReferenceResultWrapper();
						wrapper.setDoc(doc);
						wrapper.setNodeList(list);
						return wrapper;
					}
					
				} catch (XPathExpressionException e) {
					e.printStackTrace();
					LOG.error("An error occured while searching inside the document via xpath. The message is: " + e.getMessage());
				}
			}
		}
		
		LOG.warn("The ID \"" + elementReference + "\" was not found.");
		
		return null;
	}
	
	/**
	 * Searches for elements inside of DOM documents which have an ID (note that
	 * an attribute called id is not an xsd:ID necessarily).
	 * 
	 * @param reference The QName which references the element.
	 * @return the ReferenceResultWrapper in case of success, otherwise null
	 */
	private ReferenceResultWrapper searchElementWithIDWithoutNamespacePresort(QName reference) {
		
		LOG.debug("Search somewhere else.");
		
		// if there are no documents
		if (mapOfNSToDocuments.isEmpty()) {
			LOG.error("There are no known documents.");
			return null;
		}
		
		// xpath expression which selects by ID
		String exprString = "//*[@id=\"" + reference.getLocalPart() + "\"]";
		
		// search inside of all known documents
		for (String key : mapOfNSToDocuments.keySet()) {
			for (Document doc : mapOfNSToDocuments.get(key)) {
				
				// search and wrap the result
				try {
					
					XPathExpression expr = xpath.compile(exprString);
					Object result = expr.evaluate(doc, XPathConstants.NODESET);
					NodeList list = (NodeList) result;
					if (list.getLength() > 0) {
						ReferenceResultWrapper wrapper = new ReferenceResultWrapper();
						wrapper.setDoc(doc);
						wrapper.setNodeList(list);
						return wrapper;
					}
					
				} catch (XPathExpressionException e) {
					e.printStackTrace();
					LOG.error("An error occured while searching inside the document via xpath. The message is: " + e.getMessage());
				}
			}
		}
		
		LOG.error("The ID \"" + reference + "\" was not found.");
		
		return null;
	}
	
	/**
	 * Searches for elements inside of DOM documents with a certain name.
	 * 
	 * @param elementReference The QName which references the element. The local
	 *            part of this shall be the value of the attribute name.
	 * @param elementName The local name of the requested element. If the
	 *            parameter is null, this method searches for all elements.
	 * @param documentType The Type of document in which shall be searched.
	 * @return the ReferenceResultWrapper in case of success, otherwise null
	 */
	private ReferenceResultWrapper searchElementWithName(QName elementReference, String elementName, String documentType) {
		
		// if there are no documents
		if (mapOfNSToDocuments.isEmpty()) {
			LOG.error("There are no known documents.");
			return null;
		}
		
		// if there is no document list defined for the namespace of the element
		// reference there cannot be searched
		if (!mapOfNSToDocuments.containsKey(elementReference.getNamespaceURI())) {
			LOG.warn("The namespace \"" + elementReference.getNamespaceURI() + "\" was not found inside the data structure.");
			return null;
		}
		
		// no name passed, thus search for all via wildcard
		if (elementName == null) {
			elementName = "*";
		}
		
		LOG.debug("Search for a name for the QName \"" + elementReference.toString() + "\" inside of an element \"" + elementName + "\".");
		
		// xpath expression which selects by element name and the attribute name
		// of the element
		String exprString = "//" + elementName + "[@name=\"" + elementReference.getLocalPart() + "\"]";
		
		// search inside of the documents of the passed reference namespace
		for (Document doc : mapOfNSToDocuments.get(elementReference.getNamespaceURI())) {
			
			// checks if the document type is the passed one
			if (doc.getFirstChild().getLocalName().equals(documentType)) {
				
				try {
					
					XPathExpression expr = xpath.compile(exprString);
					Object result = expr.evaluate(doc, XPathConstants.NODESET);
					NodeList list = (NodeList) result;
					if (list.getLength() > 0) {
						ReferenceResultWrapper wrapper = new ReferenceResultWrapper();
						wrapper.setDoc(doc);
						wrapper.setNodeList(list);
						return wrapper;
					}
					
				} catch (XPathExpressionException e) {
					e.printStackTrace();
					LOG.error("An error occured while searching inside the document via xpath. The message is: " + e.getMessage());
				}
			}
		}
		
		LOG.warn("The element with the name \"" + elementReference + "\" was not found.");
		
		return null;
	}
	
	/**
	 * Searches for elements inside of DOM documents with a certain name. This
	 * method searches inside all known documents for a certain THOR.
	 * 
	 * @param reference The QName which references the element.
	 * @param elementName The name of the requested element. If the parameter is
	 *            null, this method searches for all kind of elements.
	 * @return the ReferenceResultWrapper in case of success, otherwise null
	 */
	private ReferenceResultWrapper searchElementWithNameWithoutNamespacePresort(QName reference, String elementName) {
		
		LOG.debug("Search somewhere else.");
		
		// if there are no documents
		if (mapOfNSToDocuments.isEmpty()) {
			LOG.error("There are no known documents.");
			return null;
		}
		
		// no name passed, thus search for all via wildcard
		if (elementName == null) {
			elementName = "*";
		}
		
		// xpath expression which selects by element name and the attribute name
		// of the element
		// String exprString = "//" + elementName + "[@name=\"" +
		// reference.getLocalPart() + "\" and @targetNamespace='" +
		// reference.getNamespaceURI() + "']";
		String exprString = "//" + elementName + "[@name=\"" + reference.getLocalPart() + "\"]";
		LOG.debug(exprString);
		
		// search inside of all known documents
		for (String key : mapOfNSToDocuments.keySet()) {
			for (Document doc : mapOfNSToDocuments.get(key)) {
				
				// search and wrap the result
				try {
					
					XPathExpression expr = xpath.compile(exprString);
					Object result = expr.evaluate(doc, XPathConstants.NODESET);
					NodeList list = (NodeList) result;
					if (list.getLength() > 0) {
						ReferenceResultWrapper wrapper = new ReferenceResultWrapper();
						wrapper.setDoc(doc);
						wrapper.setNodeList(list);
						return wrapper;
					}
					
				} catch (XPathExpressionException e) {
					e.printStackTrace();
					LOG.error("An error occured while searching inside the document via xpath. The message is: " + e.getMessage());
				}
			}
		}
		
		LOG.error("The element with the name \"" + reference + "\" was not found.");
		
		return null;
	}
	
	/**
	 * Searches for an element inside the ServiceTemplate via IDRef and stores
	 * it. Possible are only TNodeTemplates.
	 * 
	 * @param targetElement IDREF as Object
	 * @return true means no error, false one or more errors
	 */
	protected boolean searchElementViaIDREF(Object targetElement, String targetNamespace) {
		
		LOG.debug("Resolve an IDREF.");
		
		if (!((targetElement instanceof TNodeTemplate) || (targetElement instanceof TRequirement) || (targetElement instanceof TCapability))) {
			LOG.error("The referenced element is of the Type \"" + targetElement.getClass().getCanonicalName() + "\". It has to be one of NodeTemplate, Requirement or Capability.");
			return false;
		}
		
		Node node = null;
		String id = null;
		
		node = ServiceHandler.xmlSerializerService.getXmlSerializer().marshalToNode(targetElement);
		
		// if not null, store it and nesting Document
		if (node != null) {
			
			id = node.getAttributes().getNamedItem("id").getTextContent();
			storeNodeIntoReferenceMapper(new QName(targetNamespace, id), node);
			return true;
			
		} else {
			LOG.error("There occured an error while marshalling \"" + id + "\" to a Node.");
		}
		
		return false;
	}
	
	/**
	 * Search an element of TOSCA. If the element is available, store it and the
	 * containing document inside the referenceMapper. The QName parameter has
	 * to contain the ID of the demanded element as local part. Due the new
	 * version of TOSCA there can be references to the attribute name instead of
	 * ID. In this case please use the method searchToscaElementByQNameWithName.
	 * 
	 * TODO check the namespace the requested element has to be inside
	 * 
	 * @param reference the QName of the demanded element
	 * @return true means no error, false one or more errors
	 */
	protected boolean searchToscaElementByQNameWithID(QName reference) {
		
		// reference is null, therefore there is no reference to resolve
		if ((reference == null) || reference.toString().equals("")) {
			return true;
		}
		
		// search
		LOG.debug("Search for a element inside of a Definitions with the QName \"" + reference + "\".");
		ReferenceResultWrapper wrapper = searchElementWithID(reference, "Definitions");
		
		// search again everywhere
		if (wrapper == null) {
			wrapper = searchElementWithIDWithoutNamespacePresort(reference);
			if (wrapper == null) {
				
				// not found, thus error
				LOG.error("The requested Element was not found!");
				return false;
			} else {
				LOG.info("Luckily found the requested Element!");
			}
		}
		
		// found something
		if (wrapper.getNodeList().getLength() == 1) {
			
			// found exactly the requested
			storeNodeIntoReferenceMapper(reference, wrapper.getNodeList().item(0));
			LOG.info("The element " + reference + " was found.");
			return true;
		} else if (wrapper.getNodeList().getLength() == 0) {
			
			// found nothing, but should be catched by the both conditions
			// wrapper == null
			LOG.debug("The element was not found.");
		} else {
			
			// found too much
			LOG.error("There are " + wrapper.getNodeList().getLength() + " elements with the requested QName found inside this document. The following Nodes are found:");
			NodeList foundElements = wrapper.getNodeList();
			for (int itr = 0; itr < foundElements.getLength(); itr++) {
				LOG.debug(ServiceHandler.xmlSerializerService.getXmlSerializer().docToString(foundElements.item(itr), true));
			}
		}
		
		return false;
	}
	
	/**
	 * Search an element of TOSCA. If the element is available, store it and the
	 * containing document inside the referenceMapper. The QName parameter has
	 * to contain the ID of the demanded element as local part. Due the new
	 * version of TOSCA there can be references to the attribute name instead of
	 * ID. In this case please use the method searchToscaElementByQNameWithName.
	 * 
	 * TODO check the namespace the requested element has to be inside
	 * 
	 * @param reference the QName of the demanded element
	 * @return true means no error, false one or more errors
	 */
	protected boolean searchToscaElementByQNameWithName(QName reference, ElementNamesEnum element) {
		
		// reference is null, therefore there is no reference to resolve
		if ((reference == null) || reference.toString().equals("")) {
			return true;
		}
		
		// search
		LOG.debug("Search for a " + element + " inside of a Definitions with the name \"" + reference.getLocalPart() + "\" inside the namespace \"" + reference.getNamespaceURI() + "\".");
		ReferenceResultWrapper wrapper = searchElementWithName(reference, element.toString(), "Definitions");
		
		// search again everywhere
		if (wrapper == null) {
			wrapper = searchElementWithNameWithoutNamespacePresort(reference, element.toString());
			if (wrapper == null) {
				
				// not found, thus error
				LOG.error("The requested Element was not found!");
				return false;
			} else {
				LOG.info("Luckily found the requested Element!");
			}
		}
		
		// found something
		if (wrapper.getNodeList().getLength() == 1) {
			
			// found exactly the requested
			storeNodeIntoReferenceMapper(reference, wrapper.getNodeList().item(0));
			LOG.info("The element " + reference + " was found.");
			return true;
		} else
			
			// found nothing, but should be catched by the both conditions
			// wrapper == null
			if (wrapper.getNodeList().getLength() == 0) {
				LOG.debug("The element was not found.");
			} else {
				
				// found too much
				LOG.error("There are " + wrapper.getNodeList().getLength() + " elements with the requested QName found inside this document. The following Nodes are found:");
				NodeList foundElements = wrapper.getNodeList();
				for (int itr = 0; itr < foundElements.getLength(); itr++) {
					LOG.debug(ServiceHandler.xmlSerializerService.getXmlSerializer().docToString(foundElements.item(itr), true));
				}
			}
		
		return false;
	}
	
	/**
	 * Search an element inside a XML Schema. If the element is available, store
	 * it and the containing document inside the referenceMapper.
	 * 
	 * @param reference the QName of the demanded element
	 * @return true means no error, false one or more errors
	 */
	protected boolean searchXMLElement(QName element) {
		
		// search
		LOG.debug("Search for a element with the QName \"" + element + "\".");
		ReferenceResultWrapper wrapper = searchElementWithName(element, null, "schema");
		
		// search again everywhere
		if (wrapper == null) {
			wrapper = searchElementWithNameWithoutNamespacePresort(element, "xs:element");
			if (wrapper == null) {
				
				// not found, thus error
				LOG.error("The requested Element was not found!");
				return false;
			} else {
				LOG.info("Luckily found the requested Element!");
			}
		}
		
		// found something
		if (wrapper.getNodeList().getLength() == 1) {
			
			// found exactly the requested
			storeNodeIntoReferenceMapper(element, wrapper.getNodeList().item(0));
			storeDocumentIntoReferenceMapper(element, wrapper.getDoc());
			return true;
		} else if (wrapper.getNodeList().getLength() == 0) {
			
			// found nothing, but should be catched by the both conditions
			// wrapper == null
			LOG.debug("The element was not found.");
		} else {
			
			// found too much
			LOG.error("There are " + wrapper.getNodeList().getLength() + " elements with the requested QName found inside this document. The following Nodes are found:");
			NodeList foundElements = wrapper.getNodeList();
			for (int itr = 0; itr < foundElements.getLength(); itr++) {
				LOG.debug(ServiceHandler.xmlSerializerService.getXmlSerializer().docToString(foundElements.item(itr), true));
			}
			
		}
		
		return false;
	}
	
	/**
	 * Search type inside a XML Schema. If the element is available, store it
	 * and the containing document inside the referenceMapper.
	 * 
	 * @param reference the QName of the demanded element
	 * @return true means no error, false one or more errors
	 */
	protected boolean searchXMLType(QName type) {
		
		// search
		LOG.debug("Search for a ComplexType with the QName \"" + type + "\".");
		ReferenceResultWrapper wrapper = searchElementWithName(type, "xs:complexType", "schema");
		
		// search again everywhere
		if (wrapper == null) {
			wrapper = searchElementWithNameWithoutNamespacePresort(type, "xs:complexType");
			if (wrapper == null) {
				
				// not found, thus error
				LOG.error("The requested Element was not found!");
				return false;
			} else {
				LOG.info("Luckily found the requested Element!");
			}
		}
		
		// found something
		if (wrapper.getNodeList().getLength() == 1) {
			
			// found exactly the requested
			storeNodeIntoReferenceMapper(type, wrapper.getNodeList().item(0));
			storeDocumentIntoReferenceMapper(type, wrapper.getDoc());
			return true;
		} else if (wrapper.getNodeList().getLength() == 0) {
			
			// found nothing, but should be catched by the both conditions
			// wrapper == null
			LOG.debug("The complex type was not found.");
		} else {
			
			// found too much
			LOG.error("There are " + wrapper.getNodeList().getLength() + " complex types with the requested QName found inside this document. The following Nodes are found:");
			NodeList foundElements = wrapper.getNodeList();
			for (int itr = 0; itr < foundElements.getLength(); itr++) {
				LOG.debug(ServiceHandler.xmlSerializerService.getXmlSerializer().docToString(foundElements.item(itr), true));
			}
			
		}
		
		return false;
	}
	
	/**
	 * Search a definition for a REST body inside of a XML Schema. If the
	 * element is available, store it and the containing document inside the
	 * referenceMapper.
	 * 
	 * @param reference the QName of the demanded element
	 * @return true means no error, false one or more errors
	 */
	protected boolean searchRESTBody(QName body) {
		
		// TODO dont know how to search, thus search for all
		LOG.debug("Search for a REST body with the QName \"" + body + "\".");
		ReferenceResultWrapper wrapper = searchElementWithName(body, null, "schema");
		
		// search again everywhere
		if (wrapper == null) {
			wrapper = searchElementWithNameWithoutNamespacePresort(body, ElementNamesEnum.ALLELEMENTS.toString());
			if (wrapper == null) {
				
				// not found, thus error
				LOG.error("The requested Element was not found!");
				return false;
			} else {
				LOG.info("Luckily found the requested Element!");
			}
		}
		
		// found something
		if (wrapper.getNodeList().getLength() == 1) {
			
			// found exactly the requested
			storeNodeIntoReferenceMapper(body, wrapper.getNodeList().item(0));
			storeDocumentIntoReferenceMapper(body, wrapper.getDoc());
			return true;
		} else if (wrapper.getNodeList().getLength() == 0) {
			
			// found nothing, but should be catched by the both conditions
			// wrapper == null
			LOG.debug("The element was not found.");
		} else {
			
			// found too much
			LOG.error("There are " + wrapper.getNodeList().getLength() + " elements with the requested QName found inside this document. The following Nodes are found:");
			NodeList foundElements = wrapper.getNodeList();
			for (int itr = 0; itr < foundElements.getLength(); itr++) {
				LOG.debug(ServiceHandler.xmlSerializerService.getXmlSerializer().docToString(foundElements.item(itr), true));
			}
			
		}
		
		return false;
	}
	
	public void storeRelationshipTemplateIDForServiceTemplateAndCSAR(CSARID csarID, QName serviceTemplateId, String id){
		ToscaEngineServiceImpl.toscaReferenceMapper.storeRelationshipTemplateIDForServiceTemplateAndCSAR(csarID, serviceTemplateId, id);
	}
	
	public void storeNodeTemplateIDForServiceTemplateAndCSAR(CSARID csarID, QName serviceTemplateID, String id) {
		ToscaEngineServiceImpl.toscaReferenceMapper.storeNodeTemplateIDForServiceTemplateAndCSAR(csarID, serviceTemplateID, id);
	}
	
	public void storeServiceTemplateBoundsProperties(CSARID csarID, QName serviceTemplateID, String propertiesContent, PropertyMappings propertyMappings) {
		ToscaEngineServiceImpl.toscaReferenceMapper.storeServiceTemplateBoundsPropertiesInformation(csarID, serviceTemplateID, propertiesContent, propertyMappings);
	}
}

