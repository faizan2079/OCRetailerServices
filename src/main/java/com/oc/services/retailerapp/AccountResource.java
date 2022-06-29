/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oc.services.retailerapp;

import com.google.gson.Gson;
import com.oc.services.entity.ServiceSecurity;
import com.oc.services.enums.Security;
import java.util.Date;
import java.util.Objects;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import org.oc.db.controller.GenTypedetailDAO;
import org.oc.db.controller.OutletInformationDAO;
import org.oc.db.controller.UserInformationDAO;
import org.oc.db.controller.UserVerificationAccountActivityDAO;
import org.oc.db.entity.OutletInformation;
import org.oc.db.entity.UserActivity;
import org.oc.db.entity.UserInformation;
import org.oc.db.entity.UserSmsactivity;
import org.oc.db.entity.UserVerifyaccountactivity;
import org.oc.db.entity.enums.AccountVerificationStatusType;
import org.oc.db.entity.enums.ActivityType;
import com.oc.services.enums.ServiceStatus;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.oc.db.controller.ConfigDAO;
import org.oc.db.controller.GenImageDAO;
import org.oc.db.controller.GeoLocationDAO;
import org.oc.db.controller.UserDeviceinformationDAO;
import org.oc.db.controller.UserVerificationLoginActivityDAO;
import org.oc.db.controller.Utilities;
import org.oc.db.entity.Config;
import org.oc.db.entity.GenImage;
import org.oc.db.entity.GeoLocation;
import org.oc.db.entity.OutletPackage;
import org.oc.db.entity.UserDeviceinformation;
import org.oc.db.entity.UserRegistrationactivity;
import org.oc.db.entity.UserRole;
import org.oc.db.entity.UserVerifyloginactivity;
import org.oc.db.entity.enums.Configuration;
import org.oc.db.entity.enums.EntityStatus;
import org.oc.db.entity.enums.MasterType;
import org.oc.db.entity.enums.OutletSMSNotification;
import org.oc.db.entity.enums.OutletStatusType;
import org.oc.db.entity.enums.OutletTradeCategory;
import org.oc.db.entity.enums.RegistrationStatusTypes;
import org.oc.db.entity.enums.SMSStatusType;
import org.oc.db.entity.enums.UserRoles;

/**
 *
 * @author Clc
 */
@Path("account")
public class AccountResource {

    @Context
    private ContainerRequestContext requestContext;
    private UserInformation loggedUser = null;

    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    @ServiceSecurity(security = Security.RESTRICTED)
    public Message login() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Message responseMessage = new Message();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();
        OutletInformation outlet = OutletInformationDAO.load(loggedUser.getRelationId(), EntityStatus.ACTIVE);
        ArrayList<GenImage> lstGenOutletImages = GenImageDAO.loadList(outlet.getOutletId(), null);
        ArrayList<GenImage> lstGenUserImages = GenImageDAO.loadList(null, loggedUser.getUserId());

        outlet.setLstGenImages(lstGenOutletImages);
        loggedUser.setLstGenImages(lstGenUserImages);

        responseMessage.setUser(loggedUser);
        UserInformationDAO.clearSets(responseMessage.getUser());
        responseMessage.setOutletInformation(OutletInformationDAO.clearSets(outlet));

        responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
        responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());
        return responseMessage;
    }

    @POST
    @Path("/generatepin")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ServiceSecurity(security = Security.BOTH)
    public Message generatePin(Message requestMessage) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        Message responseMessage = new Message();

        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();

        if (Objects.isNull(loggedUser) && (Objects.isNull(requestMessage) || Objects.isNull(requestMessage.getCellNumber()) || requestMessage.getCellNumber().trim().isEmpty())) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        }

        if (Objects.isNull(loggedUser)) {
            //In this case do not authenticate the user, instead the user should be loaded by specifying cell number and isactive = 1 and outlet role type id. 
            //The request will NOT receive password in RequestMessage
            loggedUser = UserInformationDAO.loadUser(requestMessage.getCellNumber(), MasterType.OUTLET_TYPES.getId());
        }

        if (Objects.isNull(loggedUser)) {
            responseMessage.setStatus(ServiceStatus.UNREGISTERED_MOBILE_NUMBER.getValue());
            responseMessage.setStatusText(ServiceStatus.UNREGISTERED_MOBILE_NUMBER.getStatusText());
            return responseMessage;
        }

        UserActivity activity = buildUserActivity();
        UserVerifyaccountactivity accVerify = buildUserVerifyAccountActivity(activity);
        UserSmsactivity smsActivity = buildUserSmsActivity(activity, accVerify);

        UserVerificationAccountActivityDAO.add(activity, accVerify, smsActivity);

        responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
        responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());
        return responseMessage;
    }

    @POST
    @Path("/verifyaccount")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ServiceSecurity(security = Security.BOTH)
    public Message verifyAccount(Message requestMessage) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        Message responseMessage = new Message();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();

        if (Objects.isNull(requestMessage) || Objects.isNull(requestMessage.getPinNumber())) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        }

        if (Objects.isNull(loggedUser) && (Objects.isNull(requestMessage) || Objects.isNull(requestMessage.getCellNumber()) || requestMessage.getCellNumber().trim().isEmpty())) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        }

        if (Objects.isNull(loggedUser)) {
            //In this case do not authenticate the user, instead the user should be loaded by specifying cell number and isactive = 1 and outlet role type id. 
            //The request will NOT receive password in RequestMessage
            loggedUser = UserInformationDAO.loadUser(requestMessage.getCellNumber(), MasterType.OUTLET_TYPES.getId());
        }

        if (Objects.isNull(loggedUser)) {
            responseMessage.setStatus(ServiceStatus.UNREGISTERED_MOBILE_NUMBER.getValue());
            responseMessage.setStatusText(ServiceStatus.UNREGISTERED_MOBILE_NUMBER.getStatusText());
            return responseMessage;
        }

        UserVerifyaccountactivity accVerify = UserVerificationAccountActivityDAO.load(loggedUser.getRelationId(), requestMessage.getPinNumber());

        if (Objects.isNull(accVerify)) {
            responseMessage.setStatus(ServiceStatus.PIN_VERIFICATION_FAILED.getValue());
            responseMessage.setStatusText(ServiceStatus.PIN_VERIFICATION_FAILED.statusText);
            return responseMessage;
        }

        accVerify.setEditDate(new Date());
        accVerify.setGenTypedetail(GenTypedetailDAO.build(AccountVerificationStatusType.VERIFIED.getId()));
        accVerify.setUserInformationByEditId(UserInformationDAO.getSystemDefaultUser());

        UserVerificationAccountActivityDAO.modify(accVerify);

        responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
        responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());
        return responseMessage;
    }

    @POST
    @Path("/createaccount")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({MediaType.APPLICATION_JSON})
    @ServiceSecurity(security = Security.BY_PASS)
    public Message createAccount( Message requestMessage) throws Exception {
        
        Message responseMessage = new Message();

        //        //<editor-fold defaultstate="collapsed" desc="validateParams">
        if (Objects.isNull(requestMessage)) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        }

        if (Objects.isNull(requestMessage.getOutletInformation()) || Objects.isNull(requestMessage.getUser())) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        }

        if (Objects.isNull(requestMessage.getOutletInformation().getGenTypedetailByTradeCategoryId())) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        }

        if (!Utilities.validateRequiredParameter(requestMessage.getOutletInformation().getOutletName()) || !Utilities.validateRequiredParameter(requestMessage.getOutletInformation().getLatitude()) || !Utilities.validateRequiredParameter(requestMessage.getOutletInformation().getLongitude()) || !Utilities.validateRequiredParameter(requestMessage.getOutletInformation().getStreetAddress()) || !Utilities.validateRequiredParameter(requestMessage.getOutletInformation().getGenTypedetailByTradeCategoryId().getTypeDetailId())
                || !Utilities.validateRequiredParameter(requestMessage.getUser().getUserFullName()) || !Utilities.validateRequiredParameter(requestMessage.getUser().getUserLoginId()) || !Utilities.validateRequiredParameter(requestMessage.getUser().getUserPassword()) || !Utilities.validateRequiredParameter(requestMessage.getConfirmPassword()) || !Utilities.validateRequiredParameter(requestMessage.getUser().getUserCellNumber1()) || !Utilities.validateRequiredParameter(requestMessage.getUser().getStreetAddress())
                || !Utilities.validateRequiredParameter(requestMessage.getLatitude()) || !Utilities.validateRequiredParameter(requestMessage.getLongitude()) || Objects.isNull(requestMessage.getSkipNumberVerification()) || Objects.isNull(requestMessage.getCnicVerification())) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        }
