/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oc.services.enums;

/**
 *
 * @author DELL PRECISION M6800
 */
public enum OrderStatusTypeService {
    BOOKED(1),
    ACCEPTED(2),
    HISTORY(3);
    
    private Integer value; 

    private OrderStatusTypeService(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
    
    
    
}
