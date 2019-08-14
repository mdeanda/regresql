package com.thedeanda.regresql.ui.list;

import com.google.common.collect.Streams;
import com.thedeanda.regresql.RegresqlService;
import com.thedeanda.regresql.model.TestSource;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class TestListPanel extends JPanel {
    private final JList list;
    private final RegresqlService regresqlService;
    private final TestListModel model;

    public TestListPanel(final RegresqlService regresqlService) {
        this.regresqlService = regresqlService;

        setLayout(new BorderLayout());

        list = new JList();
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        model = new TestListModel(regresqlService);
        list.setModel(model);
        list.addListSelectionListener(a -> {
            if (!a.getValueIsAdjusting()) {
                int[] indices = list.getSelectedIndices();

                Collection<TestSource> items = Arrays.stream(indices).boxed()
                        .map(i -> model.get(i))
                        .collect(Collectors.toList());
                selected(items);
            }
        });

        add(new JScrollPane(list), BorderLayout.CENTER);
    }

    private void selected(Collection<TestSource> items) {
        log.info("item selected: {}, {}", items.size(), items);
    }
}
