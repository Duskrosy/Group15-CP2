/*
 * @author Gavril Escarcha
 * File: LoginController.java
 * Notes: Handles login and calls Authenticator well to authenticate 👍
 */

package com.motorph.controllers;

import com.motorph.Authenticator;
import com.motorph.models.LoginSession;

public class LoginController {
    public static LoginSession login(String employeeID, String password) {
        String role = Authenticator.authenticate(employeeID, password);
        if (role != null) {
            return new LoginSession(employeeID, role);
        }
        return null;
    }
}
