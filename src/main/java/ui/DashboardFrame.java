package com.motorph.ui;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class DashboardFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private NavBarPanel navBar;
    private String employeeID, fullName;
    private boolean isAdmin;
    private Map<String, JPanel> panels = new HashMap<>();

    public DashboardFrame(String employeeID, String fullName, boolean isAdmin) {
        this.employeeID = employeeID;
        this.fullName = fullName;
        this.isAdmin = isAdmin;

        setTitle("MotorPH");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1660, 860));
        setMinimumSize(new Dimension(1100, 700));
        setResizable(false);

        try {
            setIconImage(new ImageIcon(getClass().getResource("/img/favicon.png")).getImage());
        } catch (Exception ex) {
            System.err.println("Favicon not found!");
        }

        // Nav bar with navigation callback
        navBar = new NavBarPanel(fullName.split(" ")[0], isAdmin, this::navigateTo);
        add(navBar, BorderLayout.NORTH);

        // Card panel with CardLayout
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

		// Panels for each section
		HomePanel homePanel = new HomePanel(employeeID, fullName, isAdmin);
		MyDetailsPanel myDetailsPanel = new MyDetailsPanel(employeeID);
		PayrollPanel payrollPanel = new PayrollPanel(employeeID, isAdmin);

		panels.put("Home", homePanel);
		panels.put("My Details", myDetailsPanel);
		panels.put("Payroll", payrollPanel);

		// Admin permission for AllEmployees
		if (isAdmin) {
		    panels.put("All Employees", new AllEmployeesPanel(isAdmin));
		}

// Add to cardPanel
for (Map.Entry<String, JPanel> entry : panels.entrySet()) {
    cardPanel.add(entry.getValue(), entry.getKey());
}

        add(cardPanel, BorderLayout.CENTER);
        add(new FooterPanel(), BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        // Show Home first
        navigateTo("Home");
    }

    // Handles navigation by showing the right panel
    private void navigateTo(String section) {
        cardLayout.show(cardPanel, section);
    }
}
