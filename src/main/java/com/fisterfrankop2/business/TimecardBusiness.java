package com.fisterfrankop2.business;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import companydata.Timecard;

public class TimecardBusiness extends BusinessEntity {

    // Method to retrieve a single timecard by ID
    public Timecard getTimecard(int timecardId) {
        try {
            return this.dl.getTimecard(timecardId);
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(BusinessEntity.class.getName())
                    .log(java.util.logging.Level.SEVERE, "Error retrieving timecard", e);
            return null;
        }
    }

    // Method to retrieve all timecards for an employee
    public List<Timecard> getAllTimecards(int empId) {
        try {
            return this.dl.getAllTimecard(empId);
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(BusinessEntity.class.getName())
                    .log(java.util.logging.Level.SEVERE, "Error retrieving timecards", e);
            return Collections.emptyList();
        }
    }

    // Method to insert a new timecard
    public Timecard insertTimecard(Timecard timecard) {
        try {
            return this.dl.insertTimecard(timecard);
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(BusinessEntity.class.getName())
                    .log(java.util.logging.Level.SEVERE, "Error inserting timecard", e);
            return null;
        }
    }

    // Method to update an existing timecard
    public Timecard updateTimecard(Timecard timecard) {
        try {
            return this.dl.updateTimecard(timecard);
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(BusinessEntity.class.getName())
                    .log(java.util.logging.Level.SEVERE, "Error updating timecard", e);
            return null;
        }
    }

    // Method to delete a timecard by ID
    public boolean deleteTimecard(int timecardId) {
        try {
            return this.dl.deleteTimecard(timecardId) == 1;
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(BusinessEntity.class.getName())
                    .log(java.util.logging.Level.SEVERE, "Error deleting timecard", e);
            return false;
        }
    }

    // Method to delete all timecards
    public boolean deleteAllTimecards(int empId) {
        Logger logger = Logger.getLogger(TimecardBusiness.class.getName());

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
}
