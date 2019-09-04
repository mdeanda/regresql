package com.thedeanda.regresql;

import com.thedeanda.regresql.datasource.DataSource;
import com.thedeanda.regresql.datastream.CsvDataStreamSource;
import com.thedeanda.regresql.datastream.ResultSetDataStreamSource;
import com.thedeanda.regresql.model.DataStream;
import com.thedeanda.regresql.model.RowDifference;
import com.thedeanda.regresql.model.TestSource;
import com.thedeanda.regresql.service.DiffWriter;
import com.thedeanda.regresql.service.StreamToCsvService;
import com.thedeanda.regresql.service.comparator.SimpleDataStreamComparator;
import com.thedeanda.regresql.service.comparator.TestLocator;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.List;

@Slf4j
public class RegresqlService {
    private final DataSource dataSource;
    private final DiffWriter diffWriter;

    public RegresqlService(DataSource dataSource) {
        this.dataSource = dataSource;
        diffWriter = new DiffWriter();
    }

    public List<TestSource> listTests(File source, File expected) {
        List<TestSource> tests = listTestsInternal(source, expected);
        tests.forEach(t -> {
            log.info("test name: {}/{}, has expected file: {}", t.getRelativePath(), t.getBaseName(), t.getExpected().exists());
        });
        return tests;
    }

    private List<TestSource> listTestsInternal(File source, File expected) {
        TestLocator locator = new TestLocator(source, expected);
        return locator.locateTests();
    }

    public void updateAllTests(File source, File expected) throws Exception {
        List<TestSource> tests = listTestsInternal(source, expected);
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
        List<TestSource> tests = listTestsInternal(source, expected);
        for (TestSource test : tests) {
            if (test.getExpected().exists()) {
                File targetDir = new File(output, test.getRelativePath());
                targetDir.mkdirs();
                boolean passed = runTest(test, targetDir, maxErrors);
                hasErrors |= !passed;
            } else {
                log.info("{} missing expected output, skipping.", test.getSource());
            }
        }
        return !hasErrors;
    }

    /** runs test and returns diffs */
    public List<RowDifference> runTest(TestSource test, int maxErrors) throws Exception {
        long start = System.currentTimeMillis();
        log.info("Running test: {}", test.getSource());

        ResultSetDataStreamSource rsdss = new ResultSetDataStreamSource(dataSource, test.getSource());
        rsdss.init();

        CsvDataStreamSource expectedData = new CsvDataStreamSource(test.getExpected());
        SimpleDataStreamComparator comparator = new SimpleDataStreamComparator(new DataStream(expectedData), new DataStream(rsdss), maxErrors);

        List<RowDifference> diffs = comparator.compareStreams();
        long end = System.currentTimeMillis();
        if (!diffs.isEmpty()) {
            log.warn("Test failed: {}, {} errors, duration {}ms", test.getSource(), diffs.size(), (end-start));
        } else {
            log.info("Test passed: {}, duration {}ms", test.getSource(), (end-start));
        }
        return diffs;
    }

    public boolean runTest(TestSource test, File outputDir, int maxErrors) throws Exception {
        //TODO: write db result to output file

        long start = System.currentTimeMillis();
        log.info("Running test: {}", test.getSource());
        File outputCsv = new File(outputDir, test.getBaseName() + TestLocator.CSV_EXT);

        ResultSetDataStreamSource rsdss = new ResultSetDataStreamSource(dataSource, test.getSource());
        rsdss.init();
        StreamToCsvService streamToCsvService = new StreamToCsvService();
        streamToCsvService.convert(rsdss, outputCsv);

        CsvDataStreamSource actualData = new CsvDataStreamSource(outputCsv);
        CsvDataStreamSource expectedData = new CsvDataStreamSource(test.getExpected());
        SimpleDataStreamComparator comparator = new SimpleDataStreamComparator(new DataStream(expectedData), new DataStream(actualData), maxErrors);

        List<RowDifference> diffs = comparator.compareStreams();
        long end = System.currentTimeMillis();
        if (!diffs.isEmpty()) {
            log.warn("Test failed: {}, {} errors, duration {}ms", test.getSource(), diffs.size(), (end-start));
            diffWriter.outputDiffs(expectedData.getHeaderModel(), diffs, outputDir, test);
        } else {
            log.info("Test passed: {}, duration {}ms", test.getSource(), (end-start));
        }
        return diffs.isEmpty();
    }
}
