/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oc.services.entity;

/**
 *
 * @author dev2
 */
public enum ReportFormat {

    PDF(1, "PDF", ".pdf"),
    EXCEL(2, "Excel", ".xlsx"),
    DOCUMENT(3,"Document",".docx"),
    HTML(4,"HTML",".html");

    private int value;
    private String name;
    private String extension;


    private ReportFormat(int value, String name, String extension) {
        this.value = value;
        this.name = name;
        this.extension = extension;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public String getExtension() {
        return extension;
    }
    
    public static ReportFormat get(Integer id){
        ReportFormat[] reports = ReportFormat.values();
        for(ReportFormat report : reports){
            if(report.getValue() == id){
                return report;
            }
        }
        return null;
    }
}
