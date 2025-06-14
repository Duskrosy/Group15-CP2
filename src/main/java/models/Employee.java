/*
 * @author Gavril Escarcha 
 * Notes: Now includes a 'deleted' flag for soft/hard delete.
 */

package com.motorph.models;

public class Employee {
    public String id;
    public String lastName;
    public String firstName;
    public String birthday;
    public String address;
    public String phoneNumber;
    public String sss;
    public String philhealth;
    public String tin;
    public String pagibig;
    public String status;
    public String position;
    public String supervisor;
    public String basicSalary;
    public String riceSubsidy;
    public String phoneAllowance;
    public String clothingAllowance;
    public String grossRate;
    public String hourlyRate;
    public boolean deleted = false; // Soft-delete flag (default: not deleted)

    public Employee(String[] data) {
        if (data.length >= 19) {
            this.id = data[0];
            this.lastName = data[1];
            this.firstName = data[2];
            this.birthday = data[3];
            this.address = data[4];
            this.phoneNumber = data[5];
            this.sss = data[6];
            this.philhealth = data[7];
            this.tin = data[8];
            this.pagibig = data[9];
            this.status = data[10];
            this.position = data[11];
            this.supervisor = data[12];
            this.basicSalary = data[13];
            this.riceSubsidy = data[14];
            this.phoneAllowance = data[15];
            this.clothingAllowance = data[16];
            this.grossRate = data[17];
            this.hourlyRate = data[18];

            // If a 20th column exists, treats it as the 'deleted' flag
            if (data.length >= 20) {
                this.deleted = "true".equalsIgnoreCase(data[19].trim());
            }
        }
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String[] toCsvArray() {
        return new String[]{
            id, lastName, firstName, birthday, address, phoneNumber, sss, philhealth, tin, pagibig, status,
            position, supervisor, basicSalary, riceSubsidy, phoneAllowance, clothingAllowance, grossRate, hourlyRate,
            String.valueOf(deleted) // Always include the deleted flag
        };
    }

    // For tables etc
    public String[] toArray() {
        return toCsvArray();
    }

    // --- Numeric helper methods ---

    public double getBasicSalary() {
        try { return Double.parseDouble(basicSalary.replace(",", "").trim()); }
        catch (Exception e) { return 0.0; }
    }

    public double getRiceSubsidy() {
        try { return Double.parseDouble(riceSubsidy.replace(",", "").trim()); }
        catch (Exception e) { return 0.0; }
    }

    public double getPhoneAllowance() {
        try { return Double.parseDouble(phoneAllowance.replace(",", "").trim()); }
        catch (Exception e) { return 0.0; }
    }

    public double getClothingAllowance() {
        try { return Double.parseDouble(clothingAllowance.replace(",", "").trim()); }
        catch (Exception e) { return 0.0; }
    }

    public double getGrossRate() {
        try { return Double.parseDouble(grossRate.replace(",", "").trim()); }
        catch (Exception e) { return 0.0; }
    }

    public double getHourlyRate() {
        try { return Double.parseDouble(hourlyRate.replace(",", "").trim()); }
        catch (Exception e) { return 0.0; }
    }
}
