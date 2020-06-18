/**
 *
 */
package org.opentosca.container.api.planbuilder.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Copyright 2015 IAAS University of Stuttgart <br>
 * <br>
 *
 * <p>
 * This class represent the request body for generating Plans
 * </p>
 *
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
@XmlRootElement
public class GeneratePlanForTopology {

    @XmlElement
    public String CSARURL;

    @XmlElement
    public String PLANPOSTURL;
}
