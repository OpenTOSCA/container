package org.opentosca.planbuilder.prephase.plugin.fileupload.bpel;

import java.util.Collection;
import java.util.HashSet;

import javax.xml.namespace.QName;

import org.opentosca.container.core.convention.Types;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.plugins.artifactbased.IPlanBuilderPrePhaseDAPlugin;
import org.opentosca.planbuilder.core.plugins.artifactbased.IPlanBuilderPrePhaseIAPlugin;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderPrePhasePlugin;
import org.opentosca.planbuilder.model.tosca.AbstractDeploymentArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractImplementationArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractNodeType;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
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
    public boolean handle(final BPELPlanContext context, final AbstractDeploymentArtifact da,
                          final AbstractNodeTemplate nodeTemplate) {

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
    public boolean handle(final BPELPlanContext context, final AbstractImplementationArtifact ia,
                          final AbstractNodeTemplate nodeTemplate) {
        final QName type = ia.getArtifactType();
        return type.equals(warArtifactType) || type.equals(warArtifactTypeOld);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canHandle(final AbstractDeploymentArtifact deploymentArtifact,
                             final AbstractNodeType infrastructureNodeType) {
        for (final QName artType : ModelUtils.getArtifactTypeHierarchy(deploymentArtifact.getArtifactRef())) {
            for (final QName nodeType : ModelUtils.getNodeTypeHierarchy(infrastructureNodeType)) {
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
    public boolean canHandle(final AbstractImplementationArtifact ia, final AbstractNodeType infrastructureNodeType) {
        for (final QName artType : ModelUtils.getArtifactTypeHierarchy(ia.getArtifactRef())) {
            for (final QName nodeType : ModelUtils.getNodeTypeHierarchy(infrastructureNodeType)) {
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
            && (BPELPrePhasePlugin.warArtifactType.equals(artifactType)
            || BPELPrePhasePlugin.warArtifactTypeOld.equals(artifactType))
            && infrastructureNodeType
            .equals(new QName("http://opentosca.org/nodetypes", "TOSCAManagmentInfrastructure"))) {
            // WARs are deployed as environment-centric artifacts -> doesn't
            // need to be deployed on a node inside the topology, instead we
            // install it inside the management infrastructure
            return true;
        }

        if (!org.opentosca.container.core.convention.Utils
            .isSupportedInfrastructureNodeType(infrastructureNodeType)) {
            return false;
        }

        boolean isSupportedArtifactType = false;

        if (BPELPrePhasePlugin.jarArtifactType.equals(artifactType)) {
            isSupportedArtifactType |= true;
        }

        if (BPELPrePhasePlugin.archiveArtifactType.equals(artifactType)) {
            isSupportedArtifactType |= true;
        }

        if (BPELPrePhasePlugin.scriptArtifactType.equals(artifactType)) {
            isSupportedArtifactType |= true;
        }

        if (BPELPrePhasePlugin.ansibleArtifactType.equals(artifactType)) {
            isSupportedArtifactType |= true;
        }

        if (BPELPrePhasePlugin.chefArtifactType.equals(artifactType)) {
            isSupportedArtifactType |= true;
        }

        if (BPELPrePhasePlugin.bpelArchiveArtifactType.equals(artifactType)) {
            isSupportedArtifactType |= true;
        }

        if (BPELPrePhasePlugin.warArtifactType.equals(artifactType)) {
            isSupportedArtifactType |= true;
        }

        if (BPELPrePhasePlugin.warArtifactTypeOld.equals(artifactType)) {
            isSupportedArtifactType |= true;
        }

        if (BPELPrePhasePlugin.sqlArtifactType.equals(artifactType)) {
            isSupportedArtifactType |= true;
        }

        if (BPELPrePhasePlugin.configurationArtifactType.equals(artifactType)) {
            isSupportedArtifactType |= true;
        }

        if (BPELPrePhasePlugin.dockerContainerArtefactTypeOld.equals(artifactType)) {
            isSupportedArtifactType |= true;
        }

        // if (BPELPrePhasePlugin.dockerContainerArtefactType.equals(artifactType)) {
        // isSupportedArtifactType |= true;
        // }

        if (BPELPrePhasePlugin.tdlConfigurationArtifactType.equals(artifactType)) {
            isSupportedArtifactType |= true;
        }

        // We always support state artifacts.
        if (BPELPrePhasePlugin.stateArtifactType.equals(artifactType)) {
            isSupportedArtifactType |= true;
        }

        // we can deploy on debian nodes (ubuntu, rasbpian, docker containers based on
        // debian,..)

        return isSupportedArtifactType;
    }

    @Override
    public boolean canHandleCreate(final AbstractNodeTemplate nodeTemplate) {
        LOG.debug("Checking if DAs of node template {} can be deployed", nodeTemplate.getId());
        // Find infrastructures of this node and check if we can deploy all of its DA's
        for (final AbstractDeploymentArtifact da : nodeTemplate.getDeploymentArtifacts()) {
            if (getDeployableInfrastructureNode(nodeTemplate, da) == null) {
                LOG.debug("DAs of node template {} can't be deployed", nodeTemplate.getId());
                return false;
            }
        }
        LOG.debug("DAs of node template {} can be deployed", nodeTemplate.getId());
        return true;
    }

    public AbstractNodeTemplate getDeployableInfrastructureNode(final AbstractNodeTemplate nodeToDeploy,
                                                                final AbstractDeploymentArtifact da) {
        final Collection<AbstractNodeTemplate> infraNodes = new HashSet<>();
        ModelUtils.getInfrastructureNodes(nodeToDeploy, infraNodes);
        for (final AbstractNodeTemplate node : infraNodes) {
            for (final QName artType : ModelUtils.getArtifactTypeHierarchy(da.getArtifactRef())) {
                for (final QName nodeType : ModelUtils.getNodeTypeHierarchy(node.getType())) {
                    if (isSupportedDeploymentPair(artType, nodeType, true)) {
                        return node;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public boolean handleCreate(final BPELPlanContext context, final AbstractNodeTemplate nodeTemplate) {
        boolean handle = true;
        for (final AbstractDeploymentArtifact da : nodeTemplate.getDeploymentArtifacts()) {
            final AbstractNodeTemplate infraNode = getDeployableInfrastructureNode(nodeTemplate, da);
            handle &= this.handler.handle(context, da, infraNode);
        }
        return handle;
    }

    @Override
    public boolean canHandleCreate(final AbstractRelationshipTemplate relationshipTemplate) {
        return false;
    }

    @Override
    public boolean handleCreate(final BPELPlanContext context,
                                final AbstractRelationshipTemplate relationshipTemplate) {
        return false;
    }

    @Override
    public int getPriority() {
        return 1;
    }
}
