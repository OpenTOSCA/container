package org.opentosca.planbuilder.importer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;

import com.google.common.collect.Lists;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.model.ModelUtils;
import org.opentosca.container.core.model.csar.Csar;
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
import org.opentosca.planbuilder.core.bpel.typebasedplanbuilder.BPELUpdateProcessBuilder;
import org.opentosca.planbuilder.core.plugins.registry.PluginRegistry;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.springframework.stereotype.Service;

import static org.opentosca.container.core.convention.PlanConstants.OpenTOSCA_BackupPlanOperation;
import static org.opentosca.container.core.convention.PlanConstants.OpenTOSCA_BuildPlanOperation;
import static org.opentosca.container.core.convention.PlanConstants.OpenTOSCA_DefrostPlanOperation;
import static org.opentosca.container.core.convention.PlanConstants.OpenTOSCA_FreezePlanOperation;
import static org.opentosca.container.core.convention.PlanConstants.OpenTOSCA_LifecycleInterface;
import static org.opentosca.container.core.convention.PlanConstants.OpenTOSCA_ManagementFeatureInterface;
import static org.opentosca.container.core.convention.PlanConstants.OpenTOSCA_StatefulLifecycleInterface;
import static org.opentosca.container.core.convention.PlanConstants.OpenTOSCA_TerminationPlanOperation;
import static org.opentosca.container.core.convention.PlanConstants.OpenTOSCA_TestPlanOperation;
import static org.opentosca.container.core.convention.PlanConstants.OpenTOSCA_UpdatePlanOperation;

