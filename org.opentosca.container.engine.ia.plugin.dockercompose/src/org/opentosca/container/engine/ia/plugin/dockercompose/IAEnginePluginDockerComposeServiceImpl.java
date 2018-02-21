package org.opentosca.container.engine.ia.plugin.dockercompose;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.opentosca.container.core.model.AbstractArtifact;
import org.opentosca.container.core.model.AbstractFile;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.service.IHTTPService;
import org.opentosca.container.core.tosca.model.TPropertyConstraint;
import org.opentosca.container.engine.ia.plugin.IIAEnginePluginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class IAEnginePluginDockerComposeServiceImpl implements IIAEnginePluginService {

    private static final String TYPES = "{http://toscafy.github.io/artifacttypes}DockerComposeArtifact, {http://toscafy.github.io/artifacttypes}any2api";
    private static final String CAPABILITIES = "http://docs.docker.com/compose, http://www.docker.com/products/docker-compose, http://github.com/docker/compose, http://www.any2api.org, http://github.com/any2api";
    private static final Logger LOG = LoggerFactory.getLogger(IAEnginePluginDockerComposeServiceImpl.class);

    private static final Map<String, String> ENDPOINTS = new HashMap<>(); // artifactId:
                                                                          // endpoint
    private static final Map<String, String> CONTEXT_PATHS = new HashMap<>(); // endpoint:
                                                                              // contextPath
    private static final Map<String, String> ARTIFACT_IDS = new HashMap<>(); // endpoint:
                                                                             // artifactId

    private IHTTPService httpService;


    @Override
    public URI deployImplementationArtifact(final CSARID csarId, final QName nodeTypeImplementationID,
                    final QName artifactTypeQName, final Document artifactContent, final Document properties,
                    final List<TPropertyConstraint> propertyConstraints, final List<AbstractArtifact> artifacts,
                    final List<String> requiredFeatures) {
        /*
         * ArtifactProperties: contextFile: 'context.tar.gz', serviceName: 'mysql-mgmt-api', containerPort:
         * '3000', endpointPath: '/', endpointKind: 'soap'
         *
         * AbstractFile: String filePath = warFile.getPath(); String fileName = warFile.getName();
         * java.io.File file = warFile.getFile().toFile();
         */
        final String artifactType = artifactTypeQName.getLocalPart(); // "DockerComposeArtifact",
        // "any2api"
        final String artifactName = IAEnginePluginDockerComposeServiceImpl.getProperty(properties, "artifactName");
        final String contextFile = IAEnginePluginDockerComposeServiceImpl.getProperty(properties, "contextFile");
        final String envFileContent = IAEnginePluginDockerComposeServiceImpl.getProperty(properties, "envFileContent"); // https://docs.docker.com/compose/env-file
        String serviceName = IAEnginePluginDockerComposeServiceImpl.getProperty(properties, "serviceName");
        String containerPort = IAEnginePluginDockerComposeServiceImpl.getProperty(properties, "containerPort");
        String endpointPath = IAEnginePluginDockerComposeServiceImpl.getProperty(properties, "endpointPath");
        String endpointKind = IAEnginePluginDockerComposeServiceImpl.getProperty(properties, "endpointKind");
        final String soapPortType = IAEnginePluginDockerComposeServiceImpl.getProperty(properties, "soapPortType");

        IAEnginePluginDockerComposeServiceImpl.LOG.info(
            "artifactType={} artifactName={} contextFile={} serviceName={} containerPort={} endpointPath={} endpointKind={} soapPortType={}",
            artifactType, artifactName, contextFile, serviceName, containerPort, endpointPath, endpointKind,
            soapPortType);

        if (endpointPath == null) {
            endpointPath = "";
        }

        final String artifactId = artifactType + artifactName + contextFile + envFileContent + serviceName
            + containerPort + endpointPath + endpointKind + soapPortType;

        String endpoint = IAEnginePluginDockerComposeServiceImpl.ENDPOINTS.get(artifactId);

        if (endpoint != null) {
            return IAEnginePluginDockerComposeServiceImpl.toUri(endpoint);
        }

        try {
            final String csarIdStr = IAEnginePluginDockerComposeServiceImpl.csarIdToStr(csarId);
            final AbstractFile context = IAEnginePluginDockerComposeServiceImpl.getFile(artifacts, contextFile);
            final String contextFilePath = context.getFile().toFile().getCanonicalPath();
            // String contextFileName = context.getName();
            // String contextPath = "/tmp/opentosca-docker-compose-" + csarIdStr
            // + "-" + serviceName;
            // String contextPath =
            // java.nio.file.Files.createTempDirectory("docker-compose-ia-").toString();
            final String contextPath = IAEnginePluginDockerComposeServiceImpl.TEMP_DIR + "/docker-compose-ia-"
                + java.util.UUID.randomUUID().toString();

            if (artifactType.equals("any2api")) {
                if (serviceName == null) {
                    serviceName = "api";
                }
                if (containerPort == null) {
                    containerPort = "3000";
                }
                if (endpointKind == null) {
                    endpointKind = "rest";
                }

                // String any2apiExecutablePath =
                // java.nio.file.Files.createTempDirectory("any2api-executable-").toString();
                final String any2apiExecutablePath = IAEnginePluginDockerComposeServiceImpl.TEMP_DIR
                    + "/any2api-executable-" + java.util.UUID.randomUUID().toString();

                IAEnginePluginDockerComposeServiceImpl.mkdirp(any2apiExecutablePath);

                if (contextFilePath.toLowerCase().endsWith(".json")) {
                    IAEnginePluginDockerComposeServiceImpl.copy(contextFilePath,
                        any2apiExecutablePath + "/apispec.json");
                } else {
                    IAEnginePluginDockerComposeServiceImpl.untar(contextFilePath, any2apiExecutablePath);
                }

                IAEnginePluginDockerComposeServiceImpl.any2apiGen(any2apiExecutablePath, contextPath, endpointKind);
            } else { // artifactType.equals("DockerComposeArtifact")
                IAEnginePluginDockerComposeServiceImpl.mkdirp(contextPath);

                if (contextFilePath.toLowerCase().endsWith(".yml") || contextFilePath.toLowerCase().endsWith(".yaml")) {
                    IAEnginePluginDockerComposeServiceImpl.copy(contextFilePath, contextPath + "/docker-compose.yml");
                } else {
                    IAEnginePluginDockerComposeServiceImpl.untar(contextFilePath, contextPath);
                }
            }

            if (envFileContent != null) {
                IAEnginePluginDockerComposeServiceImpl.write(contextPath + "/.env", envFileContent.trim());
            }

            IAEnginePluginDockerComposeServiceImpl.dcBuild(contextPath);

            IAEnginePluginDockerComposeServiceImpl.dcUp(contextPath);

            final String publicPort = IAEnginePluginDockerComposeServiceImpl.dcPort(contextPath, serviceName,
                containerPort);

            final String containerId = IAEnginePluginDockerComposeServiceImpl.dcId(contextPath, serviceName);

            if (IAEnginePluginDockerComposeServiceImpl.SHARED_NETWORK != null) {
                IAEnginePluginDockerComposeServiceImpl.dnConnect(IAEnginePluginDockerComposeServiceImpl.SHARED_NETWORK,
                    containerId);
            }

            final String containerIp = IAEnginePluginDockerComposeServiceImpl.diIp(containerId);

            // String logs = dcLogs(contextPath);

            endpoint = "http://localhost:" + publicPort + endpointPath;

            IAEnginePluginDockerComposeServiceImpl.ENDPOINTS.put(artifactId, endpoint);
            IAEnginePluginDockerComposeServiceImpl.CONTEXT_PATHS.put(endpoint, contextPath);
            IAEnginePluginDockerComposeServiceImpl.ARTIFACT_IDS.put(endpoint, artifactId);

            final String endpointJson = "{" + "\"artifactType\":  \"" + artifactType + "\"," + "\"artifactName\":  \""
                + artifactName + "\"," + "\"contextPath\":   \"" + contextPath + "\"," + "\"endpoint\":      \""
                + endpoint + "\"," + "\"publicPort\":    \"" + publicPort + "\"," + "\"containerPort\": \""
                + containerPort + "\"," + "\"containerId\":   \"" + containerId + "\"," + "\"containerIp\":   \""
                + containerIp + "\"," + "\"serviceName\":   \"" + serviceName + "\"," + "\"endpointPath\":  \""
                + endpointPath + "\"," + "\"endpointKind\":  \"" + endpointKind + "\"," + "\"soapPortType\":  \""
                + soapPortType + "\"," + "\"csar\":          \"" + csarIdStr + "\","
                + "\"deployed\":            true                " + "}";

            IAEnginePluginDockerComposeServiceImpl.append(IAEnginePluginDockerComposeServiceImpl.ENDPOINTS_FILE,
                endpointJson);
        } catch (final Exception e) {
            IAEnginePluginDockerComposeServiceImpl.LOG.error("Error deployImplementationArtifact", e);
        }

        IAEnginePluginDockerComposeServiceImpl.LOG.info("Docker Compose IA deployed: {}", endpoint);

        return IAEnginePluginDockerComposeServiceImpl.toUri(endpoint);
    }

    @Override
    public boolean undeployImplementationArtifact(final String iaName, final QName nodeTypeImpl, final CSARID csarId,
                    final URI endpointUri) {
        String endpoint = null;

        try {
            endpoint = endpointUri.toString();

            final String contextPath = IAEnginePluginDockerComposeServiceImpl.CONTEXT_PATHS.get(endpoint);
            final String artifactId = IAEnginePluginDockerComposeServiceImpl.ARTIFACT_IDS.get(endpoint);

            if (contextPath == null) {
                return true;
            }

            IAEnginePluginDockerComposeServiceImpl.dcDown(contextPath);

            IAEnginePluginDockerComposeServiceImpl.rmrf(contextPath);

            IAEnginePluginDockerComposeServiceImpl.ENDPOINTS.remove(artifactId);
            IAEnginePluginDockerComposeServiceImpl.CONTEXT_PATHS.remove(endpoint);
            IAEnginePluginDockerComposeServiceImpl.ARTIFACT_IDS.remove(endpoint);

            IAEnginePluginDockerComposeServiceImpl.append(IAEnginePluginDockerComposeServiceImpl.ENDPOINTS_FILE,
                "{" + "\"contextPath\":   \"" + contextPath + "\"," + "\"endpoint\":      \"" + endpoint + "\","
                    + "\"undeployed\":          true                " + "}");
        } catch (final Exception e) {
            IAEnginePluginDockerComposeServiceImpl.LOG.error("Error undeployImplementationArtifact", e);

            return false;
        }

        IAEnginePluginDockerComposeServiceImpl.LOG.info("Docker Compose IA undeployed: {}", endpoint);

        return true;
    }

    private static AbstractFile getFile(final List<AbstractArtifact> artifacts, final String filename) {
        if (artifacts != null && filename != null) {
            for (final AbstractArtifact artifact : artifacts) {
                final Set<AbstractFile> files = artifact.getFilesRecursively();

                for (final AbstractFile file : files) {
                    if (file.getName().toLowerCase().endsWith(filename.toLowerCase())) {
                        return file;
                    }
                }
            }
        }

        return null;
    }

    private static String getProperty(final Document properties, final String propertyName) {
        if (properties != null) {
            final NodeList list = properties.getFirstChild().getChildNodes();

            for (int i = 0; i < list.getLength(); i++) {
                final Node propertyNode = list.item(i);

                if (IAEnginePluginDockerComposeServiceImpl.hasProperty(propertyNode, propertyName)) {
                    final String propertyValue = IAEnginePluginDockerComposeServiceImpl.getNodeContent(propertyNode);

                    IAEnginePluginDockerComposeServiceImpl.LOG.info("{} property found: {}", propertyName,
                        propertyValue);

                    return propertyValue;
                }
            }
        }

        IAEnginePluginDockerComposeServiceImpl.LOG.debug("{} property not found", propertyName);

        return null;
    }

    private static boolean hasProperty(final Node node, final String propertyName) {
        final String localName = node.getLocalName();

        if (localName != null) {
            return localName.equals(propertyName);
        }

        return false;
    }

    private static String getNodeContent(final Node node) {
        return node.getTextContent().trim();
    }

    private static URI toUri(final String endpoint) {
        URI endpointURI = null;

        if (endpoint != null) {
            try {
                endpointURI = new URI(endpoint);
            } catch (final Exception e) {
                IAEnginePluginDockerComposeServiceImpl.LOG.error("Exception occurred while creating endpoint URI: {}",
                    endpoint, e);
            }
        }

        return endpointURI;
    }

    private static String csarIdToStr(final CSARID id) {
        if (id == null) {
            return null;
        } else {
            return id.toString(); // .replaceAll("[^-a-zA-Z0-9]", "");
        }
    }

    @Override
    public List<String> getSupportedTypes() {
        final List<String> types = new ArrayList<>();

        for (final String type : IAEnginePluginDockerComposeServiceImpl.TYPES.split("[,;]")) {
            types.add(type.trim());
        }

        return types;
    }

    @Override
    public List<String> getCapabilties() {
        final List<String> capabilities = new ArrayList<>();

        for (final String capability : IAEnginePluginDockerComposeServiceImpl.CAPABILITIES.split("[,;]")) {
            capabilities.add(capability.trim());
        }

        return capabilities;
    }

    // probably required for compatibility reasons
    public void bindHTTPService(final IHTTPService httpService) {
        if (httpService != null) {
            this.httpService = httpService;
            IAEnginePluginDockerComposeServiceImpl.LOG.debug("Register IHTTPService: {} registered",
                httpService.toString());
        } else {
            IAEnginePluginDockerComposeServiceImpl.LOG.error("Register IHTTPService: supplied parameter is null");
        }
    }

    // probably required for compatibility reasons
    public void unbindHTTPService(final IHTTPService httpService) {
        this.httpService = null;
        IAEnginePluginDockerComposeServiceImpl.LOG.debug("Unregister IHTTPService: {} unregistered",
            httpService.toString());
    }


    /*
     *
     * Static helper functions
     *
     */
    // private static final String DOCKER_COMPOSE_SCRIPT_URL =
    // "https://github.com/docker/compose/releases/download/1.8.0/run.sh";
    private static String DOCKER = System.getenv("OPENTOSCA_DOCKER_CMD");
    private static String DOCKER_COMPOSE = System.getenv("OPENTOSCA_DOCKER_COMPOSE_CMD");
    private static String SHARED_NETWORK = System.getenv("OPENTOSCA_DOCKER_COMPOSE_NET");
    private static String TEMP_DIR = System.getenv("OPENTOSCA_DOCKER_COMPOSE_TMP");
    private static String LOG_FILE = System.getenv("OPENTOSCA_DOCKER_COMPOSE_LOG");
    private static String ENDPOINTS_FILE = System.getenv("OPENTOSCA_ENDPOINTS_JSON");

    static {
        if (IAEnginePluginDockerComposeServiceImpl.DOCKER == null) {
            IAEnginePluginDockerComposeServiceImpl.DOCKER = "docker";
        }
        if (IAEnginePluginDockerComposeServiceImpl.DOCKER_COMPOSE == null) {
            IAEnginePluginDockerComposeServiceImpl.DOCKER_COMPOSE = "docker-compose";
        }

        if (IAEnginePluginDockerComposeServiceImpl.TEMP_DIR == null) {
            IAEnginePluginDockerComposeServiceImpl.TEMP_DIR = "/tmp";
        }
        if (IAEnginePluginDockerComposeServiceImpl.ENDPOINTS_FILE == null) {
            IAEnginePluginDockerComposeServiceImpl.ENDPOINTS_FILE = IAEnginePluginDockerComposeServiceImpl.TEMP_DIR
                + "/opentosca-docker-compose-endpoints.json";
        }

        if (IAEnginePluginDockerComposeServiceImpl.SHARED_NETWORK != null) {
            IAEnginePluginDockerComposeServiceImpl.dnCreate(IAEnginePluginDockerComposeServiceImpl.SHARED_NETWORK);
        }

        /*
         * if (DOCKER_COMPOSE == null) { try { DOCKER_COMPOSE =
         * java.nio.file.Files.createTempDirectory("docker-compose-").toString() + "/run.sh";
         *
         * fetchFile(DOCKER_COMPOSE_SCRIPT_URL, DOCKER_COMPOSE);
         *
         * log("docker-compose is available: " + DOCKER_COMPOSE); } catch (Exception e) {
         * e.printStackTrace(); } }
         */
    }


    private static void log(final String message) {
        try {
            if (IAEnginePluginDockerComposeServiceImpl.LOG_FILE != null) {
                IAEnginePluginDockerComposeServiceImpl.append(IAEnginePluginDockerComposeServiceImpl.LOG_FILE, message);
                // else System.out.println(message);
            } else {
                IAEnginePluginDockerComposeServiceImpl.LOG.info(message);
            }
        } catch (final Exception e) {
            // System.out.println(message);
            IAEnginePluginDockerComposeServiceImpl.LOG.info(message);
            IAEnginePluginDockerComposeServiceImpl.LOG.error("Error", e);
            // e.printStackTrace();
        }
    }

    private static void log(final String[] cmd, final String cwd, final String exitCode, final String stdout,
                    final String stderr) {
        final String message = "COMMAND " + java.util.Arrays.deepToString(cmd) + " (CWD " + cwd + ") EXIT " + exitCode
            + ". STDOUT: " + stdout + ". STDERR: " + stderr;

        IAEnginePluginDockerComposeServiceImpl.log(message);
    }

    private static void dcBuild(final String contextPath) throws Exception {
        final String[] cmd = {IAEnginePluginDockerComposeServiceImpl.DOCKER_COMPOSE, "build", "--force-rm"};
        IAEnginePluginDockerComposeServiceImpl.execCmd(cmd, contextPath);
    }

    private static void dcUp(final String contextPath) throws Exception {
        final String[] cmd = {IAEnginePluginDockerComposeServiceImpl.DOCKER_COMPOSE, "up", "-d", "--remove-orphans"};
        IAEnginePluginDockerComposeServiceImpl.execCmd(cmd, contextPath);
    }

    private static String dcLogs(final String contextPath) throws Exception {
        final String[] cmd = {IAEnginePluginDockerComposeServiceImpl.DOCKER_COMPOSE, "logs"};
        final String[] res = IAEnginePluginDockerComposeServiceImpl.execCmd(cmd, contextPath);

        final String logs = res[1];
        return logs;
    }

    private static String dcId(final String contextPath, final String serviceName) throws Exception {
        final String[] cmd = {IAEnginePluginDockerComposeServiceImpl.DOCKER_COMPOSE, "ps", "-q", serviceName};
        final String[] res = IAEnginePluginDockerComposeServiceImpl.execCmd(cmd, contextPath);

        final String containerId = res[1];
        return containerId;
    }

    private static String diIp(final String containerId) {
        try {
            // String[] cmd = { DOCKER, "inspect", "--format", "'{{
            // .NetworkSettings.IPAddress }}'", containerId };
            // docker inspect --format '{{(index .NetworkSettings.Networks
            // "opentosca-docker-compose").IPAddress}}' 8fe5baffbf26
            final String[] cmd = {IAEnginePluginDockerComposeServiceImpl.DOCKER, "inspect", "--format",
                                  "{{(index .NetworkSettings.Networks \""
                                      + IAEnginePluginDockerComposeServiceImpl.SHARED_NETWORK + "\").IPAddress}}",
                                  containerId};
            final String[] res = IAEnginePluginDockerComposeServiceImpl.execCmd(cmd);

            final String containerIp = res[1];
            return containerIp;
        } catch (final Exception e) {
            return null;
        }
    }

    private static void dnCreate(final String networkName) {
        try {
            final String[] cmd = {IAEnginePluginDockerComposeServiceImpl.DOCKER, "network", "create", networkName};
            final String[] res = IAEnginePluginDockerComposeServiceImpl.execCmd(cmd);
        } catch (final Exception e) {
            IAEnginePluginDockerComposeServiceImpl.LOG.info("cannot create Docker network " + networkName);
        }
    }

    private static void dnConnect(final String networkName, final String containerId) throws Exception {
        final String[] cmd = {IAEnginePluginDockerComposeServiceImpl.DOCKER, "network", "connect", networkName,
                              containerId};
        final String[] res = IAEnginePluginDockerComposeServiceImpl.execCmd(cmd);
    }

    private static String dcPort(final String contextPath, final String serviceName, final String containerPort) {
        try {
            final String[] cmd = {IAEnginePluginDockerComposeServiceImpl.DOCKER_COMPOSE, "port", serviceName,
                                  containerPort};
            final String[] res = IAEnginePluginDockerComposeServiceImpl.execCmd(cmd, contextPath);

            final String port = res[1].split(":")[1];
            return port;
        } catch (final Exception e) {
            return containerPort;
        }
    }

    private static void dcDown(final String contextPath) throws Exception {
        final String[] cmd = {IAEnginePluginDockerComposeServiceImpl.DOCKER_COMPOSE, "down", "--rmi", "all", "-v",
                              "--remove-orphans"};
        IAEnginePluginDockerComposeServiceImpl.execCmd(cmd, contextPath);
    }

    private static void any2apiGen(final String apispecPath, final String outputPath, final String endpointKind)
        throws Exception {
        final String[] cmd = {IAEnginePluginDockerComposeServiceImpl.DOCKER, "run", "--rm", "-v",
                              IAEnginePluginDockerComposeServiceImpl.TEMP_DIR + ":"
                                  + IAEnginePluginDockerComposeServiceImpl.TEMP_DIR,
                              "any2api/cli:legacy", "-i", endpointKind, "-c", "-o", outputPath, "gen", apispecPath};
        IAEnginePluginDockerComposeServiceImpl.execCmd(cmd, apispecPath);
    }

    private static void untar(final String filePath, final String dirPath) throws Exception {
        final String[] cmd = {"tar", "-xvzf", filePath, "-C", dirPath};
        IAEnginePluginDockerComposeServiceImpl.execCmd(cmd);
    }

    private static void copy(final String sourcePath, final String targetPath) throws Exception {
        final String[] cmd = {"cp", "-a", sourcePath, targetPath};
        IAEnginePluginDockerComposeServiceImpl.execCmd(cmd);
    }

    private static void mkdirp(final String dirPath) throws Exception {
        final String[] cmd = {"mkdir", "-p", dirPath};
        IAEnginePluginDockerComposeServiceImpl.execCmd(cmd);
    }

    private static void rmrf(final String dirPath) throws Exception {
        final String[] cmd = {"rm", "-rf", dirPath};
        IAEnginePluginDockerComposeServiceImpl.execCmd(cmd);
    }

    private static void append(final String filePath, final String content) throws Exception {
        final String touchCmd[] = {"touch", filePath};
        IAEnginePluginDockerComposeServiceImpl.execCmd(touchCmd);

        java.nio.file.Files.write(java.nio.file.Paths.get(filePath), (content + "\n").getBytes(),
            java.nio.file.StandardOpenOption.APPEND);
    }

    private static void write(final String filePath, final String content) throws Exception {
        java.nio.file.Files.write(java.nio.file.Paths.get(filePath), (content + "\n").getBytes());
    }

    /*
     * private static void fetchFile(String url, String filePath) throws Exception { java.net.URL
     * website = new java.net.URL(url);
     *
     * java.nio.channels.ReadableByteChannel rbc =
     * java.nio.channels.Channels.newChannel(website.openStream()); java.io.FileOutputStream fos = new
     * java.io.FileOutputStream(filePath); fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
     *
     * fos.close(); }
     */

    private static String[] execCmd(final String[] cmd) throws Exception {
        return IAEnginePluginDockerComposeServiceImpl.execCmd(cmd, null, null);
    }

    private static String[] execCmd(final String[] cmd, final String cwd) throws Exception {
        return IAEnginePluginDockerComposeServiceImpl.execCmd(cmd, cwd, null);
    }

    private static String[] execCmd(final String[] cmd, final String cwd, final String[] env) throws Exception {
        java.io.File cwdObj = null;
        if (cwd != null) {
            cwdObj = new java.io.File(cwd);
        }

        final Process proc = Runtime.getRuntime().exec(cmd, env, cwdObj);

        final java.io.InputStream stdoutStream = proc.getInputStream();
        final java.io.InputStream stderrStream = proc.getErrorStream();

        final java.util.Scanner stdoutScanner = new java.util.Scanner(stdoutStream).useDelimiter("\\A");
        final java.util.Scanner stderrScanner = new java.util.Scanner(stderrStream).useDelimiter("\\A");

        String stdout = "";
        if (stdoutScanner.hasNext()) {
            stdout = stdoutScanner.next();
        } else {
            stdout = "";
        }

        String stderr = "";
        if (stderrScanner.hasNext()) {
            stdout = stderrScanner.next();
        } else {
            stderr = "";
        }

        final int exitCode = proc.waitFor();
        // String exitCode = Integer.toString(proc.waitFor());
        // String exitCode = Integer.toString(proc.exitValue());

        IAEnginePluginDockerComposeServiceImpl.log(cmd, cwd, Integer.toString(exitCode), stdout.trim(), stderr.trim());

        if (exitCode != 0) {
            throw new Exception("COMMAND " + java.util.Arrays.deepToString(cmd) + " (CWD " + cwd + ") EXIT " + exitCode
                + ". STDOUT: " + stdout.trim() + ". STDERR: " + stderr.trim());
        }

        final String[] result = {Integer.toString(exitCode), stdout.trim(), stderr.trim()};

        return result;
    }

}
