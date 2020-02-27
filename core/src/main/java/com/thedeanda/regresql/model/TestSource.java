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
public class TestSource implements Comparable<TestSource> {
    private String baseName;
    private File source;
    private File expected;
    private String relativePath;
    private String contents;

    @Override
    public int compareTo(TestSource testSource) {
        int val = 0;
        val = relativePath.compareTo(testSource.getRelativePath());
        if (val == 0) {
            val = baseName.compareTo(testSource.getBaseName());
        }
        return val;
    }
}
