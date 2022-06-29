        /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oc.services.retailerapp;

import com.oc.controller.CacheController;
import com.oc.services.entity.ServiceSecurity;
import org.oc.db.entity.enums.ConfigurationService;
import com.oc.services.enums.Security;
import com.oc.services.enums.ServiceStatus;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.oc.db.controller.ConfigAppDAO;
import org.oc.db.controller.ConfigDAO;
import org.oc.db.controller.GenTypedetailDAO;
import org.oc.db.controller.GeoLocationDAO;
import org.oc.db.controller.HibernateController;
import org.oc.db.controller.OutletInformationDAO;
import org.oc.db.controller.UserCampaignActivityDAO;
import org.oc.db.entity.Config;
import org.oc.db.entity.ConfigApp;
import org.oc.db.entity.ConfigService;
import org.oc.db.entity.GeoLocation;
import org.oc.db.entity.OutletInformation;
import org.oc.db.entity.UserCampaignactivity;
import org.oc.db.entity.UserInformation;
import org.oc.db.entity.enums.ApplicationReleaseType;
import org.oc.db.entity.enums.ApplicationType;
import org.oc.db.entity.enums.Configuration;
import org.oc.db.controller.DistDeliverySlotDAO;
import org.oc.db.entity.DistDeliveryslot;
import com.oc.services.response.DeliverySlot;
import com.oc.services.response.DeliverySlotResponse;
import java.text.SimpleDateFormat;
import javax.ws.rs.BadRequestException;
import org.oc.db.controller.UserInformationDAO;


/**
 *
 * @author Clc
 */
@Path("general")
public class GeneralResource {

    @Context
    private ContainerRequestContext requestContext;
    private UserInformation loggedUser = null;
    @Context
    private ServletContext servletContext;

    @POST
    @Path("/getlocationdata")
    @Produces(MediaType.APPLICATION_JSON)
    @ServiceSecurity(security = Security.BY_PASS)
    public Message getLocation() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Message responseMessage = new Message();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();
        ArrayList<GeoLocation> lstCity = null;
        ArrayList<GeoLocation> lstTown = null;
        ArrayList<GeoLocation> lstArea = null;

        lstCity = GeoLocationDAO.loadList("1", Boolean.TRUE);
        GeoLocationDAO.clearSets(lstCity);
        lstTown = GeoLocationDAO.loadList("2", Boolean.TRUE);
        GeoLocationDAO.clearSets(lstTown);
        lstArea = GeoLocationDAO.loadList("3", Boolean.TRUE);
        GeoLocationDAO.clearSets(lstArea);

        responseMessage.setCityList(lstCity);
        responseMessage.setTownList(lstTown);
        responseMessage.setAreaList(lstArea);

        responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
        responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());
        return responseMessage;
    }

    @POST
    @Path("/getglobalsettings")
    @Produces(MediaType.APPLICATION_JSON)
    @ServiceSecurity(security = Security.BY_PASS)
    public Response getGlobalSettings() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Message responseMessage = new Message();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();
        ArrayList<Config> lstGlobalConfig = null;

        Integer[] configIds = {
            Configuration.SKIP_CNIC_VERIFICATION.getId(),
            Configuration.SKIP_MOBILE_NUMBER_VERIFICATION.getId(),
            Configuration.TERMS_OF_SERVICE.getId(),
            Configuration.PRIVACY_POLICY_URL.getId(),
            Configuration.FAQs_URL.getId(),
            Configuration.VALIDATE_ACNO_BOOK_TIME.getId(),
            Configuration.VALIDATE_AC_CNIC_BOOK_TIME.getId(),
            Configuration.AUDIO_BASE_URL.getId(),
            Configuration.DELIVERY_CUTOFF_TIME.getId(),
            Configuration.UNIVERSAL_MINIMUM_ORDER_VALUE.getId(),
            Configuration.DELIVERY_CUTOFF_TIME.getId(),
            Configuration.DELIVERY_DAYS_AFTER_CUTOFF.getId(),
            Configuration.UNIVERSAL_MAXIMUM_ORDER_VALUE.getId(),
            Configuration.CART_NOTIFICATION_DURATION_1.getId(),
            Configuration.CART_NOTIFICATION_DURATION_2.getId(),
            Configuration.CART_NOTIFICATION_MESSAGE.getId(),
            Configuration.FIREBASE_AUTHENTICATION_FLAG.getId()
        };

        lstGlobalConfig = ConfigDAO.loadList(configIds);
        responseMessage.setLstConfig(lstGlobalConfig);
        responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
        responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());
        CacheControl cc = new CacheControl();
        cc.setMaxAge(300);
        cc.setPrivate(true);
        cc.setNoStore(true);
        Response.ResponseBuilder builder = Response.ok(responseMessage, "application/json");
        builder.cacheControl(cc);
        return builder.build();
