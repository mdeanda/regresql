package com.thedeanda.regresql.ui.detail;

import com.thedeanda.regresql.model.TestSource;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

/**
 * panel to show when multiple tests are selected
 */
public class MultiDetailPanel extends JPanel {
    public MultiDetailPanel() {
        BorderLayout layout = new BorderLayout();
        setMinimumSize(new Dimension(200, 80));
        this.setLayout(layout);
        add(new JButton("MultiDetailPanel"), BorderLayout.CENTER);
    }

    public void setTestSource(Collection<TestSource> testSource) {

    }
}
