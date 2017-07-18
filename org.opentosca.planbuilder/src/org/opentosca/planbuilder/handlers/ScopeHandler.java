package org.opentosca.planbuilder.handlers;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.opentosca.planbuilder.model.plan.TOSCAPlan;
import org.opentosca.planbuilder.model.plan.TemplateBuildPlan;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This class is a part of the Facade to operate on BuildPlans. This particular
 * class is responsible for high-level operations on TemplateBuildPlans
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 * 
 */
public class ScopeHandler {
	
	private final static Logger LOG = LoggerFactory.getLogger(ScopeHandler.class);
	
	private BPELScopeHandler templateHandler;
	
	
	/**
	 * Constructor
	 */
	public ScopeHandler() {
		this.templateHandler = new BPELScopeHandler();
	}
	
	/**
	 * Creates a TemplateBuildPlan skeleton and connects it to the given
	 * BuildPlan
	 * 
	 * @param buildPlan the BuildPlan the TemplateBuildPlan should belong to
	 * @return a new TemplateBuildPlan skeleton
	 */
	public TemplateBuildPlan createTemplateBuildPlan(TOSCAPlan buildPlan) {
		TemplateBuildPlan newTemplateBuildPlan = new TemplateBuildPlan();
		this.templateHandler.initializeXMLElements(newTemplateBuildPlan, buildPlan);
		return newTemplateBuildPlan;
	}
	
	/**
	 * Creates a TemplateBuildPlan for the given NodeTemplate connected to the
	 * given BuildPlan
	 * 
	 * @param nodeTemplate the NodeTemplate the new TemplateBuildPlan should
	 *            belong to
	 * @param buildPlan the BuildPlan the new TemplateBuildPlan should belong to
	 * @return a new TemplateBuildPlann skeleton for the given NodeTemplate
	 */
	public TemplateBuildPlan createTemplateBuildPlan(AbstractNodeTemplate nodeTemplate, TOSCAPlan buildPlan) {
		TemplateBuildPlan templatePlan = this.createTemplateBuildPlan(buildPlan);
		this.templateHandler.setName(this.getNCNameFromString(nodeTemplate.getId()), templatePlan);
		return templatePlan;
	}
	
	/**
	 * Returns a valid NCName string
	 * 
	 * @param string a String to convert to valid NCName
	 * @return a String which can be used as NCName
	 */
	public String getNCNameFromString(String string) {
		// TODO check if this enough ;)
		return string.replace(" ", "_");
	}
	
	/**
	 * Creates a new TemplateBuildPlan for the given RelationshipTemplate and
	 * BuildPlan
	 * 
	 * @param relationshipTemplate the RelationshipTemplate the new
	 *            TemplateBuildPlan should belong to
	 * @param buildPlan the BuildPlan the new TemplateBuildPlan should belong to
	 * @return a new TemplateBuildPlan skeleton
	 */
	public TemplateBuildPlan createTemplateBuildPlan(AbstractRelationshipTemplate relationshipTemplate, TOSCAPlan buildPlan) {
		TemplateBuildPlan templatePlan = this.createTemplateBuildPlan(buildPlan);
		this.templateHandler.setName(relationshipTemplate.getId(), templatePlan);
		return templatePlan;
	}
	
	/**
	 * Returns all Successors of the given TemplateBuildPlan
	 * 
	 * @param templatePlan the TemplateBuildPlan whose Successors should be
	 *            returned
	 * @return a List of TemplateBuildPlans that are Successors of the given
	 *         TemplateBuildPlan
	 */
	public List<TemplateBuildPlan> getSuccessors(TemplateBuildPlan templatePlan) {
		List<TemplateBuildPlan> successors = new ArrayList<TemplateBuildPlan>();
		
		List<String> linkNamesInSources = this.templateHandler.getLinksInSources(templatePlan);
		
		for (String linkAsSource : linkNamesInSources) {
			for (TemplateBuildPlan template : templatePlan.getBuildPlan().getTemplateBuildPlans()) {
				List<String> linkNamesInTargets = this.templateHandler.getLinksInTarget(template);
				if (linkNamesInTargets.contains(linkAsSource)) {
					successors.add(template);
				}
			}
		}
		
		return successors;
	}
	