/**
 * <p>
 * This class is a PlanBuilder Importer for openTOSCA. Importing of CSARs is handled by passing a CSARID
 * </p>
 * Copyright 2013-2022 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
@Service
public class Importer {

    private final PluginRegistry pluginRegistry;

    @Inject
    public Importer(PluginRegistry pluginRegistry) {
        this.pluginRegistry = pluginRegistry;
    }

    protected AbstractPlan buildAdaptationPlan(final Csar csar, final TDefinitions definitions,
                                               final QName serviceTemplateId,
                                               final Collection<TNodeTemplate> sourceNodeTemplates,
                                               final Collection<TRelationshipTemplate> sourceRelationshipTemplates,
                                               final Collection<TNodeTemplate> targetNodeTemplates,
                                               final Collection<TRelationshipTemplate> targetRelationshipTemplates) {
        final BPELTransformationProcessBuilder transformPlanBuilder = new BPELTransformationProcessBuilder(pluginRegistry);

        return transformPlanBuilder.buildPlan(csar, definitions, serviceTemplateId, sourceNodeTemplates,
            sourceRelationshipTemplates, targetNodeTemplates,
            targetRelationshipTemplates);
    }

    protected List<AbstractPlan> buildTransformationPlans(final Csar sourceCsarName,
                                                          final TDefinitions sourceDefinitions,
                                                          final Csar targetCsarName,
                                                          final TDefinitions targetDefinitions) {
        final List<AbstractPlan> plans = new ArrayList<>();

        final BPELTransformationProcessBuilder transformPlanBuilder = new BPELTransformationProcessBuilder(pluginRegistry);
        TServiceTemplate sourceServiceTemplate = Lists.newArrayList(sourceDefinitions.getServiceTemplates()).get(0);
        TServiceTemplate targetServiceTemplate = Lists.newArrayList(targetDefinitions.getServiceTemplates()).get(0);
        QName sourceQName = new QName(sourceServiceTemplate.getTargetNamespace(), sourceServiceTemplate.getId());
        QName targetQName = new QName(targetServiceTemplate.getTargetNamespace(), targetServiceTemplate.getId());

        plans.add(transformPlanBuilder.buildPlan(sourceCsarName, sourceDefinitions,
            sourceQName,
            targetCsarName, targetDefinitions,
            targetQName));

        return plans;
    }

    /**
     * Generates Plans for ServiceTemplates inside the given Definitions document
     *
     * @param defs an TDefinitions
     * @param csar the CSAR the given Definitions is contained in
     * @return a List of Plans
     */
    public List<AbstractPlan> generatePlans(final TDefinitions defs, final Csar csar) {

        final List<AbstractPlan> plans = new ArrayList<>();

        boolean foundTopo = false;
        for (TServiceTemplate servTemp : defs.getServiceTemplates()) {
            if (servTemp.getTopologyTemplate() != null) {
                foundTopo = true;
            }
        }

        if (!foundTopo) {
            return plans;
        }

        AbstractSimplePlanBuilder buildPlanBuilder = new BPELBuildProcessBuilder(pluginRegistry);
        final BPELSituationAwareBuildProcessBuilder sitAwareBuilder = new BPELSituationAwareBuildProcessBuilder(pluginRegistry);

        if (!sitAwareBuilder.buildPlans(csar, defs).isEmpty()) {
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
        final AbstractSimplePlanBuilder defrostPlanBuilder = new BPELDefrostProcessBuilder(pluginRegistry);

        final AbstractSimplePlanBuilder backupPlanBuilder = new BPELBackupManagementProcessBuilder(pluginRegistry);
        final AbstractSimplePlanBuilder testPlanBuilder = new BPELTestManagementProcessBuilder(pluginRegistry);

        final AbstractSimplePlanBuilder updatePlanBuilder = new BPELUpdateProcessBuilder(pluginRegistry);

        if (ModelUtils.findServiceTemplateOperation(defs, OpenTOSCA_LifecycleInterface, OpenTOSCA_BuildPlanOperation) == null) {
            plans.addAll(buildPlanBuilder.buildPlans(csar, defs));
        }

        if (ModelUtils.findServiceTemplateOperation(defs, OpenTOSCA_LifecycleInterface, OpenTOSCA_TerminationPlanOperation) == null) {
            plans.addAll(terminationPlanBuilder.buildPlans(csar, defs));
        }

        // most of these builders have some kind of check whether they can generate a plan or not, therefore the collection they return are empty.
        // However, in this state we don't properly check whether there IS already such a plan provided, e.g., a freeze plan and so forth.
        // Therefore here is now a TODO to properly check via the service template interface operation implementing e.g. freeze and check whether there is an implementation behind that operation
        if (ModelUtils.findServiceTemplateOperation(defs, OpenTOSCA_StatefulLifecycleInterface, OpenTOSCA_FreezePlanOperation) == null) {
            plans.addAll(freezePlanBuilder.buildPlans(csar, defs));
        }

        if (ModelUtils.findServiceTemplateOperation(defs, OpenTOSCA_StatefulLifecycleInterface, OpenTOSCA_DefrostPlanOperation) == null) {
            plans.addAll(defrostPlanBuilder.buildPlans(csar, defs));
        }

        if (ModelUtils.findServiceTemplateOperation(defs, OpenTOSCA_StatefulLifecycleInterface, OpenTOSCA_UpdatePlanOperation) == null) {
            plans.addAll(updatePlanBuilder.buildPlans(csar, defs));
        }

        if (ModelUtils.findServiceTemplateOperation(defs, OpenTOSCA_ManagementFeatureInterface, OpenTOSCA_BackupPlanOperation) == null) {
            plans.addAll(backupPlanBuilder.buildPlans(csar, defs));
        }

        if (ModelUtils.findServiceTemplateOperation(defs, OpenTOSCA_ManagementFeatureInterface, OpenTOSCA_TestPlanOperation) == null) {
            plans.addAll(testPlanBuilder.buildPlans(csar, defs));
        }

        // hard to check honestly, TODO check if there are scaling plan definitions and if they are already available in the TOSCA interface of the service template
        plans.addAll(scalingPlanBuilder.buildPlans(csar, defs));

        return plans;
    }

    /**
     * Generates a List of BuildPlans for the given CSARID. The BuildPlans are generated for the ServiceTemplates inside
     * the Entry-Definitions Document, that haven't got a BuildPlan yet.
     *
     * @param csar the CSARID for the CSAR the BuildPlans should be generated
     * @return a List of BuildPlan
     */
    public List<AbstractPlan> generatePlans(final Csar csar) {
        final TDefinitions defs = csar.entryDefinitions();
        final List<AbstractPlan> plans = this.generatePlans(defs, csar);
        return plans;
    }

    public AbstractPlan generateAdaptationPlan(Csar csar, QName serviceTemplateId,
                                               Collection<String> sourceNodeTemplateIds,
                                               Collection<String> sourceRelationshipTemplateIds,
                                               Collection<String> targetNodeTemplateId,
                                               Collection<String> targetRelationshipTemplateId) throws SystemException {

        TDefinitions defs = csar.entryDefinitions();
        TTopologyTemplate topology = Lists.newArrayList(defs.getServiceTemplates()).get(0).getTopologyTemplate();

        return this.buildAdaptationPlan(csar, defs, serviceTemplateId,
            this.getNodes(topology, sourceNodeTemplateIds),
            this.getRelations(topology, sourceRelationshipTemplateIds),
            this.getNodes(topology, targetNodeTemplateId),
            this.getRelations(topology, targetRelationshipTemplateId));
    }

    private Collection<TNodeTemplate> getNodes(TTopologyTemplate topology, Collection<String> nodeIds) {
        Collection<TNodeTemplate> result = new ArrayList<>();

        for (TNodeTemplate node : topology.getNodeTemplates()) {
            if (nodeIds.contains(node.getId())) {
                result.add(node);
            }
        }

        return result;
    }

    private Collection<TRelationshipTemplate> getRelations(TTopologyTemplate topology,
                                                           Collection<String> relationIds) {
        Collection<TRelationshipTemplate> result = new ArrayList<>();

        for (TRelationshipTemplate relation : topology.getRelationshipTemplates()) {
            if (relationIds.contains(relation.getId())) {
                result.add(relation);
            }
        }

        return result;
    }

    public List<AbstractPlan> generateTransformationPlans(final Csar sourceCsarId, final Csar targetCsarId) {
        final List<AbstractPlan> plans = new ArrayList<>();
        final TDefinitions sourceDefs = sourceCsarId.entryDefinitions();
        final TDefinitions targetDefs = targetCsarId.entryDefinitions();

        plans.addAll(this.buildTransformationPlans(sourceCsarId, sourceDefs,
            targetCsarId, targetDefs));
        return plans;
    }
}
