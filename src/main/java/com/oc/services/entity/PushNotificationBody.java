/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oc.services.entity;

import org.oc.db.entity.BrandInformation;
import org.oc.db.entity.BrandVariantdetail;
import org.oc.db.entity.GenTypedetail;
import org.oc.db.entity.ManInformation;

/**
 *
 * @author DELL PRECISION M6800
 */
public class PushNotificationBody {

    Long notificationID;
    String title;
    String message;
    String imageURL;
    GenTypedetail type;
    ManInformation manufacturer;
    BrandInformation brandInformation;
    BrandVariantdetail brandVariantdetail;
    Boolean isPromotionAvailable;

    public PushNotificationBody(String title, String message, String imageURL, GenTypedetail type, ManInformation manufacturer) {
        this.title = title;
        this.message = message;
        this.imageURL = imageURL;
        this.type = type;
        this.manufacturer = manufacturer;
    }

    public PushNotificationBody(String title, String message, String imageURL, GenTypedetail type, ManInformation manufacturer, BrandInformation brandInformation, BrandVariantdetail brandVariantdetail, boolean isPromotionAvailable) {
        this.title = title;
        this.message = message;
        this.imageURL = imageURL;
        this.type = type;
        this.manufacturer = manufacturer;
        this.brandInformation = brandInformation;
        this.brandVariantdetail = brandVariantdetail;
        this.isPromotionAvailable = isPromotionAvailable;
    }

    public PushNotificationBody(Long notificationID, String title, String message, String imageURL, GenTypedetail type, ManInformation manufacturer, BrandInformation brandInformation, BrandVariantdetail brandVariantdetail, Boolean isPromotionAvailable) {
        this.notificationID = notificationID;
        this.title = title;
        this.message = message;
        this.imageURL = imageURL;
        this.type = type;
        this.manufacturer = manufacturer;
        this.brandInformation = brandInformation;
        this.brandVariantdetail = brandVariantdetail;
        this.isPromotionAvailable = isPromotionAvailable;
    }
    
    

    
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public GenTypedetail getType() {
        return type;
    }

    public void setType(GenTypedetail type) {
        this.type = type;
    }

    public ManInformation getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(ManInformation manufacturer) {
        this.manufacturer = manufacturer;
    }

    public BrandInformation getBrandInformation() {
        return brandInformation;
    }

    public void setBrandInformation(BrandInformation brandInformation) {
        this.brandInformation = brandInformation;
    }

    public BrandVariantdetail getBrandVariantdetail() {
        return brandVariantdetail;
    }

    public void setBrandVariantdetail(BrandVariantdetail brandVariantdetail) {
        this.brandVariantdetail = brandVariantdetail;
    }

    public boolean isIsPromotionAvailable() {
        return isPromotionAvailable;
    }

    public void setIsPromotionAvailable(Boolean isPromotionAvailable) {
        this.isPromotionAvailable = isPromotionAvailable;
    }

    public Long getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(Long notificationID) {
        this.notificationID = notificationID;
    }
    

}
