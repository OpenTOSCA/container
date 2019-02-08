package org.opentosca.planbuilder.core.bpel;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.container.core.tosca.convention.Interfaces;
import org.opentosca.planbuilder.AbstractFreezePlanBuilder;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.bpel.fragments.BPELProcessFragments;
import org.opentosca.planbuilder.core.bpel.handlers.BPELPlanHandler;
import org.opentosca.planbuilder.core.bpel.helpers.BPELFinalizer;
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
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.opentosca.planbuilder.plugins.context.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * @author Jan Ruthardt - st107755@stud.uni-stuttgart.de
 *
 */
public class BPELFreezeProcessBuilder extends AbstractFreezePlanBuilder {

    private final static Logger LOG = LoggerFactory.getLogger(BPELFreezeProcessBuilder.class);

    // handler for abstract buildplan operations
    public BPELPlanHandler planHandler;

    // class for initializing properties inside the build plan
    private final PropertyVariableInitializer propertyInitializer;
    // adds serviceInstance Variable and instanceDataAPIUrl to buildPlans
    private ServiceInstanceVariablesHandler serviceInstanceVarsHandler;
    // adds nodeInstanceIDs to each templatePlan
    private NodeRelationInstanceVariablesHandler instanceVarsHandler;
    // class for finalizing build plans (e.g when some template didn't receive
    // some provisioning logic and they must be filled with empty elements)
    private final BPELFinalizer finalizer;

    private BPELProcessFragments bpelFragments;

    // accepted operations for provisioning
    private final List<String> opNames = new ArrayList<>();
    
    

