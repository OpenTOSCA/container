package org.opentosca.planbuilder.core.plugins.typebased;

import org.eclipse.winery.model.tosca.TNodeTemplate;

import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.core.plugins.context.PlanContext;

public interface IPlanBuilderPolicyAwareTypePlugin<T extends PlanContext> extends IPlanBuilderPlugin {

    /**
     * This method should generate and add a fragment which handle the Template inside the TemplateContext
     *
     * @param templateContext a TemplateContext of a Template
     * @return true iff when generating and adding fragment that handles the template completely
     */
    boolean handlePolicyAwareCreate(T templateContext);

    /**
     * This method should return true if the plugin can handle the given nodeTemplate
     *
     * @param nodeTemplate the NodeTemplate to be handled by this plugin
     * @return true iff this plugin can handle the given nodeTemplate
     */
    boolean canHandlePolicyAwareCreate(Csar csar, TNodeTemplate nodeTemplate);
}
