package org.opentosca.planbuilder.core.bpel.typebasedplanbuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TPolicy;
import org.eclipse.winery.model.tosca.TServiceTemplate;

import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.core.AbstractBuildPlanBuilder;
import org.opentosca.planbuilder.core.bpel.artifactbasednodehandler.BPELScopeBuilder;
import org.opentosca.planbuilder.core.bpel.fragments.BPELProcessFragments;
import org.opentosca.planbuilder.core.bpel.handlers.BPELFinalizer;
import org.opentosca.planbuilder.core.bpel.handlers.BPELPlanHandler;
import org.opentosca.planbuilder.core.bpel.handlers.CorrelationIDInitializer;
import org.opentosca.planbuilder.core.bpel.handlers.EmptyPropertyToInputHandler;
import org.opentosca.planbuilder.core.bpel.handlers.NodeRelationInstanceVariablesHandler;
import org.opentosca.planbuilder.core.bpel.handlers.PropertyVariableHandler;
import org.opentosca.planbuilder.core.bpel.handlers.ServiceTemplateBoundaryPropertyMappingsToOutputHandler;
import org.opentosca.planbuilder.core.bpel.handlers.SimplePlanBuilderServiceInstanceHandler;
import org.opentosca.planbuilder.core.bpel.handlers.SituationTriggerRegistration;
import org.opentosca.planbuilder.core.bpel.typebasednodehandler.BPELPluginHandler;
import org.opentosca.planbuilder.core.plugins.context.Property2VariableMapping;
import org.opentosca.planbuilder.core.plugins.registry.PluginRegistry;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

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
public class BPELSituationAwareBuildProcessBuilder extends AbstractBuildPlanBuilder {

    final static Logger LOG = LoggerFactory.getLogger(BPELSituationAwareBuildProcessBuilder.class);
    // class for initializing output with boundarydefinitions of a
    // serviceTemplate
    private final ServiceTemplateBoundaryPropertyMappingsToOutputHandler propertyOutputInitializer;
    // class for finalizing build plans (e.g when some template didn't receive
    // some provisioning logic and they must be filled with empty elements)
    private final BPELFinalizer finalizer;
    // adds serviceInstance Variable and instanceDataAPIUrl to buildPlans
    private final BPELPluginHandler bpelPluginHandler;
    private final EmptyPropertyToInputHandler emptyPropInit;
    // class for initializing properties inside the plan
    private PropertyVariableHandler propertyInitializer;
    private SimplePlanBuilderServiceInstanceHandler serviceInstanceInitializer;
    private CorrelationIDInitializer correlationHandler;
    private SituationTriggerRegistration sitRegistrationPlugin;
    private BPELPlanHandler planHandler;
    private NodeRelationInstanceVariablesHandler nodeRelationInstanceHandler;
    private BPELProcessFragments fragments;

