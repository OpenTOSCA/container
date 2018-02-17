package org.opentosca.deployment.verification;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Before;
import org.junit.Test;
import org.opentosca.bus.management.header.MBHeader;
import org.opentosca.bus.management.service.IManagementBusService;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.next.utils.Types;
import org.opentosca.container.integration.tests.ServiceTrackerUtil;

public class TriggerOperationTest {

  private IManagementBusService bus;

  private final DefaultCamelContext camelContext = Activator.getCamelContext();


  private final ExecutorService executor = Executors.newFixedThreadPool(5);

  @Before
  public void init() {
    bus = ServiceTrackerUtil.getService(IManagementBusService.class);
  }


  @Test
  public void execute() throws Exception {

    final Map<String, Object> headers = new HashMap<>();
    headers.put(MBHeader.CSARID.toString(), new CSARID("Java_Web_Application__MySQL.csar"));
    headers.put(MBHeader.SERVICETEMPLATEID_QNAME.toString(),
        "{http://opentosca.org/servicetemplates}Java_Web_Application__MySQL");
    headers.put(MBHeader.SERVICEINSTANCEID_URI.toString(), new URI("25"));
    headers.put(MBHeader.NODETEMPLATEID_STRING.toString(), "Ubuntu-14_04-VM");
    headers.put(MBHeader.NODEINSTANCEID_STRING.toString(), "30");
    headers.put(MBHeader.INTERFACENAME_STRING.toString(), "OperatingSystemInterface");
    headers.put(MBHeader.OPERATIONNAME_STRING.toString(), "runScript");
    headers.put(MBHeader.HASOUTPUTPARAMS_BOOLEAN.toString(), true);
    headers.put(MBHeader.SYNCINVOCATION_BOOLEAN.toString(), true);

    final Map<String, Object> body = new HashMap<>();
    body.put("Script", "sudo lsof -n -iTCP:8080 | grep LISTEN");

    final ProducerTemplate producer = camelContext.createProducerTemplate();

    CompletableFuture<Map<String, String>> future = producer
        .asyncRequestBodyAndHeaders("direct:invokeIA", body, headers, Types.generify(Map.class));
    Map<String, String> response = future.get();

    Thread.sleep(1);
  }
}
