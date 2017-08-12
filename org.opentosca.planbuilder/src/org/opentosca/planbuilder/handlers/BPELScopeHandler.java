package org.opentosca.planbuilder.handlers;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.plan.bpel.TemplateBuildPlan;
import org.opentosca.planbuilder.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <p>
 * This class is part of the Facade to handle actions on BuildPlans. This
 * particular class handle XML related operations on TemplateBuildPlans
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 * 
 */
public class BPELScopeHandler {

	private final static Logger LOG = LoggerFactory.getLogger(BPELScopeHandler.class);

	/**
	 * Initializes XML Elements of the given TemplateBuildPlan and connects it
	 * to the given BuildPlan
	 * 
	 * @param newTemplateBuildPlan
	 *            the TemplateBuildPlan to initialize
	 * @param buildPlan
	 *            the BuildPlan to connect to TemplateBuildPlan to
	 */
	public void initializeXMLElements(TemplateBuildPlan newTemplateBuildPlan, BPELPlan buildPlan) {
		// set the build plan of the new template buildplan
		newTemplateBuildPlan.setBuildPlan(buildPlan);

		newTemplateBuildPlan.getBuildPlan();
		// initialize bpelScopeElement and append to flow
		newTemplateBuildPlan.setBpelScopeElement(
				newTemplateBuildPlan.getBpelDocument().createElementNS(BPELPlan.bpelNamespace, "scope"));
		// info: append to flow element of the buildplan
		newTemplateBuildPlan.getBuildPlan().getBpelMainFlowElement()
				.appendChild(newTemplateBuildPlan.getBpelScopeElement());

		// initialize bpelTargetsElement and append to scope
		newTemplateBuildPlan.setBpelTargetsElement(
				newTemplateBuildPlan.getBpelDocument().createElementNS(BPELPlan.bpelNamespace, "targets"));
		newTemplateBuildPlan.getBpelScopeElement().appendChild(newTemplateBuildPlan.getBpelTargetsElement());

		// initialize bpelSourcesElement and append to scope
		newTemplateBuildPlan.setBpelSourcesElement(
				newTemplateBuildPlan.getBpelDocument().createElementNS(BPELPlan.bpelNamespace, "sources"));
		newTemplateBuildPlan.getBpelScopeElement().appendChild(newTemplateBuildPlan.getBpelSourcesElement());

		// init bpelPartnerLinksElement append to scope
		newTemplateBuildPlan.setBpelPartnerLinks(
				newTemplateBuildPlan.getBpelDocument().createElementNS(BPELPlan.bpelNamespace, "partnerLinks"));
		newTemplateBuildPlan.getBpelScopeElement().appendChild(newTemplateBuildPlan.getBpelPartnerLinksElement());

		// init bpelVariablesElement and append to scope
		newTemplateBuildPlan.setBpelVariablesElement(
				newTemplateBuildPlan.getBpelDocument().createElementNS(BPELPlan.bpelNamespace, "variables"));
		newTemplateBuildPlan.getBpelScopeElement().appendChild(newTemplateBuildPlan.getBpelVariablesElement());

		// init bpelCorrelationSetsElement and append to scope
		newTemplateBuildPlan.setBpelCorrelationSets(
				newTemplateBuildPlan.getBpelDocument().createElementNS(BPELPlan.bpelNamespace, "correlationSets"));
		newTemplateBuildPlan.getBpelScopeElement().appendChild(newTemplateBuildPlan.getBpelCorrelationSets());

		// initialize bpelMainSequenceElement and append to scope
		newTemplateBuildPlan.setBpelMainSequenceElement(
				newTemplateBuildPlan.getBpelDocument().createElementNS(BPELPlan.bpelNamespace, "sequence"));
		newTemplateBuildPlan.getBpelScopeElement().appendChild(newTemplateBuildPlan.getBpelMainSequenceElement());

		// initialize bpelSequencePrePhaseElement and append to mainsequence
		newTemplateBuildPlan.setBpelSequencePrePhaseElement(
				newTemplateBuildPlan.getBpelDocument().createElementNS(BPELPlan.bpelNamespace, "sequence"));
		newTemplateBuildPlan.getBpelMainSequenceElement()
				.appendChild(newTemplateBuildPlan.getBpelSequencePrePhaseElement());

		// initialize bpelSequenceProvisioningPhaseElement and append to
		// mainsequence
		newTemplateBuildPlan.setBpelSequenceProvisioningPhaseElement(
				newTemplateBuildPlan.getBpelDocument().createElementNS(BPELPlan.bpelNamespace, "sequence"));
		newTemplateBuildPlan.getBpelMainSequenceElement()
				.appendChild(newTemplateBuildPlan.getBpelSequenceProvisioningPhaseElement());

		// initialize bpelSequencePostPhaseElement and append to scope
		newTemplateBuildPlan.setBpelSequencePostPhaseElement(
				newTemplateBuildPlan.getBpelDocument().createElementNS(BPELPlan.bpelNamespace, "sequence"));
		newTemplateBuildPlan.getBpelMainSequenceElement()
				.appendChild(newTemplateBuildPlan.getBpelSequencePostPhaseElement());

	}

