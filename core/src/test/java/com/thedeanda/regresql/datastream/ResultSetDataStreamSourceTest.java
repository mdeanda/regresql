package com.thedeanda.regresql.datastream;

import com.thedeanda.regresql.RegresqlService;
import com.thedeanda.regresql.TestDatabase;
import com.thedeanda.regresql.datasource.DataSource;
import com.thedeanda.regresql.model.HeaderModel;
import com.thedeanda.regresql.model.RowDifference;
import com.thedeanda.regresql.model.RowModel;
import com.thedeanda.regresql.model.TestSource;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

public class ResultSetDataStreamSourceTest {

    private DataSource ds;

    @Before
    public void init() throws SQLException {
        TestDatabase testDatabase = new TestDatabase();
        ds = testDatabase.getDataSource();
    }

    @Test
    public void testBasic() throws Exception {
        String sql = "select id, username \n\n\n --\n from user_account limit 2";
        ResultSetDataStreamSource source = new ResultSetDataStreamSource(ds, sql);
        source.init();

        HeaderModel header = source.getHeaderModel();
        assertThat(header.getColumnNames()).containsExactly("ID", "USERNAME");

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

    @Test
    public void testFromParsedFile() throws Exception {
        File source = new File("./src/test/resources/testdata/src/user.sql");
        File expected = new File("./src/test/resources/testdata/expected/user.csv");

        RegresqlService service = new RegresqlService(ds, source, expected);
        TestSource test = TestSource.builder()
                .expected(expected)
                .source(source)
                .baseName("user")
                .build();
        List<RowDifference> results = service.runTest(test, 10);

        assertThat(results).isNotNull().isEmpty();
    }

    @Test
    public void testFromParsedFileWithComments() throws Exception {
        File source = new File("./src/test/resources/testdata/src/user-comments.sql");
        File expected = new File("./src/test/resources/testdata/expected/user.csv");

        RegresqlService service = new RegresqlService(ds, source, expected);
        TestSource test = TestSource.builder()
                .expected(expected)
                .source(source)
                .baseName("user")
                .build();
        List<RowDifference> results = service.runTest(test, 10);

        assertThat(results).isNotNull().isEmpty();
    }
}
