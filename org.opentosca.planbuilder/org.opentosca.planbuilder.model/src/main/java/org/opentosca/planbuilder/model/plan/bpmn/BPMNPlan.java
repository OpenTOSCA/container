package org.opentosca.planbuilder.model.plan.bpmn;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TServiceTemplate;

import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.planbuilder.model.plan.AbstractActivity;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELScope;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class BPMNPlan extends AbstractPlan{
    public static final String bpmnNamespace = "http://www.omg.org/spec/BPMN/20100524/MODEL";
    private String toscaInterfaceName = null;
    private String toscaOperationName = null;
    // imported files of the whole buildplan, to keep track for export
    private Set<Path> importedFiles;
    // xml document

    // bpmn -> process = main sequence, subprozess = process element
    // process -> 'main' sequence / flow
    private Document bpmnProcessDocument;
    private Element bpmnDefinitionElement;
    private Element bpmnProcessElement;
    private Element bpmnMainSequenceElement;
    private Element bpmnStartEvent;
    private Element bpmnEndEvent;

    private String csarName = null;
    private List<BPMNScope> templateBuildPlans = new ArrayList<>();



    public BPMNPlan(String id, PlanType type, TDefinitions definitions, TServiceTemplate serviceTemplate, Collection<AbstractActivity> activities, Collection<AbstractPlan.Link> links) {
        super(id, type, definitions, serviceTemplate, activities, links);;
    }

    public void setBpmnDocument(final Document bpmnProcessDocument) {
        this.bpmnProcessDocument = bpmnProcessDocument;
    }

    public Document getBpmnDocument() {
        return this.bpmnProcessDocument;
    }

    public void setBpmnProcessElement(final Element bpmnProcessElement) {
        this.bpmnProcessElement = bpmnProcessElement;
    }

    public Element getBpmnProcessElement() {
        return this.bpmnProcessElement;
    }

    public void setBpmnDefinitionElement(final Element bpmnDefinitionElement) {
        this.bpmnDefinitionElement = bpmnDefinitionElement;
    }

    public Element getBpmnDefinitionElement() {
        return this.bpmnDefinitionElement;
    }

    public void setBpmnStartEvent(final Element bpmnStartEvent) {
        this.bpmnStartEvent = bpmnStartEvent;
    }

    public Element getBpmnEndEvent() {
        return this.bpmnEndEvent;
    }

    public void setBpmnEndEvent(final Element bpmnEndEvent) {
        this.bpmnEndEvent = bpmnEndEvent;
    }

    public Element getBpmnStartEvent() {
        return this.bpmnStartEvent;
    }


    public Element getBpmnMainSequenceElement() {
        return this.bpmnMainSequenceElement;
    }

    /**
     * Sets the main BPEL Sequence element of this BuildPlan
     *
     * @param bpmnMainSequenceElement a DOM Element
     */

    public void setBpmnMainSequenceElement(final Element bpmnMainSequenceElement) {
        this.bpmnMainSequenceElement = bpmnMainSequenceElement;
    }

    public String getTOSCAInterfaceName() {
        if (this.toscaInterfaceName != null) {
            return this.toscaInterfaceName;
        } else {
            return this.bpmnProcessElement.getAttribute("name");
        }
    }

    public void setTOSCAInterfaceName(String name) {
        this.toscaOperationName = name;
    }

    public String getTOSCAOperationName() {
        if (this.toscaOperationName != null) {
            return this.toscaOperationName;
        } else{
            return null;
        }
    }

    public void setTOSCAOperationname(String initiate) {
    }

    public boolean addTemplateBuildPlan(final BPMNScope template) {
        return this.templateBuildPlans.add(template);
    }
    public void setCsarName(final String csarName) {
        this.csarName = csarName;
    }
    public String getCsarName()  {
        return csarName;
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
}
