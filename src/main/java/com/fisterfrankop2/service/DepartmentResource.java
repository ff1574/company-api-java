package com.fisterfrankop2.service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fisterfrankop2.business.DepartmentBusiness;
import com.fisterfrankop2.response.ErrorResponse;
import com.fisterfrankop2.response.SuccessResponse;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import companydata.Department;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
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
public class DepartmentResource {

    @Context
    @SuppressWarnings("unused")
    private UriInfo context;
    private DepartmentBusiness department = null;
    private static final Logger logger = Logger.getLogger(DepartmentResource.class.getName());

    public DepartmentResource() {
        this.department = new DepartmentBusiness();
    }

    @GET
    @Path("/departments")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@QueryParam("company") String companyName) {
        try {
            logger.log(Level.INFO, "Fetching all departments for company: {0}", companyName);
            List<Department> departments = this.department.getAll(companyName);
            return Response.status(Response.Status.OK).entity(departments).build();
        } catch (NotFoundException e) {
            logger.log(Level.WARNING, "Company not found: {0}", companyName);
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResponse(e.getMessage())).build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("Error retrieving departments for company: %s", companyName), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/department")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@QueryParam("company") String companyName, @QueryParam("dept_id") int deptId) {
        try {
            logger.log(Level.INFO, "Fetching department with ID {0} for company {1}",
                    new Object[] { deptId, companyName });
            Department dept = this.department.getDepartment(companyName, deptId);
            if (dept != null) {
                return Response.status(Response.Status.OK).entity(dept).build();
            } else {
                logger.log(Level.WARNING, "Department not found: {0}", deptId);
                return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResponse("Department not found"))
                        .build();
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("Error retrieving department with ID %d", deptId), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("/department")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insert(
            @FormParam("company") String companyName,
            @FormParam("dept_name") String deptName,
            @FormParam("dept_no") String deptNo,
            @FormParam("location") String location) {
        try {
            logger.log(Level.INFO, "Inserting new department for company {0}", companyName);

            // Check if dept_no is unique across all departments
            List<Department> allDepartments = this.department.getAll(companyName);
            for (Department dept : allDepartments) {
                if (dept.getDeptNo().equals(deptNo)) {
                    logger.log(Level.WARNING, "Department number {0} must be unique", deptNo);
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity(new ErrorResponse("Department number must be unique")).build();
                }
            }

            Department newDept = new Department(companyName, deptName, deptNo, location);
            newDept = this.department.insertDepartment(newDept);
            if (newDept.getId() > 0) {
                logger.log(Level.INFO, "Department inserted successfully: {0}", newDept);
                return Response.status(Response.Status.CREATED)
                        .entity(new SuccessResponse("Department successfully inserted.", newDept)).build();
            } else {
                logger.log(Level.WARNING, "Failed to insert department");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Department could not be inserted")).build();
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error inserting department", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    @PUT
    @Path("/department")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(String departmentJson) {
        try {
            logger.log(Level.INFO, "Updating department with data: {0}", departmentJson);
            Gson gson = new Gson();
            Department departmentToUpdate = gson.fromJson(departmentJson, Department.class);

            // Check if dept_id exists
            Department existingDept = this.department.getDepartment(departmentToUpdate.getCompany(),
                    departmentToUpdate.getId());
            if (existingDept == null) {
                logger.log(Level.WARNING, "Department ID {0} does not exist", departmentToUpdate.getId());
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Department ID does not exist"))
                        .build();
            }

            // Check if dept_no is unique across all departments
            List<Department> allDepartments = this.department.getAll(departmentToUpdate.getCompany());
            for (Department dept : allDepartments) {
                if (dept.getDeptNo().equals(departmentToUpdate.getDeptNo())
                        && dept.getId() != departmentToUpdate.getId()) {
                    logger.log(Level.WARNING, "Department number {0} must be unique", departmentToUpdate.getDeptNo());
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity(new ErrorResponse("Department number must be unique")).build();
                }
            }

            Department updatedDept = this.department.updateDepartment(departmentToUpdate);
            if (updatedDept != null) {
                logger.log(Level.INFO, "Department updated successfully: {0}", updatedDept);
                return Response.status(Response.Status.OK)
                        .entity(new SuccessResponse("Department successfully updated.", updatedDept)).build();
            } else {
                logger.log(Level.WARNING, "Failed to update department");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Department could not be updated"))
                        .build();
            }
        } catch (JsonSyntaxException e) {
            logger.log(Level.SEVERE, "Error parsing JSON", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorResponse("Invalid JSON format"))
                    .build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating department", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    @DELETE
    @Path("/department")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@QueryParam("company") String companyName, @QueryParam("dept_id") int deptId) {
        try {
            logger.log(Level.INFO, "Deleting department with ID {0} from company {1}",
                    new Object[] { deptId, companyName });

            // Check if dept_id exists
            Department existingDept = this.department.getDepartment(companyName, deptId);
            if (existingDept == null) {
                logger.log(Level.WARNING, "Department ID {0} does not exist", deptId);
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Department ID does not exist"))
                        .build();
            }

            // Delete the department
            if (this.department.deleteDepartment(companyName, deptId)) {
                logger.log(Level.INFO, "Department deleted successfully: {0}", deptId);
                return Response.status(Response.Status.OK)
                        .entity(new SuccessResponse("Department deleted successfully")).build();
            } else {
                logger.log(Level.WARNING, "Failed to delete department");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Failed to delete department"))
                        .build();
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deleting department", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
}
