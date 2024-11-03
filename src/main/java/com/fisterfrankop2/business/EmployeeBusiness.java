package com.fisterfrankop2.business;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import companydata.Department;
import companydata.Employee;

public class EmployeeBusiness extends BusinessEntity {
    private static final Logger logger = Logger.getLogger(EmployeeBusiness.class.getName());
    private DepartmentBusiness departmentBusiness = null;

    public EmployeeBusiness() {
        this.departmentBusiness = new DepartmentBusiness();
    }

    // Method to retrieve a single employee by ID
    public Employee getEmployee(int empId) {
        try {
            return this.dl.getEmployee(empId);
        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Error retrieving employee", e);
            return null;
        }
    }

    // Method to retrieve all employees by department ID
    public List<Employee> getAllEmployees(String companyName) {
        try {
            return this.dl.getAllEmployee(companyName);
        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Error retrieving employees", e);
            return Collections.emptyList();
        }
    }

    // Method to insert a new employee
    public Employee insertEmployee(Employee employee) {
        try {
            return this.dl.insertEmployee(employee);
        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Error inserting employee", e);
            return null;
        }
    }

    // Method to update an existing employee
    public Employee updateEmployee(Employee employee) {
        try {
            return this.dl.updateEmployee(employee);
        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Error updating employee", e);
            return null;
        }
    }

    // Method to delete an employee by ID
    public boolean deleteEmployee(int empId) {
        try {
            return this.dl.deleteEmployee(empId) == 1;
        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Error deleting employee", e);
            return false;
        }
    }

    // Method to delete all employees
    public boolean deleteAllEmployees(String companyName) {
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

    // Validation methods
    public boolean validateUniqueEmpNo(String empNo, String companyName, Integer empId) {
        List<Employee> allEmployees = getAllEmployees(companyName);
        for (Employee emp : allEmployees) {
            if (emp.getEmpNo().equals(empNo) && (empId == null || emp.getId() != empId)) {
                logger.log(Level.WARNING, "Employee number {0} must be unique in the database", empNo);
                return false;
            }
        }
        return true;
    }

    public boolean validateDepartment(int deptId, String companyName) {
        Department department = this.departmentBusiness.getDepartment(companyName, deptId);
        if (department == null) {
            logger.log(Level.WARNING, "Department ID {0} does not exist for company: {1}",
                    new Object[] { deptId, companyName });
            return false;
        }
        return true;
    }

    public boolean validateManager(Integer mngId, String companyName) {
        if (mngId != 0 && getEmployee(mngId) == null) {
            logger.log(Level.WARNING, "Manager ID {0} does not exist for company: {1}",
                    new Object[] { mngId, companyName });
            return false;
        }
        return true;
    }

    public String validateHireDate(Date hireDate) {
        Date currentDate = new Date(System.currentTimeMillis());
        if (hireDate.after(currentDate)) {
            logger.log(Level.WARNING, "Hire date {0} cannot be in the future", hireDate);
            return "Hire date " + hireDate + " cannot be in the future";
        }
        LocalDate parsedDate = hireDate.toLocalDate();
        if (parsedDate.getDayOfWeek().getValue() > 5) {
            logger.log(Level.WARNING, "Hire date {0} must be a weekday (Monday-Friday)", hireDate);
            return "Hire date " + hireDate + " must be a weekday (Monday-Friday)";
        }
        return "true";
    }

    public boolean validateEmpIdExists(int empId) {
        if (getEmployee(empId) == null) {
            logger.log(Level.WARNING, "Employee ID {0} does not exist", empId);
            return false;
        }
        return true;
    }
}
