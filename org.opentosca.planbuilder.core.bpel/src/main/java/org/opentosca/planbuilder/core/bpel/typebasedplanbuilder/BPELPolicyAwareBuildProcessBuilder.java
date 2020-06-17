package org.opentosca.planbuilder.core.bpel.typebasedplanbuilder;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.opentosca.planbuilder.AbstractBuildPlanBuilder;
import org.opentosca.planbuilder.core.bpel.artifactbasednodehandler.BPELScopeBuilder;
import org.opentosca.planbuilder.core.bpel.artifactbasednodehandler.OperationChain;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.bpel.handlers.BPELFinalizer;
import org.opentosca.planbuilder.core.bpel.handlers.BPELPlanHandler;
import org.opentosca.planbuilder.core.bpel.handlers.CorrelationIDInitializer;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.EmptyPropertyToInputHandler;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.NodeRelationInstanceVariablesHandler;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.PropertyVariableHandler;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.ServiceTemplateBoundaryPropertyMappingsToOutputHandler;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.SimplePlanBuilderServiceInstanceHandler;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELScope;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractPolicy;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.opentosca.planbuilder.plugins.context.Property2VariableMapping;
import org.opentosca.planbuilder.plugins.registry.PluginRegistry;
import org.opentosca.planbuilder.plugins.typebased.IPlanBuilderPolicyAwarePostPhasePlugin;
import org.opentosca.planbuilder.plugins.typebased.IPlanBuilderPolicyAwarePrePhasePlugin;
import org.opentosca.planbuilder.plugins.typebased.IPlanBuilderPolicyAwareTypePlugin;
import org.opentosca.planbuilder.plugins.typebased.IPlanBuilderPostPhasePlugin;
import org.opentosca.planbuilder.plugins.typebased.IPlanBuilderPrePhasePlugin;
import org.opentosca.planbuilder.plugins.typebased.IPlanBuilderTypePlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This Class represents the high-level algorithm of the concept in <a href= "http://www2.informatik.uni-stuttgart.de/cgi-bin/NCSTRL/NCSTRL_view.pl?id=BCLR-0043&mod=0&engl=1&inst=FAK"
 * >Konzept und Implementierung eine Java-Komponente zur Generierung von WS-BPEL 2.0 BuildPlans fuer OpenTOSCA</a>. It
 * is responsible for generating the Build Plan Skeleton and assign plugins to handle the different templates inside a
 * TopologyTemplate.
 * </p>
 * <p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public class BPELPolicyAwareBuildProcessBuilder extends AbstractBuildPlanBuilder {

    final static Logger LOG = LoggerFactory.getLogger(BPELPolicyAwareBuildProcessBuilder.class);

    // class for initializing properties inside the plan
    private final PropertyVariableHandler propertyInitializer;
    // class for initializing output with boundarydefinitions of a
    // serviceTemplate
    private final ServiceTemplateBoundaryPropertyMappingsToOutputHandler propertyOutputInitializer;
    // adds serviceInstance Variable and instanceDataAPIUrl to buildPlans

    private SimplePlanBuilderServiceInstanceHandler serviceInstanceHandler;
    // class for finalizing build plans (e.g when some template didn't receive
    // some provisioning logic and they must be filled with empty elements)
    private final BPELFinalizer finalizer;
    // accepted operations for provisioning
    private final List<String> opNames = new ArrayList<>();

    private BPELPlanHandler planHandler;
    private final BPELScopeBuilder scopeBuilder;
    private NodeRelationInstanceVariablesHandler instanceInit;

    private CorrelationIDInitializer correlationHandler;

    private final EmptyPropertyToInputHandler emptyPropInit;

    /**
     * <p>
     * Default Constructor
     * </p>
     */
    public BPELPolicyAwareBuildProcessBuilder(PluginRegistry pluginRegistry) {
        super(pluginRegistry);
        scopeBuilder = new BPELScopeBuilder(pluginRegistry);
        emptyPropInit = new EmptyPropertyToInputHandler(scopeBuilder);
        try {
            this.planHandler = new BPELPlanHandler();
            this.serviceInstanceHandler = new SimplePlanBuilderServiceInstanceHandler();
            this.instanceInit = new NodeRelationInstanceVariablesHandler(this.planHandler);
            this.correlationHandler = new CorrelationIDInitializer();
        } catch (final ParserConfigurationException e) {
            LOG.error("Error while initializing BuildPlanHandler", e);
        }
        // TODO seems ugly
        this.propertyInitializer = new PropertyVariableHandler(this.planHandler);
        this.propertyOutputInitializer = new ServiceTemplateBoundaryPropertyMappingsToOutputHandler();
        this.finalizer = new BPELFinalizer();
        this.opNames.add("install");
        this.opNames.add("configure");
        this.opNames.add("start");
        // this.opNames.add("connectTo");
        // this.opNames.add("hostOn");
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
        // create empty plan from servicetemplate and add definitions

        final String processName = serviceTemplate.getId() + "_buildPlan";
        final String processNamespace = serviceTemplate.getTargetNamespace() + "_buildPlan";

        final AbstractPlan buildPlan =
            this.generatePOG(new QName(processNamespace, processName).toString(), definitions, serviceTemplate);

        LOG.debug("Generated the following abstract prov plan: ");
        LOG.debug(buildPlan.toString());

        final BPELPlan newBuildPlan =
            this.planHandler.createEmptyBPELPlan(processNamespace, processName, buildPlan, "initiate");

        newBuildPlan.setTOSCAInterfaceName("OpenTOSCA-Lifecycle-Interface");
        newBuildPlan.setTOSCAOperationname("initiate");

        this.planHandler.initializeBPELSkeleton(newBuildPlan, csarName);
        // newBuildPlan.setCsarName(csarName);

        // create empty templateplans for each template and add them to
        // buildplan
        // for (AbstractNodeTemplate nodeTemplate :
        // serviceTemplate.getTopologyTemplate().getNodeTemplates()) {
        // BPELScope newTemplate =
        // this.templateHandler.createTemplateBuildPlan(nodeTemplate,
        // newBuildPlan);
        // newTemplate.setNodeTemplate(nodeTemplate);
        // newBuildPlan.addTemplateBuildPlan(newTemplate);
        // }
        //
        // for (AbstractRelationshipTemplate relationshipTemplate :
        // serviceTemplate.getTopologyTemplate().getRelationshipTemplates())
        // {
        // BPELScope newTemplate =
        // this.templateHandler.createTemplateBuildPlan(relationshipTemplate,
        // newBuildPlan);
        // newTemplate.setRelationshipTemplate(relationshipTemplate);
        // newBuildPlan.addTemplateBuildPlan(newTemplate);
        // }
        //
        // // connect the templates
        // this.initializeDependenciesInBuildPlan(newBuildPlan);

        this.planHandler.registerExtension("http://www.apache.org/ode/bpel/extensions/bpel4restlight", true,
            newBuildPlan);

        final Property2VariableMapping propMap =
            this.propertyInitializer.initializePropertiesAsVariables(newBuildPlan, serviceTemplate);
        // init output
        this.propertyOutputInitializer.initializeBuildPlanOutput(definitions, newBuildPlan, propMap, serviceTemplate);

        // instanceDataAPI handling is done solely trough this extension

        // initialize instanceData handling
        this.serviceInstanceHandler.appendCreateServiceInstanceVarsAndAnitializeWithInstanceDataAPI(newBuildPlan);

        String serviceInstanceUrl = this.serviceInstanceHandler.findServiceInstanceUrlVariableName(newBuildPlan);
        String serviceInstanceId = this.serviceInstanceHandler.findServiceInstanceIdVarName(newBuildPlan);
        String serviceTemplateUrl = this.serviceInstanceHandler.findServiceTemplateUrlVariableName(newBuildPlan);

        this.emptyPropInit.initializeEmptyPropertiesAsInputParam(newBuildPlan, propMap, serviceInstanceUrl,
            serviceInstanceId, serviceTemplateUrl, serviceTemplate,
            csarName);

        runPlugins(newBuildPlan, propMap, serviceInstanceUrl, serviceInstanceId, serviceTemplateUrl, csarName);

        this.correlationHandler.addCorrellationID(newBuildPlan);

        String serviceInstanceURLVarName = this.serviceInstanceHandler.findServiceInstanceUrlVariableName(newBuildPlan);
        this.serviceInstanceHandler.appendSetServiceInstanceState(newBuildPlan, newBuildPlan.getBpelMainFlowElement(),
            "CREATING", serviceInstanceURLVarName);
        this.serviceInstanceHandler.appendSetServiceInstanceState(newBuildPlan,
            newBuildPlan.getBpelMainSequenceOutputAssignElement(),
            "CREATED", serviceInstanceURLVarName);

        this.finalizer.finalize(newBuildPlan);
        LOG.debug("Created BuildPlan:");
        LOG.debug(getStringFromDoc(newBuildPlan.getBpelDocument()));
        return newBuildPlan;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opentosca.planbuilder.IPlanBuilder#buildPlans(java.lang.String,
     * org.opentosca.planbuilder.model.tosca.AbstractDefinitions)
     */
    @Override
    public List<AbstractPlan> buildPlans(final String csarName, final AbstractDefinitions definitions) {
        final List<AbstractPlan> plans = new ArrayList<>();
        for (final AbstractServiceTemplate serviceTemplate : definitions.getServiceTemplates()) {

            if (!serviceTemplate.hasBuildPlan()) {
                LOG.debug("ServiceTemplate {} has no BuildPlan, generating BuildPlan",
                    serviceTemplate.toString());
                final BPELPlan newBuildPlan = buildPlan(csarName, definitions, serviceTemplate);

                if (newBuildPlan != null) {
                    LOG.debug("Created BuildPlan "
                        + newBuildPlan.getBpelProcessElement().getAttribute("name"));
                    plans.add(newBuildPlan);
                }
            } else {
                LOG.debug("ServiceTemplate {} has BuildPlan, no generation needed",
                    serviceTemplate.getQName().toString());
            }
        }
        return plans;
    }

    /**
     * <p>
     * This method assigns plugins to the already initialized BuildPlan and its TemplateBuildPlans. First there will be
     * checked if any generic plugin can handle a template of the TopologyTemplate
     * </p>
     *
     * @param buildPlan a BuildPlan which is alread initialized
     * @param map       a PropertyMap which contains mappings from Template to Property and to variable name of inside
     *                  the BuidlPlan
     */
    private boolean runPlugins(final BPELPlan buildPlan, final Property2VariableMapping map, String serviceInstanceUrl,
                               String serviceInstanceId, String serviceTemplateUrl, String csarName) {

        for (final BPELScope templatePlan : buildPlan.getTemplateBuildPlans()) {
            boolean handled = false;
            if (templatePlan.getNodeTemplate() != null) {
                // handling nodetemplate
                final AbstractNodeTemplate nodeTemplate = templatePlan.getNodeTemplate();
                LOG.debug("Trying to handle NodeTemplate " + nodeTemplate.getId());
                final BPELPlanContext context = new BPELPlanContext(scopeBuilder, buildPlan, templatePlan, map, buildPlan.getServiceTemplate(),
                    serviceInstanceUrl, serviceInstanceId, serviceTemplateUrl, csarName);
                // check if we have a generic plugin to handle the template
                // Note: if a generic plugin fails during execution the
                // TemplateBuildPlan is broken!

                for (IPlanBuilderPrePhasePlugin prePhasePlugin : this.pluginRegistry.getPrePlugins()) {
                    if (prePhasePlugin.canHandleCreate(nodeTemplate)) {
                        prePhasePlugin.handleCreate(context, nodeTemplate);
                    }
                }

                if (nodeTemplate.getPolicies().isEmpty()) {
                    final IPlanBuilderTypePlugin plugin = this.pluginRegistry.findTypePluginForCreation(nodeTemplate);
                    if (plugin != null) {
                        LOG.info("Handling NodeTemplate {} with type plugin {}",
                            nodeTemplate.getId(), plugin.getID());
                        handled = plugin.handleCreate(context, nodeTemplate);
                    } else {
                        LOG.warn("Can't handle NodeTemplate {} with type plugin",
                            nodeTemplate.getId());
                    }

                    for (final IPlanBuilderPostPhasePlugin postPhasePlugin : this.pluginRegistry.getPostPlugins()) {
                        if (postPhasePlugin.canHandleCreate(context, nodeTemplate)) {
                            handled = postPhasePlugin.handleCreate(context, nodeTemplate);
                        }
                    }
                } else {
                    // policy aware handling
                    final IPlanBuilderPolicyAwareTypePlugin policyPlugin =
                        this.pluginRegistry.findPolicyAwareTypePluginForCreation(nodeTemplate);
                    if (policyPlugin == null) {
                        LOG.debug("Handling NodeTemplate {} with ProvisioningChain",
                            nodeTemplate.getId());
                        final OperationChain chain = new BPELScopeBuilder(pluginRegistry).createOperationChain(nodeTemplate, this.opNames);
                        if (chain == null) {
                            LOG.warn("Couldn't create ProvisioningChain for NodeTemplate {}",
                                nodeTemplate.getId());
                        } else {
                            LOG.debug("Created ProvisioningChain for NodeTemplate {}",
                                nodeTemplate.getId());

                            final List<AbstractPolicy> policies = nodeTemplate.getPolicies();
                            final Map<AbstractPolicy, IPlanBuilderPolicyAwarePrePhasePlugin<BPELPlanContext>> compatiblePrePlugins =
                                new HashMap<>();
                            final Map<AbstractPolicy, IPlanBuilderPolicyAwarePostPhasePlugin<BPELPlanContext>> compatiblePostPlugins =
                                new HashMap<>();

                            for (final AbstractPolicy policy : policies) {
                                boolean matched = false;
                                for (final IPlanBuilderPolicyAwarePrePhasePlugin<?> policyPrePhasePlugin : this.pluginRegistry.getPolicyAwarePrePhasePlugins()) {
                                    if (policyPrePhasePlugin.canHandlePolicyAwareCreate(nodeTemplate, policy)) {
                                        compatiblePrePlugins.put(policy,
                                            (IPlanBuilderPolicyAwarePrePhasePlugin<BPELPlanContext>) policyPrePhasePlugin);
                                        matched = true;
                                        break;
                                    }
                                }

                                if (matched) {
                                    continue;
                                }

                                for (final IPlanBuilderPolicyAwarePostPhasePlugin<?> policyPostPhasePlugin : this.pluginRegistry.getPolicyAwarePostPhasePlugins()) {
                                    if (policyPostPhasePlugin.canHandle(nodeTemplate, policy)) {
                                        compatiblePostPlugins.put(policy,
                                            (IPlanBuilderPolicyAwarePostPhasePlugin<BPELPlanContext>) policyPostPhasePlugin);
                                        matched = true;
                                        break;
                                    }
                                }
                            }

                            if (policies.size() != compatiblePrePlugins.keySet().size()
                                + compatiblePostPlugins.keySet().size()) {
                                handled = false;
                            } else {

                                for (final AbstractPolicy policy : compatiblePrePlugins.keySet()) {
                                    compatiblePrePlugins.get(policy).handlePolicyAwareCreate(context, nodeTemplate,
                                        policy);
                                }

                                for (final AbstractPolicy policy : compatiblePostPlugins.keySet()) {
                                    compatiblePostPlugins.get(policy).handle(context, nodeTemplate, policy);
                                }

                                chain.executeIAProvisioning(context);
                                chain.executeDAProvisioning(context);
                                chain.executeOperationProvisioning(context, this.opNames);
                            }
                        }
                    } else {
                        LOG.info("Handling NodeTemplate {} with generic policy aware plugin",
                            nodeTemplate.getId());
                        handled = policyPlugin.handlePolicyAwareCreate(context);
                    }

                    for (final IPlanBuilderPostPhasePlugin postPhasePlugin : this.pluginRegistry.getPostPlugins()) {
                        if (postPhasePlugin.canHandleCreate(context, nodeTemplate)) {
                            handled = postPhasePlugin.handleCreate(context, nodeTemplate);
                        }
                    }
                }

                if (!handled) {
                    return handled;
                }
            } else {
                // handling relationshiptemplate
                final AbstractRelationshipTemplate relationshipTemplate = templatePlan.getRelationshipTemplate();
                final BPELPlanContext context = new BPELPlanContext(scopeBuilder, buildPlan, templatePlan, map, buildPlan.getServiceTemplate(),
                    serviceInstanceUrl, serviceInstanceId, serviceTemplateUrl, csarName);

                // check if we have a generic plugin to handle the template
                // Note: if a generic plugin fails during execution the
                // TemplateBuildPlan is broken here!
                // TODO implement fallback
                if (canGenericPluginHandle(relationshipTemplate)) {

                    LOG.info("Handling RelationshipTemplate {} with generic plugin",
                        relationshipTemplate.getId());
                    IPlanBuilderTypePlugin plugin = this.pluginRegistry.findTypePluginForCreation(relationshipTemplate);
                    handled = this.pluginRegistry.handleCreateWithTypePlugin(context, relationshipTemplate, plugin);
                } else {
                    LOG.debug("Couldn't handle RelationshipTemplate {} with type plugin",
                        relationshipTemplate.getId());
                }

                for (final IPlanBuilderPostPhasePlugin postPhasePlugin : this.pluginRegistry.getPostPlugins()) {
                    if (postPhasePlugin.canHandleCreate(context, relationshipTemplate)) {
                        handled = postPhasePlugin.handleCreate(context, relationshipTemplate);
                    }
                }
            }
        }
        return true;
    }

    /**
     * <p>
     * Checks whether there is any generic plugin, that can handle the given RelationshipTemplate
     * </p>
     *
     * @param relationshipTemplate an AbstractRelationshipTemplate denoting a RelationshipTemplate
     * @return true if there is any generic plugin which can handle the given RelationshipTemplate, else false
     */
    private boolean canGenericPluginHandle(final AbstractRelationshipTemplate relationshipTemplate) {
        for (final IPlanBuilderTypePlugin plugin : this.pluginRegistry.getTypePlugins()) {
            if (plugin.canHandleCreate(relationshipTemplate)) {
                LOG.info("Found GenericPlugin {} thath can handle RelationshipTemplate {}",
                    plugin.getID(), relationshipTemplate.getId());
                return true;
            }
        }
        return false;
    }

    // TODO delete this method, or add to utils. is pretty much copied from the net

    /**
     * <p>
     * Converts the given DOM Document to a String
     * </p>
     *
     * @param doc a DOM Document
     * @return a String representation of the complete Document given
     */
    public String getStringFromDoc(final org.w3c.dom.Document doc) {
        try {
            final DOMSource domSource = new DOMSource(doc);
            final StringWriter writer = new StringWriter();
            final StreamResult result = new StreamResult(writer);
            final TransformerFactory tf = TransformerFactory.newInstance();
            final Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.transform(domSource, result);
            writer.flush();
            return writer.toString();
        } catch (final TransformerException ex) {
            LOG.error("Couldn't transform DOM Document to a String", ex);
            return null;
        }
    }
}
