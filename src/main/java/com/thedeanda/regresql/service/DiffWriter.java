package com.thedeanda.regresql.service;

import com.google.common.collect.Lists;
import com.thedeanda.regresql.model.HeaderModel;
import com.thedeanda.regresql.model.RowDifference;
import com.thedeanda.regresql.model.RowModel;
import com.thedeanda.regresql.model.TestSource;
import com.thedeanda.regresql.service.comparator.TestLocator;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DiffWriter {
    public static final String DIFF_TYPE_1_SUFFIX = ".diffs.csv";
    public static final String DIFF_TYPE_SPARSE_SUFFIX = ".sparse.csv";
    private static final String DIFF_HEADER = "Source";
    private static final String ROW_HEADER = "Line";
    private static final String EXPECTED = "Expected";
    private static final String ACTUAL = "Actual";

    public void outputDiffs(HeaderModel headerModel, List<RowDifference> diffs, File outputDir, TestSource test) throws IOException {
        outputDiffsAsCsv(headerModel, diffs, outputDir, test);
        outputDiffsAsSparseCsv(headerModel, diffs, outputDir, test);
    }

    public void outputDiffsAsCsv(HeaderModel headerModel, List<RowDifference> diffs, File outputDir, TestSource test) throws IOException {
        File outputFile = new File(outputDir, test.getBaseName() + DIFF_TYPE_1_SUFFIX);
        outputFile.delete();
        try (CSVPrinter printer = new CSVPrinter(new FileWriter(outputFile), CSVFormat.DEFAULT)) {
            List<String> header = getListWithPrefixValue(headerModel.getColumnNames(), ROW_HEADER, DIFF_HEADER);
            printer.printRecord(header);

            for (RowDifference diff : diffs) {
                RowModel re = diff.getExpectedRow();
                RowModel ra = diff.getActualRow();
                if (re != null) {
                    List<String> data = getListWithPrefixValue(re.getData(), String.valueOf(diff.getLine()), EXPECTED);
                    printer.printRecord(data);
                }
                if (ra != null) {
                    List<String> data = getListWithPrefixValue(ra.getData(), String.valueOf(diff.getLine()), ACTUAL);
                    printer.printRecord(data);
                }
            }
        }
    }

    public void outputDiffsAsSparseCsv(HeaderModel headerModel, List<RowDifference> diffs, File outputDir, TestSource test) throws IOException {
        File outputFile = new File(outputDir, test.getBaseName() + DIFF_TYPE_SPARSE_SUFFIX);
        outputFile.delete();
        try (CSVPrinter printer = new CSVPrinter(new FileWriter(outputFile), CSVFormat.DEFAULT)) {
            List<String> header = getListWithPrefixValue(headerModel.getColumnNames(), ROW_HEADER, DIFF_HEADER);
            printer.printRecord(header);

            for (RowDifference diff : diffs) {
                RowModel re = diff.getExpectedRow();
                RowModel ra = diff.getActualRow();
                if (re != null & ra != null) {
                    List<String> expectedOutput = Lists.newArrayList(String.valueOf(diff.getLine()), EXPECTED);
                    List<String> actualOutput = Lists.newArrayList(String.valueOf(diff.getLine()), ACTUAL);
                    prepSparse(expectedOutput, actualOutput, re.getData(), ra.getData());
                    printer.printRecord(expectedOutput);
                    printer.printRecord(actualOutput);
                } else if (re != null) {
                    List<String> data = getListWithPrefixValue(re.getData(), String.valueOf(diff.getLine()), EXPECTED);
                    printer.printRecord(data);
                } else if (ra != null) {
                    List<String> data = getListWithPrefixValue(ra.getData(), String.valueOf(diff.getLine()), ACTUAL);
                    printer.printRecord(data);
                }
            }
        }
    }

    private void prepSparse(List<String> expectedOutput,
                            List<String> actualOutput,
                            List<String> expected,
                            List<String> actual) {
        for (int i=0; i<expected.size(); i++) {
            String e = expected.get(i);
            String a = actual.get(i);

            if (!StringUtils.equals(a, e)) {
                expectedOutput.add(e);
                actualOutput.add(a);
            } else {
                expectedOutput.add(null);
                actualOutput.add(null);
            }
        }
    }

    private List<String> getListWithPrefixValue(List<String> data, String ... prefix) {
        List<String> output = new ArrayList<>();
        for (String p : prefix) {
            output.add(p);
        }
        output.addAll(data);
        return output;
    }

}
