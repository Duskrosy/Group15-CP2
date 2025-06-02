package com.motorph;

/**
 * based on @DAYANG GWAPA's work 
 * extended and built upon by @Gavril Pogi
 */

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;

public class PayrollPanel extends JPanel {
    private DefaultTableModel model;
    private JTable table;
    private JTextField filterIdField;
    private JComboBox<String> periodComboBox;
    private String employeeID;
    private String role;

    public PayrollPanel(String employeeID, String role) {
        this.employeeID = employeeID;
        this.role = role;
        setLayout(new BorderLayout());

        String[] columns = {"Employee ID", "Employee Name", "Period", "Gross Pay", "Deductions", "Net Pay"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);

        // Admin-only filter dont touch
        if (role.equals("admin")) {
            JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            filterPanel.add(new JLabel("Filter by Employee ID:"));
            filterIdField = new JTextField(10);
            filterPanel.add(filterIdField);

            filterPanel.add(new JLabel("Cutoff Period:"));
            periodComboBox = new JComboBox<>(new String[]{"All", "May 15", "May 30"});
            filterPanel.add(periodComboBox);

            JButton applyFilterBtn = new JButton("Apply Filters");
            filterPanel.add(applyFilterBtn);
            applyFilterBtn.addActionListener(e -> applyFilters());

            add(filterPanel, BorderLayout.NORTH);
        }

        add(new JScrollPane(table), BorderLayout.CENTER);
        loadPayrollData(); // initial load
    }

    private void loadPayrollData() {
        model.setRowCount(0); // Clear existing data
        try (BufferedReader br = new BufferedReader(new FileReader("payroll_data.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] row = line.split("\t");
                if (row.length == 6) {
                    boolean isAdmin = role.equals("admin");
                    boolean isOwner = row[0].equals(employeeID);

                    if (isAdmin || isOwner) {
                        model.addRow(row);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
		//honestly how the hell do I make this more efficient?

    private void applyFilters() {
        if (!role.equals("admin")) return;

        String empIdFilter = filterIdField.getText().trim();
        String selectedPeriod = (String) periodComboBox.getSelectedItem();

        model.setRowCount(0); // Clear rows
        try (BufferedReader br = new BufferedReader(new FileReader("payroll_data.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] row = line.split("\t");
                if (row.length == 6) {
                    boolean matchesId = empIdFilter.isEmpty() || row[0].equals(empIdFilter);
                    boolean matchesPeriod = selectedPeriod.equals("All") || row[2].equals(selectedPeriod);
                    if (matchesId && matchesPeriod) {
                        model.addRow(row);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}