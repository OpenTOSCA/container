package org.opentosca.planbuilder.core;

import java.util.List;

import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TServiceTemplate;

import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.core.plugins.registry.PluginRegistry;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSimplePlanBuilder extends AbstractPlanBuilder {

    private final static Logger LOG = LoggerFactory.getLogger(AbstractSimplePlanBuilder.class);

    public AbstractSimplePlanBuilder(PluginRegistry pluginRegistry) {
        super(pluginRegistry);
    }

    /**
     * <p>
     * Creates a BuildPlan in WS-BPEL 2.0 for the specified values csarName, definitions and serviceTemplateId. Where
     * csarName denotes the fileName of the CSAR, definitions denotes the Definitions document and serviceTemplateId a
     * QName denoting the ServiceTemplate inside the Definitions document
     * </p>
     *
     * @param csar          the CSAR
     * @param definitions       the Definitions document as TDefinitions Object
     * @param serviceTemplateId a QName denoting a ServiceTemplate inside the Definitions document
     * @return a complete BuildPlan for the given ServiceTemplate, if the ServiceTemplate denoted by the given QName
     * isn't found inside the Definitions document null is returned instead
     */
    abstract public AbstractPlan buildPlan(Csar csar, TDefinitions definitions,
                                           TServiceTemplate serviceTemplateId);

    /**
     * <p>
     * Returns a List of BuildPlans for the ServiceTemplates contained in the given Definitions document
     * </p>
     *
     * @param csar    the CSAR
     * @param definitions a TDefinitions Object denoting the Definitions document
     * @return a List of Build Plans for each ServiceTemplate contained inside the Definitions document
     */
    abstract public List<AbstractPlan> buildPlans(Csar csar, TDefinitions definitions);
}
