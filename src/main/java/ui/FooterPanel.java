package com.motorph.ui;

import com.motorph.utils.FontHelper;

import javax.swing.*;
import java.awt.*;

public class FooterPanel extends JPanel {
    public FooterPanel() {
        setLayout(null);
        setPreferredSize(new Dimension(1660, 46));
        setBackground(new Color(191, 180, 241));

        // Left label
        JLabel leftLabel = new JLabel("MotorPH Self-Service Employee Hub");
        leftLabel.setFont(FontHelper.semiBold(15f));
        leftLabel.setForeground(new Color(90, 70, 200));
        leftLabel.setBounds(24, 13, 330, 22);
        add(leftLabel);

        // Center: version
        JLabel versionLabel = new JLabel("v1.0.0", SwingConstants.CENTER);
        versionLabel.setFont(FontHelper.medium(15f));
        versionLabel.setForeground(new Color(78, 54, 139));
        versionLabel.setBounds((1660 - 150) / 2, 13, 150, 22);
        add(versionLabel);

        // Right: Contact Admin
        JButton contactAdmin = new JButton("Contact an Admin");
        contactAdmin.setFont(FontHelper.semiBold(15f));
        contactAdmin.setBackground(new Color(120, 83, 255));
        contactAdmin.setForeground(Color.WHITE);
        contactAdmin.setFocusPainted(false);
        contactAdmin.setBounds(1660 - 230 - 24, 7, 220, 32);
        contactAdmin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        contactAdmin.addActionListener(e -> JOptionPane.showMessageDialog(this, "Ticketing system coming soon!")); //Wala pa
        add(contactAdmin);
    }
}
