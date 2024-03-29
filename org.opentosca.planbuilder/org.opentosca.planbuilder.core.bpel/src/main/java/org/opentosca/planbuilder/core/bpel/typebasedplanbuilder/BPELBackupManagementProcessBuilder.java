package org.opentosca.planbuilder.core.bpel.typebasedplanbuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TParameter;
import org.eclipse.winery.model.tosca.TServiceTemplate;

import org.opentosca.container.core.convention.Interfaces;
import org.opentosca.container.core.model.ModelUtils;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.planbuilder.core.AbstractManagementFeaturePlanBuilder;
import org.opentosca.planbuilder.core.bpel.artifactbasednodehandler.BPELScopeBuilder;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.bpel.fragments.BPELProcessFragments;
import org.opentosca.planbuilder.core.bpel.handlers.BPELFinalizer;
import org.opentosca.planbuilder.core.bpel.handlers.BPELPlanHandler;
import org.opentosca.planbuilder.core.bpel.handlers.CorrelationIDInitializer;
import org.opentosca.planbuilder.core.bpel.handlers.NodeRelationInstanceVariablesHandler;
import org.opentosca.planbuilder.core.bpel.handlers.PropertyVariableHandler;
import org.opentosca.planbuilder.core.bpel.handlers.SimplePlanBuilderServiceInstanceHandler;
import org.opentosca.planbuilder.core.plugins.context.Property2VariableMapping;
import org.opentosca.planbuilder.core.plugins.context.Variable;
import org.opentosca.planbuilder.core.plugins.registry.PluginRegistry;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.ActivityType;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import static org.opentosca.container.core.convention.PlanConstants.OpenTOSCA_BackupPlanOperation;
import static org.opentosca.container.core.convention.PlanConstants.OpenTOSCA_ManagementFeatureInterface;

/**
 * <p>
 * This process builder creates a backup management plan if one of the NodeTemplates in the topology is of a type that
 * defines the freeze interface.
 * </p>
 * <p>
 * Copyright 2019 IAAS University of Stuttgart <br>
 * <br>
 */
public class BPELBackupManagementProcessBuilder extends AbstractManagementFeaturePlanBuilder {

    private final static Logger LOG = LoggerFactory.getLogger(BPELBackupManagementProcessBuilder.class);
    // class for initializing properties inside the build plan
    private final PropertyVariableHandler propertyInitializer;
    // class for finalizing build plans (e.g., when some template didn't receive
    // some provisioning logic and they must be filled with empty elements)
    private final BPELFinalizer finalizer;
    // handler for abstract buildPlan operations
    public BPELPlanHandler planHandler;
    // adds serviceInstance Variable and instanceDataAPIUrl to buildPlans
    private SimplePlanBuilderServiceInstanceHandler serviceInstanceVarsHandler;
    // adds nodeInstanceIDs to each templatePlan
    private NodeRelationInstanceVariablesHandler instanceVarsHandler;
    private BPELProcessFragments bpelFragments;

    private CorrelationIDInitializer correlationHandler;

