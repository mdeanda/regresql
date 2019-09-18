package com.thedeanda.regresql.datastream;

import com.google.common.collect.ImmutableList;
import com.thedeanda.regresql.model.HeaderModel;
import com.thedeanda.regresql.model.RowModel;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CsvDataStreamSource implements DataStreamSource {
    private int cursor = 0;
    private final List<String> columns;
    private final List<CSVRecord> data;
    private CSVParser parser;

    public CsvDataStreamSource(File file) throws IOException {
        this(new FileReader(file));
    }

    public CsvDataStreamSource(InputStreamReader inputStreamReader) throws IOException {
        try {
            parser = new CSVParser(inputStreamReader, CSVFormat.DEFAULT.withHeader());
            columns = ImmutableList.copyOf(parser.getHeaderNames());

            //TODO: this loads entire dataset into memory, consider a streaming version
            this.data = parser.getRecords();
        } finally {
            parser.close();
            parser = null;
            inputStreamReader.close();
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
        if (data.size() <= cursor)
            return null;

        CSVRecord row = data.get(cursor++);
        List<String> rowData = new ArrayList<>();
        for (String col : columns) {
            rowData.add(row.get(col));
        }
        return RowModel.builder()
                .data(rowData)
                .build();
    }

    @Override
    public void close() throws Exception {
        if (parser!=null) parser.close();
    }
}
