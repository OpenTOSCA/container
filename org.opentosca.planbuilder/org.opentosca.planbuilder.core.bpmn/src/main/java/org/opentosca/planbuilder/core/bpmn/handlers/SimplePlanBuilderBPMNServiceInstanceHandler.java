package org.opentosca.planbuilder.core.bpmn.handlers;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.core.bpmn.fragments.BPMNProcessFragments;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNDataObject;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNPlan;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNSubprocess;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNSubprocessType;

/**
 * Handles all actions regarding service template url, service instance id/url. Also, it takes care if the service
 * instance id is part of the input.
 */
public class SimplePlanBuilderBPMNServiceInstanceHandler {
    protected static final String ServiceInstanceURLVarKeyword = "ServiceInstanceURL";
    protected static final String ServiceInstanceIDVarKeyword = "ServiceInstanceID";
    protected static final String ServiceTemplateURLVarKeyword = "ServiceTemplateURL";

    protected final BPMNProcessFragments fragments;
    protected final BPMNPlanHandler bpmnPlanHandler;
    protected final DocumentBuilderFactory docFactory;

    public SimplePlanBuilderBPMNServiceInstanceHandler() throws ParserConfigurationException {
        this.fragments = new BPMNProcessFragments();
        this.bpmnPlanHandler = new BPMNPlanHandler();
        this.docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setNamespaceAware(true);
    }

    /**
     * This method appends a create-service-instance task, a data object reference and a data object to the bpmn plan.
     * Can be skipped if the plan contains the service instance vars as input.
     */
    public void appendCreateServiceInstanceVarsAndInitializeWithInstanceDataAPI(BPMNPlan bpmnPlan) {
        BPMNDataObject dataObject = new BPMNDataObject(BPMNSubprocessType.DATA_OBJECT_ST, ServiceInstanceURLVarKeyword + bpmnPlan.getInternalCounterId());
        String idPrefix = BPMNSubprocessType.CREATE_ST_INSTANCE.name();
        String idSubprocessPrefix = BPMNSubprocessType.SUBPROCESS.toString();
        BPMNSubprocess createServiceInstanceSubprocess = new BPMNSubprocess(BPMNSubprocessType.SUBPROCESS, idSubprocessPrefix + "_" + dataObject.getId());
        BPMNSubprocess createServiceInstanceTask = new BPMNSubprocess(BPMNSubprocessType.CREATE_ST_INSTANCE, idPrefix + "_" + bpmnPlan.getIdForNamesAndIncrement());
        createServiceInstanceTask.setBuildPlan(bpmnPlan);
        String serviceInstanceURL = ServiceInstanceURLVarKeyword + bpmnPlan.getInternalCounterId();
        createServiceInstanceSubprocess.setSubProServiceInstanceTask(createServiceInstanceTask);
        createServiceInstanceTask.setResultVariableName(serviceInstanceURL);
        createServiceInstanceSubprocess.setServiceInstanceURL(serviceInstanceURL);
        createServiceInstanceSubprocess.setBuildPlan(bpmnPlan);
        createServiceInstanceSubprocess.setDataObject(dataObject);
        dataObject.getProperties().add(ServiceInstanceURLVarKeyword + bpmnPlan.getInternalCounterId());
        dataObject.getProperties().add(ServiceInstanceIDVarKeyword + bpmnPlan.getInternalCounterId());
        dataObject.getProperties().add(ServiceTemplateURLVarKeyword + bpmnPlan.getInternalCounterId());
        dataObject.setServiceInstanceURL(serviceInstanceURL);
        dataObject.setId("DataObject_" + dataObject.getId());
        bpmnPlan.getDataObjectsList().add(dataObject);
        createServiceInstanceSubprocess.addTaskToSubprocess(createServiceInstanceTask);
        bpmnPlan.getSubprocess().add(createServiceInstanceSubprocess);
    }

    /**
     * If the service instance is given as input, the data object will contain it as input. In order that the scripts
     * work correct the name of the parameter should match the ServiceInstanceURLVarKeyword
     */
    public BPMNSubprocess addServiceInstanceHandlingFromInput(final BPMNPlan bpmnPlan) {
        String idSubprocessPrefix = BPMNSubprocessType.SUBPROCESS.toString();
        BPMNSubprocess dataObjectSubprocess = new BPMNSubprocess(BPMNSubprocessType.SUBPROCESS, idSubprocessPrefix + "_InputOutput_DataObject" + bpmnPlan.getIdForNamesAndIncrement());
        BPMNDataObject dataObjectInput = new BPMNDataObject(BPMNSubprocessType.DATA_OBJECT_INOUT, "DataObject_InputOutput");
        dataObjectInput.setProperties(bpmnPlan.getInputParameters());
        bpmnPlan.getDataObjectsList().add(dataObjectInput);
        dataObjectSubprocess.setBuildPlan(bpmnPlan);
        dataObjectSubprocess.setDataObject(dataObjectInput);
        bpmnPlan.addSubprocess(dataObjectSubprocess);
        return dataObjectSubprocess;
    }

    public String findServiceInstanceUrlVariableName(final BPMNPlan plan) {
        return getCorrespondingServiceInstanceVariable(plan, ServiceInstanceURLVarKeyword);
    }

    public String findServiceInstanceIdVarName(BPMNPlan plan) {
        return getCorrespondingServiceInstanceVariable(plan, ServiceInstanceIDVarKeyword);
    }

    public String findServiceTemplateUrlVariableName(BPMNPlan plan) {
        return getCorrespondingServiceInstanceVariable(plan, ServiceTemplateURLVarKeyword);
    }

    /**
     * Returns the corresponding variable of the bpmn plan based on the serviceInstanceIDVarKeyword.
     */
    private String getCorrespondingServiceInstanceVariable(BPMNPlan plan, String serviceInstanceIDVarKeyword) {
        ArrayList<String> ids = new ArrayList<>();
        for (BPMNDataObject dataObject : plan.getDataObjectsList()) {
            // we can restrict it to service-instance / input output data objects since they are the only data objects that
            // contains the information in the input
            if (dataObject.getDataObjectType() == BPMNSubprocessType.DATA_OBJECT_ST || dataObject.getDataObjectType() == BPMNSubprocessType.DATA_OBJECT_INOUT) {
                for (String property : dataObject.getProperties()) {
                    if (property.startsWith(serviceInstanceIDVarKeyword)) {
                        ids.add(property);
                    }
                }
            }
        }
        return getLowestId(ids, serviceInstanceIDVarKeyword);
    }

    /**
     * Computes the lowest id regarding the keyword.
     */
    public String getLowestId(Collection<String> ids, String keyword) {
        double lowestIdValue = -1;
        String lowestId = null;

        for (String id : ids) {
            double currentValue = Double.parseDouble(id.substring(keyword.length()));
            if (lowestIdValue == -1) {
                lowestIdValue = currentValue;
                lowestId = id;
            }
        }
        return lowestId;
    }

    /**
     * This method creates a set state task which is appended to the bpmn plan.
     */
    public void addServiceInstanceSetState(String state, BPMNPlan bpmnPlan) {
        BPMNSubprocess setInstanceState = new BPMNSubprocess(BPMNSubprocessType.SET_ST_STATE, "Activity_ServiceInstanceState" + bpmnPlan.getIdForOuterFlowTestAndIncrement());
        setInstanceState.setInstanceState(state);
        setInstanceState.setBuildPlan(bpmnPlan);
        bpmnPlan.addSubprocess(setInstanceState);
    }
}
