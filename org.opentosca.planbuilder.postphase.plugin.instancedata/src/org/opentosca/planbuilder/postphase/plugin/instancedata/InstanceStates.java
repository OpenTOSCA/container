package org.opentosca.planbuilder.postphase.plugin.instancedata;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

/**
 * @author Kálmán Képes - kalman.kepes@iaas.uni-stuttgart.de
 */
public class InstanceStates {

	private static Map<String, String> operationPreStates;
	private static Map<String, String> operationPostStates;

	static {
		operationPreStates = new HashMap<String, String>();
		operationPostStates = new HashMap<String, String>();

		// lifecycle
		operationPreStates.put("install", "installing");
		operationPreStates.put("uninstall", "uninstalling");
		operationPreStates.put("configure", "configuring");
		operationPreStates.put("start", "starting");
		operationPreStates.put("stop", "stopping");

		operationPostStates.put("install", "installed");
		operationPostStates.put("uninstall", "uninstalled");
		operationPostStates.put("configure", "configured");
		operationPostStates.put("start", "started");
		operationPostStates.put("stop", "stopped");

		// VM's
		operationPreStates.put("createVM", "starting");
		operationPreStates.put("waitForAvailability", "pending");

		operationPostStates.put("createVM", "pending");
		operationPostStates.put("waitForAvailability", "started");
		
		// Docker
		operationPreStates.put("startContainer", "starting");
		operationPostStates.put("startContainer", "started");
		
		operationPreStates.put("removeContainer", "deleting");
		operationPostStates.put("removeContainer", "deleted");
	}

	public static String getOperationPreState(String operationName) {
		return operationPreStates.get(operationName);
	}

	public static String getOperationPostState(String operationName) {
		return operationPostStates.get(operationName);
	}
	
	public static boolean isStableOperationState(String state){
		return operationPostStates.containsValue(state);
	}

	/**
	 * Returns the next stable state for the given state. A stable state means
	 * that the node isn't in a state of modification such as installing,
	 * starting, pending, etc..
	 * 
	 * The next stable state of e.g., uninstalled would be installed, for
	 * installing it would be installed, configuring would be configured, etc.
	 * 
	 * @param state
	 *            a String containing a lifecycle state
	 * @return a String containing the next stable state from the given state
	 */
	public static String getNextStableOperationState(String state) {

		if (operationPreStates.containsValue(state)) {
			// given state is unstable
			switch (state) {
			case "installing":
				return "installed";
			case "uninstalling":
				return "uninstalled";
			case "configuring":
				return "configured";
			case "starting":
				return "started";
			case "stopping":
				return "stopped";
			case "pending":
				return "started";
			}
		} else if (operationPostStates.containsValue(state)) {
			// given state is stable
			switch (state) {
			case "uninstalled":
				return "installed";
			case "installed":
				return "configured";
			case "configured":
				return "started";
			case "started":
				return "started";
			case "stopped":
				return "stopped";
			}
		}

		return null;
	}

}
