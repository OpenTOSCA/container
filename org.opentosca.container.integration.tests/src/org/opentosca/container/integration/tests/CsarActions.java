package org.opentosca.container.integration.tests;

import org.opentosca.container.api.service.CsarService;

public final class CsarActions {

  // public static void uploadCsar(final URL csar) {
  // final CsarController ctrl = ServiceTrackerUtil.getService(CsarController.class);
  // final CsarUploadRequest request = new CsarUploadRequest();
  // request.setName(csar.getFile());
  // request.setUrl(csar.toString());
  // ctrl.upload(request);
  // }

  public static boolean hasCsar(final String id) {
    final CsarService service = ServiceTrackerUtil.getService(CsarService.class);
    if (service.findById(id) != null) {
      return true;
    }
    return false;
  }

  // public static void removeCsar(final String id) {
  // final CsarController ctrl = ServiceTrackerUtil.getService(CsarController.class);
  // ctrl.delete(id);
  // }

  private CsarActions() {
    throw new UnsupportedOperationException();
  }
}
