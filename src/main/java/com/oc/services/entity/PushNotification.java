/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oc.services.entity;

/**
 *
 * @author DELL PRECISION M6800
 */
public class PushNotification {
    
    String title;
    String body;
    String image;
    String click_action;

    public PushNotification(String title, String body, String image, String click_action) {
        this.title = title;
        this.body = body;
        this.image = image;
        this.click_action = click_action;
    }

    public PushNotification(){
        
    }
    
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getClick_action() {
        return click_action;
    }

    public void setClick_action(String click_action) {
        this.click_action = click_action;
    }
    
    
    
}
