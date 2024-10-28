package com.fisterfrankop2;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import companydata.DataLayer;
import companydata.Department;
import companydata.Employee;
import companydata.Timecard;

public class DataLayerTester {

    public static void main(String args[]) {
        // You start using the data layer by using the DataLayer object
        DataLayer dl = null;
        try {
            // REMEMBER: USE YOUR RIT USERNAME IN PLACE OF ff1574
            dl = new DataLayer("ff1574");

            // DEPARTMENT
            // -------------------------------------------------------------------
            System.out.println("INFO: DELETE ALL DEPARTMENTS");
            List<Department> allDepartments = dl.getAllDepartment("ff1574");
            for (Department d : allDepartments) {
                int deletedDept = dl.deleteDepartment("ff1574", d.getId());
                if (deletedDept >= 1) {
                    System.out.println("Deleted department id: " + d.getId());
                } else {
                    System.out.println("Department not deleted id: " + d.getId());
                }
            }


            System.out.println("INFO: CREATE DEPARTMENT");
            Department dept = new Department("ff1574", "IT", "d50", "rochester");
            dept = dl.insertDepartment(dept);
            if (dept.getId() > 0) {
                System.out.println("inserted id: " + dept.getId());
            } else {
                System.out.println("Not inserted");
            }
            System.out.println("INFO: GET DEPARTMENTS");
            List<Department> departments = dl.getAllDepartment("ff1574");
            for (Department d : departments) {
                System.out.println("--------");
                System.out.println(d.getId());
                System.out.println(d.getCompany());
                System.out.println(d.getDeptName());
                System.out.println(d.getDeptNo());
                System.out.println(d.getLocation());
                System.out.println("--------\n\n");
            }

            System.out.println("INFO: UPDATE DEPARTMENT");
            Department department = dl.getDepartment("ff1574", 6);

            // Print the department details
            System.out.println("\n\nCurrent Department:");
            System.out.println(department.getId());
            System.out.println(department.getCompany());
            System.out.println(department.getDeptName());
            System.out.println(department.getDeptNo());
            System.out.println(department.getLocation());

            department.setDeptName("Computing");
            department = dl.updateDepartment(department);

            // Print the updated department details
            System.out.println("\n\nUpdated Department:");
            System.out.println(department.getId());
            System.out.println(department.getCompany());
            System.out.println(department.getDeptName());
            System.out.println(department.getDeptNo());
            System.out.println(department.getLocation());

            int deleted = dl.deleteDepartment("ff1574", 10);
            if (deleted >= 1) {
                System.out.println("\nDepartment deleted");
            } else {
                System.out.println("\nDepartment not deleted");
            }
            // EMPOLYEE
            // ---------------------------------------------------------------------

            System.out.println("INFO: CREATE EMPLOYEE");
            Employee employee = new Employee("French", "e2020", new java.sql.Date(new java.util.Date().getTime()),
                    "Developer", 80000.00, 1, 1);
            employee = dl.insertEmployee(employee);
            if (employee.getId() > 0) {
                System.out.println("inserted id: " + employee.getId());
            } else {
                System.out.println("Not inserted");
            }

            System.out.println("INFO: GET EMPLOYEES");
            List<Employee> employees = dl.getAllEmployee("ff1574");

            for (Employee emp : employees) {
                System.out.println(emp.getId());
                System.out.println(emp.getEmpName());
                System.out.println(emp.getEmpNo());
                System.out.println(emp.getHireDate());
                System.out.println(emp.getJob());
                System.out.println(emp.getSalary());
                System.out.println(emp.getDeptId());
                System.out.println(emp.getMngId());
                System.out.println("--------\n\n");
            }

            System.out.println("INFO: GET & UPDATE EMPLOYEE");
            employee = dl.getEmployee(13);

            // Print the employee details
            System.out.println(employee.getId());
            System.out.println(employee.getEmpName());
            System.out.println(employee.getEmpNo());
            System.out.println(employee.getHireDate());
            System.out.println(employee.getJob());
            System.out.println(employee.getSalary());
            System.out.println(employee.getDeptId());
            System.out.println(employee.getMngId());
            System.out.println("--------\n\n");

            employee.setSalary(60000.00);
            employee = dl.updateEmployee(employee);

            // Print the updated employee details
            System.out.println("\n\nUpdated Employee:");
            System.out.println(employee.getId());
            System.out.println(employee.getEmpName());
            System.out.println(employee.getEmpNo());
            System.out.println(employee.getHireDate());
            System.out.println(employee.getJob());
            System.out.println(employee.getSalary());
            System.out.println(employee.getDeptId());
            System.out.println(employee.getMngId());
            System.out.println("--------\n\n");

            System.out.println("INFO: DELETE EMPLOYEE");
            int deletedEmp = dl.deleteEmployee(15);
            if (deletedEmp >= 1) {
                System.out.println("\nEmployee deleted");
            } else {
                System.out.println("\nEmployee not deleted");
            }
            // TIMECARDS
            // --------------------------------------------------------------------
            Calendar cal = Calendar.getInstance();

            System.out.println("INFO: INSERT TIMECARD");
            Timestamp startTime = new Timestamp(new Date().getTime());
            cal.setTimeInMillis(startTime.getTime());
            cal.add(Calendar.HOUR, 5);
            Timecard tc = new Timecard(startTime,
                    new Timestamp(cal.getTime().getTime()), 2);
            tc = dl.insertTimecard(tc);
            if (tc.getId() > 0) {
                System.out.println("inserted id: " + tc.getId());
            } else {
                System.out.println("Not inserted");
            }

            System.out.println("INFO: INSERT TIMECARD 2");
            startTime = new Timestamp(new Date().getTime());
            cal.setTimeInMillis(startTime.getTime());
            cal.add(Calendar.HOUR, 6);
            Timecard tc2 = new Timecard(startTime,
                    new Timestamp(cal.getTime().getTime()), 2);
            tc2 = dl.insertTimecard(tc2);
            if (tc2.getId() > 0) {
                System.out.println("inserted id: " + tc2.getId());
            } else {
                System.out.println("Not inserted");
            }

            System.out.println("INFO: INSERT TIMECARD 3");
            startTime = new Timestamp(new Date().getTime());
            cal.setTimeInMillis(startTime.getTime());
            cal.add(Calendar.HOUR, 7);
            Timecard tc3 = new Timecard(startTime,
                    new Timestamp(cal.getTime().getTime()), 2);
            tc3 = dl.insertTimecard(tc3);
            if (tc3.getId() > 0) {
                System.out.println("inserted id: " + tc3.getId());
            } else {
                System.out.println("Not inserted");
            }

            System.out.println("INFO: INSERT TIMECARD 4");
            startTime = new Timestamp(new Date().getTime());
            cal.setTimeInMillis(startTime.getTime());
            cal.add(Calendar.HOUR, 8);
            Timecard tc4 = new Timecard(startTime,
                    new Timestamp(cal.getTime().getTime()), 2);
            tc4 = dl.insertTimecard(tc4);
            if (tc4.getId() > 0) {
                System.out.println("inserted id: " + tc4.getId());
            } else {
                System.out.println("Not inserted");
            }

            System.out.println("INFO: GET TIMECARDS");
            List<Timecard> timecards = dl.getAllTimecard(2);

            for (Timecard tcard : timecards) {
                System.out.println(tcard.getId());
                System.out.println(tcard.getStartTime());
                System.out.println(tcard.getEndTime());
                System.out.println(tcard.getEmpId());
                System.out.println("--------\n\n");
            }

            System.out.println("INFO: UPDATE A TIMECARD");
            Timecard timecard = dl.getTimecard(3);
            System.out.println("\n\nCurrent Timecard:");
            System.out.println(timecard.getId());
            System.out.println(timecard.getStartTime());
            System.out.println(timecard.getEndTime());
            System.out.println(timecard.getEmpId());
            System.out.println("--------\n\n");

            cal.setTimeInMillis(timecard.getStartTime().getTime());
            cal.add(Calendar.HOUR, 8);
            timecard.setEndTime(new Timestamp(cal.getTime().getTime()));
            timecard = dl.updateTimecard(timecard);

            System.out.println("\n\nUpdated Timecard:");
            System.out.println(timecard.getId());
            System.out.println(timecard.getStartTime());
            System.out.println(timecard.getEndTime());
            System.out.println(timecard.getEmpId());
            System.out.println("--------\n\n");

            // System.out.println("INFO: DELETE TIMECARDS");
            // int deletedTC = dl.deleteTimecard(1);
            // if (deletedTC >= 1) {
            // System.out.println("\nTimecard deleted");
            // } else {
            // System.out.println("\nTimecard not deleted");
            // }

            System.out.println("INFO: FETCH ALL TIMECARDS");
            List<Timecard> allTimecards = dl.getAllTimecard(1);
            for (Timecard tcard : allTimecards) {
                System.out.println(tcard.getId());
                System.out.println(tcard.getStartTime());
                System.out.println(tcard.getEndTime());
                System.out.println(tcard.getEmpId());
                System.out.println("--------\n\n");
            }

            // DELETE ALL FOR A COMPANY
            // -----------------------------------------------------
            int numRowsDeleted = dl.deleteCompany("ff1574");
            System.out.println("Number of rows deleted: " + numRowsDeleted);
        } catch (Exception e) {
            System.out.println("Problem with query: " + e.getMessage());
        } finally {
            dl.close();
        }

    }

}
