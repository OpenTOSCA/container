package org.opentosca.planbuilder.core.bpel.handlers;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.core.bpel.fragments.BPELProcessFragments;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public abstract class AbstractServiceInstanceHandler {

    protected static final String ServiceInstanceURLVarKeyword = "OpenTOSCAContainerAPIServiceInstanceURL";
    protected static final String ServiceInstanceIDVarKeyword = "OpenTOSCAContainerAPIServiceInstanceID";
    protected static final String ServiceTemplateURLVarKeyword = "OpenTOSCAContainerAPIServiceTemplateURL";
    protected static final String PlanInstanceURLVarKeyword = "OpenTOSCAContainerAPIPlanInstanceURL";
    protected static final String InstanceDataAPIUrlKeyword = "instanceDataAPIUrl";

    protected final BPELProcessFragments fragments;

    protected final BPELPlanHandler bpelProcessHandler;

    protected final DocumentBuilderFactory docFactory;

    public AbstractServiceInstanceHandler() throws ParserConfigurationException {
        this.bpelProcessHandler = new BPELPlanHandler();
        this.fragments = new BPELProcessFragments();
        this.docFactory = DocumentBuilderFactory.newInstance();
        this.docFactory.setNamespaceAware(true);
    }

    public String getLowestId(Collection<String> ids, String keyword) {
        double lowestIdValue = -1;
        String lowestId = null;

        for (String id : ids) {
            double currentValue = Double.valueOf(id.substring(keyword.length()));
            if (lowestIdValue == -1) {
                lowestIdValue = currentValue;
                lowestId = id;
            }
        }

        return lowestId;
    }

    public Collection<String> findServiceInstanceVarNames(final BPELPlanHandler bpelplanHandler, final BPELPlan plan,
                                                          String keyword) {
        Collection<String> serviceInstanceVariableNames = new HashSet<String>();
        for (final String varName : bpelplanHandler.getMainVariableNames(plan)) {
            if (varName.contains(keyword)) {
                serviceInstanceVariableNames.add(varName);
            }
        }
        return serviceInstanceVariableNames;
    }

    public Collection<String> findPlanInstanceURLVarName(final BPELPlanHandler bpelplanHandler, final BPELPlan plan) {
        return findServiceInstanceVarNames(bpelplanHandler, plan, PlanInstanceURLVarKeyword);
    }

    public Collection<String> findServiceTemplateURLVarName(final BPELPlanHandler bpelplanHandler,
                                                            final BPELPlan plan) {
        return findServiceInstanceVarNames(bpelplanHandler, plan, ServiceTemplateURLVarKeyword);
    }

    public Collection<String> findServiceInstanceURLVarName(final BPELPlanHandler bpelplanHandler,
                                                            final BPELPlan plan) {
        return findServiceInstanceVarNames(bpelplanHandler, plan, ServiceInstanceURLVarKeyword);
    }

    public Collection<String> findServiceInstanceIdVarNames(final BPELPlanHandler bpelplanHandler,
                                                            final BPELPlan plan) {
        return findServiceInstanceVarNames(bpelplanHandler, plan, ServiceInstanceIDVarKeyword);
    }

    public String addServiceTemplateURLVariable(BPELPlan plan) {
        return this.bpelProcessHandler.addGlobalStringVariable(ServiceTemplateURLVarKeyword
            + System.currentTimeMillis(), plan);
    }

    public String addServiceInstanceIDVariable(BPELPlan plan) {
        return this.bpelProcessHandler.addGlobalStringVariable(ServiceInstanceIDVarKeyword + System.currentTimeMillis(),
            plan);
    }

    public String addPlanInstanceURLVariable(BPELPlan plan) {
        return this.bpelProcessHandler.addGlobalStringVariable(PlanInstanceURLVarKeyword + System.currentTimeMillis(),
            plan);
    }

    public String addServiceInstanceURLVariable(BPELPlan plan) {
        return this.bpelProcessHandler.addGlobalStringVariable(ServiceInstanceURLVarKeyword
            + System.currentTimeMillis(), plan);
    }

    public String addInstanceDataAPIURLVariable(BPELPlan plan) {
        return this.bpelProcessHandler.addGlobalStringVariable(InstanceDataAPIUrlKeyword, plan);
    }

    public void addAssignServiceInstanceIdVarFromServiceInstanceURLVar(final BPELPlan plan,
                                                                       String serviceInstanceURLVarName,
                                                                       String serviceInstanceIDVarName) {
        if (serviceInstanceURLVarName == null) {
            throw new IllegalArgumentException("ServiceInstanceURLVarName is null in plan " + plan.getId());
        }

        if (serviceInstanceIDVarName == null) {
            throw new IllegalArgumentException("ServiceInstanceIDVarName is null in plan " + plan.getId());
        }

        try {
            Node assignFragment =
                this.fragments.createAssignVarToVarWithXpathQueryAsNode("assignServiceInstanceIDFromServiceInstanceURl"
                        + System.currentTimeMillis(), serviceInstanceURLVarName, serviceInstanceIDVarName,
                    "tokenize(//*,'/')[last()]");
            assignFragment = plan.getBpelDocument().importNode(assignFragment, true);
            appendToInitSequence(assignFragment, plan);
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final SAXException e) {
            e.printStackTrace();
        }
    }

    /**
     * Appends the given node the the main sequence of the buildPlan this context belongs to
     *
     * @param node a XML DOM Node
     * @return true if adding the node to the main sequence was successfull
     */
    protected boolean appendToInitSequence(final Node node, final BPELPlan buildPlan) {

        final Element flowElement = buildPlan.getBpelMainFlowElement();

        final Node mainSequenceNode = flowElement.getParentNode();

        mainSequenceNode.insertBefore(node, flowElement);

        return true;
    }

    /**
     * Adds an element with the given varName to the input message of the given plan and adds logic assign the input
     * value to an internal variable with the given varName.
     *
     * @param plan           a plan to add the logic to
     * @param inputLocalName a name to use inside the input message and as name for the global string variable where the
     *                       value will be added to.
     * @return a String containing the generated Variable Name of the Variable holding the value from the input at
     * runtime
     */
    protected String appendAssignFromInputToVariable(final BPELPlan plan, final String inputLocalName,
                                                     String variableName) {

        // generate single string variable for InstanceDataAPI HTTP calls, as
        // REST BPEL PLugin
        // can only handle simple xsd types (no queries from input message)

        try {
            Node assignNode =
                this.fragments.generateAssignFromInputMessageToStringVariableAsNode(inputLocalName, variableName);

            assignNode = plan.getBpelDocument().importNode(assignNode, true);
            appendToInitSequence(assignNode, plan);
        } catch (final IOException e) {
            e.printStackTrace();
            return null;
        } catch (final SAXException e) {
            e.printStackTrace();
            return null;
        }
        return variableName;
    }
}
