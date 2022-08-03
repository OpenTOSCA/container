package org.opentosca.planbuilder.importer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;

import com.google.common.collect.Lists;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.core.plugins.registry.PluginRegistry;
import org.opentosca.planbuilder.integration.layer.AbstractImporter;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.springframework.stereotype.Service;

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
public class Importer extends AbstractImporter {

    @Inject
    public Importer(PluginRegistry pluginRegistry) {
        super(pluginRegistry);
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
