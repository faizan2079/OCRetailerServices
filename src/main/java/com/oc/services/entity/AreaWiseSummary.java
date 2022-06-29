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
public class AreaWiseSummary {

   private Integer areaId;
   private String areaName;
   private Integer totalOrders;
   private Integer totalAssignedOrders;
   private Integer totalUnassignedOrders;

    public AreaWiseSummary() {

    }

    public AreaWiseSummary(Integer areaId, String areaName, Integer totalUnassignedOrders, Integer totalAssignedOrders) {
        this.areaId = areaId;
        this.areaName = areaName;
        this.totalAssignedOrders = totalAssignedOrders;
        this.totalUnassignedOrders = totalUnassignedOrders;
        this.totalOrders=totalAssignedOrders+totalUnassignedOrders;
    }

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
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
