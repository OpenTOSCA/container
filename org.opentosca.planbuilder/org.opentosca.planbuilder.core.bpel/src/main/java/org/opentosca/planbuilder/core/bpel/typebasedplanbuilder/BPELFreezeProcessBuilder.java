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
import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TParameter;
import org.eclipse.winery.model.tosca.TServiceTemplate;

import org.opentosca.container.core.convention.Interfaces;
import org.opentosca.container.core.model.ModelUtils;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.next.model.PlanType;
import org.opentosca.planbuilder.core.AbstractFreezePlanBuilder;
import org.opentosca.planbuilder.core.bpel.artifactbasednodehandler.BPELScopeBuilder;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.bpel.fragments.BPELProcessFragments;
import org.opentosca.planbuilder.core.bpel.handlers.BPELFinalizer;
import org.opentosca.planbuilder.core.bpel.handlers.BPELPlanHandler;
import org.opentosca.planbuilder.core.bpel.handlers.CorrelationIDInitializer;
import org.opentosca.planbuilder.core.bpel.handlers.NodeRelationInstanceVariablesHandler;
import org.opentosca.planbuilder.core.bpel.handlers.PropertyVariableHandler;
import org.opentosca.planbuilder.core.bpel.handlers.SimplePlanBuilderServiceInstanceHandler;
import org.opentosca.planbuilder.core.bpel.typebasednodehandler.BPELPluginHandler;
import org.opentosca.planbuilder.core.plugins.context.Property2VariableMapping;
import org.opentosca.planbuilder.core.plugins.context.Variable;
import org.opentosca.planbuilder.core.plugins.registry.PluginRegistry;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * @author Jan Ruthardt - st107755@stud.uni-stuttgart.de
 */
public class BPELFreezeProcessBuilder extends AbstractFreezePlanBuilder {

    private final static Logger LOG = LoggerFactory.getLogger(BPELFreezeProcessBuilder.class);
    // class for initializing properties inside the build plan
    private final PropertyVariableHandler propertyInitializer;
    // class for finalizing build plans (e.g when some template didn't receive
    // some provisioning logic and they must be filled with empty elements)
    private final BPELFinalizer finalizer;
    // handler for abstract buildplan operations
    public BPELPlanHandler planHandler;

