package org.opentosca.planbuilder.generic.plugin.ec2vm;

import javax.xml.namespace.QName;

import org.opentosca.planbuilder.generic.plugin.ec2vm.handler.Handler;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.plugins.IPlanBuilderGenericPlugin;
import org.opentosca.planbuilder.plugins.context.TemplatePlanContext;

public class Plugin implements IPlanBuilderGenericPlugin {
	
	private final QName ec2NodeType = new QName("http://www.example.com/tosca/ServiceTemplates/EC2VM", "EC2");
	private final QName vmNodeType = new QName("http://www.example.com/tosca/ServiceTemplates/EC2VM", "VM");
	private final QName ubuntuNodeType = new QName("http://www.example.com/tosca/ServiceTemplates/EC2VM", "Ubuntu");
	private Handler handler = new Handler();
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getID() {
		return "OpenTOSCA Generic PlanBuilder Plugin for EC2 VM Instances";
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean handle(TemplatePlanContext templateContext) {
		AbstractNodeTemplate nodeTemplate = templateContext.getNodeTemplate();
		if (nodeTemplate == null) {
			return false;
		}
		if (nodeTemplate.getType().getId().toString().equals(this.ec2NodeType.toString()) || nodeTemplate.getType().getId().toString().equals(this.vmNodeType.toString())) {
			// requirement: nodeTemplates with these two nodeTypes are handled,
			// by doing nothing
			return true;
			
		} else if (nodeTemplate.getType().getId().toString().equals(this.ubuntuNodeType.toString())) {
			return this.handler.handle(templateContext, nodeTemplate);
		} else {
			return false;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canHandle(AbstractNodeTemplate nodeTemplate) {
		// this plugin can handle all referenced nodeType
		if (nodeTemplate.getType().getId().toString().equals(this.ec2NodeType.toString())) {
			return true;
		} else if (nodeTemplate.getType().getId().toString().equals(this.vmNodeType.toString())) {
			// checking if this vmNode is connected to a nodeTemplate of Type
			// EC2, if not this plugin can't handle this node
			for (AbstractRelationshipTemplate relationshipTemplate : nodeTemplate.getOutgoingRelations()) {
				if (relationshipTemplate.getTarget().getType().getId().toString().equals(this.ec2NodeType.toString())) {
					return true;
				}
			}
			return false;
		} else if (nodeTemplate.getType().getId().toString().equals(this.ubuntuNodeType.toString())) {
			// checking whether this ubuntu NodeTemplate is connected to a VM
			// Node, after this checking whether the VM Node is connected to a
			// EC2 Node
			for (AbstractRelationshipTemplate relationshipTemplate : nodeTemplate.getOutgoingRelations()) {
				if (relationshipTemplate.getTarget().getType().getId().toString().equals(this.vmNodeType.toString())) {
					for (AbstractRelationshipTemplate relationshipTemplate2 : relationshipTemplate.getTarget().getOutgoingRelations()) {
						if (relationshipTemplate2.getTarget().getType().getId().toString().equals(this.ec2NodeType.toString())) {
							return true;
						}
					}
					
				}
			}
			return false;
		} else {
			return false;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canHandle(AbstractRelationshipTemplate relationshipTemplate) {
		// this plugin doesn't handle relations
		return false;
	}
	
}
