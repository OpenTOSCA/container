package org.opentosca.planbuilder.postphase.plugin.setproperties.bpmn;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;

import org.opentosca.planbuilder.core.bpmn.context.BPMNPlanContext;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderBPMNPostPhasePlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * This class represents a POST-Phase Plugin to set the Properties of the NodeTemplate
 *
 */
@Component
public class BPMNSetPropertiesPlugin implements IPlanBuilderBPMNPostPhasePlugin<BPMNPlanContext> {

    private static Logger LOG = LoggerFactory.getLogger(BPMNSetPropertiesPlugin.class);
    private static final String PLAN_ID = "BPMN OpenTOSCA SetProperties Post Phase Plugin";
    private final BPMNSetPropertiesHandler handler = new BPMNSetPropertiesHandler();
    private final QName securePasswordPolicyType =
        new QName("http://opentosca.org/policytypes", "SecurePasswordPolicyType");

    @Override
    public boolean handleCreate(BPMNPlanContext context, TNodeTemplate nodeTemplate) {
        return this.handler.handleCreate(context, nodeTemplate);
        //return false;
    }

    @Override
    public boolean handleCreate(BPMNPlanContext context, TRelationshipTemplate relationshipTemplate) {
        return false;
    }

    @Override
    public boolean canHandleCreate(BPMNPlanContext context, TNodeTemplate nodeTemplate) {
       return true;
    }

    @Override
    public boolean canHandleCreate(BPMNPlanContext context, TRelationshipTemplate relationshipTemplate) {
        return false;
    }

    @Override
    public boolean handleTerminate(BPMNPlanContext context, TNodeTemplate nodeTemplate) {
        return false;
    }

    @Override
    public boolean handleTerminate(BPMNPlanContext context, TRelationshipTemplate relationshipTemplate) {
        return false;
    }

    @Override
    public boolean canHandleTerminate(BPMNPlanContext context, TNodeTemplate nodeTemplate) {
        return false;
    }

    @Override
    public boolean canHandleTerminate(BPMNPlanContext context, TRelationshipTemplate relationshipTemplate) {
        return false;
    }

    @Override
    public boolean handleUpdate(BPMNPlanContext sourceContext, BPMNPlanContext targetContext, TNodeTemplate sourceNodeTemplate, TNodeTemplate targetNodeTemplate) {
        return false;
    }

    @Override
    public boolean canHandleUpdate(TNodeTemplate sourceNodeTemplate, TNodeTemplate targetNodeTemplate) {
        return false;
    }

    @Override
    public boolean handleUpdate(BPMNPlanContext sourceContext, BPMNPlanContext targetContext, TRelationshipTemplate sourceRelationshipTemplate, TRelationshipTemplate targetRelationshipTemplate) {
        return false;
    }

    @Override
    public boolean canHandleUpdate(TRelationshipTemplate sourceRelationshipTemplate, TRelationshipTemplate targetRelationshipTemplate) {
        return false;
    }

    @Override
    public boolean handleUpgrade(BPMNPlanContext context, TNodeTemplate nodeTemplate) {
        return false;
    }

    @Override
    public boolean handleUpgrade(BPMNPlanContext context, TRelationshipTemplate relationshipTemplate) {
        return false;
    }

    @Override
    public boolean canHandleUpgrade(BPMNPlanContext context, TNodeTemplate nodeTemplate) {
        return false;
    }

    @Override
    public boolean canHandleUpgrade(BPMNPlanContext context, TRelationshipTemplate relationshipTemplate) {
        return false;
    }

    @Override
    public String getID() {
        return PLAN_ID;
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
