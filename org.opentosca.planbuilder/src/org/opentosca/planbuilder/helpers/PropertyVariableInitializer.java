package org.opentosca.planbuilder.helpers;

import java.util.HashMap;
import java.util.Map;

import org.opentosca.planbuilder.handlers.PlanHandler;
import org.opentosca.planbuilder.model.plan.TOSCAPlan;
import org.opentosca.planbuilder.model.plan.TemplateBuildPlan;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * <p>
 * This class is used to initialize Template properties as variables in
 * BuildPlans.
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 * 
 */
public class PropertyVariableInitializer {
	
	private final static Logger LOG = LoggerFactory.getLogger(PropertyVariableInitializer.class);
	
	private PlanHandler planHandler;
	
	
	/**
	 * <p>
	 * This class represents a mapping from TemplateId to Property LocalName and
	 * VariableName
	 * </p>
	 * Copyright 2013 IAAS University of Stuttgart <br>
	 * <br>
	 * 
	 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
	 * 
	 */
	public class PropertyMap {
		
		// the internal map TemplateId -> PropertyLocalName,VariableName
		private Map<String, Map<String, String>> internalMap;
		
		
		/**
		 * Constructor
		 */
		public PropertyMap() {
			this.internalMap = new HashMap<String, Map<String, String>>();
		}
		
		/**
		 * Adds a mapping from TemplateId to Property LocalName and VariableName
		 * 
		 * @param templateId the Id of the Template
		 * @param propertyName a localName of Template Property
		 * @param propertyVariableName a variable name
		 * @return true if adding was successful, else false
		 */
		public boolean addPropertyMapping(String templateId, String propertyName, String propertyVariableName) {
			if (this.internalMap.containsKey(templateId)) {
				// template has already properties set
				Map<String, String> propertyMappingMap = this.internalMap.get(templateId);
				if (propertyMappingMap.containsKey(propertyName)) {
					// this is an error, because we don't map properties to two
					// variables
					return false;
				} else {
					// add property and propertyVariableName
					propertyMappingMap.put(propertyName, propertyVariableName);
					// FIXME ? don't know yet if the retrieved map is backed in
					// the internalMap
					this.internalMap.put(templateId, propertyMappingMap);
					return true;
				}
			} else {
				// template has no properties assigned
				Map<String, String> propertyMappingMap = new HashMap<String, String>();
				propertyMappingMap.put(propertyName, propertyVariableName);
				this.internalMap.put(templateId, propertyMappingMap);
				return true;
			}
		}
		
		/**
		 * Returns all mappings from Property localName to variable name for a
		 * given TemplateId
		 * 
		 * @param templateid the Id of a Template
		 * @return a Map from String to String representing Property localName
		 *         as key and variable name as value
		 */
		public Map<String, String> getPropertyMappingMap(String templateid) {
			return this.internalMap.get(templateid);
		}
	}
	
	
	/**
	 * Constructor
	 * 
	 * @param planHandler a BuildPlanHandler for the class
	 * @param templateHandler a TemplateBuildPlanHandler for the class
	 */
	public PropertyVariableInitializer(PlanHandler planHandler) {
		this.planHandler = planHandler;
	}
	
	/**
	 * Initializes the BuildPlan with variables for Template Properties and
	 * returns the Mappings for the Properties and variables
	 * 
	 * @param buildPlan the BuildPlan to initialize
	 * @return a PropertyMap which holds mappings from Template to Template
	 *         Property and BuildPlan variable
	 */
	public PropertyMap initializePropertiesAsVariables(TOSCAPlan buildPlan) {
		PropertyMap map = new PropertyMap();
		for (TemplateBuildPlan templatePlan : buildPlan.getTemplateBuildPlans()) {
			this.initializePropertiesAsVariables(map, templatePlan);
		}
		return map;
	}
	
	/**
	 * Initializes Properties inside the given PropertyMap of the given
	 * TemplateBuildPlan
	 * 
	 * @param map a PropertyMap to save the mappings to
	 * @param templatePlan the TemplateBuildPlan to initialize its properties
	 */
	public void initializePropertiesAsVariables(PropertyMap map, TemplateBuildPlan templatePlan) {
		if (templatePlan.getRelationshipTemplate() != null) {
			// template corresponds to a relationshiptemplate
			this.initPropsAsVarsInRelationship(map, templatePlan);
		} else {
			this.initPropsAsVarsInNode(map, templatePlan);
		}
	}
	
