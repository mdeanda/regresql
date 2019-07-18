package com.thedeanda.regresql.model;

import com.thedeanda.regresql.datastream.DataStreamSource;

import java.util.LinkedList;

public class DataStream {
    private HeaderModel headerModel;
    private final DataStreamSource dataStreamSource;
    private final LinkedList<RowModel> queue = new LinkedList<RowModel>();

    public DataStream(DataStreamSource dataStreamSource) {
        this.dataStreamSource = dataStreamSource;
    }


    public HeaderModel getHeaderModel() {
        if (headerModel == null) {
            headerModel = dataStreamSource.getHeaderModel();
        }
        return headerModel;
    }

    public RowModel peek() {
        if (!queue.isEmpty()) {
            return queue.getFirst();
        }
        RowModel first = dataStreamSource.next();
        if (first != null) {
            queue.add(first);
        }
        return first;
    }

    public RowModel next() {
        if (!queue.isEmpty()) {
            return queue.removeFirst();
        }
        return dataStreamSource.next();
    }

    /** returns number or rows we need to skip to reach the row given. */
    public int stepsUntilRow(RowModel rowToFind, int maxLookahead) {
        int steps = 0;

        //always consider all in queue regardless of lookahead
        for (RowModel row : queue) {
            if (rowToFind.equals(row)) {
                return steps;
            }
            steps++;
        }
        while (steps < maxLookahead) {
            RowModel row = dataStreamSource.next();
            if (row != null) {
                queue.add(row);
            } else {
                //done reading stream, exit
                return steps;
            }
            if (rowToFind.equals(row)) {
                return steps;
            }
            steps++;
        }

        return steps;
    }
}
