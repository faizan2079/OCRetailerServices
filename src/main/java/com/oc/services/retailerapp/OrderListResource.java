/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oc.services.retailerapp;

import com.oc.services.entity.ServiceSecurity;
import com.oc.services.enums.Security;
import com.oc.services.enums.ServiceStatus;
import com.oc.services.response.OrderList;
import com.oc.services.response.OrderListResponse;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.servlet.ServletContext;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Context;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.lang.StringUtils;
import org.oc.db.controller.BrandVariantDetailDAO;
import org.oc.db.controller.OrderItemListDAO;
import org.oc.db.controller.OrderItemListDetailDAO;
import org.oc.db.controller.OutletInformationDAO;
import org.oc.db.controller.Utilities;
import org.oc.db.entity.BrandVariantdetail;
import org.oc.db.entity.OutletInformation;
import org.oc.db.entity.UserInformation;
import org.oc.db.entity.DistInformation;
import org.oc.db.entity.OrderItemList;
import org.oc.db.entity.OrderItemListDetail;

/**
 * REST Web Service
 *
 * @author DELL PRECISION M6800
 */
@Path("orderitemlist")
public class OrderListResource {

    @Context
    private ContainerRequestContext requestContext;
    private UserInformation loggedUser = null;
    @Context
    private ServletContext servletContext;

    public OrderListResource() {
    }

    @POST
    @Path("/addneworderitemlist")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ServiceSecurity(security = Security.RESTRICTED)
    public OrderListResponse addNewOrderItemList(Message requestMessage) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
       
       
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();
        OutletInformation outlet = OutletInformationDAO.loadOutlet(loggedUser.getRelationId());
        
        OrderItemList itemList = new OrderItemList();
        DistInformation dist = new DistInformation();
        dist.setDistributerId(1);
        itemList.setDistInformation(dist);
        itemList.setOutletInformation(outlet);
        itemList.setUserInformationByInsertId(loggedUser);
        itemList.setInsertDate( new Date() );
        itemList.setListName(requestMessage.getOrderListName());
        itemList.setIsActive(true);
       
        OrderListResponse response = new OrderListResponse();
        
