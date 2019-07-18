package com.thedeanda.regresql.service.comparator;

import com.thedeanda.regresql.model.TestSource;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

public class TestLocatorTest {
    private static final File SOURCE = new File("./src/test/resources/testlocator/dir1");
    private static final File EXPECTED = new File("./src/test/resources/testlocator/dir2");

    @Test
    public void testWithFiles() {
        File s1 = new File(SOURCE, "/a/file1.sql");
        File s2 = new File(SOURCE, "/a/file2.sql");
        String e1 = "file1.csv";
        String e2 = "file2.csv";

        TestLocator locator = new TestLocator(SOURCE, EXPECTED);
        List<TestSource> tests = locator.locateTests();

        assertThat(tests).hasSize(2).extracting("source", "expected.name", "relativePath")
                .containsExactlyInAnyOrder(
                        tuple(s1, e1, "/a"),
                        tuple(s2, e2, "/a")
                );
    }
}
