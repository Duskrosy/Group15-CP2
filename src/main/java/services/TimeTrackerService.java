/*
 * @author Gavril
 * File: TimeTrackerService.java
 * Notes: I swear to god kung ganto kahirap ilipat from csv to db bak a mag drop na lang ako joke
 */

package com.motorph.services;

import com.motorph.repositories.EmployeeRepository;
import com.motorph.models.Employee;
import com.motorph.utils.DataPathHelper;

import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TimeTrackerService {
    private final String employeeId;
    private final String employeeName;
    private LocalTime timeIn;

    public TimeTrackerService(String employeeId, String employeeName) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
    }

    private Employee getEmployee() {
        return EmployeeRepository.findById(employeeId);
    }

    public String timeIn() {
        timeIn = LocalTime.now().withSecond(0).withNano(0);
        LocalDate today = LocalDate.now();
        String dateStr = today.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        String logInStr = timeIn.format(DateTimeFormatter.ofPattern("HH:mm"));

        Employee emp = getEmployee();
        if (emp == null) return "Unknown Employee";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DataPathHelper.getAttendanceFile(), true))) {
            String[] row = {
                emp.id,
                emp.lastName,
                emp.firstName,
                dateStr,
                logInStr,
                ""
            };
            writer.write(String.join(",", row));
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return logInStr;
    }

    public String timeOutAndLog() {
        if (timeIn == null) return null;

        LocalTime timeOut = LocalTime.now().withSecond(0).withNano(0);
        LocalDate today = LocalDate.now();
        String dateStr = today.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        String logOutStr = timeOut.format(DateTimeFormatter.ofPattern("HH:mm"));

        List<String> lines = new ArrayList<>();
        boolean updated = false;
        File attendanceFile = DataPathHelper.getAttendanceFile();

        try (BufferedReader reader = new BufferedReader(new FileReader(attendanceFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",", -1);
                if (!updated &&
                    fields.length >= 6 &&
                    fields[0].equals(employeeId) &&
                    fields[3].equals(dateStr) &&
                    (fields[5].isEmpty() || fields[5].equals(""))) {
                    fields[5] = logOutStr;
                    updated = true;
                    line = String.join(",", fields);
                }
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!updated) {
            Employee emp = getEmployee();
            if (emp != null) {
                String[] row = {
                    emp.id,
                    emp.lastName,
                    emp.firstName,
                    dateStr,
                    "",
                    logOutStr
                };
                lines.add(String.join(",", row));
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(attendanceFile))) {
            for (String l : lines) {
                writer.write(l);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return logOutStr;
    }

    public boolean isLate() {
        return timeIn != null && timeIn.isAfter(LocalTime.of(8, 0));
    }

    public String formatTime(LocalTime time) {
        return time.format(DateTimeFormatter.ofPattern("HH:mm"));
    }
}
