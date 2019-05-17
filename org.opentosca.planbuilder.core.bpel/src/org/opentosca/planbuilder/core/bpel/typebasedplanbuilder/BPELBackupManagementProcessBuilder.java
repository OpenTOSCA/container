package org.opentosca.planbuilder.core.bpel.typebasedplanbuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.container.core.tosca.convention.Interfaces;
import org.opentosca.planbuilder.AbstractManagementFeaturePlanBuilder;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.bpel.fragments.BPELProcessFragments;
import org.opentosca.planbuilder.core.bpel.handlers.BPELFinalizer;
import org.opentosca.planbuilder.core.bpel.handlers.BPELPlanHandler;
import org.opentosca.planbuilder.core.bpel.handlers.CorrelationIDInitializer;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.NodeRelationInstanceVariablesHandler;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.PropertyVariableHandler;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.SimplePlanBuilderServiceInstanceHandler;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.AbstractPlan.PlanType;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELScope;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractInterface;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.planbuilder.model.tosca.AbstractParameter;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.opentosca.planbuilder.plugins.context.Property2VariableMapping;
import org.opentosca.planbuilder.plugins.context.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * <p>
 * This process builder creates a backup management plan if one of the NodeTemplates in the topology
 * is of a type that defines the freeze interface.
 * </p>
 *
 * Copyright 2019 IAAS University of Stuttgart <br>
 * <br>
 */
public class BPELBackupManagementProcessBuilder extends AbstractManagementFeaturePlanBuilder {

    private final static Logger LOG = LoggerFactory.getLogger(BPELBackupManagementProcessBuilder.class);

    // handler for abstract buildplan operations
    public BPELPlanHandler planHandler;

    // class for initializing properties inside the build plan
    private final PropertyVariableHandler propertyInitializer;

    // adds serviceInstance Variable and instanceDataAPIUrl to buildPlans
    private SimplePlanBuilderServiceInstanceHandler serviceInstanceVarsHandler;

    // adds nodeInstanceIDs to each templatePlan
    private NodeRelationInstanceVariablesHandler instanceVarsHandler;

    // class for finalizing build plans (e.g when some template didn't receive
    // some provisioning logic and they must be filled with empty elements)
    private final BPELFinalizer finalizer;

    private BPELProcessFragments bpelFragments;

    private CorrelationIDInitializer correlationHandler;

    /**
     * <p>
     * Default Constructor
     * </p>
     */
    public BPELBackupManagementProcessBuilder() {
        try {
            this.planHandler = new BPELPlanHandler();
            this.serviceInstanceVarsHandler = new SimplePlanBuilderServiceInstanceHandler();
            this.instanceVarsHandler = new NodeRelationInstanceVariablesHandler(this.planHandler);
            this.bpelFragments = new BPELProcessFragments();
            this.correlationHandler = new CorrelationIDInitializer();
        }
        catch (final ParserConfigurationException e) {
            LOG.error("Error while initializing BuildPlanHandler", e);
        }
        this.propertyInitializer = new PropertyVariableHandler(this.planHandler);
        this.finalizer = new BPELFinalizer();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opentosca.planbuilder.IPlanBuilder#buildPlan(java.lang.String,
     * org.opentosca.planbuilder.model.tosca.AbstractDefinitions, javax.xml.namespace.QName)
     */
    @Override
    public BPELPlan buildPlan(final String csarName, final AbstractDefinitions definitions,
                              final AbstractServiceTemplate serviceTemplate) {
        LOG.debug("Creating Backup Management Plan...");

        final String processName = ModelUtils.makeValidNCName(serviceTemplate.getId() + "_backupManagementPlan");
        final String processNamespace = serviceTemplate.getTargetNamespace() + "_backupManagementPlan";

        final AbstractPlan abstractFreezePlan = generateMOG(new QName(processNamespace, processName).toString(),
                                                            definitions, serviceTemplate, processNamespace);

        LOG.debug("Generated the following abstract backup plan: ");
        LOG.debug(abstractFreezePlan.toString());

        abstractFreezePlan.setType(PlanType.MANAGE);
        final BPELPlan newFreezePlan =
            this.planHandler.createEmptyBPELPlan(processNamespace, processName, abstractFreezePlan, "backup");

        this.planHandler.initializeBPELSkeleton(newFreezePlan, csarName);

        newFreezePlan.setTOSCAInterfaceName("OpenTOSCA-Management-Feature-Interface");
        newFreezePlan.setTOSCAOperationname("backup");

        this.instanceVarsHandler.addInstanceURLVarToTemplatePlans(newFreezePlan, serviceTemplate);
        this.instanceVarsHandler.addInstanceIDVarToTemplatePlans(newFreezePlan, serviceTemplate);

        final Property2VariableMapping propMap =
            this.propertyInitializer.initializePropertiesAsVariables(newFreezePlan, serviceTemplate);

        // initialize instanceData handling
        this.planHandler.registerExtension("http://www.apache.org/ode/bpel/extensions/bpel4restlight", true,
                                           newFreezePlan);
        this.serviceInstanceVarsHandler.addServiceInstanceHandlingFromInput(newFreezePlan);
        final String serviceTemplateURLVarName =
            this.serviceInstanceVarsHandler.getServiceTemplateURLVariableName(newFreezePlan);
        this.serviceInstanceVarsHandler.appendInitPropertyVariablesFromServiceInstanceData(newFreezePlan, propMap,
                                                                                           serviceTemplateURLVarName,
                                                                                           serviceTemplate);

        // fetch all node instances that are running
        this.instanceVarsHandler.addNodeInstanceFindLogic(newFreezePlan,
                                                          "?state=STARTED&amp;state=CREATED&amp;state=CONFIGURED",
                                                          serviceTemplate);
        this.instanceVarsHandler.addPropertyVariableUpdateBasedOnNodeInstanceID(newFreezePlan, propMap,
                                                                                serviceTemplate);

        try {
            appendGenerateStatefulServiceTemplateLogic(newFreezePlan);
        }
        catch (final IOException e) {
            e.printStackTrace();
        }
        catch (final SAXException e) {
            e.printStackTrace();
        }

        runPlugins(newFreezePlan, propMap, csarName);

        this.correlationHandler.addCorrellationID(newFreezePlan);
        this.finalizer.finalize(newFreezePlan);

        LOG.debug("Created Plan:");
        LOG.debug(ModelUtils.getStringFromDoc(newFreezePlan.getBpelDocument()));

        return newFreezePlan;
    }

