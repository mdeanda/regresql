package com.thedeanda.regresql.ui;

import com.thedeanda.regresql.RegresqlService;

import javax.swing.*;
import java.io.File;

public class RegresqlUiFrame extends JFrame {
    public RegresqlUiFrame(RegresqlService service) {
        RegresqlUiMainPanel panel = new RegresqlUiMainPanel(service);
        getContentPane().add(panel);
    }
}
