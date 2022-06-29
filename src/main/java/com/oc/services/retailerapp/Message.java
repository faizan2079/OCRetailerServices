/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oc.services.retailerapp;

import com.oc.controller.DateAdapter;
import com.oc.services.entity.AreaWiseSummary;
import com.oc.services.entity.BookerWiseSummary;
import com.oc.services.entity.RouteWiseSummary;
import com.oc.services.entity.TicketDateGroup;
import java.util.ArrayList;
import java.util.Date;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.oc.db.entity.BrandCategory;
import org.oc.db.entity.BrandGroup;
import org.oc.db.entity.BrandInformation;
import org.oc.db.entity.BrandProduct;
import org.oc.db.entity.BrandVariantdetail;
import org.oc.db.entity.Config;
import org.oc.db.entity.ConfigApp;
import org.oc.db.entity.DistOutletmapping;
import org.oc.db.entity.DistVan;
import org.oc.db.entity.GenImage;
import org.oc.db.entity.GenTypedetail;
import org.oc.db.entity.GeoLocation;
import org.oc.db.entity.ManInformation;
import org.oc.db.entity.OrderDetail;
import org.oc.db.entity.OutletComplain;
import org.oc.db.entity.OutletComplaindetail;
import org.oc.db.entity.OrderInformation;
import org.oc.db.entity.OutletInformation;
import org.oc.db.entity.UserCampaignactivity;
import org.oc.db.entity.UserInformation;
import org.oc.db.entity.OrderDistmapping;
import org.oc.db.entity.OrderTracking;
import org.oc.db.entity.UserNotificationactivity;
import org.oc.db.entity.GenTypemaster;
import org.oc.db.entity.OrderItemList;
import org.oc.db.entity.OrderItemListDetail;

@XmlRootElement(name = "message")
/**
 *
 * @author Clc
 */
public class Message {

    private Integer status;
    private String statusText;
    private Boolean hasMoreData;
    private String imageBaseURL;
    private String sessionId;
    private Integer startIndex;
    private Integer pageCount;
    private UserInformation user;
    private Date currentTime;
    private ArrayList<GeoLocation> cityList;
    private ArrayList<GeoLocation> townList;
    private ArrayList<GeoLocation> areaList;
    private Integer pinNumber;
    private String cellNumber;
    private String password;
    private OutletInformation outletInformation;
    private String confirmPassword;
    private ArrayList<Config> lstConfig;
    private Double longitude;
    private Double latitude;
    private Boolean skipNumberVerification;
    private Boolean cnicVerification;
    private Integer areaId;
    private ArrayList<ManInformation> lstManInformation;
    private ArrayList<BrandCategory> lstBrandCategory;
    private ArrayList<BrandInformation> lstBrandInformation;
    private ArrayList<BrandProduct> lstBrandProduct;
    private Integer categoryId;
    private Integer manufactureId;
    private Integer productId;
    private String searchText;
    private Boolean promotionAvailable;
    private Integer brandId;
    private ArrayList<BrandVariantdetail> lstBrandVariantDetail;
    private Integer cityId;
    private Integer townId;
    private Integer distributorId;
    private ArrayList<DistOutletmapping> lstDistOutletmappings;
    private Integer outletId;
    private Integer tradeCategoryId;
    private ArrayList<UserCampaignactivity> lstUserCampaignActivity;
    private ArrayList<UserInformation> lstUserInformation;
    private Integer ticketTypeId;
    private Integer ticketId;
//    private Integer orderId;
    private Long orderDistMappingId;
    private ArrayList<GenTypedetail> lstGenTypedetails;
    private ArrayList<OutletComplain> lstOutletComplains;
    private ArrayList<OutletComplaindetail> lstOutletComplaindetails;
    private ArrayList<OrderDetail> lstOrderDetail;
    OutletComplain outletComplain;
    private Integer orderStatus;
    private Integer sortField;
    private Integer sortOrder;
    private ArrayList<OrderInformation> lstOrderInformation;
    private Long orderId;
    private ArrayList<OrderDistmapping> lstOrderDistmapping;
    private String cnic;
    private Integer userRoleId;
    private ArrayList<DistVan> lstDistVans;
    private Date startDate;
    private Date endDate;
    private ArrayList<AreaWiseSummary> lstAreaWiseSummarys;
    private ArrayList<BookerWiseSummary> lstBookerWiseSummarys;
    private ArrayList<RouteWiseSummary> lstRouteWiseSummarys;
    private Integer bookerId;
    private Integer routeId;
    private Boolean isAssigned;
    private ArrayList<OrderDistmapping> lstOrderDistmappings;
    private ArrayList<OrderDetail> lstOrderDetails;
    private Integer vanId;
    private Integer salesmanId;
    private Date assignedDate;
    private OrderDetail skuInformation;
    private Integer brandVariantDetailId;
    private OutletComplaindetail outletComplaindetail;
    private String versionNumber;
    private Integer brandGroupId;
    private ArrayList<BrandGroup> lstBrandGroup;
    private ConfigApp configApp;
    private String token;
    private Boolean isRateModified;
    private ArrayList<UserNotificationactivity> lstUserNotificationactivitys;
    private String pushNotificationIds;
    private Long unSeenNotification;
    private Integer notificationStatusId;
    private Integer notificationTypeId;
    private String audioBaseURL;
    private ArrayList<TicketDateGroup> lstTicketDateGroups;
    private String audioFileName;
    private ArrayList<GenImage> lstGenImages;
    private Integer imageId;
    private String comment;    
    private Date preferredDeliveryDate;
    private String specialInstruction;  
    private Long distDeliverySlotId;
    private ArrayList<OrderTracking> orderTrackingList; 
    private ArrayList<GenTypemaster> lstComplaintHeads; 
    private int isAuthenticated;
    private Integer isLoggedIn;
    private String orderListName;
    private Long orderListId;
    private ArrayList<OrderItemList> orderItemList; 

   

