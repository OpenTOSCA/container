package org.opentosca.planbuilder.handlers;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.model.plan.BuildPlan;
import org.opentosca.planbuilder.model.plan.GenericWsdlWrapper;
import org.opentosca.planbuilder.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * <p>
 * This class is a part of the facade, which is used to work on a BuildPlan.
 * This is class in particular is responsible for the handling of various XML
 * related actions on the whole BPEL process
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kálmán Képes - kepeskn@studi.informatik.uni-stuttgart.de
 * 
 */
public class BPELProcessHandler {
	
	private final static Logger LOG = LoggerFactory.getLogger(BPELProcessHandler.class);
	
	private DocumentBuilderFactory documentBuilderFactory;
	private DocumentBuilder documentBuilder;
	
	
	/**
	 * Default Constructor
	 * 
	 * @throws ParserConfigurationException is thrown when the interal DOM
	 *             Builders couldn't be initialized
	 */
	public BPELProcessHandler() throws ParserConfigurationException {
		this.documentBuilderFactory = DocumentBuilderFactory.newInstance();
		this.documentBuilderFactory.setNamespaceAware(true);
		this.documentBuilder = this.documentBuilderFactory.newDocumentBuilder();
	}
	
	/**
	 * Adds a namespace declaration into the given BuildPlan
	 * 
	 * @param prefix the prefix to use for the namespace
	 * @param namespace the namespace
	 * @param buildPlan the BuildPlan to set the namespace to
	 * @return true if the namespace isn't alread used, else false
	 */
	public boolean addNamespaceToBPELDoc(String prefix, String namespace, BuildPlan buildPlan) {
		BPELProcessHandler.LOG.debug("Adding namespace {} to BuildPlan {}", namespace, buildPlan.getBpelProcessElement().getAttribute("name"));
		buildPlan.getBpelProcessElement().setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + prefix, namespace);
		// TODO make a real check
		return true;
	}
	
	/**
	 * Sets the given element with the given attribute and value
	 * 
	 * @param element the Element to set
	 * @param attrName the attribute name to set
	 * @param attrValue the value for the attribute
	 */
	public void setAttribute(Element element, String attrName, String attrValue) {
		BPELProcessHandler.LOG.debug("Setting attribute {} with value {} on Element {}", attrName, attrValue, element.getLocalName());
		// TODO check why this method is here
		element.setAttribute(attrName, attrValue);
	}
	
	/**
	 * Initializes the XML DOM elements inside the given BuildPlan
	 * 
	 * @param newBuildPlan a new BuildPlan
	 */
	public void initializeXMLElements(BuildPlan newBuildPlan) {
		newBuildPlan.setBpelDocument(this.documentBuilder.newDocument());
		
		// initialize processElement and append to document
		newBuildPlan.setBpelProcessElement(newBuildPlan.getBpelDocument().createElementNS(BuildPlan.bpelNamespace, "process"));
		newBuildPlan.getBpelDocument().appendChild(newBuildPlan.getBpelProcessElement());
		
		// FIXME declare xml schema namespace
		newBuildPlan.getBpelProcessElement().setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
		
		// init import files list
		newBuildPlan.setImportedFiles(new ArrayList<File>());
		
		// initialize and append extensions element to process
		newBuildPlan.setBpelExtensionsElement(newBuildPlan.getBpelDocument().createElementNS(BuildPlan.bpelNamespace, "extensions"));
		newBuildPlan.getBpelProcessElement().appendChild(newBuildPlan.getBpelExtensionsElement());
		
		// init and append imports element
		newBuildPlan.setBpelImportElements(new ArrayList<Element>());
		
		// TODO this is here to not to forget that the imports elements aren't
		// attached, cause there are none and import elements aren't nested in a
		// list element
		//
		// this.bpelImportsElement = this.bpelProcessDocument.createElementNS(
		// BuildPlan.bpelNamespace, "imports");
		// this.bpelProcessElement.appendChild(bpelImportsElement);
		
		// init and append partnerlink element
		newBuildPlan.setBpelPartnerLinksElement(newBuildPlan.getBpelDocument().createElementNS(BuildPlan.bpelNamespace, "partnerLinks"));
		newBuildPlan.getBpelProcessElement().appendChild(newBuildPlan.getBpelPartnerLinksElement());
		
		// initialize and append variables element
		newBuildPlan.setBpelProcessVariablesElement(newBuildPlan.getBpelDocument().createElementNS(BuildPlan.bpelNamespace, "variables"));
		newBuildPlan.getBpelProcessElement().appendChild(newBuildPlan.getBpelProcessVariablesElement());
		
		// init and append main sequence to process element
		newBuildPlan.setBpelMainSequenceElement(newBuildPlan.getBpelDocument().createElementNS(BuildPlan.bpelNamespace, "sequence"));
		newBuildPlan.getBpelProcessElement().appendChild(newBuildPlan.getBpelMainSequenceElement());
		
		// init and append main sequence receive element to main sequence
		// element
		newBuildPlan.setBpelMainSequenceReceiveElement(newBuildPlan.getBpelDocument().createElementNS(BuildPlan.bpelNamespace, "receive"));
		newBuildPlan.getBpelMainSequenceElement().appendChild(newBuildPlan.getBpelMainSequenceReceiveElement());
		
		// init and append main sequence property assign element to main
		// sequence element
		newBuildPlan.setBpelMainSequencePropertyAssignElement(newBuildPlan.getBpelDocument().createElementNS(BuildPlan.bpelNamespace, "assign"));
		newBuildPlan.getBpelMainSequenceElement().appendChild(newBuildPlan.getBpelMainSequencePropertyAssignElement());
		
		// init and append main sequence flow element to main sequence element
		newBuildPlan.setBpelMainFlowElement(newBuildPlan.getBpelDocument().createElementNS(BuildPlan.bpelNamespace, "flow"));
		newBuildPlan.getBpelMainSequenceElement().appendChild(newBuildPlan.getBpelMainFlowElement());
		
		// init and append flow links element
		newBuildPlan.setBpelMainFlowLinksElement(newBuildPlan.getBpelDocument().createElementNS(BuildPlan.bpelNamespace, "links"));
		newBuildPlan.getBpelMainFlowElement().appendChild(newBuildPlan.getBpelMainFlowLinksElement());
		
		// init and append output assign element
		newBuildPlan.setBpelMainSequenceOutputAssignElement(newBuildPlan.getBpelDocument().createElementNS(BuildPlan.bpelNamespace, "assign"));
		newBuildPlan.getBpelMainSequenceElement().appendChild(newBuildPlan.getBpelMainSequenceOutputAssignElement());
		
		// init and append main sequence callback invoke element to main
		// sequence element
		newBuildPlan.setBpelMainSequenceCallbackInvokeElement(newBuildPlan.getBpelDocument().createElementNS(BuildPlan.bpelNamespace, "invoke"));
		newBuildPlan.getBpelMainSequenceElement().appendChild(newBuildPlan.getBpelMainSequenceCallbackInvokeElement());
	}
	
	/**
	 * Sets the name and namespace of the given buildPlan
	 * 
	 * @param namespace the namespace to set
	 * @param name the name to set
	 * @param buildPlan the buildPlan to change
	 */
	public void setId(String namespace, String name, BuildPlan buildPlan) {
		BPELProcessHandler.LOG.debug("Setting name {} with namespace {} BuidlPlan", name, namespace);
		// change the bpel document
		buildPlan.getBpelProcessElement().setAttribute("name", name);
		buildPlan.getBpelProcessElement().setAttribute("targetNamespace", namespace);
		// FIXME should work if we ask the internet, but we will see later
		buildPlan.getBpelProcessElement().setAttribute("xmlns:tns", namespace);
	}
	
	/**
	 * Sets ID's on the WSDL of the given BuildPlan
	 * 
	 * @param namespace the namespace for the WSDL
	 * @param name the name for the WSDL
	 * @param buildPlan the BuildPlan to work on
	 */
	public void setWsdlId(String namespace, String name, BuildPlan buildPlan) {
		BPELProcessHandler.LOG.debug("Setting name {} and namespace {} of WSDL of BuildPlan {}", name, namespace, buildPlan.getBpelProcessElement().getAttribute("name"));
		GenericWsdlWrapper wsdl = buildPlan.getWsdl();
		wsdl.setId(namespace, name);
	}
	
	/**
	 * Adds an ImportElement to the given BuildPlan
	 * 
	 * @param namespace the namespace of the Import
	 * @param location the location of the document to import
	 * @param importType the type of the import
	 * @param buildPlan the BuildPlan to add the Import to
	 * @return true if adding the ImportElement was successful, else false
	 */
	public boolean addImports(String namespace, String location, BuildPlan.ImportType importType, BuildPlan buildPlan) {
		BPELProcessHandler.LOG.debug("Trying to add Import with namespace {}, location {} and importType {} to BuildPlan {}", namespace, location, importType, buildPlan.getBpelProcessElement().getAttribute("name"));
		
		if (this.hasImport(namespace, location, importType, buildPlan)) {
			BPELProcessHandler.LOG.warn("Failed adding Import");
			return false;
		}
		
		// create new import element
		Element importElement = buildPlan.getBpelDocument().createElementNS(BuildPlan.bpelNamespace, "import");
		importElement.setAttribute("namespace", namespace);
		importElement.setAttribute("location", location);
		
		importElement.setAttribute("importType", importType.toString());
		
		// add to process
		if (buildPlan.getBpelImportElements().isEmpty()) {
			buildPlan.getBpelProcessElement().appendChild(importElement);
		} else {
			buildPlan.getBpelProcessElement().insertBefore(importElement, buildPlan.getBpelImportElements().get(0));
		}
		buildPlan.addBpelImportElement(importElement);
		
		BPELProcessHandler.LOG.debug("Adding import was successful");
		return true;
	}
	
	/**
	 * Checks whether an ImportElement is already add
	 * 
	 * @param namespace the namespace of the import
	 * @param location the location of the import
	 * @param type the type of the import
	 * @param buildPlan the BuildPlan to check on
	 * @return true if the BuildPlan already has the specified import, else
	 *         false
	 */
	public boolean hasImport(String namespace, String location, BuildPlan.ImportType type, BuildPlan buildPlan) {
		BPELProcessHandler.LOG.debug("Checking if import with namespace " + namespace + " and location " + location + " is already imported");
		for (Element importElement : buildPlan.getBpelImportElements()) {
			BPELProcessHandler.LOG.debug("Checking import element");
			int checkInt = 0;
			if (importElement.hasAttribute("namespace") && importElement.getAttribute("namespace").equals(namespace)) {
				BPELProcessHandler.LOG.debug("Found import with same namespace");
				checkInt++;
			}
			if (importElement.hasAttribute("location") && importElement.getAttribute("location").equals(location)) {
				BPELProcessHandler.LOG.debug("Found import with same location");
				checkInt++;
			}
			if (checkInt == 2) {
				return true;
			}
			if (importElement.hasAttribute("type") && importElement.getAttribute("type").equals(type.toString())) {
				BPELProcessHandler.LOG.debug("Found import with same type");
				checkInt++;
			}
			if (checkInt == 3) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Adds a Partnerlink to the given BuildPlan
	 * 
	 * @param partnerLinkName the name to use for the PartnerLink
	 * @param partnerLinkType the type of the PartnerLink (must be already set)
	 * @param myRole the role of the process inside this partnerLink
	 * @param partnerRole the role of the called entity inside this partnerLink
	 * @param initializePartnerRole whether to set initializePartnerRole to
	 *            'yes' or 'no'
	 * @param buildPlan the BuildPlan to add the partnerLink to
	 * @return true if adding the PartnerLink was successful, else false
	 */
	public boolean addPartnerLink(String partnerLinkName, QName partnerLinkType, String myRole, String partnerRole, boolean initializePartnerRole, BuildPlan buildPlan) {
		BPELProcessHandler.LOG.debug("Trying to add partnerLink {} with type {}, myRole {}, partnerRole {} and initializePartnerRole {} to BuildPlan {}", partnerLinkName, partnerLinkType.toString(), myRole, partnerRole, String.valueOf(initializePartnerRole), buildPlan.getBpelProcessElement().getAttribute("name"));
		if (this.hasPartnerLink(partnerLinkName, buildPlan)) {
			BPELProcessHandler.LOG.warn("Failed to add partnerLink");
			return false;
		} else {
			Element partnerLinksElement = buildPlan.getBpelPartnerLinksElement();
			Element partnerLinkElement = buildPlan.getBpelDocument().createElementNS(BuildPlan.bpelNamespace, "partnerLink");
			partnerLinkElement.setAttribute("name", partnerLinkName);
			partnerLinkElement.setAttribute("partnerLinkType", partnerLinkType.getPrefix() + ":" + partnerLinkType.getLocalPart());
			partnerLinkElement.setAttribute("myRole", myRole);
			if ((partnerRole != null) && !partnerRole.equals("")) {
				partnerLinkElement.setAttribute("partnerRole", partnerRole);
			}
			
			partnerLinkElement.setAttribute("initializePartnerRole", (initializePartnerRole) ? "yes" : "no");
			
			partnerLinksElement.appendChild(partnerLinkElement);
		}
		BPELProcessHandler.LOG.debug("Adding partnerLink was successful");
		return true;
	}
	
	/**
	 * Adds a partnerLinkType which only has one portType (e.g. syncronous)
	 * 
	 * @param partnerLinkTypeName the name for the partnerLinkType
	 * @param roleName the roleName of the Process
	 * @param portType the PortType used in the PartnerLink
	 * @param buildPlan the BuildPlan to add the PartnerLinkType to
	 * @return true if adding partnerLink was successful, else false
	 */
	public boolean addPartnerLinkType(String partnerLinkTypeName, String roleName, QName portType, BuildPlan buildPlan) {
		BPELProcessHandler.LOG.debug("Trying to add partnerLinkType {} with roleName {} and portType {} to BuildPlan {}", partnerLinkTypeName, roleName, portType.toString(), buildPlan.getBpelProcessElement().getAttribute("name"));
		return buildPlan.getWsdl().addPartnerLinkType(partnerLinkTypeName, roleName, portType);
	}
	
	/**
	 * Adds a partnerLinkType which has to PortType (e.g. asynchronous callback)
	 * 
	 * @param partnerLinkTypeName the name for the partnerLinkType
	 * @param roleName1 the name for the first role
	 * @param portType1 the portType of the first role
	 * @param roleName2 the name for the second role
	 * @param portType2 the portType for second role
	 * @param buildPlan the BuildPlan to add the partnerLinkType to
	 * @return true if adding the partnerLinkType was successful, else false
	 */
	public boolean addPartnerLinkType(String partnerLinkTypeName, String roleName1, QName portType1, String roleName2, QName portType2, BuildPlan buildPlan) {
		BPELProcessHandler.LOG.debug("Trying to add partnerLinkType {} with roleName1 {}, portType1 {}, roleName2 {} and portType2 {} to BuildPlan {}", partnerLinkTypeName, roleName1, portType1.toString(), roleName2, portType2.toString(), buildPlan.getBpelProcessElement().getAttribute("name"));
		return buildPlan.getWsdl().addPartnerLinkType(partnerLinkTypeName, roleName1, portType1, roleName2, portType2);
	}
	
	/**
	 * Adds the given variable to the process of the given buildplan
	 * 
	 * @param name the name of the variable
	 * @param variableType the type of the variable, e.g. MessageType
	 * @param declarationId the QName of the schema declaration, e.g.
	 *            {ownSchema}Local. Note: Prefix must be set.
	 * @param buildPlan the buildPlan to add the variable to
	 * @return
	 */
	public boolean addVariable(String name, BuildPlan.VariableType variableType, QName declarationId, BuildPlan buildPlan) {
		BPELProcessHandler.LOG.debug("Trying to add variable {} with type {} and declarationId {} to BuildPlan {}", name, variableType, declarationId.toString(), buildPlan.getBpelProcessElement().getAttribute("name"));
		if (this.hasVariable(name, buildPlan)) {
			BPELProcessHandler.LOG.warn("Adding variable failed");
			return false;
		}
		
		// fetch variables element and create variable element
		Element variablesElement = buildPlan.getBpelProcessVariablesElement();
		Element variableElement = buildPlan.getBpelDocument().createElementNS(BuildPlan.bpelNamespace, "variable");
		
		// set the type and declaration id
		switch (variableType) {
		case MESSAGE:
			variableElement.setAttribute("messageType", declarationId.getPrefix() + ":" + declarationId.getLocalPart());
			break;
		case TYPE:
			variableElement.setAttribute("type", declarationId.getPrefix() + ":" + declarationId.getLocalPart());
			break;
		default:
			;
			break;
		}
		
		// set name
		variableElement.setAttribute("name", name);
		
		// append to variables element
		variablesElement.appendChild(variableElement);
		BPELProcessHandler.LOG.debug("Adding variable was successful");
		return true;
	}
	
	/**
	 * Checks whether the given BuildPlan has a variable with the given name
	 * 
	 * @param name the name of the variable
	 * @param buildPlan the BuildPlan to check in
	 * @return true if the given BuildPlan has a variable with the given name,
	 *         else false
	 */
	private boolean hasVariable(String name, BuildPlan buildPlan) {
		return Utils.hasChildElementWithAttribute(buildPlan.getBpelProcessVariablesElement(), "name", name);
	}
	
	/**
	 * Checks whether the given BuildPlan has partnerLink with the given name
	 * 
	 * @param name the name of the partnerLink
	 * @param buildPlan the BuildPlan to check in
	 * @return true if the BuildPlan has partnerLink with the given name
	 */
	private boolean hasPartnerLink(String name, BuildPlan buildPlan) {
		return Utils.hasChildElementWithAttribute(buildPlan.getBpelPartnerLinksElement(), "name", name);
	}
	
	/**
	 * Adds a link with the given name to the given BuildPlan. Note that links
	 * can be added to BPEL flow's without using it in the elements of the flow.
	 * 
	 * @param linkName the name of the link to set
	 * @param buildPlan the BuildPlan to add the link
	 * @return true if adding the link was successful, else false
	 */
	public boolean addLink(String linkName, BuildPlan buildPlan) {
		BPELProcessHandler.LOG.debug("Trying to add link {} to BuildPlan {}", linkName, buildPlan.getBpelProcessElement().getAttribute("name"));
		
		if (this.hasLink(linkName, buildPlan)) {
			BPELProcessHandler.LOG.warn("Adding link failed");
			return false;
		}
		
		Element linksElement = buildPlan.getBpelMainFlowLinksElement();
		Element linkElement = buildPlan.getBpelDocument().createElementNS(BuildPlan.bpelNamespace, "link");
		
		linkElement.setAttribute("name", linkName);
		linksElement.appendChild(linkElement);
		BPELProcessHandler.LOG.debug("Adding link was successful");
		return true;
	}
	
	/**
	 * Checks whether the given BuildPlan has a link with the given name
	 * 
	 * @param linkName the name of the link to check with
	 * @param buildPlan the BuildPlan to check for the link
	 * @return true if the BuildPlan has a link with the given name, else false
	 */
	private boolean hasLink(String linkName, BuildPlan buildPlan) {
		return Utils.hasChildElementWithAttribute(buildPlan.getBpelMainFlowLinksElement(), "name", linkName);
	}
	
	/**
	 * Adds an Extension Element to the given BuildPlan
	 * 
	 * @param namespace the namespace of the extension
	 * @param mustUnderstand sets if the extensions must be understood or not
	 * @param buildPlan the BuildPlan to add extension to
	 * @return true if adding the extension was successful, else false
	 */
	public boolean addExtension(String namespace, boolean mustUnderstand, BuildPlan buildPlan) {
		BPELProcessHandler.LOG.debug("Trying to add extension {} with mustUnderstand {} to BuildPlan {}", namespace, String.valueOf(mustUnderstand), buildPlan.getBpelProcessElement().getAttribute("name"));
		if (this.hasExtension(namespace, buildPlan)) {
			BPELProcessHandler.LOG.warn("Adding extension failed");
			return false;
		} else {
			Element extensionElement = buildPlan.getBpelDocument().createElementNS(BuildPlan.bpelNamespace, "extension");
			extensionElement.setAttribute("namespace", namespace);
			extensionElement.setAttribute("mustUnderstand", (mustUnderstand) ? "yes" : "no");
			buildPlan.getBpelExtensionsElement().appendChild(extensionElement);
		}
		BPELProcessHandler.LOG.debug("Adding Extension was successful");
		return true;
	}
	
	/**
	 * Chechs whether the given BuildPlan has a extension with the given
	 * namespace
	 * 
	 * @param namespace the namespace of the extension
	 * @param buildPlan the BuildPlan to check with
	 * @return true if the BuidlPlan has an extension with the given namespace,
	 *         else false
	 */
	private boolean hasExtension(String namespace, BuildPlan buildPlan) {
		return Utils.hasChildElementWithAttribute(buildPlan.getBpelExtensionsElement(), "namespace", namespace);
	}
	
	/**
	 * Assigns a String value to a variable inside the assign of the main
	 * sequence of the given buildplan
	 * 
	 * @param variableName the variableName of the variable to set
	 * @param variableValue the value to set
	 * @param buildPlan the buildPlan where this has to happen
	 * @return true
	 */
	public boolean assignVariableStringValue(String variableName, String variableValue, BuildPlan buildPlan) {
		BPELProcessHandler.LOG.debug("Trying to add assign of variable {} with value {} to BuildPlan {}", variableName, variableValue, buildPlan.getBpelProcessElement().getAttribute("name"));
		Element propertyAssignElement = buildPlan.getBpelMainSequencePropertyAssignElement();
		// create copy element
		Element copyElement = buildPlan.getBpelDocument().createElementNS(BuildPlan.bpelNamespace, "copy");
		Element fromElement = buildPlan.getBpelDocument().createElementNS(BuildPlan.bpelNamespace, "from");
		Element literalElement = buildPlan.getBpelDocument().createElementNS(BuildPlan.bpelNamespace, "literal");
		literalElement.setTextContent(variableValue);
		fromElement.appendChild(literalElement);
		Element toElement = buildPlan.getBpelDocument().createElementNS(BuildPlan.bpelNamespace, "to");
		toElement.setAttribute("variable", variableName);
		copyElement.appendChild(fromElement);
		copyElement.appendChild(toElement);
		propertyAssignElement.appendChild(copyElement);
		
		BPELProcessHandler.LOG.debug("Adding assing was successful");
		// TODO check if a false can be made
		return true;
	}
	
	/**
	 * Adds a BPEL copy element given as String to the last assign of the
	 * BuildPlan. Note that the string given must be valid
	 * 
	 * @param copyElementString a valid string of a BPEL copy element
	 * @param buildPlan the BuildPlan to add the BPEL copy element to
	 * @return true if adding the string was successful, else false
	 */
	public boolean addCopyStringToOutputAssign(String copyElementString, BuildPlan buildPlan) {
		BPELProcessHandler.LOG.debug("Trying to add following copy to outputassign of BuildPlan {}", copyElementString, buildPlan.getBpelProcessElement().getAttribute("name"));
		try {
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(copyElementString));
			Document doc = this.documentBuilder.parse(is);
			Node copyElement = doc.getFirstChild();
			copyElement = buildPlan.getBpelDocument().importNode(copyElement, true);
			if (buildPlan.getBpelMainSequenceOutputAssignElement().getChildNodes().getLength() == 0) {
				buildPlan.getBpelMainSequenceOutputAssignElement().appendChild(copyElement);
			} else {
				Element outputAssignElement = buildPlan.getBpelMainSequenceOutputAssignElement();
				outputAssignElement.insertBefore(copyElement, outputAssignElement.getFirstChild());
			}
		} catch (SAXException e) {
			BPELProcessHandler.LOG.error("Failed adding copy to output assign", e);
			return false;
		} catch (IOException e) {
			BPELProcessHandler.LOG.error("Failed adding copy to output assign", e);
			return false;
		}
		BPELProcessHandler.LOG.debug("Adding copy was successful");
		return true;
		
	}
	
	/**
	 * Adds a copy from a String variable to the specified Element inside the
	 * output message of the given BuildPlan
	 * 
	 * @param variableName the variableName of the given BuildPlan the value
	 *            should be assigned from
	 * @param outputElementName the Element LocalName inside the outputMessage
	 *            of the BuildPlan
	 * @param buildPlan the BuildPlan to add the copy to
	 * @return true if adding the copy was successful, else false
	 */
	public boolean assginOutputWithVariableValue(String variableName, String outputElementName, BuildPlan buildPlan) {
		BPELProcessHandler.LOG.debug("Trying to add copy from variable {} to element {} of OutputMessage of BuildPlan {}", variableName, outputElementName, buildPlan.getBpelProcessElement().getAttribute("name"));
		Element outputAssignElement = buildPlan.getBpelMainSequenceOutputAssignElement();
		// create copy elements
		Element copyElement = buildPlan.getBpelDocument().createElementNS(BuildPlan.bpelNamespace, "copy");
		Element fromElement = buildPlan.getBpelDocument().createElementNS(BuildPlan.bpelNamespace, "from");
		Element toElement = buildPlan.getBpelDocument().createElementNS(BuildPlan.bpelNamespace, "to");
		Element queryElement = buildPlan.getBpelDocument().createElementNS(BuildPlan.bpelNamespace, "query");
		CDATASection cdataSection = buildPlan.getBpelDocument().createCDATASection("tns:" + outputElementName);
		
		// set attributes
		fromElement.setAttribute("variable", variableName);
		toElement.setAttribute("variable", "output");
		toElement.setAttribute("part", "payload");
		
		// set query element
		// e.g. <bpel:query
		// queryLanguage="urn:oasis:names:tc:wsbpel:2.0:sublang:xpath1.0"><![CDATA[tns:ami]]></bpel:query>
		queryElement.setAttribute("queryLanguage", "urn:oasis:names:tc:wsbpel:2.0:sublang:xpath1.0");
		queryElement.appendChild(cdataSection);
		
		// set everything together
		toElement.appendChild(queryElement);
		copyElement.appendChild(fromElement);
		copyElement.appendChild(toElement);
		outputAssignElement.appendChild(copyElement);
		BPELProcessHandler.LOG.debug("Adding copy was successful");
		return true;
	}
	
	/**
	 * Checks whether the variable given by name is initialized at the beginning
	 * of the plan
	 * 
	 * @param variableName the name of the variable to check for
	 * @param buildPlan the BuildPlan to check in
	 * @return true if there is a copy element inside the main assign element of
	 *         the given BuildPlan
	 */
	public boolean isVariableInitialized(String variableName, BuildPlan buildPlan) {
		Element propertyAssignElement = buildPlan.getBpelMainSequencePropertyAssignElement();
		// get all copy elements
		for (int i = 0; i < propertyAssignElement.getChildNodes().getLength(); i++) {
			if (propertyAssignElement.getChildNodes().item(i).getLocalName().equals("copy")) {
				Node copyElement = propertyAssignElement.getChildNodes().item(i);
				for (int j = 0; j < copyElement.getChildNodes().getLength(); j++) {
					if (copyElement.getChildNodes().item(j).getLocalName().equals("to")) {
						Node toElement = copyElement.getChildNodes().item(j);
						if (toElement.getAttributes().getNamedItem("variable").getNodeValue().equals(variableName)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * Removes a link with given name from the given BuildPlan
	 * 
	 * @param link the name of the link to remove
	 * @param buildPlan the BuildPlan to remove the link from
	 */
	public void removeLink(String link, BuildPlan buildPlan) {
		NodeList children = buildPlan.getBpelMainFlowLinksElement().getChildNodes();
		Node toRemove = null;
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getAttributes().getNamedItem("name").getNodeValue().equals(link)) {
				toRemove = children.item(i);
			}
			if (children.item(i).getAttributes().getNamedItem("name").getTextContent().equals(link)) {
				toRemove = children.item(i);
			}
			
		}
		if (toRemove != null) {
			buildPlan.getBpelMainFlowLinksElement().removeChild(toRemove);
		}
		
	}
	
	public List<String> getMainVariableNames(BuildPlan buildPlan) {
		List<String> names = new ArrayList<String>();
		NodeList childNodes = buildPlan.getBpelProcessVariablesElement().getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node child = childNodes.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				String varName = child.getAttributes().getNamedItem("name").getNodeValue();
				names.add(varName);
			}
		}
		return names;
	}
	
}
