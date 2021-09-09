package org.opentosca.planbuilder.model.tosca;

import java.util.Collection;
import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TBoundaryDefinitions;
import org.eclipse.winery.model.tosca.TTag;

/**
 * <p>
 * This class represents a TOSCA ServiceTemplate
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public abstract class AbstractServiceTemplate {

    /**
     * Returns the TopologyTemplate of this ServiceTemplate
     *
     * @return an AbstractTopologyTemplate
     */
    public abstract AbstractTopologyTemplate getTopologyTemplate();

    /**
     * Returns the Id of this ServiceTemplate
     *
     * @return a String containing an Id for this ServiceTemplate
     */
    public abstract String getId();

    /**
     * Returns the Name of this ServiceTemplate
     *
     * @return a String containing the Name for this ServiceTemplate, if not Name set null
     */
    public abstract String getName();

    /**
     * Returns the targetNamespace of this ServiceTemplate
     *
     * @return a String containing the logical namespace of this ServiceTemplate
     */
    public abstract String getTargetNamespace();

    /**
     * Returns a QName for this ServiceTemplate
     *
     * @return a QName for this ServiceTemplate
     */
    public abstract QName getQName();

    /**
     * Returns the BoundaryDefinitions of this ServiceTemplate
     *
     * @return an AbstractBoundaryDefinitions of this ServiceTemplate
     */
    public abstract TBoundaryDefinitions getBoundaryDefinitions();

    /**
     * Returns whether this ServiceTemplate has a BuildPlan or not
     *
     * @return true iff this ServiceTemplate has some BuildPlan
     */
    public abstract boolean hasBuildPlan();

    /**
     * Returns whether this ServiceTempalte has a TerminationPlan or not
     *
     * @return true iff this ServiceTemplate has at least one TerminationPlan
     */
    public abstract boolean hasTerminationPlan();

    /**
     * Returns the Tags set on this Service Template
     *
     * @return a Map from String to Strin representing the keys and values of TOSCA tags
     */
    public abstract Collection<TTag> getTags();
}
