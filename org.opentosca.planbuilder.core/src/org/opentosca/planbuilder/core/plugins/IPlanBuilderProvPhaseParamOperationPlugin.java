package org.opentosca.planbuilder.core.plugins;

import java.util.Map;

import org.opentosca.planbuilder.core.plugins.context.PlanContext;
import org.opentosca.planbuilder.core.plugins.context.Variable;
import org.opentosca.planbuilder.model.plan.bpel.BPELScopeActivity.BPELScopePhaseType;
import org.opentosca.planbuilder.model.tosca.AbstractImplementationArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.planbuilder.model.tosca.AbstractParameter;

/**
 *
 * <p>
 * This interface should be implemented by Plugins which can generate Fragments that implement the
 * Provisioning trough TOSCA Operations on Node-/RelationshipTypes with a map of operation
 * parameters mapped to tosca properties.
 * </p>
 * Copyright 2016 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kálmán Képes - kalman.kepes@iaas.uni-stuttgart.de
 *
 */
public interface IPlanBuilderProvPhaseParamOperationPlugin<T extends PlanContext>
                                                          extends IPlanBuilderProvPhaseOperationPlugin<T> {

    /**
     * This method is used to generate and add a fragment which calls an TOSCA Operations
     *
     * @param context the TemplateContext of the Template to call the Operation on
     * @param operation the Operation to call on the Template
     * @param ia the IA which implements the Operation
     * @param param2propertyMapping a mapping from operation parameters to tosca property variables
     * @return true iff the plugin generated and added a fragment into the ProvisioningPhase in the
     *         TemplateContext
     */
    public boolean handle(T context, AbstractOperation operation, AbstractImplementationArtifact ia,
                          Map<AbstractParameter, Variable> param2propertyMapping);

    public boolean handle(T context, AbstractOperation operation, AbstractImplementationArtifact ia,
                          Map<AbstractParameter, Variable> param2propertyMapping, BPELScopePhaseType phase);

    public boolean handle(T context, AbstractOperation operation, AbstractImplementationArtifact ia,
                          Map<AbstractParameter, Variable> param2propertyMapping,
                          Map<AbstractParameter, Variable> param2PropertyOutputMapping);

    public boolean handle(T context, AbstractOperation operation, AbstractImplementationArtifact ia,
                          Map<AbstractParameter, Variable> param2propertyMapping,
                          Map<AbstractParameter, Variable> param2PropertyOutputMapping, BPELScopePhaseType phase);

}
