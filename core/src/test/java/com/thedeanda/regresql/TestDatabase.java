package com.thedeanda.regresql;

import com.thedeanda.regresql.datasource.DataSource;
import org.hsqldb.server.Server;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

//TODO: revisit the ID as it can use a lot of memory if there are many tests
public class TestDatabase {
    Properties props;
    DataSource dataSource;
    static int id = 0;

    public TestDatabase() throws SQLException {
        props = new Properties();
        props.setProperty(DataSource.PROP_DBURL, "jdbc:hsqldb:mem:mymemdb" + id++);
        props.setProperty(DataSource.PROP_DBUSER, "SA");
        props.setProperty(DataSource.PROP_DBPASS, "");

        dataSource = new DataSource(props);
        createTestData();
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    private void createTestData() throws SQLException {
        Connection con = dataSource.getConnection();

        String sql = "create table user_account(ID integer, USERNAME varchar(64))";
        Statement statement = con.createStatement();
        statement.execute(sql);

        sql = "insert into user_account(ID, USERNAME) values (1, 'mdeanda'), (2, 'sodamnmad'), (3, 'fancyuser')";
        statement = con.createStatement();
        statement.execute(sql);
    }

    public void reset() throws SQLException {
        Connection con = dataSource.getConnection();
        try {
            Statement stmt = con.createStatement();
            try {
                stmt.execute("TRUNCATE SCHEMA PUBLIC RESTART IDENTITY AND COMMIT NO CHECK");
                con.commit();
            } finally {
                stmt.close();
            }
        } finally {
            con.close();
        }
    }
}
