/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oc.services.response;

import java.util.Date;
import org.oc.db.entity.DistInformation;
import org.oc.db.entity.GenTypedetail;
import org.oc.db.entity.GeoLocation;
import org.oc.db.entity.UserInformation;

/**
 *
 * @author Muzammil
 */
public class DeliverySlot {
    
     private Long id;
     private String deliveryDate;
     private Date slotStartTime;
     private Date slotEndTime;
     private Date slotCuttoffTime;
     private Integer orderQuantity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Date getSlotStartTime() {
        return slotStartTime;
    }

    public void setSlotStartTime(Date slotStartTime) {
        this.slotStartTime = slotStartTime;
    }

    public Date getSlotEndTime() {
        return slotEndTime;
    }

    public void setSlotEndTime(Date slotEndTime) {
        this.slotEndTime = slotEndTime;
    }

    public Date getSlotCuttoffTime() {
        return slotCuttoffTime;
    }

    public void setSlotCuttoffTime(Date slotCuttoffTime) {
        this.slotCuttoffTime = slotCuttoffTime;
    }

    public Integer getOrderQuantity() {
        return orderQuantity;
    }

    public void setOrderQuantity(Integer orderQuantity) {
        this.orderQuantity = orderQuantity;
    }
     
     
    
}
