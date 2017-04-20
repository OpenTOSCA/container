package org.opentosca.core.model.csar.toscametafile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.springsource.util.parser.manifest.ManifestContents;

/**
 * Provides structured access to the content of a TOSCA meta file.
 */
public class TOSCAMetaFile implements Serializable {
	
	private static final long serialVersionUID = 5636441655503533546L;
	
	Map<String, String> block0 = new HashMap<>();
	List<Map<String, String>> fileBlocks = new ArrayList<>();
	
	
	public TOSCAMetaFile(final ManifestContents manifestContent) {
		this.block0 = manifestContent.getMainAttributes();
		for (final String name : manifestContent.getSectionNames()) {
			final Map<String, String> fileBlock = new HashMap<>();
			fileBlock.put(TOSCAMetaFileAttributes.NAME, name);
			fileBlock.putAll(manifestContent.getAttributesForSection(name));
			this.fileBlocks.add(fileBlock);
		}
	}

	public String getCSARVersion() {
		return this.block0.get(TOSCAMetaFileAttributes.CSAR_VERSION);
	}

	public String getTOSCAMetaVersion() {
		return this.block0.get(TOSCAMetaFileAttributes.TOSCA_META_VERSION);
	}

	public String getCreatedBy() {
		return this.block0.get(TOSCAMetaFileAttributes.CREATED_BY);
	}

	public String getEntryDefinitions() {
		return this.block0.get(TOSCAMetaFileAttributes.ENTRY_DEFINITIONS);
	}
	
	public String getDescription() {
		return this.block0.get(TOSCAMetaFileAttributes.DESCRIPTION);
	}
	
	public String getTopology() {
		return this.block0.get(TOSCAMetaFileAttributes.TOPOLOGY);
	}
	
	public Map<String, String> getBlock0() {
		return this.block0;
	}
	
	public List<Map<String, String>> getFileBlocks() {
		return this.fileBlocks;
	}
	
	/**
	 * Returns the Mime Type for a given name.
	 *
	 * @param name a reference to a file
	 * @return the mime type associated with the given name, null if no mime
	 *         type was found
	 */
	public String getMimeType(final String name) {
		Objects.requireNonNull(name, "Name must not be null");
		for (final Map<String, String> map : this.getFileBlocks()) {
			final String storedName = map.get("Name");
			if (name.equals(storedName)) {
				// first hit, check whether content-type is stored
				final String contentType = map.get("Content-Type");
				if (contentType != null) {
					// hit - return the found content type
					return contentType;
				}
			}
		}
		// nothing found
		return null;
	}
}
