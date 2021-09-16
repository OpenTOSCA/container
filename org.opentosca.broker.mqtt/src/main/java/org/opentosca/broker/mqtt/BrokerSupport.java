package org.opentosca.broker.mqtt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import com.google.common.hash.Hashing;
import io.moquette.server.Server;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.impl.service.FileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

@Component
public class BrokerSupport {
    private static final Logger LOG = LoggerFactory.getLogger(BrokerSupport.class);

    private static final String CONFIGFILE_PATH = "credentials/password.config";

    private Server mqttBroker;

    public synchronized void start() {
        if (!Boolean.parseBoolean(Settings.OPENTOSCA_COLLABORATION_MODE)) {
            LOG.info("Collaboration mode is turned off. Skipping MQTT broker startup.");
            return;
        }
        if (mqttBroker != null) {
            LOG.info("Registered attempt to start Broker again. Aborting!");
            return;
        }
        LOG.info("Starting local MQTT broker at port: {}", Settings.OPENTOSCA_BROKER_MQTT_PORT);
        // get username/password from config and create hash
        final String username = Settings.OPENTOSCA_BROKER_MQTT_USERNAME;
        final String passwordHash =
            Hashing.sha256().hashString(Settings.OPENTOSCA_BROKER_MQTT_PASSWORD, UTF_8).toString();
        final String fileEntry = username + ":" + passwordHash;
        // try to create a credentials file
        final Path credentialsFile = FileSystem.getTemporaryFolder().resolve(CONFIGFILE_PATH);
        try {
            // add username/password to the credentials file
            Files.createDirectories(credentialsFile.getParent());
            Files.write(credentialsFile, fileEntry.getBytes(UTF_8), TRUNCATE_EXISTING, CREATE, WRITE);
        } catch (final Exception e) {
            LOG.error("Failed to create credentials file: ", e);
        }

        // Set properties for the local Moquette MQTT broker
        final Properties props = new Properties();
        props.setProperty("port", Settings.OPENTOSCA_BROKER_MQTT_PORT);
        props.setProperty("host", "0.0.0.0");

        // Set the max message size according to the MQTT spec
        // http://docs.oasis-open.org/mqtt/mqtt/v3.1.1/errata01/os/mqtt-v3.1.1-errata01-os-complete.html#_Toc442180836
        props.setProperty("netty.mqtt.message_size", "268435455");

        if (Files.exists(credentialsFile)) {
            // set credentials file
            props.setProperty("allow_anonymous", "false");
            props.setProperty("password_file", credentialsFile.toAbsolutePath().toString());
        } else {
            // start broker without authentication
            props.setProperty("allow_anonymous", "true");
            LOG.warn("Caution: Unable to create credentials file. Starting broker without authentication");
        }

        // start Moquette broker
        mqttBroker = new Server();
        try {
            mqttBroker.startServer(props);
        } catch (IOException e) {
            LOG.warn("Starting MQTT broker produced an exception", e);
        }
        LOG.info("MQTT broker started");
    }

    public synchronized void stop() {
        LOG.info("Stopping MQTT borker");
        mqttBroker.stopServer();
        // reset local variable to allow calling start again
        mqttBroker = null;
        LOG.info("MQTT broker stopped");
    }
}
