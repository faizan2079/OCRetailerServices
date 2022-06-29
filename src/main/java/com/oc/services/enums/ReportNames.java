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
public enum ReportNames {

    PAY_RECIPT(1, "PayRecipt.jasper");

    private int value;
    private String name;

    private ReportNames(int value, String name) {
        this.value = value;
        this.name = name;
    }
    
     public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
    
      public static ReportNames get(Integer id){
        ReportNames[] reports = ReportNames.values();
        for(ReportNames report : reports){
            if(report.getValue() == id){
                return report;
            }
        }
        return null;
    }
}
