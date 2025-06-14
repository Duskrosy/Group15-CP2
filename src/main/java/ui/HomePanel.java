package com.motorph.ui;

import com.motorph.services.TimeTrackerService;

import javax.swing.*;
import java.awt.*;

public class HomePanel extends JPanel {
    private final String employeeID;
    private final String fullName;
    private final boolean isAdmin;
    private final TimeTrackerService tracker;

    public HomePanel(String employeeID, String fullName, boolean isAdmin) {
        this.employeeID = employeeID;
        this.fullName = fullName;
        this.isAdmin = isAdmin;
        this.tracker = new TimeTrackerService(employeeID, fullName);

        setLayout(new BorderLayout());

        // Center Card
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        centerPanel.add(new HomeCardPanel(employeeID, fullName, tracker), gbc);
        add(centerPanel, BorderLayout.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        try {
            Image bgImg = new ImageIcon(getClass().getResource("/img/Background2.png")).getImage();
            g.drawImage(bgImg, 0, 0, getWidth(), getHeight(), this);
        } catch (Exception ignored) {
            g.setColor(new Color(246, 241, 254));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}
