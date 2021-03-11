package org.opentosca.container.war;

import java.util.Collections;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;

import ch.qos.logback.ext.spring.web.LogbackConfigListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.filter.CharacterEncodingFilter;

@SpringBootApplication
@ImportResource({"classpath:spring/root-context.xml"})
public class Application implements ServletContextInitializer {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Application.class);
        application.setDefaultProperties(Collections.singletonMap("server.port", "1337"));
        application.run(args);
    }

    @Override
    public void onStartup(ServletContext servletContext) {
        // this whole thing here should replace the web.xml under WEB-INF to enable springboot support
        // However, as springboot praises a "conventions over configurations"-approach we have to _configure_ here a little bit, the @ImportResource of this class, and add @Component to SpringJaxRSConfiguration
        // Conventions are great
        servletContext.setInitParameter("logbackConfigLocation", "classpath:logback.xml");

        servletContext.addListener(LogbackConfigListener.class);
        servletContext.addListener(Config.class);

        FilterRegistration.Dynamic encodingFilter = servletContext.addFilter("encoding-filter", CharacterEncodingFilter.class);
        encodingFilter.setInitParameter("encoding", "UTF-8");
        encodingFilter.addMappingForUrlPatterns(null, true, "/*");
    }
}
