package com.thedeanda.regresql.ui.detail;

import com.thedeanda.regresql.model.TestSource;

import javax.swing.*;
import java.awt.*;

/**
 * panel to show when multiple tests are selected
 */
public class SingleDetailPanel extends JPanel {
    private final JLabel lbl;

    public SingleDetailPanel() {
        BorderLayout layout = new BorderLayout();
        setMinimumSize(new Dimension(200, 80));
        this.setLayout(layout);
        lbl = new JLabel("SingleDetailPanel");
        add(lbl, BorderLayout.CENTER);
    }

    public void setTestSource(TestSource testSource) {
        lbl.setText(testSource.toString());
    }
}
