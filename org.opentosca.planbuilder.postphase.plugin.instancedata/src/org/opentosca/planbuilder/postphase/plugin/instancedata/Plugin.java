/**
 *
 */
package org.opentosca.planbuilder.postphase.plugin.instancedata;

import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractProperties;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.plugins.IPlanBuilderPostPhasePlugin;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;
import org.w3c.dom.Element;

/**
 * <p>
 * This class represents a POST-Phase Plugin which sends runtime values of
 * NodeTemplate Instances to the OpenTOSCA Container InstanceData API
 * </p>
 * Copyright 2014 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 * 
 */
public class Plugin implements IPlanBuilderPostPhasePlugin {
	
	private Handler handler = new Handler();
	
	
	@Override
	public String getID() {
		return "OpenTOSCA InstanceData Post Phase Plugin";
	}
	
	@Override
	public boolean handle(TemplatePlanContext context, AbstractNodeTemplate nodeTemplate) {
		return this.handler.handle(context, nodeTemplate);
	}
	
	@Override
	public boolean handle(TemplatePlanContext context, AbstractRelationshipTemplate relationshipTemplate) {
		// TODO blocking relationshipTemplate handling from now, as the
		// instancedata api can't handle this
		return false;
	}
	
	@Override
	public boolean canHandle(AbstractNodeTemplate nodeTemplate) {
		return this.checkProperties(nodeTemplate.getProperties());
	}
	
	@Override
	public boolean canHandle(AbstractRelationshipTemplate relationshipTemplate) {
		return this.checkProperties(relationshipTemplate.getProperties());
	}
	
	/**
	 * <p>
	 * Checks the given AbstractProperties against following criteria:
	 * Nullpointer-Check for properties itself and its given DOM Element,
	 * followed by whether the dom element has any child elements (if not, we
	 * have no properties/bpel-variables defined)
	 * </p>
	 * 
	 * @param properties AbstractProperties of an AbstractNodeTemplate or
	 *            AbstractRelationshipTemplate
	 * @return true iff properties and properties.getDomElement() != null and
	 *         DomElement.hasChildNodes() == true
	 */
	private boolean checkProperties(AbstractProperties properties) {
		if (properties == null) {
			return false;
		}
		
		if (properties.getDOMElement() == null) {
			return false;
		}
		
		Element propertiesRootElement = properties.getDOMElement();
		
		if (!propertiesRootElement.hasChildNodes()) {
			return false;
		}
		
		return true;
	}
	
}
