package com.fisterfrankop2.business;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import companydata.Employee;

public class EmployeeBusiness extends BusinessEntity {

    // Method to retrieve a single employee by ID
    public Employee getEmployee(int empId) {
        try {
            return this.dl.getEmployee(empId);
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(BusinessEntity.class.getName())
                    .log(java.util.logging.Level.SEVERE, "Error retrieving employee", e);
            return null;
        }
    }

    // Method to retrieve all employees by department ID
    public List<Employee> getAllEmployees(String companyName) {
        try {
            return this.dl.getAllEmployee(companyName);
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(BusinessEntity.class.getName())
                    .log(java.util.logging.Level.SEVERE, "Error retrieving employees", e);
            return Collections.emptyList();
        }
    }

    // Method to insert a new employee
    public Employee insertEmployee(Employee employee) {
        try {
            return this.dl.insertEmployee(employee);
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(BusinessEntity.class.getName())
                    .log(java.util.logging.Level.SEVERE, "Error inserting employee", e);
            return null;
        }
    }

    // Method to update an existing employee
    public Employee updateEmployee(Employee employee) {
        try {
            return this.dl.updateEmployee(employee);
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(BusinessEntity.class.getName())
                    .log(java.util.logging.Level.SEVERE, "Error updating employee", e);
            return null;
        }
    }

    // Method to delete an employee by ID
    public boolean deleteEmployee(int empId) {
        try {
            return this.dl.deleteEmployee(empId) == 1;
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(BusinessEntity.class.getName())
                    .log(java.util.logging.Level.SEVERE, "Error deleting employee", e);
            return false;
        }
    }

    // Method to delete all employees
    public boolean deleteAllEmployees(String companyName) {
        Logger logger = Logger.getLogger(EmployeeBusiness.class.getName());

        try {
            List<Employee> employees = this.dl.getAllEmployee(companyName);
            for (Employee employee : employees) {
                this.dl.deleteEmployee(employee.getId());
                logger.log(Level.INFO, "Deleted employee: {0}", employee);
            }
            logger.info("All employees deleted successfully.");
            return true;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deleting all employees", e);
            return false;
        }
    }
}
