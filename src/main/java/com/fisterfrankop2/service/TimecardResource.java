package com.fisterfrankop2.service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fisterfrankop2.business.EmployeeBusiness;
import com.fisterfrankop2.business.TimecardBusiness;
import com.fisterfrankop2.response.ErrorResponse;
import com.fisterfrankop2.response.SuccessResponse;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import companydata.Employee;
import companydata.Timecard;
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
public class TimecardResource {

    @Context
    @SuppressWarnings("unused")
    private UriInfo context;
    private TimecardBusiness timecardBusiness = null;
    private EmployeeBusiness employeeBusiness = new EmployeeBusiness();
    private static final Logger logger = Logger.getLogger(TimecardResource.class.getName());

    private static final String HOUR_ERROR_MSG = "End time must be at least 1 hour after start time";
    private static final String WEEKDAY_ERROR_MSG = "Timecard entries must be on a weekday (Monday-Friday)";
    private static final String SAME_DAY_ERROR_MSG = "End time must be on the same day as start time";
    private static final String WORKING_HOURS_ERROR_MSG = "Timecard entries must be between 06:00 and 18:00";

    public TimecardResource() {
        this.timecardBusiness = new TimecardBusiness();
        this.employeeBusiness = new EmployeeBusiness();
    }

    @GET
    @Path("/timecard")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTimecard(@QueryParam("timecard_id") int timecardId) {
        try {
            logger.log(Level.INFO, "Fetching timecard with ID: {0}", timecardId);
            Timecard timecard = this.timecardBusiness.getTimecard(timecardId);
            if (timecard != null) {
                return Response.status(Response.Status.OK)
                        .entity(new SuccessResponse("Timecard retrieved successfully.", timecard))
                        .build();
            } else {
                logger.log(Level.WARNING, "Timecard not found with ID: {0}", timecardId);
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Timecard not found"))
                        .build();
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("Error retrieving timecard with ID: %d", timecardId), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/timecards")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllTimecards(@QueryParam("emp_id") int empId) {
        try {
            logger.log(Level.INFO, "Fetching all timecards for employee ID: {0}", empId);
            List<Timecard> timecards = this.timecardBusiness.getAllTimecards(empId);
            return Response.status(Response.Status.OK)
                    .entity(new SuccessResponse("Timecards retrieved successfully.", timecards))
                    .build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("Error retrieving timecards for employee ID: %d", empId), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("/timecard")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertTimecard(
            @FormParam("start_time") String startTimeStr,
            @FormParam("end_time") String endTimeStr,
            @FormParam("emp_id") int empId) {
        try {
            logger.log(Level.INFO, "Inserting new timecard for employee ID: {0}", empId);

            // Parse start and end time strings to Timestamp
            Timestamp startTime = Timestamp.valueOf(startTimeStr);
            Timestamp endTime = Timestamp.valueOf(endTimeStr);

            // Validate emp_id: must exist as a valid Employee record
            Employee employee = this.employeeBusiness.getEmployee(empId);
            if (employee == null) {
                logger.log(Level.WARNING, "Employee ID {0} does not exist", empId);
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Employee ID is not valid"))
                        .build();
            }

            // Validate start_time is within the last week and not in the future
            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            Timestamp oneWeekAgo = new Timestamp(currentTime.getTime() - 7 * 24 * 60 * 60 * 1000L); // 1 week ago
            if (startTime.after(currentTime) || startTime.before(oneWeekAgo)) {
                logger.log(Level.WARNING, "Start time {0} must be within the last week", startTime);
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Start time must be within the last week and not in the future"))
                        .build();
            }

            // Validate end_time is at least 1 hour after start_time and on the same day
            long oneHourInMillis = 60 * 60 * 1000L;
            if (endTime.getTime() - startTime.getTime() < oneHourInMillis) {
                logger.log(Level.WARNING, HOUR_ERROR_MSG);
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse(HOUR_ERROR_MSG))
                        .build();
            }
            if (!startTime.toLocalDateTime().toLocalDate().equals(endTime.toLocalDateTime().toLocalDate())) {
                logger.log(Level.WARNING, SAME_DAY_ERROR_MSG);
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse(SAME_DAY_ERROR_MSG))
                        .build();
            }

            // Validate start_time and end_time fall on a weekday (Monday-Friday)
            int dayOfWeek = startTime.toLocalDateTime().getDayOfWeek().getValue();
            if (dayOfWeek == 6 || dayOfWeek == 7) {
                logger.log(Level.WARNING, WEEKDAY_ERROR_MSG);
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse(WEEKDAY_ERROR_MSG))
                        .build();
            }

            // Validate start_time and end_time are between 06:00:00 and 18:00:00
            LocalTime startLocalTime = startTime.toLocalDateTime().toLocalTime();
            LocalTime endLocalTime = endTime.toLocalDateTime().toLocalTime();
            LocalTime earliestTime = LocalTime.of(6, 0);
            LocalTime latestTime = LocalTime.of(18, 0);
            if (startLocalTime.isBefore(earliestTime) || startLocalTime.isAfter(latestTime) ||
                    endLocalTime.isBefore(earliestTime) || endLocalTime.isAfter(latestTime)) {
                logger.log(Level.WARNING, WORKING_HOURS_ERROR_MSG);
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse(WORKING_HOURS_ERROR_MSG))
                        .build();
            }

            // Validate start_time is unique per day for the employee
            List<Timecard> employeeTimecards = this.timecardBusiness.getAllTimecards(empId);
            for (Timecard timecard : employeeTimecards) {
                LocalDate existingDate = timecard.getStartTime().toLocalDateTime().toLocalDate();
                LocalDate newDate = startTime.toLocalDateTime().toLocalDate();
                if (existingDate.equals(newDate)) {
                    logger.log(Level.WARNING, "Employee ID {0} already has a timecard for the date {1}",
                            new Object[] { empId, existingDate });
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity(new ErrorResponse("Employee already has a timecard for the specified day"))
                            .build();
                }
            }

            // Proceed to insert the timecard
            Timecard newTimecard = new Timecard(0, startTime, endTime, empId);
            newTimecard = this.timecardBusiness.insertTimecard(newTimecard);
            if (newTimecard.getId() > 0) {
                logger.log(Level.INFO, "Timecard inserted successfully: {0}", newTimecard);
                return Response.status(Response.Status.CREATED)
                        .entity(new SuccessResponse("Timecard successfully inserted.", newTimecard))
                        .build();
            } else {
                logger.log(Level.WARNING, "Failed to insert timecard");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Timecard could not be inserted"))
                        .build();
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error inserting timecard", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    @PUT
    @Path("/timecard")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateTimecard(String timecardJson) {
        try {
            logger.log(Level.INFO, "Updating timecard with data: {0}", timecardJson);

            // Parse JSON to extract necessary fields
            JsonObject jsonObject = JsonParser.parseString(timecardJson).getAsJsonObject();

            // Extract timecard fields from JSON
            int timecardId = jsonObject.get("timecard_id").getAsInt();
            int empId = jsonObject.get("emp_id").getAsInt();
            String startTimeStr = jsonObject.get("start_time").getAsString();
            String endTimeStr = jsonObject.get("end_time").getAsString();

            // Convert start_time and end_time to Timestamp objects
            Timestamp startTime = Timestamp.valueOf(startTimeStr);
            Timestamp endTime = Timestamp.valueOf(endTimeStr);

            // Validate timecard_id exists in the database
            Timecard existingTimecard = this.timecardBusiness.getTimecard(timecardId);
            if (existingTimecard == null) {
                logger.log(Level.WARNING, "Timecard ID {0} does not exist", timecardId);
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Timecard ID is not valid"))
                        .build();
            }

            // Validate emp_id exists as a valid Employee record
            Employee employee = this.employeeBusiness.getEmployee(empId);
            if (employee == null) {
                logger.log(Level.WARNING, "Employee ID {0} does not exist", empId);
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Employee ID is not valid"))
                        .build();
            }

            // Validate start_time is within the last week and not in the future
            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            Timestamp oneWeekAgo = new Timestamp(currentTime.getTime() - 7 * 24 * 60 * 60 * 1000L); // 1 week ago
            if (startTime.after(currentTime) || startTime.before(oneWeekAgo)) {
                logger.log(Level.WARNING, "Start time {0} must be within the last week", startTime);
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Start time must be within the last week and not in the future"))
                        .build();
            }

            // Validate end_time is at least 1 hour after start_time and on the same day
            long oneHourInMillis = 60 * 60 * 1000L;
            if (endTime.getTime() - startTime.getTime() < oneHourInMillis) {
                logger.log(Level.WARNING, HOUR_ERROR_MSG);
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse(HOUR_ERROR_MSG))
                        .build();
            }
            if (!startTime.toLocalDateTime().toLocalDate().equals(endTime.toLocalDateTime().toLocalDate())) {
                logger.log(Level.WARNING, SAME_DAY_ERROR_MSG);
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse(SAME_DAY_ERROR_MSG))
                        .build();
            }

            // Validate start_time and end_time fall on a weekday (Monday-Friday)
            int dayOfWeek = startTime.toLocalDateTime().getDayOfWeek().getValue();
            if (dayOfWeek == 6 || dayOfWeek == 7) {
                logger.log(Level.WARNING, WEEKDAY_ERROR_MSG);
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse(WEEKDAY_ERROR_MSG))
                        .build();
            }

            // Validate start_time and end_time are between 06:00:00 and 18:00:00
            LocalTime startLocalTime = startTime.toLocalDateTime().toLocalTime();
            LocalTime endLocalTime = endTime.toLocalDateTime().toLocalTime();
            LocalTime earliestTime = LocalTime.of(6, 0);
            LocalTime latestTime = LocalTime.of(18, 0);
            if (startLocalTime.isBefore(earliestTime) || startLocalTime.isAfter(latestTime) ||
                    endLocalTime.isBefore(earliestTime) || endLocalTime.isAfter(latestTime)) {
                logger.log(Level.WARNING, WORKING_HOURS_ERROR_MSG);
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse(WORKING_HOURS_ERROR_MSG))
                        .build();
            }

            // Validate start_time is unique per day for the employee, excluding the current
            // timecard
            List<Timecard> employeeTimecards = this.timecardBusiness.getAllTimecards(empId);
            for (Timecard timecard : employeeTimecards) {
                LocalDate existingDate = timecard.getStartTime().toLocalDateTime().toLocalDate();
                LocalDate newDate = startTime.toLocalDateTime().toLocalDate();
                if (timecard.getId() != timecardId && existingDate.equals(newDate)) {
                    logger.log(Level.WARNING, "Employee ID {0} already has a timecard for the date {1}",
                            new Object[] { empId, existingDate });
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity(new ErrorResponse("Employee already has a timecard for the specified day"))
                            .build();
                }
            }

            // Proceed to update the timecard
            Timecard timecardToUpdate = new Timecard(timecardId, startTime, endTime, empId);
            Timecard updatedTimecard = this.timecardBusiness.updateTimecard(timecardToUpdate);
            if (updatedTimecard != null) {
                logger.log(Level.INFO, "Timecard updated successfully: {0}", updatedTimecard);
                return Response.status(Response.Status.OK)
                        .entity(new SuccessResponse("Timecard updated successfully.", updatedTimecard))
                        .build();
            } else {
                logger.log(Level.WARNING, "Failed to update timecard");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Timecard could not be updated"))
                        .build();
            }
        } catch (JsonSyntaxException e) {
            logger.log(Level.SEVERE, "JSON parsing error", e);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Invalid JSON format"))
                    .build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating timecard", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }

    @DELETE
    @Path("/timecard")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteTimecard(@QueryParam("timecard_id") int timecardId) {
        try {
            logger.log(Level.INFO, "Deleting timecard with ID: {0}", timecardId);

            if (this.timecardBusiness.deleteTimecard(timecardId)) {
                logger.log(Level.INFO, "Timecard deleted successfully: {0}", timecardId);
                return Response.status(Response.Status.OK)
                        .entity(new SuccessResponse("Timecard deleted successfully"))
                        .build();
            } else {
                logger.log(Level.WARNING, "Failed to delete timecard with ID: {0}", timecardId);
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Failed to delete timecard"))
                        .build();
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("Error deleting timecard with ID: %d", timecardId), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
}