    /**
     * <p>
     * Default Constructor
     * </p>
     */
    public BPELFreezeProcessBuilder() {
        try {
            this.planHandler = new BPELPlanHandler();
            this.serviceInstanceVarsHandler = new ServiceInstanceVariablesHandler();
            this.instanceVarsHandler = new NodeRelationInstanceVariablesHandler(this.planHandler);
            this.bpelFragments = new BPELProcessFragments();
        }
        catch (final ParserConfigurationException e) {
            BPELFreezeProcessBuilder.LOG.error("Error while initializing BuildPlanHandler", e);
        }
        this.propertyInitializer = new PropertyVariableInitializer(this.planHandler);
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
                              final QName serviceTemplateId) {
        BPELFreezeProcessBuilder.LOG.info("Making Plans");

        for (final AbstractServiceTemplate serviceTemplate : definitions.getServiceTemplates()) {
            String namespace;
            if (serviceTemplate.getTargetNamespace() != null) {
                namespace = serviceTemplate.getTargetNamespace();
            } else {
                namespace = definitions.getTargetNamespace();
            }

            final String processName = ModelUtils.makeValidNCName(serviceTemplate.getId() + "_freezePlan");
            final String processNamespace = serviceTemplate.getTargetNamespace() + "_freezePlan";

            // we take the overall flow of an termination plan, basically with the goal of saving state from
            // the top to the bottom
            final AbstractPlan newAbstractBackupPlan =
                generateFOG(new QName(processNamespace, processName).toString(), definitions, serviceTemplate);

            newAbstractBackupPlan.setType(PlanType.MANAGE);
            final BPELPlan newFreezePlan =
                this.planHandler.createEmptyBPELPlan(processNamespace, processName, newAbstractBackupPlan, "freeze");

            this.planHandler.initializeBPELSkeleton(newFreezePlan, csarName);
            
            

            this.instanceVarsHandler.addInstanceURLVarToTemplatePlans(newFreezePlan);
            this.instanceVarsHandler.addInstanceIDVarToTemplatePlans(newFreezePlan);

            final PropertyMap propMap = this.propertyInitializer.initializePropertiesAsVariables(newFreezePlan);

            // instanceDataAPI handling is done solely trough this extension
            this.planHandler.registerExtension("http://www.apache.org/ode/bpel/extensions/bpel4restlight", true,
                                               newFreezePlan);

            // initialize instanceData handling, add
            // instanceDataAPI/serviceInstanceID into input, add global
            // variables to hold the value for plugins
            this.serviceInstanceVarsHandler.addManagementPlanServiceInstanceVarHandlingFromInput(newFreezePlan);
            this.serviceInstanceVarsHandler.initPropertyVariablesFromInstanceData(newFreezePlan, propMap);

            // fetch all nodeinstances that are running
            this.instanceVarsHandler.addNodeInstanceFindLogic(newFreezePlan,
                                                              "?state=STARTED&amp;state=CREATED&amp;state=CONFIGURED");
            this.instanceVarsHandler.addPropertyVariableUpdateBasedOnNodeInstanceID(newFreezePlan, propMap);

            try {
                this.appendGenerateStatefulServiceTemplateLogic(newFreezePlan);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            catch (SAXException e) {
                e.printStackTrace();
            }

            final List<BPELScopeActivity> changedActivities = runPlugins(newFreezePlan, propMap);

            this.serviceInstanceVarsHandler.addCorrellationID(newFreezePlan);

            this.finalizer.finalize(newFreezePlan);

            // add for each loop over found node instances to terminate each running
            // instance
            /*
             * for (final BPELScopeActivity activ : changedActivities) { if (activ.getNodeTemplate() != null) {
             * final BPELPlanContext context = new BPELPlanContext(activ, propMap,
             * newTerminationPlan.getServiceTemplate());
             * this.instanceVarsHandler.appendCountInstancesLogic(context, activ.getNodeTemplate(),
             * "?state=STARTED&amp;state=CREATED&amp;state=CONFIGURED"); } }
             */

            BPELFreezeProcessBuilder.LOG.debug("Created Plan:");
            BPELFreezeProcessBuilder.LOG.debug(ModelUtils.getStringFromDoc(newFreezePlan.getBpelDocument()));

            return newFreezePlan;
        }

        BPELFreezeProcessBuilder.LOG.warn("Couldn't create FreezePlan for ServiceTemplate {} in Definitions {} of CSAR {}",
                                          serviceTemplateId.toString(), definitions.getId(), csarName);
        return null;
    }

    @Override
    public List<AbstractPlan> buildPlans(final String csarName, final AbstractDefinitions definitions) {
        BPELFreezeProcessBuilder.LOG.info("Builing the Plans");
        final List<AbstractPlan> plans = new ArrayList<>();
        for (final AbstractServiceTemplate serviceTemplate : definitions.getServiceTemplates()) {
            QName serviceTemplateId;
            // targetNamespace attribute doesn't has to be set, so we check it
            if (serviceTemplate.getTargetNamespace() != null) {
                serviceTemplateId = new QName(serviceTemplate.getTargetNamespace(), serviceTemplate.getId());
            } else {
                serviceTemplateId = new QName(definitions.getTargetNamespace(), serviceTemplate.getId());
            }

            BPELFreezeProcessBuilder.LOG.debug("ServiceTemplate {} has no BackupPlan, generating BackuopPlan",
                                               serviceTemplateId.toString());
            final BPELPlan newBuildPlan = buildPlan(csarName, definitions, serviceTemplateId);

            if (newBuildPlan != null) {
                BPELFreezeProcessBuilder.LOG.debug("Created BackupPlan "
                    + newBuildPlan.getBpelProcessElement().getAttribute("name"));
                plans.add(newBuildPlan);
            }

        }
        return plans;
    }
    
    private boolean isStateful(AbstractNodeTemplate nodeTemplate) {
        return this.hasSaveStateInterface(nodeTemplate) && this.hasStatefulComponentPolicy(nodeTemplate);
    }

    private boolean hasSaveStateInterface(AbstractNodeTemplate nodeTemplate) {
        AbstractOperation op = this.getSaveStateOperation(nodeTemplate);
        if (op != null) {
            if (this.getSaveStateParameter(op) != null) {
                return true;
            }
        }
        return false;
    }

    private AbstractInterface getSaveStateInterface(AbstractNodeTemplate nodeTemplate) {
        for (AbstractInterface iface : nodeTemplate.getType().getInterfaces()) {
            if (!iface.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE)) {
                continue;
            }

            return iface;
        }
        return null;
    }

