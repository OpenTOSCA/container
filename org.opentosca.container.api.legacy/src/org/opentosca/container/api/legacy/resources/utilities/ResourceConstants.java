package org.opentosca.container.api.legacy.resources.utilities;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

/**
 * Class to Hold all Constants of MediaTypes<br>
 * <br>
 * Copyright 2012 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Markus Fischer - fischema@studi.informatik.uni-stuttgart.de
 * 
 */
public final class ResourceConstants {
	
	public static final String TEXT_PLAIN = MediaType.TEXT_PLAIN;
	public static final String LINKED_XML = MediaType.TEXT_XML;
	public static final String LINKED_JSON = MediaType.APPLICATION_JSON;
	public static final String TOSCA_XML = MediaType.APPLICATION_XML;
	public static final String TOSCA_JSON = MediaType.APPLICATION_JSON;
	public static final String APPLICATION_JSON = MediaType.APPLICATION_JSON;
	public static final String OCTET_STREAM = MediaType.APPLICATION_OCTET_STREAM;
	public static final String IMAGE = "image/*";
	
	// File extensions and corresponding media type
	public static final Map<String, String> imageMediaTypes;
	static {
		Map<String, String> tmp = new HashMap<String, String>();
		// http://www.iana.org/assignments/media-types/image
		tmp.put("png","image/png");
		tmp.put("jpg","image/jpeg");
		tmp.put("jpeg","image/jpeg");
		tmp.put("gif","image/gif");
		imageMediaTypes = Collections.unmodifiableMap(tmp);
	}
	
	
	public static final String ROOT = "/containerapi";
	
	public static final String NOT_IMPLEMENTED = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><Implementation status = \"NOT_DONE\"/>";
	public static final String NOT_FOUND = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><Resource status = \"NOT_Found\"/>";
	
}
