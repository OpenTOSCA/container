package org.opentosca.broker.mqtt;

import java.io.File;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Properties;

import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.common.file.ResourceAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.hash.Hashing;

import io.moquette.server.Server;

/**
 * This bundle is used to start a local MQTT broker for the OpenTOSCA ecosystem. The broker is
 * intended for the communication between different OpenTOSCA Container instances to, e.g., deploy
 * and call IAs in a collaborative manner. However, it can also be used for other communication via
 * MQTT. The port, username and password to access the MQTT broker can be defined in the global
 * config.ini file.<br>
 * <br>
 * <p>
 * Copyright 2018 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Benjamin Weder - st100495@stud.uni-stuttgart.de
 */
// FIXME reinstate this functionality
public class Activator {

  final private static Logger LOG = LoggerFactory.getLogger(Activator.class);

  private static final String METAINF_FOLDER = "/META-INF/";
  private static final String CONFIGFILE_PATH = "/credentials/password.config";

  private Server mqttBroker;

  public void start() throws Exception {
    Activator.LOG.info("Starting local MQTT broker at port: {}", Settings.OPENTOSCA_BROKER_MQTT_PORT);

    File credentialsFile = null;

    // try to create a credentials file
    try {
      // get META-INF folder as File
      final URL bundleResURL = getClass().getResource(METAINF_FOLDER);
      final Path fileResURL = ResourceAccess.resolveUrl(bundleResURL);
      // FIXME this will never work
      final File metainfFolder = fileResURL.toFile();

      // create a password file
      credentialsFile = new File(metainfFolder.getPath() + CONFIGFILE_PATH);
      credentialsFile.getParentFile().mkdirs();
      credentialsFile.createNewFile();

      // get username/password from config and create hash
      final String username = Settings.OPENTOSCA_BROKER_MQTT_USERNAME;
      final String passwordHash =
        Hashing.sha256().hashString(Settings.OPENTOSCA_BROKER_MQTT_PASSWORD, StandardCharsets.UTF_8).toString();

      // add username/password to the credentials file
      final PrintWriter writer = new PrintWriter(credentialsFile, "UTF-8");
      writer.println(username + ":" + passwordHash);
      writer.close();
    } catch (final Exception e) {
      Activator.LOG.error("Failed to create credentials file: ", e);
    }

    // Set properties for the local Moquette MQTT broker
    final Properties props = new Properties();
    props.put("port", String.valueOf(Settings.OPENTOSCA_BROKER_MQTT_PORT));
    props.put("host", "0.0.0.0");

    // Set the max message size according to the MQTT spec
    // http://docs.oasis-open.org/mqtt/mqtt/v3.1.1/errata01/os/mqtt-v3.1.1-errata01-os-complete.html#_Toc442180836
    props.put("netty.mqtt.message_size", "268435455");

    if (credentialsFile != null) {
      // set credentials file
      props.put("allow_anonymous", "false");
      props.put("password_file", credentialsFile.getPath());
    } else {
      // start broker without authentication
      props.put("allow_anonymous", "true");
      Activator.LOG.warn("Caution: Unable to create credentials file. Starting broker without authentication");
    }

    // start Moquette broker
    this.mqttBroker = new Server();
    this.mqttBroker.startServer(props);

    Activator.LOG.info("MQTT broker started");
  }

  public void stop() throws Exception {
    Activator.LOG.info("Stopping MQTT borker");

    this.mqttBroker.stopServer();

    Activator.LOG.info("MQTT broker stopped");
  }
}
