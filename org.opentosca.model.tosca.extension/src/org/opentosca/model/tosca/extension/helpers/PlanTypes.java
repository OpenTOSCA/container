package org.opentosca.model.tosca.extension.helpers;

import java.io.Serializable;

public enum PlanTypes implements Comparable<PlanTypes>, Serializable {

    BUILD("http://docs.oasis-open.org/tosca/ns/2011/12/PlanTypes/BuildPlan"), OTHERMANAGEMENT(
	"undefined or custom management plan"), APPLICATION("http://www.opentosca.org"), TERMINATION(
	    "http://docs.oasis-open.org/tosca/ns/2011/12/PlanTypes/TerminationPlan");

    private String type;

    PlanTypes(String type) {
	this.type = type;
    }

    @Override
    public String toString() {
	return type;
    }

    /**
     * This method returns the String representation of the type. For example:
     * <br/>
     * The enum BUILD has the URI
     * http://docs.oasis-open.org/tosca/ns/2011/12/PlanTypes/BuildPlan. This
     * method returns for BUILD the String "BUILD".
     * 
     * @return String representation
     */
    public String toEnumRepresentation() {
	if (type.startsWith(PlanTypes.BUILD.toString())) {
	    return "BUILD";
	} else if (type.startsWith(PlanTypes.TERMINATION.toString())) {
	    return "TERMINATION";
	} else if (type.startsWith(PlanTypes.APPLICATION.toString())) {
	    return "APPLICATION";
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
     * @param planType
     *            String
     * @return PlanTypes plan type.
     */
    public static PlanTypes isPlanTypeURI(String planType) {
	if (planType.startsWith(PlanTypes.BUILD.toString())) {
	    return BUILD;
	} else if (planType.startsWith(PlanTypes.TERMINATION.toString())) {
	    return TERMINATION;
	}  else if (planType.startsWith(PlanTypes.APPLICATION.toString())) {
	    return PlanTypes.APPLICATION;
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
     * @param planType
     *            String
     * @return PlanTypes plan type.
     */
    public static PlanTypes isPlanTypeEnumRepresentation(String planType) {
	if (planType.equals("BUILD")) {
	    return BUILD;
	} else if (planType.equals("TERMINATION")) {
	    return TERMINATION;
	}  else if (planType.equals("APPLICATION")) {
	    return APPLICATION;
	} else {
	    return OTHERMANAGEMENT;
	}
    }
}
