package org.opentosca.planbuilder.helpers;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.handlers.BPELTemplateScopeHandler;
import org.opentosca.planbuilder.handlers.BuildPlanHandler;
import org.opentosca.planbuilder.handlers.TemplateBuildPlanHandler;
import org.opentosca.planbuilder.model.plan.BuildPlan;
import org.opentosca.planbuilder.model.plan.TemplateBuildPlan;
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
 * This class is used to finalize BPEL BuildPlans. For example when BPEL Scopes
 * and their Sequences don't have any sub-elements they must be filled with
 * empty elements, otherwise the plan isn't valid to the specification and a
 * BPEL Engine won't allow the process to be deployed.
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kálmán Képes - kepeskn@studi.informatik.uni-stuttgart.de
 * 
 */
public class BPELFinalizer {
	
	private final static Logger LOG = LoggerFactory.getLogger(BPELFinalizer.class);
	
	private DocumentBuilderFactory docFactory;
	private DocumentBuilder docBuilder;
	private TemplateBuildPlanHandler templateHandler = new TemplateBuildPlanHandler();
	private BuildPlanHandler buildPlanHandler;
	private BPELTemplateScopeHandler scopeHandler;
	
	
	public BPELFinalizer() {
		try {
			this.docFactory = DocumentBuilderFactory.newInstance();
			this.docFactory.setNamespaceAware(true);
			this.docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			this.buildPlanHandler = new BuildPlanHandler();
			this.scopeHandler = new BPELTemplateScopeHandler();
		} catch (ParserConfigurationException e) {
			BPELFinalizer.LOG.error("Initializing factories and handlers failed", e);
		}
	}
	
