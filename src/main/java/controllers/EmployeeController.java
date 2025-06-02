/*
 * @author Gavril Escarcha 😤
 * File: EmployeeController.java
 * Notes: Employee_data.csv CRUD | Connector instead of always having to call for the file eme
 */

package com.motorph.controllers;

import com.motorph.models.Employee;
import com.motorph.repositories.EmployeeRepository;

import java.util.List;

public class EmployeeController {

    public List<Employee> getAllEmployees() {
        return EmployeeRepository.getAllEmployees();
    }

    public Employee findById(String id) {
        return EmployeeRepository.findById(id);
    }

    public void saveOrUpdate(Employee employee, String oldId) {
        EmployeeRepository.saveOrUpdate(employee, oldId);
    }

    public void deleteById(String id) {
        EmployeeRepository.deleteById(id);
    }
}
