package com.fisterfrankop2.service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fisterfrankop2.business.DepartmentBusiness;
import com.fisterfrankop2.business.EmployeeBusiness;
import com.fisterfrankop2.business.TimecardBusiness;
import com.fisterfrankop2.response.ErrorResponse;
import com.fisterfrankop2.response.SuccessResponse;

import companydata.Employee;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("")
public class CompanyResource {

    private static final Logger logger = Logger.getLogger(CompanyResource.class.getName());

    private final DepartmentBusiness departmentBusiness = new DepartmentBusiness();
    private final EmployeeBusiness employeeBusiness = new EmployeeBusiness();
    private final TimecardBusiness timecardBusiness = new TimecardBusiness();

    @DELETE
    @Path("company")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAllData(@QueryParam("company") String companyName) {
        if (companyName == null || companyName.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Company name is required to delete all records."))
                    .build();
        }

        try {
            logger.log(Level.INFO, "Starting deletion of all records for company: {0}", companyName);

            // Step 1: Retrieve all employees for the company and delete their timecards
            List<Employee> employees = employeeBusiness.getAllEmployees(companyName);
            for (Employee employee : employees) {
                boolean timecardsDeleted = timecardBusiness.deleteAllTimecards(employee.getId());
                if (!timecardsDeleted) {
                    logger.log(Level.WARNING, "Failed to delete timecards for employee ID: {0}", employee.getId());
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity(new ErrorResponse(
                                    "Failed to delete timecards for employee ID: " + employee.getId()))
                            .build();
                }
            }
            logger.log(Level.INFO, "All timecards deleted for employees of company: {0}", companyName);

            // Step 2: Delete all employees for the company
            boolean employeesDeleted = employeeBusiness.deleteAllEmployees(companyName);
            if (!employeesDeleted) {
                logger.log(Level.WARNING, "Failed to delete employees for company: {0}", companyName);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(new ErrorResponse("Failed to delete employees for company: " + companyName))
                        .build();
            }
            logger.log(Level.INFO, "All employees deleted for company: {0}", companyName);

            // Step 3: Delete all departments for the company
            boolean departmentsDeleted = departmentBusiness.deleteAllDepartments(companyName);
            if (!departmentsDeleted) {
                logger.log(Level.WARNING, "Failed to delete departments for company: {0}", companyName);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(new ErrorResponse("Failed to delete departments for company: " + companyName))
                        .build();
            }
            logger.log(Level.INFO, "All departments deleted for company: {0}", companyName);

            // If all deletions were successful
            logger.log(Level.INFO, "All records deleted successfully for company: {0}", companyName);
            return Response.status(Response.Status.OK)
                    .entity(new SuccessResponse("All records deleted successfully for company: " + companyName))
                    .build();

        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("Error during deletion of all records for company: %s", companyName), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error during deletion: " + e.getMessage()))
                    .build();
        }
    }
}
