package org.opentosca.planbuilder.core.bpmn.handlers;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.opentosca.container.core.model.ModelUtils;
import org.opentosca.planbuilder.core.bpmn.fragments.BPMNProcessFragments;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNPlan;

import java.util.List;
import java.util.Map;

/**
 * Reference to package org.opentosca.planbuilder.core.bpel.handlers; AbstractServiceInstanceHandler
 * The class is dedicated to handler all service instance
 */
public class SimplePlanBuilderServiceInstanceHandler {
    protected static final String ServiceInstanceURLVarKeyword = "OpenTOSCAContainerAPIServiceInstanceURL";
    protected static final String ServiceInstanceIDVarKeyword = "OpenTOSCAContainerAPIServiceInstanceID";
    protected static final String ServiceTemplateURLVarKeyword = "OpenTOSCAContainerAPIServiceTemplateURL";
    protected static final String PlanInstanceURLVarKeyword = "OpenTOSCAContainerAPIPlanInstanceURL";
    protected static final String InstanceDataAPIUrlKeyword = "instanceDataAPIUrl";
    protected static final String CorrelationIDKeyWord = "CorrelationID";

    // user-defined input
    public static final String SERVICETEMPLATE_GETINPUT_PREFIX = "get_input: ";

    // TODO: consider user-defined output

    protected final BPMNProcessFragments fragments;

    protected final BPMNPlanHandler bpmnPlanHandler;

    protected  final DocumentBuilderFactory docFactory;

    public SimplePlanBuilderServiceInstanceHandler() throws ParserConfigurationException {
        this.fragments = new BPMNProcessFragments();
        this.bpmnPlanHandler = new BPMNPlanHandler();
        this.docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setNamespaceAware(true);
    }


    /**
     * Adds TOSCA Container default input parameters to BPMN Plan
     *
     * @param newBuildPlan
     */
    public void addInputOutputParameterCorrelationIDAndInstanceDataAPI(BPMNPlan newBuildPlan) {
        bpmnPlanHandler.addInputParameter(InstanceDataAPIUrlKeyword, newBuildPlan);
        bpmnPlanHandler.addInputParameter(CorrelationIDKeyWord, newBuildPlan);

        bpmnPlanHandler.addOutputParameter(CorrelationIDKeyWord, newBuildPlan);
    }

    // TODO: implement
    public String findServiceInstanceUrlVariableName(BPMNPlan newBuildPlan) {
        return "";
    }

    // TODO: implement
    public String findServiceInstanceIdVarName(BPMNPlan newBuildPlan) {
        return "";
    }

    // TODO: implement
    public String findServiceTemplateUrlVariableName(BPMNPlan newBuildPlan) {
        return "";
    }

    // TODO: implement
    public String findPlanInstanceUrlVariableName(BPMNPlan newBuildPlan) {
        return "";
    }

    /**
     * Adds User-defined input parameters to BPMN Plan
     * iterate through all Node Template with user-defined input
     * ex. "get_input: DockerEngineURL" -> "DockerEngineURL"
     * @param newBuildPlan
     */
    public void addInputOutputParameterUserDefined(BPMNPlan newBuildPlan) {
        TServiceTemplate serviceTemplate = newBuildPlan.getServiceTemplate();
        List<TNodeTemplate> list = serviceTemplate.getTopologyTemplate().getNodeTemplates();
        for (TNodeTemplate nodeTemplate : list) {
            Map<String, String> propMap = ModelUtils.asMap(nodeTemplate.getProperties());
            for (String value : propMap.values()) {
                if (value.startsWith(SERVICETEMPLATE_GETINPUT_PREFIX)) {
                    bpmnPlanHandler.addInputParameter(value.substring(SERVICETEMPLATE_GETINPUT_PREFIX.length()), newBuildPlan);
                }
            }
        }
    }
}
