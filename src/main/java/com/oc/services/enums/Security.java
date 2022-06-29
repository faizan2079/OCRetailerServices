/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oc.services.enums;

/**
 *
 * @author Dev3
 */
public enum Security {

    RESTRICTED(1),
    BY_PASS(2),
    BOTH(3),
    ROLE_RESTRICTED(3);
    
    public Integer id;
    private Security(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
    
    public static Security getEnumElement(Integer activityTypeId){
        for(Security activityType: values()){
            if(activityType.getId().equals(activityTypeId)){
                return activityType;
            }
        }
        return null;
    }
   
}