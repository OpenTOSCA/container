package org.opentosca.bus.application.service.impl.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.opentosca.bus.application.model.constants.ApplicationBusConstants;
import org.opentosca.bus.application.model.exception.ApplicationBusExternalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ParameterCheckProcessor of the Application Bus.<br>
 * <br>
 *
 * This processor checks if all needed parameters are specified..
 *
 *
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 *
 */
public class ParameterCheckProcessor implements Processor {

    final private static Logger LOG = LoggerFactory.getLogger(ParameterCheckProcessor.class);

    @Override
    public void process(final Exchange exchange) throws ApplicationBusExternalException {

        ParameterCheckProcessor.LOG.info("Checking if all needed parameters are specified...");

        final Message message = exchange.getIn();

        final Integer serviceInstanceID =
            message.getHeader(ApplicationBusConstants.SERVICE_INSTANCE_ID_INT.toString(), Integer.class);
        ParameterCheckProcessor.LOG.debug("serviceInstanceID: {}", serviceInstanceID);

        final String nodeTemplateID =
            message.getHeader(ApplicationBusConstants.NODE_TEMPLATE_ID.toString(), String.class);
        ParameterCheckProcessor.LOG.debug("nodeTemplateID: {}", nodeTemplateID);

        final Integer nodeInstanceID =
            message.getHeader(ApplicationBusConstants.NODE_INSTANCE_ID_INT.toString(), Integer.class);
        ParameterCheckProcessor.LOG.debug("nodeInstanceID: {}", nodeInstanceID);

        final String interfaceName = message.getHeader(ApplicationBusConstants.INTERFACE_NAME.toString(), String.class);
        ParameterCheckProcessor.LOG.debug("interfaceName: {}", interfaceName);

        final String operationName = message.getHeader(ApplicationBusConstants.OPERATION_NAME.toString(), String.class);
        ParameterCheckProcessor.LOG.debug("operationName: {}", operationName);

        if (serviceInstanceID != null || nodeInstanceID != null) {

            final StringBuilder error = new StringBuilder();

            if (interfaceName == null) {
                error.append(" >>Interface<<");
            }

            if (operationName == null) {
                error.append(" >>Operation<<");
            }

            if (serviceInstanceID != null) {

                if (nodeTemplateID == null) {
                    error.append(" >>NodeTemplateID<<");
                }

            }

            if (!error.toString().isEmpty()) {
                ParameterCheckProcessor.LOG.warn("Can't process request: " + error.toString() + " is missing.");
                throw new ApplicationBusExternalException(
                    "Can't process request: " + error.toString() + " is missing.");
            }

        } else {
            ParameterCheckProcessor.LOG.warn("Can't process request: neither >>ServiceInstanceID<< nor >>NodeInstanceID<< is specified!");
            throw new ApplicationBusExternalException(
                "Can't process request: neither >>ServiceInstanceID<< nor >>NodeInstanceID<< is specified!");
        }

    }

}
