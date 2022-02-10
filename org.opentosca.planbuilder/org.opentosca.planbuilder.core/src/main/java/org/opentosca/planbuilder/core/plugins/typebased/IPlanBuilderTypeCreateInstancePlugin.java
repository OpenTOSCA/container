package org.opentosca.planbuilder.core.plugins.typebased;

import org.opentosca.planbuilder.core.plugins.context.PlanContext;

/**
 * <p>
 * This interface should be implemented by Plugins which can generate BPMN Task to create
 * node/relationship instance for build plan
 * Template
 * </p>
 * Copyright 2022 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kuang-Yu Li
 */
public interface IPlanBuilderTypeCreateInstancePlugin<T extends PlanContext> extends IPlanBuilderTypePlugin<T> {
}
