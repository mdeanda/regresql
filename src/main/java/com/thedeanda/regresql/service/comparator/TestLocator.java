package com.thedeanda.regresql.service.comparator;

import com.thedeanda.regresql.model.TestSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
public class TestLocator {
    public static final String SOURCE_EXT = ".sql";
    public static final String EXPECTED_EXT = ".csv";

    private final File sourcePath;
    private final File expectedPath;


    public TestLocator(File sourcePath, File expectedPath) {
        this.sourcePath = sourcePath;
        this.expectedPath = expectedPath;
    }


    public List<TestSource> locateTests() {
        List<TestSource> results = new ArrayList<>();

        findTests(sourcePath, "", results);

        return results;
    }

    private void findTests(File base, String extraPath, List<TestSource> results) {
        File src = new File(base, extraPath);
        File[] files = src.listFiles(f ->
                f.isDirectory() || StringUtils.endsWithIgnoreCase(f.getName(), SOURCE_EXT)
        );

        Stream.of(files).filter(f -> !f.isDirectory()).forEach(f -> {
            results.add(TestSource.builder()
                    .source(f)
                    .expected(findExpected(extraPath, f))
                    .relativePath(extraPath)
                    .build());
        });

        Stream.of(files).filter(File::isDirectory).forEach(f -> {
            findTests(base, extraPath + File.separator + f.getName(), results);
        });
    }

    private File findExpected(String extraPath, File f) {
        String fn = extraPath + File.separator + StringUtils.removeEnd(f.getName(), SOURCE_EXT) + EXPECTED_EXT;
        File expectedFile = new File(expectedPath, fn);
        return expectedFile;
    }
}
