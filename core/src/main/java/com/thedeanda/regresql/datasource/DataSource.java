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

    public static final String ENVPROP_DBURL = "datasource.url";
    public static final String ENVPROP_DBUSER = "datasource.username";
    public static final String ENVPROP_DBPASS = "datasource.password";

    public static final String PROP_DBURL = "url";
    public static final String PROP_DBUSER = "username";
    public static final String PROP_DBPASS = "password";

    public DataSource(Properties properties) {
        prepDriver();

        String url = properties.getProperty(PROP_DBURL);
        String user = properties.getProperty(PROP_DBUSER);
        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(properties.getProperty(PROP_DBPASS));

        log.info("Connecting to {} as {}", url, user);
        ds = new HikariDataSource(config);
    }

    public DataSource(String url, String username, String password) {
        prepDriver();

        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);

        log.info("Connecting to {} as {}", url, username);
        ds = new HikariDataSource(config);
    }

    private void prepDriver() {
        // fixes drivers when run from maven and cli, might need for other db's later.
        try {
            Class.forName("org.hsqldb.jdbc.JDBCDriver");
        } catch (ClassNotFoundException e) {
        }
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
        }
    }

    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

}

