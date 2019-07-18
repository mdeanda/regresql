package com.thedeanda.regresql.datastream;

import com.thedeanda.regresql.datasource.DataSource;
import com.thedeanda.regresql.model.HeaderModel;
import com.thedeanda.regresql.model.RowModel;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

public class ResultSetDataStreamSourceTest {

    private static DataSource ds;

    @BeforeClass
    public static void init() {
        Properties props = new Properties(); //TODO: test properties
        ds = new DataSource(props);
    }

    @Ignore
    @Test
    public void testBasic() throws Exception {
        String sql = "select id, username \n\n\n --\n from user_account limit 2";
        ResultSetDataStreamSource source = new ResultSetDataStreamSource(ds, sql);
        source.init();

        HeaderModel header = source.getHeaderModel();
        assertThat(header.getColumnNames()).containsExactly("id", "username");

        RowModel row = source.next();
        assertThat(row).isNotNull();
        assertThat(row.getData()).containsExactly("1", "mdeanda");

        row = source.next();
        assertThat(row).isNotNull();
        assertThat(row.getData()).containsExactly("2", "sodamnmad");

        row = source.next();
        assertThat(row).isNull();

        source.close();
    }
}