    private AbstractOperation getSaveStateOperation(AbstractNodeTemplate nodeTemplate) {
        AbstractInterface iface = this.getSaveStateInterface(nodeTemplate);
        if (iface != null) {
            for (AbstractOperation op : iface.getOperations()) {
                if (!op.getName().equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE_FREEZE)) {
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
    
 

    private void appendGenerateStatefulServiceTemplateLogic(BPELPlan plan) throws IOException, SAXException {
        QName serviceTemplateId = plan.getServiceTemplate().getQName();

        this.planHandler.addStringElementToPlanRequest(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE_FREEZE_MANDATORY_PARAM_ENDPOINT, plan);

        // var to save serviceTemplate url on storage service
        String statefulServiceTemplateVarName =
            this.planHandler.addGlobalStringVariable("statefulServiceTemplateUrl" + System.currentTimeMillis(), plan);
        String responseVarName = this.planHandler.createAnyTypeVar(plan);

       
        // assign variable with the original service template url
        Node assignStatefuleServiceTemplateStorageVar =
            this.bpelFragments.createAssignVarToVarWithXpathQueryAsNode("assignServiceTemplateStorageUrl"
                + System.currentTimeMillis(), "input", statefulServiceTemplateVarName,
                                                                        "concat(//*[local-name()='"
                                                                            + Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE_FREEZE_MANDATORY_PARAM_ENDPOINT
                                                                            + "']/text(),'/servicetemplates/"
                                                                            + URLEncoder.encode(URLEncoder.encode(serviceTemplateId.getNamespaceURI(),
                                                                                                                  "UTF-8"),
                                                                                                "UTF-8")
                                                                            + "','/" + serviceTemplateId.getLocalPart()
                                                                            + "','/createnewstatefulversion')");
        assignStatefuleServiceTemplateStorageVar =
            plan.getBpelDocument().importNode(assignStatefuleServiceTemplateStorageVar, true);                
        plan.getBpelMainSequenceElement().insertBefore(assignStatefuleServiceTemplateStorageVar, plan.getBpelMainSequencePropertyAssignElement());        


        // create append POST for creating a stateful service template version
        Node createStatefulServiceTemplatePOST =
            this.bpelFragments.createHTTPPOST(statefulServiceTemplateVarName, responseVarName);

        createStatefulServiceTemplatePOST = plan.getBpelDocument().importNode(createStatefulServiceTemplatePOST, true);
        
        plan.getBpelMainSequenceElement().insertBefore(createStatefulServiceTemplatePOST, plan.getBpelMainSequencePropertyAssignElement());        


        // read response and assign url of created stateful service template
        // query the localname from the response
        String xpathQuery1 = "concat(substring-before($" + statefulServiceTemplateVarName + ",'"
            + serviceTemplateId.getLocalPart() + "'),encode-for-uri(encode-for-uri(//*[local-name()='QName']/*[local-name()='localname']/text())))";
        // query original service template url without the last path fragment(/service template localname)
        String xpathQuery2 = "string($" + statefulServiceTemplateVarName + ")";
        Node assignCreatedStatefulServiceTemplate =
            this.bpelFragments.createAssignVarToVarWithXpathQueriesAsNode("assignCreatedStatefuleServiceTemplateUrl",
                                                                          responseVarName, null,
                                                                          statefulServiceTemplateVarName, null,
                                                                          xpathQuery1, xpathQuery2,
                                                                          "change the url from original service template to stateful",
                                                                          null);

        assignCreatedStatefulServiceTemplate =
            plan.getBpelDocument().importNode(assignCreatedStatefulServiceTemplate, true);
        plan.getBpelMainSequenceElement().insertBefore(assignCreatedStatefulServiceTemplate, plan.getBpelMainSequencePropertyAssignElement());        
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

                boolean alreadyHandled = false;
                AbstractNodeTemplate nodeTemplate = templatePlan.getNodeTemplate();

                
                // TODO add termination logic
                
                /*
                 * generic save state code
                 */
                if (this.isStateful(nodeTemplate)) {

                    String saveStateUrlVarName =
                        this.planHandler.addGlobalStringVariable("nodeTemplateStateSaveURL", plan);

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

                    inputs.put(this.getSaveStateParameter(this.getSaveStateOperation(nodeTemplate)), saveStateUrlVar);

                    alreadyHandled =
                        context.executeOperation(nodeTemplate, Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE,
                                                 Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE_FREEZE, inputs);
                }

                /*
                 * Legacy Code for specific docker containers:
                 */
                // Only looks at Nodes that are no docker engine
                if (!alreadyHandled
                    && !org.opentosca.container.core.tosca.convention.Utils.isSupportedDockerEngineNodeType(templatePlan.getNodeTemplate()
                                                                                                                        .getType()
                                                                                                                        .getId())) {

                    final List<AbstractNodeTemplate> nodes = new ArrayList<>();
                    ModelUtils.getNodesFromNodeToSink(context.getNodeTemplate(), nodes);
                    for (final AbstractNodeTemplate node : nodes) {

                        // I've got no idea what is going on down here...
                        List<AbstractNodeTypeImplementation> IA = node.getImplementations();
                        Iterator<AbstractNodeTypeImplementation> iai = IA.iterator();
                        while (iai.hasNext()) {
                            List<AbstractImplementationArtifact> Ias = iai.next().getImplementationArtifacts();
                            Iterator<AbstractImplementationArtifact> iasi = Ias.iterator();

                            while (iasi.hasNext()) {
                                String methods = iasi.next().getName();
                                //
                                if (methods.contains(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER_Defreeze)) {
                                    boolean gotExecuted =
                                        context.executeOperation(node,
                                                                 Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER_Backup,
                                                                 Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER_Freeze,
                                                                 null);
                                    if (gotExecuted) {
                                        BPELFreezeProcessBuilder.LOG.debug("Freeze Plan created");
                                    } else {
                                        BPELFreezeProcessBuilder.LOG.debug("Freeze Plan creation failed");
                                    }
                                    BPELFreezeProcessBuilder.LOG.info("" + String.valueOf(gotExecuted));
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
}
