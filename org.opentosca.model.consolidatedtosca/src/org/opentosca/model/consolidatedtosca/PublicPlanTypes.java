package org.opentosca.model.consolidatedtosca;

import java.io.Serializable;

public enum PublicPlanTypes implements Comparable<PublicPlanTypes>,
		Serializable {
	BUILD("http://docs.oasis-open.org/tosca/ns/2011/12/PlanTypes/BuildPlan"), TERMINATION(
			"http://docs.oasis-open.org/tosca/ns/2011/12/PlanTypes/TerminationPlan"), OTHERMANAGEMENT(
			"undefined or custom management plan");
	
	private String type;
	
	
	PublicPlanTypes(String type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		return this.type;
	}
	
	/**
	 * This method returns the String representation of the type. For example: <br/>
	 * The enum BUILD has the URI
	 * http://docs.oasis-open.org/tosca/ns/2011/12/PlanTypes/BuildPlan. This
	 * method returns for BUILD the String "BUILD".
	 * 
	 * @return String representation
	 */
	public String toEnumRepresentation() {
		if (this.type.startsWith(PublicPlanTypes.BUILD.toString())) {
			return "BUILD";
		} else if (this.type.startsWith(PublicPlanTypes.TERMINATION.toString())) {
			return "TERMINATION";
		} else {
			return "OTHERMANAGEMENT";
		}
	}
	
	/**
	 * Checks if the given String is one of the URIs defined of TOSCA (build and
	 * termination plan). If not, the Type is of OTHERMANAGEMENT.
	 * <p/>
	 * For example: The given parameter contains the URI
	 * http://docs.oasis-open.org/tosca/ns/2011/12/PlanTypes/BuildPlan. This URI
	 * is mapped to the enum BUILD.
	 * 
	 * @param planType String
	 * @return PublicPlanTypes plan type.
	 */
	public static PublicPlanTypes isPlanTypeURI(String planType) {
		if (planType.startsWith(PublicPlanTypes.BUILD.toString())) {
			return BUILD;
		} else if (planType.startsWith(PublicPlanTypes.TERMINATION.toString())) {
			return TERMINATION;
		} else {
			return OTHERMANAGEMENT;
		}
	}
	
	/**
	 * Checks the given String if it is one of the ENUM Strings defined in this
	 * ENUM (BUILD and TERMINATION). If not, the Type is of OTHERMANAGEMENT.
	 * <p/>
	 * For example: The given parameter contains the String "BUILD". This String
	 * is mapped to the enum BUILD.
	 * 
	 * @param planType String
	 * @return PublicPlanTypes plan type.
	 */
	public static PublicPlanTypes isPlanTypeEnumRepresentation(String planType) {
		if (planType.equals("BUILD")) {
			return BUILD;
		} else if (planType.equals("TERMINATION")) {
			return TERMINATION;
		} else {
			return OTHERMANAGEMENT;
		}
	}
}
