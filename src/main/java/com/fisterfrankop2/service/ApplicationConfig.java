package com.fisterfrankop2.service;

import java.util.Set;

@jakarta.ws.rs.ApplicationPath("api")
public class ApplicationConfig extends jakarta.ws.rs.core.Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(com.fisterfrankop2.service.DepartmentResource.class);
        resources.add(com.fisterfrankop2.service.EmployeeResource.class);
        resources.add(com.fisterfrankop2.service.TimecardResource.class);
        resources.add(com.fisterfrankop2.service.CompanyResource.class);
    }
}