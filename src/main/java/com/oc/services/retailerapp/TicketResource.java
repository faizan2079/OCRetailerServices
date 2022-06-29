/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oc.services.retailerapp;

import com.google.gson.Gson;
import com.oc.services.entity.ServiceSecurity;
import com.oc.services.entity.TicketDateGroup;
import com.oc.services.enums.Security;
import com.oc.services.enums.ServiceStatus;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import javax.sound.sampled.UnsupportedAudioFileException;
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
import org.apache.commons.io.FilenameUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.oc.db.controller.BrandInformationDAO;
import org.oc.db.controller.BrandVariantDetailDAO;
import org.oc.db.controller.ConfigDAO;
import org.oc.db.controller.DistInformationDAO;
import org.oc.db.controller.GenTypedetailDAO;
import org.oc.db.controller.ManInformationDAO;
import org.oc.db.controller.OrderDistmappingDAO;
import org.oc.db.controller.OutletComplainDAO;
import org.oc.db.controller.OutletComplaindetailDAO;
import org.oc.db.controller.OutletInformationDAO;
import org.oc.db.controller.Utilities;
import org.oc.db.entity.BrandInformation;
import org.oc.db.entity.BrandVariantdetail;
import org.oc.db.entity.Config;
import org.oc.db.entity.DistInformation;
import org.oc.db.entity.GenTypedetail;
import org.oc.db.entity.GenTypemaster;
import org.oc.db.entity.ManInformation;
import org.oc.db.entity.OrderDistmapping;
import org.oc.db.entity.OutletComplain;
import org.oc.db.entity.OutletComplaindetail;
import org.oc.db.entity.OutletInformation;
import org.oc.db.entity.UserInformation;
import org.oc.db.entity.enums.Configuration;
import org.oc.db.entity.enums.MasterType;
import org.oc.db.entity.enums.TicketActivityType;
import org.oc.db.entity.enums.TicketStatusType;

/**
 *
 * @author MUNIB DESHMUKH
 */
@Path("ticket")
public class TicketResource {

    @Context
    private ContainerRequestContext requestContext;
    private UserInformation loggedUser = null;