	/**
	 * Sets the name of the TemplateBuildPlan
	 * 
	 * @param name
	 *            the name to set
	 * @param templateBuildPlan
	 *            the TemplateBuildPlan to set the name for
	 */
	public void setName(String name, TemplateBuildPlan templateBuildPlan) {
		BPELScopeHandler.LOG.debug("Setting name {} for TemplateBuildPlan", name);
		// set scope name
		templateBuildPlan.getBpelScopeElement().setAttribute("name", name + "_scope");
		// set main sequence name
		templateBuildPlan.getBpelMainSequenceElement().setAttribute("name", name + "_mainSequence");
		// set prephase name
		templateBuildPlan.getBpelSequencePrePhaseElement().setAttribute("name", name + "_prePhase");
		// set provisioning phase name
		templateBuildPlan.getBpelSequenceProvisioningPhaseElement().setAttribute("name", name + "_provisioningPhase");
		// set post phase name
		templateBuildPlan.getBpelSequencePostPhaseElement().setAttribute("name", name + "_postPhase");
	}

	/**
	 * Adds a partnerLink to the given TemplateBuildPlan
	 * 
	 * @param partnerLinkName
	 *            the name of the partnerLink
	 * @param partnerLinkType
	 *            the partnerLinkType which must be already set in the BuildPlan
	 *            of the TemplateBuildPlan
	 * @param myRole
	 *            the 1st role of this partnerLink
	 * @param partnerRole
	 *            the 2nd role this partnerLink
	 * @param initializePartnerRole
	 *            whether to initialize the partnerRole
	 * @param templateBuildPlan
	 *            the TemplateBuildPlan to add the partnerLink to
	 * @return true if setting partnerLink was successful, else false
	 */
	public boolean addPartnerLink(String partnerLinkName, QName partnerLinkType, String myRole, String partnerRole,
			boolean initializePartnerRole, TemplateBuildPlan templateBuildPlan) {
		BPELScopeHandler.LOG.debug(
				"Trying to add partnerLink {} with partnerLinkType {}, myRole {}, partnerRole {} and initializePartnerRole {} on TemplateBuildPlan {}",
				partnerLinkName, partnerLinkType.toString(), myRole, partnerRole, String.valueOf(initializePartnerRole),
				templateBuildPlan.getBpelScopeElement().getAttribute("name"));
		if (this.hasPartnerlink(partnerLinkName, templateBuildPlan)) {
			BPELScopeHandler.LOG.warn("Failed to add partnerLink");
			return false;
		}
		Element partnerLinksElement = templateBuildPlan.getBpelPartnerLinksElement();
		Element partnerLinkElement = templateBuildPlan.getBpelDocument().createElementNS(BPELPlan.bpelNamespace,
				"partnerLink");

		partnerLinkElement.setAttribute("name", partnerLinkName);
		partnerLinkElement.setAttribute("partnerLinkType",
				partnerLinkType.getPrefix() + ":" + partnerLinkType.getLocalPart());
		if (myRole != null) {
			partnerLinkElement.setAttribute("myRole", myRole);
		}
		if ((partnerRole != null) && !partnerRole.equals("")) {
			partnerLinkElement.setAttribute("partnerRole", partnerRole);
		}

		partnerLinkElement.setAttribute("initializePartnerRole", (initializePartnerRole) ? "yes" : "no");

		partnerLinksElement.appendChild(partnerLinkElement);
		BPELScopeHandler.LOG.debug("Adding partnerLink was successful");
		return true;
	}

