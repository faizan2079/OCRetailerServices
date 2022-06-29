/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oc.services.response;

import java.util.List;
import org.oc.db.entity.OrderDistmapping;
import org.oc.db.entity.OutletInformation;

/**
 *
 * @author Muzammil
 */
public class BookerOrder {
    
    private OrderDistmapping order;
    private OutletInformation outlet;
    private Double orderAmount;
    private Integer genTypeDetailId;
    private Long orderId;

    public OrderDistmapping getOrder() {
        return order;
    }

    public void setOrder(OrderDistmapping order) {
        this.order = order;
    }

    public OutletInformation getOutlet() {
        return outlet;
    }

    public void setOutlet(OutletInformation outlet) {
        this.outlet = outlet;
    }

    public Double getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(Double orderAmount) {
        this.orderAmount = orderAmount;
    }

    public Integer getGenTypeDetailId() {
        return genTypeDetailId;
    }

    public void setGenTypeDetailId(Integer genTypeDetailId) {
        this.genTypeDetailId = genTypeDetailId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

      
    
    
}
