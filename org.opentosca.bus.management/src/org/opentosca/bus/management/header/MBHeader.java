package org.opentosca.bus.management.header;

import org.opentosca.container.core.model.csar.id.CSARID;

/**
 * Enum needed for the MB-components.<br>
 * <br>
 *
 * Copyright 2018 IAAS University of Stuttgart <br>
 * <br>
 *
 * This enum defines the headers of the camel exchange message that is used from all MB-components.
 *
 *
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 * @author Benjamin Weder - st100495@stud.uni-stuttgart.de
 *
 */
public enum MBHeader {
    /**
     * <b>CSARID</b> This header field contains an identifier for a CSAR file in OpenTOSCA (see
     * {@link CSARID}).
     */
    CSARID,

    SERVICEINSTANCEID_URI,

    NODEINSTANCEID_STRING,

    SERVICETEMPLATEID_QNAME,

    NODETEMPLATEID_STRING,

    RELATIONSHIPTEMPLATEID_STRING,

    NODETYPEID_QNAME,

    RELATIONSHIPTYPEID_QNAME,

    /**
     * <b>OPERATIONNAME_STRING</b> This header field specifies the interface which contains the
     * operation that shall be executed by passing the camel exchange message to an invocation
     * plug-in.
     */
    INTERFACENAME_STRING,

    /**
     * <b>OPERATIONNAME_STRING</b> This header field specifies the operation name of the operation
     * which shall be executed by passing the camel exchange message to an invocation plug-in.
     */
    OPERATIONNAME_STRING,

    /**
     * <b>PLANID_QNAME</b> This header field specifies the ID of a plan which shall be executed by
     * passing the camel exchange message to an invocation plug-in.
     */
    PLANID_QNAME,

    /**
     * <b>ENDPOINT_URI</b> This header field contains the endpoint of an Implementation Artifact or
     * a Plan.
     */
    ENDPOINT_URI,

    SPECIFICCONTENT_DOCUMENT,

    HASOUTPUTPARAMS_BOOLEAN,

    SYNCINVOCATION_BOOLEAN,

    APIID_STRING,

    ARTIFACTTEMPLATEID_QNAME,

    DEPLOYMENT_ARTIFACTS,

    /**
     * <b>OPERATIONSTATE_BOOLEAN</b> This header field contains the state of an operation or method
     * that is called by passing the camel exchange message to a service or plug-in. It is true if
     * the operation was called successful and false otherwise.
     */
    OPERATIONSTATE_BOOLEAN
}
