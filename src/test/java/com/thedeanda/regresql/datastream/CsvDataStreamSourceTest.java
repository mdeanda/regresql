package com.thedeanda.regresql.datastream;

import com.thedeanda.regresql.model.HeaderModel;
import com.thedeanda.regresql.model.RowModel;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class CsvDataStreamSourceTest {

    @Test
    public void testWithFile() throws IOException {
        CsvDataStreamSource src1 = new CsvDataStreamSource(new File("./src/test/resources/csv/simple.csv"));


        assertThat(src1).isNotNull();
        HeaderModel header = src1.getHeaderModel();
        assertThat(header.getColumnNames()).containsExactly("header1", "header2", "header3");

        RowModel row = src1.next();
        assertThat(row).isNotNull();
        assertThat(row.getData()).containsExactly("value1", "value 2", "value three");

        row = src1.next();
        assertThat(row).isNotNull();
        assertThat(row.getData()).containsExactly("2", "4", "6");

        row = src1.next();
        assertThat(row).isNull();

        row = src1.next();
        assertThat(row).isNull();
    }
}
