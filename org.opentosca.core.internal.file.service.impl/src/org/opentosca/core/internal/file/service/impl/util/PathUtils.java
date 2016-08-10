package org.opentosca.core.internal.file.service.impl.util;

import java.nio.file.Path;

/**
 * File name and file path utilities.<br />
 * <br />
 * Copyright 2013 IAAS University of Stuttgart<br />
 * <br />
 * 
 * @author Rene Trefft - rene.trefft@developers.opentosca.org
 * 
 */
public class PathUtils {
	
	public final static char FILE_EXTENSION_SEPARATOR = '.';
	
	
	/**
	 * @param file
	 * @param extensions of a file.
	 * @return {@code true}, if {@code file} has any of the file extensions
	 *         {@code extensions}, otherwise {@code false}.
	 */
	public static boolean hasFileExtension(Path file, String... extensions) {
		for (String extension : extensions) {
			if (file.toString().toLowerCase().endsWith(PathUtils.FILE_EXTENSION_SEPARATOR + extension)) {
				return true;
			}
		}
		return false;
	}
	
}
