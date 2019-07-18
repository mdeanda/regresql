package com.thedeanda.regresql.service.comparator;

import com.thedeanda.regresql.model.DataStream;
import com.thedeanda.regresql.model.RowDifference;
import com.thedeanda.regresql.model.RowModel;

import java.util.ArrayList;
import java.util.List;

public class SimpleDataStreamComparator extends DataStreamComparator {
    public SimpleDataStreamComparator(DataStream expectedStream, DataStream actualStream) {
        super(expectedStream, actualStream);
    }

    @Override
    public List<RowDifference> compareStreams() {

        int index = 0;
        List<RowDifference> results = new ArrayList<>();

        RowModel rowExpected = null;
        RowModel rowActual = null;

        do {
            rowActual = null;
            rowExpected = expectedStream.next();
            if (rowExpected == null) {
                // expected stream done, get the rest of the actual stream
                addTheRestActual(results, actualStream, index+1);
                //break out of loop
            } else {
                rowActual = actualStream.next();
                if (rowActual == null) {
                    results.add(RowDifference.builder()
                            .index(index)
                            .expectedRow(rowExpected)
                            .build());
                    addTheRestExpected(results, expectedStream, index+1);
                    //break out of loop
                } else if (!rowActual.equals(rowExpected)) {
                    results.add(RowDifference.builder()
                            .index(index)
                            .expectedRow(rowExpected)
                            .actualRow(rowActual)
                            .build());
                }
            }
            index++;
        } while(rowExpected != null && rowActual != null);


        return results;
    }

    private void addTheRestExpected(List<RowDifference> results, DataStream stream, int startIndex) {
        RowModel row;
        while ((row = stream.next()) != null) {
            results.add(RowDifference.builder()
                    .index(startIndex++)
                    .expectedRow(row)
                    .build());
        }
    }

    private void addTheRestActual(List<RowDifference> results, DataStream stream, int startIndex) {
        RowModel row;
        while ((row = stream.next()) != null) {
            results.add(RowDifference.builder()
                    .index(startIndex++)
                    .actualRow(row)
                    .build());
        }
    }

}
