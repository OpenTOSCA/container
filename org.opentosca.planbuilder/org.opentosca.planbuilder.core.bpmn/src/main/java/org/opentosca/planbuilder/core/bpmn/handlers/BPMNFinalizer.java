package org.opentosca.planbuilder.core.bpmn.handlers;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.core.bpmn.fragments.BPMNDiagramFragments;
import org.opentosca.planbuilder.core.bpmn.fragments.BPMNProcessFragments;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNDiagramElement;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNPlan;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNScope;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class BPMNFinalizer {
    private final static Logger LOG = LoggerFactory.getLogger(BPMNFinalizer.class);
    private DocumentBuilderFactory docFactory;
    private DocumentBuilder docBuilder;

    private BPMNPlanHandler planHandler;
    private BPMNScopeHandler scopeHandler;
    private BPMNProcessFragments processFragments;
    private BPMNDiagramFragments diagramFragments;
    public BPMNFinalizer() {
        try {
            this.docFactory = DocumentBuilderFactory.newInstance();
            this.docFactory.setNamespaceAware(true);
            this.docBuilder = docFactory.newDocumentBuilder();
            this.planHandler = new BPMNPlanHandler();
            this.scopeHandler = new BPMNScopeHandler();
            this.processFragments = new BPMNProcessFragments();
            this.diagramFragments = new BPMNDiagramFragments();
        } catch (ParserConfigurationException e) {
            LOG.error("Initializing factories and handlers failed", e);
        }
    }

    /**
     * The method generates XML elements from all the BPMNScopes contained in
     * the current build plan and add to the document
     * @param buildPlan
     * @throws IOException
     * @throws SAXException
     */
    public void finalize(final BPMNPlan buildPlan) throws IOException, SAXException {
        LOG.info("Finalizing BPMN build Plan {}", buildPlan.getId());
        final Document doc = buildPlan.getBpmnDocument();
        List<BPMNScope> scopeList = buildPlan.getTemplateBuildPlans();

        // Create and import XML process element to document
        final Element processElement = buildPlan.getBpmnProcessElement();
        for (BPMNScope bpmnScope : scopeList) {
            Node node = processFragments.createBPMNScopeAsNode(bpmnScope);
            processElement.appendChild(doc.importNode(node, true));
        }

        // Create and import XML diagram element to document
        final Element planeElement = buildPlan.getBpmnPlaneElement();
        List<BPMNDiagramElement> diagramList = buildPlan.getDiagramElements();
        for (BPMNDiagramElement diagram : diagramList) {
            Node node = diagramFragments.createBPMNDiagramElementAsNode(diagram);
            planeElement.appendChild(doc.importNode(node, true));
        }
        LOG.info("BPMN build Plan is finalized");
    }
}
