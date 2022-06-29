/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oc.services.retailerapp;

import java.util.Set;
import javax.ws.rs.core.Application;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

/**
 *
 * @author Clc
 */
@javax.ws.rs.ApplicationPath("retailerappnew")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        resources.add(MultiPartFeature.class);
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method. It is automatically
     * populated with all resources defined in the project. If required, comment
     * out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(com.oc.controller.GensonCustomResolver.class);
        resources.add(com.oc.services.retailerapp.AccountResource.class);
        resources.add(com.oc.services.retailerapp.AuthenticationFilter.class);
        resources.add(com.oc.services.retailerapp.GeneralResource.class);
        resources.add(com.oc.services.retailerapp.ManufacturerResource.class);
        resources.add(com.oc.services.retailerapp.NotificationResource.class);
        resources.add(com.oc.services.retailerapp.OrderListResource.class);
        resources.add(com.oc.services.retailerapp.OrderResource.class);
        resources.add(com.oc.services.retailerapp.ResponseFilter.class);
        resources.add(com.oc.services.retailerapp.TicketResource.class);
        resources.add(com.owlike.genson.ext.jaxrs.GensonJsonConverter.class);
        resources.add(org.glassfish.jersey.client.filter.HttpDigestAuthFilter.class);
        resources.add(org.glassfish.jersey.server.wadl.internal.WadlResource.class);
        
    }

}
