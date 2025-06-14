package com.motorph.controllers;

import com.motorph.ui.MyDetailsPanel;
import com.motorph.utils.FontHelper;
import com.motorph.LoginScreen;

import javax.swing.*;
import java.awt.*;


public class DashboardController {

    public static JTabbedPane buildDashboardTabs(String employeeID, String role, JFrame frame) {
        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.setFont(FontHelper.semiBold(14f));

        tabbedPane.addTab("My Info", new MyDetailsPanel(employeeID));
        // (You might need to update the next two panels to their latest correct class names as well!)
        // tabbedPane.addTab("Time Tracker", new TimeTracker(employeeID));
        // tabbedPane.addTab("Payroll", new PayrollPanel(employeeID, role));

        if (role.equalsIgnoreCase("admin")) {
            // tabbedPane.addTab("All Employees", new AllEmployeesPanel());
        }

        JPanel logoutPanel = new JPanel();
        JLabel logoutMsg = new JLabel("You have been logged out.");
        logoutMsg.setFont(FontHelper.medium(16f));
        logoutPanel.add(logoutMsg);

        JLabel logoutLabel = new JLabel("Logout");
        logoutLabel.setFont(FontHelper.bold(14f));
        logoutLabel.setForeground(Color.RED);
        tabbedPane.addTab("Logout", logoutPanel);
        tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, logoutLabel);

        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedComponent() == logoutPanel) {
                int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to log out?", "Logout", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    frame.dispose();
                    new LoginScreen();
                } else {
                    tabbedPane.setSelectedIndex(0);
                }
            }
        });

        return tabbedPane;
    }
}