	/**
	 * Adds the given correlation to the given TemplateBuildPlan
	 * 
	 * @param correlationSetName
	 *            the name of the correlationSet
	 * @param propertyName
	 *            the property the correlationSet works on
	 * @param templateBuildPlan
	 *            the TemplateBuildPlan to add the correlationSet to
	 * @return true if adding CorrelationSet was successful, else false
	 */
	public boolean addCorrelationSet(String correlationSetName, String propertyName,
			TemplateBuildPlan templateBuildPlan) {
		BPELScopeHandler.LOG.debug("Trying to add correlationSet {} with property {} to templateBuildPlan {}",
				correlationSetName, propertyName, templateBuildPlan.getBpelScopeElement().getAttribute("name"));
		if (this.hasCorrelationSet(correlationSetName, templateBuildPlan)) {
			BPELScopeHandler.LOG.warn("Failed adding correlationSet");
			return false;
		}
		Element correlationSetsElement = templateBuildPlan.getBpelCorrelationSets();
		Element correlationSetElement = templateBuildPlan.getBpelDocument().createElementNS(BPELPlan.bpelNamespace,
				"correlationSet");
		correlationSetElement.setAttribute("name", correlationSetName);
		correlationSetElement.setAttribute("properties", "tns:" + propertyName);
		correlationSetsElement.appendChild(correlationSetElement);
		BPELScopeHandler.LOG.debug("Adding correlationSet was succesful");
		return true;
	}

	/**
	 * Checks whether the given TemplateBuildPlan has the given correlationSet
	 * 
	 * @param correlationSetName
	 *            the name of the correlationSet to check for
	 * @param templateBuildPlan
	 *            the TemplateBuildPlan to check on
	 * @return true if the correlationSet is declared in the TemplateBuildPlan
	 */
	private boolean hasCorrelationSet(String correlationSetName, TemplateBuildPlan templateBuildPlan) {
		return Utils.hasChildElementWithAttribute(templateBuildPlan.getBpelCorrelationSets(), "name",
				correlationSetName);
	}

	/**
	 * Adds a link as a Source to the given TemplateBuildPlan
	 * 
	 * @param linkName
	 *            the name of the link to use
	 * @param templateBuildPlan
	 *            the TemplateBuildPlan to add the Link to
	 * @return true if adding the Link was successful, else false
	 */
	public boolean addSource(String linkName, TemplateBuildPlan templateBuildPlan) {
		BPELScopeHandler.LOG.debug("Trying to add link {} as source to TemplateBuildPlan {}", linkName,
				templateBuildPlan.getBpelScopeElement().getAttribute("name"));
		if (this.hasSource(linkName, templateBuildPlan)) {
			BPELScopeHandler.LOG.warn("Failed to add link as source");
			return false;
		}
		Element sourcesElement = templateBuildPlan.getBpelSourcesElement();
		Element sourceElement = templateBuildPlan.getBpelDocument().createElementNS(BPELPlan.bpelNamespace, "source");
		sourceElement.setAttribute("linkName", linkName);
		sourcesElement.appendChild(sourceElement);
		BPELScopeHandler.LOG.debug("Adding link as source was successful");
		return true;
	}

	/**
	 * Adds a variable to the given TemplateBuildPlan
	 * 
	 * @param name
	 *            the name of the variable
	 * @param variableType
	 *            the type of the variable
	 * @param declarationId
	 *            the QName of the XML complexType of the variable
	 * @param templateBuildPlan
	 *            the TemplateBuildPlan to add the variable to
	 * @return true if adding variable was successful, else false
	 */
	public boolean addVariable(String name, BPELPlan.VariableType variableType, QName declarationId,
			TemplateBuildPlan templateBuildPlan) {
		BPELScopeHandler.LOG.debug(
				"Trying to add variable {} with of type {} and XML Schematype {} to TemplateBuildPlan {}", name,
				variableType, declarationId.toString(), templateBuildPlan.getBpelScopeElement().getAttribute("name"));
		if (this.hasVariable(name, templateBuildPlan)) {
			BPELScopeHandler.LOG.warn("Failed adding variable");
			return false;
		}

		// fetch variables element and create variable element
		Element variablesElement = templateBuildPlan.getBpelVariablesElement();
		Element variableElement = templateBuildPlan.getBpelDocument().createElementNS(BPELPlan.bpelNamespace,
				"variable");

		// set the type and declaration id
		switch (variableType) {
		case MESSAGE:
			variableElement.setAttribute("messageType", declarationId.getPrefix() + ":" + declarationId.getLocalPart());
			break;
		case TYPE:
			variableElement.setAttribute("type", declarationId.getPrefix() + ":" + declarationId.getLocalPart());
			break;
		default:
			;
			break;
		}

		// set name
		variableElement.setAttribute("name", name);

		// append to variables element
		variablesElement.appendChild(variableElement);
		BPELScopeHandler.LOG.debug("Adding variable was successful");
		return true;
	}

