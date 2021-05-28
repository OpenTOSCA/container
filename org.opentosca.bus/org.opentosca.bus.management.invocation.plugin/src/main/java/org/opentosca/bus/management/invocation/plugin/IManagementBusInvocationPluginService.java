package org.opentosca.bus.management.invocation.plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TOperation;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.opentosca.bus.management.header.MBHeader;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.service.CsarStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Interface of the Management Bus Invocation Plug-ins.<br>
 * <br>
 * <p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * <p>
 * The interface specifies two methods. One for invoking a service like an operation of an implementation artifact or a
 * plan and one method that returns the supported invocation-type of the plug-in.
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 */
public abstract class IManagementBusInvocationPluginService {

    final private static Logger LOG = LoggerFactory.getLogger(IManagementBusInvocationPluginService.class);

    /**
     * Invokes a service like an ImplementationArtifact or a Plan.
     *
     * @param exchange contains all needed information like endpoint of the service, the operation to invoke and the
     *                 data to be transferred.
     * @return the response of the invoked service as body of the exchange message.
     */
    abstract public Exchange invoke(Exchange exchange);

    /**
     * Returns the supported invocation-types of the plug-in.
     */
    abstract public List<String> getSupportedTypes();

    public Exchange respondViaMocking(final Exchange exchange, CsarStorageService storage) {

        final long waitTime = System.currentTimeMillis() + 10000;
        while (System.currentTimeMillis() > waitTime) {
            // busy waiting here...
        }

        final Message message = exchange.getIn();
        final Map<String, String> responseMap = new HashMap<>();

        final Object params = message.getBody();
        if (params != null && params instanceof HashMap && ((HashMap) params).containsValue("fault")) {
            responseMap.put("Fault", "managementBusMockFaultValue");
        }

        final String csarId = message.getHeader(MBHeader.CSARID.toString(), String.class);
        final String nodeTemplateId = message.getHeader(MBHeader.NODETEMPLATEID_STRING.toString(), String.class);
        final String interfaceName = message.getHeader(MBHeader.INTERFACENAME_STRING.toString(), String.class);
        final String operationName = message.getHeader(MBHeader.OPERATIONNAME_STRING.toString(), String.class);

        Csar csar = storage.findById(new CsarId(csarId));
        QName nodeTypeId = csar.entryServiceTemplate().getTopologyTemplate().getNodeTemplate(nodeTemplateId).getTypeAsQName();
        TNodeType nodeType = csar.nodeTypes().stream().filter(x -> x.getQName().equals(nodeTypeId)).findFirst().get();

        if (nodeType.getInterfaces() != null) {
            nodeType.getInterfaces().getInterface()
                .stream()
                .filter(x -> x.getName().equals(interfaceName)).findFirst()
                .ifPresent(x -> {
                    TOperation.OutputParameters outputParameters = x.getOperation().stream().filter(op ->
                        op.getName().equals(operationName)).findFirst().get().getOutputParameters();
                    if (outputParameters != null) {
                        outputParameters.getOutputParameter().forEach(param -> responseMap.put(param.getName(), "managementBusMockValue"));
                    }

                });
        }

        LOG.info("Returning following response:");
        LOG.info(responseMap.toString());
        exchange.getIn().setBody(responseMap);
        return exchange;
    }
}