////</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="validateConfig">
        ArrayList<Config> lstConfig = ConfigDAO.loadList(new Integer[]{Configuration.SKIP_MOBILE_NUMBER_VERIFICATION.getId(), Configuration.SKIP_CNIC_VERIFICATION.getId()});
        boolean skipNumberVerificationFlag = false;
        boolean skipCnicVerificationFlag = false;
        for (Config config : lstConfig) {
            if (Objects.equals(config.getConfID(), Configuration.SKIP_MOBILE_NUMBER_VERIFICATION.getId())) {
                if (Integer.parseInt(config.getConfValue()) == 1 && requestMessage.getSkipNumberVerification()) {
                    skipNumberVerificationFlag = true;
                } else if (Integer.parseInt(config.getConfValue()) == 0 && requestMessage.getSkipNumberVerification()) {
                    throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Number Verification Required.").build());
                }
            } else {
                if (Objects.isNull(requestMessage.getUser().getUserNic()) || requestMessage.getUser().getUserNic().isEmpty()) {
                    if (Integer.parseInt(config.getConfValue()) == 1 && requestMessage.getCnicVerification()) {
                        skipCnicVerificationFlag = true;
                    } else if (Integer.parseInt(config.getConfValue()) == 0 && requestMessage.getCnicVerification()) {
                        throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("CNIC Verification Required.").build());
                    }
                }
            }
        }

        UserActivity activity = null;
        UserVerifyaccountactivity accVerify = null;
        UserSmsactivity smsActivity = null;

        if (!skipNumberVerificationFlag) {
            activity = buildUserActivity();
            accVerify = buildUserVerifyAccountActivity(activity);
            smsActivity = buildUserSmsActivity(activity, accVerify);

        }
        if (!skipCnicVerificationFlag) {
            //UserInformation CNIC, CNIC Back and Front Loginc

            if (Objects.isNull(requestMessage.getUser().getUserNic())) {
                throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
            }

            if (!validateCNIC(requestMessage.getUser().getUserNic().trim())) {
                responseMessage.setStatus(ServiceStatus.CNIC_VALIDATION_FAILED.getValue());
                responseMessage.setStatusText(ServiceStatus.CNIC_VALIDATION_FAILED.getStatusText());
                return responseMessage;
            }

        
        }

//        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="TradeCategoryValidation">
        if (!Objects.equals(OutletTradeCategory.DEFAULT.getId(), requestMessage.getOutletInformation().getGenTypedetailByTradeCategoryId().getTypeDetailId())
                && !Objects.equals(OutletTradeCategory.BAKERY.getId(), requestMessage.getOutletInformation().getGenTypedetailByTradeCategoryId().getTypeDetailId())
                && !Objects.equals(OutletTradeCategory.GENERAL_STORE.getId(), requestMessage.getOutletInformation().getGenTypedetailByTradeCategoryId().getTypeDetailId())
                && !Objects.equals(OutletTradeCategory.MEDICAL_STORE.getId(), requestMessage.getOutletInformation().getGenTypedetailByTradeCategoryId().getTypeDetailId())) {
            responseMessage.setStatus(ServiceStatus.OUTLET_TRADE_CATEGORY_VALIDATION_FAILED.getValue());
            responseMessage.setStatusText(ServiceStatus.OUTLET_TRADE_CATEGORY_VALIDATION_FAILED.getStatusText());
            return responseMessage;
        }
////</editor-fold>
        //        //<editor-fold defaultstate="collapsed" desc="validateCellNumber">
        if (!validateCellNumber(requestMessage.getUser().getUserCellNumber1().trim())) {
            responseMessage.setStatus(ServiceStatus.CELLNUMBER_VALIDATION_FAILED.getValue());
            responseMessage.setStatusText(ServiceStatus.CELLNUMBER_VALIDATION_FAILED.getStatusText());
            return responseMessage;
        }
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="validatePassword">
        if (!validatePassword(requestMessage.getUser().getUserPassword(), requestMessage.getConfirmPassword())) {
            responseMessage.setStatus(ServiceStatus.PASSWORD_VALIDATION_FAILED.getValue());
            responseMessage.setStatusText(ServiceStatus.PASSWORD_VALIDATION_FAILED.getStatusText());
            return responseMessage;
        }
//</editor-fold>
        //       <editor-fold defaultstate="collapsed" desc="duplicateAAccountCheck">
        if (UserInformationDAO.checkDuplicate(requestMessage.getUser().getUserCellNumber1().trim())) {
            responseMessage.setStatus(ServiceStatus.CELLNUMBER_ALREADY_EXISTS.getValue());
            responseMessage.setStatusText(ServiceStatus.CELLNUMBER_ALREADY_EXISTS.getStatusText());
            return responseMessage;
        }
