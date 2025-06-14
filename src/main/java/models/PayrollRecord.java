/*
 * @author Gavril 
 * Notes: Outputs Payroll data | Handles Computation for later once I figure out kung ano formula
 * Note 2: Kailangan ko mahanap yung tamang formula for tax, mandatory + tiering
 */

package com.motorph.models;

public class PayrollRecord {
    public String employeeId;
    public String employeeName;
    public String period;
    public double grossPay;
    public double deductions;
    public double netPay;

    public PayrollRecord(String[] row) {
        try {
            if (row.length >= 6) {
                this.employeeId = row[0];
                this.employeeName = row[1];
                this.period = row[2];
                this.grossPay = Double.parseDouble(row[3]);
                this.deductions = Double.parseDouble(row[4]);
                this.netPay = Double.parseDouble(row[5]);
            } else {
                throw new IllegalArgumentException("Row too short for PayrollRecord: " + String.join(",", row));
            }
        } catch (NumberFormatException e) {
            // Handle (skip) if the numeric columns can't be parsed
            System.err.println("Failed to parse payroll record: " + String.join(",", row) + " (" + e.getMessage() + ")");
            this.employeeId = null;
        }
    }

    public String[] toCsvArray() {
        return new String[] {
            employeeId, employeeName, period,
            String.valueOf(grossPay),
            String.valueOf(deductions),
            String.valueOf(netPay)
        };
    }

    public Object[] toTableRow() {
        return new Object[]{
            employeeId, employeeName, period,
            String.format("%.2f", grossPay),
            String.format("%.2f", deductions),
            String.format("%.2f", netPay)
        };
    }
}