package com.thedeanda.regresql;

import com.thedeanda.regresql.datasource.DataSource;
import com.thedeanda.regresql.datastream.CsvDataStreamSource;
import com.thedeanda.regresql.datastream.ResultSetDataStreamSource;
import com.thedeanda.regresql.model.DataStream;
import com.thedeanda.regresql.model.RowDifference;
import com.thedeanda.regresql.model.TestSource;
import com.thedeanda.regresql.service.StreamToCsvService;
import com.thedeanda.regresql.service.comparator.SimpleDataStreamComparator;
import com.thedeanda.regresql.service.comparator.TestLocator;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.List;

@Slf4j
public class RegresqlService {
    private final DataSource dataSource;

    public RegresqlService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<TestSource> listTests(File source, File expected) {
        TestLocator locator = new TestLocator(source, expected);
        return locator.locateTests();
    }

    public void updateAllTests(File source, File expected) throws Exception {
        List<TestSource> tests = listTests(source, expected);
        for (TestSource test : tests) {
            updateTest(test, source, expected);
        }
    }

    public void updateTest(TestSource test, File source, File expected) throws Exception {
        log.info("updating test data for {}", test.getSource());
        ResultSetDataStreamSource rsdss = new ResultSetDataStreamSource(dataSource, test.getSource());
        rsdss.init();
        StreamToCsvService streamToCsvService = new StreamToCsvService();
        streamToCsvService.convert(rsdss, test.getExpected());
    }

    public boolean runAllTests(File source, File expected, File output, int maxErrors) throws Exception {
        boolean hasErrors = false;
        List<TestSource> tests = listTests(source, expected);
        for (TestSource test : tests) {
            if (test.getExpected().exists()) {
                File targetDir = new File(output, test.getRelativePath());
                File targetFile = new File(targetDir, test.getExpected().getName());
                targetFile.getParentFile().mkdirs();
                boolean passed = runTest(test, targetFile, maxErrors);
                hasErrors |= !passed;
            } else {
                log.info("{} missing expected output, skipping.", test.getSource());
            }
        }
        return !hasErrors;
    }

    public boolean runTest(TestSource test, File output, int maxErrors) throws Exception {
        //TODO: write db result to output file

        long start = System.currentTimeMillis();
        log.info("Running test: {}", test.getSource());
        ResultSetDataStreamSource rsdss = new ResultSetDataStreamSource(dataSource, test.getSource());
        rsdss.init();
        StreamToCsvService streamToCsvService = new StreamToCsvService();
        streamToCsvService.convert(rsdss, output);

        CsvDataStreamSource actualData = new CsvDataStreamSource(output);
        CsvDataStreamSource expectedData = new CsvDataStreamSource(test.getExpected());
        SimpleDataStreamComparator comparator = new SimpleDataStreamComparator(new DataStream(expectedData), new DataStream(actualData), maxErrors);

        List<RowDifference> diffs = comparator.compareStreams();
        long end = System.currentTimeMillis();
        if (!diffs.isEmpty()) {
            log.warn("Test failed: {} {}ms", diffs, (end-start));
        } else {
            log.info("Test passed: {} {}ms", test.getSource(), (end-start));
        }
        return diffs.isEmpty();
    }
}
