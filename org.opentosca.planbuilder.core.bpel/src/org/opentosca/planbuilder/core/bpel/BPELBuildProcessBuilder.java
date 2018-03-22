package org.opentosca.planbuilder.core.bpel;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.planbuilder.AbstractBuildPlanBuilder;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.bpel.handlers.BPELPlanHandler;
import org.opentosca.planbuilder.core.bpel.helpers.BPELFinalizer;
import org.opentosca.planbuilder.core.bpel.helpers.EmptyPropertyToInputInitializer;
import org.opentosca.planbuilder.core.bpel.helpers.NodeInstanceVariablesHandler;
import org.opentosca.planbuilder.core.bpel.helpers.PropertyMappingsToOutputInitializer;
import org.opentosca.planbuilder.core.bpel.helpers.PropertyVariableInitializer;
import org.opentosca.planbuilder.core.bpel.helpers.PropertyVariableInitializer.PropertyMap;
import org.opentosca.planbuilder.core.bpel.helpers.ServiceInstanceVariablesHandler;
import org.opentosca.planbuilder.core.plugins.IPlanBuilderPostPhasePlugin;
import org.opentosca.planbuilder.core.plugins.IPlanBuilderTypePlugin;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELScopeActivity;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This Class represents the high-level algorithm of the concept in <a href=
 * "http://www2.informatik.uni-stuttgart.de/cgi-bin/NCSTRL/NCSTRL_view.pl?id=BCLR-0043&mod=0&engl=1&inst=FAK"
 * >Konzept und Implementierung eine Java-Komponente zur Generierung von WS-BPEL 2.0 BuildPlans fuer
 * OpenTOSCA</a>. It is responsible for generating the Build Plan Skeleton and assign plugins to
 * handle the different templates inside a TopologyTemplate.
 * </p>
 *
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public class BPELBuildProcessBuilder extends AbstractBuildPlanBuilder {

    final static Logger LOG = LoggerFactory.getLogger(BPELBuildProcessBuilder.class);

    // class for initializing properties inside the plan
    private final PropertyVariableInitializer propertyInitializer;
    // class for initializing output with boundarydefinitions of a
    // serviceTemplate
    private final PropertyMappingsToOutputInitializer propertyOutputInitializer;
    // adds serviceInstance Variable and instanceDataAPIUrl to buildPlans

    private ServiceInstanceVariablesHandler serviceInstanceInitializer;

    // class for finalizing build plans (e.g when some template didn't receive
    // some provisioning logic and they must be filled with empty elements)
    private final BPELFinalizer finalizer;
    // accepted operations for provisioning
    private final List<String> opNames = new ArrayList<>();

    private BPELPlanHandler planHandler;

    private NodeInstanceVariablesHandler instanceInit;


    private final EmptyPropertyToInputInitializer emptyPropInit = new EmptyPropertyToInputInitializer();

    /**
     * <p>
     * Default Constructor
     * </p>
     */
    public BPELBuildProcessBuilder() {
        try {
            this.planHandler = new BPELPlanHandler();
            this.serviceInstanceInitializer = new ServiceInstanceVariablesHandler();
            this.instanceInit = new NodeInstanceVariablesHandler(this.planHandler);
        }
        catch (final ParserConfigurationException e) {
            BPELBuildProcessBuilder.LOG.error("Error while initializing BuildPlanHandler", e);
        }
        // TODO seems ugly
        this.propertyInitializer = new PropertyVariableInitializer(this.planHandler);
        this.propertyOutputInitializer = new PropertyMappingsToOutputInitializer();
        this.finalizer = new BPELFinalizer();
        this.opNames.add("install");
        this.opNames.add("configure");
        this.opNames.add("start");
        // this.opNames.add("connectTo");
        // this.opNames.add("hostOn");
    }

    /**
     * Returns the number of the plugins registered with this planbuilder
     *
     * @return integer denoting the count of plugins
     */
    public int registeredPlugins() {
        return this.pluginRegistry.getGenericPlugins().size() + this.pluginRegistry.getDaPlugins().size()
            + this.pluginRegistry.getIaPlugins().size() + this.pluginRegistry.getPostPlugins().size()
            + this.pluginRegistry.getProvPlugins().size();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opentosca.planbuilder.IPlanBuilder#buildPlan(java.lang.String,
     * org.opentosca.planbuilder.model.tosca.AbstractDefinitions, javax.xml.namespace.QName)
     */
    @Override
    public BPELPlan buildPlan(final String csarName, final AbstractDefinitions definitions,
                              final QName serviceTemplateId) {
        // create empty plan from servicetemplate and add definitions

        for (final AbstractServiceTemplate serviceTemplate : definitions.getServiceTemplates()) {
            String namespace;
            if (serviceTemplate.getTargetNamespace() != null) {
                namespace = serviceTemplate.getTargetNamespace();
            } else {
                namespace = definitions.getTargetNamespace();
            }

            if (namespace.equals(serviceTemplateId.getNamespaceURI())
                && serviceTemplate.getId().equals(serviceTemplateId.getLocalPart())) {
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


                this.planHandler.registerExtension("http://iaas.uni-stuttgart.de/bpel/extensions/bpel4restlight", true,
                                                   newBuildPlan);

                final PropertyMap propMap = this.propertyInitializer.initializePropertiesAsVariables(newBuildPlan);
                // init output
                this.propertyOutputInitializer.initializeBuildPlanOutput(definitions, newBuildPlan, propMap);

                // instanceDataAPI handling is done solely trough this extension

                // initialize instanceData handling
                this.serviceInstanceInitializer.initializeInstanceDataFromInput(newBuildPlan);

                this.emptyPropInit.initializeEmptyPropertiesAsInputParam(newBuildPlan, propMap);

                runPlugins(newBuildPlan, propMap);

                this.serviceInstanceInitializer.addCorrellationID(newBuildPlan);

                this.serviceInstanceInitializer.appendSetServiceInstanceState(newBuildPlan,
                                                                              newBuildPlan.getBpelMainFlowElement(),
                                                                              "CREATING");
                this.serviceInstanceInitializer.appendSetServiceInstanceState(newBuildPlan,
                                                                              newBuildPlan.getBpelMainSequenceOutputAssignElement(),
                                                                              "CREATED");

                this.finalizer.finalize(newBuildPlan);
                BPELBuildProcessBuilder.LOG.debug("Created BuildPlan:");
                BPELBuildProcessBuilder.LOG.debug(ModelUtils.getStringFromDoc(newBuildPlan.getBpelDocument()));
                return newBuildPlan;
            }
        }
        BPELBuildProcessBuilder.LOG.warn("Couldn't create BuildPlan for ServiceTemplate {} in Definitions {} of CSAR {}",
                                         serviceTemplateId.toString(), definitions.getId(), csarName);
        return null;
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
            QName serviceTemplateId;
            // targetNamespace attribute doesn't has to be set, so we check it
            if (serviceTemplate.getTargetNamespace() != null) {
                serviceTemplateId = new QName(serviceTemplate.getTargetNamespace(), serviceTemplate.getId());
            } else {
                serviceTemplateId = new QName(definitions.getTargetNamespace(), serviceTemplate.getId());
            }

            if (!serviceTemplate.hasBuildPlan()) {
                BPELBuildProcessBuilder.LOG.debug("ServiceTemplate {} has no BuildPlan, generating BuildPlan",
                                                  serviceTemplateId.toString());
                final BPELPlan newBuildPlan = buildPlan(csarName, definitions, serviceTemplateId);

                if (newBuildPlan != null) {
                    BPELBuildProcessBuilder.LOG.debug("Created BuildPlan "
                        + newBuildPlan.getBpelProcessElement().getAttribute("name"));
                    plans.add(newBuildPlan);
                }
            } else {
                BPELBuildProcessBuilder.LOG.debug("ServiceTemplate {} has BuildPlan, no generation needed",
                                                  serviceTemplateId.toString());
            }
        }
        return plans;
    }

    /**
     * <p>
     * This method assigns plugins to the already initialized BuildPlan and its TemplateBuildPlans.
     * First there will be checked if any generic plugin can handle a template of the TopologyTemplate
     * </p>
     *
     * @param buildPlan a BuildPlan which is alread initialized
     * @param map a PropertyMap which contains mappings from Template to Property and to variable name
     *        of inside the BuidlPlan
     */
    private void runPlugins(final BPELPlan buildPlan, final PropertyMap map) {

        for (final BPELScopeActivity templatePlan : buildPlan.getTemplateBuildPlans()) {
            if (templatePlan.getNodeTemplate() != null) {
                // handling nodetemplate
                final AbstractNodeTemplate nodeTemplate = templatePlan.getNodeTemplate();
                BPELBuildProcessBuilder.LOG.debug("Trying to handle NodeTemplate " + nodeTemplate.getId());
                final BPELPlanContext context = new BPELPlanContext(templatePlan, map, buildPlan.getServiceTemplate());
                // check if we have a generic plugin to handle the template
                // Note: if a generic plugin fails during execution the
                // TemplateBuildPlan is broken!
                final IPlanBuilderTypePlugin plugin = this.findTypePlugin(nodeTemplate);
                if (plugin == null) {
                    BPELBuildProcessBuilder.LOG.debug("Handling NodeTemplate {} with ProvisioningChain",
                                                      nodeTemplate.getId());
                    final OperationChain chain = BPELScopeBuilder.createOperationChain(nodeTemplate);
                    if (chain == null) {
                        BPELBuildProcessBuilder.LOG.warn("Couldn't create ProvisioningChain for NodeTemplate {}",
                                                         nodeTemplate.getId());
                    } else {
                        BPELBuildProcessBuilder.LOG.debug("Created ProvisioningChain for NodeTemplate {}",
                                                          nodeTemplate.getId());
                        chain.executeIAProvisioning(context);
                        chain.executeDAProvisioning(context);
                        chain.executeOperationProvisioning(context, this.opNames);
                    }
                } else {
                    BPELBuildProcessBuilder.LOG.info("Handling NodeTemplate {} with generic plugin",
                                                     nodeTemplate.getId());
                    plugin.handle(context);
                }

                for (final IPlanBuilderPostPhasePlugin postPhasePlugin : this.pluginRegistry.getPostPlugins()) {
                    if (postPhasePlugin.canHandle(nodeTemplate)) {
                        postPhasePlugin.handle(context, nodeTemplate);
                    }
                }

            } else {
                // handling relationshiptemplate
                final AbstractRelationshipTemplate relationshipTemplate = templatePlan.getRelationshipTemplate();
                final BPELPlanContext context = new BPELPlanContext(templatePlan, map, buildPlan.getServiceTemplate());

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
                        targetChain.executeOperationProvisioning(context, this.opNames);
                    }

                    if (sourceChain != null) {
                        BPELBuildProcessBuilder.LOG.warn("Couldn't create ProvisioningChain for SourceInterface of RelationshipTemplate {}",
                                                         relationshipTemplate.getId());
                        sourceChain.executeIAProvisioning(context);
                        sourceChain.executeOperationProvisioning(context, this.opNames);
                    }
                } else {
                    BPELBuildProcessBuilder.LOG.info("Handling RelationshipTemplate {} with generic plugin",
                                                     relationshipTemplate.getId());
                    handleWithTypePlugin(context, relationshipTemplate);
                }

                for (final IPlanBuilderPostPhasePlugin postPhasePlugin : this.pluginRegistry.getPostPlugins()) {
                    if (postPhasePlugin.canHandle(relationshipTemplate)) {
                        postPhasePlugin.handle(context, relationshipTemplate);
                    }
                }
            }
        }
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

}
