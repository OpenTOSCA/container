package org.opentosca.planbuilder.core.bpmn.typebasedplanbuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TServiceTemplate;

import org.opentosca.container.core.model.ModelUtils;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.core.AbstractTerminationPlanBuilder;
import org.opentosca.planbuilder.core.bpmn.handlers.BPMNFinalizer;
import org.opentosca.planbuilder.core.bpmn.handlers.BPMNPlanHandler;
import org.opentosca.planbuilder.core.bpmn.handlers.SimplePlanBuilderServiceInstanceHandler;
import org.opentosca.planbuilder.core.bpmn.typebasednodehandler.BPMNPluginHandler;
import org.opentosca.planbuilder.core.plugins.context.Property2VariableMapping;
import org.opentosca.planbuilder.core.plugins.registry.PluginRegistry;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class BPMNTerminationProcessBuilder extends AbstractTerminationPlanBuilder {

    final static Logger LOG = LoggerFactory.getLogger(BPMNTerminationProcessBuilder.class);
    public static final String SUFFIX_BPMN_TERMINATION_PLAN = "_bpmn_terminationPlan";

    private BPMNPlanHandler planHandler;
    private BPMNFinalizer bpmnFinalizer;
    private BPMNPluginHandler bpmnPluginHandler;
    private SimplePlanBuilderServiceInstanceHandler serviceInstanceInitializer;

    public BPMNTerminationProcessBuilder(PluginRegistry pluginRegistry) {
        super(pluginRegistry);
        try {
            this.planHandler = new BPMNPlanHandler();
            this.bpmnFinalizer = new BPMNFinalizer();
            this.bpmnPluginHandler = new BPMNPluginHandler(pluginRegistry);
            this.serviceInstanceInitializer = new SimplePlanBuilderServiceInstanceHandler();

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<AbstractPlan> buildPlans(Csar csar, TDefinitions definitions) {
        final List<AbstractPlan> plans = new ArrayList<>();
        LOG.info(""+definitions);
        for (final TServiceTemplate serviceTemplate : definitions.getServiceTemplates()) {
            LOG.info("Generating termination plan for service template {}", serviceTemplate);
            final BPMNPlan newBuildPlan = buildPlan(csar, definitions, serviceTemplate);
            plans.add(newBuildPlan);

            return plans;
        }
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opentosca.planbuilder.IPlanBuilder#buildPlan(java.lang.String,
     * org.opentosca.planbuilder.model.tosca.TDefinitions, javax.xml.namespace.QName)
     */
    private BPMNPlan buildPlan(final Csar csar, final TDefinitions definitions,
                               final TServiceTemplate serviceTemplate) {
        LOG.info("Start Generating Termination Plan in BPMN for {}", csar.id());
        // create empty plan from servicetemplate and add definitions
        String namespace;
        if (serviceTemplate.getTargetNamespace() != null) {
            namespace = serviceTemplate.getTargetNamespace();
        } else {
            namespace = definitions.getTargetNamespace();
        }

        QName serviceTemplateQname = new QName(serviceTemplate.getTargetNamespace(), serviceTemplate.getId());
        if (namespace.equals(serviceTemplateQname.getNamespaceURI())
            && serviceTemplate.getId().equals(serviceTemplateQname.getLocalPart())) {
            LOG.info("Start Building BPMN buildPlan for ServiceTemplate {} in Definitions {} of CSAR {}",
                serviceTemplateQname.toString(), definitions.getId(), csar.id().csarName());

            final String processName = ModelUtils.makeValidNCName(serviceTemplate.getId() + SUFFIX_BPMN_TERMINATION_PLAN);
            final String processNamespace = serviceTemplate.getTargetNamespace() + SUFFIX_BPMN_TERMINATION_PLAN;

            AbstractPlan buildPlan = generateTOG(new QName(processNamespace, processName).toString(), definitions, serviceTemplate, csar);

            LOG.debug("Generated the following abstract prov plan: ");
            LOG.debug(buildPlan.toString());

            final BPMNPlan terminationPlan =
                this.planHandler.createEmptyBPMNPlan(processNamespace, processName, buildPlan, "initiate", csar);

            terminationPlan.setTOSCAInterfaceName("OpenTOSCA-Lifecycle-Interface");
            terminationPlan.setTOSCAOperationName("terminate");

            //this.planHandler.initializeBPMNSkeleton(terminationPlan, csar);

            // TODO: implement propertyInitializer
            final Property2VariableMapping propMap = null;
            //final Property2VariableMapping propMap =
            //    this.propertyInitializer.initializePropertiesAsVariables(terminationPlan, serviceTemplate);

            // instanceDataAPI handling is done solely trough this extension

            // initialize instanceData handling
            this.serviceInstanceInitializer.addInputOutputParameterCorrelationIDAndInstanceDataAPI(terminationPlan);
            this.serviceInstanceInitializer.addInputOutputParameterUserDefined(terminationPlan);
            String serviceInstanceUrl =
                this.serviceInstanceInitializer.findServiceInstanceUrlVariableName(terminationPlan);
            String serviceInstanceID = this.serviceInstanceInitializer.findServiceInstanceIdVarName(terminationPlan);
            String serviceTemplateUrl =
                this.serviceInstanceInitializer.findServiceTemplateUrlVariableName(terminationPlan);
            String planInstanceUrl = this.serviceInstanceInitializer.findPlanInstanceUrlVariableName(terminationPlan);

            // only generate diagram when all elements are instantiated
            //this.planHandler.generateBPMNDiagram(terminationPlan);
            // terminationPlan.setCsarName(csarName);

            /*
            try {
                this.bpmnFinalizer.finalize(terminationPlan);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }
            */
            return terminationPlan;
        }

        LOG.warn("Couldn't create BuildPlan for ServiceTemplate {} in Definitions {} of CSAR {}",
            serviceTemplateQname.toString(), definitions.getId(), csar.id().csarName());
        return null;
    }
}
