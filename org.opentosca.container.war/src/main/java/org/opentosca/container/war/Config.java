package org.opentosca.container.war;

import org.opentosca.broker.mqtt.BrokerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class Config implements ServletContextListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);

  @Override
  public void contextInitialized(ServletContextEvent event) {
    LOGGER.info("STARTING APPLICATION CONTEXT CONFIGURATION FOR OPENTOSCA CONTAINER API");
    BrokerSupport mqttBroker = WebApplicationContextUtils
      .getRequiredWebApplicationContext(event.getServletContext())
      .getBean(BrokerSupport.class);
    mqttBroker.start();
  }

  @Override
  public void contextDestroyed(ServletContextEvent event) {
    LOGGER.info("SHUTTING DOWN APPLICATION CONTEXT");
    BrokerSupport mqttBroker = WebApplicationContextUtils
      .getRequiredWebApplicationContext(event.getServletContext())
      .getBean(BrokerSupport.class);
    mqttBroker.stop();
  }

}
