package org.opentosca.planbuilder.core.plugins.artifactbased;

import java.util.Map;

import org.eclipse.winery.model.tosca.TImplementationArtifact;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TParameter;

import org.opentosca.planbuilder.core.plugins.context.PlanContext;
import org.opentosca.planbuilder.core.plugins.context.Variable;
import org.w3c.dom.Element;

/**
 * <p>
 * This interface should be implemented by Plugins which can generate Fragments that implement the Provisioning trough
 * TOSCA Operations on Node-/RelationshipTypes with a map of operation parameters mapped to tosca properties.
 * </p>
 * Copyright 2016 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kálmán Képes - kalman.kepes@iaas.uni-stuttgart.de
 */
public interface IPlanBuilderProvPhaseParamOperationPlugin<T extends PlanContext>
    extends IPlanBuilderProvPhaseOperationPlugin<T> {

    /**
     * This method is used to generate and add a fragment which calls a TOSCA Operation
     *
     * @param context               the TemplateContext of the Template to call the Operation on
     * @param operation             the Operation to call on the Template
     * @param ia                    the IA which implements the Operation
     * @param param2propertyMapping a mapping from operation parameters to tosca property variables
     * @return true iff the plugin generated and added a fragment into the ProvisioningPhase in the TemplateContext
     */
    boolean handle(T context, TOperation operation, TImplementationArtifact ia,
                   Map<TParameter, Variable> param2propertyMapping);

    boolean handle(T context, TOperation operation, TImplementationArtifact ia,
                   Map<TParameter, Variable> param2propertyMapping, Element elementToAppendTo);

    boolean handle(T context, TOperation operation, TImplementationArtifact ia,
                   Map<TParameter, Variable> param2propertyMapping,
                   Map<TParameter, Variable> param2PropertyOutputMapping);

    boolean handle(T context, TOperation operation, TImplementationArtifact ia,
                   Map<TParameter, Variable> param2propertyMapping,
                   Map<TParameter, Variable> param2PropertyOutputMapping, Element elementToAppendTo);
}
