package org.opentosca.core.model.artifact.pattern;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.opentosca.core.model.artifact.file.AbstractFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Matches files at an artifact reference against include and exclude patterns.<br />
 * An include pattern includes certain files at a reference in the artifact. By
 * analogy, an exclude pattern excludes files from the artifact.<br />
 * A pattern must be given as a regular expression (RegExp).<br />
 * <br />
 * Copyright 2013 IAAS University of Stuttgart<br />
 * <br />
 * 
 * @author Rene Trefft - rene.trefft@developers.opentosca.org
 * 
 */
public class PatternMatcher {
	
	private static final Logger LOG = LoggerFactory.getLogger(PatternMatcher.class);
	
	
	/**
	 * Matches {@code file} against {@code includePatterns} and
	 * {@code excludePatterns} using
	 * {@link #findFilesMatchesPatterns(Set, Set, Set)}.
	 * 
	 * @param file that should be matched against the patterns.
	 * @param includePatterns to include {@code file}
	 * @param excludePatterns to exclude {@code file}
	 * @return {@code true} if {@code file} matches against the include and
	 *         exclude patterns, otherwise {@code false}. If no patterns were
	 *         given, {@code true} will be returned.
	 * 
	 * @see #findFilesMatchesPatterns(Set, Set, Set)
	 */
	public static boolean isFileMatchesPatterns(AbstractFile file, Set<String> includePatterns, Set<String> excludePatterns) {
		
		Set<AbstractFile> files = new HashSet<AbstractFile>();
		files.add(file);
		
		Set<AbstractFile> matches = PatternMatcher.findFilesMatchesPatterns(files, includePatterns, excludePatterns);
		
		if (!matches.isEmpty()) {
			return true;
		}
		
		return false;
		
	}
	
	/**
	 * Matches {@code files} against {@code includePatterns} and
	 * {@code excludePatterns} in the following way:<br />
	 * <br />
	 * 1. Matches {@code files} against the include patterns. A matched file
	 * will be added to a Set X. If no include patterns were given, all files
	 * will be added to X.<br />
	 * 2. Files in X will be matched against the exclude patterns. If a file
	 * matches, it will be removed from X.<br />
	 * <br />
	 * Finally Set X contains the files that matches the include and exclude
	 * patterns.
	 * 
	 * @param files that should be matched against the patterns.
	 * @param includePatterns to include certain files.
	 * @param excludePatterns to exclude certain files.
	 * @return Files that matches the include and exclude patterns. If no
	 *         patterns were given, all given files will be returned.
	 */
	public static Set<AbstractFile> findFilesMatchesPatterns(Set<AbstractFile> files, Set<String> includePatterns, Set<String> excludePatterns) {
		
		PatternMatcher.LOG.debug("Matching {} file(s) against pattern(s)...", files.size());
		
		// we don't want to change the given Set "files", so we copy its content
		// to a new Set
		Set<AbstractFile> filesToMatch = new HashSet<AbstractFile>(files);
		
		Set<AbstractFile> filesMatchingPatterns = new HashSet<AbstractFile>();
		
		if ((includePatterns != null) && !includePatterns.isEmpty()) {
			for (String includePattern : includePatterns) {
				PatternMatcher.LOG.debug("Matching file(s) against include pattern \"{}\"...", includePattern);
				Set<AbstractFile> filesMatchingPattern = PatternMatcher.getSubsetMatchesPattern(filesToMatch, includePattern);
				filesMatchingPatterns.addAll(filesMatchingPattern);
				// Files that matches an include pattern must be not matched
				// against further include patterns
				filesToMatch.removeAll(filesMatchingPattern);
				PatternMatcher.LOG.debug("Matching file(s) against include pattern \"{}\" completed.", includePattern);
			}
		} else {
			PatternMatcher.LOG.debug("No include patterns were given.");
			filesMatchingPatterns.addAll(files);
		}
		
		if ((excludePatterns != null) && !excludePatterns.isEmpty()) {
			for (String excludePattern : excludePatterns) {
				PatternMatcher.LOG.debug("Matching file(s) against exclude pattern \"{}\"...", excludePattern);
				filesMatchingPatterns.removeAll(PatternMatcher.getSubsetMatchesPattern(filesMatchingPatterns, excludePattern));
				PatternMatcher.LOG.debug("Matching file(s) against exclude pattern \"{}\" completed.", excludePattern);
			}
		} else {
			PatternMatcher.LOG.debug("No exclude patterns were given.");
		}
		
		PatternMatcher.LOG.debug("Matching file(s) completed - {} of {} file(s) match pattern(s).", filesMatchingPatterns.size(), files.size());
		
		return filesMatchingPatterns;
		
	}
	
	/**
	 * Matches {@code files} against {@code pattern}.
	 * 
	 * @param files to match against {@code pattern}.
	 * @param pattern - regular expression
	 * @return Files that matches {@code pattern}.
	 */
	private static Set<AbstractFile> getSubsetMatchesPattern(Set<AbstractFile> files, String pattern) {
		
		Set<AbstractFile> filesMatches = new HashSet<AbstractFile>();
		
		Pattern regexp;
		Matcher matcher;
		
		regexp = Pattern.compile(pattern);
		for (AbstractFile file : files) {
			// match only file name against pattern
			String fileName = file.getName();
			matcher = regexp.matcher(fileName);
			if (matcher.matches()) {
				PatternMatcher.LOG.debug("File \"{}\" matches pattern \"{}\".", fileName, pattern);
				filesMatches.add(file);
			} else {
				PatternMatcher.LOG.debug("File \"{}\" not matches pattern \"{}\".", fileName, pattern);
			}
		}
		
		return filesMatches;
		
	}
	
}
