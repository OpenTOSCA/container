package org.opentosca.container.war;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

@WebListener
public class Config implements ServletContextListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);

    @Override
    public void contextInitialized(ServletContextEvent event) {
        LOGGER.info("STARTING APPLICATION CONTEXT CONFIGURATION FOR OPENTOSCA CONTAINER API");
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        LOGGER.info("SHUTTING DOWN APPLICATION CONTEXT");
    }
}
