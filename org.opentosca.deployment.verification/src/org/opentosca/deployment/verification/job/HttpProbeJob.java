package org.opentosca.deployment.verification.job;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.model.VerificationResult;
import org.opentosca.container.core.next.xml.DomUtil;
import org.opentosca.deployment.verification.VerificationContext;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class HttpProbeJob implements ServiceTemplateJob {

  @Override
  public VerificationResult execute(final VerificationContext context,
      final AbstractServiceTemplate serviceTemplate,
      final ServiceTemplateInstance serviceTemplateInstance) {

    final VerificationResult result = new VerificationResult();
    result.setName(HttpProbeJob.class.getSimpleName());
    result.setServiceTemplateInstance(serviceTemplateInstance);
    result.start();

    final Map<String, String> properties = serviceTemplateInstance.getPropertiesAsMap();
    final String url = Jobs.resolveUrl(properties);
    try {
      final URL endpoint = new URL(url);
      final HttpURLConnection con = (HttpURLConnection) endpoint.openConnection();
      con.setInstanceFollowRedirects(true);
      con.setRequestMethod("GET");
      con.setConnectTimeout(5000);
      con.setReadTimeout(5000);
      int status = con.getResponseCode();
      con.disconnect();
      if (status >= 200 && status < 400) {
        result.append(String.format("Successfully connected to URL \"%s\".", url));
        result.success();
      } else {
        result.append(
            String.format("Could not connect to URL \"%s\"; status code was \"%d\"", url, status));
        result.failed();
      }
    } catch (Exception e) {
      result.append(String.format("Could not connect to URL \"%s\": " + e.getMessage(), url));
      result.failed();
    }
    return result;
  }

  @Override
  public boolean canExecute(final AbstractServiceTemplate serviceTemplate) {

    final Element el =
        serviceTemplate.getBoundaryDefinitions().getProperties().getProperties().getDOMElement();
    if (el.hasChildNodes()) {
      final NodeList nodes = el.getChildNodes();
      if (DomUtil.matchesNodeName(".*selfserviceapplicationurl.*", nodes)) {
        return true;
      }
    } else {
      if (el.getLocalName().equalsIgnoreCase("selfserviceapplicationurl")) {
        return true;
      }
    }
    return false;
  }
}
