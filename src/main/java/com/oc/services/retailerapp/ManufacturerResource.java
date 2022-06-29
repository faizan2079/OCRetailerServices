/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oc.services.retailerapp;

import com.oc.services.entity.ServiceSecurity;
import com.oc.services.enums.Security;
import com.oc.services.enums.ServiceStatus;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Objects;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.oc.db.controller.BrandInformationDAO;
import org.oc.db.controller.ConfigDAO;
import org.oc.db.controller.DistInformationDAO;
import org.oc.db.controller.DistLocationmappingDAO;
import org.oc.db.controller.DistOutletmappingDAO;
import org.oc.db.controller.GenTypedetailDAO;
import org.oc.db.controller.ManInformationDAO;
import org.oc.db.controller.OutletInformationDAO;
import org.oc.db.controller.Utilities;
import org.oc.db.entity.BrandInformation;
import org.oc.db.entity.Config;
import org.oc.db.entity.DistInformation;
import org.oc.db.entity.DistLocationmapping;
import org.oc.db.entity.DistOutletmapping;
import org.oc.db.entity.GenTypedetail;
import org.oc.db.entity.ManInformation;
import org.oc.db.entity.OutletInformation;
import org.oc.db.entity.UserInformation;
import org.oc.db.entity.enums.Configuration;
import org.oc.db.entity.enums.OutletMappingStatus;

/**
 *
 * @author Clc
 */
@Path("manufacturer")
public class ManufacturerResource {

    @Context
    private ContainerRequestContext requestContext;
    private UserInformation loggedUser = null;

    @POST
    @Path("/retrieveList")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ServiceSecurity(security = Security.RESTRICTED)
    public Message retrieveList(Message requestMessage) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Message responseMessage = new Message();
        ArrayList<ManInformation> lstManInformation = null;
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();
        OutletInformation outlet = OutletInformationDAO.loadOutlet(loggedUser.getRelationId());

