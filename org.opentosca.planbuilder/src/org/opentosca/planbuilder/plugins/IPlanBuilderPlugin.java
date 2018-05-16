package org.opentosca.planbuilder.plugins;

/**
 * <p>
 * This the common interface for all plugins the PlanBuilder will use
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public interface IPlanBuilderPlugin {

    /**
     * Returns the Id of the Plugin
     *
     * @return a String used to identify this Plugin
     */
    public String getID();
}
