package org.opentosca.planbuilder.core.bpmn.handlers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.apache.ode.schemas.dd._2007._03.ObjectFactory;
import org.opentosca.container.core.common.file.ResourceAccess;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.model.plan.AbstractActivity;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.NodeTemplateActivity;
import org.opentosca.planbuilder.model.plan.RelationshipTemplateActivity;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELScope;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNPlan;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import org.opentosca.planbuilder.core.bpmn.fragments.BPMNProcessFragments;
import org.xml.sax.SAXException;

public class BPMNPlanHandler {

    private final static Logger LOG = LoggerFactory.getLogger(BPMNPlanHandler.class);
    private final DocumentBuilderFactory documentBuilderFactory;
    private final DocumentBuilder documentBuilder;
    private final BPMNScopeHandler bpmnScopeHandler;
    private final BPMNProcessFragments fragmentclass;

    public BPMNPlanHandler() throws ParserConfigurationException {
        this.documentBuilderFactory = DocumentBuilderFactory.newInstance();
        this.documentBuilderFactory.setNamespaceAware(true);
        this.documentBuilder = this.documentBuilderFactory.newDocumentBuilder();
        this.bpmnScopeHandler = new BPMNScopeHandler();
        // test
        this.fragmentclass = new BPMNProcessFragments();
    }

    public BPMNPlan createEmptyBPMNPlan(final String processNamespace, final String processName,
                                        final AbstractPlan abstractPlan, final String inputOperationName) {
        BPMNPlanHandler.LOG.debug("Creating BuildPlan for ServiceTemplate {}",
            abstractPlan.getServiceTemplate().getId());

        final BPMNPlan buildPlan =
            new BPMNPlan(abstractPlan.getId(), abstractPlan.getType(), abstractPlan.getDefinitions(),
                abstractPlan.getServiceTemplate(), abstractPlan.getActivites(), abstractPlan.getLinks());
        initializeXMLElements(buildPlan);
        initializeScriptDocuments(buildPlan);

        return buildPlan;
    }

