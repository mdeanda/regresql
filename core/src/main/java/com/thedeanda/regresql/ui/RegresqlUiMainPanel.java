package com.thedeanda.regresql.ui;

import com.thedeanda.regresql.RegresqlService;
import com.thedeanda.regresql.model.TestSource;
import com.thedeanda.regresql.ui.detail.MultiDetailPanel;
import com.thedeanda.regresql.ui.detail.SingleDetailPanel;
import com.thedeanda.regresql.ui.list.TestListPanel;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Collection;
import java.util.Collections;

public class RegresqlUiMainPanel extends JPanel {
    private static final String MULTI_DETAIL = "multi";
    private static final String SINGLE_DETAIL = "single";
    private final RegresqlService service;

    private JSplitPane split;
    private TestListPanel testList;
    private JScrollPane listScroll;
    private MultiDetailPanel multiDetailPanel = new MultiDetailPanel();
    private SingleDetailPanel singleDetailPanel = new SingleDetailPanel();
    private CardLayout cardLayout;
    private JPanel cardPanel;

    public RegresqlUiMainPanel(RegresqlService service) {
        this.service = service;

        BorderLayout layout = new BorderLayout();
        setPreferredSize(new Dimension(800, 480));
        this.setLayout(layout);
        this.initItems();
    }

    private void initItems() {
        testList = new TestListPanel(service, this::onSelect);
        listScroll = new JScrollPane(testList);

        cardPanel = new JPanel();
        cardLayout = new CardLayout();
        cardPanel.setLayout(cardLayout);
        cardPanel.add(multiDetailPanel, MULTI_DETAIL);
        cardPanel.add(singleDetailPanel, SINGLE_DETAIL);

        split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(300);
        split.setLeftComponent(listScroll);
        split.setRightComponent(cardPanel);

        add(split, BorderLayout.CENTER);
    }

    private void onSelect(Collection<TestSource> testSources) {
        if (testSources.size() == 1) {
            cardLayout.show(cardPanel, SINGLE_DETAIL);
            singleDetailPanel.setTestSource(testSources.stream().findAny().get());
        } else {
            cardLayout.show(cardPanel, MULTI_DETAIL);
            multiDetailPanel.setTestSource(testSources);
        }
    }
}
