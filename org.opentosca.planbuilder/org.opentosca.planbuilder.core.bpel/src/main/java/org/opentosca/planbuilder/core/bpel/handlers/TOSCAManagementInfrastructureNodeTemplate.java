package org.opentosca.planbuilder.core.bpel.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TCapability;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRequirement;

/**
 * This class represents the TOSCA Management Infrastructure as a NodeTemplate.
 *
 * @author kalman.kepes@iaas.uni-stuttgart.de
 */
public class TOSCAManagementInfrastructureNodeTemplate extends TNodeTemplate {

    /*
     * (non-Javadoc)
     *
     * @see org.opentosca.planbuilder.model.tosca.TNodeTemplate# getCapabilities()
     */
    @Override
    public List<TCapability> getCapabilities() {
        return new ArrayList<>();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opentosca.planbuilder.model.tosca.TNodeTemplate# getRequirements()
     */
    @Override
    public List<TRequirement> getRequirements() {
        return new ArrayList<>();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opentosca.planbuilder.model.tosca.TNodeTemplate#getName()
     */
    @Override
    public String getName() {
        return "TOSCA Management Infrastructure NodeTemplate";
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opentosca.planbuilder.model.tosca.TNodeTemplate#getId()
     */
    @Override
    public String getId() {
        return "TOSCAMgmt";
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opentosca.planbuilder.model.tosca.TNodeTemplate#getType()
     */
    @Override
    public QName getType() {
        return new QName("http://opentosca.org/nodetypes", "TOSCAManagmentInfrastructure");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opentosca.planbuilder.model.tosca.TNodeTemplate#getProperties( )
     */
    @Override
    public TEntityTemplate.Properties getProperties() {
        return new TEntityTemplate.Properties() {

        };
    }

    @Override
    public Map<QName, String> getOtherAttributes() {
        return new HashMap<QName, String>();
    }
}
