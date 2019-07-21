package com.thedeanda.regresql.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.File;

@Builder
@Getter
@ToString
@EqualsAndHashCode
public class TestSource {
    private String baseName;
    private File source;
    private File expected;
    private String relativePath;
}
