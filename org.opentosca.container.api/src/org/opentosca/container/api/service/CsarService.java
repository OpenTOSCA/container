package org.opentosca.container.api.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import org.apache.commons.io.FileUtils;
import org.eclipse.winery.model.selfservice.Application;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.engine.IToscaEngineService;
import org.opentosca.container.core.model.csar.CSARContent;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.service.ICoreFileService;
import org.opentosca.container.core.service.IFileAccessService;
import org.opentosca.planbuilder.export.Exporter;
import org.opentosca.planbuilder.importer.Importer;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

public class CsarService {

    private static Logger logger = LoggerFactory.getLogger(CsarService.class);

    private ICoreFileService fileService;

    private IToscaEngineService engineService;

    private IFileAccessService fileAccessService;


    /**
     * Loads all available CSARs as {@link CSARContent}
     *
     * @return Set of {@link CSARContent} objects
     */
    public Set<CSARContent> findAll() {
        logger.debug("Requesting all CSARs...");
        final Set<CSARContent> csarSet = Sets.newHashSet();
        for (final CSARID id : this.fileService.getCSARIDs()) {
            try {
                csarSet.add(this.findById(id));
            }
            catch (final Exception e) {
                logger.error("Error while loading CSAR with ID \"{}\": {}", id, e.getMessage(), e);
                throw new ServerErrorException(Response.serverError().build());
            }
        }
        return csarSet;
    }

    /**
     * Loads a CSAR as {@link CSARContent} by a given id
     *
     * @param id The id of the CSAR
     * @return The CSAR as {@link CSARContent}
     */
    public CSARContent findById(final CSARID id) {
        logger.debug("Requesting CSAR \"{}\"...", id);
        try {
            return this.fileService.getCSAR(id);
        }
        catch (final UserException e) {
            logger.info("CSAR \"" + id.getFileName() + "\" could not be found");
            throw new NotFoundException("CSAR \"" + id.getFileName() + "\" could not be found");
        }
    }

    /**
     * Loads a CSAR as {@link CSARContent} by a given id
     *
     * @param id The id of the CSAR
     * @return The CSAR as {@link CSARContent}
     */
    public CSARContent findById(final String id) {
        return this.findById(new CSARID(id));
    }

    /**
     * Reads the self-service metadata of a CSAR and returns it as a Java object
     *
     * @param csarContent The content object of the CSAR
     * @return The self-service metadata as Java object
     */
    public Application getSelfserviceMetadata(final CSARContent csarContent) {
        try (final InputStream is =
            csarContent.getDirectory("SELFSERVICE-Metadata").getFile("data.xml").getFileAsInputStream()) {
            final JAXBContext jaxbContext = JAXBContext.newInstance(Application.class);
            final Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            return (Application) jaxbUnmarshaller.unmarshal(is);
        }
        catch (final Exception e) {
            logger.error("Could not serialize data.xml from CSAR", e);
            throw new ServerErrorException(Status.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Returns a set of strings representing service templates contained in a CSAR
     *
     * @param id The id of the CSAR
     * @return A Set of String objects representing service templates
     */
    public Set<String> getServiceTemplates(final CSARID id) {
        logger.debug("Requesting ServiceTemplates of CSAR \"{}\"...", id);
        final List<QName> result = this.engineService.getServiceTemplatesInCSAR(id);
        return result.stream().filter(Objects::nonNull).map(item -> item.toString()).collect(Collectors.toSet());
    }

    /**
     * Utility to check if a given CSAR has a certain ServiceTemplate attached
     *
     * @param id The id of the CSAR
     * @param name The QName as string of the ServiceTemplate
     * @return true or false
     */
    public boolean hasServiceTemplate(final CSARID id, final String name) {
        return this.getServiceTemplates(id).contains(name);
    }

    public File storeTemporaryFile(final String filename, final InputStream is) {

        final File tempDirectory = this.fileAccessService.getTemp();

        // Make sure the temp directory exists
        tempDirectory.mkdir();

        // Determine temporary file location
        final File file = new File(tempDirectory, filename);

        // Write the input stream into the file
        try {
            FileUtils.copyInputStreamToFile(is, file);
        }
        catch (final IOException e) {
            logger.error("Error writing temporary CSAR file: {}", e.getMessage(), e);
            return null;
        }

        return file;
    }

    /**
     * Checks whether the plan builder should generate a build plans.
     *
     * @param csarId the {@link CSARID} to generate build plans
     * @return the new {@link CSARID} for the repackaged CSAR or null if an error occurred
     */
    public CSARID generatePlans(final CSARID csarId) {

        final Importer planBuilderImporter = new Importer();
        final Exporter planBuilderExporter = new Exporter();

        final List<AbstractPlan> buildPlans = planBuilderImporter.importDefs(csarId);

        if (buildPlans.isEmpty()) {
            return csarId;
        }

        final File file = planBuilderExporter.export(buildPlans, csarId);

        try {
            this.fileService.deleteCSAR(csarId);
            return this.fileService.storeCSAR(file.toPath());
        }
        catch (final Exception e) {
            logger.error("Could not store repackaged CSAR: {}", e.getMessage(), e);
        }

        return null;
    }

    public void setFileService(final ICoreFileService fileService) {
        this.fileService = fileService;
    }

    public void setEngineService(final IToscaEngineService engineService) {
        this.engineService = engineService;
    }

    public void setFileAccessService(final IFileAccessService fileAccessService) {
        this.fileAccessService = fileAccessService;
    }
}
