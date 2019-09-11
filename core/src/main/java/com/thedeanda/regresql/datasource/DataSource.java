package com.thedeanda.regresql.datasource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

@Slf4j
public class DataSource {
    private HikariConfig config = new HikariConfig();
    private HikariDataSource ds;

    public static final String PROP_DBURL = "datasource.url";
    public static final String PROP_DBUSER = "datasource.username";
    public static final String PROP_DBPASS = "datasource.password";

    public DataSource(Properties properties) {
        String url = properties.getProperty(PROP_DBURL);
        String user = properties.getProperty(PROP_DBUSER);
        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(properties.getProperty(PROP_DBPASS));

        log.info("Connecting to {} as {}", url, user);
        ds = new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

}

