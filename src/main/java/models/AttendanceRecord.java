/*
 * @author Gavril Escarcha
 * Notes: Simple model for attendance rows, might change soon, saw this in a tutorial
 */

package com.motorph.models;

public class AttendanceRecord {
    public String employeeId;
    public String lastName;
    public String firstName;
    public String date; // MM/dd/yyyy
    public String logIn; // HH:mm
    public String logOut; // HH:mm

    public AttendanceRecord(String[] row) {
        this.employeeId = row[0];
        this.lastName = row[1];
        this.firstName = row[2];
        this.date = row[3];
        this.logIn = row[4];
        this.logOut = row[5];
    }
}