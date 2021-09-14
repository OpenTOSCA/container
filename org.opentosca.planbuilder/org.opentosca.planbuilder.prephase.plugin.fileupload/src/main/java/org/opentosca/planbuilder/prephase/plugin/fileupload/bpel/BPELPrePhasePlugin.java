package org.opentosca.planbuilder.prephase.plugin.fileupload.bpel;

import java.util.Collection;
import java.util.HashSet;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TImplementationArtifact;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;

import org.opentosca.container.core.convention.Types;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.plugins.artifactbased.IPlanBuilderPrePhaseDAPlugin;
import org.opentosca.planbuilder.core.plugins.artifactbased.IPlanBuilderPrePhaseIAPlugin;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderPrePhasePlugin;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.opentosca.planbuilder.prephase.plugin.fileupload.bpel.handler.BPELPrePhasePluginHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This class is a PrePhase Plugin for IAs of type {http://docs.oasis-open.org/tosca
 * /ns/2011/12/ToscaBaseTypes}ScriptArtifact,{http ://www.example.com/ToscaTypes}WAR and DAs of type
 * {http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes}ArchiveArtifact
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public class BPELPrePhasePlugin implements IPlanBuilderPrePhasePlugin<BPELPlanContext>,
    IPlanBuilderPrePhaseIAPlugin<BPELPlanContext>, IPlanBuilderPrePhaseDAPlugin<BPELPlanContext> {

    private final static Logger LOG = LoggerFactory.getLogger(BPELPrePhasePlugin.class);

    private static final String PLUGIN_ID = "openTOSCA DA/IA On Linux Plugin v0.1";

    private static final QName scriptArtifactType =
        new QName("http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes", "ScriptArtifact");
    private static final QName jarArtifactType = new QName("http://opentosca.org/artifacttypes", "JAR");
    private static final QName archiveArtifactType =
        new QName("http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes", "ArchiveArtifact");
    private static final QName bpelArchiveArtifactType =
        new QName("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "BPEL");
    private static final QName warArtifactTypeOld = new QName("http://www.example.com/ToscaTypes", "WAR");
    private static final QName warArtifactType = new QName("http://opentosca.org/artifacttypes", "WAR");
    private static final QName sqlArtifactType = new QName("http://opentosca.org/artifacttypes", "SQLArtifact");
    private static final QName configurationArtifactType =
        new QName("http://opentosca.org/artifacttypes", "ConfigurationArtifact");
    private static final QName tdlConfigurationArtifactType =
        new QName("http://opentosca.org/artifacttypes", "TDLArtifact");

    private static final QName ansibleArtifactType = new QName("http://opentosca.org/artifacttypes", "Ansible");
    private static final QName chefArtifactType = new QName("http://opentosca.org/artifacttypes", "Chef");
    private static final QName dockerContainerArtefactTypeOld =
        new QName("http://opentosca.org/artefacttypes", "DockerContainerArtefact");
    private static final QName dockerContainerArtefactType =
        new QName("http://opentosca.org/artifacttypes", "DockerContainerArtifact");
    private static final QName stateArtifactType = new QName("http://opentosca.org/artifacttypes", "State");

    private final BPELPrePhasePluginHandler handler = new BPELPrePhasePluginHandler();

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean handle(final BPELPlanContext context, final TDeploymentArtifact da,
                          final TNodeTemplate nodeTemplate) {

        if (da.getArtifactType().equals(dockerContainerArtefactType)
            || da.getArtifactType().equals(dockerContainerArtefactTypeOld)) {
            return true;
        }

        return this.handler.handle(context, da, nodeTemplate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean handle(final BPELPlanContext context, final TImplementationArtifact ia,
                          final TNodeTemplate nodeTemplate) {
        final QName type = ia.getArtifactType();
        return type.equals(warArtifactType) || type.equals(warArtifactTypeOld);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canHandle(BPELPlanContext context, final TDeploymentArtifact deploymentArtifact,
                             final TNodeType infrastructureNodeType) {

        for (final QName artType : ModelUtils.getArtifactTypeHierarchy(ModelUtils.findArtifactTemplate(deploymentArtifact.getArtifactRef(), context.getCsar()), context.getCsar())) {
            for (final QName nodeType : ModelUtils.getNodeTypeHierarchy(infrastructureNodeType, context.getCsar())) {
                BPELPrePhasePlugin.LOG.debug("Checking if type: " + artType.toString()
                    + " and infrastructure nodeType: " + nodeType.toString() + " can be handled");

                if (isSupportedDeploymentPair(artType, nodeType, true)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean canHandle(BPELPlanContext context, final TImplementationArtifact ia, final TNodeType infrastructureNodeType) {

        for (final QName artType : ModelUtils.getArtifactTypeHierarchy(ModelUtils.findArtifactTemplate(ia.getArtifactRef(), context.getCsar()), context.getCsar())) {
            for (final QName nodeType : ModelUtils.getNodeTypeHierarchy(infrastructureNodeType, context.getCsar())) {
                BPELPrePhasePlugin.LOG.debug("Checking if type: " + artType.toString()
                    + " and infrastructure nodeType: " + nodeType.toString() + " can be handled");
                if (isSupportedDeploymentPair(artType, nodeType, false)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getID() {
        return PLUGIN_ID;
    }

    /**
     * Checks whether this Plugin can handle deploying artifacts of the given artifactType to a given InfrastructureNode
     * of the given infrastructureNodeType
     *
     * @param artifactType           a QName denoting an scriptArtifactType
     * @param infrastructureNodeType a QName denoting an infrastructureNodeType
     * @param isDA                   indicates whether this check is on an IA or DA with the given artifactType
     * @return a Boolean. True if given pair of QName's denotes a pair which this plugin can handle
     */
    private boolean isSupportedDeploymentPair(final QName artifactType, final QName infrastructureNodeType,
                                              final boolean isDA) {

        if (infrastructureNodeType.equals(Types.dockerEngineNodeType)) {
            return false;
        }

        if (!isDA
            && (BPELPrePhasePlugin.warArtifactType.equals(artifactType) || BPELPrePhasePlugin.warArtifactTypeOld.equals(artifactType))
            && infrastructureNodeType.equals(
            new QName("http://opentosca.org/nodetypes", "TOSCAManagmentInfrastructure"))) {
            // WARs are deployed as environment-centric artifacts -> doesn't
            // need to be deployed on a node inside the topology, instead we
            // install it inside the management infrastructure
            return true;
        }

        if (!org.opentosca.container.core.convention.Utils
            .isSupportedInfrastructureNodeType(infrastructureNodeType)) {
            return false;
        }

        // we can deploy on debian nodes (ubuntu, rasbpian, docker containers based on
        // debian,..)
        return BPELPrePhasePlugin.jarArtifactType.equals(artifactType)
            || BPELPrePhasePlugin.archiveArtifactType.equals(artifactType)
            || BPELPrePhasePlugin.scriptArtifactType.equals(artifactType)
            || BPELPrePhasePlugin.ansibleArtifactType.equals(artifactType)
            || BPELPrePhasePlugin.chefArtifactType.equals(artifactType)
            || BPELPrePhasePlugin.bpelArchiveArtifactType.equals(artifactType)
            || BPELPrePhasePlugin.warArtifactType.equals(artifactType)
            || BPELPrePhasePlugin.warArtifactTypeOld.equals(artifactType)
            || BPELPrePhasePlugin.sqlArtifactType.equals(artifactType)
            || BPELPrePhasePlugin.configurationArtifactType.equals(artifactType)
            || BPELPrePhasePlugin.dockerContainerArtefactTypeOld.equals(artifactType)
            || BPELPrePhasePlugin.tdlConfigurationArtifactType.equals(artifactType)
            // We always support state artifacts.
            || BPELPrePhasePlugin.stateArtifactType.equals(artifactType);
    }

    @Override
    public boolean canHandleCreate(BPELPlanContext context, final TNodeTemplate nodeTemplate) {
        LOG.debug("Checking if DAs of node template {} can be deployed", nodeTemplate.getId());
        // Find infrastructures of this node and check if we can deploy all of its DA's
        if (nodeTemplate.getDeploymentArtifacts() == null) {
            // No DAs = we can work with that
            return true;
        }
        for (final TDeploymentArtifact da : nodeTemplate.getDeploymentArtifacts()) {
            if (getDeployableInfrastructureNode(nodeTemplate, da, context.getCsar()) == null) {
                LOG.debug("DAs of node template {} can't be deployed", nodeTemplate.getId());
                return false;
            }
        }
        LOG.debug("DAs of node template {} can be deployed", nodeTemplate.getId());
        return true;
    }

    public TNodeTemplate getDeployableInfrastructureNode(final TNodeTemplate nodeToDeploy,
                                                         final TDeploymentArtifact da, Csar csar) {
        final Collection<TNodeTemplate> infraNodes = new HashSet<>();
        ModelUtils.getInfrastructureNodes(nodeToDeploy, infraNodes, csar);
        for (final TNodeTemplate node : infraNodes) {
            for (final QName artType : ModelUtils.getArtifactTypeHierarchy(ModelUtils.findArtifactTemplate(da.getArtifactRef(), csar), csar)) {
                for (final QName nodeType : ModelUtils.getNodeTypeHierarchy(node.getType(), csar)) {
                    if (isSupportedDeploymentPair(artType, nodeType, true)) {
                        return node;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public boolean handleCreate(final BPELPlanContext context, final TNodeTemplate nodeTemplate) {
        boolean handle = true;

        if (nodeTemplate.getDeploymentArtifacts() == null) {
            return handle;
        }

        for (final TDeploymentArtifact da : nodeTemplate.getDeploymentArtifacts()) {
            final TNodeTemplate infraNode = getDeployableInfrastructureNode(nodeTemplate, da, context.getCsar());
            handle &= this.handler.handle(context, da, infraNode);
        }
        return handle;
    }

    @Override
    public boolean canHandleCreate(BPELPlanContext context, final TRelationshipTemplate relationshipTemplate) {
        return false;
    }

    @Override
    public boolean handleCreate(final BPELPlanContext context,
                                final TRelationshipTemplate relationshipTemplate) {
        return false;
    }

    @Override
    public int getPriority() {
        return 1;
    }
}
