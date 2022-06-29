package com.oc.services.response;

import com.oc.services.entity.ComplaintResponseDto;
import java.util.List;


public class ComplaintResponse {
        
    private Integer status;
    private String statusText;
    private List<ComplaintResponseDto>complaintResponseList;

    public List<ComplaintResponseDto> getComplaintResponseList() {
        return complaintResponseList;
    }

    public void setComplaintResponseList(List<ComplaintResponseDto> complaintResponseList) {
        this.complaintResponseList = complaintResponseList;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

}
