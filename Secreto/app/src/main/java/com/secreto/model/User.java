package com.secreto.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class User extends BaseResponse implements Serializable {
    @SerializedName("user_id")
    private String userId;
    @SerializedName("name")
    private String name;
    @SerializedName("email")
    private String emailId;
    @SerializedName("password")
    private String password;
    @SerializedName("contact")
    private String contactNumber;
    @SerializedName("user_status")
    private String status;
    @SerializedName("media_id")
    private String media_id;
    @SerializedName("media")
    private String media;
    @SerializedName("type")
    private String type;

    public String getUserId() {
        return userId;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMedia_id() {
        return media_id;
    }

    public String getMedia() {
        return media;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", emailId='" + emailId + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
