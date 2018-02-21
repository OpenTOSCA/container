package org.opentosca.container.core.impl.service.internal.file.csar;

import java.nio.file.Path;
import java.util.Set;

import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.impl.service.internal.file.visitors.DirectoryVisitor;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Validates the content of a CSAR.
 */
public class CSARValidator {

    private final static Logger LOG = LoggerFactory.getLogger(CSARValidator.class);

    /**
     * Relative path to CSAR root of the {@code Definitions} directory.
     *
     * @see org.opentosca.settings.Settings
     */
    private final String CSAR_DEFINITIONS_DIR_REL_PATH = Settings.getSetting("csarDefinitionsRelPath");

    /**
     * Relative path to CSAR root of the TOSCA meta file.
     *
     * @see org.opentosca.settings.Settings
     */
    private final String TOSCA_META_FILE_REL_PATH = Settings.getSetting("toscaMetaFileRelPath");

    // /**
    // * Possible file extensions of a TOSCA file.
    // *
    // * @see org.opentosca.settings.Settings
    // */
    // private final String[] TOSCA_FILE_EXTENSIONS =
    // Settings.getSetting("toscaFileExtensions").split(";");

    /**
     * CSAR ID of the CSAR to validate.
     */
    private final CSARID CSAR_ID;

    /**
     * Absolute path to unpack directory of the CSAR to validate.
     */
    private final Path CSAR_UNPACK_DIR;

    /**
     * Contains all directories and files in the unpack directory of the CSAR to validate.
     */
    private final DirectoryVisitor CSAR_VISITOR;

    /**
     * Error message that can fetched if any errors occurred during validation.
     */
    private final StringBuilder errorMessage = new StringBuilder();

    private boolean isValidCSAR = true;


    /**
     * Creates a {@link CSARValidator}.<br />
     * After creation {@link #isValid()} should be called to validate the CSAR content.
     *
     * @param csarID of CSAR
     * @param csarUnpackDir - absolute path of CSAR unpack directory
     * @param csarVisitor - must contain all files and directories in {@code csarUnpackDir}.
     */
    public CSARValidator(final CSARID csarID, final Path csarUnpackDir, final DirectoryVisitor csarVisitor) {
        this.CSAR_ID = csarID;
        this.CSAR_UNPACK_DIR = csarUnpackDir;
        this.CSAR_VISITOR = csarVisitor;
    }

    /**
     * Basic validation of the content of the CSAR.
     *
     * @return {@code true} if content of CSAR is valid, otherwise {@code false} .
     */
    public boolean isValid() {

        CSARValidator.LOG.debug("Validating content of CSAR \"{}\"...", this.CSAR_ID);

        final Set<Path> csarFiles = this.CSAR_VISITOR.getVisitedFiles();

        if (!this.containsTOSCAs(this.CSAR_ID, this.CSAR_UNPACK_DIR, csarFiles)) {
            this.isValidCSAR = false;
        }

        if (!this.existsTOSCAMetaFile(this.CSAR_ID, this.CSAR_UNPACK_DIR, csarFiles)) {
            this.isValidCSAR = false;
        }

        if (this.isValidCSAR) {
            CSARValidator.LOG.debug("Validation of CSAR \"{}\" completed. CSAR is valid.", this.CSAR_ID);
        } else {
            CSARValidator.LOG.warn("Validation of CSAR \"{}\" completed. CSAR is invalid!", this.CSAR_ID);
        }

        return this.isValidCSAR;
    }

    /**
     *
     * @return Occurred errors during validation of CSAR. If CSAR was not validated yet using
     *         {@link CSARValidator#isValid()} or no errors occurred {@code null}.
     */
    public String getErrorMessage() {
        if (this.isValidCSAR) {
            return null;
        } else {
            // add beginning to error message
            this.errorMessage.insert(0, "Content of CSAR \"" + this.CSAR_ID + "\" is invalid. Found errors:");
            return this.errorMessage.toString();
        }
    }

    /**
     * @param csarID of CSAR.
     * @param csarUnpackDir - absolute path of CSAR unpack directory
     * @param csarFiles - all files in {@code csarUnpackDir}.
     * @return {@code true} if {@code Definitions} directory of CSAR {@code csarID} contains at least
     *         one TOSCA file, otherwise {@code false}.
     */
    private boolean containsTOSCAs(final CSARID csarID, final Path csarUnpackDir, final Set<Path> csarFiles) {

        final Path csarDefinitionsDirAbsPath = csarUnpackDir.resolve(this.CSAR_DEFINITIONS_DIR_REL_PATH);

        for (final Path csarFile : csarFiles) {
            if (csarFile.startsWith(csarDefinitionsDirAbsPath) /*
                                                                * && PathUtils. hasFileExtension (csarFile, this.
                                                                * TOSCA_FILE_EXTENSIONS )
                                                                */) {
                CSARValidator.LOG.debug("At least one file was found in directory \"{}\" of CSAR \"{}\".",
                                        this.CSAR_DEFINITIONS_DIR_REL_PATH, this.CSAR_ID);
                return true;
            }
        }

        this.errorMessage.append("\n");
        this.errorMessage.append("No files were found in directory \"" + this.CSAR_DEFINITIONS_DIR_REL_PATH
            + "\" of CSAR \"" + this.CSAR_ID + "\". There must be at least one!");

        return false;

    }

    /**
     * @param csarID of CSAR.
     * @param csarUnpackDir - absolute path of CSAR unpack directory
     * @param csarFiles - all files in {@code csarUnpackDir}.
     * @return {@code true} if TOSCA meta file exists in CSAR {@code csarID}, otherwise {@code false}.
     */
    private boolean existsTOSCAMetaFile(final CSARID csarID, final Path csarUnpackDir, final Set<Path> csarFiles) {

        final Path toscaMetaFileAbsPath = csarUnpackDir.resolve(this.TOSCA_META_FILE_REL_PATH);

        if (csarFiles.contains(toscaMetaFileAbsPath)) {
            CSARValidator.LOG.debug("TOSCA meta file exists at \"{}\" in CSAR \"{}\".", this.TOSCA_META_FILE_REL_PATH,
                                    this.CSAR_ID);
            return true;
        }

        this.errorMessage.append("\n");
        this.errorMessage.append("TOSCA meta file does not exist at \"" + this.TOSCA_META_FILE_REL_PATH
            + "\" in CSAR \"" + this.CSAR_ID + "\".");

        return false;

    }

}
