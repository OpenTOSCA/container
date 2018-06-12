package org.opentosca.planbuilder.service.model;

import java.io.File;
import java.net.URL;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.opentosca.container.core.model.csar.id.CSARID;

/**
 * Copyright 2015 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
@XmlRootElement
public class PlanGenerationState {

    public enum PlanGenerationStates {
        INITIALIZED, CSARDOWNLOADING, CSARDOWNLOADFAILED, CSARDOWNLOADED, PLANGENERATING, PLANGENERATIONFAILED, PLANGENERATED, PLANSENDING, PLANSENDINGFAILED, PLANSENT, OPTIONSENDING, OPTIONSENDINGFAILED, OPTIONSENT
    }


    @XmlElement
    public String currentMessage = "Task is initializing";

    @XmlElement
    private final URL csarUrl;

    private final CSARID csarId = null;

    @XmlElement
    private final URL planPostUrl;

    private final File planTmpFile = null;

    @XmlElement
    public PlanGenerationStates currentState = PlanGenerationStates.INITIALIZED;


    public PlanGenerationState() {
        this.csarUrl = null;
        this.planPostUrl = null;
    }

    public PlanGenerationState(final URL csarUrl, final URL planPostUrl) {
        this.csarUrl = csarUrl;
        this.planPostUrl = planPostUrl;
    }

    public URL getCsarUrl() {
        return this.csarUrl;
    }

    public URL getPostUrl() {
        return this.planPostUrl;
    }

}
