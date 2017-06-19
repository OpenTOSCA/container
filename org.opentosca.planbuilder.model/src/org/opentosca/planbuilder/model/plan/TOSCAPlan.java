package org.opentosca.planbuilder.model.plan;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * <p>
 * This class is the main model for the PlanBuilder. It represents a BPEL
 * Process, which enforces the structure of the concepts in <a href=
 * "http://www2.informatik.uni-stuttgart.de/cgi-bin/NCSTRL/NCSTRL_view.pl?id=BCLR-0043&mod=0&engl=1&inst=FAK"
 * >Konzept und Implementierung eine Java-Komponente zur Generierung von WS-BPEL
 * 2.0 BuildPlans für OpenTOSCA</a>. The methods on this class mostly consist of
 * setters/getters, all logic should be made through the facade under
 * org.opentosca.planbuilder.handlers
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 * 
 */
public class TOSCAPlan {

	// determines whether this plan is a BuildPlan, ManagementPlan, TerminatePlan
	public enum PlanType {
		BUILD, MANAGE, TERMINATE
	}

	/**
	 * <p>
	 * Defines which variables the model allows to define
	 * <p>
	 * Copyright 2013 IAAS University of Stuttgart <br>
	 * <br>
	 * 
	 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
	 * 
	 */
	public enum VariableType {
		MESSAGE, TYPE
	}

	/**
	 * <p>
	 * Defines which imports the model allows to define
	 * <p>
	 * Copyright 2013 IAAS University of Stuttgart <br>
	 * <br>
	 * 
	 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
	 * 
	 */
	public enum ImportType {
		WSDL, XSD;

		@Override
		public String toString() {
			switch (this) {
			case XSD:
				return "http://www.w3.org/2001/XMLSchema";
			case WSDL:
				return "http://schemas.xmlsoap.org/wsdl/";
			default:
				return null;
			}
		}
	}

	// xml document
	private Document bpelProcessDocument;

	// variables associated with the bpel xml document itself
	private Element bpelProcessElement;
	private Element bpelExtensionsElement;
	private List<Element> bpelImportElements;
	private Element bpelPartnerLinksElement;
	private Element bpelProcessVariablesElement;

	// variables associated with the bpel orchestration
	// the main sequence element of this process
	private Element bpelMainSequenceElement;
	// assign element for property assigns
	private Element bpelMainSequencePropertyAssignElement;
	// assign element for output
	private Element bpelMainSequenceOutputAssignElement;
	// the main receive element of this process
	private Element bpelMainSequenceReceiveElement;
	// the main reply element, it is an invoke element for the callback
	private Element bpelMainSequenceCallbackInvokeElement;
	// the main flow element, all scopes are defined here on which the plugins
	// will work on
	private Element bpelMainFlowElement;
	private Element bpelMainFlowLinksElement;

	// variable for TemplateBuildPlans, makes it easier or handlers and
	// planbuilder to hold it here extra
	private List<TemplateBuildPlan> templateBuildPlans = new ArrayList<TemplateBuildPlan>();
	// same here for definitions
	private AbstractDefinitions definitions = null;

	// imported files of the whole buildplan, to keep track for export
	private List<File> importedFiles;

	// var for apache ode deployment deskriptor
	private Deploy deploymentDeskriptor;

	// holds the qname of the serviceTemplate this buildPlan belongs to
	private QName serviceTemplate;

	// the file name of the csar the serviceTemplate and this buildPlan belongs
	// to
	private String csarName = null;

	// used to generate unique id's for the plugins
	private int id = 0;

	// wsdl related stuff
	private GenericWsdlWrapper processWsdl = null;

	public static String bpelNamespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable";
	
	private PlanType type;
	
	

	/**
	 * @return the type
	 */
	public PlanType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(PlanType type) {
		this.type = type;
	}

	/**
	 * Returns a id for the plugins to make their declarations unique
	 * 
	 * @return an Integer
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Sets the id
	 * 
	 * @param id
	 *            an Integer
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Returns the csar file name this BuildPlan belongs to
	 * 
	 * @return a String
	 */
	public String getCsarName() {
		return this.csarName;
	}

