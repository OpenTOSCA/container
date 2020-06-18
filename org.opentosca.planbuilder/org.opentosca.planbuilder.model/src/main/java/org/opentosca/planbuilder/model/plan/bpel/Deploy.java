package org.opentosca.planbuilder.model.plan.bpel;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.ode.schemas.dd._2007._03.TDeployment;

/**
 * <p>
 * This class is used to be able to generate Apache ODE DeploymentDeskriptors
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
@XmlRootElement(namespace = "http://www.apache.org/ode/schemas/dd/2007/03")
public class Deploy extends TDeployment {

}
