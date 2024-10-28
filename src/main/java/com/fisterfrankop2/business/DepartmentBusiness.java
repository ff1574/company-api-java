package com.fisterfrankop2.business;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import companydata.Department;

public class DepartmentBusiness extends BusinessEntity {

    // Method to retrieve a single department by company name and department id
    public Department getDepartment(String companyName, int deptId) {
        if (companyName.isBlank()) {
            java.util.logging.Logger.getLogger(BusinessEntity.class.getName())
                    .log(java.util.logging.Level.SEVERE, "The company name is required.");
            return null;
        }

        try {
            return this.dl.getDepartment(companyName, deptId);
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(BusinessEntity.class.getName())
                    .log(java.util.logging.Level.SEVERE, "Error retrieving department", e);
            return null;
        }
    }

    // Method to retrieve all departments by company name
    public List<Department> getAll(String companyName) {
        if (companyName.isBlank()) {
            java.util.logging.Logger.getLogger(BusinessEntity.class.getName())
                    .log(java.util.logging.Level.SEVERE, "The company name is required.");
            return Collections.emptyList();
        }

        return this.dl.getAllDepartment(companyName);
    }

    // Method to insert a new department
    public Department insertDepartment(Department department) {
        try {
            return this.dl.insertDepartment(department);
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(BusinessEntity.class.getName())
                    .log(java.util.logging.Level.SEVERE, "Error inserting department", e);
            return null;
        }
    }

    // Method to update a department
    public Department updateDepartment(Department department) {
        try {
            return this.dl.updateDepartment(department);
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(BusinessEntity.class.getName())
                    .log(java.util.logging.Level.SEVERE, "Error updating department", e);
            return null;
        }
    }

    // Method to delete a department
    public boolean deleteDepartment(String companyName, int deptId) {
        try {
            return this.dl.deleteDepartment(companyName, deptId) == 1;
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(BusinessEntity.class.getName())
                    .log(java.util.logging.Level.SEVERE, "Error deleting department", e);
            return false;
        }
    }

    // Method to delete all departments
    public boolean deleteAllDepartments(String companyName) {
        Logger logger = Logger.getLogger(DepartmentBusiness.class.getName());

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
}