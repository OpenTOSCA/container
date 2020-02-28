
package org.opentosca.planbuilder.type.plugin.patternbased.bpel;

import java.util.Collection;
import java.util.HashSet;

import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.plugins.context.PlanContext;
import org.opentosca.planbuilder.plugins.typebased.IPlanBuilderPlugin;
import org.opentosca.planbuilder.plugins.typebased.IPlanBuilderTypePlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Copyright 2018 IAAS University of Stuttgart <br>
 * <br>
 *
 * <p>
 * This class implements a PlanBuilder Type Plugin for the provisioning of NodeType that are modeled
 * based on provisioning patterns.
 * </p>
 *
 *
 * @author Kalman Kepes - kalman.kepes@iaas.uni-stuttgart.de
 *
 */
public class PatternBasedPlugin implements IPlanBuilderTypePlugin<BPELPlanContext>,
                                IPlanBuilderTypePlugin.NodeDependencyInformationInterface {

    private final static Logger LOG = LoggerFactory.getLogger(PatternBasedPlugin.class);

    private static final String id = "OpenTOSCA PlanBuilder Type Plugin Pattern-Based Provisioning";

    private static final ContainerPatternBasedHandler containerPatternHandler = new ContainerPatternBasedHandler();

    private static final LifecyclePatternBasedHandler lifecyclePatternHandler = new LifecyclePatternBasedHandler();
    
    private static final RemoteManagerPatternBasedHandler remoteMgrHandler = new RemoteManagerPatternBasedHandler();

    @Override
    public String getID() {
        return id;
    }

    @Override
    public boolean handleCreate(final BPELPlanContext templateContext, AbstractNodeTemplate nodeTemplate) {
        LOG.debug("Handling nodeTemplate {} by pattern", nodeTemplate.getId());
        boolean check = true;
        if (containerPatternHandler.isProvisionableByContainerPattern(nodeTemplate)) {
            LOG.debug("Handling by container pattern");
            check &= containerPatternHandler.handleCreate(templateContext, nodeTemplate,
                                                          templateContext.getProvisioningPhaseElement());

            if (containerPatternHandler.isDeprovisionableByContainerPattern(nodeTemplate)) {
                LOG.debug("Adding container pattern compensation logic");
                check &=
                    containerPatternHandler.handleTerminate(templateContext, nodeTemplate,
                                                            templateContext.getProvisioningCompensationPhaseElement());
            }
        } else if (lifecyclePatternHandler.isProvisionableByLifecyclePattern(nodeTemplate)) {
            LOG.debug("Handling by lifecycle pattern");

            check &= lifecyclePatternHandler.handleCreate(templateContext, nodeTemplate,
                                                          templateContext.getProvisioningPhaseElement());

            if (lifecyclePatternHandler.isDeprovisionableByLifecyclePattern(nodeTemplate)) {
                LOG.debug("Adding lifecycle pattern compensation logic");
                check &=
                    lifecyclePatternHandler.handleTerminate(templateContext, nodeTemplate,
                                                            templateContext.getProvisioningCompensationPhaseElement());
            }
        } else if(remoteMgrHandler.isProvisionableByRemoteManagerPattern(nodeTemplate)) {
          LOG.debug("Handling by remote manager pattern");
          check &= remoteMgrHandler.handleCreate(templateContext, nodeTemplate, templateContext.getPrePhaseElement());
        } else {
            return false;
        }
        return check;
    }

    @Override
    public boolean canHandleCreate(final AbstractNodeTemplate nodeTemplate) {
        LOG.debug("Checking if nodeTemplate {} can be handled by container or lifecycle pattern", nodeTemplate.getId());
        if (containerPatternHandler.isProvisionableByContainerPattern(nodeTemplate)) {
            LOG.debug("Can be handled by container pattern");
            return true;
        } else if (lifecyclePatternHandler.isProvisionableByLifecyclePattern(nodeTemplate)) {
            LOG.debug("Can be handled by lifecycle pattern");
            return true;
        } else if(remoteMgrHandler.isProvisionableByRemoteManagerPattern(nodeTemplate)) {
            LOG.debug("Can be handled by remote mgr pattern");
            return true;
        } else {
            LOG.debug("Can't be handled by pattern plugin");
            return false;
        }
    }

    @Override
    public boolean canHandleCreate(final AbstractRelationshipTemplate relationshipTemplate) {
        // can only handle node templates
        return false;
    }

    @Override
    public boolean handleCreate(BPELPlanContext templateContext, AbstractRelationshipTemplate relationshipTemplate) {
        return false;
    }

    @Override
    public Collection<AbstractNodeTemplate> getCreateDependencies(AbstractNodeTemplate nodeTemplate) {
        Collection<AbstractNodeTemplate> deps = new HashSet<AbstractNodeTemplate>();
        LOG.debug("Checking nodeTemplate {} dependencies", nodeTemplate.getId());
        if (containerPatternHandler.isProvisionableByContainerPattern(nodeTemplate)) {
            LOG.debug("Can be handled by container pattern");
            deps.add(containerPatternHandler.getHostingNode(nodeTemplate));
            LOG.debug("returning hosting node {} as dependency", deps.iterator().next().getId());
            return deps;
        } else if (lifecyclePatternHandler.isProvisionableByLifecyclePattern(nodeTemplate)) {
            LOG.debug("Can be handled by lifecycle pattern");
            deps.addAll(lifecyclePatternHandler.getMatchedNodesForProvisioning(nodeTemplate));
            LOG.debug("Adding matched nodes to handle by lifecycle pattern");
            return deps;
        } else if(remoteMgrHandler.isProvisionableByRemoteManagerPattern(nodeTemplate)) {
            deps.addAll(remoteMgrHandler.getNodeDependencies(nodeTemplate));
            return deps;
        } else {
            LOG.debug("Can't be handled by pattern plugin");
            return null;
        }
    }

    @Override
    public Collection<AbstractNodeTemplate> getTerminateDependencies(AbstractNodeTemplate nodeTemplate) {
        Collection<AbstractNodeTemplate> deps = new HashSet<AbstractNodeTemplate>();
        LOG.debug("Checking nodeTemplate {} dependencies", nodeTemplate.getId());
        if (containerPatternHandler.isDeprovisionableByContainerPattern(nodeTemplate)) {
            LOG.debug("Can be handled by container pattern");
            deps.add(containerPatternHandler.getHostingNode(nodeTemplate));
            LOG.debug("returning hosting node {} as dependency", deps.iterator().next().getId());
            return deps;
        } else if (lifecyclePatternHandler.isDeprovisionableByLifecyclePattern(nodeTemplate)) {
            LOG.debug("Can be handled by lifecycle pattern");
            deps.addAll(lifecyclePatternHandler.getMatchedNodesForDeprovisioning(nodeTemplate));
            LOG.debug("Adding matched nodes to handle by lifecycle pattern");
            return deps;
        } else {
            LOG.debug("Can't be handled by pattern plugin");
            return null;
        }
    }

    @Override
    public boolean handleTerminate(BPELPlanContext templateContext, AbstractNodeTemplate nodeTemplate) {
        LOG.debug("Handling nodeTemplate {} by pattern", nodeTemplate.getId());
        boolean check = true;
        if (containerPatternHandler.isDeprovisionableByContainerPattern(nodeTemplate)) {
            LOG.debug("Handling by container pattern");
            check &= containerPatternHandler.handleTerminate(templateContext, nodeTemplate,
                                                             templateContext.getProvisioningPhaseElement());

            if (containerPatternHandler.isProvisionableByContainerPattern(nodeTemplate)) {
                LOG.debug("Adding container pattern compensation logic");
                check &=
                    containerPatternHandler.handleCreate(templateContext, nodeTemplate,
                                                         templateContext.getProvisioningCompensationPhaseElement());
            }

        } else if (lifecyclePatternHandler.isDeprovisionableByLifecyclePattern(nodeTemplate)) {
            LOG.debug("Handling by lifecycle pattern");
            check &= lifecyclePatternHandler.handleTerminate(templateContext, nodeTemplate,
                                                             templateContext.getProvisioningPhaseElement());
            if (lifecyclePatternHandler.isProvisionableByLifecyclePattern(nodeTemplate)) {
                LOG.debug("Adding lifecycle pattern compensation logic");
                check &=
                    lifecyclePatternHandler.handleCreate(templateContext, nodeTemplate,
                                                         templateContext.getProvisioningCompensationPhaseElement());
            }
        } else {
            return false;
        }
        return check;
    }

    @Override
    public boolean handleTerminate(BPELPlanContext templateContext, AbstractRelationshipTemplate relationshipTemplate) {
        // never handles relationshipTemplates
        return false;
    }

    @Override
    public boolean canHandleTerminate(AbstractNodeTemplate nodeTemplate) {
        LOG.debug("Checking if nodeTemplate {} can be handled by container or lifecycle pattern", nodeTemplate.getId());
        if (containerPatternHandler.isDeprovisionableByContainerPattern(nodeTemplate)) {
            LOG.debug("Can be handled by container pattern");
            return true;
        } else if (lifecyclePatternHandler.isDeprovisionableByLifecyclePattern(nodeTemplate)) {
            LOG.debug("Can be handled by lifecycle pattern");
            return true;
        } else {
            LOG.debug("Can't be handled by pattern plugin");
            return false;
        }
    }

    @Override
    public boolean canHandleTerminate(AbstractRelationshipTemplate relationshipTemplate) {
        // never handles relationshipTemplates
        return false;
    }

    @Override
    public int getPriority() {
        return 1;
    }

}
