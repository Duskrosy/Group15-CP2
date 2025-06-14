package com.motorph.ui.dialogs;

import com.motorph.models.Employee;
import com.motorph.utils.FontHelper;

import javax.swing.*;
import java.awt.*;

public class EmployeeDialog extends JDialog {
    private JTextField[] fields;
    private String[] fieldLabels;
    private Employee result;

    public EmployeeDialog(Frame parent, String[] fieldLabels, Employee existingData) {
        super(parent, existingData == null ? "Add Employee" : "Edit Employee", true);
        this.fieldLabels = fieldLabels;

        fields = new JTextField[fieldLabels.length];
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(250, 251, 254));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(7, 8, 7, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        for (int i = 0; i < fieldLabels.length; i++) {
            c.gridx = 0;
            c.gridy = i;
            JLabel label = new JLabel(fieldLabels[i]);
            panel.add(label, c);

            c.gridx = 1;
            fields[i] = new JTextField(existingData != null ? existingData.toArray()[i] : "", 22);
            fields[i].setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(67, 56, 202, 60), 1),
                    BorderFactory.createEmptyBorder(7, 8, 7, 8)
            ));
            panel.add(fields[i], c);
        }

        // --- Buttons ---
        JButton ok = new JButton(existingData == null ? "Add" : "Save");
        ok.setBackground(new Color(67, 56, 202));
        ok.setForeground(Color.WHITE);
        ok.setFocusPainted(false);
        ok.setFont(new Font("SansSerif", Font.BOLD, 14));
        ok.setBorder(BorderFactory.createEmptyBorder(8, 28, 8, 28));
        ok.addActionListener(e -> {
            String[] data = new String[fieldLabels.length];
            for (int i = 0; i < data.length; i++) {
                data[i] = fields[i].getText().trim();
            }
            result = new Employee(data);
            dispose();
        });

        JButton cancel = new JButton("Cancel");
        cancel.setBackground(new Color(247, 247, 252));
        cancel.setForeground(new Color(67, 56, 202));
        cancel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        cancel.setFocusPainted(false);
        cancel.setBorder(BorderFactory.createEmptyBorder(8, 28, 8, 28));
        cancel.addActionListener(e -> {
            result = null;
            dispose();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 8));
        buttonPanel.setBackground(new Color(250, 251, 254));
        buttonPanel.add(ok);
        buttonPanel.add(cancel);

        setLayout(new BorderLayout(0, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(18, 26, 8, 26));
        add(panel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    public Employee getResult() {
        return result;
    }
}
