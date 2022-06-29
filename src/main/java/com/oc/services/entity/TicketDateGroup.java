/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oc.services.entity;

import java.util.ArrayList;
import org.oc.db.entity.OutletComplaindetail;

/**
 *
 * @author DELL PRECISION M6800
 */
public class TicketDateGroup {
    
    
    String date;
    ArrayList<OutletComplaindetail> lstComplaindetails;

    public TicketDateGroup(String date, ArrayList<OutletComplaindetail> lstComplaindetails) {
        this.date = date;
        this.lstComplaindetails = lstComplaindetails;
    }
    
    

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<OutletComplaindetail> getLstComplaindetails() {
        return lstComplaindetails;
    }

    public void setLstComplaindetails(ArrayList<OutletComplaindetail> lstComplaindetails) {
        this.lstComplaindetails = lstComplaindetails;
    }
    
    
    
}
