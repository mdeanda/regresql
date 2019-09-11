package com.thedeanda.regresql.service.comparator;

import com.thedeanda.regresql.model.DataStream;
import com.thedeanda.regresql.model.HeaderModel;
import com.thedeanda.regresql.model.RowDifference;
import com.thedeanda.regresql.model.RowModel;

import javax.swing.table.TableRowSorter;
import java.util.ArrayList;
import java.util.List;

public abstract class DataStreamComparator {
    protected final DataStream expectedStream;
    protected final DataStream actualStream;

    public DataStreamComparator(DataStream expectedStream, DataStream actualStream) {
        this.expectedStream = expectedStream;
        this.actualStream = actualStream;
    }

    public boolean columnsMatch() {
        HeaderModel c1 = expectedStream.getHeaderModel();
        HeaderModel c2 = actualStream.getHeaderModel();

        return c1.equals(c2);
    }

    abstract public List<RowDifference> compareStreams();
}
