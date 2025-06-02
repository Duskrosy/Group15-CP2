/*
 * @Gav
*/

package com.motorph;

import com.motorph.controllers.EmployeeController;
import com.motorph.models.Employee;
import com.motorph.ui.components.EmployeeDetailPane;

import javax.swing.*;
import java.awt.*;

public class EmployeeInfoPanel extends JPanel {
    private final EmployeeController controller = new EmployeeController();
    private final String[] labels = {
        "Employee ID", "Last Name", "First Name", "Birthday", "Address", "Phone Number",
        "SSS #", "Philhealth #", "TIN #", "Pag-ibig #", "Status", "Position",
        "Immediate Supervisor", "Basic Salary", "Rice Subsidy", "Phone Allowance",
        "Clothing Allowance", "Gross Semi-monthly Rate", "Hourly Rate"
    };

    public EmployeeInfoPanel(String employeeID) {
        setLayout(new BorderLayout());

        Employee employee = controller.findById(employeeID);
        JLabel header = new JLabel("My Info", SwingConstants.CENTER);
        header.setFont(new Font("SansSerif", Font.BOLD, 20));
        add(header, BorderLayout.NORTH);

        if (employee != null) {
            EmployeeDetailPane detailPane = new EmployeeDetailPane(labels);
            detailPane.showEmployee(employee);
            add(new JScrollPane(detailPane), BorderLayout.CENTER);
        } else {
            add(new JLabel("Employee not found."), BorderLayout.CENTER);
        }
    }
}
