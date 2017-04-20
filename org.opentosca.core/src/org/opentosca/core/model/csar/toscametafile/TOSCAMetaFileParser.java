package org.opentosca.core.model.csar.toscametafile;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.springsource.util.parser.manifest.ManifestContents;
import com.springsource.util.parser.manifest.ManifestParser;
import com.springsource.util.parser.manifest.ManifestProblem;
import com.springsource.util.parser.manifest.RecoveringManifestParser;

/**
 * Parses and validates a TOSCA meta file.
 */
public class TOSCAMetaFileParser {

	final private static Logger LOG = LoggerFactory.getLogger(TOSCAMetaFileParser.class);


	/**
	 * Parses and validates the <code>toscaMetaFile</code>.
	 *
	 * @param toscaMetaFile to process
	 * @return <code>TOSCAMetaFile</code> that gives access to the content of
	 *         the TOSCA meta file. If the given file doesn't exist or is
	 *         invalid <code>null</code>.
	 */
	public TOSCAMetaFile parse(final Path toscaMetaFile) {

		// counts the errors during parsing
		int numErrors = 0;

		FileReader reader = null;
		ManifestParser parser = null;
		ManifestContents manifestContent = null;
		TOSCAMetaFile toscaMetaFileContent = null;

		try {

			parser = new RecoveringManifestParser();
			reader = new FileReader(toscaMetaFile.toFile());
			TOSCAMetaFileParser.LOG.debug("Parsing TOSCA meta file \"{}\"...", toscaMetaFile.getFileName().toString());
			manifestContent = parser.parse(reader);
			reader.close();

			for (final ManifestProblem problem : parser.getProblems()) {
				this.logManifestProblem(problem);
				numErrors++;
			}

			numErrors += this.validateBlock0(manifestContent);
			numErrors += this.validateFileBlocks(manifestContent);

			if (numErrors == 0) {
				TOSCAMetaFileParser.LOG.debug("Parsing TOSCA meta file \"{}\" completed without errors. TOSCA meta file is valid.", toscaMetaFile.getFileName().toString());
				toscaMetaFileContent = new TOSCAMetaFile(manifestContent);
			} else {
				TOSCAMetaFileParser.LOG.error("Parsing TOSCA meta file \"{}\" failed - {} error(s) occured. TOSCA meta file is invalid.", toscaMetaFile.getFileName().toString(), numErrors);
			}

		} catch (final FileNotFoundException exc) {
			TOSCAMetaFileParser.LOG.error("\"{}\" doesn't exist or is not a file.", toscaMetaFile, exc);
		} catch (final IOException exc) {
			TOSCAMetaFileParser.LOG.error("An IO Exception occured.", exc);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (final IOException exc) {
					TOSCAMetaFileParser.LOG.warn("An IOException occured.", exc);
				}
			}
		}

