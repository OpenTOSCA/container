package org.opentosca.planbuilder.type.plugin.patternbased.bpmn;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;

import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.bpmn.context.BPMNPlanContext;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderBPMNTypePlugin;
// import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderTypePlugin;
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
public class BPMNPatternBasedPlugin implements IPlanBuilderBPMNTypePlugin<BPMNPlanContext>,
    IPlanBuilderBPMNTypePlugin.NodeDependencyInformationInterface {

    private final static Logger LOG = LoggerFactory.getLogger(BPMNPatternBasedPlugin.class);

    private static final String id = "OpenTOSCA PlanBuilder Type Plugin BPMNPattern-Based Provisioning";

    private static final BPMNContainerPatternBasedHandler containerPatternHandler = new BPMNContainerPatternBasedHandler();

    private static final BPMNLifecyclePatternBasedHandler lifecyclePatternHandler = new BPMNLifecyclePatternBasedHandler();

    private static final BPMNRemoteManagerPatternBasedHandler remoteMgrHandler = new BPMNRemoteManagerPatternBasedHandler();

    private static final BPMNRemoteManagerPatternBasedHandler remoteManagerPatternHandler =
        new BPMNRemoteManagerPatternBasedHandler();

    @Override
    public String getID() {
        return id;
    }

    @Override
    public boolean handleCreate(final BPMNPlanContext templateContext, final TNodeTemplate nodeTemplate) {
        LOG.debug("Handling nodeTemplate {} by pattern", nodeTemplate.getId());
        LOG.info("im patternbased plugin (handle create)");
        LOG.info("nodeTemplate: " + nodeTemplate.getName());
        boolean check = true;
        Map<TOperation, TOperation> usedOps = new HashMap<>();
        // genau eins drunter ( geht die hosted on kante runter)
        if (containerPatternHandler.isProvisionableByContainerPattern(nodeTemplate, templateContext.getCsar())) {
            TOperation createOp = null;
            // TOperation terminateOp = null;
            LOG.debug("Handling by container pattern");
            LOG.info("Handling by container pattern");
            check &= containerPatternHandler.handleCreate(templateContext, nodeTemplate,
                templateContext.getSubprocessElement().getBpmnSubprocessElement(), templateContext.getCsar());
            createOp = containerPatternHandler.getContainerPatternCreateMethod(nodeTemplate, templateContext.getCsar());
            usedOps.put(createOp, null);
        } else if (lifecyclePatternHandler.isProvisionableByLifecyclePattern(nodeTemplate, templateContext.getCsar())) {
            LOG.debug("Handling by lifecycle pattern");
            LOG.info("Handling by lifecycle pattern");

            TOperation installOp = null;
            TOperation configureOp = null;
            TOperation startOp = null;

            check &= lifecyclePatternHandler.handleCreate(templateContext, nodeTemplate,
                templateContext.getSubprocessElement().getBpmnSubprocessElement());

            installOp = lifecyclePatternHandler.getLifecyclePatternInstallMethod(nodeTemplate, templateContext.getCsar());
            configureOp = lifecyclePatternHandler.getLifecyclePatternConfigureMethod(nodeTemplate, templateContext.getCsar());
            startOp = lifecyclePatternHandler.getLifecyclePatternStartMethod(nodeTemplate, templateContext.getCsar());
            if (installOp != null) {
                usedOps.put(installOp, null);
            }
            if (configureOp != null) {
                usedOps.put(configureOp, null);
            }
            if (startOp != null) {
                usedOps.put(startOp, null);
            }
        } else if (remoteMgrHandler.isProvisionableByRemoteManagerPattern(nodeTemplate, templateContext.getCsar())) {
            LOG.debug("Handling by remote manager pattern");
            LOG.info("Handling by remote manager pattern");
            check &= remoteMgrHandler.handleCreate(templateContext, nodeTemplate, templateContext.getSubprocessElement().getBpmnSubprocessElement());

            if (check) {
                TOperation installOp = remoteMgrHandler.getRemoteManagerPatternInstallMethod(nodeTemplate, templateContext.getCsar());
                TOperation resetOp = remoteMgrHandler.getRemoteManagerPatternResetMethod(nodeTemplate, templateContext.getCsar());

                if (installOp != null & resetOp != null) {
                    usedOps.put(installOp, resetOp);
                }
            }
        } else {
            LOG.info("plugin funktioniert nicht");
            return false;
        }
        return check;
    }

    @Override
    public boolean canHandleCreate(final Csar csar, final TNodeTemplate nodeTemplate) {
        LOG.info("Checking if nodeTemplate {} can be handled by container or lifecycle pattern", nodeTemplate.getId());
        if (containerPatternHandler.isProvisionableByContainerPattern(nodeTemplate, csar)) {
            LOG.debug("Can be handled by container pattern");
            LOG.info("Can be handled by lifecycle pattern");
            return true;
        } else if (lifecyclePatternHandler.isProvisionableByLifecyclePattern(nodeTemplate, csar)) {
            LOG.debug("Can be handled by lifecycle pattern");
            LOG.info("Can be handled by lifecycle pattern");
            return true;
        } else if (remoteMgrHandler.isProvisionableByRemoteManagerPattern(nodeTemplate, csar)) {
            LOG.debug("Can be handled by remote mgr pattern");
            LOG.info("Can be handled by remote mgr pattern");
            return true;
        } else {
            LOG.debug("Can't be handled by pattern plugin");
            LOG.info("Can't be handled by pattern plugin");
            return false;
        }
    }

    @Override
    public boolean canHandleCreate(final Csar csar, final TRelationshipTemplate relationshipTemplate) {
        // can only handle node templates
        return false;
    }

    @Override
    public boolean handleCreate(final BPMNPlanContext templateContext, final TRelationshipTemplate relationshipTemplate) {
        return false;
    }

    @Override
    public boolean handleTerminate(final BPMNPlanContext templateContext, final TRelationshipTemplate relationshipTemplate) {
        return false;
    }

    // gets used by hardware plugin
    @Override
    public Collection<TNodeTemplate> getCreateDependencies(final TNodeTemplate nodeTemplate, final Csar csar) {
        Collection<TNodeTemplate> deps = new HashSet<TNodeTemplate>();
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
        } else if (remoteMgrHandler.isProvisionableByRemoteManagerPattern(nodeTemplate, csar)) {
            deps.addAll(remoteMgrHandler.getNodeDependencies(nodeTemplate, csar));
            return deps;
        } else {
            LOG.debug("Can't be handled by pattern plugin");
            return null;
        }
    }

    @Override
    public Collection<TNodeTemplate> getTerminateDependencies(final TNodeTemplate nodeTemplate, final Csar csar) {
        Collection<TNodeTemplate> deps = new HashSet<>();
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
    public boolean handleTerminate(final BPMNPlanContext templateContext, final TNodeTemplate nodeTemplate) {
        LOG.debug("Handling nodeTemplate {} by pattern", nodeTemplate.getId());
        return false;
    }

    public boolean handleTerminate(final BPELPlanContext templateContext, final TRelationshipTemplate relationshipTemplate) {
        // never handles relationshipTemplates
        return false;
    }

    @Override
    public boolean canHandleTerminate(final Csar csar, final TNodeTemplate nodeTemplate) {
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
    public boolean canHandleTerminate(final Csar csar, final TRelationshipTemplate relationshipTemplate) {
        // never handles relationshipTemplates
        return false;
    }

    @Override
    public boolean canHandleUpdate(final Csar csar, final TNodeTemplate nodeTemplate) {
        return true;
    }

    @Override
    public boolean handleUpdate(final BPMNPlanContext templateContext, final TNodeTemplate nodeTemplate) {
        return lifecyclePatternHandler.handleUpdate(templateContext, nodeTemplate,
            templateContext.getSubprocessElement().getBpmnSubprocessElement(), templateContext.getCsar());
    }

    @Override
    public int getPriority() {
        return 1;
    }
}
