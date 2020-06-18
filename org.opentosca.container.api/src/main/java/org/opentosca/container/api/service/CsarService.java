package org.opentosca.container.api.service;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.xml.namespace.QName;

import org.eclipse.winery.repository.backend.filebased.FileUtils;

import com.google.common.collect.Lists;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.service.CsarStorageService;
import org.opentosca.planbuilder.core.csarhandler.CSARHandler;
import org.opentosca.planbuilder.export.Exporter;
import org.opentosca.planbuilder.export.Exporter.PlanExportResult;
import org.opentosca.planbuilder.importer.Importer;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CsarService {
    private static Logger logger = LoggerFactory.getLogger(CsarService.class);

    private final CsarStorageService storage;
    private final Exporter planBuilderExporter;
    private final Importer planBuilderImporter;

    public class AdaptationPlanGenerationResult {
        public CsarId csarId;
        public String planId;

        public AdaptationPlanGenerationResult(CsarId csarId, String planId) {
            this.csarId = csarId;
            this.planId = planId;
        }
    }

    // FIXME remove this as soon as planbuilder works off new csar model
    private final CSARHandler planbuilderStorage = new CSARHandler();

    @Inject
    public CsarService(CsarStorageService storage, Exporter planBuilderExporter, Importer planBuilderImporter) {
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
        if (!zipFile.isPresent()) {
            return false;
        }

        try {
            planbuilderStorage.storeCSAR(zipFile.get().toFile());
            final List<AbstractPlan> buildPlans = planBuilderImporter.generatePlans(csar.id().toOldCsarId());
            // no plans, save ourselves some work by returning early
            if (buildPlans.isEmpty()) {
                return true;
            }

            final Path file = planBuilderExporter.exportToCSAR(buildPlans, csar.id().toOldCsarId()).csarFile;
            // reimport CSAR after generating plans
            storage.deleteCSAR(csar.id());
            storage.storeCSAR(file);
            return true;
        } catch (UserException | SystemException e) {
            logger.warn("Reimport of Csar after building plans failed with an exception", e);
        } finally {
            planbuilderStorage.deleteCSAR(csar.id().toOldCsarId());
        }
        return false;
    }

    public AdaptationPlanGenerationResult generateAdaptationPlan(final CsarId csarId, QName serviceTemplateId, Collection<String> sourceNodeTemplateIds, Collection<String> sourceRelationshipTemplateIds, Collection<String> targetNodeTemplateId, Collection<String> targetRelationshipTemplateId) {
        try {

            AbstractPlan plan = planBuilderImporter.generateAdaptationPlan(new CSARID(csarId.csarName()), serviceTemplateId, sourceNodeTemplateIds, sourceRelationshipTemplateIds, targetNodeTemplateId, targetRelationshipTemplateId);

            if (plan == null) {
                return null;
            }
            List<AbstractPlan> plans = Lists.newArrayList();
            plans.add(plan);
            final PlanExportResult result = planBuilderExporter.exportToCSAR(plans, new CSARID(csarId.csarName()));
            final Path file = result.csarFile;

            storage.deleteCSAR(csarId);

            CsarId newCsarId = storage.storeCSAR(file);
            return new AdaptationPlanGenerationResult(newCsarId, result.planIds.iterator().next());
        } catch (final Exception e) {
            logger.error("Could not store repackaged CSAR: {}", e.getMessage(), e);
        }

        return null;
    }

    public CsarId generateTransformationPlans(final CsarId sourceCsarId, final CsarId targetCsarId) {

//    final Importer planBuilderImporter = new Importer();
//    final Exporter planBuilderExporter = new Exporter(new FileAccessServiceImpl());

        //planBuilderImporter.buildTransformationPlans(sourceCsarId.getFileName(), sourceDefinitions, targetCsarId.getFileName(), targetDefinitions)
        List<AbstractPlan> plans = planBuilderImporter.generateTransformationPlans(sourceCsarId.toOldCsarId(), targetCsarId.toOldCsarId());

        if (plans.isEmpty()) {
            return sourceCsarId;
        }

        final Path file = planBuilderExporter.exportToCSAR(plans, sourceCsarId.toOldCsarId()).csarFile;
        try {
            storage.deleteCSAR(sourceCsarId);
            return storage.storeCSAR(file);
        } catch (final Exception e) {
            logger.error("Could not store repackaged CSAR: {}", e.getMessage(), e);
        }
        return null;
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
}
