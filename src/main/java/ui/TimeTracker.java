/*
 * @Gav
*/

package com.motorph;

import com.motorph.services.TimeTrackerService;
import com.motorph.repositories.EmployeeRepository;
import com.motorph.models.Employee;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimeTracker extends JPanel {
    private JLabel statusLabel;
    private JCheckBox timeFormatToggle;
    private TimeTrackerService service;

    public TimeTracker(String employeeID) {
        String employeeName = fetchEmployeeName(employeeID);
        this.service = new TimeTrackerService(employeeID, employeeName);

        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Time Tracker - " + employeeName, SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new FlowLayout());
        JButton timeInButton = new JButton("Time In");
        JButton timeOutButton = new JButton("Time Out");
        timeFormatToggle = new JCheckBox("Use 12-hour format");
        statusLabel = new JLabel("Status: Not logged in");

        timeInButton.addActionListener(e -> handleTimeIn());
        timeOutButton.addActionListener(e -> handleTimeOut());

        centerPanel.add(timeInButton);
        centerPanel.add(timeOutButton);
        centerPanel.add(timeFormatToggle);
        centerPanel.add(statusLabel);

        add(centerPanel, BorderLayout.CENTER);
    }

    private void handleTimeIn() {
        String formattedTime = service.timeIn();
        boolean late = service.isLate();

        String displayTime = displayFormat(formattedTime);

        if (late) {
            JOptionPane.showMessageDialog(this, "You are late! Time In: " + displayTime, "Late Notice", JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "On time. Time In: " + displayTime);
        }

        statusLabel.setText("Time In at " + displayTime);
    }

    private void handleTimeOut() {
        String formattedTime = service.timeOutAndLog();
        if (formattedTime == null) {
            JOptionPane.showMessageDialog(this, "Please Time In first.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String displayTime = displayFormat(formattedTime);

        JOptionPane.showMessageDialog(this, "Time Out at " + displayTime);
        statusLabel.setText(statusLabel.getText() + " | Time Out at " + displayTime);
    }

    private String displayFormat(String timeStr) {
        try {
            LocalTime time = LocalTime.parse(timeStr);
            return timeFormatToggle.isSelected()
                    ? time.format(DateTimeFormatter.ofPattern("hh:mm a"))
                    : time.format(DateTimeFormatter.ofPattern("HH:mm"));
        } catch (Exception e) {
            return timeStr;
        }
    }

    private String fetchEmployeeName(String empId) {
        Employee emp = EmployeeRepository.findById(empId);
        if (emp == null) return "Unknown Employee";
        return emp.getFullName();
    }
}
