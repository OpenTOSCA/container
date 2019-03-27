package org.opentosca.bus.application.service.impl.servicehandler;

import org.opentosca.container.legacy.core.engine.IToscaEngineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 */
public class ToscaServiceHandler {

  final private static Logger LOG = LoggerFactory.getLogger(ToscaServiceHandler.class);

  private static IToscaEngineService toscaEngineService;


  /**
   * @return IToscaEngineService
   */
  public static IToscaEngineService getToscaEngineService() {
    return ToscaServiceHandler.toscaEngineService;
  }
}
