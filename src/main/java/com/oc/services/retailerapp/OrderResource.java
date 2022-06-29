/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oc.services.retailerapp;

import com.oc.controller.CacheController;
import com.oc.services.entity.ServiceSecurity;
import org.oc.db.entity.enums.ConfigurationService;
import com.oc.services.enums.OrderStatusTypeService;
import com.oc.services.enums.Security;
import com.oc.services.enums.ServiceStatus;
import com.oc.services.enums.SortFieldTypeService;
import com.oc.services.enums.SortOrderTypeService;
import com.oc.services.entity.ComplaintResponseDto;
import com.oc.services.response.ComplaintResponse;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.servlet.ServletContext;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Context;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.joda.time.DateTime;
import org.oc.db.controller.BrandCategoryDAO;
import org.oc.db.controller.BrandGroupDAO;
import org.oc.db.controller.BrandInformationDAO;
import org.oc.db.controller.BrandProductDAO;
import org.oc.db.controller.BrandVariantDAO;
import org.oc.db.controller.BrandVariantDetailDAO;
import org.oc.db.controller.ConfigAppDAO;
import org.oc.db.controller.ConfigDAO;
import org.oc.db.controller.DistBrandmappingDAO;
import org.oc.db.controller.DistInformationDAO;
import org.oc.db.controller.DistPolygonDAO;
import org.oc.db.controller.GenTypedetailDAO;
import org.oc.db.controller.ManInformationDAO;
import org.oc.db.controller.OrderDetailDAO;
import org.oc.db.controller.OrderDistmappingDAO;
import org.oc.db.controller.OrderInformationDAO;
import org.oc.db.controller.OutletInformationDAO;
import org.oc.db.controller.TpDetailDAO;
import org.oc.db.controller.UserActivityDAO;
import org.oc.db.controller.UserInformationDAO;
import org.oc.db.controller.Utilities;
import org.oc.db.entity.BrandCategory;
import org.oc.db.entity.BrandGroup;
import org.oc.db.entity.BrandInformation;
import org.oc.db.entity.BrandProduct;
import org.oc.db.entity.BrandVariantdetail;
import org.oc.db.entity.ConfigApp;
import org.oc.db.entity.ConfigService;
import org.oc.db.entity.DistBrandmapping;
import org.oc.db.entity.GenTypedetail;
import org.oc.db.entity.ManInformation;
import org.oc.db.entity.OrderDetail;
import org.oc.db.entity.OrderDistmapping;
import org.oc.db.entity.OrderInformation;
import org.oc.db.entity.OutletInformation;
import org.oc.db.entity.TpDetail;
import org.oc.db.entity.UserActivity;
import org.oc.db.entity.UserInformation;
import org.oc.db.entity.enums.ActivityStatusType;
import org.oc.db.entity.enums.ConfigAppType;
import org.oc.db.entity.enums.Configuration;
import org.oc.db.entity.enums.OrderItemType;
import org.oc.db.entity.enums.OrderStatus;
import org.oc.db.entity.enums.OrderType;
import org.oc.db.entity.enums.OutletActivityType;
import org.oc.db.entity.DistDeliveryslot;
import org.oc.db.controller.OrderTrackingDAO;
import org.oc.db.entity.Config;
import org.oc.db.entity.DistPolygon;
import org.oc.db.entity.OrderActivity;
import org.oc.db.entity.OrderTracking;
import org.oc.db.entity.enums.ApplicationType;
import org.oc.db.entity.enums.MasterType;
import com.oc.services.response.OrderList;
import com.oc.services.response.OrderListResponse;

/**
 * REST Web Service
 *
 * @author DELL PRECISION M6800
 */
@Path("order")
public class OrderResource {

    @Context
    private ContainerRequestContext requestContext;
    private UserInformation loggedUser = null;
    @Context
    private ServletContext servletContext;

    public OrderResource() {
    }

    @POST
    @Path("/getManufactureList")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ServiceSecurity(security = Security.RESTRICTED)
    public Message getManufactureList(Message requestMessage) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Message responseMessage = new Message();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();
        OutletInformation outlet = OutletInformationDAO.loadOutlet(loggedUser.getRelationId());

        ArrayList<ManInformation> lstManInformations = ManInformationDAO.loadList(outlet.getGeoLocation().getLocationId(), outlet.getOutletId());
        if (lstManInformations.size() > 0) {
            lstManInformations = ManInformationDAO.clearSets(lstManInformations);
        }

        if (lstManInformations.isEmpty()) {
            responseMessage.setStatus(ServiceStatus.NO_DATA_AVAILABLE.getValue());
            responseMessage.setStatusText(ServiceStatus.NO_DATA_AVAILABLE.getStatusText());
            return responseMessage;
        }

        responseMessage.setLstManInformation(lstManInformations);
        responseMessage.setImageBaseURL(ConfigDAO.load(Configuration.IMAGE_BASE_URL.getId()).getConfValue());

        responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
        responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());

        return responseMessage;

    }

    @GET
    @Path("/getCategoryList")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ServiceSecurity(security = Security.RESTRICTED)
    public Message getCateoryList() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Message responseMessage = new Message();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();
        OutletInformation outlet = OutletInformationDAO.loadOutlet(loggedUser.getRelationId());

        ArrayList<BrandCategory> lstBrandCategory = BrandCategoryDAO.loadList(outlet.getGeoLocation().getLocationId(), outlet.getOutletId());
        if (lstBrandCategory.size() > 0) {
            lstBrandCategory = BrandCategoryDAO.clearSets(lstBrandCategory);
        }

        if (lstBrandCategory.isEmpty()) {
            responseMessage.setStatus(ServiceStatus.NO_DATA_AVAILABLE.getValue());
            responseMessage.setStatusText(ServiceStatus.NO_DATA_AVAILABLE.getStatusText());
            return responseMessage;
        }

        responseMessage.setLstBrandCategory(lstBrandCategory);
        responseMessage.setImageBaseURL(ConfigDAO.load(Configuration.IMAGE_BASE_URL.getId()).getConfValue());

        responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
        responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());

        return responseMessage;

    }

    @POST
    @Path("/getBrandList")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ServiceSecurity(security = Security.RESTRICTED)
    public Response getBrandList(Message requestMessage) throws ParseException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        CacheControl cc = new CacheControl();
        cc.setMaxAge(60);
        cc.setPrivate(true);
        cc.setNoStore(true);
        Message responseMessage = new Message();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();
        OutletInformation outlet = OutletInformationDAO.loadOutlet(loggedUser.getRelationId());

        if (Objects.isNull(requestMessage)) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        }

        if (!Objects.isNull(requestMessage)) {
            if (!Objects.isNull(requestMessage.getCategoryId())) {
                if (!Utilities.validateRequiredParameter(requestMessage.getCategoryId())) {
                    throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
                }
            }
            if (!Objects.isNull(requestMessage.getManufactureId())) {
                if (!Utilities.validateRequiredParameter(requestMessage.getManufactureId())) {
                    throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
                }
            }
            if (!Objects.isNull(requestMessage.getSearchText())) {
                if (!Utilities.validateRequiredParameter(requestMessage.getSearchText())) {
                    throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
                }
            }
            if (!Objects.isNull(requestMessage.getBrandGroupId())) {
                if (!Utilities.validateRequiredParameter(requestMessage.getBrandGroupId())) {
                    throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
                }
            }
        }

        if (Objects.isNull(requestMessage.getStartIndex())) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        }

        ArrayList<BrandInformation> lstRows = BrandInformationDAO.loadList(
                requestMessage.getCategoryId(),
                requestMessage.getManufactureId(), requestMessage.getProductId(), Objects.isNull(requestMessage.getSearchText()) ? "" : requestMessage.getSearchText(), outlet.getGeoLocation().getLocationId(), requestMessage.getPromotionAvailable(), requestMessage.getStartIndex(), outlet.getOutletId(), requestMessage.getBrandGroupId());

        ArrayList<BrandInformation> lstBrandInformation = new ArrayList<>();
        if (lstRows.size() > 0) {
            lstBrandInformation = BrandInformationDAO.clearSets(lstRows);
        }

        if (lstBrandInformation.isEmpty()) {
            responseMessage.setStatus(ServiceStatus.NO_DATA_AVAILABLE.getValue());
            responseMessage.setStatusText(ServiceStatus.NO_DATA_AVAILABLE.getStatusText());
            Response.ResponseBuilder builder = Response.ok(responseMessage, "application/json");
            builder.cacheControl(cc);
            return builder.build();
//            return responseMessage;
        }

        if (lstRows.size() == Integer.parseInt(ConfigDAO.load(Configuration.MAX_RECORDS.getId()).getConfValue())) {
            responseMessage.setHasMoreData(true);
        } else {
            responseMessage.setHasMoreData(false);
        }

        responseMessage.setImageBaseURL(ConfigDAO.load(Configuration.IMAGE_BASE_URL.getId()).getConfValue());
        responseMessage.setLstBrandInformation(lstBrandInformation);

        responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
        responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());

        Response.ResponseBuilder builder = Response.ok(responseMessage, "application/json");
        builder.cacheControl(cc);
        return builder.build();

    }

    @POST
    @Path("/getSubCategoryList")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ServiceSecurity(security = Security.RESTRICTED)
    public Message getSubCateoryList(Message requestMessage) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Message responseMessage = new Message();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();
        OutletInformation outlet = OutletInformationDAO.loadOutlet(loggedUser.getRelationId());
        Integer categoryId = null;
        String searchText = null;
