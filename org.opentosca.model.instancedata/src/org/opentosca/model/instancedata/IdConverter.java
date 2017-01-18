package org.opentosca.model.instancedata;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Converts internal service and nodeInstanceIDS (the int values inside the DB)
 * to external URIs which can be used by external services and vice-versa
 *
 * @author Marcus Eisele - marcus.eisele@gmail.com
 */
public class IdConverter {
	
	
	// TODO refactor to match new API
	
	public final static String containerApiRoot = "/containerapi";
	public final static String nodeInstancePath = "/instancedata/nodeInstances/";
	public final static String serviceInstancePath = "/instancedata/serviceInstances/";
	
	public static Integer nodeInstanceUriToID(URI nodeInstanceID) {
		String path = nodeInstanceID.getPath();
		
		if (path.contains(nodeInstancePath) && path.contains(containerApiRoot)) {
			path = path.replace(containerApiRoot, "");
			path = path.replace(nodeInstancePath, "");
		}
		
		try {
			return Integer.parseInt(path);
		} catch (NumberFormatException e) {
			return null;
		}
		
	}
	
	public static Integer serviceInstanceUriToID(URI serviceInstanceID) {
		String path = serviceInstanceID.getPath();
		
		if (path.contains(containerApiRoot)
				&& path.contains(serviceInstancePath)) {
			path = path.replace(containerApiRoot, "");
			path = path.replace(serviceInstancePath, "");
		}
		
		try {
			return Integer.parseInt(path);
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
	public static URI nodeInstanceIDtoURI(int id) {
		
		try {
			return new URI(containerApiRoot + nodeInstancePath + id);
		} catch (URISyntaxException e) {
			return null;
		}
	}
	
	public static URI serviceInstanceIDtoURI(int id) {
		
		try {
			return new URI(containerApiRoot + serviceInstancePath + id);
		} catch (URISyntaxException e) {
			return null;
		}
	}
	
	/**
	 * Checks if <code>uri</code> is a valid serviceInstanceID-URI in the
	 * context of the InstanceDataAPI this means that it has one of the
	 * following formats f.ex. with ID=12345
	 *
	 * <pre>
	 *   http://opentosca.org/servicetemplates/instances/12345
	 *   http://localhost:1337/containerapi/instancedata/serviceInstances/12345
	 *   12345
	 * </pre>
	 *
	 * <b>NULL</b> is considered invalid!
	 *
	 * @param uri
	 * @return true - if uri is a valid serviceInstanceID false - if uri is
	 *         null/invalid
	 */
	public static boolean isValidServiceInstanceID(URI uri) {
		if (uri == null) {
			return false;
		}
		
		Integer serviceInstanceUriToID = serviceInstanceUriToID(uri);
		if (serviceInstanceUriToID != null) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Checks if <code>uri</code> is a valid nodeInstanceID-URI in the context
	 * of the InstanceDataAPI this means that it has one of the following
	 * formats f.ex. with ID=12345
	 *
	 * <pre>
	 *   http://opentosca.org/nodetemplates/instances/12345
	 *   http://localhost:1337/containerapi/instancedata/nodeInstances/12345
	 *   12345
	 * </pre>
	 *
	 * <b>NULL</b> is considered invalid!
	 *
	 * @param uri
	 * @return true - if uri is a valid nodeInstanceID false - if uri is
	 *         null/invalid
	 */
	public static boolean isValidNodeInstanceID(URI uri) {
		if (uri == null) {
			return false;
		}
		
		Integer nodeInstanceUriToID = nodeInstanceUriToID(uri);
		if (nodeInstanceUriToID != null) {
			return true;
		}
		
		return false;
	}
	
}
