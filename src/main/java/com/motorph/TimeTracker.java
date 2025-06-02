package com.motorph;

/**
 * based on @DAYANG GWAPA's take on old loginsession.java 
 * extended and built upon by @Gavril Pogi
 * P.S. if this breaks, it was always like this
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class TimeTracker extends JPanel {
    private JLabel statusLabel;
    private JCheckBox timeFormatToggle;
    private LocalTime timeIn, timeOut;
    private String employeeID;
    private String employeeName;

    public TimeTracker(String employeeID) {
        this.employeeID = employeeID;
        this.employeeName = fetchEmployeeName(employeeID); // hoping this actually returns something useful // EDIT: ok na

        setLayout(new BorderLayout());
		
		// ganto mag title pls guys
        JLabel titleLabel = new JLabel("Time Tracker - " + employeeName, SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new FlowLayout());
        // added the 12-hour format toggle kasi hindi ako marunong mag-basa ng military time -gav
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
        timeIn = LocalTime.now().withSecond(0).withNano(0); // sana gumana nakita ko lng to sa tutorial
        String timeStr = formatTime(timeIn);
        boolean late = isLate(timeIn);

        if (late) {
            JOptionPane.showMessageDialog(this, "You are late! Time In: " + timeStr, "Late Notice", JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "On time. Time In: " + timeStr);
        }

        statusLabel.setText("Time In at " + timeStr);
    }

    private void handleTimeOut() {
        if (timeIn == null) {
            JOptionPane.showMessageDialog(this, "Please Time In first.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        timeOut = LocalTime.now().withSecond(0).withNano(0);
        String timeStr = formatTime(timeOut);

        JOptionPane.showMessageDialog(this, "Time Out at " + timeStr);
        statusLabel.setText(statusLabel.getText() + " | Time Out at " + timeStr);

        saveTimeLog(); // yung HR na di marunong dito lang natingin
    }

    private void saveTimeLog() {
        LocalDate today = LocalDate.now();
        String period = getCutoffPeriod(today);

        Duration duration = Duration.between(timeIn, timeOut);
        long hoursWorked = duration.toHours();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("payroll_data.txt", true))) {
            writer.write(String.format("%s\t%s\t%s\t%s\t%s\t%d%n",
                    employeeID, employeeName, period, timeIn, timeOut, hoursWorked));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isLate(LocalTime time) {
        return time.isAfter(LocalTime.of(8, 0)); // Hindi ko pa alam paano maglagay ng grace-period tinamad nako
    }

    private String formatTime(LocalTime time) {
		// refer to line 54
        return timeFormatToggle.isSelected()
                ? time.format(DateTimeFormatter.ofPattern("hh:mm a"))  // 12-hour
                : time.format(DateTimeFormatter.ofPattern("HH:mm"));   // 24-hour
    }

    private String getCutoffPeriod(LocalDate date) {
        int day = date.getDayOfMonth();
        String monthYear = date.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        return day <= 15 ? monthYear + "-15" : monthYear + "-30";
    }

    private String fetchEmployeeName(String empId) {
        try (BufferedReader reader = new BufferedReader(new FileReader("employee_data.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\t");
                if (fields[0].equals(empId)) {
                    return fields[2] + " " + fields[1]; // Firstname Lastname
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Unknown Employee"; // Just in case kesa mag crash ulit
    }
}