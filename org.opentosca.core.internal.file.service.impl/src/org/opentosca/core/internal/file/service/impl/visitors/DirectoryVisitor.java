package org.opentosca.core.internal.file.service.impl.visitors;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;

/**
 * Visits and remembers all files and directories in a directory.<br />
 * <br />
 * Copyright 2013 IAAS University of Stuttgart<br />
 * <br />
 * 
 * @author Rene Trefft - rene.trefft@developers.opentosca.org
 * 
 */
public class DirectoryVisitor extends SimpleFileVisitor<Path> {
	
	/**
	 * Visited files and directories in the directory.
	 */
	
	private final Set<Path> VISITED_FILES = new HashSet<Path>();
	private final Set<Path> VISITED_DIRECTORIES = new HashSet<Path>();
	
	
	/**
	 * Called for a file. The file path be saved in a {@code Set}.
	 */
	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		this.VISITED_FILES.add(file);
		return super.visitFile(file, attrs);
	}
	
	/**
	 * Called for a directory after all files and directories in the directory
	 * have been visited. The directory path be saved in a {@code Set}.
	 */
	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
		this.VISITED_DIRECTORIES.add(dir);
		return super.postVisitDirectory(dir, exc);
	}
	
	/**
	 * @return Visited files.
	 */
	public Set<Path> getVisitedFiles() {
		return this.VISITED_FILES;
	}
	
	/**
	 * @return Visited directories.
	 */
	public Set<Path> getVisitedDirectories() {
		return this.VISITED_DIRECTORIES;
	}
	
}