        if (Objects.isNull(requestMessage)) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameter is Missing").build());
        }
        lstManInformation = ManInformationDAO.loadList(requestMessage.getSearchText(), outlet.getGeoLocation().getLocationId(),outlet.getOutletId(), Boolean.TRUE);
        Config config = ConfigDAO.load(Configuration.IMAGE_BASE_URL.getId());

        if (!Objects.isNull(lstManInformation) && !lstManInformation.isEmpty()) {
            responseMessage.setLstManInformation(ManInformationDAO.clearSets(lstManInformation));
            responseMessage.setImageBaseURL(config.getConfValue());

            responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
            responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());
        } else {

            responseMessage.setStatus(ServiceStatus.NO_DATA_AVAILABLE.getValue());
            responseMessage.setStatusText(ServiceStatus.NO_DATA_AVAILABLE.getStatusText());
            return responseMessage;

        }

        return responseMessage;
    }

    @POST
    @Path("/retrieveBrandList")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ServiceSecurity(security = Security.RESTRICTED)
    public Message retrieveBrandList(Message requestMessage) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, ParseException {
        Message responseMessage = new Message();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();
        OutletInformation outlet = OutletInformationDAO.loadOutlet(loggedUser.getRelationId());

        if (Objects.isNull(requestMessage)) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameter is Missing").build());
        }
        if (!Utilities.validateRequiredParameter(requestMessage.getManufactureId())) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameter is Missing").build());
        }
        ArrayList<BrandInformation> lstbBrandInformation = BrandInformationDAO.loadList(requestMessage.getManufactureId(), outlet.getGeoLocation().getLocationId(),outlet.getOutletId());
        Config config = ConfigDAO.load(Configuration.IMAGE_BASE_URL.getId());

        if (!Objects.isNull(lstbBrandInformation) && !lstbBrandInformation.isEmpty()) {
            responseMessage.setLstBrandInformation(BrandInformationDAO.clearSets(lstbBrandInformation));
            responseMessage.setImageBaseURL(config.getConfValue());

            responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
            responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());
            return responseMessage;
        } else {
            responseMessage.setStatus(ServiceStatus.NO_DATA_AVAILABLE.getValue());
            responseMessage.setStatusText(ServiceStatus.NO_DATA_AVAILABLE.getStatusText());
            return responseMessage;
        }

    }

    @POST
    @Path("/retrieveOutletMappingList")
    @Produces(MediaType.APPLICATION_JSON)
    @ServiceSecurity(security = Security.RESTRICTED)
    public Message retrieveOutletMappingList() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Message responseMessage = new Message();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();
        OutletInformation outlet = OutletInformationDAO.loadOutlet(loggedUser.getRelationId());

        ArrayList<ManInformation> lstManInformation = ManInformationDAO.loadList(outlet.getOutletId(), outlet.getGeoLocation().getLocationId(), Boolean.TRUE);
        Config config = ConfigDAO.load(Configuration.IMAGE_BASE_URL.getId());
        if (!Objects.isNull(lstManInformation) && !lstManInformation.isEmpty()) {
            responseMessage.setLstManInformation(ManInformationDAO.clearSets(lstManInformation));
            responseMessage.setImageBaseURL(config.getConfValue());
            responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
            responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());
            return responseMessage;
        } else {
            responseMessage.setStatus(ServiceStatus.NO_DATA_AVAILABLE.getValue());
            responseMessage.setStatusText(ServiceStatus.NO_DATA_AVAILABLE.getStatusText());
            return responseMessage;
        }

    }

    @POST
    @Path("/addOutletMappings")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ServiceSecurity(security = Security.RESTRICTED)
    public Message addOutletMappings(Message requestMessage) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Message responseMessage = new Message();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();
        OutletInformation outlet = OutletInformationDAO.loadOutlet(loggedUser.getRelationId());
        ArrayList<DistOutletmapping> lstDistOutletmappings = new ArrayList<>();
        if (Objects.isNull(requestMessage) || Objects.isNull(requestMessage.getLstDistOutletmappings())) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters are Missing").build());
        }

        for (DistOutletmapping distOutletmapping : requestMessage.getLstDistOutletmappings()) {

            if (!Utilities.validateRequiredParameter(distOutletmapping.getDistInformation().getDistributerId()) || !Utilities.validateRequiredParameter(distOutletmapping.getExternalOutletId())) {
                throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters are Missing").build());
            }
            DistInformation distInformation = DistInformationDAO.load(distOutletmapping.getDistInformation().getDistributerId());
            ArrayList<DistLocationmapping> lstDistLocationmapping = DistLocationmappingDAO.loadList(null, null, outlet.getGeoLocation().getLocationId().toString(), distInformation);
            if (lstDistLocationmapping.isEmpty()) {
                throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("distributor does not provide service in the requested outlets area.").build());
            }
            DistOutletmapping dom = new DistOutletmapping();
            dom.setDistInformation(distInformation);
            dom.setOutletInformation(outlet);
            if (DistOutletmappingDAO.checkDuplicate(dom)) {
                responseMessage.setStatus(ServiceStatus.DUPLICATE_CHECK_FAILED.getValue());
                responseMessage.setStatusText(ServiceStatus.DUPLICATE_CHECK_FAILED.getStatusText());
                return responseMessage;
            }
            if (!Objects.isNull(distInformation.getOutletCodeRegex()) && !Objects.equals(distInformation.getOutletCodeRegex(), "")) {
                if (!distOutletmapping.getExternalOutletId().matches(distInformation.getOutletCodeRegex())) {
                    responseMessage.setStatus(ServiceStatus.DATA_FORMAT_VALIDATION_FAILED.getValue());
                    responseMessage.setStatusText(ServiceStatus.DATA_FORMAT_VALIDATION_FAILED.getStatusText());
                    return responseMessage;
                }
            }
            dom.setExternalOutletId(distOutletmapping.getExternalOutletId());
            GenTypedetail statusType = GenTypedetailDAO.load(OutletMappingStatus.PENDING.getId());
            dom.setGenTypedetail(statusType);
            dom.setIsActive(true);
            lstDistOutletmappings.add(dom);
        }

        DistOutletmappingDAO.add(lstDistOutletmappings);

        responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
        responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());
        return responseMessage;
    }

}
