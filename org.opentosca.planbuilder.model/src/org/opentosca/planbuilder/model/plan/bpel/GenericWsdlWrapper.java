package org.opentosca.planbuilder.model.plan.bpel;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.FileLocator;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.AbstractPlan.PlanType;
import org.osgi.framework.FrameworkUtil;

/**
 * <p>
 * This class represents a WSDL v1.1. This class is mainly used for the
 * BuildPlan. It uses a internal fragmented WSDL File and allows to add
 * declarations at defined points. The WSDL declares a single PortType for
 * invoking the BuildPlan and second for callback. Both have a single one-way
 * operation defined. With the given operations of this class, the messages can
 * have additional elements defined, which can be used by the plugins to fetch
 * data outside of the TopoloyTemplate scope.
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class GenericWsdlWrapper {

	// corresponds the processname and namespace
	private final static String WSDL_NAME_TAG = "{wsdlname}";
	private final static String WSDL_TARGETNAMESPACE_TAG = "{wsdltargetnamespace}";

	// used for adding namespace declarations
	private final static String WSDL_NAMESPACEPREFIX_TAG = "{wsdlnamespaceprefix}";
	// used as tag to add partnerlinks at the right place
	private final static String WSDL_PARTNERLINKS_TAG = "{wsdlpartnerlinks}";
	// used as tag to add imports at the right place
	private final static String WSDL_IMPORTS_TAG = "{wsdlimports}";
	// used as tag to add elements to the requestmessage
	private final static String WSDL_REQUESTTYPEELEMENTS_TAG = "{wsdlrequesttypeelements}";
	// use as tag to add elements to the response message
	private final static String WSDL_RESPONETYPEELEMENTS_TAG = "{wsdlresponsetypeelements}";
	// use as tag to add propertys
	private final static String WSDL_PROPERTYS_TAG = "{vprops}";
	// use as tag to add propertyaliases
	private final static String WSDL_PROPERTYALIAS_TAG = "{vpropaliases}";

	// use as tag to set invoke operation name
	private final static String WSDL_INVOKE_OPERATION_NAME = "{operationName}";

	// this holds the complete wsdl
	private String genericWsdlFileAsString;

	// the namespace and name of the process this wsdl belongs to
	private String processName = null;
	private String namespace = null;

	// the names of the partnerLinkTypes this wsdl holds

	private List<String> partnerLinkTypeNames;

	// the localNames inside the input and output message
	private List<String> inputMessageLocalNames;
	private List<String> outputMessageLocalNames;

	// a list of absolute locations of imported wsdl/xsd's
	private List<String> absoluteLocations;

	// a list of names of delcared properties
	private List<String> properties;

	// a map to store partnerLinks
	private PltMap pltMap = new PltMap();

	// counts namespaces
	private int namespaceCounter = 0;
	// a set of namespaces used in the wsdl

	private Set<String> namespaces = new HashSet<String>();

	/**
	 * <p>
	 * This class is used to map and store partnerLinks inside the
	 * GenericWsdlWrapper calss
	 * </p>
	 * Copyright 2013 IAAS University of Stuttgart <br>
	 * <br>
	 *
	 * @author nyu
	 *
	 */
	private class PltMap {


		private List<String> partnerLinkTypeNames = new ArrayList<String>();
		private List<String> roleNames1 = new ArrayList<String>();
		private List<QName> portTypes1 = new ArrayList<QName>();
		private List<String> roleNames2 = new ArrayList<String>();
		private List<QName> portTypes2 = new ArrayList<QName>();

		/**
		 * Adds a partnerLinkType to this PltMap
		 * 
		 * @param partnerLinkTypeName
		 *            the name of the partnerLinkType to use
		 * @param role1
		 *            the name of the 1st role
		 * @param portType1
		 *            a QName of the 1st portType
		 * @param role2
		 *            the name of the 2nd role
		 * @param portType2
		 *            a QName of the 2nd portType
		 * @return true iff adding was successful
		 */
		public boolean addPLT(String partnerLinkTypeName, String role1, QName portType1, String role2,
				QName portType2) {
			boolean check = true;
			check &= this.partnerLinkTypeNames.add(partnerLinkTypeName);
			check &= this.roleNames1.add(role1);
			check &= this.portTypes1.add(portType1);
			check &= this.roleNames2.add(role2);
			check &= this.portTypes2.add(portType2);
			return check;
		}

		/**
		 * Returns the names of the partnerLinkTypes
		 *
		 * @return a List of Strings
		 */
		public List<String> getPartnerLinkTypeNames() {
			return this.partnerLinkTypeNames;
		}

		/**
		 * Returns the 1st portType of the given partnerLinkType
		 * 
		 * @param partnerLinkTypeName
		 *            the name of the partnerLinkType
		 * @return a QName if the partnerLinkType is found, else null
		 */
		public QName getPortType1OfPLT(final String partnerLinkTypeName) {
			final int pos = this.partnerLinkTypeNames.indexOf(partnerLinkTypeName);
			return this.portTypes1.get(pos);
		}

		/**
		 * Returns the 2nd portType of the given partnerLinkType
		 * 
		 * @param partnerLinkTypeName
		 *            the name of the partnerLinkType
		 * @return a QName of the 2nd PortType, else null
		 */
		public QName getPortType2OfPLT(final String partnerLinkTypeName) {
			final int pos = this.partnerLinkTypeNames.indexOf(partnerLinkTypeName);
			final QName portType = this.portTypes2.get(pos);
			// check if this is a portType dummy
			if (portType.getLocalPart().equals("")) {
				return null;
			} else {
				return portType;
			}

		}
	}


	/**
	 * Constructor
	 * 
	 * @throws IOException
	 *             is thrown when reading the internal file fails
	 */
	public GenericWsdlWrapper(BPELPlan.PlanType planType) throws IOException {
		URL url = FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle()
				.getResource("genericProcessWsdl.wsdl");
		File genericWsdlFile = new File(FileLocator.toFileURL(url).getPath());
		this.genericWsdlFileAsString = FileUtils.readFileToString(genericWsdlFile);
		this.partnerLinkTypeNames = new ArrayList<String>();
		this.absoluteLocations = new ArrayList<String>();
		this.inputMessageLocalNames = new ArrayList<String>();
		this.outputMessageLocalNames = new ArrayList<String>();
		this.properties = new ArrayList<String>();

		switch (planType) {
		case BUILD:
			this.genericWsdlFileAsString = this.genericWsdlFileAsString.replace(WSDL_INVOKE_OPERATION_NAME,
					"initiate");
		case MANAGE:
			this.genericWsdlFileAsString = this.genericWsdlFileAsString.replace(WSDL_INVOKE_OPERATION_NAME,
					"initiate");
		case TERMINATE:
			this.genericWsdlFileAsString = this.genericWsdlFileAsString.replace(WSDL_INVOKE_OPERATION_NAME,
					"terminate");
		}
	}

	/**
	 * Returns the file name of this wsdl
	 *
	 * @return a WSDL file name as String
	 */
	public String getFileName() {
		return this.processName + ".wsdl";
	}

	/**
	 * Returns the localNames of the Elements inside the output message of this
	 * wsdl
	 *
	 * @return a List of Strings
	 */
	public List<String> getOuputMessageLocalNames() {
		return this.outputMessageLocalNames;
	}

	/**
	 * Returns the localNames of the Elements inside the input message of this
	 * wsdl
	 *
	 * @return a List of Strings
	 */
	public List<String> getInputMessageLocalNames() {
		return this.inputMessageLocalNames;
	}

	/**
	 * Adds a element declaration to the input message of this wsdl
	 * 
	 * @param elementName
	 *            the localName of the element
	 * @param type
	 *            the XSD type of the element
	 * @return true iff adding was successful
	 */
	public boolean addElementToRequestMessage(final String elementName, final QName type) {
		if (!this.inputMessageLocalNames.contains(elementName)) {
			this.genericWsdlFileAsString = this.genericWsdlFileAsString.replace(
					GenericWsdlWrapper.WSDL_REQUESTTYPEELEMENTS_TAG,
					this.generateElementString(elementName, type.getLocalPart())
							+ GenericWsdlWrapper.WSDL_REQUESTTYPEELEMENTS_TAG);
			this.inputMessageLocalNames.add(elementName);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Adds a element declaration to the output message of this wsdl
	 * 
	 * @param elementName
	 *            the localName of the element
	 * @param type
	 *            the XSD type of the element
	 * @return true iff adding was successful
	 */
	public boolean addElementToResponseMessage(final String elementName, final QName type) {
		if (!this.outputMessageLocalNames.contains(elementName)) {
			this.genericWsdlFileAsString = this.genericWsdlFileAsString.replace(
					GenericWsdlWrapper.WSDL_RESPONETYPEELEMENTS_TAG,
					this.generateElementString(elementName, type.getLocalPart())
							+ GenericWsdlWrapper.WSDL_RESPONETYPEELEMENTS_TAG);
			this.outputMessageLocalNames.add(elementName);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Adds a namespace with fiven prefix to this wsdl
	 * 
	 * @param namespace
	 *            the namespace to add
	 * @param prefix
	 *            the prefix for the given namespace
	 * @return true iff adding was successful
	 */
	private boolean addNamespace(final String namespace, final String prefix) {
		if (!this.namespaces.contains(namespace)) {
			this.namespaces.add(namespace);
			if ((prefix == null) | prefix.equals("")) {
				String nsDecl1 = "xmlns:ns" + this.namespaceCounter + "=\"" + namespace + "\" ";
				this.genericWsdlFileAsString = this.genericWsdlFileAsString.replace(
						GenericWsdlWrapper.WSDL_NAMESPACEPREFIX_TAG,
						nsDecl1 + GenericWsdlWrapper.WSDL_NAMESPACEPREFIX_TAG);
				this.namespaceCounter++;
			} else {
				String nsDecl2 = "xmlns:" + prefix + "=\"" + namespace + "\" ";
				this.genericWsdlFileAsString = this.genericWsdlFileAsString.replace(
						GenericWsdlWrapper.WSDL_NAMESPACEPREFIX_TAG,
						nsDecl2 + GenericWsdlWrapper.WSDL_NAMESPACEPREFIX_TAG);
			}

			return true;
		} else {
			return false;
		}
	}

	/**
	 * Adds an import element to his wsdl
	 * 
	 * @param importType
	 *            the type of the import (wsdl, xsd)
	 * @param namespace
	 *            the namespace of the import
	 * @param prefix
	 *            the prefix of namespace
	 * @param location
	 *            the location of the import
	 * @return true iff adding was successful
	 */
	public boolean addImportElement(final String importType, final String namespace, final String prefix, final String location) {
		// TODO we assume the location is absolute for packaging later this has
		// to be fixed
		if (this.absoluteLocations.contains(location)) {
			return false;
		}


		String importString = this.generateImportString(importType, namespace, location);
		this.absoluteLocations.add(location);
		this.genericWsdlFileAsString = this.genericWsdlFileAsString.replace(GenericWsdlWrapper.WSDL_IMPORTS_TAG,
				importString + GenericWsdlWrapper.WSDL_IMPORTS_TAG);
		this.addNamespace(namespace, prefix);
		return true;
	}

	/**
	 * Generates a string which contains a wsdl import string
	 * 
	 * @param importType
	 *            the importType of the import as String
	 * @param namespace
	 *            the namespace of the import as String
	 * @param location
	 *            the location of the import as String
	 * @return a String containing an WSDL import declaration
	 */
	private String generateImportString(final String importType, final String namespace, final String location) {
		// FIXME ? killed importType here importType=\"" + importType + "\"
		return "<import namespace=\"" + namespace + "\" location=\"" + location + "\"/>";
	}

	/**
	 * Generates a XSD element declaration as string
	 * 
	 * @param name
	 *            the name of the element as String
	 * @param type
	 *            the type of the element as String
	 * @return a String containing a XSD declaration
	 */
	private String generateElementString(final String name, final String type) {
		return "<element name=\"" + name + "\" " + "type=\"" + type + "\"/>";
	}

	/**
	 * Sets the id of this WSDL
	 * 
	 * @param namespace
	 *            the namespace of the WSDL to set
	 * @param name
	 *            the name of the WSDL to set
	 */
	public void setId(final String namespace, final String name) {
		if (this.processName == null) {
			this.genericWsdlFileAsString = this.genericWsdlFileAsString.replace(GenericWsdlWrapper.WSDL_NAME_TAG, name);
		} else {
			this.genericWsdlFileAsString = this.genericWsdlFileAsString.replace(this.processName, name);
		}
		this.processName = name;

		if (this.namespace == null) {
			this.genericWsdlFileAsString = this.genericWsdlFileAsString
					.replace(GenericWsdlWrapper.WSDL_TARGETNAMESPACE_TAG, namespace);
		} else {
			this.genericWsdlFileAsString = this.genericWsdlFileAsString.replace(this.namespace, namespace);
		}
		this.namespace = namespace;
	}

	/**
	 * Returns the targetNamespace of this WSDL
	 *
	 * @return a String containing the targetNamespace of this WSDL
	 */
	public String getTargetNamespace() {
		return this.namespace;
	}

	/**
	 * Returns the localName of the Response message this WSDL
	 *
	 * @return a String containing the localName of the Response message
	 */
	public String getResponseMessageLocalName() {
		return this.processName + "Response";
	}

	/**
	 * Returns the localName of the Request message this WSDL
	 *
	 * @return a String containing the localName of the Request message
	 */
	public String getRequestMessageLocalName() {
		return this.processName + "Request";
	}

	/**
	 * Adds a partnerLinkType to this WSDL
	 * 
	 * @param partnerLinkTypeName
	 *            the name of partnerLinkType
	 * @param roleName
	 *            the name of the 1st role
	 * @param portType
	 *            the portType of the partner
	 * @return true iff adding the partnerLinkType was successful
	 */
	public boolean addPartnerLinkType(final String partnerLinkTypeName, final String roleName, final QName portType) {
		if (this.isPartnerLinkTypeNameAlreadyUsed(partnerLinkTypeName)) {
			return false;
		} else {
			// replace the tag with new partnerlinktype+tag, for adding other
			// partnerlinks later
			this.genericWsdlFileAsString = this.genericWsdlFileAsString.replace(
					GenericWsdlWrapper.WSDL_PARTNERLINKS_TAG,
					this.generatePartnerLinkTypeString(partnerLinkTypeName, roleName, portType)
							+ GenericWsdlWrapper.WSDL_PARTNERLINKS_TAG);
		}
		this.partnerLinkTypeNames.add(partnerLinkTypeName);
		this.pltMap.addPLT(partnerLinkTypeName, roleName, portType, "", new QName(""));
		this.addNamespace(portType.getNamespaceURI(), portType.getPrefix());
		return true;
	}

	/**
	 * Adds a partnerLinkType to this WSDL
	 * 
	 * @param partnerLinkTypeName
	 *            the name of the partnerLinkType
	 * @param roleName1
	 *            the name of the 1st role
	 * @param portType1
	 *            the portType of the 1st role
	 * @param roleName2
	 *            the name of the 2nd role
	 * @param portType2
	 *            the portType of the 2nd role
	 * @return true iff adding was successful
	 */
	public boolean addPartnerLinkType(String partnerLinkTypeName, String roleName1, QName portType1, String roleName2,
			QName portType2) {
		if (this.isPartnerLinkTypeNameAlreadyUsed(partnerLinkTypeName)) {
			return false;
		} else {
			// replace the tag with new partnerlinktype+tag, for adding other
			// partnerlinks later
			this.genericWsdlFileAsString = this.genericWsdlFileAsString.replace(
					GenericWsdlWrapper.WSDL_PARTNERLINKS_TAG,
					this.generatePartnerLinkTypeString(partnerLinkTypeName, roleName1, portType1, roleName2, portType2)
							+ GenericWsdlWrapper.WSDL_PARTNERLINKS_TAG);
		}
		this.partnerLinkTypeNames.add(partnerLinkTypeName);
		this.pltMap.addPLT(partnerLinkTypeName, roleName1, portType1, roleName2, portType2);
		this.addNamespace(portType1.getNamespaceURI(), portType1.getPrefix());
		this.addNamespace(portType2.getNamespaceURI(), portType2.getPrefix());
		return true;
	}

	/**
	 * Returns the names of all registered partnerLinkTypes in this WSDL
	 *
	 * @return a List of Strings containing the names of the partnerLinkTypes
	 */
	public List<String> getPartnerlinkTypeNames() {
		return this.pltMap.getPartnerLinkTypeNames();
	}

	/**
	 * Returns the QName of the 1st portType for the given partnerLinkType name
	 * 
	 * @param partnerLinkTypeName
	 *            the name of the partnerLinkType
	 * @return a QName representing the 1st portType of the partnerLinkType,
	 *         else null
	 */
	public QName getPortType1FromPartnerLinkType(final String partnerLinkTypeName) {
		return this.pltMap.getPortType1OfPLT(partnerLinkTypeName);
	}

	/**
	 * Adds a property declaration to this WSDL
	 * 
	 * @param propertyName
	 *            the name of the property
	 * @param type
	 *            the type of the property
	 * @return true iff adding was succesful
	 */
	public boolean addProperty(final String propertyName, final QName type) {
		if (this.properties.contains(propertyName)) {
			return false;
		}
		this.addNamespace(type.getNamespaceURI(), type.getPrefix());
		String property = this.generatePropertyString(propertyName, type);
		this.genericWsdlFileAsString = this.genericWsdlFileAsString.replace(GenericWsdlWrapper.WSDL_PROPERTYS_TAG,
				property + GenericWsdlWrapper.WSDL_PROPERTYS_TAG);
		return true;
	}

	/**
	 * Adds a propertyAlias to this WSDL for the given property
	 * 
	 * @param propertyName
	 *            the name of the property the propertyAlias should belong to
	 * @param partName
	 *            the name of the message part
	 * @param messageType
	 *            the type of the message
	 * @param query
	 *            a XPath Query
	 * @return true iff adding was successful
	 */
	public boolean addPropertyAlias(final String propertyName, final String partName, final QName messageType, final String query) {
		this.addNamespace(messageType.getNamespaceURI(), messageType.getPrefix());
		String propertyAlias = this.generatePropertyAliasString(propertyName, partName, messageType, query);
		this.genericWsdlFileAsString = this.genericWsdlFileAsString.replace(GenericWsdlWrapper.WSDL_PROPERTYALIAS_TAG,
				propertyAlias + GenericWsdlWrapper.WSDL_PROPERTYALIAS_TAG);
		return true;
	}

	/**
	 * Returns the 2nd portType of referenced partnerLinkType
	 * 
	 * @param partnerLinkTypeName
	 *            the name of a partnerLinkType
	 * @return a QName representing the 2nd portType, else null
	 */
	public QName getPortType2FromPartnerLinkType(final String partnerLinkTypeName) {
		return this.pltMap.getPortType2OfPLT(partnerLinkTypeName);
	}

	/**
	 * Generates a String which contains a property declaration
	 * 
	 * @param propertyName
	 *            the name of the property
	 * @param type
	 *            the type of the property
	 * @return a String containing a property declaration
	 */
	private String generatePropertyString(final String propertyName, final QName type) {
		// <vprop:property name="createEC2InstanceCorrelationID"
		// type="xsd:string"/>
		return "<vprop:property name=\"" + propertyName + "\" type=\"" + type.getPrefix() + ":" + type.getLocalPart()
				+ "\"/>";
	}

	/**
	 * Generates a String which contains a propertyAlias declaration
	 * 
	 * @param propertyName
	 *            the name of the property the propertyAlias should belong to
	 * @param partName
	 *            the part name of the message the propertyAlias should
	 *            reference
	 * @param messageType
	 *            the type of the message the propertyAlias should reference
	 * @param query
	 *            a XPath query which the propertyAlias should use
	 * @return a String containing a propertyAlias declaration
	 */
	private String generatePropertyAliasString(final String propertyName, final String partName, final QName messageType, final String query) {
		// <vprop:propertyAlias messageType="wsdl:createEC2InstanceResponse"
		// part="parameters" propertyName="tns:createEC2InstanceCorrelationID">
		// <vprop:query><![CDATA[/wsdl:CorrelationId]]></vprop:query>
		// </vprop:propertyAlias>
		return "<vprop:propertyAlias messageType=\"" + messageType.getPrefix() + ":" + messageType.getLocalPart()
				+ "\" part=\"" + partName + "\" propertyName=\"tns:" + propertyName + "\"><vprop:query><![CDATA["
				+ query + "]]></vprop:query></vprop:propertyAlias>";
	}

	/**
	 * Generates a String containing a partnerLinkType declaration with one
	 * portType
	 * 
	 * @param partnerLinkTypeName
	 *            the name for the partnerLinkType
	 * @param roleName
	 *            the name for the role
	 * @param portType
	 *            a QName for the portType
	 * @return a String containing a partnerLinkType declaration
	 */
	private String generatePartnerLinkTypeString(String partnerLinkTypeName, String roleName, QName portType) {
		return "<plnk:partnerLinkType name=\"" + partnerLinkTypeName + "\"><plnk:role name=\"" + roleName
				+ "\" portType=\"" + portType.getPrefix() + ":" + portType.getLocalPart()
				+ "\"/></plnk:partnerLinkType>";
	}

	/**
	 * Generates a String containing a partnerLinkType declaration with two
	 * roles
	 * 
	 * @param partnerLinkTypeName
	 *            the name of the partnerLinkType
	 * @param roleName1
	 *            the name of the 1st role
	 * @param portType1
	 *            a QName of a portType for the 1st role
	 * @param roleName2
	 *            the name of the 2nd role
	 * @param portType2
	 *            a QName of a portType for the 2nd role
	 * @return a String containing a partnerLinkType declaration with 2 roles
	 */
	private String generatePartnerLinkTypeString(String partnerLinkTypeName, String roleName1, QName portType1,
			String roleName2, QName portType2) {
		return "<plnk:partnerLinkType name=\"" + partnerLinkTypeName + "\"><plnk:role name=\"" + roleName1
				+ "\" portType=\"" + portType1.getPrefix() + ":" + portType1.getLocalPart() + "\"/><plnk:role name=\""
				+ roleName2 + "\" portType=\"" + portType2.getPrefix() + ":" + portType2.getLocalPart()
				+ "\"/></plnk:partnerLinkType>";
	}

	/**
	 * Checks whether the given partnerLinkType name is already in use
	 * 
	 * @param partnerLinkTypeName
	 *            a String
	 * @return true if the given String is already used as partnerLinkType name,
	 *         else false
	 */
	private boolean isPartnerLinkTypeNameAlreadyUsed(final String partnerLinkTypeName) {
		return this.partnerLinkTypeNames.contains(partnerLinkTypeName);
	}

	/**
	 * Returns a representation of this WSDL as String.
	 *
	 * @return a String containing a complete WSDL definition document
	 */
	public String getFinalizedWsdlAsString() {
		String wsdlString = this.genericWsdlFileAsString;
		wsdlString = wsdlString.replace(GenericWsdlWrapper.WSDL_IMPORTS_TAG, "");
		wsdlString = wsdlString.replace(GenericWsdlWrapper.WSDL_NAMESPACEPREFIX_TAG, "");
		wsdlString = wsdlString.replace(GenericWsdlWrapper.WSDL_NAME_TAG, "");
		wsdlString = wsdlString.replace(GenericWsdlWrapper.WSDL_PARTNERLINKS_TAG, "");
		wsdlString = wsdlString.replace(GenericWsdlWrapper.WSDL_REQUESTTYPEELEMENTS_TAG, "");
		wsdlString = wsdlString.replace(GenericWsdlWrapper.WSDL_PROPERTYS_TAG, "");
		wsdlString = wsdlString.replace(GenericWsdlWrapper.WSDL_PROPERTYALIAS_TAG, "");
		if (this.outputMessageLocalNames.size() == 0) {
			wsdlString = wsdlString.replace(GenericWsdlWrapper.WSDL_RESPONETYPEELEMENTS_TAG, "<any minOccurs=\"0\"/>");
		} else {
			wsdlString = wsdlString.replace(GenericWsdlWrapper.WSDL_RESPONETYPEELEMENTS_TAG, "");
		}
		wsdlString = wsdlString.replace(GenericWsdlWrapper.WSDL_TARGETNAMESPACE_TAG, "");

		// change absolute locations to relative
		for (final String absolutePath : this.absoluteLocations) {
			wsdlString = wsdlString.replace(absolutePath, new File(absolutePath).getName());
		}
		return wsdlString;
	}

	/**
	 * Checks whether the QName with the given location is already imported
	 * inside this wsdl
	 * 
	 * @param qName
	 *            a QName
	 * @param absolutePath
	 *            a location where the QName is defined
	 * @return true iff the given QName is already imported inside this WSDL
	 */
	public boolean isImported(final QName qName, final String absolutePath) {
		boolean check = true;
		check &= this.absoluteLocations.contains(absolutePath);
		check &= this.namespaces.contains(qName.getNamespaceURI());
		return check;
	}
}
