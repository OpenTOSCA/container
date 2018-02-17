package org.opentosca.deployment.verification.test;

import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.namespace.QName;

import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.VerificationResult;
import org.opentosca.deployment.verification.VerificationContext;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractPolicyTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpTest implements TestExecutionPlugin {

  public static final QName ANNOTATION_HTTP_TEST =
      new QName("http://opentosca.org/policytypes/annotations", "HttpTest");
  public static final QName ANNOTATION_HTTPS_TEST =
      new QName("http://opentosca.org/policytypes/annotations", "HttpsTest");

  private static Logger logger = LoggerFactory.getLogger(HttpTest.class);


  @Override
  public VerificationResult execute(VerificationContext context, AbstractNodeTemplate nodeTemplate,
      NodeTemplateInstance nodeTemplateInstance, AbstractPolicyTemplate policyTemplate) {

    final VerificationResult result = new VerificationResult();
    result.setName(HttpTest.class.getSimpleName());
    result.setNodeTemplateInstance(nodeTemplateInstance);
    result.start();

    final String url = "https://www.google.com";
    try {
      final URL endpoint = new URL(url);
      final HttpURLConnection con = getConnection(endpoint, policyTemplate.getType().getId());
      con.setRequestMethod("GET");
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
      logger.error("Error executing test: {}", e.getMessage(), e);
      result.append(String.format("Could not connect to URL \"%s\": " + e.getMessage(), url));
      result.failed();
    }

    logger.info("Test executed: {}", result);
    return result;
  }

  private HttpURLConnection getConnection(final URL url, final QName annotation) throws Exception {
    final HttpURLConnection connection;
    if (annotation.equals(ANNOTATION_HTTPS_TEST)) {
      final SSLContext ctx = SSLContext.getInstance("TLS");
      ctx.init(new KeyManager[0], new TrustManager[] {new LaxTrustManager()}, new SecureRandom());
      SSLContext.setDefault(ctx);
      HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());
      final HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
      conn.setHostnameVerifier(new LaxHostnameVerifier());
      connection = conn;
    } else {
      connection = (HttpURLConnection) url.openConnection();
    }
    connection.setInstanceFollowRedirects(true);
    connection.setConnectTimeout(5000);
    connection.setReadTimeout(5000);
    return connection;
  }

  @Override
  public boolean canExecute(AbstractNodeTemplate nodeTemplate,
      AbstractPolicyTemplate policyTemplate) {

    if (policyTemplate.getType().getId().equals(ANNOTATION_HTTP_TEST)
        || policyTemplate.getType().getId().equals(ANNOTATION_HTTPS_TEST)) {
      return true;
    }

    return false;
  }

  // --- Helper classes

  private static class LaxTrustManager implements X509TrustManager {
    @Override
    public void checkClientTrusted(final X509Certificate[] certificates, final String s)
        throws CertificateException {}

    @Override
    public void checkServerTrusted(final X509Certificate[] certificates, final String s)
        throws CertificateException {}

    @Override
    public X509Certificate[] getAcceptedIssuers() {
      return null;
    }
  }

  private static class LaxHostnameVerifier implements HostnameVerifier {
    @Override
    public boolean verify(final String s, final SSLSession session) {
      return true;
    }
  }
}