	/**
	 * Adds link as a target to the given TemplateBuildPlan
	 * 
	 * @param linkName
	 *            the name of the link
	 * @param templateBuildPlan
	 *            the TemplateBuildPlan to add the link to
	 * @return true if adding link was successful, else false
	 */
	public boolean addTarget(String linkName, TemplateBuildPlan templateBuildPlan) {
		BPELScopeHandler.LOG.debug("Trying to add link {} as target to TemplateBuildPlan {}", linkName,
				templateBuildPlan.getBpelScopeElement().getAttribute("name"));
		if (this.hasTarget(linkName, templateBuildPlan)) {
			BPELScopeHandler.LOG.warn("Failed adding link as target");
			return false;
		}
		Element targetsElement = templateBuildPlan.getBpelTargetsElement();
		Element targetElement = templateBuildPlan.getBpelDocument().createElementNS(BPELPlan.bpelNamespace, "target");
		targetElement.setAttribute("linkName", linkName);
		targetsElement.appendChild(targetElement);
		BPELScopeHandler.LOG.debug("Adding link as target was successful");
		return true;
	}

	/**
	 * Checks whether the given link is a source in the given TemplateBuildPlan
	 * 
	 * @param name
	 *            the name of the link to check with
	 * @param templateBuildPlan
	 *            the TemplateBuildPlan to check on
	 * @return true if the given link is declared as source in the given
	 *         TemplateBuildPlan
	 */
	public boolean hasSource(String name, TemplateBuildPlan templateBuildPlan) {
		return Utils.hasChildElementWithAttribute(templateBuildPlan.getBpelSourcesElement(), "linkName", name);
	}

	/**
	 * Checks whether the given link is a target in the given TemplateBuildPlan
	 * 
	 * @param name
	 *            the name of the link to check with
	 * @param templateBuildPlan
	 *            the TemplateBuildPlan to check on
	 * @return true if the given link is declared as target in the given
	 *         TemplateBuildPlan
	 */
	public boolean hasTarget(String name, TemplateBuildPlan templateBuildPlan) {
		return Utils.hasChildElementWithAttribute(templateBuildPlan.getBpelTargetsElement(), "linkName", name);
	}

	/**
	 * Checks whether the given partnerLinkName is already used in the given
	 * TemplateBuildPlan
	 * 
	 * @param name
	 *            the name of the partnerLink to check with
	 * @param templateBuildPlan
	 *            the TemplateBuildPlan to check on
	 * @return true if the given TemplateBuildPlan already has a partnerLink
	 *         with the given name, else false
	 */
	private boolean hasPartnerlink(String name, TemplateBuildPlan templateBuildPlan) {
		return Utils.hasChildElementWithAttribute(templateBuildPlan.getBpelPartnerLinksElement(), "name", name);
	}

	/**
	 * Checks whether the given variableName is already used in the given
	 * TemplateBuildPlan
	 * 
	 * @param name
	 *            the name of the variable to check
	 * @param templateBuildPlan
	 *            the TemplateBuildPlan to check on
	 * @return true if the variableName is already used, else false
	 */
	private boolean hasVariable(String name, TemplateBuildPlan templateBuildPlan) {
		return Utils.hasChildElementWithAttribute(templateBuildPlan.getBpelVariablesElement(), "name", name);
	}

	/**
	 * Checks whether the given TemplateBuildPlan is for a NodeTemplate
	 * 
	 * @param template
	 *            the TemplateBuildPlan to check
	 * @return true if the given TemplateBuildPlan is for a NodeTemplate
	 */
	public boolean isNodeTemplatePlan(TemplateBuildPlan template) {
		return template.getNodeTemplate() != null;
	}

