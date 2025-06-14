package com.motorph.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class NotificationBellButton extends JButton {
    public NotificationBellButton() {
        setIcon(new ImageIcon(getClass().getResource("/img/icon_bell.png")));
        setToolTipText("Notifications");
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public void addBellActionListener(ActionListener listener) {
        addActionListener(listener);
    }
}
