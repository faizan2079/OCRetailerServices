/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oc.services.entity;

import java.util.Date;
import javax.xml.bind.annotation.XmlElement;

public class ComplaintResponseDto {
 
    private Integer status;
    private String statusText;
    private Date preferredDeliveryDate;
   // private Integer manCount;
    private Long orderId;
    private Date orderDate;
    private Double orderTotalAmount;
   // private Integer manId;
      private Boolean isModified;
      private Boolean complaintRegFlag;

    
    public Integer getStatus() {
        return status;
    }

    @XmlElement
    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusText() {
        return statusText;
    }

    @XmlElement
    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }    
  
    
    public Long getOrderId() {
        return orderId;
    }


    @XmlElement
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    
    public Double getOrderTotalAmount() {
        return orderTotalAmount;
    }


    @XmlElement
    public void setOrderTotalAmount(Double orderTotalAmount) {
        this.orderTotalAmount = orderTotalAmount;
    }

    public Date getPreferredDeliveryDate() {
        return preferredDeliveryDate;
    }
    
    @XmlElement
    public void setPreferredDeliveryDate(Date preferredDeliveryDate) {
        this.preferredDeliveryDate = preferredDeliveryDate;
    }

    public Date getOrderDate() {
        return orderDate;
    }
    
    @XmlElement
    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Boolean getIsModified() {
        return isModified;
    }
@XmlElement
    public void setIsModified(Boolean isModified) {
        this.isModified = isModified;
    }

    public Boolean getComplaintRegFlag() {
        return complaintRegFlag;
    }
@XmlElement
    public void setComplaintRegFlag(Boolean complaintRegFlag) {
        this.complaintRegFlag = complaintRegFlag;
    }

   

}
