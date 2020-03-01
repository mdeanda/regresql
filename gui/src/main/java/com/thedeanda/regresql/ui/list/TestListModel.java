package com.thedeanda.regresql.ui.list;

import com.thedeanda.regresql.RegresqlService;
import com.thedeanda.regresql.model.TestSource;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.util.ArrayList;
import java.util.List;

import static javax.swing.event.ListDataEvent.CONTENTS_CHANGED;

public class TestListModel implements ListModel {

    private final RegresqlService regresqlService;
    private final List<TestSource> tests;
    private final List<ListDataListener> listDataListeners;

    public TestListModel(RegresqlService regresqlService) {
        this.regresqlService = regresqlService;
        tests = new ArrayList<>();
        listDataListeners = new ArrayList<>();
        init();
    }


    private void init() {
        SwingUtilities.invokeLater(() -> {
            List<TestSource> tests = regresqlService.listTests();
            this.tests.addAll(tests);
            listDataListeners.forEach(l -> l.contentsChanged(new ListDataEvent(this, CONTENTS_CHANGED, 0, tests.size()-1)));
        });
    }

    @Override
    public int getSize() {
        return tests.size();
    }

    public TestSource get(int i) {
        return tests.get(i);
    }

    @Override
    public Object getElementAt(int i) {
        return get(i);
    }

    @Override
    public void addListDataListener(ListDataListener listDataListener) {
        listDataListeners.add(listDataListener);
    }

    @Override
    public void removeListDataListener(ListDataListener listDataListener) {
        listDataListeners.remove(listDataListener);
    }
}
