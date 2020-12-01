package org.opentosca.planbuilder.model.tosca;

import java.util.List;

import javax.xml.namespace.QName;

/**
 * <p>
 * This class represents TOSCA NodeTypeImplementation.
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public abstract class AbstractNodeTypeImplementation {

    /**
     * Returns the name of this NodeTypeImplementation
     *
     * @return a String containing the name of this NodeTypeImplementation
     */
    public abstract String getName();

    /**
     * The targetNamespace of this NodeTypeImplementation
     *
     * @return returns the logical namespace of this NodeTypeImplementation
     */
    public abstract String getTargetNamespace();

    /**
     * Returns whether this NodeTypeImplementation is abstract or not
     *
     * @return true if this NodeTypeImplementation is abstract, else false
     */
    public abstract boolean isAbstract();

    /**
     * Returns whether this NodeTypeImplementation is final or not
     *
     * @return true if this NodeTypeImplementation is final, else false
     */
    public abstract boolean isFinal();

    /**
     * Returns the TOSCA Tags of this NodeTypeImplementation
     *
     * @return a List of AbstractTags for this NodeTypeImplementation
     */
    public abstract List<AbstractTag> getTags();

    /**
     * Returns the required ContainerFeatures of this NodeTypeImplementation
     *
     * @return a List of Strings representing the needed Features for the Container
     */
    public abstract List<String> getRequiredContainerFeatures();

    /**
     * Returns the parent NodeTypeImplementation of this NodeTypeImplementation
     *
     * @return a QName representing the parent NodeTypeImplementation
     */
    public abstract QName getDerivedFrom();

    /**
     * Returns the TOSCA ImplementationArtifacts in this NodeTypeImplementation
     *
     * @return a List of AbstractImplementationArtifacts of this NodeTypeImplementation
     */
    public abstract List<AbstractImplementationArtifact> getImplementationArtifacts();

    /**
     * Returns the TOSCA DeploymentArtifacts in this NodeTypeImplementation
     *
     * @return a List of AbstractDeployomentArtifacts of this NodeTypeImplementation
     */
    public abstract List<AbstractDeploymentArtifact> getDeploymentArtifacts();

    /**
     * Returns the TOSCA NodeType this NodeTypeImplementation implements
     *
     * @return an AbstractNodeType this NodeTypeImplementation implements
     */
    public abstract AbstractNodeType getNodeType();

    @Override
    public boolean equals(Object obj) {

        if(!(obj instanceof AbstractNodeTypeImplementation)){
            return false;
        }

        AbstractNodeTypeImplementation nodeImpl = (AbstractNodeTypeImplementation) obj;

        if(!nodeImpl.getName().equals(this.getName())){
            return false;
        }

        if(!nodeImpl.getNodeType().equals(this.getNodeType())){
            return false;
        }

        return super.equals(obj);
    }
}