    public void initializeScriptDocuments(final BPMNPlan newBuildPlan) {
        ArrayList<String> scripts = new ArrayList<>();
        newBuildPlan.setBpmnScript(scripts);
        String script = "";
        try {
            String[] scriptNames = {"CreateServiceInstance", "CreateNodeInstance", "CreateRelationshipInstance", "DataObject", "SetProperties", "SetState"};
            for (String name : scriptNames) {
                script = fragmentclass.createScript(name);
                scripts.add(script);
            }
            newBuildPlan.setBpmnScript(scripts);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initializeXMLElements(final BPMNPlan newBuildPlan) {
        newBuildPlan.setBpmnDocument(this.documentBuilder.newDocument());
        newBuildPlan.setBpmnDefinitionElement(newBuildPlan.getBpmnDocument().createElementNS(BPMNPlan.bpmnNamespace,
            "bpmn:definitions"));
        newBuildPlan.getBpmnDocument().appendChild(newBuildPlan.getBpmnDefinitionElement());
        // declare xml schema namespace
        newBuildPlan.getBpmnDefinitionElement().setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:bpmn",
            "http://www.omg.org/spec/BPMN/20100524/MODEL");
        newBuildPlan.getBpmnDefinitionElement().setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:camunda","http://camunda.org/schema/1.0/bpmn");
        // initialize and append extensions element to process
        newBuildPlan.setBpmnProcessElement((newBuildPlan.getBpmnDocument().createElementNS(BPMNPlan.bpmnNamespace,
            "bpmn:process")));

        newBuildPlan.setBpmnDiagramElement(newBuildPlan.getBpmnDocument().createElementNS(BPMNPlan.bpmnNamespace,
            "bpmn:BPMNDiagram"));
        newBuildPlan.getBpmnProcessElement().setAttribute("id", "Process_Random");
        newBuildPlan.getBpmnProcessElement().setAttribute("isExecutable", "true");
        newBuildPlan.getBpmnDefinitionElement().appendChild(newBuildPlan.getBpmnProcessElement());
        newBuildPlan.getBpmnDefinitionElement().appendChild(newBuildPlan.getBpmnDiagramElement());
        Element planeElement = newBuildPlan.getBpmnDocument().createElement("bpmndi:BPMNPlane");
        planeElement.setAttribute("id", "Plane_Id");
        planeElement.setAttribute("bpmnElement", "Process_Id");
        newBuildPlan.getBpmnDiagramElement().appendChild(planeElement);
        // create start event from fragment -> just a test, has to be done by plugin!
        try {
            Element startEvent = (Element) newBuildPlan.getBpmnDocument().importNode((Element) fragmentclass.createBPMNStartEventAsNode("lustigerEventName", "tollerFlow"), true);
            Element endEvent = (Element) newBuildPlan.getBpmnDocument().importNode((Element) fragmentclass.createBPMNEndEventAsNode("lustigerEventName", "tollerFlow"), true);
            newBuildPlan.setBpmnStartEvent(startEvent);
            newBuildPlan.getBpmnProcessElement().appendChild(newBuildPlan.getBpmnStartEvent());
            newBuildPlan.setBpmnEndEvent(endEvent);
            newBuildPlan.getBpmnProcessElement().appendChild(newBuildPlan.getBpmnEndEvent());

        } catch (Exception e){
            BPMNPlanHandler.LOG.debug("error with fragments:", e);
        }

        // TODO: to be removed, testing purpose only
        // write the content into xml file

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(newBuildPlan.getBpmnDocument());
            StreamResult result = new StreamResult(new File("test-bpmn.xml"));
            transformer.transform(source, result);
            // Output to console for testing
            StreamResult consoleResult = new StreamResult(System.out);
            transformer.transform(source, consoleResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initializeBPMNSkeleton(final BPMNPlan plan, final Csar csar) {
        plan.setCsarName(csar.id().csarName());

        final Map<AbstractActivity, BPMNScope> abstract2bpmnMap = new HashMap<>();

        for (final AbstractActivity activity : plan.getActivites()) {
            BPMNScope newEmpty3SequenceScopeBPELActivity = null;

            if (activity instanceof NodeTemplateActivity) {
                final NodeTemplateActivity ntActivity = (NodeTemplateActivity) activity;
                newEmpty3SequenceScopeBPELActivity = this.bpmnScopeHandler.createTemplateBuildPlan(ntActivity, plan, "");
                plan.addTemplateBuildPlan(newEmpty3SequenceScopeBPELActivity);
                abstract2bpmnMap.put(ntActivity, newEmpty3SequenceScopeBPELActivity);

                //final BPMNScope newFaultHandlerScope =
                //  this.bpelScopeHandler.createTemplateBuildPlan(ntActivity, plan, "fault");
                //newEmpty3SequenceScopeBPELActivity.setBpelFaultHandlerScope(newFaultHandlerScope);

                //final BPELScope newCompensationHandlerScope =
                //  this.bpelScopeHandler.createTemplateBuildPlan(ntActivity, plan, "compensation");
                //newEmpty3SequenceScopeBPELActivity.setBpelCompensationHandlerScope(newCompensationHandlerScope);
            } else if (activity instanceof RelationshipTemplateActivity) {
                final RelationshipTemplateActivity rtActivity = (RelationshipTemplateActivity) activity;
                newEmpty3SequenceScopeBPELActivity = this.bpmnScopeHandler.createTemplateBuildPlan(rtActivity, plan, "");
                plan.addTemplateBuildPlan(newEmpty3SequenceScopeBPELActivity);
                abstract2bpmnMap.put(rtActivity, newEmpty3SequenceScopeBPELActivity);

                //final BPELScope newFaultHandlerScope =
                //  this.bpelScopeHandler.createTemplateBuildPlan(rtActivity, plan, "fault");
                //newEmpty3SequenceScopeBPELActivity.setBpelFaultHandlerScope(newFaultHandlerScope);

                //BPELScope newCompensationHandlerScope =
                //  this.bpelScopeHandler.createTemplateBuildPlan(rtActivity, plan, "compensation");
                //newEmpty3SequenceScopeBPELActivity.setBpelCompensationHandlerScope(newCompensationHandlerScope);
            } else {
                newEmpty3SequenceScopeBPELActivity = this.bpmnScopeHandler.createTemplateBuildPlan(activity, plan, "");
                plan.addTemplateBuildPlan(newEmpty3SequenceScopeBPELActivity);
                abstract2bpmnMap.put(activity, newEmpty3SequenceScopeBPELActivity);

                //final BPELScope newFaultHandlerScope =
                //  this.bpelScopeHandler.createTemplateBuildPlan(activity, plan, "fault");
                //newEmpty3SequenceScopeBPELActivity.setBpelFaultHandlerScope(newFaultHandlerScope);

                //final BPELScope newCompensationHandlerScope =
                //  this.bpelScopeHandler.createTemplateBuildPlan(activity, plan, "compensation");
                //newEmpty3SequenceScopeBPELActivity.setBpelCompensationHandlerScope(newCompensationHandlerScope);
            }
        }

        plan.setAbstract2BPMNMapping(abstract2bpmnMap);

        // connect the templates
        //initializeConnectionsAsLinkInBPELPlan(plan);
    }
}
