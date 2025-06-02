package com.motorph;

/**
 * @author Gavril pogi
 * honestly I'm glad it just works
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Authenticator {
    public static String authenticate(String employeeID, String password) {
        try (BufferedReader br = new BufferedReader(new FileReader("login.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String storedID = parts[0].trim();
                    String storedPassword = parts[1].trim();
                    String role = parts[2].trim();

                    if (employeeID.equals(storedID) && password.equals(storedPassword)) {
                        return role; // "admin" or "user"
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null; // invalid credentials
    }
}