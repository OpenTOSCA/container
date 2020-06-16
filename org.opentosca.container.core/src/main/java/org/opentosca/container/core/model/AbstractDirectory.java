package org.opentosca.container.core.model;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class of a directory. Provides methods for browsing the directory and getting their meta data.
 */
@Deprecated
public abstract class AbstractDirectory implements IBrowseable {

    private final static Logger LOG = LoggerFactory.getLogger(AbstractDirectory.class);

    /**
     * Reference that points to this directory.
     */
    private final String DIRECTORY_REFERENCE;

    private final Set<String> INCLUDE_PATTERNS;
    private final Set<String> EXCLUDE_PATTERNS;

    /**
     * Creates a directory.
     *
     * @param directoryReference that points to this directory.
     * @param includePatterns    to include only certain files in this directory.
     * @param excludePatterns    to exclude certain files from this directory.
     * @param fileArtifact       - {@code true} if this directory represents a file artifact (directory contains only
     *                           the file at the artifact reference), otherwise {@code false}.
     */
    public AbstractDirectory(final String directoryReference, final Set<String> includePatterns,
                             final Set<String> excludePatterns, final boolean fileArtifact) {
        this.DIRECTORY_REFERENCE = directoryReference;
        this.INCLUDE_PATTERNS = includePatterns;
        this.EXCLUDE_PATTERNS = excludePatterns;
    }

    /**
     * @return {@inheritDoc}<br /> Also {@code null} if {@code relPathOfFile} not matches patterns (if any were given).
     */
    @Override
    public AbstractFile getFile(final String relPathOfFile) {

        final AbstractFile file = this.getFileNotConsiderPatterns(relPathOfFile);

        if (file != null) {

            // no patterns were given => we not doing pattern matching
            if (this.getIncludePatterns().isEmpty() && this.getExcludePatterns().isEmpty()) {
                AbstractDirectory.LOG.debug("File \"{}\" relative to \"{}\" was found.", relPathOfFile, this.getPath());
                return file;
            } else {

                final boolean matchesPatterns =
                    PatternMatcher.isFileMatchesPatterns(file, this.getIncludePatterns(), this.getExcludePatterns());

                if (matchesPatterns) {
                    AbstractDirectory.LOG.debug("File \"{}\" relative to \"{}\" was found and matches pattern(s).",
                        relPathOfFile, this.getPath());
                    return file;
                }
            }
        }

        AbstractDirectory.LOG.warn("File \"{}\" relative to \"{}\" was not found.", relPathOfFile, this.getPath());

        return null;
    }

    /**
     * This method should not consider any given patterns.
     *
     * @see AbstractDirectory#getFile(String)
     */
    protected abstract AbstractFile getFileNotConsiderPatterns(String relPathOfFile);

    /**
     * @return {@inheritDoc}<br /> If patterns were given only files will be returned that matches these patterns.
     */
    @Override
    public Set<AbstractFile> getFiles() {

        Set<AbstractFile> files;

        // no patterns were given => we not doing pattern matching
        if (this.getIncludePatterns().isEmpty() && this.getExcludePatterns().isEmpty()) {
            files = this.getFilesNotConsiderPatterns();
        } else {
            files = PatternMatcher.findFilesMatchesPatterns(this.getFilesNotConsiderPatterns(),
                this.getIncludePatterns(), this.getExcludePatterns());
        }
        return files;
    }

    /**
     * This method should not consider any given patterns.
     *
     * @see AbstractDirectory#getFiles()
     */
    protected abstract Set<AbstractFile> getFilesNotConsiderPatterns();

    /**
     * @return {@inheritDoc}<br /> If patterns were given only files will be returned that matches these patterns.
     */
    @Override
    public Set<AbstractFile> getFilesRecursively() {
        final Set<AbstractFile> files = new HashSet<>();
        this.walkFileTree(this, files);
        return files;
    }

    /**
     * Recursively adds all files in {@code directory} and it's sub directories to {@code files}.
     *
     * @param directory
     * @param files
     */
    private void walkFileTree(final AbstractDirectory directory, final Set<AbstractFile> files) {

        files.addAll(directory.getFiles());

        final Set<AbstractDirectory> subDirectories = directory.getDirectories();

        for (final AbstractDirectory subDirectory : subDirectories) {
            this.walkFileTree(subDirectory, files);
        }
    }

    @Override
    public abstract AbstractDirectory getDirectory(String relPathOfDirectory);

    /**
     * @return Name of this directory.
     */
    public abstract String getName();

    /**
     * @return Reference that points to this directory.
     */
    public String getPath() {
        return this.DIRECTORY_REFERENCE;
    }

    /**
     * @return Patterns to include only certain files in this directory.
     */
    protected Set<String> getIncludePatterns() {
        return this.INCLUDE_PATTERNS;
    }

    /**
     * @return Patterns to exclude certain files from this directory.
     */
    protected Set<String> getExcludePatterns() {
        return this.EXCLUDE_PATTERNS;
    }
}
