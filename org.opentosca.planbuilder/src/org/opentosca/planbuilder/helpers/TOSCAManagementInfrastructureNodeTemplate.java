package org.opentosca.planbuilder.helpers;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.opentosca.planbuilder.model.tosca.AbstractCapability;
import org.opentosca.planbuilder.model.tosca.AbstractDeploymentArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractInterface;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractNodeType;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTypeImplementation;
import org.opentosca.planbuilder.model.tosca.AbstractProperties;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRequirement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This class represents the TOSCA Management Infrastructure as a NodeTemplate.
 * 
 * @author kalman.kepes@iaas.uni-stuttgart.de
 *
 */
public class TOSCAManagementInfrastructureNodeTemplate extends AbstractNodeTemplate {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate#
	 * getOutgoingRelations()
	 */
	@Override
	public List<AbstractRelationshipTemplate> getOutgoingRelations() {
		return new ArrayList<AbstractRelationshipTemplate>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate#
	 * getIngoingRelations()
	 */
	@Override
	public List<AbstractRelationshipTemplate> getIngoingRelations() {
		return new ArrayList<AbstractRelationshipTemplate>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate#
	 * getCapabilities()
	 */
	@Override
	public List<AbstractCapability> getCapabilities() {
		return new ArrayList<AbstractCapability>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate#
	 * getRequirements()
	 */
	@Override
	public List<AbstractRequirement> getRequirements() {
		return new ArrayList<AbstractRequirement>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate#getName()
	 */
	@Override
	public String getName() {
		return "TOSCA Management Infrastructure NodeTemplate";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate#
	 * getImplementations()
	 */
	@Override
	public List<AbstractNodeTypeImplementation> getImplementations() {
		return new ArrayList<AbstractNodeTypeImplementation>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate#getId()
	 */
	@Override
	public String getId() {
		return "TOSCAMgmt";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate#getType()
	 */
	@Override
	public AbstractNodeType getType() {
		return new AbstractNodeType() {
			
			@Override
			public AbstractNodeType getTypeRef() {
				return null;
			}
			
			@Override
			public String getTargetNamespace() {
				return "http://opentosca.org/nodetypes";
			}
			
			@Override
			public String getName() {
				return "TOSCA Managment Infrastructure Type";
			}
			
			@Override
			public List<AbstractInterface> getInterfaces() {
				return new ArrayList<AbstractInterface>();
			}
			
			@Override
			public QName getId() {
				return new QName("http://opentosca.org/nodetypes","TOSCAManagmentInfrastructure");
			}

			@Override
			public List<Node> getAdditionalElements() {
				return new ArrayList<Node>();
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate#getProperties(
	 * )
	 */
	@Override
	public AbstractProperties getProperties() {
		return new AbstractProperties() {
			
			@Override
			public Element getDOMElement() {
				return null;
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate#
	 * getDeploymentArtifacts()
	 */
	@Override
	public List<AbstractDeploymentArtifact> getDeploymentArtifacts() {
		return new ArrayList<AbstractDeploymentArtifact>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate#
	 * getMinInstances()
	 */
	@Override
	public int getMinInstances() {
		return 0;
	}

}
