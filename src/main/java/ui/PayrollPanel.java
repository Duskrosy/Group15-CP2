package com.motorph.ui;

import com.motorph.models.PayrollRecord;
import com.motorph.repositories.PayrollRepository;
import com.motorph.ui.components.PayrollTableModel;
import com.motorph.utils.CurrencyFormatter;
import com.motorph.utils.FontHelper;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.stream.Collectors;

public class PayrollPanel extends JPanel {
    private PayrollTableModel tableModel;
    private JTable table;
    private JTextField employeeIdField;
    private JComboBox<String> periodCombo;
    private JLabel summaryLabel;
    private JButton filterBtn, exportBtn, printBtn;
    private boolean isAdmin;
    private String employeeId;

    public PayrollPanel(String employeeId, boolean isAdmin) {
        this.employeeId = employeeId;
        this.isAdmin = isAdmin;

        setLayout(new BorderLayout(0, 18));
        setBackground(new Color(247, 247, 252));

        // --- Top Filter Card ---
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 16));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(18, 32, 12, 32),
                BorderFactory.createLineBorder(new Color(27, 67, 160, 20), 1)
        ));

        // Filter controls
        employeeIdField = new JTextField(12);
        employeeIdField.setFont(FontHelper.regular(15f));
        employeeIdField.setToolTipText("Employee ID (leave blank for all)");

        // Get all payrolls (admin) or just user's (user)
        List<PayrollRecord> allPayrolls = isAdmin
                ? PayrollRepository.getAll()
                : PayrollRepository.filter(employeeId, null);

        String[] periods = allPayrolls.stream().map(r -> r.period).distinct().sorted().toArray(String[]::new);
        String[] periodOpts = new String[periods.length + 2];
        periodOpts[0] = "All";
        periodOpts[1] = "Last 30 Days";
        System.arraycopy(periods, 0, periodOpts, 2, periods.length);

        periodCombo = new JComboBox<>(periodOpts);
        periodCombo.setFont(FontHelper.regular(15f));

        filterBtn = new JButton("Apply");
        filterBtn.setFont(FontHelper.medium(14f));

        exportBtn = new JButton("Export (Last 30 Days)");
        exportBtn.setFont(FontHelper.medium(14f));
        printBtn = new JButton("Print");
        printBtn.setFont(FontHelper.medium(14f));

        // Admin sees all controls
        if (isAdmin) {
            filterPanel.add(new JLabel("Employee ID:"));
            filterPanel.add(employeeIdField);
            filterPanel.add(new JLabel("Period:"));
            filterPanel.add(periodCombo);
            filterPanel.add(filterBtn);
            filterPanel.add(exportBtn);
            filterPanel.add(printBtn);
        } else {
            // User: disable, hide filter controls, always self payroll, period enabled
            employeeIdField.setText(employeeId);
            employeeIdField.setEnabled(false);
            periodCombo.setEnabled(true);
            filterPanel.add(new JLabel("Period:"));
            filterPanel.add(periodCombo);
            filterPanel.add(filterBtn);
            // Hide export/print for users
            exportBtn.setVisible(false);
            printBtn.setVisible(false);
        }

        add(filterPanel, BorderLayout.NORTH);

        // --- Payroll Table ---
        tableModel = new PayrollTableModel(allPayrolls);
        table = new JTable(tableModel);
        table.setFont(FontHelper.regular(15f));
        table.setRowHeight(28);
        table.setFillsViewportHeight(true);

        // Table header styling
        JTableHeader th = table.getTableHeader();
        th.setFont(FontHelper.bold(16f));
        th.setBackground(new Color(15, 39, 114));
        th.setForeground(Color.WHITE);

        // Custom cell renderers for currency and color
        table.getColumnModel().getColumn(3).setCellRenderer(new CurrencyRenderer());
        table.getColumnModel().getColumn(4).setCellRenderer(new DeductionRenderer());
        table.getColumnModel().getColumn(5).setCellRenderer(new NetPayRenderer());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(8, 30, 18, 30));
        add(scrollPane, BorderLayout.CENTER);

        // --- Summary Row ---
        summaryLabel = new JLabel();
        summaryLabel.setFont(FontHelper.medium(16f));
        summaryLabel.setBorder(BorderFactory.createEmptyBorder(8, 40, 12, 20));
        updateSummary(allPayrolls);
        add(summaryLabel, BorderLayout.SOUTH);

        // --- Action logic ---
        filterBtn.addActionListener(e -> applyFilters());
        if (isAdmin) {
            exportBtn.addActionListener(e -> exportLast30Days());
            printBtn.addActionListener(e -> printTable());
        }

        // Support Enter key for search
        employeeIdField.addActionListener(e -> applyFilters());
        periodCombo.addActionListener(e -> applyFilters());
    }

    // --- Filtering and summary ---
    private void applyFilters() {
        String empId = isAdmin ? employeeIdField.getText().trim() : employeeId;
        String period = (String) periodCombo.getSelectedItem();
        List<PayrollRecord> filtered = PayrollRepository.filter(empId, period);
        tableModel.setPayrolls(filtered);
        updateSummary(filtered);
    }

    private void updateSummary(List<PayrollRecord> records) {
        double gross = 0, ded = 0, net = 0;
        for (PayrollRecord p : records) {
            gross += parseDouble(p.grossPay);
            ded += parseDouble(p.deductions);
            net += parseDouble(p.netPay);
        }
        summaryLabel.setText(String.format(
            "<html><b>Total Gross:</b> %s &nbsp;&nbsp; <b>Deductions:</b> %s &nbsp;&nbsp; <b>Net:</b> %s</html>",
            CurrencyFormatter.format(String.valueOf(gross)),
            CurrencyFormatter.format(String.valueOf(ded)),
            CurrencyFormatter.format(String.valueOf(net))
        ));
    }

    // --- Custom Renderers ---
    static class CurrencyRenderer extends DefaultTableCellRenderer {
        @Override
        public void setValue(Object v) {
            setText(CurrencyFormatter.format(v == null ? "" : v.toString()));
            setHorizontalAlignment(RIGHT);
        }
    }
    static class DeductionRenderer extends CurrencyRenderer {
        @Override
        public void setValue(Object v) {
            super.setValue(v);
            try {
                double d = parseDouble(v);
                setForeground(d > 0 ? new Color(206, 47, 64) : new Color(30, 30, 30));
                setFont(getFont().deriveFont(Font.BOLD));
            } catch (Exception ex) {
                setForeground(new Color(30, 30, 30));
            }
        }
    }
    static class NetPayRenderer extends CurrencyRenderer {
        @Override
        public void setValue(Object v) {
            super.setValue(v);
            try {
                double d = parseDouble(v);
                setForeground(d > 0 ? new Color(10, 150, 80) : new Color(206, 47, 64));
                setFont(getFont().deriveFont(Font.BOLD));
            } catch (Exception ex) {
                setForeground(new Color(30, 30, 30));
            }
        }
    }

    private static double parseDouble(Object v) {
        if (v == null) return 0.0;
        try {
            if (v instanceof Double) return (Double) v;
            if (v instanceof Number) return ((Number) v).doubleValue();
            return Double.parseDouble(v.toString().replace(",", "").trim());
        } catch (Exception ex) {
            return 0.0;
        }
    }

    // --- Dummy export/print methods (stub) ---
    private void exportLast30Days() {
        JOptionPane.showMessageDialog(this, "Export (Last 30 Days) feature coming soon!");
    }
    private void printTable() {
        JOptionPane.showMessageDialog(this, "Print feature coming soon!");
    }
}
