package org.opentosca.planbuilder.core.plugins.typebased;

import org.opentosca.planbuilder.core.plugins.context.PlanContext;

/**
 * <p>
 * This interface should be implemented by Plugins which can generate BPMN Task to set
 * node/relationship property for given instance in a build plan
 * Temp
 * </p>
 * Copyright 2022 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kuang-Yu Li
 */
public interface IPlanBuilderTypeSetPropertyPlugin<T extends PlanContext> extends IPlanBuilderTypePlugin<T> {
    // TODO: add Set Node/Relationship Type specific method
}
