package org.opentosca.planbuilder;

import java.util.Collection;
import java.util.List;

import javax.xml.namespace.QName;

import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.plugins.IPlanBuilderPolicyAwareTypePlugin;
import org.opentosca.planbuilder.plugins.IPlanBuilderTypePlugin;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.opentosca.planbuilder.plugins.registry.PluginRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractPlanBuilder {
	
	private final static Logger LOG = LoggerFactory.getLogger(AbstractPlanBuilder.class);
	
	/**
	 * <p>
	 * Creates a BuildPlan in WS-BPEL 2.0 for the specified values csarName,
	 * definitions and serviceTemplateId. Where csarName denotes the fileName of
	 * the CSAR, definitions denotes the Definitions document and
	 * serviceTemplateId a QName denoting the ServiceTemplate inside the
	 * Definitions document
	 * </p>
	 *
	 * @param csarName the file name of the CSAR as String
	 * @param definitions the Definitions document as AbstractDefinitions Object
	 * @param serviceTemplateId a QName denoting a ServiceTemplate inside the
	 *            Definitions document
	 * @return a complete BuildPlan for the given ServiceTemplate, if the
	 *         ServiceTemplate denoted by the given QName isn't found inside the
	 *         Definitions document null is returned instead
	 */
	abstract public AbstractPlan buildPlan(String csarName, AbstractDefinitions definitions, QName serviceTemplateId);
	
	/**
	 * <p>
	 * Returns a List of BuildPlans for the ServiceTemplates contained in the
	 * given Definitions document
	 * </p>
	 *
	 * @param csarName the file name of CSAR
	 * @param definitions a AbstractDefinitions Object denoting the Definitions
	 *            document
	 * @return a List of Build Plans for each ServiceTemplate contained inside
	 *         the Definitions document
	 */
	abstract public List<AbstractPlan> buildPlans(String csarName, AbstractDefinitions definitions);
	
	/**
	 * <p>
	 * Checks whether there is any generic plugin, that can handle the given
	 * NodeTemplate
	 * </p>
	 *
	 * @param nodeTemplate an AbstractNodeTemplate denoting a NodeTemplate
	 * @return true if there is any generic plugin which can handle the given
	 *         NodeTemplate, else false
	 */
	public IPlanBuilderTypePlugin findTypePlugin(AbstractNodeTemplate nodeTemplate) {
		for (IPlanBuilderTypePlugin plugin : PluginRegistry.getGenericPlugins()) {
			AbstractPlanBuilder.LOG.debug("Checking whether Generic Plugin " + plugin.getID() + " can handle NodeTemplate " + nodeTemplate.getId());
			if (plugin.canHandle(nodeTemplate)) {
				AbstractPlanBuilder.LOG.info("Found GenericPlugin {} that can handle NodeTemplate {}", plugin.getID(), nodeTemplate.getId());
				return plugin;
			}
		}
		return null;
	}
	
	/**
	 * <p>
	 * Checks whether there is any generic plugin, that can handle the given
	 * NodeTemplate
	 * </p>
	 *
	 * @param nodeTemplate an AbstractNodeTemplate denoting a NodeTemplate
	 * @return true if there is any generic plugin which can handle the given
	 *         NodeTemplate, else false
	 */
	public IPlanBuilderPolicyAwareTypePlugin findPolicyAwareTypePlugin(AbstractNodeTemplate nodeTemplate, Collection<String> policies) {
		for (IPlanBuilderPolicyAwareTypePlugin plugin : PluginRegistry.getGenericPolicyAwarePlugins()) {
			AbstractPlanBuilder.LOG.debug("Checking whether Generic Plugin " + plugin.getID() + " can handle NodeTemplate " + nodeTemplate.getId());
			if (plugin.canHandle(nodeTemplate,policies)) {
				AbstractPlanBuilder.LOG.info("Found GenericPlugin {} that can handle NodeTemplate {}", plugin.getID(), nodeTemplate.getId());
				return plugin;
			}
		}
		return null;
	}
	
	/**
	 * <p>
	 * Checks whether there is any generic plugin, that can handle the given
	 * RelationshipTemplate
	 * </p>
	 *
	 * @param nodeTemplate an AbstractNodeTemplate denoting a NodeTemplate
	 * @return true if there is any generic plugin which can handle the given
	 *         NodeTemplate, else false
	 */
	public IPlanBuilderTypePlugin findTypePlugin(AbstractRelationshipTemplate relationshipTemplate) {
		for (IPlanBuilderTypePlugin plugin : PluginRegistry.getGenericPlugins()) {
			AbstractPlanBuilder.LOG.debug("Checking whether Type Plugin " + plugin.getID() + " can handle NodeTemplate " + relationshipTemplate.getId());
			if (plugin.canHandle(relationshipTemplate)) {
				AbstractPlanBuilder.LOG.info("Found TypePlugin {} that can handle NodeTemplate {}", plugin.getID(), relationshipTemplate.getId());
				return plugin;
			}
		}
		return null;
	}
	
	/**
	 * <p>
	 * Takes the first occurence of a generic plugin which can handle the given
	 * RelationshipTemplate
	 * </p>
	 *
	 * @param context a TemplatePlanContext which was initialized for the given
	 *            RelationshipTemplate
	 * @param nodeTemplate a RelationshipTemplate as an
	 *            AbstractRelationshipTemplate
	 * @return returns true if there was a generic plugin which could handle the
	 *         given RelationshipTemplate and execution was successful, else
	 *         false
	 */
	public boolean handleWithTypePlugin(TemplatePlanContext context, AbstractRelationshipTemplate relationshipTemplate) {
		for (IPlanBuilderTypePlugin plugin : PluginRegistry.getGenericPlugins()) {
			if (plugin.canHandle(relationshipTemplate)) {
				AbstractPlanBuilder.LOG.info("Handling relationshipTemplate {} with generic plugin {}", relationshipTemplate.getId(), plugin.getID());
				return plugin.handle(context);
			}
		}
		return false;
	}
}