/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oc.controller;

import com.oc.services.entity.Message;
import com.oc.services.entity.PushNotification;
import com.oc.services.entity.PushNotificationBody;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.oc.db.controller.ConfigDAO;
import org.oc.db.controller.UserDeviceinformationDAO;
import org.oc.db.controller.UserNotificationActivityDAO;
import org.oc.db.controller.Utilities;
import org.oc.db.entity.Config;
import org.oc.db.entity.GenTypedetail;
import org.oc.db.entity.UserDeviceinformation;
import org.oc.db.entity.UserInformation;
import org.oc.db.entity.UserNotificationactivity;
import org.oc.db.entity.enums.AppType;
import org.oc.db.entity.enums.Configuration;
import org.oc.db.entity.enums.MasterType;
import org.oc.db.entity.enums.PushNotificationConfiguration;
import org.oc.db.entity.enums.PushNotificationStatus;

/**
 *
 * @author DELL PRECISION M6800
 */
public class NotificationServiceController {

    private static Config configRetailerAppApiKey;
    private static Config configFCMUrl;
    private static Config configAdminAppApiKey;

    public static void start() {
        try {

            configRetailerAppApiKey = ConfigDAO.load(PushNotificationConfiguration.PUSHNOTIFICATION_API_KEY.getValue());
            configAdminAppApiKey = ConfigDAO.load(PushNotificationConfiguration.PUSHNOTIFICATION_ADMINAPP_API_KEY.getValue());
            configFCMUrl = ConfigDAO.load(PushNotificationConfiguration.PUSHNOTIFICATION_FCM_URL.getValue());
            createMessageAndSend();

        } catch (Exception e) {
            Logger.getLogger(NotificationServiceController.class.getName()).log(Level.SEVERE, null, e);

        }
    }

    private static void createMessageAndSend() throws ParseException {
        ArrayList<UserNotificationactivity> lstuNotificationactivitys = UserNotificationActivityDAO.loadList(MasterType.PUSHNOTIFICATION_TYPE.getId());
        PushNotificationController pushNotificationController = null;
        try {
            for (UserNotificationactivity noti : lstuNotificationactivitys) {
                UserDeviceinformation ud = (UserDeviceinformationDAO.loadList(noti.getUserInformationBySendToId().getUserId())).get(0);
                if (Objects.equals(ud.getGenTypedetail().getTypeDetailId(), AppType.ADMIN_APP.getId())) {
                    pushNotificationController = new PushNotificationController(configAdminAppApiKey.getConfValue(), configFCMUrl.getConfValue());
                } else if (Objects.equals(ud.getGenTypedetail().getTypeDetailId(), AppType.RETAILER_APP.getId())) {
                    pushNotificationController = new PushNotificationController(configRetailerAppApiKey.getConfValue(), configFCMUrl.getConfValue());
                }
                Message message = new Message(noti.getAppToken());

                PushNotificationBody data = new PushNotificationBody(
                        noti.getNotificationId(),
                        noti.getTitle(),
                        noti.getMessage(),
                        noti.getImageUrl(),
                        noti.getGenTypedetailByTypeId(),
                        noti.getManInformation(),
                        noti.getBrandInformation(),
                        noti.getBrandVariantdetail(),
                        noti.getIsPromotionAvailable()
                );

                Config defaultClickAction = ConfigDAO.load(Configuration.DEFAULT_RA_CLICK_ACTION.getId());
                PushNotification pn;
                if (Objects.equals(ud.getGenTypedetail().getTypeDetailId(), AppType.ADMIN_APP.getId())) {
                     pn = new PushNotification(noti.getTitle(), noti.getMessage(), noti.getImageUrl(), null);
                }else{
                      pn = new PushNotification(noti.getTitle(), noti.getMessage(), noti.getImageUrl(), defaultClickAction.getConfValue());
                }
                
                message.setNotification(pn);
                HashMap<String, PushNotificationBody> dataHash = new HashMap<>();
                dataHash.put("data", data);
                message.setData(dataHash);
                Boolean status = pushNotificationController.sendNotification(message);
                GenTypedetail genTypedetail = new GenTypedetail();
                if (status) {
                    genTypedetail.setTypeDetailId(PushNotificationStatus.SENT.getValue());
                    noti.setGenTypedetailByStatusId(genTypedetail);
                    UserInformation userInformation = new UserInformation();
                    if (!Utilities.validateRequiredParameter(noti.getDistInformation().getDistributerId())) {
                        noti.setDistInformation(null);
                    }
                    if (!Utilities.validateRequiredParameter(noti.getUserInformationBySendToId().getUserId())) {
                        noti.setUserInformationBySendToId(null);
                    }
                    if (!Utilities.validateRequiredParameter(noti.getManInformation().getManufacturerId())) {
                        noti.setManInformation(null);
                    }
                    if (!Utilities.validateRequiredParameter(noti.getOutletInformation().getOutletId())) {
                        noti.setOutletInformation(null);
                    }
                    if (!Utilities.validateRequiredParameter(noti.getBrandInformation().getBrandId())) {
                        noti.setBrandInformation(null);
                    }
                    if (!Utilities.validateRequiredParameter(noti.getBrandVariantdetail().getVariantDetailId())) {
                        noti.setBrandVariantdetail(null);
                    }
                    userInformation.setUserId(1);
                    noti.setUserInformationByEditId(userInformation);
                    noti.setEditDate(new Date());
                    noti.setIsActive(true);
                    UserNotificationActivityDAO.modify(noti);
                }

            }

        } catch (Exception ex) {
            Logger.getLogger(NotificationServiceController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
