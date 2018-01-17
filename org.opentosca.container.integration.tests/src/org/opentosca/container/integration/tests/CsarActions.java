package org.opentosca.container.integration.tests;

import java.net.URL;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.opentosca.container.api.controller.CsarController;
import org.opentosca.container.api.dto.CsarUploadRequest;

public final class CsarActions {

  public static void uploadCsar(final URL csar) {
    final CsarController ctrl = ServiceTrackerUtil.getService(CsarController.class);
    final CsarUploadRequest request = new CsarUploadRequest();
    request.setName(csar.getFile());
    request.setUrl(csar.toString());
    ctrl.upload(request);
  }

  public static boolean hasCsar(final String id) {
    final CsarController ctrl = ServiceTrackerUtil.getService(CsarController.class);
    try {
      final Response response = ctrl.getCsar(id);
      if (response != null && response.getStatus() == Status.OK.getStatusCode()) {
        return true;
      }
    } catch (Exception e) {
      // Something went wrong, assume CSAR is not available
      return false;
    }
    return false;
  }

  public static void removeCsar(final String id) {
    final CsarController ctrl = ServiceTrackerUtil.getService(CsarController.class);
    ctrl.delete(id);
  }

  private CsarActions() {
    throw new UnsupportedOperationException();
  }
}
