package org.opentosca.core.model.csar.toscametafile;

/**
 * Predefined attribute names and values of a TOSCA meta file. <br />
 * <br />
 * Copyright 2013 IAAS University of Stuttgart <br />
 * <br />
 * 
 * @author Rene Trefft - rene.trefft@developers.opentosca.org
 * 
 */
public class TOSCAMetaFileAttributes {
	
	// of block 0
	final public static String TOSCA_META_VERSION = "TOSCA-Meta-Version";
	final public static String TOSCA_META_VERSION_VALUE = "1.0";
	final public static String CSAR_VERSION = "CSAR-Version";
	final public static String CSAR_VERSION_VALUE = "1.0";
	final public static String CREATED_BY = "Created-By";
	final public static String ENTRY_DEFINITIONS = "Entry-Definitions";
	final public static String TOPOLOGY = "Topology";
	final public static String DESCRIPTION = "Description";
	
	// of blocks > 0 (file blocks)
	final public static String NAME = "Name";
	final public static String CONTENT_TYPE = "Content-Type";
	
}
