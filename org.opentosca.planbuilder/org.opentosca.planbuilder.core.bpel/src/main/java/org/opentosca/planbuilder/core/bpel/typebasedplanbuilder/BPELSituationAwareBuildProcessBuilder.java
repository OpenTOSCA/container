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

import org.opentosca.planbuilder.core.AbstractBuildPlanBuilder;
import org.opentosca.planbuilder.core.bpel.artifactbasednodehandler.BPELScopeBuilder;
import org.opentosca.planbuilder.core.bpel.fragments.BPELProcessFragments;
import org.opentosca.planbuilder.core.bpel.handlers.BPELFinalizer;
import org.opentosca.planbuilder.core.bpel.handlers.BPELPlanHandler;
import org.opentosca.planbuilder.core.bpel.handlers.CorrelationIDInitializer;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.EmptyPropertyToInputHandler;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.NodeRelationInstanceVariablesHandler;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.PropertyVariableHandler;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.ServiceTemplateBoundaryPropertyMappingsToOutputHandler;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.SimplePlanBuilderServiceInstanceHandler;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.SituationTriggerRegistration;
import org.opentosca.planbuilder.core.bpel.typebasednodehandler.BPELPluginHandler;
import org.opentosca.planbuilder.core.plugins.context.Property2VariableMapping;
import org.opentosca.planbuilder.core.plugins.registry.PluginRegistry;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractPolicy;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
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
     * org.opentosca.planbuilder.model.tosca.AbstractDefinitions, javax.xml.namespace.QName)
     */
    @Override
    public BPELPlan buildPlan(final String csarName, final AbstractDefinitions definitions,
                              final AbstractServiceTemplate serviceTemplate) {
        // create empty plan from servicetemplate and add definitions

        String namespace;
        if (serviceTemplate.getTargetNamespace() != null) {
            namespace = serviceTemplate.getTargetNamespace();
        } else {
            namespace = definitions.getTargetNamespace();
        }

        if (namespace.equals(serviceTemplate.getQName().getNamespaceURI())
            && serviceTemplate.getId().equals(serviceTemplate.getQName().getLocalPart())) {

            final String processName = ModelUtils.makeValidNCName(serviceTemplate.getId() + "_sitAwareBuildPlan");
            final String processNamespace = serviceTemplate.getTargetNamespace() + "_sitAwareBuildPlan";

            Map<AbstractNodeTemplate, Collection<AbstractPolicy>> situationPolicies =
                this.getSituationPolicies(serviceTemplate);

            if (situationPolicies.isEmpty()) {
                // no situation-aware handling possible
                return null;
            }

            // generate id for each situation policy
            Map<AbstractPolicy, String> policy2IdMap = this.nodePolicyToId(situationPolicies);

            final AbstractPlan buildPlan =
                generatePOG(new QName(processNamespace, processName).toString(), definitions, serviceTemplate);

            LOG.debug("Generated the following abstract prov plan: ");
            LOG.debug(buildPlan.toString());

            final BPELPlan newBuildPlan =
                this.planHandler.createEmptyBPELPlan(processNamespace, processName, buildPlan, "initiate");

            newBuildPlan.setTOSCAInterfaceName("OpenTOSCA-Lifecycle-Interface");
            newBuildPlan.setTOSCAOperationname("initiate");

            this.planHandler.initializeBPELSkeleton(newBuildPlan, csarName);

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
            serviceTemplate.getQName().toString(), definitions.getId(),
            csarName);
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

            if (!serviceTemplate.hasBuildPlan()) {
                BPELSituationAwareBuildProcessBuilder.LOG.debug("ServiceTemplate {} has no BuildPlan, generating BuildPlan",
                    serviceTemplate.getQName().toString());
                final BPELPlan newBuildPlan = buildPlan(csarName, definitions, serviceTemplate);

                if (newBuildPlan != null) {
                    BPELSituationAwareBuildProcessBuilder.LOG.debug("Created BuildPlan "
                        + newBuildPlan.getBpelProcessElement().getAttribute("name"));
                    plans.add(newBuildPlan);
                }
            } else {
                BPELSituationAwareBuildProcessBuilder.LOG.debug("ServiceTemplate {} has BuildPlan, no generation needed",
                    serviceTemplate.getQName().toString());
            }
        }
        if (!plans.isEmpty()) {
        	LOG.info("Created {} situation-aware build plans for CSAR {}", String.valueOf(plans.size()), csarName);
        }
        return plans;
    }

    private Map<AbstractPolicy, String> nodePolicyToId(Map<AbstractNodeTemplate, Collection<AbstractPolicy>> situationPolicies) {
        Map<AbstractPolicy, String> nodePolicyToIdMap = new HashMap<AbstractPolicy, String>();

        for (AbstractNodeTemplate node : situationPolicies.keySet()) {
            for (AbstractPolicy policy : situationPolicies.get(node)) {
                String id = node.getId() + "_" + policy.getName();
                nodePolicyToIdMap.put(policy, id);
            }
        }

        return nodePolicyToIdMap;
    }

    private Map<AbstractNodeTemplate, Collection<AbstractPolicy>> getSituationPolicies(AbstractServiceTemplate serviceTemplate) {
        Map<AbstractNodeTemplate, Collection<AbstractPolicy>> nodeToPolicies =
            new HashMap<AbstractNodeTemplate, Collection<AbstractPolicy>>();

        if (serviceTemplate.getTopologyTemplate()  == null) {
            return nodeToPolicies;
        }

        for (AbstractNodeTemplate nodeTemplate : serviceTemplate.getTopologyTemplate().getNodeTemplates()) {
            Collection<AbstractPolicy> situationPolicies = new HashSet<AbstractPolicy>();
            for (AbstractPolicy policy : nodeTemplate.getPolicies()) {
                if (policy.getType().getId().equals(new QName("http://opentosca.org/servicetemplates/policytypes",
                    "SituationPolicy_w1-wip1"))) {
                    situationPolicies.add(policy);
                }
            }
            if (!situationPolicies.isEmpty()) {
                nodeToPolicies.put(nodeTemplate, situationPolicies);
            }
        }
        return nodeToPolicies;
    }
}
