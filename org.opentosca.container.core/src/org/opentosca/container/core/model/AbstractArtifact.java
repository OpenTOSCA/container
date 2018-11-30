/*******************************************************************************
 * Copyright 2012 - 2017, University of Stuttgart and the OpenTOSCA contributors
 * SPDX-License-Identifier: Apache-2.0
 *******************************************************************************/
package org.opentosca.container.core.model;

import java.util.Collections;
import java.util.Set;

import org.opentosca.container.core.common.UserException;

/**
 * Abstract class of an artifact that consists of files and directories.<br />
 * Each class that extends this class represents an certain type of artifact respectively artifact
 * reference, e.g. an HTTP artifact (reference begins with http).
 * <p>
 * Provides methods for getting the passed artifact reference and include and/or exclude patterns,
 * check if an artifact reference belongs to this type of artifact, and browsing the artifact
 * content (this is the artifact root).
 */
// FIXME remove the abomination that is IBrowseable
public abstract class AbstractArtifact implements IBrowseable {

    /**
     * Reference that points to this artifact.
     */
    private final String ARTIFACT_REFERENCE;

    private final Set<String> INCLUDE_PATTERNS;
    private final Set<String> EXCLUDE_PATTERNS;


    /**
     * Creates an artifact.
     *
     * @param artifactReference that points to this artifact.
     * @param includePatterns to include only certain files in this artifact.
     * @param excludePatterns to exclude certain files from this artifact.
     * @throws UserException if artifact reference points a non-existent file / directory.
     */
    public AbstractArtifact(final String artifactReference, final Set<String> includePatterns,
                            final Set<String> excludePatterns) throws UserException {

        this.ARTIFACT_REFERENCE = artifactReference;
        this.INCLUDE_PATTERNS = includePatterns;
        this.EXCLUDE_PATTERNS = excludePatterns;

    }

    /**
     * @return {@link AbstractDirectory} that represents the root of the artifact.<br />
     *         Note: The browsing methods in this class redirecting to the same methods of this
     *         {@link AbstractDirectory} by delegation.
     */
    protected abstract AbstractDirectory getArtifactRoot();

    /**
     * @return {@inheritDoc}<br />
     *         Also {@code null} if {@code relPathOfFile} not matches patterns (if any were
     *         given).<br />
     *         If this artifact represents an file artifact (artifact reference points to a file) the
     *         file of the artifact can be returned by passing an arbitrary string.
     */
    @Override
    public AbstractFile getFile(final String relPathOfFile) {
        return this.getArtifactRoot().getFile(relPathOfFile);
    }

    /**
     * @return {@inheritDoc}<br />
     *         If any patterns were given only files will be returned that matches these patterns.<br />
     *         In case this artifact represents a file artifact (artifact reference points to a file) a
     *         {@code Set} with one element will be returned that contains the file of the artifact
     *         (patterns will be ignored).
     */
    @Override
    public Set<AbstractFile> getFiles() {
        return this.getArtifactRoot().getFiles();
    }

    /**
     * @return {@inheritDoc}<br />
     *         If patterns were given only files will be returned that matches these patterns.<br />
     *         In case this artifact represents a file artifact (artifact reference points to a file) a
     *         {@code Set} with one element will be returned that contains the file of the artifact
     *         (patterns will be ignored).
     */
    @Override
    public Set<AbstractFile> getFilesRecursively() {
        return this.getArtifactRoot().getFilesRecursively();
    }

    /**
     * @return {@inheritDoc}<br />
     *         If any patterns were given only files will be returned that matches these patterns.
     */
    @Override
    public AbstractDirectory getDirectory(final String relPathOfDirectory) {
        return this.getArtifactRoot().getDirectory(relPathOfDirectory);
    }

    @Override
    public Set<AbstractDirectory> getDirectories() {
        return this.getArtifactRoot().getDirectories();
    }

    /**
     *
     * @return {@code true} if reference of this artifact points to a file, otherwise {@code false}.
     */
    public abstract boolean isFileArtifact();

    /**
     * Checks if {@code artifactReference} fits to this type of artifact.<br />
     * It only performs syntax checks which not need network access.
     *
     * @param artifactReference to check
     * @return {@code true} if {@code artifactReference} fits, otherwise {@code false}
     */
    public static boolean fitsArtifactReference(final String artifactReference) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * @return Artifact reference.
     */
    public String getArtifactReference() {
        return this.ARTIFACT_REFERENCE;
    }

    /**
     * @return Patterns to include only certain files in this artifact.<br />
     *         In case of an file artifact no patterns are allowed. Thus, always an empty {@code Set}
     *         will be returned.
     */
    public Set<String> getIncludePatterns() {

        if (this.isFileArtifact()) {
            return Collections.<String>emptySet();
        }

        return this.INCLUDE_PATTERNS;

    }

    /**
     * @return Patterns to exclude certain files from this artifact.<br />
     *         In case of an file artifact no patterns are allowed. Thus, always an empty {@code Set}
     *         will be returned.
     */
    public Set<String> getExcludePatterns() {

        if (this.isFileArtifact()) {
            return Collections.<String>emptySet();
        }

        return this.EXCLUDE_PATTERNS;
    }

}