    @POST
    @Path("/retrieveTicketReasonList")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ServiceSecurity(security = Security.RESTRICTED)
    public Message retrieveTicketReasonList(Message requestMessage) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Message responseMessage = new Message();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();
        if (!Utilities.validateRequiredParameter(requestMessage.getTicketTypeId())) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameter is Missing").build());
        }
        Integer[] ticketTpId = new Integer[]{requestMessage.getTicketTypeId()};
        ArrayList<GenTypedetail> lstgGenTypedetails = GenTypedetailDAO.loadList(ticketTpId);
        if (lstgGenTypedetails.isEmpty()) {
            responseMessage.setStatus(ServiceStatus.NO_DATA_AVAILABLE.getValue());
            responseMessage.setStatusText(ServiceStatus.NO_DATA_AVAILABLE.getStatusText());
            return responseMessage;
        }
        responseMessage.setLstGenTypedetails(GenTypedetailDAO.clearSets(lstgGenTypedetails));

        responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
        responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());

        return responseMessage;
    }

    @POST
    @Path("/addTicket")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    @ServiceSecurity(security = Security.RESTRICTED)
    public Message addTicket(@FormDataParam("ticketAudioRecording") InputStream inputStreamTicketAudioRecording, @FormDataParam("ticketAudioRecording") FormDataContentDisposition ticketAudioRecordingData,
            @FormDataParam("ticketImage") InputStream inputStreamTicketImage, @FormDataParam("ticketImage") FormDataContentDisposition ticketImageData, @FormDataParam("requestMessage") String jsonString) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, IOException, UnsupportedEncodingException, NoSuchAlgorithmException {
        Message responseMessage = new Message();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();
        String result = jsonString.replaceAll("^[\"']+|[\"']+$", "");
        result = result.replaceAll("\\\\", "");
        Message requestMessage = new Gson().fromJson(result, Message.class);
        OutletComplain outletComplain = new OutletComplain();
        OutletInformation outlet = OutletInformationDAO.loadOutlet(loggedUser.getRelationId());
        String imageLocation = ConfigDAO.load(Configuration.IMAGE_PHYSICAL_PATH.getId()).getConfValue() + "outlet" + File.separator + "complain";
        String audioLocation = ConfigDAO.load(Configuration.AUDIO_PHYSICAL_PATH.getId()).getConfValue() + "outlet" + File.separator + "complain";
        String hashAudioFileName = "";
        String hashImageFileName = "";
        if (Objects.isNull(requestMessage.getOutletComplain())) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters are Missing").build());
        }
        if (Objects.isNull(requestMessage.getOutletComplain().getGenTypedetailByComplainTypeId())) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters are Missing").build());
        }
        if (Objects.isNull(requestMessage.getOutletComplain())) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters are Missing").build());
        }
        if (Objects.isNull(requestMessage.getOutletComplain().getGenTypedetailByComplainTypeId())) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters are Missing").build());
        }
        if (!Utilities.validateRequiredParameter(requestMessage.getOutletComplain().getGenTypedetailByComplainTypeId().getTypeDetailId())) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters are Missing").build());
        }
        outletComplain = requestMessage.getOutletComplain();
        GenTypedetail genTypedetail = GenTypedetailDAO.load(outletComplain.getGenTypedetailByComplainTypeId().getTypeDetailId());
        outletComplain.setTitle(genTypedetail.getTypeDetailName());
        if (!Objects.isNull(MasterType.getEnumElement(genTypedetail.getGenTypemaster().getTypeMasterId()))) {
            switch (MasterType.getEnumElement(genTypedetail.getGenTypemaster().getTypeMasterId())) {
                case PRODUCT_COMPLAINT_TICKET:
                    if (Objects.isNull(requestMessage.getOutletComplain().getManInformation())) {
                        throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters are Missing").build());
                    }
                    if (!Utilities.validateRequiredParameter(outletComplain.getManInformation().getManufacturerId())) {
                        throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters are Missing").build());
                    }
                    ManInformation manInformation = ManInformationDAO.load(outletComplain.getManInformation().getManufacturerId());
                    if (Objects.isNull(manInformation)) {
                        throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Incorect Parameter").build());
                    }
                    if (!Utilities.validateRequiredParameter(outletComplain.getBrandInformation().getBrandId())) {
                        throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters are Missing").build());
                    }
                    BrandInformation brandInformation = BrandInformationDAO.load(outletComplain.getBrandInformation().getBrandId(), outletComplain.getManInformation().getManufacturerId());
                    if (Objects.isNull(brandInformation)) {
                        throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Incorect Parameter").build());
                    }
                    outletComplain.setManInformation(manInformation);
                    outletComplain.setBrandInformation(brandInformation);
                    break;
                case SERVICE_COMPLAINT_TICKET:
                    if (Objects.isNull(requestMessage.getOutletComplain().getManInformation())) {
                        throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters are Missing").build());
                    }
                    if (!Utilities.validateRequiredParameter(outletComplain.getManInformation().getManufacturerId())) {
                        throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters are Missing").build());
                    }
                    manInformation = ManInformationDAO.load(outletComplain.getManInformation().getManufacturerId());
                    if (Objects.isNull(manInformation)) {
                        throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Incorect Parameter").build());
                    }
                    DistInformation distInformation = DistInformationDAO.load(manInformation.getManufacturerId(), outlet.getOutletId());
                    if (Objects.isNull(distInformation)) {
                        throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Incorect Parameter").build());
                    }
                    outletComplain.setManInformation(manInformation);
                    outletComplain.setDistInformation(distInformation);
                    break;
                case ORDER_COMPLAINT_TICKET:
                    if (Objects.isNull(requestMessage.getOutletComplain().getManInformation()) || Objects.isNull(outletComplain.getOrderDistmapping())) {
                        throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters are Missing").build());
                    }
                    if (!Utilities.validateRequiredParameter(outletComplain.getManInformation().getManufacturerId())) {
                        throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters are Missing").build());
                    }
                    OrderDistmapping orderDistmapping = OrderDistmappingDAO.load(outletComplain.getOrderDistmapping().getDistMappingId());
                    DistInformation distInfo = orderDistmapping.getDistInformation();
                    if (Objects.isNull(distInfo) || Objects.isNull(orderDistmapping)) {
                        throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Incorect Parameter").build());
                    }
                    manInformation = ManInformationDAO.load(outletComplain.getManInformation().getManufacturerId());
                    if (Objects.isNull(manInformation)) {
                        throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Incorect Parameter").build());
                    }
                    outletComplain.setManInformation(manInformation);
                    outletComplain.setDistInformation(distInfo);
                    outletComplain.setOrderInformation(orderDistmapping.getOrderInformation());
                    outletComplain.setOrderDistmapping(orderDistmapping);
                    break;
            }
        }
        //<editor-fold defaultstate="collapsed" desc="storeTicketAudioRecording">
        String audioFileNameWithExt = null;
        if (!Objects.isNull(inputStreamTicketAudioRecording) && !Objects.isNull(ticketAudioRecordingData)) {
            String locationStoreTicketAudioRecording = "";
            boolean isStoreTicketAudioRecording = false;
            if (!Objects.isNull(inputStreamTicketAudioRecording)) {
                if (hashAudioFileName.isEmpty()) {
                    hashAudioFileName = generateHashNameForFiles(outletComplain);
                }
                audioFileNameWithExt = hashAudioFileName + "." + FilenameUtils.getExtension(ticketAudioRecordingData.getFileName());
                locationStoreTicketAudioRecording = audioLocation + File.separator + audioFileNameWithExt;
                isStoreTicketAudioRecording = Utilities.uploadFile(audioLocation, locationStoreTicketAudioRecording, inputStreamTicketAudioRecording);
                if (!isStoreTicketAudioRecording) {
                    throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Problem Occured While Uploading Audio.").build());
                }
            }
        } else if (!Utilities.validateRequiredParameter(outletComplain.getDescription())) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters are Missing").build());
        }
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="storeTicketImage">
        String imageFileNameWithExt = "";
        if (!Objects.isNull(inputStreamTicketImage) && !Objects.isNull(ticketImageData)) {
            String locationStoreTicketImage = "";
            boolean isStoreTicketImage = false;
            if (!Objects.isNull(inputStreamTicketImage)) {
                if (hashImageFileName.isEmpty()) {
                    hashImageFileName = generateHashNameForFiles(outletComplain);
                }
                imageFileNameWithExt = hashImageFileName + "." + FilenameUtils.getExtension(ticketImageData.getFileName());
                locationStoreTicketImage = imageLocation + File.separator + imageFileNameWithExt;
                isStoreTicketImage = Utilities.uploadFile(imageLocation, locationStoreTicketImage, inputStreamTicketImage);
                if (!isStoreTicketImage) {
                    throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Problem Occured While Uploading Image.").build());
                }
            }
        }
