/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oc.services.response;

import java.util.List;

/**
 *
 * @author Muzammil
 */
public class BookerOrderList {
    
    private Integer status;
    private String statusText;
    List<BookerOrder> orderList;
    private Boolean hasMoreData;
    private Long cancelOrderCount;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public List<BookerOrder> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<BookerOrder> orderList) {
        this.orderList = orderList;
    }

    public Boolean getHasMoreData() {
        return hasMoreData;
    }

    public void setHasMoreData(Boolean hasMoreData) {
        this.hasMoreData = hasMoreData;
    }

    public Long getCancelOrderCount() {
        return cancelOrderCount;
    }

    public void setCancelOrderCount(Long cancelOrderCount) {
        this.cancelOrderCount = cancelOrderCount;
    }
    
    
}
