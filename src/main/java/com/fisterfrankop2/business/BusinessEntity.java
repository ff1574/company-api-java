package com.fisterfrankop2.business;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import companydata.DataLayer;

public class BusinessEntity {

    protected DataLayer dl = null;
    protected Gson gson = null;

    public BusinessEntity() {
        try {
            this.dl = new DataLayer(BusinessConfig.COMPANY_NAME);
            this.gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(BusinessEntity.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    e);
        }

    }

}