    private BPELPluginHandler bpelPluginHandler;
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
    public BPELFreezeProcessBuilder(PluginRegistry pluginRegistry) {
        super(pluginRegistry);
        try {
            this.bpelPluginHandler = new BPELPluginHandler(pluginRegistry);
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
        LOG.debug("Creating Freeze Plan...");

        if (!this.isStateful(serviceTemplate, csar)) {
            LOG.warn("Couldn't create FreezePlan for ServiceTemplate {} in Definitions {} of CSAR {}",
                serviceTemplate.getId(), definitions.getId(), csar.id().csarName());
            return null;
        }

        final String processName = ModelUtils.makeValidNCName(serviceTemplate.getId() + "_freezePlan");
        final String processNamespace = serviceTemplate.getTargetNamespace() + "_freezePlan";

        // we take the overall flow of an termination plan, basically with the goal of
        // saving state from the top to the bottom
        final AbstractPlan newAbstractBackupPlan =
            generateFOG(new QName(processNamespace, processName).toString(), definitions, serviceTemplate, csar);

        newAbstractBackupPlan.setType(PlanType.TERMINATION);
        final BPELPlan newFreezePlan =
            this.planHandler.createEmptyBPELPlan(processNamespace, processName, newAbstractBackupPlan, "freeze");

        this.planHandler.initializeBPELSkeleton(newFreezePlan, csar);

        newFreezePlan.setTOSCAInterfaceName("OpenTOSCA-Stateful-Lifecycle-Interface");
        newFreezePlan.setTOSCAOperationname("freeze");

        this.instanceVarsHandler.addInstanceURLVarToTemplatePlans(newFreezePlan, serviceTemplate);
        this.instanceVarsHandler.addInstanceIDVarToTemplatePlans(newFreezePlan, serviceTemplate);

        final Property2VariableMapping propMap =
            this.propertyInitializer.initializePropertiesAsVariables(newFreezePlan, serviceTemplate);

        // instanceDataAPI handling is done solely trough this extension
        this.planHandler.registerExtension("http://www.apache.org/ode/bpel/extensions/bpel4restlight", true,
            newFreezePlan);

        // initialize instanceData handling, add
        // instanceDataAPI/serviceInstanceID into input, add global
        // variables to hold the value for plugins
        this.serviceInstanceVarsHandler.addServiceInstanceHandlingFromInput(newFreezePlan);
        final String serviceTemplateURLVarName =
            this.serviceInstanceVarsHandler.getServiceTemplateURLVariableName(newFreezePlan);
        this.serviceInstanceVarsHandler.appendInitPropertyVariablesFromServiceInstanceData(newFreezePlan, propMap,
            serviceTemplateURLVarName,
            serviceTemplate,
            "?state=STARTED&amp;state=CREATED&amp;state=CONFIGURED");

        // fetch all nodeinstances that are running
        this.instanceVarsHandler.addNodeInstanceFindLogic(newFreezePlan,
            "?state=STARTED&amp;state=CREATED&amp;state=CONFIGURED",
            serviceTemplate);
        this.instanceVarsHandler.addPropertyVariableUpdateBasedOnNodeInstanceID(newFreezePlan, propMap,
            serviceTemplate);

        try {
            appendGenerateStatefulServiceTemplateLogic(newFreezePlan);
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final SAXException e) {
            e.printStackTrace();
        }

        runPlugins(newFreezePlan, propMap, csar);

        final String serviceInstanceURLVarName =
            this.serviceInstanceVarsHandler.findServiceInstanceUrlVariableName(newFreezePlan);
        this.serviceInstanceVarsHandler.appendSetServiceInstanceState(newFreezePlan,
            newFreezePlan.getBpelMainSequenceOutputAssignElement(),
            "DELETED", serviceInstanceURLVarName);

        this.correlationHandler.addCorrellationID(newFreezePlan);

        this.serviceInstanceVarsHandler.appendSetServiceInstanceStateAsChild(newFreezePlan,
            this.planHandler.getMainCatchAllFaultHandlerSequenceElement(newFreezePlan),
            "ERROR", serviceInstanceURLVarName);
        this.serviceInstanceVarsHandler.appendSetServiceInstanceStateAsChild(newFreezePlan, this.planHandler.getMainCatchAllFaultHandlerSequenceElement(newFreezePlan), "FAILED", this.serviceInstanceVarsHandler.findPlanInstanceUrlVariableName(newFreezePlan));

        String planInstanceUrlVarName = this.serviceInstanceVarsHandler.findPlanInstanceUrlVariableName(newFreezePlan);
        this.serviceInstanceVarsHandler.appendSetServiceInstanceState(newFreezePlan,
            newFreezePlan.getBpelMainFlowElement(),
            "RUNNING", planInstanceUrlVarName);

        this.serviceInstanceVarsHandler.appendSetServiceInstanceState(newFreezePlan,
            newFreezePlan.getBpelMainSequenceOutputAssignElement(),
            "FINISHED", planInstanceUrlVarName);

        this.finalizer.finalize(newFreezePlan);

        LOG.debug("Created Plan:");
        LOG.debug(ModelUtils.getStringFromDoc(newFreezePlan.getBpelDocument()));

        return newFreezePlan;
    }

    @Override
    public List<AbstractPlan> buildPlans(final Csar csar, final TDefinitions definitions) {
        LOG.debug("Building the Freeze Plans");
        final List<AbstractPlan> plans = new ArrayList<>();
        for (final TServiceTemplate serviceTemplate : definitions.getServiceTemplates()) {
            if (!this.isStateful(serviceTemplate, csar)) {
                continue;
            }

            LOG.debug("ServiceTemplate {} has no Freeze Plan, generating Freeze Plan",
                serviceTemplate.getId());
            final BPELPlan newBuildPlan = buildPlan(csar, definitions, serviceTemplate);

            if (newBuildPlan != null) {
                LOG.debug("Created Freeze Plan " + newBuildPlan.getBpelProcessElement().getAttribute("name"));
                plans.add(newBuildPlan);
            }
        }
        if (!plans.isEmpty()) {
            LOG.info("Created {} freeze plan for CSAR {}", plans.size(), csar.id().csarName());
        }
        return plans;
    }

    private boolean isStateful(final TServiceTemplate serviceTemplate, Csar csar) {
        return serviceTemplate.getTopologyTemplate().getNodeTemplates().stream()
            .filter(node -> isStateful(node, csar)).findFirst().isPresent();
    }

    private boolean isStateful(final TNodeTemplate nodeTemplate, Csar csar) {
        return hasSaveStateInterface(nodeTemplate, csar) && hasStatefulComponentPolicy(nodeTemplate);
    }

    private boolean hasSaveStateInterface(final TNodeTemplate nodeTemplate, Csar csar) {
        final TOperation op = getSaveStateOperation(nodeTemplate, csar);
        return Objects.nonNull(op) && Objects.nonNull(getSaveStateParameter(op));
    }

    private TOperation getSaveStateOperation(final TNodeTemplate nodeTemplate, Csar csar) {
        final TInterface iface = ModelUtils.getInterfaceOfNode(nodeTemplate, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE, csar);
        if (iface != null) {
            for (final TOperation op : iface.getOperations()) {
                if (op.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE_FREEZE)) {
                    return op;
                }
            }
        }
        return null;
    }

