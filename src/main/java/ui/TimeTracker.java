/*
 * @author Gavril
 * File: TimeTracker.java
 * Notes: Final cleaned up version. One centered "CURRENT STATUS" with name, only one message per detail.
 */

package com.motorph;

import com.motorph.services.TimeTrackerService;
import com.motorph.services.TimeTrackerService.StatusInfo;
import com.motorph.repositories.EmployeeRepository;
import com.motorph.models.Employee;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimeTracker extends JPanel {
    private JLabel currentStatusLabel;
    private JLabel subStatusLabel;
    private JLabel greetingClockLabel;
    private JLabel timeInLabel;
    private JLabel timeInMsgLabel;
    private JLabel timeOutLabel;

    private JCheckBox timeFormatToggle;
    private JButton timeInButton;
    private JButton timeOutButton;
    private JButton breakButton;
    private Timer clockTimer;
    private final TimeTrackerService service;
    private final String employeeName;
    private final String employeeID;

    public TimeTracker(String employeeID) {
        this.employeeID = employeeID;
        this.employeeName = fetchEmployeeName(employeeID);
        this.service = new TimeTrackerService(employeeID, employeeName);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);

        // CURRENT STATUS
        currentStatusLabel = new JLabel("", SwingConstants.CENTER);
        currentStatusLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        currentStatusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Sub-status below current status
        subStatusLabel = new JLabel("", SwingConstants.CENTER);
        subStatusLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        subStatusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Greeting + Live Clock
        greetingClockLabel = new JLabel("", SwingConstants.CENTER);
        greetingClockLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        greetingClockLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 12-hour toggle
        timeFormatToggle = new JCheckBox("12-hour format");
        timeFormatToggle.setAlignmentX(Component.CENTER_ALIGNMENT);
        timeFormatToggle.addActionListener(e -> updateGreetingClock());

        // Buttons
        timeInButton = new JButton("Time In");
        timeOutButton = new JButton("Time Out");
        breakButton = new JButton("Lunch Break");
        timeInButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        timeOutButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        breakButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        timeInButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        timeOutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        breakButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        timeInButton.addActionListener(e -> handleTimeIn());
        timeOutButton.addActionListener(e -> handleTimeOut());
        breakButton.addActionListener(e -> handleBreakToggle());

        // Info labels for time in/out
        timeInLabel = new JLabel();
        timeInMsgLabel = new JLabel();
        timeOutLabel = new JLabel();

        timeInLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        timeInMsgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        timeOutLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Use HTML for alignment and clarity
        timeInLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timeInMsgLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timeOutLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Layout - Centered, with spacing
        add(Box.createVerticalStrut(24));
        add(currentStatusLabel);
        add(subStatusLabel);
        add(Box.createVerticalStrut(10));
        add(greetingClockLabel);
        add(timeFormatToggle);
        add(Box.createVerticalStrut(14));
        add(timeInButton);
        add(timeOutButton);
        add(breakButton);
        add(Box.createVerticalStrut(12));
        add(timeInLabel);
        add(timeInMsgLabel);
        add(timeOutLabel);

        updateUIState();

        clockTimer = new Timer(1000, e -> {
            updateGreetingClock();
            updateUIState();
        });
        clockTimer.start();
    }

    private void updateUIState() {
        StatusInfo info = service.getStatusInfo();

        // Button logic
        boolean onBreak = service.isOnBreak();
        boolean canTimeIn = service.canTimeInNow();
        boolean canTimeOut = service.canTimeOutNow();

        timeInButton.setVisible(!onBreak && canTimeIn);
        timeOutButton.setVisible(!onBreak && canTimeOut);
        breakButton.setVisible(canTimeOut); // Only if clocked in

        breakButton.setText(onBreak ? "Resume Work" : "Lunch Break");

        // Main status
        currentStatusLabel.setText(
            "<html><div style='text-align:center;'><b>CURRENT STATUS: " + info.status +
            " - " + employeeName + "</b></div></html>");
        subStatusLabel.setText("<html><div style='text-align:center;'>" + info.subStatus + "</div></html>");

        // Time in info/message
        if (!info.timeIn.isEmpty() && !service.isOnBreak()) {
            timeInLabel.setText("<html><div style='text-align:center;'>Time In: <b>" +
                info.timeIn + "</b></div></html>");
            timeInMsgLabel.setText("<html><div style='text-align:center;'>" +
                info.timeInMsg + "</div></html>");
        } else {
            timeInLabel.setText("");
            timeInMsgLabel.setText("");
        }

	if (!info.timeOut.isEmpty() && info.status.contains("CLOCKED OUT")) {
			// Only show the Time Out time, NOT the message again
			timeOutLabel.setText("<html><div style='text-align:center;'>Time Out: <b>" +
				info.timeOut + "</b></div></html>");
	} else {
			timeOutLabel.setText("");
	}
	}

    private void handleTimeIn() {
        String formattedTime = service.timeIn();
        boolean late = service.isLate();
        String displayTime = displayFormat(formattedTime);
        if (late) {
            JOptionPane.showMessageDialog(this, "You were late today. Time In: " + displayTime, "Late Notice", JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "On time. Time In: " + displayTime);
        }
        updateUIState();
    }

    private void handleTimeOut() {
        String formattedTime = service.timeOutAndLog();
        if (formattedTime == null) {
            JOptionPane.showMessageDialog(this, "Please time in first.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String displayTime = displayFormat(formattedTime);
        JOptionPane.showMessageDialog(this, "Time Out at " + displayTime);
        updateUIState();
    }

    private void handleBreakToggle() {
        boolean nowOnBreak = !service.isOnBreak();
        service.setOnBreak(nowOnBreak);
        if (nowOnBreak) {
            JOptionPane.showMessageDialog(this, "Lunch Break started. Your time is paused.", "On Break", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Welcome back! Lunch Break ended.", "Back to Work", JOptionPane.INFORMATION_MESSAGE);
        }
        updateUIState();
    }

    private void updateGreetingClock() {
        String greeting = service.getGreeting();
        LocalTime now = LocalTime.now();
        String timePattern = timeFormatToggle.isSelected() ? "hh:mm:ss a" : "HH:mm:ss";
        String timeStr = now.format(DateTimeFormatter.ofPattern(timePattern));
        greetingClockLabel.setText("<html><div style='text-align:center;'>" + greeting + " The time is <b>" + timeStr + "</b></div></html>");
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
