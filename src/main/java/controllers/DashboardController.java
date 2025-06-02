/*
 * @author Gavril Escarcha (solo grind fr)
 * File: DashboardController.java
 * Notes: 
 */

package com.motorph.controllers;

import com.motorph.*;
import javax.swing.*;
import java.awt.*;

public class DashboardController {

    public static JTabbedPane buildDashboardTabs(String employeeID, String role, JFrame frame) {
        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("My Info", new EmployeeInfoPanel(employeeID));
        tabbedPane.addTab("Time Tracker", new TimeTracker(employeeID));
        tabbedPane.addTab("Payroll", new PayrollPanel(employeeID, role));

        if (role.equalsIgnoreCase("admin")) {
            tabbedPane.addTab("All Employees", new AllEmployeesPanel());
        }

        // Setup logout
        JPanel logoutPanel = new JPanel();
        logoutPanel.add(new JLabel("You have been logged out."));

        JLabel logoutLabel = new JLabel("Logout");
        logoutLabel.setForeground(Color.RED);
        tabbedPane.addTab("Logout", logoutPanel);
        tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, logoutLabel);

        // Listener for logout
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedComponent() == logoutPanel) {
                int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to log out?", "Logout", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    frame.dispose();
                    new LoginScreen();
                } else {
                    tabbedPane.setSelectedIndex(0); // Return to first tab
                }
            }
        });

        return tabbedPane;
    }
}
