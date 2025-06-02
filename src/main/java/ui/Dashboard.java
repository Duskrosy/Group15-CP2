/*
 * @Gav
*/

package com.motorph;

import com.motorph.controllers.DashboardController;
import javax.swing.*;

public class Dashboard extends JFrame {

    public Dashboard(String employeeID, String role) {
        setTitle("MotorPH Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = DashboardController.buildDashboardTabs(employeeID, role, this);
        add(tabbedPane);
        setVisible(true);
    }
}