//        if (Objects.isNull(requestMessage)) {
//            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
//        }
        if (!Objects.isNull(requestMessage)) {
            if (!Objects.isNull(requestMessage.getCategoryId())) {
                if (!Utilities.validateRequiredParameter(requestMessage.getCategoryId())) {
                    throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
                } else {
                    categoryId = requestMessage.getCategoryId();
                }
            }
            if (!Objects.isNull(requestMessage.getSearchText())) {
                if (!Utilities.validateRequiredParameter(requestMessage.getSearchText())) {
                    throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
                } else {
                    searchText = requestMessage.getSearchText();
                }
            }
        }

        ArrayList<BrandProduct> lstBrandProduct = BrandProductDAO.loadList(outlet.getGeoLocation().getLocationId(), categoryId, Objects.isNull(searchText) ? "" : requestMessage.getSearchText(), outlet.getOutletId());

        if (lstBrandProduct.size() > 0) {
            lstBrandProduct = BrandProductDAO.clearSets(lstBrandProduct);
        }

        if (lstBrandProduct.isEmpty()) {
            responseMessage.setStatus(ServiceStatus.NO_DATA_AVAILABLE.getValue());
            responseMessage.setStatusText(ServiceStatus.NO_DATA_AVAILABLE.getStatusText());
            return responseMessage;
        }

        responseMessage.setLstBrandProduct(lstBrandProduct);
        responseMessage.setImageBaseURL(ConfigDAO.load(Configuration.IMAGE_BASE_URL.getId()).getConfValue());

        responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
        responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());

        return responseMessage;

    }

    @POST
    @Path("/getSkuList")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ServiceSecurity(security = Security.RESTRICTED)
    public Message getSkuList(Message requestMessage) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, ParseException {
        Message responseMessage = new Message();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();
        OutletInformation outlet = OutletInformationDAO.loadOutlet(loggedUser.getRelationId());

        if (Objects.isNull(requestMessage)) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        }

        if (!Utilities.validateRequiredParameter(requestMessage.getBrandId())) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        }

        if (!Objects.isNull(requestMessage.getPromotionAvailable())) {
            if (requestMessage.getPromotionAvailable() != true && requestMessage.getPromotionAvailable() != false) {
                throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
            }
        } else {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        }

        if (!Objects.isNull(requestMessage.getSearchText())) {
            if (!Utilities.validateRequiredParameter(requestMessage.getSearchText())) {
                throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
            }
        }

        if (!Objects.isNull(requestMessage.getBrandVariantDetailId())) {
            if (!Utilities.validateRequiredParameter(requestMessage.getBrandVariantDetailId())) {
                throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
            }
        }

        ArrayList<BrandVariantdetail> lstBrandVariantDetail = BrandVariantDetailDAO.loadBrandVariantDetailList(requestMessage.getBrandId(), requestMessage.getPromotionAvailable(), requestMessage.getSearchText(), outlet.getGeoLocation().getLocationId(), outlet.getOutletId(), requestMessage.getBrandVariantDetailId());

        ArrayList<Integer> skuIds = new ArrayList<>();
        ArrayList<TpDetail> lstTpDetail = new ArrayList<>();
        if (lstBrandVariantDetail.size() > 0) {
            for (BrandVariantdetail sku : lstBrandVariantDetail) {
                skuIds.add(sku.getVariantDetailId());
            }
            if (skuIds.size() > 0) {
                lstTpDetail = TpDetailDAO.loadList(skuIds.toArray(new Integer[0]), outlet.getGeoLocation().getLocationId());
            }
            if (!Objects.isNull(requestMessage.getVersionNumber())) {
                ArrayList<DistBrandmapping> lstBrandDistributors = DistBrandmappingDAO.loadSkusDistributers(skuIds.toArray(new Integer[0]), outlet.getGeoLocation().getLocationId(), outlet.getOutletId());
                String skuIdsCsv = getVariantDetailIds(lstBrandVariantDetail);
                String distIdsCsv = getDistIds(lstBrandDistributors);
                ArrayList<Object[]> lstSkusAvailability = BrandVariantDetailDAO.loadSkusAvailablility(skuIdsCsv, distIdsCsv);

                for (BrandVariantdetail brandVariantdetail : lstBrandVariantDetail) {
                    boolean flag = true;
                    for (Object[] object : lstSkusAvailability) {
                        if (!Objects.isNull(object)) {
                            if (!Objects.isNull(object[0]) && Integer.parseInt(object[0].toString()) == brandVariantdetail.getVariantDetailId()) {
                                brandVariantdetail.setAvailableCount(Objects.isNull(object[3]) ? 0.0 : Double.valueOf(object[3].toString()));
                                flag = false;
                            }
                        }
                    }
                    if (flag) {
                        brandVariantdetail.setAvailableCount(0.0);
                    }
                }
            }
            if (lstTpDetail.size() > 0) {
                for (BrandVariantdetail brandVariantDetail : lstBrandVariantDetail) {
                    ArrayList<TpDetail> lstAllPromotions = new ArrayList<>();
                    boolean flag = false;
                    for (TpDetail tpDetail : lstTpDetail) {
                        if (tpDetail.getTpInformation().getBrandVariantdetail().getVariantDetailId().equals(brandVariantDetail.getVariantDetailId())) {
                            //brandVariantDetail.setLstAllPromotions(lstTpDetail);
                            if (!Objects.isNull(requestMessage.getVersionNumber())) {
                                if (brandVariantDetail.getAvailableCount() >= tpDetail.getOrderQty()
                                        && brandVariantDetail.getMaximumOrderQuantity() >= tpDetail.getOrderQty()) {
                                    lstAllPromotions.add(tpDetail);
                                    flag = true;
                                }
                            } else {
                                lstAllPromotions.add(tpDetail);
                                flag = true;
                            }
                        }
                    }
                    if (flag) {
                        brandVariantDetail.setLstAllPromotions(lstAllPromotions);
                    }
                }
            }

            lstBrandVariantDetail = BrandVariantDetailDAO.clearSets(lstBrandVariantDetail);
        }

        if (lstBrandVariantDetail.isEmpty()) {
            responseMessage.setStatus(ServiceStatus.NO_DATA_AVAILABLE.getValue());
            responseMessage.setStatusText(ServiceStatus.NO_DATA_AVAILABLE.getStatusText());
            return responseMessage;
        }

        responseMessage.setLstBrandVariantDetail(lstBrandVariantDetail);
        responseMessage.setImageBaseURL(ConfigDAO.load(Configuration.IMAGE_BASE_URL.getId()).getConfValue());

        responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
        responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());

        return responseMessage;
    }

    @POST
    @Path("/getMCSBList")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ServiceSecurity(security = Security.RESTRICTED)
    public Message getMCSBList(Message requestMessage) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, ParseException {
        Message responseMessage = new Message();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();

        Integer outletId = loggedUser.getRelationId();
        OutletInformation outlet = null;

        if (Objects.isNull(requestMessage)) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        }

        if (!Utilities.validateRequiredParameter(requestMessage.getSearchText())) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        }

        if (!Objects.isNull(outletId)) {
            outlet = OutletInformationDAO.load(outletId);
        }

        ArrayList<ManInformation> lstManInformation = ManInformationDAO.loadManList(requestMessage.getSearchText(), outlet.getGeoLocation().getLocationId(), outletId);
        ArrayList<BrandInformation> lstBrandInformation = BrandInformationDAO.loadList(requestMessage.getSearchText(), outlet.getGeoLocation().getLocationId(), outletId);
        ArrayList<BrandCategory> lstBrandCategory = BrandCategoryDAO.loadlist(requestMessage.getSearchText(), outlet.getGeoLocation().getLocationId(), outletId);
        ArrayList<BrandProduct> lstBrandProduct = BrandProductDAO.loadList(requestMessage.getSearchText(), outlet.getGeoLocation().getLocationId(), outletId);

        if (!lstManInformation.isEmpty()) {
            lstManInformation = ManInformationDAO.clearSets(lstManInformation);
        }

        if (!lstBrandInformation.isEmpty()) {
            lstBrandInformation = BrandInformationDAO.clearSets(lstBrandInformation);
        }

        if (!lstBrandCategory.isEmpty()) {
            lstBrandCategory = BrandCategoryDAO.clearSets(lstBrandCategory);
        }

        if (!lstBrandProduct.isEmpty()) {
            lstBrandProduct = BrandProductDAO.clearSets(lstBrandProduct);
        }

        if (lstBrandCategory.isEmpty() && lstBrandInformation.isEmpty() && lstBrandProduct.isEmpty() && lstManInformation.isEmpty()) {
            responseMessage.setStatus(ServiceStatus.NO_DATA_AVAILABLE.getValue());
            responseMessage.setStatusText(ServiceStatus.NO_DATA_AVAILABLE.getStatusText());
            return responseMessage;
        }

        responseMessage.setLstManInformation(lstManInformation);
        responseMessage.setLstBrandInformation(lstBrandInformation);
        responseMessage.setLstBrandCategory(lstBrandCategory);
        responseMessage.setLstBrandProduct(lstBrandProduct);
        responseMessage.setImageBaseURL(ConfigDAO.load(Configuration.IMAGE_BASE_URL.getId()).getConfValue());

        responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
        responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());

        return responseMessage;

    }

    @POST
    @Path("/getFrequentSkuList")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ServiceSecurity(security = Security.RESTRICTED)
    public Message getFrequentSkuList(Message requestMessage) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, ParseException {
        Message responseMessage = new Message();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();

        Integer outletId = loggedUser.getRelationId();
        OutletInformation outlet = null;
        Integer areaId = null;
        Integer townId = null;
        Integer cityId = null;
        Integer tradeCategoryId = null;

        if (Objects.isNull(requestMessage)) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        }

        if (!Objects.isNull(requestMessage.getSearchText())) {
            if (!Utilities.validateRequiredParameter(requestMessage.getSearchText())) {
                throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
            }
        }

        if (Objects.isNull(requestMessage.getStartIndex())) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        }

        if (!Objects.isNull(outletId)) {
            outlet = OutletInformationDAO.load(outletId);
        }

        Date dateFrom = new DateTime().minusMonths(24).toDate();
        ArrayList<BrandVariantdetail> lstBrandVariantdetail = BrandVariantDetailDAO.loadFrequentSKUList(outletId, outlet.getGeoLocation().getLocationId(), requestMessage.getStartIndex(), dateFrom, requestMessage.getSearchText());

        ArrayList<Integer> skuIds = new ArrayList<>();
        ArrayList<TpDetail> lstTpDetail = new ArrayList<>();
        if (lstBrandVariantdetail.size() > 0) {
            for (BrandVariantdetail sku : lstBrandVariantdetail) {
                skuIds.add(sku.getVariantDetailId());
            }
            if (skuIds.size() > 0) {
                lstTpDetail = TpDetailDAO.loadList(skuIds.toArray(new Integer[0]), outlet.getGeoLocation().getLocationId());
            }

            if (!Objects.isNull(requestMessage.getVersionNumber())) {
                if (lstBrandVariantdetail.size() > 0) {
                    ArrayList<DistBrandmapping> lstBrandDistributors = DistBrandmappingDAO.loadSkusDistributers(skuIds.toArray(new Integer[0]), outlet.getGeoLocation().getLocationId(), outlet.getOutletId());
                    String skuIdsCsv = getVariantDetailIds(lstBrandVariantdetail);
                    String distIdsCsv = getDistIds(lstBrandDistributors);
                    ArrayList<Object[]> lstSkusAvailability = BrandVariantDetailDAO.loadSkusAvailablility(skuIdsCsv, distIdsCsv);

                    for (BrandVariantdetail brandVariantdetail : lstBrandVariantdetail) {
                        boolean flag = true;

                        for (Object[] object : lstSkusAvailability) {
                            if (!Objects.isNull(object)) {
                                if (!Objects.isNull(object[0]) && Integer.parseInt(object[0].toString()) == brandVariantdetail.getVariantDetailId()) {
                                    brandVariantdetail.setAvailableCount(Objects.isNull(object[3]) ? 0.0 : Double.valueOf(object[3].toString()));
                                    flag = false;
                                }
                            }
                        }
                        if (flag) {
                            brandVariantdetail.setAvailableCount(0.0);
                        }
                    }
                }
            }

            if (lstTpDetail.size() > 0) {
                for (BrandVariantdetail brandVariantDetail : lstBrandVariantdetail) {
                    ArrayList<TpDetail> lstAllPromotions = new ArrayList<>();
                    boolean flag = false;
                    for (TpDetail tpDetail : lstTpDetail) {
                        if (tpDetail.getTpInformation().getBrandVariantdetail().getVariantDetailId().equals(brandVariantDetail.getVariantDetailId())) {
                            if (!Objects.isNull(requestMessage.getVersionNumber())) {
                                if (brandVariantDetail.getAvailableCount() >= tpDetail.getOrderQty()
                                        && brandVariantDetail.getMaximumOrderQuantity() >= tpDetail.getOrderQty()) {
                                    lstAllPromotions.add(tpDetail);
                                    flag = true;
                                }
                            } else {
                                lstAllPromotions.add(tpDetail);
                                flag = true;
                            }
                        }
                    }
                    if (flag) {
                        brandVariantDetail.setLstAllPromotions(lstAllPromotions);
                    }
                }
            }
        }

        if (!lstBrandVariantdetail.isEmpty()) {
            lstBrandVariantdetail = BrandVariantDetailDAO.clearSets(lstBrandVariantdetail);
        } else {
            responseMessage.setStatus(ServiceStatus.NO_DATA_AVAILABLE.getValue());
            responseMessage.setStatusText(ServiceStatus.NO_DATA_AVAILABLE.getStatusText());
            return responseMessage;
        }

        if (lstBrandVariantdetail.size() == Integer.parseInt(ConfigDAO.load(Configuration.MAX_RECORDS.getId()).getConfValue())) {
            responseMessage.setHasMoreData(true);
        } else {
            responseMessage.setHasMoreData(false);
        }

        responseMessage.setLstBrandVariantDetail(lstBrandVariantdetail);

        responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
        responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());

        return responseMessage;

    }

    @POST
    @Path("/bookOrder")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ServiceSecurity(security = Security.RESTRICTED)
    public Message bookOrder(Message requestMessage) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchAlgorithmException, UnsupportedEncodingException, Exception {
        Message responseMessage = new Message();
        Utilities util = new Utilities();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();
        OutletInformation outlet = OutletInformationDAO.loadOutlet(loggedUser.getRelationId());

        ArrayList<OrderDetail> lstOrderItem = requestMessage.getLstOrderDetail();

        if (Objects.isNull(lstOrderItem) || lstOrderItem.isEmpty()) {
            responseMessage.setStatus(ServiceStatus.ORDER_SHOULD_CONTAIN_ATLEAST_ONE_ITEM.getValue());
            responseMessage.setStatusText(ServiceStatus.ORDER_SHOULD_CONTAIN_ATLEAST_ONE_ITEM.getStatusText());
            return responseMessage;
        }

        boolean isOrderDetailValidated = validateRequestedOrderDetail(lstOrderItem);

        if (!isOrderDetailValidated) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        }
        
        DistPolygon area = DistPolygonDAO.getServiceArea(outlet.getLongitude(), outlet.getLatitude());       
        if(area!=null){
            if(!area.getIsServiceArea() && !area.getIsFutureServiceArea() ){
                throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Area Not Allowed to be Searved").build());
            }
        }else{
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("No Area Found").build());
        }

        ArrayList<Integer> skuIds = new ArrayList<>();
        if (!lstOrderItem.isEmpty()) {
            for (OrderDetail orderDetail : lstOrderItem) {
                skuIds.add(orderDetail.getBrandVariantdetail().getVariantDetailId());
            }
        }

        boolean isValidated = validateSKU(skuIds.toArray(new Integer[0]), lstOrderItem, outlet, Objects.isNull(requestMessage.getVersionNumber()) ? null : requestMessage.getVersionNumber());

        if (!isValidated) {
            responseMessage.setStatus(ServiceStatus.SKU_VALIDATION_FAILED.getValue());
            responseMessage.setStatusText(ServiceStatus.SKU_VALIDATION_FAILED.getStatusText());
            return responseMessage;
        }

        Double totalOrderValue = getOrderTotal(lstOrderItem);
