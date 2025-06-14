package com.motorph.ui;

import com.motorph.models.Employee;
import com.motorph.repositories.EmployeeRepository;
import com.motorph.utils.FontHelper;
import com.motorph.ui.dialogs.EmployeeDialog;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class AllEmployeesPanel extends JPanel {
    private JTable table;
    private EmployeeTableModel tableModel;
    private JTextField searchField;
    private JPanel detailPanel;
    private JButton addBtn, editBtn, deleteBtn, restoreBtn;
    private List<Employee> allEmployees;
    private boolean isAdmin;

    public AllEmployeesPanel(boolean isAdmin) {
        this.isAdmin = isAdmin;

        setLayout(new BorderLayout(0, 18));
        setBackground(new Color(247, 247, 252));

        // --- Top Bar (Search + Actions) ---
        JPanel topBar = new JPanel(new BorderLayout(16, 0));
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(18, 32, 12, 32),
            BorderFactory.createLineBorder(new Color(27, 67, 160, 24), 1)
        ));

        // Search field
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(FontHelper.medium(15f));
        topBar.add(searchLabel, BorderLayout.WEST);

        searchField = new JTextField();
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(27, 67, 160, 30), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        searchField.setFont(FontHelper.regular(15f));
        searchField.setPreferredSize(new Dimension(240, 34));
        topBar.add(searchField, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        btnPanel.setOpaque(false);

        addBtn = makeButton("Add");
        editBtn = makeButton("Edit");
        deleteBtn = makeButton("Delete");
        restoreBtn = makeButton("Restore");
        restoreBtn.setVisible(false);

        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(restoreBtn);

        topBar.add(btnPanel, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

        // --- Table ---
        allEmployees = EmployeeRepository.getAllEmployees();
        tableModel = new EmployeeTableModel(allEmployees);
        table = new JTable(tableModel);
        table.setFont(FontHelper.regular(15f));
        table.setRowHeight(30);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Set preferred widths for columns 
        TableColumnModel colModel = table.getColumnModel();
        colModel.getColumn(0).setPreferredWidth(130); // Employee #
        colModel.getColumn(1).setPreferredWidth(270); // Name
        colModel.getColumn(2).setPreferredWidth(130); // Status

        // Table header
        JTableHeader th = table.getTableHeader();
        th.setFont(FontHelper.bold(16f));
        th.setBackground(new Color(15, 39, 114));
        th.setForeground(Color.WHITE);
        ((DefaultTableCellRenderer)th.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.LEFT);

        // Status pill
        table.getColumnModel().getColumn(2).setCellRenderer(new StatusCellRenderer());

        // ID with copy icon
        table.getColumnModel().getColumn(0).setCellRenderer(new CopyCellRenderer());

        // Enable horizontal scroll for wide tables
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createEmptyBorder(8, 30, 18, 10));
        tableScroll.getHorizontalScrollBar().setUnitIncrement(16);

        // --- Detail Panel (right side) ---
        detailPanel = new JPanel(new GridBagLayout());
        detailPanel.setBackground(Color.WHITE);
        detailPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(24, 24, 24, 24),
            BorderFactory.createLineBorder(new Color(27, 67, 160, 16), 1)
        ));

        JLabel noSelect = new JLabel("Select an employee to view details");
        noSelect.setFont(FontHelper.regular(16f));
        noSelect.setForeground(new Color(90, 90, 90));
        detailPanel.add(noSelect);

        // --- Split Pane ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableScroll, detailPanel);
        splitPane.setResizeWeight(0.0);
        splitPane.setDividerLocation(670);
        splitPane.setDividerSize(8);
        add(splitPane, BorderLayout.CENTER);

        // --- Listeners ---
        // Search filter
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterTable(); }
            public void removeUpdate(DocumentEvent e) { filterTable(); }
            public void changedUpdate(DocumentEvent e) { filterTable(); }
        });

        // Selection: show details, update buttons
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                Employee emp = tableModel.getEmployee(table.convertRowIndexToModel(row));
                showDetails(emp);
                boolean softDeleted = emp.status != null && emp.status.equalsIgnoreCase("DELETED");
                editBtn.setEnabled(!softDeleted);
                deleteBtn.setText(softDeleted ? "Hard Delete" : "Delete");
                restoreBtn.setVisible(softDeleted);
            } else {
                clearDetails();
            }
        });

        // Buttons
		addBtn.addActionListener(e -> openEmployeeDialog(null));
		editBtn.addActionListener(e -> {
			Employee emp = getSelectedEmployee();
			if (emp != null && !isSoftDeleted(emp)) {
			openEmployeeDialog(emp);
		} else {
        JOptionPane.showMessageDialog(this, "Please select an active employee to edit.");
		}
	});
        deleteBtn.addActionListener(e -> deleteOrHardDeleteSelected());
        restoreBtn.addActionListener(e -> restoreSelected());

        // Disable actions for user
        if (!isAdmin) {
            addBtn.setVisible(false);
            editBtn.setVisible(false);
            deleteBtn.setVisible(false);
            restoreBtn.setVisible(false);
        }

        // Row sorter that moves DELETED rows to the bottom and highlights them
        TableRowSorter<EmployeeTableModel> sorter = new TableRowSorter<>(tableModel);
        sorter.setComparator(2, (o1, o2) -> {
            String s1 = o1 == null ? "" : o1.toString();
            String s2 = o2 == null ? "" : o2.toString();

            if (s1.equalsIgnoreCase("DELETED") && !s2.equalsIgnoreCase("DELETED")) return 1;
            if (!s1.equalsIgnoreCase("DELETED") && s2.equalsIgnoreCase("DELETED")) return -1;
            return s1.compareToIgnoreCase(s2);
        });
        table.setRowSorter(sorter);

        // Custom renderer to highlight soft-deleted rows gray
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);
                int modelRow = tbl.convertRowIndexToModel(row);
                Employee emp = tableModel.getEmployee(modelRow);
                if (emp.status != null && emp.status.equalsIgnoreCase("DELETED")) {
                    c.setBackground(isSelected ? new Color(160, 160, 160) : new Color(220, 220, 220));
                    c.setForeground(Color.DARK_GRAY);
                } else {
                    c.setBackground(isSelected ? tbl.getSelectionBackground() : tbl.getBackground());
                    c.setForeground(tbl.getForeground());
                }
                return c;
            }
        });
    }

    // Button helper
    private JButton makeButton(String label) {
        JButton btn = new JButton(label);
        btn.setBackground(new Color(67, 56, 202));
        btn.setForeground(Color.WHITE);
        btn.setFont(FontHelper.medium(14f));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(7, 28, 7, 28));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // TableModel
    private static class EmployeeTableModel extends AbstractTableModel {
        private final String[] columns = {"Employee #", "Name", "Status"};
        private List<Employee> employees;
        public EmployeeTableModel(List<Employee> employees) { this.employees = new ArrayList<>(employees); }
        public void setEmployees(List<Employee> employees) {
            this.employees = new ArrayList<>(employees); fireTableDataChanged();
        }
        public Employee getEmployee(int row) { return employees.get(row); }
        @Override public int getRowCount() { return employees.size(); }
        @Override public int getColumnCount() { return columns.length; }
        @Override public String getColumnName(int col) { return columns[col]; }
        @Override public Object getValueAt(int row, int col) {
            Employee e = employees.get(row);
            switch (col) {
                case 0: return e.id;
                case 1: return e.getFullName();
                case 2: return e.status;
                default: return "";
            }
        }
    }

    // Status cell with colored pill
    private static class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable table, Object v, boolean isSel, boolean focus, int r, int c) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, v, isSel, focus, r, c);
            String status = (v == null) ? "" : v.toString();
            lbl.setText(status);
            lbl.setOpaque(true);
            lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
            if ("ACTIVE".equalsIgnoreCase(status)) lbl.setForeground(new Color(32, 201, 151));
            else if ("DELETED".equalsIgnoreCase(status)) lbl.setForeground(new Color(240, 92, 77));
            else lbl.setForeground(new Color(67, 56, 202));
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            return lbl;
        }
    }

    // Copy icon on Employee #
    private class CopyCellRenderer extends DefaultTableCellRenderer {
        private final Icon copyIcon = UIManager.getIcon("FileView.floppyDriveIcon"); // You can use a custom icon!
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSel, boolean hasFocus, int row, int col) {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
            panel.setOpaque(true);
            panel.setBackground(isSel ? table.getSelectionBackground() : table.getBackground());

            JLabel idLabel = new JLabel(value == null ? "" : value.toString());
            idLabel.setFont(FontHelper.medium(15f));

            JButton copyBtn = new JButton("Copy");
            copyBtn.setFont(FontHelper.regular(11f));
            copyBtn.setFocusable(false);
            copyBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            copyBtn.setMargin(new Insets(2, 8, 2, 8));
            copyBtn.addActionListener(ev -> {
                StringSelection sel = new StringSelection(idLabel.getText());
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sel, null);
                JOptionPane.showMessageDialog(AllEmployeesPanel.this, "Copied to clipboard!");
            });

            panel.add(idLabel);
            panel.add(copyBtn);
            return panel;
        }
    }

    private void filterTable() {
        String q = searchField.getText().trim().toLowerCase();
        List<Employee> filtered = allEmployees.stream().filter(emp -> {
            return emp.id.toLowerCase().contains(q)
                || emp.getFullName().toLowerCase().contains(q)
                || (emp.status != null && emp.status.toLowerCase().contains(q));
        }).collect(Collectors.toList());
        tableModel.setEmployees(filtered);
        clearDetails();
    }

    private Employee getSelectedEmployee() {
        int row = table.getSelectedRow();
        return (row >= 0) ? tableModel.getEmployee(table.convertRowIndexToModel(row)) : null;
    }

    private boolean isSoftDeleted(Employee emp) {
        return emp.status != null && emp.status.equalsIgnoreCase("DELETED");
    }
	
	// Show Details
	private void showDetails(Employee emp) {
    detailPanel.removeAll();

    // Header with blue bg
    JLabel header = new JLabel("Employee Details (*/ω＼*)(*^_^*)");
    header.setFont(FontHelper.bold(20f));
    header.setOpaque(true);
    header.setBackground(new Color(15, 39, 114)); // blue background
    header.setForeground(Color.WHITE);
    header.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
    header.setHorizontalAlignment(SwingConstants.LEFT);

    detailPanel.setLayout(new BorderLayout());
    detailPanel.add(header, BorderLayout.NORTH);

    // Content panel with grid layout 0 rows, 2 columns, spacing 10x8
    JPanel content = new JPanel(new GridLayout(0, 2, 10, 8));
    content.setBackground(Color.WHITE);

    // Helper method to add label pairs
    addLabel(content, "Name:", emp.getFullName());
    addLabel(content, "Employee #:", emp.id);
    addLabel(content, "Status:", emp.status);
    addLabel(content, "Birthday:", emp.birthday);
    addLabel(content, "Address:", emp.address);
    addLabel(content, "Phone:", emp.phoneNumber);
    addLabel(content, "Position:", emp.position);
    addLabel(content, "Supervisor:", emp.supervisor);

    // Format numeric values with commas
    addLabel(content, "Basic Salary:", formatNumber(emp.basicSalary));
    addLabel(content, "Rice Subsidy:", formatNumber(emp.riceSubsidy));
    addLabel(content, "Phone Allowance:", formatNumber(emp.phoneAllowance));
    addLabel(content, "Clothing Allowance:", formatNumber(emp.clothingAllowance));
    addLabel(content, "Gross Rate:", formatNumber(emp.grossRate));
    addLabel(content, "Hourly Rate:", formatNumber(emp.hourlyRate));

    detailPanel.add(content, BorderLayout.CENTER);

    detailPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createEmptyBorder(0, 24, 24, 24), // no top padding for flush header
        BorderFactory.createLineBorder(new Color(27, 67, 160, 16), 1)
    ));
    detailPanel.setBackground(Color.WHITE);

    detailPanel.revalidate();
    detailPanel.repaint();
}


