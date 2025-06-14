/*
 * @author Gav
 * File: EmployeeRepository
 * Notes: File Management for employee_data.csv (now with soft/hard delete support)
 */

package com.motorph.repositories;

import com.motorph.models.Employee;
import com.motorph.utils.DataPathHelper;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeRepository {

    // Updated: the CSV header must include 'deleted' as the last column!
    private static final String[] HEADER = {
        "id", "lastName", "firstName", "birthday", "address", "phoneNumber", "sss", "philhealth",
        "tin", "pagibig", "status", "position", "supervisor", "basicSalary", "riceSubsidy",
        "phoneAllowance", "clothingAllowance", "grossRate", "hourlyRate", "deleted"
    };

    public static List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        File empFile = DataPathHelper.getEmployeeDataFile();

        try (
            FileReader fr = new FileReader(empFile);
            CSVReader csvReader = new CSVReader(fr)
        ) {
            String[] line;
            boolean firstLine = true;
            while ((line = csvReader.readNext()) != null) {
                if (firstLine) { // skips header
                    firstLine = false;
                    continue;
                }
                employees.add(new Employee(line));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return employees;
    }

    public static Employee findById(String id) {
        return getAllEmployees().stream()
                .filter(e -> e.id != null && e.id.equals(id))
                .findFirst()
                .orElse(null);
    }

    public static void saveOrUpdate(Employee newData, String oldEmpId) {
        List<String[]> csvLines = new ArrayList<>();
        boolean updated = false;

        File empFile = DataPathHelper.getEmployeeDataFile();

        // Load all lines from the CSV (preserving header)
        try (
            FileReader fr = new FileReader(empFile);
            CSVReader csvReader = new CSVReader(fr)
        ) {
            String[] line;
            int index = 0;
            while ((line = csvReader.readNext()) != null) {
                if (index == 0) {
                    csvLines.add(HEADER); // Always write updated header
                } else if (line[0].equals(oldEmpId)) {
                    csvLines.add(newData.toCsvArray());
                    updated = true;
                } else {
                    // Append the correct deleted flag if missing (backwards compatibility)
                    String[] row = line;
                    if (row.length < 20) {
                        String[] newRow = new String[20];
                        System.arraycopy(row, 0, newRow, 0, row.length);
                        newRow[19] = "false";
                        row = newRow;
                    }
                    csvLines.add(row);
                }
                index++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // If new, add to end
        if (!updated && newData.id != null && !newData.id.isEmpty()) {
            csvLines.add(newData.toCsvArray());
        }

        // Write all back
        try (
            FileWriter fw = new FileWriter(empFile);
            CSVWriter csvWriter = new CSVWriter(fw)
        ) {
            csvWriter.writeAll(csvLines);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Soft-delete: set deleted=true and update. */
    public static void softDeleteById(String empId) {
        List<Employee> all = getAllEmployees();
        for (Employee emp : all) {
            if (emp.id.equals(empId)) {
                emp.deleted = true;
                break;
            }
        }
        saveAll(all);
    }

    /** Hard-delete: remove if already soft-deleted. */
    public static void hardDeleteById(String empId) {
        List<Employee> all = getAllEmployees();
        all.removeIf(emp -> emp.id.equals(empId) && emp.deleted);
        saveAll(all);
    }

    /** Overwrites all employees to CSV. */
    private static void saveAll(List<Employee> employees) {
        File empFile = DataPathHelper.getEmployeeDataFile();
        try (
            FileWriter fw = new FileWriter(empFile);
            CSVWriter csvWriter = new CSVWriter(fw)
        ) {
            csvWriter.writeNext(HEADER);
            for (Employee emp : employees) {
                csvWriter.writeNext(emp.toCsvArray());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Legacy hard delete method (still works for backwards compatibility)
    public static void deleteById(String empId) {
        hardDeleteById(empId);
    }
}
