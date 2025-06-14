/*
 * @author Gavril
 * File: TimeTrackerService.java
 * Notes: Handles time in/out, greeting, and attendance status.
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
    private LocalTime timeOut;
    private boolean onBreak = false;  // UI state only, does not persist

    public TimeTrackerService(String employeeId, String employeeName) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
    }

    // UI logic only: lunch break
    public boolean isOnBreak() { return onBreak; }
    public void setOnBreak(boolean b) { onBreak = b; }

    private Employee getEmployee() {
        return EmployeeRepository.findById(employeeId);
    }

public String getGreeting() {
    int hour = LocalTime.now().getHour();
    String greet;
    if (hour < 12) greet = "Good morning, ";
    else if (hour < 18) greet = "Good afternoon, ";
    else greet = "Good evening, ";

    // Use HTML for wrapping and bold name
    return "<html>" + greet + "<b>" + employeeName + "</b></html>";
}

    /**
     * Returns a structured status info object for the UI.
     */
    public StatusInfo getStatusInfo() {
        AttendanceLog todayLog = getLatestTodayLog();
        LocalTime now = LocalTime.now();

        String status, subStatus = "", timeInStr = "", timeOutStr = "";
        String timeInMsg = "", timeOutMsg = "";

        // --- Current status ---
        if (todayLog == null || todayLog.timeIn == null) {
            status = "<span style='color:red;'>NOT CLOCKED IN</span>";
            subStatus = "You have not timed in today.";
        } else if (onBreak) {
            status = "<span style='color:orange;'>ON BREAK</span>";
            subStatus = "You are currently on break.";
            // Show time in below
        } else if (todayLog.timeOut == null) {
            status = "<span style='color:green;'>CLOCKED IN</span>";
            subStatus = "You are clocked in.";
        } else {
            status = "<span style='color:blue;'>CLOCKED OUT</span>";
            subStatus = "You have timed out. Have a good rest of your day.";
        }

        // --- Time In/Out fields ---
        if (todayLog != null && todayLog.timeIn != null) {
            timeInStr = todayLog.timeIn.format(DateTimeFormatter.ofPattern("hh:mm a"));
            timeInMsg = todayLog.timeIn.isAfter(LocalTime.of(8, 0))
                ? "<span style='color:red;'>You were late today.</span>"
                : "You were on time today.";
        }
        if (todayLog != null && todayLog.timeOut != null) {
            timeOutStr = todayLog.timeOut.format(DateTimeFormatter.ofPattern("hh:mm a"));
            timeOutMsg = "You have timed out. Have a good rest of your day.";
        }

        return new StatusInfo(
            status,
            subStatus,
            timeInStr,
            timeInMsg,
            timeOutStr,
            timeOutMsg
        );
    }

    // UI logic: can show TimeIn/TimeOut
    public boolean canTimeInNow() { return !hasOpenAttendanceEntry(); }
    public boolean canTimeOutNow() { return hasOpenAttendanceEntry(); }

    private boolean hasOpenAttendanceEntry() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        File attendanceFile = DataPathHelper.getAttendanceFile();
        try (BufferedReader reader = new BufferedReader(new FileReader(attendanceFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] f = line.split(",", -1);
                if (f.length >= 6 && f[0].equals(employeeId) && f[3].equals(dateStr)) {
                    if (!f[4].isEmpty() && (f[5].isEmpty() || f[5].isBlank())) {
                        return true;
                    }
                }
            }
        } catch (Exception ignore) {}
        return false;
    }

    private AttendanceLog getLatestTodayLog() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        File attendanceFile = DataPathHelper.getAttendanceFile();
        AttendanceLog latest = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(attendanceFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] f = line.split(",", -1);
                if (f.length >= 6 && f[0].equals(employeeId) && f[3].equals(dateStr)) {
                    LocalTime logIn = null, logOut = null;
                    try { if (!f[4].isEmpty()) logIn = LocalTime.parse(f[4]); } catch (Exception ignored) {}
                    try { if (!f[5].isEmpty()) logOut = LocalTime.parse(f[5]); } catch (Exception ignored) {}
                    latest = new AttendanceLog(logIn, logOut);
                }
            }
        } catch (Exception ignore) {}
        return latest;
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
        onBreak = false; // reset break on time in
        return logInStr;
    }

    public String timeOutAndLog() {
        LocalDate today = LocalDate.now();
        String dateStr = today.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        String logOutStr = LocalTime.now().withSecond(0).withNano(0)
                .format(DateTimeFormatter.ofPattern("HH:mm"));

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
                    !fields[4].isEmpty() &&
                    (fields[5].isEmpty() || fields[5].isBlank())) {
                    // Update open entry with time out
                    fields[5] = logOutStr;
                    updated = true;
                    line = String.join(",", fields);
                }
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (updated) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(attendanceFile))) {
                for (String l : lines) {
                    writer.write(l);
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            onBreak = false; // reset break on time out
            return logOutStr;
        }
        return null;
    }

    public boolean isLate() {
        AttendanceLog latest = getLatestTodayLog();
        return latest != null && latest.timeIn != null && latest.timeIn.isAfter(LocalTime.of(8, 0));
    }

    public String formatTime(LocalTime time) {
        return time.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    // Helper classes
    public static class AttendanceLog {
        public final LocalTime timeIn;
        public final LocalTime timeOut;
        public AttendanceLog(LocalTime in, LocalTime out) {
            this.timeIn = in;
            this.timeOut = out;
        }
    }
    public static class StatusInfo {
        public final String status;       // HTML status label
        public final String subStatus;    // Message under status
        public final String timeIn;       // e.g. 08:34 AM
        public final String timeInMsg;    // e.g. You were late today.
        public final String timeOut;      // e.g. 05:07 PM
        public final String timeOutMsg;   // e.g. Have a good rest of your day.
        public StatusInfo(String s, String sub, String ti, String tim, String to, String tomsg) {
            status = s; subStatus = sub; timeIn = ti; timeInMsg = tim; timeOut = to; timeOutMsg = tomsg;
        }
    }
}
