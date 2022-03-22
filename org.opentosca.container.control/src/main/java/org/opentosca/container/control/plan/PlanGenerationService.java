package org.opentosca.container.control.plan;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.xml.namespace.QName;

import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.backend.filebased.FileUtils;

import com.google.common.collect.Lists;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.service.CsarStorageService;
import org.opentosca.planbuilder.export.WineryExporter;
import org.opentosca.planbuilder.importer.Importer;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PlanGenerationService {
    private static final Logger logger = LoggerFactory.getLogger(PlanGenerationService.class);

    private final CsarStorageService storage;
    private final WineryExporter planBuilderExporter;
    private final Importer planBuilderImporter;

    @Inject
    public PlanGenerationService(CsarStorageService storage, WineryExporter planBuilderExporter, Importer planBuilderImporter) {
        this.storage = storage;
        this.planBuilderExporter = planBuilderExporter;
        this.planBuilderImporter = planBuilderImporter;
    }

    /**
     * Checks whether the plan builder should generate a build plans.
     *
     * @param csar the {@link Csar} to generate build plans for
     * @return true for success or false for failure
     */
    public boolean generatePlans(final Csar csar) throws SystemException, UserException {
        Optional<Path> zipFile = safeExport(csar);
        if (zipFile.isEmpty()) {
            return false;
        }

        final List<AbstractPlan> plans = planBuilderImporter.generatePlans(csar);
        // no plans, save ourselves some work by returning early
        if (plans.isEmpty()) {
            return true;
        }
        IRepository repo = RepositoryFactory.getRepository(csar.getSaveLocation());

        final Path file = planBuilderExporter.exportToCSAR(plans, csar.id(), repo, this.storage).csarFile;
        logger.debug("Exported BuildPlan under {}", file);

        csar.reload();
        return true;
    }

    public AdaptationPlanGenerationResult generateAdaptationPlan(final Csar csar, QName serviceTemplateId, Collection<String> sourceNodeTemplateIds, Collection<String> sourceRelationshipTemplateIds, Collection<String> targetNodeTemplateId, Collection<String> targetRelationshipTemplateId) {
        try {
            AbstractPlan plan = planBuilderImporter.generateAdaptationPlan(csar, serviceTemplateId, sourceNodeTemplateIds, sourceRelationshipTemplateIds, targetNodeTemplateId, targetRelationshipTemplateId);

            if (plan == null) {
                return null;
            }
            List<AbstractPlan> plans = Lists.newArrayList();
            plans.add(plan);
            IRepository repo = RepositoryFactory.getRepository(csar.getSaveLocation());

            final WineryExporter.PlanExportResult result = planBuilderExporter.exportToCSAR(plans, csar.id(), repo, this.storage);
            final Path file = result.csarFile;
            logger.debug("Exported AdaptationPlan under {}", file);
            csar.reload();
            return new AdaptationPlanGenerationResult(csar.id(), result.planIds.iterator().next());
        } catch (final Exception e) {
            logger.error("Could not store repackaged CSAR: {}", e.getMessage(), e);
        }

        csar.reload();
        return null;
    }

    public Collection<AbstractPlan> generateTransformationPlans(final Csar sourceCsar, final Csar targetCsar) {
        List<AbstractPlan> plans = planBuilderImporter.generateTransformationPlans(sourceCsar, targetCsar);
        IRepository repo = RepositoryFactory.getRepository(sourceCsar.getSaveLocation());
        final Path file = planBuilderExporter.exportToCSAR(plans, sourceCsar.id(), repo, this.storage).csarFile;
        logger.debug("Exported TransformationPlan under {}", file);
        sourceCsar.reload();
        targetCsar.reload();
        return plans;
    }

    private Optional<Path> safeExport(Csar csar) {
        Optional<Path> zipFile = Optional.empty();
        try {
            zipFile = Optional.of(storage.exportCSAR(csar.id()));
        } catch (UserException | SystemException e) {
            logger.info("Exporting the Csar that is to be planned failed with an exception", e);
            zipFile.ifPresent(FileUtils::forceDelete);
            return Optional.empty();
        }
        return zipFile;
    }

    public class AdaptationPlanGenerationResult {
        public CsarId csarId;
        public String planId;

        public AdaptationPlanGenerationResult(CsarId csarId, String planId) {
            this.csarId = csarId;
            this.planId = planId;
        }
    }
}
