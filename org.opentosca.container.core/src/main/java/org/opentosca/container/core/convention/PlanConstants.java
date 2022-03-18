package org.opentosca.container.core.convention;

public abstract class PlanConstants {

    public static final String OpenTOSCA_LifecycleInterface = "OpenTOSCA-Lifecycle-Interface";
    public static final String OpenTOSCA_StatefulLifecycleInterface = "OpenTOSCA-Stateful-Lifecycle-Interface";
    public static final String OpenTOSCA_ManagementFeatureInterface = "OpenTOSCA-Management-Feature-Interface";

    public static final String OpenTOSCA_BuildPlanOperation = "initiate";
    public static final String OpenTOSCA_TerminationPlanOperation = "terminate";

    public static final String OpenTOSCA_FreezePlanOperation = "freeze";
    public static final String OpenTOSCA_DefrostPlanOperation = "defrost";
    public static final String OpenTOSCA_UpdatePlanOperation = "update";
    public static final String OpenTOSCA_BackupPlanOperation = "backup";
    public static final String OpenTOSCA_TestPlanOperation = "test";
}