	/**
	 * Initializes Property variables and mappings for a TemplateBuildPlan which
	 * handles a RelationshipTemplate
	 * 
	 * @param map the PropertyMap to save the result to
	 * @param templatePlan a TemplateBuildPlan which handles a
	 *            RelationshipTemplate
	 */
	private void initPropsAsVarsInRelationship(PropertyMap map, TemplateBuildPlan templatePlan) {
		AbstractRelationshipTemplate relationshipTemplate = templatePlan.getRelationshipTemplate();
		if (relationshipTemplate.getProperties() != null) {
			Element propertyElement = relationshipTemplate.getProperties().getDOMElement();
			for (int i = 0; i < propertyElement.getChildNodes().getLength(); i++) {
				
				if (propertyElement.getChildNodes().item(i).getNodeType() == Node.TEXT_NODE) {
					continue;
				}
				
				String propName = propertyElement.getChildNodes().item(i).getLocalName();
				String propVarName = relationshipTemplate.getId() + "_" + propertyElement.getChildNodes().item(i).getLocalName();
				map.addPropertyMapping(relationshipTemplate.getId(), propName, "prop_" + propVarName);
				// String value =
				// propertyElement.getChildNodes().item(i).getFirstChild().getNodeValue();
				String value = "";
				
				for (int j = 0; j < propertyElement.getChildNodes().item(i).getChildNodes().getLength(); j++) {
					if (propertyElement.getChildNodes().item(i).getChildNodes().item(j).getNodeType() == Node.TEXT_NODE) {
						value += propertyElement.getChildNodes().item(i).getChildNodes().item(j).getNodeValue();
					}
				}
				
				PropertyVariableInitializer.LOG.debug("Setting property variable " + propVarName);
				PropertyVariableInitializer.LOG.debug("with value: " + value);
				
				// tempID_PropLocalName as property variable name
				this.planHandler.addPropertyVariable(propVarName, templatePlan.getBuildPlan());
				
				if (!value.trim().isEmpty() && !value.trim().equals("")) {
					// init the variable with the node value
					this.planHandler.initializePropertyVariable(propVarName, value, templatePlan.getBuildPlan());
				}
				
			}
		}
	}
	
	/**
	 * Initializes Property variables for the given TemplateBuildPlan which
	 * handles a NodeTemplate
	 * 
	 * @param map a PropertyMap to save the result/mappings to
	 * @param templatePlan a TemplateBuildPlan which handles a NodeTemplate
	 */
	private void initPropsAsVarsInNode(PropertyMap map, TemplateBuildPlan templatePlan) {
		AbstractNodeTemplate nodeTemplate = templatePlan.getNodeTemplate();
		if (nodeTemplate.getProperties() != null) {
			Element propertyElement = nodeTemplate.getProperties().getDOMElement();
			for (int i = 0; i < propertyElement.getChildNodes().getLength(); i++) {
				
				if (propertyElement.getChildNodes().item(i).getNodeType() == Node.TEXT_NODE) {
					continue;
				}
				
				String propName = propertyElement.getChildNodes().item(i).getLocalName();
				String propVarName = nodeTemplate.getId() + "_" + propertyElement.getChildNodes().item(i).getLocalName();
				
				// TODO that "prop_" is a huge hack cause official only the
				// buildplanhandler knows about the "prop_" piece
				map.addPropertyMapping(nodeTemplate.getId(), propName, "prop_" + propVarName);
				
				String value = "";
				
				for (int j = 0; j < propertyElement.getChildNodes().item(i).getChildNodes().getLength(); j++) {
					if (propertyElement.getChildNodes().item(i).getChildNodes().item(j).getNodeType() == Node.TEXT_NODE) {
						value += propertyElement.getChildNodes().item(i).getChildNodes().item(j).getNodeValue();
					}
				}
				
				PropertyVariableInitializer.LOG.debug("Setting property variable " + propVarName);
				PropertyVariableInitializer.LOG.debug("with value: " + value);
				
				// tempID_PropLocalName as property variable name
				this.planHandler.addPropertyVariable(propVarName, templatePlan.getBuildPlan());
				
				// init the variable with the node value
				this.planHandler.initializePropertyVariable(propVarName, value, templatePlan.getBuildPlan());				
			}
		}
	}
	
}
