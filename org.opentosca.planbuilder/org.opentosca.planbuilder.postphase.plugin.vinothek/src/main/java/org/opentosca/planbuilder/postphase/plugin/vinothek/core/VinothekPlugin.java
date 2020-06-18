/**
 *
 */
package org.opentosca.planbuilder.postphase.plugin.vinothek.core;

import javax.xml.namespace.QName;

import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderPostPhasePlugin;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.utils.ModelUtils;

/**
 * Copyright 2014 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - nyuuyn@googlemail.com
 *
 */
public abstract class VinothekPlugin<T extends BPELPlanContext> implements IPlanBuilderPostPhasePlugin<T> {

    protected static final String PLUGIN_ID = "OpenTOSCA PlanBuilder PostPhase Plugin Vinothek";
    protected static final QName phpApp = new QName("http://opentosca.org/types/declarative", "PhpApplication");
    protected static final QName bpelProcess = new QName("http://opentosca.org/declarative/", "BPEL");
    protected final QName zipArtifactType =
        new QName("http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes", "ArchiveArtifact");

    @Override
    public boolean canHandleCreate(BPELPlanContext context, final AbstractNodeTemplate nodeTemplate) {
        // if the nodeTemplate is some kind of PhpApp we're happy
        return ModelUtils.checkForTypeInHierarchy(nodeTemplate, phpApp)
            || ModelUtils.checkForTypeInHierarchy(nodeTemplate, bpelProcess);
    }

    @Override
    public boolean canHandleCreate(BPELPlanContext context, final AbstractRelationshipTemplate relationshipTemplate) {
        // only handling nodeTemplates
        return false;
    }

    @Override
    public String getID() {
        return VinothekPlugin.PLUGIN_ID;
    }
}
