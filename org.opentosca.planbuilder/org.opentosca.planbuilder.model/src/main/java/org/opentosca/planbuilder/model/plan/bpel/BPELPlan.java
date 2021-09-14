package org.opentosca.planbuilder.model.plan.bpel;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;

import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.planbuilder.model.plan.AbstractActivity;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * <p>
 * This class is the main model for the PlanBuilder. It represents a BPEL Process, which enforces the structure of the
 * concepts in <a href= "http://www2.informatik.uni-stuttgart.de/cgi-bin/NCSTRL/NCSTRL_view.pl?id=BCLR-0043&mod=0&engl=1&inst=FAK"
 * >Konzept und Implementierung eine Java-Komponente zur Generierung von WS-BPEL 2.0 BuildPlans für OpenTOSCA</a>. The
 * methods on this class mostly consist of setters/getters, all logic should be made through the facade under
 * org.opentosca.planbuilder.handlers
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public class BPELPlan extends AbstractPlan {

    public static final String bpelNamespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable";
    public static final String xpath2Namespace = "urn:oasis:names:tc:wsbpel:2.0:sublang:xpath2.0";
    public Map<String, String> namespaceMap = new HashMap<>();
    // xml document
    private Document bpelProcessDocument;
    private Element bpelProcessElement;
    private Element bpelExtensionsElement;
    // variables associated with the bpel xml document itself
    private List<Element> bpelImportElements;
    private Element bpelPartnerLinksElement;
    private Element bpelProcessVariablesElement;
    private Element bpelCorrelationSetsElement;
    private Element bpelFaultHandlersElement;
    private Element bpelMainSequenceElement;
    // assign element for property assigns
    private Element bpelMainSequencePropertyAssignElement;

    // variables associated with the bpel orchestration
    // the main sequence element of this process
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
    private List<BPELScope> templateBuildPlans = new ArrayList<>();
    // imported files of the whole buildplan, to keep track for export
    private Set<Path> importedFiles;
    // variable for TemplateBuildPlans, makes it easier or handlers and
    // planbuilder to hold it here extra
    private Deploy deploymentDeskriptor;
    private String csarName = null;
    // var for apache ode deployment deskriptor
    private GenericWsdlWrapper processWsdl = null;
    // the file name of the csar the serviceTemplate and this buildPlan belongs
    // to
    private Map<AbstractActivity, BPELScope> abstract2bpelMap;
    // wsdl related stuff
    private String toscaInterfaceName = null;
    private String toscaOperationName = null;

    public BPELPlan(final String id, final PlanType type, final TDefinitions definitions,
                    final TServiceTemplate serviceTemplate, final Collection<AbstractActivity> activities,
                    final Collection<Link> links) {
        super(id, type, definitions, serviceTemplate, activities, links);
    }

    public void setTOSCAOperationname(final String name) {
        this.toscaOperationName = name;
    }

    public String getTOSCAInterfaceName() {
        if (this.toscaInterfaceName != null) {
            return this.toscaInterfaceName;
        } else {
            return this.bpelProcessElement.getAttribute("name");
        }
    }

    public void setTOSCAInterfaceName(final String name) {
        this.toscaInterfaceName = name;
    }

    public String getProcessNamespace() {
        return this.bpelProcessElement.getAttribute("targetNamespace");
    }

    public String getTOSCAOperationName() {
        if (this.toscaOperationName != null) {
            return this.toscaOperationName;
        } else {
            return getBpelMainSequenceReceiveElement().getAttribute("operation");
        }
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
     * @param csarName a String
     */
    public void setCsarName(final String csarName) {
        this.csarName = csarName;
    }

    public Element getBpelCorrelationSetsElement() {
        return bpelCorrelationSetsElement;
    }

    public void setBpelCorrelationSetsElement(Element bpelCorrelationSetsElement) {
        this.bpelCorrelationSetsElement = bpelCorrelationSetsElement;
    }

    public Element getBpelFaultHandlersElement() {
        return bpelFaultHandlersElement;
    }

    public void setBpelFaultHandlersElement(Element bpelFaultHandlersElement) {
        this.bpelFaultHandlersElement = bpelFaultHandlersElement;
    }

    /**
     * Returns all files this bBuildPlan has imported
     *
     * @return a List of File
     */
    public Set<Path> getImportedFiles() {
        return this.importedFiles;
    }

    /**
     * Sets the imported files of this BuildPlan
     *
     * @param files a List of File
     */
    public void setImportedFiles(final Set<Path> files) {
        this.importedFiles = files;
    }

    /**
     * Adds a file to the imported files of this BuildPlan
     *
     * @param file the File to add as imported file
     * @return true iff adding was successful
     */
    public boolean addImportedFile(final Path file) {
        return this.importedFiles.add(file);
    }

    /**
     * Returns the TemplateBuildPlans this BuildPlan contains
     *
     * @return a List of TemplateBuildPlan
     */
    public List<BPELScope> getTemplateBuildPlans() {
        return this.templateBuildPlans;
    }

    public BPELScope getTemplateBuildPlan(TNodeTemplate nodeTemplate) {
        for (BPELScope scope : this.getTemplateBuildPlans()) {
            if (scope.getNodeTemplate() != null && scope.getNodeTemplate().equals(nodeTemplate)) {
                return scope;
            }
        }
        return null;
    }

    public BPELScope getTemplateBuildPlan(TRelationshipTemplate relationshipTemplate) {
        for (BPELScope scope : this.getTemplateBuildPlans()) {
            if (scope.getRelationshipTemplate() != null
                && scope.getRelationshipTemplate().equals(relationshipTemplate)) {
                return scope;
            }
        }
        return null;
    }

    /**
     * Adds a TemplateBuildPlan to this BuildPlan
     *
     * @param template a TemplateBuildPlan to add
     * @return true iff adding was successful
     */
    public boolean addTemplateBuildPlan(final BPELScope template) {
        return this.templateBuildPlans.add(template);
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
     * @param bpelMainFlowLinksElement a DOM Element
     */
    public void setBpelMainFlowLinksElement(final Element bpelMainFlowLinksElement) {
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
     * @param bpelProcessDocument a DOM Document
     */
    public void setBpelDocument(final Document bpelProcessDocument) {
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
     * @param bpelProcessElement a DOM Element
     */
    public void setBpelProcessElement(final Element bpelProcessElement) {
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
     * @param bpelExtensionsElement a DOM Element
     */
    public void setBpelExtensionsElement(final Element bpelExtensionsElement) {
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
     * Sets the BPEL imports of this BuildPlan
     *
     * @param bpelImportElements a List of DOM Element
     */
    public void setBpelImportElements(final List<Element> bpelImportElements) {
        this.bpelImportElements = bpelImportElements;
    }

    /**
     * Adds a import element to this BuildPlan
     *
     * @param bpelImportsElement a DOM Element
     * @return true iff adding was successful
     */
    public boolean addBpelImportElement(final Element bpelImportsElement) {
        return this.bpelImportElements.add(bpelImportsElement);
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
     * @param bpelPartnerLinksElement a DOM Element
     */
    public void setBpelPartnerLinksElement(final Element bpelPartnerLinksElement) {
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
     * @param bpelProcessVariablesElement a DOM Element
     */
    public void setBpelProcessVariablesElement(final Element bpelProcessVariablesElement) {
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
     * @param bpelMainSequenceElement a DOM Element
     */
    public void setBpelMainSequenceElement(final Element bpelMainSequenceElement) {
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
     * @param bpelMainSequenceReceiveElement a DOM Element
     */
    public void setBpelMainSequenceReceiveElement(final Element bpelMainSequenceReceiveElement) {
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
     * @param bpelMainSequenceCallbackInvokeElement a DOM Element
     */
    public void setBpelMainSequenceCallbackInvokeElement(final Element bpelMainSequenceCallbackInvokeElement) {
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
     * @param bpelMainFlowElement a DOM Element
     */
    public void setBpelMainFlowElement(final Element bpelMainFlowElement) {
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
     * @param processWsdl a GenericWsdlWrapper
     */
    public void setProcessWsdl(final GenericWsdlWrapper processWsdl) {
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
     * @param bpelMainSequencePropertyAssignElement a DOM Element
     */
    public void setBpelMainSequencePropertyAssignElement(final Element bpelMainSequencePropertyAssignElement) {
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
     * @param deploymentDeskriptor a JAXB Deploy Object
     */
    public void setDeploymentDeskriptor(final Deploy deploymentDeskriptor) {
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
     * @param bpelMainSequenceOutputAssignElement a DOM Element
     */
    public void setBpelMainSequenceOutputAssignElement(final Element bpelMainSequenceOutputAssignElement) {
        this.bpelMainSequenceOutputAssignElement = bpelMainSequenceOutputAssignElement;
    }

    public void setAbstract2BPELMapping(final Map<AbstractActivity, BPELScope> abstract2bpelMap) {
        this.abstract2bpelMap = abstract2bpelMap;
    }

    public Map<AbstractActivity, BPELScope> getAbstract2BPEL() {
        return this.abstract2bpelMap;
    }

    /**
     * <p>
     * Defines which variables the model allows to define
     * <p>
     * Copyright 2013 IAAS University of Stuttgart <br>
     * <br>
     *
     * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
     */
    public enum VariableType {
        MESSAGE, TYPE, ELEMENT
    }

    /**
     * <p>
     * Defines which imports the model allows to define
     * <p>
     * Copyright 2013 IAAS University of Stuttgart <br>
     * <br>
     *
     * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
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
}
