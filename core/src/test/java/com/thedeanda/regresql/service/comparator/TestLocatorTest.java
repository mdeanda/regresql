package com.thedeanda.regresql.service.comparator;

import com.thedeanda.regresql.model.TestSource;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

public class TestLocatorTest {
    private static final File SOURCE = new File("./src/test/resources/testlocator/dir1");
    private static final File EXPECTED = new File("./src/test/resources/testlocator/dir2");

    @Test
    public void testWithFiles() throws IOException {
        File s1 = new File(SOURCE, "/a/file1.sql");
        File s2 = new File(SOURCE, "/a/file2.sql");
        String e1 = "file1.csv";
        String e2 = "file2.csv";

        TestLocator locator = new TestLocator(SOURCE, EXPECTED);
        List<TestSource> tests = locator.locateTests();

        assertThat(tests).hasSize(2);
        TestSource t = tests.get(0);
        assertThat(t.getSource().getCanonicalPath()).isEqualTo(s1.getCanonicalPath());
        assertThat(t.getExpected().getName()).isEqualTo(e1);
        assertThat(t.getRelativePath()).isEqualTo("./a");


        t = tests.get(1);
        assertThat(t.getSource().getCanonicalPath()).isEqualTo(s2.getCanonicalPath());
        assertThat(t.getExpected().getName()).isEqualTo(e2);
        assertThat(t.getRelativePath()).isEqualTo("./a");
    }
}
