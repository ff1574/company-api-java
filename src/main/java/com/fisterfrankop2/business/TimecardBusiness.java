package com.fisterfrankop2.business;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import companydata.Employee;
import companydata.Timecard;

public class TimecardBusiness extends BusinessEntity {
    private static final Logger logger = Logger.getLogger(TimecardBusiness.class.getName());

    // Method to retrieve a single timecard by ID
    public Timecard getTimecard(int timecardId) {
        try {
            return this.dl.getTimecard(timecardId);
        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Error retrieving timecard", e);
            return null;
        }
    }

    // Method to retrieve all timecards for an employee
    public List<Timecard> getAllTimecards(int empId) {
        try {
            return this.dl.getAllTimecard(empId);
        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Error retrieving timecards", e);
            return Collections.emptyList();
        }
    }

    // Method to insert a new timecard
    public Timecard insertTimecard(Timecard timecard) {
        try {
            return this.dl.insertTimecard(timecard);
        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Error inserting timecard", e);
            return null;
        }
    }

    // Method to update an existing timecard
    public Timecard updateTimecard(Timecard timecard) {
        try {
            return this.dl.updateTimecard(timecard);
        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Error updating timecard", e);
            return null;
        }
    }

    // Method to delete a timecard by ID
    public boolean deleteTimecard(int timecardId) {
        try {
            return this.dl.deleteTimecard(timecardId) == 1;
        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Error deleting timecard", e);
            return false;
        }
    }

    // Method to delete all timecards
    public boolean deleteAllTimecards(int empId) {
        try {
            List<Timecard> timecards = this.dl.getAllTimecard(empId);
            for (Timecard timecard : timecards) {
                this.dl.deleteTimecard(timecard.getId());
                logger.log(Level.INFO, "Deleted timecard: {0}", timecard);
            }
            logger.info("All timecards deleted successfully.");
            return true;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deleting all timecards", e);
            return false;
        }
    }

    // Validation methods
    public boolean validateEmployeeExists(int empId, EmployeeBusiness employeeBusiness) {
        Employee employee = employeeBusiness.getEmployee(empId);
        if (employee == null) {
            logger.log(Level.WARNING, "Employee ID {0} does not exist", empId);
            return false;
        }
        return true;
    }

    public boolean validateStartTimeWithinLastWeek(Timestamp startTime) {
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        Timestamp oneWeekAgo = new Timestamp(currentTime.getTime() - 7 * 24 * 60 * 60 * 1000L); // 1 week ago
        if (startTime.after(currentTime) || startTime.before(oneWeekAgo)) {
            logger.log(Level.WARNING, "Start time {0} must be within the last week", startTime);
            return false;
        }
        return true;
    }

    public boolean validateEndTime(Timestamp startTime, Timestamp endTime) {
        long oneHourInMillis = 60 * 60 * 1000L;
        if (endTime.getTime() - startTime.getTime() < oneHourInMillis) {
            logger.log(Level.WARNING, "End time must be at least 1 hour after start time");
            return false;
        }
        if (!startTime.toLocalDateTime().toLocalDate().equals(endTime.toLocalDateTime().toLocalDate())) {
            logger.log(Level.WARNING, "End time must be on the same day as start time");
            return false;
        }
        return true;
    }

    public boolean validateWeekday(Timestamp timestamp) {
        int dayOfWeek = timestamp.toLocalDateTime().getDayOfWeek().getValue();
        if (dayOfWeek == 6 || dayOfWeek == 7) {
            logger.log(Level.WARNING, "Timecard entries must be on a weekday (Monday-Friday)");
            return false;
        }
        return true;
    }

    public boolean validateWorkingHours(Timestamp startTime, Timestamp endTime) {
        LocalTime startLocalTime = startTime.toLocalDateTime().toLocalTime();
        LocalTime endLocalTime = endTime.toLocalDateTime().toLocalTime();
        LocalTime earliestTime = LocalTime.of(6, 0);
        LocalTime latestTime = LocalTime.of(18, 0);
        if (startLocalTime.isBefore(earliestTime) || startLocalTime.isAfter(latestTime) ||
                endLocalTime.isBefore(earliestTime) || endLocalTime.isAfter(latestTime)) {
            logger.log(Level.WARNING, "Timecard entries must be between 06:00 and 18:00");
            return false;
        }
        return true;
    }

    public boolean validateUniqueStartTimePerDay(Timestamp startTime, int empId) {
        List<Timecard> employeeTimecards = getAllTimecards(empId);
        LocalDate newDate = startTime.toLocalDateTime().toLocalDate();
        for (Timecard timecard : employeeTimecards) {
            LocalDate existingDate = timecard.getStartTime().toLocalDateTime().toLocalDate();
            if (existingDate.equals(newDate)) {
                logger.log(Level.WARNING, "Employee ID {0} already has a timecard for the date {1}",
                        new Object[] { empId, existingDate });
                return false;
            }
        }
        return true;
    }

    public boolean validateTimecardIdExists(int timecardId) {
        if (getTimecard(timecardId) == null) {
            logger.log(Level.WARNING, "Timecard ID {0} does not exist", timecardId);
            return false;
        }
        return true;
    }
}
