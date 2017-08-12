package org.opentosca.planbuilder.model.plan;

import java.util.Collection;
import java.util.Map;

import javax.xml.namespace.QName;

import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;

/**
 * Copyright 2017 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kálmán Képes - kalman.kepes@iaas.uni-stuttgart.de
 *
 */
public abstract class AbstractPlan {
	
	// general categories
	public enum PlanType {
		BUILD, MANAGE, TERMINATE
	}
	
	
	private AbstractServiceTemplate serviceTemplate;
	
	private PlanType type;
	
	private final AbstractDefinitions definitions;
	
	private final Collection<AbstractActivity> activites;
	
	private final Map<AbstractActivity, AbstractActivity> links;
	
	private final String id;
	
	
	public AbstractPlan(final String id, final PlanType type, final AbstractDefinitions definitions, final AbstractServiceTemplate serviceTemplate, final Collection<AbstractActivity> activities, final Map<AbstractActivity, AbstractActivity> links) {
		this.id = id;
		this.type = type;
		this.definitions = definitions;
		this.serviceTemplate = serviceTemplate;
		this.activites = activities;
		this.links = links;
	}
	
	public String getId() {
		return this.id;
	}
	
	/**
	 * @return the type
	 */
	public PlanType getType() {
		return type;
	}
	
	/**
	 * @type the type to set
	 */
	public void setType(PlanType type) {
		this.type = type;
	}
	
	/**
	 * Returns the definitions document this AbstractPlan belongs to. The
	 * ServiceTemplate this BuildPlan provisions must be contained in the given
	 * AbstractDefinitions.
	 * 
	 * @return an AbstractDefinitions
	 */
	public AbstractDefinitions getDefinitions() {
		return this.definitions;
	}
	
	/**
	 * Returns the AbstractServiceTemplate of the ServiceTemplate this
	 * AbstractPlan belongs to
	 * 
	 * @return a AbstractServiceTemplate
	 */
	public AbstractServiceTemplate getServiceTemplate() {
		return this.serviceTemplate;
	}

	public Collection<AbstractActivity> getActivites() {
		return activites;
	}

	public Map<AbstractActivity, AbstractActivity> getLinks() {
		return links;
	}
	
}
