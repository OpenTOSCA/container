package org.opentosca.container.legacy.core.service;

import org.opentosca.container.core.engine.IToscaReferenceMapper;
import org.opentosca.container.core.engine.xml.IXMLSerializerService;
import org.opentosca.container.legacy.core.plan.CorrelationHandler;
import org.opentosca.container.core.service.ICSARInstanceManagementService;
import org.opentosca.container.legacy.core.engine.IToscaEngineService;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * Static service handler which provides services of other OSGI components in a static way for
 * classes of this bundle.
 */
@Deprecated
public class ServiceProxy {

  @Inject
  public static IToscaEngineService toscaEngineService = null;
  @Inject
  public static IToscaReferenceMapper toscaReferenceMapper = null;
  @Inject
  public static IXMLSerializerService xmlSerializerService = null;
  @Inject
  public static ICSARInstanceManagementService csarInstanceManagement = null;
  @Inject
  public static EventAdmin eventAdmin;

  public static CorrelationHandler correlationHandler = new CorrelationHandler();

  private final Logger LOG = LoggerFactory.getLogger(ServiceProxy.class);
}
