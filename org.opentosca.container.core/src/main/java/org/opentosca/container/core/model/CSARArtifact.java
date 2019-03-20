/*******************************************************************************
 * Copyright 2012 - 2017, University of Stuttgart and the OpenTOSCA contributors
 * SPDX-License-Identifier: Apache-2.0
 *******************************************************************************/
package org.opentosca.container.core.model;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a CSAR artifact that is a directory or file in a CSAR. It can be also a complete CSAR.
 * Provides methods for getting the artifact reference and include and / or exclude patterns, check
 * if an artifact reference is a CSAR artifact (relative artifact reference), and browsing the
 * content of the CSAR artifact (this is the artifact root).
 */
@Deprecated
public class CSARArtifact extends AbstractArtifact {

  final private static Logger LOG = LoggerFactory.getLogger(CSARArtifact.class);

  /**
   * {@link CSARDirectory} that represents the artifact root.
   */
  private final CSARDirectory CSAR_ARTIFACT_ROOT;

  /**
   * {@code true} if the reference of this CSAR artifact points to a file. If reference points to a
   * directory {@code false}.
   */
  private boolean fileArtifactReference = false;


  /**
   * Creates a {@link CSARArtifact}.
   *
   * @param artifactReference          that points to this CSAR artifact.
   * @param includePatterns            to include only certain files in this CSAR artifact.
   * @param excludePatterns            to exclude certain files from this CSAR artifact.
   * @param csarID                     of CSAR this artifact belongs to.
   * @param directories                - directories in this CSAR artifact (recursively). Each directory must be
   *                                   given relative to the CSAR root.
   * @param fileToStorageProviderIDMap - file to storage provider ID mapping of all files in this CSAR
   *                                   artifact (recursively). Each file path must be given relative to the CSAR root.
   * @throws UserException if artifact reference points a non-existent file / directory in CSAR or URL
   *                       decoding on artifact reference failed.
   */
  public CSARArtifact(final String artifactReference, final Set<String> includePatterns,
                      final Set<String> excludePatterns, final CSARID csarID, final Set<Path> directories,
                      final Map<Path, String> fileToStorageProviderIDMap) throws UserException {
    super(artifactReference, includePatterns, excludePatterns);

    // As the artifact reference is a URI we must decode it to get the
    // directory / file path with that can be searched in the CSAR meta
    // data.
    try {
      final String artifactReferenceDecoded = URLDecoder.decode(this.getArtifactReference(), "UTF-8");

      // directories and files in the artifact
      final Set<Path> artifactDirectories = new HashSet<>();
      final Map<Path, String> artifactFileToStorageProviderIDMap = new HashMap<>();

      // Artifact reference points to the CSAR root. We know
      // directly that all directories and files of the CSAR belongs
      // the artifact.
      if (artifactReferenceDecoded.equals("")) {

        artifactDirectories.addAll(directories);
        artifactFileToStorageProviderIDMap.putAll(fileToStorageProviderIDMap);

      } else {

        final Path artifactReferenceAsPath = Paths.get(artifactReferenceDecoded);

        for (final Map.Entry<Path, String> fileToStorageProviderIDEntry : fileToStorageProviderIDMap.entrySet()) {
          final Path file = fileToStorageProviderIDEntry.getKey();
          if (file.startsWith(artifactReferenceAsPath)) {
            final String fileStorageProviderID = fileToStorageProviderIDEntry.getValue();
            artifactFileToStorageProviderIDMap.put(file, fileStorageProviderID);
          }
        }

        // artifact reference points to a file in CSAR
        if (artifactFileToStorageProviderIDMap.containsKey(artifactReferenceAsPath)) {

          this.fileArtifactReference = true;

          // artifact reference points to a directory or non-existent
          // file / directory in CSAR
        } else {

          for (final Path directory : directories) {
            if (directory.startsWith(artifactReferenceAsPath)) {
              artifactDirectories.add(directory);
            }
          }

          // artifact reference points to a directory
          if (!artifactDirectories.isEmpty() || !artifactFileToStorageProviderIDMap.isEmpty()) {
            // remove artifact root, because it's not in the
            // artifact
            artifactDirectories.remove(artifactReferenceAsPath);
          } else {
            throw new UserException("Artifact reference \"" + artifactReferenceDecoded
              + "\" points to a non-existent file / directory in CSAR \"" + csarID + "\".");
          }

        }

      }

      // creates CSARDirectory that represents the artifact root
      this.CSAR_ARTIFACT_ROOT =
        new CSARDirectory(artifactReferenceDecoded, this.getIncludePatterns(), this.getExcludePatterns(),
          csarID, artifactDirectories, artifactFileToStorageProviderIDMap, this.isFileArtifact());

    } catch (final UnsupportedEncodingException exc) {
      throw new UserException(
        "URL decoding on artifact reference \"" + this.getArtifactReference() + "\" failed.", exc);
    }

  }

  @Override
  protected AbstractDirectory getArtifactRoot() {
    return this.CSAR_ARTIFACT_ROOT;
  }

  /**
   * Syntactically checks if the URI {@code artifactReference} refers to a file or directory in a
   * CSAR.<br />
   * It points to a CSAR file / directory if it's a relative URI respectively without scheme.<br />
   * <br />
   * Note: This method only performs a syntax check, it doesn't make a statement about the existence
   * of the CSAR file / directory.
   *
   * @param artifactReference to check
   * @return {@code true} if {@code artifactReference} refers to a CSAR file / directory, otherwise
   * {@code false}.
   */
  public static boolean fitsArtifactReference(final String artifactReference) {

    String artifactRefScheme;

    try {

      artifactRefScheme = new URI(artifactReference).getScheme();
      if (artifactRefScheme == null) {
        CSARArtifact.LOG.debug("Artifact reference \"{}\" refers to a file or directory in a CSAR.",
          artifactReference);
        return true;
      }

    } catch (final URISyntaxException exc) {
      CSARArtifact.LOG.warn("An URI Exception occured.", exc);
    }

    CSARArtifact.LOG.debug("Artifact reference \"{}\" refers not to a file or directory in a CSAR.",
      artifactReference);
    return false;

  }

  @Override
  public boolean isFileArtifact() {
    return this.fileArtifactReference;
  }

}