//        Double minimumOrderValue = Double.valueOf(ConfigDAO.load(Configuration.UNIVERSAL_MINIMUM_ORDER_VALUE.getId()).getConfValue());
//        Double maximumOrderValue = Double.valueOf(ConfigDAO.load(Configuration.UNIVERSAL_MAXIMUM_ORDER_VALUE.getId()).getConfValue());
//
//        ConfigAppType appVersion = ConfigAppType.getEnumElement(requestMessage.getVersionNumber());
//        if (!Objects.isNull(appVersion) && appVersion.getId() >= ConfigAppType.VERSION18.getId()) {
//            if (totalOrderValue < minimumOrderValue) {
//                responseMessage.setStatus(ServiceStatus.MINIMUM_ORDER_VALUE_VALIDATION_FAILED.getValue());
//                responseMessage.setStatusText(ServiceStatus.MINIMUM_ORDER_VALUE_VALIDATION_FAILED.getStatusText());
//                return responseMessage;
//            } else if (totalOrderValue > maximumOrderValue) {
//                responseMessage.setStatus(ServiceStatus.MAXIMUM_ORDER_VALUE_VALIDATION_FAILED.getValue());
//                responseMessage.setStatusText(ServiceStatus.MAXIMUM_ORDER_VALUE_VALIDATION_FAILED.getStatusText());
//                return responseMessage;
//            }
//        }

        OrderInformation order = buildOrder(outlet, loggedUser);

        HashMap<Integer, OrderDistmapping> lstMapping = new HashMap<>();
    try{
        for (OrderDetail item : lstOrderItem) {
            Integer distributerId = Integer.valueOf(((Object) DistBrandmappingDAO.getDistId("" + item.getBrandInformation().getBrandId(), "" + outlet.getGeoLocation().getLocationId()).get(0)).toString());
            OrderDistmapping mapping = lstMapping.get(distributerId);
            if (Objects.isNull(mapping)) {
                mapping = buildMapping(distributerId, order);
                if(requestMessage.getComment() != null )
                {
                    mapping.setSpecialInstruction(requestMessage.getComment());
                }
                
                if(requestMessage.getDistDeliverySlotId() != null && requestMessage.getDistDeliverySlotId() > 0 )
                {
                    DistDeliveryslot deliverySlot = new DistDeliveryslot();
                    deliverySlot.setId(requestMessage.getDistDeliverySlotId());
                    mapping.setDistDeliveryslot(deliverySlot);
                }
                
                if(requestMessage.getPreferredDeliveryDate() != null )
                {   
                    mapping.setPreferredDeliveryDate(util.s3.parse(util.s3.format(requestMessage.getPreferredDeliveryDate())));
                }
                
                lstMapping.put(distributerId, mapping);
            }
            buildItem(item, mapping);
        }
        
    }catch (Exception e){
        e.printStackTrace();
        responseMessage.setStatus(ServiceStatus.FAILED.getValue());
        responseMessage.setStatusText(e.getMessage() + " : " + e);
    
    }
        OrderInformationDAO.addAppOrder(order, lstMapping, lstOrderItem);

        try {
            UserActivity userActivity = buildUserActivity(loggedUser);
            UserActivityDAO.insertUserActivity(userActivity, order.getOrderId());
        } catch (Exception ex) {
        }

        responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
        responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());

        return responseMessage;

    }

    @POST
    @Path("/validateOrderSku")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ServiceSecurity(security = Security.RESTRICTED)
    public Message validateOrderSKU(Message requestMessage) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchAlgorithmException, UnsupportedEncodingException, Exception {
        Message responseMessage = new Message();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();
        OutletInformation outlet = OutletInformationDAO.loadOutlet(loggedUser.getRelationId());
        Double minimumOrderValue = Double.valueOf(ConfigDAO.load(Configuration.UNIVERSAL_MINIMUM_ORDER_VALUE.getId()).getConfValue());
        Double maximumOrderValue = Double.valueOf(ConfigDAO.load(Configuration.UNIVERSAL_MAXIMUM_ORDER_VALUE.getId()).getConfValue());

        ArrayList<OrderDetail> lstOrderItem = requestMessage.getLstOrderDetail();

        if (Objects.isNull(lstOrderItem) || lstOrderItem.isEmpty()) {
            responseMessage.setStatus(ServiceStatus.ORDER_SHOULD_CONTAIN_ATLEAST_ONE_ITEM.getValue());
            responseMessage.setStatusText(ServiceStatus.ORDER_SHOULD_CONTAIN_ATLEAST_ONE_ITEM.getStatusText());
            return responseMessage;
        }

        boolean isOrderDetailValidated = validateRequestedOrderDetail(lstOrderItem);

        if (!isOrderDetailValidated) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        }

        ArrayList<Integer> skuIds = new ArrayList<>();
        if (!lstOrderItem.isEmpty()) {
            for (OrderDetail orderDetail : lstOrderItem) {
                skuIds.add(orderDetail.getBrandVariantdetail().getVariantDetailId());
            }
        }

        validateDuplicateSkus(lstOrderItem);
        Double totalOrderValue = validateRequestedOrderDetail(lstOrderItem, outlet, skuIds, Objects.isNull(requestMessage.getVersionNumber()) ? null : requestMessage.getVersionNumber());

        ArrayList<OrderDetail> toRemoveOrderList = new ArrayList<>();
        for (OrderDetail orderDetail : lstOrderItem) {
            if (Objects.isNull(orderDetail.getComment())) {
                toRemoveOrderList.add(orderDetail);
            }
        }

        lstOrderItem.removeAll(toRemoveOrderList);

        if (!lstOrderItem.isEmpty()) {
            OrderDetailDAO.clearSets(lstOrderItem);
            responseMessage.setLstOrderDetail(lstOrderItem);
        }

        int counter = 0;
        for (OrderDetail orderDetail : lstOrderItem) {
            if (!Objects.isNull(orderDetail.getIsRateModified()) && orderDetail.getIsRateModified()) {
                responseMessage.setIsRateModified(Boolean.TRUE);
                counter++;
                break;
            }
        }

