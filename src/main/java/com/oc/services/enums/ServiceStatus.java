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
public enum ServiceStatus {

    SUCCESS(200, "Success"),
    UNREGISTERED_MOBILE_NUMBER(401, "Unregistered mobile number."),
    PIN_VERIFICATION_FAILED(402, "Pin and account could not be verified."),
    PASSWORD_VALIDATION_FAILED(403, "Password validation failed"),
    CELLNUMBER_VALIDATION_FAILED(404, "Cell Number validation failed"),
    CELLNUMBER_ALREADY_EXISTS(405, "Specified Cell Number already exists"),
    CNIC_VALIDATION_FAILED(406, "CNIC validation failed"),
    OUTLET_TRADE_CATEGORY_VALIDATION_FAILED(407, "Outlet Trade Category validation failed"),
    CNIC_IMAGES_VALIDATION_FAILED(408, "CNIC Images Required."),
    NO_DATA_AVAILABLE(409, "No Data Available."),
    ORDER_SHOULD_CONTAIN_ATLEAST_ONE_ITEM(410, "Order should contain atleast one item."),
    SKU_VALIDATION_FAILED(411, "SKU validation failed."),
    CNIC_ALREADY_VERIFIED(412, "CNIC already verified."),
    DATA_FORMAT_VALIDATION_FAILED(412,"Data provided in incorrect format"),
    DUPLICATE_CHECK_FAILED(413,"Duplicate Entry not allowed"),
    ORDER_STATUS_VALIDATION_FAILED(414,"Order not in required status"),
    FAILED(400, "Failed"),
    MINIMUM_ORDER_VALUE_VALIDATION_FAILED(415,"Minimum order value validation failed"),
    MAXIMUM_ORDER_VALUE_VALIDATION_FAILED(416,"Maximum order value validation failed"),
    APP_VERSION_NA(400,"APP Version Not Provided"),
    APP_VERSION_ERROR(400,"APP Version Not Compatible"),
    USERNAME_ALREADY_EXISTS(405, "Specified User Name already exists"),
    USERNAME_NOT_EXISTS(405, "User Name does not exists"),
    LOGINID_LENGTH_ISSUE(405, "UserLoginId must not be greater than 11 characters"),
    NOT_IN_SA(417, "Not in service area"),
    OTP_VERIFICATION_FAILED(402, "OTP could not be verified."),
    LIST_CREATION_FAILED(402, "Order List Creation Failed.");

    public Integer value;
    public String statusText;

    private ServiceStatus(Integer value, String statusText) {
        this.value = value;
        this.statusText = statusText;
    }

    public Integer getValue() {
        return value;
    }

    public String getStatusText() {
        return statusText;
    }
}
