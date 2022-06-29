/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oc.controller;

import java.text.ParseException;
import java.util.Date;
import java.util.Objects;
import javax.ws.rs.core.CacheControl;
import org.oc.db.controller.Utilities;
import org.oc.db.entity.ConfigService;
import org.oc.db.entity.enums.ServiceTypes;

/**
 *
 * @author DELL PRECISION M6800
 */
public class CacheController {

    public CacheController() {
    }

    public CacheControl createCacheControl(ConfigService configService) throws ParseException {
        CacheControl cacheControl = new CacheControl();
        Utilities util = new Utilities();
        long timeDiff;
        String currentTime = util.s2.format(new Date());
        Date currentDate = util.s2.parse(currentTime);

        if (!Objects.isNull(configService.getGenTypedetail())) {
            if (Objects.equals(configService.getGenTypedetail().getTypeDetailId(), ServiceTypes.AGE_IN_SECONDS.getId())) {
                cacheControl.setMaxAge(configService.getMaxAgeInSec().intValue());
                cacheControl.setSMaxAge(configService.getSmaxAge().intValue());
            } else {
                timeDiff = 0;
                timeDiff = configService.getMaxAgeCutOffTime().getTime() - currentDate.getTime();
                cacheControl.setMaxAge(((Long) ((timeDiff > 0 ? timeDiff : 0) / 1000)).intValue());
                timeDiff = 0;
                timeDiff = configService.getSmaxAgeCutOffTime().getTime() - currentDate.getTime();
                cacheControl.setSMaxAge(((Long) ((timeDiff > 0 ? timeDiff : 0) / 1000)).intValue());
            }
        } else {
            if (!Objects.isNull(configService.getMaxAgeInSec())) {
                cacheControl.setMaxAge(configService.getMaxAgeInSec().intValue());
            } else if (!Objects.isNull(configService.getMaxAgeCutOffTime())) {
                timeDiff = 0;
                timeDiff = configService.getMaxAgeCutOffTime().getTime() - currentDate.getTime();
                cacheControl.setMaxAge(((Long) ((timeDiff > 0 ? timeDiff : 0) / 1000)).intValue());
            }

            if (!Objects.isNull(configService.getSmaxAge())) {
                cacheControl.setSMaxAge(configService.getSmaxAge().intValue());
            } else if (!Objects.isNull(configService.getSmaxAgeCutOffTime())) {
                timeDiff = 0;
                timeDiff = configService.getSmaxAgeCutOffTime().getTime() - currentDate.getTime();
                cacheControl.setSMaxAge(((Long) ((timeDiff > 0 ? timeDiff : 0) / 1000)).intValue());
            }

        }

        if (!Objects.isNull(configService.getMustRevalidate())) {
            cacheControl.setMustRevalidate(configService.getMustRevalidate());
        }

        if (!Objects.isNull(configService.getProxyRevalidate())) {
            cacheControl.setProxyRevalidate(configService.getProxyRevalidate());
        }

        if (!Objects.isNull(configService.getNoCache())) {
            cacheControl.setNoCache(configService.getNoCache());
        }

        if (!Objects.isNull(configService.getNoTransform())) {
            cacheControl.setNoTransform(configService.getNoTransform());
        }

        if (!Objects.isNull(configService.getNoStore())) {
            cacheControl.setNoStore(configService.getNoStore());
        }

        if (!Objects.isNull(configService.getPublic_())) {
            cacheControl.setPrivate(!configService.getPublic_());
        }

        if (!Objects.isNull(configService.getPrivate_())) {
            cacheControl.setPrivate(configService.getPrivate_());
        }

        return cacheControl;
    }
}
