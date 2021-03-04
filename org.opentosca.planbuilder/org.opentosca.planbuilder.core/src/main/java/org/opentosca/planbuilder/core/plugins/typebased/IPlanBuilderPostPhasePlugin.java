package org.opentosca.planbuilder.core.plugins.typebased;

import org.opentosca.planbuilder.core.plugins.context.PlanContext;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;

/**
 * <p>
 * This interface should be implemented by Plugins which are PostPhasePlugins. PostPhasePlugins are used to update data
 * outside of the BuildPlan, like Databases
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
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
    boolean handleCreate(T context, AbstractNodeTemplate nodeTemplate);

    /**
     * When this method is called the Plugin should fetch relevant runtime data inside the BuildPlan of the given
     * RelationshipTemplate and send it to the Component it belongs to
     *
     * @param context              a TemplatePlanContext for accessing data inside the BuildPlan
     * @param relationshipTemplate the RelationshipTemplate the plugin should handle
     * @return true if generating the Fragment of this Plugin was successful, else false
     */
    boolean handleCreate(T context, AbstractRelationshipTemplate relationshipTemplate);

    /**
     * Evaluates whether the given NodeTemplate can be handled by this post phase plugin.
     *
     * @param nodeTemplate An AbstractNodeTemplate
     * @return true iff this plugin can handle the given nodeTemplate
     */
    boolean canHandleCreate(T context, AbstractNodeTemplate nodeTemplate);

    /**
     * Evaluates whether the given RelationshipTemplate can be handled by this post phase plugin.
     *
     * @param relationshipTemplate An AbstractRelationshipTemplate
     * @return true iff this plugin can handle the given relationshipTemplate
     */
    boolean canHandleCreate(T context, AbstractRelationshipTemplate relationshipTemplate);

    /**
     * When this method is called the Plugin should fetch relevant runtime data inside the BuildPlan of the given
     * NodeTemplate and send it to the Component it belongs to
     *
     * @param context      a TemplatePlanContext for accessing data inside the BuildPlan
     * @param nodeTemplate the NodeTemplate the plugin should handle
     * @return true if generating the Fragment of this Plugin was successful, else false
     */
    boolean handleTerminate(T context, AbstractNodeTemplate nodeTemplate);

    /**
     * When this method is called the Plugin should fetch relevant runtime data inside the BuildPlan of the given
     * RelationshipTemplate and send it to the Component it belongs to
     *
     * @param context              a TemplatePlanContext for accessing data inside the BuildPlan
     * @param relationshipTemplate the RelationshipTemplate the plugin should handle
     * @return true if generating the Fragment of this Plugin was successful, else false
     */
    boolean handleTerminate(T context, AbstractRelationshipTemplate relationshipTemplate);

    /**
     * Evaluates whether the given NodeTemplate can be handled by this post phase plugin.
     *
     * @param nodeTemplate An AbstractNodeTemplate
     * @return true iff this plugin can handle the given nodeTemplate
     */
    boolean canHandleTerminate(T context, AbstractNodeTemplate nodeTemplate);

    /**
     * Evaluates whether the given RelationshipTemplate can be handled by this post phase plugin.
     *
     * @param relationshipTemplate An AbstractRelationshipTemplate
     * @return true iff this plugin can handle the given relationshipTemplate
     */
    boolean canHandleTerminate(T context, AbstractRelationshipTemplate relationshipTemplate);

    boolean handleUpdate(T sourceContext, T targetContext, AbstractNodeTemplate sourceNodeTemplate,
                         AbstractNodeTemplate targetNodeTemplate);

    boolean canHandleUpdate(AbstractNodeTemplate sourceNodeTemplate, AbstractNodeTemplate targetNodeTemplate);

    boolean handleUpdate(T sourceContext, T targetContext,
                         AbstractRelationshipTemplate sourceRelationshipTemplate,
                         AbstractRelationshipTemplate targetRelationshipTemplate);

    boolean canHandleUpdate(AbstractRelationshipTemplate sourceRelationshipTemplate,
                            AbstractRelationshipTemplate targetRelationshipTemplate);

    /**
     * @param context      a TemplatePlanContext for accessing data inside the BuildPlan
     * @param nodeTemplate the NodeTemplate the plugin should handle
     * @return true if generating the Fragment of this Plugin was successful, else false
     */
    boolean handleUpgrade(T context, AbstractNodeTemplate nodeTemplate);

    /**
     * @param context              a TemplatePlanContext for accessing data inside the BuildPlan
     * @param relationshipTemplate the RelationshipTemplate the plugin should handle
     * @return true if generating the Fragment of this Plugin was successful, else false
     */
    boolean handleUpgrade(T context, AbstractRelationshipTemplate relationshipTemplate);

    /**
     * Evaluates whether the given NodeTemplate can be handled by this post phase plugin.
     *
     * @param nodeTemplate An AbstractNodeTemplate
     * @return true iff this plugin can handle the given nodeTemplate
     */
    boolean canHandleUpgrade(T context, AbstractNodeTemplate nodeTemplate);

    /**
     * Evaluates whether the given RelationshipTemplate can be handled by this post phase plugin.
     *
     * @param relationshipTemplate An AbstractRelationshipTemplate
     * @return true iff this plugin can handle the given relationshipTemplate
     */
    boolean canHandleUpgrade(T context, AbstractRelationshipTemplate relationshipTemplate);
}