//</editor-fold>
        outletComplain.setGenTypedetailByComplainStatusId(GenTypedetailDAO.load(TicketStatusType.NEW.getId()));
        outletComplain.setUserInformationByRequestedById(loggedUser);
        outletComplain.setOutletInformation(outlet);
        outletComplain.setImageFileName(imageFileNameWithExt);
        outletComplain.setAudioFileName(audioFileNameWithExt);
        outletComplain.setComplainDate(new Date());
        outletComplain.setIsActive(true);
        OutletComplainDAO.add(outletComplain, loggedUser);
        responseMessage.setAudioFileName(audioFileNameWithExt);
        responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
        responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());
        return responseMessage;
    }

    @POST
    @Path("/retrieveTicketList")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ServiceSecurity(security = Security.RESTRICTED)
    public Message retrieveTicketList(Message requestMessage) throws ParseException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Message responseMessage = new Message();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();
        Config audioBaseURL = ConfigDAO.load(Configuration.AUDIO_BASE_URL.getId());
        ArrayList<OutletComplain> lstOutletComplains = new ArrayList<>();

        if (Objects.isNull(requestMessage.getStartIndex())) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameter is Missing").build());
        }
        if (!Utilities.validateRequiredParameter(requestMessage.getTicketTypeId())) {
            lstOutletComplains = OutletComplainDAO.loadList(requestMessage.getStartIndex(), loggedUser.getRelationId());
        } else {
            lstOutletComplains = OutletComplainDAO.loadList(requestMessage.getStartIndex(), loggedUser.getRelationId(), requestMessage.getTicketTypeId());
        }

        if (lstOutletComplains.isEmpty()) {
            responseMessage.setStatus(ServiceStatus.NO_DATA_AVAILABLE.getValue());
            responseMessage.setStatusText(ServiceStatus.NO_DATA_AVAILABLE.getStatusText());
            return responseMessage;
        }

        responseMessage.setLstOutletComplains(OutletComplainDAO.clearSets(lstOutletComplains));
        responseMessage.setAudioBaseURL(audioBaseURL.getConfValue());

        if (lstOutletComplains.size() == Integer.parseInt(ConfigDAO.load(Configuration.MAX_RECORDS.getId()).getConfValue())) {
            responseMessage.setHasMoreData(true);
        } else {
            responseMessage.setHasMoreData(false);
        }

        responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
        responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());

        return responseMessage;
    }

    @POST
    @Path("/retrieveTicketDetail")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ServiceSecurity(security = Security.RESTRICTED)
    public Message retrieveTicketDetail(Message requestMessage) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Message responseMessage = new Message();
        Utilities util = new Utilities();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();
        Config audioBaseURL = ConfigDAO.load(Configuration.AUDIO_BASE_URL.getId());
        OutletInformation outlet = OutletInformationDAO.loadOutlet(loggedUser.getRelationId());
        ArrayList<TicketDateGroup> lstTicketDateGroups = new ArrayList<>();
        ArrayList<OutletComplaindetail> lstCompDetail = new ArrayList<>();
        if (!Utilities.validateRequiredParameter(requestMessage.getTicketId())) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameter is Missing").build());
        }

        ArrayList<OutletComplaindetail> lstOutletComplaindetail = OutletComplaindetailDAO.loadList(requestMessage.getTicketId().toString(), outlet.getOutletId());
        if (lstOutletComplaindetail.isEmpty()) {
            responseMessage.setStatus(ServiceStatus.NO_DATA_AVAILABLE.getValue());
            responseMessage.setStatusText(ServiceStatus.NO_DATA_AVAILABLE.getStatusText());
            return responseMessage;
        }
        OutletComplaindetailDAO.clearSets(lstOutletComplaindetail);
        String dateGroup = "";
        int iterationNo = 0;
        for (OutletComplaindetail outletComplaindetail : lstOutletComplaindetail) {
            String[] date = util.s1.format(outletComplaindetail.getInsertDate()).split(" ");
            if (!Objects.equals(dateGroup, date[0])) {
                if (iterationNo != 0) {
                    TicketDateGroup tdg = new TicketDateGroup(dateGroup, lstCompDetail);
                    lstTicketDateGroups.add(tdg);
                }
                dateGroup = date[0];
                lstCompDetail = new ArrayList<>();
            }
            lstCompDetail.add(outletComplaindetail);
            iterationNo++;

        }
        TicketDateGroup tdg = new TicketDateGroup(dateGroup, lstCompDetail);
        lstTicketDateGroups.add(tdg);

        //  responseMessage.setLstOutletComplaindetails();
        responseMessage.setLstTicketDateGroups(lstTicketDateGroups);
        responseMessage.setAudioBaseURL(audioBaseURL.getConfValue());

        responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
        responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());

        return responseMessage;
    }

    private String generateHashNameForFiles(OutletComplain oc) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        boolean duplicateHashFileName = true;
        int i = 0;
        StringBuilder stringBuilder = new StringBuilder(oc.getTitle()).append(new Date()).append(System.currentTimeMillis());
        String hashFileName = OutletComplainDAO.getHash(stringBuilder.toString());
        while (duplicateHashFileName) {
            i++;
            duplicateHashFileName = OutletComplainDAO.checkDuplicateFileName(hashFileName);
            if (duplicateHashFileName) {
                stringBuilder = new StringBuilder(oc.getTitle()).append(new Date()).append(System.currentTimeMillis()).append(i);
                hashFileName = OutletComplainDAO.getHash(stringBuilder.toString());
            }
        }
        return hashFileName;
    }

    private String generateHashNameForFiles(String key) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        boolean duplicateHashFileName = true;
        int i = 0;
        StringBuilder stringBuilder = new StringBuilder(key).append(new Date()).append(System.currentTimeMillis());
        String hashFileName = OutletComplainDAO.getHash(stringBuilder.toString());
        while (duplicateHashFileName) {
            i++;
            duplicateHashFileName = OutletComplainDAO.checkDuplicateFileName(hashFileName);
            if (duplicateHashFileName) {
                stringBuilder = new StringBuilder(key).append(new Date()).append(System.currentTimeMillis()).append(i);
                hashFileName = OutletComplainDAO.getHash(stringBuilder.toString());
            }
        }
        return hashFileName;
    }

    @POST
    @Path("/addTicketDetail")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    @ServiceSecurity(security = Security.RESTRICTED)
    public Message addTicketDetail(@FormDataParam("ticketAudioRecording") InputStream inputStreamTicketAudioRecording,
            @FormDataParam("ticketAudioRecording") FormDataContentDisposition ticketAudioRecordingData,
            @FormDataParam("requestMessage") String jsonString) throws UnsupportedEncodingException, NoSuchAlgorithmException, IOException, UnsupportedAudioFileException {
        Message responseMessage = new Message();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();
        Message requestMessage = new Gson().fromJson(jsonString, Message.class);
        if (!Utilities.validateRequiredParameter(requestMessage.getOutletComplaindetail().getOutletComplain().getComplainId())) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameter is Missing").build());
        }
        //<editor-fold defaultstate="collapsed" desc="storeTicketAudioRecording">
        String audioLocation = ConfigDAO.load(Configuration.AUDIO_PHYSICAL_PATH.getId()).getConfValue() + "outlet" + File.separator + "complain";
     //   String audioLocation = "C:\\application\\oc\\audio\\" + "outlet" + File.separator + "complain";

        String hashAudioFileName = "";
        String audioFileNameWithExt = "";
        if (!Objects.isNull(inputStreamTicketAudioRecording) && !Objects.isNull(ticketAudioRecordingData)) {
            String locationStoreTicketAudioRecording = "";
            boolean isStoreTicketAudioRecording = false;
            if (!Objects.isNull(inputStreamTicketAudioRecording)) {
                if (hashAudioFileName.isEmpty()) {
                    hashAudioFileName = generateHashNameForFiles(requestMessage.getOutletComplaindetail().getOutletComplain().getComplainId() + "");
                }
                audioFileNameWithExt = hashAudioFileName + "." + FilenameUtils.getExtension(ticketAudioRecordingData.getFileName());
                locationStoreTicketAudioRecording = audioLocation + File.separator + audioFileNameWithExt;
                isStoreTicketAudioRecording = Utilities.uploadFile(audioLocation, locationStoreTicketAudioRecording, inputStreamTicketAudioRecording);
                if (!isStoreTicketAudioRecording) {
                    throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Problem Occured While Uploading Audio.").build());
                }
            }
        } else if (!Utilities.validateRequiredParameter(requestMessage.getOutletComplaindetail().getDescription())) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters are Missing").build());
        }
        //</editor-fold>

        OutletComplaindetail outletComplaindetail = new OutletComplaindetail();
        if (Utilities.validateRequiredParameter(audioFileNameWithExt)) {
            outletComplaindetail.setAudioFileName(audioFileNameWithExt);
        }
        outletComplaindetail.setOutletComplain(requestMessage.getOutletComplaindetail().getOutletComplain());
        outletComplaindetail.setDescription(requestMessage.getOutletComplaindetail().getDescription());
        outletComplaindetail.setGenTypedetailByDetailTypeId(GenTypedetailDAO.load(TicketActivityType.NEW.getId()));
        outletComplaindetail.setPerformedDate(new Date());
        outletComplaindetail.setUserInformationByAssignedToId(loggedUser);
        outletComplaindetail.setUserInformationByInsertId(loggedUser);
        outletComplaindetail.setInsertDate(new Date());
        outletComplaindetail.setIsActive(true);

        OutletComplaindetailDAO.add(outletComplaindetail);

        responseMessage.setAudioFileName(outletComplaindetail.getAudioFileName());
        responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
        responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());

        return responseMessage;

    }
    
    @GET
    @Path("/getComplaintHead")
    @Produces(MediaType.APPLICATION_JSON)
    @ServiceSecurity(security = Security.RESTRICTED)
    public Message getComplaintHead(Message requestMessage) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, ParseException {
        Message responseMessage = new Message();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();
       
        ArrayList<GenTypemaster> lstComplaintHeads = null;

        lstComplaintHeads = OutletComplainDAO.getComplaintHeads();
        
        for(GenTypemaster master: lstComplaintHeads ){
            master.setGenTypedetails(null);
            master.setUserInformationByEditId(null);
            master.setUserInformationByInsertId(null);
        }
        
         Config config = ConfigDAO.load(Configuration.IMAGE_BASE_URL.getId());
            
        
        if (!lstComplaintHeads.isEmpty()) {
            responseMessage.setImageBaseURL(config.getConfValue());
            responseMessage.setLstComplaintHeads(lstComplaintHeads);
            responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
            responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());
        } else {
            responseMessage.setStatus(ServiceStatus.NO_DATA_AVAILABLE.getValue());
            responseMessage.setStatusText(ServiceStatus.NO_DATA_AVAILABLE.getStatusText());
            return responseMessage;
        }

        return responseMessage;
    }
    
    @GET
    @Path("/getComplaintHeadDetail")
    @Produces(MediaType.APPLICATION_JSON)
    @ServiceSecurity(security = Security.RESTRICTED)
    public Message getComplaintHeadDetail(@QueryParam("headId") Integer headId) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, ParseException {
        Message responseMessage = new Message();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();
        
        if (headId == null ) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameter is Missing").build());
        }
       
        ArrayList<GenTypedetail> lstComplaintHeadDetail = GenTypedetailDAO.loadList(new Integer[]{headId});         
  
       if (lstComplaintHeadDetail.isEmpty()) {
            responseMessage.setStatus(ServiceStatus.NO_DATA_AVAILABLE.getValue());
            responseMessage.setStatusText(ServiceStatus.NO_DATA_AVAILABLE.getStatusText());
            return responseMessage;
        }
        
        responseMessage.setLstGenTypedetails(GenTypedetailDAO.clearSets(lstComplaintHeadDetail));
        responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
        responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());
        return responseMessage;        
    }
    
    @POST
    @Path("/addComplaint")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    @ServiceSecurity(security = Security.RESTRICTED)
    public Message addComplaint(@FormDataParam("requestMessage") String jsonString) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, IOException, UnsupportedEncodingException, NoSuchAlgorithmException {
         
        Message responseMessage = new Message();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();
        String result = jsonString.replaceAll("^[\"']+|[\"']+$", "");
        result = result.replaceAll("\\\\", "");
        Message requestMessage = new Gson().fromJson(result, Message.class);
        
        OutletComplain outletComplain = new OutletComplain();        
        OutletInformation outlet = OutletInformationDAO.loadOutlet(loggedUser.getRelationId());
       
        if (Objects.isNull(requestMessage.getOutletComplain())) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters are Missing").build());
        }
        if (Objects.isNull(requestMessage.getOutletComplain().getGenTypedetailByComplainTypeId())) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters are Missing").build());
        }
       
        if (!Utilities.validateRequiredParameter(requestMessage.getOutletComplain().getGenTypedetailByComplainTypeId().getTypeDetailId())) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters are Missing").build());
        }
        
        outletComplain = requestMessage.getOutletComplain();
        GenTypedetail genTypedetail = GenTypedetailDAO.load(outletComplain.getGenTypedetailByComplainTypeId().getTypeDetailId());
        
        outletComplain.setTitle(genTypedetail.getTypeDetailName());
        
        if (!Objects.isNull(MasterType.getEnumElement(genTypedetail.getGenTypemaster().getTypeMasterId()))) {
            if( (MasterType.getEnumElement(genTypedetail.getGenTypemaster().getTypeMasterId()).toString().equalsIgnoreCase("PRODUCT_COMPLAINT"))) {
               
                    if (Objects.isNull(requestMessage.getOutletComplain().getManInformation())) {
                        throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters are Missing").build());
                    }
                    if (!Utilities.validateRequiredParameter(outletComplain.getManInformation().getManufacturerId())) {
                        throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters are Missing").build());
                    }
                    ManInformation manInformation = ManInformationDAO.load(outletComplain.getManInformation().getManufacturerId());
                    if (Objects.isNull(manInformation)) {
                        throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Incorect Parameter").build());
                    }
                    if (!Utilities.validateRequiredParameter(outletComplain.getBrandInformation().getBrandId())) {
                        throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters are Missing").build());
                    }
                    BrandInformation brandInformation = BrandInformationDAO.load(outletComplain.getBrandInformation().getBrandId(), outletComplain.getManInformation().getManufacturerId());
                    if (Objects.isNull(brandInformation)) {
                        throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Incorect Parameter").build());
                    }
                    
                     if (!Utilities.validateRequiredParameter(outletComplain.getBrandVariantdetail().getVariantDetailId())) {
                        throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters are Missing").build());
                    }
                    
                    BrandVariantdetail brandVariantDetail = BrandVariantDetailDAO.loadInfo(outletComplain.getBrandVariantdetail().getVariantDetailId().toString());
                    outletComplain.setManInformation(manInformation);
                    outletComplain.setBrandInformation(brandInformation);
                    outletComplain.setBrandVariantdetail(brandVariantDetail);
                
            }
        }

        outletComplain.setGenTypedetailByComplainStatusId(GenTypedetailDAO.load(TicketStatusType.NEW.getId()));
        outletComplain.setUserInformationByRequestedById(loggedUser);
        outletComplain.setOutletInformation(outlet);
        outletComplain.setComplainDate(new Date());
        outletComplain.setIsActive(true);
        OutletComplainDAO.add(outletComplain, loggedUser);
        responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
        responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());
        return responseMessage;
    }
}
