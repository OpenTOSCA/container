package org.opentosca.container.api.service;

import org.eclipse.winery.repository.backend.filebased.FileUtils;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.impl.service.FileAccessServiceImpl;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.service.CsarStorageService;
import org.opentosca.planbuilder.csarhandler.CSARHandler;
import org.opentosca.planbuilder.export.Exporter;
import org.opentosca.planbuilder.importer.Importer;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Service
public class CsarService {

  private static Logger logger = LoggerFactory.getLogger(CsarService.class);

  @Inject
  private CsarStorageService storage;
  @Inject
  private Exporter planBuilderExporter;
  @Inject
  private Importer planBuilderImporter;


  private final CSARHandler planbuilderStorage = new CSARHandler();

  public CsarService() {
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

      final File file = planBuilderExporter.export(buildPlans, csar.id().toOldCsarId());
      // reimport CSAR after generating plans
      storage.deleteCSAR(csar.id());
      storage.storeCSAR(file.toPath());
      return true;
    } catch (UserException | SystemException e) {
      logger.warn("Reimport of Csar after building plans failed with an exception", e);
    } finally {
      planbuilderStorage.deleteCSAR(csar.id().toOldCsarId());
    }
    return false;
  }


  public CsarId generateTransformationPlans(final CsarId sourceCsarId, final CsarId targetCsarId) {

    final Importer planBuilderImporter = new Importer();
    final Exporter planBuilderExporter = new Exporter(new FileAccessServiceImpl());

    //planBuilderImporter.buildTransformationPlans(sourceCsarId.getFileName(), sourceDefinitions, targetCsarId.getFileName(), targetDefinitions)
    List<AbstractPlan> plans = planBuilderImporter.generateTransformationPlans(sourceCsarId.toOldCsarId(), targetCsarId.toOldCsarId());

    if (plans.isEmpty()) {
      return sourceCsarId;
    }

    final File file = planBuilderExporter.export(plans, sourceCsarId.toOldCsarId());

    try {
      storage.deleteCSAR(sourceCsarId);
      return storage.storeCSAR(file.toPath());
    }
    catch (final Exception e) {
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