		return toscaMetaFileContent;

	}

	/**
	 * Validates block 0 of the TOSCA meta file.<br />
	 * <br />
	 * Required attributes in block 0:
	 * <ul>
	 * <li><code>TOSCA-Meta-Version</code> (value must be <code>1.0</code>)</li>
	 * <li><code>CSAR-Version</code> (value must be <code>1.0</code>)</li>
	 * <li><code>Created-By</code></li>
	 * </ul>
	 * Optional attributes in block 0:
	 * <ul>
	 * <li><code>Entry-Definitions</code></li>
	 * <li><code>Description</code></li>
	 * <li><code>Topology</code></li>
	 * </ul>
	 *
	 * Further, arbitrary attributes are also allowed.<br />
	 * <br />
	 *
	 * @param mf to validate
	 * @return Number of errors occurred during validation.
	 */
	private int validateBlock0(final ManifestContents mf) {

		int numErrors = 0;

		String metaFileVersion = null;
		String csarVersion = null;
		String createdBy = null;
		String entryDefinitions = null;
		String description = null;
		String topology = null;

		final Map<String, String> mainAttr = mf.getMainAttributes();

		metaFileVersion = mainAttr.get(TOSCAMetaFileAttributes.TOSCA_META_VERSION);

		if (metaFileVersion == null) {
			this.logAttrMissing(TOSCAMetaFileAttributes.TOSCA_META_VERSION, 0);
			numErrors++;
		} else if (!(metaFileVersion = metaFileVersion.trim()).equals(TOSCAMetaFileAttributes.TOSCA_META_VERSION_VALUE)) {
			this.logAttrWrongVal(TOSCAMetaFileAttributes.TOSCA_META_VERSION, 0, TOSCAMetaFileAttributes.TOSCA_META_VERSION_VALUE);
			numErrors++;
		}

		csarVersion = mainAttr.get(TOSCAMetaFileAttributes.CSAR_VERSION);

		if (csarVersion == null) {
			this.logAttrMissing(TOSCAMetaFileAttributes.CSAR_VERSION, 0);
			numErrors++;
		} else if (!(csarVersion = csarVersion.trim()).equals(TOSCAMetaFileAttributes.TOSCA_META_VERSION_VALUE)) {
			this.logAttrWrongVal(TOSCAMetaFileAttributes.CSAR_VERSION, 0, TOSCAMetaFileAttributes.CSAR_VERSION_VALUE);
			numErrors++;
		}

		createdBy = mainAttr.get(TOSCAMetaFileAttributes.CREATED_BY);

		if (createdBy == null) {
			this.logAttrMissing(TOSCAMetaFileAttributes.CREATED_BY, 0);
			numErrors++;
		} else if ((createdBy = createdBy.trim()).isEmpty()) {
			this.logAttrValEmpty(TOSCAMetaFileAttributes.CREATED_BY, 0);
			numErrors++;
		}

		entryDefinitions = mainAttr.get(TOSCAMetaFileAttributes.ENTRY_DEFINITIONS);

		if ((entryDefinitions != null) && entryDefinitions.trim().isEmpty()) {
			this.logAttrValEmpty(TOSCAMetaFileAttributes.ENTRY_DEFINITIONS, 0);
			numErrors++;
		}

		description = mainAttr.get(TOSCAMetaFileAttributes.DESCRIPTION);

		if ((description != null) && description.trim().isEmpty()) {
			this.logAttrValEmpty(TOSCAMetaFileAttributes.DESCRIPTION, 0);
			numErrors++;
		}

		topology = mainAttr.get(TOSCAMetaFileAttributes.TOPOLOGY);

		if ((topology != null) && topology.trim().isEmpty()) {
			this.logAttrValEmpty(TOSCAMetaFileAttributes.TOPOLOGY, 0);
			numErrors++;
		}

		return numErrors;

	}

	/**
	 * Validates the file blocks (block 1 to last block) of the TOSCA meta
	 * file.<br />
	 * <br />
	 * Each file block has the following required attributes:
	 * <ul>
	 * <li><code>Name</code></li>
	 * <li><code>Content-Type</code> (will be checked for correct syntax)</li>
	 * </ul>
	 *
	 * Further, arbitrary attributes are also allowed in a file block.<br />
	 * <br />
	 *
	 * @param mf to validate.
	 * @return Number of errors occurred during validation.
	 */
	private int validateFileBlocks(final ManifestContents mf) {

		int blockNr = 0;
		int numErrors = 0;

		String contentType;

		final List<String> names = mf.getSectionNames();

		for (final String name : names) {

			blockNr++;

			if ((name != null) && name.trim().isEmpty()) {
				this.logAttrValEmpty(name, blockNr);
				numErrors++;
			}

			final Map<String, String> attr = mf.getAttributesForSection(name);
			contentType = attr.get(TOSCAMetaFileAttributes.CONTENT_TYPE);

			if (contentType == null) {
				this.logAttrMissing(TOSCAMetaFileAttributes.CONTENT_TYPE, blockNr);
				numErrors++;
			} else if (!contentType.trim().matches("^[-\\w\\+\\.]+/[-\\w\\+\\.]+$")) {
				this.logAttrWrongVal(TOSCAMetaFileAttributes.CONTENT_TYPE, blockNr);
				numErrors++;
			}

		}

		return numErrors;

	}

	/**
	 * Logs that attribute <code>attributeName</code> in block
	 * <code>blockNr</code> is missing.
	 *
	 * @param attributeName
	 * @param blockNr
	 */
	private void logAttrMissing(final String attributeName, final int blockNr) {
		TOSCAMetaFileParser.LOG.warn("Required attribute {} in block {} is missing.", attributeName, blockNr);
	}

	/**
	 * Logs that attribute <code>attributeName</code> in block
	 * <code>blockNr</code> has an invalid value. Correct is
	 * <code>correctValue</code>.
	 *
	 * @param attributeName
	 * @param blockNr
	 * @param correctValue
	 */
	private void logAttrWrongVal(final String attributeName, final int blockNr, final String correctValue) {
		TOSCAMetaFileParser.LOG.warn("Attribute {} in block {} has an invalid value. Must be {}.", attributeName, blockNr, correctValue);
	}

	/**
	 * Logs that attribute <code>attributeName</code> in block
	 * <code>blockNr</code> has an invalid value.
	 *
	 * @param attributeName
	 * @param blockNr
	 */
	private void logAttrWrongVal(final String attributeName, final int blockNr) {
		TOSCAMetaFileParser.LOG.warn("Attribute {} in block {} has an invalid value.", attributeName, blockNr);
	}

	/**
	 * Logs that attribute <code>attributeName</code> in block
	 * <code>blockNr</code> has an empty value.
	 *
	 * @param attributeName
	 * @param blockNr
	 */
	private void logAttrValEmpty(final String attributeName, final int blockNr) {
		TOSCAMetaFileParser.LOG.warn("Attribute {} in block {} has a empty value.", attributeName, blockNr);
	}

	/**
	 * Logs the ManifestProblem <code>problem</code>.
	 *
	 * @param problem
	 */
	private void logManifestProblem(final ManifestProblem problem) {
		TOSCAMetaFileParser.LOG.warn(problem.toString());
	}
}
