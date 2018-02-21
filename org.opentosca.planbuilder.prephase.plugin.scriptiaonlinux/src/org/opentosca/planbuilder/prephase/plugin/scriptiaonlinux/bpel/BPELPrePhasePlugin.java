package org.opentosca.planbuilder.prephase.plugin.scriptiaonlinux.bpel;

import javax.xml.namespace.QName;

import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.model.tosca.AbstractDeploymentArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractImplementationArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.prephase.plugin.scriptiaonlinux.bpel.handler.BPELPrePhasePluginHandler;
import org.opentosca.planbuilder.prephase.plugin.scriptiaonlinux.core.PrePhasePlugin;
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
public class BPELPrePhasePlugin extends PrePhasePlugin<BPELPlanContext> {

    private final static Logger LOG = LoggerFactory.getLogger(BPELPrePhasePlugin.class);

    private final QName warArtifactTypeOld = new QName("http://www.example.com/ToscaTypes", "WAR");
    private final QName warArtifactType = new QName("http://opentosca.org/artifacttypes", "WAR");
    private final QName dockerContainerArtefactTypeOld = new QName("http://opentosca.org/artefacttypes",
        "DockerContainerArtefact");
    private final QName dockerContainerArtefactType = new QName("http://opentosca.org/artifacttypes",
        "DockerContainerArtifact");

    private final BPELPrePhasePluginHandler handler = new BPELPrePhasePluginHandler();

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean handle(final BPELPlanContext context, final AbstractDeploymentArtifact da,
                    final AbstractNodeTemplate nodeTemplate) {

        if (da.getArtifactType().equals(this.dockerContainerArtefactType)
            || da.getArtifactType().equals(this.dockerContainerArtefactTypeOld)) {
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
        return type.equals(this.warArtifactType) || type.equals(this.warArtifactTypeOld);
    }

}
