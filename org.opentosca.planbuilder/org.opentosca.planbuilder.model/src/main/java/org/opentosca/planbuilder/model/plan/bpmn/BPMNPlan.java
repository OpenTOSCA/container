package org.opentosca.planbuilder.model.plan.bpmn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TServiceTemplate;

import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.planbuilder.model.plan.AbstractActivity;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELScope;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class BPMNPlan extends AbstractPlan{
    public static final String bpmnNamespace = "http://docs.oasis-open.org/BPMN/2.0";
    private String toscaInterfaceName = null;
    private String toscaOperationName = null;
    // xml document
    private Document bpmnProcessDocument;
    private Element bpmnDefinitionElement;
    private Element bpmnProcessElement;
    private Element bpmnMainSequenceElement;
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

    public void setTOSCAInterfaceName(String name) {
        this.toscaOperationName = name;
    }

    public void setTOSCAOperationname(String initiate) {
    }

    public boolean addTemplateBuildPlan(final BPMNScope template) {
        return this.templateBuildPlans.add(template);
    }
    public void setCsarName(final String csarName) {
        this.csarName = csarName;
    }
}
