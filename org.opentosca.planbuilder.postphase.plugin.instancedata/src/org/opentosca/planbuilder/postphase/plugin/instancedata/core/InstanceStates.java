package org.opentosca.planbuilder.postphase.plugin.instancedata.core;

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

		/*
		 * INITIAL, CREATING, CREATED, CONFIGURING, CONFIGURED, STARTING, STARTED, STOPPING, STOPPED, DELETING, DELETED, ERROR
		 */
		
		// lifecycle
		operationPreStates.put("install", "creating");
		operationPreStates.put("uninstall", "deleting");
		operationPreStates.put("configure", "configuring");
		operationPreStates.put("start", "starting");
		operationPreStates.put("stop", "stopping");

		operationPostStates.put("install", "created");
		operationPostStates.put("uninstall", "initial");
		operationPostStates.put("configure", "configured");
		operationPostStates.put("start", "started");
		operationPostStates.put("stop", "stopped");

		// VM's
		operationPreStates.put("createVM", "starting");
		operationPreStates.put("waitForAvailability", "starting");

		operationPostStates.put("createVM", "configured");
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
			/*
			 * INITIAL, CREATING, CREATED, CONFIGURING, CONFIGURED, STARTING, STARTED, STOPPING, STOPPED, DELETING, DELETED, ERROR
			 */
			// given state is unstable
			switch (state) {
			case "creating":
				return "created";
			case "deleting":
				return "deleted";
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
			case "initial":
				return "created";
			case "created":
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