        if( OrderItemListDAO.saveOrderList(itemList)){       
        
        
        OrderList  temp = new OrderList();
        temp.setListId(itemList.getOrderListId());
        temp.setListName(itemList.getListName());
        List<OrderList> o_list = new ArrayList<OrderList>();
        o_list.add(temp);
        
        response.setOrderList(o_list);
        response.setStatus(ServiceStatus.SUCCESS.getValue());
        response.setStatusText(ServiceStatus.SUCCESS.getStatusText());

        }
        else
        {
            response.setOrderList(null);
            response.setStatus(ServiceStatus.LIST_CREATION_FAILED.getValue());
            response.setStatusText(ServiceStatus.LIST_CREATION_FAILED.getStatusText());
        }
        return response;

    }
    
    @POST
    @Path("/addneworderitemlistdetail")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ServiceSecurity(security = Security.RESTRICTED)
    public Message addNewOrderItemListDetail(Message requestMessage) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Message responseMessage = new Message();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();
        OutletInformation outlet = OutletInformationDAO.loadOutlet(loggedUser.getRelationId());
        
        if (Objects.isNull(requestMessage)) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        }

        if (!Objects.isNull(requestMessage)) { 
            
                if (Objects.isNull(requestMessage.getOrderListId())) {
                    throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("OrderItemList Id is missing").build());
                }
                
                if (Objects.isNull(requestMessage.getLstBrandVariantDetail())) {
                    throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Skus List is missing").build());
                }
                if (!Utilities.validateRequiredParameter(requestMessage.getOrderListId())) {
                    throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("OrderItemList Id is incorrect").build());
                }
        }
       
        
        OrderItemListDetail itemDetail = new OrderItemListDetail();
        
        OrderItemList listId = new OrderItemList();
        listId.setOrderListId(requestMessage.getOrderListId());
        
        for (BrandVariantdetail bvd : requestMessage.getLstBrandVariantDetail()){
            
            
            if(OrderItemListDetailDAO.validateOrderListItemId(listId.getOrderListId(),bvd.getVariantDetailId())){
        
                itemDetail.setOrderItemList(listId);
        
                itemDetail.setUserInformationByInsertId(loggedUser);
                itemDetail.setInsertDate( new Date() );
                itemDetail.setBrandVariantdetail(bvd);
                itemDetail.setIsActive(true);
      
                OrderItemListDetailDAO.saveOrderListDetail(itemDetail);
    
            }
        
        }    
        responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
        responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());

        return responseMessage;

    }
    
    @GET
    @Path("/getallorderitemlist")
    @Produces(MediaType.APPLICATION_JSON)
    @ServiceSecurity(security = Security.RESTRICTED)
    public OrderListResponse getAllOrderItemList() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, ParseException {
       
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();
        OutletInformation outlet = OutletInformationDAO.loadOutlet(loggedUser.getRelationId());

        
        List<OrderItemList> orderlist = OrderItemListDAO.getAllItemList(outlet.getOutletId());
        
        OrderListResponse response = new OrderListResponse();
        List<OrderList> orderList = new  ArrayList<>();
        List<Integer> skuIds = null;
        
        for(OrderItemList o: orderlist){
            
            OrderList temp = new OrderList();
            temp.setListId(o.getOrderListId());
            temp.setListName(o.getListName());
           
                        
            List<OrderItemListDetail> detailList = OrderItemListDetailDAO.getAllRecord(o.getOrderListId());
            
            if(detailList != null && !detailList.isEmpty()){
                temp.setNumberOfSKU(detailList.size());
                skuIds = new ArrayList<>();
                    for(OrderItemListDetail d: detailList){
                        // ArrayList<BrandVariantdetail> temp_Sku = new ArrayList<>();
                        // temp_Sku.add(d.getBrandVariantdetail());
                        
                        skuIds.add(d.getBrandVariantdetail().getVariantDetailId());                
                        // ArrayList<BrandVariantdetail> tempList = BrandVariantDetailDAO.clearSets(temp_Sku);
                        // skuList_Temp.add(tempList.get(0));
                
                    }
            
                temp.setSkuIdList(StringUtils.join(skuIds, ","));                
                temp.setSkuList(null);
                orderList.add(temp);
            }
            else
            {
                  temp.setNumberOfSKU(0);
                  temp.setSkuIdList(null);
                  temp.setSkuList(null);
                  
            }
        }
        
        response.setOrderList(orderList);
        response.setStatus(ServiceStatus.SUCCESS.getValue());
        response.setStatusText(ServiceStatus.SUCCESS.getStatusText());

        return response;

    }
    
    
    @GET
    @Path("/getorderitemlist")
    @Produces(MediaType.APPLICATION_JSON)
    @ServiceSecurity(security = Security.RESTRICTED)
    public Message getOrderItemList(Message requestMessage) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, ParseException {
        Message responseMessage = new Message();
        //OrderListResponse response = new OrderListResponse();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();

        
        if (Objects.isNull(requestMessage)) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        }

        if (!Objects.isNull(requestMessage)) { 
            
                if (Objects.isNull(requestMessage.getOrderListId())) {
                    throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("OrderItemList Id is missing").build());
                }
                else if (!Utilities.validateRequiredParameter(requestMessage.getOrderListId())) {
                    throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("OrderItemList Id is incorrect").build());
                }
        }
        
            List<OrderItemList> orderlist = OrderItemListDAO.getbyorderItemListId(requestMessage.getOrderListId());
            
            if(orderlist != null && !orderlist.isEmpty()) {              
                List<OrderItemListDetail> detailList = OrderItemListDetailDAO.getAllRecord(orderlist.get(0).getOrderListId());
                
                responseMessage.setOrderItemList(new ArrayList<>(orderlist));
                responseMessage.setOrderItemListDetails(new ArrayList<>(detailList));
                responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
                responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());
                
                
            }else
            {
            
                responseMessage.setStatus(ServiceStatus.NO_DATA_AVAILABLE.getValue());
                responseMessage.setStatusText(ServiceStatus.NO_DATA_AVAILABLE.getStatusText());
            
            }
        
        return responseMessage;

    }

    @POST
    @Path("/deleteorderitemlist")
    @Produces(MediaType.APPLICATION_JSON)
    @ServiceSecurity(security = Security.RESTRICTED)
    public Message deleteOrderItemList(Message requestMessage) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, ParseException {
        Message responseMessage = new Message();
        loggedUser = (UserInformation) requestContext.getSecurityContext().getUserPrincipal();

        
        if (Objects.isNull(requestMessage)) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Required Parameters Are Missing").build());
        }

        if (!Objects.isNull(requestMessage)) { 
            
                if (Objects.isNull(requestMessage.getOrderListId())) {
                    throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("OrderItemList Id is missing").build());
                }
                else if (!Utilities.validateRequiredParameter(requestMessage.getOrderListId())) {
                    throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("OrderItemList Id is incorrect").build());
                }
        }
        
             if(OrderItemListDAO.deleteOrderItemList(requestMessage.getOrderListId(),loggedUser.getUserId()))
             {
                
                responseMessage.setStatus(ServiceStatus.SUCCESS.getValue());
                responseMessage.setStatusText(ServiceStatus.SUCCESS.getStatusText());

            }
             else
            {
            
                responseMessage.setStatus(ServiceStatus.FAILED.getValue());
                responseMessage.setStatusText(ServiceStatus.FAILED.getStatusText());
            
            }
        
        return responseMessage;

    }
}
