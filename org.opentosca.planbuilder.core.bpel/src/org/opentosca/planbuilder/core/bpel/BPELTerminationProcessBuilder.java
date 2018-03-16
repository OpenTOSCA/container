package org.opentosca.planbuilder.core.bpel;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.Node;

import org.opentosca.container.core.tosca.convention.Interfaces;
import org.opentosca.planbuilder.AbstractTerminationPlanBuilder;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.bpel.handlers.BPELPlanHandler;
import org.opentosca.planbuilder.core.bpel.helpers.BPELFinalizer;
import org.opentosca.planbuilder.core.bpel.helpers.NodeInstanceVariablesHandler;
import org.opentosca.planbuilder.core.bpel.helpers.PropertyVariableInitializer;
import org.opentosca.planbuilder.core.bpel.helpers.PropertyVariableInitializer.PropertyMap;
import org.opentosca.planbuilder.core.bpel.helpers.ServiceInstanceVariablesHandler;
import org.opentosca.planbuilder.core.plugins.IPlanBuilderPostPhasePlugin;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELScopeActivity;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Kálmán Képes - kalman.kepes@iaas.uni-stuttgart.de
 *
 */
public class BPELTerminationProcessBuilder extends AbstractTerminationPlanBuilder {

    private final static Logger LOG = LoggerFactory.getLogger(BPELTerminationProcessBuilder.class);

    // handler for abstract buildplan operations
    private BPELPlanHandler planHandler;

    // class for initializing properties inside the build plan
    private final PropertyVariableInitializer propertyInitializer;
    // adds serviceInstance Variable and instanceDataAPIUrl to buildPlans
    private ServiceInstanceVariablesHandler serviceInstanceVarsHandler;
    // adds nodeInstanceIDs to each templatePlan
    private NodeInstanceVariablesHandler nodeInstanceVarsHandler;
    // class for finalizing build plans (e.g when some template didn't receive
    // some provisioning logic and they must be filled with empty elements)
    private final BPELFinalizer finalizer;

    // accepted operations for provisioning
    private final List<String> opNames = new ArrayList<>();

