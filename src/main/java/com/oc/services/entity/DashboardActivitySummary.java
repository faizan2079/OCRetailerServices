/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oc.services.entity;

/**
 *
 * @author msn
 */
public class DashboardActivitySummary {

  
    private Integer countOfPending;
    private Integer countOfClosed;
    private Integer countOfCancelled;
    private Integer userActivityId;
    private String activityDescription;
    private Integer totalCloseActivities;
    private Integer totalPendingActivities;
    private Integer totalCancelledActivities;

    public Integer getTotalCancelledActivities() {
        return totalCancelledActivities;
    }

    public void setTotalCancelledActivities(Integer totalCancelledActivities) {
        this.totalCancelledActivities = totalCancelledActivities;
    }
    private Integer totalActivities;

        
    public Integer getCountOfPending() {
        return countOfPending;
    }

    public void setCountOfPending(Integer countOfPending) {
        this.countOfPending = countOfPending;
    }

    public Integer getCountOfClosed() {
        return countOfClosed;
    }

    public void setCountOfClosed(Integer countOfClosed) {
        this.countOfClosed = countOfClosed;
    }

    

    public Integer getUserActivityId() {
        return userActivityId;
    }

    public void setUserActivityId(Integer userActivityId) {
        this.userActivityId = userActivityId;
    }

    public String getActivityDescription() {
        return activityDescription;
    }

    public void setActivityDescription(String activityDescription) {
        this.activityDescription = activityDescription;
    }
    
    public Integer getTotalCloseActivities() {
        return totalCloseActivities;
    }

    public void setTotalCloseActivities(Integer totalCloseActivities) {
        this.totalCloseActivities = totalCloseActivities;
    }

    public Integer getTotalPendingActivities() {
        return totalPendingActivities;
    }

    public void setTotalPendingActivities(Integer totalPendingActivities) {
        this.totalPendingActivities = totalPendingActivities;
    }

    public Integer getCountOfCancelled() {
        return countOfCancelled;
    }

    public void setCountOfCancelled(Integer countOfCancelled) {
        this.countOfCancelled = countOfCancelled;
    }

    public Integer getTotalActivities() {
        return totalActivities;
    }

    public void setTotalActivities(Integer totalActivities) {
        this.totalActivities = totalActivities;
    }
    
}
