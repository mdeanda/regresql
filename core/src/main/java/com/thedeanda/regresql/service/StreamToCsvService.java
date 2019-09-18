package com.thedeanda.regresql.service;

import com.thedeanda.regresql.datastream.DataStreamSource;
import com.thedeanda.regresql.model.HeaderModel;
import com.thedeanda.regresql.model.RowModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;

@Slf4j
public class StreamToCsvService {
    public void convert(DataStreamSource dataStreamSource, File output) {
        RowModel record;
        output.getParentFile().mkdirs();
        output.delete();
        try (FileWriter fileWriter = new FileWriter(output);
                CSVPrinter printer = new CSVPrinter(fileWriter, CSVFormat.DEFAULT.withQuoteMode(QuoteMode.ALL))) {
            HeaderModel headerModel = dataStreamSource.getHeaderModel();
            printer.printRecord(headerModel.getColumnNames());
            while ((record = dataStreamSource.next()) != null) {
                printer.printRecord(record.getData());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