    /**
     * <p>
     * Default Constructor
     * </p>
     */
    @Inject
    public BPELSituationAwareBuildProcessBuilder(PluginRegistry pluginRegistry) {
        super(pluginRegistry);
        bpelPluginHandler = new BPELPluginHandler(pluginRegistry);
        emptyPropInit = new EmptyPropertyToInputHandler(new BPELScopeBuilder(pluginRegistry));
        try {
            this.planHandler = new BPELPlanHandler();
            this.serviceInstanceInitializer = new SimplePlanBuilderServiceInstanceHandler();
            this.nodeRelationInstanceHandler = new NodeRelationInstanceVariablesHandler(this.planHandler);
            this.sitRegistrationPlugin = new SituationTriggerRegistration();
            this.correlationHandler = new CorrelationIDInitializer();
            this.fragments = new BPELProcessFragments();
            this.propertyInitializer = new PropertyVariableHandler(this.planHandler);
        } catch (final ParserConfigurationException e) {
            BPELSituationAwareBuildProcessBuilder.LOG.error("Error while initializing BuildPlanHandler, couldn't initialize internal parser", e);
        }
        // TODO seems ugly
        this.propertyOutputInitializer = new ServiceTemplateBoundaryPropertyMappingsToOutputHandler();
        this.finalizer = new BPELFinalizer();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opentosca.planbuilder.IPlanBuilder#buildPlan(java.lang.String,
     * org.opentosca.planbuilder.model.tosca.TDefinitions, javax.xml.namespace.QName)
     */
    @Override
    public BPELPlan buildPlan(final Csar csar, final TDefinitions definitions,
                              final TServiceTemplate serviceTemplate) {
        // create empty plan from servicetemplate and add definitions

        String namespace;
        if (serviceTemplate.getTargetNamespace() != null) {
            namespace = serviceTemplate.getTargetNamespace();
        } else {
            namespace = definitions.getTargetNamespace();
        }

        if (namespace.equals(serviceTemplate.getTargetNamespace())
            && serviceTemplate.getId().equals(serviceTemplate.getId())) {

            final String processName = ModelUtils.makeValidNCName(serviceTemplate.getId() + "_sitAwareBuildPlan");
            final String processNamespace = serviceTemplate.getTargetNamespace() + "_sitAwareBuildPlan";

            Map<TNodeTemplate, Collection<TPolicy>> situationPolicies =
                this.getSituationPolicies(serviceTemplate);

            if (situationPolicies.isEmpty()) {
                // no situation-aware handling possible
                return null;
            }

            // generate id for each situation policy
            Map<TPolicy, String> policy2IdMap = this.nodePolicyToId(situationPolicies);

            final AbstractPlan buildPlan =
                generatePOG(new QName(processNamespace, processName).toString(), definitions, serviceTemplate, csar);

            LOG.debug("Generated the following abstract prov plan: ");
            LOG.debug(buildPlan.toString());

            final BPELPlan newBuildPlan =
                this.planHandler.createEmptyBPELPlan(processNamespace, processName, buildPlan, "initiate");

            newBuildPlan.setTOSCAInterfaceName("OpenTOSCA-Lifecycle-Interface");
            newBuildPlan.setTOSCAOperationname("initiate");

            this.planHandler.initializeBPELSkeleton(newBuildPlan, csar);

            this.planHandler.registerExtension("http://www.apache.org/ode/bpel/extensions/bpel4restlight", true,
                newBuildPlan);

            final Property2VariableMapping propMap =
                this.propertyInitializer.initializePropertiesAsVariables(newBuildPlan, serviceTemplate);
            // init output
            this.propertyOutputInitializer.initializeBuildPlanOutput(definitions, newBuildPlan, propMap,
                serviceTemplate);

            // initialize instanceData handling
            this.serviceInstanceInitializer.appendCreateServiceInstanceVarsAndAnitializeWithInstanceDataAPI(newBuildPlan);

            String serviceInstanceUrl =
                this.serviceInstanceInitializer.findServiceInstanceUrlVariableName(newBuildPlan);

            // add situations to input to get Ids of situations and create situationsmonitor request
            this.correlationHandler.addCorrellationID(newBuildPlan);

            this.serviceInstanceInitializer.appendSetServiceInstanceState(newBuildPlan,
                newBuildPlan.getBpelMainSequenceOutputAssignElement(),
                "CREATED", serviceInstanceUrl);
            for (String inputLocalName : policy2IdMap.values()) {
                this.planHandler.addStringElementToPlanRequest(inputLocalName, newBuildPlan);
            }

            String anyVarName = this.planHandler.createAnyTypeVar(newBuildPlan);
            String requestVarName =
                this.planHandler.addGlobalStringVariable("situationsMonitorsRequestVar", newBuildPlan);

            try {
                Node createSituationMonitorNode =
                    this.fragments.createAssignAndPostSituationMonitorAsNode(situationPolicies, policy2IdMap, serviceInstanceUrl, anyVarName, requestVarName);
                createSituationMonitorNode = this.planHandler.importNode(newBuildPlan, createSituationMonitorNode);
                newBuildPlan.getBpelMainSequenceElement().appendChild(createSituationMonitorNode);
            } catch (SAXException e) {
                LOG.error("Couldn't parse XML file", e);
                return null;
            } catch (IOException e) {
                LOG.error("Couldn't read file", e);
                return null;
            }

            String planInstanceUrlVarName = this.serviceInstanceInitializer.findPlanInstanceUrlVariableName(newBuildPlan);
            this.serviceInstanceInitializer.appendSetServiceInstanceState(newBuildPlan,
                newBuildPlan.getBpelMainFlowElement(),
                "RUNNING", planInstanceUrlVarName);

            this.serviceInstanceInitializer.appendSetServiceInstanceState(newBuildPlan,
                newBuildPlan.getBpelMainSequenceOutputAssignElement(),
                "FINISHED", planInstanceUrlVarName);

            this.finalizer.finalize(newBuildPlan);
            return newBuildPlan;
        }

        BPELSituationAwareBuildProcessBuilder.LOG.warn("Couldn't create BuildPlan for ServiceTemplate {} in Definitions {} of CSAR {}",
            serviceTemplate.getId(), definitions.getId(),
            csar.id().csarName());
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opentosca.planbuilder.IPlanBuilder#buildPlans(java.lang.String,
     * org.opentosca.planbuilder.model.tosca.TDefinitions)
     */
    @Override
    public List<AbstractPlan> buildPlans(final Csar csar, final TDefinitions definitions) {
        final List<AbstractPlan> plans = new ArrayList<>();
        for (final TServiceTemplate serviceTemplate : definitions.getServiceTemplates()) {

            if (!ModelUtils.hasBuildPlan(serviceTemplate)) {
                BPELSituationAwareBuildProcessBuilder.LOG.debug("ServiceTemplate {} has no BuildPlan, generating BuildPlan",
                    serviceTemplate.getId());
                final BPELPlan newBuildPlan = buildPlan(csar, definitions, serviceTemplate);

                if (newBuildPlan != null) {
                    BPELSituationAwareBuildProcessBuilder.LOG.debug("Created BuildPlan "
                        + newBuildPlan.getBpelProcessElement().getAttribute("name"));
                    plans.add(newBuildPlan);
                }
            } else {
                BPELSituationAwareBuildProcessBuilder.LOG.debug("ServiceTemplate {} has BuildPlan, no generation needed",
                    serviceTemplate.getId());
            }
        }
        if (!plans.isEmpty()) {
        	LOG.info("Created {} situation-aware build plans for CSAR {}", String.valueOf(plans.size()), csar.id().csarName());
        }
        return plans;
    }

