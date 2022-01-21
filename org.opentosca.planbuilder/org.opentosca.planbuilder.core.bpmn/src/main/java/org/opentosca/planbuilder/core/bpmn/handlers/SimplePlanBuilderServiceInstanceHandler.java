package org.opentosca.planbuilder.core.bpmn.handlers;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.core.bpmn.fragments.BPMNProcessFragments;

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

    protected final BPMNProcessFragments fragments;

    protected final BPMNPlanHandler bpmnPlanHandler;

    protected  final DocumentBuilderFactory docFactory;

    public SimplePlanBuilderServiceInstanceHandler() throws ParserConfigurationException {
        this.fragments = new BPMNProcessFragments();
        this.bpmnPlanHandler = new BPMNPlanHandler();
        this.docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setNamespaceAware(true);
    }


}
