/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oc.services.retailerapp;

import com.oc.services.entity.ServiceSecurity;
import com.oc.services.enums.Security;
import java.io.IOException;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import org.oc.db.controller.AuditTrailDAO;
import org.oc.db.controller.GenTypedetailDAO;
import org.oc.db.controller.UserDeviceinformationDAO;
import org.oc.db.controller.UserInformationDAO;
import org.oc.db.entity.GenTypedetail;
import org.oc.db.entity.UserDeviceinformation;
import org.oc.db.entity.UserInformation;
import org.oc.db.entity.enums.AppType;
import org.oc.db.entity.enums.AuditType;
import org.oc.db.entity.enums.RoleType;

/**
 *
 * @author Clc
 */
@Provider
public class AuthenticationFilter implements javax.ws.rs.container.ContainerRequestFilter {

    @Context
    private ResourceInfo resourceInfo;
    @Context
    private HttpServletRequest servletRequest;

    private static final String AUTHORIZATION_PROPERTY = "authorization";
    private static final String PUSHNOTIFICATION_TOKEN = "token";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        try {
            Method method = resourceInfo.getResourceMethod();
            final MultivaluedMap<String, String> headers = requestContext.getHeaders();
            final List<String> authorization = headers.get(AUTHORIZATION_PROPERTY);
            final List<String> notificationToken = headers.get(PUSHNOTIFICATION_TOKEN);

            if (method.isAnnotationPresent(ServiceSecurity.class) && Objects.equals(method.getAnnotation(ServiceSecurity.class).security(), Security.BY_PASS)) {
                return;
            } else if (method.isAnnotationPresent(ServiceSecurity.class) && Objects.equals(method.getAnnotation(ServiceSecurity.class).security(), Security.RESTRICTED) && (authorization == null || authorization.isEmpty())) {
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("Username or password incorrect").build());
                return;
            } else if (method.isAnnotationPresent(ServiceSecurity.class) && Objects.equals(method.getAnnotation(ServiceSecurity.class).security(), Security.BOTH) && (authorization == null || authorization.isEmpty())) {
                return;
            } else if (authorization == null || authorization.isEmpty()) {
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("Username or password incorrect").build());
                return;
            }

            final String authorizationText = authorization.get(0);
            final StringTokenizer tokenizer = new StringTokenizer(authorizationText, ":");
            final String username = tokenizer.nextToken();
            final String password = tokenizer.nextToken();

            final UserInformation user = UserInformationDAO.authenticateUser(username, password, RoleType.OUTLET.id);

            if (Objects.isNull(user)) {
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("Username or password incorrect").build());
                return;
            }

            try {
                if (!Objects.isNull(notificationToken) && !notificationToken.isEmpty()) {
                    final String tokenText = notificationToken.get(0);
                    final StringTokenizer pushnotificationTokenizer = new StringTokenizer(tokenText);
                    final String token = pushnotificationTokenizer.nextToken();
                    UserDeviceinformation ud = UserDeviceinformationDAO.load(null, token);
                    if (Objects.isNull(ud)) {
                        ud = new UserDeviceinformation();
                        ud.setUserInformationByUserId(user);
                        ud.setGenTypedetail(GenTypedetailDAO.build(AppType.RETAILER_APP.getId()));
                        ud.setToken(token);
                        ud.setIsActive(true);
                        UserDeviceinformationDAO.add(ud, user);
                    }else if(!Objects.equals(ud.getUserInformationByUserId().getUserId(), user.getUserId())){
                        ud.setUserInformationByUserId(user);
                        UserDeviceinformationDAO.modify(ud, user);
                    }
                }
            } catch (Exception e) {
                Logger.getLogger(AuthenticationFilter.class.getName()).log(Level.SEVERE, null, e);
            }

            try {
                String versionText = "";
                try {
                    final List<String> version = headers.get("version");
                    if(!Objects.isNull(version) && !version.isEmpty()){
                        versionText = version.get(0);
                    }
                } catch (Exception e) {
                }
                AuditTrailDAO.insertAuditLog(user.getUserId(), AuditType.RETAILER_APP_SERVICE_CALL.getId(), org.oc.db.controller.Utilities.getIpAddr(servletRequest.getRemoteAddr()), "version:"+versionText+",service:"+resourceInfo.getResourceClass().getName()+"."+resourceInfo.getResourceMethod().getName());
            } catch (Exception e) {
                Logger.getLogger(AuthenticationFilter.class.getName()).log(Level.SEVERE, null, e);
            }

            requestContext.setSecurityContext(new SecurityContext() {

                @Override
                public Principal getUserPrincipal() {
                    return user;
                }

                @Override
                public boolean isUserInRole(String string) {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public boolean isSecure() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public String getAuthenticationScheme() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
            });

        } catch (Exception e) {
            Logger.getLogger(AuthenticationFilter.class.getName()).log(Level.SEVERE, null, e);
            requestContext.abortWith(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("A problem occurrec while processing request.").build());
        }

    }

}
