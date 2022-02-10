package org.opentosca.planbuilder.core.plugins.artifactbased;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TImplementationArtifact;
import org.eclipse.winery.model.tosca.TOperation;

import org.opentosca.planbuilder.core.plugins.context.PlanContext;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderPlugin;

/**
 * <p>
 * This interface should be implemented by Plugins which can generate Fragments that implement the Provisioning trough
 * TOSCA Operations on Node-/RelationshipTypes
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public interface IPlanBuilderProvPhaseOperationPlugin<T extends PlanContext> extends IPlanBuilderPlugin {

    /**
     * This method is used determine whether the Plugin can handle Operations which are implemented by an IA with the
     * given ArtifactType
     *
     * @param operationArtifactType the Type of the IA which implements a TOSCA Operation
     * @return true iff the plugin can handle Operations that are implemented by IA of the given ArtifactType
     */
    boolean canHandle(QName operationArtifactType);

    /**
     * This method is used to generate and add a fragment which calls an TOSCA Operations
     *
     * @param context   the TemplateContext of the Template to call the Operation on
     * @param operation the Operation to call on the Template
     * @param ia        the IA which implements the Operation
     * @return true iff the plugin generated and added a fragment into the ProvisioningPhase in the TemplateContext
     */
    boolean handle(T context, TOperation operation, TImplementationArtifact ia);
}
