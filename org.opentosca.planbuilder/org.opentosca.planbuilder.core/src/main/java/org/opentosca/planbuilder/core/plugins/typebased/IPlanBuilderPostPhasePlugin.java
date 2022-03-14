package org.opentosca.planbuilder.core.plugins.typebased;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;

import org.opentosca.planbuilder.core.plugins.context.PlanContext;

/**
 * <p>
 * This interface should be implemented by Plugins which are PostPhasePlugins. PostPhasePlugins are used to update data
 * outside of the BuildPlan, like Databases
 * </p>
 * Copyright 2013-2022 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public interface IPlanBuilderPostPhasePlugin<T extends PlanContext> extends IPlanBuilderPlugin {

    /**
     * When this method is called the Plugin should fetch relevant runtime data inside the BuildPlan of the given
     * NodeTemplate and send it to the Component it belongs to
     *
     * @param context      a TemplatePlanContext for accessing data inside the BuildPlan
     * @param nodeTemplate the NodeTemplate the plugin should handle
     * @return true if generating the Fragment of this Plugin was successful, else false
     */
    boolean handleCreate(T context, TNodeTemplate nodeTemplate);

    /**
     * When this method is called the Plugin should fetch relevant runtime data inside the BuildPlan of the given
     * RelationshipTemplate and send it to the Component it belongs to
     *
     * @param context              a TemplatePlanContext for accessing data inside the BuildPlan
     * @param relationshipTemplate the RelationshipTemplate the plugin should handle
     * @return true if generating the Fragment of this Plugin was successful, else false
     */
    boolean handleCreate(T context, TRelationshipTemplate relationshipTemplate);

    /**
     * Evaluates whether the given NodeTemplate can be handled by this post phase plugin.
     *
     * @param nodeTemplate An TNodeTemplate
     * @return true iff this plugin can handle the given nodeTemplate
     */
    boolean canHandleCreate(T context, TNodeTemplate nodeTemplate);

    /**
     * Evaluates whether the given RelationshipTemplate can be handled by this post phase plugin.
     *
     * @param relationshipTemplate An TRelationshipTemplate
     * @return true iff this plugin can handle the given relationshipTemplate
     */
    boolean canHandleCreate(T context, TRelationshipTemplate relationshipTemplate);

    /**
     * When this method is called the Plugin should fetch relevant runtime data inside the BuildPlan of the given
     * NodeTemplate and send it to the Component it belongs to
     *
     * @param context      a TemplatePlanContext for accessing data inside the BuildPlan
     * @param nodeTemplate the NodeTemplate the plugin should handle
     * @return true if generating the Fragment of this Plugin was successful, else false
     */
    boolean handleTerminate(T context, TNodeTemplate nodeTemplate);

    /**
     * When this method is called the Plugin should fetch relevant runtime data inside the BuildPlan of the given
     * RelationshipTemplate and send it to the Component it belongs to
     *
     * @param context              a TemplatePlanContext for accessing data inside the BuildPlan
     * @param relationshipTemplate the RelationshipTemplate the plugin should handle
     * @return true if generating the Fragment of this Plugin was successful, else false
     */
    boolean handleTerminate(T context, TRelationshipTemplate relationshipTemplate);

    /**
     * Evaluates whether the given NodeTemplate can be handled by this post phase plugin.
     *
     * @param nodeTemplate An TNodeTemplate
     * @return true iff this plugin can handle the given nodeTemplate
     */
    boolean canHandleTerminate(T context, TNodeTemplate nodeTemplate);

    /**
     * Evaluates whether the given RelationshipTemplate can be handled by this post phase plugin.
     *
     * @param relationshipTemplate An TRelationshipTemplate
     * @return true iff this plugin can handle the given relationshipTemplate
     */
    boolean canHandleTerminate(T context, TRelationshipTemplate relationshipTemplate);

    boolean handleUpdate(T sourceContext, T targetContext, TNodeTemplate sourceNodeTemplate,
                         TNodeTemplate targetNodeTemplate);

    boolean canHandleUpdate(TNodeTemplate sourceNodeTemplate, TNodeTemplate targetNodeTemplate);

    boolean handleUpdate(T sourceContext, T targetContext,
                         TRelationshipTemplate sourceRelationshipTemplate,
                         TRelationshipTemplate targetRelationshipTemplate);

    boolean canHandleUpdate(TRelationshipTemplate sourceRelationshipTemplate,
                            TRelationshipTemplate targetRelationshipTemplate);

    /**
     * @param context      a TemplatePlanContext for accessing data inside the BuildPlan
     * @param nodeTemplate the NodeTemplate the plugin should handle
     * @return true if generating the Fragment of this Plugin was successful, else false
     */
    boolean handleUpgrade(T context, TNodeTemplate nodeTemplate);

    /**
     * @param context              a TemplatePlanContext for accessing data inside the BuildPlan
     * @param relationshipTemplate the RelationshipTemplate the plugin should handle
     * @return true if generating the Fragment of this Plugin was successful, else false
     */
    boolean handleUpgrade(T context, TRelationshipTemplate relationshipTemplate);

    /**
     * Evaluates whether the given NodeTemplate can be handled by this post phase plugin.
     *
     * @param nodeTemplate An TNodeTemplate
     * @return true iff this plugin can handle the given nodeTemplate
     */
    boolean canHandleUpgrade(T context, TNodeTemplate nodeTemplate);

    /**
     * Evaluates whether the given RelationshipTemplate can be handled by this post phase plugin.
     *
     * @param relationshipTemplate An TRelationshipTemplate
     * @return true iff this plugin can handle the given relationshipTemplate
     */
    boolean canHandleUpgrade(T context, TRelationshipTemplate relationshipTemplate);
}