	/**
	 * Finalizes the given BuildPlan. Finalizing here means, that possible
	 * invalid parts of the plan are made vaid against the specification
	 * 
	 * @param buildPlan the BuildPlan to finalize
	 */
	public void finalize(BuildPlan buildPlan) {
		
		// initialize output message
		List<String> localNames = buildPlan.getWsdl().getOuputMessageLocalNames();
		Document doc = buildPlan.getBpelDocument();
		
		// create bpel elements
		Element copy = doc.createElementNS("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "copy");
		Element from = doc.createElementNS("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "from");
		Element literal = doc.createElementNS("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "literal");
		Element to = doc.createElementNS("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "to");
		// create literal value
		String tns = buildPlan.getWsdl().getTargetNamespace();
		String responseMessageLocalName = buildPlan.getWsdl().getResponseMessageLocalName();
		Element literalElement = doc.createElementNS(tns, responseMessageLocalName);
		
		// set to element
		to.setAttribute("variable", "output");
		to.setAttribute("part", "payload");
		
		// set everything together
		from.appendChild(literal);
		copy.appendChild(from);
		copy.appendChild(to);
		literal.appendChild(literalElement);
		
		if (localNames.size() != 0) {
			for (String localName : localNames) {
				Element childElement = doc.createElementNS(tns, localName);
				childElement.setTextContent("tns:" + localName);
				literalElement.appendChild(childElement);
			}
			
		}
		
		buildPlan.getBpelMainSequencePropertyAssignElement().appendChild(copy);
		
		// set reply to address with ws-addressing
		try {
			Node addressingCopy = this.generateWSAddressingOutputAssign();
			addressingCopy = buildPlan.getBpelDocument().importNode(addressingCopy, true);
			buildPlan.getBpelMainSequenceOutputAssignElement().appendChild(addressingCopy);
		} catch (SAXException e) {
			BPELFinalizer.LOG.error("Generating BPEL Copy element to enable callback with WS-Addressing failed", e);
		} catch (IOException e) {
			BPELFinalizer.LOG.error("Generating BPEL Copy element to enable callback with WS-Addressing failed", e);
		}
		
		// check the extensions element
		Element extensions = buildPlan.getBpelExtensionsElement();
		if (extensions.getChildNodes().getLength() == 0) {
			Node parent = extensions.getParentNode();
			parent.removeChild(extensions);
			buildPlan.setBpelExtensionsElement(null);
		}
		
		this.makeSequential(buildPlan);
		
		for (TemplateBuildPlan templateBuildPlan : buildPlan.getTemplateBuildPlans()) {
			// check if any phase of this templatebuildplan has no child
			// elements, if it's empty, add an empty activity
			Element prePhaseElement = templateBuildPlan.getBpelSequencePrePhaseElement();
			if (prePhaseElement.getChildNodes().getLength() == 0) {
				Element emptyElement = buildPlan.getBpelDocument().createElementNS(BuildPlan.bpelNamespace, "empty");
				prePhaseElement.appendChild(emptyElement);
			}
			
			Element provPhaseElement = templateBuildPlan.getBpelSequenceProvisioningPhaseElement();
			if (provPhaseElement.getChildNodes().getLength() == 0) {
				Element emptyElement = buildPlan.getBpelDocument().createElementNS(BuildPlan.bpelNamespace, "empty");
				provPhaseElement.appendChild(emptyElement);
			}
			
			Element postPhaseElement = templateBuildPlan.getBpelSequencePostPhaseElement();
			if (postPhaseElement.getChildNodes().getLength() == 0) {
				Element emptyElement = buildPlan.getBpelDocument().createElementNS(BuildPlan.bpelNamespace, "empty");
				postPhaseElement.appendChild(emptyElement);
			}
			
			// check if sources, targets, variables or partnerlinks is empty, if
			// yes remove each of them
			Element targets = templateBuildPlan.getBpelTargetsElement();
			if (targets.getChildNodes().getLength() == 0) {
				Node parent = targets.getParentNode();
				parent.removeChild(targets);
				templateBuildPlan.setBpelTargetsElement(null);
			} else {
				// add join conditions, where all templates which are target of
				// a
				// edge should be used in a conjuction
				NodeList targetsChildren = targets.getChildNodes();
				List<String> targetLinkNames = new ArrayList<String>();
				for (int index = 0; index < targetsChildren.getLength(); index++) {
					Node targetNode = targetsChildren.item(index);
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
				Element joinCondition = buildPlan.getBpelDocument().createElementNS("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "joinCondition");
				joinCondition.setTextContent(condition);
				targets.insertBefore(joinCondition, targets.getFirstChild());
			}
			
			Element sources = templateBuildPlan.getBpelSourcesElement();
			if (sources.getChildNodes().getLength() == 0) {
				Node parent = sources.getParentNode();
				parent.removeChild(sources);
				templateBuildPlan.setBpelSourcesElement(null);
			}/*
			 * else { NodeList childElements = sources.getChildNodes(); for (int
			 * index = 0; index < childElements.getLength(); index++) { Element
			 * conditionElement = doc.createElementNS(
			 * "http://docs.oasis-open.org/wsbpel/2.0/process/executable",
			 * "transitionCondition");
			 * conditionElement.setTextContent("true()");
			 * childElements.item(index).appendChild(conditionElement); } }
			 */
			
			Element correlationSets = templateBuildPlan.getBpelCorrelationSets();
			if (correlationSets.getChildNodes().getLength() == 0) {
				Node parent = correlationSets.getParentNode();
				parent.removeChild(correlationSets);
				templateBuildPlan.setBpelCorrelationSets(null);
			}
			
			Element variables = templateBuildPlan.getBpelVariablesElement();
			if (variables.getChildNodes().getLength() == 0) {
				Node parent = variables.getParentNode();
				parent.removeChild(variables);
				templateBuildPlan.setBpelVariablesElement(null);
			}
			
			Element partnerlinks = templateBuildPlan.getBpelPartnerLinksElement();
			if (partnerlinks.getChildNodes().getLength() == 0) {
				Node parent = partnerlinks.getParentNode();
				parent.removeChild(partnerlinks);
				templateBuildPlan.setBpelPartnerLinks(null);
			}
			
		}
		
	}
	
	/**
	 * Transforms the Scopes inside the Flow Element of the given buildPlan, so
	 * that the overall provisioning is executed sequentially <b>Info:</b> This
	 * method assumes that the given BuildPlan contains a single sink inside the
	 * flow
	 * 
	 * @param buildPlan the BuildPlan to transform to sequential provisioning
	 */
	public void makeSequential(BuildPlan buildPlan) {
		BPELFinalizer.LOG.info("Starting to transform BuildPlan {} to sequential provsioning", buildPlan.getBpelProcessElement().getAttribute("name"));
		List<TemplateBuildPlan> templateBuildPlans = buildPlan.getTemplateBuildPlans();
		
		List<TemplateBuildPlan> sinks = this.getSinks(templateBuildPlans);
		// we assume a single sink
		
		TemplateBuildPlan sink = sinks.get(0);
		BPELFinalizer.LOG.info("Found sink with name " + sink.getBpelScopeElement().getAttribute("name"));
		
		List<TemplateBuildPlan> sequentialOrder = this.calcSequentialOrder(sink);
		
		for (TemplateBuildPlan template : sequentialOrder) {
			BPELFinalizer.LOG.info("Seq order: " + template.getBpelScopeElement().getAttribute("name"));
		}
		
		List<String> links = this.buildPlanHandler.getAllLinks(buildPlan);
		for (String link : links) {
			this.buildPlanHandler.removeLink(link, buildPlan);
		}
		
		for (TemplateBuildPlan templateBuildPlan : templateBuildPlans) {
			this.scopeHandler.removeSources(templateBuildPlan);
			this.scopeHandler.removeTargets(templateBuildPlan);
		}
		
		Iterator<TemplateBuildPlan> iter = sequentialOrder.iterator();
		if (iter.hasNext()) {
			TemplateBuildPlan target = iter.next();
			BPELFinalizer.LOG.info("Beginning connecting with " + target.getBpelScopeElement().getAttribute("name"));
			int counter = 0;
			while (iter.hasNext()) {
				TemplateBuildPlan source = iter.next();
				BPELFinalizer.LOG.info("Connecting source " + source.getBpelScopeElement().getAttribute("name") + " with target " + target.getBpelScopeElement().getAttribute("name"));
				this.buildPlanHandler.addLink("seqEdge" + counter, buildPlan);
				this.templateHandler.connect(source, target, "seqEdge" + counter);
				counter++;
				target = source;
			}
		}
		
	}
	
	/**
	 * Calculates a sequential order of TemplateBuildPlans beginning from the
	 * given TemplateBuildPlan
	 * 
	 * @param sink a TemplateBuildPlan which should be a sink of a BuildPlan
	 *            Flow Element
	 * @return a List of TemplateBuildPlan which contains a sequential order for
	 *         provisioning
	 */
	private List<TemplateBuildPlan> calcSequentialOrder(TemplateBuildPlan sink) {
		List<TemplateBuildPlan> sequence = new ArrayList<TemplateBuildPlan>();
		
		List<TemplateBuildPlan> preds = this.templateHandler.getPredecessors(sink);
		BPELFinalizer.LOG.info("Sink " + sink.getBpelScopeElement().getAttribute("name") + " has following preds");
		
		for (TemplateBuildPlan pred : preds) {
			BPELFinalizer.LOG.info("Pred " + pred.getBpelScopeElement().getAttribute("name"));
		}
		
		if (preds.isEmpty()) {
			// if no predecessors are available this path is ending
			sequence.add(sink);
		} else {
			// add this node in the path
			sequence.add(sink);
			
			// calculate the paths for the predecessors
			List<List<TemplateBuildPlan>> predSeqList = new ArrayList<List<TemplateBuildPlan>>();
			for (TemplateBuildPlan pred : preds) {
				List<TemplateBuildPlan> seqOrder = this.calcSequentialOrder(pred);
				predSeqList.add(seqOrder);
			}
			
			// make an order for the predecessor path according to their length
			List<List<TemplateBuildPlan>> predOrderSeqList = new ArrayList<List<TemplateBuildPlan>>();
			while (!predSeqList.isEmpty()) {
				int longest = 0;
				int index = 0;
				for (List<TemplateBuildPlan> predSeq : predSeqList) {
					if (longest < predSeq.size()) {
						longest = predSeq.size();
						index = predSeqList.indexOf(predSeq);
					}
				}
				predOrderSeqList.add(predSeqList.remove(index));
			}
			
			// begin adding the elements of each path beginning with the longest
			for (List<TemplateBuildPlan> seqOrder : predOrderSeqList) {
				for (TemplateBuildPlan seq : seqOrder) {
					// avoid duplicates
					if (!sequence.contains(seq)) {
						sequence.add(seq);
					}
				}
			}
			
		}
		return sequence;
	}
	
	/**
	 * Returns all sinks inside the given List of TemplateBuildPlans
	 * 
	 * @param templateBuildPlans a List of connected TemplateBuildPlans (Graph)
	 * @return a List of TemplateBuildPlan which are possible sinks of the given
	 *         Graph
	 */
	private List<TemplateBuildPlan> getSinks(List<TemplateBuildPlan> templateBuildPlans) {
		List<TemplateBuildPlan> sinks = new ArrayList<TemplateBuildPlan>();
		for (TemplateBuildPlan templateBuildPlan : templateBuildPlans) {
			if (this.templateHandler.getSuccessors(templateBuildPlan).isEmpty()) {
				sinks.add(templateBuildPlan);
			}
		}
		return sinks;
	}
	
	/**
	 * Generates a BPEL copy element for the output message of a BuildPlan,
	 * which sets the callback with WS-Addressing Headers
	 * 
	 * @return a DOM Node containing a complete BPEL Copy Element
	 * @throws SAXException if parsing the internal String fails
	 * @throws IOException if parsing the internal String fails
	 */
	private Node generateWSAddressingOutputAssign() throws SAXException, IOException {
		String copyString = "<bpel:copy xmlns:bpel=\"http://docs.oasis-open.org/wsbpel/2.0/process/executable\"><bpel:from variable=\"input\" header=\"ReplyTo\"><bpel:query queryLanguage=\"urn:oasis:names:tc:wsbpel:2.0:sublang:xpath1.0\"><![CDATA[string(/*[local-name()='Address'])]]></bpel:query></bpel:from><bpel:to partnerLink=\"client\" endpointReference=\"partnerRole\"/></bpel:copy>";
		/*
		 * <from variable="BPELVariableName" header="NCName"?> <query
		 * queryLanguage="anyURI"?>? queryContent </query> </from>
		 */
		/*
		 * <to partnerLink="mainPartnerLink" endpointReference="partnerRole"/>
		 */
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(copyString));
		Document doc = this.docBuilder.parse(is);
		return doc.getFirstChild();
	}
}
