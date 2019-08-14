package com.thedeanda.regresql.ui;

import com.thedeanda.regresql.RegresqlService;
import com.thedeanda.regresql.ui.list.TestListPanel;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class RegresqlUiMainPanel extends JPanel {
    private final RegresqlService service;

    private JSplitPane split;
    private TestListPanel testList;
    private JScrollPane listScroll;

    public RegresqlUiMainPanel(RegresqlService service) {
        this.service = service;

        BorderLayout layout = new BorderLayout();
        setPreferredSize(new Dimension(800, 480));
        this.setLayout(layout);
        this.initItems();
    }

    private void initItems() {
        testList = new TestListPanel(service);
        listScroll = new JScrollPane(testList);
        split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(300);
        split.setLeftComponent(listScroll);
        split.setRightComponent(new JButton("button"));

        add(split, BorderLayout.CENTER);
    }
}
