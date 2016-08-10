package org.opentosca.toscaengine.service.impl.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.opentosca.core.model.csar.CSARContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PathResolver {
	
	private static final Logger LOG = LoggerFactory.getLogger(PathResolver.class);
	
	
	/**
	 * Returns the absolute path of a absolute location path and a assumed
	 * relative path. If the relative path is absolute, the relative path is
	 * returned. If there is an error, null is returned.
	 * 
	 * TODO to avoid legacy problems this method handles absolute pathes in the
	 * parameter relativePath. This is not specification compliant!
	 * 
	 * @param locationPath
	 * @param relativePath
	 * @return the absolute path or in case of an error null
	 */
	public static String resolveRelativePath(String locationPath, String relativePath, CSARContent csarContent) {
		
		// try {
		// locationPath = URLDecoder.decode(locationPath, "UTF-8");
		// relativePath = URLDecoder.decode(relativePath, "UTF-8");
		// } catch (UnsupportedEncodingException e1) {
		// PathResolver.LOG.error("The decoding of the location attribute of an import failed: {}",
		// e1.getLocalizedMessage());
		// e1.printStackTrace();
		// return null;
		// }
		
		// String fs = System.getProperty("file.separator");
		String fs = "/";
		
		locationPath = locationPath.replace("/", fs);
		locationPath = locationPath.replace("\\", fs);
		locationPath = locationPath.replace("//", fs);
		locationPath = locationPath.replace("\\\\", fs);
		
		relativePath = relativePath.replace("/", fs);
		relativePath = relativePath.replace("\\", fs);
		relativePath = relativePath.replace("//", fs);
		relativePath = relativePath.replace("\\\\", fs);
		
		PathResolver.LOG.trace("Resolve the relative path with " + System.getProperty("line.separator") + "entry point:   " + locationPath + System.getProperty("line.separator") + "relative path: " + relativePath);
		
		String newPath = locationPath.substring(0, locationPath.lastIndexOf(fs));
		
		// somewhere else
		if (relativePath.startsWith(".." + fs)) {
			
			PathResolver.LOG.trace("somewhere else");
			
			while (relativePath.startsWith(".." + fs)) {
				PathResolver.LOG.trace(newPath + " " + relativePath);
				if (newPath.contains(fs)) {
					newPath = newPath.substring(0, locationPath.lastIndexOf(fs));
				} else {
					newPath = "";
				}
				relativePath = relativePath.substring(3);
				PathResolver.LOG.trace(newPath + " " + relativePath);
			}
			
			if (newPath.equals("")) {
				newPath = relativePath;
			} else {
				newPath = newPath + fs + relativePath;
			}
		}
		
		// subdirectory
		else if (relativePath.startsWith("." + fs)) {
			
			PathResolver.LOG.trace("subdirectory 1 with " + newPath + " " + relativePath);
			
			if (!newPath.endsWith(fs)) {
				newPath = newPath + fs;
			}
			newPath = newPath + relativePath.substring(2);
		}
		
		// absolute
		else if (relativePath.startsWith(fs)) {
			
			PathResolver.LOG.trace("absolute");
			
			newPath = relativePath.substring(1);
		}
		
		// subdirectory again
		else {
			
			PathResolver.LOG.trace("subdirectory 2");
			
			if (!newPath.endsWith(fs)) {
				newPath = newPath + fs;
			}
			newPath = newPath + relativePath;
			
			if (null == csarContent.getFile(newPath)) {
				newPath = relativePath;
			}
		}
		
		try {
			if (!newPath.equals("") && (null != csarContent.getFile(URLDecoder.decode(newPath, "UTF-8")))) {
				PathResolver.LOG.trace("New path \"" + newPath + "\" seems legit, file exists.");
				return newPath;
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		PathResolver.LOG.error("The file at the new path \"" + newPath + "\" does not exist.");
		return null;
	}
}
