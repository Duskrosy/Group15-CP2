/*
 * @Gav
*/

package com.motorph;

import com.motorph.models.PayrollRecord;
import com.motorph.repositories.PayrollRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PayrollPanel extends JPanel {
    private DefaultTableModel model;
    private JTable table;
    private JTextField filterIdField;
    private JComboBox<String> periodComboBox;
    private final String employeeID;
    private final String role;

    public PayrollPanel(String employeeID, String role) {
        this.employeeID = employeeID;
        this.role = role;

        setLayout(new BorderLayout()); // test out sizes * done

        String[] columns = {"Employee ID", "Employee Name", "Period", "Gross Pay", "Deductions", "Net Pay"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);

        // Auto resize see if it works * done
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // 800,400
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(800, 400)); // done

        if (role.equals("admin")) {
            JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            filterPanel.add(new JLabel("Filter by Employee ID:"));
            filterIdField = new JTextField(10);
            filterPanel.add(filterIdField);

            filterPanel.add(new JLabel("Cutoff Period:"));
            periodComboBox = new JComboBox<>(new String[]{"All", "May 15", "May 30"});
            filterPanel.add(periodComboBox);

            JButton applyFilterBtn = new JButton("Apply Filters");
            applyFilterBtn.addActionListener(e -> loadFilteredPayroll());
            filterPanel.add(applyFilterBtn);

            add(filterPanel, BorderLayout.NORTH);
        }

        add(scrollPane, BorderLayout.CENTER);

        // 800,500
        setPreferredSize(new Dimension(800, 500));

        loadPayrollData();
    }

    private void loadPayrollData() {
        model.setRowCount(0);
        List<PayrollRecord> records = PayrollRepository.getAll();

        for (PayrollRecord record : records) {
            if (role.equals("admin") || record.employeeId.equals(employeeID)) {
                model.addRow(record.toTableRow());
            }
        }
    }

    private void loadFilteredPayroll() {
        String empId = filterIdField.getText().trim();
        String period = (String) periodComboBox.getSelectedItem();

        model.setRowCount(0);
        List<PayrollRecord> results = PayrollRepository.filter(empId, period);

        for (PayrollRecord record : results) {
            model.addRow(record.toTableRow());
        }
    }
}
