/*
 * @author Gav
 * File: EmployeeRepository
 * Notes: File Management for employee_data.csv
 * IMPORTANT: CALL HERE INSTEAD OF DIRETCLY TO FILE
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
    // FILE_PATH removed, testing muna

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
        String[] header = null;
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
                    header = line;
                    csvLines.add(header);
                } else if (line[0].equals(oldEmpId)) {
                    csvLines.add(newData.toCsvArray());
                    updated = true;
                } else {
                    csvLines.add(line);
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

    public static void deleteById(String empId) {
        List<String[]> csvLines = new ArrayList<>();
        String[] header = null;

        File empFile = DataPathHelper.getEmployeeDataFile();

        try (
            FileReader fr = new FileReader(empFile);
            CSVReader csvReader = new CSVReader(fr)
        ) {
            String[] line;
            int index = 0;
            while ((line = csvReader.readNext()) != null) {
                if (index == 0) {
                    header = line;
                    csvLines.add(header);
                } else if (!line[0].equals(empId)) {
                    csvLines.add(line);
                }
                index++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try (
            FileWriter fw = new FileWriter(empFile);
            CSVWriter csvWriter = new CSVWriter(fw)
        ) {
            csvWriter.writeAll(csvLines);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
