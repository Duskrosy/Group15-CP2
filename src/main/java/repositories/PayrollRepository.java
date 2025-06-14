/*
 * @author Gav
 * File: Payroll Repository
 * IMPORTANT: CALL HERE INSTEAD OF DIRETCLY TO FILE
 */

package com.motorph.repositories;

import com.motorph.models.PayrollRecord;
import com.motorph.models.AttendanceRecord;
import com.motorph.models.Employee;
import java.text.SimpleDateFormat;
import java.util.*;

public class PayrollRepository {

    // No more FILE_PATH or CSV writing!

    public static List<PayrollRecord> getAll() {
        List<PayrollRecord> records = new ArrayList<>();
        List<Employee> employees = EmployeeRepository.getAllEmployees();
        List<AttendanceRecord> attendance = AttendanceRepository.getAll();

        // Map: employeeId -> Map<cutoffKey, List<AttendanceRecord>>
        Map<String, Map<String, List<AttendanceRecord>>> empToPeriod = new HashMap<>();
        SimpleDateFormat inFmt = new SimpleDateFormat("MM/dd/yyyy");
        Calendar cal = Calendar.getInstance();

        // Organize attendance by employee and cutoff period (1-15, 16-end)
        for (AttendanceRecord rec : attendance) {
            String cutoffKey = "";
            try {
                Date d = inFmt.parse(rec.date.trim());
                cal.setTime(d);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                int month = cal.get(Calendar.MONTH) + 1;
                int year = cal.get(Calendar.YEAR);

                if (day <= 15) {
                    cutoffKey = String.format("%04d-%02d-15", year, month);
                } else {
                    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                    cutoffKey = String.format("%04d-%02d-%02d", year, month, cal.get(Calendar.DAY_OF_MONTH));
                }
            } catch (Exception ex) {
                continue;
            }

            empToPeriod
                .computeIfAbsent(rec.employeeId, k -> new HashMap<>())
                .computeIfAbsent(cutoffKey, k -> new ArrayList<>())
                .add(rec);
        }

        // Generate payroll for each employee for each cutoff
        for (Employee emp : employees) {
            Map<String, List<AttendanceRecord>> periods = empToPeriod.get(emp.id);
            if (periods == null) continue;
            for (Map.Entry<String, List<AttendanceRecord>> entry : periods.entrySet()) {
                String period = entry.getKey();
                List<AttendanceRecord> logs = entry.getValue();

                // Sum total hours for the cutoff
                double totalHours = 0;
                for (AttendanceRecord log : logs) {
                    try {
                        SimpleDateFormat tFmt = new SimpleDateFormat("HH:mm");
                        Date in = tFmt.parse(log.logIn.trim());
                        Date out = tFmt.parse(log.logOut.trim());
                        long diffMs = out.getTime() - in.getTime();
                        double hours = diffMs / (1000.0 * 60 * 60);
                        if (hours < 0) hours = 0;
                        totalHours += hours;
                    } catch (Exception ex) {
                        // skip broken rows
                    }
                }

                // Use safe numeric helpers from Employee class
                double hourlyRate = emp.getHourlyRate();
                double gross = totalHours * hourlyRate;
                double deductions = 0.0; // Update this if you add deductions logic!
                double net = gross - deductions;

                String fullName = emp.firstName + " " + emp.lastName;

                records.add(new PayrollRecord(new String[] {
                    emp.id,
                    fullName,
                    period,
                    String.format("%.2f", gross),
                    String.format("%.2f", deductions),
                    String.format("%.2f", net)
                }));
            }
        }
        return records;
    }

    public static List<PayrollRecord> filter(String empId, String period) {
        List<PayrollRecord> results = new ArrayList<>();
        for (PayrollRecord record : getAll()) {
            boolean matchId = empId == null || empId.isBlank() || record.employeeId.equals(empId);
            boolean matchPeriod = period == null || period.equals("All") || record.period.equals(period);
            if (matchId && matchPeriod) {
                results.add(record);
            }
        }
        return results;
    }

    // These are now unsupported, attendance is king!
    public static void saveOrUpdate(PayrollRecord newData, String oldEmpId) {
        throw new UnsupportedOperationException("Payroll data is 100% computed from attendance. Editing not supported.");
    }

    public static void deleteById(String empId) {
        throw new UnsupportedOperationException("Payroll data is 100% computed from attendance. Deletion not supported.");
    }
}
