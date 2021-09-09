package org.opentosca.planbuilder.type.plugin.patternbased.bpel;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.winery.model.tosca.TOperation;

import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderTypePlugin;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Copyright 2018 IAAS University of Stuttgart <br>
 * <br>
 *
 * <p>
 * This class implements a PlanBuilder Type Plugin for the provisioning of NodeType that are modeled based on
 * provisioning patterns.
 * </p>
 *
 * @author Kalman Kepes - kalman.kepes@iaas.uni-stuttgart.de
 */
public class PatternBasedPlugin implements IPlanBuilderTypePlugin<BPELPlanContext>,
    IPlanBuilderTypePlugin.NodeDependencyInformationInterface {

    private final static Logger LOG = LoggerFactory.getLogger(PatternBasedPlugin.class);

    private static final String id = "OpenTOSCA PlanBuilder Type Plugin Pattern-Based Provisioning";

    private static final ContainerPatternBasedHandler containerPatternHandler = new ContainerPatternBasedHandler();

    private static final LifecyclePatternBasedHandler lifecyclePatternHandler = new LifecyclePatternBasedHandler();

    private static final RemoteManagerPatternBasedHandler remoteMgrHandler = new RemoteManagerPatternBasedHandler();

    private static final RemoteManagerPatternBasedHandler remoteManagerPatternHandler =
        new RemoteManagerPatternBasedHandler();

    @Override
    public String getID() {
        return id;
    }

    @Override
    public boolean handleCreate(final BPELPlanContext templateContext, AbstractNodeTemplate nodeTemplate) {
        LOG.debug("Handling nodeTemplate {} by pattern", nodeTemplate.getId());
        boolean check = true;
        Map<TOperation, TOperation> usedOps = new HashMap<TOperation, TOperation>();
        if (containerPatternHandler.isProvisionableByContainerPattern(nodeTemplate, templateContext.getCsar())) {
            TOperation createOp = null;
            TOperation terminateOp = null;
            LOG.debug("Handling by container pattern");
            check &= containerPatternHandler.handleCreate(templateContext, nodeTemplate,
                templateContext.getProvisioningPhaseElement(), templateContext.getCsar());
            createOp = containerPatternHandler.getContainerPatternCreateMethod(nodeTemplate);
            usedOps.put(createOp, null);
            if (containerPatternHandler.isDeprovisionableByContainerPattern(nodeTemplate, templateContext.getCsar())) {
                LOG.debug("Adding container pattern compensation logic");
                check &=
                    containerPatternHandler.handleTerminate(templateContext, nodeTemplate,
                        templateContext.getProvisioningCompensationPhaseElement(), templateContext.getCsar());
                terminateOp = containerPatternHandler.getContainerPatternTerminateMethod(nodeTemplate);
                usedOps.put(createOp, terminateOp);
            }
        } else if (lifecyclePatternHandler.isProvisionableByLifecyclePattern(nodeTemplate, templateContext.getCsar())) {
            LOG.debug("Handling by lifecycle pattern");

            TOperation installOp = null;
            TOperation configureOp = null;
            TOperation startOp = null;
            TOperation uninstallOp = null;
            TOperation stopOp = null;

            check &= lifecyclePatternHandler.handleCreate(templateContext, nodeTemplate,
                templateContext.getProvisioningPhaseElement());

            installOp = lifecyclePatternHandler.getLifecyclePatternInstallMethod(nodeTemplate);
            configureOp = lifecyclePatternHandler.getLifecyclePatternConfigureMethod(nodeTemplate);
            startOp = lifecyclePatternHandler.getLifecyclePatternStartMethod(nodeTemplate);
            if (installOp != null) {
                usedOps.put(installOp, null);
            }
            if (configureOp != null) {
                usedOps.put(configureOp, null);
            }
            if (startOp != null) {
                usedOps.put(startOp, null);
            }

            if (lifecyclePatternHandler.isDeprovisionableByLifecyclePattern(nodeTemplate, templateContext.getCsar())) {
                LOG.debug("Adding lifecycle pattern compensation logic");
                check &=
                    lifecyclePatternHandler.handleTerminate(templateContext, nodeTemplate,
                        templateContext.getProvisioningCompensationPhaseElement());
                stopOp = lifecyclePatternHandler.getLifecyclePatternStopMethod(nodeTemplate);
                uninstallOp = lifecyclePatternHandler.getLifecyclePatternUninstallMethod(nodeTemplate);
                if (installOp != null & uninstallOp != null) {
                    usedOps.put(installOp, uninstallOp);
                }
                if (startOp != null & stopOp != null) {
                    usedOps.put(startOp, stopOp);
                }
            }
        } else if (remoteMgrHandler.isProvisionableByRemoteManagerPattern(nodeTemplate)) {
            LOG.debug("Handling by remote manager pattern");
            check &= remoteMgrHandler.handleCreate(templateContext, nodeTemplate, templateContext.getProvisioningPhaseElement());

            if (check == true) {
                TOperation installOp = remoteMgrHandler.getRemoteManagerPatternInstallMethod(nodeTemplate);
                TOperation resetOp = remoteMgrHandler.getRemoteManagerPatternResetMethod(nodeTemplate);

                if (installOp != null & resetOp != null) {
                    usedOps.put(installOp, resetOp);
                }
            }
        } else {
            return false;
        }

        for (TOperation op : usedOps.keySet()) {
            templateContext.addUsedOperation(op, usedOps.get(op));
        }

        return check;
    }

    @Override
    public boolean canHandleCreate(Csar csar, final AbstractNodeTemplate nodeTemplate) {
        LOG.debug("Checking if nodeTemplate {} can be handled by container or lifecycle pattern", nodeTemplate.getId());
        if (containerPatternHandler.isProvisionableByContainerPattern(nodeTemplate, csar)) {
            LOG.debug("Can be handled by container pattern");
            return true;
        } else if (lifecyclePatternHandler.isProvisionableByLifecyclePattern(nodeTemplate, csar)) {
            LOG.debug("Can be handled by lifecycle pattern");
            return true;
        } else if (remoteMgrHandler.isProvisionableByRemoteManagerPattern(nodeTemplate)) {
            LOG.debug("Can be handled by remote mgr pattern");
            return true;
        } else {
            LOG.debug("Can't be handled by pattern plugin");
            return false;
        }
    }

    @Override
    public boolean canHandleCreate(Csar csar, final AbstractRelationshipTemplate relationshipTemplate) {
        // can only handle node templates
        return false;
    }

    @Override
    public boolean handleCreate(BPELPlanContext templateContext, AbstractRelationshipTemplate relationshipTemplate) {
        return false;
    }

    @Override
    public Collection<AbstractNodeTemplate> getCreateDependencies(AbstractNodeTemplate nodeTemplate, Csar csar) {
        Collection<AbstractNodeTemplate> deps = new HashSet<AbstractNodeTemplate>();
        LOG.debug("Checking nodeTemplate {} dependencies", nodeTemplate.getId());
        if (containerPatternHandler.isProvisionableByContainerPattern(nodeTemplate, csar)) {
            LOG.debug("Can be handled by container pattern");
            deps.add(containerPatternHandler.getHostingNode(nodeTemplate, csar));
            LOG.debug("returning hosting node {} as dependency", deps.iterator().next().getId());
            return deps;
        } else if (lifecyclePatternHandler.isProvisionableByLifecyclePattern(nodeTemplate, csar)) {
            LOG.debug("Can be handled by lifecycle pattern");
            deps.addAll(lifecyclePatternHandler.getMatchedNodesForProvisioning(nodeTemplate, csar));
            LOG.debug("Adding matched nodes to handle by lifecycle pattern");
            return deps;
        } else if (remoteMgrHandler.isProvisionableByRemoteManagerPattern(nodeTemplate)) {
            deps.addAll(remoteMgrHandler.getNodeDependencies(nodeTemplate));
            return deps;
        } else {
            LOG.debug("Can't be handled by pattern plugin");
            return null;
        }
    }

    @Override
    public Collection<AbstractNodeTemplate> getTerminateDependencies(AbstractNodeTemplate nodeTemplate, Csar csar) {
        Collection<AbstractNodeTemplate> deps = new HashSet<AbstractNodeTemplate>();
        LOG.debug("Checking nodeTemplate {} dependencies", nodeTemplate.getId());
        if (containerPatternHandler.isDeprovisionableByContainerPattern(nodeTemplate, csar)) {
            LOG.debug("Can be handled by container pattern");
            deps.add(containerPatternHandler.getHostingNode(nodeTemplate, csar));
            LOG.debug("returning hosting node {} as dependency", deps.iterator().next().getId());
            return deps;
        } else if (lifecyclePatternHandler.isDeprovisionableByLifecyclePattern(nodeTemplate, csar)) {
            LOG.debug("Can be handled by lifecycle pattern");
            deps.addAll(lifecyclePatternHandler.getMatchedNodesForDeprovisioning(nodeTemplate, csar));
            LOG.debug("Adding matched nodes to handle by lifecycle pattern");
            return deps;
        } else if (remoteManagerPatternHandler.isDeprovisionableByRemoteManagerPattern(nodeTemplate)) {
            LOG.debug("Can be handled by remote manager pattern");
            deps.addAll(remoteManagerPatternHandler.getMatchedNodesForDeprovisioning(nodeTemplate));
            LOG.debug("Adding matched nodes to handle by remote manager pattern");
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
        Map<TOperation, TOperation> usedOps = new HashMap<TOperation, TOperation>();
        if (containerPatternHandler.isDeprovisionableByContainerPattern(nodeTemplate, templateContext.getCsar())) {
            LOG.debug("Handling by container pattern");

            TOperation createOp = null;
            TOperation terminateOp = null;

            check &= containerPatternHandler.handleTerminate(templateContext, nodeTemplate,
                templateContext.getProvisioningPhaseElement(), templateContext.getCsar());
            terminateOp = containerPatternHandler.getContainerPatternTerminateMethod(nodeTemplate);
            usedOps.put(terminateOp, null);

            if (containerPatternHandler.isProvisionableByContainerPattern(nodeTemplate, templateContext.getCsar())) {
                LOG.debug("Adding container pattern compensation logic");
                check &=
                    containerPatternHandler.handleCreate(templateContext, nodeTemplate,
                        templateContext.getProvisioningCompensationPhaseElement(), templateContext.getCsar());
                createOp = containerPatternHandler.getContainerPatternCreateMethod(nodeTemplate);
                usedOps.put(terminateOp, createOp);
            }
        } else if (lifecyclePatternHandler.isDeprovisionableByLifecyclePattern(nodeTemplate, templateContext.getCsar())) {
            LOG.debug("Handling by lifecycle pattern");
            check &= lifecyclePatternHandler.handleTerminate(templateContext, nodeTemplate,
                templateContext.getProvisioningPhaseElement());

            TOperation installOp = null;
            TOperation configureOp = null;
            TOperation startOp = null;
            TOperation uninstallOp = null;
            TOperation stopOp = null;

            stopOp = lifecyclePatternHandler.getLifecyclePatternStopMethod(nodeTemplate);
            uninstallOp = lifecyclePatternHandler.getLifecyclePatternUninstallMethod(nodeTemplate);

            if (stopOp != null) {
                usedOps.put(stopOp, null);
            }

            if (uninstallOp != null) {
                usedOps.put(uninstallOp, null);
            }

            if (lifecyclePatternHandler.isProvisionableByLifecyclePattern(nodeTemplate, templateContext.getCsar())) {
                LOG.debug("Adding lifecycle pattern compensation logic");
                check &=
                    lifecyclePatternHandler.handleCreate(templateContext, nodeTemplate,
                        templateContext.getProvisioningCompensationPhaseElement());

                installOp = lifecyclePatternHandler.getLifecyclePatternInstallMethod(nodeTemplate);
                configureOp = lifecyclePatternHandler.getLifecyclePatternConfigureMethod(nodeTemplate);
                startOp = lifecyclePatternHandler.getLifecyclePatternStartMethod(nodeTemplate);

                if (installOp != null & uninstallOp != null) {
                    usedOps.put(uninstallOp, installOp);
                }
                if (startOp != null & stopOp != null) {
                    usedOps.put(stopOp, startOp);
                }
                if (configureOp != null) {
                    usedOps.put(null, configureOp);
                }
            }
        } else if (remoteManagerPatternHandler.isDeprovisionableByRemoteManagerPattern(nodeTemplate)) {
            check &= remoteManagerPatternHandler.handleTerminate(templateContext, nodeTemplate,
                templateContext.getProvisioningPhaseElement());

            TOperation installOp = null;
            TOperation resetOp = null;

            resetOp = remoteManagerPatternHandler.getRemoteManagerPatternResetMethod(nodeTemplate);
            installOp = remoteManagerPatternHandler.getRemoteManagerPatternInstallMethod(nodeTemplate);

            if (remoteManagerPatternHandler.isProvisionableByRemoteManagerPattern(nodeTemplate)) {
                LOG.debug("Adding compensation logic for remote manager pattern");
                check &=
                    remoteManagerPatternHandler.handleCreate(templateContext, nodeTemplate,
                        templateContext.getProvisioningCompensationPhaseElement());

                if (installOp != null & resetOp != null) {
                    usedOps.put(resetOp, installOp);
                }
            }
        } else {
            return false;
        }

        for (TOperation op : usedOps.keySet()) {
            templateContext.addUsedOperation(op, usedOps.get(op));
        }

        return check;
    }

    @Override
    public boolean handleTerminate(BPELPlanContext templateContext, AbstractRelationshipTemplate relationshipTemplate) {
        // never handles relationshipTemplates
        return false;
    }

    @Override
    public boolean canHandleTerminate(Csar csar, AbstractNodeTemplate nodeTemplate) {
        LOG.debug("Checking if nodeTemplate {} can be handled by container or lifecycle pattern", nodeTemplate.getId());
        if (containerPatternHandler.isDeprovisionableByContainerPattern(nodeTemplate, csar)) {
            LOG.debug("Can be handled by container pattern");
            return true;
        } else if (lifecyclePatternHandler.isDeprovisionableByLifecyclePattern(nodeTemplate, csar)) {
            LOG.debug("Can be handled by lifecycle pattern");
            return true;
        } else if (remoteManagerPatternHandler.isDeprovisionableByRemoteManagerPattern(nodeTemplate)) {
            LOG.debug("Can be handled by lifecycle pattern");
            return true;
        } else {
            LOG.debug("Can't be handled by pattern plugin");
            return false;
        }
    }

    @Override
    public boolean canHandleTerminate(Csar csar, AbstractRelationshipTemplate relationshipTemplate) {
        // never handles relationshipTemplates
        return false;
    }

    @Override
    public boolean canHandleUpdate(Csar csar, AbstractNodeTemplate nodeTemplate) {
        return true;
    }

    @Override
    public boolean handleUpdate(BPELPlanContext templateContext, AbstractNodeTemplate nodeTemplate) {
        return lifecyclePatternHandler.handleUpdate(templateContext, nodeTemplate,
            templateContext.getProvisioningPhaseElement(), templateContext.getCsar());
    }

    @Override
    public int getPriority() {
        return 1;
    }
}
