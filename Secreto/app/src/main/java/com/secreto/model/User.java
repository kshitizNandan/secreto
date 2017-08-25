package com.secreto.model;

import com.google.gson.annotations.SerializedName;
import com.secreto.responsemodel.BaseResponse;
import com.secreto.responsemodel.MediaResponse;

import java.io.Serializable;

public class User extends UserMedia implements Serializable {
    @SerializedName("userId")
    private String userId;
    @SerializedName("name")
    private String name;
    @SerializedName("email")
    private String emailId;
    @SerializedName("password")
    private String password;
    @SerializedName("contact")
    private String contactNumber;
    @SerializedName("userStatus")
    private String status;

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
