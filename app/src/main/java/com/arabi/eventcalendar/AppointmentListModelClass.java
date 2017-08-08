package com.arabi.eventcalendar;

/**
 * Created by Sayem43 on 6/15/2017.
 */

public class AppointmentListModelClass {
    private String serviceName;
    private String startTime;
    private String endTime;
    private int profileImage;

    public AppointmentListModelClass(String serviceName, String startTime, String endTime, int profileImage) {
        this.serviceName = serviceName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.profileImage = profileImage;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(int profileImage) {
        this.profileImage = profileImage;
    }
}
