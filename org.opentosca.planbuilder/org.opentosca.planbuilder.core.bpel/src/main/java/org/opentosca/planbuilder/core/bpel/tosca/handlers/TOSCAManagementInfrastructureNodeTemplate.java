package org.opentosca.planbuilder.core.bpel.tosca.handlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.opentosca.planbuilder.model.tosca.AbstractCapability;
import org.opentosca.planbuilder.model.tosca.AbstractDeploymentArtifact;
import org.opentosca.planbuilder.model.tosca.AbstractInterface;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractNodeType;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTypeImplementation;
import org.opentosca.planbuilder.model.tosca.AbstractPolicy;
import org.opentosca.planbuilder.model.tosca.AbstractProperties;
import org.opentosca.planbuilder.model.tosca.AbstractRelationshipTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractRequirement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This class represents the TOSCA Management Infrastructure as a NodeTemplate.
 *
 * @author kalman.kepes@iaas.uni-stuttgart.de
 */
public class TOSCAManagementInfrastructureNodeTemplate extends AbstractNodeTemplate {

    /*
     * (non-Javadoc)
     *
     * @see org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate# getOutgoingRelations()
     */
    @Override
    public List<AbstractRelationshipTemplate> getOutgoingRelations() {
        return new ArrayList<>();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate# getIngoingRelations()
     */
    @Override
    public List<AbstractRelationshipTemplate> getIngoingRelations() {
        return new ArrayList<>();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate# getCapabilities()
     */
    @Override
    public List<AbstractCapability> getCapabilities() {
        return new ArrayList<>();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate# getRequirements()
     */
    @Override
    public List<AbstractRequirement> getRequirements() {
        return new ArrayList<>();
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
     * @see org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate# getImplementations()
     */
    @Override
    public List<AbstractNodeTypeImplementation> getImplementations() {
        return new ArrayList<>();
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
                return new ArrayList<>();
            }

            @Override
            public QName getId() {
                return new QName("http://opentosca.org/nodetypes", "TOSCAManagmentInfrastructure");
            }

            @Override
            public List<Node> getAdditionalElements() {
                return new ArrayList<>();
            }
        };
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate#getProperties( )
     */
    @Override
    public AbstractProperties getProperties() {
        return new AbstractProperties() {

            public Element getDOMElement() {
                return null;
            }

            @Override
            public String getNamespace() {return null;}

            @Override
            public String getElementName() {
                return null;
            }

            @Override
            public Map<String, String> asMap() {
                return null;
            }
        };
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate# getDeploymentArtifacts()
     */
    @Override
    public Collection<AbstractDeploymentArtifact> getDeploymentArtifacts() {
        return new HashSet<>();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate# getMinInstances()
     */
    @Override
    public int getMinInstances() {
        return 0;
    }

    @Override
    public List<AbstractPolicy> getPolicies() {
        return new ArrayList<>();
    }

    @Override
    public Map<QName, String> getOtherAttributes() {
        return new HashMap<QName, String>();
    }
}
