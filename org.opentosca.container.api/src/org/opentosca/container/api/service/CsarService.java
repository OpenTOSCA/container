package org.opentosca.container.api.service;

import java.io.File;
import java.util.List;

import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.service.ICoreFileService;
import org.opentosca.planbuilder.export.Exporter;
import org.opentosca.planbuilder.importer.Importer;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CsarService {

    private static Logger logger = LoggerFactory.getLogger(CsarService.class);

    private ICoreFileService fileService;

    /**
     * Checks whether the plan builder should generate a build plans.
     *
     * @param csar the {@link Csar} to generate build plans for
     * @return true for success or false for failure
     */
    public boolean generatePlans(final Csar csarId) {
        // Force NoOP, because importer / exporter internals can't deal with new representation yet
        return true;
//        final Importer planBuilderImporter = new Importer();
//        final Exporter planBuilderExporter = new Exporter();
//
//        final List<AbstractPlan> buildPlans = planBuilderImporter.importDefs(csarId);
//
//        if (buildPlans.isEmpty()) {
//            return csarId;
//        }
//
//        final File file = planBuilderExporter.export(buildPlans, csarId);
//
//        try {
//            this.fileService.deleteCSAR(csarId);
//            return this.fileService.storeCSAR(file.toPath());
//        } catch (final Exception e) {
//            logger.error("Could not store repackaged CSAR: {}", e.getMessage(), e);
//        }
//
//        return null;
    }

    public void setFileService(final ICoreFileService fileService) {
        this.fileService = fileService;
    }
}
