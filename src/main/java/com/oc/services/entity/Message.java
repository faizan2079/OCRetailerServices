/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oc.services.entity;

import java.util.HashMap;

/**
 *
 * @author DELL PRECISION M6800
 */
public class Message {
    
    String to;
    String priority="high";
    PushNotification notification;
    HashMap<String, PushNotificationBody> data;

    public Message(String to) {
        this.to = to;
    }
    

    public String getTo() {
        return to;
    }


    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public PushNotification getNotification() {
        return notification;
    }

    public void setNotification(PushNotification notification) {
        this.notification = notification;
    }

    public HashMap<String, PushNotificationBody> getData() {
        return data;
    }

    public void setData(HashMap<String, PushNotificationBody> data) {
        this.data = data;
    }
}