    private TParameter getSaveStateParameter(final TOperation op) {
        return op.getInputParameters().stream()
            .filter(param -> param.getName()
                .equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE_FREEZE_MANDATORY_PARAM_ENDPOINT))
            .findFirst().orElse(null);
    }

    private String findStatefulServiceTemplateUrlVar(final BPELPlan plan) {
        return this.planHandler.getMainVariableNames(plan).stream()
            .filter(varName -> varName.contains("statefulServiceTemplateUrl")).findFirst()
            .orElse(null);
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

        // read response and assign url of created stateful service template
        // query the localname from the response
        final String xpathQuery1 =
            "concat(substring-before($" + statefulServiceTemplateVarName + ",'" + serviceTemplateId.getLocalPart()
                + "'),encode-for-uri(encode-for-uri(//*[local-name()='QName']/*[local-name()='localname']/text())))";
        // query original service template url without the last path fragment(/service
        // template localname)
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

    /**
     * This Methods Finds out if a Service Template Container a freeze method and then creats a freeze plan out of this
     * method
     *
     * @param plan    the plan to execute the plugins on*
     * @param propMap a PropertyMapping from NodeTemplate to Properties to BPELVariables
     * @param csar    the csar in the context
     */
    private List<BPELScope> runPlugins(final BPELPlan plan, final Property2VariableMapping propMap,
                                       final Csar csar) {

        final List<BPELScope> changedActivities = new ArrayList<>();

        final String statefulServiceTemplateUrlVarName = findStatefulServiceTemplateUrlVar(plan);

        final String serviceInstanceUrl = this.serviceInstanceVarsHandler.findServiceInstanceUrlVariableName(plan);
        final String serviceInstanceId = this.serviceInstanceVarsHandler.findServiceInstanceIdVarName(plan);
        final String serviceTemplateUrl = this.serviceInstanceVarsHandler.findServiceTemplateUrlVariableName(plan);
        final String planInstanceUrl = this.serviceInstanceVarsHandler.findPlanInstanceUrlVariableName(plan);

        for (final BPELScope templatePlan : plan.getTemplateBuildPlans()) {
            final BPELPlanContext context = new BPELPlanContext(new BPELScopeBuilder(pluginRegistry), plan, templatePlan, propMap, plan.getServiceTemplate(),
                serviceInstanceUrl, serviceInstanceId, serviceTemplateUrl, planInstanceUrl, csar);
            if (templatePlan.getNodeTemplate() != null) {

                // create a context for the node

                final TNodeTemplate nodeTemplate = templatePlan.getNodeTemplate();

                // TODO add termination logic

                /*
                 * generic save state code
                 */
                if (this.isStateful(nodeTemplate, csar)) {

                    final String saveStateUrlVarName =
                        this.planHandler.addGlobalStringVariable("nodeTemplateStateSaveURL", plan);

                    final String xpathQuery = "concat($" + statefulServiceTemplateUrlVarName
                        + ",'/topologytemplate/nodetemplates/" + nodeTemplate.getId() + "/state')";
                    try {
                        Node assignSaveStateURL =
                            this.bpelFragments.createAssignVarToVarWithXpathQueryAsNode("assignNodeTemplate"
                                    + nodeTemplate.getId() + "state" + System.currentTimeMillis(),
                                statefulServiceTemplateUrlVarName,
                                saveStateUrlVarName,
                                xpathQuery);
                        assignSaveStateURL = context.importNode(assignSaveStateURL);
                        context.getPrePhaseElement().appendChild(assignSaveStateURL);
                    } catch (final IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (final SAXException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    final Variable saveStateUrlVar = BPELPlanContext.getVariable(saveStateUrlVarName);

                    final Map<TParameter, Variable> inputs = new HashMap<>();

                    inputs.put(getSaveStateParameter(getSaveStateOperation(nodeTemplate, csar)), saveStateUrlVar);

                    boolean addedOperationCall = context.executeOperation(nodeTemplate, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE,
                        Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE_FREEZE, inputs);

                    if (!addedOperationCall) {
                        LOG.error("Couldn´t generate freeze operation call, maybe you miss an IA or Parameters?");
                    }
                }
                this.bpelPluginHandler.handleActivity(context, templatePlan, nodeTemplate);
            }
        }

        return changedActivities;
    }
}
