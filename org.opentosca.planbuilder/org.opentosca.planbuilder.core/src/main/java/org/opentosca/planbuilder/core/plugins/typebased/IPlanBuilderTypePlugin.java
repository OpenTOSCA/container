package org.opentosca.planbuilder.core.plugins.typebased;

import java.util.Collection;

import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.core.plugins.context.PlanContext;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;

/**
 * <p>
 * This interface should be implemented by Plugins which can generate and add fragments that provision a complete
 * Template
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public interface IPlanBuilderTypePlugin<T extends PlanContext> extends IPlanBuilderPlugin {

    /**
     * This method should generate and add a fragment which handle the creation of the Template inside the
     * TemplateContext
     *
     * @param templateContext a TemplateContext of a Template
     * @return true iff when generating and adding fragment that handles the template completely
     */
    boolean handleCreate(T templateContext, AbstractNodeTemplate nodeTemplate);

    /**
     * This method should generate and add a fragment which handle the termination of the Template inside the
     * TemplateContext
     *
     * @param templateContext a TemplateContext of a Template
     * @return true iff when generating and adding fragment that handles the template completely
     */
    boolean handleTerminate(T templateContext, AbstractNodeTemplate nodeTemplate);

    /**
     * This method should generate and add a fragment which handle the creation of the Template inside the
     * TemplateContext
     *
     * @param templateContext a TemplateContext of a Template
     * @return true iff when generating and adding fragment that handles the template completely
     */
    boolean handleCreate(T templateContext, AbstractRelationshipTemplate relationshipTemplate);

    /**
     * This method should generate and add a fragment which handle the termination of the Template inside the
     * TemplateContext
     *
     * @param templateContext a TemplateContext of a Template
     * @return true iff when generating and adding fragment that handles the template completely
     */
    boolean handleTerminate(T templateContext, AbstractRelationshipTemplate relationshipTemplate);

    /**
     * This method should return true if the plugin can handle creation of the given nodeTemplate
     *
     * @param nodeTemplate the NodeTemplate to be handled by this plugin
     * @return true iff this plugin can handle the given nodeTemplate
     */
    boolean canHandleCreate(Csar csar, AbstractNodeTemplate nodeTemplate);

    /**
     * This method should return true if the plugin can handle the termination of the given nodeTemplate
     *
     * @param nodeTemplate the NodeTemplate to be handled by this plugin
     * @return true iff this plugin can handle the given nodeTemplate
     */
    boolean canHandleTerminate(Csar csar, AbstractNodeTemplate nodeTemplate);

    /**
     * This method should return true if the plugin can handle the creation of the given relationshipTemplate
     *
     * @param relationshipTemplate the RelationshipTemplate to be handled by this plugin
     * @return true iff this can handle the given relationshipTemplate
     */
    boolean canHandleCreate(Csar csar, AbstractRelationshipTemplate relationshipTemplate);

    /**
     * This method should return true if the plugin can handle the termination of the given relationshipTemplate
     *
     * @param relationshipTemplate the RelationshipTemplate to be handled by this plugin
     * @return true iff this can handle the given relationshipTemplate
     */
    boolean canHandleTerminate(Csar csar, AbstractRelationshipTemplate relationshipTemplate);

    /**
     * This method should generate and add a fragment which handle the update of the Template inside the
     * TemplateContext
     *
     * @param templateContext a TemplateContext of a Template
     * @return true iff when generating and adding fragment that handles the template completely
     */
    default boolean handleUpdate(T templateContext, AbstractNodeTemplate nodeTemplate) {
        return false;
    }

    /**
     * This method should return true if the plugin can handle update of the given nodeTemplate
     *
     * @param nodeTemplate the NodeTemplate to be handled by this plugin
     * @return true iff this plugin can handle the given nodeTemplate
     */
    default boolean canHandleUpdate(Csar csar, AbstractNodeTemplate nodeTemplate) {
        return false;
    }

    /**
     * May be implemented by Type Plugins to give the planbuilder more information about needed dependencies to handle
     * nodeTemplates
     *
     * @author kalmankepes
     */
    interface NodeDependencyInformationInterface {

        /**
         * Returns a collection of nodeTemplates that are needed to be able to create an instance of the given
         * nodeTemplate
         *
         * @param nodeTemplate the nodeTemplate to check its dependencies
         * @return a collection of nodeTemplates that must be available for the nodeTemplate to create it by this
         * plugin, if null -> the given NodeTemplate cannot be created under the context of the node (e.g. the topology
         * template misses nodes)
         */
        Collection<AbstractNodeTemplate> getCreateDependencies(AbstractNodeTemplate nodeTemplate, Csar csar);

        /**
         * Returns a collection of nodeTemplates that are needed to be able to terminate an instance of the given
         * nodeTemplate
         *
         * @param nodeTemplate the nodeTemplate to check its dependencies
         * @return a collection of nodeTemplates that must be available for the nodeTemplate to terminate it by this
         * plugin, if null -> the given NodeTemplate cannot be terminated under the context of the node (e.g. the
         * topology template misses nodes)
         */
        Collection<AbstractNodeTemplate> getTerminateDependencies(AbstractNodeTemplate nodeTemplate, Csar csar);
    }
}
