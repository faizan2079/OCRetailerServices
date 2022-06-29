/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oc.services.response;

import java.util.List;
import org.oc.db.entity.BrandVariantdetail;

/**
 *
 * @author Muzammil
 */
public class OrderList {
    
     private Long listId;
     private String ListName;
     private Integer numberOfSKU;     
     private List<BrandVariantdetail> SkuList;
     private String skuIdList;

    public Long getListId() {
        return listId;
    }

    public void setListId(Long listId) {
        this.listId = listId;
    }

    public String getListName() {
        return ListName;
    }

    public void setListName(String ListName) {
        this.ListName = ListName;
    }

    public Integer getNumberOfSKU() {
        return numberOfSKU;
    }

    public void setNumberOfSKU(Integer numberOfSKU) {
        this.numberOfSKU = numberOfSKU;
    }
     
    
    public List<BrandVariantdetail> getSkuList() {
        return SkuList;
    }

    public void setSkuList(List<BrandVariantdetail> SkuList) {
        this.SkuList = SkuList;
    }
     
    public String getSkuIdList() {
        return skuIdList;
    }

    public void setSkuIdList(String skuIdList) {
        this.skuIdList = skuIdList;
    }
     
    
}
