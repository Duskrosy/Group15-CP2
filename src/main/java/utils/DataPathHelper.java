/*
 * @Gavril
 * Notes: This is honestly cool
 * Notes 2: Find a way to encrypt while still being able to read ok
*/

package com.motorph.utils;

import java.io.*;

public class DataPathHelper {
    private static final String DATA_FOLDER = "data";

    // Utility to ensure data dir exists
    private static File getDataDir() {
        File dataDir = new File(DATA_FOLDER);
        if (!dataDir.exists()) dataDir.mkdirs();
        return dataDir;
    }

    public static File getAttendanceFile() {
        return getOrCreateFile("attendance.csv", "Employee #,Last Name,First Name,Date,Log In,Log Out");
    }

    public static File getEmployeeDataFile() {
        return getOrCreateFile("employee_data.csv", 
            "Employee #,Last Name,First Name,Birthday,Address,Phone Number,SSS #,Philhealth #,TIN #,Pag-ibig #,Status,Position,Immediate Supervisor,Basic Salary,Rice Subsidy,Phone Allowance,Clothing Allowance,Gross Semi-monthly Rate,Hourly Rate"
        );
    }

    public static File getLoginFile() {
        return getOrCreateFile("login.csv", "employeeId,password,isAdmin");
    }

    // Helper checks file if it exists else creates
    private static File getOrCreateFile(String fileName, String header) {
        File file = new File(getDataDir(), fileName);
        if (!file.exists()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(header);
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }
}
