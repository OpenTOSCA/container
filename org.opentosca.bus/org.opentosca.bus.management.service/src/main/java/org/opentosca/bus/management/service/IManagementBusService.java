package org.opentosca.bus.management.service;

import org.apache.camel.Exchange;

/**
 * Interface of the Management Bus.<br>
 * <br>
 *
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * The interface specifies two methods. One for deploying an implementation artifact and invoking an operation of it.
 * Another method can be used for invoking a plan.
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 */
public interface IManagementBusService {

    /**
     * Handles the invoke-request of an implementation artifact. This includes the deployment of the implementation
     * artifact on a suited infrastructure if needed.
     *
     * @param exchange contains all needed information like csarID, ServiceTemplateID,... to determine the
     *                 implementation artifact and the data to be transferred to it.
     * @return the response of the invoked implementation artifact as body of the exchange message.
     */
    public void invokeIA(Exchange exchange);

    /**
     * Handles the invoke-request of a plan.
     *
     * @param exchange contains all needed information like csarID, PlanID,... to get the endpoint of the specified plan
     *                 and the data to be transferred to it.
     * @return the response of the invoked plan as body of the exchange message.
     */
    public void invokePlan(Exchange exchange);

    /**
     * Notifies a connected partner about the possibility to start handling the connectsTo between him and the partner
     * hosting this OpenTOSCA Container. Additionally, passes the input parameters required for the connectsTo to the
     * partner.
     *
     * @param exchange contains all needed information.
     */
    public void notifyPartner(Exchange exchange);

    /**
     * Handles the initial notification of all partners for a choreography.
     *
     * @param exchange contains all needed information.
     */
    public void notifyPartners(Exchange exchange);

    /**
     * Adds given partner to list of ready partners
     *
     * @param correlationID the correlation id of the partners plans
     * @param partnerID     the partners' id
     */
    void addPartnerToReadyList(String correlationID, String partnerID);

    /**
     * Checks whether a partner with the given correlation is ready and available for the bus
     *
     * @param correlationID the correlation id
     */
    boolean isPartnerAvailable(String correlationID, String partnerID);
}
