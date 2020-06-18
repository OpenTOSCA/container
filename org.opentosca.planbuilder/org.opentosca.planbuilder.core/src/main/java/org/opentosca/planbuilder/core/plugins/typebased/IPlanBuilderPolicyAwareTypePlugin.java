package org.opentosca.planbuilder.core.plugins.typebased;

import org.opentosca.planbuilder.core.plugins.context.PlanContext;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;

public interface IPlanBuilderPolicyAwareTypePlugin<T extends PlanContext> extends IPlanBuilderPlugin {

    /**
     * This method should generate and add a fragment which handle the Template inside the TemplateContext
     *
     * @param templateContext a TemplateContext of a Template
     * @return true iff when generating and adding fragment that handles the template completely
     */
    public boolean handlePolicyAwareCreate(T templateContext);

    /**
     * This method should return true if the plugin can handle the given nodeTemplate
     *
     * @param nodeTemplate the NodeTemplate to be handled by this plugin
     * @return true iff this plugin can handle the given nodeTemplate
     */
    public boolean canHandlePolicyAwareCreate(AbstractNodeTemplate nodeTemplate);
}
