package org.opentosca.siengine.service;

import org.apache.camel.Exchange;

/**
 * Interface of the SIEngine.<br>
 * <br>
 * 
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * The interface specifies two methods. One for invoking an operation of a
 * implementation artifact and one method for invoking a plan.
 * 
 * 
 * 
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 * 
 */
public interface ISIEngineService {
	
	/**
	 * Handles the invoke-request of an implementation artifact.
	 * 
	 * @param exchange contains all needed information like csarID,
	 *            ServiceTemplateID,... to determine the implementation artifact
	 *            and the data to be transferred to it.
	 * 
	 * @return the response of the invoked implementation artifact as body of
	 *         the exchange message.
	 * 
	 */
	public void invokeIA(Exchange exchange);
	
	/**
	 * Handles the invoke-request of a plan.
	 * 
	 * @param exchange contains all needed information like csarID, PlanID,...
	 *            to get the endpoint of the specified plan and the data to be
	 *            transferred to it.
	 * 
	 * @return the response of the invoked plan as body of the exchange message.
	 * 
	 */
	public void invokePlan(Exchange exchange);
}
