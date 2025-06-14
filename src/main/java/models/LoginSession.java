/*
 * @author Gavril Escarcha solo grind fr fr 😤
 * Notes: Role Management 
 */

package com.motorph.models;

public class LoginSession {
    public final String employeeID;
    public final String role;

    public LoginSession(String employeeID, String role) {
        this.employeeID = employeeID;
        this.role = role;
    }
}
