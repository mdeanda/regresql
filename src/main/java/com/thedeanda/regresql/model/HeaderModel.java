package com.thedeanda.regresql.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@ToString
@EqualsAndHashCode
public class HeaderModel {
    @Builder.Default
    List<String> columnNames = new ArrayList<>();
}