    private Map<TPolicy, String> nodePolicyToId(Map<TNodeTemplate, Collection<TPolicy>> situationPolicies) {
        Map<TPolicy, String> nodePolicyToIdMap = new HashMap<TPolicy, String>();

        for (TNodeTemplate node : situationPolicies.keySet()) {
            for (TPolicy policy : situationPolicies.get(node)) {
                String id = node.getId() + "_" + policy.getName();
                nodePolicyToIdMap.put(policy, id);
            }
        }

        return nodePolicyToIdMap;
    }

    private Map<TNodeTemplate, Collection<TPolicy>> getSituationPolicies(TServiceTemplate serviceTemplate) {
        Map<TNodeTemplate, Collection<TPolicy>> nodeToPolicies =
            new HashMap<TNodeTemplate, Collection<TPolicy>>();

        if (serviceTemplate.getTopologyTemplate()  == null) {
            return nodeToPolicies;
        }

        for (TNodeTemplate nodeTemplate : serviceTemplate.getTopologyTemplate().getNodeTemplates()) {
            Collection<TPolicy> situationPolicies = new HashSet<TPolicy>();
            if(nodeTemplate.getPolicies() != null) {
                for (TPolicy policy : nodeTemplate.getPolicies()) {
                    if (policy.getPolicyType().equals(new QName("http://opentosca.org/servicetemplates/policytypes",
                        "SituationPolicy_w1-wip1"))) {
                        situationPolicies.add(policy);
                    }
                }
            }
            if (!situationPolicies.isEmpty()) {
                nodeToPolicies.put(nodeTemplate, situationPolicies);
            }
        }
        return nodeToPolicies;
    }
}
