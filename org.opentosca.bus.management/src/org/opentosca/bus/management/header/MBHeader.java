package org.opentosca.bus.management.header;

/**
 * Enum needed for the MB-components.<br>
 * <br>
 *
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * This enum defines the headers of the camel exchange message that is used from all MB-components.
 *
 *
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 *
 */
public enum MBHeader {
    CSARID, SERVICEINSTANCEID_URI, NODEINSTANCEID_STRING, SERVICETEMPLATEID_QNAME, NODETEMPLATEID_STRING, RELATIONSHIPTEMPLATEID_STRING, NODETYPEID_QNAME, RELATIONSHIPTYPEID_QNAME, INTERFACENAME_STRING, OPERATIONNAME_STRING, PLANID_QNAME, ENDPOINT_URI, SPECIFICCONTENT_DOCUMENT, HASOUTPUTPARAMS_BOOLEAN, SYNCINVOCATION_BOOLEAN, APIID_STRING, ARTIFACTTEMPLATEID_QNAME, DEPLOYMENT_ARTIFACTS, OPENTOSCA_PUBLIC_IP
}
