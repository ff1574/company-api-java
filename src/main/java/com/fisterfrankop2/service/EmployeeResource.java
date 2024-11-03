package com.fisterfrankop2.service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fisterfrankop2.business.DepartmentBusiness;
import com.fisterfrankop2.business.EmployeeBusiness;
import com.fisterfrankop2.response.ErrorResponse;
import com.fisterfrankop2.response.SuccessResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import companydata.Department;
import companydata.Employee;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Path("")
public class EmployeeResource {

    @Context
    @SuppressWarnings("unused")
    private UriInfo context;
    private EmployeeBusiness employeeBusiness = null;
    private DepartmentBusiness departmentBusiness = null;
    private static final Logger logger = Logger.getLogger(EmployeeResource.class.getName());
    private static final String HIRE_DATE = "hire_date";
    private static final String COMPANY = "company";

    public EmployeeResource() {
        this.employeeBusiness = new EmployeeBusiness();
        this.departmentBusiness = new DepartmentBusiness();
    }

    // Endpoint to retrieve a single employee by ID
    @GET
    @Path("/employee")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEmployee(@QueryParam("emp_id") int empId) {
        try {
            logger.log(Level.INFO, "Fetching employee with ID: {0}", empId);
            Employee emp = this.employeeBusiness.getEmployee(empId);
            if (emp != null) {
                return Response.status(Response.Status.OK).entity(emp).build();
            } else {
                logger.log(Level.WARNING, "Employee not found with ID: {0}", empId);
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Employee not found"))
                        .build();
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("Error retrieving employee with ID: %d", empId), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    // Endpoint to retrieve all employees by company name
    @GET
    @Path("/employees")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllEmployees(@QueryParam(COMPANY) String companyName) {
        try {
            logger.log(Level.INFO, "Fetching all employees for company: {0}", companyName);
            List<Employee> employees = this.employeeBusiness.getAllEmployees(companyName);
            return Response.status(Response.Status.OK).entity(employees).build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("Error retrieving employees for company: %s", companyName), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    // Endpoint to insert a new employee
    @POST
    @Path("/employee")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertEmployee(
            @FormParam(COMPANY) String companyName,
            @FormParam("emp_name") String empName,
            @FormParam("emp_no") String empNo,
            @FormParam("hire_date") Date hireDate,
            @FormParam("job") String job,
            @FormParam("salary") double salary,
            @FormParam("dept_id") int deptId,
            @FormParam("mng_id") Integer mngId) {
        try {
            logger.log(Level.INFO, "Inserting new employee for company: {0}", companyName);

            // Check if emp_no is unique amongst all employees in the database
            List<Employee> allEmployees = this.employeeBusiness.getAllEmployees(companyName);
            for (Employee emp : allEmployees) {
                if (emp.getEmpNo().equals(empNo)) {
                    logger.log(Level.WARNING, "Employee number {0} must be unique in the database", empNo);
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity(new ErrorResponse("Employee number must be unique in the database"))
                            .build();
                }
            }

            // Check if dept_id exists as a Department in the company
            Department department = this.departmentBusiness.getDepartment(companyName, deptId);
            if (department == null) {
                logger.log(Level.WARNING, "Department ID {0} does not exist for company: {1}",
                        new Object[] { deptId, companyName });
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Department ID does not exist in the company"))
                        .build();
            }

            // Check if mng_id is valid (either 0 for no manager or exists as an employee ID
            // in the company)
            if (mngId != 0 && this.employeeBusiness.getEmployee(mngId) == null) {
                logger.log(Level.WARNING, "Manager ID {0} does not exist for company: {1}",
                        new Object[] { mngId, companyName });
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Manager ID does not exist in the company"))
                        .build();
            }

            // Check if hire_date is valid and not a future date
            Date currentDate = new Date(System.currentTimeMillis());
            if (hireDate.after(currentDate)) {
                logger.log(Level.WARNING, "Hire date {0} cannot be in the future", hireDate);
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Hire date cannot be in the future"))
                        .build();
            }

            // Check if hire_date is a weekday (Monday-Friday)
            LocalDate parsedDate = hireDate.toLocalDate();
            if (parsedDate.getDayOfWeek().getValue() > 5) {
                logger.log(Level.WARNING, "Hire date {0} must be a weekday (Monday-Friday)", hireDate);
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Hire date must be a weekday (Monday-Friday)"))
                        .build();
            }

            // Proceed to insert the employee
            Employee newEmployee = new Employee(empName, empNo, hireDate, job, salary, deptId, mngId);
            newEmployee = this.employeeBusiness.insertEmployee(newEmployee);
            if (newEmployee.getId() > 0) {
                logger.log(Level.INFO, "Employee inserted successfully: {0}", newEmployee);
                return Response.status(Response.Status.CREATED)
                        .entity(new SuccessResponse("Employee successfully inserted.", newEmployee))
                        .build();
            } else {
                logger.log(Level.WARNING, "Failed to insert employee");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Employee could not be inserted"))
                        .build();
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error inserting employee", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    // Endpoint to update an employee
    @PUT
    @Path("/employee")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateEmployee(String employeeJson) {
        try {
            logger.log(Level.INFO, "Updating employee with data: {0}", employeeJson);

            // Parse JSON to handle hire_date separately
            JsonObject jsonObject = JsonParser.parseString(employeeJson).getAsJsonObject();
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

            // Check if emp_id is present and valid
            if (!jsonObject.has("emp_id")) {
                logger.log(Level.WARNING, "Employee ID (emp_id) is missing in the request");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Employee ID is required for update"))
                        .build();
            }
            int empId = jsonObject.get("emp_id").getAsInt();

            // Retrieve the existing employee record to verify emp_id is valid
            Employee existingEmployee = this.employeeBusiness.getEmployee(empId);
            if (existingEmployee == null) {
                logger.log(Level.WARNING, "Employee ID {0} is not a valid record ID in the database", empId);
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Employee ID is not valid"))
                        .build();
            }

            // Perform additional validations similar to insertEmployee
            if (jsonObject.has("emp_no") && jsonObject.has(COMPANY)) {
                String empNo = jsonObject.get("emp_no").getAsString();
                String companyName = jsonObject.get(COMPANY).getAsString();
                List<Employee> allEmployees = this.employeeBusiness.getAllEmployees(companyName);
                for (Employee emp : allEmployees) {
                    if (emp.getEmpNo().equals(empNo) && emp.getId() != empId) {
                        logger.log(Level.WARNING, "Employee number {0} must be unique", empNo);
                        return Response.status(Response.Status.BAD_REQUEST)
                                .entity(new ErrorResponse("Employee number must be unique"))
                                .build();
                    }
                }
            }

            if (jsonObject.has("dept_id") && jsonObject.has(COMPANY)) {
                int deptId = jsonObject.get("dept_id").getAsInt();
                String companyName = jsonObject.get(COMPANY).getAsString();
                Department department = this.departmentBusiness.getDepartment(companyName, deptId);
                if (department == null) {
                    logger.log(Level.WARNING, "Department ID {0} does not exist", deptId);
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity(new ErrorResponse("Department ID does not exist"))
                            .build();
                }
            }

            if (jsonObject.has("mng_id")) {
                int mngId = jsonObject.get("mng_id").getAsInt();
                if (mngId != 0 && this.employeeBusiness.getEmployee(mngId) == null) {
                    logger.log(Level.WARNING, "Manager ID {0} does not exist", mngId);
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity(new ErrorResponse("Manager ID does not exist"))
                            .build();
                }
            }

            if (jsonObject.has(HIRE_DATE)) {
                String hireDateStr = jsonObject.get(HIRE_DATE).getAsString();
                LocalDate parsedDate = LocalDate.parse(hireDateStr, DateTimeFormatter.ISO_LOCAL_DATE);

                // Check if hire_date is not in the future
                if (parsedDate.isAfter(LocalDate.now())) {
                    logger.log(Level.WARNING, "Hire date {0} cannot be in the future", hireDateStr);
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity(new ErrorResponse("Hire date cannot be in the future"))
                            .build();
                }

                // Check if hire_date falls on a weekday
                if (parsedDate.getDayOfWeek().getValue() > 5) {
                    logger.log(Level.WARNING, "Hire date {0} must be a weekday (Monday-Friday)", hireDateStr);
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity(new ErrorResponse("Hire date must be a weekday (Monday-Friday)"))
                            .build();
                }

                jsonObject.addProperty(HIRE_DATE, Date.valueOf(parsedDate).toString());
            }

            // Convert JSON to Employee object
            Employee employeeToUpdate = gson.fromJson(jsonObject, Employee.class);

            // Perform the update operation
            Employee updatedEmployee = this.employeeBusiness.updateEmployee(employeeToUpdate);
            if (updatedEmployee != null) {
                logger.log(Level.INFO, "Employee updated successfully: {0}", updatedEmployee);
                return Response.status(Response.Status.OK)
                        .entity(new SuccessResponse("Employee successfully updated.", updatedEmployee))
                        .build();
            } else {
                logger.log(Level.WARNING, "Failed to update employee");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Employee could not be updated"))
                        .build();
            }
        } catch (JsonSyntaxException e) {
            logger.log(Level.SEVERE, "JSON parsing error", e);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Invalid JSON format"))
                    .build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating employee", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    // Endpoint to delete an employee by ID
    @DELETE
    @Path("/employee")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteEmployee(@QueryParam("emp_id") int empId) {
        try {
            logger.log(Level.INFO, "Deleting employee with ID: {0}", empId);

            if (this.employeeBusiness.deleteEmployee(empId)) {
                logger.log(Level.INFO, "Employee deleted successfully: {0}", empId);
                return Response.status(Response.Status.OK)
                        .entity(new SuccessResponse("Employee deleted successfully"))
                        .build();
            } else {
                logger.log(Level.WARNING, "Failed to delete employee with ID: {0}", empId);
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Failed to delete employee"))
                        .build();
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("Error deleting employee with ID: %d", empId), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
}
