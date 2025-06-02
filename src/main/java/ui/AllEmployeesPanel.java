/*
 * @author Gavril
 * File: AllEmployeesPanel.java
 */

package com.motorph;

import com.motorph.controllers.EmployeeController;
import com.motorph.models.Employee;
import com.motorph.ui.components.EmployeeDetailPane;
import com.motorph.ui.dialogs.EmployeeDialog;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class AllEmployeesPanel extends JPanel {
    private final EmployeeController controller = new EmployeeController();

    private DefaultTableModel model;
    private JTable table;
    private EmployeeDetailPane detailPane;
    private JTextField searchField;
    private List<Employee> allEmployees;

    private final String[] fullColumns = {
        "Employee #", "Last Name", "First Name", "Birthday", "Address", "Phone Number",
        "SSS #", "Philhealth #", "TIN #", "Pag-ibig #", "Status", "Position",
        "Immediate Supervisor", "Basic Salary", "Rice Subsidy", "Phone Allowance",
        "Clothing Allowance", "Gross Semi-monthly Rate", "Hourly Rate"
    };

    public AllEmployeesPanel() {
        setLayout(new BorderLayout());

        model = new DefaultTableModel(new Object[]{"Employee #", "Name", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);

        detailPane = new EmployeeDetailPane(fullColumns);

        loadEmployeeData();

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    String empId = (String) table.getValueAt(selectedRow, 0);
                    Employee emp = controller.findById(empId);
                    if (emp != null) {
                        detailPane.showEmployee(emp);
                    }
                }
            }
        });

        JPanel topPanel = new JPanel(new BorderLayout());
        searchField = new JTextField();
        topPanel.add(new JLabel("Search: "), BorderLayout.WEST);
        topPanel.add(searchField, BorderLayout.CENTER);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
        });

        JButton addBtn = new JButton("Add");
        JButton editBtn = new JButton("Edit");
        JButton deleteBtn = new JButton("Delete");

        addBtn.addActionListener(e -> openEmployeeDialog(null));
        editBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                String id = (String) table.getValueAt(selectedRow, 0);
                Employee emp = controller.findById(id);
                openEmployeeDialog(emp);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a row to edit.");
            }
        });

        deleteBtn.addActionListener(e -> deleteSelectedRow());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(table), new JScrollPane(detailPane));
        splitPane.setResizeWeight(0.0);
        splitPane.setDividerLocation(440);
        splitPane.setDividerSize(6);

        add(topPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadEmployeeData() {
        model.setRowCount(0);
        allEmployees = controller.getAllEmployees();
        for (Employee emp : allEmployees) {
            model.addRow(new Object[]{emp.id, emp.getFullName(), emp.status});
        }
    }

    private void filter() {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchField.getText()));
        table.setRowSorter(sorter);
    }

    private void openEmployeeDialog(Employee data) {
        Window window = SwingUtilities.getWindowAncestor(this);
		Frame parent = (window instanceof Frame) ? (Frame) window : null;
		EmployeeDialog dialog = new EmployeeDialog(parent, fullColumns, data);

        dialog.setVisible(true);
        Employee result = dialog.getResult();

        if (result != null) {
            controller.saveOrUpdate(result, data != null ? data.id : null);
            loadEmployeeData();
        }
    }

    private void deleteSelectedRow() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a row to delete.");
            return;
        }

        String empId = (String) table.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete Employee #" + empId + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            controller.deleteById(empId);
            loadEmployeeData();
        }
    }
}
