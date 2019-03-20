package org.opentosca.container.integration.tests;

import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.service.CsarStorageService;

public final class CsarActions {

  private CsarActions() {
    throw new UnsupportedOperationException();
  }

  public static boolean hasCsar(final String id) {
    final CsarStorageService service = ServiceTrackerUtil.getService(CsarStorageService.class);
    return service.findById(new CsarId(id)) != null;
  }
}
