package com.thedeanda.regresql.ui;

import com.thedeanda.regresql.RegresqlService;

import javax.swing.*;
import java.io.File;

public class RegresqlUiFrame extends JFrame {
    public RegresqlUiFrame(RegresqlService service, File source, File expected) {
        RegresqlUiMainPanel panel = new RegresqlUiMainPanel(service, source, expected);
        getContentPane().add(panel);
    }
}
