/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oc.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.glassfish.jersey.server.ResourceConfig;
import org.oc.db.controller.ConfigDAO;
import org.oc.db.controller.ConfigServiceDAO;
import org.oc.db.controller.OCUtil;
import org.oc.db.entity.Config;
import org.oc.db.entity.ConfigService;
import org.oc.db.entity.enums.Configuration;
import org.oc.db.entity.enums.PushNotificationConfiguration;

/**
 *
 * @author Clc
 */
@WebListener
public class ApplicationContextListener implements ServletContextListener {

    private static ScheduledExecutorService pushNotificationService;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        String configPath = context.getRealPath("/WEB-INF/OCOperations.cfg.xml");
        OCUtil.setConfigPath(configPath);

        ArrayList<ConfigService> lstConfigService = ConfigServiceDAO.loadList();
        ServletContext servletContext = sce.getServletContext();
        servletContext.setAttribute("configServiceMap", createConfigServiceMap(lstConfigService));

        initServices();
    }

    /**
     *
     * @param sce
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if(!Objects.isNull(pushNotificationService)){
            pushNotificationService.shutdown();
        }
    }

    private void initServices(){
        Config pnService = ConfigDAO.load(PushNotificationConfiguration.ENABLE_PUSHNOTIFICATION.getValue());
        Config pnServiceInterval = ConfigDAO.load(PushNotificationConfiguration.PUSHNOTIFICATION_SERVICE_INTERVAL.getValue());

        pushNotificationService = Executors.newSingleThreadScheduledExecutor();
        if (pnService.getConfValue().equals("1")) {
            pushNotificationService.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    NotificationServiceController.start();
                }
            }, 0, Integer.valueOf(pnServiceInterval.getConfValue()), TimeUnit.SECONDS);
        }
    }
    
    private Map<Integer, ConfigService> createConfigServiceMap(ArrayList<ConfigService> lstConfigService) {
        Map<Integer, ConfigService> map = new HashMap<>();
        for (ConfigService configService : lstConfigService) {
            map.put(configService.getConfigServiceId(), configService);
        }
        return map;
    }

}
