package com.motorph.ui;

import com.motorph.utils.FontHelper;
import com.motorph.ui.components.NotificationBellButton;
import com.motorph.ui.components.DarkModeToggleButton;
import com.motorph.LoginScreen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.function.Consumer;

public class NavBarPanel extends JPanel {
    private JLabel profileNameLabel;
    private boolean isDarkMode = false;

    public NavBarPanel(String fullName, boolean isAdmin, Consumer<String> onNav) {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1660, 75));
        setBackground(Color.WHITE);

        // Logo
        JLabel logo = new JLabel("MotorPH");
        logo.setFont(FontHelper.black(34f));
        logo.setForeground(new Color(106, 29, 120));
        logo.setBorder(BorderFactory.createEmptyBorder(0, 38, 0, 0));
        add(logo, BorderLayout.WEST);

        // Center nav
        String[] navItems = isAdmin
                ? new String[]{"Home", "My Details", "Payroll", "All Employees", "Admin Menu"}
                : new String[]{"Home", "My Details", "Payroll", "My Tickets"};
        JPanel centerNavPanel = new JPanel();
        centerNavPanel.setOpaque(false);
        centerNavPanel.setBorder(BorderFactory.createEmptyBorder(18, 0, 10, 0));

        Color normal = new Color(91, 68, 132);
        Color hover = new Color(106, 29, 120);

        for (String item : navItems) {
            JLabel navItem = new JLabel(item);
            navItem.setFont(FontHelper.semiBold(18f));
            navItem.setForeground(normal);
            navItem.setBorder(BorderFactory.createEmptyBorder(0, 28, 0, 28));
            navItem.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            navItem.setHorizontalAlignment(SwingConstants.CENTER);
            navItem.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (onNav != null) onNav.accept(item);
                }
                public void mouseEntered(MouseEvent e) {
                    navItem.setForeground(hover);
                }
                public void mouseExited(MouseEvent e) {
                    navItem.setForeground(normal);
                }
            });
            centerNavPanel.add(navItem);
        }
        add(centerNavPanel, BorderLayout.CENTER);

        // Right cluster
        JPanel rightNavPanel = new JPanel();
        rightNavPanel.setOpaque(false);
        rightNavPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 12, 18));

        // --- Notification Bell Button ---
        NotificationBellButton bellButton = new NotificationBellButton();
        bellButton.addBellActionListener(e -> JOptionPane.showMessageDialog(this, "No new notifications!"));
        rightNavPanel.add(bellButton);

        // --- Dark Mode Toggle Button ---
        DarkModeToggleButton darkModeButton = new DarkModeToggleButton();
        darkModeButton.addToggleActionListener(e -> {
            boolean dark = darkModeButton.isSelected();
            ThemeManager.setTheme(dark ? ThemeManager.Theme.DARK : ThemeManager.Theme.LIGHT);
        });
        rightNavPanel.add(darkModeButton);

        // --- Profile picture + name, clickable for dropdown ---
        ImageIcon profileIcon;
        try {
            profileIcon = new ImageIcon(getClass().getResource("/img/profile_placeholder.png"));
        } catch (Exception e) {
            profileIcon = null;
        }
        JLabel profilePicLabel = new JLabel();
        if (profileIcon != null) {
            profilePicLabel.setIcon(new ImageIcon(profileIcon.getImage().getScaledInstance(36, 36, Image.SCALE_SMOOTH)));
        } else {
            profilePicLabel.setText("👤");
        }
        profilePicLabel.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));

        profileNameLabel = new JLabel(fullName);
        profileNameLabel.setFont(FontHelper.medium(16f));
        profileNameLabel.setForeground(new Color(66, 28, 115));
        profileNameLabel.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 10));

        JPanel profilePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        profilePanel.setOpaque(false);
        profilePanel.add(profilePicLabel);
        profilePanel.add(profileNameLabel);
        profilePanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Create popup menu
        JPopupMenu profileMenu = new JPopupMenu();
        JMenuItem editProfileItem = new JMenuItem("Edit Profile");
        JMenuItem logoutItem = new JMenuItem("Logout");
        profileMenu.add(editProfileItem);
        profileMenu.add(logoutItem);

        profilePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                profileMenu.show(profilePanel, e.getX(), profilePanel.getHeight());
            }
        });

        // Logout action: close dashboard and open login screen
        logoutItem.addActionListener(e -> {
            int res = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (res == JOptionPane.YES_OPTION) {
                Window window = SwingUtilities.getWindowAncestor(this);
                if (window != null) window.dispose();
                new LoginScreen().setVisible(true);
            }
        });

        rightNavPanel.add(profilePanel);

        add(rightNavPanel, BorderLayout.EAST);
    }
}
