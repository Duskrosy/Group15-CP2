package com.motorph;

import javax.swing.*;
import java.awt.*;

/**
 * based on @DAYANG GWAPA's work 
 * extended and built upon by @Gavril Pogi
 */
public class Dashboard extends JFrame {

    public Dashboard(String employeeID, String role) {
        setTitle("MotorPH Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Add user-level tabs
        tabbedPane.addTab("My Info", new EmployeeInfoPanel(employeeID));
        tabbedPane.addTab("Time Tracker", new TimeTracker(employeeID));
        tabbedPane.addTab("Payroll", new PayrollPanel(employeeID, role));

        // If admin; show all employee info
        if (role.equalsIgnoreCase("admin")) {
            tabbedPane.addTab("All Employees", new AllEmployeesPanel());
        }

        // Putchangina paano ba ilipat yung logout button sa taas? Tuwing ilalagay ko siya bilang tab nasisira yung code.
		// Ok na pala.
        JLabel logoutLabel = new JLabel("Logout");
        logoutLabel.setForeground(Color.RED);
        JPanel logoutPanel = new JPanel();
        logoutPanel.add(new JLabel("You have been logged out."));
        tabbedPane.addTab("Logout", logoutPanel);
        tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, logoutLabel);

        // Handle logout logic when "Logout" tab is selected
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedComponent() == logoutPanel) {
                int confirmed = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Logout", JOptionPane.YES_NO_OPTION);
                if (confirmed == JOptionPane.YES_OPTION) {
                    dispose();
                    new LoginScreen();
                } else {
                    tabbedPane.setSelectedIndex(0); // Go back to first tab
                }
            }
        });

        add(tabbedPane);
        setVisible(true);
    }
}