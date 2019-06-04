package org.opentosca.container.legacy.core.service;

import org.opentosca.container.legacy.core.engine.IToscaReferenceMapper;
import org.opentosca.container.core.engine.xml.IXMLSerializerService;
import org.opentosca.container.legacy.core.plan.CorrelationHandler;
import org.opentosca.container.core.service.ICSARInstanceManagementService;
import org.opentosca.container.legacy.core.engine.IToscaEngineService;
import org.osgi.service.event.EventAdmin;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * Static service handler which provides services of other OSGI components in a static way for
 * classes of this bundle.
 */
@Deprecated
@Service
public class ServiceProxy {

  @Inject
  public static IToscaEngineService toscaEngineService;
  @Inject
  public static IToscaReferenceMapper toscaReferenceMapper;
  @Inject
  public static IXMLSerializerService xmlSerializerService;
  @Inject
  public static ICSARInstanceManagementService csarInstanceManagement;

  public static CorrelationHandler correlationHandler = new CorrelationHandler();
}
