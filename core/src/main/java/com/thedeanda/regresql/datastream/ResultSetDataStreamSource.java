package com.thedeanda.regresql.datastream;

import com.thedeanda.regresql.datasource.DataSource;
import com.thedeanda.regresql.model.HeaderModel;
import com.thedeanda.regresql.model.RowModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class ResultSetDataStreamSource implements DataStreamSource {
    private final DataSource datasource;
    private final String sql;
    private List<String> columns;

    Connection con;
    Statement statement;
    ResultSet resultSet;

    public ResultSetDataStreamSource(DataSource ds, File file) throws Exception {
        this.datasource = ds;
        try (FileInputStream fis = new FileInputStream(file)) {
            List<String> lines = IOUtils.readLines(fis, Charset.forName("UTF-8"));
            this.sql = lines.stream().collect(Collectors.joining("\n"));
        }
    }

    public ResultSetDataStreamSource(DataSource ds, String sqlQuery) throws Exception {
        this.datasource = ds;
        this.sql = sqlQuery;
    }

    public void init() throws Exception {
        if (statement != null) {
            throw new IllegalStateException("already initialized");
        }

        con = datasource.getConnection();

        try {
            statement = con.createStatement();
            resultSet = statement.executeQuery(sql);

            ResultSetMetaData rsmd = resultSet.getMetaData();
            int colCount = rsmd.getColumnCount();
            columns = new ArrayList<>();
            for (int i=0; i<colCount; i++) {
                columns.add(rsmd.getColumnName(i+1));
            }
        } catch (SQLException e ) {
            close();
            throw e;
        }

    }

    @Override
    public HeaderModel getHeaderModel() {
        return HeaderModel.builder()
                .columnNames(columns)
                .build();
    }


    @Override
    public RowModel next() {
        RowModel rowModel = null;
        try {
            if (resultSet.next()) {
                List<String> data = new ArrayList<>();
                for (String col : columns) {
                    data.add(resultSet.getString(col));
                }
                rowModel = RowModel.builder()
                        .data(data)
                        .build();
            }
        } catch (SQLException e) {
            log.warn(e.getMessage(), e);
        }
        return rowModel;
    }

    @Override
    public void close() throws Exception {
        if (statement != null) statement.close();
        if (resultSet != null) resultSet.close();
        if (con != null) con.close();
    }
}
