package org.opentosca.iaengine.plugins.dockercompose.service.impl;

import java.net.URI;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

import org.opentosca.core.model.artifact.AbstractArtifact;
import org.opentosca.core.model.artifact.file.AbstractFile;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.iaengine.plugins.service.IIAEnginePluginService;
import org.opentosca.model.tosca.TImplementationArtifact;
import org.opentosca.model.tosca.TPropertyConstraint;
import org.opentosca.util.http.service.IHTTPService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class IAEnginePluginDockerComposeServiceImpl implements IIAEnginePluginService {

	private static final String TYPES = "{http://toscafy.github.io/artifacttypes}DockerComposeArtifact";
	private static final String CAPABILITIES = "http://docs.docker.com/compose, http://www.docker.com/products/docker-compose, http://github.com/docker/compose";
	private static final Logger LOG = LoggerFactory.getLogger(IAEnginePluginDockerComposeServiceImpl.class);

	private static final Map<String, String> CONTEXT = new HashMap<String, String>();

	private IHTTPService httpService;

	@Override
	public URI deployImplementationArtifact(CSARID csarId, QName artifactType, Document artifactContent,
			Document properties, List<TPropertyConstraint> propertyConstraints, List<AbstractArtifact> artifacts,
			List<String> requiredFeatures) {
		/*
		ArtifactProperties:
			contextFile: 'context.tar.gz',
			serviceName: 'mysql-mgmt-api',
			containerPort: '3000',
			endpointPath: '/',
			endpointKind: 'soap'

		AbstractFile:
			String filePath = warFile.getPath();
			String fileName = warFile.getName();
			java.io.File file = warFile.getFile().toFile();
		*/
		String contextFile = getProperty(properties, "contextFile");
		String serviceName = getProperty(properties, "serviceName");
		String containerPort = getProperty(properties, "containerPort");
		String endpointPath = getProperty(properties, "endpointPath");
		String endpointKind = getProperty(properties, "endpointKind");

		LOG.info("contextFile={} serviceName={} containerPort={} endpointPath={} endpointKind={}", contextFile, serviceName, containerPort, endpointPath, endpointKind);

		String endpoint = null;

		try {
			String csarIdStr = normalizeCsarId(csarId);
			AbstractFile context = getFile(artifacts, contextFile);
			String contextFilePath = context.getPath();
			//String contextFileName = context.getName();
			//String contextPath = "/tmp/opentosca-docker-compose-" + csarIdStr + "-" + serviceName;
			String contextPath = java.nio.file.Files.createTempDirectory("docker-compose-ia-").toString();

			untar(contextFilePath, contextPath);

			dcBuild(contextPath);

			dcUp(contextPath);

			String publicPort = dcPort(contextPath, serviceName, containerPort);

			//String logs = dcLogs(contextPath);

			endpoint = "http://localhost:" + publicPort + endpointPath;

			CONTEXT.put(endpoint, contextPath);

			append(ENDPOINTS_FILE, "{"
			                     + "\"contextPath\":   \"" + contextPath   + "\","
			                     + "\"endpoint\":      \"" + endpoint      + "\","
			                     + "\"publicPort\":    \"" + publicPort    + "\","
			                     + "\"containerPort\": \"" + containerPort + "\","
			                     + "\"serviceName\":   \"" + serviceName   + "\","
			                     + "\"endpointPath\":  \"" + endpointPath  + "\","
			                     + "\"endpointKind\":  \"" + endpointKind  + "\","
			                     + "\"csar\":          \"" + csarIdStr     + "\""
													 + "}");
 		} catch (Exception e) {
			LOG.error("Error deployImplementationArtifact", e);
 		}

		return toUri(endpoint);
	}

	@Override
	public boolean undeployImplementationArtifact(String iaName, QName nodeTypeImpl, CSARID csarId, URI endpointUri) {
		try {
			String endpoint = endpointUri.toString();

			String contextPath = CONTEXT.get(endpoint);

			dcDown(contextPath);

			rmrf(contextPath);

			CONTEXT.remove(endpoint);
		} catch (Exception e) {
			LOG.error("Error undeployImplementationArtifact", e);

			return false;
		}

		return true;
	}

	private static AbstractFile getFile(List<AbstractArtifact> artifacts, String filename) {
		if (artifacts != null && filename != null) {
			for (AbstractArtifact artifact : artifacts) {
				Set<AbstractFile> files = artifact.getFilesRecursively();

				for (AbstractFile file : files) {
					if (file.getName().toLowerCase().endsWith(filename.toLowerCase())) {
						return file;
					}
				}
			}
		}

		return null;
	}

	/*
	private boolean isADeployableWar(AbstractFile file) {

		if (file.getName().toLowerCase().endsWith(".war")) {
			return true;
		} else {
			LOG.warn(
					"Although the plugin-type and the IA-type are matching, the file {} can't be un-/deployed from this plugin.",
					file.getName());
		}

		return false;
	}
	*/

	private static String getProperty(Document properties, String propertyName) {
		if (properties != null) {
			NodeList list = properties.getFirstChild().getChildNodes();

			for (int i = 0; i < list.getLength(); i++) {
				Node propertyNode = list.item(i);

				if (hasProperty(propertyNode, propertyName)) {
					String propertyValue = getNodeContent(propertyNode);

					LOG.info("{} property found: {}", propertyName, propertyValue);

					return propertyValue;
				}
			}
		}

		LOG.debug("{} property not found", propertyName);

		return null;
	}

	private static boolean hasProperty(Node node, String propertyName) {
		String localName = node.getLocalName();

		if (localName != null) {
			return localName.equals(propertyName);
		}

		return false;
	}

	private static String getNodeContent(Node node) {
		return node.getTextContent().trim();
	}

	private static URI toUri(String endpoint) {
		URI endpointURI = null;

		if (endpoint != null) {
			try {
				endpointURI = new URI(endpoint);
			} catch (Exception e) {
				LOG.error("Exception occurred while creating endpoint URI: {}", endpoint, e);
			}
		}

		return endpointURI;
	}

	private static String normalizeCsarId(CSARID id) {
		if (id == null) return null;
		else return id.toString().replaceAll("[^-a-zA-Z0-9]", "");
	}

	@Override
	public List<String> getSupportedTypes() {
		List<String> types = new ArrayList<String>();

		for (String type : IAEnginePluginDockerComposeServiceImpl.TYPES.split("[,;]")) {
			types.add(type.trim());
		}

		return types;
	}

	@Override
	public List<String> getCapabilties() {
		List<String> capabilities = new ArrayList<String>();

		for (String capability : IAEnginePluginDockerComposeServiceImpl.CAPABILITIES.split("[,;]")) {
			capabilities.add(capability.trim());
		}

		return capabilities;
	}

	// probably required for compatibility reasons
	public void bindHTTPService(IHTTPService httpService) {
		if (httpService != null) {
			this.httpService = httpService;
			LOG.debug("Register IHTTPService: {} registered", httpService.toString());
		} else {
			LOG.error("Register IHTTPService: supplied parameter is null");
		}
	}

	// probably required for compatibility reasons
	public void unbindHTTPService(IHTTPService httpService) {
		this.httpService = null;
		LOG.debug("Unregister IHTTPService: {} unregistered", httpService.toString());
	}



  private static final String DOCKER_COMPOSE_SCRIPT_URL = "https://github.com/docker/compose/releases/download/1.8.0/run.sh";
  private static String DOCKER_COMPOSE = System.getenv("OPENTOSCA_DOCKER_COMPOSE_SCRIPT");
  private static String LOG_FILE = System.getenv("OPENTOSCA_DOCKER_COMPOSE_LOG");
  private static String ENDPOINTS_FILE = System.getenv("OPENTOSCA_ENDPOINTS_JSON");

  static {
      if (ENDPOINTS_FILE == null) ENDPOINTS_FILE = "/tmp/opentosca-docker-compose-endpoints.json";

      if (DOCKER_COMPOSE == null) {
          try {
              DOCKER_COMPOSE = java.nio.file.Files.createTempDirectory("docker-compose-").toString() + "/run.sh";

              fetchFile(DOCKER_COMPOSE_SCRIPT_URL, DOCKER_COMPOSE);

              log("docker-compose is available: " + DOCKER_COMPOSE);
          } catch (Exception e) {
              e.printStackTrace();
          }
      }
  }

  private static void log(String message) {
      try {
          if (LOG_FILE != null) append(LOG_FILE, message);
          //else System.out.println(message);
          else LOG.info(message);
      } catch (Exception e) {
          //System.out.println(message);
          LOG.info(message);
          LOG.error("Error", e);
          //e.printStackTrace();
      }
  }

  private static void log(String[] cmd, String exitCode, String stdout, String stderr) {
      String message = "Command " + cmd.toString() + " exit " + exitCode + ". stdout: " + stdout + ". stderr: " + stderr;

      log(message);
  }

  private static void dcBuild(String contextPath) throws Exception {
      String[] cmd = { "bash", DOCKER_COMPOSE, "build", "--force-rm" };
      execCmd(cmd, contextPath);
  }

  private static void dcUp(String contextPath) throws Exception {
      String[] cmd = { "bash", DOCKER_COMPOSE, "up", "-d", "--remove-orphans" };
      execCmd(cmd, contextPath);
  }

  private static String dcLogs(String contextPath) throws Exception {
      String[] cmd = { "bash", DOCKER_COMPOSE, "logs" };
      String[] res = execCmd(cmd, contextPath);

      String logs = res[1];
      return logs;
  }

  private static String dcPort(String contextPath, String serviceName, String containerPort) throws Exception {
      String[] cmd = { "bash", DOCKER_COMPOSE, "port", serviceName, containerPort };
      String[] res = execCmd(cmd, contextPath);

      String port = res[1];
      return port;
  }

  private static void dcDown(String contextPath) throws Exception {
      String[] cmd = { "bash", DOCKER_COMPOSE, "down", "--rmi", "all", "-v", "--remove-orphans" };
      execCmd(cmd, contextPath);
  }

  private static void untar(String filePath, String dirPath) throws Exception {
      String[] cmd = { "tar", "-xvzf", filePath, "-C", dirPath };
      execCmd(cmd);
  }

  private static void rmrf(String dirPath) throws Exception {
      String[] cmd = { "rm", "-rf", dirPath };
      execCmd(cmd);
  }

  private static void append(String filePath, String content) throws Exception {
      String touchCmd[] = { "touch", filePath };
      execCmd(touchCmd);

      java.nio.file.Files.write(java.nio.file.Paths.get(filePath), (content + "\n").getBytes(), java.nio.file.StandardOpenOption.APPEND);
  }

  private static void fetchFile(String url, String filePath) throws Exception {
      java.net.URL website = new java.net.URL(url);

      java.nio.channels.ReadableByteChannel rbc = java.nio.channels.Channels.newChannel(website.openStream());
      java.io.FileOutputStream fos = new java.io.FileOutputStream(filePath);
      fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

      fos.close();
  }

  private static String[] execCmd(String[] cmd) throws Exception {
      return execCmd(cmd, null, null);
  }

  private static String[] execCmd(String[] cmd, String cwd) throws Exception {
      return execCmd(cmd, cwd, null);
  }

  private static String[] execCmd(String[] cmd, String cwd, String[] env) throws Exception {
      Process proc = Runtime.getRuntime().exec(cmd, env, new java.io.File(cwd));

      java.io.InputStream stdoutStream = proc.getInputStream();
      java.io.InputStream stderrStream = proc.getErrorStream();

      java.util.Scanner stdoutScanner = new java.util.Scanner(stdoutStream).useDelimiter("\\A");
      java.util.Scanner stderrScanner = new java.util.Scanner(stderrStream).useDelimiter("\\A");

      String stdout = "";
      if (stdoutScanner.hasNext()) stdout = stdoutScanner.next();
      else stdout = "";

      String stderr = "";
      if (stderrScanner.hasNext()) stdout = stderrScanner.next();
      else stderr = "";

      int exitCode = proc.waitFor();
      //String exitCode = Integer.toString(proc.waitFor());
      //String exitCode = Integer.toString(proc.exitValue());

      log(cmd, Integer.toString(exitCode), stdout.trim(), stderr.trim());

      if (exitCode != 0) {
          throw new Exception("Command " + cmd.toString() + " exit " + exitCode + ". stdout: " + stdout.trim() + ". stderr: " + stderr.trim());
      }

      String[] result = { Integer.toString(exitCode), stdout.trim(), stderr.trim() };

      return result;
  }

}
