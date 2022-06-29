/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oc.services.retailerapp;

import com.oc.services.enums.ServiceStatus;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.oc.db.controller.ConfigDAO;
import org.oc.db.controller.GenTypedetailDAO;
import org.oc.db.controller.UserNotificationActivityDAO;
import org.oc.db.controller.Utilities;
import org.oc.db.entity.Config;
import org.oc.db.entity.OutletInformation;
import org.oc.db.entity.UserActivity;
import org.oc.db.entity.UserInformation;
import org.oc.db.entity.UserNotificationactivity;
import org.oc.db.entity.enums.ActivityStatusType;
import org.oc.db.entity.enums.Configuration;
import org.oc.db.entity.enums.NotificationActivityTypes;
import org.oc.db.entity.enums.PushNotificationStatus;
import org.oc.db.entity.enums.PushNotificationType;

/**
 *
 * @author DELL PRECISION M6800
 */
@Path("pushnotification")
public class NotificationResource {

    @Context
    private ContainerRequestContext requestContext;
    private UserInformation loggedUser = null;

    @GET
    @Path("/retrievepushnotifications")
    public Response getPushNotifications(
            @QueryParam("startindex") Integer startIndex,
            @QueryParam("searchtxt") String searchTxt
    ) throws ParseException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Message responseMessage = new Message();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();
        if (Objects.isNull(startIndex)) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters are Missing").build());

        }

        ArrayList<UserNotificationactivity> lstUserNotificationactivitys = UserNotificationActivityDAO.loadList(loggedUser.getUserId(), startIndex, searchTxt);

        if (!Objects.isNull(lstUserNotificationactivitys) && !lstUserNotificationactivitys.isEmpty()) {
            if (lstUserNotificationactivitys.size() == Integer.parseInt(ConfigDAO.load(Configuration.MAX_RECORDS.getId()).getConfValue())) {
                responseMessage.setHasMoreData(true);
            }

            responseMessage.setLstUserNotificationactivitys(UserNotificationActivityDAO.clearSets(lstUserNotificationactivitys));
            responseMessage.setUnSeenNotification(lstUserNotificationactivitys.get(0).getUnSeenNotifications());
            responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
            responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());
            Response.ResponseBuilder builder = Response.ok(responseMessage, "application/json");
            return builder.build();
        }
        responseMessage.setHasMoreData(false);
        responseMessage.setStatus(ServiceStatus.NO_DATA_AVAILABLE.getValue());
        responseMessage.setStatusText(ServiceStatus.NO_DATA_AVAILABLE.getStatusText());
        Response.ResponseBuilder builder = Response.ok(responseMessage, "application/json");
        return builder.build();
    }

    @POST
    @Path("/modifypushnotificationstatus")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Message getPushNotifications(Message requestMessage) throws ParseException {
        Message responseMessage = new Message();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();
        if (!Utilities.validateRequiredParameter(requestMessage.getPushNotificationIds())) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters are Missing").build());

        }

        if (UserNotificationActivityDAO.modifyListStatus(requestMessage.getPushNotificationIds(), loggedUser)) {
            responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
            responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());
            return responseMessage;
        }
        responseMessage.setStatus(ServiceStatus.FAILED.getValue());
        responseMessage.setStatusText(ServiceStatus.FAILED.getStatusText());
        return responseMessage;
    }

    @POST
    @Path("/addpushnotification")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Message addPushNotification(Message requestMessage) throws ParseException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Message responseMessage = new Message();
        final long HOUR = 3600 * 1000;
        final long MINUTE = 60 * 1000;
        ArrayList<UserNotificationactivity> lstUserNotificationactivitys = new ArrayList<>();
        ArrayList<UserActivity> lstUserActivitys = new ArrayList<>();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();
        OutletInformation outlet = new OutletInformation();
        outlet.setOutletId(loggedUser.getRelationId());
        if (Objects.isNull(requestMessage)
                || Objects.isNull(requestMessage.getLstUserNotificationactivitys())
                || requestMessage.getLstUserNotificationactivitys().isEmpty()) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters are Missing").build());

        }
        UserNotificationActivityDAO.blockList(null,
                null,
                PushNotificationType.CART_NOTIFICATION.getValue(),
                loggedUser);
        for (UserNotificationactivity una : requestMessage.getLstUserNotificationactivitys()) {
            if (Objects.isNull(una.getGenTypedetailByTypeId())
                    || !Utilities.validateRequiredParameter(una.getMessage())
                    || !Utilities.validateRequiredParameter(una.getAppToken())
                    || Objects.isNull(una.getScheduledDate())) {
                throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters are Missing").build());
            }
            UserActivity ua = new UserActivity();
            ua.setActivityDate(una.getScheduledDate());
            ua.setGenTypedetailByActivityTypeId(GenTypedetailDAO.build(NotificationActivityTypes.RETAILER_APP_NOTIFICATION.getId()));
            ua.setGenTypedetailByActivityStatusId(GenTypedetailDAO.build(ActivityStatusType.CLOSED.getId()));
            ua.setOutletInformation(outlet);
            ua.setInsertDate(new Date());
            ua.setUserInformationByInsertId(loggedUser);
            ua.setIsActive(true);
            UserNotificationactivity userNotificationactivity = new UserNotificationactivity();
            userNotificationactivity.setGenTypedetailByTypeId(una.getGenTypedetailByTypeId());
            userNotificationactivity.setAppToken(una.getAppToken());
            userNotificationactivity.setTitle(una.getTitle());
            userNotificationactivity.setMessage(una.getMessage());
            userNotificationactivity.setScheduledDate(una.getScheduledDate());
            userNotificationactivity.setGenTypedetailByStatusId(GenTypedetailDAO.build(PushNotificationStatus.PENDING.getValue()));
            userNotificationactivity.setIsActive(true);
            userNotificationactivity.setInsertDate(new Date());
            userNotificationactivity.setUserInformationByInsertId(loggedUser);
            userNotificationactivity.setOutletInformation(outlet);
            userNotificationactivity.setUserInformationBySendToId(loggedUser);
            userNotificationactivity.setUserActivity(ua);
            userNotificationactivity.setIsPromotionAvailable(Boolean.FALSE);

            lstUserNotificationactivitys.add(userNotificationactivity);
            lstUserActivitys.add(ua);
        }

        if (UserNotificationActivityDAO.addNotification(lstUserNotificationactivitys, lstUserActivitys)) {
            responseMessage.setLstUserNotificationactivitys(UserNotificationActivityDAO.clearSets(lstUserNotificationactivitys));
            responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
            responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());
            return responseMessage;
        }

        responseMessage.setStatus(ServiceStatus.FAILED.getValue());
        responseMessage.setStatusText(ServiceStatus.FAILED.getStatusText());
        return responseMessage;

    }

    @POST
    @Path("/blockpushnotification")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Message blockPushNotification(Message requestMessage) throws ParseException {
        Message responseMessage = new Message();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();
        if (!Utilities.validateRequiredParameter(requestMessage.getPushNotificationIds())
                && !Utilities.validateRequiredParameter(requestMessage.getNotificationStatusId())
                && !Utilities.validateRequiredParameter(requestMessage.getNotificationTypeId())) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters are Missing").build());

        }
        if (UserNotificationActivityDAO.blockList(requestMessage.getPushNotificationIds(),
                requestMessage.getNotificationStatusId(),
                requestMessage.getNotificationTypeId(),
                loggedUser)) {
            responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
            responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());
            return responseMessage;
        }
        responseMessage.setStatus(ServiceStatus.FAILED.getValue());
        responseMessage.setStatusText(ServiceStatus.FAILED.getStatusText());
        return responseMessage;

    }
}
