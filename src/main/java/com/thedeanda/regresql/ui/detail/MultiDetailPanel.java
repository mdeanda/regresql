package com.thedeanda.regresql.ui.detail;

import javax.swing.*;
import java.awt.*;

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
}
