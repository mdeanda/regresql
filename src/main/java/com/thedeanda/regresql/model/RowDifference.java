package com.thedeanda.regresql.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
@EqualsAndHashCode
public class RowDifference {
    private int line;
    private RowModel expectedRow;
    private RowModel actualRow;
}
