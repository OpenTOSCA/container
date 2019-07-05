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
import org.opentosca.planbuilder.AbstractFreezePlanBuilder;
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
import org.opentosca.planbuilder.plugins.typebased.IPlanBuilderPostPhasePlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Jan Ruthardt - st107755@stud.uni-stuttgart.de
 *
 */
public class BPELFreezeProcessBuilder extends AbstractFreezePlanBuilder {

    private final static Logger LOG = LoggerFactory.getLogger(BPELFreezeProcessBuilder.class);

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
    public BPELFreezeProcessBuilder() {
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
        LOG.info("Creating Freeze Plan...");


        if (!this.isStateful(serviceTemplate)) {
            LOG.warn("Couldn't create FreezePlan for ServiceTemplate {} in Definitions {} of CSAR {}",
                     serviceTemplate.getQName().toString(), definitions.getId(), csarName);
            return null;
        }

        final String processName = ModelUtils.makeValidNCName(serviceTemplate.getId() + "_freezePlan");
        final String processNamespace = serviceTemplate.getTargetNamespace() + "_freezePlan";

        // we take the overall flow of an termination plan, basically with the goal of
        // saving state from the top to the bottom
        final AbstractPlan newAbstractBackupPlan =
            generateFOG(new QName(processNamespace, processName).toString(), definitions, serviceTemplate);

        newAbstractBackupPlan.setType(PlanType.TERMINATE);
        final BPELPlan newFreezePlan =
            this.planHandler.createEmptyBPELPlan(processNamespace, processName, newAbstractBackupPlan, "freeze");

        this.planHandler.initializeBPELSkeleton(newFreezePlan, csarName);

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
                                                                                           serviceTemplate, "?state=STARTED&amp;state=CREATED&amp;state=CONFIGURED");

        // fetch all nodeinstances that are running
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

        final String serviceInstanceURLVarName =
            this.serviceInstanceVarsHandler.findServiceInstanceUrlVariableName(newFreezePlan);
        this.serviceInstanceVarsHandler.appendSetServiceInstanceState(newFreezePlan,
                                                                      newFreezePlan.getBpelMainSequenceOutputAssignElement(),
                                                                      "DELETED", serviceInstanceURLVarName);

        this.correlationHandler.addCorrellationID(newFreezePlan);

        this.finalizer.finalize(newFreezePlan);

        // add for each loop over found node instances to terminate each running
        // instance
        /*
         * for (final BPELScopeActivity activ : changedActivities) { if (activ.getNodeTemplate() != null) {
         * final BPELPlanContext context = new BPELPlanContext(activ, propMap,
         * newTerminationPlan.getServiceTemplate());
         * this.instanceVarsHandler.appendCountInstancesLogic(context, activ.getNodeTemplate(),
         * "?state=STARTED&amp;state=CREATED&amp;state=CONFIGURED"); } }
         */

        LOG.debug("Created Plan:");
        LOG.debug(ModelUtils.getStringFromDoc(newFreezePlan.getBpelDocument()));

