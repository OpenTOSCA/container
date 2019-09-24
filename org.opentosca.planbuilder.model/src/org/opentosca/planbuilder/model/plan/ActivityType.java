package org.opentosca.planbuilder.model.plan;

/**
 * Represents the abstract activity types supported by the planbuilder system
 *
 * @author Kálmán Képes - kepes@iaas.uni-stuttgart.de
 *
 */
public enum ActivityType {

    PROVISIONING, TERMINATION, RECURSIVESELECTION, STRATEGICSELECTION, FREEZE, DEFROST, MIGRATION, TEST, BACKUP, MONITORING, NONE, BPMN4TOSCA
}
