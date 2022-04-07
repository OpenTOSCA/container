package org.opentosca.bus.management.service.impl;

import java.net.URI;

import javax.xml.namespace.QName;

import org.opentosca.container.core.model.csar.CsarId;

class IAInvocationArguments {
    public final CsarId csarId;
    public final URI serviceInstanceId;
    public final QName serviceTemplateId;
    public final long serviceTemplateInstanceId;
    public final String nodeTemplateId;
    public final String relationshipTemplateId;
    public final String interfaceName;
    public final String operationName;

    public IAInvocationArguments(CsarId csarId, URI serviceInstanceId, QName serviceTemplateId,
                                 long serviceTemplateInstanceId, String nodeTemplateId,
                                 String relationshipTemplateId, String interfaceName, String operationName) {
        this.csarId = csarId;
        this.serviceInstanceId = serviceInstanceId;
        this.serviceTemplateId = serviceTemplateId;
        this.serviceTemplateInstanceId = serviceTemplateInstanceId;
        this.nodeTemplateId = nodeTemplateId;
        this.relationshipTemplateId = relationshipTemplateId;
        this.interfaceName = interfaceName;
        this.operationName = operationName;
    }
}
