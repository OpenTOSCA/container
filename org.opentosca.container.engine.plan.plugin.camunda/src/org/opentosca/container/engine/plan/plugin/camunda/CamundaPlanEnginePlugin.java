package org.opentosca.container.engine.plan.plugin.camunda;

import java.util.Arrays;
import java.util.List;

import javax.xml.namespace.QName;

import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.next.model.PlanLanguage;
import org.opentosca.container.core.tosca.model.TPlan.PlanModelReference;
import org.opentosca.container.engine.plan.plugin.IPlanEnginePlanRefPluginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CamundaPlanEnginePlugin implements IPlanEnginePlanRefPluginService {

    private final String CAMUNDA_DESCRIPTION = "OpenTOSCA PlanEngine Camunda BPMN 2.0 Plugin v1.0";

    final private static Logger LOG = LoggerFactory.getLogger(CamundaPlanEnginePlugin.class);

    @Override
    public boolean deployPlanReference(final QName planId, final PlanModelReference planRef, final CSARID csarId) {
        // TODO
        LOG.warn("The deploy method for the Camunda plan engine is not implemented yet.");
        return false;
    }

    @Override
    public boolean undeployPlanReference(final QName planId, final PlanModelReference planRef, final CSARID csarId) {
        // TODO
        LOG.warn("The undeploy method for the Camunda plan engine is not implemented yet.");
        return false;
    }

    @Override
    public String getLanguageUsed() {
        return PlanLanguage.BPMN.toString();
    }

    @Override
    public List<String> getCapabilties() {
        return Arrays.asList(PlanLanguage.BPMN.toString());
    }

    @Override
    public String toString() {
        return this.CAMUNDA_DESCRIPTION;
    }
}