	/**
	 * Sets the csar file name this BuildPlan belongs to
	 * 
	 * @param csarName
	 *            a String
	 */
	public void setCsarName(String csarName) {
		this.csarName = csarName;
	}

	/**
	 * Returns the QName of the ServiceTemplate this BuildPlan belongs to
	 * 
	 * @return a QName
	 */
	public QName getServiceTemplate() {
		return this.serviceTemplate;
	}

	/**
	 * Sets the ServiceTemplate this BuildPlan belongs to
	 * 
	 * @param serviceTemplate
	 *            a QName
	 */
	public void setServiceTemplate(QName serviceTemplate) {
		this.serviceTemplate = serviceTemplate;
	}

	/**
	 * Returns the definitions document this buildPlan belongs to. the
	 * ServiceTemplate this BuildPlan provisions should be contained in the
	 * given Definitions
	 * 
	 * @return an AbstractDefinitions
	 */
	public AbstractDefinitions getDefinitions() {
		return this.definitions;
	}

	/**
	 * Returns all files this bBuildPlan has imported
	 * 
	 * @return a List of File
	 */
	public List<File> getImportedFiles() {
		return this.importedFiles;
	}

	/**
	 * Sets the imported files of this BuildPlan
	 * 
	 * @param files
	 *            a List of File
	 */
	public void setImportedFiles(List<File> files) {
		this.importedFiles = files;
	}

	/**
	 * Adds a file to the imported files of this BuildPlan
	 * 
	 * @param file
	 *            the File to add as imported file
	 * @return true iff adding was successful
	 */
	public boolean addImportedFile(File file) {
		return this.importedFiles.add(file);
	}

	/**
	 * Sets the AbstractDefinitions of this BuildPlan
	 * 
	 * @param definitions
	 *            an AbstractDefinitions
	 */
	public void setDefinitions(AbstractDefinitions definitions) {
		this.definitions = definitions;
	}

	/**
	 * Returns the TemplateBuildPlans this BuildPlan contains
	 * 
	 * @return a List of TemplateBuildPlan
	 */
	public List<TemplateBuildPlan> getTemplateBuildPlans() {
		return this.templateBuildPlans;
	}

	/**
	 * Adds a TemplateBuildPlan to this BuildPlan
	 * 
	 * @param template
	 *            a TemplateBuildPlan to add
	 * @return true iff adding was successful
	 */
	public boolean addTemplateBuildPlan(TemplateBuildPlan template) {
		return this.templateBuildPlans.add(template);
	}

	/**
	 * Sets the TemplateBuildPlans of this BuildPlan
	 * 
	 * @param templateBuildPlans
	 *            a List of TemplateBuildPlan
	 */
	public void setTemplateBuildPlans(List<TemplateBuildPlan> templateBuildPlans) {
		this.templateBuildPlans = templateBuildPlans;
	}

	/**
	 * Returns the the main BPEL Flow Element of this BuildPlan
	 * 
	 * @return a DOM Element which is a BPEL Flow ELement
	 */
	public Element getBpelMainFlowLinksElement() {
		return this.bpelMainFlowLinksElement;
	}

	/**
	 * Sets the Links Element for the flow element of this BuildPlan
	 * 
	 * @param bpelMainFlowLinksElement
	 *            a DOM Element
	 */
	public void setBpelMainFlowLinksElement(Element bpelMainFlowLinksElement) {
		this.bpelMainFlowLinksElement = bpelMainFlowLinksElement;
	}

	/**
	 * Returns the DOM Document of this BuildPlan
	 * 
	 * @return a DOM Document
	 */
	public Document getBpelDocument() {
		return this.bpelProcessDocument;
	}

	/**
	 * Sets the BPEL Document of this BuildPlan
	 * 
	 * @param bpelProcessDocument
	 *            a DOM Document
	 */
	public void setBpelDocument(Document bpelProcessDocument) {
		this.bpelProcessDocument = bpelProcessDocument;
	}

