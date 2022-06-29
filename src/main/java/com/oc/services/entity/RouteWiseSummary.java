/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oc.services.entity;

/**
 *
 * @author DELL PRECISION M6800
 */
public class RouteWiseSummary {

    private Integer routeId;
    private String routeName;
    private Integer totalOrders;
    private Integer totalAssignedOrders;
    private Integer totalUnassignedOrders;

    public RouteWiseSummary() {

    }

    public RouteWiseSummary(Integer routeId, String routeName, Integer totalUnassignedOrders, Integer totalAssignedOrders) {
        this.routeId = routeId;
        this.routeName = routeName;
        this.totalAssignedOrders = totalAssignedOrders;
        this.totalUnassignedOrders = totalUnassignedOrders;
        this.totalOrders = totalAssignedOrders + totalUnassignedOrders;

    }

    public Integer getRouteId() {
        return routeId;
    }

    public void setRouteId(Integer routeId) {
        this.routeId = routeId;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public Integer getTotalAssignedOrders() {
        return totalAssignedOrders;
    }

    public void setTotalAssignedOrders(Integer totalAssignedOrders) {
        this.totalAssignedOrders = totalAssignedOrders;
    }

    public Integer getTotalUnassignedOrders() {
        return totalUnassignedOrders;
    }

    public void setTotalUnassignedOrders(Integer totalUnassignedOrders) {
        this.totalUnassignedOrders = totalUnassignedOrders;
    }

    public Integer getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Integer totalOrders) {
        this.totalOrders = totalOrders;
    }

}
