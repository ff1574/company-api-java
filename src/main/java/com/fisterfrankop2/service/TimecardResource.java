package com.fisterfrankop2.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fisterfrankop2.business.TimecardBusiness;
import com.fisterfrankop2.response.ErrorResponse;
import com.fisterfrankop2.response.SuccessResponse;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

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
    private static final Logger logger = Logger.getLogger(TimecardResource.class.getName());

    public TimecardResource() {
        this.timecardBusiness = new TimecardBusiness();
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
            Timestamp startTime = Timestamp.valueOf(startTimeStr);
            Timestamp endTime = Timestamp.valueOf(endTimeStr);

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

            // Parse JSON to get start_time and end_time as strings
            JsonObject jsonObject = JsonParser.parseString(timecardJson).getAsJsonObject();

            int timecardId = jsonObject.get("timecard_id").getAsInt();
            int empId = jsonObject.get("emp_id").getAsInt();
            String startTimeStr = jsonObject.get("start_time").getAsString();
            String endTimeStr = jsonObject.get("end_time").getAsString();

            Timestamp startTime = Timestamp.valueOf(startTimeStr);
            Timestamp endTime = Timestamp.valueOf(endTimeStr);

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