	/**
	 * Returns the BPEL process element of this BuildPlan
	 * 
	 * @return a DOM Element
	 */
	public Element getBpelProcessElement() {
		return this.bpelProcessElement;
	}

	/**
	 * Sets the BPEL Process element of this BuildPlan
	 * 
	 * @param bpelProcessElement
	 *            a DOM Element
	 */
	public void setBpelProcessElement(Element bpelProcessElement) {
		this.bpelProcessElement = bpelProcessElement;
	}

	/**
	 * Returns the BPEL Extensions element of this BuildPlan
	 * 
	 * @return a DOM Element
	 */
	public Element getBpelExtensionsElement() {
		return this.bpelExtensionsElement;
	}

	/**
	 * Sets the BPEL Extensions element of this BuildPlan
	 * 
	 * @param bpelExtensionsElement
	 *            a DOM Element
	 */
	public void setBpelExtensionsElement(Element bpelExtensionsElement) {
		this.bpelExtensionsElement = bpelExtensionsElement;
	}

	/**
	 * Returns a List of DOM Element which are import BPEL Import elements
	 * 
	 * @return a List of DOM Element
	 */
	public List<Element> getBpelImportElements() {
		return this.bpelImportElements;
	}

	/**
	 * Adds a import element to this BuildPlan
	 * 
	 * @param bpelImportsElement
	 *            a DOM Element
	 * @return true iff adding was successful
	 */
	public boolean addBpelImportElement(Element bpelImportsElement) {
		return this.bpelImportElements.add(bpelImportsElement);
	}

	/**
	 * Sets the BPEL imports of this BuildPlan
	 * 
	 * @param bpelImportElements
	 *            a List of DOM Element
	 */
	public void setBpelImportElements(List<Element> bpelImportElements) {
		this.bpelImportElements = bpelImportElements;
	}

	/**
	 * Returns the BPEL Partnerlinks element of this BuildPlan
	 * 
	 * @return a DOM Element
	 */
	public Element getBpelPartnerLinksElement() {
		return this.bpelPartnerLinksElement;
	}

	/**
	 * Sets the BPEL Partnerlink element of this BuildPlan
	 * 
	 * @param bpelPartnerLinksElement
	 *            a DOM Element
	 */
	public void setBpelPartnerLinksElement(Element bpelPartnerLinksElement) {
		this.bpelPartnerLinksElement = bpelPartnerLinksElement;
	}

	/**
	 * Returns the global BPEL Variables element of this BuildPlan
	 * 
	 * @return a DOM Element
	 */
	public Element getBpelProcessVariablesElement() {
		return this.bpelProcessVariablesElement;
	}

	/**
	 * Sets the gloval BPEL Variables Element of this BuildPlan
	 * 
	 * @param bpelProcessVariablesElement
	 *            a DOM Element
	 */
	public void setBpelProcessVariablesElement(Element bpelProcessVariablesElement) {
		this.bpelProcessVariablesElement = bpelProcessVariablesElement;
	}

	/**
	 * Returns the main BPEL Sequence element of this BuildPlan
	 * 
	 * @return a DOM Element
	 */
	public Element getBpelMainSequenceElement() {
		return this.bpelMainSequenceElement;
	}

	/**
	 * Sets the main BPEL Sequence element of this BuildPlan
	 * 
	 * @param bpelMainSequenceElement
	 *            a DOM Element
	 */
	public void setBpelMainSequenceElement(Element bpelMainSequenceElement) {
		this.bpelMainSequenceElement = bpelMainSequenceElement;
	}

	/**
	 * Returns the main BPEL Receive element of this BuildPlan
	 * 
	 * @return a DOM Element
	 */
	public Element getBpelMainSequenceReceiveElement() {
		return this.bpelMainSequenceReceiveElement;
	}

