package com.fisterfrankop2.business;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import companydata.Department;

public class DepartmentBusiness extends BusinessEntity {
    private static final Logger logger = Logger.getLogger(DepartmentBusiness.class.getName());

    // Method to retrieve a single department by company name and department id
    public Department getDepartment(String companyName, int deptId) {
        if (companyName.isBlank()) {
            logger.log(java.util.logging.Level.SEVERE, "The company name is required.");
            return null;
        }

        try {
            return this.dl.getDepartment(companyName, deptId);
        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Error retrieving department", e);
            return null;
        }
    }

    // Method to retrieve all departments by company name
    public List<Department> getAll(String companyName) {
        if (companyName.isBlank()) {
            logger.log(java.util.logging.Level.SEVERE, "The company name is required.");
            return Collections.emptyList();
        }

        return this.dl.getAllDepartment(companyName);
    }

    // Method to insert a new department
    public Department insertDepartment(Department department) {
        try {
            return this.dl.insertDepartment(department);
        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Error inserting department", e);
            return null;
        }
    }

    // Method to update a department
    public Department updateDepartment(Department department) {
        try {
            return this.dl.updateDepartment(department);
        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Error updating department", e);
            return null;
        }
    }

    // Method to delete a department
    public boolean deleteDepartment(String companyName, int deptId) {
        try {
            return this.dl.deleteDepartment(companyName, deptId) == 1;
        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Error deleting department", e);
            return false;
        }
    }

    // Method to delete all departments
    public boolean deleteAllDepartments(String companyName) {
        try {
            List<Department> departments = this.dl.getAllDepartment(companyName);
            for (Department department : departments) {
                this.dl.deleteDepartment(department.getCompany(), department.getId());
                logger.log(Level.INFO, "Deleted department: {0}", department);
            }
            logger.info("All departments deleted successfully.");
            return true;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deleting all departments", e);
            return false;
        }
    }

    public boolean validateUniqueDeptNo(String deptNo, String companyName, Integer deptId) {
        List<Department> allDepartments = getAll(companyName);
        for (Department dept : allDepartments) {
            if (dept.getDeptNo().equals(deptNo) && (deptId == null || dept.getId() != deptId)) {
                logger.log(Level.WARNING, "Department number {0} must be unique", deptNo);
                return false;
            }
        }
        return true;
    }

    public boolean validateDeptExists(String companyName, int deptId) {
        Department department = getDepartment(companyName, deptId);
        if (department == null) {
            logger.log(Level.WARNING, "Department ID {0} does not exist in company {1}",
                    new Object[] { deptId, companyName });
            return false;
        }
        return true;
    }
}