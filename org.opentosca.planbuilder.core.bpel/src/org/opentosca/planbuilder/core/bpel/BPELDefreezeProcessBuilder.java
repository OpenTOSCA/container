package org.opentosca.planbuilder.core.bpel;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.container.core.tosca.convention.Interfaces;
import org.opentosca.planbuilder.AbstractTerminationPlanBuilder;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.bpel.fragments.BPELProcessFragments;
import org.opentosca.planbuilder.core.bpel.handlers.BPELPlanHandler;
import org.opentosca.planbuilder.core.bpel.helpers.BPELFinalizer;
import org.opentosca.planbuilder.core.bpel.helpers.NodeRelationInstanceVariablesHandler;
import org.opentosca.planbuilder.core.bpel.helpers.PropertyVariableInitializer;
import org.opentosca.planbuilder.core.bpel.helpers.ServiceInstanceVariablesHandler;
import org.opentosca.planbuilder.core.bpel.helpers.PropertyVariableInitializer.PropertyMap;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELScopeActivity;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractImplementationArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractInterface;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTypeImplementation;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.planbuilder.model.tosca.AbstractParameter;
import org.opentosca.planbuilder.model.tosca.AbstractPolicy;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.opentosca.planbuilder.plugins.context.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class BPELDefreezeProcessBuilder extends AbstractTerminationPlanBuilder {

    private final static Logger LOG = LoggerFactory.getLogger(BPELDefreezeProcessBuilder.class);

    // handler for abstract buildplan operations
    private BPELPlanHandler planHandler;

    // class for initializing properties inside the build plan
    private final PropertyVariableInitializer propertyInitializer;
    // adds serviceInstance Variable and instanceDataAPIUrl to buildPlans
    private ServiceInstanceVariablesHandler serviceInstanceVarsHandler;
    // adds nodeInstanceIDs to each templatePlan
    private NodeRelationInstanceVariablesHandler instanceVarsHandler;
    // class for finalizing build plans (e.g when some template didn't receive
    // some provisioning logic and they must be filled with empty elements)
    private final BPELFinalizer finalizer;
    
    private QName statefulComponentPolicy = new QName("http://opentosca.org/policytypes","StatefulComponent");

    // accepted operations for provisioning
    private final List<String> opNames = new ArrayList<>();

    private BPELProcessFragments bpelFragments;


    public BPELDefreezeProcessBuilder() {
        try {
            this.planHandler = new BPELPlanHandler();
            this.serviceInstanceVarsHandler = new ServiceInstanceVariablesHandler();
            this.instanceVarsHandler = new NodeRelationInstanceVariablesHandler(this.planHandler);
            this.bpelFragments = new BPELProcessFragments();
        }
        catch (final ParserConfigurationException e) {
            BPELDefreezeProcessBuilder.LOG.error("Error while initializing BuildPlanHandler", e);
        }
        this.propertyInitializer = new PropertyVariableInitializer(this.planHandler);
        this.finalizer = new BPELFinalizer();
    }

    @Override
    public BPELPlan buildPlan(final String csarName, final AbstractDefinitions definitions,
                              final QName serviceTemplateId) {
        BPELDefreezeProcessBuilder.LOG.info("Making Concrete Plans");

        for (final AbstractServiceTemplate serviceTemplate : definitions.getServiceTemplates()) {
            String namespace;
            if (serviceTemplate.getTargetNamespace() != null) {
                namespace = serviceTemplate.getTargetNamespace();
            } else {
                namespace = definitions.getTargetNamespace();
            }

            final String processName = ModelUtils.makeValidNCName(serviceTemplate.getId() + "_defreezePlan");
            final String processNamespace = serviceTemplate.getTargetNamespace() + "_defreezePlan";

            final AbstractPlan newAbstractBackupPlan =
                generateTOG(new QName(processNamespace, processName).toString(), definitions, serviceTemplate);

            final BPELPlan newDefreezePlan =
                this.planHandler.createEmptyBPELPlan(processNamespace, processName, newAbstractBackupPlan, "defreeze");


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

            // initialize instanceData handling, add
            // instanceDataAPI/serviceInstanceID into input, add global
            // variables to hold the value for plugins
            this.serviceInstanceVarsHandler.addManagementPlanServiceInstanceVarHandlingFromInput(newDefreezePlan);
            this.serviceInstanceVarsHandler.initPropertyVariablesFromInstanceData(newDefreezePlan, propMap);

            // fetch all nodeinstances that are running
            this.instanceVarsHandler.addNodeInstanceFindLogic(newDefreezePlan,
                                                              "?state=STARTED&amp;state=CREATED&amp;state=CONFIGURED");
            this.instanceVarsHandler.addPropertyVariableUpdateBasedOnNodeInstanceID(newDefreezePlan, propMap);

            final List<BPELScopeActivity> changedActivities = runPlugins(newDefreezePlan, propMap);

            this.serviceInstanceVarsHandler.addCorrellationID(newDefreezePlan);

            this.finalizer.finalize(newDefreezePlan);

            // add for each loop over found node instances to terminate each running
            // instance
            for (final BPELScopeActivity activ : changedActivities) {
                if (activ.getNodeTemplate() != null) {
                    final BPELPlanContext context =
                        new BPELPlanContext(activ, propMap, newDefreezePlan.getServiceTemplate());
                    this.instanceVarsHandler.appendCountInstancesLogic(context, activ.getNodeTemplate(),
                                                                       "?state=STARTED&amp;state=CREATED&amp;state=CONFIGURED");
                }
            }

            BPELDefreezeProcessBuilder.LOG.debug("Created Plan:");
            BPELDefreezeProcessBuilder.LOG.debug(ModelUtils.getStringFromDoc(newDefreezePlan.getBpelDocument()));

            return newDefreezePlan;
        }

        BPELDefreezeProcessBuilder.LOG.warn("Couldn't create DeFreezePlan for ServiceTemplate {} in Definitions {} of CSAR {}",
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
    
    private boolean isStateful(AbstractNodeTemplate nodeTemplate) {
        return this.hasLoadStateInterface(nodeTemplate) && this.hasStatefulComponentPolicy(nodeTemplate);
    }
    
    private boolean hasStatefulComponentPolicy(AbstractNodeTemplate nodeTemplate) {
        for(AbstractPolicy policy : nodeTemplate.getPolicies()) {
            if(policy.getType().getId().equals(this.statefulComponentPolicy)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasLoadStateInterface(AbstractNodeTemplate nodeTemplate) {
        AbstractOperation op = this.getLoadStateOperation(nodeTemplate);
        if (op != null) {
            if (this.getSaveStateParameter(op) != null) {
                return true;
            }
        }
        return false;
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

    private AbstractParameter getSaveStateParameter(AbstractOperation op) {
        for (AbstractParameter param : op.getInputParameters()) {
            if (param.getName()
                     .equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE_FREEZE_MANDATORY_PARAM_ENDPOINT)) {
                return param;
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
            if (templatePlan.getNodeTemplate() != null) {

                // create a context for the node
                final BPELPlanContext context = new BPELPlanContext(templatePlan, propMap, plan.getServiceTemplate());

                AbstractNodeTemplate nodeTemplate = templatePlan.getNodeTemplate();
                boolean alreadyHandled = false;


                if (this.isStateful(nodeTemplate)) {


                    String saveStateUrlVarName =
                        this.planHandler.addGlobalStringVariable("nodeTemplateStateSaveURL" + nodeTemplate.getId(),
                                                                 plan);

                    String xpathQuery = "concat($" + statefulServiceTemplateUrlVarName
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
                    catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    catch (SAXException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Variable saveStateUrlVar = BPELPlanContext.getVariable(saveStateUrlVarName);

                    Map<AbstractParameter, Variable> inputs = new HashMap<AbstractParameter, Variable>();

                    inputs.put(this.getSaveStateParameter(this.getLoadStateOperation(nodeTemplate)), saveStateUrlVar);

                    alreadyHandled =
                        context.executeOperation(nodeTemplate, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE,
                                                 Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE_DEFREEZE, inputs);

                }



                // Only looks at Nodes that are no docker engine
                if (!alreadyHandled
                    && !org.opentosca.container.core.tosca.convention.Utils.isSupportedDockerEngineNodeType(templatePlan.getNodeTemplate()
                                                                                                                        .getType()
                                                                                                                        .getId())) {

                    final OperationChain chain =
                        BPELScopeBuilder.createOperationChain(templatePlan.getNodeTemplate(), this.opNames);
                    if (chain == null) {
                        BPELBuildProcessBuilder.LOG.warn("Couldn't create ProvisioningChain for NodeTemplate {}");
                    } else {
                        BPELBuildProcessBuilder.LOG.debug("Created ProvisioningChain for NodeTemplate {}");
                        Iterator<IANodeTypeImplCandidate> IAIterator = chain.iaCandidates.iterator();

                    }

                    final List<AbstractNodeTemplate> nodes = new ArrayList<>();
                    ModelUtils.getNodesFromNodeToSink(context.getNodeTemplate(), nodes);
                    for (final AbstractNodeTemplate node : nodes) {

                        List<AbstractNodeTypeImplementation> IA = node.getImplementations();
                        Iterator<AbstractNodeTypeImplementation> iai = IA.iterator();
                        while (iai.hasNext()) {
                            List<AbstractImplementationArtifact> Ias = iai.next().getImplementationArtifacts();
                            Iterator<AbstractImplementationArtifact> iasi = Ias.iterator();

                            while (iasi.hasNext()) {
                                String methods = iasi.next().getName();
                                //
                                if (this.containsString(methods,
                                                        Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER_Defreeze)) {
                                    boolean gotExecuted =
                                        context.executeOperation(node,
                                                                 Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER_Backup,
                                                                 Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER_Defreeze,
                                                                 null);
                                    if (gotExecuted) {
                                        BPELDefreezeProcessBuilder.LOG.debug("Defreeze Plan created");
                                    } else {
                                        BPELDefreezeProcessBuilder.LOG.debug("Defreeze Plan creation failed");
                                    }
                                    changedActivities.add(templatePlan);
                                }
                            }
                        }
                    }

                }
            }

        }
        return changedActivities;
    }

    private static boolean containsString(String s, String subString) {
        return s.indexOf(subString) > -1 ? true : false;
    }

    @Override
    public List<AbstractPlan> buildPlans(final String csarName, final AbstractDefinitions definitions) {
        BPELDefreezeProcessBuilder.LOG.info("Builing the Plans");
        final List<AbstractPlan> plans = new ArrayList<>();
        for (final AbstractServiceTemplate serviceTemplate : definitions.getServiceTemplates()) {
            QName serviceTemplateId;
            // targetNamespace attribute doesn't has to be set, so we check it
            if (serviceTemplate.getTargetNamespace() != null) {
                serviceTemplateId = new QName(serviceTemplate.getTargetNamespace(), serviceTemplate.getId());
            } else {
                serviceTemplateId = new QName(definitions.getTargetNamespace(), serviceTemplate.getId());
            }

            BPELDefreezeProcessBuilder.LOG.debug("ServiceTemplate {} has no BackupPlan, generating BackuopPlan",
                                                 serviceTemplateId.toString());
            final BPELPlan newBuildPlan = buildPlan(csarName, definitions, serviceTemplateId);

            if (newBuildPlan != null) {
                BPELDefreezeProcessBuilder.LOG.debug("Created BackupPlan "
                    + newBuildPlan.getBpelProcessElement().getAttribute("name"));
                plans.add(newBuildPlan);
            }

        }
        return plans;
    }

}
