package org.opentosca.planbuilder.importer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.inject.Inject;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.common.RepositoryFileReference;

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
        final TDefinitions defs = this.createContext(csar);
        final List<AbstractPlan> plans = this.generatePlans(defs, csar);
        return plans;
    }

    public AbstractPlan generateAdaptationPlan(Csar csar, QName serviceTemplateId,
                                               Collection<String> sourceNodeTemplateIds,
                                               Collection<String> sourceRelationshipTemplateIds,
                                               Collection<String> targetNodeTemplateId,
                                               Collection<String> targetRelationshipTemplateId) throws SystemException {

        TDefinitions defs = this.createContext(csar);
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
        final TDefinitions sourceDefs = this.createContext(sourceCsarId);
        final TDefinitions targetDefs = this.createContext(targetCsarId);

        plans.addAll(this.buildTransformationPlans(sourceCsarId, sourceDefs,
            targetCsarId, targetDefs));
        return plans;
    }

    /**
     * Returns a TOSCA Definitions object which contains the Entry-ServiceTemplate
     *
     * @param csarId an ID of a CSAR
     * @return an TDefinitions object
     */
    public TDefinitions getMainDefinitions(final Csar csarId) {
        return this.createContext(csarId);
    }

    /**
     * Creates an TDefinitions Object of the given CSARContent
     *
     * @param csarContent the CSARContent to generate an TDefinitions for
     * @return an TDefinitions which is the Entry-Definitions of the given CSAR
     * @throws SystemException is thrown if accessing data inside the OpenTOSCA Core fails
     */
    /**
     * public TDefinitions createContext(final CSARContent csarContent) throws SystemException { final AbstractFile
     * rootTosca = csarContent.getRootTOSCA(); final Set<AbstractFile> referencedFilesInCsar =
     * csarContent.getFilesRecursively(); return new DefinitionsImpl(rootTosca, referencedFilesInCsar, true); }
     */

    public TDefinitions createContext(final Csar csar) {
        IRepository repo = RepositoryFactory.getRepository(csar.getSaveLocation());
        Collection<RepositoryFileReference> entryDefRefs = new HashSet<RepositoryFileReference>();

        entryDefRefs.addAll(repo.getContainedFiles(new ServiceTemplateId(new QName(csar.entryServiceTemplate().getTargetNamespace(), csar.entryServiceTemplate().getId()))));
        TDefinitions entryDef = null;
        for (RepositoryFileReference ref : entryDefRefs) {
            if (ref.getFileName().endsWith(".tosca")) {
                try {
                    entryDef = repo.definitionsFromRef(ref);
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return entryDef;
    }
}