private void addLabel(JPanel panel, String label, String value) {
    JLabel lbl = new JLabel(label);
    lbl.setFont(FontHelper.medium(14f));
    lbl.setHorizontalAlignment(SwingConstants.LEFT);  // LEFT aligned label
    panel.add(lbl);

    JLabel val = new JLabel(value != null ? value : "");
    val.setFont(FontHelper.regular(14f));
    val.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 0));
    val.setHorizontalAlignment(SwingConstants.LEFT);  // LEFT aligned value
    panel.add(val);
}

private String formatNumber(String numStr) {
    try {
        double val = Double.parseDouble(numStr.replace(",", ""));
        return String.format("%,.2f", val);
    } catch (Exception e) {
        return numStr != null ? numStr : "";
    }
}

    private void addLabel(JPanel panel, String label, String value, int row, GridBagConstraints c) {
        c.gridx = 0; c.gridy = row;
        JLabel lbl = new JLabel(label);
        lbl.setFont(FontHelper.medium(14f));
        panel.add(lbl, c);

        c.gridx = 1;
        JLabel val = new JLabel(value != null ? value : "");
        val.setFont(FontHelper.regular(14f));
        panel.add(val, c);
    }

    private void clearDetails() {
        detailPanel.removeAll();
        JLabel noSelect = new JLabel("Select an employee to view details");
        noSelect.setFont(FontHelper.regular(16f));
        noSelect.setForeground(new Color(90, 90, 90));
        detailPanel.add(noSelect);
        detailPanel.revalidate();
        detailPanel.repaint();
    }

