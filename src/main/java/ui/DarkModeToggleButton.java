package com.motorph.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class DarkModeToggleButton extends JToggleButton {
    private Icon sunIcon;
    private Icon moonIcon;

    public DarkModeToggleButton() {
        sunIcon = new ImageIcon(getClass().getResource("/img/icon_sun.png"));
        moonIcon = new ImageIcon(getClass().getResource("/img/icon_moon.png"));

        setIcon(sunIcon);
        setToolTipText("Toggle Dark Mode");
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addChangeListener(e -> {
            setIcon(isSelected() ? moonIcon : sunIcon);
        });
    }

    public void addToggleActionListener(ActionListener listener) {
        addActionListener(listener);
    }
}
