package org.opentosca.planbuilder.core.plugins.typebased;

/**
 * <p>
 * This the common interface for all plugins the PlanBuilder will use
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public interface IPlanBuilderPlugin {

    /**
     * Returns the Id of the Plugin
     *
     * @return a String used to identify this Plugin
     */
    String getID();

    /**
     * Returns the priority to use this plugin by the plan builders. The higher the priority the more it is advised to
     * use this plugin instead of another which also can handle the requested task (e.g. generating code to create an
     * instance of a node template). The highest priority is 0, while it is advised that generic plugins that can handle
     * node/relationship templates in a generic way (e.g. by a pattern), and only if they can, should have a lower
     * priority than node/relation specific plugins
     *
     * @return an Integer denoting the priority of this plugin
     */
    int getPriority();
}
