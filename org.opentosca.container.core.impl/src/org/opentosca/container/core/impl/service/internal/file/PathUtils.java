package org.opentosca.container.core.impl.service.internal.file;

import java.nio.file.Path;

/**
 * File name and file path utilities.
 */
public class PathUtils {

	public final static char FILE_EXTENSION_SEPARATOR = '.';


	/**
	 * @param file
	 * @param extensions of a file.
	 * @return {@code true}, if {@code file} has any of the file extensions
	 *         {@code extensions}, otherwise {@code false}.
	 */
	public static boolean hasFileExtension(final Path file, final String... extensions) {
		for (final String extension : extensions) {
			if (file.toString().toLowerCase().endsWith(PathUtils.FILE_EXTENSION_SEPARATOR + extension)) {
				return true;
			}
		}
		return false;
	}

}
