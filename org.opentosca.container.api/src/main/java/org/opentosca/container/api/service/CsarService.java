package org.opentosca.container.api.service;

import csarhandler.CSARHandler;
import org.eclipse.winery.repository.backend.filebased.FileUtils;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
// FIXME prefer to not depend on ZipManager
import org.opentosca.container.core.impl.service.ZipManager;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.service.CsarStorageService;
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

@Service
public class CsarService {

  private static Logger logger = LoggerFactory.getLogger(CsarService.class);

  @Inject
  private CsarStorageService storage;

  public CsarService() {
  }

  /**
   * Checks whether the plan builder should generate a build plans.
   *
   * @param csar the {@link Csar} to generate build plans for
   * @return true for success or false for failure
   */
  public boolean generatePlans(final Csar csar) {
    // Importer requires an unzipped Csar file instead of the winery representation
    final Path zipFile;
    try {
      zipFile = storage.exportCSAR(csar.id());
    } catch (UserException | SystemException e) {
      logger.info("Exporting the Csar that is to be planned failed with an exception", e);
      return false;
    }
    final Path planbuilderCsar = CSARHandler.planBuilderWorkingDir.resolve(csar.id().csarName());
    ZipManager.getInstance().unzip(zipFile.toFile(), planbuilderCsar.toFile());
    // clean up the zip file to allow reexporting the Csar
    FileUtils.forceDelete(zipFile);

    final Importer planBuilderImporter = new Importer();
    final Exporter planBuilderExporter = new Exporter();

    final List<AbstractPlan> buildPlans = planBuilderImporter.importDefs(csar.id().toOldCsarId());
    // no plans, ergo no export and reimport necessary
    if (buildPlans.isEmpty()) {
      return true;
    }

    final File file = planBuilderExporter.export(buildPlans, csar.id().toOldCsarId());
    // clean up the temporary csar we were working on
    FileUtils.forceDelete(planbuilderCsar);
    try {
      // reimport CSAR after generating plans
      storage.deleteCSAR(csar.id());
      storage.storeCSAR(file.toPath());
      return true;
    } catch (UserException | SystemException e) {
      logger.warn("Reimport of Csar after building plans failed with an exception", e);
    }
    return false;
  }
}