	/**
	 * Checks whether the given TemplateBuildPlan is for a RelationshipTemplate
	 * 
	 * @param template
	 *            the TemplateBuildPlan to check
	 * @return true if the given TemplateBuildPlan is for a RelationshipTemplate
	 */
	public boolean isRelationshipTemplatePlan(TemplateBuildPlan template) {
		return template.getRelationshipTemplate() != null;
	}

	/**
	 * Returns the names of the links declared in the sources element of the
	 * given TemplateBuildPlan
	 * 
	 * @param template
	 *            the TemplateBuildPlan to fetch the names from
	 * @return a List of String representing Links inside the sources element of
	 *         the given TemplateBuildPlan
	 */
	public List<String> getLinksInSources(TemplateBuildPlan template) {
		List<String> sourcesLinkNames = new ArrayList<String>();
		if (template.getBpelSourcesElement() == null) {
			return sourcesLinkNames;
		}
		if (template.getBpelSourcesElement().hasChildNodes()) {
			NodeList children = template.getBpelSourcesElement().getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				NamedNodeMap attrs = children.item(i).getAttributes();
				if (attrs.getNamedItem("linkName") != null) {
					sourcesLinkNames.add(attrs.getNamedItem("linkName").getNodeValue());
				}
			}
		}
		return sourcesLinkNames;
	}

	/**
	 * Returns the names of the links declared in the targets element of the
	 * given TemplateBuildPlan
	 * 
	 * @param template
	 *            the TemplateBuildPlan to fetch the names from
	 * @return a List of Strings representing Links inside the targets element
	 *         of the given TemplateBuildPlan
	 */
	public List<String> getLinksInTarget(TemplateBuildPlan template) {
		List<String> targetsLinkNames = new ArrayList<String>();
		if (template.getBpelTargetsElement() == null) {
			return targetsLinkNames;
		}
		if (template.getBpelTargetsElement().hasChildNodes()) {
			for (int i = 0; i < template.getBpelTargetsElement().getChildNodes().getLength(); i++) {
				if (template.getBpelTargetsElement().getChildNodes().item(i).hasAttributes()) {
					NamedNodeMap attrs = template.getBpelTargetsElement().getChildNodes().item(i).getAttributes();
					if (attrs.getNamedItem("linkName") != null) {
						targetsLinkNames.add(attrs.getNamedItem("linkName").getNodeValue());
					}
				}
			}
		}
		return targetsLinkNames;
	}

	/**
	 * Removes all links which use the given TemplateBuildPlan as source
	 * 
	 * @param template
	 *            the TemplateBuildPlan for that all source relations have to be
	 *            removed
	 */
	public void removeSources(TemplateBuildPlan template) {
		Element sources = template.getBpelSourcesElement();

		if (sources != null) {
			BPELScopeHandler.removeAllChildNodes(sources);
		}
	}

	/**
	 * Removes all links which use the given TemplateBuildPlan as source
	 * 
	 * @param template
	 *            the TemplateBuildPlan for that all target relations have to be
	 *            removed
	 */
	public void removeTargets(TemplateBuildPlan template) {
		Element targets = template.getBpelTargetsElement();
		if (targets != null) {
			BPELScopeHandler.removeAllChildNodes(targets);
		}
	}

	/**
	 * Removes all ChildNodes of he given DOM Node
	 * 
	 * @param node
	 *            the DOM Node to remove its child elements
	 */
	private static void removeAllChildNodes(Node node) {
		NodeList children = node.getChildNodes();
		while (children.getLength() > 0) {
			Node child = children.item(0);
			child.getParentNode().removeChild(child);
		}
	}

	/**
	 * Returns a List of Names of the variables defined inside the given
	 * templatePlan
	 *
	 * @param templatePlan
	 *            a templatePlan
	 * @return a List of Strings with the names of the variables defined inside
	 *         the given templatePlan
	 */
	public List<String> getVariableNames(TemplateBuildPlan templatePlan) {
		List<String> varNames = new ArrayList<String>();
		NodeList variableNodesList = templatePlan.getBpelVariablesElement().getChildNodes();

		for (int index = 0; index < variableNodesList.getLength(); index++) {
			if (variableNodesList.item(index).getNodeType() == Node.ELEMENT_NODE) {
				String varName = ((Element) variableNodesList.item(index)).getAttribute("name");
				if (varName != null) {
					varNames.add(varName);
				}
			}
		}

		return varNames;
	}
}
