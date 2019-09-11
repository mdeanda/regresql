package com.thedeanda.regresql.datastream;

import com.thedeanda.regresql.model.HeaderModel;
import com.thedeanda.regresql.model.RowModel;

public interface DataStreamSource extends AutoCloseable {
    public HeaderModel getHeaderModel();
    public RowModel next();
}
