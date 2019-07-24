package com.thedeanda.regresql.service.comparator;

import com.google.common.collect.ImmutableList;
import com.thedeanda.regresql.model.*;
import com.thedeanda.regresql.datastream.DataStreamSource;
import com.thedeanda.regresql.datastream.MemoryDataStreamSource;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

public class SimpleDataStreamComparatorTest {
    DataStreamComparator comparator;



    @Test
    public void testHeadersAreSame() {
        List<RowModel> rows = ImmutableList.of();
        DataStreamSource dataSourceA = new MemoryDataStreamSource(ImmutableList.of("a", "b", "c"), rows);
        DataStreamSource dataSourceB = new MemoryDataStreamSource(ImmutableList.of("a", "b", "c"), rows);
        DataStream dsA = new DataStream(dataSourceA);
        DataStream dsB = new DataStream(dataSourceB);

        comparator = new SimpleDataStreamComparator(dsA, dsB, 10);

        assertThat(comparator.columnsMatch()).isTrue();
    }

    @Test
    public void testHeadersAreDifferent() {
        List<RowModel> rows = ImmutableList.of();
        DataStreamSource dataSourceA = new MemoryDataStreamSource(ImmutableList.of("a", "b", "c"), rows);
        DataStreamSource dataSourceB = new MemoryDataStreamSource(ImmutableList.of("a", "b", "d"), rows);
        DataStream dsA = new DataStream(dataSourceA);
        DataStream dsB = new DataStream(dataSourceB);

        comparator = new SimpleDataStreamComparator(dsA, dsB, 10);


        assertThat(comparator.columnsMatch()).isFalse();
    }

    @Test
    public void testStreamsMatch() {
        RowModel row1 = RowModel.builder()
                .data(ImmutableList.of("ABC"))
                .build();
        RowModel row2 = RowModel.builder()
                .data(ImmutableList.of("DEF"))
                .build();
        RowModel row3 = RowModel.builder()
                .data(ImmutableList.of("GHI"))
                .build();
        List<RowModel> rows = ImmutableList.of(row1, row2, row3);
        DataStreamSource dataSourceA = new MemoryDataStreamSource(ImmutableList.of("a", "b", "c"), rows);
        DataStreamSource dataSourceB = new MemoryDataStreamSource(ImmutableList.of("a", "b", "c"), rows);
        DataStream dsA = new DataStream(dataSourceA);
        DataStream dsB = new DataStream(dataSourceB);

        comparator = new SimpleDataStreamComparator(dsA, dsB, 10);

        List<RowDifference> results = comparator.compareStreams();

        assertThat(results).isEmpty();
    }

    @Test
    public void testStreamsDontMatch_whenActualEmpty() {
        RowModel row1 = RowModel.builder()
                .data(ImmutableList.of("ABC"))
                .build();
        RowModel row2 = RowModel.builder()
                .data(ImmutableList.of("DEF"))
                .build();
        RowModel row3 = RowModel.builder()
                .data(ImmutableList.of("GHI"))
                .build();
        List<RowModel> rowsExpected = ImmutableList.of();
        List<RowModel> rowsActual = ImmutableList.of(row1, row2, row3);
        DataStreamSource dataSourceA = new MemoryDataStreamSource(ImmutableList.of("a", "b", "c"), rowsExpected);
        DataStreamSource dataSourceB = new MemoryDataStreamSource(ImmutableList.of("a", "b", "c"), rowsActual);
        DataStream dsA = new DataStream(dataSourceA);
        DataStream dsB = new DataStream(dataSourceB);

        comparator = new SimpleDataStreamComparator(dsA, dsB, 10);

        List<RowDifference> results = comparator.compareStreams();

        assertThat(results).hasSize(3).extracting("expectedRow", "actualRow", "line")
                .containsExactly(
                        tuple(null, row1, 2),
                        tuple(null, row2, 3),
                        tuple(null, row3, 4)
                );
    }

    @Test
    public void testStreamsDontMatch_whenEmpectedEmpty() {
        RowModel row1 = RowModel.builder()
                .data(ImmutableList.of("ABC"))
                .build();
        RowModel row2 = RowModel.builder()
                .data(ImmutableList.of("DEF"))
                .build();
        RowModel row3 = RowModel.builder()
                .data(ImmutableList.of("GHI"))
                .build();
        List<RowModel> rowsExpected = ImmutableList.of(row1, row2, row3);
        List<RowModel> rowsActual = ImmutableList.of();
        DataStreamSource dataSourceA = new MemoryDataStreamSource(ImmutableList.of("a", "b", "c"), rowsExpected);
        DataStreamSource dataSourceB = new MemoryDataStreamSource(ImmutableList.of("a", "b", "c"), rowsActual);
        DataStream dsA = new DataStream(dataSourceA);
        DataStream dsB = new DataStream(dataSourceB);

        comparator = new SimpleDataStreamComparator(dsA, dsB, 10);

        List<RowDifference> results = comparator.compareStreams();

        assertThat(results).hasSize(3).extracting("expectedRow", "actualRow", "line")
                .containsExactly(
                        tuple(row1, null, 2),
                        tuple(row2, null, 3),
                        tuple(row3, null, 4)
                );
    }

    @Test
    public void testStreamsDontMatch_1() {
        RowModel row1 = RowModel.builder()
                .data(ImmutableList.of("ABC"))
                .build();
        RowModel row2 = RowModel.builder()
                .data(ImmutableList.of("DEF"))
                .build();
        RowModel row3 = RowModel.builder()
                .data(ImmutableList.of("GHI"))
                .build();
        List<RowModel> rowsExpected = ImmutableList.of(row1, row2, row3);
        List<RowModel> rowsActual = ImmutableList.of(row1, row3);
        DataStreamSource dataSourceA = new MemoryDataStreamSource(ImmutableList.of("a", "b", "c"), rowsExpected);
        DataStreamSource dataSourceB = new MemoryDataStreamSource(ImmutableList.of("a", "b", "c"), rowsActual);
        DataStream dsA = new DataStream(dataSourceA);
        DataStream dsB = new DataStream(dataSourceB);

        comparator = new SimpleDataStreamComparator(dsA, dsB, 10);

        List<RowDifference> results = comparator.compareStreams();

        assertThat(results).hasSize(2).extracting("expectedRow", "actualRow", "line")
                .containsExactly(
                        tuple(row2, row3, 3),
                        tuple(row3, null, 4)
                );
    }
}