	/**
	 * Sets the main BPEL Receive element of this BuildPlan
	 * 
	 * @param bpelMainSequenceReceiveElement
	 *            a DOM Element
	 */
	public void setBpelMainSequenceReceiveElement(Element bpelMainSequenceReceiveElement) {
		this.bpelMainSequenceReceiveElement = bpelMainSequenceReceiveElement;
	}

	/**
	 * Returns the main BPEL Invoke Element for Callback of this BuildPlan
	 * 
	 * @return a DOM Element
	 */
	public Element getBpelMainSequenceCallbackInvokeElement() {
		return this.bpelMainSequenceCallbackInvokeElement;
	}

	/**
	 * Sets the main BPEL Invoke element for Callback of this BuildPlan
	 * 
	 * @param bpelMainSequenceCallbackInvokeElement
	 *            a DOM Element
	 */
	public void setBpelMainSequenceCallbackInvokeElement(Element bpelMainSequenceCallbackInvokeElement) {
		this.bpelMainSequenceCallbackInvokeElement = bpelMainSequenceCallbackInvokeElement;
	}

	/**
	 * Returns the main BPEL Flow element of this BuildPlan
	 * 
	 * @return a DOM Element
	 */
	public Element getBpelMainFlowElement() {
		return this.bpelMainFlowElement;
	}

	/**
	 * Sets the main BPEL Flow element of this BuildPlan
	 * 
	 * @param bpelMainFlowElement
	 *            a DOM Element
	 */
	public void setBpelMainFlowElement(Element bpelMainFlowElement) {
		this.bpelMainFlowElement = bpelMainFlowElement;
	}

	/**
	 * Returns the WSDL of this BuildPlan
	 * 
	 * @return a GenericWsdlWrapper
	 */
	public GenericWsdlWrapper getWsdl() {
		return this.processWsdl;
	}

	/**
	 * Sets the WSDL of this BuildPlan
	 * 
	 * @param processWsdl
	 *            a GenericWsdlWrapper
	 */
	public void setProcessWsdl(GenericWsdlWrapper processWsdl) {
		this.processWsdl = processWsdl;
	}

	/**
	 * Returns the main BPEL assign element for properties of this BuildPlan
	 * 
	 * @return a DOM Element
	 */
	public Element getBpelMainSequencePropertyAssignElement() {
		return this.bpelMainSequencePropertyAssignElement;
	}

	/**
	 * Sets the main BPEL assign element for properties of this BuildPlan
	 * 
	 * @param bpelMainSequencePropertyAssignElement
	 *            a DOM Element
	 */
	public void setBpelMainSequencePropertyAssignElement(Element bpelMainSequencePropertyAssignElement) {
		this.bpelMainSequencePropertyAssignElement = bpelMainSequencePropertyAssignElement;
	}

	/**
	 * Returns the DeploymentDeskriptor of this BuildPlan
	 * 
	 * @return a JAXB Deploy Object
	 */
	public Deploy getDeploymentDeskriptor() {
		return this.deploymentDeskriptor;
	}

	/**
	 * Set the DeploymentDeskriptor of this BuildPlan
	 * 
	 * @param deploymentDeskriptor
	 *            a JAXB Deploy Object
	 */
	public void setDeploymentDeskriptor(Deploy deploymentDeskriptor) {
		this.deploymentDeskriptor = deploymentDeskriptor;
	}

	/**
	 * Returns the main BPEL Assign element for the output of this BuildPlan
	 * 
	 * @return a DOM Element
	 */
	public Element getBpelMainSequenceOutputAssignElement() {
		return this.bpelMainSequenceOutputAssignElement;
	}

	/**
	 * Sets the main BPEL Assign element for the ouput of this BuildPlan
	 * 
	 * @param bpelMainSequenceOutputAssignElement
	 *            a DOM Element
	 */
	public void setBpelMainSequenceOutputAssignElement(Element bpelMainSequenceOutputAssignElement) {
		this.bpelMainSequenceOutputAssignElement = bpelMainSequenceOutputAssignElement;
	}

}
