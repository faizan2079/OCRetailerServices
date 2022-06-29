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
public class BookerWiseSummary {

    private Integer bookerId;
    private String bookerFullName;
    private Integer totalOrders;
    private Integer totalAssignedOrders;
    private Integer totalUnassignedOrders;

    public BookerWiseSummary() {

    }

    public BookerWiseSummary(Integer bookerId, String bookerFullName, Integer totalUnassignedOrders, Integer totalAssignedOrders) {
        this.bookerId = bookerId;
        this.bookerFullName = bookerFullName;
        this.totalAssignedOrders = totalAssignedOrders;
        this.totalUnassignedOrders = totalUnassignedOrders;
        this.totalOrders = totalAssignedOrders + totalUnassignedOrders;

    }

    public Integer getBookerId() {
        return bookerId;
    }

    public void setBookerId(Integer bookerId) {
        this.bookerId = bookerId;
    }

    public String getBookerFullName() {
        return bookerFullName;
    }

    public void setBookerFullName(String bookerFullName) {
        this.bookerFullName = bookerFullName;
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
