package org.opentosca.bus.application.service;

/**
 * Interface of the Application Bus.<br>
 * <br>
 *
 *
 * The interface specifies one method that returns the routing endpoint of the Application Bus.
 *
 *
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 *
 */
public interface IApplicationBusService {

    /**
     * @return the routing endpoint of this bundle
     */
    public String getRoutingEndpoint();

}
