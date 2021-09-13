package org.opentosca.planbuilder.importer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.common.RepositoryFileReference;

import com.google.common.collect.Lists;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.service.CsarStorageService;
import org.opentosca.planbuilder.core.plugins.registry.PluginRegistry;
import org.opentosca.planbuilder.integration.layer.AbstractImporter;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractTopologyTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * <p>
 * This class is a PlanBuilder Importer for openTOSCA. Importing of CSARs is handled by passing a CSARID
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
@Service
public class Importer extends AbstractImporter {

    final private static Logger LOG = LoggerFactory.getLogger(Importer.class);

    @Inject
    public Importer(PluginRegistry pluginRegistry, CsarStorageService storage) {
        super(pluginRegistry, storage);
    }

    /**
     * Generates a List of BuildPlans for the given CSARID. The BuildPlans are generated for the ServiceTemplates inside
     * the Entry-Definitions Document, that haven't got a BuildPlan yet.
     *
     * @param csar the CSARID for the CSAR the BuildPlans should be generated
     * @return a List of BuildPlan
     */
    public List<AbstractPlan> generatePlans(final Csar csar) {
        final AbstractDefinitions defs = this.createContext(csar);
        final List<AbstractPlan> plans = this.buildPlans(defs, csar);
        return plans;
    }

    public AbstractPlan generateAdaptationPlan(Csar csar, QName serviceTemplateId,
                                               Collection<String> sourceNodeTemplateIds,
                                               Collection<String> sourceRelationshipTemplateIds,
                                               Collection<String> targetNodeTemplateId,
                                               Collection<String> targetRelationshipTemplateId) throws SystemException {

        AbstractDefinitions defs = this.createContext(csar);
        AbstractTopologyTemplate topology = Lists.newArrayList(defs.getServiceTemplates()).get(0).getTopologyTemplate();

        return this.buildAdaptationPlan(csar, defs, serviceTemplateId,
            this.getNodes(topology, sourceNodeTemplateIds),
            this.getRelations(topology, sourceRelationshipTemplateIds),
            this.getNodes(topology, targetNodeTemplateId),
            this.getRelations(topology, targetRelationshipTemplateId));
    }

    private Collection<TNodeTemplate> getNodes(AbstractTopologyTemplate topology, Collection<String> nodeIds) {
        Collection<TNodeTemplate> result = new ArrayList<>();

        for (TNodeTemplate node : topology.getNodeTemplates()) {
            if (nodeIds.contains(node.getId())) {
                result.add(node);
            }
        }

        return result;
    }

    private Collection<TRelationshipTemplate> getRelations(AbstractTopologyTemplate topology,
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
        this.createContext(sourceCsarId);
        final AbstractDefinitions sourceDefs = this.createContext(sourceCsarId);
        final AbstractDefinitions targetDefs = this.createContext(targetCsarId);

        plans.addAll(this.buildTransformationPlans(sourceCsarId, sourceDefs,
            targetCsarId, targetDefs));
        return plans;
    }

    /**
     * Returns a TOSCA Definitions object which contains the Entry-ServiceTemplate
     *
     * @param csarId an ID of a CSAR
     * @return an AbstractDefinitions object
     */
    public AbstractDefinitions getMainDefinitions(final Csar csarId) {
        return this.createContext(csarId);
    }

    /**
     * Creates an AbstractDefinitions Object of the given CSARContent
     *
     * @param csarContent the CSARContent to generate an AbstractDefinitions for
     * @return an AbstractDefinitions which is the Entry-Definitions of the given CSAR
     * @throws SystemException is thrown if accessing data inside the OpenTOSCA Core fails
     */
    /**
     * public AbstractDefinitions createContext(final CSARContent csarContent) throws SystemException { final
     * AbstractFile rootTosca = csarContent.getRootTOSCA(); final Set<AbstractFile> referencedFilesInCsar =
     * csarContent.getFilesRecursively(); return new DefinitionsImpl(rootTosca, referencedFilesInCsar, true); }
     */

    public AbstractDefinitions createContext(final Csar csar) {

        IRepository repo = RepositoryFactory.getRepository(csar.getSaveLocation());
        Collection<DefinitionsChildId> ids = repo.getAllDefinitionsChildIds();
        Collection<RepositoryFileReference> allRefs = new HashSet<RepositoryFileReference>();
        Collection<RepositoryFileReference> entryDefRefs = new HashSet<RepositoryFileReference>();
        Collection<Path> allPaths = new HashSet<Path>();
        for (DefinitionsChildId id : ids) {
            allRefs.addAll(repo.getContainedFiles(id));
        }

        for (RepositoryFileReference ref : allRefs) {
            allPaths.add(repo.ref2AbsolutePath(ref));
        }

        entryDefRefs.addAll(repo.getContainedFiles(new ServiceTemplateId(new QName(csar.entryServiceTemplate().getTargetNamespace(), csar.entryServiceTemplate().getId()))));
        TDefinitions entryDef = null;
        for (RepositoryFileReference ref : entryDefRefs) {
            if (ref.getFileName().endsWith(".tosca")) {
                try {
                    entryDef = repo.definitionsFromRef(ref);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return new org.opentosca.planbuilder.importer.winery.context.impl.impl.DefinitionsImpl(entryDef, ids.stream().map(x -> repo.getDefinitions(x)).collect(Collectors.toList()), allPaths, repo);
    }
}