	/**
	 * Adds a partnerLink to the given TemplateBuildPlan
	 * 
	 * @param partnerLinkName the name of the partnerLink
	 * @param partnerLinkType the name of the partnerLinkType
	 * @param myRole the name of the 1st role
	 * @param partnerRole the name of the 2nd role
	 * @param initializePartnerRole whether to initialize the partnerRole
	 * @param templateBuildPlan the TemplateBuildPlan to add the partnerLink to
	 * @return true iff adding partnerLink was successful
	 */
	public boolean addPartnerLink(String partnerLinkName, QName partnerLinkType, String myRole, String partnerRole, boolean initializePartnerRole, TemplateBuildPlan templateBuildPlan) {
		ScopeHandler.LOG.debug("Trying to add partnerLink {} with partnerLinkType {}, myRole {}, partnerRole {} and initializePartnerRole {} for TemplateBuildPlan {}", partnerLinkName, partnerLinkType.toString(), myRole, partnerRole, String.valueOf(initializePartnerRole), templateBuildPlan.getBpelScopeElement().getAttribute("name"));
		return this.templateHandler.addPartnerLink(partnerLinkName, partnerLinkType, myRole, partnerRole, initializePartnerRole, templateBuildPlan);
	}
	
	/**
	 * Returns the predecessors of the given TemplateBuildPlan
	 * 
	 * @param templatePlan the TemplateBuildPlan to get predecessors from
	 * @return a List of TemplateBuildPlans that are predecessors of the given
	 *         TemplateBuildPlan
	 */
	public List<TemplateBuildPlan> getPredecessors(TemplateBuildPlan templatePlan) {
		List<TemplateBuildPlan> preds = new ArrayList<TemplateBuildPlan>();
		List<String> linkNamesInTargets = this.templateHandler.getLinksInTarget(templatePlan);
		
		for (String linkAsTarget : linkNamesInTargets) {
			for (TemplateBuildPlan template : templatePlan.getBuildPlan().getTemplateBuildPlans()) {
				List<String> linkNamesInSources = this.templateHandler.getLinksInSources(template);
				if (linkNamesInSources.contains(linkAsTarget)) {
					preds.add(template);
				}
			}
		}
		return preds;
	}
	
	/**
	 * Connects two TemplateBuildPlans (which are basically bpel scopes) with
	 * the given link
	 * 
	 * @param source the TemplateBuildPlan which should be a source of the link
	 * @param target the TemplateBuildPlan which should be a target of the link
	 * @param linkName the name of the link used to connect the two templates
	 * @return true if connections between templates was sucessfully created,
	 *         else false
	 */
	public boolean connect(TemplateBuildPlan source, TemplateBuildPlan target, String linkName) {
		ScopeHandler.LOG.debug("Trying to connect TemplateBuildPlan {} as source with TemplateBuildPlan {} as target", source.getBpelScopeElement().getAttribute("name"), target.getBpelScopeElement().getAttribute("name"));
		boolean check = true;
		// if everything was successfully added return true
		check &= this.templateHandler.addSource(linkName, source);
		check &= this.templateHandler.addTarget(linkName, target);
		return check;
	}
	
	/**
	 * Removes all connections the given TemplateBuildPlan contains. All
	 * source/target relations are removed from the given TemplateBuildPlan
	 * 
	 * @param template the TemplateBuildPlan to remove its relations
	 */
	public void removeAllConnetions(TemplateBuildPlan template) {
		this.templateHandler.removeSources(template);
		this.templateHandler.removeTargets(template);
	}
	
}