//</editor-fold>
    

        UserInformation userInformation = buildUserInformation(requestMessage.getUser());
        OutletInformation outletInformation = buildOutletInformation(requestMessage.getOutletInformation(), userInformation, skipCnicVerificationFlag);
        UserRegistrationactivity userRegistrationActivity = buildUserRegistrationActivity(requestMessage);

        Integer outletId = UserInformationDAO.add(userInformation, outletInformation, activity, accVerify, smsActivity, userRegistrationActivity, false);

        OutletInformation outlet = OutletInformationDAO.loadOutletAndAccountHolderInformation(outletId);

        if (!Objects.isNull(outlet)) {
            OutletInformationDAO.clearSets(outlet);
            UserInformationDAO.clearSets(outlet.getUserInformationByAccountHolderId());
            responseMessage.setOutletInformation(outlet);
        } else {
            responseMessage.setStatus(ServiceStatus.FAILED.getValue());
            responseMessage.setStatusText(ServiceStatus.FAILED.getStatusText());
            return responseMessage;
        }

        responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
        responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());

        return responseMessage;
    }

    @POST
    @Path("/duplicateaccountcheck")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ServiceSecurity(security = Security.BY_PASS)
    public Message duplicateAccountCheck(Message requestMessage) {
        Message responseMessage = new Message();

        if (Objects.isNull(requestMessage) || Objects.isNull(requestMessage.getCellNumber()) || requestMessage.getCellNumber().trim().isEmpty()) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        }

        if (UserInformationDAO.checkDuplicate(requestMessage.getCellNumber())) {
            responseMessage.setStatus(ServiceStatus.CELLNUMBER_ALREADY_EXISTS.getValue());
            responseMessage.setStatusText(ServiceStatus.CELLNUMBER_ALREADY_EXISTS.getStatusText());
            return responseMessage;
        }

        responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
        responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());
        return responseMessage;
    }

    @POST
    @Path("/changepassword")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ServiceSecurity(security = Security.BOTH)
    public Message changePassword(Message requestMessage) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        Message responseMessage = new Message();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();

        if (Objects.isNull(requestMessage) || Objects.isNull(requestMessage.getCellNumber())
                || Objects.isNull(requestMessage.getPassword()) || Objects.isNull(requestMessage.getConfirmPassword())
                || requestMessage.getCellNumber().trim().isEmpty() || requestMessage.getPassword().trim().isEmpty()
                || requestMessage.getConfirmPassword().trim().isEmpty()) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        }

        if (Objects.isNull(loggedUser)) {
            //Specify Outlet Types as only outlet user roles are allowed in this case
            loggedUser = UserInformationDAO.loadUserByUserLoginId(requestMessage.getCellNumber(), MasterType.OUTLET_TYPES.getId());
        }

        if (Objects.isNull(loggedUser)) {
            responseMessage.setStatus(ServiceStatus.UNREGISTERED_MOBILE_NUMBER.getValue());
            responseMessage.setStatusText(ServiceStatus.UNREGISTERED_MOBILE_NUMBER.getStatusText());
            return responseMessage;
        }

        if (!validatePassword(requestMessage.getPassword(), requestMessage.getConfirmPassword())) {
            responseMessage.setStatus(ServiceStatus.PASSWORD_VALIDATION_FAILED.getValue());
            responseMessage.setStatusText(ServiceStatus.PASSWORD_VALIDATION_FAILED.getStatusText());
            return responseMessage;
        }

        //Password should not be saved in clear text is should be saved in MD5 please update the query, 
        //and also test login with new password to verify that it works
        OutletInformation outlet = OutletInformationDAO.load(loggedUser.getRelationId(), null, EntityStatus.ACTIVE);

        boolean changeOutletStatusFlag = false;
        if (!Objects.isNull(outlet)) {
            if (Objects.equals(outlet.getGenTypedetailByOutletStatusId().getTypeDetailId(), OutletStatusType.DEFAULT.getId())) {
                changeOutletStatusFlag = true;
            }
        }

        boolean successFlag = UserInformationDAO.updatePassword(requestMessage.getPassword(), loggedUser, changeOutletStatusFlag);

        if (successFlag) {
            responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
            responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());
        } else {
            responseMessage.setStatus(ServiceStatus.FAILED.getValue());
            responseMessage.setStatusText(ServiceStatus.FAILED.getStatusText());
        }

        return responseMessage;
    }

    @POST
    @Path("/changemobilenumber")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ServiceSecurity(security = Security.RESTRICTED)
    public Message changeMobileNumber(Message requestMessage) {
        Message responseMessage = new Message();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();

        if (Objects.isNull(requestMessage) || Objects.isNull(requestMessage.getCellNumber()) || requestMessage.getCellNumber().trim().isEmpty()) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        }

        if (!validateCellNumber(requestMessage.getCellNumber())) {
            responseMessage.setStatus(ServiceStatus.CELLNUMBER_VALIDATION_FAILED.getValue());
            responseMessage.setStatusText(ServiceStatus.CELLNUMBER_VALIDATION_FAILED.getStatusText());
            return responseMessage;
        }

        if (UserInformationDAO.checkDuplicate(requestMessage.getCellNumber())) {
            responseMessage.setStatus(ServiceStatus.CELLNUMBER_ALREADY_EXISTS.getValue());
            responseMessage.setStatusText(ServiceStatus.CELLNUMBER_ALREADY_EXISTS.getStatusText());
            return responseMessage;
        }

        UserActivity activity = buildUserActivity();
        UserVerifyaccountactivity accVerify = buildUserVerifyAccountActivity(activity);
        UserSmsactivity smsActivity = buildUserSmsActivity(activity, accVerify);
        smsActivity.setActivityNumber(requestMessage.getCellNumber());

        UserVerificationAccountActivityDAO.add(activity, accVerify, smsActivity, requestMessage.getCellNumber());

        responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
        responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());

        return responseMessage;
    }

    @POST
    @Path("/uploadcnic")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    @ServiceSecurity(security = Security.RESTRICTED)
    public Message uploadCnic(@FormDataParam("NICFront") InputStream inputStreamNICFront, @FormDataParam("NICFront") FormDataContentDisposition NICDataFront, @FormDataParam("NICBack") InputStream inputStreamNICBack, @FormDataParam("NICBack") FormDataContentDisposition NICDataBack, @FormDataParam("requestMessage") String jsonString) throws UnsupportedEncodingException, NoSuchAlgorithmException, IOException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, Exception {
        Message responseMessage = new Message();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();
        Message requestMessage = new Gson().fromJson(jsonString, Message.class);

        if (Objects.isNull(requestMessage)) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        }

        if (Objects.isNull(requestMessage.getCnic())) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        }

        if (!validateCNIC(requestMessage.getCnic().trim())) {
            responseMessage.setStatus(ServiceStatus.CNIC_VALIDATION_FAILED.getValue());
            responseMessage.setStatusText(ServiceStatus.CNIC_VALIDATION_FAILED.getStatusText());
            return responseMessage;
        }

        OutletInformation outlet = OutletInformationDAO.load(loggedUser.getRelationId());

        if (Objects.equals(outlet.getGenTypedetailByOutletStatusId().getTypeDetailId(), OutletStatusType.CNIC_VERIFIED_ACCOUNT.getId()) || Objects.equals(outlet.getGenTypedetailByOutletStatusId().getTypeDetailId(), OutletStatusType.VERIFIED_ACCOUNT.getId())) {
            responseMessage.setStatus(ServiceStatus.CNIC_ALREADY_VERIFIED.getValue());
            responseMessage.setStatusText(ServiceStatus.CNIC_ALREADY_VERIFIED.getStatusText());
            return responseMessage;
        }

        String fileLocation = ConfigDAO.load(Configuration.IMAGE_PHYSICAL_PATH.getId()).getConfValue();
        String tempLocation = ConfigDAO.load(Configuration.TEMP_DIRECTORY.getId()).getConfValue();
        String hashFileName = "";

        requestMessage.setUser(loggedUser);

        requestMessage.setOutletInformation(outlet);

        if (!Objects.isNull(inputStreamNICFront) && !Objects.isNull(inputStreamNICBack)) {
            hashFileName = generateHashNameForFiles(requestMessage);
            boolean successFlag = uploadImageToTemp(hashFileName, tempLocation, inputStreamNICFront, NICDataFront, inputStreamNICBack, NICDataBack);
            outlet.setOutletImagePath(hashFileName + "." + FilenameUtils.getExtension(NICDataBack.getFileName()));
            if (!successFlag) {
                throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Problem Occured While Uploading Image.").build());
            }
           
             boolean flagCopySuccess = copyFilesToMainFolder(outlet.getOutletId(), hashFileName, 
                                FilenameUtils.getExtension(NICDataFront.getFileName()), 
                                        FilenameUtils.getExtension(NICDataBack.getFileName()), "", 
                                                        tempLocation, fileLocation, false, false);

            if (!flagCopySuccess) {
                throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Problem Occured While Uploading Image.").build());
            } 
            
        }

       //else {
            UserInformationDAO.update(loggedUser, requestMessage.getCnic().trim(), outlet);
        //}

        OutletInformation outletInformation = OutletInformationDAO.loadOutletAndAccountHolderInformation(loggedUser.getRelationId());

        if (!Objects.isNull(outlet)) {
            OutletInformationDAO.clearSets(outletInformation);
            UserInformationDAO.clearSets(outletInformation.getUserInformationByAccountHolderId());
            responseMessage.setOutletInformation(outletInformation);
        } else {
            responseMessage.setStatus(ServiceStatus.FAILED.getValue());
            responseMessage.setStatusText(ServiceStatus.FAILED.getStatusText());
            return responseMessage;
        }

        responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
        responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());

        return responseMessage;
    }

    private boolean validatePassword(String password, String confirmPassword) {

        if (!password.equals(confirmPassword)) {
            return false;
        }

        if (password.length() < 4) {
            return false;
        }
//
//        if (!password.matches(".*[a-zA-Z]+.*") || !password.matches(".*[0-9]+.*")) {
//            return false;
//        }

        return true;
    }

    private boolean validateCellNumber(String cellNumber) {

        if (!cellNumber.matches("^(03)\\d*") || !(cellNumber.length() == 11)) {
            return false;
        }

        return true;
    }

    private boolean validateCNIC(String CNIC) {
        if (!CNIC.matches("[0-9]*$") || !(CNIC.length() == 13)) {
            return false;
        }
        return true;
    }

    private UserActivity buildUserActivity() {
        UserActivity activity = new UserActivity();
        activity.setActivityDate(new Date());
        activity.setGenTypedetailByActivityStatusId(GenTypedetailDAO.build(AccountVerificationStatusType.PENDING.getId()));
        activity.setGenTypedetailByActivityTypeId(GenTypedetailDAO.build(ActivityType.Account_Verification.getId()));
        activity.setInsertDate(new Date());
        activity.setIsActive(true);
        if (!Objects.isNull(loggedUser)) {
            activity.setOutletInformation(OutletInformationDAO.build(loggedUser.getRelationId()));
            activity.setUserInformationByInsertId(loggedUser);
        }
        return activity;
    }

    private UserVerifyaccountactivity buildUserVerifyAccountActivity(UserActivity activity) {
        UserVerifyaccountactivity accVerify = new UserVerifyaccountactivity();
        accVerify.setGenTypedetail(GenTypedetailDAO.build(AccountVerificationStatusType.PENDING.getId()));
        accVerify.setInsertDate(new Date());
        accVerify.setIsActive(true);
        accVerify.setPinNumber((int) (Math.random() * 9000) + 1000);
        accVerify.setUserActivity(activity);
        accVerify.setUserInformationByInsertId(UserInformationDAO.getSystemDefaultUser());
        if (!Objects.isNull(loggedUser)) {
            accVerify.setOutletInformation(OutletInformationDAO.build(loggedUser.getRelationId()));
            accVerify.setUserInformationByRequestById(loggedUser);
        }
        return accVerify;

    }

    private UserSmsactivity buildUserSmsActivity(UserActivity activity, UserVerifyaccountactivity accVerify) {
        UserSmsactivity smsActivity = new UserSmsactivity();
        smsActivity.setGenTypedetail(GenTypedetailDAO.build(SMSStatusType.PENDING.getId()));
        smsActivity.setInsertDate(new Date());
        smsActivity.setIsActive(true);
        smsActivity.setUserActivity(activity);
        smsActivity.setMessageBody(GenTypedetailDAO.load(OutletSMSNotification.PIN_GENERATED.getId()).getTypeInfo1().replace("{pin_number}", accVerify.getPinNumber().toString()));
        smsActivity.setProcessCount(0);
        smsActivity.setScheduledDate(new Date());
        smsActivity.setUserInformationByInsertId(UserInformationDAO.getSystemDefaultUser());
        if (!Objects.isNull(loggedUser)) {
            smsActivity.setActivityNumber(loggedUser.getUserCellNumber1());
            smsActivity.setOutletInformation(OutletInformationDAO.build(loggedUser.getRelationId()));
        }
        return smsActivity;
    }

    private OutletInformation buildOutletInformation(OutletInformation requestedOutletInformation, UserInformation userInformation, boolean skipCnicVerificationFlag) {
        OutletInformation outletInformation = new OutletInformation();

        outletInformation.setLandmark("");
        outletInformation.setOutletName(requestedOutletInformation.getOutletName().trim());
        outletInformation.setOutletNameShort("");
        outletInformation.setUserInformationByAccountHolderId(userInformation);
        outletInformation.setGeoLocation(userInformation.getGeoLocation());
        outletInformation.setLatitude(requestedOutletInformation.getLatitude());
        outletInformation.setLongitude(requestedOutletInformation.getLongitude());
        outletInformation.setStreetAddress(requestedOutletInformation.getStreetAddress().trim());
        outletInformation.setGenTypedetailByOictypeId(GenTypedetailDAO.build(Integer.parseInt(ConfigDAO.load(Configuration.OIC_TYPE.getId()).getConfValue())));
        outletInformation.setOutletPackage(new OutletPackage(Integer.parseInt(ConfigDAO.load(Configuration.OUTLET_PACKAGE.getId()).getConfValue())));
        outletInformation.setGenTypedetailByTradeCategoryId(requestedOutletInformation.getGenTypedetailByTradeCategoryId());
        outletInformation.setGenTypedetailByOrderTimePreferenceId(GenTypedetailDAO.build(Integer.parseInt(ConfigDAO.load(Configuration.ORDER_TIME_PREFERENCE.getId()).getConfValue())));
        outletInformation.setGenTypedetailByServiceTypeId(GenTypedetailDAO.build(Integer.parseInt(ConfigDAO.load(Configuration.SERVICE_TYPE.getId()).getConfValue())));
        outletInformation.setGenTypedetailByTransactionModeId(GenTypedetailDAO.build(Integer.parseInt(ConfigDAO.load(Configuration.TRANSACTION_MODE.getId()).getConfValue())));
        outletInformation.setUserInformationByInsertId(UserInformationDAO.getSystemDefaultUser());
        outletInformation.setInsertDate(new Date());
        outletInformation.setIsActive((byte) 1);
        if (!skipCnicVerificationFlag) {
            outletInformation.setGenTypedetailByOutletStatusId(GenTypedetailDAO.build(OutletStatusType.CNIC_VERIFIED_ACCOUNT.getId()));
        } else {
            outletInformation.setGenTypedetailByOutletStatusId(GenTypedetailDAO.build(OutletStatusType.UN_VERIFIED_ACCCOUNT.getId()));
        }
        outletInformation.setOutletImagePath(Objects.isNull(requestedOutletInformation.getOutletImagePath()) ? "" : requestedOutletInformation.getOutletImagePath());
        outletInformation.setSpecialCode(" ");
        return outletInformation;
    }

    private UserInformation buildUserInformation(UserInformation requestedUserInformation) {
        UserInformation userInformation = new UserInformation();

        userInformation = getExtractedName(requestedUserInformation.getUserFullName());
        GeoLocation area = getExtractedLocation(requestedUserInformation.getStreetAddress());

        userInformation.setUserLoginId(requestedUserInformation.getUserCellNumber1().trim());
        userInformation.setUserPassword(requestedUserInformation.getUserPassword());
        userInformation.setUserNic(requestedUserInformation.getUserNic().trim());
        userInformation.setUserRole(new UserRole(UserRoles.OUTLET_ACCOUNT_HOLDER.getId()));
        userInformation.setGenTypedetailByLanguagePreferenceId(GenTypedetailDAO.build(Integer.parseInt(ConfigDAO.load(Configuration.LANGUAGE_PREFERENCE.getId()).getConfValue())));
        userInformation.setUserCellNumber1(requestedUserInformation.getUserCellNumber1().trim());
        userInformation.setStreetAddress(requestedUserInformation.getStreetAddress().trim());
        userInformation.setGeoLocation(area);
        userInformation.setGenTypedetailByDesignationId(GenTypedetailDAO.build(Integer.parseInt(ConfigDAO.load(Configuration.DESIGNATION.getId()).getConfValue())));
        userInformation.setUserInformationByInsertId(UserInformationDAO.getSystemDefaultUser());
        userInformation.setInsertDate(new Date());
        userInformation.setIsActive(true);

        return userInformation;
    }

    private UserRegistrationactivity buildUserRegistrationActivity(Message requestMessage) {
        UserRegistrationactivity userRegistrationActivity = new UserRegistrationactivity();

        userRegistrationActivity.setScheduledDate(new Date());
        userRegistrationActivity.setGenTypedetail(GenTypedetailDAO.build(RegistrationStatusTypes.REGISTERED.getValue()));
        userRegistrationActivity.setPerformedDate(new Date());
        userRegistrationActivity.setReceivedLattitude(requestMessage.getLatitude());
        userRegistrationActivity.setReceivedLongitude(requestMessage.getLongitude());
        userRegistrationActivity.setUserInformationByInsertId(UserInformationDAO.getSystemDefaultUser());
        userRegistrationActivity.setInsertDate(new Date());
        userRegistrationActivity.setIsActive(true);
        userRegistrationActivity.setUserActivity(new UserActivity(Long.parseLong(ConfigDAO.load(Configuration.ACTIVITY.getId()).getConfValue())));

        return userRegistrationActivity;
    }

    private boolean copyFilesToMainFolder(Integer outletId, String NICName, String frontExtension, String backExtension, String tempLocationStoreImage, String tempLocation, String fileLocation, boolean isStoreImage, boolean skipCnicVerificationFlag) throws IOException {
        boolean flag = true;
        try {
            if (isStoreImage) {
                FileUtils.copyFile(new File(tempLocationStoreImage), new File(fileLocation + File.separator + "outlet" + File.separator + outletId));
                FileUtils.deleteQuietly(new File(tempLocationStoreImage));
            }
            if (!skipCnicVerificationFlag) {
                FileUtils.copyFile(new File(tempLocation + File.separator + "back" + File.separator + NICName + "." + backExtension), new File(fileLocation + "outlet" + File.separator + "back" + File.separator + NICName + "." + backExtension));
                FileUtils.deleteQuietly(new File(tempLocation + File.separator + "back" + File.separator + NICName + "." + backExtension));
                FileUtils.copyFile(new File(tempLocation + File.separator + "front" + File.separator + NICName + "." + frontExtension), new File(fileLocation + "outlet" + File.separator + "front" + File.separator + NICName + "." + backExtension));
                FileUtils.deleteQuietly(new File(tempLocation + File.separator + "front" + File.separator + NICName + "." + frontExtension));
            }
        } catch (IOException ex) {
            flag = false;
        }

        return flag;

    }

    private UserInformation getExtractedName(String fullName) {
        UserInformation userInformation = new UserInformation();

        String[] nameArray = fullName.split(" ");
        String firstName = "";
        String middleName = "";
        String lastName = "";

        switch (nameArray.length) {
            case 1:
                firstName = nameArray[0].trim();
                break;
            case 2:
                firstName = nameArray[0].trim();
                lastName = nameArray[1].trim();
                break;
            default:
                for (int i = nameArray.length - 1; i >= 0; i--) {
                    if (i == nameArray.length - 1) {
                        lastName = nameArray[i].trim();
                    } else if (i == nameArray.length - 2) {
                        middleName = nameArray[i].trim();
                    } else {
                        firstName = nameArray[i].trim() + " " + firstName;
                    }
                }

                break;
        }

        userInformation.setUserFirstName(firstName.trim());
        userInformation.setUserMiddleName(middleName);
        userInformation.setUserLastName(lastName);

        return userInformation;
    }

    private GeoLocation getExtractedLocation(String streetAddress) {

        String[] locationNames = streetAddress.split(",");
        for (int i = 0; i < locationNames.length - 1; i++) {
            locationNames[i] = locationNames[i].trim();
        }
        String[] defaultLocation = {"Default"};
        GeoLocation city = GeoLocationDAO.load("1", locationNames, null);
        if (Objects.isNull(city)) {
            city = GeoLocationDAO.load("1", defaultLocation, null);
        }
        GeoLocation town = null;
        GeoLocation area = null;
        Integer areaId = null;
        if (!Objects.isNull(city)) {
            town = GeoLocationDAO.load("2", locationNames, city.getLocationId().toString());
            if (Objects.isNull(town)) {
                town = GeoLocationDAO.load("2", defaultLocation, city.getLocationId().toString());
            }
            if (!Objects.isNull(town)) {
                area = GeoLocationDAO.load("3", locationNames, town.getLocationId().toString());
                if (Objects.isNull(area)) {
                    area = GeoLocationDAO.load("3", defaultLocation, town.getLocationId().toString());
                }
                if (Objects.isNull(area)) {
                    areaId = GeoLocationDAO.addDefault(city, town, area);
                    area = new GeoLocation(areaId);
                }
            } else {
                areaId = GeoLocationDAO.addDefault(city, town, area);
                area = new GeoLocation(areaId);
            }
        } else {
            areaId = GeoLocationDAO.addDefault(city, town, area);
            area = new GeoLocation(areaId);
        }

        return area;
    }

    private boolean uploadImageToTemp(String hashFileName, String tempLocation, InputStream inputStreamNICFront, FormDataContentDisposition NICDataFront, InputStream inputStreamNICBack, FormDataContentDisposition NICDataBack) throws NoSuchAlgorithmException, UnsupportedEncodingException, IOException {
        File file = new File(tempLocation);
        if (!file.exists()) {
            file.mkdir();
        }
        File fileFront = new File(tempLocation + File.separator + "front" + File.separator);
        File fileBack = new File(tempLocation + File.separator + "back" + File.separator);
        if (!fileFront.exists()) {
            fileFront.mkdir();
        }
        if (!fileBack.exists()) {
            fileBack.mkdir();
        }

        String frontFileLocation = fileFront + File.separator + hashFileName + "." + FilenameUtils.getExtension(NICDataFront.getFileName());
        String backFileLocation = fileBack + File.separator + hashFileName + "." + FilenameUtils.getExtension(NICDataBack.getFileName());
        FileOutputStream outFront = null;
        FileOutputStream outBack = null;
        boolean successFlag = false;
        try {
            outFront = new FileOutputStream(new File(frontFileLocation));
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = inputStreamNICFront.read(bytes)) != -1) {
                outFront.write(bytes, 0, read);
            }

            outBack = new FileOutputStream(new File(backFileLocation));
            read = 0;
            byte[] bytes1 = new byte[1024];
            while ((read = inputStreamNICBack.read(bytes1)) != -1) {
                outBack.write(bytes1, 0, read);
            }

            successFlag = true;
        } catch (IOException e) {
            successFlag = false;
        } finally {
            if (!Objects.isNull(outFront)) {
                outFront.flush();
                outFront.close();
            }
            if (!Objects.isNull(outBack)) {
                outBack.flush();
                outBack.close();
            }
        }

        return successFlag;
    }

    private String generateHashNameForFiles(Message requestMessage) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        boolean duplicateHashFileName = true;
        int i = 0;
        String hashFileName = UserInformationDAO.getHash(requestMessage.getUser().getUserCellNumber1() + requestMessage.getUser().getUserFullName() + requestMessage.getOutletInformation().getOutletName() + new Date() + System.currentTimeMillis());
        while (duplicateHashFileName) {
            i++;
            duplicateHashFileName = UserInformationDAO.checkDuplicateImageName(hashFileName);
            if (duplicateHashFileName) {
                hashFileName = UserInformationDAO.getHash(requestMessage.getUser().getUserCellNumber1() + requestMessage.getUser().getUserFullName() + requestMessage.getOutletInformation().getOutletName() + new Date() + System.currentTimeMillis() + i);
            }
        }
        return hashFileName;
    }

    private boolean uploadStoreImageToTemp(String hashFileName, String tempLocation, String tempLocationStoreImage, InputStream inputStreamStoreImage, FormDataContentDisposition storeImageData) throws IOException {
        File file = new File(tempLocation);
        FileOutputStream out = null;
        boolean successFlag = false;
        if (!file.exists()) {
            file.mkdir();
        }
        try {
            out = new FileOutputStream(new File(tempLocationStoreImage));
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = inputStreamStoreImage.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }

            successFlag = true;
        } catch (IOException e) {
            successFlag = false;
        } finally {
            if (!Objects.isNull(out)) {
                out.flush();
                out.close();
            }
        }

        return successFlag;
    }

    private boolean copyStoreImageToMainFolder(Integer outletId, String tempLocationStoreImage, String fileLocation, boolean isStoreImage) throws IOException {
        boolean flag = true;
        try {

            FileUtils.copyFile(new File(tempLocationStoreImage), new File(fileLocation + File.separator + "outlet" + File.separator + outletId));
            FileUtils.deleteQuietly(new File(tempLocationStoreImage));

        } catch (IOException ex) {
            flag = false;
        }

        return flag;

    }

    private boolean copyCNICImagesToMainFolder(String NICName, String frontExtension, String backExtension, String tempLocation, String fileLocation, boolean skipCnicVerificationFlag) throws IOException {
        boolean flag = true;
        try {
            if (!skipCnicVerificationFlag) {
                FileUtils.copyFile(new File(tempLocation + File.separator + "back" + File.separator + NICName + "." + backExtension), new File(fileLocation + "outlet" + File.separator + "back" + File.separator + NICName + "." + backExtension));
                FileUtils.deleteQuietly(new File(tempLocation + File.separator + "back" + File.separator + NICName + "." + backExtension));
                FileUtils.copyFile(new File(tempLocation + File.separator + "front" + File.separator + NICName + "." + frontExtension), new File(fileLocation + "outlet" + File.separator + "front" + File.separator + NICName + "." + backExtension));
                FileUtils.deleteQuietly(new File(tempLocation + File.separator + "front" + File.separator + NICName + "." + frontExtension));
            }
        } catch (IOException ex) {
            flag = false;
        }

        return flag;

    }

    @POST
    @Path("/logout")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ServiceSecurity(security = Security.RESTRICTED)
    public Message logout(Message requestMessage) {
        Message responseMessage = new Message();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();

        if (Objects.isNull(requestMessage) || Objects.isNull(requestMessage.getToken())) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        }
        UserDeviceinformation ud = UserDeviceinformationDAO.load(null, requestMessage.getToken());
        if (!Objects.isNull(ud)) {
            UserDeviceinformationDAO.remove(ud);
        }
        
        UserInformationDAO.modifyUserLoggedIn(loggedUser);

        responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
        responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());
        return responseMessage;
    }

    @POST
    @Path("/modifyaccount")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ServiceSecurity(security = Security.RESTRICTED)
    public Message modifyAccount(Message requestMessage) {
        Message responseMessage = new Message();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();
        OutletInformation outlet = OutletInformationDAO.load(loggedUser.getRelationId());
        if (Objects.isNull(requestMessage)) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        }

        if (Utilities.validateRequiredParameter(requestMessage.getOutletInformation().getOutletName())) {
            outlet.setOutletName(requestMessage.getOutletInformation().getOutletName());
        }

        if (!Objects.isNull(requestMessage.getOutletInformation().getGenTypedetailByTradeCategoryId())
                && Utilities.validateRequiredParameter(requestMessage.getOutletInformation().getGenTypedetailByTradeCategoryId().getTypeDetailId())) {
            outlet.setGenTypedetailByTradeCategoryId(requestMessage.getOutletInformation().getGenTypedetailByTradeCategoryId());
        }

        if (Utilities.validateRequiredParameter(requestMessage.getOutletInformation().getStreetAddress())) {
            outlet.setStreetAddress(requestMessage.getOutletInformation().getStreetAddress());
        }
        if (Utilities.validateRequiredParameter(requestMessage.getLatitude()) && Utilities.validateRequiredParameter(requestMessage.getLongitude())) {
            outlet.setLatitude(requestMessage.getLatitude());
            outlet.setLongitude(requestMessage.getLongitude());
        }

        OutletInformationDAO.modify(outlet, loggedUser);
        responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
        responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());
        return responseMessage;
    }

    @POST
    @Path("/uploadImage")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    @ServiceSecurity(security = Security.RESTRICTED)
    public Message uploadImage(@FormDataParam("outletImage") InputStream inputStreamOutletImage,
            @FormDataParam("outletImage") FormDataContentDisposition OutletDataImage,
            @FormDataParam("userImage") InputStream inputStreamUserImage,
            @FormDataParam("userImage") FormDataContentDisposition UserDataImage) throws UnsupportedEncodingException, NoSuchAlgorithmException, IOException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, Exception {
        Message responseMessage = new Message();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();

        OutletInformation outlet = OutletInformationDAO.load(loggedUser.getRelationId());
         String fileLocation = ConfigDAO.load(Configuration.IMAGE_PHYSICAL_PATH.getId()).getConfValue();
        String imageBaseURL = ConfigDAO.load(Configuration.IMAGE_BASE_URL.getId()).getConfValue();
       // String fileLocation = "C:\\application\\oc\\images\\";
        String tempLocation = ConfigDAO.load(Configuration.TEMP_DIRECTORY.getId()).getConfValue();
        String hashFileName = "";
        Message requestMessage = new Message();
        requestMessage.setUser(loggedUser);
        requestMessage.setOutletInformation(outlet);

        String locationStoreImage = "";
        boolean isStoreImage = false;
        if (!Objects.isNull(inputStreamOutletImage)) {
            if (hashFileName.isEmpty()) {
                hashFileName = generateHashNameForFiles(requestMessage);
            }
            locationStoreImage = fileLocation + File.separator + "outlet" + File.separator + hashFileName;

            isStoreImage = Utilities.uploadFile(fileLocation + File.separator + "outlet" + File.separator, locationStoreImage, inputStreamOutletImage);
            if (!isStoreImage) {
                throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Problem Occured While Uploading Image.").build());
            }
            GenImage genImage = new GenImage();
            genImage.setOutletInformation(outlet);
            genImage.setImageName(hashFileName);
            genImage.setUserInformationByInsertId(loggedUser);
            genImage.setInsertDate(new Date());
            genImage.setIsActive(isStoreImage);
            GenImageDAO.add(genImage);
            ArrayList<GenImage> lstGenOutletImages = GenImageDAO.loadList(outlet.getOutletId(), null);
            responseMessage.setLstGenImages(lstGenOutletImages);

        }

        if (!Objects.isNull(inputStreamUserImage)) {
            if (hashFileName.isEmpty()) {
                hashFileName = generateHashNameForFiles(requestMessage);
            }
            locationStoreImage = fileLocation + File.separator + "user" + File.separator + hashFileName;

            isStoreImage = Utilities.uploadFile(fileLocation + File.separator + "user", locationStoreImage, inputStreamUserImage);
            if (!isStoreImage) {
                throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Problem Occured While Uploading Image.").build());
            }
            GenImage genImage = new GenImage();
            ArrayList<GenImage> lstGenUserImages = GenImageDAO.loadList(null, loggedUser.getUserId());

            if (!Objects.isNull(lstGenUserImages) && !lstGenUserImages.isEmpty()) {
                File imageFile = new File(fileLocation + File.separator + "user" + File.separator + lstGenUserImages.get(0).getImageName());
                if (imageFile.exists()) {
                    imageFile.delete();
                }
                genImage = lstGenUserImages.get(0);
                genImage.setUserInformationByUserId(loggedUser);
                genImage.setImageName(hashFileName);
                genImage.setUserInformationByEditId(loggedUser);
                genImage.setEditDate(new Date());
                genImage.setIsActive(true);
                GenImageDAO.update(genImage);
            } else {
                genImage.setUserInformationByUserId(loggedUser);
                genImage.setImageName(hashFileName);
                genImage.setUserInformationByInsertId(loggedUser);
                genImage.setInsertDate(new Date());
                genImage.setIsActive(true);
                GenImageDAO.add(genImage);
                lstGenUserImages = new ArrayList<>();
                lstGenUserImages.add(genImage);
            }

            responseMessage.setLstGenImages(lstGenUserImages);
        }
        responseMessage.setImageBaseURL(imageBaseURL);
        responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
        responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());
        return responseMessage;

    }

    @POST
    @Path("/removeImage")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({MediaType.APPLICATION_JSON})
    @ServiceSecurity(security = Security.RESTRICTED)
    public Message removeImage(Message requestMessage) {
        Message responseMessage = new Message();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();

        OutletInformation outlet = OutletInformationDAO.load(loggedUser.getRelationId());
         String fileLocation = ConfigDAO.load(Configuration.IMAGE_PHYSICAL_PATH.getId()).getConfValue();
        String imageBaseURL = ConfigDAO.load(Configuration.IMAGE_BASE_URL.getId()).getConfValue();
       // String fileLocation = "C:\\application\\oc\\images\\";

        if (Objects.isNull(requestMessage)) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        }
        if (!Utilities.validateRequiredParameter(requestMessage.getImageId())) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        }

        GenImage genImage = GenImageDAO.load(requestMessage.getImageId());
        if (!Objects.isNull(genImage)) {
            if (!Objects.isNull(genImage.getUserInformationByUserId())) {
                File imageFile = new File(fileLocation + File.separator + "user" + File.separator + genImage.getImageName());
                if (imageFile.exists()) {
                    imageFile.delete();
                }
                genImage.setUserInformationByEditId(loggedUser);
                genImage.setEditDate(new Date());
                genImage.setIsActive(false);
                GenImageDAO.update(genImage);
                ArrayList<GenImage> lstGenUserImages = GenImageDAO.loadList(null, loggedUser.getUserId());
                responseMessage.setLstGenImages(lstGenUserImages);
            } else if (!Objects.isNull(genImage.getOutletInformation())) {
                File imageFile = new File(fileLocation + File.separator + "outlet" + File.separator + genImage.getImageName());
                if (imageFile.exists()) {
                    imageFile.delete();
                }
                genImage.setUserInformationByEditId(loggedUser);
                genImage.setEditDate(new Date());
                genImage.setIsActive(false);
                GenImageDAO.update(genImage);
                ArrayList<GenImage> lstGenOutletImages = GenImageDAO.loadList(outlet.getOutletId(), null);
                responseMessage.setLstGenImages(lstGenOutletImages);

            }

        }

        responseMessage.setImageBaseURL(imageBaseURL);
        responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
        responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());
        return responseMessage;
    }



    @POST
    @Path("/generateloginotp")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ServiceSecurity(security = Security.BOTH)
    public Message generateloginotp(Message requestMessage) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        Message responseMessage = new Message();

        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();

        if (Objects.isNull(loggedUser) && (Objects.isNull(requestMessage) || Objects.isNull(requestMessage.getCellNumber()) || requestMessage.getCellNumber().trim().isEmpty())) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        }

        if (Objects.isNull(loggedUser)) {
            //In this case do not authenticate the user, instead the user should be loaded by specifying cell number and isactive = 1 and outlet role type id. 
            //The request will NOT receive password in RequestMessage
            loggedUser = UserInformationDAO.loadUser(requestMessage.getCellNumber(), MasterType.OUTLET_TYPES.getId());
        }

        if (Objects.isNull(loggedUser)) {
            responseMessage.setStatus(ServiceStatus.UNREGISTERED_MOBILE_NUMBER.getValue());
            responseMessage.setStatusText(ServiceStatus.UNREGISTERED_MOBILE_NUMBER.getStatusText());
            return responseMessage;
        }

        UserActivity activity = new UserActivity();
        activity.setActivityDate(new Date());
        activity.setGenTypedetailByActivityStatusId(GenTypedetailDAO.build(AccountVerificationStatusType.PENDING.getId()));
        activity.setGenTypedetailByActivityTypeId(GenTypedetailDAO.build(ActivityType.Account_Login.getId()));
        activity.setInsertDate(new Date());
        activity.setIsActive(true);
        if (!Objects.isNull(loggedUser)) {
            activity.setOutletInformation(OutletInformationDAO.build(loggedUser.getRelationId()));
            activity.setUserInformationByInsertId(loggedUser);
        }
        
            
        UserVerifyloginactivity loginVerify = new UserVerifyloginactivity();
        loginVerify.setGenTypedetail(GenTypedetailDAO.build(AccountVerificationStatusType.PENDING.getId()));
        loginVerify.setInsertDate(new Date());
        loginVerify.setIsActive(true);
        loginVerify.setPinNumber((int) (Math.random() * 9000) + 1000);
        loginVerify.setUserActivity(activity);
        loginVerify.setUserInformationByInsertId(UserInformationDAO.getSystemDefaultUser());
        if (!Objects.isNull(loggedUser)) {
            loginVerify.setOutletInformation(OutletInformationDAO.build(loggedUser.getRelationId()));
            loginVerify.setUserInformationByRequestById(loggedUser);
        }

        UserSmsactivity smsActivity = new UserSmsactivity();
        smsActivity.setGenTypedetail(GenTypedetailDAO.build(SMSStatusType.PENDING.getId()));
        smsActivity.setInsertDate(new Date());
        smsActivity.setIsActive(true);
        smsActivity.setUserActivity(activity);
        smsActivity.setMessageBody(GenTypedetailDAO.load(OutletSMSNotification.OTP_GENERATED.getId()).getTypeInfo1().replace("{pin_number}", loginVerify.getPinNumber().toString()));
        smsActivity.setProcessCount(0);
        smsActivity.setScheduledDate(new Date());
        smsActivity.setUserInformationByInsertId(UserInformationDAO.getSystemDefaultUser());
        if (!Objects.isNull(loggedUser)) {
            smsActivity.setActivityNumber(loggedUser.getUserCellNumber1());
            smsActivity.setOutletInformation(OutletInformationDAO.build(loggedUser.getRelationId()));
        }
        

        UserVerificationLoginActivityDAO.add(activity, loginVerify, smsActivity);

        responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
        responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());
        return responseMessage;
    }

    @POST
    @Path("/verifyaccountlogin")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ServiceSecurity(security = Security.BOTH)
    public Message verifyAccountLogin(Message requestMessage) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        Message responseMessage = new Message();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();

        if (Objects.isNull(requestMessage) || Objects.isNull(requestMessage.getPinNumber())) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        }

        if (Objects.isNull(loggedUser) && (Objects.isNull(requestMessage) || Objects.isNull(requestMessage.getCellNumber()) || requestMessage.getCellNumber().trim().isEmpty())) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        }

        if (Objects.isNull(loggedUser)) {
            //In this case do not authenticate the user, instead the user should be loaded by specifying cell number and isactive = 1 and outlet role type id. 
            //The request will NOT receive password in RequestMessage
            loggedUser = UserInformationDAO.loadUser(requestMessage.getCellNumber(), MasterType.OUTLET_TYPES.getId());
        }

        if (Objects.isNull(loggedUser)) {
            responseMessage.setStatus(ServiceStatus.UNREGISTERED_MOBILE_NUMBER.getValue());
            responseMessage.setStatusText(ServiceStatus.UNREGISTERED_MOBILE_NUMBER.getStatusText());
            return responseMessage;
        }
        
        UserVerifyloginactivity accVerify = UserVerificationLoginActivityDAO.load(loggedUser.getRelationId(), requestMessage.getPinNumber());

        if (Objects.isNull(accVerify)) {
            responseMessage.setStatus(ServiceStatus.OTP_VERIFICATION_FAILED.getValue());
            responseMessage.setStatusText(ServiceStatus.OTP_VERIFICATION_FAILED.statusText);
            return responseMessage;
        }

        accVerify.setEditDate(new Date());   
        accVerify.setGenTypedetail(GenTypedetailDAO.build(AccountVerificationStatusType.VERIFIED.getId()));
        accVerify.setUserInformationByEditId(UserInformationDAO.getSystemDefaultUser());

        UserVerificationLoginActivityDAO.modify(loggedUser.getUserId() , accVerify);

        responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
        responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());
        return responseMessage;
    }

}
