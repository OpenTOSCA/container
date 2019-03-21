package org.opentosca.planbuilder.core.bpel;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.container.core.tosca.convention.Interfaces;
import org.opentosca.container.core.tosca.extension.PlanTypes;
import org.opentosca.planbuilder.AbstractDefrostPlanBuilder;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.bpel.fragments.BPELProcessFragments;
import org.opentosca.planbuilder.core.bpel.handlers.BPELPlanHandler;
import org.opentosca.planbuilder.core.bpel.helpers.BPELFinalizer;
import org.opentosca.planbuilder.core.bpel.helpers.EmptyPropertyToInputInitializer;
import org.opentosca.planbuilder.core.bpel.helpers.NodeRelationInstanceVariablesHandler;
import org.opentosca.planbuilder.core.bpel.helpers.PropertyVariableInitializer;
import org.opentosca.planbuilder.core.bpel.helpers.PropertyVariableInitializer.PropertyMap;
import org.opentosca.planbuilder.core.bpel.helpers.ServiceInstanceVariablesHandler;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.AbstractPlan.PlanType;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELScopeActivity;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractImplementationArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractInterface;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTypeImplementation;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.planbuilder.model.tosca.AbstractParameter;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.opentosca.planbuilder.plugins.IPlanBuilderPostPhasePlugin;
import org.opentosca.planbuilder.plugins.IPlanBuilderTypePlugin;
import org.opentosca.planbuilder.plugins.context.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class BPELDefrostProcessBuilder extends AbstractDefrostPlanBuilder {

    private final static Logger LOG = LoggerFactory.getLogger(BPELDefrostProcessBuilder.class);

    // handler for abstract buildplan operations
    private BPELPlanHandler planHandler;

    // class for initializing properties inside the build plan
    private final PropertyVariableInitializer propertyInitializer;
    // adds serviceInstance Variable and instanceDataAPIUrl to buildPlans
    private ServiceInstanceVariablesHandler serviceInstanceInitializer;
    // adds nodeInstanceIDs to each templatePlan
    private NodeRelationInstanceVariablesHandler instanceVarsHandler;
    // class for finalizing build plans (e.g when some template didn't receive
    // some provisioning logic and they must be filled with empty elements)
    private final BPELFinalizer finalizer;

    private QName statefulComponentPolicy = new QName("http://opentosca.org/policytypes", "StatefulComponent");
    private final EmptyPropertyToInputInitializer emptyPropInit = new EmptyPropertyToInputInitializer();

    // accepted operations for provisioning
    private final List<String> provisioningOpNames = new ArrayList<>();
    private final List<String> defrostOpNames = new ArrayList<>();

    private BPELProcessFragments bpelFragments;


    public BPELDefrostProcessBuilder() {
        try {
            this.planHandler = new BPELPlanHandler();
            this.serviceInstanceInitializer = new ServiceInstanceVariablesHandler();
            this.instanceVarsHandler = new NodeRelationInstanceVariablesHandler(this.planHandler);
            this.bpelFragments = new BPELProcessFragments();
        }
        catch (final ParserConfigurationException e) {
            BPELDefrostProcessBuilder.LOG.error("Error while initializing BuildPlanHandler", e);
        }
        this.propertyInitializer = new PropertyVariableInitializer(this.planHandler);
        this.finalizer = new BPELFinalizer();

        this.provisioningOpNames.add("install");
        this.provisioningOpNames.add("configure");
        this.provisioningOpNames.add("start");

        this.defrostOpNames.add(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE_DEFREEZE);
    }

    @Override
    public BPELPlan buildPlan(final String csarName, final AbstractDefinitions definitions,
                              final QName serviceTemplateId) {
        BPELDefrostProcessBuilder.LOG.info("Making Concrete Plans");

        for (final AbstractServiceTemplate serviceTemplate : definitions.getServiceTemplates()) {
            String namespace;
            if (serviceTemplate.getTargetNamespace() != null) {
                namespace = serviceTemplate.getTargetNamespace();
            } else {
                namespace = definitions.getTargetNamespace();
            }

            final String processName = ModelUtils.makeValidNCName(serviceTemplate.getId() + "_defrostPlan");
            final String processNamespace = serviceTemplate.getTargetNamespace() + "_defrostPlan";

            final AbstractPlan newAbstractBackupPlan =
                generateDOG(new QName(processNamespace, processName).toString(), definitions, serviceTemplate);

            final BPELPlan newDefreezePlan =
                this.planHandler.createEmptyBPELPlan(processNamespace, processName, newAbstractBackupPlan, "defrost");

            newDefreezePlan.setTOSCAInterfaceName("OpenTOSCA-Stateful-Lifecycle-Interface");
            newDefreezePlan.setTOSCAOperationname("defrost");
            newDefreezePlan.setType(PlanType.BUILD);

            this.planHandler.initializeBPELSkeleton(newDefreezePlan, csarName);

            this.instanceVarsHandler.addInstanceURLVarToTemplatePlans(newDefreezePlan);
            this.instanceVarsHandler.addInstanceIDVarToTemplatePlans(newDefreezePlan);

            final PropertyMap propMap = this.propertyInitializer.initializePropertiesAsVariables(newDefreezePlan);

            // instanceDataAPI handling is done solely trough this extension
            this.planHandler.registerExtension("http://www.apache.org/ode/bpel/extensions/bpel4restlight", true,
                                               newDefreezePlan);

            try {
                this.appendLoadStatefulServiceTemplateLogic(newDefreezePlan);
            }
            catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (SAXException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // initialize instanceData handling
            this.serviceInstanceInitializer.initializeInstanceDataFromInput(newDefreezePlan);

            this.emptyPropInit.initializeEmptyPropertiesAsInputParam(newDefreezePlan, propMap);

            final List<BPELScopeActivity> changedActivities = runPlugins(newDefreezePlan, propMap);


            this.serviceInstanceInitializer.addCorrellationID(newDefreezePlan);

            this.serviceInstanceInitializer.appendSetServiceInstanceState(newDefreezePlan,
                                                                          newDefreezePlan.getBpelMainFlowElement(),
                                                                          "CREATING");
            this.serviceInstanceInitializer.appendSetServiceInstanceState(newDefreezePlan,
                                                                          newDefreezePlan.getBpelMainSequenceOutputAssignElement(),
                                                                          "CREATED");
            this.finalizer.finalize(newDefreezePlan);


            BPELDefrostProcessBuilder.LOG.debug("Created Plan:");
            BPELDefrostProcessBuilder.LOG.debug(ModelUtils.getStringFromDoc(newDefreezePlan.getBpelDocument()));

            return newDefreezePlan;
        }

        BPELDefrostProcessBuilder.LOG.warn("Couldn't create DeFreezePlan for ServiceTemplate {} in Definitions {} of CSAR {}",
                                           serviceTemplateId.toString(), definitions.getId(), csarName);
        return null;
    }

    private void appendLoadStatefulServiceTemplateLogic(BPELPlan plan) throws UnsupportedEncodingException, IOException,
                                                                       SAXException {
        this.planHandler.addStringElementToPlanRequest(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE_FREEZE_MANDATORY_PARAM_ENDPOINT,
                                                       plan);


        // var to save serviceTemplate url on storage service
        String statefulServiceTemplateVarName =
            this.planHandler.addGlobalStringVariable("statefulServiceTemplateUrl" + System.currentTimeMillis(), plan);


        // assign variable with the original service template url
        Node assignStatefuleServiceTemplateStorageVar =
            this.bpelFragments.createAssignVarToVarWithXpathQueryAsNode("assignServiceTemplateStorageUrl"
                + System.currentTimeMillis(), "input", statefulServiceTemplateVarName,
                                                                        "string(//*[local-name()='"
                                                                            + Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE_FREEZE_MANDATORY_PARAM_ENDPOINT
                                                                            + "']/text())");
        assignStatefuleServiceTemplateStorageVar =
            plan.getBpelDocument().importNode(assignStatefuleServiceTemplateStorageVar, true);
        plan.getBpelMainSequenceElement().insertBefore(assignStatefuleServiceTemplateStorageVar,
                                                       plan.getBpelMainSequencePropertyAssignElement());

        // not sure if we need more right now
    }

    private boolean isDefrostable(AbstractNodeTemplate nodeTemplate) {
        return Objects.nonNull(this.getLoadStateOperation(nodeTemplate))
            && this.hasFreezeableComponentPolicy(nodeTemplate);
    }

    private AbstractInterface getLoadStateInterface(AbstractNodeTemplate nodeTemplate) {
        for (AbstractInterface iface : nodeTemplate.getType().getInterfaces()) {
            if (!iface.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE)) {
                continue;
            }

            return iface;
        }
        return null;
    }

    private AbstractOperation getLoadStateOperation(AbstractNodeTemplate nodeTemplate) {
        AbstractInterface iface = this.getLoadStateInterface(nodeTemplate);
        if (iface != null) {
            for (AbstractOperation op : iface.getOperations()) {
                if (!op.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE_DEFREEZE)) {
                    continue;
                }

                return op;
            }
        }
        return null;
    }

    private String findStatefulServiceTemplateUrlVar(BPELPlan plan) {
        for (String varName : this.planHandler.getMainVariableNames(plan)) {
            if (varName.contains("statefulServiceTemplateUrl")) {
                return varName;
            }
        }

        return null;
    }

    /**
     * This Methods Finds out if a Service Template Container a freeze method and then creats a freeze
     * plan out of this method
     *
     * @param plan the plan to execute the plugins on
     * @param serviceTemplate the serviceTemplate the plan belongs to
     * @param propMap a PropertyMapping from NodeTemplate to Properties to BPELVariables
     */
    private List<BPELScopeActivity> runPlugins(final BPELPlan plan, final PropertyMap propMap) {

        final List<BPELScopeActivity> changedActivities = new ArrayList<>();

        String statefulServiceTemplateUrlVarName = this.findStatefulServiceTemplateUrlVar(plan);

        for (final BPELScopeActivity templatePlan : plan.getTemplateBuildPlans()) {
            final BPELPlanContext context = new BPELPlanContext(templatePlan, propMap, plan.getServiceTemplate());

            if (templatePlan.getNodeTemplate() != null) {
                // create a context for the node

                AbstractNodeTemplate nodeTemplate = templatePlan.getNodeTemplate();
                boolean alreadyHandled = false;

                List<String> operationNames = this.provisioningOpNames;

                if (this.isDefrostable(nodeTemplate)) {
                    operationNames = this.defrostOpNames;
                }


                if (isRunning(context, templatePlan.getNodeTemplate())) {
                    BPELBuildProcessBuilder.LOG.debug("Skipping the provisioning of NodeTemplate "
                        + templatePlan.getNodeTemplate().getId() + "  beacuse state=running is set.");
                    for (final IPlanBuilderPostPhasePlugin postPhasePlugin : this.pluginRegistry.getPostPlugins()) {
                        if (postPhasePlugin.canHandle(templatePlan.getNodeTemplate())) {
                            postPhasePlugin.handle(context, templatePlan.getNodeTemplate());
                        }
                    }
                    continue;
                }

                BPELBuildProcessBuilder.LOG.debug("Trying to handle NodeTemplate " + nodeTemplate.getId());
                // check if we have a generic plugin to handle the template
                // Note: if a generic plugin fails during execution the
                // TemplateBuildPlan is broken!
                final IPlanBuilderTypePlugin plugin = this.findTypePlugin(nodeTemplate);
                if (plugin == null) {
                    BPELBuildProcessBuilder.LOG.debug("Handling NodeTemplate {} with ProvisioningChain",
                                                      nodeTemplate.getId());
                    final OperationChain chain = BPELScopeBuilder.createOperationChain(nodeTemplate, operationNames);
                    if (chain == null) {
                        BPELBuildProcessBuilder.LOG.warn("Couldn't create ProvisioningChain for NodeTemplate {}",
                                                         nodeTemplate.getId());
                    } else {
                        BPELBuildProcessBuilder.LOG.debug("Created ProvisioningChain for NodeTemplate {}",
                                                          nodeTemplate.getId());
                        chain.executeIAProvisioning(context);
                        chain.executeDAProvisioning(context);
                        chain.executeOperationProvisioning(context, operationNames);
                    }
                } else {
                    BPELBuildProcessBuilder.LOG.info("Handling NodeTemplate {} with generic plugin",
                                                     nodeTemplate.getId());
                    plugin.handle(context);
                }

                for (final IPlanBuilderPostPhasePlugin postPhasePlugin : this.pluginRegistry.getPostPlugins()) {
                    if (postPhasePlugin.canHandle(templatePlan.getNodeTemplate())) {
                        postPhasePlugin.handle(context, templatePlan.getNodeTemplate());
                    }
                }

            } else if (templatePlan.getRelationshipTemplate() != null) {
                // handling relationshiptemplate
                final AbstractRelationshipTemplate relationshipTemplate = templatePlan.getRelationshipTemplate();

                // check if we have a generic plugin to handle the template
                // Note: if a generic plugin fails during execution the
                // TemplateBuildPlan is broken here!
                // TODO implement fallback
                if (!canGenericPluginHandle(relationshipTemplate)) {
                    BPELBuildProcessBuilder.LOG.debug("Handling RelationshipTemplate {} with ProvisioningChains",
                                                      relationshipTemplate.getId());
                    final OperationChain sourceChain =
                        BPELScopeBuilder.createOperationChain(relationshipTemplate, true);
                    final OperationChain targetChain =
                        BPELScopeBuilder.createOperationChain(relationshipTemplate, false);

                    // first execute provisioning on target, then on source
                    if (targetChain != null) {
                        BPELBuildProcessBuilder.LOG.warn("Couldn't create ProvisioningChain for TargetInterface of RelationshipTemplate {}",
                                                         relationshipTemplate.getId());
                        targetChain.executeIAProvisioning(context);
                        targetChain.executeOperationProvisioning(context, this.provisioningOpNames);
                    }

                    if (sourceChain != null) {
                        BPELBuildProcessBuilder.LOG.warn("Couldn't create ProvisioningChain for SourceInterface of RelationshipTemplate {}",
                                                         relationshipTemplate.getId());
                        sourceChain.executeIAProvisioning(context);
                        sourceChain.executeOperationProvisioning(context, this.provisioningOpNames);
                    }
                } else {
                    BPELBuildProcessBuilder.LOG.info("Handling RelationshipTemplate {} with generic plugin",
                                                     relationshipTemplate.getId());
                    handleWithTypePlugin(context, relationshipTemplate);
                }

                for (final IPlanBuilderPostPhasePlugin postPhasePlugin : this.pluginRegistry.getPostPlugins()) {
                    if (postPhasePlugin.canHandle(templatePlan.getRelationshipTemplate())) {
                        postPhasePlugin.handle(context, templatePlan.getRelationshipTemplate());
                    }
                }
            }

        }
        return changedActivities;
    }

    private static boolean containsString(String s, String subString) {
        return s.indexOf(subString) > -1 ? true : false;
    }

    private boolean isRunning(final BPELPlanContext context, final AbstractNodeTemplate nodeTemplate) {
        final Variable state = context.getPropertyVariable(nodeTemplate, "State");
        if (state != null) {
            if (BPELPlanContext.getVariableContent(state, context).equals("Running")) {
                return true;
            }
        }
        return false;
    }

    /**
     * <p>
     * Checks whether there is any generic plugin, that can handle the given RelationshipTemplate
     * </p>
     *
     * @param relationshipTemplate an AbstractRelationshipTemplate denoting a RelationshipTemplate
     * @return true if there is any generic plugin which can handle the given RelationshipTemplate, else
     *         false
     */
    private boolean canGenericPluginHandle(final AbstractRelationshipTemplate relationshipTemplate) {
        for (final IPlanBuilderTypePlugin plugin : this.pluginRegistry.getGenericPlugins()) {
            if (plugin.canHandle(relationshipTemplate)) {
                BPELBuildProcessBuilder.LOG.info("Found GenericPlugin {} thath can handle RelationshipTemplate {}",
                                                 plugin.getID(), relationshipTemplate.getId());
                return true;
            }
        }
        return false;
    }

    @Override
    public List<AbstractPlan> buildPlans(final String csarName, final AbstractDefinitions definitions) {
        BPELDefrostProcessBuilder.LOG.info("Builing the Plans");
        final List<AbstractPlan> plans = new ArrayList<>();
        for (final AbstractServiceTemplate serviceTemplate : definitions.getServiceTemplates()) {
            QName serviceTemplateId;
            // targetNamespace attribute doesn't has to be set, so we check it
            if (serviceTemplate.getTargetNamespace() != null) {
                serviceTemplateId = new QName(serviceTemplate.getTargetNamespace(), serviceTemplate.getId());
            } else {
                serviceTemplateId = new QName(definitions.getTargetNamespace(), serviceTemplate.getId());
            }

            BPELDefrostProcessBuilder.LOG.debug("ServiceTemplate {} has no DefreezePlan, generating a new plan",
                                                serviceTemplateId.toString());
            final BPELPlan newBuildPlan = buildPlan(csarName, definitions, serviceTemplateId);

            if (newBuildPlan != null) {
                BPELDefrostProcessBuilder.LOG.debug("Created Defreeze sPlan "
                    + newBuildPlan.getBpelProcessElement().getAttribute("name"));
                plans.add(newBuildPlan);
            }

        }
        return plans;
    }

}
