package org.opentosca.bus.management.service.impl;

import javax.xml.namespace.QName;

import org.opentosca.container.core.model.csar.Csar;

class PlanInvocationArguments {
    public final Csar csar;
    public final QName serviceTemplateId;
    public final Long serviceTemplateInstanceId;
    public final QName planId;
    public final String correlationId;
    public final String chorCorrelationId;
    public final String chorPartners;
    public final String operationName;

    public PlanInvocationArguments(Csar csar, QName serviceTemplateID, Long serviceTemplateInstanceID, QName planID,
                                   String operationName, String correlationID, String chorCorrelationId,
                                   String chorPartners) {
        this.csar = csar;
        this.serviceTemplateId = serviceTemplateID;
        this.serviceTemplateInstanceId = serviceTemplateInstanceID;
        this.planId = planID;
        this.operationName = operationName;
        this.correlationId = correlationID;
        this.chorCorrelationId = chorCorrelationId;
        this.chorPartners = chorPartners;
    }
}