private void openEmployeeDialog(Employee emp) {
    Window window = SwingUtilities.getWindowAncestor(this);
    Frame parent = (window instanceof Frame) ? (Frame) window : null;

    String[] fullColumns = {
        "Employee #", "Last Name", "First Name", "Birthday", "Address", "Phone Number",
        "SSS #", "Philhealth #", "TIN #", "Pag-ibig #", "Status", "Position",
        "Immediate Supervisor", "Basic Salary", "Rice Subsidy", "Phone Allowance",
        "Clothing Allowance", "Gross Semi-monthly Rate", "Hourly Rate"
    };

    EmployeeDialog dialog = new EmployeeDialog(parent, fullColumns, emp);
    dialog.setVisible(true);

    Employee result = dialog.getResult();
    if (result != null) {
        EmployeeRepository.saveOrUpdate(result, emp != null ? emp.id : null);
        allEmployees = EmployeeRepository.getAllEmployees();
        tableModel.setEmployees(allEmployees);
        clearDetails();
    }
}


    // Delete/soft-delete/hard-delete logic with confirmation
    private void deleteOrHardDeleteSelected() {
        Employee emp = getSelectedEmployee();
        if (emp == null) return;

        if (isSoftDeleted(emp)) {
            int confirm = JOptionPane.showConfirmDialog(this, "This will permanently DELETE Employee #" + emp.id + ". Continue?", "Hard Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                // Hard delete: really remove
                EmployeeRepository.deleteById(emp.id);
                allEmployees = EmployeeRepository.getAllEmployees();
                tableModel.setEmployees(allEmployees);
                clearDetails();
            }
        } else {
            int confirm = JOptionPane.showConfirmDialog(this, "Soft-delete Employee #" + emp.id + "?\n(Employee will be flagged as deleted, not erased.)", "Soft Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                emp.status = "DELETED";
                EmployeeRepository.saveOrUpdate(emp, emp.id);
                allEmployees = EmployeeRepository.getAllEmployees();
                tableModel.setEmployees(allEmployees);
                clearDetails();
            }
        }
    }

    // Restore logic with confirmation
    private void restoreSelected() {
        Employee emp = getSelectedEmployee();
        if (emp == null || !isSoftDeleted(emp)) return;
        int confirm = JOptionPane.showConfirmDialog(this, "Restore Employee #" + emp.id + "?", "Restore", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            emp.status = "ACTIVE";
            EmployeeRepository.saveOrUpdate(emp, emp.id);
            allEmployees = EmployeeRepository.getAllEmployees();
            tableModel.setEmployees(allEmployees);
            clearDetails();
        }
    }
}
