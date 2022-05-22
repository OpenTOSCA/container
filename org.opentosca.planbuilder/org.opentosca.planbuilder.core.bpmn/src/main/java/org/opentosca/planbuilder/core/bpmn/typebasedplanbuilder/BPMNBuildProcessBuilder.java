package org.opentosca.planbuilder.core.bpmn.typebasedplanbuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.opentosca.planbuilder.core.bpmn.fragments.BPMNProcessFragments;
import org.opentosca.planbuilder.core.bpmn.context.BPMNPlanContext;
import org.opentosca.planbuilder.core.bpmn.handlers.BPMNFinalizer;
import org.opentosca.planbuilder.core.bpmn.handlers.BPMNPlanHandler;
import org.opentosca.planbuilder.core.bpmn.handlers.BPMNPluginHandler;
//import org.opentosca.planbuilder.core.bpmn.handlers.ServiceTemplateBoundaryPropertyMappingsToOutputHandler;
import org.opentosca.planbuilder.core.bpmn.handlers.BPMNPropertyVariableHandler;
import org.opentosca.planbuilder.core.bpmn.handlers.BPMNSubprocessHandler;
import org.opentosca.planbuilder.core.bpmn.handlers.ServiceTemplateBoundaryPropertyMappingsToOutputHandler;
import org.opentosca.planbuilder.core.bpmn.handlers.SimplePlanBuilderBPMNServiceInstanceHandler;
import org.opentosca.planbuilder.core.plugins.context.Property2VariableMapping;
import org.opentosca.planbuilder.core.plugins.registry.PluginRegistry;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderBPMNPostPhasePlugin;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderBPMNPrePhasePlugin;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNPlan;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNSubprocess;
import org.opentosca.planbuilder.model.plan.bpmn.BPMNSubprocessType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * This class is responsible for generating the Build Plan Skeleton and assign plugins to handle the different templates inside a
 * TopologyTemplate.
 *
 * <p>
 * Copyright 2022 IAAS University of Stuttgart <br>
 * <br>
 */
public class BPMNBuildProcessBuilder extends AbstractBuildPlanBuilder {

    final static Logger LOG = LoggerFactory.getLogger(BPMNBuildProcessBuilder.class);

    private BPMNPlanHandler planHandler;
    private BPMNProcessFragments processFragments;
    private BPMNPluginHandler bpmnPluginHandler;
    private SimplePlanBuilderBPMNServiceInstanceHandler serviceInstanceInitializer;

    private BPMNPropertyVariableHandler propertyInitializer;
    // class for initializing output with boundarydefinitions of a
    // serviceTemplate
    private ServiceTemplateBoundaryPropertyMappingsToOutputHandler propertyOutputInitializer;

    private BPMNFinalizer bpmnFinalizer;
    private BPMNSubprocessHandler bpmnSubprocessHandler;

