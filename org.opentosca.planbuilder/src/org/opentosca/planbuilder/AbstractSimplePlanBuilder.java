package org.opentosca.planbuilder;

import java.util.List;

import javax.xml.namespace.QName;

import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.planbuilder.model.plan.AbstractPlan.PlanType;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.opentosca.planbuilder.plugins.IPlanBuilderTypePlugin;
import org.opentosca.planbuilder.plugins.context.PlanContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSimplePlanBuilder extends AbstractPlanBuilder {

	private final static Logger LOG = LoggerFactory.getLogger(AbstractSimplePlanBuilder.class);

	/**
	 * <p>
	 * Creates a BuildPlan in WS-BPEL 2.0 for the specified values csarName,
	 * definitions and serviceTemplateId. Where csarName denotes the fileName of the
	 * CSAR, definitions denotes the Definitions document and serviceTemplateId a
	 * QName denoting the ServiceTemplate inside the Definitions document
	 * </p>
	 *
	 * @param csarName          the file name of the CSAR as String
	 * @param definitions       the Definitions document as AbstractDefinitions
	 *                          Object
	 * @param serviceTemplateId a QName denoting a ServiceTemplate inside the
	 *                          Definitions document
	 * @return a complete BuildPlan for the given ServiceTemplate, if the
	 *         ServiceTemplate denoted by the given QName isn't found inside the
	 *         Definitions document null is returned instead
	 */
	abstract public AbstractPlan buildPlan(String csarName, AbstractDefinitions definitions, AbstractServiceTemplate serviceTemplateId);

	/**
	 * <p>
	 * Returns a List of BuildPlans for the ServiceTemplates contained in the given
	 * Definitions document
	 * </p>
	 *
	 * @param csarName    the file name of CSAR
	 * @param definitions a AbstractDefinitions Object denoting the Definitions
	 *                    document
	 * @return a List of Build Plans for each ServiceTemplate contained inside the
	 *         Definitions document
	 */
	abstract public List<AbstractPlan> buildPlans(String csarName, AbstractDefinitions definitions);
}