    /**
     * <p>
     * Default Constructor
     * </p>
     */
    public BPELBackupManagementProcessBuilder(PluginRegistry pluginRegistry) {
        super(pluginRegistry);
        try {
            this.planHandler = new BPELPlanHandler();
            this.serviceInstanceVarsHandler = new SimplePlanBuilderServiceInstanceHandler();
            this.instanceVarsHandler = new NodeRelationInstanceVariablesHandler(this.planHandler);
            this.bpelFragments = new BPELProcessFragments();
            this.correlationHandler = new CorrelationIDInitializer();
        } catch (final ParserConfigurationException e) {
            LOG.error("Error while initializing BuildPlanHandler", e);
        }
        this.propertyInitializer = new PropertyVariableHandler(this.planHandler);
        this.finalizer = new BPELFinalizer();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opentosca.planbuilder.IPlanBuilder#buildPlan(java.lang.String,
     * org.opentosca.planbuilder.model.tosca.TDefinitions, javax.xml.namespace.QName)
     */
    private BPELPlan buildPlan(final Csar csar, final TDefinitions definitions,
                               final TServiceTemplate serviceTemplate) {
        LOG.debug("Creating Backup Management Plan...");

        if (Objects.isNull(serviceTemplate)) {
            LOG.error("Unable to generate Backup Plan with ServiceTempolate equal to null.");
            return null;
        }

        final String processName = ModelUtils.makeValidNCName(serviceTemplate.getId() + "_backupManagementPlan");
        final String processNamespace = serviceTemplate.getTargetNamespace() + "_backupManagementPlan";

        final AbstractPlan abstractBackupPlan =
            generateMOG(new QName(processNamespace, processName).toString(), definitions, serviceTemplate,
                Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE, ActivityType.BACKUP, true, csar);

        LOG.debug("Generated the following abstract backup plan: ");
        LOG.debug(abstractBackupPlan.toString());

        abstractBackupPlan.setType(PlanType.MANAGEMENT);
        final BPELPlan newBackupPlan =
            this.planHandler.createEmptyBPELPlan(processNamespace, processName, abstractBackupPlan, OpenTOSCA_BackupPlanOperation);

        this.planHandler.initializeBPELSkeleton(newBackupPlan, csar);

        newBackupPlan.setTOSCAInterfaceName(OpenTOSCA_ManagementFeatureInterface);
        newBackupPlan.setTOSCAOperationname(OpenTOSCA_BackupPlanOperation);

        this.instanceVarsHandler.addInstanceURLVarToTemplatePlans(newBackupPlan, serviceTemplate);
        this.instanceVarsHandler.addInstanceIDVarToTemplatePlans(newBackupPlan, serviceTemplate);

        final Property2VariableMapping propMap =
            this.propertyInitializer.initializePropertiesAsVariables(newBackupPlan, serviceTemplate);

        // initialize instanceData handling
        this.planHandler.registerExtension("http://www.apache.org/ode/bpel/extensions/bpel4restlight", true,
            newBackupPlan);
        this.serviceInstanceVarsHandler.addServiceInstanceHandlingFromInput(newBackupPlan);

        final String serviceTemplateURLVarName =
            this.serviceInstanceVarsHandler.getServiceTemplateURLVariableName(newBackupPlan);

        this.serviceInstanceVarsHandler.appendInitPropertyVariablesFromServiceInstanceData(newBackupPlan, propMap,
            serviceTemplateURLVarName,
            serviceTemplate, null);

        // fetch all node instances that are running
        this.instanceVarsHandler.addNodeInstanceFindLogic(newBackupPlan,
            "?state=STARTED&amp;state=CREATED&amp;state=CONFIGURED",
            serviceTemplate);
        this.instanceVarsHandler.addPropertyVariableUpdateBasedOnNodeInstanceID(newBackupPlan, propMap,
            serviceTemplate);

        try {
            appendGenerateStatefulServiceTemplateLogic(newBackupPlan);
        } catch (final IOException | SAXException e) {
            LOG.error("Error while generating stateful ServiceTemplate logic...", e);
        }

        runPlugins(newBackupPlan, propMap, csar);

        this.correlationHandler.addCorrellationID(newBackupPlan);

        this.serviceInstanceVarsHandler.appendSetServiceInstanceStateAsChild(newBackupPlan,
            this.planHandler.getMainCatchAllFaultHandlerSequenceElement(newBackupPlan),
            "ERROR",
            this.serviceInstanceVarsHandler.findServiceInstanceUrlVariableName(newBackupPlan));
        this.serviceInstanceVarsHandler.appendSetServiceInstanceStateAsChild(newBackupPlan,
            this.planHandler.getMainCatchAllFaultHandlerSequenceElement(newBackupPlan),
            "FAILED",
            this.serviceInstanceVarsHandler.findPlanInstanceUrlVariableName(newBackupPlan));

        String planInstanceUrlVarName = this.serviceInstanceVarsHandler.findPlanInstanceUrlVariableName(newBackupPlan);
        this.serviceInstanceVarsHandler.appendSetServiceInstanceState(newBackupPlan,
            newBackupPlan.getBpelMainFlowElement(),
            "RUNNING", planInstanceUrlVarName);

        this.serviceInstanceVarsHandler.appendSetServiceInstanceState(newBackupPlan,
            newBackupPlan.getBpelMainSequenceCallbackInvokeElement(),
            "FINISHED", planInstanceUrlVarName);

        this.finalizer.finalize(newBackupPlan);

        LOG.debug("Created Plan:");
        LOG.debug(ModelUtils.getStringFromDoc(newBackupPlan.getBpelDocument()));

        return newBackupPlan;
    }

