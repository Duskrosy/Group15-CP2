/*
 * @author Gav
 * Note: ADD/EDIT Button Pop-Ups
 */

package com.motorph.ui.dialogs;

import com.motorph.models.Employee;

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
        JPanel panel = new JPanel(new GridLayout(fieldLabels.length, 2, 4, 4));

        for (int i = 0; i < fieldLabels.length; i++) {
            panel.add(new JLabel(fieldLabels[i]));
            fields[i] = new JTextField(existingData != null ? existingData.toArray()[i] : "");
            panel.add(fields[i]);
        }

        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");

        ok.addActionListener(e -> {
            String[] data = new String[fieldLabels.length];
            for (int i = 0; i < data.length; i++) {
                data[i] = fields[i].getText().trim();
            }
            result = new Employee(data);
            dispose();
        });

        cancel.addActionListener(e -> {
            result = null;
            dispose();
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(ok);
        buttonPanel.add(cancel);

        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(parent);
    }

    public Employee getResult() {
        return result;
    }
}
