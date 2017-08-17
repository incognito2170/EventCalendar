package com.arabi.eventcalendar;

/**
 * Created by Arabi on 8/11/2017.
 */

public class AppointmentListModelClass {
    private String patientFirstName;
    private String patientLastName;
    private String reason;
    private String startTime;
    private String endTime;
    private int profileImage;
    private String patientAvatar;
    private String day;
    private String status;
    private String timeDuration;

    public AppointmentListModelClass(String patientFirstName, String patientLastName, String reason, String startTime, String endTime, int profileImage, String patientAvatar, String day, String status, String timeDuration) {
        this.patientFirstName = patientFirstName;
        this.patientLastName = patientLastName;
        this.reason = reason;
        this.startTime = startTime;
        this.endTime = endTime;
        this.profileImage = profileImage;
        this.patientAvatar = patientAvatar;
        this.day = day;
        this.status = status;
        this.timeDuration = timeDuration;
    }

    public AppointmentListModelClass() {

    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPatientFirstName() {
        return patientFirstName;
    }

    public void setPatientFirstName(String patientFirstName) {
        this.patientFirstName = patientFirstName;
    }

    public String getPatientLastName() {
        return patientLastName;
    }

    public void setPatientLastName(String patientLastName) {
        this.patientLastName = patientLastName;
    }


    public int getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(int profileImage) {
        this.profileImage = profileImage;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
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


    public String getTimeDuration() {
        return timeDuration;
    }

    public void setTimeDuration(String timeDuration) {
        this.timeDuration = timeDuration;
    }

    public String getPatientAvatar() {
        return patientAvatar;
    }

    public void setPatientAvatar(String patientAvatar) {
        this.patientAvatar = patientAvatar;
    }
}