//        ConfigAppType appVersion = ConfigAppType.getEnumElement(requestMessage.getVersionNumber());
//
//        if (!Objects.isNull(appVersion) && appVersion.getId() >= ConfigAppType.VERSION18.getId()) {
//            if (totalOrderValue < minimumOrderValue) {
//                responseMessage.setStatus(ServiceStatus.MINIMUM_ORDER_VALUE_VALIDATION_FAILED.getValue());
//                responseMessage.setStatusText(ServiceStatus.MINIMUM_ORDER_VALUE_VALIDATION_FAILED.getStatusText());
//                return responseMessage;
//
//            } else if (totalOrderValue > maximumOrderValue) {
//                responseMessage.setStatus(ServiceStatus.MAXIMUM_ORDER_VALUE_VALIDATION_FAILED.getValue());
//                responseMessage.setStatusText(ServiceStatus.MAXIMUM_ORDER_VALUE_VALIDATION_FAILED.getStatusText());
//                return responseMessage;
//            }
//        }
        if (!lstOrderItem.isEmpty()) {
            if (counter == 0) {
                responseMessage.setIsRateModified(Boolean.FALSE);
            }
            responseMessage.setStatus(ServiceStatus.FAILED.getValue());
            responseMessage.setStatusText(ServiceStatus.FAILED.getStatusText());
        } else {
            responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
            responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());
        }

        return responseMessage;

    }

    private OrderInformation buildOrder(OutletInformation outlet, UserInformation loggedUser) {
        OrderInformation order = new OrderInformation();
        order.setGenTypedetail(GenTypedetailDAO.build(OrderType.APP.getId()));
        order.setGeoLocation(outlet.getGeoLocation());
        order.setInsertDate(new Date());
        order.setIsActive(true);
        order.setOrderDate(new Date());
        order.setOutletInformation(outlet);
        order.setOutletName(outlet.getOutletName());
        order.setUserInformationByRequestedbyId(loggedUser);
        order.setUserInformationByBookingAgentId(UserInformationDAO.getSystemDefaultUser());
        order.setUserInformationByInsertId(UserInformationDAO.getSystemDefaultUser());
        return order;
    }

    private OrderDistmapping buildMapping(Integer distributerId, OrderInformation order) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        Utilities utilities = new Utilities();
        OrderDistmapping mapping = new OrderDistmapping();
        mapping.setOrderInformation(order);
        mapping.setOrderDate(order.getOrderDate());
        mapping.setDistMappingUuid(UserInformationDAO.getHash(utilities.s6.format(new Date()) + order.getUserInformationByRequestedbyId().getUserId() + distributerId));
        mapping.setDistInformation(DistInformationDAO.build(distributerId));
        mapping.setOutletInformation(order.getOutletInformation());
        mapping.setGenTypedetail(GenTypedetailDAO.build(OrderStatus.NEW.getId()));
        mapping.setUserInformationByInsertId(UserInformationDAO.getSystemDefaultUser());
        mapping.setInsertDate(new Date());
        mapping.setIsActive(true);
        return mapping;
    }

    private OrderDetail buildItem(OrderDetail item, OrderDistmapping mapping) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        item.setOrderInformation(mapping.getOrderInformation());
        item.setOrderDetailUuid(UserInformationDAO.getHash(new Date() + String.valueOf(mapping.getOrderInformation().getUserInformationByRequestedbyId().getUserId()) + mapping.getOutletInformation().getOutletId() + mapping.getDistInformation().getDistributerId() + item.getBrandVariantdetail().getVariantDetailId()));
        item.setOrderDistmapping(mapping);
        item.setOutletInformation(mapping.getOutletInformation());
        item.setInitialOrderQty(item.getOrderQty());
        item.setOrderDiscount(0.00);
        item.setDiscountTprate(0.00);
        item.setGenTypedetailByOrderDetailTypeId(GenTypedetailDAO.build(OrderItemType.PURCHASED.getId()));
        item.setOrderStatusId(OrderStatus.NEW.getId());
        item.setUserInformationByInsertId(UserInformationDAO.getSystemDefaultUser());
        item.setInsertDate(mapping.getInsertDate());
        item.setOrderTotal(item.getOrderRate() * item.getOrderQty());
        item.setIsActive(true);
        return item;
    }

    private boolean validateSKU(Integer[] skuIds, ArrayList<OrderDetail> lstOrderItem, OutletInformation outlet, String versionNumber) throws ParseException {
        ArrayList<OrderDetail> lstTempOrderDetail = new ArrayList<>();

        if (skuIds.length > 0) {
            lstTempOrderDetail = OrderDetailDAO.loadOrderDetail(skuIds, outlet.getOutletId());
        }

        ArrayList<DistBrandmapping> lstBrandDistributors = DistBrandmappingDAO.loadSkusDistributers(skuIds, outlet.getGeoLocation().getLocationId(), outlet.getOutletId());
        List<Integer> lstSkuId = Arrays.asList(skuIds);
        String skuIdsCsv = lstSkuId.toString();
        skuIdsCsv = skuIdsCsv.replaceAll("\\[", "");
        skuIdsCsv = skuIdsCsv.replaceAll("\\]", "");
        String distIdsCsv = getDistIds(lstBrandDistributors);
        ArrayList<Object[]> lstSkusAvailability = BrandVariantDetailDAO.loadSkusAvailablility(skuIdsCsv, distIdsCsv);

        for (OrderDetail orderDetail : lstOrderItem) {
            Double calcOrderQty = orderDetail.getOrderQty();
            Double maxOrderQuantity = null;
            for (OrderDetail tempOrderDetail : lstTempOrderDetail) {
                if (orderDetail.getBrandVariantdetail().getVariantDetailId().equals(tempOrderDetail.getBrandVariantdetail().getVariantDetailId())) {
                    calcOrderQty += tempOrderDetail.getOrderQty();
                    maxOrderQuantity = tempOrderDetail.getBrandVariantdetail().getMaximumOrderQuantity();
                }
            }
            if (!Objects.isNull(maxOrderQuantity) && calcOrderQty > maxOrderQuantity) {
                return false;
            }

            if (!Objects.isNull(versionNumber)) {
                for (Object[] object : lstSkusAvailability) {
                    if (!Objects.isNull(object)) {
                        if (!Objects.isNull(object[0]) && Integer.parseInt(object[0].toString()) == orderDetail.getBrandVariantdetail().getVariantDetailId()) {
                            if (!Objects.isNull(object[3])) {
                                Double availableQty = Double.valueOf(object[3].toString());
                                if (availableQty < 0) {
                                    return false;
                                } else if (orderDetail.getOrderQty() > availableQty) {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }

        ArrayList<BrandVariantdetail> lstBrandVariantdetail = BrandVariantDetailDAO.loadList(skuIds, outlet.getGeoLocation().getLocationId(), outlet.getOutletId());

        if (lstBrandVariantdetail.isEmpty() || (lstBrandVariantdetail.size() < skuIds.length)) {
            return false;
        }
        ConfigAppType appVersion = ConfigAppType.getEnumElement(versionNumber);
        for (BrandVariantdetail sku : lstBrandVariantdetail) {
            if (sku.isExcludeFromSale()) {
                return false;
            }
            for (OrderDetail orderDetail : lstOrderItem) {
                if (orderDetail.getBrandVariantdetail().getVariantDetailId().equals(sku.getVariantDetailId())) {
                    if (orderDetail.getOrderQty() > sku.getMaximumOrderQuantity()) {
                        return false;
                    }
                    if (!Objects.isNull(appVersion) && appVersion.getId() >= ConfigAppType.VERSION015.getId()) {
                        if (orderDetail.getOrderRate() != sku.getRegisteredRate()) {
                            return false;
                        }
                    }
                }

            }

        }

        return true;
    }

    private boolean validateRequestedOrderDetail(ArrayList<OrderDetail> lstOrderItems) {
        for (OrderDetail orderDetail : lstOrderItems) {
            if (!Objects.isNull(orderDetail.getManInformation()) && !Objects.isNull(orderDetail.getBrandInformation()) && !Objects.isNull(orderDetail.getBrandVariant()) && !Objects.isNull(orderDetail.getBrandVariantdetail())) {
                if (!Utilities.validateRequiredParameter(orderDetail.getManInformation().getManufacturerId()) || !Utilities.validateRequiredParameter(orderDetail.getBrandInformation().getBrandId())
                        || !Utilities.validateRequiredParameter(orderDetail.getBrandVariant().getVariantId()) || !Utilities.validateRequiredParameter(orderDetail.getBrandVariantdetail().getVariantDetailId())
                        || !Utilities.validateRequiredParameter(orderDetail.getOrderQty()) || !Utilities.validateRequiredParameter(orderDetail.getOrderRate())) {
                    return false;
                }
            } else {
                return false;
            }
        }

        return true;
    }

    private Double validateRequestedOrderDetail(ArrayList<OrderDetail> lstOrderItems, OutletInformation outlet, ArrayList<Integer> mainSkuIds, String versionNumber) throws ParseException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        ArrayList<OrderDetail> lstTempOrderDetail = new ArrayList<>();
        Integer[] skuIds = mainSkuIds.toArray(new Integer[0]);
        Double totalOrderValue = 0.0;

        if (skuIds.length > 0) {
            lstTempOrderDetail = OrderDetailDAO.loadOrderDetail(skuIds, outlet.getOutletId());
        }

        ArrayList<DistBrandmapping> lstBrandDistributors = DistBrandmappingDAO.loadSkusDistributers(skuIds, outlet.getGeoLocation().getLocationId(), outlet.getOutletId());
        String skuIdsCsv = mainSkuIds.toString();
        skuIdsCsv = skuIdsCsv.replaceAll("\\[", "");
        skuIdsCsv = skuIdsCsv.replaceAll("\\]", "");
        String distIdsCsv = getDistIds(lstBrandDistributors);
        ArrayList<Object[]> lstSkusAvailability = BrandVariantDetailDAO.loadSkusAvailablility(skuIdsCsv, distIdsCsv);
        ArrayList<BrandVariantdetail> lstBrandVariantdetail = BrandVariantDetailDAO.loadList(skuIds, outlet.getGeoLocation().getLocationId(), outlet.getOutletId());

        for (OrderDetail orderDetail : lstOrderItems) {
            Double calcOrderQty = orderDetail.getOrderQty();
            Double maxOrderQuantity = null;
            totalOrderValue += calcOrderQty * orderDetail.getOrderRate();
            for (OrderDetail tempOrderDetail : lstTempOrderDetail) {
                if (orderDetail.getBrandVariantdetail().getVariantDetailId().equals(tempOrderDetail.getBrandVariantdetail().getVariantDetailId())) {
                    calcOrderQty += tempOrderDetail.getOrderQty();
                    maxOrderQuantity = tempOrderDetail.getBrandVariantdetail().getMaximumOrderQuantity();
                }
            }
            if (!Objects.isNull(maxOrderQuantity) && calcOrderQty > maxOrderQuantity) {
                calcOrderQty = calcOrderQty - orderDetail.getOrderQty();
                orderDetail.setTotalOrderQuantity(calcOrderQty);
                orderDetail.setComment("Daily Limit Exceeded," + calcOrderQty + " items have been already ordered today.");
            }

            if (!Objects.isNull(versionNumber)) {
                for (Object[] object : lstSkusAvailability) {
                    if (!Objects.isNull(object)) {
                        if (!Objects.isNull(object[0]) && Integer.parseInt(object[0].toString()) == orderDetail.getBrandVariantdetail().getVariantDetailId()) {
                            if (!Objects.isNull(object[3])) {
                                Double availableQty = Double.valueOf(object[3].toString());
                                if (availableQty < 0) {
                                    if (Objects.isNull(orderDetail.getComment()) || orderDetail.getComment().isEmpty()) {
                                        orderDetail.setComment("Stock is not available.");
                                    }
                                } else if (orderDetail.getOrderQty() > availableQty) {
                                    if (Objects.isNull(orderDetail.getComment()) || orderDetail.getComment().isEmpty()) {
                                        orderDetail.setComment("Limited stock available. Available Quantity is " + availableQty);
                                    }
                                }
                                orderDetail.getBrandVariantdetail().setAvailableCount(availableQty);
                            }
                        }
                    }
                }
            }
        }

        ArrayList<Integer> returnedSkuIds = new ArrayList<>();
        if (!Objects.isNull(lstBrandVariantdetail) && !lstBrandVariantdetail.isEmpty()) {
            if (!lstBrandVariantdetail.isEmpty() || lstBrandVariantdetail.size() < skuIds.length) {
                for (BrandVariantdetail sku : lstBrandVariantdetail) {
                    returnedSkuIds.add(sku.getVariantDetailId());
                }
            }
        }

        if (returnedSkuIds.size() > 0) {
            mainSkuIds.removeAll(returnedSkuIds);
        }

        if (mainSkuIds.size() > 0) {
            for (OrderDetail orderDetail : lstOrderItems) {
                for (Integer variantDetailId : mainSkuIds) {
                    if (orderDetail.getBrandVariantdetail().getVariantDetailId().equals(variantDetailId)) {
                        orderDetail.setComment("The item is not available in your area");
                    }
                }
            }
        }
        
      //  ConfigAppType appVersion = ConfigAppType.getEnumElement(versionNumber);
        ConfigApp appVersion = ConfigAppDAO.load(versionNumber.trim(), ApplicationType.RETAILER_APP.getId());
        for (BrandVariantdetail sku : lstBrandVariantdetail) {
            for (OrderDetail orderDetail : lstOrderItems) {
                if (orderDetail.getBrandVariantdetail().getVariantDetailId().equals(sku.getVariantDetailId())) {
                    if (sku.isExcludeFromSale()) {
                        orderDetail.setComment("The item is no longer available");
                    }
                   // if (!Objects.isNull(appVersion) && appVersion.getId() >= ConfigAppType.VERSION015.getId()) {
                   if (!Objects.isNull(appVersion)){     
                   if (orderDetail.getOrderRate() != sku.getRegisteredRate()) {
                            if (Objects.isNull(orderDetail.getComment()) || orderDetail.getComment().isEmpty()) {
                                orderDetail.setIsRateModified(Boolean.TRUE);
                                orderDetail.setBrandVariantdetail(getModifiedSku(orderDetail, outlet, versionNumber));
                                orderDetail.setComment("Price Updated.");
                            }
                        }
                    }

                    if (orderDetail.getOrderQty() > sku.getMaximumOrderQuantity()) {
                        if (Objects.isNull(orderDetail.getComment()) || orderDetail.getComment().isEmpty()) {
                            orderDetail.setComment("Order Quantity limit exceeded. (Max Order Qty is " + sku.getMaximumOrderQuantity() + ")");
                        }
                    }
                }

            }
        }

        return totalOrderValue;

    }

    @POST
    @Path("/getOrderList")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ServiceSecurity(security = Security.RESTRICTED)
    public Message getOrderList(Message requestMessage) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {     
        Message responseMessage = new Message();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();
        OutletInformation outlet = OutletInformationDAO.loadOutlet(loggedUser.getRelationId());

        if (Objects.isNull(requestMessage)) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        }
        if (Objects.isNull(requestMessage.getStartIndex()) || Objects.isNull(requestMessage.getOrderStatus())) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        }

        if(!requestMessage.getOrderStatus().equals(Integer.valueOf(4)) && !requestMessage.getOrderStatus().equals(Integer.valueOf(5))){
        if (!Objects.equals(requestMessage.getOrderStatus(), OrderStatusTypeService.ACCEPTED.getValue()) && !Objects.equals(requestMessage.getOrderStatus(), OrderStatusTypeService.BOOKED.getValue())
                && !Objects.equals(requestMessage.getOrderStatus(), OrderStatusTypeService.HISTORY.getValue())) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Order Status Not Correctly Specified").build());
        }
        }
        if (!Objects.isNull(requestMessage.getSortField())) {
            if (!Objects.equals(requestMessage.getSortField(), SortFieldTypeService.AMOUNT.getValue()) && !Objects.equals(requestMessage.getSortField(), SortFieldTypeService.DATE.getValue())
                    && !Objects.equals(requestMessage.getSortField(), SortFieldTypeService.STATUS.getValue())) {
                throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Order Sort Field Not Correctly Specified").build());
            }
        }

        if (!Objects.isNull(requestMessage.getSortOrder())) {
            if (!Objects.equals(requestMessage.getSortOrder(), SortOrderTypeService.ASCENDING.getValue()) && !Objects.equals(requestMessage.getSortOrder(), SortOrderTypeService.DESCENDING.getValue())) {
                throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Order Sort Not Correctly Specified").build());
            }
        }

        ArrayList<OrderInformation> lstOrderInformation = OrderInformationDAO.loadList(outlet.getOutletId(), requestMessage.getOrderStatus(), requestMessage.getSortField(), requestMessage.getSortOrder(), requestMessage.getStartIndex(), requestMessage.getSearchText());
        ArrayList<Long> lstOrderId = new ArrayList<>();
        if (!lstOrderInformation.isEmpty()) {
            for (OrderInformation order : lstOrderInformation) {
                lstOrderId.add(order.getOrderId());
            }
            OrderInformationDAO.clearSets(lstOrderInformation);
        }

        ArrayList<ManInformation> lstManInformation = new ArrayList<>();
        if (!lstOrderId.isEmpty()) {
            lstManInformation = ManInformationDAO.loadManOrderList(outlet.getOutletId(), requestMessage.getOrderStatus(), lstOrderId.toArray(new Long[0]));
        }

        if (!lstOrderInformation.isEmpty() && !lstManInformation.isEmpty()) {
            ManInformationDAO.clearSets(lstManInformation);
            for (OrderInformation order : lstOrderInformation) {
                ArrayList<ManInformation> lstManInfo = new ArrayList<>();
                for (ManInformation man : lstManInformation) {
                    if (order.getOrderId().equals(man.getOrderDistributorMappingId().getOrderInformation().getOrderId())) {
//                        if (Objects.equals(man.getOrderDistributorMappingId().getOrderInformation().getTotalInitialQty(), man.getOrderDistributorMappingId().getOrderInformation().getTotalOrderQty())) {
//                            man.getOrderDistributorMappingId().getOrderInformation().setIsModified(Boolean.FALSE);
//                        } else {
//                            man.getOrderDistributorMappingId().getOrderInformation().setIsModified(Boolean.TRUE);
//                        }
                        lstManInfo.add(man);
                    }
                }
                order.setLstManInformation(lstManInfo);
            }
        }

        if (lstOrderInformation.size() == Integer.parseInt(ConfigDAO.load(Configuration.MAX_RECORDS.getId()).getConfValue())) {
            responseMessage.setHasMoreData(true);
        } else {
            responseMessage.setHasMoreData(false);
        }

        responseMessage.setLstOrderInformation(lstOrderInformation);
        responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
        responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());

        return responseMessage;

    }

    @POST
    @Path("/getOrderDetailList")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ServiceSecurity(security = Security.RESTRICTED)
    public Message getOrderDetailList(Message requestMessage) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, ParseException {
        Message responseMessage = new Message();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();
        OutletInformation outlet = OutletInformationDAO.loadOutlet(loggedUser.getRelationId());
        ArrayList<OrderDetail> lstOrderDetail = null;

        if (Objects.isNull(requestMessage)) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        }
//        if (!Utilities.validateRequiredParameter(requestMessage.getManufactureId())) {
//            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
//        }
        if (Objects.isNull(requestMessage.getOrderId()) || Objects.equals(requestMessage.getOrderId(), 0)) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        }

        ArrayList<Object[]> lstRows = OrderDetailDAO.loadList(requestMessage.getManufactureId(), requestMessage.getOrderId(), outlet.getOutletId(), null);

        if (!lstRows.isEmpty()) {
            lstOrderDetail = convertObjectToOrderDetail(lstRows);
            OrderDetailDAO.clearSets(lstOrderDetail);
        } else {
            responseMessage.setStatus(ServiceStatus.NO_DATA_AVAILABLE.getValue());
            responseMessage.setStatusText(ServiceStatus.NO_DATA_AVAILABLE.getStatusText());
            return responseMessage;
        }
        responseMessage.setLstOrderDetail(lstOrderDetail);
        responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
        responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());

        return responseMessage;

    }

    @POST
    @Path("/cancelManufacturerSkus")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ServiceSecurity(security = Security.RESTRICTED)
    public Message cancelManufacturerSkus(Message requestMessage) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Message responseMessage = new Message();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();
        OutletInformation outlet = OutletInformationDAO.loadOutlet(loggedUser.getRelationId());
        ArrayList<OrderDetail> lstOrderDetail = null;

        if (Objects.isNull(requestMessage)) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        }
        if (!Utilities.validateRequiredParameter(requestMessage.getManufactureId())) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        }
        if (Objects.isNull(requestMessage.getOrderId()) || Objects.equals(requestMessage.getOrderId(), 0)) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        }
       /* 
        if (Objects.isNull(requestMessage.getLongitude()) ) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        }
        
         if (Objects.isNull(requestMessage.getLatitude()) ) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        }
		*/
        ArrayList<Object[]> lstRows = OrderDetailDAO.loadList(requestMessage.getManufactureId(), requestMessage.getOrderId(), outlet.getOutletId(), null);

        if (lstRows.isEmpty()) {
            responseMessage.setStatus(ServiceStatus.NO_DATA_AVAILABLE.getValue());
            responseMessage.setStatusText(ServiceStatus.NO_DATA_AVAILABLE.getStatusText());
            return responseMessage;
        }

        String distMappingIdCsv = getDistMappingIds(lstRows);
        ArrayList<Object[]> lstOrderDetailByDistMapping = OrderDetailDAO.loadList(null, requestMessage.getOrderId(), outlet.getOutletId(), distMappingIdCsv);

        boolean isAllSkusCancelled = checkSkusStatus(lstOrderDetailByDistMapping);
        
        if (isAllSkusCancelled) {
            ArrayList<Long> distMappingIds = new ArrayList<>();
            for (Object[] object : lstOrderDetailByDistMapping) {
                distMappingIds.add(Long.parseLong(object[11].toString()));
            }
            ArrayList<OrderDistmapping> lstOrderDistMapIds = OrderDistmappingDAO.loadListExceptCancel(distMappingIds.toArray(new Long[0]));
            if (!lstOrderDistMapIds.isEmpty()) {
                String distMapids = "";
                for (int i = 0; i < lstOrderDistMapIds.size(); i++) {
                    if (i > 0 && i < lstOrderDistMapIds.size()) {
                        distMapids += ",";
                    }
                    distMapids += lstOrderDistMapIds.get(i).getDistMappingId();
                }
                boolean isSuccess = OrderDistmappingDAO.updateManDistMapStatus(requestMessage.getOrderId().toString(), distMapids, loggedUser, OrderStatus.CANCEL.getId().toString());

                if (!isSuccess) {
                    responseMessage.setStatus(ServiceStatus.FAILED.getValue());
                    responseMessage.setStatusText(ServiceStatus.FAILED.getStatusText());
                    return responseMessage;
                }

            }
        } else {
           

            OrderDistmappingDAO.updateManOrderDistMapDetailStatus(OrderStatus.CANCEL.getId().toString(), distMappingIdCsv, loggedUser, requestMessage.getManufactureId());

            lstRows = OrderDetailDAO.loadList(null, requestMessage.getOrderId(), outlet.getOutletId(), distMappingIdCsv);
            isAllSkusCancelled = checkSkusStatus(lstRows);
            if (isAllSkusCancelled) {
                boolean isSuccess = OrderDistmappingDAO.updateManDistMapStatus(requestMessage.getOrderId().toString(), distMappingIdCsv, loggedUser, OrderStatus.CANCEL.getId().toString());
                if (!isSuccess) {
                    responseMessage.setStatus(ServiceStatus.FAILED.getValue());
                    responseMessage.setStatusText(ServiceStatus.FAILED.getStatusText());
                    return responseMessage;
                }
            }

        }

        responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
        responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());

        return responseMessage;

    }

    @POST
    @Path("/getManufacturerOrderList")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ServiceSecurity(security = Security.RESTRICTED)
    public Message getManufacturerOrderList(Message requestMessage) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, ParseException {
        Message responseMessage = new Message();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();
        OutletInformation outlet = OutletInformationDAO.loadOutlet(loggedUser.getRelationId());
        ArrayList<OrderDetail> lstOrderDetail = null;
        ArrayList<OrderDetail> lstOrderDetailTemp = null;

        if (Objects.isNull(requestMessage)) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        }
        if (!Utilities.validateRequiredParameter(requestMessage.getManufactureId())) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        }

        if (Objects.isNull(requestMessage.getStartIndex())) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        }

        ArrayList<OrderDistmapping> lstRows = OrderDistmappingDAO.loadManOrderList(requestMessage.getManufactureId(), requestMessage.getStartIndex(), outlet.getOutletId());

        if (!lstRows.isEmpty()) {

            String distMapids = "";
            for (int i = 0; i < lstRows.size(); i++) {
                if (i != 0 && i != lstRows.size() - 1) {
                    distMapids += ",";
                }
                distMapids += lstRows.get(i).getDistMappingId();
            }

            ArrayList<Object[]> lstOrderDetailObject = OrderDetailDAO.loadList(requestMessage.getManufactureId(), null, outlet.getOutletId(), distMapids);
            lstOrderDetail = convertObjectToOrderDetail(lstOrderDetailObject);

            for (OrderDistmapping orderDistmapping : lstRows) {
                lstOrderDetailTemp = new ArrayList<>();
                for (OrderDetail orderDetail : lstOrderDetail) {
                    if (Objects.equals(orderDistmapping.getDistMappingId(), orderDetail.getOrderDistmapping().getDistMappingId())) {
                        lstOrderDetailTemp.add(orderDetail);
                    }
                }
                orderDistmapping.getManInformation().setLstOrderDetail(lstOrderDetailTemp);
            }

            OrderDistmappingDAO.clearSets(lstRows);

        } else {
            responseMessage.setStatus(ServiceStatus.NO_DATA_AVAILABLE.getValue());
            responseMessage.setStatusText(ServiceStatus.NO_DATA_AVAILABLE.getStatusText());
            return responseMessage;
        }

        if (lstRows.size() == Integer.parseInt(ConfigDAO.load(Configuration.MAX_RECORDS.getId()).getConfValue())) {
            responseMessage.setHasMoreData(true);
        } else {
            responseMessage.setHasMoreData(false);
        }

        responseMessage.setLstOrderDistmapping(lstRows);
        responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
        responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());

        return responseMessage;

    }

    private ArrayList<OrderDetail> convertObjectToOrderDetail(ArrayList<Object[]> lstRows) throws ParseException {
        ArrayList<OrderDetail> lstOrderDetail = new ArrayList<>();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");

        for (Object[] object : lstRows) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderDetailId(Long.parseLong(object[0].toString()));
            if (!Objects.isNull(object[1])) {
                orderDetail.setOrderRate(Double.valueOf(object[1].toString()));
            }
            if (!Objects.isNull(object[2])) {
                orderDetail.setInitialOrderQty(Double.valueOf(object[2].toString()));
            }
            if (!Objects.isNull(object[3])) {
                orderDetail.setOrderTotal(Double.valueOf(object[3].toString()));
            }
            if (!Objects.isNull(object[4]) || !Objects.isNull(object[5])) {
                orderDetail.setStatusType(new GenTypedetail(Integer.parseInt(object[4].toString()), object[5].toString()));
            }
            if (!Objects.isNull(object[6]) || !Objects.isNull(object[7])) {
                orderDetail.setGenTypedetailByOrderDetailTypeId(new GenTypedetail(Integer.parseInt(object[6].toString()), object[7].toString()));
            }
            orderDetail.setBrandVariantdetail(new BrandVariantdetail(Integer.parseInt(object[8].toString()), !Objects.isNull(object[9]) ? Integer.parseInt(object[9].toString()) : null, !Objects.isNull(object[10]) ? object[10].toString() : ""));

            orderDetail.setOrderDistmapping(OrderDistmappingDAO.build(Long.valueOf(object[11].toString())));

            orderDetail.setOrderQty(!Objects.isNull(object[12]) ? Double.valueOf(object[12].toString()) : 0.00);
            
            orderDetail.setManInformation(new ManInformation(Integer.parseInt(object[16].toString()),null));

            if (!Objects.isNull(object[13])) {
                orderDetail.setBrandInformation(new BrandInformation(Integer.valueOf(object[13].toString())));
            }

            if (!Objects.isNull(object[14])) {
                orderDetail.setUserInformationByEditId(new UserInformation(Integer.valueOf(object[14].toString())));
            }

            lstOrderDetail.add(orderDetail);
          }

        return lstOrderDetail;
    }

    private boolean checkSkusStatus(ArrayList<Object[]> lstRows) {

        for (Object[] object : lstRows) {
            if (!Objects.isNull(object[4])) {
                if (Integer.parseInt(object[4].toString()) != (OrderStatus.CANCEL.getId())) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean checkDistMapStatus(ArrayList<Object[]> lstRows) {
        for (Object[] object : lstRows) {
            if (!Objects.equals(Integer.valueOf(object[2].toString()), OrderStatus.CANCEL.getId())) {
                return false;
            }
        }
        return true;
    }

    private void validateDuplicateSkus(ArrayList<OrderDetail> lstOrderItems) {
        for (int i = 0; i < lstOrderItems.size(); i++) {
            for (int j = i + 1; j < lstOrderItems.size(); j++) {
                if (lstOrderItems.get(i).getBrandVariantdetail().getVariantDetailId().equals(lstOrderItems.get(j).getBrandVariantdetail().getVariantDetailId())) {
                    lstOrderItems.get(i).setComment("Duplicate order item found.");
                    lstOrderItems.get(j).setComment("Duplicate order item found.");
                }
            }
        }
    }

    private String getDistMappingIds(ArrayList<Object[]> lstRows) {
        String distMappingId = "";
        for (int i = 0; i < lstRows.size(); i++) {
            if (i > 0 && i < lstRows.size()) {
                distMappingId += ",";
            }
            Object[] object = lstRows.get(i);
            distMappingId += object[11].toString();
        }
        return distMappingId;
    }

    private String getVariantDetailIds(ArrayList<BrandVariantdetail> lstRows) {
        String skuIds = "";
        for (int i = 0; i < lstRows.size(); i++) {
            if (i > 0 && i < lstRows.size()) {
                skuIds += ",";
            }
            skuIds += lstRows.get(i).getVariantDetailId();
        }
        return skuIds;
    }

    private String getDistIds(ArrayList<DistBrandmapping> lstRows) {
        String distIds = "";
        for (int i = 0; i < lstRows.size(); i++) {
            if (i > 0 && i < lstRows.size()) {
                distIds += ",";
            }
            distIds += lstRows.get(i).getDistInformation().getDistributerId();
        }
        return distIds;
    }

    @GET
    @Path("/getBrands")
    @ServiceSecurity(security = Security.RESTRICTED)
    public Response getBrands(
            @QueryParam("categoryid") Integer categoryId,
            @QueryParam("manufactureid") Integer manufactureId,
            @QueryParam("searchtext") String searchText,
            @QueryParam("startindex") Integer startIndex,
            @QueryParam("productid") Integer productId,
            @QueryParam("promotionavailable") Boolean promotionAvailable,
            @QueryParam("brandgroupid") Integer brandGroupId
    ) throws ParseException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Map<Integer, ConfigService> configServiceMap = (Map<Integer, ConfigService>) servletContext.getAttribute("configServiceMap");
        ConfigService configService = configServiceMap.get(ConfigurationService.GET_BRANDS_OR.getId());
        CacheController cacheControlObject = new CacheController();
        CacheControl cc = cacheControlObject.createCacheControl(configService);

        Message responseMessage = new Message();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();
        OutletInformation outlet = OutletInformationDAO.loadOutlet(loggedUser.getRelationId());

        if (!Objects.isNull(categoryId)) {
            if (!Utilities.validateRequiredParameter(categoryId)) {
                throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
            }
        }
        if (!Objects.isNull(manufactureId)) {
            if (!Utilities.validateRequiredParameter(manufactureId)) {
                throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
            }
        }
        if (!Objects.isNull(searchText)) {
            if (!Utilities.validateRequiredParameter(searchText)) {
                throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
            }
        }

        if (Objects.isNull(startIndex)) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        }

        ArrayList<BrandInformation> lstRows = BrandInformationDAO.loadList(
                categoryId,
                manufactureId,
                productId,
                Objects.isNull(searchText) ? "" : searchText,
                outlet.getGeoLocation().getLocationId(),
                promotionAvailable,
                startIndex,
                outlet.getOutletId(),
                brandGroupId
        );

        ArrayList<BrandInformation> lstBrandInformation = new ArrayList<>();
        if (lstRows.size() > 0) {
            lstBrandInformation = BrandInformationDAO.clearSets(lstRows);
        }

        if (lstBrandInformation.isEmpty()) {
            responseMessage.setStatus(ServiceStatus.NO_DATA_AVAILABLE.getValue());
            responseMessage.setStatusText(ServiceStatus.NO_DATA_AVAILABLE.getStatusText());
            Response.ResponseBuilder builder = Response.ok(responseMessage, "application/json");
            builder.cacheControl(cc);
            return builder.build();
        }

        if (lstRows.size() == Integer.parseInt(ConfigDAO.load(Configuration.MAX_RECORDS.getId()).getConfValue())) {
            responseMessage.setHasMoreData(true);
        } else {
            responseMessage.setHasMoreData(false);
        }

        responseMessage.setImageBaseURL(ConfigDAO.load(Configuration.IMAGE_BASE_URL.getId()).getConfValue());
        responseMessage.setLstBrandInformation(lstBrandInformation);

        responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
        responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());

        Response.ResponseBuilder builder = Response.ok(responseMessage, "application/json");
        builder.cacheControl(cc);
        return builder.build();

    }

    @GET
    @Path("/getManufactures")
    @ServiceSecurity(security = Security.RESTRICTED)
    public Response getManufactures() throws ParseException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Map<Integer, ConfigService> configServiceMap = (Map<Integer, ConfigService>) servletContext.getAttribute("configServiceMap");
        ConfigService configService = configServiceMap.get(ConfigurationService.GET_MANUFACTURES_OR.getId());
        CacheController cacheControlObject = new CacheController();
        CacheControl cc = cacheControlObject.createCacheControl(configService);

        Message responseMessage = new Message();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();
        OutletInformation outlet = OutletInformationDAO.loadOutlet(loggedUser.getRelationId());

        ArrayList<ManInformation> lstManInformations = ManInformationDAO.loadList(outlet.getGeoLocation().getLocationId(), outlet.getOutletId());
        if (lstManInformations.size() > 0) {
            lstManInformations = ManInformationDAO.clearSets(lstManInformations);
        }

        if (lstManInformations.isEmpty()) {
            responseMessage.setStatus(ServiceStatus.NO_DATA_AVAILABLE.getValue());
            responseMessage.setStatusText(ServiceStatus.NO_DATA_AVAILABLE.getStatusText());
            Response.ResponseBuilder builder = Response.ok(responseMessage, "application/json");
            builder.cacheControl(cc);
            return builder.build();
        }

        responseMessage.setLstManInformation(lstManInformations);
        responseMessage.setImageBaseURL(ConfigDAO.load(Configuration.IMAGE_BASE_URL.getId()).getConfValue());

        responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
        responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());

        Response.ResponseBuilder builder = Response.ok(responseMessage, "application/json");
        builder.cacheControl(cc);
        return builder.build();

    }

    @GET
    @Path("/getCategories")
    @ServiceSecurity(security = Security.RESTRICTED)
    public Response getCategories() throws ParseException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Map<Integer, ConfigService> configServiceMap = (Map<Integer, ConfigService>) servletContext.getAttribute("configServiceMap");
        ConfigService configService = configServiceMap.get(ConfigurationService.GET_CATEGORIES_OR.getId());
        CacheController cacheControlObject = new CacheController();
        CacheControl cc = cacheControlObject.createCacheControl(configService);

        Message responseMessage = new Message();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();
        OutletInformation outlet = OutletInformationDAO.loadOutlet(loggedUser.getRelationId());

        ArrayList<BrandCategory> lstBrandCategory = BrandCategoryDAO.loadList(outlet.getGeoLocation().getLocationId(), outlet.getOutletId());
        if (lstBrandCategory.size() > 0) {
            lstBrandCategory = BrandCategoryDAO.clearSets(lstBrandCategory);
        }

        if (lstBrandCategory.isEmpty()) {
            responseMessage.setStatus(ServiceStatus.NO_DATA_AVAILABLE.getValue());
            responseMessage.setStatusText(ServiceStatus.NO_DATA_AVAILABLE.getStatusText());
            Response.ResponseBuilder builder = Response.ok(responseMessage, "application/json");
            builder.cacheControl(cc);
            return builder.build();
        }

        responseMessage.setLstBrandCategory(lstBrandCategory);
        responseMessage.setImageBaseURL(ConfigDAO.load(Configuration.IMAGE_BASE_URL.getId()).getConfValue());

        responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
        responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());

        Response.ResponseBuilder builder = Response.ok(responseMessage, "application/json");
        builder.cacheControl(cc);
        return builder.build();

    }

    @GET
    @Path("/getSubCategories")
    @ServiceSecurity(security = Security.RESTRICTED)
    public Response getSubCategories(
            @QueryParam("categoryid") Integer categoryId,
            @QueryParam("searchtext") String searchText
    ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, ParseException {
        Map<Integer, ConfigService> configServiceMap = (Map<Integer, ConfigService>) servletContext.getAttribute("configServiceMap");
        ConfigService configService = configServiceMap.get(ConfigurationService.GET_SUB_CATEGORIES_OR.getId());
        CacheController cacheControlObject = new CacheController();
        CacheControl cc = cacheControlObject.createCacheControl(configService);

        Message responseMessage = new Message();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();
        OutletInformation outlet = OutletInformationDAO.loadOutlet(loggedUser.getRelationId());

        if (!Objects.isNull(categoryId)) {
            if (!Utilities.validateRequiredParameter(categoryId)) {
                throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
            }
        }
        if (!Objects.isNull(searchText)) {
            if (!Utilities.validateRequiredParameter(searchText)) {
                throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
            }
        }

        ArrayList<BrandProduct> lstBrandProduct = BrandProductDAO.loadList(outlet.getGeoLocation().getLocationId(), categoryId, Objects.isNull(searchText) ? "" : searchText, outlet.getOutletId());

        if (lstBrandProduct.size() > 0) {
            lstBrandProduct = BrandProductDAO.clearSets(lstBrandProduct);
        }

        if (lstBrandProduct.isEmpty()) {
            responseMessage.setStatus(ServiceStatus.NO_DATA_AVAILABLE.getValue());
            responseMessage.setStatusText(ServiceStatus.NO_DATA_AVAILABLE.getStatusText());
            Response.ResponseBuilder builder = Response.ok(responseMessage, "application/json");
            builder.cacheControl(cc);
            return builder.build();
        }

        responseMessage.setLstBrandProduct(lstBrandProduct);
        responseMessage.setImageBaseURL(ConfigDAO.load(Configuration.IMAGE_BASE_URL.getId()).getConfValue());

        responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
        responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());

        Response.ResponseBuilder builder = Response.ok(responseMessage, "application/json");
        builder.cacheControl(cc);
        return builder.build();

    }

    @GET
    @Path("/getSkus")
    @ServiceSecurity(security = Security.RESTRICTED)
    public Response getSkus(
            @QueryParam("brandid") Integer brandId,
            @QueryParam("categoryid") Integer categoryId,
            @QueryParam("promotionavailable") Boolean promotionAvailable,
            @QueryParam("searchtext") String searchText,
            @QueryParam("startindex") Integer startIndex,
            @QueryParam("versionnumber") String versionNumber,                       
            @QueryParam("skuid") Integer skuId
    ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, ParseException {

        Map<Integer, ConfigService> configServiceMap = (Map<Integer, ConfigService>) servletContext.getAttribute("configServiceMap");
        ConfigService configService = configServiceMap.get(ConfigurationService.GET_SKUS_OR.getId());
        CacheController cacheControlObject = new CacheController();
        CacheControl cc = cacheControlObject.createCacheControl(configService);
        
        cc.setMaxAge(60);
        cc.setPrivate(true);
        cc.setNoStore(true);

        Message responseMessage = new Message();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();
        OutletInformation outlet = OutletInformationDAO.loadOutlet(loggedUser.getRelationId());

//        if (!Utilities.validateRequiredParameter(brandId)) {
//            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
//        }

        if (!Objects.isNull(promotionAvailable)) {
            if (promotionAvailable != true && promotionAvailable != false) {
                throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
            }
        } else {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        }

        if (!Objects.isNull(searchText)) {
            if (!Utilities.validateRequiredParameter(searchText)) {
                throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
            }
        }
        
        if (Objects.isNull(startIndex)) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        }

      //  ArrayList<BrandVariantdetail> lstBrandVariantDetail = BrandVariantDetailDAO.loadBrandVariantDetailList(brandId, promotionAvailable, searchText, outlet.getGeoLocation().getLocationId(), outlet.getOutletId(), skuId);
        ArrayList<BrandVariantdetail> lstBrandVariantDetail = BrandVariantDetailDAO.loadBrandVariantDetailList(brandId,categoryId,promotionAvailable, searchText, outlet.getGeoLocation().getLocationId(), outlet.getOutletId(), skuId, startIndex);
        ArrayList<Integer> skuIds = new ArrayList<>();
        ArrayList<TpDetail> lstTpDetail = new ArrayList<>();
        if (lstBrandVariantDetail.size() > 0) {
            for (BrandVariantdetail sku : lstBrandVariantDetail) {
                skuIds.add(sku.getVariantDetailId());
            }
            if (skuIds.size() > 0) {
                lstTpDetail = TpDetailDAO.loadList(skuIds.toArray(new Integer[0]), outlet.getGeoLocation().getLocationId());
            }
            if (!Objects.isNull(versionNumber)) {
                ArrayList<DistBrandmapping> lstBrandDistributors = DistBrandmappingDAO.loadSkusDistributers(skuIds.toArray(new Integer[0]), outlet.getGeoLocation().getLocationId(), outlet.getOutletId());
                String skuIdsCsv = getVariantDetailIds(lstBrandVariantDetail);
                String distIdsCsv = getDistIds(lstBrandDistributors);
                ArrayList<Object[]> lstSkusAvailability = BrandVariantDetailDAO.loadSkusAvailablility(skuIdsCsv, distIdsCsv);

                for (BrandVariantdetail brandVariantdetail : lstBrandVariantDetail) {
                    boolean flag = true;
                    for (Object[] object : lstSkusAvailability) {
                        if (!Objects.isNull(object)) {
                            if (!Objects.isNull(object[0]) && Integer.parseInt(object[0].toString()) == brandVariantdetail.getVariantDetailId()) {
                                brandVariantdetail.setAvailableCount(Objects.isNull(object[3]) ? 0.0 : Double.valueOf(object[3].toString()));
                                flag = false;
                            }
                        }
                    }
                    if (flag) {
                        brandVariantdetail.setAvailableCount(0.0);
                    }
                }
            }
            if (lstTpDetail.size() > 0) {
                for (BrandVariantdetail brandVariantDetail : lstBrandVariantDetail) {
                    ArrayList<TpDetail> lstAllPromotions = new ArrayList<>();
                    boolean flag = false;
                    for (TpDetail tpDetail : lstTpDetail) {
                        if (tpDetail.getTpInformation().getBrandVariantdetail().getVariantDetailId().equals(brandVariantDetail.getVariantDetailId())) {
                            //brandVariantDetail.setLstAllPromotions(lstTpDetail);
                            if (!Objects.isNull(versionNumber)) {
                                if (brandVariantDetail.getAvailableCount() >= tpDetail.getOrderQty()
                                        && brandVariantDetail.getMaximumOrderQuantity() >= tpDetail.getOrderQty()) {
                                    lstAllPromotions.add(tpDetail);
                                    flag = true;
                                }
                            } else {
                                lstAllPromotions.add(tpDetail);
                                flag = true;
                            }

                        }
                    }
                    if (flag) {
                        brandVariantDetail.setLstAllPromotions(lstAllPromotions);
                    }
                }
            }

            lstBrandVariantDetail = BrandVariantDetailDAO.clearSets(lstBrandVariantDetail);
        }

        if (lstBrandVariantDetail.isEmpty()) {
            responseMessage.setStatus(ServiceStatus.NO_DATA_AVAILABLE.getValue());
            responseMessage.setStatusText(ServiceStatus.NO_DATA_AVAILABLE.getStatusText());
            Response.ResponseBuilder builder = Response.ok(responseMessage, "application/json");
            builder.cacheControl(cc);
            return builder.build();
        }
        
        if (lstBrandVariantDetail.size() == Integer.parseInt(ConfigDAO.load(Configuration.MAX_RECORDS.getId()).getConfValue())) {
            responseMessage.setHasMoreData(true);
        } else {
            responseMessage.setHasMoreData(false);
        }

        responseMessage.setLstBrandVariantDetail(lstBrandVariantDetail);
        responseMessage.setImageBaseURL(ConfigDAO.load(Configuration.IMAGE_BASE_URL.getId()).getConfValue());

        responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
        responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());

        Response.ResponseBuilder builder = Response.ok(responseMessage, "application/json");
        builder.cacheControl(cc);
        return builder.build();
    }

    @GET
    @Path("/getMCSB")
    @ServiceSecurity(security = Security.RESTRICTED)
    public Response getMCSB(
            @QueryParam("searchtext") String searchText
    ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, ParseException {

        Map<Integer, ConfigService> configServiceMap = (Map<Integer, ConfigService>) servletContext.getAttribute("configServiceMap");
        ConfigService configService = configServiceMap.get(ConfigurationService.GET_MCSB_OR.getId());
        CacheController cacheControlObject = new CacheController();
        CacheControl cc = cacheControlObject.createCacheControl(configService);

        Message responseMessage = new Message();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();

        Integer outletId = loggedUser.getRelationId();
        OutletInformation outlet = null;

        if (!Utilities.validateRequiredParameter(searchText)) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        }

        if (!Objects.isNull(outletId)) {
            outlet = OutletInformationDAO.load(outletId);
        }

        ArrayList<ManInformation> lstManInformation = ManInformationDAO.loadManList(searchText, outlet.getGeoLocation().getLocationId(), outletId);
        ArrayList<BrandInformation> lstBrandInformation = BrandInformationDAO.loadList(searchText, outlet.getGeoLocation().getLocationId(), outletId);
        ArrayList<BrandCategory> lstBrandCategory = BrandCategoryDAO.loadlist(searchText, outlet.getGeoLocation().getLocationId(), outletId);
        ArrayList<BrandProduct> lstBrandProduct = BrandProductDAO.loadList(searchText, outlet.getGeoLocation().getLocationId(), outletId);

        if (!lstManInformation.isEmpty()) {
            lstManInformation = ManInformationDAO.clearSets(lstManInformation);
        }

        if (!lstBrandInformation.isEmpty()) {
            lstBrandInformation = BrandInformationDAO.clearSets(lstBrandInformation);
        }

        if (!lstBrandCategory.isEmpty()) {
            lstBrandCategory = BrandCategoryDAO.clearSets(lstBrandCategory);
        }

        if (!lstBrandProduct.isEmpty()) {
            lstBrandProduct = BrandProductDAO.clearSets(lstBrandProduct);
        }

        if (lstBrandCategory.isEmpty() && lstBrandInformation.isEmpty() && lstBrandProduct.isEmpty() && lstManInformation.isEmpty()) {
            responseMessage.setStatus(ServiceStatus.NO_DATA_AVAILABLE.getValue());
            responseMessage.setStatusText(ServiceStatus.NO_DATA_AVAILABLE.getStatusText());
            Response.ResponseBuilder builder = Response.ok(responseMessage, "application/json");
            builder.cacheControl(cc);
            return builder.build();
        }

        responseMessage.setLstManInformation(lstManInformation);
        responseMessage.setLstBrandInformation(lstBrandInformation);
        responseMessage.setLstBrandCategory(lstBrandCategory);
        responseMessage.setLstBrandProduct(lstBrandProduct);
        responseMessage.setImageBaseURL(ConfigDAO.load(Configuration.IMAGE_BASE_URL.getId()).getConfValue());

        responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
        responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());

        Response.ResponseBuilder builder = Response.ok(responseMessage, "application/json");
        builder.cacheControl(cc);
        return builder.build();

    }

    @GET
    @Path("/getFrequentSkus")
    @ServiceSecurity(security = Security.RESTRICTED)
    public Response getFrequentSkus(
            @QueryParam("searchtext") String searchText,
            @QueryParam("startindex") Integer startIndex,
            @QueryParam("versionnumber") String versionNumber
    ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, ParseException {

        Map<Integer, ConfigService> configServiceMap = (Map<Integer, ConfigService>) servletContext.getAttribute("configServiceMap");
        ConfigService configService = configServiceMap.get(ConfigurationService.GET_FREQUENT_SKUS_OR.getId());
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

        if (!Objects.isNull(searchText)) {
            if (!Utilities.validateRequiredParameter(searchText)) {
                throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
            }
        }

        if (Objects.isNull(startIndex)) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        }

        if (!Objects.isNull(outletId)) {
            outlet = OutletInformationDAO.load(outletId);
        }

        Date dateFrom = new DateTime().minusMonths(24).toDate();
        ArrayList<BrandVariantdetail> lstBrandVariantdetail = BrandVariantDetailDAO.loadFrequentSKUList(outletId, outlet.getGeoLocation().getLocationId(), startIndex, dateFrom, searchText);

        ArrayList<Integer> skuIds = new ArrayList<>();
        ArrayList<TpDetail> lstTpDetail = new ArrayList<>();
        if (lstBrandVariantdetail.size() > 0) {
            for (BrandVariantdetail sku : lstBrandVariantdetail) {
                skuIds.add(sku.getVariantDetailId());
            }
            if (skuIds.size() > 0) {
                lstTpDetail = TpDetailDAO.loadList(skuIds.toArray(new Integer[0]), outlet.getGeoLocation().getLocationId());
            }
            if (!Objects.isNull(versionNumber)) {
                if (lstBrandVariantdetail.size() > 0) {
                    ArrayList<DistBrandmapping> lstBrandDistributors = DistBrandmappingDAO.loadSkusDistributers(skuIds.toArray(new Integer[0]), outlet.getGeoLocation().getLocationId(), outlet.getOutletId());
                    String skuIdsCsv = getVariantDetailIds(lstBrandVariantdetail);
                    String distIdsCsv = getDistIds(lstBrandDistributors);
                    ArrayList<Object[]> lstSkusAvailability = BrandVariantDetailDAO.loadSkusAvailablility(skuIdsCsv, distIdsCsv);

                    for (BrandVariantdetail brandVariantdetail : lstBrandVariantdetail) {
                        boolean flag = true;
                        for (Object[] object : lstSkusAvailability) {
                            if (!Objects.isNull(object)) {
                                if (!Objects.isNull(object[0]) && Integer.parseInt(object[0].toString()) == brandVariantdetail.getVariantDetailId()) {
                                    brandVariantdetail.setAvailableCount(Objects.isNull(object[3]) ? 0.0 : Double.valueOf(object[3].toString()));
                                    flag = false;
                                }
                            }
                        }
                        if (flag) {
                            brandVariantdetail.setAvailableCount(0.0);
                        }
                    }
                }
            }
            if (lstTpDetail.size() > 0) {
                for (BrandVariantdetail brandVariantDetail : lstBrandVariantdetail) {
                    ArrayList<TpDetail> lstAllPromotions = new ArrayList<>();
                    boolean flag = false;
                    for (TpDetail tpDetail : lstTpDetail) {
                        if (tpDetail.getTpInformation().getBrandVariantdetail().getVariantDetailId().equals(brandVariantDetail.getVariantDetailId())) {
                            if (!Objects.isNull(versionNumber)) {
                                if (brandVariantDetail.getAvailableCount() >= tpDetail.getOrderQty()
                                        && brandVariantDetail.getMaximumOrderQuantity() >= tpDetail.getOrderQty()) {
                                    lstAllPromotions.add(tpDetail);
                                    flag = true;
                                }
                            } else {
                                lstAllPromotions.add(tpDetail);
                                flag = true;
                            }
                        }
                    }
                    if (flag) {
                        brandVariantDetail.setLstAllPromotions(lstAllPromotions);
                    }
                }
            }
        }

        if (!lstBrandVariantdetail.isEmpty()) {
            lstBrandVariantdetail = BrandVariantDetailDAO.clearSets(lstBrandVariantdetail);
        } else {
            responseMessage.setStatus(ServiceStatus.NO_DATA_AVAILABLE.getValue());
            responseMessage.setStatusText(ServiceStatus.NO_DATA_AVAILABLE.getStatusText());
            Response.ResponseBuilder builder = Response.ok(responseMessage, "application/json");
            builder.cacheControl(cc);
            return builder.build();
        }

        if (lstBrandVariantdetail.size() == Integer.parseInt(ConfigDAO.load(Configuration.MAX_RECORDS.getId()).getConfValue())) {
            responseMessage.setHasMoreData(true);
        } else {
            responseMessage.setHasMoreData(false);
        }

        responseMessage.setLstBrandVariantDetail(lstBrandVariantdetail);

        responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
        responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());

        Response.ResponseBuilder builder = Response.ok(responseMessage, "application/json");
        builder.cacheControl(cc);
        return builder.build();

    }

    @GET
    @Path("/getBrandGroups")
    @ServiceSecurity(security = Security.RESTRICTED)
    public Response getBrandGroups(
            @QueryParam("manufactureid") Integer manufactureId,
            @QueryParam("startindex") Integer startIndex
    ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, ParseException {

        if (Objects.isNull(startIndex)) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        }

        Map<Integer, ConfigService> configServiceMap = (Map<Integer, ConfigService>) servletContext.getAttribute("configServiceMap");
        ConfigService configService = configServiceMap.get(ConfigurationService.GET_BRAND_GROUPS_OR.getId());
        CacheController cacheControlObject = new CacheController();
        CacheControl cc = cacheControlObject.createCacheControl(configService);
        OutletInformation outlet = null;

        Message responseMessage = new Message();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();

        Integer outletId = loggedUser.getRelationId();

        if (!Objects.isNull(outletId)) {
            outlet = OutletInformationDAO.load(outletId);
        }

        if (!Objects.isNull(manufactureId)) {
            if (!Utilities.validateRequiredParameter(manufactureId)) {
                throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
            }
        }

        ArrayList<BrandGroup> lstBrandGroup = BrandGroupDAO.loadList(manufactureId, outletId, outlet.getGeoLocation().getLocationId(), startIndex);

        if (!lstBrandGroup.isEmpty()) {
            lstBrandGroup = BrandGroupDAO.clearSets(lstBrandGroup);
        } else {
            responseMessage.setStatus(ServiceStatus.NO_DATA_AVAILABLE.getValue());
            responseMessage.setStatusText(ServiceStatus.NO_DATA_AVAILABLE.getStatusText());
            Response.ResponseBuilder builder = Response.ok(responseMessage, "application/json");
            builder.cacheControl(cc);
            return builder.build();
        }

        responseMessage.setLstBrandGroup(lstBrandGroup);

        responseMessage.setImageBaseURL(ConfigDAO.load(Configuration.IMAGE_BASE_URL.getId()).getConfValue());
        responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
        responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());

        Response.ResponseBuilder builder = Response.ok(responseMessage, "application/json");
        builder.cacheControl(cc);
        return builder.build();

    }

    private UserActivity buildUserActivity(UserInformation loggedUser) {
        UserActivity userActivity = new UserActivity();

        userActivity.setUserInformationByInsertId(loggedUser);
        userActivity.setActivityDate(new Date());
        userActivity.setGenTypedetailByActivityTypeId(GenTypedetailDAO.build(OutletActivityType.OUTLET_ORDER_BOOKED_SMS_NOTIFICATION.getId()));
        userActivity.setGenTypedetailByActivityStatusId(GenTypedetailDAO.build(ActivityStatusType.CLOSED.getId()));
        userActivity.setOutletInformation(OutletInformationDAO.build(loggedUser.getRelationId()));
        userActivity.setIsActive(true);

        return userActivity;
    }

    private Double getOrderTotal(ArrayList<OrderDetail> lstOrderItems) {
        Double totalOrderValue = 0.0;

        for (OrderDetail orderDetail : lstOrderItems) {
            totalOrderValue += orderDetail.getOrderQty() * orderDetail.getOrderRate();
        }

        return totalOrderValue;
    }

    private BrandVariantdetail getModifiedSku(OrderDetail orderDetail, OutletInformation outlet, String versionNumber) throws ParseException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        ArrayList<BrandVariantdetail> lstBrandVariantDetail = BrandVariantDetailDAO.loadBrandVariantDetailList(orderDetail.getBrandInformation().getBrandId(), Boolean.FALSE, "", outlet.getGeoLocation().getLocationId(), outlet.getOutletId(), orderDetail.getBrandVariantdetail().getVariantDetailId());

        ArrayList<Integer> skuIds = new ArrayList<>();
        ArrayList<TpDetail> lstTpDetail = new ArrayList<>();
        if (lstBrandVariantDetail.size() > 0) {
            for (BrandVariantdetail sku : lstBrandVariantDetail) {
                skuIds.add(sku.getVariantDetailId());
            }
            if (skuIds.size() > 0) {
                lstTpDetail = TpDetailDAO.loadList(skuIds.toArray(new Integer[0]), outlet.getGeoLocation().getLocationId());
            }
            if (!Objects.isNull(versionNumber)) {
                ArrayList<DistBrandmapping> lstBrandDistributors = DistBrandmappingDAO.loadSkusDistributers(skuIds.toArray(new Integer[0]), outlet.getGeoLocation().getLocationId(), outlet.getOutletId());
                String skuIdsCsv = getVariantDetailIds(lstBrandVariantDetail);
                String distIdsCsv = getDistIds(lstBrandDistributors);
                ArrayList<Object[]> lstSkusAvailability = BrandVariantDetailDAO.loadSkusAvailablility(skuIdsCsv, distIdsCsv);

                for (BrandVariantdetail brandVariantdetail : lstBrandVariantDetail) {
                    boolean flag = true;
                    for (Object[] object : lstSkusAvailability) {
                        if (!Objects.isNull(object)) {
                            if (!Objects.isNull(object[0]) && Integer.parseInt(object[0].toString()) == brandVariantdetail.getVariantDetailId()) {
                                brandVariantdetail.setAvailableCount(Objects.isNull(object[3]) ? 0.0 : Double.valueOf(object[3].toString()));
                                flag = false;
                            }
                        }
                    }
                    if (flag) {
                        brandVariantdetail.setAvailableCount(0.0);
                    }
                }
            }
            if (lstTpDetail.size() > 0) {
                for (BrandVariantdetail brandVariantDetail : lstBrandVariantDetail) {
                    ArrayList<TpDetail> lstAllPromotions = new ArrayList<>();
                    boolean flag = false;
                    for (TpDetail tpDetail : lstTpDetail) {
                        if (tpDetail.getTpInformation().getBrandVariantdetail().getVariantDetailId().equals(brandVariantDetail.getVariantDetailId())) {
                            //brandVariantDetail.setLstAllPromotions(lstTpDetail);
                            if (!Objects.isNull(versionNumber)) {
                                if (brandVariantDetail.getAvailableCount() >= tpDetail.getOrderQty()
                                        && brandVariantDetail.getMaximumOrderQuantity() >= tpDetail.getOrderQty()) {
                                    lstAllPromotions.add(tpDetail);
                                    flag = true;
                                }
                            } else {
                                lstAllPromotions.add(tpDetail);
                                flag = true;
                            }

                        }
                    }
                    if (flag) {
                        brandVariantDetail.setLstAllPromotions(lstAllPromotions);
                    }
                }
            }

            lstBrandVariantDetail = BrandVariantDetailDAO.clearSets(lstBrandVariantDetail);
            return lstBrandVariantDetail.get(0);
        } else {
            return orderDetail.getBrandVariantdetail();
        }

    }
    
    @GET
    @Path("/getOrderTrackingStatusList")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ServiceSecurity(security = Security.RESTRICTED)
    public Message getOrderTrackingStatusList(Message requestMessage) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, ParseException {
        Message responseMessage = new Message();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();
        //OutletInformation outlet = OutletInformationDAO.loadOutlet(loggedUser.getRelationId());
        ArrayList<OrderTracking> lstOrderTracking = null;

        lstOrderTracking = OrderTrackingDAO.getOrderTracking(loggedUser.getRelationId());

        if (!lstOrderTracking.isEmpty()) {
            responseMessage.setOrderTrackingList(lstOrderTracking);
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
    @Path("/getOrderDetailListForComplain")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ServiceSecurity(security = Security.RESTRICTED)
    public Message getOrderDetailListForComplain(Message requestMessage) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, ParseException {
        Message responseMessage = new Message();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();
        OutletInformation outlet = OutletInformationDAO.loadOutlet(loggedUser.getRelationId());
        ArrayList<OrderDetail> lstOrderDetail = null;

        if (Objects.isNull(requestMessage)) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        }

        if (Objects.isNull(requestMessage.getOrderId()) || Objects.equals(requestMessage.getOrderId(), 0)) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        }

           ArrayList<Object[]> lstRows = OrderDetailDAO.loadComplainOrderList(null, requestMessage.getOrderId(), outlet.getOutletId(), null);
       

        if (!lstRows.isEmpty()) {
            lstOrderDetail = convertObjectToOrderDetail(lstRows);
            OrderDetailDAO.clearSets(lstOrderDetail);
        } else {
            responseMessage.setStatus(ServiceStatus.NO_DATA_AVAILABLE.getValue());
            responseMessage.setStatusText(ServiceStatus.NO_DATA_AVAILABLE.getStatusText());
            return responseMessage;
        }

        responseMessage.setImageBaseURL(ConfigDAO.load(Configuration.IMAGE_BASE_URL.getId()).getConfValue());
        responseMessage.setLstOrderDetail(lstOrderDetail);
        responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
        responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());

        return responseMessage;

    }
    
    
        
    @POST
    @Path("/cancelorder")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ServiceSecurity(security = Security.RESTRICTED)
    public Message cancelOrder(Message requestMessage) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, ParseException {
        Message responseMessage = new Message();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();
        OutletInformation outlet = OutletInformationDAO.loadOutlet(loggedUser.getRelationId());
        ArrayList<OrderDetail> lstOrderDetail = null;
        ArrayList<OrderDistmapping> orderDist = null;
        

        if (Objects.isNull(requestMessage)) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        }

        if (Objects.isNull(requestMessage.getOrderId()) || Objects.equals(requestMessage.getOrderId(), 0)) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        }
        
        if (Objects.isNull(requestMessage.getLatitude())) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        }
        
        if (Objects.isNull(requestMessage.getLongitude())) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        } 
 
           OrderDistmapping orderDistmapping = new OrderDistmapping();
           
            orderDistmapping = OrderDistmappingDAO.loadByOrderId(requestMessage.getOrderId());
           if(orderDistmapping.getGenTypedetail().getTypeDetailId()==87)
           {
            ArrayList<Object[]> lstRows = OrderDetailDAO.orderDetail(requestMessage.getOrderId());   
            if (!lstRows.isEmpty()) {
            lstOrderDetail = convertObjectToOrderDetailObject(lstRows,requestMessage.getOrderId());
            try
            {
           
            orderDistmapping.setEditDate(new Date());
            
            GenTypedetail genTypeDetail = new  GenTypedetail();
            genTypeDetail.setTypeDetailId(OrderStatus.CANCEL.getId());
            
            orderDistmapping.setGenTypedetail(genTypeDetail);
            orderDistmapping.setUserInformationByEditId(loggedUser);
            OrderDetailDAO.modify(lstOrderDetail);
            OrderDistmappingDAO.modifyOrderDistMappingObject(orderDistmapping);
            OrderActivity orderActivity = new OrderActivity();
            orderActivity.setGenTypedetailByStatusId(genTypeDetail);
            orderActivity.setGenTypedetailByTypeId(genTypeDetail);
            orderActivity.setScheduledDate(new Date());
            orderActivity.setOrderDistmapping(orderDistmapping);
            orderActivity.setLatitude(requestMessage.getLatitude());
            orderActivity.setLongitude(requestMessage.getLongitude());          
            orderActivity.setUserInformationByInsertId(loggedUser);
            orderActivity.setInsertDate(new Date());
            OrderDistmappingDAO.saveOrderActivity(orderActivity);
                    
        
           }
            catch (Exception e){
        
        responseMessage.setStatus(ServiceStatus.FAILED.getValue());
        responseMessage.setStatusText(e.getMessage() + " : " + e);
    
            }
        }
             else {
            responseMessage.setStatus(ServiceStatus.NO_DATA_AVAILABLE.getValue());
            responseMessage.setStatusText(ServiceStatus.NO_DATA_AVAILABLE.getStatusText());
            return responseMessage;
        }
            
      }     
           else
           {
                throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Order is not in a  new state").build());
           }
           
          
        responseMessage.setLstOrderDetail(lstOrderDetail);
        responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
        responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());

        return responseMessage;

    }
    
    private ArrayList<OrderDetail> convertObjectToOrderDetailObject(ArrayList<Object[]> lstRows,long orderId) throws ParseException {
        ArrayList<OrderDetail> lstOrderDetail = new ArrayList<>();
      //  DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");

        for (Object[] object : lstRows) {
           OrderDetail orderDetail = new OrderDetail();
           orderDetail.setOrderDetailId(Long.parseLong(object[0].toString()));
           orderDetail.setOrderQty(0.00);
           orderDetail.setOrderRate(0.00);
           orderDetail.setOrderDiscount(0.00);
           orderDetail.setOrderTotal(0.00);
           orderDetail.setDiscountTprate(0.00);
           orderDetail.setOrderStatusId(OrderStatus.CANCEL.getId());
           orderDetail.setInitialOrderQty(Double.parseDouble(object[10].toString()));
           orderDetail.setOrderDetailUuid(object[2].toString());           
           orderDetail.setGenTypedetailByOrderDetailTypeId(new GenTypedetail(Integer.valueOf(object[19].toString())));
           orderDetail.setIsActive(true);   
           
           if(object[29] == null)
            orderDetail.setComment(null);   
           else
            orderDetail.setComment(object[29].toString());
           
           orderDetail.setUserInformationByInsertId(new UserInformation(Integer.valueOf(object[22].toString())));
           orderDetail.setInsertDate( new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(object[23].toString()) );
           
           orderDetail.setUserInformationByEditId(loggedUser);
           orderDetail.setEditDate(new Date());
           orderDetail.setOrderDistmapping(OrderDistmappingDAO.build(Long.valueOf(object[3].toString())));
           OrderInformation orderInformation = new  OrderInformation();
           orderInformation.setOrderId(orderId);
           orderDetail.setOrderInformation(orderInformation);
           orderDetail.setManInformation(ManInformationDAO.build(Integer.valueOf(object[5].toString())));
           orderDetail.setBrandInformation(BrandInformationDAO.build(Integer.valueOf(object[6].toString())));
           orderDetail.setBrandVariantdetail(BrandVariantDetailDAO.build(Integer.valueOf(object[8].toString())));
           orderDetail.setBrandVariant(BrandVariantDAO.build(Integer.valueOf(object[7].toString())));
           orderDetail.setOutletInformation(OutletInformationDAO.build(Integer.valueOf(object[4].toString())));
           
           
           

            lstOrderDetail.add(orderDetail);
        }

        return lstOrderDetail;
    }

    @GET
    @Path("/getBrandSubCategory")
    @ServiceSecurity(security = Security.RESTRICTED)
    public Response getBrandSubCategory(
             @QueryParam("brandid") Integer brandId,
             @QueryParam("categoryid") Integer categoryId,           
             @QueryParam("searchtext") String searchText
    ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, ParseException {
        Map<Integer, ConfigService> configServiceMap = (Map<Integer, ConfigService>) servletContext.getAttribute("configServiceMap");
        ConfigService configService = configServiceMap.get(ConfigurationService.GET_SUB_CATEGORIES_OR.getId());
        CacheController cacheControlObject = new CacheController();
        CacheControl cc = cacheControlObject.createCacheControl(configService);

        Message responseMessage = new Message();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();
        OutletInformation outlet = OutletInformationDAO.loadOutlet(loggedUser.getRelationId());

        if (!Objects.isNull(categoryId)) {
            if (!Utilities.validateRequiredParameter(categoryId)) {
                throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
            }
        }
        if (!Objects.isNull(searchText)) {
            if (!Utilities.validateRequiredParameter(searchText)) {
                throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
            }
        }
        
         if (!Objects.isNull(brandId)) {
            if (!Utilities.validateRequiredParameter(brandId)) {
                throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
            }
        }

        ArrayList<BrandProduct> lstBrandProduct = BrandProductDAO.loadList(outlet.getGeoLocation().getLocationId(), categoryId, Objects.isNull(searchText) ? "" : searchText, outlet.getOutletId(),brandId);

        if (lstBrandProduct.size() > 0) {
            lstBrandProduct = BrandProductDAO.clearSets(lstBrandProduct);
        }

        if (lstBrandProduct.isEmpty()) {
            responseMessage.setStatus(ServiceStatus.NO_DATA_AVAILABLE.getValue());
            responseMessage.setStatusText(ServiceStatus.NO_DATA_AVAILABLE.getStatusText());
            Response.ResponseBuilder builder = Response.ok(responseMessage, "application/json");
            builder.cacheControl(cc);
            return builder.build();
        }

        responseMessage.setLstBrandProduct(lstBrandProduct);
        responseMessage.setImageBaseURL(ConfigDAO.load(Configuration.IMAGE_BASE_URL.getId()).getConfValue());

        responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
        responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());

        Response.ResponseBuilder builder = Response.ok(responseMessage, "application/json");
        builder.cacheControl(cc);
        return builder.build();

    }
    
    @GET
    @Path("/getOrderStatus")
    @Produces(MediaType.APPLICATION_JSON)
    @ServiceSecurity(security = Security.RESTRICTED)
    public Message getOrderStatus() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, ParseException {
        Message responseMessage = new Message();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();
       

         ArrayList<GenTypedetail> lstOtherStatus = GenTypedetailDAO.loadList(new Integer [] {MasterType.ORDER_STATUS.getId()});      

         Config config = ConfigDAO.load(Configuration.IMAGE_BASE_URL.getId());
  
        
        if (!lstOtherStatus.isEmpty()) {
            responseMessage.setImageBaseURL(config.getConfValue());
            responseMessage.setLstGenTypedetails(GenTypedetailDAO.clearSets(lstOtherStatus));
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
    @Path("/getOrderListForComplaint")
    @Produces(MediaType.APPLICATION_JSON)
    @ServiceSecurity(security = Security.RESTRICTED)
    public ComplaintResponse getOrderListForComplaint() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, ParseException {     
        //Message responseMessage = new Message();
        ComplaintResponse complaintResponse = new ComplaintResponse();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();
        OutletInformation outlet = OutletInformationDAO.loadOutlet(loggedUser.getRelationId());

        ArrayList<Object[]> lstOrderInfo = OrderInformationDAO.loadListForComplaint(outlet.getOutletId());

        complaintResponse.setComplaintResponseList(convertObjectToOrderInformationForComplain(lstOrderInfo));
        complaintResponse.setStatus(ServiceStatus.SUCCESS.getValue());
        complaintResponse.setStatusText(ServiceStatus.SUCCESS.getStatusText());

        return complaintResponse;

    }
    
    private ArrayList<ComplaintResponseDto> convertObjectToOrderInformationForComplain(ArrayList<Object[]> lstRows) throws ParseException {
        ArrayList<ComplaintResponseDto> complaintResponseList = new ArrayList<>();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");

        for (Object[] object : lstRows) {
            ComplaintResponseDto complaintResponseDto = new ComplaintResponseDto();
            if (!Objects.isNull(object[0])) {
                complaintResponseDto.setPreferredDeliveryDate(new SimpleDateFormat("yyyy-MM-dd").parse(object[0].toString()));
            }
            if (!Objects.isNull(object[1])) {
                complaintResponseDto.setOrderId(Long.valueOf(object[1].toString()));
            }
            if (!Objects.isNull(object[2])) {
                complaintResponseDto.setOrderDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(object[2].toString()));
            }
            if (!Objects.isNull(object[3])) {
                complaintResponseDto.setOrderTotalAmount(Double.valueOf(object[3].toString()));
            }
            if (!Objects.isNull(object[6]) ) {
                complaintResponseDto.setIsModified(Boolean.getBoolean(object[6].toString()));
            }            
            if (!Objects.isNull(object[8]) ) {
                complaintResponseDto.setComplaintRegFlag(Boolean.getBoolean(object[8].toString()));
            }

            complaintResponseList.add(complaintResponseDto);
        }

        return complaintResponseList;
    }
    
    @GET
    @Path("/retrievecancelorderreasonlist")
    @Produces(MediaType.APPLICATION_JSON)
    @ServiceSecurity(security = Security.RESTRICTED)
    public Message retrieveCanceleOrderReasonlist() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchAlgorithmException, UnsupportedEncodingException, ParseException {
        Message responseMessage = new Message();
        Utilities util = new Utilities();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();
        ArrayList<GenTypedetail> lstGenTypedetails = GenTypedetailDAO.loadList(new Integer[]{MasterType.RETAILER_APP_ORDER_CANCELLATION_REASONS.getId()});
        responseMessage.setLstGenTypedetails(GenTypedetailDAO.clearSets(lstGenTypedetails));
        
        responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
        responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());
        return responseMessage;
    }
        
    @GET
    @Path("/getorderagaindetaillist")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ServiceSecurity(security = Security.RESTRICTED)
    public Message getOrderAgainDetailList(@QueryParam("orderId") Long orderId) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, ParseException {
        Message responseMessage = new Message();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();
        OutletInformation outlet = OutletInformationDAO.loadOutlet(loggedUser.getRelationId());
        ArrayList<OrderDetail> lstOrderDetail = null;

        if(orderId == null || orderId == 0)
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());

        ArrayList<Object[]> lstRows = OrderDetailDAO.loadList(null, orderId, outlet.getOutletId(), null);

        if (!lstRows.isEmpty()) {
            lstOrderDetail = convertObjectToOrderDetail(lstRows);
            OrderDetailDAO.clearSets(lstOrderDetail);
        } else {
            responseMessage.setStatus(ServiceStatus.NO_DATA_AVAILABLE.getValue());
            responseMessage.setStatusText(ServiceStatus.NO_DATA_AVAILABLE.getStatusText());
            return responseMessage;
        }
        responseMessage.setLstOrderDetail(lstOrderDetail);
        responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
        responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());

        return responseMessage;

    }
    
    @GET
    @Path("/getorderlistskus")
    @ServiceSecurity(security = Security.RESTRICTED)
    public Response getOrderListkus(
            @QueryParam("promotionavailable") Boolean promotionAvailable,
            @QueryParam("searchtext") String searchText,
            @QueryParam("startindex") Integer startIndex,
            @QueryParam("versionnumber") String versionNumber,                       
            @QueryParam("skuidlist") String skuIdList
    ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, ParseException {

        Map<Integer, ConfigService> configServiceMap = (Map<Integer, ConfigService>) servletContext.getAttribute("configServiceMap");
        ConfigService configService = configServiceMap.get(ConfigurationService.GET_SKUS_OR.getId());
        CacheController cacheControlObject = new CacheController();
        CacheControl cc = cacheControlObject.createCacheControl(configService);
        
        cc.setMaxAge(60);
        cc.setPrivate(true);
        cc.setNoStore(true);

        Message responseMessage = new Message();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();
        OutletInformation outlet = OutletInformationDAO.loadOutlet(loggedUser.getRelationId());


        if (!Objects.isNull(promotionAvailable)) {
            if (promotionAvailable != true && promotionAvailable != false) {
                throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
            }
        } else {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        }
        
         if (Objects.isNull(skuIdList)) {
            
                throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
         }
       

        if (!Objects.isNull(searchText)) {
            if (!Utilities.validateRequiredParameter(searchText)) {
                throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
            }
        }
        
        if (Objects.isNull(startIndex)) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        }
        
        String[] string = skuIdList.replaceAll("\\[", "")
                              .replaceAll("]", "")
                               .replaceAll("\"","")
                              .split(",");
        
        Integer[] variantDetailIds = new Integer[string.length];
        
        for (int i = 0; i < string.length; i++) {
            variantDetailIds[i] = Integer.valueOf(string[i]);
        }

        ArrayList<BrandVariantdetail> lstBrandVariantDetail = BrandVariantDetailDAO.loadBrandVariantDetailList(promotionAvailable, searchText, outlet.getGeoLocation().getLocationId(), outlet.getOutletId(), variantDetailIds, startIndex);
        ArrayList<Integer> skuIds = new ArrayList<>();
        ArrayList<TpDetail> lstTpDetail = new ArrayList<>();
        if (lstBrandVariantDetail.size() > 0) {
            for (BrandVariantdetail sku : lstBrandVariantDetail) {
                skuIds.add(sku.getVariantDetailId());
            }
            if (skuIds.size() > 0) {
                lstTpDetail = TpDetailDAO.loadList(skuIds.toArray(new Integer[0]), outlet.getGeoLocation().getLocationId());
            }
            if (!Objects.isNull(versionNumber)) {
                ArrayList<DistBrandmapping> lstBrandDistributors = DistBrandmappingDAO.loadSkusDistributers(skuIds.toArray(new Integer[0]), outlet.getGeoLocation().getLocationId(), outlet.getOutletId());
                String skuIdsCsv = getVariantDetailIds(lstBrandVariantDetail);
                String distIdsCsv = getDistIds(lstBrandDistributors);
                ArrayList<Object[]> lstSkusAvailability = BrandVariantDetailDAO.loadSkusAvailablility(skuIdsCsv, distIdsCsv);

                for (BrandVariantdetail brandVariantdetail : lstBrandVariantDetail) {
                    boolean flag = true;
                    for (Object[] object : lstSkusAvailability) {
                        if (!Objects.isNull(object)) {
                            if (!Objects.isNull(object[0]) && Integer.parseInt(object[0].toString()) == brandVariantdetail.getVariantDetailId()) {
                                brandVariantdetail.setAvailableCount(Objects.isNull(object[3]) ? 0.0 : Double.valueOf(object[3].toString()));
                                flag = false;
                            }
                        }
                    }
                    if (flag) {
                        brandVariantdetail.setAvailableCount(0.0);
                    }
                }
            }
            if (lstTpDetail.size() > 0) {
                for (BrandVariantdetail brandVariantDetail : lstBrandVariantDetail) {
                    ArrayList<TpDetail> lstAllPromotions = new ArrayList<>();
                    boolean flag = false;
                    for (TpDetail tpDetail : lstTpDetail) {
                        if (tpDetail.getTpInformation().getBrandVariantdetail().getVariantDetailId().equals(brandVariantDetail.getVariantDetailId())) {
                           
                            if (!Objects.isNull(versionNumber)) {
                                if (brandVariantDetail.getAvailableCount() >= tpDetail.getOrderQty()
                                        && brandVariantDetail.getMaximumOrderQuantity() >= tpDetail.getOrderQty()) {
                                    lstAllPromotions.add(tpDetail);
                                    flag = true;
                                }
                            } else {
                                lstAllPromotions.add(tpDetail);
                                flag = true;
                            }

                        }
                    }
                    if (flag) {
                        brandVariantDetail.setLstAllPromotions(lstAllPromotions);
                    }
                }
            }

            lstBrandVariantDetail = BrandVariantDetailDAO.clearSets(lstBrandVariantDetail);
        }

        if (lstBrandVariantDetail.isEmpty()) {
            responseMessage.setStatus(ServiceStatus.NO_DATA_AVAILABLE.getValue());
            responseMessage.setStatusText(ServiceStatus.NO_DATA_AVAILABLE.getStatusText());
            Response.ResponseBuilder builder = Response.ok(responseMessage, "application/json");
            builder.cacheControl(cc);
            return builder.build();
        }
        
        if (lstBrandVariantDetail.size() == Integer.parseInt(ConfigDAO.load(Configuration.MAX_RECORDS.getId()).getConfValue())) {
            responseMessage.setHasMoreData(true);
        } else {
            responseMessage.setHasMoreData(false);
        }

        responseMessage.setLstBrandVariantDetail(lstBrandVariantDetail);
        responseMessage.setImageBaseURL(ConfigDAO.load(Configuration.IMAGE_BASE_URL.getId()).getConfValue());

        responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
        responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());

        Response.ResponseBuilder builder = Response.ok(responseMessage, "application/json");
        builder.cacheControl(cc);
        return builder.build();
    }
}
