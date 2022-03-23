package org.opentosca.container.api.planbuilder.model;

import java.io.File;
import java.net.URL;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.opentosca.container.core.model.csar.CsarId;

/**
 * Copyright 2015 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
@XmlRootElement
public class PlanGenerationState {

    @XmlElement
    private final URL csarUrl;
    @XmlElement
    private final URL planPostUrl;
    @XmlElement
    public String currentMessage = "Task is initializing";
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

    public enum PlanGenerationStates {
        INITIALIZED, CSARDOWNLOADING, CSARDOWNLOADFAILED, CSARDOWNLOADED, PLANGENERATING, PLANGENERATIONFAILED, PLANSGENERATED, PLANSENDING, PLANSENDINGFAILED, PLANSSENT, OPTIONSENDING, OPTIONSENDINGFAILED, OPTIONSENT, FINISHED, FAILED
    }
}
