package org.opentosca.planbuilder.core.bpel.helpers;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.core.bpel.handlers.BPELPlanHandler;
import org.opentosca.planbuilder.core.bpel.handlers.BPELScopeHandler;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELScopeActivity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * <p>
 * This class is used to finalize BPEL BuildPlans. For example when BPEL Scopes and their Sequences
 * don't have any sub-elements they must be filled with empty elements, otherwise the plan isn't
 * valid to the specification and a BPEL Engine won't allow the process to be deployed.
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class BPELFinalizer {

    private final static Logger LOG = LoggerFactory.getLogger(BPELFinalizer.class);
    private DocumentBuilderFactory docFactory;
    private DocumentBuilder docBuilder;
    private BPELPlanHandler buildPlanHandler;

    private BPELScopeHandler scopeHandler;

    public BPELFinalizer() {
        try {
            this.docFactory = DocumentBuilderFactory.newInstance();
            this.docFactory.setNamespaceAware(true);
            this.docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            this.buildPlanHandler = new BPELPlanHandler();
            this.scopeHandler = new BPELScopeHandler();
        } catch (final ParserConfigurationException e) {
            BPELFinalizer.LOG.error("Initializing factories and handlers failed", e);
        }
    }

    private List<BPELScopeActivity> calcTopologicalOrdering(final List<BPELScopeActivity> templateBuildPlans) {
        // will contain the order at the end
        final List<BPELScopeActivity> topologicalOrder = new ArrayList<>();

        // init marks
        final Map<BPELScopeActivity, TopologicalSortMarking> markings = new HashMap<>();

        for (final BPELScopeActivity template : templateBuildPlans) {
            markings.put(template, new TopologicalSortMarking());
        }

        while (this.hasUnmarkedNode(markings)) {
            final BPELScopeActivity templateBuildPlan = this.getUnmarkedNode(markings);
            this.visitTopologicalOrdering(templateBuildPlan, markings, topologicalOrder);
        }

        return topologicalOrder;
    }

    /**
     * Finalizes the given BuildPlan. Finalizing here means, that possible invalid parts of the plan are
     * made vaid against the specification
     *
     * @param buildPlan the BuildPlan to finalize
     */
    public void finalize(final BPELPlan buildPlan) {

        // initialize output message
        final List<String> localNames = buildPlan.getWsdl().getOuputMessageLocalNames();
        final Document doc = buildPlan.getBpelDocument();

        // create bpel elements
        final Element copy = doc.createElementNS("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "copy");
        final Element from = doc.createElementNS("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "from");
        final Element literal = doc.createElementNS("http://docs.oasis-open.org/wsbpel/2.0/process/executable",
            "literal");
        final Element to = doc.createElementNS("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "to");
        // create literal value
        final String tns = buildPlan.getWsdl().getTargetNamespace();
        final String responseMessageLocalName = buildPlan.getWsdl().getResponseMessageLocalName();
        final Element literalElement = doc.createElementNS(tns, responseMessageLocalName);
        literalElement.setPrefix("tns");
        literalElement.setAttribute("xmlns:tns", tns);

        // set to element
        to.setAttribute("variable", "output");
        to.setAttribute("part", "payload");

        // set everything together
        from.appendChild(literal);
        copy.appendChild(from);
        copy.appendChild(to);
        literal.appendChild(literalElement);

        if (localNames.size() != 0) {
            for (final String localName : localNames) {
                LOG.debug("Adding localName \"" + localName + "\" to literal assign for buildplan output");
                final Element childElement = doc.createElementNS(tns, localName);
                childElement.setTextContent("tns:" + localName);
                childElement.setPrefix("tns");
                literalElement.appendChild(childElement);
            }

        }

        buildPlan.getBpelMainSequencePropertyAssignElement().appendChild(copy);

        // set reply to address with ws-addressing
        try {
            Node addressingCopy = this.generateWSAddressingOutputAssign();
            addressingCopy = buildPlan.getBpelDocument().importNode(addressingCopy, true);
            buildPlan.getBpelMainSequenceOutputAssignElement().appendChild(addressingCopy);
        } catch (final SAXException e) {
            BPELFinalizer.LOG.error("Generating BPEL Copy element to enable callback with WS-Addressing failed", e);
        } catch (final IOException e) {
            BPELFinalizer.LOG.error("Generating BPEL Copy element to enable callback with WS-Addressing failed", e);
        }

        // check the extensions element
        final Element extensions = buildPlan.getBpelExtensionsElement();
        if (extensions.getChildNodes().getLength() == 0) {
            final Node parent = extensions.getParentNode();
            parent.removeChild(extensions);
            buildPlan.setBpelExtensionsElement(null);
        }

        this.makeSequential(buildPlan);

        for (final BPELScopeActivity templateBuildPlan : buildPlan.getTemplateBuildPlans()) {
            // check if any phase of this templatebuildplan has no child
            // elements, if it's empty, add an empty activity
            final Element prePhaseElement = templateBuildPlan.getBpelSequencePrePhaseElement();
            if (prePhaseElement.getChildNodes().getLength() == 0) {
                final Element emptyElement = buildPlan.getBpelDocument().createElementNS(BPELPlan.bpelNamespace,
                    "empty");
                prePhaseElement.appendChild(emptyElement);
            }

            final Element provPhaseElement = templateBuildPlan.getBpelSequenceProvisioningPhaseElement();
            if (provPhaseElement.getChildNodes().getLength() == 0) {
                final Element emptyElement = buildPlan.getBpelDocument().createElementNS(BPELPlan.bpelNamespace,
                    "empty");
                provPhaseElement.appendChild(emptyElement);
            }

            final Element postPhaseElement = templateBuildPlan.getBpelSequencePostPhaseElement();
            if (postPhaseElement.getChildNodes().getLength() == 0) {
                final Element emptyElement = buildPlan.getBpelDocument().createElementNS(BPELPlan.bpelNamespace,
                    "empty");
                postPhaseElement.appendChild(emptyElement);
            }

            // check if sources, targets, variables or partnerlinks is empty, if
            // yes remove each of them
            final Element targets = templateBuildPlan.getBpelTargetsElement();
            if (targets.getChildNodes().getLength() == 0) {
                final Node parent = targets.getParentNode();
                parent.removeChild(targets);
                templateBuildPlan.setBpelTargetsElement(null);
            } else {
                // add join conditions, where all templates which are target of
                // a
                // edge should be used in a conjuction
                final NodeList targetsChildren = targets.getChildNodes();
                final List<String> targetLinkNames = new ArrayList<>();
                for (int index = 0; index < targetsChildren.getLength(); index++) {
                    final Node targetNode = targetsChildren.item(index);
                    if (targetNode.getLocalName().equals("target")) {
                        targetLinkNames.add(targetNode.getAttributes().getNamedItem("linkName").getTextContent());
                    }
                }
                String condition = "";
                for (int index = 0; index < targetLinkNames.size(); index++) {
                    if (index == 0) {
                        condition += "$" + targetLinkNames.get(index);
                    } else {
                        condition += " and $" + targetLinkNames.get(index);
                    }
                }
                final Element joinCondition = buildPlan.getBpelDocument().createElementNS(
                    "http://docs.oasis-open.org/wsbpel/2.0/process/executable", "joinCondition");
                joinCondition.setTextContent(condition);
                targets.insertBefore(joinCondition, targets.getFirstChild());
            }

            final Element sources = templateBuildPlan.getBpelSourcesElement();
            if (sources.getChildNodes().getLength() == 0) {
                final Node parent = sources.getParentNode();
                parent.removeChild(sources);
                templateBuildPlan.setBpelSourcesElement(null);
            }

            final Element correlationSets = templateBuildPlan.getBpelCorrelationSets();
            if (correlationSets.getChildNodes().getLength() == 0) {
                final Node parent = correlationSets.getParentNode();
                parent.removeChild(correlationSets);
                templateBuildPlan.setBpelCorrelationSets(null);
            }

            final Element variables = templateBuildPlan.getBpelVariablesElement();
            if (variables.getChildNodes().getLength() == 0) {
                final Node parent = variables.getParentNode();
                parent.removeChild(variables);
                templateBuildPlan.setBpelVariablesElement(null);
            }

            final Element partnerlinks = templateBuildPlan.getBpelPartnerLinksElement();
            if (partnerlinks.getChildNodes().getLength() == 0) {
                final Node parent = partnerlinks.getParentNode();
                parent.removeChild(partnerlinks);
                templateBuildPlan.setBpelPartnerLinks(null);
            }
        }
    }

    /**
     * Generates a BPEL copy element for the output message of a BuildPlan, which sets the callback with
     * WS-Addressing Headers
     *
     * @return a DOM Node containing a complete BPEL Copy Element
     * @throws SAXException if parsing the internal String fails
     * @throws IOException if parsing the internal String fails
     */
    private Node generateWSAddressingOutputAssign() throws SAXException, IOException {
        final String copyString = "<bpel:copy xmlns:bpel=\"http://docs.oasis-open.org/wsbpel/2.0/process/executable\"><bpel:from variable=\"input\" header=\"ReplyTo\"><bpel:query queryLanguage=\"urn:oasis:names:tc:wsbpel:2.0:sublang:xpath1.0\"><![CDATA[string(/*[local-name()='Address'])]]></bpel:query></bpel:from><bpel:to partnerLink=\"client\" endpointReference=\"partnerRole\"/></bpel:copy>";
        /*
         * <from variable="BPELVariableName" header="NCName"?> <query queryLanguage="anyURI"?>? queryContent
         * </query> </from>
         */
        /*
         * <to partnerLink="mainPartnerLink" endpointReference="partnerRole"/>
         */
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(copyString));
        final Document doc = this.docBuilder.parse(is);
        return doc.getFirstChild();
    }

    private BPELScopeActivity getUnmarkedNode(final Map<BPELScopeActivity, TopologicalSortMarking> markings) {
        for (final BPELScopeActivity plan : markings.keySet()) {
            if (markings.get(plan).permMark == false & markings.get(plan).tempMark == false) {
                return plan;
            }
        }
        return null;
    }

    private boolean hasUnmarkedNode(final Map<BPELScopeActivity, TopologicalSortMarking> markings) {
        for (final TopologicalSortMarking marking : markings.values()) {
            if (marking.permMark == false & marking.tempMark == false) {
                return true;
            }
        }
        return false;
    }

    /**
     * Transforms the Scopes inside the Flow Element of the given buildPlan, so that the overall
     * provisioning is executed sequentially <b>Info:</b> This method assumes that the given BuildPlan
     * contains a single sink inside the flow
     *
     * @param buildPlan the BuildPlan to transform to sequential provisioning
     */
    public void makeSequential(final BPELPlan buildPlan) {
        BPELFinalizer.LOG.debug("Starting to transform BuildPlan {} to sequential provsioning",
            buildPlan.getBpelProcessElement().getAttribute("name"));
        final List<BPELScopeActivity> templateBuildPlans = buildPlan.getTemplateBuildPlans();

        final List<BPELScopeActivity> sequentialOrder = this.calcTopologicalOrdering(templateBuildPlans);

        Collections.reverse(sequentialOrder);

        for (final BPELScopeActivity template : sequentialOrder) {
            BPELFinalizer.LOG.debug("Seq order: " + template.getBpelScopeElement().getAttribute("name"));
        }

        final List<String> links = this.buildPlanHandler.getAllLinks(buildPlan);
        for (final String link : links) {
            this.buildPlanHandler.removeLink(link, buildPlan);
        }

        for (final BPELScopeActivity templateBuildPlan : templateBuildPlans) {
            this.scopeHandler.removeSources(templateBuildPlan);
            this.scopeHandler.removeTargets(templateBuildPlan);
        }

        final Iterator<BPELScopeActivity> iter = sequentialOrder.iterator();
        if (iter.hasNext()) {
            BPELScopeActivity target = iter.next();
            BPELFinalizer.LOG.debug("Beginning connecting with " + target.getBpelScopeElement().getAttribute("name"));
            int counter = 0;
            while (iter.hasNext()) {
                final BPELScopeActivity source = iter.next();
                BPELFinalizer.LOG.debug("Connecting source " + source.getBpelScopeElement().getAttribute("name")
                    + " with target " + target.getBpelScopeElement().getAttribute("name"));
                this.buildPlanHandler.addLink("seqEdge" + counter, buildPlan);
                this.scopeHandler.connect(source, target, "seqEdge" + counter);
                counter++;
                target = source;
            }
        }

    }

    private void visitTopologicalOrdering(final BPELScopeActivity templateBuildPlan,
                    final Map<BPELScopeActivity, TopologicalSortMarking> markings,
                    final List<BPELScopeActivity> topologicalOrder) {

        if (markings.get(templateBuildPlan).tempMark) {
            BPELFinalizer.LOG.error("Topological order detected cycle!");
            return;
        }
        if (!markings.get(templateBuildPlan).permMark && !markings.get(templateBuildPlan).tempMark) {
            markings.get(templateBuildPlan).tempMark = true;
            for (final BPELScopeActivity successor : this.scopeHandler.getSuccessors(templateBuildPlan)) {
                this.visitTopologicalOrdering(successor, markings, topologicalOrder);
            }
            markings.get(templateBuildPlan).permMark = true;
            markings.get(templateBuildPlan).tempMark = false;
            topologicalOrder.add(0, templateBuildPlan);
        }
    }

    /**
     * <p>
     * Used to enable markings for topological sort
     * </p>
     * Copyright 2014 IAAS University of Stuttgart <br>
     * <br>
     *
     * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
     *
     */
    private class TopologicalSortMarking {

        boolean tempMark = false;
        boolean permMark = false;
    }
}
