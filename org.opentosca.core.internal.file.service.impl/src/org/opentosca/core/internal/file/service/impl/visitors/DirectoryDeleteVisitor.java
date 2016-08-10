package org.opentosca.core.internal.file.service.impl.visitors;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Visits and deletes all files and directories in a directory. <br />
 * <br />
 * Copyright 2013 IAAS University of Stuttgart<br />
 * <br />
 * 
 * @author Rene Trefft - rene.trefft@developers.opentosca.org
 * 
 */
public class DirectoryDeleteVisitor extends SimpleFileVisitor<Path> {
	
	/**
	 * Called for a file. It deletes the file.
	 */
	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		Files.delete(file);
		return super.visitFile(file, attrs);
	}
	
	/**
	 * Called for a directory after all files and directories in the directory
	 * have been visited. It deletes the directory.
	 */
	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
		Files.delete(dir);
		return super.postVisitDirectory(dir, exc);
	}
	
}