    public Message() {
    }

    public Message(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getImageBaseURL() {
        return imageBaseURL;
    }

    @XmlElement
    public void setImageBaseURL(String imageBaseURL) {
        this.imageBaseURL = imageBaseURL;
    }

    public Boolean getHasMoreData() {
        return hasMoreData;
    }

    @XmlElement
    public void setHasMoreData(Boolean hasMoreData) {
        this.hasMoreData = hasMoreData;
    }

    public Integer getStatus() {
        return status;
    }

    @XmlElement
    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusText() {
        return statusText;
    }

    @XmlElement
    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public String getSessionId() {
        return sessionId;
    }

    @XmlElement
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public UserInformation getUser() {
        return user;
    }

    @XmlElement
    public void setUser(UserInformation user) {
        this.user = user;
    }

    public Integer getStartIndex() {
        return startIndex;
    }

    @XmlElement
    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }

    public Date getCurrentTime() {
        return currentTime;
    }

    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setCurrentTime(Date currentTime) {
        this.currentTime = currentTime;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    @XmlElement
    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public ArrayList<GeoLocation> getCityList() {
        return cityList;
    }

    @XmlElement
    public void setCityList(ArrayList<GeoLocation> cityList) {
        this.cityList = cityList;
    }

    public ArrayList<GeoLocation> getTownList() {
        return townList;
    }

    @XmlElement
    public void setTownList(ArrayList<GeoLocation> townList) {
        this.townList = townList;
    }

    public ArrayList<GeoLocation> getAreaList() {
        return areaList;
    }

    @XmlElement
    public void setAreaList(ArrayList<GeoLocation> areaList) {
        this.areaList = areaList;
    }

    public Integer getPinNumber() {
        return pinNumber;
    }

    @XmlElement
    public void setPinNumber(Integer pinNumber) {
        this.pinNumber = pinNumber;
    }

    public String getCellNumber() {
        return cellNumber;
    }

    @XmlElement
    public void setCellNumber(String cellNumber) {
        this.cellNumber = cellNumber;
    }

    public String getPassword() {
        return password;
    }

    @XmlElement
    public void setPassword(String password) {
        this.password = password;
    }

    public OutletInformation getOutletInformation() {
        return outletInformation;
    }

    @XmlElement
    public void setOutletInformation(OutletInformation outletInformation) {
        this.outletInformation = outletInformation;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    @XmlElement
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public ArrayList<Config> getLstConfig() {
        return lstConfig;
    }

    @XmlElement
    public void setLstConfig(ArrayList<Config> lstConfig) {
        this.lstConfig = lstConfig;
    }

    public Double getLongitude() {
        return longitude;
    }

    @XmlElement
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    @XmlElement
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Boolean getSkipNumberVerification() {
        return skipNumberVerification;
    }

    @XmlElement
    public void setSkipNumberVerification(Boolean skipNumberVerification) {
        this.skipNumberVerification = skipNumberVerification;
    }

    public Boolean getCnicVerification() {
        return cnicVerification;
    }

    @XmlElement
    public void setCnicVerification(Boolean cnicVerification) {
        this.cnicVerification = cnicVerification;
    }

    public Integer getAreaId() {
        return areaId;
    }

    @XmlElement
    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    public ArrayList<ManInformation> getLstManInformation() {
        return lstManInformation;
    }

    @XmlElement
    public void setLstManInformation(ArrayList<ManInformation> lstManInformation) {
        this.lstManInformation = lstManInformation;
    }

    public ArrayList<BrandCategory> getLstBrandCategory() {
        return lstBrandCategory;
    }

    @XmlElement
    public void setLstBrandCategory(ArrayList<BrandCategory> lstBrandCategory) {
        this.lstBrandCategory = lstBrandCategory;
    }

    public ArrayList<BrandInformation> getLstBrandInformation() {
        return lstBrandInformation;
    }

    @XmlElement
    public void setLstBrandInformation(ArrayList<BrandInformation> lstBrandInformation) {
        this.lstBrandInformation = lstBrandInformation;
    }

    public ArrayList<BrandProduct> getLstBrandProduct() {
        return lstBrandProduct;
    }

    @XmlElement
    public void setLstBrandProduct(ArrayList<BrandProduct> lstBrandProduct) {
        this.lstBrandProduct = lstBrandProduct;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    @XmlElement
    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getManufactureId() {
        return manufactureId;
    }

    @XmlElement
    public void setManufactureId(Integer manufactureId) {
        this.manufactureId = manufactureId;
    }

    public Integer getProductId() {
        return productId;
    }

    @XmlElement
    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getSearchText() {
        return searchText;
    }

    @XmlElement
    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public Boolean getPromotionAvailable() {
        return promotionAvailable;
    }

    @XmlElement
    public void setPromotionAvailable(Boolean promotionAvailable) {
        this.promotionAvailable = promotionAvailable;
    }

    public Integer getBrandId() {
        return brandId;
    }

    @XmlElement
    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    public ArrayList<BrandVariantdetail> getLstBrandVariantDetail() {
        return lstBrandVariantDetail;
    }

    @XmlElement
    public void setLstBrandVariantDetail(ArrayList<BrandVariantdetail> lstBrandVariantDetail) {
        this.lstBrandVariantDetail = lstBrandVariantDetail;
    }

    public Integer getCityId() {
        return cityId;
    }

    @XmlElement
    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public Integer getTownId() {
        return townId;
    }

    @XmlElement
    public void setTownId(Integer townId) {
        this.townId = townId;
    }

    public Integer getDistributorId() {
        return distributorId;
    }

    @XmlElement
    public void setDistributorId(Integer distributorId) {
        this.distributorId = distributorId;
    }

    public Integer getOutletId() {
        return outletId;
    }

    @XmlElement
    public void setOutletId(Integer outletId) {
        this.outletId = outletId;
    }

    public Integer getTradeCategoryId() {
        return tradeCategoryId;
    }

    @XmlElement
    public void setTradeCategoryId(Integer tradeCategoryId) {
        this.tradeCategoryId = tradeCategoryId;
    }

    public ArrayList<UserCampaignactivity> getLstUserCampaignActivity() {
        return lstUserCampaignActivity;
    }

    @XmlElement
    public void setLstUserCampaignActivity(ArrayList<UserCampaignactivity> lstUserCampaignActivity) {
        this.lstUserCampaignActivity = lstUserCampaignActivity;
    }

    public ArrayList<DistOutletmapping> getLstDistOutletmappings() {
        return lstDistOutletmappings;
    }

    @XmlElement
    public void setLstDistOutletmappings(ArrayList<DistOutletmapping> lstDistOutletmappings) {
        this.lstDistOutletmappings = lstDistOutletmappings;
    }

    public Integer getTicketTypeId() {
        return ticketTypeId;
    }

    @XmlElement
    public void setTicketTypeId(Integer ticketTypeId) {
        this.ticketTypeId = ticketTypeId;
    }

    public ArrayList<GenTypedetail> getLstGenTypedetails() {
        return lstGenTypedetails;
    }

    @XmlElement
    public void setLstGenTypedetails(ArrayList<GenTypedetail> lstGenTypedetails) {
        this.lstGenTypedetails = lstGenTypedetails;
    }

    public OutletComplain getOutletComplain() {
        return outletComplain;
    }

    @XmlElement
    public void setOutletComplain(OutletComplain outletComplain) {
        this.outletComplain = outletComplain;
    }

//    public Integer getOrderId() {
//        return orderId;
//    }
//
//    @XmlElement
//    public void setOrderId(Integer orderId) {
//        this.orderId = orderId;
//    }
    public Long getOrderDistMappingId() {
        return orderDistMappingId;
    }

    @XmlElement
    public void setOrderDistMappingId(Long orderDistMappingId) {
        this.orderDistMappingId = orderDistMappingId;
    }

    public ArrayList<OutletComplain> getLstOutletComplains() {
        return lstOutletComplains;
    }

    @XmlElement
    public void setLstOutletComplains(ArrayList<OutletComplain> lstOutletComplains) {
        this.lstOutletComplains = lstOutletComplains;
    }

    public Integer getTicketId() {
        return ticketId;
    }

    @XmlElement
    public void setTicketId(Integer ticketId) {
        this.ticketId = ticketId;
    }

    public ArrayList<OutletComplaindetail> getLstOutletComplaindetails() {
        return lstOutletComplaindetails;
    }

    @XmlElement
    public void setLstOutletComplaindetails(ArrayList<OutletComplaindetail> lstOutletComplaindetails) {
        this.lstOutletComplaindetails = lstOutletComplaindetails;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    @XmlElement
    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Integer getSortField() {
        return sortField;
    }

    @XmlElement
    public void setSortField(Integer sortField) {
        this.sortField = sortField;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    @XmlElement
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public ArrayList<OrderInformation> getLstOrderInformation() {
        return lstOrderInformation;
    }

    @XmlElement
    public void setLstOrderInformation(ArrayList<OrderInformation> lstOrderInformation) {
        this.lstOrderInformation = lstOrderInformation;
    }

    public Long getOrderId() {
        return orderId;
    }

    @XmlElement
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public ArrayList<OrderDistmapping> getLstOrderDistmapping() {
        return lstOrderDistmapping;
    }

    @XmlElement
    public void setLstOrderDistmapping(ArrayList<OrderDistmapping> lstOrderDistmapping) {
        this.lstOrderDistmapping = lstOrderDistmapping;
    }

    public String getCnic() {
        return cnic;
    }

    @XmlElement
    public void setCnic(String cnic) {
        this.cnic = cnic;
    }

    public ArrayList<OrderDetail> getLstOrderDetail() {
        return lstOrderDetail;
    }

    @XmlElement
    public void setLstOrderDetail(ArrayList<OrderDetail> lstOrderDetail) {
        this.lstOrderDetail = lstOrderDetail;
    }

    public Integer getUserRoleId() {
        return userRoleId;
    }

    @XmlElement
    public void setUserRoleId(Integer userRoleId) {
        this.userRoleId = userRoleId;
    }

    public ArrayList<UserInformation> getLstUserInformation() {
        return lstUserInformation;
    }

    @XmlElement
    public void setLstUserInformation(ArrayList<UserInformation> lstUserInformation) {
        this.lstUserInformation = lstUserInformation;
    }

    public ArrayList<DistVan> getLstDistVans() {
        return lstDistVans;
    }

    @XmlElement
    public void setLstDistVans(ArrayList<DistVan> lstDistVans) {
        this.lstDistVans = lstDistVans;
    }

    public Date getStartDate() {
        return startDate;
    }

    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public ArrayList<AreaWiseSummary> getLstAreaWiseSummarys() {
        return lstAreaWiseSummarys;
    }

    @XmlElement
    public void setLstAreaWiseSummarys(ArrayList<AreaWiseSummary> lstAreaWiseSummarys) {
        this.lstAreaWiseSummarys = lstAreaWiseSummarys;
    }

    public ArrayList<BookerWiseSummary> getLstBookerWiseSummarys() {
        return lstBookerWiseSummarys;
    }

    @XmlElement
    public void setLstBookerWiseSummarys(ArrayList<BookerWiseSummary> lstBookerWiseSummarys) {
        this.lstBookerWiseSummarys = lstBookerWiseSummarys;
    }

    public ArrayList<RouteWiseSummary> getLstRouteWiseSummarys() {
        return lstRouteWiseSummarys;
    }

    @XmlElement
    public void setLstRouteWiseSummarys(ArrayList<RouteWiseSummary> lstRouteWiseSummarys) {
        this.lstRouteWiseSummarys = lstRouteWiseSummarys;
    }

    public Integer getBookerId() {
        return bookerId;
    }

    @XmlElement
    public void setBookerId(Integer bookerId) {
        this.bookerId = bookerId;
    }

    public Integer getRouteId() {
        return routeId;
    }

    @XmlElement
    public void setRouteId(Integer routeId) {
        this.routeId = routeId;
    }

    public Boolean getIsAssigned() {
        return isAssigned;
    }

    @XmlElement
    public void setIsAssigned(Boolean isAssigned) {
        this.isAssigned = isAssigned;
    }

    public ArrayList<OrderDistmapping> getLstOrderDistmappings() {
        return lstOrderDistmappings;
    }

    @XmlElement
    public void setLstOrderDistmappings(ArrayList<OrderDistmapping> lstOrderDistmappings) {
        this.lstOrderDistmappings = lstOrderDistmappings;
    }

    public ArrayList<OrderDetail> getLstOrderDetails() {
        return lstOrderDetails;
    }

    @XmlElement
    public void setLstOrderDetails(ArrayList<OrderDetail> lstOrderDetails) {
        this.lstOrderDetails = lstOrderDetails;
    }

    public Integer getVanId() {
        return vanId;
    }

    @XmlElement
    public void setVanId(Integer vanId) {
        this.vanId = vanId;
    }

    public Integer getSalesmanId() {
        return salesmanId;
    }

    @XmlElement
    public void setSalesmanId(Integer salesmanId) {
        this.salesmanId = salesmanId;
    }

    public Date getAssignedDate() {
        return assignedDate;
    }

    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setAssignedDate(Date assignedDate) {
        this.assignedDate = assignedDate;
    }

    public OrderDetail getSkuInformation() {
        return skuInformation;
    }

    @XmlElement
    public void setSkuInformation(OrderDetail skuInformation) {
        this.skuInformation = skuInformation;
    }

    public Integer getBrandVariantDetailId() {
        return brandVariantDetailId;
    }

    @XmlElement
    public void setBrandVariantDetailId(Integer brandVariantDetailId) {
        this.brandVariantDetailId = brandVariantDetailId;
    }

    public OutletComplaindetail getOutletComplaindetail() {
        return outletComplaindetail;
    }

    @XmlElement
    public void setOutletComplaindetail(OutletComplaindetail outletComplaindetail) {
        this.outletComplaindetail = outletComplaindetail;
    }

    public String getVersionNumber() {
        return versionNumber;
    }

    @XmlElement
    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
    }

    public Integer getBrandGroupId() {
        return brandGroupId;
    }

    @XmlElement
    public void setBrandGroupId(Integer brandGroupId) {
        this.brandGroupId = brandGroupId;
    }

    public ArrayList<BrandGroup> getLstBrandGroup() {
        return lstBrandGroup;
    }

    @XmlElement
    public void setLstBrandGroup(ArrayList<BrandGroup> lstBrandGroup) {
        this.lstBrandGroup = lstBrandGroup;
    }

    public ConfigApp getConfigApp() {
        return configApp;
    }

    @XmlElement
    public void setConfigApp(ConfigApp configApp) {
        this.configApp = configApp;
    }

    public String getToken() {
        return token;
    }

    @XmlElement
    public void setToken(String token) {
        this.token = token;
    }

    public Boolean getIsRateModified() {
        return isRateModified;
    }

    @XmlElement
    public void setIsRateModified(Boolean isRateModified) {
        this.isRateModified = isRateModified;
    }

    public ArrayList<UserNotificationactivity> getLstUserNotificationactivitys() {
        return lstUserNotificationactivitys;
    }

    @XmlElement
    public void setLstUserNotificationactivitys(ArrayList<UserNotificationactivity> lstUserNotificationactivitys) {
        this.lstUserNotificationactivitys = lstUserNotificationactivitys;
    }

    public String getPushNotificationIds() {
        return pushNotificationIds;
    }

    @XmlElement
    public void setPushNotificationIds(String pushNotificationIds) {
        this.pushNotificationIds = pushNotificationIds;
    }

    public Long getUnSeenNotification() {
        return unSeenNotification;
    }

    @XmlElement
    public void setUnSeenNotification(Long unSeenNotification) {
        this.unSeenNotification = unSeenNotification;
    }

    public Integer getNotificationStatusId() {
        return notificationStatusId;
    }

    @XmlElement
    public void setNotificationStatusId(Integer notificationStatusId) {
        this.notificationStatusId = notificationStatusId;
    }

    public Integer getNotificationTypeId() {
        return notificationTypeId;
    }

    @XmlElement
    public void setNotificationTypeId(Integer notificationTypeId) {
        this.notificationTypeId = notificationTypeId;
    }

    public String getAudioBaseURL() {
        return audioBaseURL;
    }

    @XmlElement
    public void setAudioBaseURL(String audioBaseURL) {
        this.audioBaseURL = audioBaseURL;
    }

    public ArrayList<TicketDateGroup> getLstTicketDateGroups() {
        return lstTicketDateGroups;
    }

    @XmlElement
    public void setLstTicketDateGroups(ArrayList<TicketDateGroup> lstTicketDateGroups) {
        this.lstTicketDateGroups = lstTicketDateGroups;
    }

    public String getAudioFileName() {
        return audioFileName;
    }

    @XmlElement
    public void setAudioFileName(String audioFileName) {
        this.audioFileName = audioFileName;
    }

    public ArrayList<GenImage> getLstGenImages() {
        return lstGenImages;
    }

    @XmlElement
    public void setLstGenImages(ArrayList<GenImage> lstGenImages) {
        this.lstGenImages = lstGenImages;
    }

    public Integer getImageId() {
        return imageId;
    }

    @XmlElement
    public void setImageId(Integer imageIds) {
        this.imageId = imageIds;
    }
    
    public String getComment() {
        return comment;
    }
    
    @XmlElement
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public Date getPreferredDeliveryDate() {
        return preferredDeliveryDate;
    }
    
    @XmlElement
    public void setPreferredDeliveryDate(Date preferredDeliveryDate) {
        this.preferredDeliveryDate = preferredDeliveryDate;
    }

    public String getSpecialInstruction() {
        return specialInstruction;
    }

    @XmlElement
    public void setSpecialInstruction(String specialInstruction) {
        this.specialInstruction = specialInstruction;
    }
    
    public Long getDistDeliverySlotId() {
        return distDeliverySlotId;
    }
    
    @XmlElement
    public void setDistDeliverySlotId(Long distDeliverySlotId) {
        this.distDeliverySlotId = distDeliverySlotId;
    }
    
    public ArrayList<OrderTracking> getOrderTrackingList() {
        return orderTrackingList;
    }
    
    @XmlElement
    public void setOrderTrackingList(ArrayList<OrderTracking> orderTrackingList) {
        this.orderTrackingList = orderTrackingList;
    }
    
    public ArrayList<GenTypemaster> getLstComplaintHeads() {
        return lstComplaintHeads;
    }

    @XmlElement
    public void setLstComplaintHeads(ArrayList<GenTypemaster> lstComplaintHeads) {
        this.lstComplaintHeads = lstComplaintHeads;
    }

    public int getIsAuthenticated() {
        return isAuthenticated;
    }

    public void setIsAuthenticated(int isAuthenticated) {
        this.isAuthenticated = isAuthenticated;
    }
    
    public Integer getIsLoggedIn() {
        return isLoggedIn;
    }

    @XmlElement
    public void setIsLoggedIn(Integer isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }

    public String getOrderListName() {
        return orderListName;
    }

    @XmlElement
    public void setOrderListName(String orderListName) {
        this.orderListName = orderListName;
    }

    public Long getOrderListId() {
        return orderListId;
    }
	
    @XmlElement
    public void setOrderListId(Long orderListId) {
        this.orderListId = orderListId;
    }
    
     public ArrayList<OrderItemList> getOrderItemList() {
        return orderItemList;
    }
    @XmlElement 
    public void setOrderItemList(ArrayList<OrderItemList> orderItemList) {
        this.orderItemList = orderItemList;
    }

    public ArrayList<OrderItemListDetail> getOrderItemListDetails() {
        return orderItemListDetails;
    }
    @XmlElement
    public void setOrderItemListDetails(ArrayList<OrderItemListDetail> orderItemListDetails) {
        this.orderItemListDetails = orderItemListDetails;
    }
    private ArrayList<OrderItemListDetail> orderItemListDetails; 
   
}
