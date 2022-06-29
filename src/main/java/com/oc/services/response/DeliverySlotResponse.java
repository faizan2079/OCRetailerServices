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
public class DeliverySlotResponse {
    
    private Integer status;
    private String statusText;
    private List<DeliverySlot> deliverySlotList;

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

    public List<DeliverySlot> getDeliverySlotList() {
        return deliverySlotList;
    }

    public void setDeliverySlotList(List<DeliverySlot> deliverySlotList) {
        this.deliverySlotList = deliverySlotList;
    }
    
    
    
    
    
}
