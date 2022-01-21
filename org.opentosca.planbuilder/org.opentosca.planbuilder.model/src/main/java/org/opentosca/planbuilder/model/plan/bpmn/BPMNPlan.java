package org.opentosca.planbuilder.model.plan.bpmn;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TServiceTemplate;

import org.opentosca.container.core.next.model.PlanLanguage;
import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.planbuilder.model.plan.AbstractActivity;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELScope;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.w3c.dom.Node;

public class BPMNPlan extends AbstractPlan {
    public static final String bpmnNamespace = "http://www.omg.org/spec/BPMN/20100524/MODEL";
    private String toscaInterfaceName = null;
    private String toscaOperationName = null;
    // imported files of the whole buildplan, to keep track for export
    private Set<Path> importedFiles;
    // xml document

    // bpmn -> process = main sequence, subprozess = process element
    // process -> 'main' sequence / flow
    private Document bpmnProcessDocument;
    private ArrayList<String> bpmnScript;
    private Element bpmnDefinitionElement;
    private Element bpmnProcessElement;
    private Element bpmnDiagramElement;
    private Element bpmnStartEvent;
    private Element bpmnEndEvent;

    private Map<AbstractActivity, BPMNScope> abstract2bpmnMap;

    // the localNames inside the input and output message
    // TODO: be reviewed
    private final List<String> inputMessageLocalNames = new ArrayList<>();
    private final List<String> outputMessageLocalNames = new ArrayList<>();

    // variables associated with the bpmn xml document itself
    private List<Element> bpmnImportElements;

    private String csarName = null;

    public List<BPMNScope> getTemplateBuildPlans() {
        return templateBuildPlans;
    }

    public void setTemplateBuildPlans(List<BPMNScope> templateBuildPlans) {
        this.templateBuildPlans = templateBuildPlans;
    }

    // TODO: rename, current name is misleading
    private List<BPMNScope> templateBuildPlans = new ArrayList<>();

    public BPMNPlan(String id, PlanType type, TDefinitions definitions, TServiceTemplate serviceTemplate, Collection<AbstractActivity> activities, Collection<AbstractPlan.Link> links) {
        super(id, type, definitions, serviceTemplate, activities, links);
        this.setLanguage(PlanLanguage.BPMN);
        // TODO: is it necessary to have imported files and elements
        // this.setImportedFiles(new HashSet<>());
        this.setBpmnImportElements(new ArrayList<>());
        this.setTemplateBuildPlans(new ArrayList<>());
    }

    public void setBpmnDocument(final Document bpmnProcessDocument) {
        this.bpmnProcessDocument = bpmnProcessDocument;
    }

    public Document getBpmnDocument() {
        return this.bpmnProcessDocument;
    }

    public ArrayList<String> getBpmnScripts() {
        return this.bpmnScript;
    }

    public void setBpmnScript(final ArrayList<String> bpmnScript) {
        this.bpmnScript = bpmnScript;
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

    public Element getBpmnDiagramElement() {
        return bpmnDiagramElement;
    }

    public void setBpmnDiagramElement(Element bpmnDiagramElement) {
        this.bpmnDiagramElement = bpmnDiagramElement;
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
        } else {
            return null;
        }
    }

    public void setTOSCAOperationname(String initiate) {
    }

    public void setAbstract2BPMNMapping(final Map<AbstractActivity, BPMNScope> abstract2bpmnMap) {
        this.abstract2bpmnMap = abstract2bpmnMap;
    }

    public Map<AbstractActivity, BPMNScope> getAbstract2BPMN() {
        return this.abstract2bpmnMap;
    }

    public boolean addTemplateBuildPlan(final BPMNScope template) {
        return this.templateBuildPlans.add(template);
    }

    public void setCsarName(final String csarName) {
        this.csarName = csarName;
    }

    public String getCsarName() {
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

    public List<Element> getBpmnImportElements() {
        return bpmnImportElements;
    }

    public void setBpmnImportElements(List<Element> bpmnImportElements) {
        this.bpmnImportElements = bpmnImportElements;
    }

    public List<String> getInputMessageLocalNames() {
        return this.inputMessageLocalNames;
    }

    public List<String> getOutputMessageLocalNames() {
        return this.outputMessageLocalNames;
    }
}
