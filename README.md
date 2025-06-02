## Group 9 CP2

GUI-Trial and better file-handling for the MotorPH App

I have a name for it now; **MotorPESH**

## How to Run

1. Make sure you have **Java 17+** and **Maven** installed.
- Here's the link to [download Java](https://www.oracle.com/ph/java/technologies/downloads/)
- Here's how to [install Maven](https://phoenixnap.com/kb/install-maven-windows)

3. Clone the project or download the ZIP.
4. From the root directory, run:


```bash
mvn clean compile exec:java
```

### **Use these test credentials from /data/login.csv to log in!**

Example Admin - **EID: 10000 PW: admin1234**

Example User - **EID: 10001 PW: password123**

## ✅ Features Checklist

### 🔧 Application Conversion & Input Handling
- [x] Convert the console-based application to a working GUI-based application.
- [x] Provide exception handling for the input of Employee Number and Pay Coverage.

### 📋 Employee Table & Salary Computation
- [x] Display all employees in a JTable with the following fields:
  - Employee Number
  - Last Name
  - First Name
  - SSS Number
  - PhilHealth Number
  - TIN
  - Pag-IBIG Number
  - And every type of pay (Rice subsidy, clothing allowance, etc...)
- [x] Allow user to select an employee from the table and click **View Employee** to open a detailed frame.
- [x] Prompt the user to select a month for salary computation in the detail frame.
- [x] After clicking **Compute**, display employee details and computed salary info in the same frame.
- [x] Add a **New Employee** button that opens a form to enter new employee information.
- [x] Append new employee data to the CSV file and refresh the JTable after submission.

### 🛠️ Edit/Delete Employee Records
- [x] When a JTable row is selected, display employee data in textboxes.
- [x] Enable an **Update** button to modify and save selected employee details.
- [x] Enable a **Delete** button to remove selected employee from the CSV file.
- [x] Refresh the JTable after update or deletion.

### 🔐 Login System
- [x] Require correct username and password to access the application.
- [x] Store authorized accounts in a separate CSV file.
- [x] Deny access and display an error message for invalid login credentials.

### What I'm proud of honestly:
- Cutoff periods are automatically determined by date (15 and 30 of each month).
- Payroll entries are automatically captured using the Time Tracker.
- You can toggle 12-hour and 24-hour formats in the Time Tracker.

## COMING SOON!
- Amazing GUI with UI/UX 😂
- Encryption of data for protection especially with read/write files
- Forgot Password / Sign-up OR Integration with MS Azure
- Compute Every Type of Deductions with Tiering

## Group Members:
- Gavril (Pogi)
- hi

## Built With
- Java (JDK 17)
- Maven
- Swing (javax.swing)