//        return responseMessage;
    }

    @POST
    @Path("/getCampaignList")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ServiceSecurity(security = Security.RESTRICTED)
    public Message getCampaignList(Message requestMessage) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, ParseException {
        Message responseMessage = new Message();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();

        Integer outletId = loggedUser.getRelationId();
        OutletInformation outlet = null;
        Integer areaId = null;
        Integer townId = null;
        Integer cityId = null;
        Integer tradeCategoryId = null;

        if (!Objects.isNull(outletId)) {
            outlet = OutletInformationDAO.load(outletId);
        }

        ArrayList<UserCampaignactivity> lstUserCampaign = UserCampaignActivityDAO.loadList(
                outlet,
                new Date(),
                outlet.getGenTypedetailByTradeCategoryId().getTypeDetailId(),
                outlet.getGeoLocation().getLocationId(),
                outlet.getGeoLocation().getGeoLocation().getLocationId(),
                outlet.getGeoLocation().getGeoLocation().getGeoLocation().getLocationId()
        );

        if (lstUserCampaign.size() > 0) {
            lstUserCampaign = UserCampaignActivityDAO.clearSets(lstUserCampaign);
        }

        if (lstUserCampaign.isEmpty()) {
            responseMessage.setStatus(ServiceStatus.NO_DATA_AVAILABLE.getValue());
            responseMessage.setStatusText(ServiceStatus.NO_DATA_AVAILABLE.getStatusText());
            return responseMessage;
        }

        responseMessage.setLstUserCampaignActivity(lstUserCampaign);
        responseMessage.setImageBaseURL(ConfigDAO.load(Configuration.IMAGE_BASE_URL.getId()).getConfValue());
        responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
        responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());

        return responseMessage;

    }
