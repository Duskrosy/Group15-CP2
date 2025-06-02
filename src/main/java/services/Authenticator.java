/*
 * @author Gavril
 * File: Login System
 */

package com.motorph;

import com.opencsv.CSVReader;
import com.motorph.utils.DataPathHelper;
import java.io.FileReader;

public class Authenticator {
    public static String authenticate(String employeeID, String password) {
        try (
            CSVReader reader = new CSVReader(new FileReader(DataPathHelper.getLoginFile()))
        ) {
            String[] line;
            boolean firstLine = true;
            while ((line = reader.readNext()) != null) {
                if (firstLine) { // skip header
                    firstLine = false;
                    continue;
                }
                if (line.length >= 3) {
                    String storedID = line[0].trim();
                    String storedPassword = line[1].trim();
                    String role = line[2].trim();

                    if (employeeID.equals(storedID) && password.equals(storedPassword)) {
                        return role; // "admin" or "user"
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error logging in, check your internet connection.: " + e.getMessage());
        }

        return null; // invalid credentials
    }
}
