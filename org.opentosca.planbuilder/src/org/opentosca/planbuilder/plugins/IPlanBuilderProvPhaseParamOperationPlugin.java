package org.opentosca.planbuilder.plugins;

import java.util.Map;

import org.opentosca.planbuilder.model.tosca.AbstractImplementationArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.planbuilder.model.tosca.AbstractParameter;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext.Variable;

/**
 * 
 * <p>
 * This interface should be implemented by Plugins which can generate Fragments
 * that implement the Provisioning trough TOSCA Operations on
 * Node-/RelationshipTypes with a map of operation parameters mapped to tosca
 * properties.
 * </p>
 * Copyright 2016 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kálmán Képes - kalman.kepes@iaas.uni-stuttgart.de
 *
 */
public interface IPlanBuilderProvPhaseParamOperationPlugin extends IPlanBuilderProvPhaseOperationPlugin {

	/**
	 * This method is used to generate and add a fragment which calls an TOSCA
	 * Operations
	 * 
	 * @param context
	 *            the TemplateContext of the Template to call the Operation on
	 * @param operation
	 *            the Operation to call on the Template
	 * @param ia
	 *            the IA which implements the Operation
	 * @param param2propertyMapping
	 *            a mapping from operation parameters to tosca property
	 *            variables
	 * @return true iff the plugin generated and added a fragment into the
	 *         ProvisioningPhase in the TemplateContext
	 */
	public boolean handle(TemplatePlanContext context, AbstractOperation operation, AbstractImplementationArtifact ia,
			Map<AbstractParameter, Variable> param2propertyMapping);
	
	public boolean handle(TemplatePlanContext context, AbstractOperation operation, AbstractImplementationArtifact ia,
			Map<AbstractParameter, Variable> param2propertyMapping, Map<AbstractParameter, Variable> param2PropertyOutputMapping);

}
