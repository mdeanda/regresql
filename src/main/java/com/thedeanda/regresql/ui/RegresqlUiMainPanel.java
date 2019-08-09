package com.thedeanda.regresql.ui;

import com.thedeanda.regresql.RegresqlService;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class RegresqlUiMainPanel extends JPanel {
    private final RegresqlService service;
    private final File source;
    private final File expected;

    private JSplitPane split;
    private JList<Object> testList;
    private JScrollPane listScroll;

    public RegresqlUiMainPanel(RegresqlService service, File source, java.io.File expected) {
        this.service = service;
        this.source = source;
        this.expected = expected;

        BorderLayout layout = new BorderLayout();
        setPreferredSize(new Dimension(640, 480));
        this.setLayout(layout);
        this.initItems();
    }

    private void initItems() {
        testList = new JList<>();
        listScroll = new JScrollPane(testList);
        split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(300);
        split.setLeftComponent(listScroll);
        split.setRightComponent(new JButton("button"));

        add(split, BorderLayout.CENTER);
    }
}
