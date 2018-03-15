package org.opentosca.planbuilder.prephase.plugin.scriptiaonlinux.core;

import javax.xml.namespace.QName;

import org.opentosca.planbuilder.core.plugins.IPlanBuilderPrePhaseDAPlugin;
import org.opentosca.planbuilder.core.plugins.IPlanBuilderPrePhaseIAPlugin;
import org.opentosca.planbuilder.core.plugins.context.PlanContext;
import org.opentosca.planbuilder.model.tosca.AbstractDeploymentArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractImplementationArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractNodeType;
import org.opentosca.planbuilder.model.utils.ModelUtils;
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
 *
 */
public abstract class PrePhasePlugin<T extends PlanContext>
                                    implements IPlanBuilderPrePhaseIAPlugin<T>, IPlanBuilderPrePhaseDAPlugin<T> {

    private static final Logger LOG = LoggerFactory.getLogger(PrePhasePlugin.class);
    private static final String PLUGIN_ID = "openTOSCA DA/IA On Linux Plugin v0.1";

    private static final QName scriptArtifactType =
        new QName("http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes", "ScriptArtifact");
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

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canHandle(final AbstractDeploymentArtifact deploymentArtifact,
                             final AbstractNodeType infrastructureNodeType) {
        for (final QName artType : ModelUtils.getArtifactTypeHierarchy(deploymentArtifact.getArtifactRef())) {
            for (final QName nodeType : ModelUtils.getNodeTypeHierarchy(infrastructureNodeType)) {
                PrePhasePlugin.LOG.debug("Checking if type: " + artType.toString() + " and infrastructure nodeType: "
                    + nodeType.toString() + " can be handled");

                if (this.isSupportedDeploymentPair(artType, nodeType, true)) {
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
                PrePhasePlugin.LOG.debug("Checking if type: " + artType.toString() + " and infrastructure nodeType: "
                    + nodeType.toString() + " can be handled");
                if (this.isSupportedDeploymentPair(artType, nodeType, false)) {
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
     * Checks whether this Plugin can handle deploying artifacts of the given artfiactType to a given
     * InfrastructureNode of the given infrastructureNodeType
     *
     * @param scriptArtifactType a QName denoting an scriptArtifactType
     * @param infrastructureNodeType a QName denoting an infrastructureNodeType
     * @param isDA indicates whether this check is on an IA or DA with the given artifactType
     * @return a Boolean. True if given pair of QName's denotes a pair which this plugin can handle
     */
    private boolean isSupportedDeploymentPair(final QName artifactType, final QName infrastructureNodeType,
                                              final boolean isDA) {

        if (!isDA
            && (PrePhasePlugin.warArtifactType.equals(artifactType)
                || PrePhasePlugin.warArtifactTypeOld.equals(artifactType))
            && infrastructureNodeType.equals(new QName("http://opentosca.org/nodetypes",
                "TOSCAManagmentInfrastructure"))) {
            // WARs are deployed as environment-centric artifacts -> doesn't
            // need to be deployed on a node inside the topology, instead we
            // install it inside the management infrastructure
            return true;
        }

        if (!org.opentosca.container.core.tosca.convention.Utils.isSupportedInfrastructureNodeType(infrastructureNodeType)) {
            return false;
        }

        boolean isSupportedArtifactType = false;

        if (PrePhasePlugin.archiveArtifactType.equals(artifactType)) {
            isSupportedArtifactType |= true;
        }

        if (PrePhasePlugin.scriptArtifactType.equals(artifactType)) {
            isSupportedArtifactType |= true;
        }

        if (PrePhasePlugin.ansibleArtifactType.equals(artifactType)) {
            isSupportedArtifactType |= true;
        }

        if (PrePhasePlugin.chefArtifactType.equals(artifactType)) {
            isSupportedArtifactType |= true;
        }

        if (PrePhasePlugin.bpelArchiveArtifactType.equals(artifactType)) {
            isSupportedArtifactType |= true;
        }

        if (PrePhasePlugin.warArtifactType.equals(artifactType)) {
            isSupportedArtifactType |= true;
        }

        if (PrePhasePlugin.warArtifactTypeOld.equals(artifactType)) {
            isSupportedArtifactType |= true;
        }

        if (PrePhasePlugin.sqlArtifactType.equals(artifactType)) {
            isSupportedArtifactType |= true;
        }

        if (PrePhasePlugin.configurationArtifactType.equals(artifactType)) {
            isSupportedArtifactType |= true;
        }

        if (PrePhasePlugin.dockerContainerArtefactTypeOld.equals(artifactType)) {
            isSupportedArtifactType |= true;
        }

        if (PrePhasePlugin.dockerContainerArtefactType.equals(artifactType)) {
            isSupportedArtifactType |= true;
        }

        if (PrePhasePlugin.tdlConfigurationArtifactType.equals(artifactType)) {
            isSupportedArtifactType |= true;
        }

        // we can deploy on debian nodes (ubuntu, rasbpian, docker containers based on
        // debian,..)

        return isSupportedArtifactType;
    }

}
