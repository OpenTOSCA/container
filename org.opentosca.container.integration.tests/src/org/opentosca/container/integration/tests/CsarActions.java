package org.opentosca.container.integration.tests;

import org.opentosca.container.api.service.CsarService;

public final class CsarActions {

  public static boolean hasCsar(final String id) {
    final CsarService service = ServiceTrackerUtil.getService(CsarService.class);
    return service.findById(id) != null;
  }

  private CsarActions() {
    throw new UnsupportedOperationException();
  }
}
