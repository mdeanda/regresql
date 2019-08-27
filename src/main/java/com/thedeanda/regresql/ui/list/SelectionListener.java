package com.thedeanda.regresql.ui.list;

import com.thedeanda.regresql.model.TestSource;

import java.util.Collection;

public interface SelectionListener {
    void onSelect(Collection<TestSource> items);
}
