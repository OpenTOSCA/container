package org.opentosca.deployment.checks.test;

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

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TPolicyTemplate;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import org.opentosca.container.core.next.model.DeploymentTestResult;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.deployment.checks.TestContext;
import org.opentosca.deployment.checks.TestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpTest implements org.opentosca.deployment.checks.test.TestExecutionPlugin {

    public static final QName ANNOTATION_HTTP_TEST =
        new QName("http://opentosca.org/policytypes/annotations/tests", "HttpTest");
    public static final QName ANNOTATION_HTTPS_TEST =
        new QName("http://opentosca.org/policytypes/annotations/tests", "HttpsTest");

    private static Logger logger = LoggerFactory.getLogger(HttpTest.class);

    @Override
    public DeploymentTestResult execute(final TestContext context, final TNodeTemplate nodeTemplate,
                                        final NodeTemplateInstance nodeTemplateInstance,
                                        final TPolicyTemplate policyTemplate) {

        logger.debug("Execute test \"{}\" for node template \"{}\" (instance={}) based on policy template \"{}\"",
            this.getClass().getSimpleName(), nodeTemplate.getId(), nodeTemplateInstance.getId(),
            policyTemplate.getId());

        final DeploymentTestResult result = new DeploymentTestResult();
        result.setName(policyTemplate.getId());
        result.setNodeTemplateInstance(nodeTemplateInstance);
        result.start();

        if (policyTemplate.getProperties() == null) {
            throw new IllegalStateException("Properties of policy template not initialized");
        }

        Set<NodeTemplateInstance> nodes;

        // Input properties
        final Map<String, String> inputProperties = (Map<String, String>) policyTemplate.getProperties();
        logger.debug("Input properties: {}", inputProperties);
        // TODO String testMethod = inputProperties.get("TestMethod");
        final String testPath = inputProperties.get("TestPath");
        // TODO String testHeader = inputProperties.get("TestHeader");
        // TODO String testBody = inputProperties.get("TestBody");
        final Integer expectedStatus = Integer.parseInt(inputProperties.get("ExpectedStatus"));
        // TODO String expectedHeader = inputProperties.get("ExpectedHeader");
        // TODO String expectedBody = inputProperties.get("ExpectedBody");
        final String hostnameProperty = inputProperties.get("HostnamePropertyName");
        String port = inputProperties.get("Port");
        final String portProperty = inputProperties.get("PortPropertyName");

        nodes = Sets.newHashSet(nodeTemplateInstance);
        TestUtil.resolveInfrastructureNodes(nodeTemplateInstance, context, nodes);
        final Map<String, String> nodeProperties = TestUtil.map(nodes, n -> n.getPropertiesAsMap());
        logger.debug("Node stack properties: {}", nodeProperties);

        /*
         * Resolve hostname
         */
        final String hostname = nodeProperties.get(hostnameProperty);
        if (Strings.isNullOrEmpty(hostname)) {
            result.append(String.format("Could not determine hostname by property \"%s\".", hostnameProperty));
            result.failed();
            return result;
        }

        /*
         * Resolve port
         */
        if (Strings.isNullOrEmpty(port)) {
            logger.debug("Port not specified, try resolve it by property name...");
            nodes = Sets.newHashSet(nodeTemplateInstance);
            TestUtil.resolveChildNodes(nodeTemplateInstance, context, nodes);
            final Map<String, String> p = TestUtil.map(nodes, n -> n.getPropertiesAsMap());
            port = p.get(portProperty);
            if (Strings.isNullOrEmpty(port)) {
                result.append(String.format("Could not determine port by property \"%s\".", portProperty));
                result.failed();
                return result;
            }
        }

        /*
         * Determine HTTPS or HTTP
         */
        String scheme = "http://";
        if (policyTemplate.getType().equals(ANNOTATION_HTTPS_TEST)) {
            scheme = "https://";
        }

        final String url = scheme + hostname + ":" + port + testPath;
        logger.debug("URL: {}", url);
        try {
            final URL endpoint = new URL(url);
            final HttpURLConnection con = getConnection(endpoint, policyTemplate.getType());
            con.setRequestMethod("GET");
            final int status = con.getResponseCode();
            con.disconnect();
            if (status != expectedStatus) {
                result.append(String.format("Test failed: expected \"%s\" but was \"%s\".", expectedStatus, status));
                result.failed();
            }
        } catch (final Exception e) {
            logger.error("Error executing test: {}", e.getMessage(), e);
            result.append("Error executing test: " + e.getMessage());
            result.failed();
        }
        result.success();

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
    public boolean canExecute(final TNodeTemplate nodeTemplate, final TPolicyTemplate policyTemplate) {

        if (policyTemplate.getType().equals(ANNOTATION_HTTP_TEST)
            || policyTemplate.getType().equals(ANNOTATION_HTTPS_TEST)) {
            return true;
        }

        return false;
    }

    // --- Helper classes

    private static class LaxTrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(final X509Certificate[] certificates,
                                       final String s) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(final X509Certificate[] certificates,
                                       final String s) throws CertificateException {
        }

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
