package org.opentosca.planbuilder.core.bpel.typebasedplanbuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.opentosca.container.core.tosca.convention.Interfaces;
import org.opentosca.planbuilder.AbstractDefrostPlanBuilder;
import org.opentosca.planbuilder.core.bpel.context.BPELPlanContext;
import org.opentosca.planbuilder.core.bpel.fragments.BPELProcessFragments;
import org.opentosca.planbuilder.core.bpel.handlers.BPELFinalizer;
import org.opentosca.planbuilder.core.bpel.handlers.BPELPlanHandler;
import org.opentosca.planbuilder.core.bpel.handlers.CorrelationIDInitializer;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.EmptyPropertyToInputHandler;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.NodeRelationInstanceVariablesHandler;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.PropertyVariableHandler;
import org.opentosca.planbuilder.core.bpel.tosca.handlers.SimplePlanBuilderServiceInstanceHandler;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.AbstractPlan.PlanType;
import org.opentosca.planbuilder.model.plan.bpel.BPELPlan;
import org.opentosca.planbuilder.model.plan.bpel.BPELScope;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractInterface;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractOperation;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.opentosca.planbuilder.model.utils.ModelUtils;
import org.opentosca.planbuilder.plugins.context.Property2VariableMapping;
import org.opentosca.planbuilder.plugins.context.PropertyVariable;
import org.opentosca.planbuilder.plugins.typebased.IPlanBuilderPostPhasePlugin;
import org.opentosca.planbuilder.plugins.typebased.IPlanBuilderPrePhasePlugin;
import org.opentosca.planbuilder.plugins.typebased.IPlanBuilderTypePlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BPELDefrostProcessBuilder extends AbstractDefrostPlanBuilder {

  private final static Logger LOG = LoggerFactory.getLogger(BPELDefrostProcessBuilder.class);

  // handler for abstract buildplan operations
  private BPELPlanHandler planHandler;

  // class for initializing properties inside the build plan
  private final PropertyVariableHandler propertyInitializer;
  // adds serviceInstance Variable and instanceDataAPIUrl to buildPlans
  private SimplePlanBuilderServiceInstanceHandler serviceInstanceInitializer;
  // adds nodeInstanceIDs to each templatePlan
  private NodeRelationInstanceVariablesHandler instanceVarsHandler;
  // class for finalizing build plans (e.g when some template didn't receive
  // some provisioning logic and they must be filled with empty elements)
  private final BPELFinalizer finalizer;

  private QName statefulComponentPolicy = new QName("http://opentosca.org/policytypes", "StatefulComponent");
  private final EmptyPropertyToInputHandler emptyPropInit = new EmptyPropertyToInputHandler();

  // accepted operations for provisioning
  private final List<String> provisioningOpNames = new ArrayList<>();
  private final List<String> defrostOpNames = new ArrayList<>();

  private BPELProcessFragments bpelFragments;

  private CorrelationIDInitializer correlationHandler;

  public BPELDefrostProcessBuilder() {
    try {
      this.planHandler = new BPELPlanHandler();
      this.serviceInstanceInitializer = new SimplePlanBuilderServiceInstanceHandler();
      this.instanceVarsHandler = new NodeRelationInstanceVariablesHandler(this.planHandler);
      this.bpelFragments = new BPELProcessFragments();
      this.correlationHandler = new CorrelationIDInitializer();
    }
    catch (final ParserConfigurationException e) {
      BPELDefrostProcessBuilder.LOG.error("Error while initializing BuildPlanHandler", e);
    }
    this.propertyInitializer = new PropertyVariableHandler(this.planHandler);
    this.finalizer = new BPELFinalizer();

    this.provisioningOpNames.add("install");
    this.provisioningOpNames.add("configure");
    this.provisioningOpNames.add("start");

    this.defrostOpNames.add(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_STATE_DEFREEZE);
  }

  @Override
  public BPELPlan buildPlan(final String csarName, final AbstractDefinitions definitions,
                            final AbstractServiceTemplate serviceTemplate) {
    BPELDefrostProcessBuilder.LOG.info("Making Concrete Plans");



    if (!this.isDefrostable(serviceTemplate)) {
      BPELDefrostProcessBuilder.LOG.warn("Couldn't create DeFreezePlan for ServiceTemplate {} in Definitions {} of CSAR {}",
        serviceTemplate.getQName().toString(), definitions.getId(), csarName);
      return null;
    }

    String namespace;
    if (serviceTemplate.getTargetNamespace() != null) {
      namespace = serviceTemplate.getTargetNamespace();
    } else {
      namespace = definitions.getTargetNamespace();
    }

    final String processName = ModelUtils.makeValidNCName(serviceTemplate.getId() + "_defrostPlan");
    final String processNamespace = serviceTemplate.getTargetNamespace() + "_defrostPlan";

    final AbstractPlan newAbstractBackupPlan =
      generateDOG(new QName(processNamespace, processName).toString(), definitions, serviceTemplate);

    final BPELPlan newDefreezePlan =
      this.planHandler.createEmptyBPELPlan(processNamespace, processName, newAbstractBackupPlan, "defrost");

    newDefreezePlan.setTOSCAInterfaceName("OpenTOSCA-Stateful-Lifecycle-Interface");
    newDefreezePlan.setTOSCAOperationname("defrost");
    newDefreezePlan.setType(PlanType.BUILD);

    this.planHandler.initializeBPELSkeleton(newDefreezePlan, csarName);

    this.instanceVarsHandler.addInstanceURLVarToTemplatePlans(newDefreezePlan, serviceTemplate);
    this.instanceVarsHandler.addInstanceIDVarToTemplatePlans(newDefreezePlan, serviceTemplate);

    final Property2VariableMapping propMap =
      this.propertyInitializer.initializePropertiesAsVariables(newDefreezePlan, serviceTemplate);

    // instanceDataAPI handling is done solely trough this extension
    this.planHandler.registerExtension("http://www.apache.org/ode/bpel/extensions/bpel4restlight", true,
      newDefreezePlan);

    // initialize instanceData handling
    this.serviceInstanceInitializer.appendCreateServiceInstanceVarsAndAnitializeWithInstanceDataAPI(newDefreezePlan);

    String serviceInstanceUrl = this.serviceInstanceInitializer.findServiceInstanceUrlVariableName(newDefreezePlan);
    String serviceInstanceId = this.serviceInstanceInitializer.findServiceInstanceIdVarName(newDefreezePlan);
    String serviceTemplateUrl = this.serviceInstanceInitializer.findServiceTemplateUrlVariableName(newDefreezePlan);

    this.emptyPropInit.initializeEmptyPropertiesAsInputParam(newDefreezePlan, propMap, serviceInstanceUrl,
      serviceInstanceId, serviceTemplateUrl, serviceTemplate,
      csarName);

    final List<BPELScope> changedActivities =
      runPlugins(newDefreezePlan, propMap, serviceInstanceUrl, serviceInstanceId, serviceTemplateUrl, csarName);

    this.correlationHandler.addCorrellationID(newDefreezePlan);

    String serviceInstanceURLVarName =
      this.serviceInstanceInitializer.findServiceInstanceUrlVariableName(newDefreezePlan);

    this.serviceInstanceInitializer.appendSetServiceInstanceState(newDefreezePlan,
      newDefreezePlan.getBpelMainFlowElement(),
      "CREATING", serviceInstanceURLVarName);
    this.serviceInstanceInitializer.appendSetServiceInstanceState(newDefreezePlan,
      newDefreezePlan.getBpelMainSequenceOutputAssignElement(),
      "CREATED", serviceInstanceURLVarName);
    this.finalizer.finalize(newDefreezePlan);

    BPELDefrostProcessBuilder.LOG.debug("Created Plan:");
    BPELDefrostProcessBuilder.LOG.debug(ModelUtils.getStringFromDoc(newDefreezePlan.getBpelDocument()));

    return newDefreezePlan;
  }

  private boolean isDefrostable(AbstractServiceTemplate serviceTemplate) {

    for (AbstractNodeTemplate nodeTemplate : serviceTemplate.getTopologyTemplate().getNodeTemplates()) {
      if (this.isDefrostable(nodeTemplate)) {
        return true;
      }
    }
    return false;
  }

  private boolean isDefrostable(AbstractNodeTemplate nodeTemplate) {
    return Objects.nonNull(this.getLoadStateOperation(nodeTemplate))
      && this.hasFreezeableComponentPolicy(nodeTemplate);
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
  private List<BPELScope> runPlugins(final BPELPlan plan, final Property2VariableMapping propMap,
                                     String serviceInstanceUrl, String serviceInstanceId, String serviceTemplateUrl,
                                     String csarFileName) {

    final List<BPELScope> changedActivities = new ArrayList<>();

    String statefulServiceTemplateUrlVarName = this.findStatefulServiceTemplateUrlVar(plan);

    for (final BPELScope templatePlan : plan.getTemplateBuildPlans()) {
      final BPELPlanContext context = new BPELPlanContext(plan, templatePlan, propMap, plan.getServiceTemplate(),
        serviceInstanceUrl, serviceInstanceId, serviceTemplateUrl, csarFileName);

      if (templatePlan.getNodeTemplate() != null) {
        // create a context for the node

        AbstractNodeTemplate nodeTemplate = templatePlan.getNodeTemplate();
        boolean alreadyHandled = false;

        List<String> operationNames = this.provisioningOpNames;

        if (this.isDefrostable(nodeTemplate)) {
          operationNames = this.defrostOpNames;
        }

        if (this.isRunning(templatePlan.getNodeTemplate())) {
          BPELBuildProcessBuilder.LOG.debug("Skipping the provisioning of NodeTemplate "
            + templatePlan.getNodeTemplate().getId() + "  beacuse state=running is set.");
          for (final IPlanBuilderPostPhasePlugin postPhasePlugin : this.pluginRegistry.getPostPlugins()) {
            if (postPhasePlugin.canHandleCreate(templatePlan.getNodeTemplate())) {
              postPhasePlugin.handleCreate(context, templatePlan.getNodeTemplate());
            }
          }
          continue;
        }

        BPELBuildProcessBuilder.LOG.debug("Trying to handle NodeTemplate " + nodeTemplate.getId());
        // check if we have a generic plugin to handle the template
        // Note: if a generic plugin fails during execution the
        // TemplateBuildPlan is broken!

        for (final IPlanBuilderPrePhasePlugin prePlugin : this.pluginRegistry.getPrePlugins()) {
          if (prePlugin.canHandleCreate(templatePlan.getNodeTemplate())) {
            prePlugin.handleCreate(context, templatePlan.getNodeTemplate());
          }
        }

        final IPlanBuilderTypePlugin plugin = this.pluginRegistry.findTypePluginForCreation(nodeTemplate);
        if (plugin != null) {
          BPELDefrostProcessBuilder.LOG.debug("Handling NodeTemplate {} with type plugin {}",
            nodeTemplate.getId(), plugin.getID());
          plugin.handleCreate(context, nodeTemplate);
        } else {
          BPELDefrostProcessBuilder.LOG.info("Can't handle NodeTemplate {} with type plugin",
            nodeTemplate.getId());
        }

        for (final IPlanBuilderPostPhasePlugin postPhasePlugin : this.pluginRegistry.getPostPlugins()) {
          if (postPhasePlugin.canHandleCreate(templatePlan.getNodeTemplate())) {
            postPhasePlugin.handleCreate(context, templatePlan.getNodeTemplate());
          }
        }

      } else if (templatePlan.getRelationshipTemplate() != null) {
        // handling relationshiptemplate
        final AbstractRelationshipTemplate relationshipTemplate = templatePlan.getRelationshipTemplate();

        // check if we have a generic plugin to handle the template
        // Note: if a generic plugin fails during execution the
        // TemplateBuildPlan is broken here!
        // TODO implement fallback
        if (pluginRegistry.canTypePluginHandleCreate(relationshipTemplate)) {
          BPELBuildProcessBuilder.LOG.info("Handling RelationshipTemplate {} with generic plugin",
            relationshipTemplate.getId());
          IPlanBuilderTypePlugin plugin = this.pluginRegistry.findTypePluginForCreation(relationshipTemplate);
          this.pluginRegistry.handleCreateWithTypePlugin(context, relationshipTemplate, plugin);

        } else {
          BPELBuildProcessBuilder.LOG.debug("Couldn't handle RelationshipTemplate {} with type plugin",
            relationshipTemplate.getId());
        }

        for (final IPlanBuilderPostPhasePlugin postPhasePlugin : this.pluginRegistry.getPostPlugins()) {
          if (postPhasePlugin.canHandleCreate(templatePlan.getRelationshipTemplate())) {
            postPhasePlugin.handleCreate(context, templatePlan.getRelationshipTemplate());
          }
        }
      }

    }
    return changedActivities;
  }

  @Override
  public List<AbstractPlan> buildPlans(final String csarName, final AbstractDefinitions definitions) {
    BPELDefrostProcessBuilder.LOG.info("Builing the Plans");
    final List<AbstractPlan> plans = new ArrayList<>();
    for (final AbstractServiceTemplate serviceTemplate : definitions.getServiceTemplates()) {

      if (!this.isDefrostable(serviceTemplate)) {
        continue;
      }


      BPELDefrostProcessBuilder.LOG.debug("ServiceTemplate {} has no DefreezePlan, generating a new plan",
        serviceTemplate.getQName().toString());
      final BPELPlan newBuildPlan = buildPlan(csarName, definitions, serviceTemplate);

      if (newBuildPlan != null) {
        BPELDefrostProcessBuilder.LOG.debug("Created Defreeze sPlan "
          + newBuildPlan.getBpelProcessElement().getAttribute("name"));
        plans.add(newBuildPlan);
      }

    }
    return plans;
  }

}