    /**
     * Default Constructor
     */
    public BPMNBuildProcessBuilder(PluginRegistry pluginRegistry) {
        super(pluginRegistry);
        try {
            this.bpmnPluginHandler = new BPMNPluginHandler(pluginRegistry);
            this.planHandler = new BPMNPlanHandler();
            this.processFragments = new BPMNProcessFragments();
            this.bpmnFinalizer = new BPMNFinalizer();
            this.bpmnSubprocessHandler = new BPMNSubprocessHandler();
            this.propertyOutputInitializer = new ServiceTemplateBoundaryPropertyMappingsToOutputHandler();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        this.propertyInitializer = new BPMNPropertyVariableHandler(this.planHandler);
        //this.propertyOutputInitializer = new ServiceTemplateBoundaryPropertyMappingsToOutputHandler();
        try {
            this.serviceInstanceInitializer = new SimplePlanBuilderBPMNServiceInstanceHandler();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<AbstractPlan> buildPlans(Csar csar, TDefinitions definitions) {
        final List<AbstractPlan> plans = new ArrayList<>();
        for (final TServiceTemplate serviceTemplate : definitions.getServiceTemplates()) {
            final BPMNPlan newBuildPlan = buildPlan(csar, definitions, serviceTemplate);
            plans.add(newBuildPlan);
            return plans;
        }
        return null;
    }

    private BPMNPlan buildPlan(final Csar csar, final TDefinitions definitions,
                               final TServiceTemplate serviceTemplate) {

        LOG.info("Start of generating BPMN Build Plan for {}", csar.id());
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

            final String processName = ModelUtils.makeValidNCName(serviceTemplate.getId() + "_buildPlan2");
            final String processNamespace = serviceTemplate.getTargetNamespace() + "_buildPlan2";

            AbstractPlan buildPlan =
                AbstractBuildPlanBuilder.generatePOG(new QName(processNamespace, processName).toString(), definitions, serviceTemplate, csar);

            LOG.debug("Generated the following abstract prov plan: ");
            LOG.debug(buildPlan.toString());

            ArrayList<String> inputParameters = this.bpmnSubprocessHandler.computeInputParametersBasedTopology(serviceTemplate.getTopologyTemplate());
            final BPMNPlan bpmnPlan =
                this.planHandler.createEmptyBPMNPlan(processNamespace, processName, buildPlan, "initiate");

            bpmnPlan.setCsarName(csar.id().csarName());
            bpmnPlan.setInputParameters(inputParameters);
            //BPMNDataObject dataObjectInput = new BPMNDataObject(BPMNSubprocessType.DATA_OBJECT_INOUT, "InputOutput_DataObject");
            //dataObjectInput.setProperties(inputParameters);
            //bpmnPlan.getDataObjectsList().add(dataObjectInput);

            // this step can be skipped in management plans
            this.serviceInstanceInitializer.appendCreateServiceInstanceVarsAndInitializeWithInstanceDataAPI(bpmnPlan);
            BPMNSubprocess dataObjectSubprocess = this.serviceInstanceInitializer.addServiceInstanceHandlingFromInput(bpmnPlan);
            bpmnPlan.setTOSCAInterfaceName("OpenTOSCA-Lifecycle-Interface");
            bpmnPlan.setTOSCAOperationname("initiate");
            this.planHandler.initializeBPMNSkeleton(bpmnPlan, csar);
            this.planHandler.addActivateDataObjectTaskToSubprocess(dataObjectSubprocess, bpmnPlan);
            String serviceInstanceUrl = this.serviceInstanceInitializer.findServiceInstanceUrlVariableName(bpmnPlan);
            String serviceInstanceID = this.serviceInstanceInitializer.findServiceInstanceIdVarName(bpmnPlan);
            String serviceTemplateUrl = this.serviceInstanceInitializer.findServiceTemplateUrlVariableName(bpmnPlan);
            LOG.info("Variablen fuer Plugin");
            LOG.info(serviceInstanceUrl);
            LOG.info(serviceInstanceID);
            LOG.info(serviceTemplateUrl);

            Property2VariableMapping propMap;
            propMap = this.propertyInitializer.initializePropertiesAsVariables(bpmnPlan, serviceTemplate);
            // init output
            HashMap<String, String> propertyOutput = this.propertyOutputInitializer.initializeBuildPlanOutput(definitions, bpmnPlan, propMap,
                serviceTemplate);

            propertyOutput.put("CorrelationID", "CorrelationID");
            bpmnPlan.setOutputParameters(propertyOutput);

            this.runPlugins(bpmnPlan, propMap, serviceInstanceUrl, serviceInstanceID, serviceTemplateUrl, csar);
            //writeXML(bpmnPlan.getBpmnDocument());
            try {
                bpmnFinalizer.finalize(bpmnPlan);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }
            writeXML(bpmnPlan.getBpmnDocument());

            return bpmnPlan;
        }

        LOG.warn("Couldn't create BuildPlan for ServiceTemplate {} in Definitions {} of CSAR {}",
            serviceTemplateQname, definitions.getId(), csar.id().csarName());
        return null;
    }

    public void writeXML(Document s) {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(s);
            StreamResult result = new StreamResult(new File("C://Users//livia//Downloads//result.xml"));
            transformer.transform(source, result);
            // Output to console for testing
            StreamResult consoleResult = new StreamResult(System.out);
            transformer.transform(source, consoleResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * <p>
     * This method assigns plugins to the already initialized BuildPlan and its TemplateBuildPlans. First there will be
     * checked if any generic plugin can handle a template of the TopologyTemplate
     * </p>
     *
     * @param buildPlan a BuildPlan which is already initialized
     * @param map       a PropertyMap which contains mappings from Template to Property and to variable name of inside
     *                  the BuildPlan
     */
    private void runPlugins(final BPMNPlan buildPlan, final Property2VariableMapping map,
                            final String serviceInstanceUrl, final String serviceInstanceID,
                            final String serviceTemplateUrl, final Csar csar) {
        LOG.info("Plugins laufen jetzt");
        for (final BPMNSubprocess bpmnSubprocess : buildPlan.getTemplateBuildPlans()) {
            LOG.info("Plugins gestartet, templatebuildplan id:");
            LOG.info(bpmnSubprocess.getId());
            final BPMNPlanContext context = new BPMNPlanContext(buildPlan, bpmnSubprocess, map, buildPlan.getServiceTemplate(),
                serviceInstanceUrl, serviceInstanceID, serviceTemplateUrl, csar);
            if (bpmnSubprocess.getNodeTemplate() != null) {
                final TNodeTemplate nodeTemplate = bpmnSubprocess.getNodeTemplate();
                // if this nodeTemplate has the label running (Property: State=Running), skip
                // provisioning and just generate instance data handling
                // extended check for OperatingSystem node type
                if (isRunning(nodeTemplate)
                    /*|| ModelUtils.findNodeType(nodeTemplate, csar).getName().equals(Types.abstractOperatingSystemNodeType.getLocalPart())*/) {
                    LOG.info("Skipping the provisioning of NodeTemplate "
                        + bpmnSubprocess.getNodeTemplate().getId() + "  because state=running is set.");
                    for (final IPlanBuilderBPMNPrePhasePlugin prePhasePlugin : this.pluginRegistry.getPreBPMNPlugins()) {
                        if (prePhasePlugin.canHandleCreate(context, bpmnSubprocess.getNodeTemplate())) {
                            prePhasePlugin.handleCreate(context, bpmnSubprocess.getNodeTemplate());
                        }
                    }
                    String prefix = BPMNSubprocessType.SET_ST_STATE.name();
                    BPMNSubprocess setStateTask = new BPMNSubprocess(BPMNSubprocessType.SET_ST_STATE, prefix + "_" + buildPlan.getIdForNamesAndIncrement());
                    //BPMNSubprocess setStateTask = bpmnSubprocessHandler.createBPMNSubprocessWithinSubprocess(bpmnSubprocess, BPMNSubprocessType.SET_ST_STATE);
                    setStateTask.setNodeTemplate(nodeTemplate);
                    setStateTask.setInstanceState("STARTED");
                    setStateTask.setBuildPlan(buildPlan);
                    bpmnSubprocess.addTaskToSubproces(setStateTask);
                    for (final IPlanBuilderBPMNPostPhasePlugin postPhasePlugin : this.pluginRegistry.getPostBPMNPlugins()) {
                        if (postPhasePlugin.canHandleCreate(context, bpmnSubprocess.getNodeTemplate())) {
                            postPhasePlugin.handleCreate(context, bpmnSubprocess.getNodeTemplate());
                        }
                    }
                    continue;
                }
                // generate code for the activity
                this.bpmnPluginHandler.handleActivity(context, bpmnSubprocess, nodeTemplate);
            } else if (bpmnSubprocess.getRelationshipTemplate() != null) {
                // handling relationshiptemplate
                LOG.info("Handling of relationshiptemplate");
                final TRelationshipTemplate relationshipTemplate = bpmnSubprocess.getRelationshipTemplate();
                this.bpmnPluginHandler.handleActivity(context, bpmnSubprocess, relationshipTemplate);
            } else if (bpmnSubprocess.getServiceInstanceURL() != null || bpmnSubprocess.getSubprocessBPMNSubprocess().get(0).getSubprocessType() == BPMNSubprocessType.ACTIVATE_DATA_OBJECT_TASK) {
                // nothing happens
            } else {
                this.bpmnPluginHandler.handleActivity(context, bpmnSubprocess);
            }
        }
    }
}
