package org.opentosca.planbuilder.core.bpmn.typebasedplanbuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;

import org.opentosca.container.core.model.ModelUtils;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.core.AbstractBuildPlanBuilder;
import org.opentosca.planbuilder.core.bpmn.handlers.SimplePlanBuilderServiceInstanceHandler;
import org.opentosca.planbuilder.core.bpmn.context.BPMNPlanContext;
import org.opentosca.planbuilder.core.bpmn.handlers.BPMNFinalizer;
import org.opentosca.planbuilder.core.bpmn.handlers.BPMNPlanHandler;
import org.opentosca.planbuilder.core.bpmn.typebasednodehandler.BPMNPluginHandler;
import org.opentosca.planbuilder.core.plugins.context.Property2VariableMapping;
import org.opentosca.planbuilder.core.plugins.registry.PluginRegistry;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNPlan;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class BPMNBuildProcessBuilder extends AbstractBuildPlanBuilder {

    final static Logger LOG = LoggerFactory.getLogger(BPMNBuildProcessBuilder.class);

    private BPMNPlanHandler planHandler;
    private BPMNFinalizer bpmnFinalizer;
    private BPMNPluginHandler bpmnPluginHandler;
    private SimplePlanBuilderServiceInstanceHandler serviceInstanceInitializer;
    /**
     * <p>
     * Default Constructor
     * </p>
     */
    public BPMNBuildProcessBuilder(PluginRegistry pluginRegistry) {
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
            LOG.info("Generating build plan for service template {}", serviceTemplate);
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
        LOG.info("Start Building BPMN Plan for {}", csar.id());
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

            final String processName = ModelUtils.makeValidNCName(serviceTemplate.getId() + "_bpmn_buildPlan");
            final String processNamespace = serviceTemplate.getTargetNamespace() + "_bpmn_buildPlan";

            AbstractPlan buildPlan =
                AbstractBuildPlanBuilder.generatePOG(new QName(processNamespace, processName).toString(), definitions, serviceTemplate, csar);

            LOG.debug("Generated the following abstract prov plan: ");
            LOG.debug(buildPlan.toString());

            final BPMNPlan newBuildPlan =
                this.planHandler.createEmptyBPMNPlan(processNamespace, processName, buildPlan, "initiate", csar);

            newBuildPlan.setTOSCAInterfaceName("OpenTOSCA-Lifecycle-Interface");
            newBuildPlan.setTOSCAOperationName("initiate");

            this.planHandler.initializeBPMNSkeleton(newBuildPlan, csar);

            // TODO: implement propertyInitializer
            final Property2VariableMapping propMap = null;
            //final Property2VariableMapping propMap =
            //    this.propertyInitializer.initializePropertiesAsVariables(newBuildPlan, serviceTemplate);

            // instanceDataAPI handling is done solely trough this extension

            // initialize instanceData handling
            this.serviceInstanceInitializer.appendCreateServiceInstanceVarsAndAnitializeWithInstanceDataAPI(newBuildPlan);

            String serviceInstanceUrl =
                this.serviceInstanceInitializer.findServiceInstanceUrlVariableName(newBuildPlan);
            String serviceInstanceID = this.serviceInstanceInitializer.findServiceInstanceIdVarName(newBuildPlan);
            String serviceTemplateUrl =
                this.serviceInstanceInitializer.findServiceTemplateUrlVariableName(newBuildPlan);
            String planInstanceUrl = this.serviceInstanceInitializer.findPlanInstanceUrlVariableName(newBuildPlan);

            this.runPlugins(newBuildPlan, propMap, serviceInstanceUrl, serviceInstanceID, serviceTemplateUrl, planInstanceUrl, csar);

            // only generate diagram when all elements are instantiated
            this.planHandler.generateBPMNDiagram(newBuildPlan);
            // newBuildPlan.setCsarName(csarName);

            try {
                this.bpmnFinalizer.finalize(newBuildPlan);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }
            writeXML(newBuildPlan);
            return newBuildPlan;
        }

        LOG.warn("Couldn't create BuildPlan for ServiceTemplate {} in Definitions {} of CSAR {}",
            serviceTemplateQname.toString(), definitions.getId(), csar.id().csarName());
        return null;
    }

    // TODO: remove from normal flow, this is debugging only
    public void writeXML(BPMNPlan newBuildPlan) {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(newBuildPlan.getBpmnDocument());
            StreamResult result = new StreamResult(new File("test-bpmn.xml"));
            transformer.transform(source, result);
            // Output to console for testing
            StreamResult consoleResult = new StreamResult(System.out);
            transformer.transform(source, consoleResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * <P>
     *     This method assigns plugins to the already initialized BuildPlan and its BPMN Activity (TemplateBuildPlans).
     *     The assigned plugins fulfill the detail of each activity with mainly groovy script selection and corresponding input/output paramter
     *     The method refers to the similar method in BPELBuildProcessBuilder.java
     * </P>
     * @param buildPlan
     * @param map
     * @param serviceInstanceUrl
     * @param serviceInstanceID
     * @param serviceTemplateUrl
     * @param planInstanceUrl
     * @param csar
     */
    private void runPlugins(final BPMNPlan buildPlan, final Property2VariableMapping map,
                            final String serviceInstanceUrl, final String serviceInstanceID,
                            final String serviceTemplateUrl, final String planInstanceUrl, final Csar csar) {
        LOG.debug("Running plugins for each bpmnScope");
        // iterating through all BPMNScope to find matching plugin
        for (final BPMNScope bpmnScope : buildPlan.getTemplateBuildPlans()) {
            final BPMNPlanContext context = new BPMNPlanContext(buildPlan, bpmnScope, map, buildPlan.getServiceTemplate(),
                serviceInstanceUrl, serviceInstanceID, serviceTemplateUrl, planInstanceUrl, csar);
            LOG.debug("Running plugins for BPMN activity {}", bpmnScope.getActivity());
            if (bpmnScope.getNodeTemplate() != null) {
                final TNodeTemplate nodeTemplate = bpmnScope.getNodeTemplate();
                LOG.debug("Running plugins for node template {}", nodeTemplate);
                // if this nodeTemplate has the label running (Property: State=Running), skip
                // provisioning and just generate instance data handling
                // extended check for OperatingSystem node type
                // TODO: DockerEngine is labeled as running, need to review
                /*
                if (isRunning(nodeTemplate)
                    || ModelUtils.findNodeType(nodeTemplate, csar).getName().equals(Types.abstractOperatingSystemNodeType.getLocalPart())) {
                    LOG.debug("Skipping the provisioning of NodeTemplate "
                        + bpmnScope.getNodeTemplate().getId() + "  because state=running is set.");
                    continue;
                }
                */
                // generate detail for activity
                this.bpmnPluginHandler.handleActivity(context, bpmnScope, nodeTemplate);
            } else if (bpmnScope.getRelationshipTemplate() != null) {
                final TRelationshipTemplate relationshipTemplate = bpmnScope.getRelationshipTemplate();
                // handling relationshiptemplate

                this.bpmnPluginHandler.handleActivity(context, bpmnScope, relationshipTemplate);
            }
        }
    }
}
