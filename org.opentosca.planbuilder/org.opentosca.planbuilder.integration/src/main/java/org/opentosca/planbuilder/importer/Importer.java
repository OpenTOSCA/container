package org.opentosca.planbuilder.importer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.xml.namespace.QName;

import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.model.AbstractFile;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.legacy.core.model.CSARContent;
import org.opentosca.planbuilder.core.csarhandler.CSARHandler;
import org.opentosca.planbuilder.core.plugins.registry.PluginRegistry;
import org.opentosca.planbuilder.importer.context.impl.DefinitionsImpl;
import org.opentosca.planbuilder.integration.layer.AbstractImporter;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
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

    private final CSARHandler handler = new CSARHandler();

    @Inject
    public Importer(PluginRegistry pluginRegistry) {
        super(pluginRegistry);
    }

    /**
     * Generates a List of BuildPlans for the given CSARID. The BuildPlans are generated for the ServiceTemplates inside
     * the Entry-Definitions Document, that haven't got a BuildPlan yet.
     *
     * @param csarId the CSARID for the CSAR the BuildPlans should be generated
     * @return a List of BuildPlan
     */
    public List<AbstractPlan> generatePlans(final CSARID csarId) {
        try {
            final CSARContent content = this.handler.getCSARContentForID(csarId);
            final AbstractDefinitions defs = this.createContext(content);
            final List<AbstractPlan> plans = this.buildPlans(defs, csarId.getFileName());
            return plans;
        } catch (final UserException e) {
            Importer.LOG.error("Some error within input", e);
        } catch (final SystemException e) {
            Importer.LOG.error("Some internal error", e);
        }
        return new ArrayList<>();
    }

    public AbstractPlan generateAdaptationPlan(CSARID csarId, QName serviceTemplateId,
                                               Collection<String> sourceNodeTemplateIds,
                                               Collection<String> sourceRelationshipTemplateIds,
                                               Collection<String> targetNodeTemplateId,
                                               Collection<String> targetRelationshipTemplateId) throws SystemException {

        try {
            CSARContent content = this.handler.getCSARContentForID(csarId);
            AbstractDefinitions defs = this.createContext(content);
            AbstractTopologyTemplate topology = defs.getServiceTemplates().get(0).getTopologyTemplate();

            return this.buildAdaptationPlan(csarId.getFileName(), defs, serviceTemplateId,
                this.getNodes(topology, sourceNodeTemplateIds),
                this.getRelations(topology, sourceRelationshipTemplateIds),
                this.getNodes(topology, targetNodeTemplateId),
                this.getRelations(topology, targetRelationshipTemplateId));
        } catch (UserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    private Collection<AbstractNodeTemplate> getNodes(AbstractTopologyTemplate topology, Collection<String> nodeIds) {
        Collection<AbstractNodeTemplate> result = new ArrayList<>();

        for (AbstractNodeTemplate node : topology.getNodeTemplates()) {
            if (nodeIds.contains(node.getId())) {
                result.add(node);
            }
        }

        return result;
    }

    private Collection<AbstractRelationshipTemplate> getRelations(AbstractTopologyTemplate topology,
                                                                  Collection<String> relationIds) {
        Collection<AbstractRelationshipTemplate> result = new ArrayList<>();

        for (AbstractRelationshipTemplate relation : topology.getRelationshipTemplates()) {
            if (relationIds.contains(relation.getId())) {
                result.add(relation);
            }
        }

        return result;
    }

    public List<AbstractPlan> generateTransformationPlans(final CSARID sourceCsarId, final CSARID targetCsarId) {
        final List<AbstractPlan> plans = new ArrayList<>();
        try {
            final CSARContent sourceCsarContent = this.handler.getCSARContentForID(sourceCsarId);
            final AbstractDefinitions sourceDefs = this.createContext(sourceCsarContent);
            final CSARContent targetCsarContent = this.handler.getCSARContentForID(targetCsarId);
            final AbstractDefinitions targetDefs = this.createContext(targetCsarContent);

            plans.addAll(this.buildTransformationPlans(sourceCsarId.getFileName(), sourceDefs,
                targetCsarId.getFileName(), targetDefs));
            return plans;
        } catch (final UserException e) {
            Importer.LOG.error("Some error within input", e);
        } catch (final SystemException e) {
            Importer.LOG.error("Some internal error", e);
        }
        return new ArrayList<>();
    }

    /**
     * Returns a TOSCA Definitions object which contains the Entry-ServiceTemplate
     *
     * @param csarId an ID of a CSAR
     * @return an AbstractDefinitions object
     */
    public AbstractDefinitions getMainDefinitions(final CSARID csarId) {
        try {
            return this.createContext(this.handler.getCSARContentForID(csarId));
        } catch (final UserException e) {
            Importer.LOG.error("Some error within input", e);
        } catch (final SystemException e) {
            Importer.LOG.error("Some internal error", e);
        }
        return null;
    }

    /**
     * Creates an AbstractDefinitions Object of the given CSARContent
     *
     * @param csarContent the CSARContent to generate an AbstractDefinitions for
     * @return an AbstractDefinitions which is the Entry-Definitions of the given CSAR
     * @throws SystemException is thrown if accessing data inside the OpenTOSCA Core fails
     */
    public AbstractDefinitions createContext(final CSARContent csarContent) throws SystemException {
        final AbstractFile rootTosca = csarContent.getRootTOSCA();
        final Set<AbstractFile> referencedFilesInCsar = csarContent.getFilesRecursively();
        return new DefinitionsImpl(rootTosca, referencedFilesInCsar, true);
    }
}
