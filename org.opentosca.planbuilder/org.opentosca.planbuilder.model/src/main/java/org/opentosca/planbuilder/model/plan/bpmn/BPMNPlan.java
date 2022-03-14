package org.opentosca.planbuilder.model.plan.bpmn;

import java.nio.file.Path;
import java.util.*;

import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;

import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.next.model.PlanLanguage;
import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.planbuilder.model.plan.AbstractActivity;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
    private Element bpmnDiagramPlaneElement;
    private Element bpmnStartEvent;
    private Element bpmnEndEvent;

    private Map<AbstractActivity, BPMNScope> abstract2bpmnMap;

    // the input and output parameters for the plan
    private final Set<String> inputParameters = new HashSet<>();
    private final Set<String> outputParameters = new HashSet<>();

    // variables associated with the bpmn xml document itself
    private List<Element> bpmnImportElements;

    private String csarName = null;
    private Csar csar = null;

    // TODO: rename, this is misleading
    private List<BPMNScope> templateBuildPlans = new ArrayList<>();
    private List<BPMNDiagramElement> diagramElements = new ArrayList<>();

    // <id : variable name for url>, MyTinyToDoDockerContainer : MyTinyToDoDockerContainer_0NodeInstanceURL
    private Map<TNodeTemplate, String> nodeTemplate2InstanceUrlVariableName = new HashMap<>();
    // con_HostedOn_0: con_HostedOn_0_RelationshipInstanceURL
    private Map<TRelationshipTemplate, String> relationTemplate2InstanceUrlVariableName = new HashMap<>();

    // for building diagram
    private BPMNScope bpmnStartEventElement;



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
        this.toscaInterfaceName = name;
    }

    public String getTOSCAOperationName() {
        if (this.toscaOperationName != null) {
            return this.toscaOperationName;
        } else {
            return null;
        }
    }

    public void setTOSCAOperationName(String name) {
        this.toscaOperationName = name;
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

    public boolean addDiagramElement(final BPMNDiagramElement diagramElement) {
        return this.diagramElements.add(diagramElement);
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

    public List<BPMNScope> getTemplateBuildPlans() {
        return templateBuildPlans;
    }

    public void setTemplateBuildPlans(List<BPMNScope> templateBuildPlans) {
        this.templateBuildPlans = templateBuildPlans;
    }

    public List<BPMNDiagramElement> getDiagramElements() {
        return diagramElements;
    }

    public void setDiagramElements(List<BPMNDiagramElement> diagramElements) {
        this.diagramElements = diagramElements;
    }

    public BPMNScope getBpmnStartEventElement() {
        return bpmnStartEventElement;
    }

    public void setBpmnStartEventElement(BPMNScope bpmnStartEventElement) {
        this.bpmnStartEventElement = bpmnStartEventElement;
    }

    public Csar getCsar() {
        return csar;
    }

    public boolean addInstanceUrlVariableNameToNodeTemplate(TNodeTemplate nodeTemplate, String variableName) {
        if (nodeTemplate2InstanceUrlVariableName.containsKey(nodeTemplate)) {
            return false;
        }
        nodeTemplate2InstanceUrlVariableName.put(nodeTemplate, variableName);
        return true;
    }

    public boolean addInstanceUrlVariableNameToRelationshipTemplateUrl(TRelationshipTemplate relationshipTemplate, String variableName) {
        if (relationTemplate2InstanceUrlVariableName.containsKey(variableName)) {
            return false;
        }
        relationTemplate2InstanceUrlVariableName.put(relationshipTemplate, variableName);
        return true;
    }

    public String getNodeTemplateInstanceUrlVariableName(TNodeTemplate nodeTemplate) {
        return nodeTemplate2InstanceUrlVariableName.get(nodeTemplate);
    }

    public String getRelationshipTemplateInstanceUrlVariableName(TRelationshipTemplate relationshipTemplate) {
        return relationTemplate2InstanceUrlVariableName.get(relationshipTemplate);
    }

    public Element getBpmnPlaneElement() {
        return bpmnDiagramPlaneElement;
    }

    public void setBpmnPlaneElement(Element bpmnDiagramPlaneElement) {
        this.bpmnDiagramPlaneElement = bpmnDiagramPlaneElement;
    }

    public void setCsar(Csar csar) {
        this.csar = csar;
    }

    public Set<String> getInputParameters() {
        return inputParameters;
    }

    public Set<String> getOutputParameters() {
        return outputParameters;
    }
}
