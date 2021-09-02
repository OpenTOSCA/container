package org.opentosca.planbuilder.core.bpel.handlers;

import java.io.IOException;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.core.bpel.fragments.BPELProcessFragments;
import org.opentosca.planbuilder.core.plugins.context.Variable;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class OpenTOSCARuntimeHandler {

    private final BPELProcessFragments fragments;
    private final BPELPlanHandler planHandler;

    public OpenTOSCARuntimeHandler() throws ParserConfigurationException {
        this.fragments = new BPELProcessFragments();
        this.planHandler = new BPELPlanHandler();
    }

    /**
     * Generate bpel code which checks whether the runtime is available
     * @param plan the plan to add the check to
     * @param appendBefore the element to append the generate code to
     * @param faultMessage the message of the fault to throw when it is not working
     */
    public void checkRuntimeAvailability(BPELPlan plan, Element appendBefore, String faultMessage) throws IOException, SAXException {
        Variable runtimeResourceURLVariable = this.planHandler.createGlobalStringVariable("runtimeResourceURL" + plan.getIdForNamesAndIncrement(), "empty", plan);
        Variable runtimeResourceResponse = this.planHandler.createGlobalStringVariable("runtimeResourceResponse" + plan.getIdForNamesAndIncrement(), "empty", plan);
        Variable runtimeResourceStatusCode = this.planHandler.createGlobalStringVariable("runtimeResourceStatusCode" + plan.getIdForNamesAndIncrement(), "-1", plan);
        Variable faultVariable = this.planHandler.createGlobalStringVariable("runtimeResourceFaultVariable" + plan.getIdForNamesAndIncrement(), faultMessage, plan);

               Node sequenceNode = this.fragments.createEmptySequence("checkRuntimeAvailabilitySequence" + plan.getIdForNamesAndIncrement());
        sequenceNode = plan.getBpelDocument().importNode(sequenceNode, true);

        Node assignInputToURL = this.fragments.generateAssignFromInputMessageToStringVariableAsNode("instanceDataAPIUrl",runtimeResourceURLVariable.getVariableName());
        assignInputToURL = plan.getBpelDocument().importNode(assignInputToURL, true);
        sequenceNode.appendChild(assignInputToURL);


        Node httpGET = this.fragments.generateBPEL4RESTLightGETonURLAsNode(runtimeResourceURLVariable.getVariableName(), runtimeResourceResponse.getVariableName(), runtimeResourceStatusCode.getVariableName());

        httpGET = plan.getBpelDocument().importNode(httpGET, true);
        sequenceNode.appendChild(httpGET);

        Node checkStatusCode = this.fragments.createIfTrueThrowsError("number($" + runtimeResourceStatusCode.getVariableName() + ") >= number('400')", new QName("http://opentosca.org/faults", "RuntimeNotAvailable"), faultVariable.getVariableName());

        checkStatusCode = plan.getBpelDocument().importNode(checkStatusCode, true);
        sequenceNode.appendChild(checkStatusCode);

        appendBefore.getParentNode().insertBefore(sequenceNode, appendBefore);
    }
}
