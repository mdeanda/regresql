package com.thedeanda.regresql.datastream;

import com.google.common.collect.ImmutableList;
import com.thedeanda.regresql.model.HeaderModel;
import com.thedeanda.regresql.model.RowModel;

import java.util.ArrayDeque;
import java.util.List;

public class MemoryDataStreamSource implements DataStreamSource {
    private final List<String> columns;
    ArrayDeque<RowModel> data;

    public MemoryDataStreamSource(List<String> columns, List<RowModel> rows) {
        data = new ArrayDeque<RowModel>();
        data.addAll(rows);

        this.columns = ImmutableList.copyOf(columns);
    }

    @Override
    public HeaderModel getHeaderModel() {
        return HeaderModel.builder()
                .columnNames(columns)
                .build();
    }

    @Override
    public RowModel next() {
        return data.poll();
    }

    @Override
    public void close() throws Exception {

    }
}