    private void runPlugins(final BPELPlan plan, final Property2VariableMapping propMap, final String csarName) {
        final List<BPELScope> changedActivities = new ArrayList<>();

        final String statefulServiceTemplateUrlVarName = findStatefulServiceTemplateUrlVar(plan);

        final String serviceInstanceUrl = this.serviceInstanceVarsHandler.findServiceInstanceUrlVariableName(plan);
        final String serviceInstanceId = this.serviceInstanceVarsHandler.findServiceInstanceIdVarName(plan);
        final String serviceTemplateUrl = this.serviceInstanceVarsHandler.findServiceTemplateUrlVariableName(plan);


        for (final BPELScope templatePlan : plan.getTemplateBuildPlans()) {
            final BPELPlanContext context = new BPELPlanContext(plan, templatePlan, propMap, plan.getServiceTemplate(),
                serviceInstanceUrl, serviceInstanceId, serviceTemplateUrl, csarName);


            if (templatePlan.getNodeTemplate() != null) {
                final AbstractNodeTemplate nodeTemplate = templatePlan.getNodeTemplate();

                final String saveStateUrlVarName =
                    this.planHandler.addGlobalStringVariable("nodeTemplateStateSaveURL", plan);

                final String xpathQuery = "concat($" + statefulServiceTemplateUrlVarName
                    + ",'/topologytemplate/nodetemplates/" + nodeTemplate.getId() + "/state')";
                try {
                    Node assignSaveStateURL =
                        this.bpelFragments.createAssignVarToVarWithXpathQueryAsNode("assignNodeTemplate"
                            + nodeTemplate.getId() + "state" + System.currentTimeMillis(),
                                                                                    statefulServiceTemplateUrlVarName,
                                                                                    saveStateUrlVarName, xpathQuery);
                    assignSaveStateURL = context.importNode(assignSaveStateURL);
                    context.getPrePhaseElement().appendChild(assignSaveStateURL);
                }
                catch (final IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                catch (final SAXException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                final Variable saveStateUrlVar = BPELPlanContext.getVariable(saveStateUrlVarName);

                final Map<AbstractParameter, Variable> inputs = new HashMap<>();

                inputs.put(getSaveStateParameter(getSaveStateOperation(nodeTemplate)), saveStateUrlVar);

                context.executeOperation(nodeTemplate, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE,
                                         Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE_FREEZE, inputs);
            }
        }
    }

    @Override
    public List<AbstractPlan> buildPlans(final String csarName, final AbstractDefinitions definitions) {
        LOG.info("Building the Backup Management Plans");
        final List<AbstractPlan> plans = new ArrayList<>();
        for (final AbstractServiceTemplate serviceTemplate : definitions.getServiceTemplates()) {

            if (containsManagementInterface(serviceTemplate, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE)) {
                final BPELPlan newBuildPlan = buildPlan(csarName, definitions, serviceTemplate);
                if (Objects.nonNull(newBuildPlan)) {
                    LOG.debug("Created Backup Management Plan "
                        + newBuildPlan.getBpelProcessElement().getAttribute("name"));
                    plans.add(newBuildPlan);
                }
            }
        }
        return plans;
    }

    private void appendGenerateStatefulServiceTemplateLogic(final BPELPlan plan) throws IOException, SAXException {
        final QName serviceTemplateId = plan.getServiceTemplate().getQName();

        this.planHandler.addStringElementToPlanRequest(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE_FREEZE_MANDATORY_PARAM_ENDPOINT,
                                                       plan);

        // var to save serviceTemplate url on storage service
        final String statefulServiceTemplateVarName =
            this.planHandler.addGlobalStringVariable("statefulServiceTemplateUrl" + System.currentTimeMillis(), plan);
        final String responseVarName = this.planHandler.createAnyTypeVar(plan);

        // assign variable with the original service template url
        Node assignStatefuleServiceTemplateStorageVar =
            this.bpelFragments.createAssignVarToVarWithXpathQueryAsNode("assignServiceTemplateStorageUrl"
                + System.currentTimeMillis(), "input", statefulServiceTemplateVarName,
                                                                        "concat(//*[local-name()='"
                                                                            + Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE_FREEZE_MANDATORY_PARAM_ENDPOINT
                                                                            + "']/text(),'/servicetemplates/"
                                                                            + URLEncoder.encode(URLEncoder.encode(serviceTemplateId.getNamespaceURI(),
                                                                                                                  "UTF-8"),
                                                                                                "UTF-8")
                                                                            + "','/" + serviceTemplateId.getLocalPart()
                                                                            + "','/createnewstatefulversion')");
        assignStatefuleServiceTemplateStorageVar =
            plan.getBpelDocument().importNode(assignStatefuleServiceTemplateStorageVar, true);
        plan.getBpelMainSequenceElement().insertBefore(assignStatefuleServiceTemplateStorageVar,
                                                       plan.getBpelMainSequencePropertyAssignElement());

        // create append POST for creating a stateful service template version
        Node createStatefulServiceTemplatePOST =
            this.bpelFragments.createHTTPPOST(statefulServiceTemplateVarName, responseVarName);

        createStatefulServiceTemplatePOST = plan.getBpelDocument().importNode(createStatefulServiceTemplatePOST, true);

        plan.getBpelMainSequenceElement().insertBefore(createStatefulServiceTemplatePOST,
                                                       plan.getBpelMainSequencePropertyAssignElement());

        // read response and assign url of created stateful service template query the localname from the
        // response
        final String xpathQuery1 =
            "concat(substring-before($" + statefulServiceTemplateVarName + ",'" + serviceTemplateId.getLocalPart()
                + "'),encode-for-uri(encode-for-uri(//*[local-name()='QName']/*[local-name()='localname']/text())))";

        // query original service template url without the last path fragment(/service template localname)
        final String xpathQuery2 = "string($" + statefulServiceTemplateVarName + ")";
        Node assignCreatedStatefulServiceTemplate =
            this.bpelFragments.createAssignVarToVarWithXpathQueriesAsNode("assignCreatedStatefuleServiceTemplateUrl",
                                                                          responseVarName, null,
                                                                          statefulServiceTemplateVarName, null,
                                                                          xpathQuery1, xpathQuery2,
                                                                          "change the url from original service template to stateful",
                                                                          null);

        assignCreatedStatefulServiceTemplate =
            plan.getBpelDocument().importNode(assignCreatedStatefulServiceTemplate, true);
        plan.getBpelMainSequenceElement().insertBefore(assignCreatedStatefulServiceTemplate,
                                                       plan.getBpelMainSequencePropertyAssignElement());
    }

    private String findStatefulServiceTemplateUrlVar(final BPELPlan plan) {
        return this.planHandler.getMainVariableNames(plan).stream()
                               .filter(varName -> varName.contains("statefulServiceTemplateUrl")).findFirst()
                               .orElse(null);
    }

    private AbstractOperation getSaveStateOperation(final AbstractNodeTemplate nodeTemplate) {
        final AbstractInterface iface = getSaveStateInterface(nodeTemplate);
        if (iface != null) {
            for (final AbstractOperation op : iface.getOperations()) {
                if (!op.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE_FREEZE)) {
                    continue;
                }

                return op;
            }
        }
        return null;
    }

    private AbstractInterface getSaveStateInterface(final AbstractNodeTemplate nodeTemplate) {
        return nodeTemplate.getType().getInterfaces().stream()
                           .filter(iface -> iface.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE))
                           .findFirst().orElse(null);
    }

    private AbstractParameter getSaveStateParameter(final AbstractOperation op) {
        return op.getInputParameters().stream()
                 .filter(param -> param.getName()
                                       .equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE_FREEZE_MANDATORY_PARAM_ENDPOINT))
                 .findFirst().orElse(null);
    }
}