    /**
     * <p>
     * Default Constructor
     * </p>
     */
    public BPELTerminationProcessBuilder() {
        try {
            this.planHandler = new BPELPlanHandler();
            this.serviceInstanceVarsHandler = new ServiceInstanceVariablesHandler();
            this.nodeInstanceVarsHandler = new NodeInstanceVariablesHandler(this.planHandler);
        }
        catch (final ParserConfigurationException e) {
            BPELTerminationProcessBuilder.LOG.error("Error while initializing BuildPlanHandler", e);
        }
        this.propertyInitializer = new PropertyVariableInitializer(this.planHandler);
        this.finalizer = new BPELFinalizer();
        this.opNames.add("stop");
        this.opNames.add("uninstall");
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
        for (final AbstractServiceTemplate serviceTemplate : definitions.getServiceTemplates()) {
            String namespace;
            if (serviceTemplate.getTargetNamespace() != null) {
                namespace = serviceTemplate.getTargetNamespace();
            } else {
                namespace = definitions.getTargetNamespace();
            }

            if (namespace.equals(serviceTemplateId.getNamespaceURI())
                && serviceTemplate.getId().equals(serviceTemplateId.getLocalPart())) {
                final String processName = serviceTemplate.getId() + "_terminationPlan";
                final String processNamespace = serviceTemplate.getTargetNamespace() + "_terminationPlan";

                final AbstractPlan newAbstractTerminationPlan =
                    generateTOG(new QName(processNamespace, processName).toString(), definitions, serviceTemplate);

                final BPELPlan newTerminationPlan =
                    this.planHandler.createEmptyBPELPlan(processNamespace, processName, newAbstractTerminationPlan,
                                                         "terminate");

                newTerminationPlan.setTOSCAInterfaceName("OpenTOSCA-Lifecycle-Interface");
                newTerminationPlan.setTOSCAOperationname("terminate");

                this.planHandler.initializeBPELSkeleton(newTerminationPlan, csarName);

                // create empty templateplans for each template and add them to
                // buildplan
                // for (final AbstractNodeTemplate nodeTemplate :
                // serviceTemplate.getTopologyTemplate().getNodeTemplates()) {
                // final BPELScopeActivity newTemplate =
                // this.templateHandler.createTemplateBuildPlan(nodeTemplate,
                // newTerminationPlan);
                // newTemplate.setNodeTemplate(nodeTemplate);
                // newTerminationPlan.addTemplateBuildPlan(newTemplate);
                // }
                //
                // for (final AbstractRelationshipTemplate relationshipTemplate
                // :
                // serviceTemplate.getTopologyTemplate().getRelationshipTemplates())
                // {
                // final BPELScopeActivity newTemplate =
                // this.templateHandler.createTemplateBuildPlan(relationshipTemplate,
                // newTerminationPlan);
                // newTemplate.setRelationshipTemplate(relationshipTemplate);
                // newTerminationPlan.addTemplateBuildPlan(newTemplate);
                // }Var
                //
                // // connect the templates
                // this.initializeConnectionsInTerminationPlan(newTerminationPlan);

                final PropertyMap propMap =
                    this.propertyInitializer.initializePropertiesAsVariables(newTerminationPlan);

                // instanceDataAPI handling is done solely trough this extension
                this.planHandler.registerExtension("http://iaas.uni-stuttgart.de/bpel/extensions/bpel4restlight", true,
                                                   newTerminationPlan);

                // initialize instanceData handling, add
                // instanceDataAPI/serviceInstanceID into input, add global
                // variables to hold the value for plugins
                this.serviceInstanceVarsHandler.addManagementPlanServiceInstanceVarHandlingFromInput(newTerminationPlan);
                this.serviceInstanceVarsHandler.initPropertyVariablesFromInstanceData(newTerminationPlan, propMap);

                this.nodeInstanceVarsHandler.addNodeInstanceFindLogic(newTerminationPlan,
                                                                      "?state=STARTED&amp;state=CREATED&amp;state=CONFIGURED");
                this.nodeInstanceVarsHandler.addPropertyVariableUpdateBasedOnNodeInstanceID(newTerminationPlan,
                                                                                            propMap);

                // TODO Create a for loop over the three sequences inside the
                // flow to iterate for the instance count deleting one instance
                // at a time

                final List<BPELScopeActivity> changedActivities = runPlugins(newTerminationPlan, propMap);

                this.serviceInstanceVarsHandler.appendSetServiceInstanceState(newTerminationPlan,
                                                                              newTerminationPlan.getBpelMainSequenceOutputAssignElement(),
                                                                              "DELETED");

                this.serviceInstanceVarsHandler.addCorrellationID(newTerminationPlan);

                this.finalizer.finalize(newTerminationPlan);

                for (final BPELScopeActivity activ : changedActivities) {
                    if (activ.getNodeTemplate() != null) {
                        final BPELPlanContext context =
                            new BPELPlanContext(activ, propMap, newTerminationPlan.getServiceTemplate());
                        this.nodeInstanceVarsHandler.appendCountInstancesLogic(context, activ.getNodeTemplate(),
                                                                               "?state=STARTED&amp;state=CREATED&amp;state=CONFIGURED");
                    }
                }
                // TODO we need to wrap the pre-, prov- and post-phase sequences
                // into a forEach activity that iterates over all nodeInstances
                // of a given nodeTemplate. This allows us to generate code for
                // a single nodeInstance which can then be used for all
                // nodeInstances by using the same code on each instance

                // add logic at the end of the process to DELETE the
                // serviceInstance with the instanceDataAPI



                BPELTerminationProcessBuilder.LOG.debug("Created TerminationPlan:");
                BPELTerminationProcessBuilder.LOG.debug(ModelUtils.getStringFromDoc(newTerminationPlan.getBpelDocument()));

                return newTerminationPlan;
            }
        }

        BPELTerminationProcessBuilder.LOG.warn("Couldn't create BuildPlan for ServiceTemplate {} in Definitions {} of CSAR {}",
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
                BPELTerminationProcessBuilder.LOG.debug("ServiceTemplate {} has no TerminationPlan, generating TerminationPlan",
                                                        serviceTemplateId.toString());
                final BPELPlan newBuildPlan = buildPlan(csarName, definitions, serviceTemplateId);

                if (newBuildPlan != null) {
                    BPELTerminationProcessBuilder.LOG.debug("Created TerminationPlan "
                        + newBuildPlan.getBpelProcessElement().getAttribute("name"));
                    plans.add(newBuildPlan);
                }
            } else {
                BPELTerminationProcessBuilder.LOG.debug("ServiceTemplate {} has TerminationPlan, no generation needed",
                                                        serviceTemplateId.toString());
            }
        }
        return plans;
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
            } else if (childNodeList.item(index).getLocalName().equals("ContainerImage")) {
                foundDockerImageProp = true;
            }
        }

        if (check != 2) {
            return false;
        }
        return true;
    }

    /**
     * This method will execute plugins on each TemplatePlan inside the given plan for termination of
     * each node and relation.
     *
     * @param plan the plan to execute the plugins on
     * @param serviceTemplate the serviceTemplate the plan belongs to
     * @param propMap a PropertyMapping from NodeTemplate to Properties to BPELVariables
     */
    private List<BPELScopeActivity> runPlugins(final BPELPlan plan, final PropertyMap propMap) {

        final List<BPELScopeActivity> changedActivities = new ArrayList<>();
        /*
         * TODO/FIXME until we decided whether we allow type plugins that achieve termination, we just
         * terminate each VM and Docker Container we can find
         */
        for (final BPELScopeActivity templatePlan : plan.getTemplateBuildPlans()) {
            // we handle only nodeTemplates..
            if (templatePlan.getNodeTemplate() != null) {
                // .. that are VM nodeTypes
                if (org.opentosca.container.core.tosca.convention.Utils.isSupportedVMNodeType(templatePlan.getNodeTemplate()
                                                                                                          .getType()
                                                                                                          .getId())) {
                    // create context for the templatePlan
                    final BPELPlanContext context =
                        new BPELPlanContext(templatePlan, propMap, plan.getServiceTemplate());
                    // fetch infrastructure node (cloud provider)
                    final List<AbstractNodeTemplate> infraNodes = context.getInfrastructureNodes();
                    for (final AbstractNodeTemplate infraNode : infraNodes) {
                        if (org.opentosca.container.core.tosca.convention.Utils.isSupportedCloudProviderNodeType(infraNode.getType()
                                                                                                                          .getId())) {
                            // append logic to call terminateVM method on the
                            // node

                            context.executeOperation(infraNode,
                                                     org.opentosca.container.core.tosca.convention.Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_CLOUDPROVIDER,
                                                     org.opentosca.container.core.tosca.convention.Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_CLOUDPROVIDER_TERMINATEVM,
                                                     null);

                            changedActivities.add(templatePlan);
                        }
                    }

                } else {
                    // check whether this node is a docker container
                    final BPELPlanContext context =
                        new BPELPlanContext(templatePlan, propMap, plan.getServiceTemplate());

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

                final AbstractNodeTemplate nodeTemplate = templatePlan.getNodeTemplate();
                BPELTerminationProcessBuilder.LOG.debug("Trying to handle NodeTemplate " + nodeTemplate.getId());
                final BPELPlanContext context = new BPELPlanContext(templatePlan, propMap, plan.getServiceTemplate());

                for (final IPlanBuilderPostPhasePlugin postPhasePlugin : this.pluginRegistry.getPostPlugins()) {
                    if (postPhasePlugin.canHandle(nodeTemplate)) {
                        postPhasePlugin.handle(context, nodeTemplate);
                    }
                }
            }

        }
        return changedActivities;
    }

}
