package org.opentosca.container.core.next.repository;

import javax.sql.DataSource;

import org.opentosca.container.core.common.Settings;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class DataSourceRegistry {

    @Bean
    public DataSource opentoscaDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUsername("admin");
        dataSource.setPassword("admin");
        dataSource.setUrl("jdbc:h2:file:" + Settings.DBDIR.resolve("opentosca").toAbsolutePath());
        return dataSource;
    }
}
