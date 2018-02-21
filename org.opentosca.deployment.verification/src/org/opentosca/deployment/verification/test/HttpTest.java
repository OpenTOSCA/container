package org.opentosca.deployment.verification.test;

import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Set;

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
import org.opentosca.deployment.verification.VerificationUtil;
import org.opentosca.planbuilder.model.tosca.AbstractNodeTemplate;
import org.opentosca.planbuilder.model.tosca.AbstractPolicyTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

public class HttpTest implements TestExecutionPlugin {

    public static final QName ANNOTATION_HTTP_TEST =
            new QName("http://opentosca.org/policytypes/annotations/tests", "HttpTest");
    public static final QName ANNOTATION_HTTPS_TEST =
            new QName("http://opentosca.org/policytypes/annotations/tests", "HttpsTest");

    private static Logger logger = LoggerFactory.getLogger(HttpTest.class);

    @Override
    public VerificationResult execute(final VerificationContext context,
            final AbstractNodeTemplate nodeTemplate,
            final NodeTemplateInstance nodeTemplateInstance,
            final AbstractPolicyTemplate policyTemplate) {

        logger.debug(
                "Execute test \"{}\" for node template \"{}\" (instance={}) based on policy template \"{}\"",
                this.getClass().getSimpleName(), nodeTemplate.getId(), nodeTemplateInstance.getId(),
                policyTemplate.getId());

        final VerificationResult result = new VerificationResult();
        result.setName(policyTemplate.getId());
        result.setNodeTemplateInstance(nodeTemplateInstance);
        result.start();

        if (policyTemplate.getProperties() == null) {
            throw new IllegalStateException("Properties of policy template not initialized");
        }

        Set<NodeTemplateInstance> nodes;

        // Input properties
        final Map<String, String> inputProperties = policyTemplate.getProperties().asMap();
        logger.debug("Input properties: {}", inputProperties);
        // TODO String testMethod = inputProperties.get("TestMethod");
        String testPath = inputProperties.get("TestPath");
        // TODO String testHeader = inputProperties.get("TestHeader");
        // TODO String testBody = inputProperties.get("TestBody");
        Integer expectedStatus = Integer.parseInt(inputProperties.get("ExpectedStatus"));
        // TODO String expectedHeader = inputProperties.get("ExpectedHeader");
        // TODO String expectedBody = inputProperties.get("ExpectedBody");
        String hostnameProperty = inputProperties.get("HostnamePropertyName");
        String port = inputProperties.get("Port");
        String portProperty = inputProperties.get("PortPropertyName");

        nodes = Sets.newHashSet(nodeTemplateInstance);
        VerificationUtil.resolveInfrastructureNodes(nodeTemplateInstance, context, nodes);
        final Map<String, String> nodeProperties =
                VerificationUtil.map(nodes, n -> n.getPropertiesAsMap());
        logger.debug("Node stack properties: {}", nodeProperties);

        /*
         * Resolve hostname
         */
        final String hostname = nodeProperties.get(hostnameProperty);
        if (Strings.isNullOrEmpty(hostname)) {
            result.append(String.format("Could not determine hostname by property \"%s\".",
                    hostnameProperty));
            result.failed();
            return result;
        }

        /*
         * Resolve port
         */
        if (Strings.isNullOrEmpty(port)) {
            logger.debug("Port not specified, try resolve it by property name...");
            nodes = Sets.newHashSet(nodeTemplateInstance);
            VerificationUtil.resolveChildNodes(nodeTemplateInstance, context, nodes);
            final Map<String, String> p = VerificationUtil.map(nodes, n -> n.getPropertiesAsMap());
            port = p.get(portProperty);
            if (Strings.isNullOrEmpty(port)) {
                result.append(String.format("Could not determine port by property \"%s\".",
                        portProperty));
                result.failed();
                return result;
            }
        }

        /*
         * Determine HTTPS or HTTP
         */
        String scheme = "http://";
        if (policyTemplate.getType().getId().equals(ANNOTATION_HTTPS_TEST)) {
            scheme = "https://";
        }

        final String url = scheme + hostname + ":" + port + testPath;
        logger.debug("URL: {}", url);
        try {
            final URL endpoint = new URL(url);
            final HttpURLConnection con = getConnection(endpoint, policyTemplate.getType().getId());
            con.setRequestMethod("GET");
            int status = con.getResponseCode();
            con.disconnect();
            if (status != expectedStatus) {
                result.append(String.format("Test failed: expected \"%s\" but was \"%s\".",
                        expectedStatus, status));
                result.failed();
            }
        } catch (Exception e) {
            logger.error("Error executing test: {}", e.getMessage(), e);
            result.append("Error executing test: " + e.getMessage());
            result.failed();
        }
        result.success();

        logger.info("Test executed: {}", result);
        return result;
    }

    private HttpURLConnection getConnection(final URL url, final QName annotation)
            throws Exception {
        final HttpURLConnection connection;
        if (annotation.equals(ANNOTATION_HTTPS_TEST)) {
            final SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(new KeyManager[0], new TrustManager[] {new LaxTrustManager()},
                    new SecureRandom());
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
    public boolean canExecute(final AbstractNodeTemplate nodeTemplate,
            final AbstractPolicyTemplate policyTemplate) {

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
