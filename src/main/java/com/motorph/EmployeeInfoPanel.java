package com.motorph;

/**
 * @author Gavril pogi
 * cool
 */

import javax.swing.*;
import java.awt.*;
import java.io.*;

public class EmployeeInfoPanel extends JPanel {

    public EmployeeInfoPanel(String employeeID) {
        setLayout(new BorderLayout());

		// Hindi ko na alam paano pagandahin yung layout nito napapangitan parin talaga ako
        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        try (BufferedReader br = new BufferedReader(new FileReader("employee_data.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split("\t");
				// wow 0 security
                if (data[0].equals(employeeID)) {
                    // mas matagal pa ako nag hanap paraan paano icopy paste edi sana tinaype ko na lang
                    String[] labels = {
                        "Employee ID", "Last Name", "First Name", "Birthday", "Address", "Phone Number",
                        "SSS #", "Philhealth #", "TIN #", "Pag-ibig #", "Status", "Position",
                        "Immediate Supervisor", "Basic Salary", "Rice Subsidy", "Phone Allowance",
                        "Clothing Allowance", "Gross Semi-monthly Rate", "Hourly Rate"
                    };

                    for (int i = 0; i < labels.length && i < data.length; i++) {
                        infoPanel.add(new JLabel(labels[i] + ":"));
                        infoPanel.add(new JLabel(data[i])); 
                    }
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); //PLS WAG NIYO NA GALAWENNNNNNN
            infoPanel.add(new JLabel("Error loading employee data.")); // dito ko lang nalaman mali yung EmployeeID sa username kaya di gumagana
        }

        add(new JLabel("My Info", SwingConstants.CENTER), BorderLayout.NORTH);
        add(infoPanel, BorderLayout.CENTER);
    }
}