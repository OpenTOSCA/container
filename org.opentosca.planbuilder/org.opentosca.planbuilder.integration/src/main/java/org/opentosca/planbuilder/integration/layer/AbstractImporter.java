package org.opentosca.planbuilder.integration.layer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.namespace.QName;

import com.google.common.collect.Lists;
import org.opentosca.container.core.service.CsarStorageService;
import org.opentosca.planbuilder.core.AbstractSimplePlanBuilder;
import org.opentosca.planbuilder.core.bpel.typebasedplanbuilder.BPELBackupManagementProcessBuilder;
import org.opentosca.planbuilder.core.bpel.typebasedplanbuilder.BPELBuildProcessBuilder;
import org.opentosca.planbuilder.core.bpel.typebasedplanbuilder.BPELDefrostProcessBuilder;
import org.opentosca.planbuilder.core.bpel.typebasedplanbuilder.BPELFreezeProcessBuilder;
import org.opentosca.planbuilder.core.bpel.typebasedplanbuilder.BPELScaleOutProcessBuilder;
import org.opentosca.planbuilder.core.bpel.typebasedplanbuilder.BPELSituationAwareBuildProcessBuilder;
import org.opentosca.planbuilder.core.bpel.typebasedplanbuilder.BPELTerminationProcessBuilder;
import org.opentosca.planbuilder.core.bpel.typebasedplanbuilder.BPELTestManagementProcessBuilder;
import org.opentosca.planbuilder.core.bpel.typebasedplanbuilder.BPELTransformationProcessBuilder;
import org.opentosca.planbuilder.core.plugins.registry.PluginRegistry;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;

/**
 * <p>
 * This abstract class is used to define importers
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 * @author Jan Ruthardt - st107755@stud.uni-stuttgart.de
 */
public abstract class AbstractImporter {

    private final PluginRegistry pluginRegistry;
    protected final CsarStorageService storage;

    protected AbstractImporter(PluginRegistry pluginRegistry, CsarStorageService storage) {
        this.pluginRegistry = pluginRegistry;
        this.storage = storage;
    }

    protected AbstractPlan buildAdaptationPlan(final String csarName, final AbstractDefinitions definitions,
                                               final QName serviceTemplateId,
                                               final Collection<AbstractNodeTemplate> sourceNodeTemplates,
                                               final Collection<AbstractRelationshipTemplate> sourceRelationshipTemplates,
                                               final Collection<AbstractNodeTemplate> targetNodeTemplates,
                                               final Collection<AbstractRelationshipTemplate> targetRelationshipTemplates) {
        final BPELTransformationProcessBuilder transformPlanBuilder = new BPELTransformationProcessBuilder(pluginRegistry);

        return transformPlanBuilder.buildPlan(csarName, definitions, serviceTemplateId, sourceNodeTemplates,
            sourceRelationshipTemplates, targetNodeTemplates,
            targetRelationshipTemplates);
    }

    protected List<AbstractPlan> buildTransformationPlans(final String sourceCsarName,
                                                          final AbstractDefinitions sourceDefinitions,
                                                          final String targetCsarName,
                                                          final AbstractDefinitions targetDefinitions) {
        final List<AbstractPlan> plans = new ArrayList<>();

        final BPELTransformationProcessBuilder transformPlanBuilder = new BPELTransformationProcessBuilder(pluginRegistry);

        plans.add(transformPlanBuilder.buildPlan(sourceCsarName, sourceDefinitions,
            Lists.newArrayList(sourceDefinitions.getServiceTemplates()).get(0).getQName(),
            targetCsarName, targetDefinitions,
            Lists.newArrayList(targetDefinitions.getServiceTemplates()).get(0).getQName()));

        return plans;
    }

    /**
     * Generates Plans for ServiceTemplates inside the given Definitions document
     *
     * @param defs     an AbstractDefinitions
     * @param csarName the FileName of the CSAR the given Definitions is contained in
     * @return a List of Plans
     */
    public List<AbstractPlan> buildPlans(final AbstractDefinitions defs, final String csarName) {

        final List<AbstractPlan> plans = new ArrayList<>();

        AbstractSimplePlanBuilder buildPlanBuilder = new BPELBuildProcessBuilder(pluginRegistry);
        final BPELSituationAwareBuildProcessBuilder sitAwareBuilder = new BPELSituationAwareBuildProcessBuilder(pluginRegistry);

        if (!sitAwareBuilder.buildPlans(csarName, defs).isEmpty()) {
            buildPlanBuilder = sitAwareBuilder;
        }

        // FIXME: This does not work for me (Michael W. - 2018-02-19)
        // if (!this.hasPolicies(defs)) {
        // buildPlanBuilder = new BPELBuildProcessBuildeplanr();
        // Because policies must be enforced when they are set on the the topology, if
        // the planbuilder doesn't understand them it doesn't generate a plan -> doesn't
        // work for you
        //
        // if (!this.hasPolicies(defs)) {
        // buildPlanBuilder = new BPELBuildProcessBuilder();
        // } else {
        // buildPlanBuilder = new BPELPolicyAwareBuildProcessBuilder();
        // }

        final AbstractSimplePlanBuilder terminationPlanBuilder = new BPELTerminationProcessBuilder(pluginRegistry);
        final AbstractSimplePlanBuilder scalingPlanBuilder = new BPELScaleOutProcessBuilder(pluginRegistry);

        final AbstractSimplePlanBuilder freezePlanBuilder = new BPELFreezeProcessBuilder(pluginRegistry);
        final AbstractSimplePlanBuilder defreezePlanBuilder = new BPELDefrostProcessBuilder(pluginRegistry);

        final AbstractSimplePlanBuilder backupPlanBuilder = new BPELBackupManagementProcessBuilder(pluginRegistry);
        final AbstractSimplePlanBuilder testPlanBuilder = new BPELTestManagementProcessBuilder(pluginRegistry);

        plans.addAll(scalingPlanBuilder.buildPlans(csarName, defs));
        plans.addAll(buildPlanBuilder.buildPlans(csarName, defs));
        plans.addAll(terminationPlanBuilder.buildPlans(csarName, defs));
        plans.addAll(freezePlanBuilder.buildPlans(csarName, defs));
        plans.addAll(defreezePlanBuilder.buildPlans(csarName, defs));
        plans.addAll(backupPlanBuilder.buildPlans(csarName, defs));
        plans.addAll(testPlanBuilder.buildPlans(csarName, defs));

        return plans;
    }
}
