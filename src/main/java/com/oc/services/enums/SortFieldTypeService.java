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
public enum SortFieldTypeService {
    DATE(1),
    STATUS(2),
    AMOUNT(3);
    
    private Integer value; 

    private SortFieldTypeService(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