//
//    @POST
//    @Path("/upload")
//    @Consumes(MediaType.MULTIPART_FORM_DATA)
//    @ServiceSecurity(security = Security.BY_PASS)
//    public Response uploadFile(@FormDataParam("upload") InputStream is, @FormDataParam("upload") FormDataContentDisposition formData) {
//        String fileLocation = "c:/temp/" + formData.getFileName();
//        //saveFile(is, fileLocation);
//        String result = "Successfully File Uploaded on the path " + fileLocation;
//        return Response.status(Status.OK).entity(result).build();
//    }

    @GET
    @Path("/settings")
    @Produces(MediaType.APPLICATION_JSON)
    @ServiceSecurity(security = Security.BY_PASS)
    public Response settings(
            @QueryParam("versionnumber") String versionNumber
    ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, ParseException {

        Map<Integer, ConfigService> configServiceMap = (Map<Integer, ConfigService>) servletContext.getAttribute("configServiceMap");
        ConfigService configService = configServiceMap.get(ConfigurationService.GET_SETTINGS_GR.getId());
        CacheController cacheControlObject = new CacheController();
        CacheControl cc = cacheControlObject.createCacheControl(configService);
        ConfigApp configApp = null;

        Message responseMessage = new Message();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();
        ArrayList<Config> lstGlobalConfig = null;

        Integer[] configIds = {
            Configuration.SKIP_CNIC_VERIFICATION.getId(),
            Configuration.SKIP_MOBILE_NUMBER_VERIFICATION.getId(),
            Configuration.TERMS_OF_SERVICE.getId(),
            Configuration.PRIVACY_POLICY_URL.getId(),
            Configuration.FAQs_URL.getId(),
            Configuration.VALIDATE_ACNO_BOOK_TIME.getId(),
            Configuration.VALIDATE_AC_CNIC_BOOK_TIME.getId(),
            Configuration.AUDIO_BASE_URL.getId(),
            Configuration.DELIVERY_CUTOFF_TIME.getId(),
            Configuration.UNIVERSAL_MINIMUM_ORDER_VALUE.getId(),
            Configuration.DELIVERY_CUTOFF_TIME.getId(),
            Configuration.DELIVERY_DAYS_AFTER_CUTOFF.getId(),
            Configuration.UNIVERSAL_MAXIMUM_ORDER_VALUE.getId(),
            Configuration.CART_NOTIFICATION_DURATION_1.getId(),
            Configuration.CART_NOTIFICATION_DURATION_2.getId(),
            Configuration.CART_NOTIFICATION_MESSAGE.getId(),
            Configuration.APP_BANNER_MESSAGE.getId(),
            Configuration.APP_BANNER_MESSAGE_ALLIGNMENT.getId(),
            Configuration.FIREBASE_AUTHENTICATION_FLAG.getId()
            
        };

        lstGlobalConfig = ConfigDAO.loadList(configIds);

        if (!Objects.isNull(versionNumber)) {
            configApp = ConfigAppDAO.load(versionNumber.trim(), ApplicationType.RETAILER_APP.getId());
        } else {
            configApp = buildDefaultConfigApp(configApp);
        }

        if (!Objects.isNull(configApp)) {
            ConfigAppDAO.clearSets(configApp);
        }

        responseMessage.setConfigApp(configApp);
        responseMessage.setLstConfig(lstGlobalConfig);
        responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
        responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());
        Response.ResponseBuilder builder = Response.ok(responseMessage, "application/json");
        builder.cacheControl(cc);
        return builder.build();
    }

    @GET
    @Path("/getCampaigns")
    @ServiceSecurity(security = Security.RESTRICTED)
    public Response getCampaigns() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, ParseException {
        Map<Integer, ConfigService> configServiceMap = (Map<Integer, ConfigService>) servletContext.getAttribute("configServiceMap");
        ConfigService configService = configServiceMap.get(ConfigurationService.GET_CAMPAIGNS_GR.getId());
        CacheController cacheControlObject = new CacheController();
        CacheControl cc = cacheControlObject.createCacheControl(configService);

        Message responseMessage = new Message();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();

        Integer outletId = loggedUser.getRelationId();
        OutletInformation outlet = null;
        Integer areaId = null;
        Integer townId = null;
        Integer cityId = null;
        Integer tradeCategoryId = null;

        if (!Objects.isNull(outletId)) {
            outlet = OutletInformationDAO.load(outletId);
        }

        ArrayList<UserCampaignactivity> lstUserCampaign = UserCampaignActivityDAO.loadList(
                outlet,
                new Date(),
                outlet.getGenTypedetailByTradeCategoryId().getTypeDetailId(),
                outlet.getGeoLocation().getLocationId(),
                outlet.getGeoLocation().getGeoLocation().getLocationId(),
                outlet.getGeoLocation().getGeoLocation().getGeoLocation().getLocationId()
        );

        if (lstUserCampaign.size() > 0) {
            lstUserCampaign = UserCampaignActivityDAO.clearSets(lstUserCampaign);
        }

        if (lstUserCampaign.isEmpty()) {
            responseMessage.setStatus(ServiceStatus.NO_DATA_AVAILABLE.getValue());
            responseMessage.setStatusText(ServiceStatus.NO_DATA_AVAILABLE.getStatusText());
            responseMessage.setIsLoggedIn(UserInformationDAO.getUserLoggedIn(loggedUser.getUserId()));
            responseMessage.setImageBaseURL(ConfigDAO.load(Configuration.IMAGE_BASE_URL.getId()).getConfValue());
            Response.ResponseBuilder builder = Response.ok(responseMessage, "application/json");
            builder.cacheControl(cc);
            return builder.build();
        }

        responseMessage.setIsLoggedIn(UserInformationDAO.getUserLoggedIn(loggedUser.getUserId()));
        responseMessage.setLstUserCampaignActivity(lstUserCampaign);
        responseMessage.setImageBaseURL(ConfigDAO.load(Configuration.IMAGE_BASE_URL.getId()).getConfValue());
        responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
        responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());

        Response.ResponseBuilder builder = Response.ok(responseMessage, "application/json");
        builder.cacheControl(cc);
        return builder.build();

    }

    private ConfigApp buildDefaultConfigApp(ConfigApp configApp) {
        configApp = new ConfigApp();

        configApp.setGenTypedetailByStatusId(GenTypedetailDAO.build(ApplicationReleaseType.IMMEDIATE.getId()));
        configApp.getGenTypedetailByStatusId().setTypeDetailName(ApplicationReleaseType.IMMEDIATE.getName());
        configApp.setGenTypedetailByTypeId(GenTypedetailDAO.build(ApplicationType.RETAILER_APP.getId()));
        configApp.getGenTypedetailByTypeId().setTypeDetailName(ApplicationType.RETAILER_APP.getName());

        return configApp;
    }
       
    @GET
    @Path("/deliveryslots")
    @Produces(MediaType.APPLICATION_JSON)
    @ServiceSecurity(security = Security.RESTRICTED)
    public DeliverySlotResponse getDeliveryslot() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, ParseException {
         
        Integer count = Integer.parseInt(ConfigDAO.load(Configuration.DELIVERY_SLOT_DAYS.getId()).getConfValue());
       
        
        List<DistDeliveryslot> dList = DistDeliverySlotDAO.loadDeliverySlot(count);
        List<DeliverySlot> responseList = new ArrayList<DeliverySlot>();
       
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");    
               
        for(DistDeliveryslot ds: dList){
            DeliverySlot temp = new DeliverySlot();
            temp.setId(ds.getId());
            temp.setOrderQuantity(ds.getOrderQuantity());
            temp.setSlotStartTime(ds.getSlotStartTime());
            temp.setSlotEndTime(ds.getSlotEndTime());
            temp.setSlotCuttoffTime(ds.getSlotCuttoffTime());
            temp.setDeliveryDate(formatter.format(ds.getDeliveryDate()));
            responseList.add(temp);
        }
        
        DeliverySlotResponse response = new DeliverySlotResponse();
        
        
        if(dList.isEmpty()){
            response.setStatus(ServiceStatus.NO_DATA_AVAILABLE.getValue());
            response.setStatusText(ServiceStatus.NO_DATA_AVAILABLE.getStatusText());
            response.setDeliverySlotList(responseList);
            return response;
            
        }
        
        response.setStatus(ServiceStatus.SUCCESS.getValue());
        response.setStatusText(ServiceStatus.SUCCESS.getStatusText());
        response.setDeliverySlotList(responseList);
        
        return response;

    }
    
    @GET
    @Path("/getoutletauthenticate")
    @Produces(MediaType.APPLICATION_JSON)
    @ServiceSecurity(security = Security.RESTRICTED)
     public Message getAutenticateOutlet() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, ParseException {
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();
        Integer outletId = loggedUser.getRelationId();
        OutletInformation outlet = OutletInformationDAO.load(outletId);
        
        Message responseMessage = new Message();
        responseMessage.setIsAuthenticated(outlet.getIsAuthenticated());
       
        return responseMessage;

     }
}