    private void runPlugins(final BPELPlan plan, final Property2VariableMapping propMap, final Csar csar) {

        final String statefulServiceTemplateUrlVarName = findStatefulServiceTemplateUrlVar(plan);

        final String serviceInstanceUrl = this.serviceInstanceVarsHandler.findServiceInstanceUrlVariableName(plan);
        final String serviceInstanceId = this.serviceInstanceVarsHandler.findServiceInstanceIdVarName(plan);
        final String serviceTemplateUrl = this.serviceInstanceVarsHandler.findServiceTemplateUrlVariableName(plan);
        final String planInstanceUrl = this.serviceInstanceVarsHandler.findPlanInstanceUrlVariableName(plan);

        for (final BPELScope templatePlan : plan.getTemplateBuildPlans()) {
            final BPELPlanContext context = new BPELPlanContext(new BPELScopeBuilder(pluginRegistry), plan, templatePlan, propMap, plan.getServiceTemplate(),
                serviceInstanceUrl, serviceInstanceId, serviceTemplateUrl, planInstanceUrl, csar);

            // only handle NodeTemplates of type with save state interface
            final TNodeTemplate nodeTemplate = templatePlan.getNodeTemplate();
            if (Objects.nonNull(nodeTemplate)
                && Objects.nonNull(ModelUtils.getInterfaceOfNode(nodeTemplate,
                Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE, csar))) {
                LOG.debug("Adding backup logic for NodeTemplate {}", nodeTemplate.getName());

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
                } catch (final IOException | SAXException e) {
                    LOG.error("");
                }

                final TOperation freezeOp =
                    ModelUtils.getOperationOfNode(nodeTemplate, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE,
                        Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE_FREEZE, csar);
                if (Objects.nonNull(freezeOp)) {
                    final Variable saveStateUrlVar = BPELPlanContext.getVariable(saveStateUrlVarName);

                    final Map<TParameter, Variable> inputs = new HashMap<>();

                    // retrieve input parameters from all nodes which are downwards in the same topology stack
                    final List<TNodeTemplate> nodesForMatching = new ArrayList<>();
                    ModelUtils.getNodesFromNodeToSink(nodeTemplate, nodesForMatching, csar);

                    LOG.debug("Backup on NodeTemplate {} needs the following input parameters:",
                        nodeTemplate.getName());
                    for (final TParameter param : freezeOp.getInputParameters()) {
                        LOG.debug("Input param: {}", param.getName());
                        found:
                        for (final TNodeTemplate nodeForMatching : nodesForMatching) {
                            for (final String propName : ModelUtils.getPropertyNames(nodeForMatching)) {
                                if (param.getName().equals(propName)) {
                                    inputs.put(param, context.getPropertyVariable(nodeForMatching, propName));
                                    break found;
                                }
                            }
                        }
                    }

                    // add special parameter with winery URL
                    inputs.put(getSaveStateParameter(freezeOp), saveStateUrlVar);

                    LOG.debug("Found {} of {} input parameters.", inputs.size(), freezeOp.getInputParameters().size());

                    context.executeOperation(nodeTemplate, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE,
                        Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE_FREEZE, inputs);
                }
            }
        }
    }

    @Override
    public List<AbstractPlan> buildPlans(final Csar csar, final TDefinitions definitions) {
        LOG.debug("Building the Backup Management Plans");
        final List<AbstractPlan> plans = new ArrayList<>();
        for (final TServiceTemplate serviceTemplate : definitions.getServiceTemplates()) {

            if (containsManagementInterface(serviceTemplate, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE, csar)) {
                LOG.debug("ServiceTemplate {} contains NodeTypes with defined backup interface.",
                    serviceTemplate.getName());
                final BPELPlan newBuildPlan = buildPlan(csar, definitions, serviceTemplate);
                if (Objects.nonNull(newBuildPlan)) {
                    LOG.debug("Created Backup Management Plan "
                        + newBuildPlan.getBpelProcessElement().getAttribute("name"));
                    plans.add(newBuildPlan);
                }
            } else {
                LOG.debug("No backup interface defined in ServiceTemplate {}", serviceTemplate.getName());
            }
        }

        if (!plans.isEmpty()) {
            LOG.info("Created {} backup plans for CSAR {}", plans.size(), csar.id().csarName());
        }
        return plans;
    }

    private void appendGenerateStatefulServiceTemplateLogic(final BPELPlan plan) throws IOException, SAXException {
        final QName serviceTemplateId = new QName(plan.getServiceTemplate().getTargetNamespace(), plan.getServiceTemplate().getId());

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
                        StandardCharsets.UTF_8),
                    StandardCharsets.UTF_8)
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

    private TParameter getSaveStateParameter(final TOperation op) {
        return op.getInputParameters().stream()
            .filter(param -> param.getName()
                .equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE_FREEZE_MANDATORY_PARAM_ENDPOINT))
            .findFirst().orElse(null);
    }
}