        return newFreezePlan;
    }

    @Override
    public List<AbstractPlan> buildPlans(final String csarName, final AbstractDefinitions definitions) {
        LOG.info("Building the Freeze Plans");
        final List<AbstractPlan> plans = new ArrayList<>();
        for (final AbstractServiceTemplate serviceTemplate : definitions.getServiceTemplates()) {
            if (!this.isStateful(serviceTemplate)) {
                continue;
            }

            LOG.debug("ServiceTemplate {} has no Freeze Plan, generating Freeze Plan",
                      serviceTemplate.getQName().toString());
            final BPELPlan newBuildPlan = buildPlan(csarName, definitions, serviceTemplate);

            if (newBuildPlan != null) {
                LOG.debug("Created Freeze Plan " + newBuildPlan.getBpelProcessElement().getAttribute("name"));
                plans.add(newBuildPlan);
            }
        }
        return plans;
    }

    private boolean isStateful(final AbstractServiceTemplate serviceTemplate) {

        for (final AbstractNodeTemplate nodeTemplate : serviceTemplate.getTopologyTemplate().getNodeTemplates()) {
            if (this.isStateful(nodeTemplate)) {
                return true;
            }
        }

        return false;
    }

    private boolean isStateful(final AbstractNodeTemplate nodeTemplate) {
        return hasSaveStateInterface(nodeTemplate) && hasStatefulComponentPolicy(nodeTemplate);
    }

    private boolean hasSaveStateInterface(final AbstractNodeTemplate nodeTemplate) {
        final AbstractOperation op = getSaveStateOperation(nodeTemplate);
        return Objects.nonNull(op) && Objects.nonNull(getSaveStateParameter(op));
    }

    private AbstractInterface getSaveStateInterface(final AbstractNodeTemplate nodeTemplate) {
        return nodeTemplate.getType().getInterfaces().stream()
                           .filter(iface -> iface.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE))
                           .findFirst().orElse(null);
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

    private AbstractParameter getSaveStateParameter(final AbstractOperation op) {
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
     * This Methods Finds out if a Service Template Container a freeze method and then creats a freeze
     * plan out of this method
     *
     * @param plan the plan to execute the plugins on
     * @param serviceTemplate the serviceTemplate the plan belongs to
     * @param propMap a PropertyMapping from NodeTemplate to Properties to BPELVariables
     */
    private List<BPELScope> runPlugins(final BPELPlan plan, final Property2VariableMapping propMap,
                                       final String csarName) {

        final List<BPELScope> changedActivities = new ArrayList<>();

        final String statefulServiceTemplateUrlVarName = findStatefulServiceTemplateUrlVar(plan);

        final String serviceInstanceUrl = this.serviceInstanceVarsHandler.findServiceInstanceUrlVariableName(plan);
        final String serviceInstanceId = this.serviceInstanceVarsHandler.findServiceInstanceIdVarName(plan);
        final String serviceTemplateUrl = this.serviceInstanceVarsHandler.findServiceTemplateUrlVariableName(plan);


        for (final BPELScope templatePlan : plan.getTemplateBuildPlans()) {
            final BPELPlanContext context = new BPELPlanContext(plan, templatePlan, propMap, plan.getServiceTemplate(),
                serviceInstanceUrl, serviceInstanceId, serviceTemplateUrl, csarName);
            if (templatePlan.getNodeTemplate() != null) {

                // create a context for the node

                final AbstractNodeTemplate nodeTemplate = templatePlan.getNodeTemplate();

                // TODO add termination logic

                /*
                 * generic save state code
                 */
                if (this.isStateful(nodeTemplate)) {

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

                if (org.opentosca.container.core.tosca.convention.Utils.isSupportedVMNodeType(templatePlan.getNodeTemplate()
                                                                                                          .getType()
                                                                                                          .getId())) {

                    // fetch infrastructure node (cloud provider)
                    final List<AbstractNodeTemplate> infraNodes = context.getInfrastructureNodes();
                    for (final AbstractNodeTemplate infraNode : infraNodes) {
                        if (org.opentosca.container.core.tosca.convention.Utils.isSupportedCloudProviderNodeType(infraNode.getType()
                                                                                                                          .getId())) {
                            // append logic to call terminateVM method on the node

                            context.executeOperation(infraNode,
                                                     org.opentosca.container.core.tosca.convention.Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_CLOUDPROVIDER,
                                                     org.opentosca.container.core.tosca.convention.Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_CLOUDPROVIDER_TERMINATEVM,
                                                     null);

                            changedActivities.add(templatePlan);
                        }
                    }

                } else {

                    if (!isDockerContainer(context.getNodeTemplate())) {
                        continue;
                    }

                    // fetch infrastructure node (cloud provider)
                    final List<AbstractNodeTemplate> nodes = new ArrayList<>();
                    ModelUtils.getNodesFromNodeToSink(context.getNodeTemplate(), nodes);

                    for (final AbstractNodeTemplate node : nodes) {
                        if (org.opentosca.container.core.tosca.convention.Utils.isSupportedDockerEngineNodeType(node.getType()
                                                                                                                    .getId())) {
                            context.executeOperation(node, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE,
                                                     Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE_REMOVECONTAINER,
                                                     null);
                            changedActivities.add(templatePlan);
                        }
                    }

                }

                for (final IPlanBuilderPostPhasePlugin postPhasePlugin : this.pluginRegistry.getPostPlugins()) {
                    if (postPhasePlugin.canHandleCreate(nodeTemplate)) {
                        postPhasePlugin.handleCreate(context, nodeTemplate);
                    }
                }
            }
        }

        return changedActivities;

    }

    private boolean isDockerContainer(final AbstractNodeTemplate nodeTemplate) {
        if (nodeTemplate.getProperties() == null) {
            return false;
        }
        final Element propertyElement = nodeTemplate.getProperties().getDOMElement();
        final NodeList childNodeList = propertyElement.getChildNodes();

        int check = 0;
        boolean foundDockerImageProp = false;
        for (int index = 0; index < childNodeList.getLength(); index++) {
            if (childNodeList.item(index).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if (childNodeList.item(index).getLocalName().equals("ContainerPort")) {
                check++;
            } else if (childNodeList.item(index).getLocalName().equals("Port")) {
                check++;
            } else if (childNodeList.item(index).getLocalName().equals("ImageID")) {
                foundDockerImageProp = true;
            }
        }

        if (check != 2) {
            return false;
        }
        return true;
    }
}
